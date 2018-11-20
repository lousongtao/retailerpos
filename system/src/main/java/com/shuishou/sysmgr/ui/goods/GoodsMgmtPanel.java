package com.shuishou.sysmgr.ui.goods;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.Category1;
import com.shuishou.sysmgr.beans.Category2;
import com.shuishou.sysmgr.beans.Goods;
import com.shuishou.sysmgr.beans.GoodsSellRecord;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.CommonDialog;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.NumberInputDialog;

public class GoodsMgmtPanel extends JPanel implements TreeSelectionListener, ActionListener{
	private final Logger logger = Logger.getLogger(GoodsMgmtPanel.class.getName());
	private JTabbedPane tabPane;
	private Category1Panel pCategory1;
	private Category2Panel pCategory2;
	private JLabel lbStatistics = new JLabel();
	private JTextField tfSearchBarcode = new JTextField(); 
	private JTextField tfSearchName = new JTextField(); 
	private JButton btnLookfor = new JButton("Find");
	private JButton btnPackageBind = new JButton("Package Bind");
	private JButton btnAddGoods = new JButton("Add Goods");
	
	private GoodsPanel pGoods;
	
	private JTree goodsTree;
	private JPopupMenu popupmenuRoot = new JPopupMenu();
	private JMenuItem menuitemScanImport = new JMenuItem(Messages.getString("GoodsMgmtPanel.ScanImport"));
	private JMenuItem menuitemAddC1 = new JMenuItem(Messages.getString("GoodsMgmtPanel.AddCategory1"));
	private JMenuItem menuitemRefreshTree = new JMenuItem(Messages.getString("GoodsMgmtPanel.RefreshTree"));
	private JPopupMenu popupmenuC1 = new JPopupMenu();
	private JMenuItem menuitemModifyC1 = new JMenuItem(Messages.getString("GoodsMgmtPanel.Modify"));
	private JMenuItem menuitemAddC2 = new JMenuItem(Messages.getString("GoodsMgmtPanel.AddCategory2"));
	private JMenuItem menuitemDeleteC1 = new JMenuItem(Messages.getString("GoodsMgmtPanel.Delete"));
	private JPopupMenu popupmenuC2 = new JPopupMenu();
	private JMenuItem menuitemModifyC2 = new JMenuItem(Messages.getString("GoodsMgmtPanel.Modify"));
	private JMenuItem menuitemAddGoods = new JMenuItem(Messages.getString("GoodsMgmtPanel.AddGoods"));
	private JMenuItem menuitemDeleteC2 = new JMenuItem(Messages.getString("GoodsMgmtPanel.Delete"));
	private JPopupMenu popupmenuGoods = new JPopupMenu();
	private JMenuItem menuitemModifyGoods = new JMenuItem(Messages.getString("GoodsMgmtPanel.Modify"));
	private JMenuItem menuitemDeleteGoods = new JMenuItem(Messages.getString("GoodsMgmtPanel.Delete"));
	private JMenuItem menuitemImportGoods = new JMenuItem(Messages.getString("GoodsMgmtPanel.ImportGoods"));
	private JMenuItem menuitemUpdateAmountGoods = new JMenuItem(Messages.getString("GoodsMgmtPanel.UpdateAmountGoods"));
	private JMenuItem menuitemQuerySoldRecord = new JMenuItem(Messages.getString("GoodsMgmtPanel.QueryGoodsSoldRecord"));
	
	private ArrayList<Category1> category1s ;
	private MainFrame mainFrame;
	
	public GoodsMgmtPanel(MainFrame mainFrame, ArrayList<Category1> category1s){
		this.mainFrame = mainFrame;
		this.category1s = category1s;
		initUI();
	}
	
