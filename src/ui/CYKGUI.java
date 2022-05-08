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

public class CYKGUI {

	// -----------------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------------

	@FXML
	private VBox mainVBox;

	@FXML
	private GridPane gridPane;
	/*
	@FXML
	private JFXTextField string;

	@FXML
	private Label result;
	*/
	private ArrayList<Control[]> grammar;

	// -----------------------------------------------------------------
	// Relations
	// -----------------------------------------------------------------

	//private CYK cyk;

	// -----------------------------------------------------------------
	// Methods
	// -----------------------------------------------------------------

	public CYKGUI() {
		grammar = new ArrayList<>();
		//cyk = new CYK();
	}
	/*
	@FXML
	public void doCYKAlgorithm() {
		boolean isGenerated = cyk.checkStringGeneration(string.getText());
		if (isGenerated)
			result.setText("True");
		else
			result.setText("False");
	}
	*/
	public void initialize() {
		Label lb = nonTerminalLabel('S');
		JFXTextField tf = configureNewTextField();
		gridPane.add(lb, 0, 0);
		gridPane.add(tf, 1, 0);
		Control[] row = new Control[2];
		row[0] = lb;
		row[1] = tf;
		grammar.add(row);
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
		tf.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && tf.getText()!=null && !tf.getText().equals("")){
				String[] productions = tf.getText().split("|");
				for(int i=0; i<productions.length; i++) {
					char letter = productions[i].charAt(0);
					if(Character.isUpperCase(letter) && !containsInGrammar(letter)) {
						Label lb = nonTerminalLabel(productions[i].charAt(0));
						JFXTextField tfChild = configureNewTextField();
						gridPane.add(lb, 0, grammar.size());
						gridPane.add(tfChild, 1, grammar.size());
						Control[] row = new Control[2];
						row[0] = lb;
						row[1] = tfChild;
						grammar.add(row);
						mainVBox.getChildren().set(1, gridPane);
					}
				}
			}
		});
		tf.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				verifyProductions(tf, event);
			}
		});
		setStyle(tf);
		tf.setPrefWidth(200);
		return tf;
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
		control.setStyle("-fx-font-family: Consolas; -fx-font-weight: bold; -fx-font-size: 24px");
	}
	
	public void verifyProductions(JFXTextField tf, KeyEvent event) {
		String txt = tf.getText();
		int lg = txt.length();
		String st = event.getCharacter();
		char ch = st.charAt(0);
		boolean consume = true;
		if (Character.isAlphabetic(ch) || st.equals("|") || st.equals("&")) {
			if (lg == 0) {
				if (!st.equals("|") && !st.equals("&"))
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