package com.shuishou.sysmgr.ui.query;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.Indent;
import com.shuishou.sysmgr.beans.IndentDetail;
import com.shuishou.sysmgr.beans.LogData;
import com.shuishou.sysmgr.beans.PayWay;
import com.shuishou.sysmgr.beans.ShiftWork;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.printertool.PrintJob;
import com.shuishou.sysmgr.printertool.PrintQueue;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.JDatePicker;

public class ShiftworkQueryPanel extends JPanel implements ActionListener{
	private final Logger logger = Logger.getLogger(ShiftworkQueryPanel.class.getName());
	private MainFrame mainFrame;
	private JTextField tfUser = new JTextField();
	private JDatePicker dpStartDate = new JDatePicker();
	private JDatePicker dpEndDate = new JDatePicker();
	private JButton btnQuery = new JButton("Query");
	private JButton btnPrint = new JButton("Print Shiftwork");
	private JTable table = new JTable();
	private ShiftworkModel model = new ShiftworkModel();
	
	private ArrayList<ShiftWork> listShiftwork = new ArrayList<>();
	
	public ShiftworkQueryPanel(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		initUI();
	}
	
	private void initUI(){
		JLabel lbUser = new JLabel("User Name : ");
		JLabel lbStartDate = new JLabel("Start Date : ");
		JLabel lbEndDate = new JLabel("End Date : ");
		tfUser.setPreferredSize(new Dimension(150, 25));
		dpStartDate.setShowYearButtons(true);
		dpEndDate.setShowYearButtons(true);
		
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JPanel pCondition = new JPanel(new GridBagLayout());
		pCondition.add(lbUser,		new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(tfUser,		new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(lbStartDate,	new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(dpStartDate,	new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(lbEndDate,	new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(dpEndDate,	new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(btnQuery,	new GridBagConstraints(6, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(btnPrint,	new GridBagConstraints(7, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(new JLabel(),new GridBagConstraints(8, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		btnQuery.addActionListener(this);
		btnPrint.addActionListener(this);
		
		setLayout(new BorderLayout());
		add(jspTable, BorderLayout.CENTER);
		add(pCondition, BorderLayout.NORTH);
	}
	
	private void doQuery(){
		String url = "management/getshiftwork";
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		if (tfUser.getText() != null && tfUser.getText().length() > 0)
			params.put("userName", tfUser.getText());
		if (dpStartDate.getModel() != null && dpStartDate.getModel().getValue() != null){
			Calendar c = (Calendar)dpStartDate.getModel().getValue();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			params.put("startTime", ConstantValue.DFYMDHMS.format(c.getTime()));
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
			logger.error("get null from server for query shiftwork. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for query shiftwork. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<ShiftWork>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<ShiftWork>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query shiftwork. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		listShiftwork = result.data;
		table.updateUI();
	}
	
	private void doPrint(){
		if (table.getSelectedRow() < 0)
				return;
		String url = "indent/queryindentforshiftwork";
		ShiftWork sw = model.getObjectAt(table.getSelectedRow());
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		params.put("shiftworkId", sw.getId() + "");
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for query indent by shiftwork. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for query indent by shiftwork. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<Indent>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Indent>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query indent by shiftwork. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		ArrayList<Indent> indents = result.data;
		ArrayList<PayWay> otherPayWays = mainFrame.loadPayWayList();
		
		Date endTime = sw.getEndTime() == null ? new Date() : sw.getEndTime();
		long millsecs = endTime.getTime() - sw.getStartTime().getTime();
		int hours = (int)(millsecs / (60*60*1000));
		int minutes = (int)((millsecs - hours * 60 * 60 * 1000)/(60*1000));
		int seconds = (int)((millsecs - hours * 60 * 60 * 1000 - minutes * 60 * 1000)/1000);
		String workPeriod = (hours > 0 ? hours+"h " : "") + minutes + "m " + seconds + "s";
		
		int indentAmount = 0;
		int goodsAmount = 0;
		double cashMoney = 0;
		double bankcardMoney = 0;
		double memberMoney = 0;
		double totalPrice = 0;
		double paidPrice = 0;
		HashMap<String, Double> mapOtherPay = new HashMap<>();//other pay way money
		if (otherPayWays != null && !otherPayWays.isEmpty()){
			for(PayWay pw : otherPayWays){
				mapOtherPay.put(pw.getName(), new Double(0.0));
			}
		}
		
		Map<String, Map<String, String>> mapGoodsAmount = new HashMap<String, Map<String, String>>();
		if (indents != null){
			for(Indent indent : indents){
				if (indent.getIndentType() != ConstantValue.INDENT_TYPE_ORDER)
					continue;
				indentAmount++;
				totalPrice += indent.getTotalPrice();
				paidPrice += indent.getPaidPrice();
				if (ConstantValue.INDENT_PAYWAY_CASH.equals(indent.getPayWay())){
					cashMoney += indent.getPaidPrice();
				} else if (ConstantValue.INDENT_PAYWAY_BANKCARD.equals(indent.getPayWay())){
					bankcardMoney += indent.getPaidPrice();
				} else if (ConstantValue.INDENT_PAYWAY_MEMBER.equals(indent.getPayWay())){
					memberMoney += indent.getPaidPrice();
				} else {
					//do double check for other payway, maybe there are some payway not existing in the list, which will make get(payway) == null
					if (mapOtherPay.get(indent.getPayWay()) == null){
						mapOtherPay.put(indent.getPayWay(), new Double(0.0));
					}
					mapOtherPay.put(indent.getPayWay(), mapOtherPay.get(indent.getPayWay()) + indent.getPaidPrice());
				}
				List<IndentDetail> details = indent.getItems();
				for(IndentDetail d : details){
					goodsAmount += d.getAmount();
					Map<String, String> mg = mapGoodsAmount.get(d.getGoodsName());
					if (mg == null){
						mg = new HashMap<String, String>();
						mg.put("name", d.getGoodsName());
						mg.put("price", d.getGoodsPrice()+"");
						mg.put("amount", d.getAmount()+"");
						mg.put("subTotal", String.format(ConstantValue.FORMAT_DOUBLE, d.getSoldPrice() * d.getAmount()));
						mapGoodsAmount.put(d.getGoodsName(), mg);
					} else {
						mg.put("amount", Integer.parseInt(mg.get("amount")) + d.getAmount()+"");
						mg.put("subTotal", String.format(ConstantValue.FORMAT_DOUBLE, d.getSoldPrice() * d.getAmount() + Double.parseDouble(mg.get("subTotal"))));
					}
				}
			}
		}
		Map<String,String> keys = new HashMap<String, String>();
		keys.put("Cashier", sw.getUserName());
		keys.put("startTime", ConstantValue.DFYMDHMS.format(sw.getStartTime()));
		keys.put("endTime", ConstantValue.DFYMDHMS.format(endTime));
		keys.put("workHours", workPeriod);
		keys.put("indentAmount", indentAmount+"");
		keys.put("goodsAmount", goodsAmount + "");
		keys.put("cashMoney", String.format(ConstantValue.FORMAT_DOUBLE,cashMoney));
		keys.put("bankcardMoney", String.format(ConstantValue.FORMAT_DOUBLE,bankcardMoney));
		keys.put("memberMoney", String.format(ConstantValue.FORMAT_DOUBLE,memberMoney));
		keys.put("totalPrice", String.format(ConstantValue.FORMAT_DOUBLE,totalPrice));
		keys.put("paidPrice", String.format(ConstantValue.FORMAT_DOUBLE,paidPrice));
		keys.put("gst", String.format(ConstantValue.FORMAT_DOUBLE,(double)(totalPrice/11)));
		keys.put("printTime", ConstantValue.DFYMDHMS.format(new Date()));
		for(String key : mapOtherPay.keySet()){
			keys.put(key, String.format(ConstantValue.FORMAT_DOUBLE,mapOtherPay.get(key)));
		}
		List<Map<String, String>> goods = new ArrayList<Map<String, String>>(mapGoodsAmount.values());
		Map<String, Object> paramsmap = new HashMap<String, Object>();
		paramsmap.put("keys", keys);
		paramsmap.put("goods", goods);
		PrintJob job = new PrintJob(ConstantValue.TICKET_TEMPLATE_SHIFTWORK, paramsmap, mainFrame.printerName);
		PrintQueue.add(job);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnQuery){
			doQuery();
		} else if (e.getSource() == btnPrint){
			doPrint();			
		}
	}
	
	class ShiftworkModel extends AbstractTableModel{

		private String[] header = new String[]{"User Name", "Start Time", "End Time"};
		
		@Override
		public int getRowCount() {
			if (listShiftwork == null) return 0;
			return listShiftwork.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ShiftWork sw = listShiftwork.get(rowIndex);
			switch(columnIndex){
			case 0:
				return sw.getUserName();
			case 1:
				return ConstantValue.DFYMDHMS.format(sw.getStartTime());
			case 2:
				if (sw.getEndTime() == null)
					return "";
				else return ConstantValue.DFYMDHMS.format(sw.getEndTime());
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public ShiftWork getObjectAt(int row){
			return listShiftwork.get(row);
		}
	}

	
}
