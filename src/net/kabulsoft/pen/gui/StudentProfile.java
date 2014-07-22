package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.border.LineBorder;

import net.kabulsoft.pen.db.Course;
import net.kabulsoft.pen.db.CourseInfo;
import net.kabulsoft.pen.db.StudentInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class StudentProfile extends JPanel {

	private static final long serialVersionUID = 1L;
	private SearchStudent parent = null;
	private JLabel pic;
	private JTextField fields[];
	private JComboBox<Object> grade;
	private JComboBox<Course> course;
	private JButton save, transfer;
	private JRadioButton male, female;
	private JCheckBox active;
	private StudentInfo info = new StudentInfo();
	private CourseInfo courseInfo = new CourseInfo();
	private Vector<String> student;
	private byte[] st_image = null;
	private Course curCourse;
	private int sid;

	public StudentProfile(int id) {
		
		sid = id;
		JLabel title;
		if (id == 0) {
			title = new JLabel(" ایجاد متعلم جدید");
		} else {
			title = new JLabel(" ویرایش متعلم");
		}
		
		setLayout(new BorderLayout());
		
		// Top Panel
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks1.png"));
		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(new Color(9, 87, 151));
		top.add(title, BorderLayout.EAST);
		top.add(ks, BorderLayout.WEST);
		add(top, BorderLayout.NORTH);

		// center Panel
		JPanel center1 = new JPanel();
		center1.setFocusCycleRoot(true);
		center1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		String texts[] = { "نمبر اساس:", "نام:", "نام پدر:", "نام فامیلی:", "نمبر تذکره:", "شماره تلفن:", "ولایت:", "آدرس:", "صنف:", "جنس:" };
		JLabel [] labels = new JLabel[10];
		labels[7] = new JLabel(texts[7]);
		labels[8] = new JLabel(texts[8]);
		labels[9] = new JLabel(texts[9]);
		fields = new JTextField[8];
		grade = new JComboBox<>(new Object[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
		grade.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)grade.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);

		course = new JComboBox<Course>();
		course.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)course.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		
		male = new JRadioButton("مذکر");
		female = new JRadioButton("مونث");
		active = new JCheckBox("برحال");
		active.setSelected(true);
		male.setSelected(true);
		male.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		female.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		active.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		ButtonGroup buttons = new ButtonGroup();
		buttons.add(male);
		buttons.add(female);
		
		Reset reset = new Reset();
		ActionListener register = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveData();
			}
		};

		GroupLayout layout = new GroupLayout(center1);
		center1.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		ParallelGroup p1 = layout.createParallelGroup();
		ParallelGroup p2 = layout.createParallelGroup();
		hGroup.addGroup(p1).addGroup(p2);

		for (int i = 0; i < fields.length; i++) 
		{
			labels[i] = new JLabel(texts[i]);
			fields[i] = new JTextField();
			fields[i].addActionListener(register);
			fields[i].addFocusListener(reset);
			fields[i].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			fields[i].setHorizontalAlignment(JTextField.RIGHT);
			
			p1.addComponent(labels[i]);
			p2.addComponent(fields[i]);

			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(labels[i]).addComponent(fields[i]));
		}

		p1.addComponent(labels[8]);
		p2.addGroup(layout.createSequentialGroup().addComponent(grade).addComponent(course));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(labels[8])
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(grade).addComponent(course)));
		
		p1.addComponent(labels[9]);
		p2.addGroup(layout.createSequentialGroup().addComponent(male).addComponent(female).addComponent(active));
		
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(labels[9])
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(male).addComponent(female).addComponent(active)));
		
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
		
		JPanel center2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		center2.setPreferredSize(new Dimension(160, 200));
		pic = new JLabel(new ImageIcon("images/pic.jpg"));
		pic.setPreferredSize(new Dimension(150, 200));
		pic.setBorder(new LineBorder(Color.BLACK));
		pic.setToolTipText("تصویر (200 * 150)");
		pic.setBackground(Color.WHITE);
		JLabel tip = new JLabel("تصویر (200 * 150)");
		tip.setPreferredSize(new Dimension(150, 15));
		tip.setHorizontalAlignment(JLabel.CENTER);
		center2.add(pic);
		center2.add(tip);

		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(new LineBorder(Color.GRAY, 1));
		center.add(center1, BorderLayout.CENTER);
		center.add(center2, BorderLayout.WEST);
		add(center, BorderLayout.CENTER);

		// Bottom Panel
		JPanel bottom = new JPanel(new BorderLayout());	
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottom.add(bright, BorderLayout.EAST);
		bottom.add(bleft, BorderLayout.WEST);
		add(bottom, BorderLayout.SOUTH);
		save = new JButton("دخیره");
		bright.add(save);
		save.addActionListener(register);
		
		if (id == 0) {
			fields[0].setText(String.valueOf(info.maxStudentId()));
			putCourses(1);
		} 
		else {
			student = info.findStudent(id);
			for (int i = 0; i < fields.length; i++) {
				fields[i].setText(student.get(i));
			}
			if(!info.isNew(sid)){
				grade.setEnabled(false);	
			}
			
			fields[0].setEditable(false);
			findCourse(Integer.parseInt(student.get(8)));
			if(student.get(9) != null){
				if(student.get(9).equals("f")){
					female.setSelected(true);
				}
			}
			String state = student.get(10);
			active.setSelected(state.equals("a"));
			active.setEnabled(!state.equals("g"));
			
			ImageIcon img = info.findImage(id);
			if(img != null){
				pic.setIcon(img);
			}
			
			transfer = new JButton("ثبت انتقالی");
			bleft.add(transfer);
			transfer.setEnabled(!state.equals("g"));
			transfer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					transferStudent();
				}
			});
		}
		
		pic.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				addImage();
			}
		});
				
		grade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(grade.getSelectedIndex() < 12){
					putCourses((int)grade.getSelectedItem());
				}
			}
		});
	}
	
	public void findCourse(int g)
	{
		g = (g > 12)? 12 : g;
		grade.setSelectedItem(g);
		putCourses(g);
		curCourse = courseInfo.studentCourse(sid, g, new PersianCalendar().getPersianYear());
		course.setSelectedItem(curCourse);
	}
	
	private void putCourses(int g)
	{
		int y = new PersianCalendar().getPersianYear();
		Vector<Course> c = courseInfo.searchClass(y, g);
		Iterator<Course> iterator = c.iterator();
		course.removeAllItems();
		course.addItem(null);
		while (iterator.hasNext())
		{
			course.addItem(iterator.next());
		}
	}
	
	private void saveData()
	{
		if (!Validation.validateData(fields)) {
			return;
		}
		String values[] = new String[11];
		for (int i = 0; i < fields.length; i++) {
			values[i] = fields[i].getText();
		}
		values[8] = grade.getSelectedItem().toString();
		values[9] = (male.isSelected())? "m" : "f";
		values[10] = (active.isSelected())? "a" : "p";
		if(!active.isEnabled()) values[10] = "g";
		// New
		if (sid == 0) 
		{
			if (info.insertStudent(values)) 
			{
				if(course.getSelectedItem() != null)
				{
					int id = Integer.parseInt(fields[0].getText());
					int cid = ((Course)course.getSelectedItem()).id;
					int g = (int)grade.getSelectedItem();
					courseInfo.assignStudent(id, cid, g);
				}
				info.saveImage(st_image, Integer.parseInt(values[0]));
				PenDiags.showMsg(PenDiags.SUCCESS);
				if(parent != null){
					parent.renderData();
				}
				Validation.clearFields(fields);
				grade.setSelectedIndex(0);
				fields[0].setText(String.valueOf(info.maxStudentId()));
			}
		} 
		else 
		// Edit
		{
			if (info.editStudent(values)) 
			{
				if(course.getSelectedItem() != null)
				{
					int id = Integer.parseInt(fields[0].getText());
					int cid = ((Course)course.getSelectedItem()).id;
					int g = (int)grade.getSelectedItem();
					
					if(curCourse == null){
						courseInfo.assignStudent(id, cid, g);
					}else{
						courseInfo.switchStudent(id, curCourse.id, cid);
					}
					grade.setEnabled(false);
				}
				info.saveImage(st_image, sid);
				PenDiags.showMsg(PenDiags.SUCCESS);
				if(parent != null){
					parent.renderData();
				}
			}
		}
	}
	
	private void addImage()
	{
		FileDialog diag = new FileDialog(parent);
        diag.setResizable(true);
        diag.setVisible(true);
        
		String fn = diag.getFile();
		if(fn == null) return;
		fn = fn.toLowerCase();
		String [] exts = {".jpg", ".jpeg", ".png", ".gif"};
		boolean yes = false;
		for(int i=0; i<exts.length; i++){
			if(fn.endsWith(exts[i])){
				yes = true;
				break;
			}
		}
		if(!yes){
			PenDiags.showMsg("فایل انتخابی یک فایل تصویری نیست!");
			return;
		}
		
		String path = diag.getDirectory()+fn;
		ImageIcon image = new ImageIcon(path);
		if(image.getIconWidth() > 160 || image.getIconHeight() > 210){
			PenDiags.showMsg("اندازه تصویر بزرگتر از اندازه معیاری است!");
			return;
		}
		pic.setIcon(image);
		
		try 
		{
			FileInputStream fis = new FileInputStream(new File(path));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			for(int readNum; (readNum = fis.read(buf)) != -1;){
				bos.write(buf, 0, readNum);
			}
			st_image = bos.toByteArray();
			fis.close();
		} 
		catch (Exception e1) {}
	}
	
	private void transferStudent()
	{
		Object value = PenDiags.showInput("نام مکتب یا شرح را وارد نمایید:");
		if (value == null) return;
		String desc = value.toString();
		if(desc.trim().length() == 0){
			PenDiags.showMsg("وارد کردن شرح الزامی است!");
			return;
		}
		if(info.transferStudent(sid, student.get(8), desc)){
			PenDiags.showMsg("انتقال متعلم انجام شد!");
			active.setSelected(false);
		}
	}
		
	public void setParent(SearchStudent parent){
		this.parent = parent;
	}
}



