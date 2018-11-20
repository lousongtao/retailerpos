package com.shuishou.sysmgr.beans;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MemberBalance {
	private int id;
	
	/**
	 * record the consume shop location
	 */
	private String place;
	
	private double amount;
	
	private Date date;
	
	private int type;
	
	private Member member;

	/**
	 * 修改后客户最新的余额
	 */
	private double newValue;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public double getNewValue() {
		return newValue;
	}

	public void setNewValue(double newValue) {
		this.newValue = newValue;
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
		MemberBalance other = (MemberBalance) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MemberBalance [date=" + date + ", place=" + place + ", amount=" + amount + ", type=" + type + "]";
	}
	
	
}
