package com.shuishou.sysmgr.ui.statistics;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.Messages;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.StatItem;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.JDatePicker;
import com.shuishou.sysmgr.ui.components.WaitDialog;

public class StatisticsPanel extends JPanel implements ActionListener{
	private final Logger logger = Logger.getLogger(StatisticsPanel.class.getName());
	private Gson gson = new Gson();
	private static final String CARDLAYOUT_PAYWAY = "PAYWAY";
	private static final String CARDLAYOUT_SELL = "SELL";
	private static final String CARDLAYOUT_SELLPERIOD = "SELLPERIOD";
	private MainFrame mainFrame;
	
	private JDatePicker dpStartDate = new JDatePicker();
	private JDatePicker dpEndDate = new JDatePicker();
	private JRadioButton rbPayway = new JRadioButton(Messages.getString("StatisticsPanel.Payway"));
	private JRadioButton rbSell = new JRadioButton(Messages.getString("StatisticsPanel.Sell"));
	private JRadioButton rbPeriodSell = new JRadioButton(Messages.getString("StatisticsPanel.PeriodSell"));
	private JRadioButton rbSellByGoods = new JRadioButton(Messages.getString("StatisticsPanel.ByGoods"));
	private JRadioButton rbSellByCategory1 = new JRadioButton(Messages.getString("StatisticsPanel.ByCategory1"));
	private JRadioButton rbSellByCategory2 = new JRadioButton(Messages.getString("StatisticsPanel.ByCategory2"));
	private JRadioButton rbSellByPeriodPerDay = new JRadioButton(Messages.getString("StatisticsPanel.PerDay"));
	private JRadioButton rbSellByPeriodPerHour = new JRadioButton(Messages.getString("StatisticsPanel.PerHour"));
	private JRadioButton rbSellByPeriodPerWeek = new JRadioButton(Messages.getString("StatisticsPanel.PerWeek"));
	private JRadioButton rbSellByPeriodPerMonth = new JRadioButton(Messages.getString("StatisticsPanel.PerMonth"));
	private JCheckBox cbHideEmptyPeriod = new JCheckBox(Messages.getString("StatisticsPanel.HideEmpty"), true);
	private JButton btnToday = new JButton(Messages.getString("StatisticsPanel.Today"));
	private JButton btnYesterday = new JButton(Messages.getString("StatisticsPanel.Yesterday"));
	private JButton btnThisWeek = new JButton(Messages.getString("StatisticsPanel.Thisweek"));
	private JButton btnLastWeek = new JButton(Messages.getString("StatisticsPanel.Lastweek"));
	private JButton btnThisMonth = new JButton(Messages.getString("StatisticsPanel.Thismonth"));
	private JButton btnLastMonth = new JButton(Messages.getString("StatisticsPanel.Lastmonth"));
	private JButton btnQuery = new JButton(Messages.getString("StatisticsPanel.Query"));
	private JButton btnExportExcel = new JButton(Messages.getString("StatisticsPanel.Export"));
	private JTable tabReport = new JTable();
	private JPanel pDimensionParam = new JPanel(new CardLayout());
	private JPanel pChart = new JPanel(new GridLayout(0, 1));
	private JLabel lbTotalInfo = new JLabel();
	private IntComparator intComp = new IntComparator();
	private DoubleComparator doubleComp = new DoubleComparator();
	private StringComparator stringComp = new StringComparator();
	
	public StatisticsPanel(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		initUI();
	}
	
