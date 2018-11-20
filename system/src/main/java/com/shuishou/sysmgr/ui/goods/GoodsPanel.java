package com.shuishou.sysmgr.ui.goods;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.beans.Category1;
import com.shuishou.sysmgr.beans.Category2;
import com.shuishou.sysmgr.beans.Goods;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.CommonDialogOperatorIFC;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.NumberTextField;

public class GoodsPanel extends JPanel implements CommonDialogOperatorIFC{
	private final Logger logger = Logger.getLogger(GoodsPanel.class.getName());
	private GoodsMgmtPanel parent;
	private JTextField tfName= new JTextField(155);
	private JTextField tfBarcode= new JTextField(155);
//	private NumberTextField tfDisplaySeq= new NumberTextField(false);
	private NumberTextField tfBuyPrice= new NumberTextField(true);
	private NumberTextField tfTradePrice= new NumberTextField(true);//批发价
	private NumberTextField tfSellPrice= new NumberTextField(true);
	private NumberTextField tfMemberPrice= new NumberTextField(true);
	private NumberTextField tfLeftAmount= new NumberTextField(false);
	private JTextArea taDescription = new JTextArea();
	private JLabel lbLeftAmount = new JLabel("Left Amount");
	private JComboBox cbCategory2 = new JComboBox();
	private Goods goods;
	private Category2 parentCategory2;
	public GoodsPanel(GoodsMgmtPanel parent){
		this.parent = parent;
		initUI();
		initData();
	}
	
	public GoodsPanel(GoodsMgmtPanel parent, Category2 parentCategory2){
		this.parent = parent;
		this.parentCategory2 = parentCategory2;
		initUI();
		initData();
	}
	
	private void initData(){
		ArrayList<Category1> listCategory1 = parent.getMainFrame().getListCategory1s();
		cbCategory2.removeAllItems();
		for(Category1 c1 : listCategory1){
			for(Category2 c2 : c1.getCategory2s()){
				cbCategory2.addItem(c2);
			}
		}
		if (parentCategory2 != null){
			cbCategory2.setSelectedItem(parentCategory2);
		} else {
			cbCategory2.setSelectedIndex(0);
		}
	}
	
