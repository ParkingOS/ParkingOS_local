<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<div style="width:100%;position:relative">
    <div id='customerInfoDiv' class="telinfo" style='height:100px;width:100%;float:left;position:relative;'><@common:baseInfo:customerInfoDiv@></div>
    <div id='operateTabdivt' class="relationmenu" style='border-top:1px solid #CCC;padding-top:1px;height:30px;width:100%;float:left;position:relative'></div>
    <div id='operateListDiv'  class="relationcontainer" style='height:auto;width:100%;float:left;position:relative;border-bottom:1px solid #CCC;'>		
    	<@tab:operateList=workBillModel|customerInfo^课程咨询|callInfo:operateListDiv|operateTabdivt@>
    </div>
    <div id='relationTabdivt' class="relationmenu" style='border-top:1px solid #CCC;margin-top:20px;padding-top:1px;height:30px;width:100%;float:left;position:relative'></div>
    <div id='relationTabDiv'  class="relationcontainer" style='height:auto;width:100%;float:left;position:relative;border-bottom:1px solid #CCC;'>		
    	<@tab:relationList=markList|historyCall|workOrderList:relationTabDiv|relationTabdivt@>
    </div>
    <span id=\"kehuInfo\" style='color:#c00'></span>
</div>