<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>停车场管理</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.form.js?033434" type="text/javascript">//表单</script>
<script src="js/tq.searchform.js?0817" type="text/javascript">//查询表单</script>
<script src="js/tq.window.js?0817" type="text/javascript">//弹窗</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<script src="js/tq.newtree.js?1014" type="text/javascript"></script>

</head>
<body>
<div id="verifyparkobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var role=${role};
var states = [{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"是"}]
var add_states = [{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"是"}]
var etc_states=[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"不支持"},{"value_no":1,"value_name":"Ibeacon"},{"value_no":2,"value_name":"通道照牌"},{"value_no":3,"value_name":"手机照牌"}]
var etc_add_states=[{"value_no":0,"value_name":"不支持"},{"value_no":1,"value_name":"Ibeacon"},{"value_no":2,"value_name":"通道照牌"},{"value_no":3,"value_name":"手机照牌"}]

var isfixed = false;
if(role==7)
	isfixed=true;
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"number", twidth:"40" ,height:"",issort:false,edit:false},
		{fieldcnname:"名称",fieldname:"company_name",fieldvalue:'',inputtype:"text", twidth:"150" ,height:"",issort:false},
		{fieldcnname:"名称审核",fieldname:"vname",fieldvalue:'',inputtype:"text", twidth:"150" ,height:"",issort:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
					return setname(trId,'isname','vname');
				}},
		{fieldcnname:"详细地址",fieldname:"address",fieldvalue:'',inputtype:"text", twidth:"180" ,height:"",issort:false},
		{fieldcnname:"详细地址审核",fieldname:"vaddr",fieldvalue:'',inputtype:"text", twidth:"180" ,height:"",issort:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
					return setname(trId,'islocal','vaddr');
				}},
		{fieldcnname:"描述",fieldname:"resume",fieldvalue:'',inputtype:"text", twidth:"180" ,height:"",issort:false},
		{fieldcnname:"描述审核",fieldname:"vresume",fieldvalue:'',inputtype:"text", twidth:"180" ,height:"",issort:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
					return setname(trId,'isresume','vresume');
				}},
		{fieldcnname:"付费类型",fieldname:"type",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"付费"},{"value_no":1,"value_name":"免费"}],twidth:"60" ,height:"",issort:false},
		{fieldcnname:"付费类型审核",fieldname:"vtype",fieldvalue:'',inputtype:"text",twidth:"90" ,height:"",issort:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
					return setname(trId,'ispay','vtype');
				}},
		{fieldcnname:"创建时间",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",hide:true},
		{fieldcnname:"更新时间",fieldname:"update_time",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",hide:true},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"已审核"},{"value_no":2,"value_name":"未审核"}], twidth:"60" ,height:"",issort:false},
		{fieldcnname:"经度",fieldname:"longitude",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",edit:false},
		{fieldcnname:"纬度",fieldname:"latitude",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",edit:false},
		{fieldcnname:"上传人",fieldname:"upload_uin",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",edit:false,issort:false}
		];
var _verifyparkT = new TQTable({
	tabletitle:"已审核停车场",
	ischeck:false,
	tablename:"verifypark_tables",
	dataUrl:"parking.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=ugcquery",
	tableObj:T("#verifyparkobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function getAuthButtons(){
	var bus = [];
	if(role!=6&&role!=8)
	
	bus.push({dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_verifyparkT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
			if(o.fieldname=='strid'||o.fieldname=='nickname'||o.fieldname=='cmobile')
				o.shide=true;
		});
		Twin({Id:"verifypark_search_w",Title:"搜索停车场",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "verifypark_search_f",
					formObj:tObj,
					formWinId:"verifypark_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("verifypark_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_verifyparkT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}});
	bus.push({dname:"返回停车场管理",icon:"edit_add.png",onpress:function(Obj){
		location = "parking.do";
	}});
	bus.push({dname:"已审核UGC停车场",icon:"edit_add.png",onpress:function(Obj){
		location = "parking.do?action=ugc&state=0";
	}});
	bus.push({dname:"未审核UGC停车场",icon:"edit_add.png",onpress:function(Obj){
		location = "parking.do?action=ugc&state=2";
	}});
	return bus;
}
function getAuthIsoperateButtons(){
	var bts = [];
	
	bts.push({name:"审核通过",fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认审核通过吗",Content:"确认审核通过吗",OKFn:function(){T.A.sendData("verifypark.do?action=verify","post","selids="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"审核成功！",2,"");
					_verifyparkT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});

	if(bts.length <= 0){return false;}
	return bts;
}
function setname(pid,type,colname){
	T.A.C({
		url:"parking.do?action=getver&id="+pid+"&type="+type,
	  		method:"GET",//POST or GET
	  		param:"",//GET时为空
	  		async:false,//为空时根据是否有回调函数(success)判断
	  		dataType:"0",//0text,1xml,2obj
	  		success:function(ret,tipObj,thirdParam){
	  			if(ret&&ret!='null'){
	  				if(ret!='0/0'){
	  					var v = "<a href ='#' onclick='viewdetail(\""+pid+"\",\""+type+"\")'>"+ret+"</a>";
						updateRow(pid,colname,v);
	  				}else
	  					updateRow(pid,colname,ret);
	  			}
		},//请求成功回调function(ret,tipObj,thirdParam) ret结果
	  		failure:function(ret,tipObj,thirdParam){
			return false;
		},//请求失败回调function(null,tipObj,thirdParam) 默认提示用户<网络失败>
	  		thirdParam:"",//回调函数中的第三方参数
	  		tipObj:null,//相关提示父级容器(值为字符串"notip"时表示不进行相关提示)
	  		waitTip:"正在获取审核数据...",
	  		noCover:true
	})
	return "<font style='color:#666'>获取中...</font>";
}

/*更新表格内容*/
function updateRow(rowid,name,value){
	//alert(value);
	if(value)
		_verifyparkT.UCD(rowid,name,value);
}
function viewdetail(id,type){
	//alert(id+","+type);
	var name = _verifyparkT.GD(id,"company_name")
	Twin({
		Id:"vpark_detail_"+id,
		Title:"评价详情  -->"+name+"&nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
		Content:"<iframe src=\"parking.do?action=verifydetail&id="+id+"&type="+type+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
		Width:T.gww()/2,
		Height:T.gwh()/1.5
	})
}
_verifyparkT.C();
</script>

</body>
</html>
