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
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.DataBase;
import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

public class FinancialPeriod extends JFrame
{
	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static FinancialPeriod self;
	private JButton search, create, balance;
	private JTextField word;
	private DefaultTableModel model;
	private JTable table;
	private FinanceInfo info;
	
	public FinancialPeriod(){
		
		isOpen = true;
		self = this;
		setTitle("دوره های مالی");
		info = new FinanceInfo();
		
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new BorderLayout());
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topPanel.add(top1, BorderLayout.NORTH);
		topPanel.add(top2, BorderLayout.SOUTH);
		top1.setBackground(new Color(9, 87, 151));
		add(topPanel, BorderLayout.NORTH);
		
		JLabel title = new JLabel(" دوره های مالی");
		title.setFont(new Font("serif", Font.BOLD, 22));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks.png"));
		top1.add(title, BorderLayout.EAST);
		top1.add(ks, BorderLayout.WEST);
		
		word = new JTextField(20);
		search = new JButton("جستجو");
		top2.add(new JLabel("نام دوره مالی:"));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		word.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		top2.add(word);
		top2.add(search);
		
		model = new DefaultTableModel(new String [] {"کد", "نام", "تاریخ شروع", "تاریخ ختم", "فعال"}, 0)
		{
			private static final long serialVersionUID = 1L;
			
			public Class<?> getColumnClass(int index) 
			{
				if(index == 4) return Boolean.class;
				return String.class;
			}
			public boolean isCellEditable(int row, int col) 
			{
				return col == 1;
			}
		};
		table = new JTable(model);
		table.setShowGrid(false);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(80);
		table.getColumnModel().getColumn(4).setMaxWidth(80);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment(JLabel.RIGHT);
		add(new JScrollPane(table));
		
		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);
		create = new JButton("ختم دوره مالی و ایجاد دوره جدید");
		balance = new JButton("موجودی صندوقها در اول دوره مالی");
		bleft.add(balance);
		bright.add(create);
		
		Date now = Calendar.getInstance().getTime();
		int days = (int) (now.getTime() - DataBase.PSDATE.getTime()) / (1000*60*60*24);
		if(days < 30) create.setEnabled(false);
		
		renderData();
		
		word.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				renderData();
			}
		});
		
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		});
		
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int sig = PenDiags.showConf("پس از ختم دوره مالی امکان برگشت به آن نیست. ادامه می دهید؟", PenDiags.YN);
				if(sig != 0) return;
				Object value = PenDiags.showInput("نام دوره مالی جدید را وارد کنید:");
				if(value == null) return;
				if(info.newFinancialPeriod(value.toString())){
					PenDiags.showMsg(PenDiags.OPDONE);
					renderData();
				}
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				isOpen = false;
			}
		});
		
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	public void renderData()
	{
		Vector<Object> rows = info.financialPeriods(word.getText());
		model.setRowCount(0);
		for(Object row : rows){
			model.addRow((Vector<?>)row);
		}
	}
}
