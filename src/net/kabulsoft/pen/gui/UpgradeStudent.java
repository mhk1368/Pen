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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.CourseInfo;
import net.kabulsoft.pen.db.StudentInfo;
import net.kabulsoft.pen.db.Subject;
import net.kabulsoft.pen.db.SubjectInfo;
import net.kabulsoft.pen.util.PenDiags;

public class UpgradeStudent extends JDialog 
{
	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static UpgradeStudent self;
	private NewStudent frame = null;
	private JTable table;
	private DefaultTableModel model;
	private JButton save, cancel;
	private StudentInfo info = new StudentInfo();
	private CourseInfo courseInfo = new CourseInfo();
	private int grade;
	private int sid;
	
	public UpgradeStudent(int id) {
		
		setTitle("ارتقاء متعلم");
		isOpen = true;
		self = this;
		sid = id;
		grade = Integer.parseInt(info.findStudent(id).get(8));
		
		JLabel title = new JLabel("ارتقاء متعلم به صنف بالا (امتحان لیاقت)");
		title.setFont(new Font("serif", Font.BOLD, 18));
		title.setForeground(Color.WHITE);
		JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top.setBackground(new Color(103, 64, 228));
		top.add(title);	
		add(top, BorderLayout.NORTH);

		model = new DefaultTableModel(new String[] { "کد مضمون", "نام مضمون", "نمره"}, 0)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				return col == 2;
			}
			
			public Class<?> getColumnClass(int col) {
				if(col == 2){
					return Float.class;
				}
				return String.class;
			}
		};
		table = new JTable(model);
		table.setShowGrid(false);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(80);
		table.getColumnModel().getColumn(0).setMinWidth(80);
		table.getColumnModel().getColumn(2).setMaxWidth(80);
		table.getColumnModel().getColumn(2).setMinWidth(80);
		
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );		
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
		save = new JButton("ارتقاء متعلم");
		cancel = new JButton("انصراف");
		bottom.add(cancel);
		bottom.add(save);
		add(bottom, BorderLayout.SOUTH);

		setSize(500, 350);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		renderData();

		// Listeners

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveData();
			}
		});

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
	}
	
	private void renderData()
	{
		Vector<Subject> data = new SubjectInfo().findSubjects(grade+1);
		for(Subject item : data){
			Vector<Object> v = new Vector<>();
			v.add(item.code);
			v.add(item.name);
			model.addRow(v);
		}
	}

	private void saveData() 
	{
		for(int i=0; i<table.getRowCount(); i++)
		{
			if(model.getValueAt(i, 2) == null)
			{
				PenDiags.showMsg("وارد کردن تمام نمرات ضروری است!");
				return;
			}
			if((float)model.getValueAt(i, 2) < 40 || (float)model.getValueAt(i, 2) > 100)
			{
				PenDiags.showMsg("لطفا نمره قبولی را وارد نمایید!");
				return;
			}
		}
		save.setEnabled(false);
		boolean yes = true;
		for(int i=0; i<table.getRowCount(); i++)
		{
			int sub = Integer.parseInt(model.getValueAt(i, 0).toString());
			float mark = (float)model.getValueAt(i, 2);
			if(!info.saveMark(sid, sub, mark)){
				yes = false;
			}
		}
		if(yes){
			if(info.upgradeStudent(sid, grade + 2))
			{
				if (grade + 2 > 12) courseInfo.graduateStudent(sid);
				PenDiags.showMsg(PenDiags.SUCCESS);
				dispose();
				isOpen = false;
				if(frame != null){
					frame.updateCourseData(grade + 2);
				}
			}
		}
		save.setEnabled(true);
	}
	
	public void setFrame(NewStudent frame){
		this.frame = frame;
	}
}

