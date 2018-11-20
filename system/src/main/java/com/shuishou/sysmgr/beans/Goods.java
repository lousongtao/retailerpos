package com.shuishou.sysmgr.beans;


public class Goods {

	private int id;
	
	private String name;
	
	private int sequence;
	
	private Category2 category2;
	
	private String barcode;
	
	private double buyPrice;
	
	private double sellPrice;
	
	private double memberPrice;
	
	private int leftAmount;
	
	private String description;
	
	private double tradePrice;
	
	//是否积分
	private Boolean isScore = true;
	
	//搜索码
	private String searchCode; 

	
	public double getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(double tradePrice) {
		this.tradePrice = tradePrice;
	}

	public Boolean getIsScore() {
		return isScore;
	}

	public void setIsScore(Boolean isScore) {
		this.isScore = isScore;
	}

	public String getSearchCode() {
		return searchCode;
	}

	public void setSearchCode(String searchCode) {
		this.searchCode = searchCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Category2 getCategory2() {
		return category2;
	}

	public void setCategory2(Category2 category2) {
		this.category2 = category2;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public double getMemberPrice() {
		return memberPrice;
	}

	public void setMemberPrice(double memberPrice) {
		this.memberPrice = memberPrice;
	}

	public int getLeftAmount() {
		return leftAmount;
	}

	public void setLeftAmount(int leftAmount) {
		this.leftAmount = leftAmount;
	}

	@Override
	public String toString() {
		return "Goods [name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Goods other = (Goods) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