	private void initUI(){
		tabReport.setAutoCreateRowSorter(false);
		JPanel pReport = new JPanel(new BorderLayout());
		JScrollPane jspTable = new JScrollPane(tabReport, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel pData = new JPanel(new BorderLayout());
		pData.add(jspTable, BorderLayout.CENTER);
		pData.add(lbTotalInfo, BorderLayout.SOUTH);
		JScrollPane jspChart = new JScrollPane(pChart, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pReport.add(pData, BorderLayout.WEST);
		pReport.add(jspChart, BorderLayout.CENTER);
		
		ButtonGroup bgDimension = new ButtonGroup();
		bgDimension.add(rbPayway);
		bgDimension.add(rbSell);
		bgDimension.add(rbPeriodSell);
		
		ButtonGroup bgSellGranularity = new ButtonGroup();
		bgSellGranularity.add(rbSellByCategory1);
		bgSellGranularity.add(rbSellByCategory2);
		bgSellGranularity.add(rbSellByGoods);
		rbSellByGoods.setSelected(true);
		ButtonGroup bgSellPeriod = new ButtonGroup();
		bgSellPeriod.add(rbSellByPeriodPerDay);
		bgSellPeriod.add(rbSellByPeriodPerHour);
		bgSellPeriod.add(rbSellByPeriodPerWeek);
		bgSellPeriod.add(rbSellByPeriodPerMonth);
		rbSellByPeriodPerDay.setSelected(true);
		
		JPanel pSellGranularity = new JPanel(new GridLayout(0, 1));
		pSellGranularity.setBorder(BorderFactory.createTitledBorder(Messages.getString("StatisticsPanel.SellGranularity")));
		pSellGranularity.add(rbSellByGoods);
		pSellGranularity.add(rbSellByCategory2);
		pSellGranularity.add(rbSellByCategory1);
		
		JPanel pSellByPeriod = new JPanel(new GridBagLayout());
		pSellByPeriod.setBorder(BorderFactory.createTitledBorder(Messages.getString("StatisticsPanel.SellByPeroid")));
		pSellByPeriod.add(rbSellByPeriodPerDay, 	new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
		pSellByPeriod.add(rbSellByPeriodPerHour, 	new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
		pSellByPeriod.add(rbSellByPeriodPerWeek, 	new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
		pSellByPeriod.add(rbSellByPeriodPerMonth, 	new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
		pSellByPeriod.add(cbHideEmptyPeriod,		new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
		
		pDimensionParam.add(new JLabel(), CARDLAYOUT_PAYWAY);
		pDimensionParam.add(pSellGranularity, CARDLAYOUT_SELL);
		pDimensionParam.add(pSellByPeriod, CARDLAYOUT_SELLPERIOD);
		rbPayway.setSelected(true);
		((CardLayout)pDimensionParam.getLayout()).show(pDimensionParam, CARDLAYOUT_PAYWAY);
		
		JPanel pDimension = new JPanel(new GridBagLayout());
		pDimension.setBorder(BorderFactory.createTitledBorder(Messages.getString("StatisticsPanel.StatisticsDimension")));
		pDimension.add(rbPayway, 		new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pDimension.add(rbSell, 			new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pDimension.add(rbPeriodSell, 	new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		pDimension.add(pDimensionParam,	new GridBagConstraints(1, 0, 1, 3, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		
		JPanel pQueryTimeButton = new JPanel();
		pQueryTimeButton.add(btnToday,		new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTimeButton.add(btnYesterday,	new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTimeButton.add(btnThisWeek,	new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTimeButton.add(btnLastWeek,	new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTimeButton.add(btnThisMonth,	new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTimeButton.add(btnLastMonth,	new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		
		JLabel lbStartDate = new JLabel(Messages.getString("StatisticsPanel.StartDate"));
		JLabel lbEndDate = new JLabel(Messages.getString("StatisticsPanel.EndDate"));
		
		JPanel pQueryTime = new JPanel(new GridBagLayout());
		pQueryTime.add(lbStartDate,	new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTime.add(dpStartDate,	new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTime.add(lbEndDate,	new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTime.add(dpEndDate,	new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pQueryTime.add(pQueryTimeButton,	new GridBagConstraints(0, 1, 4, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		
		JPanel pQuery = new JPanel(new GridLayout(0, 1, 0, 20));
		pQuery.add(btnQuery);
		pQuery.add(btnExportExcel);
		JPanel pCondition = new JPanel(new GridBagLayout());
		pCondition.add(pQueryTime,	new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(pDimension,	new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(pQuery,		new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		pCondition.add(new JLabel(),new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
		setLayout(new BorderLayout());
		add(pReport, BorderLayout.CENTER);
		add(pCondition, BorderLayout.NORTH);
		
		btnQuery.addActionListener(this);
		btnToday.addActionListener(this);
		btnYesterday.addActionListener(this);
		btnThisWeek.addActionListener(this);
		btnLastWeek.addActionListener(this);
		btnThisMonth.addActionListener(this);
		btnLastMonth.addActionListener(this);
		rbPayway.addActionListener(this);
		rbSell.addActionListener(this);
		rbPeriodSell.addActionListener(this);
		btnExportExcel.addActionListener(this);
	}

	private void doQuery(){
		if (!dpStartDate.getModel().isSelected() || !dpEndDate.getModel().isSelected()){
			JOptionPane.showMessageDialog(mainFrame, Messages.getString("StatisticsPanel.MustChooseDate"));
			return;
		}
		String url = "statistics/statistics";
		final Map<String, String> params = new HashMap<>();
		params.put("userId", MainFrame.getLoginUser().getId() + "");
		if (dpStartDate.getModel() != null && dpStartDate.getModel().getValue() != null){
			Calendar c = (Calendar)dpStartDate.getModel().getValue();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			params.put("startDate", ConstantValue.DFYMDHMS.format(c.getTime()));
		}
		if (dpEndDate.getModel() != null && dpEndDate.getModel().getValue() != null){
			Calendar c = (Calendar)dpEndDate.getModel().getValue();
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			params.put("endDate", ConstantValue.DFYMDHMS.format(c.getTime()));
		}
		if (rbPayway.isSelected()){
			params.put("statisticsDimension", ConstantValue.STATISTICS_DIMENSTION_PAYWAY+"");
		} else if (rbSell.isSelected()){
			params.put("statisticsDimension", ConstantValue.STATISTICS_DIMENSTION_SELL+"");
			if (rbSellByGoods.isSelected()){
				params.put("sellGranularity", ConstantValue.STATISTICS_SELLGRANULARITY_BYGOODS+"");
			} else if (rbSellByCategory1.isSelected()){
				params.put("sellGranularity", ConstantValue.STATISTICS_SELLGRANULARITY_BYCATEGORY1+"");
			} else if (rbSellByCategory2.isSelected()){
				params.put("sellGranularity", ConstantValue.STATISTICS_SELLGRANULARITY_BYCATEGORY2+"");
			}
		} else if (rbPeriodSell.isSelected()){
			params.put("statisticsDimension", ConstantValue.STATISTICS_DIMENSTION_PERIODSELL+"");
			if (rbSellByPeriodPerDay.isSelected()){
				params.put("sellByPeriod", ConstantValue.STATISTICS_PERIODSELL_PERDAY+"");
			} else if (rbSellByPeriodPerHour.isSelected()){
				params.put("sellByPeriod", ConstantValue.STATISTICS_PERIODSELL_PERHOUR+"");
			} else if (rbSellByPeriodPerWeek.isSelected()){
				params.put("sellByPeriod", ConstantValue.STATISTICS_PERIODSELL_PERWEEK+"");
			} else if (rbSellByPeriodPerMonth.isSelected()){
				params.put("sellByPeriod", ConstantValue.STATISTICS_PERIODSELL_PERMONTH+"");
			}
		}
		final String finalurl = url;
		WaitDialog wdlg = new WaitDialog(mainFrame, "Collecting data..."){
			public Object work() {
				return HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + finalurl, params);
			}
		};
		String response = (String)wdlg.getReturnResult();
		if (response == null){
			logger.error("get null from server for statistics. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for statistics. URL = " + url);
			return;
		}
		HttpResult<ArrayList<StatItem>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<StatItem>>>(){}.getType());
		if (!result.success){
			logger.error("return false while statistics. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		if (rbPayway.isSelected()){
			AbstractTableModel model = new StatPaywayModel(result.data);
			tabReport.setModel(model);
			TableRowSorter trs = new TableRowSorter(model);
			trs.setComparator(0, stringComp);
			trs.setComparator(1, doubleComp);
			trs.setComparator(2, intComp);
			tabReport.setRowSorter(trs);
			
			tabReport.setAutoCreateRowSorter(false);
			showPaywayChart(result.data);
			double totalMoney = 0;
			int totalAmount = 0;
			for (int i = 0; i < result.data.size(); i++) {
				totalMoney += result.data.get(i).paidPrice;
				totalAmount += result.data.get(i).soldAmount;
			}
			lbTotalInfo.setText("record: " + tabReport.getRowCount()
					+ ", money: $" + String.format("%.2f", totalMoney) + ", amount: " + totalAmount
					+ ", average money: $" + String.format("%.2f", totalMoney / tabReport.getRowCount())
					+ ", average amount: " + String.format("%.2f", (double)(totalAmount / tabReport.getRowCount())));
		} else if (rbSell.isSelected()){
			AbstractTableModel model = new StatSellModel(result.data);
			tabReport.setModel(model);
			TableRowSorter trs = new TableRowSorter(model);
			trs.setComparator(0, stringComp);
			trs.setComparator(1, doubleComp);
			trs.setComparator(2, intComp);
			tabReport.setRowSorter(trs);
			
			tabReport.setAutoCreateRowSorter(false);
			showSellChart(result.data);
			double totalPrice = 0;
			int totalAmount = 0;
			double totalWeight = 0;
			for (int i = 0; i < result.data.size(); i++) {
				totalPrice += result.data.get(i).totalPrice;
				totalAmount += result.data.get(i).soldAmount;
				totalWeight += result.data.get(i).weight;
			}
			lbTotalInfo.setText("record: " + tabReport.getRowCount()
					+ ", money: $" + String.format("%.2f", totalPrice) + ", amount: " + totalAmount
					+ ", average money: $" + String.format("%.2f", totalPrice / tabReport.getRowCount())
					+ ", average amount: " + String.format("%.2f", (double)(totalAmount / tabReport.getRowCount())));
		} else if (rbPeriodSell.isSelected()){
			ArrayList<StatItem> sis = result.data;
			if (cbHideEmptyPeriod.isSelected()){
				for(int i = sis.size() - 1; i>= 0; i--){
					if (sis.get(i).soldAmount == 0){
						sis.remove(i);
					}
				}
			}
			AbstractTableModel model = new StatPeriodSellModel(sis);
			tabReport.setModel(model);
			tabReport.getColumnModel().getColumn(0).setPreferredWidth(250);
			TableRowSorter trs = new TableRowSorter(model);
			trs.setComparator(0, stringComp);
			trs.setComparator(1, doubleComp);
			trs.setComparator(2, intComp);
			tabReport.setRowSorter(trs);
			tabReport.setAutoCreateRowSorter(false);
			tabReport.getRowSorter().toggleSortOrder(0);
			showPeriodSellChart(result.data);
			double totalMoney = 0;
			int totalAmount = 0;
			double totalWeight = 0;
			for (int i = 0; i < result.data.size(); i++) {
				totalMoney += result.data.get(i).paidPrice;
				totalAmount += result.data.get(i).soldAmount;
				totalWeight += result.data.get(i).weight;
			}
			lbTotalInfo.setText("record: " + tabReport.getRowCount()
					+ ", money: $" + String.format("%.2f", totalMoney) + ", amount: " + totalAmount
					+ ", average money: $" + String.format("%.2f", totalMoney / tabReport.getRowCount())
					+ ", average amount: " + String.format("%.2f", (double)(totalAmount / tabReport.getRowCount())));
		}
	}
	
	private void showPaywayChart(ArrayList<StatItem> items){
		//创建主题样式  
        StandardChartTheme mChartTheme = new StandardChartTheme("CN");  
        //设置标题字体  
        mChartTheme.setExtraLargeFont(new Font("黑体", Font.BOLD, 20));  
        //设置轴向字体  
        mChartTheme.setLargeFont(new Font("宋体", Font.CENTER_BASELINE, 15));  
        //设置图例字体  
        mChartTheme.setRegularFont(new Font("宋体", Font.CENTER_BASELINE, 15));  
        //应用主题样式  
        ChartFactory.setChartTheme(mChartTheme);  
		pChart.removeAll();
		JTabbedPane tab = new JTabbedPane();
		
		DefaultPieDataset pieMoneyDataset = new DefaultPieDataset();
		DefaultPieDataset pieAmountDataset = new DefaultPieDataset();
		DefaultCategoryDataset barMoneyDataset = new DefaultCategoryDataset();
		DefaultCategoryDataset barAmountDataset = new DefaultCategoryDataset();
		for (int i = 0; i < items.size(); i++) {
			StatItem si = items.get(i);
			pieMoneyDataset.setValue(si.itemName, si.paidPrice);
			pieAmountDataset.setValue(si.itemName, si.soldAmount);
			barMoneyDataset.setValue(si.paidPrice, si.itemName, "");
			barAmountDataset.setValue(si.soldAmount, si.itemName, "");
		}
		JFreeChart pie_money = ChartFactory.createPieChart("", pieMoneyDataset,true, true, false);
		JFreeChart pie_amount = ChartFactory.createPieChart("", pieAmountDataset, true, true, false);
		JFreeChart bar_money = ChartFactory.createBarChart("", "", "", barMoneyDataset);
		JFreeChart bar_amount = ChartFactory.createBarChart("", "", "", barAmountDataset);
		tab.addTab("Money - Pie", new ChartPanel(pie_money));
		tab.addTab("Money - Bar", new ChartPanel(bar_money));
		tab.addTab("Amount - Pie", new ChartPanel(pie_amount));
		tab.addTab("Amount - Bar", new ChartPanel(bar_amount));
		pChart.add(tab);
		pChart.updateUI();
	}
	
	private void showSellChart(ArrayList<StatItem> items){
		//创建主题样式  
        StandardChartTheme mChartTheme = new StandardChartTheme("CN");  
        //设置标题字体  
        mChartTheme.setExtraLargeFont(new Font("黑体", Font.BOLD, 20));  
        //设置轴向字体  
        mChartTheme.setLargeFont(new Font("宋体", Font.CENTER_BASELINE, 15));  
        //设置图例字体  
        mChartTheme.setRegularFont(new Font("宋体", Font.CENTER_BASELINE, 15));  
        //应用主题样式  
        ChartFactory.setChartTheme(mChartTheme);  
		pChart.removeAll();
		JTabbedPane tab = new JTabbedPane();
		
		DefaultPieDataset pieMoneyDataset = new DefaultPieDataset();
		DefaultCategoryDataset barMoneyDataset = new DefaultCategoryDataset();
		DefaultPieDataset pieAmountDataset = new DefaultPieDataset();
		DefaultCategoryDataset barAmountDataset = new DefaultCategoryDataset();
		for (int i = 0; i < items.size(); i++) {
			StatItem si = items.get(i);
			pieMoneyDataset.setValue(si.itemName, si.totalPrice);
			barMoneyDataset.setValue(si.totalPrice, si.itemName, "");
			pieAmountDataset.setValue(si.itemName, si.soldAmount);
			barAmountDataset.setValue(si.soldAmount, si.itemName, "");
		}
		JFreeChart pie_money = ChartFactory.createPieChart("", pieMoneyDataset,true, true, false);
		JFreeChart bar_money = ChartFactory.createBarChart("", "goods", "sold", barMoneyDataset);
		JFreeChart pie_amount = ChartFactory.createPieChart("", pieAmountDataset,true, true, false);
		JFreeChart bar_amount = ChartFactory.createBarChart("", "goods", "sold", barAmountDataset);
		pie_money.removeLegend();
		bar_money.removeLegend();
		pie_amount.removeLegend();
		bar_amount.removeLegend();
		tab.addTab("Money - Pie", new ChartPanel(pie_money));
		tab.addTab("Money - Bar", new ChartPanel(bar_money));
		tab.addTab("Amount - Pie", new ChartPanel(pie_amount));
		tab.addTab("Amount - Bar", new ChartPanel(bar_amount));
		pChart.add(tab);
		pChart.updateUI();
	}
	
	private void showPeriodSellChart(ArrayList<StatItem> items){
		//创建主题样式  
        StandardChartTheme mChartTheme = new StandardChartTheme("CN");  
        //设置标题字体  
        mChartTheme.setExtraLargeFont(new Font("黑体", Font.BOLD, 20));  
        //设置轴向字体  
        mChartTheme.setLargeFont(new Font("宋体", Font.CENTER_BASELINE, 15));  
        //设置图例字体  
        mChartTheme.setRegularFont(new Font("宋体", Font.CENTER_BASELINE, 15));  
        //应用主题样式  
        ChartFactory.setChartTheme(mChartTheme);  
		pChart.removeAll();
		JTabbedPane tab = new JTabbedPane();
		
		DefaultPieDataset pieMoneyDataset = new DefaultPieDataset();
		DefaultCategoryDataset barMoneyDataset = new DefaultCategoryDataset();
		DefaultPieDataset pieAmountDataset = new DefaultPieDataset();
		DefaultCategoryDataset barAmountDataset = new DefaultCategoryDataset();
		for (int i = 0; i < items.size(); i++) {
			StatItem si = items.get(i);
			pieMoneyDataset.setValue(si.itemName, si.totalPrice);
			barMoneyDataset.setValue(si.totalPrice, si.itemName, "");
			pieAmountDataset.setValue(si.itemName, si.soldAmount);
			barAmountDataset.setValue(si.soldAmount, si.itemName, "");
		}
		JFreeChart pie_money = ChartFactory.createPieChart("", pieMoneyDataset,true, true, false);
		JFreeChart bar_money = ChartFactory.createBarChart("", "goods", "sold", barMoneyDataset);
		JFreeChart pie_amount = ChartFactory.createPieChart("", pieAmountDataset,true, true, false);
		JFreeChart bar_amount = ChartFactory.createBarChart("", "goods", "sold", barAmountDataset);
		pie_money.removeLegend();
		bar_money.removeLegend();
		pie_amount.removeLegend();
		bar_amount.removeLegend();
		tab.addTab("Money - Pie", new ChartPanel(pie_money));
		tab.addTab("Money - Bar", new ChartPanel(bar_money));
		tab.addTab("Amount - Pie", new ChartPanel(pie_amount));
		tab.addTab("Amount - Bar", new ChartPanel(bar_amount));
		pChart.add(tab);
		pChart.updateUI();
	}
	
	private void doExport(){
		if (tabReport.getRowCount() == 0)
			return;
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(mainFrame);
		if (returnVal != JFileChooser.APPROVE_OPTION){
			return;
		}
		HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("stat");
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i< tabReport.getColumnCount(); i++){
        	HSSFCell cell = row.createCell(i);
        	cell.setCellValue(tabReport.getColumnName(i));
        }
        for (int i = 0; i < tabReport.getRowCount(); i++) {
			HSSFRow rowi = sheet.createRow(i+1);
			for (int j = 0; j < tabReport.getColumnCount(); j++) {
				HSSFCell cell = rowi.createCell(j);
				cell.setCellValue(String.valueOf(tabReport.getValueAt(i, j)));
			}
		}
        FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fc.getSelectedFile());
			workbook.write(outputStream);
	        workbook.close();
		} catch (IOException e) {
			logger.error("", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
		} finally{
			if (outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error("", e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				}
		}
        
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnQuery){
			doQuery();
		} else if (e.getSource() == btnExportExcel){
			doExport();
		} else if (e.getSource() == btnToday){
			Calendar c = Calendar.getInstance();
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		} else if (e.getSource() == btnYesterday){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		} else if (e.getSource() == btnThisWeek){
			Calendar c = Calendar.getInstance(Locale.CHINA);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		} else if (e.getSource() == btnLastWeek){
			Calendar c = Calendar.getInstance(Locale.CHINA);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 7);
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		}  else if (e.getSource() == btnThisMonth){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, 1);
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		} else if (e.getSource() == btnLastMonth){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
			c.set(Calendar.DAY_OF_MONTH, 1);
//			dpStartDate.setDateTime(c);
			dpStartDate.getModel().setYear(c.get(Calendar.YEAR));
			dpStartDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpStartDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpStartDate.getModel().setSelected(true);
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setYear(c.get(Calendar.YEAR));
			dpEndDate.getModel().setMonth(c.get(Calendar.MONTH));
			dpEndDate.getModel().setDay(c.get(Calendar.DAY_OF_MONTH));
			dpEndDate.getModel().setSelected(true);
		} else if (e.getSource() == rbPayway){
			((CardLayout)pDimensionParam.getLayout()).show(pDimensionParam, CARDLAYOUT_PAYWAY);
		} else if (e.getSource() == rbSell){
			((CardLayout)pDimensionParam.getLayout()).show(pDimensionParam, CARDLAYOUT_SELL);
		} else if (e.getSource() == rbPeriodSell){
			((CardLayout)pDimensionParam.getLayout()).show(pDimensionParam, CARDLAYOUT_SELLPERIOD);
		}
	}
	
	class IntComparator implements Comparator{

		@Override
		public int compare(Object o1, Object o2) {
			if (o1 instanceof Integer && o2 instanceof Integer){
				return ((Integer)o1).compareTo((Integer)o2);
			}
			return 0;
		}
		
	}
	
	class DoubleComparator implements Comparator{

		@Override
		public int compare(Object o1, Object o2) {
			if (o1 instanceof Double && o2 instanceof Double){
				return ((Double)o1).compareTo((Double)o2);
			}
			return 0;
		}
		
	}
	
	class StringComparator implements Comparator{

		@Override
		public int compare(Object o1, Object o2) {
			if (o1 instanceof String && o2 instanceof String){
				return ((String)o1).compareTo((String)o2);
			}
			return 0;
		}
		
	}
}
