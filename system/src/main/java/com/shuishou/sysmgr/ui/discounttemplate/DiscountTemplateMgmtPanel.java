package com.shuishou.sysmgr.ui.discounttemplate;

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
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.DiscountTemplate;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;

public class DiscountTemplateMgmtPanel extends JPanel implements ActionListener{
	private final Logger logger = Logger.getLogger(DiscountTemplateMgmtPanel.class.getName());
	private MainFrame mainFrame;
	private JTable tableDiscountTemplate = new JTable();
	private DiscountTemplateTableModel modelDiscountTemplate;
	private JButton btnAdd = new JButton(Messages.getString("DiscountTemplateMgmtPanel.Add"));
	private JButton btnDelete = new JButton(Messages.getString("DiscountTemplateMgmtPanel.Delete"));
	private ArrayList<DiscountTemplate> discountTemplateList;
	public DiscountTemplateMgmtPanel(MainFrame mainFrame, ArrayList<DiscountTemplate> discountTemplateList){
		this.mainFrame = mainFrame;
		this.discountTemplateList = discountTemplateList;
		initUI();
	}
	
	private void initUI(){
		modelDiscountTemplate = new DiscountTemplateTableModel();
		tableDiscountTemplate.setModel(modelDiscountTemplate);
		tableDiscountTemplate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableDiscountTemplate.getColumnModel().getColumn(0).setPreferredWidth(20);
		tableDiscountTemplate.getColumnModel().getColumn(1).setPreferredWidth(20);
		tableDiscountTemplate.getColumnModel().getColumn(2).setPreferredWidth(50);
		tableDiscountTemplate.getColumnModel().getColumn(3).setPreferredWidth(50);
		JScrollPane jspTable = new JScrollPane(tableDiscountTemplate, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JPanel pButtons = new JPanel();
		pButtons.add(btnAdd);
		pButtons.add(btnDelete);
		
		btnAdd.addActionListener(this);
		btnDelete.addActionListener(this);
		this.setLayout(new BorderLayout());
		add(jspTable, BorderLayout.CENTER);
		add(pButtons, BorderLayout.SOUTH);
	}
	
	public void refreshData(){
		discountTemplateList = mainFrame.loadDiscountTemplateList();
		tableDiscountTemplate.updateUI();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAdd){
			DiscountTemplateDialog dlg = new DiscountTemplateDialog(mainFrame, this, "Add Discount Template");
			dlg.setVisible(true);
		} else if (e.getSource() == btnDelete){
			if (tableDiscountTemplate.getSelectedRow() < 0)
				return;
			if (JOptionPane.showConfirmDialog(this, 
					"Do you want to delete discount template : " + modelDiscountTemplate.getObjectAt(tableDiscountTemplate.getSelectedRow()).getName() + " ?",
					"Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
				return;
			}
			Map<String, String> params = new HashMap<>();
			params.put("userId", MainFrame.getLoginUser().getId() + "");
			params.put("id", modelDiscountTemplate.getObjectAt(tableDiscountTemplate.getSelectedRow()).getId()+"");
			String url = "common/deletediscounttemplate";
			String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
			if (response == null){
				logger.error("get null from server for remove Discount Template. URL = " + url + ", param = "+ params);
				JOptionPane.showMessageDialog(this, "get null from server for remove Discount Template. URL = " + url);
				return;
			}
			Gson gson = new Gson();
			HttpResult<String> result = gson.fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
			if (!result.success){
				logger.error("return false while remove Discount Template. URL = " + url + ", response = "+response);
				JOptionPane.showMessageDialog(this, result.result);
				return;
			}
			refreshData();
		} 
	}
	
	class DiscountTemplateTableModel extends AbstractTableModel{

		private String[] header = new String[]{"ID","Name","Type", "Value"};
		
		public DiscountTemplateTableModel(){

		}
		@Override
		public int getRowCount() {
			if (discountTemplateList == null) return 0;
			return discountTemplateList.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			DiscountTemplate discountTemplate = discountTemplateList.get(rowIndex);
			switch(columnIndex){
			case 0:
				return discountTemplate.getId();
			case 1: 
				return discountTemplate.getName();
			case 2:
				if (discountTemplate.getType() == ConstantValue.DISCOUNTTYPE_RATE)
					return "by rate";
				else if (discountTemplate.getType() == ConstantValue.DISCOUNTTYPE_QUANTITY)
					return "by value";
				break;
			case 3:
				return discountTemplate.getValue();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public DiscountTemplate getObjectAt(int row){
			return discountTemplateList.get(row);
		}
	}

	
}
