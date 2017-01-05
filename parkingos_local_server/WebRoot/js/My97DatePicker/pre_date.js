var isChange = true;
function SelectOptionByValue(select,value)
{
	for(var i=0;i<select.options.length;i++)
	{
		if(select.options[i].value == value)
		{
			select.options[i].selected = true;
			return;
		}
	}
}
function Reset_rangeSelect(value){
	isChange = false;
	SelectOptionByValue(document.getElementById("rangeSelect"),value)
	setTimeout(function(){isChange = true;},500);
}
function changeRange(sel)
{
	if(!isChange)
		return;
	var startDateSelectObj = document.getElementById('startDateSelect_field');
	var endDateSelectObj = document.getElementById('endDateSelect_field');
	var seconddateinputObj = document.getElementById('seconddateinput');
	var firstdateinputObj = document.getElementById('firstdateinput');
	var tipsObj = document.getElementById('tips');
	var a = sel.options[sel.selectedIndex].value;   

    	firstdateinputObj.style.display = "";
    	seconddateinputObj.style.display = "";
    	tipsObj.innerHTML = ""
		
    if(a=='selfdefine'){
    	tipsObj.innerHTML = "&nbsp;&nbsp;请选择自定义查询方式";
    	firstdateinputObj.style.display = "none";
    	seconddateinputObj.style.display = "none";
    }else if(a=='quicksearch'){
    	tipsObj.innerHTML = "&nbsp;&nbsp;请选择快捷查询方式";
    	firstdateinputObj.style.display = "none";
    	seconddateinputObj.style.display = "none";
    }else if(a==1){
    	tipsObj.innerHTML = "&nbsp;&nbsp;如：2010-10-10，则按小时统计10号的数据。";
    	seconddateinputObj.innerHTML= "";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  onclick=\" WdatePicker({minDate:'%y-{%M-1}-%d',dateFmt:'yyyy-MM-dd',maxDate:\'%y-%M-%d\'})\" \/>";
    }else if(a==2){
    	tipsObj.innerHTML = "&nbsp;&nbsp;如：2010-10-10 至 2010-10-20，则以天为单位统计10号至20号的数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  onclick=\"var endDateSelect_field=$dp.$(\'endDateSelect_field\');WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){endDateSelect_field.focus();},maxDate:\'#F{$dp.$D(\\\'endDateSelect_field\\\',{d:-1})||$dp.$DV(\\\'%y-%M-%d\\\',{d:-1})}\'})\" \/>";
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',minDate:\'#F{$dp.$D(\\\'startDateSelect_field\\\',{d:1})}\',maxDate:\'#F{$dp.$D(\\\'startDateSelect_field\\\',{d:60})}\'})\" \/>";
    }else if(a==3){//一天，以时为单位
    	tipsObj.innerHTML = "&nbsp;&nbsp;如：2013-10-11 08:00:00至 2013-12-11 09:10:59，则时间单位2013-10-11 08:00:00至2013-12-11 09:10:59的数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\"  style='width:142px;' type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  onclick=\"var endDateSelect_field=$dp.$(\'endDateSelect_field\');WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',onpicked:function(){endDateSelect_field.focus();},maxDate:\'%y-%M-%d\',minDate:\'%y-{%M-2}-%d\'})\" \/>";
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\"  style='width:142px;' type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:\'#F{$dp.$D(\\\'startDateSelect_field\\\',{d:0})}\',maxDate:\'#F{$dp.$DV(\\\'%y-%M-%d\\\',{d:1})}\'})\" \/>";
    }else if(a==4){
    	var today = Date.today();
    	tipsObj.innerHTML = "&nbsp;&nbsp;如：2010-10-10，则统计10号的汇总数据。";
    	seconddateinputObj.innerHTML= "";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  value=\""+ today.toString('yyyy-MM-dd') +"\"  onclick=\"WdatePicker({minDate:'%y-{%M-1}-%d'},dateFmt:'yyyy-MM-dd',maxDate:\'%y-%M-%d\'})\" \/>";
    }else if(a==5){
    	tipsObj.innerHTML = "&nbsp;&nbsp;如：2010-10，则按月统计10月份的汇总数据。";
    	seconddateinputObj.innerHTML= "";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  onclick=\"WdatePicker({minDate:'%y-{%M-1}-%d'},dateFmt:'yyyy-MM-dd',dateFmt:\'yyyy-M\',maxDate:\'#F{$dp.$DV(\\\'%y-%M\\\')}\'})\" \/>";
		
    }else if(a=='preYear'){
    	var today = Date.today();
    	today.addYears(-1);
    	today.set({ month:0,day:1});
    	tipsObj.innerHTML = "&nbsp;&nbsp;以月为单位统计去年的数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  onclick=\"var endDateSelect_field=$dp.$(\'endDateSelect_field\');WdatePicker({minDate:'%y-{%M-1}-%d'},dateFmt:'yyyy-MM-dd',dateFmt:\'yyyy-M\',onpicked:function(){endDateSelect_field.focus();},maxDate:\'#F{$dp.$D(\\\'endDateSelect_field\\\',{M:-1})||$dp.$DV(\\\'%y-%M\\\',{M:-1})}\'});Reset_rangeSelect(3);\"  value=\""+ today.toString('yyyy-MM') +"\"  \/>";
    	today.set({ month:11}); 
    	today.moveToLastDayOfMonth();
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',dateFmt:\'yyyy-M\',minDate:\'#F{$dp.$D(\\\'startDateSelect_field\\\',{M:1})}\',maxDate:\'%y-%M\'});Reset_rangeSelect(3);\"  value=\""+ today.toString('yyyy-MM') +"\" \/>";
		
		
    }else if(a=='currentYear'){
    	var today = Date.today();
    	today.set({ month:0,day:1});
		
    	tipsObj.innerHTML = "&nbsp;&nbsp;以月为单位统计今年的数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  onclick=\"var endDateSelect_field=$dp.$(\'endDateSelect_field\');WdatePicker({minDate:'%y-{%M-1}-%d'},dateFmt:'yyyy-MM-dd',dateFmt:\'yyyy-M\',onpicked:function(){endDateSelect_field.focus();},maxDate:\'#F{$dp.$D(\\\'endDateSelect_field\\\',{M:-1})||$dp.$DV(\\\'%y-%M\\\',{M:-1})}\'});Reset_rangeSelect(3);\"  value=\""+ today.toString('yyyy-MM') +"\"  \/>";
    	today = Date.today();
		today.addMonths(-1);
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',dateFmt:\'yyyy-M\',minDate:\'#F{$dp.$D(\\\'startDateSelect_field\\\',{M:1})}\',maxDate:\'#F{$dp.$DV(\\\'%y-%M-%d\\\',{M:-1})}\'});Reset_rangeSelect(3);\"  value=\""+ today.toString('yyyy-MM') +"\" \/>";
		
    }
	else if(a=='preYear_day'){
    	var today = Date.today();
    	today.addYears(-1);
    	today.set({ month:0,day:1});
    	tipsObj.innerHTML = "&nbsp;&nbsp;以天为单位统计去年的数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  onclick=\"var endDateSelect_field=$dp.$(\'endDateSelect_field\');WdatePicker({minDate:'%y-{%M-1}-%d'},dateFmt:'yyyy-MM-dd',onpicked:function(){endDateSelect_field.focus();},maxDate:\'#F{$dp.$D(\\\'endDateSelect_field\\\',{M:-1})||$dp.$DV(\\\'%y-%M-%d\\\',{D:-1})}\'});Reset_rangeSelect(2);\"  value=\""+ today.toString('yyyy-MM-dd') +"\"  \/>";
    	today.set({ month:11}); 
    	today.moveToLastDayOfMonth();
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\" onclick=\"WdatePicker({minDate:'%y-{%M-1}-%d'},dateFmt:'yyyy-MM-dd',minDate:\'#F{$dp.$D(\\\'startDateSelect_field\\\',{M:1})}\',maxDate:\'%y-%M-%d\'});Reset_rangeSelect(2);\"  value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";
		
		
    }else if(a=='currentYear_day'){
    	var today = Date.today();
    	today.set({ month:0,day:1});
		
    	tipsObj.innerHTML = "&nbsp;&nbsp;以月为单位统计今年的数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  onclick=\"var endDateSelect_field=$dp.$(\'endDateSelect_field\');WdatePicker({minDate:'%y-{%M-1}-%d'},dateFmt:'yyyy-MM-dd',onpicked:function(){endDateSelect_field.focus();},maxDate:\'#F{$dp.$D(\\\'endDateSelect_field\\\',{M:-1})||$dp.$DV(\\\'%y-%M-%d\\\',{D:-1})}\'});Reset_rangeSelect(2);\"  value=\""+ today.toString('yyyy-MM-dd') +"\"  \/>";
    	today = Date.today();
		//today.addMonths(-1);
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',minDate:\'#F{$dp.$D(\\\'startDateSelect_field\\\',{M:1})}\',maxDate:\'#F{$dp.$DV(\\\'%y-%M-%d\\\',{M:0})}\'});Reset_rangeSelect(2);\"  value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";
		
    }
	else if(a=='nextYear'){
    	var today = Date.today();
    	today.addYears(1);
    	today.set({ month:0,day:1});
    	startDateSelectObj.value = today.toString('yyyy-MM-dd');
    	today.set({ month:11}); 
    	today.moveToLastDayOfMonth();
    	endDateSelectObj.value=today.toString('yyyy-MM-dd');
    }else if(a=='preSeason'){
    	var today = Date.today();
    	
    	var s1= today.set({ month:0,day:1}).clone();
    	var s2= today.set({ month:3,day:1}).clone();
    	var s3= today.set({ month:6,day:1}).clone();
    	var s4= today.set({ month:9,day:1}).clone();
    	
    	var e1= today.set({ month:2,day:1}).moveToLastDayOfMonth().clone();
    	var e2= today.set({ month:5,day:1}).moveToLastDayOfMonth().clone();
    	var e3= today.set({ month:8,day:1}).moveToLastDayOfMonth().clone();
    	var e4= today.set({ month:11,day:1}).moveToLastDayOfMonth().clone();
    	
    	var today = Date.today();
		var FTstartDate,FTendDate
    	if(today.compareTo(s4)>=0){
    		FTstartDate = s3.toString('yyyy-MM-dd');
    		FTendDate=e3.toString('yyyy-MM-dd');
    	}else if(today.compareTo(s3)>=0){
    		FTstartDate = s2.toString('yyyy-MM-dd');
    		FTendDate=e2.toString('yyyy-MM-dd');
    	}else if(today.compareTo(s2)>=0){
    		FTstartDate = s1.toString('yyyy-MM-dd');
    		FTendDate=e1.toString('yyyy-MM-dd');
    	}else {
    		FTstartDate = s4.addYears(-1).toString('yyyy-MM-dd');
    		FTendDate=s4.addMonths(2).moveToLastDayOfMonth().toString('yyyy-MM-dd');
    	}
    	tipsObj.innerHTML = "&nbsp;&nbsp;以天为单位统计上个季度数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  onclick=\"var endDateSelect_field=$dp.$(\'endDateSelect_field\');WdatePicker({minDate:'%y-{%M-1}-%d'},dateFmt:'yyyy-MM-dd',onpicked:function(){endDateSelect_field.focus();},maxDate:\'#F{$dp.$D(\\\'endDateSelect_field\\\',{d:-1})||$dp.$DV(\\\'%y-%M-%d\\\',{d:-1})}\'});Reset_rangeSelect(2);\"  value=\""+ FTstartDate +"\" \/>";
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\" onclick=\"WdatePicker({minDate:'%y-{%M-1}-%d'},dateFmt:'yyyy-MM-dd',minDate:\'#F{$dp.$D(\\\'startDateSelect_field\\\',{d:1})}\',maxDate:\'%y-%M-%d\'});Reset_rangeSelect(2);\"  value=\""+ FTendDate +"\" \/>";
		
    }else if(a=='currentSeason'){
    	var today = Date.today();
    	
    	var s1= today.set({ month:0,day:1}).clone();
    	var s2= today.set({ month:3,day:1}).clone();
    	var s3= today.set({ month:6,day:1}).clone();
    	var s4= today.set({ month:9,day:1}).clone();
    	
    	var e1= today.set({ month:2,day:1}).moveToLastDayOfMonth().clone();
    	var e2= today.set({ month:5,day:1}).moveToLastDayOfMonth().clone();
    	var e3= today.set({ month:8,day:1}).moveToLastDayOfMonth().clone();
    	var e4= today.set({ month:11,day:1}).moveToLastDayOfMonth().clone();
    	
    	var today = Date.today();
		var FTstartDate,FTendDate
    	if(today.compareTo(s4)>=0){
    		FTstartDate = s4.toString('yyyy-MM-dd');
    		FTendDate=e4.toString('yyyy-MM-dd');
    	}else if(today.compareTo(s3)>=0){
    		FTstartDate = s3.toString('yyyy-MM-dd');
    		FTendDate=e3.toString('yyyy-MM-dd');
    	}else if(today.compareTo(s2)>=0){
    		FTstartDate = s2.toString('yyyy-MM-dd');
    		FTendDate=e2.toString('yyyy-MM-dd');
    	}else {
    		FTstartDate = s1.toString('yyyy-MM-dd');
    		FTendDate=e1.toString('yyyy-MM-dd');
    	}
    	
		tipsObj.innerHTML = "&nbsp;&nbsp;以天为单位统计本季度数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  onclick=\"var endDateSelect_field=$dp.$(\'endDateSelect_field\');WdatePicker({minDate:'%y-{%M-1}-%d'},dateFmt:'yyyy-MM-dd',onpicked:function(){endDateSelect_field.focus();},maxDate:\'#F{$dp.$D(\\\'endDateSelect_field\\\',{d:-1})||$dp.$DV(\\\'%y-%M-%d\\\',{d:-1})}\'});Reset_rangeSelect(2);\"  value=\""+ FTstartDate +"\" \/>";
        today = Date.today();
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',minDate:\'#F{$dp.$D(\\\'startDateSelect_field\\\',{d:1})}\',maxDate:\'%y-%M-%d\'});Reset_rangeSelect(2);\"  value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";

		
		
    }else if(a=='nextSeason'){
    	var today = Date.today();
    	
    	var s1= today.set({ month:0,day:1}).clone();
    	var s2= today.set({ month:3,day:1}).clone();
    	var s3= today.set({ month:6,day:1}).clone();
    	var s4= today.set({ month:9,day:1}).clone();
    	
    	var e1= today.set({ month:2,day:1}).moveToLastDayOfMonth().clone();
    	var e2= today.set({ month:5,day:1}).moveToLastDayOfMonth().clone();
    	var e3= today.set({ month:8,day:1}).moveToLastDayOfMonth().clone();
    	var e4= today.set({ month:11,day:1}).moveToLastDayOfMonth().clone();
    	
    	var today = Date.today();
    	if(today.compareTo(s4)>=0){
    		startDateSelectObj.value = s1.addYears(1).toString('yyyy-MM-dd');
    		endDateSelectObj.value=s1.addMonths(2).moveToLastDayOfMonth().toString('yyyy-MM-dd');
    	}else if(today.compareTo(s3)>=0){
    		startDateSelectObj.value = s4.toString('yyyy-MM-dd');
    		endDateSelectObj.value=e4.toString('yyyy-MM-dd');
    	}else if(today.compareTo(s2)>=0){
    		startDateSelectObj.value = s3.toString('yyyy-MM-dd');
    		endDateSelectObj.value=e3.toString('yyyy-MM-dd');
    	}else {
    		startDateSelectObj.value = s2.toString('yyyy-MM-dd');
    		endDateSelectObj.value=e2.toString('yyyy-MM-dd');
    	}
    }else if(a=='preMon'){
    	var today = Date.today();
    	today.addMonths(-1);
    	today.set({day:1});
    	tipsObj.innerHTML = "&nbsp;&nbsp;以天为单位统计上个月数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"   value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";
    	today.moveToLastDayOfMonth();
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\"   value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";
		
    }else if(a=='currentMon'){
    	var today = Date.today();
    	today.set({day:1});
    	tipsObj.innerHTML = "&nbsp;&nbsp;以天为单位统计本月数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";
        today = Date.today();
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\"  value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";
		
		
    }else if(a=='nextMon'){
    	var today = Date.today();
    	today.addMonths(1);
    	today.set({day:1});
    	startDateSelectObj.value = today.toString('yyyy-MM-dd');
    	today.moveToLastDayOfMonth();
    	endDateSelectObj.value=today.toString('yyyy-MM-dd');
    }else if(a=='preWeek'){
    	var today = Date.today();
    	today.addWeeks(-2);
    	today.monday();//sunday
		
    	tipsObj.innerHTML = "&nbsp;&nbsp;以天为单位统计上周数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";
    	today.sunday();//saturday
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\" value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";
		
    }else if(a=='currentWeek'){
    	var today = Date.today();
    	today.addWeeks(-1);
    	today.monday();//sunday
    	tipsObj.innerHTML = "&nbsp;&nbsp;以天为单位统计本周数据";
    	firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"    value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";
    	today = Date.today();
    	seconddateinputObj.innerHTML = "&nbsp;&nbsp;至&nbsp;&nbsp;<input class=\"Wdate\" type=\"text\" name=\"endDateSelect\" id=\"endDateSelect_field\" align=\"absmiddle\"  value=\""+ today.toString('yyyy-MM-dd') +"\" \/>";
    }else if(a=='nextWeek'){
    	var today = Date.today();
    	today.monday();
    	startDateSelectObj.value = today.toString('yyyy-MM-dd');
    	today.sunday();
    	endDateSelectObj.value=today.toString('yyyy-MM-dd');
    }else if(a=='preDay'){
    	var today = Date.today();
    	today.addDays(-1);
    	startDateSelectObj.value = today.toString('yyyy-MM-dd');
		firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  value=\""+ today.toString('yyyy-MM-dd') +"\"   readonly  \/>";
    	seconddateinputObj.innerHTML = "";
    	tipsObj.innerHTML = "&nbsp;&nbsp;如：2010-10-10，则统计10号的汇总数据。";
    }else if(a=='preDay1'){
    	var today = Date.today();
    	today.addDays(-1);
    	startDateSelectObj.value = today.toString('yyyy-MM-dd');
		firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  value=\""+ today.toString('yyyy-MM-dd') +"\"   readonly \/>";
    	seconddateinputObj.innerHTML = "";
    	tipsObj.innerHTML = "&nbsp;&nbsp;如：2010-10-10，则按小时统计10号的数据。";
    }else if(a=='currentDay'){
    	var today = Date.today();
    	startDateSelectObj.value = today.toString('yyyy-MM-dd');
		firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  value=\""+ today.toString('yyyy-MM-dd') +"\"   readonly  \/>";
    	seconddateinputObj.innerHTML = "";
    	tipsObj.innerHTML = "&nbsp;&nbsp;如：2010-10-10，则统计10号的汇总数据。";
    }else if(a=='currentDay1'){
    	var today = Date.today();
    	startDateSelectObj.value = today.toString('yyyy-MM-dd');
		firstdateinputObj.innerHTML = "&nbsp;&nbsp;<input  class=\"Wdate\" type=\"text\" name=\"startDateSelect\" id=\"startDateSelect_field\" align=\"absmiddle\"  value=\""+ today.toString('yyyy-MM-dd') +"\"   readonly \/>";
    	seconddateinputObj.innerHTML = "";
    	tipsObj.innerHTML = "&nbsp;&nbsp;如：2010-10-10，则按小时统计10号的数据。";
    }else if(a=='nextDay'){
    	var today = Date.today();
    	today.addDays(1);
    	startDateSelectObj.value = today.toString('yyyy-MM-dd');
    	endDateSelectObj.value=today.toString('yyyy-MM-dd');
    }
	//document.getElementById('tips').innerHTML = "&nbsp;&nbsp;"+GetSelectedOptionText("rangeSelect");

};
