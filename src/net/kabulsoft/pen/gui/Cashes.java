package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import net.kabulsoft.pen.util.PenDiags;

public class Cashes extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static Cashes self;
	private boolean isModified = false;
	private JTextField word;
	private JTable table;
	private DefaultTableModel model;
	private JButton search, create, delete, save, cancel;
	private FinanceInfo info = new FinanceInfo();

	public Cashes() {
		super("صندوقها");
		isOpen = true;
		self = this;

		JPanel top = new JPanel(new GridLayout(2, 1));
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel title = new JLabel("تعریف صندوقها");
		title.setFont(new Font("serif", Font.BOLD, 20));
		top1.setBackground(new Color(103, 64, 228));
		title.setForeground(Color.WHITE);
		top1.add(title);
		
		JLabel label = new JLabel("نام صندوق:");
		word = new JTextField(30);
		word.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		search = new JButton("جستجو");
		top2.add(label);
		top2.add(word);
		top2.add(search);
		
		top.add(top1);
		top.add(top2);
		add(top, BorderLayout.NORTH);
		Search find = new Search();
		word.addActionListener(find);
		word.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				renderData();
			}
		});
		search.addActionListener(find);

		model = new DefaultTableModel(new String[] { "کد صندوق", "نام صندوق", "صندوقدار" }, 0)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				return col != 0;
			}
		};
		table = new JTable(model);
		table.setShowGrid(false);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(90);
		
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		
		JTextField editor = new JTextField();
		editor.setBorder(new LineBorder(Color.BLACK));
		editor.setHorizontalAlignment(JTextField.RIGHT);
		editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(editor));
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(editor));
		
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		create = new JButton("صندوق جدید");
		delete = new JButton("حذف صندوق");
		bleft.add(create);
		bleft.add(delete);
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

		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int rows[] = table.getSelectedRows();
				if (rows.length == 0) return;
				int sig = PenDiags.showConf(PenDiags.DELETEASK, PenDiags.YN);
				if(sig != 0) return;
				for (int i = rows.length - 1; i >= 0; i--) 
				{
					if (model.getValueAt(rows[i], 0) == null) {
						model.removeRow(rows[i]);
					} 
					else {
						String id = model.getValueAt(rows[i], 0).toString();
						if(id.equals("100")){
							continue;
						}
						if(info.deleteCash(id)){
							model.removeRow(rows[i]);
						}
					}
				}
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
			String name = model.getValueAt(i, 1).toString();
			String cashier = model.getValueAt(i, 2).toString();
			if(model.getValueAt(i, 0) == null)
			{
				if(!info.insertCash(name, cashier)){
					yes = false;
				}
			}
			else{
				if(!info.editCash(name, cashier, model.getValueAt(i, 0).toString())){
					yes = false;
				}
			}

		}
		if (yes) {
			PenDiags.showMsg(PenDiags.SUCCESS);
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
		Vector<Object> recordSet = info.searchCash(word.getText());
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
