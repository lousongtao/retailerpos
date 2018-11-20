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
import com.shuishou.sysmgr.beans.Member;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.printertool.PrintJob;
import com.shuishou.sysmgr.printertool.PrintQueue;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.JDatePicker;

public class IndentQueryPanel extends JPanel implements ActionListener{
	private final Logger logger = Logger.getLogger(IndentQueryPanel.class.getName());
	private MainFrame mainFrame;
	private JTextField tfMemberCard = new JTextField();
	private JTextField tfOrderCode = new JTextField();
	private JDatePicker dpStartDate = new JDatePicker();
	private JDatePicker dpEndDate = new JDatePicker();
	private JButton btnQuery = new JButton("Query");
	private JButton btnPrintIndent = new JButton("Print Order");
	private JComboBox cbIndentType = new JComboBox();
	private JTable tableIndent = new JTable();
	private IndentModel modelIndent = new IndentModel();
	private JTable tableIndentDetail = new JTable();
	private IndentDetailModel modelIndentDetail = new IndentDetailModel();
	private JPanel conditionPanel;
	
	private ArrayList<Indent> listIndent = new ArrayList<>();
	private HashMap<String, Member> mapMember = new HashMap<>();
	
	public IndentQueryPanel(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		initUI();
		initData();
	}
	
	private void initData(){
		ArrayList<Member> members = mainFrame.getListMember();
		if (members != null){
			for (Member m: members) {
				mapMember.put(m.getMemberCard(), m);
			}
		}
	}
	
