package de.uni_mannheim.informatik.dws.wdi.Restaurants.main;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AnnotationToGoldStandard {

    public static void main(String[] args) {

        String inputPath = "identity-resolution/data/output/test_correspondences_SM_detail_ZD.csv";
        String outputPathTrain = "identity-resolution/data/goldstandard/gs_zomato_2_datafiniti_training";
        String outputPathTest = "identity-resolution/data/goldstandard/gs_zomato_2_datafiniti_test";

        try {
            annotation2goldstandard(inputPath, outputPathTrain, outputPathTest);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void annotation2goldstandard(String inputPath, String outputPathTrain, String outputPathTest) throws IOException {

        try (
                Reader reader = Files.newBufferedReader(Paths.get(inputPath));
                CSVReader csvReader = new CSVReader(reader);

                Writer writerTrain = Files.newBufferedWriter(Paths.get(outputPathTrain));
                Writer writerTest = Files.newBufferedWriter(Paths.get(outputPathTest));

                CSVWriter csvWriterTrain = new CSVWriter(writerTrain, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
                CSVWriter csvWriterTest = new CSVWriter(writerTest, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)

        ) {

            String[] annotations;
            int zeroCounter = 0;
            int oneCounter = 0;

            // assuming Train:Test Split of 80:20
            while ((annotations = csvReader.readNext()) != null) {
                String label = annotations[0];
                String id1 = annotations[7];
                String id2 = annotations[8];

                if (label.equals("0")){
                    if (zeroCounter < 4){
                        csvWriterTrain.writeNext(new String[]{id1, id2, "FALSE"});
                        zeroCounter++;
                    }else{
                        csvWriterTest.writeNext(new String[]{id1, id2, "FALSE"});
                        zeroCounter = 0;
                    }
                }else if (label.equals("1")){
                    if (oneCounter < 4){
                        csvWriterTrain.writeNext(new String[]{id1, id2, "TRUE"});
                        oneCounter++;
                    }else{
                        csvWriterTest.writeNext(new String[]{id1, id2, "TRUE"});
                        oneCounter=0;
                    }
                }
            }
        }
    }
}
