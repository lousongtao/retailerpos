package com.shuishou.sysmgr.ui.goods;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.beans.Goods;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.CommonDialogOperatorIFC;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.NumberTextField;

public class ImportGoodsPanel extends JPanel implements CommonDialogOperatorIFC{

	private final Logger logger = Logger.getLogger(ImportGoodsPanel.class.getName());
	private GoodsMgmtPanel parent;
	private JTextField tfName= new JTextField(155);
	private JTextField tfCurrentAmount = new JTextField(155);
	private NumberTextField tfBarcode = new NumberTextField(false);
	private NumberTextField tfImportAmount = new NumberTextField(false);
	private Goods goods;
	private Gson gson = new Gson();
	
	public ImportGoodsPanel(GoodsMgmtPanel parent){
		this.parent = parent;
		initUI();
	}
	
	public ImportGoodsPanel(GoodsMgmtPanel parent, Goods goods){
		this.parent = parent;
		this.goods = goods;
		initUI();
		initData(goods);
	}
	
	private void initUI(){
		JLabel lbName = new JLabel("Name");
		JLabel lbCurrentAmount = new JLabel("Current Amount");
		JLabel lbImportAmount = new JLabel("Import Amount");
		JLabel lbBarcode = new JLabel("Barcode");
		tfCurrentAmount.setEditable(false);
		tfName.setEditable(false);
		this.setLayout(new GridBagLayout());
		add(lbName, 			new GridBagConstraints(0, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfName, 			new GridBagConstraints(1, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(lbBarcode, 			new GridBagConstraints(0, 1, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfBarcode, 			new GridBagConstraints(1, 1, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(lbCurrentAmount, 	new GridBagConstraints(0, 2, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfCurrentAmount, 	new GridBagConstraints(1, 2, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(lbImportAmount, 	new GridBagConstraints(0, 3, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfImportAmount, 	new GridBagConstraints(1, 3, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		tfName.setMinimumSize(new Dimension(180,25));
		tfCurrentAmount.setMinimumSize(new Dimension(180,25));
		tfImportAmount.setMinimumSize(new Dimension(180,25));
		tfBarcode.setMinimumSize(new Dimension(180, 25));
		
		tfBarcode.addKeyListener(new KeyAdapter(){
        	public void keyTyped(KeyEvent e) {
        		if (e.getKeyChar() == KeyEvent.VK_ENTER){
        			Goods g = parent.getMainFrame().getGoodsByBarcode(tfBarcode.getText());
        			goods = g;
        			initData(goods);
        		}
        	}
        });
		
	}
	
	public void putFocusOnBarcode(){
		tfBarcode.requestFocusInWindow();
	}
	
	private void initData(Goods goods){
		if (goods != null){
			tfName.setText(goods.getName());
			tfCurrentAmount.setText(goods.getLeftAmount() + "");
			tfBarcode.setText(goods.getBarcode());
		}
	}

	@Override
	public boolean doSave() {
		if (this.goods == null){
			JOptionPane.showMessageDialog(this, "No find goods info");
			return false;
		}
		if (tfImportAmount.getText() == null || tfImportAmount.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Amount");
			return false;
		}
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("id", goods.getId() + "");
		params.put("amount", tfImportAmount.getText());
		String url = "goods/import_goods";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL+url, params);
		if (response == null){
			logger.error("get null from server for import goods. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for import goods. URL = " + url);
			return false;
		}
		HttpResult<Goods> result = gson.fromJson(response, new TypeToken<HttpResult<Goods>>(){}.getType());
		if (!result.success){
			logger.error("return false while import goods. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return false;
		}
		result.data.setCategory2(goods.getCategory2());
		parent.updateNode(result.data, goods);
		return true;
		
	}
}
