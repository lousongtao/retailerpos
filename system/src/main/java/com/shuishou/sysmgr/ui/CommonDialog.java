package com.shuishou.sysmgr.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class CommonDialog extends JDialog implements ActionListener{
	
	private Window mainFrame;
	
	private JBlockedButton btnSave = new JBlockedButton("save");
	private JButton btnClose = new JButton("Close");
	private CommonDialogOperatorIFC operator;
	private JPanel content;
	private int width;
	private int heigh;
	public CommonDialog(Window mainFrame, JPanel content, String title, int width, int heigh){
		this.mainFrame = mainFrame;
		this.content = content;
		this.operator = (CommonDialogOperatorIFC)content;
		this.width = width;
		this.heigh = heigh;
		setTitle(title);
		setModal(true);
		initUI();
	}
	
	private void initUI(){
		
		btnSave.addActionListener(this);
		btnClose.addActionListener(this);
		
		JPanel pContent = new JPanel(new BorderLayout());
		pContent.add(content, BorderLayout.CENTER);
		JPanel pButton = new JPanel();
		pButton.add(btnSave);
		pButton.add(btnClose);
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(pButton, BorderLayout.SOUTH);
		c.add(pContent, BorderLayout.CENTER);
		setSize(width, heigh);
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSave){
			boolean result = operator.doSave();
			if (result){
				this.setVisible(false);
			}
		} else if (e.getSource() == btnClose){
			this.setVisible(false);
		}
	}

}

