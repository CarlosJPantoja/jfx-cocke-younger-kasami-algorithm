/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @Authors: Juan Esteban Caicedo and Carlos Jimmy Pantoja.
 * @Date: May, 15th 2022
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/
package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

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
        String[][] table = cykAlgorithm(buildTableCYK(string));
        if (table[table.length - 1][table[table.length - 1].length - 1].contains(INITIAL_VARIABLE))
            return true;
        else
            return false;
    }

    public void fillGrammar(ArrayList<Character> nonTerminals, ArrayList<ArrayList<String>> productions) {
        for (int i = 0; i < nonTerminals.size(); i++)
            grammar.put(nonTerminals.get(i), productions.get(i));
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

    private String[][] cykAlgorithm(String[][] table) {
        // Fill characters string in the CYK table.
        for (int j = 0; j < table[0].length; j++) {
            table[0][j] = String.valueOf(string.charAt(j));
        }
        // Fill CYK table when j = 1.
        for (int j = 0; j < table[1].length; j++) {
            String[] substring = new String[1];
            substring[0] = table[0][j];
            String[] variablesProducers = getVariablesThatProduce(substring);
            table[1][j] = giveFormat(variablesProducers);
        }
        if (string.length() <= 1)
            return table;
        // Fill CYK table when j = 2.
        for (int j = 0; j < table[2].length; j++) {
            String[] x2minus1j = table[1][j].split(", ");
            String[] x2minus1jPlus1 = table[1][j + 1].split(", ");
            String[] variablesProducers = getVariablesThatProduce(combineCells(x2minus1j, x2minus1jPlus1));
            table[2][j] = giveFormat(variablesProducers);
        }
        if (string.length() <= 2)
            return table;
        // Fill CYK table when j > 2.
        Set<String> setOfVariablesProducers = new HashSet<>();
        for (int i = 3; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                for (int k = 1; k < i; k++) {
                    String[] xij1 = table[k][j].split(", ");
                    String[] xij2 = table[i - k][j + k].split(", ");
                    String[] combinations = combineCells(xij1, xij2);
                    String[] variablesProducers = getVariablesThatProduce(combinations);
                    if (table[i][j].isEmpty())
                        table[i][j] = giveFormat(variablesProducers);
                    else {
                        String[] variablesRepeated = table[i][j].split(", ");
                        ArrayList<String> variablesRepeatedAndFull = new ArrayList<String>(
                                Arrays.asList(variablesRepeated));
                        variablesRepeatedAndFull.addAll(Arrays.asList(variablesProducers));
                        setOfVariablesProducers.addAll(variablesRepeatedAndFull);
                        table[i][j] = giveFormat(
                                setOfVariablesProducers.toArray(new String[setOfVariablesProducers.size()]));
                    }
                }
                setOfVariablesProducers.clear();
            }
        }
        return table;
    }

    private String[] getVariablesThatProduce(String[] combinations) {
        ArrayList<String> variablesProducers = new ArrayList<>();
        for (Character variable : grammar.keySet()) {
            for (String production : combinations) {
                if (grammar.get(variable).contains(production))
                    variablesProducers.add(String.valueOf(variable));
            }
        }
        String[] varProducers = new String[variablesProducers.size()];
        return variablesProducers.toArray(varProducers);
    }

    private String giveFormat(String[] input) {
        String format = Arrays.toString(input).replaceAll("[\\[\\]\\,]", ",");
        return format.substring(1, format.length() - 1);
    }

    private String[] combineCells(String[] xij1, String[] xij2) {
        int lengthAllVariablesProducers = xij1.length * xij2.length;
        int combination = 0;
        String[] combinations = new String[lengthAllVariablesProducers];
        for (int i = 0; i < xij1.length; i++) {
            for (int j = 0; j < xij2.length; j++) {
                combinations[combination] = xij1[i] + xij2[j];
                combination++;
            }
        }
        return combinations;
    }
}