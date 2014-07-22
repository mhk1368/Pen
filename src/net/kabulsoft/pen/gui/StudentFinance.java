package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.border.LineBorder;

import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class StudentFinance extends JPanel {

	private static final long serialVersionUID = 1L;
	private JComboBox<Integer> year;
	private JLabel [] tips;
	private JTextField [] fields;
	private JButton [] browse;
	private JButton search, exit;
	private FinanceInfo info = new FinanceInfo();
	private int sid;

	public StudentFinance(int id){
		
		sid = id;
		setLayout(new BorderLayout());
		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new BorderLayout());
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.CENTER);
		JPanel center = new JPanel();
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		JLabel title = new JLabel(" پرونده مالی متعلم");
		title.setFont(new Font("serif", Font.BOLD, 20));
		top1.setBackground(new Color(9, 87, 151));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks1.png"));
		top1.add(title, BorderLayout.EAST);
		top1.add(ks, BorderLayout.WEST);
		
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		year = new JComboBox<>();
		year.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)year.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		year.setPreferredSize(new Dimension(80, 20));
		int y = new PersianCalendar().getPersianYear();
		for(; y>1380; y--){
			year.addItem(y);
		}
		search = new JButton("محاسبه");
		top2.add(new JLabel("حسابهای سال:"));
		top2.add(year);
		top2.add(search);
		
		tips = new JLabel [6];
		fields = new JTextField [6];
		browse = new JButton [6];
		String [] texts = {"مبلغ فیس:", "دیگر هزینه ها:", "مجموع هزینه:", "تخفیف:", "پرداختی:", "باقیمانده:"};
		
		center.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		center.setBorder(new LineBorder(Color.GRAY));
		GroupLayout layout = new GroupLayout(center);
		center.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		
		ParallelGroup p1 = layout.createParallelGroup();
		ParallelGroup p2 = layout.createParallelGroup();
		ParallelGroup p3 = layout.createParallelGroup();
		
		hGroup.addGroup(p1);
		hGroup.addGroup(p2);
		hGroup.addGroup(p3);
		
		for (int i = 0; i < fields.length; i++) 
		{
			tips[i] = new JLabel(texts[i]);

			fields[i] = new JTextField();
			fields[i].setHorizontalAlignment(JTextField.RIGHT);
			fields[i].setEditable(false);
			fields[i].setBackground(Color.WHITE);
			
			browse[i] = new JButton("...");
			browse[i].setMargin(new Insets(1, 5, 1, 5));
			
			p1.addComponent(tips[i]);
			p2.addComponent(fields[i]);
			p3.addComponent(browse[i]);

			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(tips[i]).addComponent(fields[i]).addComponent(browse[i]));
		}
		
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
		
		browse[0].setEnabled(false);
		browse[2].setEnabled(false);
		browse[5].setEnabled(false);
		
		browse[3].setText("");
		browse[3].setIcon(new ImageIcon("images/save.png"));
		fields[3].setEditable(true);
		
		exit = new JButton("انصراف");
		bottom.add(exit);
		
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		});
		
		year.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		});
		
		fields[3].addFocusListener(new Reset());
		
		browse[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(CostsOfStudent.isOpen){
					CostsOfStudent.self.requestFocus();
				}else{
					new CostsOfStudent(sid, (int)year.getSelectedItem());
				}
			}
		});
		
		browse[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveDiscount();
			}
		});
		
		fields[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveDiscount();
			}
		});
		
		browse[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(StudentPayment.isOpen){
					StudentPayment.self.requestFocus();
				}else{
					new StudentPayment(sid, (int)year.getSelectedItem());
				}
			}
		});
	}
	
	public void renderData()
	{
		int y = (int)year.getSelectedItem();
		int fees = info.findFees(sid, y);
		int cost = info.stTotalCost(sid, y);
		int disc = info.findDiscount(sid, y);
		int payed = info.stTotalPayed(sid, y);

		fields[0].setText(String.valueOf(fees));
		fields[1].setText(String.valueOf(cost));
		fields[2].setText(String.valueOf(fees + cost));
		fields[3].setText(String.valueOf(disc));
		fields[4].setText(String.valueOf(payed));
		fields[5].setText(String.valueOf(fees + cost - disc - payed));
	}
	
	private void saveDiscount()
	{
		try {
			int value = Integer.parseInt(fields[3].getText());
			int y = (int)year.getSelectedItem();
			if(info.saveDiscount(sid, y, value))
			{
				PenDiags.showMsg("تخفیف ثبت شد!");
				int total = Integer.parseInt(fields[0].getText()) + Integer.parseInt(fields[1].getText());
				int subtract = Integer.parseInt(fields[4].getText()) + value;
				fields[5].setText(String.valueOf(total - subtract));
			}
		} 
		catch (NumberFormatException e) {
			fields[3].setBackground(Color.RED);
			PenDiags.showMsg("یک مقدار عددی وارد کنید!");
		}
	}
}
