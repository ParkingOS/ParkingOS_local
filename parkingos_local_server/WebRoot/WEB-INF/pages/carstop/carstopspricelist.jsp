<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>泊车点价格价格管理</title>
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
<div id="carstopspriceobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
function getCstops(){
	return eval(T.A.sendData("carstopsprice.do?action=getcids"));
}
var role=${role};
var cstops = getCstops();
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",inputtype:"text", twidth:"50" ,issort:false,edit:false},
		{fieldcnname:"名称",fieldname:"cid",inputtype:"select", noList:cstops,twidth:"150" ,issort:false},
		{fieldcnname:"类型",fieldname:"type",inputtype:"select", twidth:"50",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"临停"},{"value_no":1,"value_name":"常停"}] ,issort:false},
		{fieldcnname:"首价格",fieldname:"first_price",inputtype:"text", twidth:"100" ,issort:false},
		{fieldcnname:"首计价单位(分钟)",fieldname:"first_unit",inputtype:"text",twidth:"120" ,issort:false},
		{fieldcnname:"价格",fieldname:"next_price",inputtype:"text",twidth:"100" ,issort:false},
		{fieldcnname:"计价单位 (分钟)",fieldname:"next_unit",inputtype:"text", twidth:"120",issort:false},
		{fieldcnname:"优惠价格",fieldname:"fav_price",inputtype:"text",twidth:"100" ,issort:false},
		{fieldcnname:"优惠计价单位 (分钟)",fieldname:"fav_unit",inputtype:"text", twidth:"120",issort:false},
		{fieldcnname:"最高价格",fieldname:"top_price",inputtype:"text",twidth:"100" ,issort:false},
		{fieldcnname:"创建 人",fieldname:"creator",inputtype:"text",twidth:"70" ,issort:false,edit:false},
		{fieldcnname:"备注",fieldname:"resume",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"创建日期",fieldname:"ctime",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"修改日期 ",fieldname:"utime",inputtype:"date", twidth:"128" ,issort:false,edit:false}
		
	];
var rules =[{name:"strid",type:"ajax",url:"carstopsprice.do?action=check&value=",requir:true,warn:"账号已存在！",okmsg:""}];
function viewpic(name){
	var url = 'viewpic.html?name='+name;
	Twin({Id:"carstopsprice_edit_pic",Title:"查看照片",Width:850,Height:600,sysfunI:"v_pic",
			Content:"<iframe id='tactic_iframe' src='"+url+"' style='width:100%;height:100%;' frameborder='0' ></iframe>",
			buttons :[],
			CloseFn:function(){
				
			}
		})
}
var _carstopspriceT = new TQTable({
	tabletitle:"泊车点价格管理",
	ischeck:false,
	tablename:"carstopsprice_tables",
	dataUrl:"carstopsprice.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query",
	tableObj:T("#carstopspriceobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"注册泊车点价格",icon:"edit_add.png",onpress:function(Obj){
		T.each(_carstopspriceT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"carstopsprice_add",Title:"添加泊车点价格",Width:550,sysfun:function(tObj){
				Tform({
					formname: "carstopsprice_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"carstopsprice.do?action=create",
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("carstopsprice_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("carstopsprice_add");
							_carstopspriceT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_carstopspriceT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"carstopsprice_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "carstopsprice_search_f",
					formObj:tObj,
					formWinId:"carstopsprice_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("carstopsprice_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_carstopspriceT.C({
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
	bts.push({name:"编辑",fun:function(id){
		T.each(_carstopspriceT.tc.tableitems,function(o,j){
			o.fieldvalue = _carstopspriceT.GD(id)[j]
		});
		Twin({Id:"carstopsprice_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "carstopsprice_edit_f",
					formObj:tObj,
					recordid:"carstopsprice_id",
					suburl:"carstopsprice.do?action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_carstopspriceT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("carstopsprice_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("carstopsprice_edit_"+id);
							_carstopspriceT.M()
						}else{
							T.loadTip(1,ret,2,o)
						}
					}
				});	
			}
		})
	}});
	bts.push({name:"删除",fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("carstopsprice.do?action=delete","post","id="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_carstopspriceT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_carstopspriceT.C();
</script>

</body>
</html>
