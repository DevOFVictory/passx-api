package net.cuodex.passxapi.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class UmlautHelper {

    private static Map<Character, String> umlauts;
    public static void init() {
        umlauts = new HashMap<Character, String>();
        umlauts.put('ß', "#ssSL#");
        umlauts.put('ü', "#ueSL#");
        umlauts.put('ö', "#oeSL#");
        umlauts.put('ä', "#aeSL#");
        umlauts.put('Ü', "#ueBG#");
        umlauts.put('Ö', "#oeBG#");
        umlauts.put('Ä', "#aeBG#");
    }
    public static String replaceUmlauts(String s) {
        for(char c : getUmlauts()) {
            s = s.replaceAll(c + "", getCode(c));
        }
        return s;
    }
    public static String reverseUmlauts(String s) {
        for(char c : getUmlauts()) {
            s = s.replaceAll(getCode(c), c + "");
        }
        return s;
    }
    public static Character[] getUmlauts() {
        List<Character> output = new ArrayList<Character>(umlauts.keySet());
        return output.toArray(new Character[0]);
    }
    public static char getUmlaut(String s) {
        for(char current : umlauts.keySet()) {
            if(umlauts.get(current).equals(s)) {
                return current;
            }
        }
        return 1;
    }
    public static String getCode(char c) {
        return umlauts.get(c);
    }
}