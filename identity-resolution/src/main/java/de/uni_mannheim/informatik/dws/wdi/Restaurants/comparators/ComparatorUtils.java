package de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators;

import java.util.Locale;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;

public class ComparatorUtils {

    public static String def(String s){
        return s;
    }

    public static String cleanLower(String s){
        if (s!= null){
            s = s.replaceAll("[^A-Za-z0-9]", " ");
            s = s.replaceAll("\\s{2,}", " ");

            return s.toLowerCase();
        } else {
            return s;
        }

    }

    public static String unifyAddress(String s){
        if (s!=null){
            s = cleanLower(s);

            s = s.replaceAll("\\bn\\b","north");
            s = s.replaceAll("\\be\\b","east");
            s = s.replaceAll("\\bs\\b","south");
            s = s.replaceAll("\\bw\\b","west");

            String[] abbreviations = {"aly", "arc", "ave", "blvd", "bldg", "ctr", "crk", "est",
                                      "fld", "mt", "mtn", "pkwy", "plz", "rd", "sq", "sta", "st", "ter",
                                      "ste"};

            String[] corr = {"alley", "arcade", "avenue", "boulevard", "building", "center", "creek", "estate",
                             "field", "mount", "mountain", "parkway", "plaza", "road", "square", "station", "street", "terrace",
                             "suite"};

            /*s = s.replaceAll("\\brd\\b","road");
            s = s.replaceAll("\\bblvd\\b","boulevard");
            s = s.replaceAll("\\bave\\b","avenue");
            s = s.replaceAll("\\bst\\b","street");
            s = s.replaceAll("\\bsq\\b","square");
            */

            for(int i = 0; i < abbreviations.length; i++){
                s = s.replaceAll("\\b"+abbreviations[i]+"\\b",corr[i]);
            }

            RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.UK, RuleBasedNumberFormat.SPELLOUT);
            for(int i = 0; i <= 30; i++)
            {
                s = s.replaceAll("\\b"+ordinal(i)+"\\b", nf.format(i,"%spellout-ordinal"));
            }

            return s;

        } else {
            return s;
        }
    }

    public static String ordinal(int i) {
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }
}
