package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.kabulsoft.pen.db.StudentInfo;
import net.kabulsoft.pen.util.PenDiags;

public class TransferredStudents extends JFrame {

	private static final long serialVersionUID = 1L;
	public static boolean isOpen = false;
	public static TransferredStudents self;
	private JButton next, prev, delete, exit;
	private JTextField word;
	private JTable table;
	private DefaultTableModel model;
	private StudentInfo info = new StudentInfo();
	private int page = 1;
	private int pages;

	public TransferredStudents() {
		super("متعلمین انتقالی");
		isOpen = true;
		self = this;

		JPanel top = new JPanel(new BorderLayout());
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel top2 = new JPanel(new BorderLayout());
		JPanel tright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel tleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top2.add(tright, BorderLayout.EAST);
		top2.add(tleft, BorderLayout.WEST);
		top.add(top1, BorderLayout.NORTH);
		top.add(top2, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);
		
		top1.setBackground(new Color(103, 64, 228));
		JLabel title = new JLabel("جستجوی متعلمین انتقالی");
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		top1.add(title);
		
		tright.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		word = new JTextField(20);
		word.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		next = new JButton("بعدی");
		prev = new JButton("قبلی");
		tright.add(new JLabel("کد یا نام متعلم:"));
		tright.add(word);
		tleft.add(next);
		tleft.add(prev);

		model = new DefaultTableModel(new String[] { "کد متعلم", "نام متعلم", "صنف", "سال", "شرح" }, 0)
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				return col == 4;
			}
		};
		table = new JTable(model);
		table.setShowGrid(false);
		table.setRowHeight(22);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setMaxWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
		table.getColumnModel().getColumn(3).setMaxWidth(100);
		table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.RIGHT );
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		JTextField editor = new JTextField();
		editor.setBorder(new LineBorder(Color.BLACK));
		editor.setHorizontalAlignment(JTextField.RIGHT);
		editor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(editor)
		{
			private static final long serialVersionUID = 1L;
			private Object value;
			
			public Component getTableCellEditorComponent(JTable table,
					Object value, boolean isSelected, int row, int column) {
				this.value = value;
				return super.getTableCellEditorComponent(table, value, isSelected, row, column);
			}
			
			public Object getCellEditorValue() 
			{
				Object value = super.getCellEditorValue();
				if(!value.toString().trim().equals("")){
					String id  = model.getValueAt(table.getSelectedRow(), 0).toString();
					String y  = model.getValueAt(table.getSelectedRow(), 3).toString();
					if(info.editTransformDescr(id, y, value.toString())){
						PenDiags.showMsg(PenDiags.SUCCESS);
						return value;
					}
				}
				return this.value;
			}
		});
		

		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);
		delete = new JButton("حذف");
		exit = new JButton("خروج");
		bright.add(delete);
		bright.add(exit);

		setSize(550, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);

		renderData();
		
		// Listeners
		word.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				page = 1;
				renderData();
			}
		});
		
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(page < pages) page++;
				renderData();
			}
		});
		
		prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(page > 1) page--;
				renderData();
			}
		});
		
		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()){
				case KeyEvent.VK_DELETE:
					deleteData();
					break;
				case KeyEvent.VK_PAGE_DOWN:
					if(page < pages) page++;
					renderData();
					break;
				case KeyEvent.VK_PAGE_UP:
					if(page > 1) page--;
					renderData();
					break;
				}
			}
		});
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteData();
			}
		});
		
		exit.addActionListener(new ActionListener() {
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

	private void renderData() 
	{
		int total = info.countTransform(word.getText());
		pages = (int) Math.ceil(total/10);
		Vector<Object> data = info.transferredStudents(word.getText(), page);
		model.setRowCount(0);
		for(Object row : data){
			model.addRow((Vector<?>)row);
		}
	}
	
	private void deleteData()
	{
		int [] rows = table.getSelectedRows();
		if(rows.length == 0) return;
		int sig = PenDiags.showConf(PenDiags.DELETEASK, PenDiags.YN);
		if(sig != 0) return;
		for(int i = rows.length-1; i >= 0; i--)
		{
			String id  = model.getValueAt(rows[i], 0).toString();
			String y  = model.getValueAt(rows[i], 3).toString();
			if(info.deleteTransform(id, y)){
				model.removeRow(rows[i]);
			}
		}
	}
}
