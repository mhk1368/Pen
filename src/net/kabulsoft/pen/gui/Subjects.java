package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.SubjectInfo;
import net.kabulsoft.pen.util.PenDiags;

public class Subjects extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static Subjects self;
	private boolean isModified = false;
	private JTextField word;
	private JComboBox<Integer> grade;
	private JTable table;
	private DefaultTableModel model;
	private JButton create, delete, save, cancel;
	private SubjectInfo info = new SubjectInfo();

	public Subjects() {
		
		super("جستجوی مضامین");
		isOpen = true;
		self = this;

		JPanel top = new JPanel(new GridLayout(2, 1));
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JLabel title = new JLabel("جستجو و تعریف مضامین");
		title.setFont(new Font("serif", Font.BOLD, 20));
		top1.setBackground(new Color(103, 64, 228));
		title.setForeground(Color.WHITE);
		top1.add(title);
		
		JLabel label1 = new JLabel("نام مضمون:");
		JLabel label2 = new JLabel("مربوط صنف:");
		word = new JTextField(20);
		word.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		grade = new JComboBox<>(new Integer [] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
		grade.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)grade.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		grade.setPreferredSize(new Dimension(50, 20));

		top2.add(label1);
		top2.add(word);
		top2.add(label2);
		top2.add(grade);
		
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
		grade.addActionListener(find);

		model = new DefaultTableModel(new String[] { "کد", "نام", "صنف" }, 0)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				return col != 0;
			}
			
			public Class<?> getColumnClass(int column) {
				if(column == 2){
					return Integer.class;
				}
				return String.class;
			}
		};
		table = new JTable(model);
		table.setShowGrid(false);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(2).setMaxWidth(50);
		
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		
		JTextField editor = new JTextField();
		editor.setBorder(new LineBorder(Color.BLACK));
		editor.setHorizontalAlignment(JTextField.RIGHT);
		editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(editor));
		table.getColumnModel().getColumn(2).setCellEditor(new SpinnerEditor(1, 1, 12));
		
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		create = new JButton("مضمون جدید");
		delete = new JButton("حذف مضمون");
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

		delete.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				int rows[] = table.getSelectedRows();
				if(rows.length == 0) return;
				int sig = PenDiags.showConf(PenDiags.DELETEASK, PenDiags.YN);
				if(sig != 0) return;
				
				for(int i=rows.length-1; i>=0; i--){
					Object val = model.getValueAt(rows[i], 0);
					if(val == null) continue;
					info.deleteSubject(val.toString());
				}
				renderData();
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
			model.addRow(new Object [] { null, "", grade.getSelectedItem() });
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
			if(model.getValueAt(i, 1).equals("") || model.getValueAt(i, 2).equals("")){
				continue;
			}
			
			String name = model.getValueAt(i, 1).toString();
			String grade = model.getValueAt(i, 2).toString();
			
			if(Integer.parseInt(grade) < 1 || Integer.parseInt(grade) > 12){
				continue;
			}

			if (model.getValueAt(i, 0) == null) 
			{
				if(!info.insertSubject(name, grade)){
					yes = false;
				}
			} 
			else {
				if(!info.editSubject(model.getValueAt(i, 0).toString() , name)){
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
		Vector<Object> recordSet = info.searchSubject(word.getText(), grade.getSelectedItem().toString());
		Iterator<Object> iterator = recordSet.iterator();
		model.setRowCount(0);
		while (iterator.hasNext()) {
			model.addRow((Vector<?>) iterator.next());
		}
		isModified = false;
	}

	private void closeConfirm() {
		if (isModified) {
			int signal = PenDiags.showConf(PenDiags.SAVEASK, PenDiags.YNC);
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

// Spinner Editor

class SpinnerEditor extends DefaultCellEditor
{
	private static final long serialVersionUID = 1L;
	private JSpinner spinner;

    public SpinnerEditor(int val, int min, int max)
    {
    	super( new JTextField() );
    	spinner = new JSpinner(new SpinnerNumberModel(val, min, max, 1));
    	spinner.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    	spinner.setEditor(new NumberEditor(spinner, "#"));
    	spinner.setBorder(new LineBorder(Color.BLACK));
    }

    public Component getTableCellEditorComponent(
    	JTable table, Object value, boolean isSelected, int row, int column)
    {
    	if(value != null){
    		spinner.setValue( Integer.parseInt(value.toString()) );
    	}else{
    		spinner.setValue(0);
    	}
    	return spinner;
    }

    public Object getCellEditorValue()
    {
    	return spinner.getValue();
    }
}