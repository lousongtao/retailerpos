package com.shuishou.sysmgr.ui.account;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.Category1;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.Permission;
import com.shuishou.sysmgr.beans.UserData;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;


public class ChangePasswordDialog extends JDialog implements ActionListener {

	private final Logger logger = Logger.getLogger(ChangePasswordDialog.class.getName());
	
	private MainFrame mainFrame;
	private AccountMgmtPanel parent;
	private UserData userData;
	
	
	private JTextField tfOldPassword = new JTextField();
	private JTextField tfNewPassword = new JTextField();
	private JButton btnSave = new JButton("Save");
	private JButton btnCancel = new JButton("Cancel");
	public ChangePasswordDialog(MainFrame mainFrame, AccountMgmtPanel parent,String title, UserData userData){
		super(mainFrame, title, true);
		this.mainFrame = mainFrame;
		this.parent = parent;
		this.userData = userData;
		initUI();
	}
	
	private void initUI(){
		JLabel lbOldPassword = new JLabel("Old Password");
		JLabel lbNewPassword = new JLabel("New Password");
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbOldPassword, 	new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(tfOldPassword, 	new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(lbNewPassword, 	new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(tfNewPassword, 	new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(btnSave, 			new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(btnCancel, 		new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		btnSave.addActionListener(this);
		btnCancel.addActionListener(this);
		
		this.setSize(240,140);
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	
	private void doSave(){
		if (tfOldPassword.getText() == null || tfOldPassword.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "must input old password");
			return;
		}
		if (tfNewPassword.getText() == null || tfNewPassword.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "must input new password");
			return;
		}
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		params.put("accountId", userData.getId() + "");
		params.put("oldPassword", tfOldPassword.getText());
		params.put("newPassword", tfNewPassword.getText());
		String url = "account/change_password";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for change user password. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for change user password. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<String> result = gson.fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error("return false while change user password. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		setVisible(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel){
			setVisible(false);
		} else if (e.getSource() == btnSave){
			doSave();			
		}
	}
	
}
