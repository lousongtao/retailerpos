package com.shuishou.sysmgr.ui.payway;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.PayWay;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.NumberTextField;


public class PayWayDialog extends JDialog implements ActionListener {

	private final Logger logger = Logger.getLogger(PayWayDialog.class.getName());
	
	private MainFrame mainFrame;
	private PayWayMgmtPanel parent;
	
	private JTextField tfName = new JTextField();
	private JTextField tfSymbol = new JTextField();
	private NumberTextField tfRate = new NumberTextField(true);
	private NumberTextField tfSequence = new NumberTextField(false);
	private JButton btnSave = new JButton("Save");
	private JButton btnCancel = new JButton("Cancel");
	
	private PayWay payway;
	public PayWayDialog(MainFrame mainFrame, PayWayMgmtPanel parent,String title){
		super(mainFrame, title, true);
		this.mainFrame = mainFrame;
		this.parent = parent;
		initUI();
	}
	
	private void initUI(){
		JLabel lbName = new JLabel("Name");
		JLabel lbRate = new JLabel("Rate");
		JLabel lbSequence = new JLabel("Sequence");
		JLabel lbSymbol = new JLabel("Symbol");
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbName, 			new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(tfName, 			new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(lbRate, 			new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(tfRate, 			new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(lbSequence, 		new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(tfSequence, 		new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(lbSymbol, 		new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(tfSymbol, 		new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		c.add(btnSave, 			new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		c.add(btnCancel, 		new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		btnSave.addActionListener(this);
		btnCancel.addActionListener(this);
		
		this.setSize(250,200);
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	private void doSave(){
		if (tfName.getText() == null || tfName.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "must input name");
			return;
		}
		if (tfSequence.getText() == null || tfSequence.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "must input squence");
			return;
		}
		if (tfSymbol.getText() == null || tfSymbol.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "must input symbol");
			return;
		}
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		params.put("name", tfName.getText());
		params.put("rate", tfRate.getText());
		params.put("sequence", tfSequence.getText());
		params.put("symbol", tfSymbol.getText());
		String url = "common/addpayway";
		if (payway != null){
			url = "common/updatepayway";
			params.put("id", payway.getId() + "");
		}
		
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for add/update pay way. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for add/update pay way. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<PayWay> result = gson.fromJson(response, new TypeToken<HttpResult<PayWay>>(){}.getType());
		if (!result.success){
			logger.error("return false while add/update pay way. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		parent.refreshData();
		setVisible(false);
	}
	
	public void setObject(PayWay payway){
		this.payway = payway;
		tfName.setText(payway.getName());
		tfRate.setText(payway.getRate()+"");
		tfSequence.setText(payway.getSequence()+"");
		tfSymbol.setText(payway.getSymbol());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel){
			setVisible(false);
		} else if (e.getSource() == btnSave){
			doSave();			
		}
	}
	
}
