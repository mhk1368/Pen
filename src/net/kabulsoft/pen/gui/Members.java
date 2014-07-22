package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.Course;
import net.kabulsoft.pen.db.CourseInfo;
import net.kabulsoft.pen.db.Subject;
import net.kabulsoft.pen.db.SubjectInfo;
import net.kabulsoft.pen.util.PenDiags;
import net.kabulsoft.pen.util.Reports;

public class Members extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static Members self;
	private boolean isModified = false;
	private JTextField word;
	private JComboBox<Subject> subs;
	private JRadioButton all, failed, passed, unpassed;
	private ButtonGroup bg;
	private DefaultTableModel model;
	private CourseInfo info = new CourseInfo();
	private JTable table;
	private int cid;
	private Course course;

	public Members(int c) {
		
		isOpen = true;
		self = this;
		setTitle("نمرات صنف");
		cid = c;

		JPanel top = new JPanel(new BorderLayout());
		JPanel header = new JPanel(new BorderLayout());
		JPanel middle = new JPanel(new  GridLayout(1, 2, 2, 2));
		JPanel midleft = new JPanel();
		JPanel midright = new JPanel();
		JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		header.setBackground(new Color(9, 87, 151));
		JLabel title = new JLabel("لیست اعضای صنف");
		title.setBorder(new EmptyBorder(0, 10, 0, 10));
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks1.png"));
		header.add(title, BorderLayout.EAST);
		header.add(ks, BorderLayout.WEST);
		
		course = info.findCourse(cid);
		
		JLabel [] infolabels = {
				new JLabel("صنف:"),
				new JLabel("شیفت:"),
				new JLabel("سال:"),
				new JLabel("شناسه:"),
		};
		
		JTextField [] infofields = {
				new JTextField(String.valueOf(course.grade), 5),
				new JTextField(String.valueOf(course.shift), 10),
				new JTextField(String.valueOf(course.year), 5),
				new JTextField(String.valueOf(course.name), 10),
		};
		
		JLabel lab5 = new JLabel("مضمون:");
		JLabel lab6 = new JLabel("نام یا کد متعلم:");
		
		for(int i=0; i<infolabels.length; i++)
		{
			infolabels[i].setHorizontalAlignment(JLabel.RIGHT);
			infofields[i].setEditable(false);
			infofields[i].setBackground(Color.WHITE);
			infofields[i].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		
		word = new JTextField(15);
		word.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		subs = new JComboBox<>(new SubjectInfo().findSubjects(course.grade));
		subs.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)subs.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		subs.setPreferredSize(new Dimension(150, 20));
		
		JButton search, exit, detach, change, upgrade, repeat, save;

		search = new JButton("جستجوی نمرات");
		failed = new JRadioButton("متعلمین ناکام");
		passed = new JRadioButton("متعلمین کامیاب");
		unpassed = new JRadioButton("متعلمین مشروط");
		
		all = new JRadioButton("همه متعلمین");
		all.setSelected(true);
		failed.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		all.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		passed.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		unpassed.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		bg = new ButtonGroup();
		bg.add(all);
		bg.add(failed);
		bg.add(passed);
		bg.add(unpassed);

		middle.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		midright.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		midleft.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		midright.setBorder(new LineBorder(Color.GRAY));
		midright.setPreferredSize(new Dimension(280, 70));

		midleft.setBorder(new LineBorder(Color.GRAY));
		midleft.setPreferredSize(new Dimension(250, 70));
		
		//=================================================
		
		GroupLayout layout = new GroupLayout(midright);
		midright.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(infolabels[0]).addComponent(infolabels[2]));
		hGroup.addGroup(layout.createParallelGroup().addComponent(infofields[0]).addComponent(infofields[2]));
		hGroup.addGroup(layout.createParallelGroup().addComponent(infolabels[1]).addComponent(infolabels[3]));
		hGroup.addGroup(layout.createParallelGroup().addComponent(infofields[1]).addComponent(infofields[3]));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(infolabels[0]).addComponent(infofields[0]).addComponent(infolabels[1]).addComponent(infofields[1]));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(infolabels[2]).addComponent(infofields[2]).addComponent(infolabels[3]).addComponent(infofields[3]));
		layout.setVerticalGroup(vGroup);
		
		//=================================================
		
		GroupLayout layout1 = new GroupLayout(midleft);
		midleft.setLayout(layout1);
		layout1.setAutoCreateGaps(true);
		layout1.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup1 = layout1.createSequentialGroup();

		hGroup1.addGroup(layout1.createParallelGroup().addComponent(all).addComponent(passed));
		hGroup1.addGroup(layout1.createParallelGroup().addComponent(failed).addComponent(unpassed));
		layout1.setHorizontalGroup(hGroup1);

		GroupLayout.SequentialGroup vGroup1 = layout1.createSequentialGroup();

		vGroup1.addGroup(layout1.createParallelGroup(Alignment.BASELINE).addComponent(all).addComponent(failed));
		vGroup1.addGroup(layout1.createParallelGroup(Alignment.BASELINE).addComponent(passed).addComponent(unpassed));
		layout1.setVerticalGroup(vGroup1);
		
		//=================================================

		searchBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		searchBox.add(lab6);
		searchBox.add(word);
		searchBox.add(lab5);
		searchBox.add(subs);
		searchBox.add(search);
		
		middle.add(midright);
		middle.add(midleft);
		top.add(header, BorderLayout.NORTH);
		top.add(middle, BorderLayout.CENTER);
		top.add(searchBox, BorderLayout.SOUTH);

		model = new DefaultTableModel(new String[] { "کد", "نام", "نام پدر", "تخلص", "حاضری", "نمره نیمسال", "نمره سالانه", "مجموع", "نمره مشروطی" }, 0)
		{
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row, int col) {
				return col > 3;
			}
			public Class<?> getColumnClass(int col) {
				if(col > 3){
					return Float.class;
				}
				return String.class;
			}
		};
		table = new JTable(model);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(4).setMinWidth(60);
		table.getColumnModel().getColumn(4).setMaxWidth(60);
		table.getColumnModel().getColumn(5).setMaxWidth(70);
		table.getColumnModel().getColumn(5).setMinWidth(70);
		table.getColumnModel().getColumn(6).setMaxWidth(70);
		table.getColumnModel().getColumn(6).setMinWidth(70);
		table.getColumnModel().getColumn(7).setMinWidth(70);
		table.getColumnModel().getColumn(7).setMaxWidth(70);
		table.getColumnModel().getColumn(8).setMinWidth(90);
		table.getColumnModel().getColumn(8).setMaxWidth(90);
		
		JPanel center = new JPanel(new BorderLayout(5, 0));
		JPanel buttons = new JPanel(new GridBagLayout());
		center.add(new JScrollPane(table), BorderLayout.CENTER);
		center.add(buttons, BorderLayout.WEST);
		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottom.add(bleft, BorderLayout.EAST);
		bottom.add(bright, BorderLayout.WEST);
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.anchor = GridBagConstraints.FIRST_LINE_START;
		cons.insets = new Insets(2, 2, 2, 2);
		cons.weightx = 1;
		cons.gridx = 0;
		buttons.setBorder(new LineBorder(Color.GRAY));
		
		exit = new JButton("خروج", new ImageIcon("images/cross.png"));
		save = new JButton("ثبت نمرات", new ImageIcon("images/save.png"));
		repeat = new JButton("تکرار صنف");
		change = new JButton("تغییر صنف");
		upgrade = new JButton((course.grade == 12)? "ثبت فراغت" : "ارتقاء متعلمین");
		detach = new JButton("حذف از صنف");
		
		ImageIcon img = new ImageIcon("images/excel.png");
		Insets insets = new Insets(2, 5, 2, 5);
		JButton paper, sheet;
		paper = new JButton("صدور کارنامه", img);
		sheet = new JButton("صدور برگه نتایج", img);
		paper.setMargin(insets);
		sheet.setMargin(insets);
		save.setMargin(insets);
		exit.setMargin(insets);
		paper.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		sheet.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		save.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		exit.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		bleft.add(exit);
		bleft.add(save);
		bright.add(paper);
		bright.add(sheet);
		
		buttons.add(upgrade, cons);
		buttons.add(repeat, cons);
		buttons.add(change, cons);
		cons.weighty = 1;
		buttons.add(detach, cons);
		
		setSize(800, 400);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		renderData();

		// Listeners
		
		ActionListener render = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		};
		search.addActionListener(render);
		all.addActionListener(render);
		failed.addActionListener(render);
		passed.addActionListener(render);
		unpassed.addActionListener(render);
		subs.addActionListener(render);
		
		word.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				renderData();
			}
		});
		
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				isModified = true;
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isModified){
					saveMarks();
				}
			}
		});
		
		detach.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				detachStudent();
			}
		});
		
		upgrade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(course.grade == 12){
					graduateStudents();
				}else{
					enrolStudent(1);
				}
			}
		});
		
		change.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enrolStudent(2);	
			}
		});
		
		repeat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enrolStudent(3);
			}
		});
		
		paper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(table.getSelectedRowCount() == 0) return;
				FileDialog diag = new FileDialog(self, "Save", FileDialog.SAVE);
				diag.setFile("workbooks.xlsx");
				diag.setVisible(true);
				String fn = diag.getFile();
				if(fn == null) return;
				if(!fn.toLowerCase().endsWith(".xlsx")){
					fn += ".xlsx";
				}
				String path = diag.getDirectory()+fn;
				
				Reports report = new Reports();
				int [] rows = table.getSelectedRows();
				int sr = 0;
				for(int i=0; i<rows.length; i++){
					int sc = (i%2 == 0)? 0 : 9;
					int id = Integer.parseInt(model.getValueAt(rows[i], 0).toString());
					report.createPaper(id, course.grade, course.year, sr, sc);
					if(i%2 != 0) sr+= 30;
				}
				report.build(path);
			}
		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeConfirm();
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeConfirm();
			}
		});
	}

	public void renderData() 
	{
		Vector<Object> students = null;
		int sub = ((Subject)subs.getSelectedItem()).code;
		String exp = word.getText();
		
		if(all.isSelected()){
			students = info.allMembers(cid, sub, course.year, exp);
		}else if(failed.isSelected()){
			students = info.failedMembers(cid, sub, course.year, exp);
		}else if(passed.isSelected()){
			students = info.passedMembers(cid, sub, course.year, exp);
		}else if(unpassed.isSelected()){
			students = info.unpassedMembers(cid, sub, course.year, exp);
		}
		
		Iterator<Object> iterator = students.iterator();
		model.setRowCount(0);
		while (iterator.hasNext()) {
			model.addRow((Vector<?>) iterator.next());
		}
		isModified = false;
	}
	
	private void saveMarks()
	{
		boolean yes = true;
		for(int i=0; i<table.getRowCount(); i++)
		{
			int st = (int) model.getValueAt(i, 0);
			int sub = ((Subject)subs.getSelectedItem()).code;
			
			if(model.getValueAt(i, 5) == null){
				continue;
			}
			
			String a = (model.getValueAt(i, 4) == null)? "NULL" : model.getValueAt(i, 4).toString();
			String h = (model.getValueAt(i, 5) == null)? "NULL" : model.getValueAt(i, 5).toString();
			String t = (model.getValueAt(i, 6) == null)? "NULL" : model.getValueAt(i, 6).toString();
			String s = (model.getValueAt(i, 8) == null)? "NULL" : model.getValueAt(i, 8).toString();
			
			if(!a.equals("NULL")){
				if(Float.parseFloat(a) < 0 || Float.parseFloat(a) > 100){
					continue;
				}
			}
			if(!h.equals("NULL")){
				if(Float.parseFloat(h) < 0 || Float.parseFloat(h) > 40){
					continue;
				}
			}
			if(!t.equals("NULL")){
				if(Float.parseFloat(t) < 0 || Float.parseFloat(t) > 60){
					continue;
				}
			}
			if(!s.equals("NULL")){
				if(Float.parseFloat(s) < 0 || Float.parseFloat(s) > 100){
					continue;
				}
			}
			if(!info.saveMark(st, sub, course.year, h, t, s) || !info.saveAttendance(st, cid, a)){
				yes = false;
			}
		}
		if(yes){
			PenDiags.showMsg(PenDiags.SUCCESS);
		}
		isModified = false;
	}
	
	private void enrolStudent(int job)
	{
		if(table.getSelectedRowCount() == 0) return;
		int [] rows = table.getSelectedRows();
		int [] ids = new int[rows.length];
		for(int i = ids.length-1; i>=0; i--){
			ids[i] = (int) model.getValueAt(rows[i], 0);
		}
		int g = (job == 2)? cid : course.grade;
		if(StudentEnrollment.isOpen){
			StudentEnrollment.self.requestFocus();
		} else{
			new StudentEnrollment(g, ids, job).setParent(self);
		}
	}
	
	private void graduateStudents()
	{
		if(table.getSelectedRowCount() == 0) return;
		int [] rows = table.getSelectedRows();
		boolean yes = false;
		for(int i=0; i<rows.length; i++)
		{
			int id = Integer.parseInt(model.getValueAt(rows[i], 0).toString());
			if(info.graduateStudent(id)){
				yes = true;
			}
		}
		if(yes){
			PenDiags.showMsg(PenDiags.OPDONE);
		}
	}
	
	private void detachStudent()
	{
		int [] rows = table.getSelectedRows();
		if(rows.length == 0) return;
		int sig = PenDiags.showConf("متعلمین از صنف حذف شوند؟", PenDiags.YN);
		if(sig != 0) return;
		boolean yes = true;
		for(int i=rows.length-1; i>=0; i--)
		{
			int id = Integer.parseInt(model.getValueAt(rows[i], 0).toString());
			if(info.detachStudent(id, cid)){
				model.removeRow(rows[i]);
			}else{
				yes = false;
			}
		}
		if(yes){
			PenDiags.showMsg(PenDiags.OPDONE);
		}
		isModified = false;
	}
	
	private void closeConfirm() 
	{
		if (isModified) {
			int signal = PenDiags.showConf(PenDiags.SAVEASK, PenDiags.YNC);
			if (signal != 2) {
				if (signal == 0) {
					saveMarks();
				}
				closeFrame();
			}
		}
		else {
			closeFrame();
		}
	}

	private void closeFrame() {
		dispose();
		isOpen = false;
	}
}