	private void initUI(){
		//build tree
		GoodsTreeNode root = new GoodsTreeNode("root");
		buildTree(root);
		goodsTree = new JTree(root);
		goodsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		goodsTree.addTreeSelectionListener(this);
		JScrollPane jspTree = new JScrollPane(goodsTree);
		Dimension d = jspTree.getPreferredSize();
		d.width = 300;
		jspTree.setPreferredSize(d);
		
		JLabel lbSearchName = new JLabel("Name");
		JLabel lbSearchBarcode = new JLabel("Barcode");
		JPanel pSearch = new JPanel(new GridBagLayout());
		pSearch.setBorder(BorderFactory.createTitledBorder("Look for"));
		pSearch.add(lbSearchName,	new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		pSearch.add(tfSearchName,	new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		pSearch.add(lbSearchBarcode,new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		pSearch.add(tfSearchBarcode,new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		pSearch.add(btnLookfor,		new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		
		//build Tab
		pCategory1 = new Category1Panel(this);
		pCategory2 = new Category2Panel(this);
		pGoods = new GoodsPanel(this);
		
		tabPane = new JTabbedPane();
		tabPane.add("Category1", pCategory1);
		tabPane.add("Category2", pCategory2);
		tabPane.add("Goods", pGoods);
		
		JPanel pTab = new JPanel(new BorderLayout());
		pTab.add(tabPane, BorderLayout.CENTER);
		pTab.add(lbStatistics, BorderLayout.SOUTH);
		
		JPanel pBottomFunction = new JPanel();
		pBottomFunction.add(btnPackageBind);
		pBottomFunction.add(btnAddGoods);
		
		JPanel pLeft = new JPanel(new BorderLayout());
		pLeft.add(jspTree, BorderLayout.CENTER);
		pLeft.add(pSearch, BorderLayout.NORTH);
		pLeft.add(pBottomFunction, BorderLayout.SOUTH);
		
		this.setLayout(new BorderLayout());
		add(pLeft, BorderLayout.WEST);
		add(pTab, BorderLayout.CENTER);
		
		//build popup menu
		popupmenuRoot.add(menuitemScanImport);
		popupmenuRoot.add(menuitemAddC1);
		popupmenuRoot.add(menuitemRefreshTree);
		popupmenuC1.add(menuitemModifyC1);
		popupmenuC1.add(menuitemAddC2);
		popupmenuC1.add(menuitemDeleteC1);
		popupmenuC2.add(menuitemModifyC2);
		popupmenuC2.add(menuitemAddGoods);
		popupmenuC2.add(menuitemDeleteC2);
		popupmenuGoods.add(menuitemModifyGoods);
		popupmenuGoods.add(menuitemImportGoods);
		popupmenuGoods.add(menuitemUpdateAmountGoods);
		popupmenuGoods.add(menuitemDeleteGoods);
		popupmenuGoods.add(menuitemQuerySoldRecord);
		
		menuitemScanImport.addActionListener(this);
		menuitemAddC1.addActionListener(this);
		menuitemRefreshTree.addActionListener(this);
		menuitemModifyC1.addActionListener(this);
		menuitemAddC2.addActionListener(this);
		menuitemModifyC2.addActionListener(this);
		menuitemAddGoods.addActionListener(this);
		menuitemDeleteC1.addActionListener(this);
		menuitemDeleteC2.addActionListener(this);
		menuitemModifyGoods.addActionListener(this);
		menuitemDeleteGoods.addActionListener(this);
		menuitemImportGoods.addActionListener(this);
		menuitemQuerySoldRecord.addActionListener(this);
		menuitemUpdateAmountGoods.addActionListener(this);
		btnLookfor.addActionListener(this);
		btnPackageBind.addActionListener(this);
		btnAddGoods.addActionListener(this);
		
		goodsTree.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				//show node info is done via valueChanged function, not here
				if (SwingUtilities.isRightMouseButton(e)){
					int row = goodsTree.getClosestRowForLocation(e.getX(), e.getY());
			        goodsTree.setSelectionRow(row);
			        GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
					if (node == null)
						return;
					if (node.toString().equals("root")){
						popupmenuRoot.show(e.getComponent(), e.getX(), e.getY());
					} else if (node.getUserObject() instanceof Category1){
						popupmenuC1.show(e.getComponent(), e.getX(), e.getY());
					} else if (node.getUserObject() instanceof Category2){
						popupmenuC2.show(e.getComponent(), e.getX(), e.getY());
					} else if (node.getUserObject() instanceof Goods){
						popupmenuGoods.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});
		tfSearchBarcode.addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent e) {
        		if (e.getKeyChar() == KeyEvent.VK_ENTER){
        			doLookfor();
        		} 
        	}
		});
		tfSearchName.addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent e) {
        		if (e.getKeyChar() == KeyEvent.VK_ENTER){
        			doLookfor();
        		} 
        	}
		});
	}
	
	private void buildTree(GoodsTreeNode root) {
		int goodsType = 0;//鍟嗗搧绫诲瀷鏁伴噺
		int goodsAmount = 0;//鍟嗗搧鎬诲簱瀛橀噺
		double buyFee = 0; //鍟嗗搧璐拱浠锋牸
		double tradeFee = 0;//鍟嗗搧鎵瑰彂浠锋牸
		double sellFee = 0;//鍟嗗搧鎬婚攢鍞环鏍�
		if (category1s != null){
			for (int i = 0; i < category1s.size(); i++) {
				Category1 c1 = category1s.get(i);
				GoodsTreeNode c1node = new GoodsTreeNode(c1);
				root.add(c1node);
				
				if (c1.getCategory2s() != null){
					for (int j = 0; j < c1.getCategory2s().size(); j++) {
						Category2 c2 = c1.getCategory2s().get(j);
						GoodsTreeNode c2node = new GoodsTreeNode(c2);
						c1node.add(c2node);
						if (c2.getGoods() != null){
							for (int k = 0; k < c2.getGoods().size(); k++) {
								Goods goods = c2.getGoods().get(k);
								GoodsTreeNode goodsNode = new GoodsTreeNode(goods);
								c2node.add(goodsNode);
								goodsType++;
								goodsAmount += goods.getLeftAmount();
								buyFee += goods.getLeftAmount() * goods.getBuyPrice();
								tradeFee += goods.getLeftAmount() * goods.getTradePrice();
								sellFee += goods.getLeftAmount() * goods.getSellPrice();
							}
						}
					}
				}
			}
		}
		lbStatistics.setText("Types: " + goodsType +", Total Amount: "+ goodsAmount 
				+ ", Purchase Fee: $" + String.format(ConstantValue.FORMAT_DOUBLE, buyFee) 
				+ ", Trade Fee: $" + String.format(ConstantValue.FORMAT_DOUBLE, tradeFee) 
				+ ", Sell Price: $" + String.format(ConstantValue.FORMAT_DOUBLE, sellFee));
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
		if (node == null)
			return;
		if (node.getUserObject() instanceof Category1){
			tabPane.setSelectedIndex(0);
			pCategory1.setObjectValue((Category1)node.getUserObject());
		} else if (node.getUserObject() instanceof Category2){
			tabPane.setSelectedIndex(1);
			pCategory2.setObjectValue((Category2)node.getUserObject());
		} else if (node.getUserObject() instanceof Goods){
			tabPane.setSelectedIndex(2);
			pGoods.setObjectValue((Goods)node.getUserObject());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menuitemAddC1){
			Category1Panel p = new Category1Panel(this);
			CommonDialog dlg = new CommonDialog(mainFrame, p, Messages.getString("GoodsMgmtPanel.AddCategory1"), 300, 300);
			dlg.setVisible(true);
		} else if (e.getSource() == menuitemScanImport) {
			final ImportGoodsPanel p = new ImportGoodsPanel(this);
			CommonDialog dlg = new CommonDialog(mainFrame, p, Messages.getString("GoodsMgmtPanel.ImportGoods"), 300, 300);
			dlg.addWindowFocusListener(new WindowAdapter(){
				public void windowGainedFocus(WindowEvent e) {
			        p.putFocusOnBarcode();
			    }
			});
			dlg.setVisible(true);
		} else if (e.getSource() == menuitemRefreshTree){
			mainFrame.reloadListCategory1s();
			this.category1s = mainFrame.getListCategory1s();
			GoodsTreeNode root = (GoodsTreeNode)goodsTree.getModel().getRoot();
			root.removeAllChildren();
			buildTree(root);
			goodsTree.updateUI();
		} else if (e.getSource() == menuitemModifyC1){
			Category1Panel p = new Category1Panel(this);
			GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
			p.setObjectValue((Category1)node.getUserObject());
			CommonDialog dlg = new CommonDialog(mainFrame, p, Messages.getString("GoodsMgmtPanel.ModifyCategory1"), 300, 300);
			dlg.setVisible(true);
		} else if (e.getSource() == menuitemAddC2){
			GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
			Category2Panel p = new Category2Panel(this, (Category1)node.getUserObject());
			CommonDialog dlg = new CommonDialog(mainFrame, p, Messages.getString("GoodsMgmtPanel.AddCategory2"), 300, 400);
			dlg.setVisible(true);
		} else if (e.getSource() == menuitemModifyC2){
			Category2Panel p = new Category2Panel(this);
			GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
			p.setObjectValue((Category2)node.getUserObject());
			CommonDialog dlg = new CommonDialog(mainFrame, p, Messages.getString("GoodsMgmtPanel.ModifyCategory2"), 300, 400);
			dlg.setVisible(true);
		} else if (e.getSource() == menuitemDeleteC1){
			GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
			onDeleteC1(node);
		} else if (e.getSource() == menuitemDeleteC2){
			GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
			onDeleteC2(node);
		} else if (e.getSource() == menuitemAddGoods){
			GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
			GoodsPanel p = new GoodsPanel(this, (Category2)node.getUserObject());
			CommonDialog dlg = new CommonDialog(mainFrame, p, Messages.getString("GoodsMgmtPanel.AddGoods"), 300, 500);
			dlg.setVisible(true);
		} else if (e.getSource() == menuitemModifyGoods){
			GoodsPanel p = new GoodsPanel(this);
			p.hideLeftAmout();//cannot change leftamount in modify
			GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
			p.setObjectValue((Goods)node.getUserObject());
			CommonDialog dlg = new CommonDialog(mainFrame, p, Messages.getString("GoodsMgmtPanel.ModifyGoods"), 300, 500);
			dlg.setVisible(true);
		} else if (e.getSource() == menuitemDeleteGoods){
			GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
			onDeleteGoods(node);
		} else if (e.getSource() == menuitemImportGoods){
			GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
			ImportGoodsPanel p = new ImportGoodsPanel(this, (Goods)node.getUserObject());
			CommonDialog dlg = new CommonDialog(mainFrame, p, Messages.getString("GoodsMgmtPanel.ImportGoods"), 300, 300);
			dlg.setVisible(true);
		} else if (e.getSource() == menuitemUpdateAmountGoods){
			GoodsTreeNode node = (GoodsTreeNode) goodsTree.getLastSelectedPathComponent();
			Goods goods = (Goods)node.getUserObject();
			doChangeGoodsAmount(goods);
		} else if (e.getSource() == btnLookfor){
			doLookfor();
		} else if (e.getSource() == menuitemQuerySoldRecord){
			GoodsTreeNode node = (GoodsTreeNode)goodsTree.getLastSelectedPathComponent();
			Goods goods = (Goods)node.getUserObject();
			HashMap<String, String> params = new HashMap<>();
			params.put("goodsId", goods.getId()+"");
			ArrayList<GoodsSellRecord> records = HttpUtil.loadGoodsSellRecord(mainFrame, mainFrame.SERVER_URL + "indent/querygoodssoldrecord", params);
			if (records == null || records.isEmpty()){
				JOptionPane.showMessageDialog(mainFrame, "Not find record for this goods");
				return;
			}
			GoodsSellRecordDialog dlg = new GoodsSellRecordDialog(mainFrame, "Sold Record - "+ goods.getName(), records, 600, 600);
			dlg.setVisible(true);
		} else if (e.getSource() == btnPackageBind){
			PackageBindMgmtDialog dlg = new PackageBindMgmtDialog(mainFrame);
			dlg.setVisible(true);
		} else if (e.getSource() == btnAddGoods){
			GoodsPanel p = new GoodsPanel(this, null);
			CommonDialog dlg = new CommonDialog(mainFrame, p, Messages.getString("GoodsMgmtPanel.AddGoods"), 300, 500);
			dlg.setVisible(true);
		}
	}
	
	private void doChangeGoodsAmount(Goods goods){
		NumberInputDialog dlg = new NumberInputDialog(mainFrame, "Change Amount", "Please input new amount for goods " +goods.getName(), false);
		dlg.setVisible(true);
		if (dlg.isConfirm){
			Map<String, String> params = new HashMap<>();
			params.put("userId", MainFrame.getLoginUser().getId()+"");
			params.put("id", goods.getId()+"");
			params.put("amount", dlg.inputInteger + "");
			String url = "goods/changeamount";
			String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
			if (response == null){
				logger.error("get null from server for change goods amount. URL = " + url + ", param = "+ params);
				JOptionPane.showMessageDialog(this, "get null from server for change goods amount. URL = " + url);
				return;
			}
			Gson gson = new Gson();
			HttpResult<Goods> result = gson.fromJson(response, new TypeToken<HttpResult<Goods>>(){}.getType());
			if (!result.success){
				logger.error("return false while change goods amount. URL = " + url + ", response = "+response);
				JOptionPane.showMessageDialog(this, result.result);
				return;
			}
			goods.setLeftAmount(dlg.inputInteger);
			this.valueChanged(null);
		}
	}
	/**
	 * Find node as the search condition;
	 * If both name and barcode are input, must match both to find the node;
	 * for barcode, must EQUAL the input value.
	 * for name, just compare part of the name, ignore its capital(no capital sensitive).
	 * if the current selection is 0 or none, look for from the root node;
	 * if the current selection is not 0, then loop the nodes firstly, until find the selection node, then start to compare;
	 * the enumeration using a breadth first loop algorithm.
	 * 
	 * if the result is more, pop up a dialog to show them.
	 */
	private void doLookfor(){
		String name = null;
		String barcode = null;
		if (tfSearchName.getText() != null && tfSearchName.getText().length() > 0)
			name = tfSearchName.getText();
		if (tfSearchBarcode.getText() != null && tfSearchBarcode.getText().length() > 0)
			barcode = tfSearchBarcode.getText();
		if (name == null && barcode == null){
			return;
		}
		//first loop all goods to find if there are more goods match the input condition
		ArrayList<Goods> matchGoods = new ArrayList<>();
		for (int i = 0; i < category1s.size(); i++) {
			Category1 c1 = category1s.get(i);
			if (c1.getCategory2s() != null){
				for (int j = 0; j < c1.getCategory2s().size(); j++) {
					Category2 c2 = c1.getCategory2s().get(j);
					if (c2.getGoods() != null){
						for (int k = 0; k < c2.getGoods().size(); k++) {
							Goods g = c2.getGoods().get(k);
							boolean suitBarcode = false;
							boolean suitName = false;
							if (name == null){
								suitName = true;
							} else {
								if (g.getName().toLowerCase().indexOf(name.toLowerCase()) >= 0){
									suitName = true;
								}
							}
							if (barcode == null){
								suitBarcode = true;
							} else {
								if (g.getBarcode().equals(barcode)){
									suitBarcode = true;
								}
							}
							if (suitName && suitBarcode){
								matchGoods.add(g);
							}
						}
					}
				}
			}
		}
		if (matchGoods.isEmpty()){
			JOptionPane.showMessageDialog(this, "Cannot find goods as the search condition");
		} else if (matchGoods.size() == 1){
			locateNode(matchGoods.get(0));
		} else {
			//pop up a dialog to list the goods
			SearchObjectListDialog dlg = new SearchObjectListDialog(mainFrame, matchGoods, 700, 600);
			dlg.setVisible(true);
			if (dlg.getChoosedGoods() != null){
				locateNode(dlg.getChoosedGoods());
			}
		}
	}
	
	private void locateNode(Goods g){
		GoodsTreeNode root = (GoodsTreeNode)goodsTree.getModel().getRoot();
		Enumeration<GoodsTreeNode> e = root.breadthFirstEnumeration();
		while(e.hasMoreElements()){
			GoodsTreeNode node = e.nextElement();
			if (node.getUserObject() instanceof Goods){
				Goods goods = (Goods)node.getUserObject();
				if (goods.getId() == g.getId()){
					TreePath path = new TreePath(node.getPath());
					goodsTree.setSelectionPath(path);
					goodsTree.scrollPathToVisible(path);
					break;
				}
			}
		}
	}
	
	/**
	 * if root node is null, then insert position = 0
	 * if cannot find any node which sequence more than this one, then insert into the last position
	 * @param c1
	 */
	public void insertNode(Category1 c1){
		GoodsTreeNode root = (GoodsTreeNode)goodsTree.getModel().getRoot();
		int count = root.getChildCount();
		GoodsTreeNode newnode = new GoodsTreeNode(c1);
		if(count == 0){
			root.insert(newnode, 0);
		} else {
			for (int i = 0; i < count; i++) {
				GoodsTreeNode nodei = (GoodsTreeNode)root.getChildAt(i);
				if (((Category1)nodei.getUserObject()).getSequence() > c1.getSequence()){
					root.insert(newnode, i);
					break;//must break, otherwise it will be inserted again for (i == count - 1)
				}
				if (i == count - 1){
					root.insert(newnode, count);
				}
			}
		}
		//refresh UI
		goodsTree.updateUI();
		
		//refresh local data
		mainFrame.reloadListCategory1s();
		category1s = mainFrame.getListCategory1s();
		pCategory2.refreshCategory1List();
		this.valueChanged(null);//refresh the property panel value
	}
	
	/**
	 * find the parent category1 node fist,
	 * if parent node is null children, then insert position = 0
	 * if cannot find any node which sequence more than this one, then insert into the last position
	 * @param c2
	 */
	public void insertNode(Category2 c2){
		GoodsTreeNode root = (GoodsTreeNode)goodsTree.getModel().getRoot();
		TreePath path = this.findPath(root, c2.getCategory1());
		GoodsTreeNode parentnode = (GoodsTreeNode)path.getLastPathComponent();
		int count = parentnode.getChildCount();
		GoodsTreeNode newnode = new GoodsTreeNode(c2);
		if (count == 0){
			parentnode.insert(newnode, 0);
		} else {
			for (int i = 0; i < count; i++) {
				GoodsTreeNode nodei = (GoodsTreeNode)parentnode.getChildAt(i);
				if(((Category2)nodei.getUserObject()).getSequence() > c2.getSequence()){
					parentnode.insert(newnode, i);
					break;//must break, otherwise it will be inserted again for (i == count - 1)
				}
				if (i == count - 1){
					parentnode.insert(newnode, count);
				}
			}
		}
		
		//refresh UI
		goodsTree.updateUI();
		
		//refresh local data
		mainFrame.reloadListCategory1s();
		category1s = mainFrame.getListCategory1s();
		pGoods.refreshCategory2List();
		this.valueChanged(null);//refresh the property panel value
	}
	
	/**
	 * find the parent category2 node fist,
	 * if parent node is null children, then insert position = 0
	 * if cannot find any node which sequence more than this one, then insert into the last position
	 * @param dish
	 */
	public void insertNode(Goods goods){
		GoodsTreeNode root = (GoodsTreeNode)goodsTree.getModel().getRoot();
		TreePath path = findPath(root, goods.getCategory2());
		GoodsTreeNode parentnode = (GoodsTreeNode)path.getLastPathComponent();
		int count = parentnode.getChildCount();
		GoodsTreeNode newnode = new GoodsTreeNode(goods);
		if (count == 0){
			parentnode.insert(newnode, 0);
		} else {
			for (int i = 0; i < count; i++) {
				GoodsTreeNode nodei = (GoodsTreeNode)parentnode.getChildAt(i);
				if (((Goods)nodei.getUserObject()).getSequence() > goods.getSequence()){
					parentnode.insert(newnode, i);
					break;//must break, otherwise it will be inserted again for (i == count - 1)
				}
				if (i == count -1){
					parentnode.insert(newnode, count);
				}
			}
		}
		
		//refresh UI
		goodsTree.updateUI();
		//refresh local data
		mainFrame.reloadListCategory1s();
		category1s = mainFrame.getListCategory1s();
		this.valueChanged(null);//refresh the property panel value
	}
	
	/**
	 * find the node then reset its userobject
	 * if sequence changed, then delete it from parent, and insert again
	 * @param c1
	 * @param origin
	 */
	public void updateNode(Category1 c1, Category1 origin){
		GoodsTreeNode root = (GoodsTreeNode)goodsTree.getModel().getRoot();
		TreePath path = findPath(root, c1);
		GoodsTreeNode node = (GoodsTreeNode)path.getLastPathComponent();
		node.setUserObject(c1);
		((DefaultTreeModel)goodsTree.getModel()).reload(node);//without reload, JTree can truncate the name as (...) if it is longer than before
		if (c1.getSequence() != origin.getSequence()){
			root.remove(node);
			int count = root.getChildCount();
			if (count == 0){
				root.insert(node, 0);
			} else {
				for (int i = 0; i < count; i++) {
					GoodsTreeNode nodei = (GoodsTreeNode)root.getChildAt(i);
					if (((Category1)nodei.getUserObject()).getSequence() > c1.getSequence()){
						root.insert(node, i);
						break;//must break, otherwise it will be inserted again for (i == count - 1)
					}
					if (i == count - 1){
						root.insert(node, count);
					}
				}
			}
		}
		
		// refresh UI
		goodsTree.updateUI();
		
		// refresh local data
		mainFrame.reloadListCategory1s();
		category1s = mainFrame.getListCategory1s();
		pCategory2.refreshCategory1List();
		this.valueChanged(null);//refresh the property panel value
	}
	
	/**
	 * find the node and reset the userobject;
	 * if sequence/parent changed, then delete it from parent node and then insert again
	 * @param c2
	 * @param origin
	 */
	public void updateNode(Category2 c2, Category2 origin){
		GoodsTreeNode root = (GoodsTreeNode)goodsTree.getModel().getRoot();
		TreePath path = findPath(root, c2);
		GoodsTreeNode node = (GoodsTreeNode)path.getLastPathComponent();
		node.setUserObject(c2);
		((DefaultTreeModel)goodsTree.getModel()).reload(node);//without reload, JTree can truncate the name as (...) if it is longer than before
		if (c2.getSequence() != origin.getSequence() || c2.getCategory1().getId() != origin.getCategory1().getId()){
			((GoodsTreeNode)node.getParent()).remove(node);
			path = findPath(root, c2.getCategory1());
			GoodsTreeNode parentnode = (GoodsTreeNode)path.getLastPathComponent();
			int count = parentnode.getChildCount();
			if (count == 0){
				parentnode.insert(node,  0);
			} else {
				for (int i = 0; i < count; i++) {
					GoodsTreeNode nodei = (GoodsTreeNode)parentnode.getChildAt(i);
					if (((Category2)nodei.getUserObject()).getSequence() > c2.getSequence()){
						parentnode.insert(node, i);
						break;//must break, otherwise it will be inserted again for (i == count - 1)
					}
					if (i == count - 1){
						parentnode.insert(node, count);
					}
				}
			}
		}
		
		// refresh UI
		goodsTree.updateUI();
		// refresh local data
		mainFrame.reloadListCategory1s();
		category1s = mainFrame.getListCategory1s();
		pGoods.refreshCategory2List();
		this.valueChanged(null);//refresh the property panel value
	}
	
	/**
	 * find the node and reset the userobject;
	 * if sequence/parent changed, then delete it from parent node and then insert again
	 * @param dish
	 * @param origin
	 */
	public void updateNode(Goods goods, Goods origin){
		GoodsTreeNode root = (GoodsTreeNode)goodsTree.getModel().getRoot();
		TreePath path = findPath(root, goods);
		GoodsTreeNode node = (GoodsTreeNode)path.getLastPathComponent();
		node.setUserObject(goods);
		((DefaultTreeModel)goodsTree.getModel()).reload(node);//without reload, JTree can truncate the name as (...) if it is longer than before
		if (goods.getSequence() != origin.getSequence() || goods.getCategory2().getId() != origin.getCategory2().getId()){
			((GoodsTreeNode)node.getParent()).remove(node);
			path = findPath(root, goods.getCategory2());
			GoodsTreeNode parentnode = (GoodsTreeNode)path.getLastPathComponent();
			int count = parentnode.getChildCount();
			if (count == 0){
				parentnode.insert(node, 0);
			} else {
				for (int i = 0; i < count; i++) {
					GoodsTreeNode nodei = (GoodsTreeNode)parentnode.getChildAt(i);
					if (((Goods)nodei.getUserObject()).getSequence() > goods.getSequence()){
						parentnode.insert(node, i);
						break;//must break, otherwise it will be inserted again for (i == count - 1)
					}
					if (i == count - 1){
						parentnode.insert(node, count);
					}
				}
			}
		}
		this.valueChanged(null);
		// refresh UI
		goodsTree.updateUI();
		// refresh local data
		mainFrame.reloadListCategory1s();
		category1s = mainFrame.getListCategory1s();
		this.valueChanged(null);//refresh the property panel value
	}
	
	private void onDeleteC1(GoodsTreeNode node){
		if (goodsTree.getModel().getChildCount(node) > 0){
			JOptionPane.showMessageDialog(this, "There are sub menu in this node, please delete them first.");
			return;
		}
		Category1 c1 = (Category1)node.getUserObject();
		if (JOptionPane.showConfirmDialog(this, "Do you want to delete this node : "+ c1.getName(), "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			return;
		}
		
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("id", c1.getId()+"");
		String url = "goods/delete_category1";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for delete category1. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for delete category1. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<HashMap<String, String>> result = gson.fromJson(response, new TypeToken<HttpResult<HashMap<String, String>>>(){}.getType());
		if (!result.success){
			logger.error("return false while delete category1. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		((DefaultTreeModel)goodsTree.getModel()).removeNodeFromParent(node);
		// refresh local data
		mainFrame.reloadListCategory1s();
		category1s = mainFrame.getListCategory1s();
		pCategory2.refreshCategory1List();
	}

	private void onDeleteC2(GoodsTreeNode node){
		if (goodsTree.getModel().getChildCount(node) > 0){
			JOptionPane.showMessageDialog(this, "There are children in this node, please delete them first.");
			return;
		}
		Category2 c2 = (Category2)node.getUserObject();
		if (JOptionPane.showConfirmDialog(this, "Do you want to delete this node : "+ c2.getName(), "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			return;
		}
		
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("id", c2.getId()+"");
		String url = "goods/delete_category2";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for delete category2. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for delete category2. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<HashMap<String, String>> result = gson.fromJson(response, new TypeToken<HttpResult<HashMap<String, String>>>(){}.getType());
		if (!result.success){
			logger.error("return false while delete category2. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		((DefaultTreeModel)goodsTree.getModel()).removeNodeFromParent(node);
		// refresh local data
		mainFrame.reloadListCategory1s();
		category1s = mainFrame.getListCategory1s();
	}
	
	private void onDeleteGoods(GoodsTreeNode node){
		Goods goods = (Goods)node.getUserObject();
		if (JOptionPane.showConfirmDialog(this, "Do you want to delete this node : "+ goods.getName(), "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
			return;
		}
		
		Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId()+"");
		params.put("id", goods.getId()+"");
		String url = "goods/delete_goods";
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
		if (response == null){
			logger.error("get null from server for delete goods. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for delete goods. URL = " + url);
			return;
		}
		Gson gson = new Gson();
		HttpResult<HashMap<String, String>> result = gson.fromJson(response, new TypeToken<HttpResult<HashMap<String, String>>>(){}.getType());
		if (!result.success){
			logger.error("return false while delete goods. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		((DefaultTreeModel)goodsTree.getModel()).removeNodeFromParent(node);
		// refresh local data
		mainFrame.reloadListCategory1s();
		category1s = mainFrame.getListCategory1s();
	}

	public ArrayList<Category1> getCategory1s() {
		return category1s;
	}

	public MainFrame getMainFrame() {
		return mainFrame;
	}
	
	private TreePath findPath(GoodsTreeNode root, Object o) {
	    @SuppressWarnings("unchecked")
	    Enumeration<GoodsTreeNode> e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			GoodsTreeNode node = e.nextElement();
			if (node.getUserObject().getClass().getName().equals(o.getClass().getName())) {
				// since Category1, Category2, Dish use id to do equal, so here can equal them directly
				if (o.equals(node.getUserObject()))
					return new TreePath(node.getPath());
			}
		}
	    return null;
	}
}
