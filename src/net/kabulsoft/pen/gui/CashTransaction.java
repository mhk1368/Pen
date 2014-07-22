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
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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

public class CashTransaction extends JFrame{

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static CashTransaction self;
	private JSpinner sdate, edate;
	private JRadioButton debit, credit, both;
	private JButton search, delete, create, print;
	private DefaultTableModel model;
	private JTable table;
	private FinanceInfo info;
	
	public CashTransaction(){
		
		isOpen = true;
		self = this;
		setTitle("واریز و برداشت صندوقها");
		info = new FinanceInfo();
		
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new BorderLayout());
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topPanel.add(top1, BorderLayout.NORTH);
		topPanel.add(top2, BorderLayout.SOUTH);
		top1.setBackground(new Color(9, 87, 151));
		add(topPanel, BorderLayout.NORTH);
		
		JLabel title = new JLabel(" واریز و برداشت صندوقها");
		title.setFont(new Font("serif", Font.BOLD, 22));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks.png"));
		top1.add(title, BorderLayout.EAST);
		top1.add(ks, BorderLayout.WEST);
		
		sdate = new JSpinner();
		edate = new JSpinner();
		sdate.setModel(new PersianDateModel(sdate));
		edate.setModel(new PersianDateModel(edate));
		sdate.setPreferredSize(new Dimension(90, 20));
		edate.setPreferredSize(new Dimension(90, 20));
		PersianCalendar cal = new PersianCalendar();
		cal.add(PersianCalendar.MONTH, -1);
		sdate.setValue(cal);
		search = new JButton("جستجو");
		debit = new JRadioButton("برداشت");
		credit = new JRadioButton("واریز");
		both = new JRadioButton("هردو");
		ButtonGroup buttons = new ButtonGroup();
		buttons.add(credit);
		buttons.add(debit);
		buttons.add(both);
		both.setSelected(true);
		
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		debit.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		credit.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		both.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		top2.add(new JLabel("از تاریخ:"));
		top2.add(sdate);
		top2.add(new JLabel("تا تاریخ:"));
		top2.add(edate);
		top2.add(debit);
		top2.add(credit);
		top2.add(both);
		top2.add(search);
		
		model = new DefaultTableModel(new String [] {"کد", "مبلغ", "تاریخ", "صندوق", "نوع", "شرح"}, 0)
		{
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};
		table = new JTable(model);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.setRowHeight(22);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(0).setMinWidth(50);
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
			
			public Object getCellEditorValue() {
				Object value = super.getCellEditorValue();
				try{
					int val = Integer.parseInt(value.toString());
					int id = Integer.parseInt(model.getValueAt(this.row, 0).toString());
					if(info.editCashTrans(val, id)){
						PenDiags.showMsg(PenDiags.SUCCESS);
						return value;
					}
				}
				catch(NumberFormatException e){
					PenDiags.showMsg("یک مقدار عددی وارد نمایید!");
				}
				return this.value;
			}
		});
		
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(bottom, BorderLayout.SOUTH);
		create = new JButton("مورد جدید");
		print = new JButton("چاپ");
		delete = new JButton("حذف");
		bottom.add(create);
		bottom.add(print);
		bottom.add(delete);
		
		setSize(700, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		renderData();
		
		ActionListener find = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		};
		search.addActionListener(find);
		debit.addActionListener(find);
		credit.addActionListener(find);
		both.addActionListener(find);
		create.addActionListener(new OpenFrame(NewCashTrans.class));
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int [] rows = table.getSelectedRows();
				if(rows.length == 0) return;
				int sig = PenDiags.showConf(PenDiags.DELETEASK, PenDiags.YN);
				if(sig != 0) return;
				for(int i=rows.length-1; i>=0; i--)
				{
					int id = Integer.parseInt(model.getValueAt(rows[i], 0).toString());
					if(info.deleteCashTrans(id)){
						model.removeRow(rows[i]);
					}
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
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String start = df.format(((PersianCalendar)sdate.getValue()).getTime());
		String end = df.format(((PersianCalendar)edate.getValue()).getTime());
		int type = 2;
		if(debit.isSelected()) type = 0;
		if(credit.isSelected()) type = 1;
		
		Vector<Object> data = info.cashTransactions(start, end, type);
		model.setRowCount(0);
		for(Object row : data){
			model.addRow((Vector<?>)row);
		}
	}

}
