package com.shuishou.sysmgr.ui.goods;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.beans.Category1;
import com.shuishou.sysmgr.beans.Category2;
import com.shuishou.sysmgr.beans.Goods;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.Promotion;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.NumberTextField;

public class PromotionDialog extends JDialog implements ActionListener {

	private final Logger logger = Logger.getLogger(PromotionDialog.class.getName());
	
	private MainFrame mainFrame;
	private PromotionMgmtPanel parent;
	
	private JCheckBox cbForbidMemberDiscount = new JCheckBox("Forbid Member Discount", true);
	private JLabel lbObjectAInfo = new JLabel();
	private JComboBox<ObjectType> cbObjectAType = new JComboBox<>();
	private JComboBox<ObjectClass> cbObjectA = new JComboBox<>();
	private NumberTextField tfObjectAQuantity = new NumberTextField(false);
	
	private JLabel lbObjectBInfo = new JLabel();
	private JComboBox<ObjectType> cbObjectBType = new JComboBox<>();
	private JComboBox<ObjectClass> cbObjectB = new JComboBox<>();
	private NumberTextField tfObjectBQuantity = new NumberTextField(false);
	
	private JComboBox<ObjectType> cbRewardType = new JComboBox<>();
	private NumberTextField tfRewardValue = new NumberTextField(true);
	
	private JButton btnSave = new JButton("Save");
	private JButton btnCancel = new JButton("Cancel");
	
	private Promotion promotion;
	public PromotionDialog(MainFrame mainFrame, PromotionMgmtPanel parent,String title){
		super(mainFrame, title, true);
		this.mainFrame = mainFrame;
		this.parent = parent;
		initUI();
		initData();
	}
	
