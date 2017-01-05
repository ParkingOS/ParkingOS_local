
ibeacon测试
扫描到ibeacon后发消息
http://127.0.0.1/zld/ibeaconhandle.do?action=regibc&uuid=88888&mobile=15801482643
提交订单
http://127.0.0.1/zld/ibeaconhandle.do?action=balance&orderid=1&uid=1000010&comid=3&total=96&carnumber=京GPS223

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

CREATE TABLE price_tb ---价格表
(
  id bigint NOT NULL,
  comid bigint,
  price numeric(10,2),
  state bigint DEFAULT 0,
  unit integer,
  pay_type integer, -- 收费类型，按时间，按天，按月，按季，按年
  CONSTRAINT product_tb_pkey PRIMARY KEY (id)
)
*******************************************

CREATE TABLE product_package_tb  --停车场包月产品表
(
  id bigint,
  valid_time character varying(30),
  b_time integer,
  e_time integer,
  remain_number integer, -- 剩余数量
  state integer DEFAULT 0, -- 0:不可用，1：可用
  comid bigint,
  price numeric(10,2)
)

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
车场收费员

url:http://127.0.0.1/zld/collectorrequest.do?

http://s.zhenlaidian.com/zld/collectorlogin.do?username=1000005&password=666666

//分享车位
action=toshare&token=dfc7f35f92014d229eee55634648bd3f&s_number=1000
//公司信息
action=cominfo&token=*
//现金收费
action=ordercash&token=*&orderid=*
//历史订单
http://s.zhenlaidian.com/zld/collectorrequest.do?action=orderhistory&token=f7d3d26b60eaa9a57d4f1f64713d9b19&page=1&size=10
//当前订单
http://s.zhenlaidian.com/zld/collectorrequest.do?action=currorders&token=f7d3d26b60eaa9a57d4f1f64713d9b19&page=1&size=10
//订单详情
action=orderdetail&token=*&orderid=*
//打折处理
action=tosale&token=*&orderid=*&hour=*

http://s.zhenlaidian.com/zld/collectorrequest.do?action=cominfo&token=dfc7f35f92014d229eee55634648bd3f&s_number=1000


*********************************************************
车主
===========================================================

1、登录：
 *车主登录，注册，验证码处理
 * 返回码，不是xml格式：
 * 1:登录成功，
 * 0:输入验证码，
 * -1：验证码无效，
 * -2：注册失败，
 * -3：给手机发送验证码失败，
 * -4：角色错误
 * -5:手机号码错误
 * -6：系统验证码不存在
 * -7:保存验证码错误
 http://s.zhenlaidian.com/zld/carlogin.do?action=login&mobile=15801482643
 http://192.168.1.103/zld/carlogin.do?action=login&mobile=15801482643
 http://192.168.1.103/zld/carlogin.do?action=validcode&code=1715&mobile=15801482643
 http://s.zhenlaidian.com/zld/carlogin.do?action=validcode&code=1630&mobile=15801682643

 ============================================================
2、车主从服务器取数据
 http://192.168.1.104/zld/carservice.do?action=getparking&begintime=utc时间，到秒
 http://s.zhenlaidian.com/zld/carservice.do?action=getparkshare&ids=

 
 ============================================================
3、扫描到Ibeacon时，请求订单
 * 返回值info节点说明：
	 *  7:未开通转账接口
	 * 	6：现金支付 ，等待收费员收钱
	 *  5:订单消费成功
	 *  4:订单新建成功 
	 *  3:出场，没有可结算的订单 
	 *  2:已存在该停车场未结算的订单，不能生成进场订单 
	 *  1:创建订单
	 *  0：结算订单 
	 *  -1：uuid 为空 
	 *  -2：uuid 未注册 
	 *  -3:uuid 不存在
	 *  -4:车主手机号为空 
	 *  -5:车主手机号未注册
	 *  -6:新建订单错误
	 *  -7:订单收费错误
	 *  -8:订单已结算，不能重启结算
	 *  -9:订单不存在
	 *  -10:停车场不存在 
	 *  -11:支付方式错误
	 *  -12:账户余额不足
（）扫描到ibeacon
http://s.zhenlaidian.com/zld/ibeaconhandle.do?action=regibc&uuid=*
参数：uuid:ibeacon uuid
返回：
进场：
info:1
parkname:车场名称
parkaddress:车场地址
begin:进场时间
comid:公司编号
uin:车主编号
balance:车主帐户余额
carnumber:车主车牌号
message:消息提示

离场：
info:0
parkname:车场名称
parkaddress:车场地址
begin:进场时间
end:离场时间
duration:时长（小时）
total:金额
issale:是否打折
orderid:订单编号
carnumber:车主车牌号
message:消息提示


（）生成订单
http://s.zhenlaidian.com/zld/ibeaconhandle.do?action=addorder&comid=&uin=&balance=

参数：
comid:公司ID
uin:车主编号 
balance:车主帐户余额

返回：
info:4
message:提示消息


 （）结算订单 
http://s.zhenlaidian.com/zld/ibeaconhandle.do?action=doorder&comid=&uin=&balance=&duration&orderid=&is_sale=&total=&carnumber&pay_type= 

参数：
comid:公司ID
uin:车主编号 
balance:车主帐户余额
duration:时长（小时）
orderid:订单编号
issale:是否打折
pay_type:支付方式:0:账户余额支付 ，1:转账，2：现金支付 

返回：
info:4
message:消息提示