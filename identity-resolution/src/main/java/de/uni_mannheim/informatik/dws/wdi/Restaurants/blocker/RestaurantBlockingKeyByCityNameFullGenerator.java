package de.uni_mannheim.informatik.dws.wdi.Restaurants.blocker;

        import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
        import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.RecordBlockingKeyGenerator;
        import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
        import de.uni_mannheim.informatik.dws.winter.model.Matchable;
        import de.uni_mannheim.informatik.dws.winter.model.Pair;
        import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
        import de.uni_mannheim.informatik.dws.winter.processing.DataIterator;
        import de.uni_mannheim.informatik.dws.winter.processing.Processable;


public class RestaurantBlockingKeyByCityNameFullGenerator extends RecordBlockingKeyGenerator<Restaurant, Attribute> {

    private static final long serialVersionUID = 1L;


    /* (non-Javadoc)
     * @see de.uni_mannheim.informatik.wdi.matching.blocking.generators.BlockingKeyGenerator#generateBlockingKeys(de.uni_mannheim.informatik.wdi.model.Matchable, de.uni_mannheim.informatik.wdi.model.Result, de.uni_mannheim.informatik.wdi.processing.DatasetIterator)
     */
    @Override
    public void generateBlockingKeys(Restaurant record, Processable<Correspondence<Attribute, Matchable>> correspondences,
                                     DataIterator<Pair<String, Restaurant>> resultCollector) {

        String blockingKeyValue = "";
        String cityName = record.getPostalAddress().getCity().getName();

        String[] tokens;
        if (cityName != null) {
            blockingKeyValue = cityName;
            resultCollector.next(new Pair<>(blockingKeyValue, record));

        }

    }

}
