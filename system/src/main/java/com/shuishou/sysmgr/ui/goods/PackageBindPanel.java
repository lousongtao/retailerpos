package com.shuishou.sysmgr.ui.goods;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.beans.Goods;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.PackageBind;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.CommonDialogOperatorIFC;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.NumberTextField;
import com.shuishou.sysmgr.ui.member.MemberQueryPanel;

public class PackageBindPanel extends JPanel implements CommonDialogOperatorIFC{
	private final Logger logger = Logger.getLogger(PackageBindPanel.class.getName());
	private PackageBindMgmtDialog parent;
	private MainFrame mainFrame;
	private JTextField tfBigBarcode = new JTextField();
	private JTextField tfBigName = new JTextField();
	private JTextField tfSmallBarcode = new JTextField();
	private JTextField tfSmallName = new JTextField();
	private NumberTextField tfRate = new NumberTextField(false);
	private Goods bigGoods;
	private Goods smallGoods;
	private PackageBind packageBind;
	public PackageBindPanel(PackageBindMgmtDialog parent, MainFrame mainFrame){
		this.parent = parent;
		this.mainFrame = mainFrame;
		initUI();
	}
	
	private void initUI(){
		JPanel pBig = new JPanel(new GridBagLayout());
		pBig.setBorder(BorderFactory.createTitledBorder("Big Package"));
		JPanel pSmall = new JPanel(new GridBagLayout());
		pSmall.setBorder(BorderFactory.createTitledBorder("Small Package"));
		tfBigName.setMinimumSize(new Dimension(180, 25));
		tfBigBarcode.setMinimumSize(new Dimension(180, 25));
		tfSmallName.setMinimumSize(new Dimension(180, 25));
		tfSmallBarcode.setMinimumSize(new Dimension(180, 25));
		tfRate.setMinimumSize(new Dimension(180, 25));
		tfBigName.setEditable(false);
		tfSmallName.setEditable(false);
		
		pBig.add(new JLabel("Barcode"), new GridBagConstraints(0, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pBig.add(tfBigBarcode, 			new GridBagConstraints(1, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pBig.add(new JLabel("Name"), 	new GridBagConstraints(2, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,10,0,0), 0, 0));
		pBig.add(tfBigName, 			new GridBagConstraints(3, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		
		pSmall.add(new JLabel("Barcode"),new GridBagConstraints(0, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		pSmall.add(tfSmallBarcode, 		new GridBagConstraints(1, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		pSmall.add(new JLabel("Name"), 	new GridBagConstraints(2, 0, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,10,0,0), 0, 0));
		pSmall.add(tfSmallName, 		new GridBagConstraints(3, 0, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		
		setLayout(new GridBagLayout());
		add(pBig,   new GridBagConstraints(0, 0, 2, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		add(pSmall, new GridBagConstraints(0, 1, 2, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		add(new JLabel("Rate"), new GridBagConstraints(0, 2, 1, 1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0, 0));
		add(tfRate, new GridBagConstraints(1, 2, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0));
		
		tfBigBarcode.addKeyListener(new KeyAdapter(){
        	public void keyTyped(KeyEvent e) {
        		if (e.getKeyChar() == KeyEvent.VK_ENTER){
        			bigGoods = null;
    				tfBigName.setText("");
        			Goods g = searchObjectByBarcode(tfBigBarcode.getText());
        			if (g != null){
        				bigGoods = g;
        				tfBigName.setText(g.getName());
        				tfSmallBarcode.requestFocusInWindow();
        			}
        		}
        	}
        });
		
		tfSmallBarcode.addKeyListener(new KeyAdapter(){
        	public void keyTyped(KeyEvent e) {
        		if (e.getKeyChar() == KeyEvent.VK_ENTER){
        			smallGoods = null;
    				tfSmallName.setText("");
        			Goods g = searchObjectByBarcode(tfSmallBarcode.getText());
        			if (g != null){
        				smallGoods = g;
        				tfSmallName.setText(g.getName());
        				tfRate.requestFocusInWindow();
        			}
        		}
        	}
        });
	}
	
	private Goods searchObjectByBarcode(String barCode){
		Goods goods = mainFrame.getGoodsByBarcode(barCode);
		if (goods == null){
			JOptionPane.showMessageDialog(this, "cannot find goods by barcode " + barCode);
			return null;
		}
		return goods;
	}
	
	@Override
	public boolean doSave() {
		if (!doCheckInput())
			return false;
		Gson gson = new Gson();
		HashMap<String, String> params = new HashMap<>();
		params.put("userId", String.valueOf(MainFrame.getLoginUser().getId()));
		params.put("bigPackageId", bigGoods.getId() + "");
		params.put("smallPackageId", smallGoods.getId() + "");
		params.put("rate", tfRate.getText());
		String url = "goods/addpackagebind";
		if (packageBind != null){
			url = "goods/updatepackagebind";
			params.put("packageBindId", packageBind.getId() + "");
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for add/update packagebind. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for add/update packagebind. URL = " + url);
			return false;
		}
		
		HttpResult<PackageBind> result = gson.fromJson(response, new TypeToken<HttpResult<PackageBind>>(){}.getType());
		if (!result.success){
			logger.error("return false while add/update packagebind. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return false;
		}
		parent.refreshTable();
		return true;
	}
	
	private boolean doCheckInput(){
		if (bigGoods == null){
			JOptionPane.showMessageDialog(this, "Please input choose a goods as the big package");
			return false;
		}
		if (smallGoods == null){
			JOptionPane.showMessageDialog(this, "Please input choose a goods as the small package");
			return false;
		}
		if (tfRate.getText() == null || tfRate.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input Rate");
			return false;
		}
		return true;
	}
	
	public void setObjectValue(PackageBind pb){
		this.packageBind = pb;
		bigGoods = pb.getBigPackage();
		smallGoods = pb.getSmallPackage();
		tfRate.setText(pb.getRate()+"");
		tfBigName.setText(bigGoods.getName());
		tfBigBarcode.setText(bigGoods.getBarcode());
		tfSmallName.setText(smallGoods.getName());
		tfSmallBarcode.setText(smallGoods.getBarcode());
	}
}
