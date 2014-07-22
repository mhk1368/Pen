package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.Cash;
import net.kabulsoft.pen.db.Course;
import net.kabulsoft.pen.db.CourseInfo;
import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class CostPayment extends JFrame{

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static CostPayment self;
	private FinanceInfo info = new FinanceInfo();
	private JSpinner paymentDate;
	private JTextField word;
	private JCheckBox debtors;
	private JButton save, exit;
	private JComboBox<Cash> cash;
	private DefaultTableModel model;
	private JTable table;
	private Course course;
	private int cid;
	
	public CostPayment(int id){
		isOpen = true;
		self = this;
		cid = id;
		course = new CourseInfo().findCourse(id);
		
		//Top Panel
		
		JLabel title = new JLabel(" ثبت پرداختهای متعلمین");
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks1.png"));
		JPanel top1 = new JPanel(new BorderLayout());
		top1.setBackground(new Color(39, 62, 97));
		top1.add(title, BorderLayout.EAST);
		top1.add(ks, BorderLayout.WEST);
		
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		top2.setBorder(new LineBorder(Color.GRAY));
		
		JTextField text1, text2, text3;
		text1 = new JTextField(String.valueOf(course.grade), 4);
		text2 = new JTextField(String.valueOf(course.shift), 4);
		text3 = new JTextField(String.valueOf(course.name), 8);
		text1.setHorizontalAlignment(JTextField.RIGHT);
		text2.setHorizontalAlignment(JTextField.RIGHT);
		text3.setHorizontalAlignment(JTextField.RIGHT);
		text1.setEditable(false);
		text2.setEditable(false);
		text3.setEditable(false);
		text1.setBackground(Color.WHITE);
		text2.setBackground(Color.WHITE);
		text3.setBackground(Color.WHITE);
		cash = new JComboBox<>(info.allCash());
		cash.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)cash.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		paymentDate = new JSpinner();
		paymentDate.setModel(new PersianDateModel(paymentDate));
		PersianCalendar cal = new PersianCalendar();
		if(cal.getPersianYear() != course.year){
			cal.setPersianDate(course.year, cal.getPersianMonth(), cal.getPersianDay());
			paymentDate.setValue(cal);
		}
		
		top2.add(new JLabel("صنف:"));
		top2.add(text1);
		top2.add(new JLabel("شیفت:"));
		top2.add(text2);
		top2.add(new JLabel("شناسه:"));
		top2.add(text3);
		top2.add(new JLabel("صندوق:"));
		top2.add(cash);
		top2.add(new JLabel("تاریخ پرداخت:"));
		top2.add(paymentDate);
		
		JPanel top3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top3.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		word = new JTextField(20);
		debtors = new JCheckBox("فقط متعلمین بدهکار");
		word.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		debtors.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		top3.add(new JLabel("نام یا کد متعلم:"));
		top3.add(word);
		top3.add(debtors);
		
		JPanel top = new JPanel(new BorderLayout());
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.CENTER);
		top.add(top3, BorderLayout.SOUTH);
		
		model = new DefaultTableModel(new String [] {"نمبر اساس", "نام متعلم", "نام پدر", "نام فامیلی", "مبلغ قابل پرداخت", "مبلغ پرداختی", "شرح"}, 0)
		{
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int index) {
				if(index == 5){
					return Integer.class;
				}
				return String.class;
			}
			public boolean isCellEditable(int r, int c) {
				return c > 4;
			}
		};
		table = new JTable(model);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		table.getColumnModel().getColumn(0).setMaxWidth(70);
		JScrollPane pane = new JScrollPane(table);
		JTextField desc = new JTextField();
		desc.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		desc.setBorder(new LineBorder(Color.BLACK));
		JTextField editor = new JTextField();
		editor.setBorder(new LineBorder(Color.BLACK));
		table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(desc));
		table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(editor)
		{
			private static final long serialVersionUID = 1L;
			private int row;
			
			public Component getTableCellEditorComponent(JTable table,
					Object value, boolean isSelected, int row, int column) {
				this.row = row;
				return super.getTableCellEditorComponent(table, value, isSelected, row, column);
			}
			
			public Object getCellEditorValue() {
				Object value = super.getCellEditorValue();
				try {
					int val = Integer.parseInt(value.toString());
					int pay = Integer.parseInt(model.getValueAt(this.row, 4).toString());
					if(val > pay){
						PenDiags.showMsg("مبلغ پرداختی بیشتر از میزان بدهی است!");
					}
				} catch (NumberFormatException e) {
					return null;
				}
				return value;
			}
		});
		
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		save = new JButton("ذخیره");
		exit = new JButton("خروج");
		bottom.add(exit);
		bottom.add(save);
		
		add(top, BorderLayout.NORTH);
		add(pane, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		renderData();
		
		setSize(700, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		//Listeners
		
		word.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				renderData();
			}
		});
		
		debtors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				Date date = ((PersianCalendar)paymentDate.getValue()).getTime();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String dateString = df.format(date);
				int cashid = ((Cash)cash.getSelectedItem()).id;
				boolean yes = true;
				for(int i=0; i<table.getRowCount(); i++)
				{
					int id = Integer.parseInt(model.getValueAt(i, 0).toString());
					Object value = model.getValueAt(i, 5);
					Object descr = model.getValueAt(i, 6);
					int val = (value == null)? 0 : Integer.parseInt(value.toString());
					String desc = (descr == null)? "" : descr.toString();
					if(val > 0){
						if(!info.saveStudentPayment(id, val, cashid, course.year, dateString, desc)){
							yes = false;
						}
					}
				}
				if(yes){
					PenDiags.showMsg(PenDiags.SUCCESS);
				}
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
		Vector<Object> students = info.studentsPayable(cid, course.year, course.grade, word.getText(), debtors.isSelected());
		model.setRowCount(0);
		for(Object student : students){
			model.addRow((Vector<?>)student);
		}
	}

}

