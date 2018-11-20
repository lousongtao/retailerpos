package com.shuishou.sysmgr.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.json.JSONObject;

import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.UserData;
import com.shuishou.sysmgr.http.HttpUtil;

public class LoginDialog extends JDialog {

	private JTextField tfName = new JTextField();
	private JPasswordField tfPassword = new JPasswordField();
	private JBlockedButton btnLogin = new JBlockedButton(Messages.getString("LoginDialog.LoginButton")); //$NON-NLS-1$
	private String loginURL = "login";
	private MainFrame mainFrame;
	public LoginDialog(MainFrame mainFrame){
		super(mainFrame, Messages.getString("LoginDialog.DialogTitle"), true); //$NON-NLS-1$
		this.mainFrame = mainFrame;
		initUI();
	}
	
	private void initUI(){
		btnLogin.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doLogin();
			}});
		Container c = this.getContentPane();
		
		c.setLayout(new GridLayout(0, 2, 10, 10));
		c.add(new JLabel(Messages.getString("LoginDialog.UserName"))); //$NON-NLS-1$
		c.add(tfName);
		c.add(new JLabel(Messages.getString("LoginDialog.Password"))); //$NON-NLS-1$
		c.add(tfPassword);
		c.add(new JLabel());
		c.add(btnLogin);
		btnLogin.setPreferredSize(new Dimension(150, 40));
		this.setSize(new Dimension(300, 200));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		this.setUndecorated(true);
	}
	
	public void setValue(String userName, String password){
		if (userName != null)
			tfName.setText(userName);
		if (password != null)
			tfPassword.setText(password);
	}
	
	private void doLogin(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", tfName.getText());
		params.put("password", tfPassword.getText());
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + "login", params, "UTF-8");
		if (response == null){
			JOptionPane.showMessageDialog(mainFrame, "cannot connect with server");
			return;
		}
		JSONObject logResult = new JSONObject(response);
		if ("ok".equals(logResult.getString("result"))){
			UserData loginUser = new UserData();
			loginUser.setId(logResult.getInt("userId"));
			loginUser.setName(logResult.getString("userName"));
			mainFrame.setLoginUser(loginUser);
			LoginDialog.this.setVisible(false);
		} else if ("invalid_password".equals(logResult.getString("result"))){
			JOptionPane.showMessageDialog(LoginDialog.this, "Password Error");
		}
	}
}
