package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.Cash;
import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class StudentPayment extends JDialog{

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static StudentPayment self;
	private JComboBox<Cash> cash;
	private JTextField value, desc;
	private JSpinner n_date, s_date, e_date;
	private JButton save, search, delete, print;
	private JTable table;
	private DefaultTableModel model;
	private FinanceInfo info = new FinanceInfo();
	private int sid;
	private int year;
	
	public StudentPayment(int id, int y){
		
		sid = id;
		self = this;
		isOpen = true;
		year = y;
		setTitle("پرداختهای نقدی متعلم");
		setLayout(new BorderLayout());
		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.CENTER);
		top.add(top3, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);
		add(bottom, BorderLayout.SOUTH);
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		top3.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		top1.setBackground(new Color(103, 64, 228));
		JLabel title = new JLabel("پرداختهای نقدی متعلم");
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		top1.add(title);
		
		top2.setBorder(new LineBorder(Color.GRAY));
		JLabel label1 = new JLabel("مبلغ:");
		JLabel label2 = new JLabel("صندوق:");
		JLabel label3 = new JLabel("تاریخ:");
		JLabel label4 = new JLabel("شرح:");
		cash = new JComboBox<>(info.allCash());
		cash.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)cash.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		value = new JTextField(8);
		desc = new JTextField(12);
		desc.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		n_date = new JSpinner();
		n_date.setModel(new PersianDateModel(n_date));
		save = new JButton("ثبت");
		
		top2.add(label1);
		top2.add(value);
		top2.add(label2);
		top2.add(cash);
		top2.add(label3);
		top2.add(n_date);
		top2.add(label4);
		top2.add(desc);
		top2.add(save);
		
		JLabel label5 = new JLabel("از تاریخ:");
		JLabel label6 = new JLabel("تا تاریخ:");
		s_date = new JSpinner();
		e_date = new JSpinner();
		s_date.setModel(new PersianDateModel(s_date));
		e_date.setModel(new PersianDateModel(e_date));
		search = new JButton("جستجو");
		
		PersianCalendar start = new PersianCalendar();
		PersianCalendar now = new PersianCalendar();
		PersianCalendar end = new PersianCalendar();
		start.setPersianDate(y, 1, 1);
		s_date.setValue(start);
		if(now.getPersianYear() != y){
			now.setPersianDate(y, now.getPersianMonth(), now.getPersianDay());
			end.setPersianDate(y, 12, 29);
			n_date.setValue(now);
			e_date.setValue(end);
		}
		
		top3.add(label5);
		top3.add(s_date);
		top3.add(label6);
		top3.add(e_date);
		top3.add(search);
		
		model = new DefaultTableModel(new String [] {"کد", "مبلغ", "صندوق", "تاریخ", "شرح"}, 0)
		{
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row, int col) {
				return col == 1;
			}	
		};
		table = new JTable(model);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.setRowHeight(22);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(0).setMaxWidth(60);
		add(new JScrollPane(table), BorderLayout.CENTER);
		JTextField editor = new JTextField();
		editor.setBorder(new LineBorder(Color.BLACK));
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(editor)
		{
			private static final long serialVersionUID = 1L;
			private Object value;
			private int row;
			
			public Component getTableCellEditorComponent(JTable table,
					Object value, boolean isSelected, int row, int column) {
				this.value = value;
				this.row = row;
				return super.getTableCellEditorComponent(table, value, isSelected, row, column);
			}
			
			public Object getCellEditorValue() 
			{
				Object value = super.getCellEditorValue();
				try{
					int val = Integer.parseInt(value.toString());
					if (val < 10) return this.value;
					int id = Integer.parseInt(model.getValueAt(this.row, 0).toString());
					if(info.editStPayment(val, id)){
						PenDiags.showMsg(PenDiags.SUCCESS);
						return value;
					}
				}
				catch(NumberFormatException e){
					PenDiags.showMsg("یک مقدار عددی وارد کنید!");
				}
				return this.value;
			}
		});
		
		delete = new JButton("حذف");
		print = new JButton("چاپ");
		bottom.add(delete);
		bottom.add(print);
		
		setBounds(0, 50, 600, 320);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		
		value.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				value.setBackground(Color.WHITE);
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveData();
			}
		});
		
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		});	
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteData();
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
		Date start = ((PersianCalendar)s_date.getValue()).getTime();
		Date end = ((PersianCalendar)e_date.getValue()).getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String sdate = df.format(start);
		String edate = df.format(end);
		Vector<Object> payments = info.studentPyaments(sid, sdate, edate);
		model.setRowCount(0);
		for(Object row : payments){
			model.addRow((Vector<?>)row);
		}
	}
	
	private void saveData()
	{
		try {
			int val = Integer.parseInt(value.getText());
			int cashid = ((Cash)cash.getSelectedItem()).id;
			Date date = ((PersianCalendar)n_date.getValue()).getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = df.format(date);
			if(info.saveStudentPayment(sid, val, cashid, year, dateString, desc.getText()))
			{
				PenDiags.showMsg(PenDiags.SUCCESS);
				cash.setSelectedIndex(0);
				value.setText("");
				desc.setText("");
			}
		} 
		catch (NumberFormatException e) {
			value.setBackground(Color.RED);
			PenDiags.showMsg("یک مقدار عددی وارد کنید!");
		}	
	}
	
	private void deleteData()
	{
		int [] rows = table.getSelectedRows();
		if(rows.length == 0) return;
		int sig = PenDiags.showConf(PenDiags.DELETEASK, PenDiags.YN);
		if(sig != 0) return;
		for(int i=rows.length-1; i>= 0; i--){
			int id = Integer.parseInt(model.getValueAt(rows[i], 0).toString());
			if(info.deleteStPayment(id)){
				model.removeRow(rows[i]);
			}
		}
	}
}
