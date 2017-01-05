<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>订单记录</title>
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
<div id="rewardobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var btime="${btime}"
var etime="${etime}";
var _mediaField = [
		{fieldcnname:"日期",fieldname:"ctime",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"停车场",fieldname:"comid",inputtype:"text", twidth:"240",issort:false},
		{fieldcnname:"收费员账户",fieldname:"uid",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"姓名",fieldname:"name",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"手机",fieldname:"mobile",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"金额",fieldname:"money",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"打赏数",fieldname:"count",inputtype:"text", twidth:"100",issort:false
			//,process:function(value,cid,id){
			//	return "<a href=# onclick=\"viewdetail('"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			//}
		},
		{fieldcnname:"积分",fieldname:"scroe",inputtype:"text", twidth:"100",issort:false}
	];
var _rewardT = new TQTable({
	tabletitle:"订单记录",
	ischeck:false,
	tablename:"reward_tables",
	dataUrl:"reward.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=query",
	tableObj:T("#rewardobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:true,
	isoperate:getAuthIsoperateButtons()
});

function coutomsearch(){
	var html=    "&nbsp;&nbsp;<input type='button' onclick='todaydata();' value=' 今天 '/> &nbsp;&nbsp; 时间：<input id='coutom_btime' value='"+btime+"' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"
				+" - <input id='coutom_etime' value='"+etime+"' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"+
				"&nbsp;&nbsp;<input type='button' onclick='searchdata();' "+
				"value=' 查 询 '/>";//"&nbsp;&nbsp;总计：900.00元";
	return html;
}

function todaydata(){
    var now = new Date();
    var year = now.getFullYear();       //年
    var month = now.getMonth() + 1;     //月
    var day = now.getDate();            //日
	btime=year+"-"+month+"-"+day;
	etime = btime;
	_rewardT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=query&btime="+btime+"&etime="+etime
	})
	T("#coutom_btime").value=btime;
	T("#coutom_etime").value=etime;
}
function searchdata(){
	btime = T("#coutom_btime").value;
	etime = T("#coutom_etime").value;
	//alert(btime);
	//alert(etime);
	_rewardT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=query&btime="+btime+"&etime="+etime
	})
	T("#coutom_btime").value=btime;
	T("#coutom_etime").value=etime;
}

function viewdetail(value,id){
	//alert(type+","+value);
	var total =_rewardT.GD(id,"total");
	var park =_rewardT.GD(id,"name");
	var tip = "打赏详情";
	Twin({
		Id:"puser_detail_"+id,
		Title:tip+"  -->  收费员："+park,
		Width:T.gww()-100,
		Height:T.gwh()-50,
		sysfunI:id,
		Content:"<iframe name='parkuser_detail_'"+id+" id='parkuser_detail_'"+id+" src='reward.do?action=detail&otype="+type+"&parkid="+id+"&btime="+btime+"&etime="+etime+"&total="+total+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}

function getAuthIsoperateButtons(){
	var bts = [
	/*{name:"车位趋势",fun:function(id){
		var pname = _rewardT.GD(id,"cname");
		var pid = _rewardT.GD(id,"comid");
		Twin({
			Id:"client_detail_"+id,
			Title:"车位趋势  &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
			Content:"<iframe src=\"parklalaanly.do?action=parkidle&pname="+encodeURI(encodeURI(pname))+"&comid="+pid+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
			Width:T.gww()-100,
			Height:T.gwh()-50
		})
		}}*/
		];
	/* bts.push({name:"刷卡详情",fun:function(id){
		var total =_rewardT.GD(id,"total");
		var park =_rewardT.GD(id,"cname");
		Twin({
			Id:"nfc_detail_"+id,
			Title:"刷卡详情        --> 停车场："+park,
			Width:T.gww()-100,
			Height:T.gwh()-50,
			sysfunI:id,
			Content:"<iframe name='nfc_detail_'"+id+" id='nfc_detail_'"+id+" src='reward.do?action=detail&parkid="+id+"&btime="+btime+"&etime="+etime+"&total="+total+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
		})
	}}); */
	if(bts.length <= 0){return false;}
	return bts;
}
_rewardT.C();
</script>

</body>
</html>
