package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.CourseInfo;
import net.kabulsoft.pen.db.StudentInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class StudentMarks extends JPanel 
{
	private static final long serialVersionUID = 1L;
	private boolean isModified = false;
	private NewStudent frame = null;
	private JComboBox<Integer> grade, year;
	private JTable table;
	private DefaultTableModel model;
	private JButton delete, save, upgrade, workbook;
	private StudentInfo info = new StudentInfo();
	private CourseInfo courseInfo = new CourseInfo();
	private Vector<String> student;
	private int sgr;
	private int sid;

	public StudentMarks(int id) {
		
		sid = id;
		student = info.findStudent(id);
		sgr = Integer.parseInt(student.get(8));
		
		setLayout(new BorderLayout());
		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new BorderLayout());
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel title = new JLabel(" نمرات متعلم");
		title.setFont(new Font("serif", Font.BOLD, 20));
		top1.setBackground(new Color(9, 87, 151));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks1.png"));
		top1.add(title, BorderLayout.EAST);
		top1.add(ks, BorderLayout.WEST);
				
		grade = new JComboBox<>();
		for(int g = sgr; g > 0; g--){
			grade.addItem(g);
		}
		
		year = new JComboBox<>();
		int y = new PersianCalendar().getPersianYear();
		for(; y>1380; y--){
			year.addItem(y);
		}
		year.setSelectedItem(info.markYear(sgr, sid, sgr));
		
		grade.setPreferredSize(new Dimension(80, 20));
		grade.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)grade.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		year.setPreferredSize(new Dimension(80, 20));
		year.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)year.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		
		top2.add(new JLabel("صنف:"));
		top2.add(grade);
		top2.add(new JLabel("سال:"));
		top2.add(year);
		
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);

		model = new DefaultTableModel(new String[] { "کد", "نام مضمون", "نمره نیمسال", "نمره سالانه", "مجموع", "نمره مشروطی" }, 0)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				return col > 1;
			}
			
			public Class<?> getColumnClass(int col) {
				if(col > 1){
					return Float.class;
				}
				return String.class;
			}
		};
		table = new JTable(model);
		table.setShowGrid(false);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(2).setMaxWidth(80);
		table.getColumnModel().getColumn(2).setMinWidth(80);
		table.getColumnModel().getColumn(3).setMaxWidth(80);
		table.getColumnModel().getColumn(3).setMinWidth(80);
		table.getColumnModel().getColumn(4).setMinWidth(80);
		table.getColumnModel().getColumn(4).setMaxWidth(80);
		table.getColumnModel().getColumn(5).setMinWidth(80);
		table.getColumnModel().getColumn(5).setMaxWidth(80);
		
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);
		
		delete = new JButton("حذف نمرات");
		save = new JButton("ثبت نمرات");
		upgrade = new JButton((sgr == 12)? "ثبت فراغت" : "ارتقاء متعلم");
		workbook = new JButton("صدور کارنامه");
		bright.add(delete);
		bright.add(save);
		bleft.add(upgrade);
		bleft.add(workbook);
		
		// Listeners
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				isModified = true;
			}
		});
		
		grade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(grade.getItemCount() == 0) return;
				year.setSelectedItem(info.markYear((int)grade.getSelectedItem(), sid, sgr));
				renderData();
			}
		});
		
		year.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		});

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isModified) {
					saveData();
				}
			}
		});
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteMarks();
			}
		});
		
		upgrade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(sgr == 12){
					if(courseInfo.graduateStudent(sid)){
						PenDiags.showMsg(PenDiags.OPDONE);
					}
					return;
				}
				if(courseInfo.isPassed(sid, sgr))
				{
					if(UpgradeStudent.isOpen){
						UpgradeStudent.self.requestFocus();
					}else{
						new UpgradeStudent(sid).setFrame(frame);
					}
				}
				else{
					PenDiags.showMsg("متعلم واجد شرایط ارتقاء نیست!");
				}
			}
		});
		
		workbook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(CreatePaper.isOpen){
					CreatePaper.self.requestFocus();
				}
				else{
					new CreatePaper(sid, sgr);
				}
			}
		});
	}
	
	public void refreshGrade(int g)
	{
		sgr = (g > 12)? 12 : g;
		grade.removeAllItems();
		for(int c = sgr; c > 0; c--){
			grade.addItem(c);
		}
		grade.setSelectedIndex(0);
	}
	
	public void renderData()
	{
		int g = (int)grade.getSelectedItem();
		int y = (int)year.getSelectedItem();
		Vector<Object> data = info.studentMarks(sid, y, g);
		Iterator<Object> items = data.iterator();
		model.setRowCount(0);
		while(items.hasNext()){
			model.addRow((Vector<?>) items.next());
		}
		isModified = false;
	}

	private void saveData() {
		boolean yes = true;
		for(int i=0; i<table.getRowCount(); i++)
		{
			if(model.getValueAt(i, 2) == null){
				continue;
			}
			
			int sub = Integer.parseInt(model.getValueAt(i, 0).toString());
			int y = (int) year.getSelectedItem();
			
			String h = (model.getValueAt(i, 2) == null)? "NULL" : model.getValueAt(i, 2).toString();
			String t = (model.getValueAt(i, 3) == null)? "NULL" : model.getValueAt(i, 3).toString();
			String s = (model.getValueAt(i, 5) == null)? "NULL" : model.getValueAt(i, 5).toString();
			
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
			if(!courseInfo.saveMark(sid, sub, y, h, t, s)){
				yes = false;
			}
		}
		if(yes){
			PenDiags.showMsg(PenDiags.SUCCESS);
		}
		renderData();
	}
	
	private void deleteMarks()
	{
		if(table.getSelectedRowCount() == 0) return;
		int sig = PenDiags.showConf(PenDiags.DELETEASK, PenDiags.YN);
		if(sig != 0) return;
		int rows[] = table.getSelectedRows();
		for(int i=0; i<rows.length; i++){
			int y = (int) year.getSelectedItem();
			int sub = Integer.parseInt(model.getValueAt(rows[i], 0).toString());
			courseInfo.deleteMark(sid, sub, y);
		}
		renderData();
	}
	
	public void setFrame(NewStudent frame){
		this.frame = frame;
	}
}

