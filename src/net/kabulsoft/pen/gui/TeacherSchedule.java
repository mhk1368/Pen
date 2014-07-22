package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.TeacherInfo;

public class TeacherSchedule extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;
	private JButton print;
	private JComboBox<Integer> shifts;
	private TeacherInfo info = new TeacherInfo();
	private int tid;

	public TeacherSchedule(int id) {
		
		tid = id;
		setLayout(new BorderLayout());
		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel title = new JLabel("برنامه تدریس استاد");
		title.setFont(new Font("serif", Font.BOLD, 20));
		top1.setBackground(new Color(103, 64, 228));
		title.setForeground(Color.WHITE);
		top1.add(title);
		
		String teacher [] = info.findTeacher(id);
		JTextField tip = new JTextField(teacher[1], 15);
		tip.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		tip.setBackground(Color.WHITE);
		tip.setEditable(false);
		
		shifts = new JComboBox<>(new Integer [] { 1, 2, 3, 4, 5 });
		shifts.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)shifts.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		shifts.setPreferredSize(new Dimension(80, 20));
		
		top2.add(new JLabel("تقسیم اوقات استاد:"));
		top2.add(tip);
		top2.add(new JLabel("شیفت:"));
		top2.add(shifts);
		
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);
		
		model = new DefaultTableModel(new String[] { "ایام هفته", "ساعت اول", "ساعت دوم", "ساعت سوم", "ساعت چهارم", "ساعت پنجم", "ساعت ششم", "ساعت هفتم" }, 0)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		table = new JTable(model);
		table.setRowHeight(22);
		table.getColumnModel().getColumn(0).setMaxWidth(60);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.CENTER );

		add(new JScrollPane(table), BorderLayout.CENTER);
		
		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		print = new JButton("چاپ");
		bright.add(print);
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);

		// Listeners
		
		shifts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderData();
			}
		});
	}

	public void renderData() 
	{
		String days [] = {"شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهار شنبه", "پنج شنبه" };
		model.setRowCount(0);
		for(int i=0; i<days.length; i++){
			Vector<String> row = info.schedule(tid, i+1, (int)shifts.getSelectedItem());
			row.add(0, days[i]);
			model.addRow(row);
		}
	}
	
}

