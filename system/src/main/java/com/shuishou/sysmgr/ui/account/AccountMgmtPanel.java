package com.shuishou.sysmgr.ui.account;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.Permission;
import com.shuishou.sysmgr.beans.UserData;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;

public class AccountMgmtPanel extends JPanel implements ActionListener{
	private final Logger logger = Logger.getLogger(AccountMgmtPanel.class.getName());
	private MainFrame mainFrame;
	private JTable tableAccount = new JTable();
	private AccountTableModel modelAccount;
	private JButton btnAdd = new JButton(Messages.getString("AccountMgmtPanel.Add"));
	private JButton btnModify = new JButton(Messages.getString("AccountMgmtPanel.Modify"));
	private JButton btnDelete = new JButton(Messages.getString("AccountMgmtPanel.Delete"));
	private JButton btnView = new JButton(Messages.getString("AccountMgmtPanel.View"));
	private JButton btnChangePassword = new JButton(Messages.getString("AccountMgmtPanel.ChangePassword"));
	private ArrayList<Permission> permissionList;
	private ArrayList<UserData> userList;
	public AccountMgmtPanel(MainFrame mainFrame, ArrayList<UserData> userList,ArrayList<Permission> permissionList){
		this.mainFrame = mainFrame;
		this.userList = userList;
		this.permissionList = permissionList;
		initUI();
	}
	
	private void initUI(){
		modelAccount = new AccountTableModel();
		tableAccount.setModel(modelAccount);
		tableAccount.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableAccount.getColumnModel().getColumn(0).setPreferredWidth(50);
		tableAccount.getColumnModel().getColumn(1).setPreferredWidth(50);
		tableAccount.getColumnModel().getColumn(2).setPreferredWidth(1000);
		tableAccount.getColumnModel().getColumn(0).setMinWidth(50);
		tableAccount.getColumnModel().getColumn(1).setMinWidth(50);
		JScrollPane jspTable = new JScrollPane(tableAccount, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JPanel pButtons = new JPanel();
		pButtons.add(btnAdd);
		pButtons.add(btnModify);
		pButtons.add(btnDelete);
		pButtons.add(btnView);
		pButtons.add(btnChangePassword);
		
		btnAdd.addActionListener(this);
		btnModify.addActionListener(this);
		btnDelete.addActionListener(this);
		btnView.addActionListener(this);
		btnChangePassword.addActionListener(this);
		this.setLayout(new BorderLayout());
		add(jspTable, BorderLayout.CENTER);
		add(pButtons, BorderLayout.SOUTH);
	}
	
	public void refreshData(){
		userList = mainFrame.loadUserList();
		tableAccount.updateUI();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAdd){
			AccountDialog dlg = new AccountDialog(mainFrame, this, "Add User", null, permissionList);
			dlg.setVisible(true);
		} else if (e.getSource() == btnModify){
			if (tableAccount.getSelectedRow() < 0)
				return;
			UserData user = modelAccount.getObjectAt(tableAccount.getSelectedRow());
			AccountDialog dlg = new AccountDialog(mainFrame, this, "Modify User", user, permissionList);
			dlg.hidePassword();
			dlg.setVisible(true);
		} else if (e.getSource() == btnDelete){
			if (tableAccount.getSelectedRow() < 0)
				return;
			if (JOptionPane.showConfirmDialog(this, 
					"Do you want to delete account : " + modelAccount.getObjectAt(tableAccount.getSelectedRow()).getName() + " ?",
					"Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
				return;
			}
			Map<String, String> params = new HashMap<>();
			params.put("userId", MainFrame.getLoginUser().getId() + "");
			params.put("id", modelAccount.getObjectAt(tableAccount.getSelectedRow()).getId()+"");
			String url = "account/remove";
			String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
			if (response == null){
				logger.error("get null from server for remove user. URL = " + url + ", param = "+ params);
				JOptionPane.showMessageDialog(this, "get null from server for remove user. URL = " + url);
				return;
			}
			Gson gson = new Gson();
			HttpResult<String> result = gson.fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
			if (!result.success){
				logger.error("return false while remove user. URL = " + url + ", response = "+response);
				JOptionPane.showMessageDialog(this, result.result);
				return;
			}
			refreshData();
		} else if (e.getSource() == btnView){
			if (tableAccount.getSelectedRow() < 0)
				return;
			UserData user = modelAccount.getObjectAt(tableAccount.getSelectedRow());
			AccountDialog dlg = new AccountDialog(mainFrame, this, "View User", user, permissionList);
			dlg.setViewStatus();
			dlg.setVisible(true);
		} else if (e.getSource() == btnChangePassword){
			if (tableAccount.getSelectedRow() < 0)
				return;
			UserData user = modelAccount.getObjectAt(tableAccount.getSelectedRow());
			ChangePasswordDialog dlg = new ChangePasswordDialog(mainFrame, this, "Change Password", user);
			dlg.setVisible(true);
		}
	}
	
	class AccountTableModel extends AbstractTableModel{

		private String[] header = new String[]{
				Messages.getString("AccountMgmtPanel.Header.ID"),
				Messages.getString("AccountMgmtPanel.Header.Name"),
				Messages.getString("AccountMgmtPanel.Header.Permissions")
		};
		
		public AccountTableModel(){

		}
		@Override
		public int getRowCount() {
			if (userList == null) return 0;
			return userList.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			UserData user = userList.get(rowIndex);
			switch(columnIndex){
			case 0:
				return user.getId();
			case 1: 
				return user.getName();
			case 2:
				if (user.getPermissions() == null)
					return "";
				String ps = "";
				for (int i = 0; i < user.getPermissions().size(); i++) {
					ps += user.getPermissions().get(i).getPermission().getName() + "/";
				}
				return ps;
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public UserData getObjectAt(int row){
			return userList.get(row);
		}
	}

	
}
