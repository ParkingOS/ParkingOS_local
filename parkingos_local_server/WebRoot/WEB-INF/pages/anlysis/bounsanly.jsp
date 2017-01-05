<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>礼包统计</title>
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
<div id="honusanlyobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var btime="${btime}"
var etime="${etime}";
var mobile = '${mobile}';
var carnumber='${carnumber}';
var viewtype="custom";

function getAtypes (){
	return eval(T.A.sendData("getdata.do?action=getbonustypes"));;
}
 var atypes =getAtypes();
/*	[{"value_no":"1","value_name":"今日头条(北京)"},
			{"value_no":"2","value_name":"传单红包"},
			{"value_no":"3","value_name":"节日红包"},
			{"value_no":"998","value_name":"直付红包"},
			{"value_no":"999","value_name":"收费员推荐"},
			{"value_no":"1000","value_name":"交易红包"},
			{"value_no":"4","value_name":"今日头条(外地)"}]; */
var tip = "区间查询";
var _mediaField = [
		{fieldcnname:"媒体类型",fieldname:"atype",inputtype:"select",noList:atypes, twidth:"100",issort:false,edit:false},
		{fieldcnname:"日期",fieldname:"ctime",inputtype:"date", twidth:"100",issort:false,edit:false},
		{fieldcnname:"金额",fieldname:"amount",inputtype:"text", twidth:"80",issort:false},
		{fieldcnname:"pv数",fieldname:"pv_number",inputtype:"text", twidth:"80",issort:false},
		{fieldcnname:"点击数",fieldname:"hit_number",inputtype:"text", twidth:"80",issort:false},
		{fieldcnname:"红包数",fieldname:"bonus_num",inputtype:"text", twidth:"80" ,issort:false,edit:false},
		{fieldcnname:"下载数",fieldname:"down_num",inputtype:"text", twidth:"80" ,issort:false},
		{fieldcnname:"车牌数",fieldname:"reg_num",inputtype:"text", twidth:"80",issort:false,edit:false},
		{fieldcnname:"产生消费数",fieldname:"order_num",inputtype:"text", twidth:"80" ,issort:false,edit:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"注册成本",fieldname:"rp",inputtype:"text", twidth:"80" ,issort:false,edit:false},
		{fieldcnname:"红包率",fieldname:"hp",inputtype:"text", twidth:"80",issort:false,edit:false},
		{fieldcnname:"下载率",fieldname:"dp",inputtype:"text", twidth:"80",issort:false,edit:false},
		{fieldcnname:"车牌转化率",fieldname:"tp",inputtype:"text", twidth:"80",issort:false,edit:false}
	];
var _honusanlyT = new TQTable({
	tabletitle:"红包统计",
	ischeck:false,
	tablename:"reg_anlysis_Tb",
	dataUrl:"bonusanly.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=query&btime="+btime+"&etime="+etime,
	tableObj:T("#honusanlyobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:false,
	isoperate:getAuthIsoperateButtons()
});

function viewdetail(value,id){
	var atype=_honusanlyT.GD(id,"atype");
	var _date =_honusanlyT.GD(id,"ctime"); 
	if(value<1)
		return ;
	Twin({
		Id:"border_detail_win_"+id,
		Title:"订单详情-->>共 "+value+" 位车主产生消费",
		Width:T.gww()-100,
		Height:T.gwh()-50,
		sysfunI:id,
		Content:"<iframe name='border_detail_'"+id+" id='border_detail_'"+id+" src='bonusanly.do?action=detail&c="+value+"&type="+atype+"&d="+_date+"&r="+Math.random()+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}

function getMedia(){
	var m = "<option value='o'>全部</option>"
	for(var a=0;a<atypes.length;a++){
		m +="<option value='"+atypes[a].value_no+"'>"+atypes[a].value_name+"</option>";
	}
	return m;
}

function coutomsearch(){
	var html=    "<input type='button' onclick='yestodaydata();' value='昨日'/>&nbsp;&nbsp;&nbsp;&nbsp;时间：<input id='coutom_btime' value='"+btime+"' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-d%',alwaysUseStartDate:true});\"/>"
				+" - <input id='coutom_etime' value='"+etime+"' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-d%',alwaysUseStartDate:true});\"/>"+
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;媒体类型：<select id ='media' name='media' style='width:120px' >"+getMedia()+"</select><input type='button' onclick='searchdata();' value=' 查 询 '/>&nbsp;&nbsp;<span id='total_money'></span>";
	return html;
}

function yestodaydata(){
	viewtype='yestoday';
	tip = "昨日统计";
	_honusanlyT.C({
		cpage:1,
		tabletitle:"昨日统计",
		extparam:"&action=query&btime="+btime+"&etime="+etime
	})
}
function searchdata(){
	btime = T("#coutom_btime").value;
	etime = T("#coutom_etime").value;
	if(btime=='')
		btime="${btime}";
	if(etime=='')
		etime="${etime}";
	var media = T("#media").value;
	viewtype="custom";
	_honusanlyT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=query&btime="+btime+"&etime="+etime+"&media="+media
	})
	T("#coutom_btime").value=btime;
	T("#coutom_etime").value=etime;
	var meidaselect = T("#media");
	for(var i=0;i<meidaselect.options.length;i++){
		if(meidaselect.options[i].value==media)
			meidaselect.options[i].selected = true;   
	}
}

function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"编辑",fun:function(id){
		T.each(_honusanlyT.tc.tableitems,function(o,j){
			o.fieldvalue = _honusanlyT.GD(id)[j]
		});
		Twin({Id:"bonusanly_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "bonusanly_edit_f",
					formObj:tObj,
					recordid:"bonusanly_id",
					suburl:"bonusanly.do?action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_honusanlyT.tc.tableitems}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("bonusanly_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("bonusanly_edit_"+id);
							_honusanlyT.M()
						}else{
							T.loadTip(1,"保存失败",2,o)
						}
					}
				});	
			}
		})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_honusanlyT.C();
</script>

</body>
</html>
