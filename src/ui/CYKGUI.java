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

	public CYKGUI() {
		grammar = new ArrayList<>();
		alphabet = new ArrayList<>();
		cyk = new CYK();
		gap = 0;
	}

	@FXML
	public void doCYKAlgorithm() {
		ArrayList<Character> nonTerminals = obtainNonTerminals();
		ArrayList<ArrayList<String>> productions = obtainProductions();
		cyk.fillGrammar(nonTerminals, productions);
		String data = string.getText().equals("") ? "&" : string.getText();
		boolean isGenerated = cyk.checkStringGeneration(data);
		if (isGenerated) {
			result.setText(data+": True");
			result.setStyle(
					"-fx-font-family: Consolas; -fx-font-size: 24px; -fx-text-fill: green; -fx-font-weight: bold");
		} else {
			result.setText(data+": False");
			result.setStyle(
					"-fx-font-family: Consolas; -fx-font-size: 24px; -fx-text-fill: red; -fx-font-weight: bold");
		}
	}

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

	private ArrayList<Character> obtainNonTerminals() {
		ArrayList<Character> nonTerminals = new ArrayList<>();
		for (int i = 0; i < grammar.size(); i++) {
			Label lb = (Label) grammar.get(i)[0];
			nonTerminals.add(lb.getText().charAt(0));
		}
		return nonTerminals;
	}

	public void initialize() {
		string = configureNewTextField();
		additionalStringProperties(string);
		mainVBox.getChildren().add(4, string);
		autoGenerateSpace('S', 0);
	}

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

	private void updateAlphabet(JFXTextField tf) {
		String[] letters = tf.getText().split("\\|");
		for (int i = 0; i < letters.length; i++) {
			if (letters[i].length() == 1 && letters[i].charAt(0) != '&')
				updateAlphabet(letters[i].charAt(0));
		}
	}

	private void updateAlphabet(char letter) {
		for (int i = 0; i < alphabet.size(); i++) {
			if (letter == alphabet.get(i))
				return;
		}
		alphabet.add(letter);
	}

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

	private boolean containsInGrammar(char letter) {
		for (int i = 0; i < grammar.size(); i++) {
			Label lb = (Label) grammar.get(i)[0];
			if (letter == lb.getText().charAt(0))
				return true;
		}
		return false;
	}

	private Label nonTerminalLabel(char letter) {
		Label lb = new Label(letter + " -> ");
		setStyle(lb);
		return lb;
	}

	private <Type extends Control> void setStyle(Type control) {
		control.setStyle("-fx-font-family: Consolas; -fx-font-size: 24px");
	}

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