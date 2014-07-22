package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.LineBorder;

import net.kabulsoft.pen.db.Course;
import net.kabulsoft.pen.db.CourseInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class StudentEnrollment extends JDialog {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static StudentEnrollment self;
	public Members parent = null;
	private JComboBox<Course> courses;
	private JLabel title, tip;
	private JButton save, cancel;
	private CourseInfo info = new CourseInfo();
	private int stids[];
	private int cid;
	private int grade;
	private int work;
	
	public StudentEnrollment(int c, int ids[], int job){
		isOpen = true;
		self = this;
		stids = ids;
		work = job;
		
		int y = new PersianCalendar().getPersianYear();
		switch(work){
		case  1:
			setTitle("ارتقاء متعلمین");
			title = new JLabel("ارتقاء متعلمین به صنف بالاتر");
			save = new JButton("ارتقاء متعلمین");
			grade = c+1;
			break;
		case 2:
			setTitle("تغییر صنف متعلمین");
			title = new JLabel("تغییر صنف متعلمین");
			save = new JButton("تبدیل صنف");
			Course cv = info.findCourse(c);
			grade = cv.grade;
			y = cv.year;
			cid = c;
			break;
		case 3:
			setTitle("ثبت متعلمین در صنف");
			title = new JLabel("ثبت متعلمین در صنف");
			save = new JButton("ثبت متعلمین");
			grade = c;
			break;
		}
		
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		panel1.setBackground(new Color(103, 64, 228));
		panel1.add(title);
		add(panel1, BorderLayout.NORTH);
		
		Vector<Course> classes = info.searchClass(y, grade);
		courses = new JComboBox<>();
		courses.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)courses.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		
		Iterator<Course> iterator = classes.iterator();
		while (iterator.hasNext()) {
			courses.addItem(iterator.next());
		}
		tip = new JLabel("انتخاب صنف:");
		
		JPanel midPanel = new JPanel(new BorderLayout());
		midPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
		
		JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		GroupLayout layout = new GroupLayout(panel2);
		panel2.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		
		hGroup.addGroup(layout.createParallelGroup().addComponent(tip));
		hGroup.addGroup(layout.createParallelGroup().addComponent(courses));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(tip).addComponent(courses));
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
		
		midPanel.add(panel2, BorderLayout.CENTER);
		add(midPanel, BorderLayout.CENTER);
		JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		cancel = new JButton("انصراف");
		panel3.add(save);
		panel3.add(cancel);
		add(panel3, BorderLayout.SOUTH);
		
		setSize(350, 160);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				isOpen = false;
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				isOpen = false;
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(courses.getSelectedItem() == null){
					return;
				}
				save.setEnabled(false);
				boolean yes = false;
				for(int i=0; i<stids.length; i++)
				{
					int nc = ((Course)courses.getSelectedItem()).id;
					switch(work){
					case 1:
						if(info.upgradeStudent(stids[i], nc, grade)) yes = true;
						break;
					case 2:
						if(info.switchStudent(stids[i], cid, nc)) yes = true;
						break;
					case 3:
						if(info.assignStudent(stids[i], nc, grade)) yes = true;
						break;
					}
				}
				if(yes){
					PenDiags.showMsg(PenDiags.OPDONE);
					dispose();
					isOpen = false;
					if(parent != null){
						parent.renderData();
					}
				}
				save.setEnabled(true);
			}
		});
	}

	public void setParent(Members parent) {
		this.parent = parent;
	}
}
