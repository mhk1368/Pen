package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

public class NewAccount extends JDialog {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static NewAccount self;
	private JTextField name;
	private JRadioButton revenue, expence;
	private ButtonGroup buttons;
	private JButton save, cancel;
	private FinanceInfo info = new FinanceInfo();
	
	public NewAccount() {
		
		isOpen = true;
		self = this;
		setTitle("ایچاد حساب مالی");

		JPanel topPanel = new JPanel(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		
		JLabel title = new JLabel(" ایجاد حساب مالی");
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks1.png"));
		topPanel.add(title, BorderLayout.EAST);
		topPanel.add(ks, BorderLayout.WEST);
		topPanel.setBackground(new Color(9, 87, 151));

		JPanel middlePanel = new JPanel();
		middlePanel.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		middlePanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel label1, label2;
		label1 = new JLabel("نوع:");
		label2 = new JLabel("نام حساب:");
		name = new JTextField();
		revenue = new JRadioButton("مصارف");
		expence = new JRadioButton("عایدات");
		name.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		revenue.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		expence.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		JPanel types = new JPanel(new GridLayout(1, 2));
		types.add(revenue);
		types.add(expence);
		buttons = new ButtonGroup();
		buttons.add(revenue);
		buttons.add(expence);
		
		//---------------------------------
		
		GroupLayout layout = new GroupLayout(middlePanel);
		middlePanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		SequentialGroup hGroup = layout.createSequentialGroup();
		SequentialGroup vGroup = layout.createSequentialGroup();
		ParallelGroup p1 = layout.createParallelGroup();
		ParallelGroup p2 = layout.createParallelGroup();
		hGroup.addGroup(p1).addGroup(p2);
		
		p1.addComponent(label1).addComponent(label2);
		p2.addGroup(layout.createSequentialGroup().addComponent(revenue).addComponent(expence)).addComponent(name);
		
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(label1)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(revenue).addComponent(expence)));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(label2).addComponent(name));
        
        layout.setHorizontalGroup(hGroup);
        layout.setVerticalGroup(vGroup);
		
		//------------------------------------

		add(middlePanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		save = new JButton("ذخیره");
		cancel = new JButton("انصراف");
		bottomPanel.add(cancel);
		bottomPanel.add(save);
		add(bottomPanel, BorderLayout.SOUTH);

		setSize(350, 180);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		name.addFocusListener(new Reset());
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(!validateForm()) return;
				if(info.insertAccount(name.getText(), (expence.isSelected())? "1":"0"))
				{
					PenDiags.showMsg("حساب مالی افزوده شد!");
					name.setText("");
					buttons.clearSelection();
				}
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
	
	private boolean validateForm()
	{
		if(buttons.getSelection() == null){
			PenDiags.showMsg("لطفا نوع حساب مالی را انتخاب کنید!");
			return false;
		}
		if(name.getText().equals("")){
			name.setBackground(Color.RED);
			PenDiags.showMsg("لطفا نام حساب مالی را وارد کنید!");
			return false;
		}
		return true;
	}
}
