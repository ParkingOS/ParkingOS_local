<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>推荐</title>
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
<div id="recommendobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true,shide:true},
		{fieldcnname:"推荐人",fieldname:"uin",fieldvalue:'',inputtype:"text", twidth:"250" ,height:"",hide:true},
		{fieldcnname:"推荐手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"120" ,height:"",hide:true},
		{fieldcnname:"推荐日期",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"推荐类型",fieldname:"type",fieldvalue:'',inputtype:"select",noList:[{"value_no":"-1","value_name":"全部"},{"value_no":"0","value_name":"用户"},{"value_no":"1","value_name":"停车场"}], twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select", noList:[{"value_no":"-1","value_name":"全部"},{"value_no":"0","value_name":"推荐中"},{"value_no":"1","value_name":"推荐成功"}],twidth:"100" ,height:"",issort:false,edit:false}
	];
var _recommendT = new TQTable({
	tabletitle:"本周推荐",
	ischeck:false,
	tablename:"recommend_tables",
	dataUrl:"recommend.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query",
	tableObj:T("#recommendobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [
			{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
			T.each(_recommendT.tc.tableitems,function(o,j){
				o.fieldvalue ="";
			});
			Twin({Id:"recommend_search_w",Title:"搜索推荐",Width:550,sysfun:function(tObj){
					TSform ({
						formname: "recommend_search_f",
						formObj:tObj,
						formWinId:"recommend_search_w",
						formFunId:tObj,
						formAttr:[{
							formitems:[{kindname:"",kinditemts:_mediaField}]
						}],
						buttons : [//工具
							{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("recommend_search_w");} }
						],
						SubAction:
						function(callback,formName){
							_recommendT.C({
								cpage:1,
								tabletitle:"高级搜索结果",
								extparam:"&action=highquery&"+Serializ(formName)
							})
						}
					});	
				}
			})
		
		}},
		{dname:"全部推荐",icon:"edit_add.png",onpress:function(Obj){
			_recommendT.C({
				cpage:1,
				tabletitle:"全部推荐",
				extparam:"&action=query"
			})}
		
		},{dname:"本周推荐",icon:"edit_add.png",onpress:function(Obj){
			_recommendT.C({
				cpage:1,
				tabletitle:"本周推荐",
				extparam:"&action=query&week=current"
			})}
		
		},{dname:"上周推荐",icon:"edit_add.png",onpress:function(Obj){
			_recommendT.C({
				cpage:1,
				tabletitle:"上周推荐",
				extparam:"&action=query&week=last"
			})}
		}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	if(bts.length <= 0){return false;}
	return bts;
}
_recommendT.C();
</script>

</body>
</html>
