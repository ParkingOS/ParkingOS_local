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
<div id="nfcanlysisobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var btime="${btime}"
var etime="${etime}";
var _mediaField = [
		{fieldcnname:"车场编号",fieldname:"comid",inputtype:"text", twidth:"100" ,issort:false},
		{fieldcnname:"停车场",fieldname:"cname",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"市场专员",fieldname:"uname",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"逃单数量",fieldname:"eorder",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('e','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"NFC结算金额",fieldname:"ntotal",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"NFC历史订单",fieldname:"hncount",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('hn','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"NFC当前订单",fieldname:"cncount",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('cn','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"照牌结算金额",fieldname:"ztotal",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"照牌历史订单",fieldname:"hzcount",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('hz','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"照牌当前订单",fieldname:"czcount",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('cz','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"极速通结算金额",fieldname:"jtotal",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"极速通历史订单",fieldname:"hjcount",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('hj','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"极速通当前订单",fieldname:"cjcount",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('cj','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"直付订单数",fieldname:"hdcount",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('hd','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"直付订单金额",fieldname:"dtotal",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"历史订单总数",fieldname:"ctotal",inputtype:"text", twidth:"100",issort:false}
	];
var _nfcanlysisT = new TQTable({
	tabletitle:"订单记录",
	ischeck:false,
	tablename:"nfcanlysis_tables",
	dataUrl:"nfcanlysis.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=query",
	tableObj:T("#nfcanlysisobj"),
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
	_nfcanlysisT.C({
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
	_nfcanlysisT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=query&btime="+btime+"&etime="+etime
	})
	T("#coutom_btime").value=btime;
	T("#coutom_etime").value=etime;
}

function viewdetail(type,value,id){
	//alert(type+","+value);
	var total =_nfcanlysisT.GD(id,"total");
	var park =_nfcanlysisT.GD(id,"cname");
	var tip = "NFC历史订单";
	if(type=='cn'){
		tip = "NFC当前订单";
	}else if(type=='hz'){
		tip = "照牌历史订单";
	}else if(type=='cz'){
		tip = "照牌当前订单";
	}else if(type=='e'){
		tip = "逃单";
	}else if(type=='hj'){
		tip = "极速通历史订单";
	}else if(type=='cj'){
		tip = "极速通当前订单";
	}else if(type=='hd'){
		tip = "直付订单";
	}
		
	Twin({
		Id:"nfc_detail_"+id,
		Title:tip+"  --> 停车场："+park,
		Width:T.gww()-100,
		Height:T.gwh()-50,
		sysfunI:id,
		Content:"<iframe name='nfc_detail_'"+id+" id='nfc_detail_'"+id+" src='nfcanlysis.do?action=detail&otype="+type+"&parkid="+id+"&btime="+btime+"&etime="+etime+"&total="+total+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}

function getAuthIsoperateButtons(){
	var bts = [{name:"车位趋势",fun:function(id){
		var pname = _nfcanlysisT.GD(id,"cname");
		var pid = _nfcanlysisT.GD(id,"comid");
		Twin({
			Id:"client_detail_"+id,
			Title:"车位趋势  &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
			Content:"<iframe src=\"parklalaanly.do?action=parkidle&pname="+encodeURI(encodeURI(pname))+"&comid="+pid+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
			Width:T.gww()-100,
			Height:T.gwh()-50
		})
		}}];
	/* bts.push({name:"刷卡详情",fun:function(id){
		var total =_nfcanlysisT.GD(id,"total");
		var park =_nfcanlysisT.GD(id,"cname");
		Twin({
			Id:"nfc_detail_"+id,
			Title:"刷卡详情        --> 停车场："+park,
			Width:T.gww()-100,
			Height:T.gwh()-50,
			sysfunI:id,
			Content:"<iframe name='nfc_detail_'"+id+" id='nfc_detail_'"+id+" src='nfcanlysis.do?action=detail&parkid="+id+"&btime="+btime+"&etime="+etime+"&total="+total+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
		})
	}}); */
	if(bts.length <= 0){return false;}
	return bts;
}
_nfcanlysisT.C();
</script>

</body>
</html>
