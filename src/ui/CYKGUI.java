/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @Authors: Juan Esteban Caicedo and Carlos Jimmy Pantoja.
 * @Date: May, 15th 2022
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package ui;

import java.util.ArrayList;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.CYK;

public class CYKGUI {

	// -----------------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------------

	@FXML
	private VBox mainVBox, grammarSpace;

	@FXML
	private GridPane gridPane;

	@FXML
	private Label result;

	private JFXTextField string;

	private ArrayList<Control[]> grammar;

	private ArrayList<Character> alphabet;

	private int gap;

	// -----------------------------------------------------------------
	// Relations
	// -----------------------------------------------------------------

	private CYK cyk;

	// -----------------------------------------------------------------
	// Methods
	// -----------------------------------------------------------------

	/**
     * Name: CYKGUI <br>
     * <br> GUI constructor method. <br>
    */
	public CYKGUI() {
		grammar = new ArrayList<>();
		alphabet = new ArrayList<>();
		cyk = new CYK();
		gap = 0;
	}

	/**
     * Name: doCYKAlgorithm <br>
     * <br> Method used to begin the process of doing the CYK algorithm to a specified string with a specified CNF grammar. <br><br>
	 * <b>pre: </b> The user has already entered the grammar and the string in the GUI. <br><br>
	 * <b>post: </b> The boolean result of applying the CYK algorithm is displayed in the GUI. <br>
    */
	@FXML
	public void doCYKAlgorithm() {
		ArrayList<Character> nonTerminals = obtainNonTerminals();
		ArrayList<ArrayList<String>> productions = obtainProductions();
		cyk.fillGrammar(nonTerminals, productions);
		String data = string.getText().equals("") ? "&" : string.getText();
		boolean isGenerated = cyk.checkStringGeneration(data);
		if (isGenerated) {
			result.setText(data + ": True");
			result.setStyle("-fx-font-family: Consolas; -fx-font-size: 24px; -fx-text-fill: green; -fx-font-weight: bold");
		} else {
			result.setText(data + ": False");
			result.setStyle("-fx-font-family: Consolas; -fx-font-size: 24px; -fx-text-fill: red; -fx-font-weight: bold");
		}
	}

	/**
     * Name: obtainProductions <br>
     * <br> Private method used to get the productions of the grammar from the GUI. <br><br>
	 * <b>pre: </b> The user has already entered all the grammar productions in the GUI. <br><br>
	 * <b>post: </b> The productions of the grammar are obtained from the GUI. <br>
	 * @return An ArrayList of ArrayList of String with the productions of the grammar specified in the GUI.
    */
	private ArrayList<ArrayList<String>> obtainProductions() {
		ArrayList<ArrayList<String>> productions = new ArrayList<>();
		for (int i = 0; i < grammar.size(); i++) {
			JFXTextField tf = (JFXTextField) grammar.get(i)[1];
			String[] temp = tf.getText().split("\\|");
			ArrayList<String> set = new ArrayList<>();
			for (int j = 0; j < temp.length; j++) {
				set.add(temp[j]);
			}
			productions.add(set);
		}
		return productions;
	}

	/**
     * Name: obtainNonTerminals <br>
     * <br> Private method used to get the non-terminals symbols of the grammar from the GUI. <br><br>
	 * <b>pre: </b> The user has already entered all the non-terminals symbols of the grammar in the GUI. <br><br>
	 * <b>post: </b> The non-terminals symbols of the grammar are obtained from the GUI. <br>
	 * @return An ArrayList of Character with the non-terminals symbols of the grammar specified in the GUI.
    */
	private ArrayList<Character> obtainNonTerminals() {
		ArrayList<Character> nonTerminals = new ArrayList<>();
		for (int i = 0; i < grammar.size(); i++) {
			Label lb = (Label) grammar.get(i)[0];
			nonTerminals.add(lb.getText().charAt(0));
		}
		return nonTerminals;
	}

	/**
     * Name: initialize <br>
     * <br> GUI initializer method to configure initial parameters. <br><br>
	 * <b>post: </b> Initial parameters in the GUI are configured. <br>
    */
	public void initialize() {
		string = configureNewTextField();
		additionalStringProperties(string);
		mainVBox.getChildren().add(4, string);
		autoGenerateSpace('S', 0);
	}

