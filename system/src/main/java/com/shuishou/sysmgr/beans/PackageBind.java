package com.shuishou.sysmgr.beans;

public class PackageBind {

private int id;
	
	private Goods bigPackage;
	
	private Goods smallPackage;
	
	private int rate;//一个大包装包含几个小包装

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Goods getBigPackage() {
		return bigPackage;
	}

	public void setBigPackage(Goods bigPackage) {
		this.bigPackage = bigPackage;
	}

	public Goods getSmallPackage() {
		return smallPackage;
	}

	public void setSmallPackage(Goods smallPackage) {
		this.smallPackage = smallPackage;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
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
		PackageBind other = (PackageBind) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PackageBind [id=" + id + ", bigPackage=" + bigPackage.getName() + ", smallPackage=" + smallPackage.getName() 
			+ ", rate=" + rate + "]";
	}
}
