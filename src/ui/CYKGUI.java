package ui;

import com.jfoenix.controls.JFXTextField;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CYKGUI {

	@FXML
	private VBox mainVBox;
	@FXML
	private GridPane gridpane;
	@FXML
	private JFXTextField productions1;

	@FXML
	public void verifyProductions(KeyEvent event) {
		JFXTextField tf = (JFXTextField) event.getSource();
		String txt = tf.getText();
		int lg = txt.length();
		String st = event.getCharacter();
		char ch = st.charAt(0);
		boolean consume = true;
		if(Character.isAlphabetic(ch) || st.equals("|") || st.equals("&")) {
			if(lg==0) {
				if(!st.equals("|") && !st.equals("&"))
					consume = false;
			} else if(lg==1){
				char lc = txt.charAt(lg-1);
				if(Character.isUpperCase(lc)) {
					if(Character.isUpperCase(ch))
						consume = false;
				} else if(st.equals("|"))
					consume = false;
			} else {
				char lc = txt.charAt(lg-1);
				char pu = txt.charAt(lg-2);
				if(Character.isLowerCase(ch) && "|".equals(lc+""))
					consume = false;
				else if(Character.isUpperCase(ch)) {
					if(Character.isUpperCase(lc) && !Character.isUpperCase(pu))
						consume = false;
					else if("|".equals(lc+""))
						consume = false;
				} else if(st.equals("&") && "|".equals(lc+""))
					consume = false;
				else if(st.equals("|") && !"|".equals(lc+""))
					consume = false;
			}
		}
		if(consume)
			event.consume();
	}
	
	public void initialize() {
		productions1.caretPositionProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(Integer.parseInt(newValue+"") != productions1.getText().length()) {
						System.out.println("old: "+oldValue+", new: "+newValue);
						productions1.positionCaret(productions1.getText().length());
				}
			}
			
		});
	}
}
