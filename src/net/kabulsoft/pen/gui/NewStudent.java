package net.kabulsoft.pen.gui;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NewStudent extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static NewStudent self;
	private JTabbedPane tabs;
	private StudentProfile profile;
	private StudentMarks stMarks;
	private StudentFinance stFinance;

	public NewStudent(){
		this(0);
	}
	
	public NewStudent(int id) {
		
		isOpen = true;
		self = this;
		profile = new StudentProfile(id);

		if (id == 0) {
			setTitle("ایجاد متعلم جدید");
			add(profile);
		} 
		else {
			setTitle("پرونده متعلم");
			tabs = new JTabbedPane();
			tabs.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			tabs.add("مشخصات متعلم", profile);
			stMarks = new StudentMarks(id);
			stMarks.setFrame(self);
			tabs.add("نمرات متعلم", stMarks);
			stFinance = new StudentFinance(id);
			tabs.add("پرونده مالی", stFinance);
			add(tabs);
			tabs.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) 
				{
					switch(tabs.getSelectedIndex()){
					case 1:
						stMarks.renderData();
						break;
					case 2:
						stFinance.renderData();
						break;
					}
				}
			});
		}
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				isOpen = false;
			}
		});
		
		setSize(600, 400);
		setMinimumSize(new Dimension(600, 400));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void updateCourseData(int g){
		profile.findCourse(g);
		stMarks.refreshGrade(g);
	}
		
	public void setParent(SearchStudent parent){
		profile.setParent(parent);
	}
}

