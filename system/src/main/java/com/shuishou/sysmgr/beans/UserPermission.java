package com.shuishou.sysmgr.beans;

public class UserPermission {

	private long id;
	
	private UserData user;
	
	private Permission permission;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public String toString(){
		return user.getName() + " " + permission.getName();
	}
	
	public boolean equals(Object o ){
		if (o instanceof UserPermission){
			return id == ((UserPermission)o).getId();
		}
		return false;
	}
	
	public int hashCode(){
		return super.hashCode();
	}
}
