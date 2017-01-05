<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>真来电ETC</title>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<style type="text/css">
	 *{font-size: 24px;}
    body{height: 100%;}
    body{background-color:Transparent;}
    input,select{border-radius:10px;}
    #content{ padding: 50px;}
	.tc{text-align:center;margin-top:130px}
	.ntb th,.ntb td{border:none;}
	.w150{width:150px}
	.p10{padding:10px;}
	.w200{width:200px}
	.ml30{margin-left:30px}
	.c{width: 400px;height: 50px;line-height: 50px;margin: 20px;}
 </style>

</head>
<body>
<div id="content" class="tc">
<form action="nfchandle.do" method="post">       
 <table class="ntb" style="margin: auto;">
 			
            <tr>
                <td class="w150">
                  开始时间：
                </td>
                <td class="w500"><input class="c"  name="btime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd hh:mm:ss',startDate:'%y-%M-01',alwaysUseStartDate:true});"/>
                </td>
            </tr>
            <tr>
                <td>
                   结束时间：
                </td>
                <td><input class="c"  name="btime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd hh:mm:ss',startDate:'%y-%M-01',alwaysUseStartDate:true});"/>
                </td>
            </tr>
            <tr>
                <td colspan='2'>
                    <input id="uid" name="uid" type="hidden" value="0459C402773480" />
                    <input name="comid" type="hidden" value="924" />
                    <input type="submit" value='测试' class='p10 w200 ml30' title="" />
                </td>
            </tr>
        </table>
</form></div>
</body>