	private void initUI(){
		JLabel lbMemberCard = new JLabel("Member Card : ");
		JLabel lbOrderCode = new JLabel("Order Code : ");
		JLabel lbStartDate = new JLabel("Start Date : ");
		JLabel lbEndDate = new JLabel("End Date : ");
		JLabel lbIndentType = new JLabel("Type : ");
		cbIndentType.addItem("ORDER");
		cbIndentType.addItem("PREORDER");
		cbIndentType.addItem("REFUND");
		cbIndentType.addItem("");
		tfMemberCard.setPreferredSize(new Dimension(120, 25));
		tfOrderCode.setPreferredSize(new Dimension(120, 25));
		dpStartDate.setPreferredSize(new Dimension(120, 25));
		dpEndDate.setPreferredSize(new Dimension(120, 25));
		dpStartDate.setShowYearButtons(true);
		dpEndDate.setShowYearButtons(true);
		
		tableIndent.setModel(modelIndent);
		tableIndent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableIndent.getColumnModel().getColumn(0).setPreferredWidth(120);
		tableIndent.getColumnModel().getColumn(1).setPreferredWidth(80);
		tableIndent.getColumnModel().getColumn(2).setPreferredWidth(100);
		tableIndent.getColumnModel().getColumn(3).setPreferredWidth(80);
		tableIndent.getColumnModel().getColumn(4).setPreferredWidth(80);
		tableIndent.getColumnModel().getColumn(5).setPreferredWidth(120);
		tableIndent.getColumnModel().getColumn(6).setPreferredWidth(120);
		tableIndent.getColumnModel().getColumn(7).setPreferredWidth(120);
		tableIndent.getColumnModel().getColumn(8).setPreferredWidth(80);
		tableIndent.setAutoCreateRowSorter(true);
		JScrollPane jspTableIndent = new JScrollPane(tableIndent, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableIndent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tableIndentDetail.setModel(modelIndentDetail);
		tableIndentDetail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableIndentDetail.getColumnModel().getColumn(0).setPreferredWidth(250);
		tableIndentDetail.getColumnModel().getColumn(1).setPreferredWidth(80);
		tableIndentDetail.getColumnModel().getColumn(2).setPreferredWidth(80);
		JScrollPane jspTableIndentDetail = new JScrollPane(tableIndentDetail, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableIndentDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		conditionPanel = new JPanel(new GridBagLayout());
		int col = 0;
		conditionPanel.add(lbMemberCard,new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(tfMemberCard,new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(lbOrderCode, new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(tfOrderCode, new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(lbStartDate,	new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(dpStartDate,	new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(lbEndDate,	new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(dpEndDate,	new GridBagConstraints(col++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(new JLabel(),new GridBagConstraints(col++, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		col = 0;
		conditionPanel.add(lbIndentType, new GridBagConstraints(col++, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(cbIndentType, new GridBagConstraints(col++, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(btnQuery,	  new GridBagConstraints(col++, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		conditionPanel.add(btnPrintIndent,new GridBagConstraints(col++, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		btnQuery.addActionListener(this);
		btnPrintIndent.addActionListener(this);
		
		JPanel pTable = new JPanel(new GridBagLayout());
		pTable.add(jspTableIndent,		new GridBagConstraints(0, 0, 1, 1, 3, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
		pTable.add(jspTableIndentDetail,new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
		
		setLayout(new BorderLayout());
		add(pTable, BorderLayout.CENTER);
		add(conditionPanel, BorderLayout.NORTH);
		
		tableIndent.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (tableIndent.getSelectedRow() < 0)
					return;
				int modelRow = tableIndent.convertRowIndexToModel(tableIndent.getSelectedRow());
				modelIndentDetail.setData((ArrayList)modelIndent.getObjectAt(modelRow).getItems());
				tableIndentDetail.updateUI();
			}
			
		});
	}
	
	public void showConditionPanel(boolean b){
		conditionPanel.setVisible(b);
	}
	
	public void setIndentData(ArrayList<Indent> indents){
		listIndent = indents;
		tableIndent.updateUI();
	}
	
	private void doQuery(){
		String url = "indent/queryindent";
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		if (tfMemberCard.getText() != null && tfMemberCard.getText().length() > 0)
			params.put("member",tfMemberCard.getText());
		if (tfOrderCode.getText() != null && tfOrderCode.getText().length() > 0)
			params.put("indentCode", tfOrderCode.getText());
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
		if (cbIndentType.getSelectedIndex() == -1){
			params.put("type", "");
		} else {
			params.put("type", cbIndentType.getSelectedItem().toString());
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for query indent. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for query indent. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<Indent>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Indent>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query indent. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		listIndent = result.data;
		tableIndent.updateUI();
	}
	
	private void doPrint(Indent indent){
		Map<String,String> keyMap = new HashMap<String, String>();
		if (indent.getMemberCard() != null && indent.getMemberCard().length() > 0){
			//reload member data from server
			Member member = HttpUtil.loadMember(mainFrame, mainFrame.getLoginUser(), indent.getMemberCard());
			keyMap.put("member", member.getMemberCard() + ", "+ member.getName() + String.format(ConstantValue.FORMAT_DOUBLE, member.getScore()) 
			+ ", " + (member.getDiscountRate() * 100) + "%");
		}else {
			keyMap.put("member", "");
		}
		keyMap.put("cashier", indent.getOperator());
		keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getCreateTime()));
		keyMap.put("totalPrice", String.format(ConstantValue.FORMAT_DOUBLE, indent.getPaidPrice()));
		keyMap.put("gst", String.format(ConstantValue.FORMAT_DOUBLE, indent.getPaidPrice()/11));
		keyMap.put("payWay", indent.getPayWay());
		keyMap.put("orderNo", indent.getIndentCode());
		keyMap.put("getcash", "");
		keyMap.put("change", "");
		double originPrice = 0;
		List<Map<String, String>> goods = new ArrayList<>();
		for (int i = 0; i< indent.getItems().size(); i++) {
			IndentDetail detail = indent.getItems().get(i);
//			ChoosedGoods cg = choosedGoods.get(i);
			Map<String, String> mg = new HashMap<String, String>();
			mg.put("name", detail.getGoodsName());
			mg.put("price", String.format(ConstantValue.FORMAT_DOUBLE, detail.getGoodsPrice()));
			mg.put("amount", detail.getAmount() + "");
			mg.put("subTotal", String.format(ConstantValue.FORMAT_DOUBLE, detail.getSoldPrice() * detail.getAmount()));
			originPrice += detail.getGoodsPrice() * detail.getAmount();
			goods.add(mg);
		}
		keyMap.put("originPrice", String.format(ConstantValue.FORMAT_DOUBLE, originPrice));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keyMap);
		params.put("goods", goods);
		String printfile = ConstantValue.TICKET_TEMPLATE_PURCHASE;
		if (indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_PAID ||
				indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_UNPAID){
			printfile = ConstantValue.TICKET_TEMPLATE_PREBUY;
		} else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_REFUND){
			printfile = ConstantValue.TICKET_TEMPLATE_REFUND;
		}
		PrintJob job = new PrintJob(printfile, params, mainFrame.printerName);
		PrintQueue.add(job);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnQuery){
			doQuery();
		} else if (e.getSource() == btnPrintIndent){
			if (tableIndent.getSelectedRow() < 0)
				return;
			int modelRow = tableIndent.convertRowIndexToModel(tableIndent.getSelectedRow());
			doPrint(((IndentModel)tableIndent.getModel()).getObjectAt(modelRow));
		} 
	}
	
	class IndentModel extends AbstractTableModel{

		private String[] header = new String[]{"Time", "Member Card", "Member Name", "Price", "Paid Price", "Pay Way", "Order Code", "Type", "Discount Template"};
		
		public IndentModel(){

		}
		@Override
		public int getRowCount() {
			if (listIndent == null) return 0;
			return listIndent.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Indent indent = listIndent.get(rowIndex);
			switch(columnIndex){
			case 0: 
				return ConstantValue.DFYMDHMS.format(indent.getCreateTime());
			case 1:
				return indent.getMemberCard();
			case 2: 
				if (indent.getMemberCard() == null || indent.getMemberCard().length() == 0) return "";
				Member m = mapMember.get(indent.getMemberCard());
				if (m != null)
					return m.getName();
			case 3: 
				return String.format(ConstantValue.FORMAT_DOUBLE, indent.getTotalPrice());
			case 4:
				return String.format(ConstantValue.FORMAT_DOUBLE, indent.getPaidPrice());
			case 5:
				return indent.getPayWay();
			
			case 6:
				return String.valueOf(indent.getIndentCode());
			case 7:
				if (indent.getIndentType() == ConstantValue.INDENT_TYPE_ORDER)
					return "ORDER";
				else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_PAID)
					return "PRE-ORDER-PAID";
				else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_UNPAID)
					return "PRE-ORDER-UNPAID";
				else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_REFUND)
					return "REFUND";
				else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_PREBUY_FINISHED)
					return "PRE-ORDER-FINISHED";
				else if (indent.getIndentType() == ConstantValue.INDENT_TYPE_ORDER_FROMPREBUY)
					return "ORDER-FROM-PREORDER";
			case 8:
				return indent.getDiscountTemplate() != null ? indent.getDiscountTemplate() : "";
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public Indent getObjectAt(int row){
			return listIndent.get(row);
		}
	}

	class IndentDetailModel extends AbstractTableModel{

		private String[] header = new String[]{"Name", "Amount", "Price", "Sold Price"};
		private ArrayList<IndentDetail> details = new ArrayList<>();
		public IndentDetailModel(){
		}
		@Override
		public int getRowCount() {
			if (details == null) return 0;
			return details.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			IndentDetail detail = details.get(rowIndex);
			switch(columnIndex){
			case 0:
				return detail.getGoodsName();
			case 1: 
				return detail.getAmount();
			case 2:
				return detail.getGoodsPrice();
			case 3:
				return detail.getSoldPrice();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public void setData(ArrayList<IndentDetail> details){
			this.details = details;
		}
		
		public IndentDetail getObjectAt(int row){
			return details.get(row);
		}
	}
}
