package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.db.Teacher;
import net.kabulsoft.pen.db.TeacherInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class NewTeacherTrans extends JDialog{

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static NewTeacherTrans self;
	private JComboBox<Cash> cash;
	private FilterComboBox tcname;
	private JTextField value, tcid, desc;
	private JSpinner transDate;
	private JButton save, cancel;
	private FinanceInfo info = new FinanceInfo();
	
	public NewTeacherTrans(){
		
		setTitle("پرداخت معاش اساتید");
		isOpen = true;
		self = this;

		JLabel tip1, tip2, tip3, tip4, tip5, tip6;
		tip1 = new JLabel("نام استاد:");
		tip2 = new JLabel("صندوق:");
		tip3 = new JLabel("مبلغ:");
		tip4 = new JLabel("کد استاد:");
		tip5 = new JLabel("تاریخ:");
		tip6 = new JLabel("شرح:");
		cash = new JComboBox<>(info.allCash());
		cash.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)cash.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		tcname = new FilterComboBox();
		value = new JTextField(10);
		tcid = new JTextField();
		tcid.setEditable(false);
		tcid.setFocusable(false);
		tcid.setBackground(Color.WHITE);
		desc = new JTextField();
		desc.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		transDate = new JSpinner();
		transDate.setModel(new PersianDateModel(transDate));
		
		JPanel top, center, bottom;
		top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top.setBackground(new Color(103, 64, 228));
		JLabel title = new JLabel("پرداخت معاش اساتید");
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
		hGroup.addGroup(layout.createParallelGroup().addComponent(tcname).addComponent(cash).addComponent(value));
		hGroup.addGroup(layout.createParallelGroup().addComponent(tip4).addComponent(tip5).addComponent(tip6));
		hGroup.addGroup(layout.createParallelGroup().addComponent(tcid).addComponent(transDate).addComponent(desc));
		
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(tip1).addComponent(tcname).addComponent(tip4).addComponent(tcid));
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
		
		tcname.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(tcname.getSelectedIndex() > -1)
				{
					tcid.setText(String.valueOf(((Teacher)tcname.getSelectedItem()).id));
					tcid.setBackground(Color.WHITE);
				}
				else{
					tcid.setText("");
				}
			}
		});
		
		value.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				value.setBackground(Color.WHITE);
			}
		});
		
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
		if(!tcid.getText().matches("^[0-9]+$"))
		{
			tcid.setBackground(Color.RED);
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
			int tid = Integer.parseInt(tcid.getText());
			int val = Integer.parseInt(value.getText());
			int cid = ((Cash)cash.getSelectedItem()).id;
			Date date = ((PersianCalendar)transDate.getValue()).getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = df.format(date);
			
			if(info.saveTeacherReceipt(tid, val, cid, dateString, desc.getText())){
				PenDiags.showMsg(PenDiags.SUCCESS);
				resetForm();
			}
		} 
		catch (NumberFormatException e) {return;}
	}
	
	private void resetForm()
	{
		tcname.setSelectedIndex(-1);
		cash.setSelectedIndex(0);
		tcid.setText("");
		value.setText("");
		desc.setText("");
	}
}

//Auto Filter Combo Box

class FilterComboBox extends JComboBox<Teacher> {

	private static final long serialVersionUID = 1L;
	protected JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
	private TeacherInfo info = new TeacherInfo();
	private int pos = 0;

	public FilterComboBox()
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
	    Vector<Teacher> teachers = info.searchByName(text);
	    if (teachers.size() > 0) {
	        this.setModel(new DefaultComboBoxModel<Teacher>(teachers));
	        textfield.setCaretPosition(0);
	        this.setSelectedItem(text);
	        this.showPopup();
	    }
	    else {
	        this.hidePopup();
	    }
	}
}
