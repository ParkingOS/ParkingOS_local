<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>分享排行</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.form.js?0817" type="text/javascript">//表单</script>
<script src="js/tq.searchform.js?0817" type="text/javascript">//查询表单</script>
<script src="js/tq.window.js?0817" type="text/javascript">//弹窗</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
</head>
<body>
<div id="collectorsortobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var ishide=role==5?true:false;
var page_type="toweek";
var _mediaField = [
		{fieldcnname:"排名 ",fieldname:"sort",fieldvalue:'',inputtype:"text", twidth:"50" ,height:"",hide:true},
		{fieldcnname:"停车员账号",fieldname:"uin",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"停车场",fieldname:"cname",fieldvalue:'',inputtype:"text", twidth:"250" ,height:"",hide:true},
		{fieldcnname:"姓名",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"120" ,height:"",hide:true},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false,fhide:ishide},
		{fieldcnname:"市场专员",fieldname:"uid",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"积分",fieldname:"share_time",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false,edit:false,
			process:function(value,trId,colId){
				return "<span title ='点击查看' onclick=\"show_dialog('"+trId+"'); this.style.color='green'\" style='cursor:pointer;_cursor:hand;color:blue'>"+value+"</span>";
			}}
	];
var _collectorsortT = new TQTable({
	tabletitle:"本周积分排行",
	ischeck:false,
	tablename:"collectorsort_tables",
	dataUrl:"collectorsort.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	isidentifier:false,
	//searchitem:true,
	param:"action=query",
	tableObj:T("#collectorsortobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function getAuthButtons(){
	return [
		{dname:"本周积分排行",icon:"edit_add.png",onpress:function(Obj){
			page_type="toweek";
			_collectorsortT.C({
				cpage:1,
				tabletitle:"本周积分排行",
				extparam:"&action=query"
			})}
		
		},{dname:"上周积分排行",icon:"edit_add.png",onpress:function(Obj){
			page_type="lastweek";
			_collectorsortT.C({
				cpage:1,
				tabletitle:"上周积分排行",
				extparam:"&action=query&week=last"
			})}
		},{dname:"本月积分排行",icon:"edit_add.png",onpress:function(Obj){
			page_type="tomonth";
			_collectorsortT.C({
				cpage:1,
				tabletitle:"本月积分排行",
				extparam:"&action=query&month=tomonth"
			})}
		},{dname:"上月积分排行",icon:"edit_add.png",onpress:function(Obj){
			page_type="lastmonth";
			_collectorsortT.C({
				cpage:1,
				tabletitle:"上月积分排行",
				extparam:"&action=query&month=last"
			})}
		}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	//if(role==7||role==0)
	if('${userid}'=='huxuelian')
		bts.push({name:"发奖金",fun:function(id){
			if(page_type!="lastweek"){
				T.loadTip(1,"只能在上周排行中发放！",2,"");
				return ;
			}
			var sort = _collectorsortT.GD(id,"sort");
			/* if(sort<16){
				T.loadTip(1,"前十五名不发奖金！",2,"");
				return ;
			} */
			var score=_collectorsortT.GD(id,"share_time");
			var money = "30";
			if(sort<6){
				money = "150";
			}else if(sort<16){
				money = "100";	
			}else if(score>=70){
				money = "50";	
			}else if(score<35){
				T.loadTip(1,"积分不足，不能发奖！",2,"");
				return ;
			}
			var name = _collectorsortT.GD(id,"nickname")+"，收费员账号："+id;
			Twin({Id:"send_money_"+id,Title:"发第"+sort+"名奖金,积分:"+score,Width:310,sysfunI:id,sysfun:function(id,tObj){
					Tform({
						formname: "send_money_f",
						formObj:tObj,
						recordid:"id",
						suburl:"collectorsort.do?action=sendmoney&uid="+id,
						method:"POST",
						dbuttonname:["确定发放"],
						Coltype:2,
						formAttr:[{
							formitems:[{kindname:"",kinditemts:[
								{fieldcnname:"发放金额",fieldname:"money",fieldvalue:money,inputtype:"text", twidth:"200" ,height:"",issort:false}]}]
						}],
						buttons : [//工具
							{name: "cancel", dname: "取消", tit:"取消成功",icon:"cancel.gif", onpress:function(){TwinC("send_money_"+id);} }
						],
						Callback:
						function(f,rcd,ret,o){
							if(ret=="1"){
								T.loadTip(1,"发放成功！",2,"");
								TwinC("send_money_"+id);
								//_collectorsortT.M()
							}else{
								T.loadTip(1,ret,2,"");
								TwinC("send_money_"+id);
							}
						}
					});	
				}
			})
		}})
	if(bts.length <= 0){return false;}
	return bts;
}
_collectorsortT.C();

function show_dialog(pid){
	var user = _collectorsortT.GD(pid,"uid");
	var parker = _collectorsortT.GD(pid,"nickname");
	Twin({
		Id:"sort_detail_"+pid,
		Title:"积分详情 &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
		Content:"<iframe src=\"collectorsort.do?action=detail&ptype="+page_type+"&uid="+encodeURI(encodeURI(user))+"&parker="+encodeURI(encodeURI(parker))+"&pid="+pid+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
		Width:700,
		Height:500
	});
}
</script>

</body>
</html>
