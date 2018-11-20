package com.shuishou.sysmgr.ui.goods;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.Category1;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.PackageBind;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.CommonDialog;
import com.shuishou.sysmgr.ui.MainFrame;

public class PackageBindMgmtDialog extends JDialog implements ActionListener{
	private final Logger logger = Logger.getLogger(PackageBindMgmtDialog.class.getName());
	private MainFrame parent;
	private JTable table = new JTable();
	private TableModel model = new TableModel();
	private JButton btnAdd = new JButton("ADD");
	private JButton btnModify = new JButton("Modify");
	private JButton btnDelete = new JButton("Delete");
	private JButton btnClose = new JButton("Close");
	
	public PackageBindMgmtDialog(MainFrame parent){
		this.parent = parent;
		setTitle("Package Bind");
		setSize(700, 400);
		this.setModal(true);
		initUI();
		initData();
	}
	
	private void initUI(){
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(60);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);
		table.getColumnModel().getColumn(4).setPreferredWidth(150);
		table.setAutoCreateRowSorter(true);
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		JPanel pButton = new JPanel();
		pButton.add(btnAdd);
		pButton.add(btnModify);
		pButton.add(btnDelete);
		pButton.add(btnClose);
		
		btnAdd.addActionListener(this);
		btnModify.addActionListener(this);
		btnDelete.addActionListener(this);
		btnClose.addActionListener(this);
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(jspTable, BorderLayout.CENTER);
		c.add(pButton, BorderLayout.SOUTH);
		setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
				(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
	}
	
	private void initData(){
		refreshTable();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAdd){
			PackageBindPanel p = new PackageBindPanel(this, parent);
			CommonDialog dlg = new CommonDialog(this, p, "Add PackageBind", 500, 300);
			dlg.setVisible(true);
		} else if (e.getSource() == btnDelete){
			doDelete();
		} else if (e.getSource() == btnModify){
			if (table.getSelectedRow() < 0){
				return;
			}
			int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
			PackageBind pb = ((TableModel)table.getModel()).getObjectAt(modelRow);
			PackageBindPanel p = new PackageBindPanel(this, parent);
			p.setObjectValue(pb);
			CommonDialog dlg = new CommonDialog(this, p, "Modify PackageBind", 500, 300);
			dlg.setVisible(true);
		} else if (e.getSource() == btnClose){
			this.setVisible(false);
		}
	}
	
	public void refreshTable(){
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		String url = "goods/querypackagebinds";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for query package bind. URL = " + url );
			JOptionPane.showMessageDialog(this, "get null from server for query package bind. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<ArrayList<PackageBind>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<PackageBind>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query package bind. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		model.setData(result.data);
		model.fireTableDataChanged();
	}

	private void doDelete(){
		if (table.getSelectedRow() < 0)
			return;
		int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
		PackageBind pb = ((TableModel)table.getModel()).getObjectAt(modelRow);
		if (JOptionPane.showConfirmDialog(this, "Do you want to delete this package bind between "+ pb.getBigPackage().getName()+"/"+pb.getSmallPackage().getName(), "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			return;
		}
		
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("packageBindId", pb.getId()+"");
		String url = "goods/deletepackagebind";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for delete package bind. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for delete package bind. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<String> result = gson.fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error("return false while delete package bind. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		model.deleteRow(modelRow);
		model.fireTableDataChanged();
	}
	
	class TableModel extends DefaultTableModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<PackageBind> data;
		private String[] header = new String[]{"Big Package Name", "Big Package Barcode","rate","Small Package Name","Small Package Barcode"};
		
		public TableModel(){

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
		public Object getValueAt(int rowIndex, int columnIndex) {
			PackageBind pb = data.get(rowIndex);
			switch(columnIndex){
			case 0:
				return pb.getBigPackage().getName();
			case 1: 
				return pb.getBigPackage().getBarcode();
			case 2:
				return pb.getRate();
			case 3:
				return pb.getSmallPackage().getName();
			case 4:
				return pb.getSmallPackage().getBarcode();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int col){
			return header[col];
		}
		
		public PackageBind getObjectAt(int row){
			return data.get(row);
		}
		
		public void setData(ArrayList<PackageBind> data){
			this.data = data;
		}
		
		public void deleteRow(int row){
			data.remove(row);
		}
		
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}
