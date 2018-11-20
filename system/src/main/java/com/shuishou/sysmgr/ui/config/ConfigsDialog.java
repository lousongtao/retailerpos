package com.shuishou.sysmgr.ui.config;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.NumberTextField;

public class ConfigsDialog extends JDialog implements ActionListener{
	private final Logger logger = Logger.getLogger(ConfigsDialog.class.getName());
	private JButton btnSaveOpenCashdrawerCode = new JButton("Save");
	private JTextField tfOldOpenCashdrawerCode;
	private NumberTextField tfNewOpenCashdrawerCode;
	private JCheckBox cbMemberScore = new JCheckBox("Score");
	private JCheckBox cbMemberDeposit = new JCheckBox("Deposit");
	private NumberTextField tfScorePerDollar = new NumberTextField(true);
	private JButton btnSaveMember = new JButton("Save");
	private JTextField tfBranchName = new JTextField();
	private JButton btnSaveBranchName = new JButton("Save");
	private MainFrame mainFrame;
	public ConfigsDialog(MainFrame mainFrame){
		super(mainFrame, Messages.getString("ConfigsDialog.Config"), true);
		this.mainFrame = mainFrame;
		initUI();
		initData();
	}
	
	private void initUI(){
		JLabel lbOldOpenCashdrawerCode = new JLabel("old code");
		JLabel lbNewOpenCashdrawerCode = new JLabel("new code");
		tfOldOpenCashdrawerCode = new JTextField();
		tfNewOpenCashdrawerCode = new NumberTextField(false);
		JPanel pOpenCashdrawerCode = new JPanel(new GridBagLayout());
		pOpenCashdrawerCode.add(lbOldOpenCashdrawerCode, new GridBagConstraints(0, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0, 0));
		pOpenCashdrawerCode.add(tfOldOpenCashdrawerCode, new GridBagConstraints(1, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,10,0,0), 0, 0));
		pOpenCashdrawerCode.add(lbNewOpenCashdrawerCode, new GridBagConstraints(2, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0, 0));
		pOpenCashdrawerCode.add(tfNewOpenCashdrawerCode, new GridBagConstraints(3, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,10,0,0), 0, 0));
		pOpenCashdrawerCode.add(btnSaveOpenCashdrawerCode, new GridBagConstraints(4, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0, 0));
		pOpenCashdrawerCode.setBorder(BorderFactory.createTitledBorder("Open Cashdrawer Code"));
		
		JLabel lbScoreInfo = new JLabel("How many score by consuming 1$");
		tfScorePerDollar.setPreferredSize(new Dimension(60, 25));
		tfScorePerDollar.setText("1");
		JPanel pMember = new JPanel(new GridBagLayout());
		pMember.setBorder(BorderFactory.createTitledBorder("Member Management"));
		pMember.add(cbMemberScore, 		new GridBagConstraints(0, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0, 0));
		pMember.add(cbMemberDeposit,	new GridBagConstraints(0, 1, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0, 0));
		pMember.add(lbScoreInfo,		new GridBagConstraints(1, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0, 0));
		pMember.add(tfScorePerDollar, 	new GridBagConstraints(2, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0, 0));
		pMember.add(btnSaveMember, 		new GridBagConstraints(2, 1, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0, 0));
		
		JPanel pBranchName = new JPanel();
		tfBranchName.setPreferredSize(new Dimension(180, 25));
		JLabel lbBranchName = new JLabel("Branch Name");
		pBranchName.setBorder(BorderFactory.createTitledBorder("Branch Name"));
		pBranchName.add(lbBranchName);
		pBranchName.add(tfBranchName);
		pBranchName.add(btnSaveBranchName);
		
		
		JPanel pBasic = new JPanel(new GridBagLayout());
		pBasic.add(pOpenCashdrawerCode, new GridBagConstraints(0, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));;
		pBasic.add(pMember,	 			new GridBagConstraints(0, 1, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));;
		pBasic.add(pBranchName,			new GridBagConstraints(0, 2, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));;
		
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Basic Setting", pBasic);
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(tabPane,  BorderLayout.CENTER);
		
		btnSaveOpenCashdrawerCode.addActionListener(this);
		btnSaveMember.addActionListener(this);
		btnSaveBranchName.addActionListener(this);
		
		setSize(500, 400);
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}

	private void initData(){
		if (mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_MEMBERMGR_BYSCORE) != null)
			cbMemberScore.setSelected(Boolean.valueOf(mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_MEMBERMGR_BYSCORE)));
		if (mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_MEMBERMGR_BYDEPOSIT) != null)
			cbMemberDeposit.setSelected(Boolean.valueOf(mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_MEMBERMGR_BYDEPOSIT)));
		if (mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_MEMBERMGR_SCOREPERDOLLAR) != null)
			tfScorePerDollar.setText(mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_MEMBERMGR_SCOREPERDOLLAR));
		if (mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_BRANCHNAME) !=null)
			tfBranchName.setText(mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_BRANCHNAME));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSaveOpenCashdrawerCode){
			doSaveOpenCashdrawerCode();
		} else if (e.getSource() == btnSaveMember){
			doSaveMemberMgmt();
		} else if (e.getSource() == btnSaveBranchName){
			doSaveBranchName();
		}
	}
	
	private void doSaveMemberMgmt(){
		if (cbMemberScore.isSelected() && (tfScorePerDollar.getText() == null || tfScorePerDollar.getText().length() == 0)){
			JOptionPane.showMessageDialog(this, "Please input the scores for member consumption.");
			return;
		}
		if (!cbMemberScore.isSelected() && !cbMemberDeposit.isSelected()){
			JOptionPane.showMessageDialog(this, "Please choose at least one option for member.");
			return;
		}
		String url = "common/savemembermanagementway";
		HashMap<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("byScore", String.valueOf(cbMemberScore.isSelected()));
		params.put("byDeposit", String.valueOf(cbMemberDeposit.isSelected()));
		params.put("scorePerDollar", tfScorePerDollar.getText());
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for save member management way. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for save member management way. URL = " + url);
			return;
		}
		
		HttpResult<String> result = new Gson().fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error("return false while save member management way. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		mainFrame.loadConfigsMap();
		this.setVisible(false);
	}
	
	private void doSaveOpenCashdrawerCode(){
		if (tfNewOpenCashdrawerCode.getText() == null || tfNewOpenCashdrawerCode.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input new code");
			return;
		}
		String url = "common/saveopencashdrawercode";
		HashMap<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("code", tfNewOpenCashdrawerCode.getText());
		params.put("oldCode", tfOldOpenCashdrawerCode.getText());
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for save open cashdrawer code. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for save open cashdrawer code. URL = " + url);
			return;
		}
		
		HttpResult<String> result = new Gson().fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error("return false while save open cashdrawer code. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		mainFrame.getConfigsMap().put(ConstantValue.CONFIGS_OPENCASHDRAWERCODE, tfNewOpenCashdrawerCode.getText());
		this.setVisible(false);
	}
	
	private void doSaveBranchName(){
		if (tfBranchName.getText() == null || tfBranchName.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input branch name");
			return;
		}
		String url = "common/savebranchname";
		HashMap<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("branchName", tfBranchName.getText());
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for save branch name. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for save branch name. URL = " + url);
			return;
		}
		
		HttpResult<String> result = new Gson().fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error("return false while save branch name. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		mainFrame.getConfigsMap().put(ConstantValue.CONFIGS_BRANCHNAME, tfBranchName.getText());
		this.setVisible(false);
	}
}
