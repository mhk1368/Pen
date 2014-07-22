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
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import net.kabulsoft.pen.util.PenDiags;

public class CourseSchedule extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static CourseSchedule self;
	private boolean isModified = false;
	private JComboBox<Subject> editor;
	private JTable table;
	private DefaultTableModel model;
	private JButton save, print, cancel;
	private CourseInfo info = new CourseInfo();
	private Course course;
	private int cid;

	public CourseSchedule(int id) {
		setTitle("تقسیم اوقات صنف");
		isOpen = true;
		self = this;
		cid = id;
		
		course = info.findCourse(id);
		Vector<Subject> subs = new SubjectInfo().findSubjects(course.grade);
		editor = new JComboBox<>(subs);
		editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)editor.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel title = new JLabel("تقسیم اوقات صنف");
		title.setFont(new Font("serif", Font.BOLD, 20));
		top1.setBackground(new Color(103, 64, 228));
		title.setForeground(Color.WHITE);
		top1.add(title);
		
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
		
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);
		
		model = new DefaultTableModel(new String[] { "ایام هفته", "ساعت اول", "ساعت دوم", "ساعت سوم", "ساعت چهارم", "ساعت پنجم", "ساعت ششم", "ساعت هفتم" }, 0)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int r, int c) {
				return c != 0;
			}
		};
		table = new JTable(model);
		table.setRowHeight(22);
		table.getColumnModel().getColumn(0).setMaxWidth(60);
		
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.CENTER );
		
		for(int i=1; i<table.getColumnCount(); i++){
			table.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(editor)
			{
				private static final long serialVersionUID = 1L;
				
				public Object getCellEditorValue() 
				{
					int d = table.getSelectedRow() + 1;
					int t = table.getSelectedColumn();
					
					Object val = super.getCellEditorValue();
					if(val == null){
						return val;
					}
					int s = ((Subject)val).code;
					if(!info.isTeacherFree(cid, d, t, s, course.year, course.shift)){
						PenDiags.showMsg("استاد این مضمون در این ساعت مصروف است!");
						return null;
					}
					return val;
				}
			});
		}
		
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		save = new JButton("ذخیره");
		cancel = new JButton("خروج");
		print = new JButton("چاپ");
		bright.add(save);
		bright.add(print);
		bright.add(cancel);
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);

		setSize(700, 330);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);

		renderData();
		
		// Listeners
		
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				isModified = true;
			}
		});

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getRowCount() != 0 && isModified) {
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
	}

	public void renderData() 
	{
		String days [] = {"شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهار شنبه", "پنج شنبه" };
		model.setRowCount(0);
		for(int i=0; i<days.length; i++){
			Vector<Object> row = info.sechedule(cid, i+1);
			row.add(0, days[i]);
			model.addRow(row);
		}
		isModified = false;
	}
	
	private void saveData()
	{
		boolean yes = true;
		for(int i=0; i<model.getRowCount(); i++)
		{
			for(int j=1; j<model.getColumnCount(); j++)
			{
				if(model.getValueAt(i, j) == null){
					continue;
				}
				int sub = ((Subject)model.getValueAt(i, j)).code;
				if(!info.saveSchedule(cid, i+1, j, sub)){
					yes = false;
				}
			}
		}
		if(yes){
			PenDiags.showMsg("اطلاعات تقسیم اوقات ذخیره شد!");
		}
		isModified = false;
	}

	private void closeConfirm() {
		if (isModified) {
			int signal = PenDiags.showConf("اطلاعات ذخیره شوند؟", PenDiags.YNC);
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
}

