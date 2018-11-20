package com.shuishou.sysmgr.ui.member;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.Member;
import com.shuishou.sysmgr.beans.MemberBalance;
import com.shuishou.sysmgr.beans.MemberScore;
import com.shuishou.sysmgr.beans.MemberUpgrade;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.CommonDialog;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.NumberInputDialog;

public class MemberUpgradeMgmtPanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger logger = Logger.getLogger(MemberUpgradeMgmtPanel.class.getName());

	private MainFrame mainFrame;
	private JTable table = new JTable();
	private TableModel model = new TableModel();
	private JButton btnAdd = new JButton("Add");
	private JButton btnUpdate = new JButton("Update");
	private JButton btnDelete = new JButton("Delete");
	private JButton btnChange = new JButton("Available/Unavailable");
	
	private ArrayList<MemberUpgrade> memberUpgrades = new ArrayList<>();
	
	public MemberUpgradeMgmtPanel(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		initUI();
		doQuery();
	}
	
	private void initUI(){
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.getColumnModel().getColumn(4).setPreferredWidth(100);
		table.getColumnModel().getColumn(5).setPreferredWidth(150);
		table.getColumnModel().getColumn(6).setPreferredWidth(100);
		table.setAutoCreateRowSorter(true);
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		
		JPanel pButtons = new JPanel();
		pButtons.add(btnAdd);
		pButtons.add(btnUpdate);
		pButtons.add(btnDelete);
		pButtons.add(btnChange);
		
		btnAdd.addActionListener(this);
		btnUpdate.addActionListener(this);
		btnDelete.addActionListener(this);
		btnChange.addActionListener(this);
		
		setLayout(new BorderLayout());
		add(jspTable, BorderLayout.CENTER);
		add(pButtons, BorderLayout.SOUTH);
	}
	
	private void doQuery(){
		memberUpgrades.clear();
		((TableModel)table.getModel()).setRowCount(0);
		String url = "member/querymemberupgrade";
		Map<String, String> params = new HashMap<>();
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for query member upgrade. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for query member upgrade. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMD).create();
		HttpResult<ArrayList<MemberUpgrade>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<MemberUpgrade>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query member upgrade. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		memberUpgrades = result.data;
		table.updateUI();
	}
	
	private void doAdd(){
		MemberUpgradePanel p = new MemberUpgradePanel(this);
		CommonDialog dlg = new CommonDialog(mainFrame, p, "Add Member Upgrade Rule", 300, 300);
		dlg.setVisible(true);
	}

	private void doUpdate(){
		if (table.getSelectedRow() < 0)
			return;
		MemberUpgradePanel p = new MemberUpgradePanel(this);
		int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
		p.setObjectValue(model.getObjectAt(modelRow));
		CommonDialog dlg = new CommonDialog(mainFrame, p, "Update Member Upgrade Rule", 300, 300);
		dlg.setVisible(true);
	}
	
	private void doDelete(){
		if (table.getSelectedRow() < 0)
			return;
		if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(mainFrame, "Do you want to delete this member upgrade rule?", "Confirm", JOptionPane.YES_NO_OPTION)){
			return;
		}
		int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
		MemberUpgrade mu = model.getObjectAt(modelRow);
		
		String url = "member/deletememberupgrade";
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("id", mu.getId()+"");
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for delete member upgrade. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for delete member upgrade. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMD).create();
		HttpResult<ArrayList<MemberUpgrade>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<MemberUpgrade>>>(){}.getType());
		if (!result.success){
			logger.error("return false while delete member upgrade. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		memberUpgrades.remove(modelRow);
		table.updateUI();
	}
	
	private void doChangeStatus(){
		if (table.getSelectedRow() < 0)
			return;
		
		int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
		MemberUpgrade mu = model.getObjectAt(modelRow);
		
		if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(mainFrame, "Do you want to change this rule to " 
				+ (mu.getStatus() == ConstantValue.MEMBERUPGRADE_STATUS_AVAILABLE ? "UNAVAILABLE" : "AVAILABLE") + "?", "Confirm", JOptionPane.YES_NO_OPTION)){
			return;
		}
		
		String url = "member/changestatusmemberupgrade";
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("id", mu.getId()+"");
		params.put("status", String.valueOf(mu.getStatus() == ConstantValue.MEMBERUPGRADE_STATUS_AVAILABLE ? ConstantValue.MEMBERUPGRADE_STATUS_UNAVAILABLE : ConstantValue.MEMBERUPGRADE_STATUS_AVAILABLE));
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for change member upgrade status. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for change member upgrade status. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMD).create();
		HttpResult<MemberUpgrade> result = gson.fromJson(response, new TypeToken<HttpResult<MemberUpgrade>>(){}.getType());
		if (!result.success){
			logger.error("return false while change member upgrade status. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		memberUpgrades.set(modelRow, result.data);
		table.updateUI();
	}
	
	public void insertRow(MemberUpgrade m){
		memberUpgrades.add(0, m);
		model.fireTableDataChanged();
	}
	
	public void updateRow(MemberUpgrade m){
		for (int i = 0; i < memberUpgrades.size(); i++) {
			if (m.getId() == memberUpgrades.get(i).getId()){
				memberUpgrades.set(i, m);
				break;
			}
		}
		model.fireTableDataChanged();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAdd){
			doAdd();
		} else if (e.getSource() == btnUpdate){
			doUpdate();
		} else if (e.getSource() == btnDelete){
			doDelete();
		} else if (e.getSource() == btnChange){
			doChangeStatus();
		}
	}
	
	class TableModel extends DefaultTableModel{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String[] header = new String[]{"Compare Field", "Start Relation", "Start Value", "End Relation", "End Value","Upgrade Field", "Upgrade Value", "Status"};
		
		public TableModel(){

		}
		@Override
		public int getRowCount() {
			if (memberUpgrades == null) return 0;
			return memberUpgrades.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			MemberUpgrade m = memberUpgrades.get(rowIndex);
			switch(columnIndex){
			case 0:
				return m.getCompareField();
			case 1: 
				if (m.getSmallRelation() == ConstantValue.MEMBERUPGRADE_RELATION_EQUAL)
					return "=";
				else if (m.getSmallRelation() == ConstantValue.MEMBERUPGRADE_RELATION_GREATER)
					return ">";
				else if (m.getSmallRelation() == ConstantValue.MEMBERUPGRADE_RELATION_GREATEREQUAL)
					return ">=";
			case 2:
				return m.getSmallValue();
			case 3:
				if (m.getBigRelation() == ConstantValue.MEMBERUPGRADE_RELATION_EQUAL)
					return "=";
				else if (m.getBigRelation() == ConstantValue.MEMBERUPGRADE_RELATION_LESS)
					return "<";
				else if (m.getBigRelation() == ConstantValue.MEMBERUPGRADE_RELATION_LESSEQUAL)
					return "<=";
			case 4:
				return m.getBigValue();
			case 5: 
				return m.getExecuteField();
			case 6:
				return m.getExecuteValue();
			case 7:
				if (m.getStatus() == ConstantValue.MEMBERUPGRADE_STATUS_AVAILABLE)
					return "AVAILABLE";
				else 
					return "UNAVAILABLE";
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public MemberUpgrade getObjectAt(int row){
			return memberUpgrades.get(row);
		}
		
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}
