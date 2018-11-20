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


public class AccountDialog extends JDialog implements ActionListener {

	private final Logger logger = Logger.getLogger(AccountDialog.class.getName());
	
	private MainFrame mainFrame;
	private AccountMgmtPanel parent;
	private UserData userData;
	
	private JLabel lbPassword = new JLabel("Password");
	private JTextField tfName = new JTextField();
	private JTextField tfPassword = new JTextField();
	private JList<PermissionChoosed> listPermission = new JList<>();
//	private CheckBoxList listPermission = new CheckBoxList();
	private DefaultListModel<PermissionChoosed> modelPermission = new DefaultListModel<>();
	private JButton btnSave = new JButton("Save");
	private JButton btnCancel = new JButton("Cancel");
	public AccountDialog(MainFrame mainFrame, AccountMgmtPanel parent,String title, UserData userData,ArrayList<Permission> permissionList){
		super(mainFrame, title, true);
		this.mainFrame = mainFrame;
		this.parent = parent;
		this.userData = userData;
		initUI();
		initData(permissionList);
	}
	
	private void initUI(){
		JLabel lbName = new JLabel("Name");
		
		listPermission.setModel(modelPermission);
		listPermission.setCellRenderer(new PermissionListRenderer());
		listPermission.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPermission.setBorder(BorderFactory.createTitledBorder("Permission"));
		listPermission.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = listPermission.locationToIndex(e.getPoint());

				if (index != -1) {
					PermissionChoosed pc = listPermission.getModel().getElementAt(index);
					pc.isChoosed = !pc.isChoosed;
					repaint();
				}
			}
		});
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbName, 			new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(tfName, 			new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(lbPassword, 		new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(tfPassword, 		new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(listPermission, 	new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
		c.add(btnSave, 			new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(btnCancel, 		new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		btnSave.addActionListener(this);
		btnCancel.addActionListener(this);
		
		this.setSize(400,600);
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	private void initData(ArrayList<Permission> permissionList){
		for (int i = 0; i < permissionList.size(); i++) {
			PermissionChoosed pc = new PermissionChoosed();
			pc.p = permissionList.get(i);
			pc.isChoosed = false;
			modelPermission.addElement(pc);
		}
		if (userData != null){
			tfName.setText(userData.getName());
			for (int i = 0; i < userData.getPermissions().size(); i++) {
				for (int j = 0; j < modelPermission.size(); j++) {
					if (userData.getPermissions().get(i).getPermission().equals(modelPermission.getElementAt(j).p)){
						modelPermission.getElementAt(j).isChoosed = true;
						break;
					}
				}
			}
		}
	}
	
	public void hidePassword(){
		lbPassword.setVisible(false);
		tfPassword.setVisible(false);
	}
	
	public void setViewStatus(){
		lbPassword.setVisible(false);
		tfPassword.setVisible(false);
		tfName.setEditable(false);
		listPermission.setEnabled(false);
		btnSave.setVisible(false);
	}
	
	private void doSave(){
		if (tfName.getText() == null || tfName.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "must input name");
			return;
		}
		String permissionIds = generateChoosedPermission();
		if (permissionIds == null || permissionIds.length() == 0){
			JOptionPane.showMessageDialog(this, "must choose permission");
			return;
		}
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		params.put("username", tfName.getText());
		params.put("permission", permissionIds);
		String url = null;
		if (userData != null) {
			url = "account/modify";
			params.put("id", userData.getId()+"");
		} else {
			url = "account/add";
			params.put("password", tfPassword.getText());
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for add/update user. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for add/update user. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<String> result = gson.fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error("return false while add/update user. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		parent.refreshData();
		setVisible(false);
	}
	private String generateChoosedPermission(){
		String ids = "";
		for (int i = 0; i < modelPermission.size(); i++) {
			if (modelPermission.getElementAt(i).isChoosed){
				ids += modelPermission.getElementAt(i).p.getId()+"/";
			}
		}
		ids = ids.substring(0, ids.length() - 1);
		return ids;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel){
			setVisible(false);
		} else if (e.getSource() == btnSave){
			doSave();			
		}
	}
	
	class PermissionListRenderer extends JCheckBox implements ListCellRenderer{
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }
			PermissionChoosed pc = (PermissionChoosed)value;
			this.setText(pc.p.getName());
			this.setSelected(pc.isChoosed);
			return this;
		}
	}
	
	class PermissionChoosed{
		Permission p;
		boolean isChoosed = false;
	}

	
}
