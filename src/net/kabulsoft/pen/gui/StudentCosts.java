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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.PenDiags;

import com.sahandrc.calendar.PersianCalendar;

public class StudentCosts extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static StudentCosts self;
	private boolean isModified = false;
	private JComboBox<Integer> year;
	private JTable table;
	private DefaultTableModel model;
	private JButton create, save, cancel;
	private FinanceInfo info = new FinanceInfo();

	public StudentCosts() {
		super("هزینه های متعلمین");
		isOpen = true;
		self = this;

		JPanel top = new JPanel(new GridLayout(2, 1));
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel title = new JLabel("هزینه های قابل پرداخت متعلمین");
		title.setFont(new Font("serif", Font.BOLD, 20));
		top1.setBackground(new Color(103, 64, 228));
		title.setForeground(Color.WHITE);
		top1.add(title);
		
		JLabel label = new JLabel("مبلغ هزینه ها برای سال:");
		year = new JComboBox<>();
		year.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		year.setPreferredSize(new Dimension(100, 22));
		((JLabel)year.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		
		for(int y = new PersianCalendar().getPersianYear(); y > 1380; y--){
			year.addItem(y);
		}
		
		top2.add(label);
		top2.add(year);
		
		top.add(top1);
		top.add(top2);
		add(top, BorderLayout.NORTH);
		Search find = new Search();
		year.addActionListener(find);

		model = new CostsModel(new String[] { "کد هزینه", "نام هزینه", "مبلغ قابل پرداخت ماهانه" });
		table = new JTable(model);
		table.setShowGrid(false);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(120);
		table.getColumnModel().getColumn(2).setMinWidth(120);
		
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		
		JTextField editor = new JTextField();
		editor.setBorder(new LineBorder(Color.BLACK));
		editor.setHorizontalAlignment(JTextField.RIGHT);
		editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(editor));
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		create = new JButton("هزینه جدید");
		bleft.add(create);
		save = new JButton("ذخیره");
		cancel = new JButton("خروج");
		bright.add(save);
		bright.add(cancel);
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);

		setSize(550, 400);
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

		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER && table.getSelectedRow() == table.getRowCount() - 1) {
					addRecord();
				}
			}
		});

		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addRecord();
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

	private void addRecord() {
		if (isFill()) {
			model.addRow(new Object [] {null, "", ""});
		}
	}

	private boolean isFill() {
		if (table.getRowCount() == 0) {
			return true;
		} else if (!table.getValueAt(table.getRowCount() - 1, 1).equals("")) {
			return true;
		}
		return false;
	}

	private void saveData()
	{
		boolean yes = true;
		for (int i=0; i < table.getRowCount(); i++) 
		{
			if(model.getValueAt(i, 1).equals("")){
				continue;
			}
			String y = year.getSelectedItem().toString();
			String name = model.getValueAt(i, 1).toString();
			Object amount = model.getValueAt(i, 2);
			if(model.getValueAt(i, 0) == null)
			{
				if(!info.insertCost(name)){
					yes = false;
				}
				if(amount != null){
					info.savePrice("0", y, amount.toString());
				}
			}
			else{
				String id = model.getValueAt(i, 0).toString();
				if(!info.editCost(name, id)){
					yes = false;
				}
				if(amount != null){
					info.savePrice(id, y, amount.toString());
				}
			}

		}
		if (yes) {
			PenDiags.showMsg("اطلاعات هزینه ها ذخیره شد!");
		} else {
			JOptionPane.showMessageDialog(null, "Problem!");
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
		Vector<Object> recordSet = info.studentCosts((int)year.getSelectedItem());
		Iterator<Object> iterator = recordSet.iterator();
		model.setRowCount(0);
		while (iterator.hasNext()) {
			model.addRow((Vector<?>) iterator.next());
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

class CostsModel extends DefaultTableModel 
{
	private static final long serialVersionUID = 1L;

	public CostsModel(String cols[]) {
		super(cols, 0);
	}

	public boolean isCellEditable(int row, int col) {
		return (col != 0);
	}
	
	public Class<?> getColumnClass(int c) {
		if(c == 2){
			return Integer.class;
		}
		return String.class;
	}
}