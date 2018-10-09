#!/usr/bin/sh

# Declare Variables

outDir='output'
dataPath='/Users/leonschuller/IdeaProjects/RestaurantsWDI/src/main/resources/schema_Restaurant'
jarPath='/Users/leonschuller/IdeaProjects/RestaurantsWDI/target/scala-2.11/restaurantswdi_2.11-0.1.jar'

declare -a cities=('las-vegas' 'new-york' 'toronto' 'montreal' 'calgary' 'charlotte' 'pittsburgh' 'scottsdale' 'cleveland' 'mississauga' 'san-francisco' 'los-angeles' 'madison' 'chicago' 'houston' 'san-antonio' 'dallas')

for city in "${cities[@]}"
do
    echo "$city"
    spark-submit --class CityExtractor $jarPath $city $dataPath $city
done

for city in "${cities[@]}"
do
    echo "Aggregating partitions of city $city"
    cat "${city}"/part-* > "${city}.json"
done

echo "Creating Final File"
cat *.json > all_cities.json

for city in "${cities[@]}"
do
    rm "${city}.json"
done

echo "Done"
       
