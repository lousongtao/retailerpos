package com.shuishou.sysmgr.ui.query;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.LogData;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.JDatePicker;

public class LogQueryPanel extends JPanel implements ActionListener{
	private final Logger logger = Logger.getLogger(LogQueryPanel.class.getName());
	private MainFrame mainFrame;
	private JTextField tfMessage = new JTextField();
	private JTextField tfUser = new JTextField();
	private JComboBox cbType = new JComboBox();
	private JDatePicker dpStartDate = new JDatePicker();
	private JDatePicker dpEndDate = new JDatePicker();
	private JButton btnQuery = new JButton("Query");
	private JTable table = new JTable();
	private TableModel model = new TableModel();
	
	private ArrayList<LogData> logList = new ArrayList<>();
	
	public LogQueryPanel(MainFrame mainFrame, ArrayList<String> listLogType){
		this.mainFrame = mainFrame;
		initUI();
		initData(listLogType);
	}
	
	private void initData(ArrayList<String> listLogType){
		cbType.removeAllItems();
		cbType.addItem("");
		for (int i = 0; i < listLogType.size(); i++) {
			cbType.addItem(listLogType.get(i));
		}
	}
	
	private void initUI(){
		JLabel lbMessage = new JLabel("Message : ");
		JLabel lbType = new JLabel("Type : ");
		JLabel lbUser = new JLabel("User : ");
		JLabel lbStartDate = new JLabel("Start Date : ");
		JLabel lbEndDate = new JLabel("End Date : ");
		tfMessage.setPreferredSize(new Dimension(120, 25));
		tfUser.setPreferredSize(new Dimension(120, 25));
		cbType.setPreferredSize(new Dimension(120, 25));
		dpStartDate.setShowYearButtons(true);
		dpEndDate.setShowYearButtons(true);
		
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.getColumnModel().getColumn(4).setPreferredWidth(600);
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JPanel pCondition = new JPanel(new GridBagLayout());
		pCondition.add(lbMessage, 	new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(tfMessage, 	new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(lbType, 		new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(cbType, 		new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(lbUser, 		new GridBagConstraints(4, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(tfUser, 		new GridBagConstraints(5, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(lbStartDate, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(dpStartDate, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(lbEndDate, 	new GridBagConstraints(2, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(dpEndDate, 	new GridBagConstraints(3, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(btnQuery, 	new GridBagConstraints(4, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
//		JScrollPane jspCondition = new JScrollPane(pCondition, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		btnQuery.addActionListener(this);
		
//		Dimension d = jspCondition.getPreferredSize();
//		d.height = 100;
//		jspCondition.setMinimumSize(d);
		setLayout(new BorderLayout());
		add(jspTable, BorderLayout.CENTER);
		add(pCondition, BorderLayout.NORTH);
	}
	
	private void doQuery(){
		String url = "log/logs";
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		if (tfMessage.getText() != null && tfMessage.getText().length() > 0)
			params.put("message",tfMessage.getText());
		if (cbType.getSelectedIndex() > -1)
			params.put("type", cbType.getSelectedItem().toString());
		if (tfUser.getText() != null && tfUser.getText().length() > 0)
			params.put("username", tfUser.getText());
		if (dpStartDate.getModel() != null && dpStartDate.getModel().getValue() != null){
			Calendar c = (Calendar)dpStartDate.getModel().getValue();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			params.put("beginTime", ConstantValue.DFYMDHMS.format(c.getTime()));
		}
		if (dpEndDate.getModel() != null && dpEndDate.getModel().getValue() != null){
			Calendar c = (Calendar)dpEndDate.getModel().getValue();
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			params.put("endTime", ConstantValue.DFYMDHMS.format(c.getTime()));
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for query log. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for query log. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<LogData>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<LogData>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query log. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		logList = result.data;
		table.updateUI();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnQuery){
			doQuery();
		}
	}
	
	class TableModel extends AbstractTableModel{

		private String[] header = new String[]{"ID","User","Type", "Time", "Message"};
		
		public TableModel(){

		}
		@Override
		public int getRowCount() {
			if (logList == null) return 0;
			return logList.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			LogData log = logList.get(rowIndex);
			switch(columnIndex){
			case 0:
				return log.getId();
			case 1: 
				return log.getUserName();
			case 2:
				return log.getType();
			case 3:
				return ConstantValue.DFYMDHMS.format(log.getTime());
			case 4: 
				return log.getMessage();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public LogData getObjectAt(int row){
			return logList.get(row);
		}
	}

}
