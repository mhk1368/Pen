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
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.FinanceInfo;
import net.kabulsoft.pen.util.OpenFrame;
import net.kabulsoft.pen.util.PenDiags;

public class Accounts extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static Accounts self;
	private boolean isModified = false;
	private JTextField word;
	private JTable table;
	private DefaultTableModel model;
	private JButton search, create, save, cancel;
	private JRadioButton both, revenue, expence;
	private int type = 2;
	private FinanceInfo info = new FinanceInfo();

	public Accounts() {
		super("حسابهای مالی");
		isOpen = true;
		self = this;

		JPanel top = new JPanel(new GridLayout(2, 1));
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel title = new JLabel("حسابهای مالی (مصارف و عایدات)");
		title.setFont(new Font("serif", Font.BOLD, 20));
		top1.setBackground(new Color(103, 64, 228));
		title.setForeground(Color.WHITE);
		top1.add(title);
		
		JLabel label = new JLabel("نام حساب مالی:");
		word = new JTextField(30);
		word.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		search = new JButton("جستجو");
		both = new JRadioButton("همه");
		revenue = new JRadioButton("مصارف");
		expence = new JRadioButton("عایدات");
		both.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		revenue.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		expence.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		both.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(revenue);
		bg.add(expence);
		bg.add(both);
		both.addActionListener(new ToggleType(2));
		revenue.addActionListener(new ToggleType(0));
		expence.addActionListener(new ToggleType(1));
		
		top2.add(label);
		top2.add(word);
		top2.add(search);
		top2.add(both);
		top2.add(revenue);
		top2.add(expence);
		
		top.add(top1);
		top.add(top2);
		add(top, BorderLayout.NORTH);
		Search find = new Search();
		word.addActionListener(find);
		word.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				renderRows();
			}
		});
		search.addActionListener(find);

		model = new AccountsModel(new String[] { "کد حساب", "نام حساب", "نوع حساب" });
		table = new JTable(model);
		table.setShowGrid(false);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(80);
		table.getColumnModel().getColumn(2).setMaxWidth(90);
		
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
		create = new JButton("حساب جدید");
		bleft.add(create);
		save = new JButton("ذخیره");
		cancel = new JButton("خروج");
		bright.add(save);
		bright.add(cancel);
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);

		setSize(650, 400);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);

		renderRows();
		
		// Listeners
		
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				isModified = true;
			}
		});

		create.addActionListener(new OpenFrame(NewAccount.class));

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
	
	private class ToggleType implements ActionListener{
		int theType;
		public ToggleType(int t){
			this.theType = t;
		}
		public void actionPerformed(ActionEvent e) {
			type = this.theType;
			renderRows();
		}
	}

	private class Search implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			renderRows();
		}
	}

	@SuppressWarnings("unchecked")
	public void renderRows() {
		Vector<Object> recordSet = info.searchAccount(word.getText(), type);
		Iterator<Object> iterator = recordSet.iterator();
		model.setRowCount(0);
		while (iterator.hasNext()) {
			model.addRow((Vector<Object>) iterator.next());
		}
		isModified = false;
	}
	
	private void saveData()
	{
		boolean yes = true;
		for (int i=0; i < table.getRowCount(); i++) 
		{
			if(model.getValueAt(i, 1) == null){
				continue;
			}		
			String id = model.getValueAt(i, 0).toString();
			String name = model.getValueAt(i, 1).toString();
			if(id.equals("1") || id.equals("2")){
				continue;
			}
			if(!info.editAccount(name, id)){
				yes = false;
			}	
		}
		if (yes) {
			PenDiags.showMsg("اطلاعات حسابهای مالی ذخیره شد!");
		} else {
			JOptionPane.showMessageDialog(null, "Problem!");
		}
		renderRows();
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

class AccountsModel extends DefaultTableModel 
{
	private static final long serialVersionUID = 1L;

	public AccountsModel(String cols[]) {
		super(cols, 0);
	}

	public boolean isCellEditable(int row, int col) {
		return (col == 1);
	}
}