package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.DataBase;
import net.kabulsoft.pen.db.EmployeeInfo;
import net.kabulsoft.pen.util.PenDiags;

public class SearchEmployee extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static SearchEmployee self;
	private DefaultTableModel model;
	private JTable table;
	private JTextField word;
	private JComboBox<String> cols;
	private JButton next, prev, create, edit, delete, print, cancel;
	private String theCols[];
	private EmployeeInfo info;
	private int page = 1;
	private int pages;

	public SearchEmployee() {
		
		isOpen = true;
		self = this;
		info = new EmployeeInfo();
		String headers[] = { "کد کارمند", "نام", "نام پدر", "نمبر تذکره", "شماره تلفن"};
		String attribs[] = { "emp_id", "emp_name", "emp_fname", "emp_idcard"};
		String colNames[] = { "کد کارمند", "نام", "نام پدر", "نمبر تذکره"};
		this.theCols = attribs;

		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new BorderLayout());
		JPanel top2 = new JPanel(new BorderLayout());
		JPanel tleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel tright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		tright.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel title = new JLabel("جستجوی کارمندان");
		title.setFont(new Font("serif", Font.BOLD, 22));
		title.setBorder(new EmptyBorder(0, 10, 0, 10));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks.png"));
		top1.setBackground(new Color(9, 87, 151));
		top1.add(title, BorderLayout.EAST);
		top1.add(ks, BorderLayout.WEST);

		JLabel label = new JLabel("جستجو بر اساس:");
		cols = new JComboBox<String>(colNames);
		cols.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)cols.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		word = new JTextField(20);
		word.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		prev = new JButton("قبلی");
		next = new JButton("بعدی");
		tright.add(label);
		tright.add(cols);
		tright.add(word);
		tleft.add(next);
		tleft.add(prev);
		
		top2.add(tleft, BorderLayout.WEST);
		top2.add(tright, BorderLayout.EAST);
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);

		model = new DefaultTableModel(headers, 0){
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		table = new JTable(model);
		table.setRowHeight(22);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(80);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		create = new JButton("کارمند جدید");
		edit = new JButton("ویرایش");
		delete = new JButton("حذف");
		print = new JButton("چاپ");
		cancel = new JButton("خروج");
		
		bright.add(create);
		bright.add(edit);
		bright.add(delete);
		bleft.add(print);
		bleft.add(cancel);
		
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);

		setSize(800, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		renderData();

		ActionListener find = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				page = 1;
				renderData();
			}
		};
		
		word.addActionListener(find);
		cols.addActionListener(find);
		
		word.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				page = 1;
				renderData();
			}
		});
		
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(page < pages) page++;
				renderData();
			}
		});
		
		prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(page > 1) page--;
				renderData();
			}
		});
		
		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) 
			{
				switch(e.getKeyCode()){
				case KeyEvent.VK_ENTER:
					employeeProfile();
					e.consume();
					break;
				case KeyEvent.VK_DELETE:
					deleteData();
					break;
				case KeyEvent.VK_PAGE_DOWN:
					if(page < pages) page++;
					renderData();
					break;
				case KeyEvent.VK_PAGE_UP:
					if(page > 1) page--;
					renderData();
					break;
				}
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){
					employeeProfile();
				}
			}
		});

		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!NewEmployee.isOpen) {
					new NewEmployee().setParent(self);
				} else {
					NewEmployee.self.setState(JFrame.NORMAL);
					NewEmployee.self.requestFocus();
				}
			}
		});

		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				employeeProfile();
			}
		});

		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteData();
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

	public void renderData() 
	{
		String col = theCols[cols.getSelectedIndex()];
		String exp = word.getText();
		double total = info.countEmployee(col, exp);
		pages = (int) Math.ceil(total/DataBase.LIMIT);
		
		Vector<Object> employees = info.searchEmployee(col, exp, page);
		model.setRowCount(0);
		for(Object row : employees){
			model.addRow((Vector<?>)row);
		}
	}
	
	private void employeeProfile(){
		if (table.getSelectedRow() >= 0) {
			if (!NewEmployee.isOpen) {
				int id = Integer.parseInt(model.getValueAt(table.getSelectedRow(), 0).toString());
				new NewEmployee(id).setParent(self);
			} else {
				NewEmployee.self.setState(JFrame.NORMAL);
				NewEmployee.self.requestFocus();
			}
		}
	}
	
	private void deleteData()
	{
		int [] rows = table.getSelectedRows();
		if(rows.length == 0) return;
		int sig = PenDiags.showConf(PenDiags.DELETEASK, PenDiags.YN);
		if(sig != 0) return;
		for(int i=rows.length-1; i>=0; i--){
			if(info.deleteEmployee(model.getValueAt(rows[i], 0).toString())){
				model.removeRow(rows[i]);
			}
		}
	}
}
