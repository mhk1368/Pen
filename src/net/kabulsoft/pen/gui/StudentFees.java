package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class StudentFees extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static StudentFees self;
	private boolean isModified = false;
	private JComboBox<Integer> year;
	private JTable table;
	private DefaultTableModel model;
	private JButton save, cancel;
	private FinanceInfo info = new FinanceInfo();

	public StudentFees() {
		super("تعیین مبلغ فیس");
		isOpen = true;
		self = this;

		JPanel top = new JPanel(new GridLayout(2, 1));
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel title = new JLabel("تعیین مبلغ فیس سالانه");
		title.setFont(new Font("serif", Font.BOLD, 20));
		top1.setBackground(new Color(103, 64, 228));
		title.setForeground(Color.WHITE);
		top1.add(title);
		
		JLabel tip = new JLabel("سال تحصیلی:");
		year = new JComboBox<>();
		year.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)year.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		year.setPreferredSize(new Dimension(100, 22));
		for(int y=new PersianCalendar().getPersianYear(); y>1380; y--){
			year.addItem(y);
		}
		
		top2.add(tip);
		top2.add(year);
		
		top.add(top1);
		top.add(top2);
		add(top, BorderLayout.NORTH);
		Search find = new Search();
		year.addActionListener(find);

		model = new FeesModel(new String[] { "صنف", "مبلغ فیس سالانه" });
		table = new JTable(model);
		table.setShowGrid(false);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		save = new JButton("ذخیره");
		cancel = new JButton("خروج");
		bright.add(save);
		bright.add(cancel);
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);

		setSize(400, 350);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		renderData();

		// Listeners
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				isModified = true;
			}
		});

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getRowCount() != 0 && isModified) {
					saveData();
				}
			}
		});

		cancel.addActionListener(new ActionListener() {
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
	
	private void saveData()
	{
		boolean yes = true;
		for (int i=0; i < table.getRowCount(); i++) 
		{
			Object val = model.getValueAt(i, 1);
			int y = (int)year.getSelectedItem();
			int c = Integer.parseInt(model.getValueAt(i, 0).toString());
			int v = (val == null)? 0 : Integer.parseInt(val.toString());
			
			if(!info.saveFees(y, c, v)){
				yes = false;
			}
		}
		if (yes) {
			PenDiags.showMsg("اطلاعات مبالغ فیس ذخیره شد!");
		}
		renderData();
	}

	private class Search implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			renderData();
		}
	}

	private void renderData() 
	{
		Vector<Object> fees = info.studentFees((int)year.getSelectedItem());
		model.setRowCount(0);
		for(Object fee : fees){
			model.addRow((Vector<?>) fee);
		}			
		isModified = false;
	}

	private void closeConfirm() {
		if (isModified) {
			int signal = PenDiags.showConf("اطلاعات ذخیره شوند؟", PenDiags.YNC);
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

class FeesModel extends DefaultTableModel 
{
	private static final long serialVersionUID = 1L;

	public FeesModel(String cols[]) {
		super(cols, 0);
	}

	public boolean isCellEditable(int row, int col) {
		return (col == 1);
	}
	
	public Class<?> getColumnClass(int column) {
		return Integer.class;
	}
}