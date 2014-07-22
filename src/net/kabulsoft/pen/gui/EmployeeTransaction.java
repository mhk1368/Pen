package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.OpenFrame;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class EmployeeTransaction extends JFrame{
	
	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static EmployeeTransaction self;
	private JSpinner s_date, e_date;
	private JButton search, delete, print, create;
	private JTextField total;
	private DefaultTableModel model;
	private JTable table;
	private FinanceInfo info = new FinanceInfo();

	public EmployeeTransaction(){
		
		isOpen = true;
		self = this;
		
		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new BorderLayout());
		JPanel top2 = new JPanel(new BorderLayout());
		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.add(right, BorderLayout.EAST);
		top2.add(left, BorderLayout.WEST);
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);
		add(bottom, BorderLayout.SOUTH);
		right.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		left.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		top1.setBackground(new Color(9, 87, 151));
		JLabel title = new JLabel(" دریافتهای نقدی کارمندان");
		title.setFont(new Font("serif", Font.BOLD, 22));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks.png"));
		top1.add(title, BorderLayout.EAST);
		top1.add(ks, BorderLayout.WEST);
		
		JLabel label4 = new JLabel("از تاریخ:");
		JLabel label5 = new JLabel("تا تاریخ:");
		JLabel label6 = new JLabel("مجموع:");
		s_date = new JSpinner();
		e_date = new JSpinner();
		s_date.setPreferredSize(new Dimension(90, 20));
		e_date.setPreferredSize(new Dimension(90, 20));
		s_date.setModel(new PersianDateModel(s_date));
		e_date.setModel(new PersianDateModel(e_date));
		PersianCalendar cal = new PersianCalendar();
		cal.add(PersianCalendar.MONTH, -1);
		s_date.setValue(cal);
		search = new JButton("جستجو");
		total = new JTextField(8);
		total.setEditable(false);
		total.setBackground(Color.WHITE);
		
		right.add(label4);
		right.add(s_date);
		right.add(label5);
		right.add(e_date);
		right.add(search);
		left.add(label6);
		left.add(total);
		
		model = new DefaultTableModel(new String [] {"کد پرداخت", "نام کارمند", "کد کارمند", "مبلغ", "صندوق", "تاریخ", "شرح"}, 0)
		{
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row, int col) {
				return col == 3;
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
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(editor)
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
				try {
					int val = Integer.parseInt(value.toString());
					if (val < 10) return this.value;
					int id = Integer.parseInt(model.getValueAt(this.row, 0).toString());
					if(info.editEmpReceipt(val, id)){
						PenDiags.showMsg(PenDiags.SUCCESS);
						return value;
					}
				} 
				catch (NumberFormatException e) {
					PenDiags.showMsg("یک مقدار عددی وارد کنید!");
				}
				return this.value;
			}
		});
		
		delete = new JButton("حذف");
		print = new JButton("چاپ");
		create = new JButton("پرداخت جدید");
		bottom.add(delete);
		bottom.add(print);
		bottom.add(create);
		
		setSize(800, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		
		create.addActionListener(new OpenFrame(NewEmployeeTrans.class));
		
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
		
		Vector<Object> data = info.employeeTransactions(sdate, edate);
		model.setRowCount(0);
		int sum = 0;
		for(Object row : data){
			model.addRow((Vector<?>)row);
			sum += Integer.parseInt(((Vector<?>)row).get(3).toString());
		}
		total.setText(String.valueOf(sum));
	}
	
	private void deleteData()
	{
		int [] rows = table.getSelectedRows();
		if(rows.length == 0) return;
		int sig = PenDiags.showConf(PenDiags.DELETEASK, PenDiags.YN);
		if(sig != 0) return;
		for(int i = rows.length-1; i>=0; i--){
			int id = Integer.parseInt(model.getValueAt(rows[i], 0).toString());
			if(info.deleteEmpReceipt(id)){
				model.removeRow(rows[i]);
			}
		}
	}
}

