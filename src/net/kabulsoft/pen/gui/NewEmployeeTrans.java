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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.LineBorder;

import net.kabulsoft.pen.db.Cash;
import net.kabulsoft.pen.db.Employee;
import net.kabulsoft.pen.db.EmployeeInfo;
import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class NewEmployeeTrans extends JDialog{

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static NewEmployeeTrans self;
	private JComboBox<Cash> cash;
	private FilterEmployee empname;
	private JTextField value, empid, desc;
	private JSpinner transDate;
	private JButton save, cancel;
	private FinanceInfo info = new FinanceInfo();
	
	public NewEmployeeTrans(){
		
		setTitle("پرداخت معاش کارمندان");
		isOpen = true;
		self = this;

		JLabel tip1, tip2, tip3, tip4, tip5, tip6;
		tip1 = new JLabel("نام کارمند:");
		tip2 = new JLabel("صندوق:");
		tip3 = new JLabel("مبلغ:");
		tip4 = new JLabel("کد کارمند:");
		tip5 = new JLabel("تاریخ:");
		tip6 = new JLabel("شرح:");
		cash = new JComboBox<>(info.allCash());
		cash.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)cash.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		empname = new FilterEmployee();
		value = new JTextField(10);
		empid = new JTextField();
		empid.setEditable(false);
		empid.setFocusable(false);
		empid.setBackground(Color.WHITE);
		desc = new JTextField();
		desc.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		transDate = new JSpinner();
		transDate.setModel(new PersianDateModel(transDate));
		
		JPanel top, center, bottom;
		top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top.setBackground(new Color(103, 64, 228));
		JLabel title = new JLabel("پرداخت معاش کارمندان");
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		top.add(title);
		
		center = new JPanel();
		center.setFocusCycleRoot(true);
		center.setBorder(new LineBorder(Color.GRAY));
		center.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		center.setBorder(new LineBorder(Color.GRAY));
		GroupLayout layout = new GroupLayout(center);
		center.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		
		hGroup.addGroup(layout.createParallelGroup().addComponent(tip1).addComponent(tip2).addComponent(tip3));
		hGroup.addGroup(layout.createParallelGroup().addComponent(empname).addComponent(cash).addComponent(value));
		hGroup.addGroup(layout.createParallelGroup().addComponent(tip4).addComponent(tip5).addComponent(tip6));
		hGroup.addGroup(layout.createParallelGroup().addComponent(empid).addComponent(transDate).addComponent(desc));
		
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(tip1).addComponent(empname).addComponent(tip4).addComponent(empid));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(tip2).addComponent(cash).addComponent(tip5).addComponent(transDate));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(tip3).addComponent(value).addComponent(tip6).addComponent(desc));
		
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
		
		bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.setFocusCycleRoot(true);
		cancel = new JButton("انصراف");
		save = new JButton("ثبت پرداخت");
		bottom.add(save);
		bottom.add(cancel);
		
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		setBounds(0, 50, 500, 210);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		
		empname.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(empname.getSelectedIndex() > -1)
				{
					empid.setText(String.valueOf(((Employee)empname.getSelectedItem()).id));
					empid.setBackground(Color.WHITE);
				}
				else{
					empid.setText("");
				}
			}
		});
		
		value.addFocusListener(new Reset());
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveData();
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
	
	private void saveData()
	{
		if(!empid.getText().matches("^[0-9]+$"))
		{
			empid.setBackground(Color.RED);
			PenDiags.showMsg("لطفا شخص را انتخاب کنید تا کد مربوطه درج شود!");
			return;
		}
		
		if(!value.getText().matches("^[0-9]+$"))
		{
			value.setBackground(Color.RED);
			PenDiags.showMsg("لطفا یک مقدار عددی وارد نمایید");
			return;
		}
		
		try {
			int tid = Integer.parseInt(empid.getText());
			int val = Integer.parseInt(value.getText());
			int cid = ((Cash)cash.getSelectedItem()).id;
			Date date = ((PersianCalendar)transDate.getValue()).getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = df.format(date);
			
			if(info.saveEmployeeReceipt(tid, val, cid, dateString, desc.getText())){
				PenDiags.showMsg(PenDiags.SUCCESS);
				resetForm();
			}
		} 
		catch (NumberFormatException e) {return;}
	}
	
	private void resetForm()
	{
		empname.setSelectedIndex(-1);
		cash.setSelectedIndex(0);
		empid.setText("");
		value.setText("");
		desc.setText("");
	}
}

//Auto Filter Combo Box

class FilterEmployee extends JComboBox<Employee> {

	private static final long serialVersionUID = 1L;
	protected JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
	private EmployeeInfo info = new EmployeeInfo();
	private int pos = 0;

	public FilterEmployee()
	{
	    this.setEditable(true);
	    this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
	    ((JLabel)this.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
	    textfield.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
	    
	    textfield.addKeyListener(new KeyAdapter()
	    {
	        public void keyPressed(KeyEvent e) 
	        {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                    pos=textfield.getCaretPosition();
	                    if(textfield.getSelectedText() == null)
	                    {
	                        textfield.setCaretPosition(0);
	                        comboFilter(textfield.getText());
	                        textfield.setCaretPosition(pos);
	                    }
	                 }
	            });
	        }
	    });
	}

	public void comboFilter(String text) 
	{
	    Vector<Employee> employees = info.searchByName(text);
	    if (employees.size() > 0) {
	        this.setModel(new DefaultComboBoxModel<Employee>(employees));
	        textfield.setCaretPosition(0);
	        this.setSelectedItem(text);
	        this.showPopup();
	    }
	    else {
	        this.hidePopup();
	    }
	}
}

