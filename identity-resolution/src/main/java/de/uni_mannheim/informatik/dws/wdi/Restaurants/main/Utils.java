package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import au.com.bytecode.opencsv.CSVReader;
import de.uni_mannheim.informatik.dws.wdi.Restaurants.model.Restaurant;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;


public class Utils {
    public static HashedDataSet<Restaurant, Attribute> removeDuplicates(String pathDuplicates, HashedDataSet<Restaurant, Attribute> data) throws
                                                                                                                                          IOException {

        Set<String> ToKeep = new HashSet<String>();
        Set<String> ToRemove = new HashSet<String>();

        try (
                Reader reader = Files.newBufferedReader(Paths.get(pathDuplicates));
                CSVReader csvReader = new CSVReader(reader)
        ) {

            String[] duplicateTuple;
            while ((duplicateTuple = csvReader.readNext()) != null) {
                String id1 = duplicateTuple[0];
                String id2 = duplicateTuple[1];


                if(ToKeep.contains(id1)){
                    ToRemove.add(id2);
                }else if(ToKeep.contains(id2)){
                    ToRemove.add(id1);
                }else{
                    ToKeep.add(id1);
                    ToRemove.add(id2);
                }
            }
        }

        for (String duplicate : ToRemove) {
            data.removeRecord(duplicate);
        }

        return data;

    }
}