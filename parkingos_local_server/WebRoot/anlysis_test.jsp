<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>统计分析菜单</title>
<link rel="stylesheet" type="text/css" href="js/anlysis/style.css?v=20100302" />
<script type="text/javascript" src="js/anlysis/jquery.min.js"></script>
<script type="text/javascript" src="js/anlysis/highcharts.js"></script>
<script type="text/javascript" src="js/anlysis/aanalysis.js"></script>
</head>
<body onload=hiddlecontent('currentMon')>
<div id="data_container">
<!--顶部开始-->
<div class="top">
<ul class="title"><li class="parentmenu">服务指标趋势分析 &raquo; </li><li class="currentmenu">电话量变化趋势</li><li class="counthelp" title="点击查看帮助" onclick="javascript:window.open('http://www.tq.cn/help_3.jsp')"><span class="help"><img src="js/js_tq8/images/spacer.gif" /></span>帮助</li>
</ul>
<ul class="search">
<span class="search_type">
      <select name="department_id"  onChange="allQuery(this)"  id="department_id">
				 <option  selected  value="">全部</option>	  	                     			
				     						<option value='-1'>|--全局</option>
    			     						<option value='10220553'>|--人事行政部</option>
    			     						<option value='10211689'>|--市场拓展部</option>
    			     						<option value='10234447'>|--产品研发部</option>
    			     						<option value='10235812'>|--客户运营部</option>
    			     			</select>
	
  <span id='userListTd'><select name="kefu_uin" id='kefu_uin' >
				 <option value="" >全部</option> 
					
		      </select>
		    </span>
			<!--
  <select name="rangeSelect" id="rangeSelect" size="1" onChange="javascript:changeRange(this);" >
    <option value="selfdefine" disabled>自定义查询&darr;</option>
    <option value="2" >以天为单位(&nbsp;区间&nbsp;)</option>
    <option value="4" >以天为单位(&nbsp;单日&nbsp;)</option>
    <option value="1"  disabled>以时为单位(&nbsp;单日&nbsp;)</option>
    <option value="3" >以月为单位(&nbsp;区间&nbsp;)</option>
    <option value="5" >以月为单位(&nbsp;单月&nbsp;)</option>
    <option value="quicksearch" disabled>快捷查询&darr;</option>
    <option selected="selected" value="currentDay" >今天（按天）</option>
    <option value="preDay" >昨天（按天）</option>
    <option value="currentDay1"  disabled>今天（按时）</option>
    <option value="preDay1"  disabled>昨天（按时）</option>
    <option value="currentWeek" >本周（按天）</option>
    <option value="preWeek" >上周（按天）</option>
    <option value="currentMon" >本月（按天）</option>
    <option value="preMon" >上月（按天）</option>
    <option value="currentSeason" >本季度（按天）</option>
    <option value="preSeason" >上季度（按天）</option>
    <option value="currentYear" >今年（按月）</option>
    <option value="preYear" >去年（按月）</option>
  </select>
  
  -->
<select name="rangeSelect" id="rangeSelect" size="1" onChange="javascript:changeRange(this);" >
  <optgroup label="选择查询方式&darr;">
    <option  value="2" >日区间</option>
    <option  value="4" >单日</option>
    <option  value="3" >月区间</option>
    <option  value="5" >单月</option>
    </optgroup>
  <optgroup label="快捷选择&darr;">
    <option  value="currentDay" >今天</option>
    <option  value="preDay" >昨天</option>
    <option  value="currentWeek" >本周</option>
    <option  value="preWeek" >上周</option>
    <option  value="currentMon" >本月</option>
    <option  value="preMon" >上月</option>
    <option  value="currentSeason" >本季度</option>
    <option  value="preSeason" >上季度</option>
    <option  value="currentYear" >今年</option>
    <option  value="preYear" >去年</option>
   </optgroup>
  </select>
</span>
<span id="firstdateinput" class="search_text">
<input  class="Wdate" type="text" name="startDateSelect" id="startDateSelect_field" align="absmiddle" readonly  onclick="WdatePicker({maxDate:'%y-%M-%d'});Reset_rangeSelect(0);" />
</span>
<span id="seconddateinput" class="search_text">
<input  class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d',minDate:'#F{$dp.$D(\\\'startDateSelect_field\\\',{d:1})}'});Reset_rangeSelect(0);" type="text" name="endDateSelect" id="endDateSelect_field" align="absmiddle" readonly   />
</span>
<span class="search_button">
<span class="button_light_green"><button name="" onclick="hiddlecontent('')"><span class="confirm"><img src="js/js_tq8/images/spacer.gif" /></span>确定</button></span>
</span>
</ul>
</div>
<!--顶部结束-->
<!--工具栏开始-->
<div class="tooles">
<span id="tips" class="count_tips">&nbsp;&nbsp;以天为单位统计本月数据</span>
<span class="tooles_botton">
<span class="button"><button name="" onclick="javascript:print();"><span class="print2"><img src="js/js_tq8/images/spacer.gif" /></span>打印</button></span>
</span>
</div>
<!--工具栏结束-->
<div class="float_clear"></div><!--清除浮动-->

<script type="text/javascript">
</script>

<div id="chart_container" style="width: 100%; height: 400px; margin:10px auto;float:left"></div>

</body>
</html>
