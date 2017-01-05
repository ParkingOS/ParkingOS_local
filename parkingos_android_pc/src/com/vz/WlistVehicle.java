package com.vz;

public class WlistVehicle {
	public  long	uVehicleID = 0;										/**<车辆在数据库的ID*/
	public    byte[]		strPlateID ;			/**<车牌字符串*/
	public    long	uCustomerID= 0;									/**<客户在数据库的ID，与VZ_LPR_WLIST_CUSTOMER::uCustomerID对应*/
	public    long	bEnable= 0;										/**<该记录有效标记*/
	public    long	bEnableTMEnable= 0;								/**<是否开启生效时间*/
	public    long	bEnableTMOverdule= 0;								/**<是否开启过期时间*/
	public   VzDateTime		struTMEnable;									/**<该记录生效时间*/
	public VzDateTime		struTMOverdule;									/**<该记录过期时间*/
	public long	bUsingTimeSeg;									/**<是否使用周期时间段*/
	    // VZ_TM_PERIOD struTimeSeg;								/**<周期时间段信息*/
	public long	   bAlarm= 0;						 	/**<是否触发报警（黑名单记录）*/
	public int			iColor= 0;											/**<车辆颜色*/
	public int			iPlateType= 0;										/**<车牌类型*/
	public byte[]		strCode;			/**<车辆编码*/
	public byte[]		strComment;	/**<车辆编码*/
	
	public WlistVehicle()
	{
		struTMEnable = new VzDateTime();
		
		struTMOverdule = new VzDateTime();
	}
}
