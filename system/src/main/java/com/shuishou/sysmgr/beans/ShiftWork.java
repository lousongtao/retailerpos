package com.shuishou.sysmgr.beans;

import java.util.Date;

public class ShiftWork {
	private int id;
	
	private String userName;
	
	private int userId;
	
	private Date startTime;
	
	private Date endTime;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	@Override
	public String toString() {
		return "ShiftWork [id=" + id + ", userName=" + userName + ", userId=" + userId + ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
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
		ShiftWork other = (ShiftWork) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	
	
	
}
