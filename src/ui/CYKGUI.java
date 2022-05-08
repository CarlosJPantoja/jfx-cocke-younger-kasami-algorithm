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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.CYK;

public class CYKGUI {

	// -----------------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------------

	@FXML
	private VBox mainVBox;

	@FXML
	private GridPane gridPane;

	@FXML
	private Label result;

	private JFXTextField string;
	private ArrayList<Control[]> grammar;
	private ArrayList<Character> alphabet;

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
	}
	
	@FXML
	public void doCYKAlgorithm() {
		ArrayList<Character> nonTerminals = obtainNonTerminals();
		ArrayList<ArrayList<String>> productions = obtainProductions();
		cyk.fillGrammar(nonTerminals, productions);
		boolean isGenerated = cyk.checkStringGeneration(string.getText());
		if (isGenerated)
			result.setText("True");
		else
			result.setText("False");
	}
	
	private ArrayList<ArrayList<String>> obtainProductions() {
		ArrayList<ArrayList<String>> productions = new ArrayList<>();
		for(int i=0; i<grammar.size(); i++) {
			JFXTextField tf = (JFXTextField) grammar.get(i)[1];
			String[] temp = tf.getText().split("\\|");
			ArrayList<String> set = new ArrayList<>();
			for(int j=0; j<temp.length; j++) {
				set.add(temp[j]);
			}
			productions.add(set);
		}
		return productions;
	}

	private ArrayList<Character> obtainNonTerminals() {
		ArrayList<Character> nonTerminals = new ArrayList<>();
		for(int i=0; i<grammar.size(); i++) {
			Label lb = (Label) grammar.get(i)[0];
			nonTerminals.add(lb.getText().charAt(0));
		}
		return nonTerminals;
	}

	public void initialize() {
		autoGenerateSpace('S', 0);
		string = configureNewTextField();
		additionalsStringProperties(string);
		mainVBox.getChildren().set(4, string);
	}

	private JFXTextField configureNewTextField() {
		JFXTextField tf = new JFXTextField() {
			@Override
			public void cut() {}
			@Override
			public void copy() {}
			@Override
			public void paste() {}
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
	
	private void additionalsProductionsProperties(JFXTextField tf) {
		tf.focusedProperty().addListener((observable, oldValue, newValue) -> {
			checkEndProductions(tf);
			checkRepeatedProductions(tf);
			if (!newValue && tf.getText()!=null && !tf.getText().equals("")){
				String[] productions = tf.getText().split("|");
				for(int i=0; i<productions.length; i++) {
					char letter = productions[i].charAt(0);
					if(Character.isUpperCase(letter) && !containsInGrammar(letter))
						autoGenerateSpace(letter, grammar.size());
				}
			}
		});
		tf.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				verifyProductions(tf, event);
			}
		});
	}
	
	private void additionalsStringProperties(JFXTextField tf) {
		tf.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				verifyString(event);
			}
		});
	}

	private void verifyString(KeyEvent event) {
		String st = event.getCharacter();
		char ch = st.charAt(0);
		boolean exists = false;
		for(int i=0; i<alphabet.size() && !exists; i++) {
			if(ch == alphabet.get(i))
				exists = true;
		}
		if(!exists)
			event.consume();
	}

	private void checkRepeatedProductions(JFXTextField tf) {
		if(tf.getText()!=null && !tf.getText().equals("")) {
			String[] productions = tf.getText().split("\\|");
			for(int i=0; i<productions.length; i++) {
				for(int j=i+1; j<productions.length; j++) {
					if(productions[i]!=null && productions[i].equals(productions[j]))
						productions[j] = null;
				}
			}
			String text = productions[0];
			if(productions[0].length()==1 && productions[0].charAt(0)!='&')
				updateAlphabet(productions[0].charAt(0));
			for(int i=1; i<productions.length; i++) {
				if(productions[i]!=null) {
					text += "|"+productions[i];
					if(productions[i].length()==1 && productions[i].charAt(0)!='&')
						updateAlphabet(productions[i].charAt(0));
				}
			}
			tf.setText(text);
		}
	}

	private void updateAlphabet(char letter) {
		for(int i=0; i<alphabet.size(); i++) {
			if(letter == alphabet.get(i))
				return;
		}
		alphabet.add(letter);
	}

	private void checkEndProductions(JFXTextField tf) {
		if(tf.getText()!=null) {
			int lg = tf.getText().length();
			if(lg==1 && Character.isUpperCase(tf.getText().charAt(0)))
				tf.setText("");
			if(lg>1 && tf.getText().charAt(lg-1)=='|')
				tf.setText(tf.getText().substring(0, lg-1));
			if(lg>2 && Character.isUpperCase(tf.getText().charAt(lg-1)) 
					&& tf.getText().charAt(lg-2)=='|')
				tf.setText(tf.getText().substring(0, lg-2));
		}
	}

	private void autoGenerateSpace(char letter, int row) {
		Label lb = nonTerminalLabel(letter);
		JFXTextField tf = configureNewTextField();
		additionalsProductionsProperties(tf);
		gridPane.add(lb, 0, row);
		gridPane.add(tf, 1, row);
		Control[] controls = new Control[2];
		controls[0] = lb;
		controls[1] = tf;
		grammar.add(controls);
		mainVBox.getChildren().set(2, gridPane);
	}
	
	private boolean containsInGrammar(char letter) {
		for(int i=0; i<grammar.size(); i++) {
			Label lb = (Label) grammar.get(i)[0];
			if(letter == lb.getText().charAt(0))
				return true;
		}
		return false;
	}

	private Label nonTerminalLabel(char letter) {
		Label lb = new Label(letter+" -> ");
		setStyle(lb);
		return lb;
	}

	private <Type extends Control> void setStyle(Type control) {
		control.setStyle("-fx-font-family: Consolas; -fx-font-size: 24px");
	}
	
	public void verifyProductions(JFXTextField tf, KeyEvent event) {
		String txt = tf.getText();
		int lg = txt.length();
		String st = event.getCharacter();
		char ch = st.charAt(0);
		boolean consume = true;
		if ((Character.isAlphabetic(ch) || st.equals("|") || st.equals("&")) 
				&& !st.equals("S")) {
			if (lg == 0) {
				if (!st.equals("|"))
					consume = false;
			} else if (lg == 1) {
				char lc = txt.charAt(lg - 1);
				if (Character.isUpperCase(lc)) {
					if (Character.isUpperCase(ch))
						consume = false;
				} else if (st.equals("|"))
					consume = false;
			} else {
				char lc = txt.charAt(lg - 1);
				char pu = txt.charAt(lg - 2);
				if (Character.isLowerCase(ch) && "|".equals(lc + ""))
					consume = false;
				else if (Character.isUpperCase(ch)) {
					if (Character.isUpperCase(lc) && !Character.isUpperCase(pu))
						consume = false;
					else if ("|".equals(lc + ""))
						consume = false;
				} else if (st.equals("&") && "|".equals(lc + ""))
					consume = false;
				else if (st.equals("|") && !"|".equals(lc + ""))
					consume = false;
			}
		}
		if (consume)
			event.consume();
	}
}