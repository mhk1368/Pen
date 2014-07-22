package net.kabulsoft.pen.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;


public class OpenFrame implements ActionListener {
	
	private Class<?> frame;
	private Class<?> [] types;
	private Object[] params;

	public OpenFrame(Class<?> frame) {
		this.frame = frame;
	}
	
	public OpenFrame(Class<?> frame, Class<?> [] types, Object [] params) {
		this.frame = frame;
		this.params = params;
		this.types = types;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		openFrame(this.frame, this.types, this.params);
	}
	
	public static void openFrame(Class<?> frame, Class<?> [] types, Object [] params)
	{
		try {
			boolean isOpen = frame.getField("isOpen").getBoolean(frame.getField("isOpen"));

			if (!isOpen) {
				frame.getConstructor(types).newInstance(params);
			} 
			else if (frame.getSuperclass().getSimpleName().equals("JFrame"))
			{
				JFrame jf = (JFrame) frame.getField("self").get(frame.getField("self"));
				jf.setExtendedState(JFrame.NORMAL);
				jf.requestFocus();
			} 
			else if (frame.getSuperclass().getSimpleName().equals("JDialog"))
			{
				JDialog jd = (JDialog) frame.getField("self").get(frame.getField("self"));
				jd.requestFocus();
			}
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}