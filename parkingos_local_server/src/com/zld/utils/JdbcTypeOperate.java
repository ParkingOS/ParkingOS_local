package com.zld.utils;

public class JdbcTypeOperate {
	
	public static final int TYPE_DOUBLE = 3;
	public static final int TYPE_INT = 4;
	public static final int TYPE_STRING = 12;
	public static final int TYPE_DATE = 93;
	public static final int OPER_EQ = 1;// =
	public static final int OPER_NE = 2;// <>
	public static final int OPER_LT = 3;// <
	public static final int OPER_GT = 4;// >
	public static final int OPER_BT = 5;// between
	public static final int OPER_LE = 6;// <=
	public static final int OPER_GE = 7;// >=
	public static final int OPER_NU = 8;// =null
	public static final int OPER_NN = 9;// not null
	public static final int OPER_IN = 10;// IN
	public static final int OPER_NIN = 11;// not in
	public static final int OPER_LK = 12;// like
	public static final int OPER_LLK = 13;// left like
	public static final int OPER_RLK = 14;// right like
	public static final int OPER_NLK = 15;// not like
	
	public static int getOperType(String oper){
		if(oper.equals("="))
			return OPER_EQ;
		else if(oper.equals(">"))
			return OPER_GT;
		else if(oper.equals("<"))
			return OPER_LT;
		else if(oper.equals(">="))
			return OPER_GE;
		else if(oper.equals("<="))
			return OPER_LE;
		else if(oper.equals("<>"))
			return OPER_NE;
		return OPER_EQ;
	}

}
