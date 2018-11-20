package com.shuishou.sysmgr.beans;

public class Permission {
	private int id;
	
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Object o){
		if (o instanceof Permission){
			return name.equals(((Permission)o).getName());
		}
		return false;
	}
	
	public int hashCode(){
		return super.hashCode();
	}
}
