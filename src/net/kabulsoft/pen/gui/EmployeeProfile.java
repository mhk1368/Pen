package net.kabulsoft.pen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.border.LineBorder;

import net.kabulsoft.pen.db.EmployeeInfo;
import net.kabulsoft.pen.util.PenDiags;

public class EmployeeProfile extends JPanel {

	private static final long serialVersionUID = 1L;
	private NewTeacher frame;
	public SearchEmployee parent;
	private int empid;
	private JTextField fields[];
	private JButton save;
	private JLabel pic;
	private byte[] tc_image = null;
	private EmployeeInfo info = new EmployeeInfo();

	public EmployeeProfile(int id) {
		
		empid = id;
		JLabel title;
		if (id == 0) {
			title = new JLabel(" ثبت کارمند جدید");
		} else {
			title = new JLabel(" ویرایش کارمند");
		}
		
		setLayout(new BorderLayout());
		JPanel top = new JPanel(new BorderLayout());
		JPanel center = new JPanel(new BorderLayout());
		JPanel center1 = new JPanel();
		JPanel center2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		center.setBorder(new LineBorder(Color.GRAY));
		
		title.setFont(new Font("serif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		JLabel ks = new JLabel(new ImageIcon("images/ks1.png"));
		top.setBackground(new Color(9, 87, 151));
		top.add(title, BorderLayout.EAST);
		top.add(ks, BorderLayout.WEST);
		add(top, BorderLayout.NORTH);

		center1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		JLabel labels [] = new JLabel[6];
		fields = new JTextField[6];
		String texts[] = { "کد کارمند:", "نام و تخلص:", "نام پدر:", "نمبر تذکره:", "شماره تلفن:", "آدرس:" };
		Register register = new Register();
		Reset reset = new Reset();
		
		GroupLayout layout = new GroupLayout(center1);
		center1.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		
		ParallelGroup p1 = layout.createParallelGroup();
		ParallelGroup p2 = layout.createParallelGroup();
		
		hGroup.addGroup(p1).addGroup(p2);
		
		for (int i = 0; i < fields.length; i++) 
		{
			labels[i] = new JLabel(texts[i]);

			fields[i] = new JTextField();
			fields[i].addActionListener(register);
			fields[i].addFocusListener(reset);
			fields[i].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			fields[i].setHorizontalAlignment(JTextField.RIGHT);
			
			p1.addComponent(labels[i]);
			p2.addComponent(fields[i]);

			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(labels[i]).addComponent(fields[i]));
		}
		
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
		
		center2.setPreferredSize(new Dimension(160, 200));
		pic = new JLabel(new ImageIcon("images/pic.jpg"));
		pic.setPreferredSize(new Dimension(150, 200));
		pic.setBorder(new LineBorder(Color.BLACK));
		pic.setToolTipText("تصویر (200 * 150)");
		pic.setBackground(Color.WHITE);
		JLabel tip = new JLabel("تصویر (200 * 150)");
		tip.setPreferredSize(new Dimension(150, 15));
		tip.setHorizontalAlignment(JLabel.CENTER);
		center2.add(pic);
		center2.add(tip);
		
		center.add(center1, BorderLayout.CENTER);
		center.add(center2, BorderLayout.WEST);
		add(center, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bright = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel bleft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		save = new JButton("ذخیره");
		bright.add(save);		
		
		bottom.add(bleft, BorderLayout.WEST);
		bottom.add(bright, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);

		if (id == 0) {
			fields[0].setText(String.valueOf(info.maxEmployeeId()));
		} else {
			String values[] = info.findEmployee(id);
			for (int i = 0; i < fields.length; i++) {
				fields[i].setText(values[i]);
			}
			fields[0].setEditable(false);
			ImageIcon img = info.findImage(id);
			if(img != null){
				pic.setIcon(img);
			}
		}

		save.addActionListener(register);
		
		pic.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				addImage();
			}
		});
	}
	
	private class Register implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{
			if (Validation.validateData(fields)) {
				String values[] = new String[6];
				for (int i = 0; i < values.length; i++) {
					values[i] = fields[i].getText();
				}
				if (empid == 0) {
					if (info.insertEmployee(values)) {
						PenDiags.showMsg(PenDiags.SUCCESS);
						info.saveImage(tc_image, Integer.parseInt(values[0]));
						Validation.clearFields(fields);
						fields[0].setText(String.valueOf(info.maxEmployeeId()));
						if(parent != null){
							parent.renderData();
						}
					}
				} else {
					if (info.editEmployee(values)) {
						PenDiags.showMsg(PenDiags.SUCCESS);
						info.saveImage(tc_image, empid);
						if(parent != null){
							parent.renderData();
						}
					}
				}
			}
		}
	}
	
	private void addImage()
	{
		FileDialog diag = new FileDialog(frame);
        diag.setResizable(true);
        diag.setVisible(true);
        
		String fn = diag.getFile();
		if(fn == null) return;
		fn = fn.toLowerCase();
		String [] exts = {".jpg", ".jpeg", ".png", ".gif"};
		boolean yes = false;
		for(int i=0; i<exts.length; i++){
			if(fn.endsWith(exts[i])){
				yes = true;
				break;
			}
		}
		if(!yes){
			PenDiags.showMsg("فایل انتخابی یک فایل تصویری نیست!");
			return;
		}
		
		String path = diag.getDirectory()+fn;
		ImageIcon image = new ImageIcon(path);
		if(image.getIconWidth() > 160 || image.getIconHeight() > 210){
			PenDiags.showMsg("اندازه تصویر بزرگتر از اندازه معیاری است!");
			return;
		}
		pic.setIcon(image);
		
		try 
		{
			FileInputStream fis = new FileInputStream(new File(path));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			for(int readNum; (readNum = fis.read(buf)) != -1;){
				bos.write(buf, 0, readNum);
			}
			tc_image = bos.toByteArray();
			fis.close();
		} 
		catch (Exception e1) {}
	}
}
