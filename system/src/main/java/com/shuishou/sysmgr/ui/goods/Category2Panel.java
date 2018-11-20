package com.shuishou.sysmgr.ui.goods;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.beans.Category1;
import com.shuishou.sysmgr.beans.Category2;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.Permission;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.CommonDialogOperatorIFC;
import com.shuishou.sysmgr.ui.MainFrame;

public class Category2Panel extends JPanel implements CommonDialogOperatorIFC{
	private final Logger logger = Logger.getLogger(Category2Panel.class.getName());
	private GoodsMgmtPanel parent;
	private JTextField tfName= new JTextField(155);
	private JTextField tfDisplaySeq= new JTextField(155);
	private JComboBox<Category1> cbCategory1 = new JComboBox();
	private Category2 c2;
	private Gson gson = new Gson();
	private Category1 parentCategory1;
	public Category2Panel(GoodsMgmtPanel parent){
		this.parent = parent;
		initUI();
		initData();
	}
	
	public Category2Panel(GoodsMgmtPanel parent, Category1 parentCategory1){
		this.parent = parent;
		this.parentCategory1 = parentCategory1;
		initUI();
		initData();
	}
	
	private void initUI(){
		JLabel lbName = new JLabel("Name");
		JLabel lbDisplaySeq = new JLabel("Display Sequence");
		JLabel lbCategory1 = new JLabel("Category1");
		this.setLayout(new GridBagLayout());
		add(lbName, 	new GridBagConstraints(0, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfName, 	new GridBagConstraints(1, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		add(lbDisplaySeq, 	new GridBagConstraints(0, 2, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfDisplaySeq, 	new GridBagConstraints(1, 2, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		add(lbCategory1, 	new GridBagConstraints(0, 3, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(cbCategory1, 	new GridBagConstraints(1, 3, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,20,0,0), 0, 0));
		add(new JPanel(), new GridBagConstraints(0, 5, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		tfDisplaySeq.setMinimumSize(new Dimension(180,25));
		tfName.setMinimumSize(new Dimension(180,25));
		cbCategory1.setMinimumSize(new Dimension(180,25));
		
		tfDisplaySeq.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!((c >= '0') && (c <= '9'))) {
					getToolkit().beep();
					e.consume();
				} 
			}
		});
	}

	private void initData(){
		ArrayList<Category1> listCategory1 = parent.getMainFrame().getListCategory1s();
		cbCategory1.removeAllItems();
		for(Category1 c1 : listCategory1){
			cbCategory1.addItem(c1);
		}
		if (parentCategory1 != null){
			cbCategory1.setSelectedItem(parentCategory1);
		}
	}
	
	@Override
	public boolean doSave() {
		if (!doCheckInput())
			return false;
		
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("name", tfName.getText());
		params.put("sequence", tfDisplaySeq.getText());
		params.put("category1Id", ((Category1)cbCategory1.getSelectedItem()).getId() + "");
		String url = "goods/add_category2";
		if (c2 != null){
			url = "goods/update_category2";
			params.put("id", c2.getId() + "");
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for add/update category2. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for add/update category2. URL = " + url);
			return false;
		}
		HttpResult<Category2> result = gson.fromJson(response, new TypeToken<HttpResult<Category2>>(){}.getType());
		if (!result.success){
			logger.error("return false while add/update category2. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return false;
		}
		result.data.setCategory1((Category1)cbCategory1.getSelectedItem());//server don't return the parent
		if (c2 == null){
			parent.insertNode(result.data);
		} else {
			parent.updateNode(result.data, c2);
		}
		return true;
	}
	
	private boolean doCheckInput(){
		if (tfName.getText() == null || tfName.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Name");
			return false;
		}
		if (tfDisplaySeq.getText() == null || tfDisplaySeq.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Display Sequence");
			return false;
		}
		if (cbCategory1.getSelectedIndex() == -1){
			JOptionPane.showMessageDialog(this, "Please input Cagetory1");
			return false;
		}
		return true;
	}
	
	public void setObjectValue(Category2 c2){
		this.c2 = c2;
		tfName.setText(c2.getName());
		tfDisplaySeq.setText(c2.getSequence()+"");
		cbCategory1.setSelectedItem(c2.getCategory1());
	}
	
	public void refreshCategory1List(){
		ArrayList<Category1> listCategory1 = parent.getMainFrame().getListCategory1s();
		cbCategory1.removeAllItems();
		for(Category1 c1 : listCategory1){
			cbCategory1.addItem(c1);
		}
	}
	

	class Category1ListRender extends JLabel implements ListCellRenderer{
		
		public Category1ListRender(){}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			setText(((Category1)value).getName());
			return this;
		}
	}
	
}
