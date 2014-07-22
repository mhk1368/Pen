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

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.DataBase;
import net.kabulsoft.pen.db.StudentInfo;
import net.kabulsoft.pen.util.PenDiags;

public class SearchStudent extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static SearchStudent self;
	private DefaultTableModel model;
	private JTable table;
	private JLabel lab1, lab2;
	private JComboBox<String> cols, level;
	private JTextField word;
	private JButton next, prev, create, edit, assign, delete, print, cancel;
	private JRadioButton male, female, both;
	private JCheckBox active;
	private String theCols[];
	private StudentInfo info = new StudentInfo();
	private int page = 1;
	private int pages;
	
	public SearchStudent() {
		setTitle("جستجوی متعلمین");
		isOpen = true;
		self = this;

		String headers[] = { "کد", "نام", "نام پدر", "نام فامیلی", "نمبر تذکره", "ولایت", "صنف", "سال شمولیت", "سال فراغت", "وضعیت" };
		String classes[] = { "تمام صنوف", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
		String attrs[] = { "st_id", "st_name", "st_fname", "st_lname", "st_province" };
		this.theCols = attrs;

		// Top Panel
		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new BorderLayout());
		JPanel topp = new JPanel(new BorderLayout());
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JLabel title = new JLabel("جستجوی متعلمین");
		title.setFont(new Font("serif", Font.BOLD, 22));
		title.setBorder(new EmptyBorder(0, 10, 0, 10));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks.png"));
		top1.setBackground(new Color(9, 87, 151));
		top1.add(title, BorderLayout.EAST);
		top1.add(ks, BorderLayout.WEST);

		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		lab1 = new JLabel("جستجو بر اساس:");
		lab2 = new JLabel("جستجو در صنف:");
		cols = new JComboBox<String>(new String[] { "کد", "نام", "نام پدر", "نام فامیلی", "ولایت" });
		cols.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)cols.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		
		level = new JComboBox<String>(classes);
		level.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)level.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		
		male = new JRadioButton("ذکور");
		female = new JRadioButton("اناث");
		both = new JRadioButton("همه");
		active = new JCheckBox("برحال");
		male.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		female.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		both.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		active.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		ButtonGroup buttons = new ButtonGroup();
		buttons.add(male);
		buttons.add(female);
		buttons.add(both);
		both.setSelected(true);
		
		word = new JTextField(15);
		word.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		next = new JButton("بعدی");
		prev = new JButton("قبلی");
		top2.add(lab1);
		top2.add(cols);
		top2.add(word);
		top2.add(lab2);
		top2.add(level);
		top2.add(male);
		top2.add(female);
		top2.add(both);
		top2.add(active);
		top3.add(next);
		top3.add(prev);

		topp.add(top2, BorderLayout.EAST);
		topp.add(top3, BorderLayout.WEST);
		top.add(top1, BorderLayout.CENTER);
		top.add(topp, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);

		// Middle Panel
		model = new DefaultTableModel(headers, 0)
		{
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		table = new JTable(model);
		table.setRowHeight(22);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(6).setMaxWidth(50);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		JScrollPane pane = new JScrollPane(table);
		add(pane, BorderLayout.CENTER);

		// Bottom Panel
		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		create = new JButton("متعلم جدید");
		edit = new JButton("ویرایش");
		assign = new JButton("الحاق به صنف");
		delete = new JButton("حذف");
		print = new JButton("چاپ");
		cancel = new JButton("خروج");
		
		bleft.add(assign);
		bleft.add(edit);
		bleft.add(delete);
		bright.add(create);
		bright.add(print);
		bright.add(cancel);
		
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);

		setSize(900, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);

		// Listeners

		renderData();
		
		ActionListener find = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				page = 1;
				renderData();
			}
		};
		word.addActionListener(find);
		level.addActionListener(find);
		both.addActionListener(find);
		male.addActionListener(find);
		female.addActionListener(find);
		active.addActionListener(find);
		
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

		word.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				page = 1;
				renderData();
			}
		});

		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!NewStudent.isOpen) {
					new NewStudent(0).setParent(self);
				} else {
					NewStudent.self.requestFocus();
				}
			}
		});
		
		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode())
				{
				case KeyEvent.VK_ENTER:
					studentProfile();
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
					studentProfile();
				}
			}
		});

		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				studentProfile();
			}
		});

		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteData();
			}
		});
		
		assign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(table.getSelectedRowCount() > 0)
				{
					int [] rows = table.getSelectedRows();
					int [] ids = new int[rows.length];
					
					int id = Integer.parseInt(model.getValueAt(rows[0], 0).toString());
					int c = info.findGrade(id);
					
					if(c == 13){
						PenDiags.showMsg("قابلیت انجام عملیات نیست. متعلم در لیست فارغین است!");
						return;
					}
					for(int i=0; i<rows.length; i++)
					{
						id = Integer.parseInt(model.getValueAt(rows[i], 0).toString());
						if(info.findGrade(id) == 13){
							PenDiags.showMsg("قابلیت انجام عملیات نیست. متعلم در لیست فارغین است!");
							return;
						}
						if(info.findGrade(id) != c){
							PenDiags.showMsg("قابلیت انجام عملیات نیست. متعلمین از صنوف مختلف هستند!");
							return;
						}
						ids[i] = Integer.parseInt(model.getValueAt(rows[i], 0).toString());
					}
					if(StudentEnrollment.isOpen){
						StudentEnrollment.self.requestFocus();
					} else{
						new StudentEnrollment(c, ids, 3);
					}
				}
			}
		});

		print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(model.getDataVector().isEmpty());
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
	
	private void studentProfile()
	{
		if (table.getSelectedRow() >= 0) {
			if (!NewStudent.isOpen) {
				new NewStudent(Integer.parseInt(model.getValueAt(
						table.getSelectedRow(), 0).toString())).setParent(self);
			} else {
				NewStudent.self.requestFocus();
				NewStudent.self.setExtendedState(JFrame.NORMAL);
			}
		}
	}

	public void renderData() 
	{
		String col = theCols[cols.getSelectedIndex()];
		String exp = word.getText();
		int lev = level.getSelectedIndex();
		String sex = "b";
		if(male.isSelected()) sex = "m";
		if(female.isSelected()) sex = "f";
		
		double total = info.countStudents(col, exp, sex, lev, active.isSelected());
		pages = (int) Math.ceil(total/DataBase.LIMIT);
		
		Vector<Object> students = info.searchStudent(col, exp, sex, lev, active.isSelected(), page);
		model.setRowCount(0);
		for(Object row : students){
			model.addRow((Vector<?>)row);
		}
	}
	
	private void deleteData()
	{
		int [] rows= table.getSelectedRows();
		if(rows.length == 0) return;
		int sig = PenDiags.showConf(PenDiags.DELETEASK, PenDiags.YN);
		if(sig != 0) return;
		for(int i=rows.length-1; i>=0; i--)
		{
			if(info.deleteStudent(model.getValueAt(rows[i], 0).toString())){
				model.removeRow(rows[i]);
			}
		}
	}
}


