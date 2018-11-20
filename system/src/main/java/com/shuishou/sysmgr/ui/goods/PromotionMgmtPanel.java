package com.shuishou.sysmgr.ui.goods;

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
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.Category1;
import com.shuishou.sysmgr.beans.Category2;
import com.shuishou.sysmgr.beans.Goods;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.PayWay;
import com.shuishou.sysmgr.beans.Promotion;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;

public class PromotionMgmtPanel extends JPanel implements ActionListener{
	private final Logger logger = Logger.getLogger(PromotionMgmtPanel.class.getName());
	private MainFrame mainFrame;
	private JTable table = new JTable();
	private PromotionTableModel model;
	private JButton btnAdd = new JButton(Messages.getString("Add"));
	private JButton btnModify = new JButton(Messages.getString("Modify"));
	private JButton btnDelete = new JButton(Messages.getString("Delete"));
	private JButton btnAvailable = new JButton("Available / Unavailable");
	private ArrayList<Promotion> promotionList;
	
	public PromotionMgmtPanel(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		initUI();
		refreshData();
	}
	
	private void initUI(){
		model = new PromotionTableModel();
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(80);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
		table.getColumnModel().getColumn(4).setPreferredWidth(80);
		table.getColumnModel().getColumn(5).setPreferredWidth(80);
		table.getColumnModel().getColumn(6).setPreferredWidth(80);
		table.getColumnModel().getColumn(7).setPreferredWidth(200);
		table.getColumnModel().getColumn(8).setPreferredWidth(80);
		table.getColumnModel().getColumn(9).setPreferredWidth(100);
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JPanel pButtons = new JPanel();
		pButtons.add(btnAdd);
		pButtons.add(btnModify);
		pButtons.add(btnDelete);
		pButtons.add(btnAvailable);
		
		btnAdd.addActionListener(this);
		btnModify.addActionListener(this);
		btnDelete.addActionListener(this);
		btnAvailable.addActionListener(this);
		this.setLayout(new BorderLayout());
		add(jspTable, BorderLayout.CENTER);
		add(pButtons, BorderLayout.SOUTH);
	}
	
	
	public void refreshData(){
		promotionList = HttpUtil.loadPromotion(mainFrame, mainFrame.SERVER_URL + "promotion/queryallpromotion");
		table.updateUI();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAdd){
			PromotionDialog dlg = new PromotionDialog(mainFrame, this, "Add Promotion");
			dlg.setVisible(true);
		} else if (e.getSource() == btnDelete){
			doDelete();
		} else if (e.getSource() == btnModify){
			if (table.getSelectedRow() < 0)
				return;
			PromotionDialog dlg = new PromotionDialog(mainFrame, this, "Modify Promotion");
			dlg.setObject(model.getObjectAt(table.getSelectedRow()));
			dlg.setVisible(true);
		} else if (e.getSource() == btnAvailable){
			doChangeAvailable();
		}
	}
	
	private void doChangeAvailable(){
		if (table.getSelectedRow() < 0)
			return;
		Promotion p = model.getObjectAt(table.getSelectedRow());
		if (JOptionPane.showConfirmDialog(this, 
				"Do you want to change this promotion's status to " + (p.isAvailable() ? "UNAVAILABLE" : "AVAILABLE") +"?",
				"Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			return;
		}
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		params.put("id", model.getObjectAt(table.getSelectedRow()).getId()+"");
		String url = "promotion/changepromotionstatus";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for change promotion status. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for change promotion status. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<Promotion> result = gson.fromJson(response, new TypeToken<HttpResult<Promotion>>(){}.getType());
		if (!result.success){
			logger.error("return false while change promotion status. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		refreshData();
	}
	
	private void doDelete(){
		if (table.getSelectedRow() < 0)
			return;
		if (JOptionPane.showConfirmDialog(this, 
				"Do you want to delete this promotion?",
				"Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			return;
		}
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		params.put("id", model.getObjectAt(table.getSelectedRow()).getId()+"");
		String url = "promotion/deletepromotion";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for remove promotion. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for remove promotion. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<String> result = gson.fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error("return false while remove promotion. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		refreshData();
	}
	
	private String getObjectName(int type, int id){
		ArrayList<Category1> category1List = mainFrame.getListCategory1s();
		if (type == ConstantValue.PROMOTION_CATEGORY1){
			for(Category1 c1 : category1List){
				if (c1.getId() == id){
					return c1.getName();
				}
			}
		} else if (type == ConstantValue.PROMOTION_CATEGORY2){
			for(Category1 c1 : category1List){
				if (c1.getCategory2s() != null){
					for(Category2 c2 : c1.getCategory2s()){
						if (c2.getId() == id){
							return c2.getName();
						}
					}
				}
			}
		} else if (type == ConstantValue.PROMOTION_GOODS){
			for(Category1 c1 : category1List){
				if (c1.getCategory2s() != null){
					for(Category2 c2 : c1.getCategory2s()){
						if (c2.getGoods() != null){
							for(Goods g : c2.getGoods()){
								if (g.getId() == id)
									return g.getName();
							}
						}
					}
				}
			}
		}
		return "";
	}
	
	class PromotionTableModel extends AbstractTableModel{

		private String[] header = new String[]{"Forbid Member Discount", "Object A Type", "Object A Name", "Object A Quantity", 
				"Object B Type", "Object B Name", "Object B Quantity", "Reward Type", "Reword Value", "Available"};
		
		public PromotionTableModel(){

		}
		@Override
		public int getRowCount() {
			if (promotionList == null) return 0;
			return promotionList.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Promotion p = promotionList.get(rowIndex);
			switch(columnIndex){
			case 0:
				return String.valueOf(p.isForbidMemberDiscount());
			case 1: 
				if (p.getObjectAType() == ConstantValue.PROMOTION_GOODS)
					return "Goods";
				if (p.getObjectAType() == ConstantValue.PROMOTION_CATEGORY2)
					return "Category2";
				if (p.getObjectAType() == ConstantValue.PROMOTION_CATEGORY1)
					return "Category1";
				break;
			case 2 :
				return getObjectName(p.getObjectAType(), p.getObjectAId());
			case 3:
				return p.getObjectAQuantity();
			case 4:
				if (p.getObjectBType() == ConstantValue.PROMOTION_GOODS)
					return "Goods";
				if (p.getObjectBType() == ConstantValue.PROMOTION_CATEGORY2)
					return "Category2";
				if (p.getObjectBType() == ConstantValue.PROMOTION_CATEGORY1)
					return "Category1";
				break;
			case 5 :
				return getObjectName(p.getObjectBType(), p.getObjectBId());
			case 6:
				return p.getObjectBQuantity();
			case 7 :
				if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNREDUCEPRICE)
					return "BUY A * n, GET PRICE REDUCE";
				if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNDISCOUNT)
					return "BUY A * n, GET PRICE DISCOUNT";
				if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNNEXTDISCOUNT)
					return "BUY A * n, THE NEXT ONE GET DISCOUNT";
				if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNNEXTREDUCEPRICE)
					return "BUY A * n, THE NEXT ONE GET PRICE REDUCE";
				if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_NEXTBREDUCEPRICE)
					return "BUY A * n, THE NEXT B GET PRICE REDUCE";
				if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_NEXTBDISCOUNT)
					return "BUY A * n, THE NEXT B GET DISCOUNT";
				if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABDISCOUNT)
					return "BUY A * n + 1 * B, THEN A & B GET DISCOUNT";
				if (p.getRewardType() == ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABREDUCEPRICE)
					return "BUY A * n + 1 * B, THEN A & B GET PRICE REDUCE";
				break;
			case 8:
				return p.getRewardValue();
			case 9:
				return p.isAvailable();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public Promotion getObjectAt(int row){
			return promotionList.get(row);
		}
	}
}