	/**
     * Name: rebuildGridPane <br>
     * <br>Private method used to rebuild the grammar in the GUI by removing the variables that aren't called anymore in some production. <br><br>
	 * <b>post: </b> The variables that aren't called anymore in some production are removed. <br>
    */
	private void rebuildGridPane() {
		alphabet = new ArrayList<>();
		for (int i = 0; i < grammar.size(); i++) {
			if (!existInProductions(((Label) grammar.get(i)[0]).getText().charAt(0)) && i != 0) {
				gridPane.getChildren().remove(grammar.get(i)[0]);
				gridPane.getChildren().remove(grammar.get(i)[1]);
				grammar.remove(i);
				gap++;
				i--;
			} else
				updateAlphabet((JFXTextField) grammar.get(i)[1]);
		}
		checkString();
	}

	/**
     * Name: checkString <br>
     * <br> Private method used to verify in the GUI that the string characters are present in the grammar productions after characters have been deleted in a text field. <br><br>
	 * <b>post: </b> Verification of the presence of the string characters in the grammar productions is done. <br>
    */
	private void checkString() {
		String alph = "", str = "";
		for (Character temp : alphabet)
			alph += temp;
		String[] letters = string.getText().split("|");
		for (String letter : letters) {
			if (alph.contains(letter))
				str += letter;
		}
		string.setText(str);
	}

	/**
     * Name: updateAlphabet <br>
     * <br> Private method used to update the alphabet as lowercase letters are used in the grammar. <br><br>
	 * <b>post: </b> Update of the alphabet as lowercase letters are used in the grammar is done. <br>
	 * @param tf - Text field from the GUI - tf = JFXTextField, tf != null
    */
	private void updateAlphabet(JFXTextField tf) {
		String[] letters = tf.getText().split("\\|");
		for (int i = 0; i < letters.length; i++) {
			if (letters[i].length() == 1 && letters[i].charAt(0) != '&')
				updateAlphabet(letters[i].charAt(0));
		}
	}

	/**
     * Name: updateAlphabet <br>
     * <br> Private method used to add a lowercase letter to the alphabet if it doesn't exist in the system. <br><br>
	 * <b>post: </b> The addition or not of a lowercase letter to the alphabet is determined. <br>
	 * @param letter - Non-terminal symbol - letter = char, tf != null
    */
	private void updateAlphabet(char letter) {
		for (int i = 0; i < alphabet.size(); i++) {
			if (letter == alphabet.get(i))
				return;
		}
		alphabet.add(letter);
	}

	/**
     * Name: existInProductions <br>
     * <br> Private method used to checks if there is a letter that is called in a production. <br><br>
	 * <b>post: </b> Verification of a letter called in a production is done. <br>
	 * @param letter - Letter in the grammar - letter = char, tf != null
	 * @return A boolean with true if the letter is called in a production, otherwise with false.
    */
	private boolean existInProductions(char letter) {
		for (int i = 0; i < grammar.size(); i++) {
			String[] letters = ((JFXTextField) grammar.get(i)[1]).getText().split("|");
			for (int j = 0; j < letters.length; j++) {
				if (letters[j].equals(letter + ""))
					return true;
			}
		}
		return false;
	}

