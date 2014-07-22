package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.Cost;
import net.kabulsoft.pen.db.Course;
import net.kabulsoft.pen.db.CourseInfo;
import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class CostCalculation extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static CostCalculation self;
	private boolean isModified = false;
	private FinanceInfo info = new FinanceInfo();
	private JComboBox<String> month;
	private JTextField word;
	private JButton search, calc, save, exit;
	private DefaultTableModel model;
	private JTable table;
	private Vector<Cost> costs;
	private Course course;
	private int cid;

	public CostCalculation(int id) {
		isOpen = true;
		self = this;
		cid = id;
		course = new CourseInfo().findCourse(id);

		// Top Panel
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel title = new JLabel("تعیین هزینه های متعلمین");
		top1.setBackground(new Color(103, 64, 228));
		title.setFont(new Font("serif", Font.BOLD, 20));
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
		
		month = new JComboBox<>(new String[] { "حمل", "ثور", "جوزا", "سرطان", "اسد", "سنبله", "میزان", "عقرب", "قوس", "جدی", "دلو", "حوت" });
		month.setSelectedIndex(new PersianCalendar().getPersianMonth() - 1);
		month.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel) month.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		month.setPreferredSize(new Dimension(100, 20));
		calc = new JButton("محاسبه هزینه"); 
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		top2.add(new JLabel("صنف:"));
		top2.add(tip1);
		top2.add(new JLabel("شیفت:"));
		top2.add(tip2);
		top2.add(new JLabel("شناسه:"));
		top2.add(tip3);
		top2.add(new JLabel("ثبت هزینه ها برای ماه:"));
		top2.add(month);
		top2.add(calc);
		
		JPanel top3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top3.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		top3.setBorder(new LineBorder(Color.GRAY));
		word = new JTextField(20);
		word.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		search = new JButton("جستجو");
		top3.add(new JLabel("نام یا کد متعلم:"));
		top3.add(word);
		top3.add(search);

		JPanel top = new JPanel(new BorderLayout());
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.CENTER);
		top.add(top3, BorderLayout.SOUTH);
		
		costs = info.studentCosts(course.year, false);
		model = new DefaultTableModel(new Object[] { "نمبر اساس", "نام متعلم", "نام پدر", "نام فامیلی" }, 0)
		{
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int index) 
			{
				if (index == this.getColumnCount() - 1){
					return Integer.class;
				}
				if (index > 3) {
					return Boolean.class;
				}
				return String.class;
			}

			public boolean isCellEditable(int r, int c) {
				return (c > 3 && c < this.getColumnCount() - 1);
			}
		};
		for (Object cost : costs) {
			model.addColumn((Cost)cost);	
		}
		model.addColumn("مجموع هزیه");
		table = new JTable(model);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setHorizontalAlignment(JLabel.RIGHT);
		JScrollPane pane = new JScrollPane(table);
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		save = new JButton("ذخیره");
		exit = new JButton("خروج");
		bottom.add(exit);
		bottom.add(save);

		add(top, BorderLayout.NORTH);
		add(pane, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);

		renderData();

		setSize(900, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		//Listeners
		
		table.getTableHeader().addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e){
		        if(model.getRowCount() > 0)
		        {
		        	int col = table.columnAtPoint(e.getPoint());
			        if(col > 3 && col < table.getColumnCount()-1)
			        {
			        	if((boolean)model.getValueAt(0, col))
			        	{
			        		for(int i=0; i<model.getRowCount(); i++){
				        		model.setValueAt(false, i, col);
				        	}
			        	}
			        	else{
			        		for(int i=0; i<model.getRowCount(); i++){
			        			model.setValueAt(true, i, col);
			        		}
			        	}
			        }	        	
		        }
		    }
		});
		
		ActionListener render = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		};
		month.addActionListener(render);
		search.addActionListener(render);
		
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
		
		calc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calc();
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveData();
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
		Vector<Object> students = info.stCostCalc(cid, course.year, month.getSelectedIndex()+1, word.getText());
		model.setRowCount(0);
		for (Object student : students) {
			model.addRow((Vector<?>) student);
		}
		isModified = false;
	}
	
	private void saveData(){
		if(isModified){
			calc();
			boolean yes = true;
			for(int i=0; i<table.getRowCount(); i++)
			{
				int sid = Integer.parseInt(model.getValueAt(i, 0).toString());
				int amount = Integer.parseInt(model.getValueAt(i, table.getColumnCount() -1).toString());
				int m = month.getSelectedIndex() + 1;
				int y = course.year;
				int code = 0;

				int j = 4;
				for(Object cost : costs){
					int a = ((Cost)cost).id;
					double b = Math.pow(2d, (double)a);	
					if((boolean)model.getValueAt(i, j++)){
						code += b;
					}
				}
				if(!info.saveStudentCost(sid, y, m, code, amount)){
					yes = false;
				}
			}
			if(yes){
				PenDiags.showMsg("اطلاعات هزینه های متعلمین ثبت شد!");
			}
			isModified = false;
		}
	}
	
	private void calc(){
		for(int i=0; i<table.getRowCount(); i++)
		{
			int total = 0;
			int j = 4;
			for(Object cost : costs){
				if((boolean)model.getValueAt(i, j++)){
					total += ((Cost)cost).value;
				}
			}
			model.setValueAt(total, i, model.getColumnCount()-1);
		}
		isModified = true;
	}
}


