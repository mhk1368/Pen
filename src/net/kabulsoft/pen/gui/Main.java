package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.DataBase;
import net.kabulsoft.pen.util.OpenFrame;
import net.kabulsoft.pen.util.PenDiags;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	public JFrame self = this;
	private DataBase db;

	public Main() {
		
		setTitle("Pen");
		db = new DataBase();
		
		String [][] labels = {
			{"تعریف متعلم", "تعریف استاد", "تعریف کارمند"},
			{ "جستجوی  متعلمین", "جستجوی اساتید", "جستجوی کارمندان", "جستجو و تعریف مضامین", "جستجو و تعریف صنوف", "متعلمین انتقالی" },
			{"دوره  های مالی", "تعریف صندوق", "مصارف و عایدات", "تعیین مبلغ فیس", "هزینه های متعلمین"},
			{"واریز و برداشت صندوقها", "پرداخت و دریافت مصارف و عایدات", "پرداخت معاشات اساتید", "پرداخت معاشات کارمندان"}
		};
		
		Class<?> Frames [][] = { 
				{
					NewStudent.class,
					NewTeacher.class,
					NewEmployee.class
				},
				{
					SearchStudent.class, 
					SearchTeacher.class, 
					SearchEmployee.class,
					Subjects.class, 
					Courses.class, 
					TransferredStudents.class
				},
				{
					FinancialPeriod.class,
					Cashes.class,
					Accounts.class,
					StudentFees.class,
					StudentCosts.class,
				},
				{
					CashTransaction.class,
					AccountTransaction.class,
					TeacherTransaction.class,
					EmployeeTransaction.class
				}
		};
		
		KeyStroke [][] keys = {
				{
					KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
					KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
					KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),
				},
				{null, null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null}
		};
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		JMenu [] menus = {new JMenu("تعریف اشخاص"), new JMenu("جستجو"), new JMenu("تعاریف مالی"), new JMenu("نقل و انتقالات مالی")};
		Font font = new Font("Tahoma", Font.PLAIN, 11);
		setJMenuBar(menuBar);
		
		for (int i = 0; i < labels.length; i++) 
		{
			menus[i].setFont(font);
			menus[i].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			menuBar.add(menus[i]);
			
			for(int j = 0; j < labels[i].length; j++)
			{
				JMenuItem item;
				item = new JMenuItem(labels[i][j]);
				item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				item.setHorizontalAlignment(JMenuItem.RIGHT);
				item.setPreferredSize(new Dimension(200, 23));
				item.setFont(font);
				item.addActionListener(new OpenFrame(Frames[i][j]));
				item.setAccelerator(keys[i][j]);
				menus[i].add(item);
			}
		}
		
		// Tool bar
		JToolBar toolBar = new JToolBar(); 
		toolBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		String [] pics = {
				"images/home.png", 
				"images/user.png", 
				"images/adim.png", 
				"images/emp.png", 
				"images/fax.png", 
				"images/print.png", 
				"images/pictures.png"
		};
		
		String [] tips = {"خانه", "متعلم جدید", "معلم جدید", "کارمند جدید", "وارد کردن", "", ""};
		JButton [] buttons = new JButton[pics.length];
		for(int i=0; i<pics.length; i++){
			buttons[i] = new JButton(new ImageIcon(pics[i]));
			buttons[i].setBorderPainted(false);
			buttons[i].setToolTipText(tips[i]);
			toolBar.add(buttons[i]);
		}
		buttons[1].addActionListener(new OpenFrame(NewStudent.class));
		buttons[2].addActionListener(new OpenFrame(NewTeacher.class));
		buttons[3].addActionListener(new OpenFrame(NewEmployee.class));
		buttons[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDialog diag = new FileDialog(self, "Save", FileDialog.SAVE);
				diag.setVisible(true);
			}
		});
		
		//==========
		
		JPanel center = new JPanel(new BorderLayout(3, 0));
		JTabbedPane tabs = new JTabbedPane();
		JPanel cright = new JPanel(new BorderLayout());
		JPanel schedule = new JPanel(new BorderLayout());
		JPanel fast = new JPanel();
		center.add(toolBar, BorderLayout.NORTH);
		center.add(tabs, BorderLayout.CENTER);
		center.add(cright, BorderLayout.EAST);
