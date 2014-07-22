package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.Course;
import net.kabulsoft.pen.db.CourseInfo;
import net.kabulsoft.pen.db.Subject;
import net.kabulsoft.pen.db.SubjectInfo;
import net.kabulsoft.pen.db.Teacher;
import net.kabulsoft.pen.db.TeacherInfo;
import net.kabulsoft.pen.util.PenDiags;

public class CourseTeachers extends JDialog {
	
	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static CourseTeachers self;
	private JButton save, cancel;
	private Vector<Subject> subs;
	private JTable table;
	private DefaultTableModel model;
	private TeacherInfo info;
	private Course course;
	private boolean isModified = false;

	public CourseTeachers(int id) {

		isOpen = true;
		self = this;
		setTitle("اساتید صنف");
		info = new TeacherInfo();
		course = new CourseInfo().findCourse(id);

		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top1.setBackground(new Color(103, 64, 228));
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);
		
		JLabel title = new JLabel("تعیین اساتید صنف");
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		top1.add(title);

		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		JTextField tip1, tip2, tip3;
		tip1 = new JTextField(String.valueOf(course.grade), 4);
		tip2 = new JTextField(String.valueOf(course.shift), 4);
		tip3 = new JTextField(String.valueOf(course.name), 8);
		tip1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		tip2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		tip3.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		tip1.setBackground(Color.WHITE);
		tip2.setBackground(Color.WHITE);
		tip3.setBackground(Color.WHITE);
		tip1.setEditable(false);
		tip2.setEditable(false);
		tip3.setEditable(false);
		
		top2.add(new JLabel("صنف:"));
		top2.add(tip1);
		top2.add(new JLabel("شیفت:"));
		top2.add(tip2);
		top2.add(new JLabel("شناسه:"));
		top2.add(tip3);
		
		model = new DefaultTableModel(new String [] {"مضمون", "نام استاد", "کد استاد"}, 0)
		{
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row, int col) 
			{
				return col == 1;
			}
			
		};
		table = new JTable(model);
		table.setRowHeight(22);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		final JComboBox<Teacher> editor = new JComboBox<>(info.allTeachers());
		editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)editor.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(2).setMaxWidth(80);
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(editor)
		{
			private static final long serialVersionUID = 1L;
			
			public Object getCellEditorValue() 
			{
				Object val = super.getCellEditorValue();
				if(val != null){
					int id = ((Teacher)val).id;
					model.setValueAt(id, table.getSelectedRow(), 2);
				}
				return val;
			}
			
		});

		JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		save = new JButton("ذخیره");
		cancel = new JButton("انصراف");
		panel3.add(save);
		panel3.add(cancel);
		add(panel3, BorderLayout.SOUTH);
		
		renderData();
		
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				isModified = true;
			}
		});

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isModified) {
					saveData();
				}
			}
		});

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeConfirm();
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeConfirm();
			}
		});

		setSize(500, 300);
		setMinimumSize(new Dimension(500, 300));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void closeConfirm() {
		if (isModified) {
			int signal = PenDiags.showConf(PenDiags.SAVEASK, PenDiags.YNC);
			if (signal != 2) {
				if (signal == 0) {
					saveData();
				}
				closeFrame();
			}
		} else {
			closeFrame();
		}
	}

	private void closeFrame() {
		dispose();
		isOpen = false;
	}
	
	private void renderData()
	{
		subs = new SubjectInfo().findSubjects(course.grade);
		for(Object sub : subs){
			Teacher tc = info.subjTeacher(((Subject) sub).code, course.grade, course.year, course.name);
			String tid = (tc != null)? String.valueOf(tc.id) : "";
			model.addRow(new Object [] {sub, tc, tid});
		}
		isModified = false;
	}

	private void saveData()
	{
		boolean yes = true;
		for (int i = 0; i < model.getRowCount(); i++) 
		{
			Object val = model.getValueAt(i, 1);
			if(val == null) continue;
			int sid = ((Subject)model.getValueAt(i, 0)).code;
			int tid = ((Teacher)val).id;
			if(!info.teacherAssign(tid, course.id, sid)){
				yes = false;
			}
		}
		if (yes) {
			PenDiags.showMsg(PenDiags.SUCCESS);
			isModified = false;
		}
	}
}

