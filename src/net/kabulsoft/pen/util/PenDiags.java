package net.kabulsoft.pen.util;

import java.awt.ComponentOrientation;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class PenDiags 
{
	public static final int YN = JOptionPane.YES_NO_OPTION;
	public static final int YNC = JOptionPane.YES_NO_CANCEL_OPTION;
	public static final String SUCCESS = "اطلاعات مورد نظر با موفقیت ذخیره شد!";
	public static final String FAILURE = "موفق به ذخیره اطلاعات نشد!";
	public static final String SAVEASK = "اطلاعات جدید ذخیره شوند؟";
	public static final String DELETEASK = "موارد انتخابی حذف شوند؟";
	public static final String OPDONE = "عملیات با موفقیت انجام شد!";
	public static final String OPFAIL = "موفق به انجام عملیات نشد!";
	private static JLabel msg = new JLabel();
	
	static{
		msg.setHorizontalAlignment(JLabel.RIGHT);
		msg.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
	}
	
	public static void showMsg(String text)
	{
		msg.setText(text);
		JOptionPane.showMessageDialog(null, msg, null, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showWarn(String text)
	{
		msg.setText(text);
		JOptionPane.showMessageDialog(null, msg, null, JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showErr(String text)
	{
		msg.setText(text);
		JOptionPane.showMessageDialog(null, msg, null, JOptionPane.ERROR_MESSAGE);
	}
	
	public static int showConf(String text, int options)
	{
		msg.setText(text);
		return JOptionPane.showConfirmDialog(null, msg, null, options);
	}
	
	public static Object showInput(String text)
	{
		msg.setText(text);
		return JOptionPane.showInputDialog(msg);
	}
}
