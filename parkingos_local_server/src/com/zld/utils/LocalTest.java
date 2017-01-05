package com.zld.utils;

import java.math.BigDecimal;

public class LocalTest {

	public static void main(String[] args) {
		
		double d1 = new Double("109.9");
		double d2 = new Double("0.012");
		//System.out.println(mul(d1, d2));
		System.err.println(Long.valueOf("010060001"));
		System.out.println(StringUtils.getPre("000000010006"));
	}
	public static double mul(double d1,double d2){ 
	        BigDecimal bd1 = new BigDecimal(Double.toString(d1)); 
	        BigDecimal bd2 = new BigDecimal(Double.toString(d2)); 
	        return bd1.multiply(bd2).doubleValue(); 
	} 
}
