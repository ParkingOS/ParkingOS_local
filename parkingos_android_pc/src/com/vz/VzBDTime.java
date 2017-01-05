package com.vz;

public class VzBDTime {
	public byte   bdt_sec;    /**<秒，取值范围[0,59]*/
	public byte   bdt_min;    /**<分，取值范围[0,59]*/
	public byte   bdt_hour;   /**<时，取值范围[0,23]*/
	public byte   bdt_mday;   /**<一个月中的日期，取值范围[1,31]*/
	public byte   bdt_mon;    /**<月份，取值范围[1,12]*/
//	byte   res1[3];    /**<预留*/
	public int   bdt_year;   /**<年份*/
//	byte   res2[4];    /**<预留*/
}
