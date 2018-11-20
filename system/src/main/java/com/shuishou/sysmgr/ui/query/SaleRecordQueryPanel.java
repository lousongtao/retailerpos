package com.shuishou.sysmgr.ui.query;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.Indent;
import com.shuishou.sysmgr.beans.PayWay;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.JDatePicker;

public class SaleRecordQueryPanel extends JPanel implements ActionListener{
	private final Logger logger = Logger.getLogger(ShiftworkQueryPanel.class.getName());
	private MainFrame mainFrame;
	private ArrayList<PayWay> paywayList;
	private JDatePicker dpStartDate = new JDatePicker();
	private JDatePicker dpEndDate = new JDatePicker();
	private JButton btnQuery = new JButton("Query");
	private JButton btnToday = new JButton(Messages.getString("StatisticsPanel.Today"));
	private JButton btnYesterday = new JButton(Messages.getString("StatisticsPanel.Yesterday"));
	private JButton btnThisWeek = new JButton(Messages.getString("StatisticsPanel.Thisweek"));
	private JButton btnLastWeek = new JButton(Messages.getString("StatisticsPanel.Lastweek"));
	private JButton btnThisMonth = new JButton(Messages.getString("StatisticsPanel.Thismonth"));
	private JButton btnLastMonth = new JButton(Messages.getString("StatisticsPanel.Lastmonth"));
	private JTable table = new JTable();
	private SaleRecordModel model;
	
	private Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
	public SaleRecordQueryPanel(MainFrame mainFrame, ArrayList<PayWay> paywayList){
		this.mainFrame = mainFrame;
		this.paywayList = paywayList;
		initUI();
	}
	
