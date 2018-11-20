/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.sysmgr.beans;

import java.util.Date;

/**
 * @author zhing the log data.
 */
public class LogData {

	public static enum LogType {
		ACCOUNT_LOGIN("ACCOUNT_LOGIN"), 
		ACCOUNT_LOGOUT("ACCOUNT_LOGOUT"), 
		ACCOUNT_ADD("ACCOUNT_ADD"), 
		ACCOUNT_MODIFY("ACCOUNT_MODIFY"), 
		ACCOUNT_DELETE("ACCOUNT_DELETE"),

		CATEGORY1_ADD("CATEGORY1_ADD"),
		CATEGORY1_MODIFY("CATEGORY1_MODIFY"),
		CATEGORY1_DELETE("CATEGORY1_DELETE"),
		CATEGORY2_ADD("CATEGORY2_ADD"),
		CATEGORY2_MODIFY("CATEGORY2_MODIFY"),
		CATEGORY2_DELETE("CATEGORY2_DELETE"),
		DISH_ADD("DISH_ADD"),
		DISH_MODIFY("DISH_MODIFY"),
		DISH_DELETE("DISH_DELETE"),
		
		CHANGE_CONFIRMCODE("CHANGE_CONFIRMCODE"),
		CHANGE_OPENCASHDRAWERCODE("CHANGE_OPENCASHDRAWERCODE"),
		CHANGE_DESK("CHANGE_DESK"),
		CHANGE_PRINTER("CHANGE_PRINTER"),
		CHANGE_DISCOUNTTEMPLATE("CHANGE_DISCOUNTTEMPLATE"),
		CHANGE_PAYWAY("CHANGE_PAYWAY"),
		
		INDENT_CANCEL("INDENT_CANCEL"),
		INDENT_PAY("INDENT_PAY"),
		INDENT_FORCEEND("INDENT_FORCEEND"),
		INDENT_PRINT("INDENT_PRINT"),
		INDENTDETAIL_DELETE("INDENTDETAIL_DELETE"),
		INDENTDETAIL_CHANGEAMOUNT("INDENTDETAIL_CHANGEAMOUNT"),
		INDENTDETAIL_ADDDISH("INDENTDETAIL_ADDDISH"),
		INDENTDETAIL_PRINTISH("INDENTDETAIL_PRINTDISH"),
		
		SHIFTWORK("SHIFTWORK"),
		
		MERGETABLE("MERGETABLE"),
		;

		private String type;

		private LogType(String _type) {
			type = _type;
		}

		public String toString() {
			return type;
		}
	}

	/**
	 * the id.
	 */
	private long id;

	/**
	 * the operator.
	 */
	private String userName;

	/**
	 * the type.
	 */
	private String type;

	/**
	 * the time.
	 */
	private Date time = new Date();

	/**
	 * the message.
	 */
	private String message;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
