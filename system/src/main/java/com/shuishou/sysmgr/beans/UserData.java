package com.shuishou.sysmgr.beans;

import java.util.Date;
import java.util.List;

public class UserData {
	private int id;
	
	private String username;
	
	private Date startTime;//on duty time
	
	public List<UserPermission> permissions;
	
	public UserData(){}
	
	public UserData(int id, String name){
		this.id = id;
		this.username = name;
	}
	
	public UserData(int id, String name, Date startTime){
		this.id = id;
		this.username = name;
		this.startTime = startTime;
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return username;
	}

	public void setName(String name) {
		this.username = name;
	}

	
	public List<UserPermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<UserPermission> permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		return "User [name=" + username + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		UserData other = (UserData) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
