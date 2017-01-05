<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>真来电ETC</title>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<style type="text/css">
	 *{font-size: 24px;}
    body{height: 100%;}
    body{background-color:Transparent;}
    input,select{border-radius:10px;}
    #content{ padding: 50px;}
	.tc{text-align:center;margin-top:20px}
	.ntb th,.ntb td{border:none;}
	.w150{width:150px}
	.p10{padding:10px;}
	.w200{width:200px}
	.ml30{margin-left:30px}
	.c{width: 300px;height: 30px;line-height: 30px;margin: 20px;}
 </style>

</head>
<body>
<div id="content" class="tc">

<form action="nfchandle.do" method="post">       
 <table class="ntb" style="margin: auto;">
 			
            <tr>
                <td >
                  开始时间：
                </td>
                <td><input class="c"  id="btime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',startDate:'%y-%M-01',alwaysUseStartDate:true});"/>
                </td>
            </tr>
            <tr>
                <td>
                   结束时间：
                </td>
                <td><input class="c"  id="etime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',startDate:'%y-%M-01',alwaysUseStartDate:true});"/>
                </td>
            </tr>
             <tr>
                <td>
                   测试结果：
                </td>
                <td><textarea id="result" rows="10" cols="40">
			</textarea>

                </td>
            </tr>
            <tr>
                <td colspan='2'>
                    <input id="uid" id="uid" type="hidden" value="0433CFEA833480" />
                    <input id="comid" value="1197" />
                    <input type="button" value='测试-停车宝演示停车场（测试）' onclick="test()" class='p10 w280 ml30' title="" />
                    <!-- <input type="button" value='测试LaLa' onclick="testLala()" class='p10 w200 ml30' title="" /> -->
                </td>
            </tr>
        </table>
</form>
</div>
<script type="text/javascript">
function test(){
	var btime =document.getElementById("btime").value;
	var etime =document.getElementById("etime").value;
	var uid=document.getElementById("uid").value;
	var comid=document.getElementById("comid").value;
	//alert(comid);
	var result = T.A.sendData("nfchandle.do?action=test&btime="+btime+"&etime="+etime+"&uuid="+uid+"&comid="+comid);
	document.getElementById("result").innerText=result;
}

function testLala(){
	var url = "collectorrequest.do?action=toshare&token=904274576b698eb9a6cf8faf9f6edf5f&s_number=80";
	for(var i=0;i<100; i++){
		location.href = url;
	}
}
function testprice(){

}
</script>
</body>

