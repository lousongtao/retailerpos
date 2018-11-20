package com.shuishou.sysmgr.ui.member;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.beans.GoodsSellRecord;
import com.shuishou.sysmgr.beans.MemberScore;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.goods.GoodsSellRecordDialog;

public class MemberScoreRecordDialog extends JDialog{
	private final Logger logger = Logger.getLogger(MemberScoreRecordDialog.class.getName());
	private JTable table = new JTable();
	private RecordTableModel model = new RecordTableModel();
	private MainFrame parent;
	
	public MemberScoreRecordDialog(MainFrame parent, String title, ArrayList<MemberScore> records, int width, int height){
		setTitle(title);
		setSize(width, height);
		this.setModal(true);
		this.parent = parent;
		initUI(records);
		
	}
	
	private void initUI(ArrayList<MemberScore> records){
		model.setData(records);
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
		table.getColumnModel().getColumn(4).setPreferredWidth(150);
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JButton btnCancel = new JButton("Close");
		JPanel pButton = new JPanel();
		pButton.add(btnCancel);
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(jspTable, BorderLayout.CENTER);
		c.add(pButton, BorderLayout.SOUTH);
		setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
				(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
		btnCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MemberScoreRecordDialog.this.setVisible(false);
			}});
	}
	
	class RecordTableModel extends DefaultTableModel{
		private ArrayList<MemberScore> items = new ArrayList<>();
		private String[] header = new String[]{"Time","Place","Amount","Type", "Balance"};

		public RecordTableModel(){
		}
		
		public RecordTableModel(ArrayList<MemberScore> items){
			this.items = items;
		}
		@Override
		public int getRowCount() {
			if (items == null)
				return 0;
			return items.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			MemberScore r = getObjectAt(rowIndex);
			switch(columnIndex){
			case 0:
				return ConstantValue.DFYMDHMS.format(r.getDate());
			case 1:
				return r.getPlace();
			case 2:
				return String.format(ConstantValue.FORMAT_DOUBLE, r.getAmount());
			case 3:
				if (r.getType() == ConstantValue.MEMBERSCORE_CONSUM)
					return "Consume";
				else if (r.getType() == ConstantValue.MEMBERSCORE_REFUND)
					return "Refund";
				else if (r.getType() == ConstantValue.MEMBERSCORE_ADJUST)
					return "Adjust";
			case 4: 
				return String.format(ConstantValue.FORMAT_DOUBLE, r.getNewValue());
			}
			return "";
		}
		
		@Override
		public String getColumnName(int column) {
			return header[column];
	    }
		
		public void setData(ArrayList<MemberScore> items){
			this.items = items;
		}
		
		public ArrayList<MemberScore> getData(){
			return items;
		}
		
		public MemberScore getObjectAt(int index){
			return items.get(index);
		}
		
		@Override
	    public boolean isCellEditable(int row, int column) { 
	        return false;
	    }
	}
}