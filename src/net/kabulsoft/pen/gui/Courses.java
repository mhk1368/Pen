package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
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
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.CourseInfo;
import net.kabulsoft.pen.util.OpenFrame;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class Courses extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static Courses self;
	private boolean isModified = false;
	private CourseInfo info = new CourseInfo();
	private JComboBox<Integer> level, year;
	private JButton search, exit, teachers, save, create, delete, members, schedule, costs, fees;
	private DefaultTableModel model;
	private PersianCalendar cal = new PersianCalendar();
	private JTable table;

	public Courses() {
		
		isOpen = true;
		self = this;
		setTitle("صنوف درسی");

		//Top Panel
		JPanel top1 = new JPanel(new BorderLayout());
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top = new JPanel(new BorderLayout());
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
		
		top1.setBackground(new Color(9, 87, 151));
		JLabel title = new JLabel("صنفهای درسی");
		title.setBorder(new EmptyBorder(0, 10, 0, 10));
		title.setFont(new Font("serif", Font.BOLD, 22));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks.png"));
		top1.add(title, BorderLayout.EAST);
		top1.add(ks, BorderLayout.WEST);
		
		
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		level = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
		year = new JComboBox<>();
		int y = cal.getPersianYear();
		while (y > 1380) {
			year.addItem(y--);
		}
		y = cal.getPersianYear();
		
		level.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		year.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		level.setPreferredSize(new Dimension(80, 20));
		year.setPreferredSize(new Dimension(80, 20));
		((JLabel)level.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((JLabel)year.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);

		search = new JButton("جستجو");
		top2.add(new JLabel("صنف:"));
		top2.add(level);
		top2.add(new JLabel("سال:"));
		top2.add(year);
		top2.add(search);

		model = new DefaultTableModel(new String[] { "کد", "سال", "صنف", "شناسه", "شیفت" }, 0)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				return col != 0;
			}
			
			public Class<?> getColumnClass(int col) {
				if(col == 3){
					return String.class;
				}
				return Integer.class;
			}
		};
		table = new JTable(model);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		
		JTextField editor = new JTextField();
		editor.setBorder(new LineBorder(Color.BLACK));
		editor.setHorizontalAlignment(JTextField.RIGHT);
		editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(editor));		
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setCellEditor(new SpinnerEditor(y, 1381, y));
		table.getColumnModel().getColumn(2).setCellEditor(new SpinnerEditor(1, 1, 12));
		table.getColumnModel().getColumn(4).setCellEditor(new SpinnerEditor(1, 1, 5));

		teachers = new JButton("تعیین اساتید");
		members = new JButton("اعضاء و نمرات");
		schedule = new JButton("تقسیم اوقات");
		costs = new JButton("تعیین هزینه ها");
		fees = new JButton("ثبت پرداختها");
		
		JPanel buttons = new JPanel(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.anchor = GridBagConstraints.NORTH;
		cons.insets = new Insets(2, 2, 2, 2);
		cons.weightx = 1;
		cons.gridx = 0;
		buttons.setBorder(new LineBorder(Color.GRAY));
		buttons.add(members, cons);
		buttons.add(schedule, cons);
		buttons.add(teachers, cons);
		buttons.add(costs, cons);
		cons.weighty = 1;
		buttons.add(fees, cons);
		
		JPanel center = new JPanel(new BorderLayout(5, 0));
		center.add(new JScrollPane(table), BorderLayout.CENTER);
		center.add(buttons, BorderLayout.WEST);
		
		add(center, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		save = new JButton("ذخیره");
		create = new JButton("صنف جدید");
		delete = new JButton("حذف");
		exit = new JButton("خروج");	
		
		bottom.add(save);
		bottom.add(create);
		bottom.add(delete);
		bottom.add(exit);
		
		add(bottom, BorderLayout.SOUTH);

		setSize(600, 350);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		renderData();

		// Listeners
		ActionListener find = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		};
		search.addActionListener(find);
		level.addActionListener(find);
		year.addActionListener(find);
		
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				isModified = true;
			}
		});
		
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				model.addRow(new Object[]{null, cal.getPersianYear(), 1, "", 1});
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isModified && model.getRowCount() > 0){
					saveData();
				}
			}
		});
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				deleteData();
			}
		});
		
		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					operation(Members.class);
					e.consume();
				}
				else if(e.getKeyCode() == KeyEvent.VK_DELETE){
					deleteData();
				}
			}
		});
		
		members.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operation(Members.class);
			}
		});

		teachers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operation(CourseTeachers.class);
			}
		});
		
		schedule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operation(CourseSchedule.class);
			}
		});
		
		costs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operation(CostCalculation.class);
			}
		});
		
		fees.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operation(CostPayment.class);
			}
		});

		exit.addActionListener(new ActionListener() {
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

	private void renderData() {

		Vector<Object> courses = info.searchCourse((int) year.getSelectedItem(), (int) level.getSelectedItem());
		Iterator<Object> iterator = courses.iterator();
		
		model.setRowCount(0);
		while (iterator.hasNext()) {
			model.addRow((Vector<?>) iterator.next());
		}
		isModified = false;
	}
	
	private void saveData(){
		boolean yes = true;
		for(int i=0; i<model.getRowCount(); i++)
		{
			if(model.getValueAt(i, 0) == null){
				String y = model.getValueAt(i, 1).toString();
				String g = model.getValueAt(i, 2).toString();
				String n = model.getValueAt(i, 3).toString();
				String s = model.getValueAt(i, 4).toString();
				if(!info.addCourse(y, g, n, s)){
					yes = false;
				}
			}
			else{
				String id = model.getValueAt(i, 0).toString();
				String name = model.getValueAt(i, 3).toString();
				if(!info.editCourse(id, name)){
					yes = false;
				}
			}
		}
		if(yes){
			PenDiags.showMsg(PenDiags.SUCCESS);
		}
		renderData();
	}
	
	private void deleteData()
	{
		int [] rows = table.getSelectedRows();
		if(rows.length == 0) return;
		int sig = PenDiags.showConf(PenDiags.DELETEASK, PenDiags.YN);
		if(sig != 0) return;
		for(int i=rows.length-1; i>=0; i--)
		{
			if(model.getValueAt(rows[i], 0) == null){
				continue;
			}
			info.deleteCourse(model.getValueAt(rows[i], 0).toString());
		}
		renderData();
	}
	
	private void operation(Class<?> window){
		Object value = model.getValueAt(table.getSelectedRow(), 0);
		if(table.getSelectedRow() > -1 && value != null)
		{
			OpenFrame.openFrame(window, new Class<?>[]{int.class}, new Object[]{(int)value});
		}
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

