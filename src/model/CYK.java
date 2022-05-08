/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @Authors: Juan Esteban Caicedo and Carlos Jimmy Pantoja.
 * @Date: May, 15th 2022
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/
package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class CYK {

    // -----------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------

    public static final String INITIAL_VARIABLE = "S";

    // -----------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------

    private String string;
    private Hashtable<Character, ArrayList<String>> grammar = new Hashtable<>();

    // -----------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------

    public boolean checkStringGeneration(String string) {
        this.string = string;
        fillGrammar();
        String[][] table = cykAlgorithm(buildTableCYK(string));
        if (table[table.length - 1][table[table.length - 1].length - 1].contains(INITIAL_VARIABLE))
            return true;
        else
            return false;
    }

    private void fillGrammar() { // MUST BE MODIFIED
        ArrayList<String> prod1 = new ArrayList<>();
        prod1.add("IB");
        grammar.put('S', prod1);
        ArrayList<String> prod2 = new ArrayList<>();
        prod2.add("AX");
        prod2.add("AJ");
        prod2.add("a");
        grammar.put('I', prod2);
        ArrayList<String> prod3 = new ArrayList<>();
        prod3.add("YZ");
        prod3.add("BZ");
        prod3.add("AK");
        prod3.add("a");
        grammar.put('J', prod3);
        ArrayList<String> prod4 = new ArrayList<>();
        prod4.add("AK");
        prod4.add("a");
        grammar.put('K', prod4);
        ArrayList<String> prod5 = new ArrayList<>();
        prod5.add("a");
        grammar.put('A', prod5);
        ArrayList<String> prod6 = new ArrayList<>();
        prod6.add("b");
        grammar.put('B', prod6);
        ArrayList<String> prod7 = new ArrayList<>();
        prod7.add("c");
        grammar.put('C', prod7);
        ArrayList<String> prod8 = new ArrayList<>();
        prod8.add("IA");
        grammar.put('X', prod8);
        ArrayList<String> prod9 = new ArrayList<>();
        prod9.add("BJ");
        grammar.put('Y', prod9);
        ArrayList<String> prod10 = new ArrayList<>();
        prod10.add("CC");
        grammar.put('Z', prod10);
    }

    private String[][] buildTableCYK(String string) {
        String[][] table = new String[string.length() + 1][];
        table[0] = new String[string.length()];
        for (int i = 1; i < table.length; i++)
            table[i] = new String[string.length() - (i - 1)];
        for (int i = 1; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++)
                table[i][j] = "";
        }
        return table;
    }

    private String[][] cykAlgorithm(String[][] table) { // NOT FINISHED YET
        // Fill characters string in the CYK table.
        for (int j = 0; j < table[0].length; j++) {
            table[0][j] = String.valueOf(string.charAt(j));
        }
        // Fill CYK table when j = 1.
        for (int j = 0; j < table[1].length; j++) {
            String[] substring = new String[table[0][j].length()];
            substring[j] = table[0][j];
            String[] variablesProducers = getVariablesThatProduce(substring);
            table[1][j] = giveFormat(variablesProducers);
        }
        if (string.length() <= 1)
            return table;
        return table;
    }

    private String[] getVariablesThatProduce(String[] combinations) {
        ArrayList<Character> variablesProducers = new ArrayList<>();
        for (Character variable : grammar.keySet()) {
            for (String production : combinations) {
                if (grammar.get(variable).contains(production))
                    variablesProducers.add(variable);
            }
        }
        if (variablesProducers.size() == 0) {
            return new String[0];
        }
        String[] varProducers = new String[variablesProducers.size()];
        return variablesProducers.toArray(varProducers);
    }

    private String giveFormat(String[] input) {
        String format = Arrays.toString(input).replaceAll("[\\[\\]\\,]", ",");
        return format.substring(1, format.length()).substring(0, format.length() - 2);
    }
}