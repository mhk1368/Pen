package net.kabulsoft.pen.gui;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

import net.kabulsoft.pen.util.PenDiags;

public class Validation {
	public static boolean validateData(JTextField fields[]) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getText().equals("")) {
				fields[i].setBackground(Color.red);
				PenDiags.showMsg("تکمیل این بخش ضروری است!");
				return false;
			}
		}
		return true;
	}

	public static void clearFields(JTextField fields[]) 
	{
		for (int i = 0; i < fields.length; i++) {
			fields[i].setText("");
		}
		fields[0].grabFocus();
	}
}

class Reset extends FocusAdapter {
	public void focusGained(FocusEvent f) {
		JTextField tf = (JTextField) f.getSource();
		tf.setBackground(Color.WHITE);
	}
}