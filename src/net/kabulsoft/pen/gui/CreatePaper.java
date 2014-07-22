package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.LineBorder;

import net.kabulsoft.pen.db.StudentInfo;
import net.kabulsoft.pen.util.PenDiags;
import net.kabulsoft.pen.util.Reports;

public class CreatePaper extends JDialog {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static CreatePaper self;
	private JComboBox<Integer> grade, year;
	private JButton create, cancel;
	private JTextField path = new JTextField();
	private StudentInfo info = new StudentInfo();
	private int sid;
	
	public CreatePaper(int id, int g){
		isOpen = true;
		self = this;
		sid = id;

		JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel title = new JLabel("ایجاد کارنامه متعلم");
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		top.setBackground(new Color(103, 64, 228));
		top.add(title);
		add(top, BorderLayout.NORTH);
		
		grade = new JComboBox<>();
		grade.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)grade.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		year = new JComboBox<>();
		year.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)year.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		
		g = (g > 12)? 12 : g;
		for(; g>0; g--){
			grade.addItem(g);
		}
		putYears();
		
		JLabel tip = new JLabel("صنف:");
		JLabel tip1 = new JLabel("سال تحصیلی:");
		JLabel tip2 = new JLabel("مسیر:");
		path.setEditable(false);
		JButton browse = new JButton("...");
		browse.setMargin(new Insets(1, 5, 1, 5));

		JPanel middle = new JPanel();
		middle.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		middle.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
		
		GroupLayout layout = new GroupLayout(middle);
		middle.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		
		hGroup.addGroup(layout.createParallelGroup().addComponent(tip).addComponent(tip1).addComponent(tip2))
		.addGroup(layout.createParallelGroup().addComponent(grade).addComponent(year)
				.addGroup(layout.createSequentialGroup().addComponent(path).addComponent(browse)));

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(tip).addComponent(grade));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(tip1).addComponent(year));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(tip2).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(path).addComponent(browse)));
		
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
		
		add(middle, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
		create = new JButton("ایجاد کارنامه", new ImageIcon("images/excel.png"));
		cancel = new JButton("انصراف", new ImageIcon("images/cross.png"));
		create.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		cancel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		Insets insets = new Insets(2, 5, 2, 5);
		create.setMargin(insets);
		cancel.setMargin(insets);
		bottom.add(create);
		bottom.add(cancel);
		add(bottom, BorderLayout.SOUTH);
		
		setSize(400, 200);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		path.setText("C:\\Users\\Hussain\\Desktop\\workbook.xlsx");
		
		grade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				putYears();
			}
		});
		
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDialog diag = new FileDialog(self, "Save", FileDialog.SAVE);
				diag.setFile("workbook.xlsx");
				diag.setVisible(true);
				String fn = diag.getFile();
				if(fn == null) return;
				if(!fn.toLowerCase().endsWith(".xlsx")){
					fn += ".xlsx";
				}
				path.setText(diag.getDirectory()+fn);
			}
		});
		
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(year.getSelectedItem() == null){
					PenDiags.showMsg("متعلم برای این صنف هیچ نمره ای ندارد!");
					return;
				}
				if(path.getText().length() == 0){
					PenDiags.showMsg("لطفا مسیر فایل را مشخص نمایید!");
					return;
				}
				dispose();
				isOpen = false;
				int g = (int)grade.getSelectedItem();
				int y = (int)year.getSelectedItem();
				Reports report = new Reports();
				report.createPaper(sid, g, y, 0, 0);
				report.build(path.getText());
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
	
	private void putYears(){
		Vector<Integer> years = info.marksYears((int)grade.getSelectedItem(), sid);
		Iterator<Integer> items = years.iterator();
		year.removeAllItems();
		while(items.hasNext()){
			year.addItem(items.next());
		}
	}
}












