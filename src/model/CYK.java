/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @Authors: Juan Esteban Caicedo and Carlos Jimmy Pantoja.
 * @Date: May, 15th 2022
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/
package model;

import java.util.ArrayList;
import java.util.Hashtable;

public class CYK {

    // -----------------------------------------------------------------
	// Constants
	// -----------------------------------------------------------------

    public static final Character INITIAL_VARIABLE = 'S';

    // -----------------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------------

    private String string;
    private static boolean isGenerated;
    private static Hashtable<Character, ArrayList<String>> grammar = new Hashtable<>();

    // -----------------------------------------------------------------
	// Methods
	// -----------------------------------------------------------------

    public CYK(String string) {
        this.string = string;
        isGenerated = false;
    }

    public String[][] buildTableCYK() {
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
}