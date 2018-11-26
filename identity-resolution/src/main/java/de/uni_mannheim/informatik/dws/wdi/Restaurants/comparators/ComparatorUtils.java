package de.uni_mannheim.informatik.dws.wdi.Restaurants.comparators;

import java.util.*;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;

public class ComparatorUtils {

    private static String[] stopWordsofwordnet = {
            "without",
            "see",
            "unless",
            "due",
            "also",
            "must",
            "might",
            "like",
            "]",
            "[",
            "}",
            "{",
            "<",
            ">",
            "?",
            "\"",
            "\\",
            "/",
            ")",
            "(",
            "will",
            "may",
            "can",
            "much",
            "every",
            "the",
            "in",
            "other",
            "this",
            "the",
            "many",
            "any",
            "an",
            "or",
            "for",
            "in",
            "an",
            "an ",
            "is",
            "a",
            "about",
            "above",
            "after",
            "again",
            "against",
            "all",
            "am",
            "an",
            "and",
            "any",
            "are",
            "aren’t",
            "as",
            "at",
            "be",
            "because",
            "been",
            "before",
            "being",
            "below",
            "between",
            "both",
            "but",
            "by",
            "can’t",
            "cannot",
            "could",
            "couldn’t",
            "did",
            "didn’t",
            "do",
            "does",
            "doesn’t",
            "doing",
            "don’t",
            "down",
            "during",
            "each",
            "few",
            "for",
            "from",
            "further",
            "had",
            "hadn’t",
            "has",
            "hasn’t",
            "have",
            "haven’t",
            "having",
            "he",
            "he’d",
            "he’ll",
            "he’s",
            "her",
            "here",
            "here’s",
            "hers",
            "herself",
            "him",
            "himself",
            "his",
            "how",
            "how’s",
            "i ",
            " i",
            "i’d",
            "i’ll",
            "i’m",
            "i’ve",
            "if",
            "in",
            "into",
            "is",
            "isn’t",
            "it",
            "it’s",
            "its",
            "itself",
            "let’s",
            "me",
            "more",
            "most",
            "mustn’t",
            "my",
            "myself",
            "no",
            "nor",
            "not",
            "of",
            "off",
            "on",
            "once",
            "only",
            "ought",
            "our",
            "ours",
            "ourselves",
            "out",
            "over",
            "own",
            "same",
            "shan’t",
            "she",
            "she’d",
            "she’ll",
            "she’s",
            "should",
            "shouldn’t",
            "so",
            "some",
            "such",
            "than",
            "that",
            "that’s",
            "their",
            "theirs",
            "them",
            "themselves",
            "then",
            "there",
            "there’s",
            "these",
            "they",
            "they’d",
            "they’ll",
            "they’re",
            "they’ve",
            "this",
            "those",
            "through",
            "to",
            "too",
            "under",
            "until",
            "up",
            "very",
            "was",
            "wasn’t",
            "we",
            "we’d",
            "we’ll",
            "we’re",
            "we’ve",
            "were",
            "weren’t",
            "what",
            "what’s",
            "when",
            "when’s",
            "where",
            "where’s",
            "which",
            "while",
            "who",
            "who’s",
            "whom",
            "why",
            "why’s",
            "with",
            "won’t",
            "would",
            "wouldn’t",
            "you",
            "you’d",
            "you’ll",
            "you’re",
            "you’ve",
            "your",
            "yours",
            "yourself",
            "yourselves",
            };
    private static Set<String> stopwordsset = new HashSet<String>(Arrays.asList(stopWordsofwordnet));


    public static String def(String s){
        return s;
    }

    public static String cleanLower(String s){
        if (s!= null){
            s = s.replaceAll("[^A-Za-z0-9]", " ");
            s = s.replaceAll("\\s{2,}", " ");

            return s.trim().toLowerCase();
        } else {
            return s;
        }
    }

    public static String cleanLowerStopwords(String s) {
        s = cleanLower(s);

        if (s == null)
            return s;

        String[] words = s.split(" ");
        ArrayList<String> wordsList = new ArrayList<String>();

        for (String word : words) {
            String wordCompare = word.toUpperCase();
            if (!stopwordsset.contains(wordCompare)) {
                wordsList.add(word);
            }
        }

        String s_nostopwords = "";
        for (String w : wordsList) {
            s_nostopwords += w + " ";
        }

        return s_nostopwords.trim();
    }

    public static String removeCityName(String s){
        if (s == null)
            return s;
        String[] aS = s.split(";");
        if (aS.length > 1) {
            String restaurantName = cleanLower(aS[0]);
            String cityName = cleanLower(aS[1]);
            restaurantName = restaurantName.replace(cityName, "");
            return restaurantName;
        }
        else {
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


    public static String unifyPostalCode(String s){
        if (s!=null){
            s = cleanLower(s);
            String[] parts = s.split(" ");
            if (parts.length > 1) {
                String unifiedPostalCode = "";
                for (String part : parts) {
                    unifiedPostalCode += part + " ";
                }
                return unifiedPostalCode;

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
