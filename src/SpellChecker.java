import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpellChecker {

    public static final int EDIT_DISTANCE_THRESHOLD = 1;
    public final HashMap<String, Integer> dict = new HashMap<>();

    public SpellChecker(String file) throws IOException {
        BufferedReader in = new BufferedReader( new FileReader( file ) );
        Pattern p = Pattern.compile( "\\w+" );
        for (String temp = ""; temp != null; temp = in.readLine()) {
            Matcher m = p.matcher( temp.toLowerCase() );
            while (m.find()) {
                String key = m.group();
                Integer value = dict.containsKey( key ) ? dict.get( key ) + 1 : 1;
                dict.put( key, value );
            }
        }
        in.close();
    }

    public List<String> probableCorrections(String misspelledWord) {
        int length = misspelledWord.length();
        List<String> corrections = new ArrayList<>();
        for (String word : dict.keySet()) {
            if (word.length() <= length + EDIT_DISTANCE_THRESHOLD && word.length() >= length - EDIT_DISTANCE_THRESHOLD) {
                if (damerauLevenshtineEditDistance( word, misspelledWord ) <= EDIT_DISTANCE_THRESHOLD) {
                    corrections.add( word );
                }
            }
        }
        Collections.sort( corrections, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return dict.get( o2 ) - dict.get( o1 );
            }
        } );
        return corrections;
    }

    private int damerauLevenshtineEditDistance(String source, String target) {

        if (source == null || target == null) {
            throw new IllegalArgumentException( "Input strings cannot be null" );
        }
        int sourceLength = source.length();
        int targetLength = target.length();

        Integer[][] E = new Integer[sourceLength + 1][targetLength + 1];
        for (int i = 0; i <= sourceLength; i++) {
            E[i][0] = i;
        }
        for (int j = 0; j <= targetLength; j++) {
            E[0][j] = j;
        }

        for (int i = 1; i <= sourceLength; i++) {
            for (int j = 1; j <= targetLength; j++) {
                int cost;
                if (source.charAt( i - 1 ) == target.charAt( j - 1 )) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                E[i][j] = Math.min(1+E[i][j - 1], Math.min(1+ E[i - 1][j], E[i - 1][j - 1] + cost ) );
                if (i > 1 && j > 1 && source.charAt( i - 1 ) == target.charAt( j - 2 ) && source.charAt( i - 2 ) == target.charAt( j - 1 )) {
                    E[i][j] = Math.min( E[i][j], 1 + E[i - 2][j - 2] );
                }
            }
        }

        return E[sourceLength][targetLength];
    }

    private void printList(List<String> strings) {
        for (String s : strings) {
            System.out.println( s );
        }
    }


    public static void main(String[] args) {
        String projectpath = System.getProperty( "user.dir" );
        String filepath = projectpath + "/SherlockHolmes.txt";
        try {
            SpellChecker spellchecker = new SpellChecker( filepath );
            spellchecker.checkWord( "manage" );
            spellchecker.checkWord( "stroes" );
            spellchecker.checkWord( "primise" );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkWord(String word) {
        if (dict.containsKey( word )) {
            System.out.println( word + " is a valid word \n" );
        } else {
            System.out.println( word + " is a mispelled word,,probable corrections are: " );
            printList( probableCorrections( word ) );
        }
    }


}