	private void initUI(){
		JLabel lbObjectAType = new JLabel("Object A Type");
		JLabel lbObjectA = new JLabel("Object A");
		JLabel lbObjectAQuantity = new JLabel("Object A Quantity");
		JLabel lbObjectBType = new JLabel("Object B Type");
		JLabel lbObjectB = new JLabel("Object B");
		JLabel lbObjectBQuantity = new JLabel("Object B Quantity");
		JLabel lbRewardType = new JLabel("Reward Type");
		JLabel lbRewardValue = new JLabel("Reward Value");
		
		
		JPanel pObjectA = new JPanel(new GridBagLayout());
		pObjectA.setBorder(BorderFactory.createTitledBorder("Object A"));
		pObjectA.add(lbObjectAType, 		new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pObjectA.add(cbObjectAType, 		new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pObjectA.add(lbObjectA, 			new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pObjectA.add(cbObjectA, 			new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pObjectA.add(lbObjectAQuantity, 	new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pObjectA.add(tfObjectAQuantity, 	new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pObjectA.add(lbObjectAInfo, 		new GridBagConstraints(0, 3, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		
		JPanel pObjectB = new JPanel(new GridBagLayout());
		pObjectB.setBorder(BorderFactory.createTitledBorder("Object B"));
		pObjectB.add(lbObjectBType, 		new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pObjectB.add(cbObjectBType, 		new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pObjectB.add(lbObjectB, 			new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pObjectB.add(cbObjectB, 			new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pObjectB.add(lbObjectBQuantity, 	new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pObjectB.add(tfObjectBQuantity, 	new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pObjectB.add(lbObjectBInfo, 		new GridBagConstraints(0, 3, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		
		JPanel pReward = new JPanel(new GridBagLayout());
		pReward.setBorder(BorderFactory.createTitledBorder("Reward"));
		pReward.add(lbRewardType, 		new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pReward.add(cbRewardType, 		new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pReward.add(lbRewardValue, 	new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pReward.add(tfRewardValue, 	new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(cbForbidMemberDiscount, 	new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(pObjectA, 				new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(pObjectB, 				new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(pReward, 					new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(btnSave, 			new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(btnCancel, 		new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		btnSave.addActionListener(this);
		btnCancel.addActionListener(this);
		
		this.setSize(750,500);
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	private void initData(){
		cbObjectAType.addItem(new ObjectType(ConstantValue.PROMOTION_CATEGORY1, "Category1"));
		cbObjectAType.addItem(new ObjectType(ConstantValue.PROMOTION_CATEGORY2, "Category2"));
		cbObjectAType.addItem(new ObjectType(ConstantValue.PROMOTION_GOODS, "Goods"));
		cbObjectAType.setSelectedIndex(-1);
		cbObjectAType.addActionListener(this);
		cbObjectBType.addItem(new ObjectType(ConstantValue.PROMOTION_CATEGORY1, "Category1"));
		cbObjectBType.addItem(new ObjectType(ConstantValue.PROMOTION_CATEGORY2, "Category2"));
		cbObjectBType.addItem(new ObjectType(ConstantValue.PROMOTION_GOODS, "Goods"));
		cbObjectBType.setSelectedIndex(-1);
		cbObjectBType.addActionListener(this);
		cbRewardType.addItem(new ObjectType(ConstantValue.PROMOTION_REWARD_BUYNDISCOUNT, "BUY A * n, GET PRICE DISCOUNT"));
		cbRewardType.addItem(new ObjectType(ConstantValue.PROMOTION_REWARD_BUYNREDUCEPRICE, "BUY A * n, GET PRICE REDUCE"));
		cbRewardType.addItem(new ObjectType(ConstantValue.PROMOTION_REWARD_BUYNNEXTDISCOUNT, "BUY A * n, THE NEXT ONE GET DISCOUNT"));
		cbRewardType.addItem(new ObjectType(ConstantValue.PROMOTION_REWARD_BUYNNEXTREDUCEPRICE, "BUY A * n, THE NEXT ONE GET PRICE REDUCE"));
		cbRewardType.addItem(new ObjectType(ConstantValue.PROMOTION_REWARD_BUYNA_NEXTBREDUCEPRICE, "BUY A * n, THE NEXT B GET PRICE REDUCE"));
		cbRewardType.addItem(new ObjectType(ConstantValue.PROMOTION_REWARD_BUYNA_NEXTBDISCOUNT, "BUY A * n, THE NEXT B GET DISCOUNT"));
		cbRewardType.addItem(new ObjectType(ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABDISCOUNT, "BUY A * n + B * m, THEN A & B GET DISCOUNT"));
		cbRewardType.addItem(new ObjectType(ConstantValue.PROMOTION_REWARD_BUYNA_MB_ABREDUCEPRICE, "BUY A * n + B * m, THEN A & B GET PRICE REDUCE"));
		cbRewardType.setSelectedIndex(0);
	}
	
	private void refreshObjectA(){
		cbObjectA.removeAllItems();
		ArrayList<Category1> category1List = mainFrame.getListCategory1s();
		if (((ObjectType)cbObjectAType.getSelectedItem()).id == ConstantValue.PROMOTION_CATEGORY1){
			for (int i = 0; i < category1List.size(); i++) {
				cbObjectA.addItem(new ObjectClass(category1List.get(i).getId(), category1List.get(i)));
			}
		} else if (((ObjectType)cbObjectAType.getSelectedItem()).id == ConstantValue.PROMOTION_CATEGORY2){
			for (int i = 0; i < category1List.size(); i++) {
				Category1 c1 = category1List.get(i);
				if (c1.getCategory2s() != null){
					for (int j = 0; j < c1.getCategory2s().size(); j++) {
						Category2 c2 = c1.getCategory2s().get(j); 
						cbObjectA.addItem(new ObjectClass(c2.getId(), c2));
					}
				}
			}
		} else if (((ObjectType)cbObjectAType.getSelectedItem()).id == ConstantValue.PROMOTION_GOODS){
			for (int i = 0; i < category1List.size(); i++) {
				Category1 c1 = category1List.get(i);
				if (c1.getCategory2s() != null){
					for (int j = 0; j < c1.getCategory2s().size(); j++) {
						Category2 c2 = c1.getCategory2s().get(j);
						if (c2.getGoods() != null){
							for (int k = 0; k < c2.getGoods().size(); k++) {
								Goods goods = c2.getGoods().get(k);
								cbObjectA.addItem(new ObjectClass(goods.getId(), goods));
							}
						}
					}
				}
			}
		} 
	}
	
	private void refreshObjectB(){
		cbObjectB.removeAllItems();
		ArrayList<Category1> category1List = mainFrame.getListCategory1s();
		if (((ObjectType)cbObjectBType.getSelectedItem()).id == ConstantValue.PROMOTION_CATEGORY1){
			for (int i = 0; i < category1List.size(); i++) {
				cbObjectB.addItem(new ObjectClass(category1List.get(i).getId(), category1List.get(i)));
			}
		} else if (((ObjectType)cbObjectBType.getSelectedItem()).id == ConstantValue.PROMOTION_CATEGORY2){
			for (int i = 0; i < category1List.size(); i++) {
				Category1 c1 = category1List.get(i);
				if (c1.getCategory2s() != null){
					for (int j = 0; j < c1.getCategory2s().size(); j++) {
						Category2 c2 = c1.getCategory2s().get(j); 
						cbObjectB.addItem(new ObjectClass(c2.getId(), c2));
					}
				}
			}
		} else if (((ObjectType)cbObjectBType.getSelectedItem()).id == ConstantValue.PROMOTION_GOODS){
			for (int i = 0; i < category1List.size(); i++) {
				Category1 c1 = category1List.get(i);
				if (c1.getCategory2s() != null){
					for (int j = 0; j < c1.getCategory2s().size(); j++) {
						Category2 c2 = c1.getCategory2s().get(j);
						if (c2.getGoods() != null){
							for (int k = 0; k < c2.getGoods().size(); k++) {
								Goods goods = c2.getGoods().get(k);
								cbObjectB.addItem(new ObjectClass(goods.getId(), goods));
							}
						}
					}
				}
			}
		} 
	}
	
	private boolean checkData(){
		if (tfRewardValue.getText() == null || tfRewardValue.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "must input Reward Value");
			return false;
		}
		if (tfObjectAQuantity.getText() == null || tfObjectAQuantity.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "must input Object A Quantity");
			return false;
		}
		int quantityA = 0;
		try{
			quantityA = Integer.parseInt(tfObjectAQuantity.getText());
		} catch(NumberFormatException e){}
		if (quantityA == 0){
			JOptionPane.showMessageDialog(this, "must input an integer more zero for Object A Quantity");
			return false;
		}
		if (cbObjectA.getSelectedIndex() == -1){
			JOptionPane.showMessageDialog(this, "must input Object A");
			return false;
		}
		int quantityB = 0;
		try{
			quantityB = Integer.parseInt(tfObjectBQuantity.getText());
		} catch(NumberFormatException e){}
		if (cbObjectB.getSelectedIndex() > -1 && quantityB == 0){
			JOptionPane.showMessageDialog(this, "must input an integer more zero for Object B Quantity");
			return false;
		}
		return true;
	}
	
	private void doSave(){
		if (!checkData()){
			return;
		}
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		params.put("forbidMemberDiscount", String.valueOf(cbForbidMemberDiscount.isSelected()));
		params.put("objectAType", ((ObjectType)cbObjectAType.getSelectedItem()).id + "");
		params.put("objectAId", ((ObjectClass)cbObjectA.getSelectedItem()).id + "");
		params.put("objectAQuantity", tfObjectAQuantity.getText());
		if (cbObjectBType.getSelectedIndex() > -1)
			params.put("objectBType", ((ObjectType)cbObjectBType.getSelectedItem()).id +"");
		else 
			params.put("objectBType", "0");
		if (cbObjectB.getSelectedIndex() > -1)
			params.put("objectBId", ((ObjectClass)cbObjectB.getSelectedItem()).id + "");
		else 
			params.put("objectBId", "0");
		if (tfObjectBQuantity.getText() != null && tfObjectBQuantity.getText().length() > 0)
			params.put("objectBQuantity", tfObjectBQuantity.getText());
		else 
			params.put("objectBQuantity", "0");
		params.put("rewardType", ((ObjectType)cbRewardType.getSelectedItem()).id + "");
		params.put("rewardValue", tfRewardValue.getText());
		String url = "promotion/addpromotion";
		if (promotion != null){
			url = "promotion/updatepromotion";
			params.put("id", promotion.getId() + "");
		}
		
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for add/update promotion. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for add/update promotion. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<Promotion> result = gson.fromJson(response, new TypeToken<HttpResult<Promotion>>(){}.getType());
		if (!result.success){
			logger.error("return false while add/update promotion. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		parent.refreshData();
		setVisible(false);
	}
	
	public void setObject(Promotion promotion){
		this.promotion = promotion;
		cbRewardType.setSelectedItem(new ObjectType(promotion.getRewardType(), ""));//here no need to set name, because the equal function only compare id
		cbObjectAType.setSelectedItem(new ObjectType(promotion.getObjectAType(), ""));
		cbObjectA.setSelectedItem(new ObjectClass(promotion.getObjectAId(), ""));
		tfObjectAQuantity.setText(promotion.getObjectAQuantity()+"");
		tfRewardValue.setText(promotion.getRewardValue() + "");
		if (promotion.getObjectBType() > 0){
			cbObjectBType.setSelectedItem(new ObjectType(promotion.getObjectBType(), ""));
		}
		if (promotion.getObjectBId() > 0){
			cbObjectB.setSelectedItem(new ObjectClass(promotion.getObjectBId(), ""));
		}
		if (promotion.getObjectBQuantity() >= 0)
			tfObjectBQuantity.setText(promotion.getObjectBQuantity() + "");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel){
			setVisible(false);
		} else if (e.getSource() == btnSave){
			doSave();			
		} else if (e.getSource() == cbObjectAType){
			refreshObjectA();
		} else if (e.getSource() == cbObjectBType){
			refreshObjectB();
		}
	}
	
	/**
	 * 对象类型和奖励类型都可以用这个包装类
	 * 对象类型, 如商品, 目录
	 * 奖励类型, 如 如何返利
	 */
	class ObjectType{
		public int id;
		public String name;
		public ObjectType(int id, String name){
			this.id = id;
			this.name = name;
		}
		
		public String toString(){
			return name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ObjectType other = (ObjectType) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id != other.id)
				return false;
			return true;
		}

		private PromotionDialog getOuterType() {
			return PromotionDialog.this;
		}
		
		
	}
	
	/**
	 * 对象包覆类, 包装商品和目录对象
	 */
	class ObjectClass{
		public int id;
		public Object obj;
		public ObjectClass(int id, Object obj){
			this.id = id;
			this.obj = obj;
		}
		
		public String toString(){
			if (obj instanceof Category1){
				return ((Category1)obj).getName();
			} else if (obj instanceof Category2){
				return ((Category2)obj).getName();
			} else if (obj instanceof Goods){
				return ((Goods)obj).getName();
			}
			return "";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ObjectClass other = (ObjectClass) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id != other.id)
				return false;
			return true;
		}

		private PromotionDialog getOuterType() {
			return PromotionDialog.this;
		}
		
		
	}
}
