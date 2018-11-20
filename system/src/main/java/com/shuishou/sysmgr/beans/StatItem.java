package com.shuishou.sysmgr.beans;

public class StatItem {
	public String itemName;
	public double totalPrice;
	public double paidPrice;
	public int soldAmount;
	public double weight;
	public StatItem(String itemName){
		this.itemName = itemName;
	}
}
