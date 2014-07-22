package net.kabulsoft.pen.gui;

import java.awt.ComponentOrientation;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class NewTeacher extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static NewTeacher self;
	public SearchTeacher parent;
	private JTabbedPane tabs;
	private TeacherProfile profile;
	private TeacherSchedule tcSch;
	private TeacherFinance tcFin;
	
	public NewTeacher(){
		this(0);
	}

	public NewTeacher(int id) {
		
		isOpen = true;
		self = this;
		profile = new TeacherProfile(id);
		
		if(id == 0){
			setTitle("ایجاد استاد جدید");
			add(profile);
		}
		else{
			setTitle("پرونده استاد");
			tcSch = new TeacherSchedule(id);
			tcFin = new TeacherFinance(id);
			tabs = new JTabbedPane();
			tabs.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			tabs.add("مشخصات استاد", profile);
			tabs.add("برنامه تدریس", tcSch);
			tabs.add("دریافتهای نقدی", tcFin);
			add(tabs);
			tabs.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if(tabs.getSelectedIndex() == 1){
						tcSch.renderData();
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
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void setParent(SearchTeacher parent){
		profile.parent = parent;
	}
}
