package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class CSVRestaurantDetailFormatter {

    public <TypeA extends Restaurant, TypeB extends Matchable> String[] format(Correspondence<TypeA, TypeB> record) {
        return new String[] { "0", record.getFirstRecord().getName(), record.getSecondRecord().getName(),
                record.getFirstRecord().getPostalAddress().getAddress(), record.getSecondRecord().getPostalAddress().getAddress(),
                record.getFirstRecord().getPostalAddress().getCity().getPostalCode(), record.getSecondRecord().getPostalAddress().getCity().getPostalCode(),
                record.getFirstRecord().getIdentifier(), record.getSecondRecord().getIdentifier(),
                Double.toString(record.getSimilarityScore()) };
    }

    /**
     * Writes the data set to a CSV file
     *
     * @param file
     * @param dataset
     * @throws IOException
     */
    public <TypeA extends Restaurant, TypeB extends Matchable> void writeCSV(File file,
                                                                            Processable<Correspondence<TypeA, TypeB>> dataset) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(file));

        for (Correspondence<TypeA, TypeB> record : dataset.get()) {
            String[] values = format(record);

            writer.writeNext(values);
        }

        writer.close();
    }
}
