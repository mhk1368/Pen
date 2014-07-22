package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.Cost;
import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

public class CostsOfStudent extends JDialog {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static CostsOfStudent self;
	private boolean isModified = false;
	private JComboBox<String> month;
	private JTextField total;
	private JButton sum;
	private JTable table;
	private DefaultTableModel model;
	private JButton save, cancel;
	private FinanceInfo info = new FinanceInfo();
	private int sid;
	private int year;

	public CostsOfStudent(int id, int y) {
		
		isOpen = true;
		self = this;
		sid = id;
		year = y;

		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new BorderLayout());
		JPanel tleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel tright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		tright.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		tleft.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		top2.add(tright, BorderLayout.EAST);
		top2.add(tleft, BorderLayout.WEST);
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
		
		JLabel title = new JLabel("هزینه های قابل پرداخت متعلم");
		title.setFont(new Font("serif", Font.BOLD, 20));
		top1.setBackground(new Color(103, 64, 228));
		title.setForeground(Color.WHITE);
		top1.add(title);
		
		month = new JComboBox<>(new String[] { "حمل", "ثور", "جوزا", "سرطان", "اسد", "سنبله", "میزان", "عقرب", "قوس", "جدی", "دلو", "حوت" });
		month.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)month.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		total = new JTextField(10);
		total.setHorizontalAlignment(JTextField.RIGHT);
		total.setEditable(false);
		total.setBackground(Color.WHITE);
		JTextField theYear = new JTextField(String.valueOf(y),5);
		theYear.setEditable(false);
		theYear.setBackground(Color.WHITE);
		sum = new JButton(new ImageIcon("images/calculator.png"));
		sum.setMargin(new Insets(1, 5, 1, 5));
		tright.add(new JLabel("هزینه های ماه:"));
		tright.add(month);
		tright.add(theYear);
		tleft.add(new JLabel("مجموع هزینه ماه:"));
		tleft.add(total);
		tleft.add(sum);
		
		model = new DefaultTableModel(new String[] { "هزینه", "مبلغ قابل پرداخت ماهانه", "" }, 0)
		{
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row, int col) {
				return col == 2;
			}
			
			public Class<?> getColumnClass(int c) {
				if(c == 2){
					return Boolean.class;
				}
				return String.class;
			}
		};
		table = new JTable(model);
		table.setShowGrid(false);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		
		JTextField editor = new JTextField();
		editor.setBorder(new LineBorder(Color.BLACK));
		editor.setHorizontalAlignment(JTextField.RIGHT);
		editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(editor));
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		save = new JButton("ذخیره");
		cancel = new JButton("خروج");
		bottom.add(save);
		bottom.add(cancel);
		add(bottom, BorderLayout.SOUTH);

		setSize(500, 300);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);

		renderData();
		
		// Listeners
		
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				isModified = true;
			}
		});
		
		month.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		});
		
		sum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sum();
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

	private void renderData() 
	{
		int m = month.getSelectedIndex() + 1;
		Vector<Object> rows = info.costsOfStudent(sid, year, m);
		model.setRowCount(0);
		for(Object row : rows){
			model.addRow((Vector<?>)row);
		}
		isModified = false;
	}
	
	private void sum(){
		int sum = 0;
		for(int i=0; i<model.getRowCount(); i++){
			if((boolean)model.getValueAt(i, 2)){
				sum += Integer.parseInt(model.getValueAt(i, 1).toString());
			}
			total.setText(String.valueOf(sum));
		}
	}
	
	private void saveData()
	{
		int code = 0;
		int value = 0;
		int m = month.getSelectedIndex() + 1;
		for(int i=0; i<model.getRowCount(); i++)
		{
			Cost cost = (Cost)model.getValueAt(i, 0);
			int a = ((Cost)cost).id;
			double b = Math.pow(2d, (double)a);	
			if((boolean)model.getValueAt(i, 2)){
				code += b;
				value += ((Cost)cost).value;
			}
		}
		if(info.saveStudentCost(sid, year, m, code, value)){
			PenDiags.showMsg(PenDiags.SUCCESS);
		}
		isModified = false;
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
}

