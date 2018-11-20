package com.shuishou.sysmgr.ui.member;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.Member;
import com.shuishou.sysmgr.beans.MemberUpgrade;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.CommonDialogOperatorIFC;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.JDatePicker;
import com.shuishou.sysmgr.ui.components.NumberTextField;

public class MemberUpgradePanel extends JPanel implements CommonDialogOperatorIFC{

	private final Logger logger = Logger.getLogger(MemberUpgradePanel.class.getName());
	private MemberUpgradeMgmtPanel parent;
	private JTextField tfCompareField= new JTextField(155);
	private JComboBox<String> cbStartRelation= new JComboBox<>();
	private JTextField tfUpgradeField= new JTextField(155);
	private JComboBox<String> cbEndRelation= new JComboBox<>();
	private NumberTextField tfStartValue= new NumberTextField(true);
	private NumberTextField tfEndValue= new NumberTextField(true);
	private NumberTextField tfUpgradeValue= new NumberTextField(true);
	private MemberUpgrade memberUpgrade;
	
	public MemberUpgradePanel(MemberUpgradeMgmtPanel parent){
		this.parent = parent;
		initUI();
	}
	
	private void initUI(){
		cbStartRelation.addItem("=");
		cbStartRelation.addItem(">");
		cbStartRelation.addItem(">=");
		cbEndRelation.addItem("=");
		cbEndRelation.addItem("<");
		cbEndRelation.addItem("<=");
		JLabel lbCompareField = new JLabel("Compare Field");
		JLabel lbStartRelation = new JLabel("Start Relation");
		JLabel lbStartValue = new JLabel("Start Value");
		JLabel lbEndRelation = new JLabel("End Relation");
		JLabel lbEndValue = new JLabel("End Value");
		JLabel lbUpgradeField = new JLabel("Upgrade Field");
		JLabel lbUpgradeValue = new JLabel("Upgrade Value");
		this.setLayout(new GridBagLayout());
		add(lbCompareField, 	new GridBagConstraints(0, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfCompareField, 	new GridBagConstraints(1, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		add(lbStartRelation, 	new GridBagConstraints(0, 1, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(cbStartRelation, 	new GridBagConstraints(1, 1, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		add(lbStartValue, 		new GridBagConstraints(0, 2, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfStartValue, 		new GridBagConstraints(1, 2, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		add(lbEndRelation, 		new GridBagConstraints(0, 3, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(cbEndRelation, 		new GridBagConstraints(1, 3, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		add(lbEndValue, 		new GridBagConstraints(0, 4, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfEndValue, 		new GridBagConstraints(1, 4, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		add(lbUpgradeField, 	new GridBagConstraints(0, 5, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfUpgradeField, 	new GridBagConstraints(1, 5, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		add(lbUpgradeValue, 	new GridBagConstraints(0, 6, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfUpgradeValue, 	new GridBagConstraints(1, 6, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		tfCompareField.setMinimumSize(new Dimension(180,25));
		
	}
	
	@Override
	public boolean doSave() {
		if (!doCheckInput())
			return false;
		
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("compareField", tfCompareField.getText());
		if (cbStartRelation.getSelectedItem().equals("="))
			params.put("smallRelation", ConstantValue.MEMBERUPGRADE_RELATION_EQUAL + "");
		else if (cbStartRelation.getSelectedItem().equals(">"))
			params.put("smallRelation", ConstantValue.MEMBERUPGRADE_RELATION_GREATER + "");
		else if (cbStartRelation.getSelectedItem().equals(">="))
			params.put("smallRelation", ConstantValue.MEMBERUPGRADE_RELATION_GREATEREQUAL + "");
		
		if (cbEndRelation.getSelectedItem().equals("="))
			params.put("bigRelation", ConstantValue.MEMBERUPGRADE_RELATION_EQUAL + "");
		else if (cbEndRelation.getSelectedItem().equals("<"))
			params.put("bigRelation", ConstantValue.MEMBERUPGRADE_RELATION_LESS + "");
		else if (cbEndRelation.getSelectedItem().equals("<="))
			params.put("bigRelation", ConstantValue.MEMBERUPGRADE_RELATION_LESSEQUAL + "");
		
		params.put("executeField", tfUpgradeField.getText());
		params.put("smallValue", tfStartValue.getText());
		params.put("bigValue",tfEndValue.getText());
		params.put("executeValue",tfUpgradeValue.getText());
		
		String url = "member/addmemberupgrade";
		if (memberUpgrade != null){
			url = "member/updatememberupgrade";
			params.put("id", memberUpgrade.getId() + "");
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for add/update memberupgrade. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for add/update memberupgrade. URL = " + url);
			return false;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<MemberUpgrade> result = gson.fromJson(response, new TypeToken<HttpResult<MemberUpgrade>>(){}.getType());
		if (!result.success){
			logger.error("return false while add/update memberupgrade. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return false;
		}
		if (memberUpgrade == null){
			parent.insertRow(result.data);
		} else {
			parent.updateRow(result.data);
		}
		return true;
	}

	private boolean doCheckInput(){
		if (tfCompareField.getText() == null || tfCompareField.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Compare Field");
			return false;
		}
		if (tfStartValue.getText() == null || tfStartValue.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Start Value");
			return false;
		}
		if (tfEndValue.getText() == null || tfEndValue.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input End Value");
			return false;
		}
		if (tfUpgradeField.getText() == null || tfUpgradeField.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Upgrade Field");
			return false;
		}
		if (tfUpgradeValue.getText() == null || tfUpgradeValue.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Upgrade Value");
			return false;
		}
		return true;
	}
	
	public void setObjectValue(MemberUpgrade mu){
		this.memberUpgrade = mu;
		tfCompareField.setText(mu.getCompareField());
		tfUpgradeField.setText(mu.getExecuteField());
		tfStartValue.setText(mu.getSmallValue()+"");
		tfEndValue.setText(mu.getBigValue() + "");
		tfUpgradeValue.setText(mu.getExecuteValue() + "");
		if (mu.getSmallRelation() == ConstantValue.MEMBERUPGRADE_RELATION_EQUAL){
			cbStartRelation.setSelectedItem("=");
		} else if (mu.getSmallRelation() == ConstantValue.MEMBERUPGRADE_RELATION_GREATER){
			cbStartRelation.setSelectedItem(">");
		} else if (mu.getSmallRelation() == ConstantValue.MEMBERUPGRADE_RELATION_GREATEREQUAL){
			cbStartRelation.setSelectedItem(">=");
		} 
		if (mu.getBigRelation() == ConstantValue.MEMBERUPGRADE_RELATION_EQUAL){
			cbEndRelation.setSelectedItem("=");
		} else if (mu.getBigRelation() == ConstantValue.MEMBERUPGRADE_RELATION_LESS){
			cbEndRelation.setSelectedItem("<");
		} else if (mu.getBigRelation() == ConstantValue.MEMBERUPGRADE_RELATION_LESSEQUAL){
			cbEndRelation.setSelectedItem("<=");
		} 
	}
}
