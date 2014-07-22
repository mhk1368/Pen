package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import net.kabulsoft.pen.db.Account;
import net.kabulsoft.pen.db.Cash;
import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class NewAccountTrans extends JDialog{

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static NewAccountTrans self;
	private JComboBox<Account> account;
	private JComboBox<Cash> cash;
	private JTextField amount, desc;
	private JSpinner transDate;
	private JButton save, cancel;
	private FinanceInfo info;
	
	public NewAccountTrans(int type){
		
		isOpen = true;
		self = this;
		setTitle((type == 1)? "ثبت دریافتهای عایدات" : "ثبت پرداختهای مصارف");
		info = new FinanceInfo();
		
		JPanel top, center, bottom;
		top = new JPanel(new BorderLayout());
		center = new JPanel();
		bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		JLabel title = new JLabel((type == 1)? " ثبت دریافتهای عایدات" : " ثبت پرداختهای مصارف");
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks1.png"));
		top.add(title, BorderLayout.EAST);
		top.add(ks, BorderLayout.WEST);
		top.setBackground(new Color(9, 87, 151));
		
		JLabel [] labels = new JLabel[5];
		String [] texts = {"حساب:", "صندوق:", "مبلغ:", "تاریخ:", "شرح:"};
		account = new JComboBox<>(info.allAccount(type));
		account.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)account.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		cash = new JComboBox<>(info.allCash());
		cash.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)cash.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		amount = new JTextField();
		transDate = new JSpinner();
		transDate.setModel(new PersianDateModel(transDate));
		desc = new JTextField();
		desc.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		Component [] elements = {account, cash, amount, transDate, desc};
		
		center.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		center.setBorder(new LineBorder(Color.GRAY));
		GroupLayout layout = new GroupLayout(center);
		center.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		SequentialGroup hGroup = layout.createSequentialGroup();
		SequentialGroup vGroup = layout.createSequentialGroup();
		ParallelGroup p1 = layout.createParallelGroup();
		ParallelGroup p2 = layout.createParallelGroup();
		hGroup.addGroup(p1);
		hGroup.addGroup(p2);
		
		for(int i=0; i<labels.length; i++)
		{
			labels[i] = new JLabel(texts[i]);
			p1.addComponent(labels[i]);
			p2.addComponent(elements[i]);
			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(labels[i]).addComponent(elements[i]));
		}
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
		
		save = new JButton("ذخیره");
		cancel = new JButton("انصراف");
		bottom.add(cancel);
		bottom.add(save);
		
		setBounds(0, 50, 450, 260);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		
		// Listeners
		amount.addFocusListener(new Reset());
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(!amount.getText().matches("^[0-9]+$"))
				{
					amount.setBackground(Color.RED);
					PenDiags.showMsg("یک مقدار عددی وارد نمایید!");
					return;
				}
				int acid = ((Account)account.getSelectedItem()).id;
				int cashid = ((Cash)cash.getSelectedItem()).id;
				int value = Integer.parseInt(amount.getText());
				Date date = ((PersianCalendar)transDate.getValue()).getTime();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String dateStr = df.format(date);
				String descr = desc.getText();
				
				if(info.saveAccountTrans(acid, cashid, value, dateStr, descr))
				{
					PenDiags.showMsg(PenDiags.SUCCESS);
					amount.setText("");
					desc.setText("");
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
}





