	private void initUI(){
		JLabel lbStartDate = new JLabel("Start Date : ");
		JLabel lbEndDate = new JLabel("End Date : ");
		dpStartDate.setPreferredSize(new Dimension(120, 25));
		dpEndDate.setPreferredSize(new Dimension(120, 25));
		dpStartDate.setShowYearButtons(true);
		dpEndDate.setShowYearButtons(true);
		
		String[] tableHeader = new String[2 + paywayList.size()];
		tableHeader[0] = "Operator";
		tableHeader[1] = "Total";
		for (int i = 0; i < paywayList.size(); i++) {
			tableHeader[i + 2] = paywayList.get(i).getName();
		}
		model = new SaleRecordModel(tableHeader);
		
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		for (int i = 0; i < tableHeader.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(120);
		}
		table.setAutoCreateRowSorter(true);
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		JPanel pQueryTimeButton = new JPanel();
		pQueryTimeButton.add(btnToday,		new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTimeButton.add(btnYesterday,	new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTimeButton.add(btnThisWeek,	new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTimeButton.add(btnLastWeek,	new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTimeButton.add(btnThisMonth,	new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTimeButton.add(btnLastMonth,	new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		
		
		JPanel pCondition = new JPanel(new GridBagLayout());
		int col = 0;
		pCondition.add(lbStartDate,	new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(dpStartDate,	new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(lbEndDate,	new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(dpEndDate,	new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(btnQuery,	  new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(pQueryTimeButton,new GridBagConstraints(0, 1, 5, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		setLayout(new BorderLayout());
		add(jspTable, BorderLayout.CENTER);
		add(pCondition, BorderLayout.NORTH);
		
		btnQuery.addActionListener(this);
		btnToday.addActionListener(this);
		btnYesterday.addActionListener(this);
		btnThisWeek.addActionListener(this);
		btnLastWeek.addActionListener(this);
		btnThisMonth.addActionListener(this);
		btnLastMonth.addActionListener(this);
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					showSaleDetail();
				}
			}
		});
	}
	
	private void showSaleDetail(){
		if (table.getSelectedRow() < 0)
			return;
		int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
		SaleRecord recs = model.getObjectAt(modelRow);
		if (recs.indents != null && !recs.indents.isEmpty()){
			JDialog dlg = new JDialog(mainFrame, "Sale Detail", true);
			dlg.setSize(1000, 600);
			dlg.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
					(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
			IndentQueryPanel qp = new IndentQueryPanel(mainFrame);
			qp.showConditionPanel(false);
			qp.setIndentData(recs.indents);
			dlg.getContentPane().setLayout(new BorderLayout());
			dlg.getContentPane().add(qp, BorderLayout.CENTER);
			dlg.setVisible(true);
		}
	}
	
	private void doQuery(){
		model.setData(null);
		model.fireTableDataChanged();
		String url = "indent/queryindent";
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		params.put("limit", "10000");//set a big value for query dataset limitation
		if (dpStartDate.getModel() != null && dpStartDate.getModel().getValue() != null){
			Calendar c = (Calendar)dpStartDate.getModel().getValue();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			params.put("starttime", ConstantValue.DFYMDHMS.format(c.getTime()));
		}
		if (dpEndDate.getModel() != null && dpEndDate.getModel().getValue() != null){
			Calendar c = (Calendar)dpEndDate.getModel().getValue();
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			params.put("endtime", ConstantValue.DFYMDHMS.format(c.getTime()));
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for query indent. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for query indent. URL = " + url);
			return;
		}
		
		HttpResult<ArrayList<Indent>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Indent>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query indent. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		if (result.data != null)
			classifyData(result.data);
	}
	
	//classify data by operators
	private void classifyData(ArrayList<Indent> indents){
		HashMap<String, SaleRecord> mapSaleRec = new HashMap<>();
		for(int i = 0; i< indents.size() ; i++){
			Indent indent = indents.get(i);
			//不统计从预购单转成的订单, 只统计"未完成的预购单" 和 "已完成的预购单"
			if (indent.getIndentType() == ConstantValue.INDENT_TYPE_ORDER_FROMPREBUY)
				continue;
			SaleRecord sr = mapSaleRec.get(indent.getOperator());
			if (sr == null){
				sr = new SaleRecord(indent.getOperator());
				mapSaleRec.put(indent.getOperator(), sr);
			}
			sr.addMoneyToPayway(indent.getPaidPrice(), indent.getPayWay());
			sr.addIndent(indent);//add indent into list, if user wants to view the detail, will display data from here
		}
		ArrayList<SaleRecord> records = new ArrayList<>();
		records.addAll(mapSaleRec.values());
		//generate the sum record
		SaleRecord sumRecord = new SaleRecord("Summary");
		for(SaleRecord sr : records){
			for(PaywayMoney pwm : sr.pwms){
				sumRecord.addMoneyToPayway(pwm.money, pwm.payway);
			}
		}
		records.add(sumRecord);
		model.setData(records);
		model.fireTableDataChanged();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnQuery){
			doQuery();
		} else if (e.getSource() == btnToday){
			Calendar c = Calendar.getInstance();
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		} else if (e.getSource() == btnYesterday){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		} else if (e.getSource() == btnThisWeek){
			Calendar c = Calendar.getInstance(Locale.CHINA);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		} else if (e.getSource() == btnLastWeek){
			Calendar c = Calendar.getInstance(Locale.CHINA);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 7);
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		} else if (e.getSource() == btnThisMonth){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, 1);
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		} else if (e.getSource() == btnLastMonth){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
			c.set(Calendar.DAY_OF_MONTH, 1);
//			dpStartDate.setDateTime(c);
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		}
	}
	
	class SaleRecord{
		public String operatorName;
		public double totalMoney;//全部的payway营收求和
		public ArrayList<PaywayMoney> pwms = new ArrayList<>();
		public ArrayList<Indent> indents = new ArrayList<>();
		
		public SaleRecord(String operatorName){
			this.operatorName = operatorName;
			for (int i = 0; i < paywayList.size(); i++) {
				PaywayMoney pwm = new PaywayMoney();
				pwm.payway = paywayList.get(i).getName();
				pwms.add(pwm);
			}
		}
		
		public double getPaywayMoneyByPayway(String paywayName){
			for(PaywayMoney pwm : pwms){
				if (pwm.payway.equals(paywayName)){
					return pwm.money;
				}
			}
			return 0;
		}
		
		public void addMoneyToPayway(double money, String paywayName){
			totalMoney += money;
			for(PaywayMoney pwm : pwms){
				if (pwm.payway.equals(paywayName)){
					pwm.money += money;
				}
			}
		}
		
		public void addIndent(Indent indent){
			indents.add(indent);
		}
	}
	
	class PaywayMoney{
		public String payway;
		public double money;
	}
	
	class SaleRecordModel extends AbstractTableModel{

		private String[] header = null;
		private ArrayList<SaleRecord> data = null;
		
		public SaleRecordModel(String[] header){
			this.header = header;
		}
		@Override
		public int getRowCount() {
			if (data == null) return 0;
			return data.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int col) {
			SaleRecord sr = data.get(rowIndex);
			if (col == 0){
				return sr.operatorName;
			} else if (col == 1){
				return String.format(ConstantValue.FORMAT_DOUBLE, sr.totalMoney);
			} else {
				return String.format(ConstantValue.FORMAT_DOUBLE, sr.getPaywayMoneyByPayway(header[col]));
			}
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public SaleRecord getObjectAt(int row){
			return data.get(row);
		}
		
		public void setData(ArrayList<SaleRecord> data){
			this.data = data;
		}
	}

	

}
