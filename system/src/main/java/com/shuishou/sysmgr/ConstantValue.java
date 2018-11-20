package com.shuishou.sysmgr;

import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ConstantValue {
	public static final DateFormat DFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat DFHMS = new SimpleDateFormat("HH:mm:ss");
	public static final DateFormat DFYMD = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DFWEEK = new SimpleDateFormat("EEE");
	public static final DateFormat DFYMDHMS_2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static final String DATE_PATTERN_YMD = "yyyy-MM-dd";
	public static final String DATE_PATTERN_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	
	public static final String FORMAT_DOUBLE = "%.2f";
	
	public static final Font FONT_30BOLD = new Font(null, Font.BOLD, 30);
	public static final Font FONT_30PLAIN = new Font(null, Font.PLAIN, 30);	
	public static final Font FONT_20BOLD = new Font(null, Font.BOLD, 20);
	public static final Font FONT_20PLAIN = new Font(null, Font.PLAIN, 20);
	
	public static final String PERMISSION_QUERY_USER = "QUERY_USER";
	public static final String PERMISSION_CREATE_USER = "CREATE_USER";
	public static final String PERMISSION_EDIT_MENU = "EDIT_MENU";
	public static final String PERMISSION_QUERY_ORDER = "QUERY_ORDER";
	public static final String PERMISSION_UPDATE_ORDER = "UPDATE_ORDER";
	public static final String PERMISSION_CHANGE_CONFIRMCODE = "CHANGE_CONFIRMCODE";
	public static final String PERMISSION_QUERY_DESK = "QUERY_DESK";
	public static final String PERMISSION_EDIT_DESK = "EDIT_DESK";
	public static final String PERMISSION_EDIT_PRINTER = "EDIT_PRINTER";
	
	public final static String SPLITTAG_PERMISSION = ";";
	
	public static final String TYPE_CATEGORY1INFO = "C1";
	public static final String TYPE_CATEGORY2INFO = "C2";
	public static final String TYPE_DISHINFO = "DISH";
	
	public static final String CATEGORY_DISHIMAGE_ORIGINAL = "dishimage_original";
	public static final String CATEGORY_DISHIMAGE_MIDDLE = "dishimage_middle";
	public static final String CATEGORY_DISHIMAGE_SMALL = "dishimage_small";
	public static final String CATEGORY_PRINTTEMPLATE = "printtemplate";
	
	public static final int DISHIMAGE_WIDTH_SMALL = 50;
	public static final int DISHIMAGE_HEIGHT_SMALL = 50;
	public static final int DISHIMAGE_WIDTH_MIDDLE = 280;
	public static final int DISHIMAGE_HEIGHT_MIDDLE = 300;
	
	public static final String CSS_MENUTREENODE_ICON_SIZE = "menutreenode-icon-size";
	
	public static final byte INDENT_STATUS_OPEN = 1;
	public static final byte INDENT_STATUS_CLOSED = 2;
	public static final byte INDENT_STATUS_PAID = 3;
	public static final byte INDENT_STATUS_CANCELED = 4;
	
	public static final byte INDENT_OPERATIONTYPE_ADD = 1;
	public static final byte INDENT_OPERATIONTYPE_DELETE = 2;
	public static final byte INDENT_OPERATIONTYPE_CANCEL = 3;
	public static final byte INDENT_OPERATIONTYPE_PAY = 4;
	
	//付款方式
	public static final String INDENT_PAYWAY_CASH = "cash";//现金
	public static final String INDENT_PAYWAY_BANKCARD = "bankcard";//刷卡
	public static final String INDENT_PAYWAY_MEMBER = "member";//会员
	
	public static final int INDENT_TYPE_ORDER = 1;//普通订单
	public static final int INDENT_TYPE_REFUND = 2;//退货单
	public static final int INDENT_TYPE_PREBUY_PAID = 3;//预购单已付款
	public static final int INDENT_TYPE_PREBUY_UNPAID = 4;//预购单未付款
	public static final int INDENT_TYPE_PREBUY_FINISHED = 5;//预购单完结,已经转为订单
	public static final int INDENT_TYPE_ORDER_FROMPREBUY = 6;//预购单完结,已经转为订单
	
	public static final byte INDENTDETAIL_OPERATIONTYPE_ADD = 1;
	public static final byte INDENTDETAIL_OPERATIONTYPE_DELETE = 2;
	public static final byte INDENTDETAIL_OPERATIONTYPE_CHANGEAMOUNT = 5;
	
	public static final byte MENUCHANGE_TYPE_SOLDOUT = 0;
	
	public static final byte PRINT_TYPE_TOGETHER = 0;
	public static final byte PRINT_TYPE_SEPARATELY = 1;
	public static final byte PRINTER_TYPE_COUNTER = 1;
	public static final byte PRINTER_TYPE_KITCHEN = 2;
	
	public static final byte SHIFTWORK_ONWORK = 0;
	public static final byte SHIFTWORK_OFFWORK = 1;
	
	public static final byte DISH_CHOOSEMODE_DEFAULT = 1;
	public static final byte DISH_CHOOSEMODE_SUBITEM = 2;
	public static final byte DISH_CHOOSEMODE_POPINFOCHOOSE = 3;
	public static final byte DISH_CHOOSEMODE_POPINFOQUIT = 4;
	
	public static final byte DISH_PURCHASETYPE_UNIT = 1;
	public static final byte DISH_PURCHASETYPE_WEIGHT = 2;
	
	public static final String CONFIGS_BRANCHNAME= "BRANCHNAME";
	public static final String CONFIGS_OPENCASHDRAWERCODE = "OPENCASHDRAWERCODE";
	public static final String CONFIGS_MEMBERMGR_BYSCORE= "MEMBERMGR_BYSCORE";
	public static final String CONFIGS_MEMBERMGR_BYDEPOSIT = "MEMBERMGR_BYDEPOSIT";
	public static final String CONFIGS_MEMBERMGR_SCOREPERDOLLAR = "MEMBERMGR_SCOREPERDOLLAR";
	
	public static final int STATISTICS_DIMENSTION_PAYWAY = 1;
	public static final int STATISTICS_DIMENSTION_SELL = 2;
	public static final int STATISTICS_DIMENSTION_PERIODSELL = 3;
	
	public static final int STATISTICS_SELLGRANULARITY_BYGOODS = 1;
	public static final int STATISTICS_SELLGRANULARITY_BYCATEGORY2 = 2;
	public static final int STATISTICS_SELLGRANULARITY_BYCATEGORY1 = 3;
	
	public static final int STATISTICS_PERIODSELL_PERDAY = 1;
	public static final int STATISTICS_PERIODSELL_PERHOUR = 2;
	public static final int STATISTICS_PERIODSELL_PERWEEK = 3;
	public static final int STATISTICS_PERIODSELL_PERMONTH = 4;
	
	public static final String TICKET_TEMPLATE_PURCHASE = "/purchase_ticket.json";
	public static final String TICKET_TEMPLATE_REFUND = "/refund_ticket.json";
	public static final String TICKET_TEMPLATE_PREBUY = "/preorder_ticket.json";
	public static final String TICKET_TEMPLATE_SHIFTWORK = "/shiftwork_ticket.json";
	
	public static final int MEMBERSCORE_CONSUM = 1;//积分类型-消费
	public static final int MEMBERSCORE_REFUND = 2;//积分类型-退货
	public static final int MEMBERSCORE_ADJUST = 3;//积分类型-调整
	public static final int MEMBERDEPOSIT_CONSUM = 1;//消费余额类型-消费
	public static final int MEMBERDEPOSIT_REFUND = 2;//消费余额类型-退款
	public static final int MEMBERDEPOSIT_RECHARGE = 3;//消费余额类型-充值
	public static final int MEMBERDEPOSIT_ADJUST = 4;//消费余额类型-调整
	
	public static final int MEMBERUPGRADE_RELATION_EQUAL = 1;//等于
	public static final int MEMBERUPGRADE_RELATION_GREATER = 2;//大于
	public static final int MEMBERUPGRADE_RELATION_GREATEREQUAL = 3;//大于等于
	public static final int MEMBERUPGRADE_RELATION_LESS = 4;//小于
	public static final int MEMBERUPGRADE_RELATION_LESSEQUAL = 5;//小于等于
	
	public static final int PROMOTION_GOODS = 1;
	public static final int PROMOTION_CATEGORY2 = 2;
	public static final int PROMOTION_CATEGORY1 = 3;
	
	public static final int PROMOTION_REWARD_BUYNREDUCEPRICE = 1; //买n个, 直接折扣x元
	public static final int PROMOTION_REWARD_BUYNDISCOUNT = 2; //买n个, 给予百分比折扣
	public static final int PROMOTION_REWARD_BUYNNEXTREDUCEPRICE = 3; //买n个, 下一个直接折扣x元
	public static final int PROMOTION_REWARD_BUYNNEXTDISCOUNT = 4; //买n个, 下一个给予百分比折扣
	public static final int PROMOTION_REWARD_BUYNA_NEXTBREDUCEPRICE = 5; //买n个A, 下一个B给予直接折扣
	public static final int PROMOTION_REWARD_BUYNA_NEXTBDISCOUNT = 6; //买n个A, 下一个B给予百分比折扣
	public static final int PROMOTION_REWARD_BUYNA_MB_ABDISCOUNT = 7; //买n个A + m个B, AB同时给予直接折扣
	public static final int PROMOTION_REWARD_BUYNA_MB_ABREDUCEPRICE = 8; //买n个A + m个B, AB同时给予百分比折扣
	
	public static final int DISCOUNTTYPE_RATE= 1;     //折扣类型-按比例折扣
	public static final int DISCOUNTTYPE_QUANTITY = 2;//折扣类型-直接减数量
	
	public static final int MEMBERUPGRADE_STATUS_AVAILABLE = 1;
	public static final int MEMBERUPGRADE_STATUS_UNAVAILABLE = 2;
}