	/**
     * Name: configureNewTextField <br>
     * <br> Private method used to configure a new text field added in the GUI. <br><br>
	 * <b>pre: </b> A new text field has been generated in the GUI. <br><br>
	 * <b>post: </b> The new text field added in the GUI is configured. <br>
	 * @return A JFXTextField with all the necessary configuration to deal with different stimulus.
    */
	private JFXTextField configureNewTextField() {
		JFXTextField tf = new JFXTextField("") {

			@Override
			public void cut() {
			}

			@Override
			public void copy() {
			}

			@Override
			public void paste() {
			}
		};
		tf.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() != tf.getText().length())
				tf.positionCaret(tf.getText().length());
		});
		tf.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.isEmpty())
				tf.deselect();
		});
		setStyle(tf);
		tf.setPrefSize(200, 44);
		tf.setMinSize(JFXTextField.USE_PREF_SIZE, JFXTextField.USE_PREF_SIZE);
		tf.setMaxSize(JFXTextField.USE_PREF_SIZE, JFXTextField.USE_PREF_SIZE);
		return tf;
	}

	/**
     * Name: additionalProductionsProperties <br>
     * <br> Private method used to add production verification properties to a text field. <br><br>
	 * <b>pre: </b> A new variable has been written in the GUI. <br><br>
	 * <b>post: </b> Production verification properties are added to a text field. <br>
	 * @param tf - Text field from the GUI - tf = JFXTextField, tf != null
    */
	private void additionalProductionsProperties(JFXTextField tf) {
		tf.focusedProperty().addListener((observable, oldValue, newValue) -> {
			checkEndProductions(tf);
			checkRepeatedProductions(tf);
			if (!newValue && tf.getText() != null && !tf.getText().equals("")) {
				String[] productions = tf.getText().split("|");
				for (int i = 0; i < productions.length; i++) {
					char letter = productions[i].charAt(0);
					if (Character.isUpperCase(letter) && !containsInGrammar(letter))
						autoGenerateSpace(letter, grammar.size());
				}
			}
			rebuildGridPane();
		});
		tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (KeyCode.ENTER == event.getCode())
					string.requestFocus();
			}
		});
		tf.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				verifyProductions(tf, event);
			}
		});
	}

	/**
     * Name: additionalStringProperties <br>
     * <br> Private method used to add verification properties of the input string to a text field. <br><br>
	 * <b>pre: </b> A new variable has been written in the GUI. <br><br>
	 * <b>post: </b> Verification properties of the input string are added to a text field. <br>
	 * @param tf - Text field from the GUI - tf = JFXTextField, tf != null
    */
	private void additionalStringProperties(JFXTextField tf) {
		tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (KeyCode.ENTER == event.getCode())
					doCYKAlgorithm();
			}
		});
		tf.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				verifyString(event);
			}
		});
	}

	/**
     * Name: verifyString <br>
     * <br> Private method used to check the input string as it is typed. <br><br>
	 * <b>post: </b> The input string is checked as it is typed. <br>
	 * @param event - Event representing the entering of a key in the string text field - event = KeyEvent
    */
	private void verifyString(KeyEvent event) {
		String st = event.getCharacter();
		if (event.getCharacter().length() != 1)
			return;
		char ch = st.charAt(0);
		boolean exists = false;
		for (int i = 0; i < alphabet.size() && !exists; i++) {
			if (ch == alphabet.get(i))
				exists = true;
		}
		if (!exists)
			event.consume();
	}

	/**
     * Name: checkRepeatedProductions <br>
     * <br> Private method used to remove repeated productions in a text field. <br><br>
	 * <b>post: </b> The repeated productions in a text field are removed. <br>
	 * @param tf - Text field from the GUI - tf = JFXTextField, tf != null
    */
	private void checkRepeatedProductions(JFXTextField tf) {
		if (tf.getText() != null && !tf.getText().equals("")) {
			String[] productions = tf.getText().split("\\|");
			for (int i = 0; i < productions.length; i++) {
				for (int j = i + 1; j < productions.length; j++) {
					if (productions[i] != null && productions[i].equals(productions[j]))
						productions[j] = null;
				}
			}
			String text = productions[0];
			for (int i = 1; i < productions.length; i++) {
				if (productions[i] != null) {
					text += "|" + productions[i];
				}
			}
			tf.setText(text);
		}
	}

	/**
     * Name: checkEndProductions <br>
     * <br> Private method used to remove characters that aren't suitable to end a text field. <br><br>
	 * <b>post: </b> Characters that aren't suitable to end a text field are removed. <br>
	 * @param tf - Text field from the GUI - tf = JFXTextField, tf != null
    */
	private void checkEndProductions(JFXTextField tf) {
		if (tf.getText() != null) {
			int lg = tf.getText().length();
			if (lg == 1 && Character.isUpperCase(tf.getText().charAt(0)))
				tf.setText("");
			else if (lg > 1 && tf.getText().charAt(lg - 1) == '|')
				tf.setText(tf.getText().substring(0, lg - 1));
			else if (lg > 2 && Character.isUpperCase(tf.getText().charAt(lg - 1))
					&& tf.getText().charAt(lg - 2) == '|')
				tf.setText(tf.getText().substring(0, lg - 2));
		}
	}

	/**
     * Name: autoGenerateSpace <br>
     * <br> Private method used to autogenerate a new text field to write new variable productions. <br><br>
	 * <b>pre: </b> A new variable has been written in the GUI. <br><br>
	 * <b>post: </b> A new text field is autogenerated to write new variable productions on it. <br>
	 * @param letter - New variable written in a previous variable production - letter = char, letter != null
	 * @param row - Row where the text field is going to be inserted in the gridPane - row = int, row != null
    */
	private void autoGenerateSpace(char letter, int row) {
		Label lb = nonTerminalLabel(letter);
		JFXTextField tf = configureNewTextField();
		additionalProductionsProperties(tf);
		gridPane.add(lb, 0, row + gap);
		gridPane.add(tf, 1, row + gap);
		Control[] controls = new Control[2];
		controls[0] = lb;
		controls[1] = tf;
		grammar.add(controls);
		grammarSpace.getChildren().set(0, gridPane);
	}

	/**
     * Name: containsInGrammar <br>
     * <br> Private method used to check that a variable is contained in the grammar. <br><br>
	 * <b>pre: </b> A new variable has been written in the GUI. <br><br>
	 * <b>post: </b> Verification of a variable contained in the grammar is done. <br>
	 * @param letter - Variable to verify - letter = char, letter != null
	 * @return A boolean with true if the variable is contained in the grammar, otherwise with false.
    */
	private boolean containsInGrammar(char letter) {
		for (int i = 0; i < grammar.size(); i++) {
			Label lb = (Label) grammar.get(i)[0];
			if (letter == lb.getText().charAt(0))
				return true;
		}
		return false;
	}

	/**
     * Name: nonTerminalLabel <br>
     * <br> Private method used to create the label of a variable. <br><br>
	 * <b>pre: </b> A new variable has been written in the GUI. <br><br>
	 * <b>post: </b> The label of a variable is created. <br>
	 * @param letter - Variable to create the label - letter = char, letter != null
	 * @return A Label representing a variable.
    */
	private Label nonTerminalLabel(char letter) {
		Label lb = new Label(letter + " -> ");
		setStyle(lb);
		return lb;
	}

	/**
     * Name: setStyle <br>
     * <br> Private generic method used to apply CSS style to Controls in general (Labels and Text Fields). <br><br>
	 * <b>pre: </b> A new variable has been written in the GUI. <br><br>
	 * <b>post: </b> CSS style is applied to Controls in general (Labels and Text Fields). <br>
	 * @param control - Any type of control - control = Type that extends Control, letter != null
    */
	private <Type extends Control> void setStyle(Type control) {
		control.setStyle("-fx-font-family: Consolas; -fx-font-size: 24px");
	}

	/**
     * Name: verifyProductions <br>
     * <br> Private method used to check productions as they are typed. <br><br>
	 * <b>post: </b> The input productions are checked as they are typed. <br>
	 * @param textField - Text field from the GUI - textField = JFXTextField, textField != null
	 * @param event - Event representing the entering of a key in a productions text field - event = KeyEvent
    */
	private void verifyProductions(JFXTextField textField, KeyEvent event) {
		String oldText = textField.getText();
		int oldLength = oldText.length();
		if (event.getCharacter().length() != 1)
			return;
		char newCharacter = event.getCharacter().charAt(0);
		boolean consume = true;
		if ((Character.isAlphabetic(newCharacter) || (newCharacter == '|') || (newCharacter == '&'))
				&& (newCharacter != 'S')) {
			if (oldLength == 0) {
				if (newCharacter != '|')
					consume = false;
			} else if (oldLength == 1) {
				char lastOldCharacter = oldText.charAt(oldLength - 1);
				if (Character.isUpperCase(lastOldCharacter)) {
					if (Character.isUpperCase(newCharacter))
						consume = false;
				} else if (newCharacter == '|')
					consume = false;
			} else {
				char lastOldCharacter = oldText.charAt(oldLength - 1);
				char penultimateOldCharacter = oldText.charAt(oldLength - 2);
				if (Character.isLowerCase(newCharacter) && (lastOldCharacter == '|'))
					consume = false;
				else if (Character.isUpperCase(newCharacter)) {
					if (Character.isUpperCase(lastOldCharacter) && !Character.isUpperCase(penultimateOldCharacter))
						consume = false;
					else if (lastOldCharacter == '|')
						consume = false;
				} else if ((newCharacter == '&') && (lastOldCharacter == '|') && grammar.get(0)[1].equals(textField))
					consume = false;
				else if ((newCharacter == '|') && (lastOldCharacter != '|')) {
					consume = false;
					if (Character.isUpperCase(lastOldCharacter) && !Character.isUpperCase(penultimateOldCharacter))
						consume = true;
				}
			}
		}
		if (consume)
			event.consume();
	}
}