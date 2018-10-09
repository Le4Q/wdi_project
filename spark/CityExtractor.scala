import org.apache.spark.sql.{SparkSession, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.functions._


import scala.annotation.tailrec

object CityExtractor {

  val RESTAURANT = "<http://schema.org/Restaurant>"

  // top level attributes
  val NAME = "<http://schema.org/Restaurant/name>"
  val DESCRIPTION = "<http://schema.org/Restaurant/description>"
  val ADDRESS = "<http://schema.org/Restaurant/address>"
  val IMAGE = "<http://schema.org/Restaurant/image>"
  val TELEPHONE = "<http://schema.org/Restaurant/telephone>"
  val CUISINE = "<http://schema.org/Restaurant/servesCuisine>"
  val REVIEW = "<http://schema.org/Restaurant/review>"
  val PRICERANGE = "<http://schema.org/Restaurant/priceRange>"
  val ACCEPTSRESERVATIONS = "<http://schema.org/Restaurant/acceptsReservations>"
  val PAYMENTMETHOD = "<http://schema.org/Restaurant/paymentAccepted>"

  // review-related attributes
  val REVIEWBODY = "<http://schema.org/Review/reviewBody>"
  val REVIEWAUTHOR = "<http://schema.org/Review/author>"
  val REVIEWRATING = "<http://schema.org/Review/reviewRating>"
  val RATINGVALUE = "<http://schema.org/Rating/ratingValue>"

  // address-related attributes
  val ADDRESSREGION = "<http://schema.org/PostalAddress/addressRegion>"
  val ADDRESSSTREET = "<http://schema.org/PostalAddress/streetAddress>"
  val ADDRESSPOSTALCODE = "<http://schema.org/PostalAddress/postalCode>"
  val ADDRESSLOCALITY = "<http://schema.org/PostalAddress/addressLocality>"
  val ADDRESSCOUNTRY = "<http://schema.org/PostalAddress/addressCountry>"

  val IMAGEURL = "<http://schema.org/ImageObject/url>"

  @tailrec
  def IterContains(iterable: Iterable[String], check: String, index: Int = 0): Int = {
    val list = iterable.toList
    if(list.tail.isEmpty){
      if (list.head.contains(check)) index
      else -1
    } else {
      if (list.head.contains(check)) index
      else IterContains(list.tail, check, index + 1)
    }
  }

  def quads2Values(quadIter: Iterable[String], checkIter: Iterable[String]): Iterable[String] = {
    val quadList = quadIter.toList
    val checkList = checkIter.toList
    val size = checkList.length - 1
    val output = for {
      i <- 0 to size
    } yield {
      val feature = checkList(i)
      val index = IterContains(quadList, feature)
      if (index != -1)
        quadList(index).split(" +").slice(2, quadList(index).split(" +").length - 2).mkString(" ")
      else "null"
    }
    output
  }

  def main(args: Array[String]) {


    val spark = SparkSession.builder
      .master("local[*]")
      .appName("Restaurants")
      .getOrCreate()

    val sc = spark.sparkContext
    val sqlContext = spark.sqlContext

    import sqlContext.implicits._

    // val conf = new SparkConf().setMaster("local[*]").setAppName("Restaurants")
    //val sc = new SparkContext(conf)


    //val dataPath = "src/main/resources/schema_Restaurant.txt"
    val dataPath = args(1)
    val city_term = args(0) // san-francisco, los-angeles, toronto, san-diego, boston, chicago, dallas

    val rawQuads = sc.textFile(dataPath)

    // get IDs for restaurants of provided search city term
    val cityRestaurantIDs = rawQuads
      .filter(q => q.split(" ")(2).contains(RESTAURANT))
      .filter(q => q.contains(city_term))
      .map(q => (q.split(" ")(0),1))

    // get ID-related quads
    val cityKeyQuads = rawQuads
      .map(q => (q.split(" ")(0), q))
      .join(cityRestaurantIDs)
      .map(x => (x._1, x._2._1)).cache()

    val RestaurantBaseRDD = cityKeyQuads.groupByKey

    val RestaurantAttrs = Seq(NAME, DESCRIPTION, ADDRESS, IMAGE, TELEPHONE, CUISINE, PRICERANGE, ACCEPTSRESERVATIONS, PAYMENTMETHOD)

    // RestaurantRDD :(RestaurantID, Name, Description, Address, Image, Telephone, Cuisine, Pricerange, Acceptsreservations)
    val RestaurantRDD = RestaurantBaseRDD
      .map(x => quads2Values(x._2, RestaurantAttrs).toList match {
        case List(name, description, address, image, telephone, cuisine, pricerange, acceptsreservations, paymentmethod) =>
          (x._1, name, description, address, image, telephone, cuisine, pricerange, acceptsreservations, paymentmethod)
        case _ => (x._1, "null", "null", "null", "null", "null", "null", "null", "null", "null")
      })


    // IDs of nested attributes
    val AddressIDs = cityKeyQuads
      .map(x => x._2)
      .filter(x => x.split(" ")(1).contains(ADDRESS))
      .filter(x => x.split(" ")(2).startsWith("_:"))
      .map(x => (x.split(" ")(2),1))

    val ReviewIDsWithRestaurantID = cityKeyQuads
      .filter(x => x._2.split(" ")(1).contains(REVIEW))
      .filter(x => x._2.split(" ")(2).startsWith("_:"))
      .map(x => (x._2.split(" ")(2), x._1))
    // (nodeRev, nodeRes)


    // Subattributes of nested attributes
    val AddressBaseRDD = rawQuads
      .map(q => (q.split(" ")(0), q))
      .join(AddressIDs)
      .map(x => (x._1, x._2._1))
      .groupByKey


    val AddressAttrs = Seq(ADDRESSSTREET, ADDRESSPOSTALCODE, ADDRESSCOUNTRY, ADDRESSLOCALITY, ADDRESSREGION)

    // AddressRDD: (AddressID, Street, Postalcode, Country, Locality, Region)
    val AddressRDD = AddressBaseRDD
      .map(x => (x._1, quads2Values(x._2, AddressAttrs).toList match {
        case List(street, postalcode, country, locality, region) => (street, postalcode, country, locality, region)
        case _ => ("null", "null", "null", "null", "null")
      }))
      .map(x => (x._1, x._2._1, x._2._2, x._2._3, x._2._4, x._2._5))

    val ImageIDs = cityKeyQuads
      .map(x => x._2)
      .filter(x => x.split(" ")(1).contains(IMAGE))
      .filter(x => x.split(" ")(2).startsWith("_:"))
      .map(x => (x.split(" ")(2),1))

    // ImageBaseRDD: (ImageID, ImageURL)
    val ImageBaseRDD = rawQuads
      .map(q => (q.split(" ")(0), q))
      .join(ImageIDs)
      .map(x => (x._1, x._2._1))
      .filter(x => x._2.split(" ")(1).contains(IMAGEURL))
      .map(x => (x._1, x._2.split(" +").slice(2, x._2.split(" +").length - 2).mkString(" ")))

    val ReviewAttrs = Seq(REVIEWAUTHOR, REVIEWBODY, REVIEWRATING)

    val RatingIDs = rawQuads
      .map(q => (q.split(" ")(0), q))
      .join(ReviewIDsWithRestaurantID)
      .map(x => x._2._1)
      .filter(x => x.split(" ")(1).contains(REVIEWRATING))
      .filter(x => x.split(" ")(2).startsWith("_:"))
      .map(x => (x.split(" ")(2), 1))


    val RatingIDWithValue = rawQuads
      .map(q => (q.split(" ")(0), q))
      .join(RatingIDs)
      .map(x => (x._1, x._2._1))
      .filter(x => x._2.split(" ")(1).contains(RATINGVALUE))
      .map(x => (x._1, x._2.split(" +").slice(2, x._2.split(" +").length - 2).mkString(" ")))


    // RestaurantIDReviewRDD: (RestaurandID, ReviewAuthor, ReviewBody, ReviewRating)
    val RestaurantIDReviewRDD = rawQuads
      .map(q => (q.split(" ")(0), q))
      .join(ReviewIDsWithRestaurantID)
      .cache()
      .map(x => ((x._1, x._2._2), x._2._1))
      .groupByKey
      .map(x => (x._1._2, quads2Values(x._2, ReviewAttrs).toList match {
        case List(first, second, third) => (first, second, third)
        case _ => ("null", "null", "null")
      }))
      .map(x => (x._2._3, (x._1, x._2._1, x._2._2)))
      .join(RatingIDWithValue)
      .map(x => (x._2._1._1, x._2._1._2, x._2._1._3 ,x._2._2))



    // WHAT WE HAVE SO FAR:
    // RestaurantRDD :(RestaurantID, Name, Description, Address, Image, Telephone, Cuisine, Pricerange, Acceptsreservations)
    // RestaurantIDReviewRDD: (RestaurandID, ReviewAuthor, ReviewBody, ReviewRating)
    // AddressRDD: (AddressID, Street, Postalcode, Country, Locality, Region)
    // ImageBaseRDD: (ImageID, ImageURL)

    // convert to Dataframes
    val RestaurantDF = spark.createDataFrame(RestaurantRDD)
      .toDF("RestaurantID", "Name", "Description", "AddressID", "Image", "Telephone", "Cuisine", "Pricerange", "AcceptsReservations", "PaymentMethods")

    val ReviewDF = spark.createDataFrame(RestaurantIDReviewRDD)
        .toDF("RestaurantKey", "Author", "Body", "Rating")
        .dropDuplicates()
        .groupBy(cols = $"RestaurantKey")
        .agg(collect_list(struct(cols = $"Author", $"Body", $"Rating")).as("Reviews"))

    val AddressDF = spark.createDataFrame(AddressRDD)
        .toDF("AddressKey", "Street", "Postalcode", "Country", "Locality", "Region")

    val ImageDF = spark.createDataFrame(ImageBaseRDD)
        .toDF("ImageID", "ImageURL")

    //val nestedReviewDF = ReviewDF.select($"RestaurantID", struct(ReviewDF.columns.map(col):_*).as("Review"))
    //val nestedAddressDF = AddressDF.select($"AddressID", struct(AddressDF.columns.map(col):_*).as("Address"))

    val output = RestaurantDF
      .join(ImageDF, $"Image" === $"ImageID", "left")
      .withColumn("ImageURL", coalesce($"ImageURL", $"Image"))
      .drop("ImageID").drop("Image")
      .join(ReviewDF, $"RestaurantID" === $"RestaurantKey", "left")
      .drop("RestaurantKey")
      //.groupBy(cols = $"RestaurantID", $"Name", $"Description", $"AddressID", $"ImageURL", $"Telephone", $"Cuisine", $"Pricerange", $"AcceptsReservations")
      //.agg(collect_list(struct(cols = $"Author", $"Body", $"Rating")).as("Reviews"))
      .join(AddressDF, $"AddressID" === $"AddressKey", "left")
      .drop("AddressKey")
      .groupBy(cols = $"RestaurantID", $"Name", $"Description", $"AddressID", $"ImageURL", $"Telephone", $"Cuisine", $"Pricerange", $"AcceptsReservations", $"Reviews", $"PaymentMethods")
      .agg(collect_list(struct(cols = $"Street", $"Postalcode", $"Country", $"Locality", $"Region")).as("Address"))
      .drop("AddressID")
      .withColumn("City", lit(city_term))



    output.write.json(args(2))




  }

}
