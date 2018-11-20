package com.shuishou.sysmgr.beans;

public class MemberUpgrade {
	private int id;
	
	private String compareField;
	
	private double smallValue;
	
	private int smallRelation;
	
	private double bigValue;
	
	private int bigRelation;
	
	private String executeField;
	
	private double executeValue;
	
	private int status;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCompareField() {
		return compareField;
	}

	public void setCompareField(String compareField) {
		this.compareField = compareField;
	}

	public double getSmallValue() {
		return smallValue;
	}

	public void setSmallValue(double smallValue) {
		this.smallValue = smallValue;
	}

	public int getSmallRelation() {
		return smallRelation;
	}

	public void setSmallRelation(int smallRelation) {
		this.smallRelation = smallRelation;
	}

	public double getBigValue() {
		return bigValue;
	}

	public void setBigValue(double bigValue) {
		this.bigValue = bigValue;
	}

	public int getBigRelation() {
		return bigRelation;
	}

	public void setBigRelation(int bigRelation) {
		this.bigRelation = bigRelation;
	}

	public String getExecuteField() {
		return executeField;
	}

	public void setExecuteField(String executeField) {
		this.executeField = executeField;
	}

	public double getExecuteValue() {
		return executeValue;
	}

	public void setExecuteValue(double executeValue) {
		this.executeValue = executeValue;
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
		MemberUpgrade other = (MemberUpgrade) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MemberUpgrade [compareField=" + compareField + ", smallValue=" + smallValue
				+ ", smallRelation=" + smallRelation + ", bigValue=" + bigValue + ", bigRelation=" + bigRelation
				+ ", executeField=" + executeField + ", executeValue=" + executeValue + "]";
	}

	
	
}