	private void initUI(){
		JLabel lbName = new JLabel("Name");
//		JLabel lbDisplaySeq = new JLabel("Display Sequence");
		JLabel lbCategory2 = new JLabel("Category2");
		JLabel lbBarcode = new JLabel("Barcode");
		JLabel lbBuyPrice = new JLabel("Buy Price");
		JLabel lbTradePrice = new JLabel("Trade Price");
		JLabel lbSellPrice = new JLabel("Sell Price");
		JLabel lbMemberPrice = new JLabel("Member Price");
		JLabel lbDescription = new JLabel("Description");
		JScrollPane jspDescription = new JScrollPane(taDescription, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		cbCategory2.setRenderer(new Category2ListRender());
		
		tfLeftAmount.setText("0");
		
		tfName.setMinimumSize(new Dimension(180, 25));
		tfBarcode.setMinimumSize(new Dimension(180, 25));
		tfTradePrice.setMinimumSize(new Dimension(180,25));
		tfBuyPrice.setMinimumSize(new Dimension(180, 25));
		tfSellPrice.setMinimumSize(new Dimension(180, 25));
		tfMemberPrice.setMinimumSize(new Dimension(180, 25));
		tfLeftAmount.setMinimumSize(new Dimension(180, 25));
		cbCategory2.setMinimumSize(new Dimension(180,25));
		jspDescription.setMinimumSize(new Dimension(180, 100));
		setLayout(new GridBagLayout());
		int row = 0;
		add(lbName, 		new GridBagConstraints(0, row, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfName, 		new GridBagConstraints(1, row, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		row++;
		add(lbBarcode, 	new GridBagConstraints(0, row, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfBarcode, 	new GridBagConstraints(1, row, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		row++;
//		add(lbDisplaySeq, new GridBagConstraints(0, row, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
//		add(tfDisplaySeq, new GridBagConstraints(1, row, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
//		row++;
		add(lbBuyPrice, 	new GridBagConstraints(0, row, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfBuyPrice, 	new GridBagConstraints(1, row, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		row++;
		add(lbTradePrice, 	new GridBagConstraints(0, row, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfTradePrice, 	new GridBagConstraints(1, row, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		row++;
		add(lbSellPrice, 	new GridBagConstraints(0, row, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfSellPrice, 	new GridBagConstraints(1, row, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		row++;
		add(lbMemberPrice, new GridBagConstraints(0, row, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfMemberPrice, new GridBagConstraints(1, row, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		row++;
		add(lbCategory2, 	new GridBagConstraints(0, row, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(cbCategory2, 	new GridBagConstraints(1, row, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		row++;
		add(lbLeftAmount, new GridBagConstraints(0, row, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfLeftAmount, new GridBagConstraints(1, row, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		row++;
		add(lbDescription, new GridBagConstraints(0, row, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(jspDescription, new GridBagConstraints(1, row, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,20,0,0), 0, 0));
		row++;
		add(new JPanel(), new GridBagConstraints(0, row, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,0,0,0), 0, 0));
		
		tfTradePrice.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e) {
				if (tfTradePrice.getText() == null || tfTradePrice.getText().length() == 0){
					tfTradePrice.setText(tfBuyPrice.getText());
					tfTradePrice.select(0, tfTradePrice.getText().length());
				}
			}
		});
		tfMemberPrice.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e) {
				if (tfMemberPrice.getText() == null || tfMemberPrice.getText().length() == 0){
					tfMemberPrice.setText(tfSellPrice.getText());
					tfMemberPrice.select(0, tfMemberPrice.getText().length());
				}
			}
		});
	}

	@Override
	public boolean doSave() {
		if (!doCheckInput())
			return false;
		Gson gson = new Gson();
		HashMap<String, String> params = new HashMap<>();
		params.put("userId", String.valueOf(MainFrame.getLoginUser().getId()));
		params.put("name", tfName.getText());
//		params.put("sequence", tfDisplaySeq.getText());
		if (tfBarcode.getText() == null || tfBarcode.getText().length() > 0)
			params.put("barcode", tfBarcode.getText());
		else 
			params.put("barcode", "");
		params.put("buyPrice", tfBuyPrice.getText());
		params.put("sellPrice", tfSellPrice.getText());
		params.put("tradePrice", tfTradePrice.getText());
		params.put("memberPrice", tfMemberPrice.getText());
		params.put("leftAmount", tfLeftAmount.getText());
		params.put("category2Id", String.valueOf(((Category2)cbCategory2.getSelectedItem()).getId()));
		if (taDescription.getText() != null && taDescription.getText().length() > 0)
			params.put("description", taDescription.getText());
		else 
			params.put("description", "");
		String url = "goods/add_goods";
		if (goods != null){
			url = "goods/update_goods";
			params.put("id", goods.getId() + "");
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for add/update goods. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for add/update goods. URL = " + url);
			return false;
		}
		
		HttpResult<Goods> result = gson.fromJson(response, new TypeToken<HttpResult<Goods>>(){}.getType());
		if (!result.success){
			logger.error("return false while add/update goods. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return false;
		}
		//the category2 info is null after get from server
		result.data.setCategory2((Category2)cbCategory2.getSelectedItem());
		if (goods == null){
			parent.insertNode(result.data);
		} else {
			parent.updateNode(result.data, goods);
		}
		return true;
	}
	
	private boolean doCheckInput(){
		if (tfName.getText() == null || tfName.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Name");
			return false;
		}
//		if (tfDisplaySeq.getText() == null || tfDisplaySeq.getText().length() == 0){
//			JOptionPane.showMessageDialog(this, "Please input Display Sequence");
//			return false;
//		}
		if (tfBuyPrice.getText() == null || tfBuyPrice.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Buy Price");
			return false;
		}
		if (tfTradePrice.getText() == null || tfTradePrice.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Trade Price");
			return false;
		}
		if (tfSellPrice.getText() == null || tfSellPrice.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Sell Price");
			return false;
		}
		if (tfMemberPrice.getText() == null || tfMemberPrice.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Member Price");
			return false;
		}
		if (tfLeftAmount.getText() == null || tfLeftAmount.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Left Amount");
			return false;
		}
//		if (tfBarcode.getText() == null || tfBarcode.getText().length() == 0){
//			JOptionPane.showMessageDialog(this, "Please input Barcode");
//			return false;
//		}
		if (cbCategory2.getSelectedIndex() == -1){
			JOptionPane.showMessageDialog(this, "Please input Category2");
			return false;
		}
		if (taDescription.getText() != null && taDescription.getText().length() > 255){
			JOptionPane.showMessageDialog(this, "The length of description is too long.");
			return false;
		}
		return true;
	}
	
	public void setObjectValue(Goods goods){
		this.goods = goods;
		tfName.setText(goods.getName());
//		tfDisplaySeq.setText(goods.getSequence()+"");
		tfBuyPrice.setText(goods.getBuyPrice()+"");
		tfTradePrice.setText(goods.getTradePrice()+"");
		tfSellPrice.setText(goods.getSellPrice()+"");
		tfMemberPrice.setText(goods.getMemberPrice()+"");
		tfBarcode.setText(goods.getBarcode()+"");
		tfLeftAmount.setText(goods.getLeftAmount()+"");
		cbCategory2.setSelectedItem(goods.getCategory2());
		if (goods.getDescription() != null){
			taDescription.setText(goods.getDescription());
		} else {
			taDescription.setText("");
		}
	}
	
	public void refreshCategory2List(){
		ArrayList<Category1> listCategory1 = parent.getMainFrame().getListCategory1s();
		cbCategory2.removeAllItems();
		for(Category1 c1 : listCategory1){
			for(Category2 c2 : c1.getCategory2s()){
				cbCategory2.addItem(c2);
			}
		}
	}
	
	public void hideLeftAmout(){
		lbLeftAmount.setVisible(false);
		tfLeftAmount.setVisible(false);
	}
	
	class Category2ListRender extends JLabel implements ListCellRenderer{
		
		public Category2ListRender(){}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			if (value != null)
				setText(((Category2)value).getName());
			return this;
		}
		
	}
	
}
