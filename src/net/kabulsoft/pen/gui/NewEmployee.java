package net.kabulsoft.pen.gui;

import java.awt.ComponentOrientation;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class NewEmployee extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static NewEmployee self;
	public SearchEmployee parent;
	private JTabbedPane tabs;
	private EmployeeProfile profile;
	private EmployeeFinance empFin;
	
	public NewEmployee(){
		this(0);
	}

	public NewEmployee(int id) {
		
		isOpen = true;
		self = this;
		profile = new EmployeeProfile(id);
		
		if(id == 0){
			setTitle("ایجاد کارمند جدید");
			add(profile);
		}
		else{
			setTitle("پرونده کارمند");
			tabs = new JTabbedPane();
			tabs.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			empFin = new EmployeeFinance(id);
			tabs.add("مشخصات کارمند", profile);
			tabs.add("دریافتهای نقدی", empFin);
			add(tabs);
		}
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				isOpen = false;
			}
		});
		
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void setParent(SearchEmployee parent){
		profile.parent = parent;
	}
}
