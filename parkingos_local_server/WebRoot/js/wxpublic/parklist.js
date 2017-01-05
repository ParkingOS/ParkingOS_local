var OFFSET = 5;
var page = 1;
var PAGESIZE = 99999;
var myScroll = null;
var pullDownEl, pullDownOffset,
	pullUpEl, pullUpOffset,
	generatedCount = 0;
var maxScrollY = 0;

var hasMoreData = false;
var today = new Date();
today.setHours(0);
today.setMinutes(0);
today.setSeconds(0);
today.setMilliseconds(0);
today = today/1000 + 24*60*60;//当天初始时间

document.addEventListener('touchmove', function(e) {
	e.preventDefault();
}, false);

document.addEventListener('DOMContentLoaded', function() {
	$(document).ready(function() {
//		loaded(39.946436,116.361442,'oRoektybTsv33_vSKKUwLAsJAquc');
	});
}, false);

function loaded(latitude,longitude,openid) {
	if(myScroll != null){
		myScroll.destroy();
	}
	pullDownEl = document.getElementById('pullDown');
	pullDownOffset = pullDownEl.offsetHeight;
	pullUpEl = document.getElementById('pullUp');
	pullUpOffset = pullUpEl.offsetHeight;

	hasMoreData = false;
	page = 1;
	$.post("wxpublic.do", {
		"page": page,
		"pagesize": PAGESIZE,
		'latitude' : latitude,
		'longitude' : longitude,
		'action' : 'getparklist',
		'payable' : 1,
		'openid' : openid
	},
	function(response, status) {
			if (status == "success") {
				$("#thelist").show();
				myScroll = new iScroll('wrapper', {
					useTransition: false,
					topOffset: pullDownOffset,
					onRefresh: function() {
						
					},
					onScrollMove: function() {
						
					},
					onScrollEnd: function() {
						
					}
				});
				if(response.length == 0){
					document.getElementById("showinfo").innerHTML = "附近2公里无车场";
					document.getElementById("imginfo").src = "images/wxpublic/tf_qrcode_close.png";
				}else{
					$("#BgDiv1").css({ display: "none", height: $(document).height() });
					$(".DialogDiv").css("display", "none");
				}
				$("#thelist").empty();
				$.each(response, function(key, value) {
					var comid = value.id;
					var company_name = value.company_name;
					var b = value.backticket;
					var epay = value.epay;
					var isfixed = value.isfixed;
					var first = value.first;
					var distance = value.distance;
					var picurl = value.picurl;
					var src = "wxpublic.do?action=toparkerpage&openid="+openid+"&comid="+comid;
					if(epay == 1 && isfixed == 1){
						var epay_logo = "";
						var hui = "";
						var huiinfo = "";
						var clickfunc = "";
						if(epay == 1){
							epay_logo = '<span class="epay">付</span>';
						}
						if(first == 1){
							hui = '<span class="hui">惠</span>';
							huiinfo = '<div class="credit" onclick="showinfo('+comid+')">优惠详情<div class="downArrow"></div></div>';
						}
						if(picurl != ""){
							clickfunc = ' onclick="previewpic('+comid+')"';
						}
						$("#thelist").append('<li class="li1"><img class="img2" src="'+picurl+'" '+clickfunc+' /><a href="'+src+'" class="a1"><div class="cname">'+company_name+'</div><div class="distance">'+distance+'/km'+epay_logo+'</div></a></li>');
						if(first == 1){
							var first_times = value.first_times;
							var fprice = value.fprice;
							var unit = value.unit;
							$("#thelist").append('<li class="li2" id="huiinfo_'+comid+'"><a href="'+src+'" class="a2"><div class="first"><span class="first_time">首</span><span class="first_cname">首优惠时长:'+first_times+'分钟</span></div><div class="price"><span class="first_price">惠</span><span class="first_cname">首优惠价格:'+fprice+'元/'+unit+'分钟</span></div></a></li>');
						}
					}
				});
				myScroll.refresh(); 
				myScroll.maxScrollY = myScroll.maxScrollY;
				maxScrollY = myScroll.maxScrollY;
			};
		},
		"json");
}
//扩展Date的format方法   
Date.prototype.format = function (format) {  
  var o = {  
      "M+": this.getMonth() + 1,  
      "d+": this.getDate(),  
      "h+": this.getHours(),  
      "m+": this.getMinutes(),  
      "s+": this.getSeconds(),  
      "q+": Math.floor((this.getMonth() + 3) / 3),  
      "S": this.getMilliseconds()  
  }  
  if (/(y+)/.test(format)) {  
      format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));  
  }  
  for (var k in o) {  
      if (new RegExp("(" + k + ")").test(format)) {  
          format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));  
      }  
  }  
  return format;  
}

/**   
*转换long值为日期字符串   
* @param l long值   
* @param isFull 是否为完整的日期数据,   
*               为true时, 格式如"2000-03-05 01:05:04"   
*               为false时, 格式如 "2000-03-05"   
* @return 符合要求的日期字符串   
*/    

function getSmpFormatDateByLong(l, isFull) {  
   return getSmpFormatDate(new Date(l), isFull);  
}  

/**   
*转换日期对象为日期字符串   
* @param date 日期对象   
* @param isFull 是否为完整的日期数据,   
*               为true时, 格式如"2000-03-05 01:05:04"   
*               为false时, 格式如 "2000-03-05"   
* @return 符合要求的日期字符串   
*/    
function getSmpFormatDate(date, isFull) {  
    var pattern = "";  
    if (isFull == true || isFull == undefined) {  
        pattern = "yyyy-MM-dd hh:mm:ss";  
    } else {  
        pattern = "yyyy-MM-dd";  
    }  
    return getFormatDate(date, pattern);  
} 

/**   
 *转换日期对象为日期字符串   
 * @param l long值   
 * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss   
 * @return 符合要求的日期字符串   
 */    
 function getFormatDate(date, pattern) {  
     if (date == undefined) {  
         date = new Date();  
     }  
     if (pattern == undefined) {  
         pattern = "yyyy-MM-dd hh:mm:ss";  
     }  
     return date.format(pattern);  
 }
