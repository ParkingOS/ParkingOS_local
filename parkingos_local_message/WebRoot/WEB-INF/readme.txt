//开发进度 
===========5月4-9日=============
1：完成停车场注册，修改，查询 ，删除等功能（总管理员）
2：完成车场管理员注册成员（收费员，财务），删除成员，修改密码
3：完成车注册，登录
4：完成订单管理 ，(后台部分)
===========5月12-16日===========
1:客户管理（总管理员）
2：订单生成 及 完成(与车主手机交与交互部分) 
3：充值管理 


订单  （订单优惠？）

停车场（公司表）

用户表（）

停车费价格表（产品表）


===新增表===============
product_tb --停车费价格表

id --编号，主键，自增长
comid --公司编号
price --价格
state --状态   0当前不可用，1当前可用

*******************************************
ibeacon_tb --蓝牙设备

id --编号，主键，自增长
ibcid --蓝牙编号，对应ibeacon的Mac
buy_time --购买日期
state --状态

*******************************************

area_ibeacon_tb --停车场蓝牙

id --编号，主键，自增长
ibcid --蓝牙编号，对应ibeacon的Mac
comid --停车场编号
reg_time --注册日期
in_out --出口/入口
state --状态

*******************************************

park_charge_tb --收费流水表

id --编号，主键，自增长
comid --停车场编号
create_tiime --收费日期
amount --金额
uin --客户帐号

*******************************************

withdrawer_tb  提现记录表

id --编号，主键，自增长
comid --停车场编号
create_tiime --收费日期
amount --金额

*******************************************

recharge_tb --充值

id --编号，主键，自增长
uin --客户帐号
amount --金额
charge_type --充值类型
create_tiime --充值日期

*******************************************


car_info_tb  ---车辆信息表

id --编号，主键，自增长
uin --车主
car_number --车牌
is_use --当前使用


*******************************************
停车场详情表

所属物业

车位类型

车位总数

分享数量

坐标（地图坐标，用于查找附近停车场）

是否直接完成订单，（处理优惠）


*******************************************






*******************************************




---创建触发器
CREATE SEQUENCE seq_ibeacon_tb
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_ibeacon_tb
  OWNER TO postgres;


CREATE OR REPLACE FUNCTION trigger_fct_ibeacon_tb_trigger()
  RETURNS trigger AS
$BODY$
BEGIN
  if new.id is NULL then
New.id:=nextval('seq_ibeacon_tb');
end if;
Return NEW;
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION trigger_fct_ibeacon_tb_trigger()
  OWNER TO postgres;


CREATE TRIGGER ibeacon_tb_trigger
  BEFORE INSERT
  ON ibeacon_tb
  FOR EACH ROW
  EXECUTE PROCEDURE trigger_fct_ibeacon_tb_trigger();