function custom_reset(){
	var elements  = document.getElementsByTagName('input');
    for(var i=0;i<elements.length;i++){
    	 var _id =elements[i].id;
    	 if(_id.indexOf('input_')!=-1)
    		elements[i].value='';
    	 if(_id.indexOf('start')!=-1||_id.indexOf('end')!=-1)
    		elements[i].style.display='';
    }
    var selects  = document.getElementsByTagName('select');
    for(var j=0;j<selects.length;j++){
    	 if(selects[j].id.indexOf('input_')!=-1)
    		selects[j].options.selectedIndex=0;
    }
    if(document.getElementById('input_department_id'))
  	 	document.getElementById('input_department_id').options.selectedIndex=0;
   	if(document.getElementById('input_kefu_uin'))
  		document.getElementById('input_kefu_uin').options.selectedIndex=0;
}



function change_input_to_select(parentId,input_name,oper,start,end,isdate,issystemfield){
    document.getElementById(parentId).innerHTML = create_select_options(input_name,oper,start,end,isdate,issystemfield);
	//document.getElementById(parentId).className =""; 
    setupinput(oper,input_name);
}

function setupinput(value,objname){
	if(value=='notnull'||value=='null'){
		document.getElementById("input_"+objname+"_start").style.display='none';
		document.getElementById("input_"+objname+"_end").style.display='none';
	}else if(value=='1'||value=='2'||value=='3'){
	    document.getElementById("input_"+objname+"_start").style.display='';
		document.getElementById("input_"+objname+"_end").style.display='none';
	}else if(value=='between'){
		document.getElementById("input_"+objname+"_start").style.display='';
		document.getElementById("input_"+objname+"_end").style.display='';
	}
}

function create_select_options(select_name,oper,start1,end1,isdate,issystemfield){
	var start=select_name+"_start";
	var end  =select_name+"_end";
	var htmlContent= "<select id='input_"+select_name+"' name='"+select_name+"' style='width:50px;' "+
			"onchange='setupinput(this.value,\""+select_name+"\");'>"+
			"<option value='between' "+setOptions('between',oper)+" >区间</option>"+
			"<option value='null' "+setOptions('null',oper)+">为空</option><option value='notnull' "+setOptions('notnull',oper)+">非空</option>"+
			"<option value='1' "+setOptions('1',oper)+">&gt;=</option><option value='2' "+setOptions('2',oper)+">&lt;=</option>"+
			"<option value='3' "+setOptions('3',oper)+">=</option></select>"+
			"<input  value='"+start1+"'  name='"+start+"'  type='text' id='input_"+start+"' ";
			if(isdate=='true')
				htmlContent+=" onClick=\"WdatePicker({startDate:'%y-%M-01 00:00',alwaysUseStartDate:true});\" style='width:100px;margin-left:1px;' ";
			else
				htmlContent+=" style='width:57px;margin-left:1px;' ";
			if(select_name!='price')
				 htmlContent+=" onkeyup='repalce_input_char(this)'  ";
			else
				htmlContent+=" onkeyup='checkDouble(this)' ";
			htmlContent+="ondblclick=\"this.value=''\" title=''  />"+
					"<input  value='"+end1+"'   name='"+end+"'  type='text' id='input_"+end+"' ";
			if(isdate=='true')
				htmlContent+= " onClick=\"WdatePicker({startDate:'%y-%M-01 00:00',alwaysUseStartDate:true});\" style='width:100px;margin-left:1px;' ";
			else
				htmlContent+=" style='width:57px;margin-left:1px;' ";
			if(select_name!='price')
				 htmlContent+=" onkeyup='repalce_input_char(this)'  ";
			else
				htmlContent+=" onkeyup='checkDouble(this)' "; 
			htmlContent+="ondblclick=\"this.value=''\" title='' style='margin-left:1px;width:100px;'/>";
			//alert(htmlContent);
	return htmlContent;		
}

function repalce_input_char(obj){
	obj.value = obj.value.replace(/\D/g,'');
}
function setOptions(v1,v2){
	if(v1==v2)
		return " selected  ";
	else return "";
}
function checkDouble(obj){
	var value = obj.value;
	if(value!=''){
		var f = true;
		for(var i =0;i<value.length;i++){
			 var c =value.charAt(i);
			 if(!isNum(c)&&c!='.'){
			 	f = false;
				break;
			 }
		}
		if(!f){
			tqshortalert('价格不合法，必须是小数!');
			obj.select();
		}
	}
}

function isNum(ch){
	//alert(Math.ceil('0.01'));
	if(ch >= '0' && ch <= '9')return true;
	return false;
}