//		center.add(schedule, BorderLayout.WEST);
		add(center, BorderLayout.NORTH);
		
		Border border = new LineBorder(Color.gray);
		cright.setBorder(border);
		tabs.setBorder(border);
		
		tabs.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		tabs.add("متعلمین ممتاز", null);
		tabs.add("اوسط نمرات", null);
		tabs.add("آمار متعلمین", null);
		
		JLabel ks = new JLabel(new ImageIcon("images/pen.jpg"));
		JLabel tip = new JLabel("دسترسی سریع");
		tip.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		tip.setFont(new Font("serif", Font.BOLD, 16));
		tip.setBorder(new EmptyBorder(3, 10, 3, 10));
		tip.setBackground(new Color(9, 87, 151));
		tip.setForeground(Color.WHITE);
		tip.setOpaque(true);
		String [] text = {"کد متعلم:", "کد استاد:", "کد کارمند:"};
		JLabel [] lab = new JLabel [3];
		final JTextField [] fff = new JTextField [3];
		JButton [] bbb = new JButton [3];
		
		cright.add(ks, BorderLayout.NORTH);
		cright.add(tip, BorderLayout.CENTER);
		cright.add(fast, BorderLayout.SOUTH);
		
		fast.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		fast.setBorder(new EmptyBorder(4, 4, 4, 4));
		fast.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.anchor = GridBagConstraints.NORTH;
		cons.insets = new Insets(2, 2, 2, 2);
		
		for(int i=0; i<text.length; i++){
			cons.gridy = i;
			lab[i] = new JLabel(text[i]);
			fff[i] = new JTextField();
			bbb[i] = new JButton(new ImageIcon("images/zoom.png"));
			bbb[i].setMargin(new Insets(2, 2, 2, 2));
			fast.add(lab[i], cons);
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.weightx = 1;
			fast.add(fff[i], cons);
			cons.fill = GridBagConstraints.NONE;
			cons.weightx = 0;
			fast.add(bbb[i], cons);
		}
		
		JPanel ppp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		ppp.add(new JButton("SEARCH"));
		schedule.add(ppp, BorderLayout.NORTH);
		DefaultTableModel model = new DefaultTableModel(new String [] {"ساعت اول", "ساعت دوم", "ساعت سوم", "ساعت چهارم", "ساعت پنجم", "ساعت ششم"}, 5);
		JTable table = new JTable(model);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		schedule.add(new JScrollPane(table), BorderLayout.CENTER);

		setSize(1000, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		ActionListener findst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(db.findPerson("students", "st_id", fff[0].getText())){
					OpenFrame.openFrame(NewStudent.class, new Class<?>[]{int.class}, new Object[]{Integer.parseInt(fff[0].getText())});
				}
				else{
					PenDiags.showWarn("کد شخص اشتباه است! لطفا کد صحیح را وارد نمایید.");
				}
			}
		};
		ActionListener findtc = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(db.findPerson("teachers", "tc_id", fff[1].getText())){
					OpenFrame.openFrame(NewTeacher.class, new Class<?>[]{int.class}, new Object[]{Integer.parseInt(fff[1].getText())});
				}
				else{
					PenDiags.showWarn("کد شخص اشتباه است! لطفا کد صحیح را وارد نمایید.");
				}
			}
		};
		ActionListener findemp = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(db.findPerson("employee", "emp_id", fff[2].getText())){
					OpenFrame.openFrame(NewEmployee.class, new Class<?>[]{int.class}, new Object[]{Integer.parseInt(fff[2].getText())});
				}
				else{
					PenDiags.showWarn("کد شخص اشتباه است! لطفا کد صحیح را وارد نمایید.");
				}
			}
		};
		fff[0].addActionListener(findst);
		bbb[0].addActionListener(findst);
		fff[1].addActionListener(findtc);
		bbb[1].addActionListener(findtc);
		fff[2].addActionListener(findemp);
		bbb[2].addActionListener(findemp);
	}

	public static void main(String a[]) 
	{
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} 
		catch (Exception e) {}
		
		if(!DataBase.setupLink()){
			System.exit(0);
		}
		new Main();
//		new CreatePaper(20595, 8);
		
	}
}