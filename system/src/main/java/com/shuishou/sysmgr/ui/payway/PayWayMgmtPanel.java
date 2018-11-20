package com.shuishou.sysmgr.ui.payway;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
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
import com.shuishou.sysmgr.beans.DiscountTemplate;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.PayWay;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;

public class PayWayMgmtPanel extends JPanel implements ActionListener{
	private final Logger logger = Logger.getLogger(PayWayMgmtPanel.class.getName());
	private MainFrame mainFrame;
	private JTable tablePayWay = new JTable();
	private PayWayTableModel modelPayWay;
	private JButton btnAdd = new JButton(Messages.getString("Add"));
	private JButton btnModify = new JButton(Messages.getString("Modify"));
	private JButton btnDelete = new JButton(Messages.getString("Delete"));
	private ArrayList<PayWay> payWayList;
	public PayWayMgmtPanel(MainFrame mainFrame, ArrayList<PayWay> payWayList){
		this.mainFrame = mainFrame;
		this.payWayList = payWayList;
		initUI();
	}
	
	private void initUI(){
		JLabel lbInfo = new JLabel(Messages.getString("PayWayMgmtPanel.warninginfo"));
		lbInfo.setBorder(BorderFactory.createTitledBorder("Information"));
		modelPayWay = new PayWayTableModel();
		tablePayWay.setModel(modelPayWay);
		tablePayWay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablePayWay.getColumnModel().getColumn(0).setPreferredWidth(20);
		tablePayWay.getColumnModel().getColumn(1).setPreferredWidth(20);
		JScrollPane jspTable = new JScrollPane(tablePayWay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JPanel pContent = new JPanel(new BorderLayout());
		pContent.add(jspTable, BorderLayout.CENTER);
		pContent.add(lbInfo, BorderLayout.SOUTH);
		
		JPanel pButtons = new JPanel();
		pButtons.add(btnAdd);
		pButtons.add(btnModify);
		pButtons.add(btnDelete);
		
		btnAdd.addActionListener(this);
		btnModify.addActionListener(this);
		btnDelete.addActionListener(this);
		this.setLayout(new BorderLayout());
		add(pContent, BorderLayout.CENTER);
		add(pButtons, BorderLayout.SOUTH);
	}
	
	public void refreshData(){
		payWayList = mainFrame.loadPayWayList();
		tablePayWay.updateUI();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAdd){
			PayWayDialog dlg = new PayWayDialog(mainFrame, this, "Add Pay Way");
			dlg.setVisible(true);
		} else if (e.getSource() == btnDelete){
			doDelete();
		} else if (e.getSource() == btnModify){
			if (tablePayWay.getSelectedRow() < 0)
				return;
			PayWayDialog dlg = new PayWayDialog(mainFrame, this, "Modify Pay Way");
			dlg.setObject(modelPayWay.getObjectAt(tablePayWay.getSelectedRow()));
			dlg.setVisible(true);
		}
	}
	
	private void doDelete(){
		if (tablePayWay.getSelectedRow() < 0)
			return;
		if (JOptionPane.showConfirmDialog(this, 
				"Do you want to delete this payway : " + modelPayWay.getObjectAt(tablePayWay.getSelectedRow()).getName() + " ?",
				"Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			return;
		}
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		params.put("id", modelPayWay.getObjectAt(tablePayWay.getSelectedRow()).getId()+"");
		String url = "common/deletepayway";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for remove pay way. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for remove pay way. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<String> result = gson.fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error("return false while remove pay way. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		refreshData();
	}
	
	class PayWayTableModel extends AbstractTableModel{

		private String[] header = new String[]{"ID","Name", "Rate", "Sequence", "Symbol"};
		
		public PayWayTableModel(){

		}
		@Override
		public int getRowCount() {
			if (payWayList == null) return 0;
			return payWayList.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PayWay payway = payWayList.get(rowIndex);
			switch(columnIndex){
			case 0:
				return payway.getId();
			case 1: 
				return payway.getName();
			case 2 :
				return payway.getRate();
			case 3:
				return payway.getSequence();
			case 4:
				return payway.getSymbol();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public PayWay getObjectAt(int row){
			return payWayList.get(row);
		}
	}

	
}
