package com.shuishou.sysmgr.ui.goods;

import javax.swing.tree.DefaultMutableTreeNode;

import com.shuishou.sysmgr.beans.Category1;
import com.shuishou.sysmgr.beans.Category2;
import com.shuishou.sysmgr.beans.Goods;

public class GoodsTreeNode extends DefaultMutableTreeNode {
	public GoodsTreeNode(Object o){
		super(o);
	}
	
	public String toString(){
		Object o = this.getUserObject();
		if (o instanceof Category1){
			return ((Category1)o).getName();
		} else if (o instanceof Category2){
			return ((Category2)o).getName();
		} else if (o instanceof Goods){
			return ((Goods)o).getName();
		}
		return super.toString();
	}
}
