<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>查看评论</title>
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
<div id="parkpraiseobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var _mediaField = [
		{fieldcnname:"编号",fieldname:"comid",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"车场",fieldname:"company_name",fieldvalue:'',inputtype:"text",twidth:"200" ,height:"",issort:false},
		{fieldcnname:"好评总数",fieldname:"zcount",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,shide:true,
			process:function(value,cid,id){
					return "<a href=# onclick=\"viewdetail('z','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
				}},
		{fieldcnname:"差评总数",fieldname:"bcount",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,shide:true,
			process:function(value,cid,id){
					return "<a href=# onclick=\"viewdetail('b','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
				}},
		{fieldcnname:"评论总数",fieldname:"ccount",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,shide:true,
			process:function(value,cid,id){
					return "<a href=# onclick=\"viewdetail('c','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
				}}
	];
var rules =[{name:"nfc_uuid",type:"",url:"",requir:true,warn:"",okmsg:""}];
var _parkpraiseT = new TQTable({
	tabletitle:"查看评论",
	ischeck:false,
	tablename:"parkpraise_tables",
	dataUrl:"parkpraise.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery",
	tableObj:T("#parkpraiseobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_parkpraiseT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"parkpraise_search_w",Title:"搜索评论",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "parkpraise_search_f",
					formObj:tObj,
					formWinId:"parkpraise_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("parkpraise_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_parkpraiseT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	if(bts.length <= 0){return false;}
	return bts;
}

function viewdetail(type,value,id){
	var park =_parkpraiseT.GD(id,"company_name");
	var tip = "好评列表";
	if(type=='b'){
		tip = "差评列表";
	}else if(type == 'c'){
		tip = "评论列表";
	}
	Twin({
		Id:"parkpraise_detail_"+id,
		Title:tip+"  --> 停车场："+park,
		Width:T.gww()-100,
		Height:T.gwh()-50,
		sysfunI:id,
		Content:"<iframe name='parkpraise_detail_'"+id+" id='parkpraise_detail_'"+id+" src='parkpraise.do?action=detail&type="+type+"&parkid="+id+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}

_parkpraiseT.C();
</script>

</body>
</html>
