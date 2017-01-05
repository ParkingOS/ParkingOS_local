var OFFSET = 5;
var page = 1;
var PAGESIZE = 9999;

var myScroll,
	pullDownEl, pullDownOffset,
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

$(document).ready(function() {
});

function loaded(comid,openid) {
	pullDownEl = document.getElementById('pullDown');
	pullDownOffset = pullDownEl.offsetHeight;
	pullUpEl = document.getElementById('pullUp');
	pullUpOffset = pullUpEl.offsetHeight;

	hasMoreData = false;

	page = 1;
	$.post("wxpublic.do", {
			"page": page,
			"pagesize": PAGESIZE,
			"comid" : comid,
			"openid" : openid,
			"action" : 'getparkerlist'
		},
		function(response, status) {
			if (status == "success") {
				$("#thelist").show();
				$("#pullDown").hide();
				myScroll = new iScroll('wrapper', {
					useTransition: true,
					topOffset: pullDownOffset,
					onRefresh: function() {
						
					},
					onScrollMove: function() {
						
					},
					onScrollEnd: function() {
						
					}
				});
				$.each(response, function(key, value) {
					var id = value.id;
					var nickname = value.nickname;
					var online_flag = value.online_flag;
					var online = '<span class="offline">离线</span>';
					var rewardcount = value.rewardcount;
					var servercount = value.servercount;
					if(online_flag == 23){
						online = '<span class="online">在线</span>';
					}
					$("#thelist").append('<li><a href="wxpfast.do?action=epay&uid='+id+'&nickname='+nickname+'&openid='+openid+'"><div><span class="nickname">'+nickname+'</span>'+online+'<span class="bianhao">编号:'+id+'</span><div><span class="reward">赏</span><span class="rew">7日内收到'+rewardcount+'笔打赏</span></div><div class="fuwu1"><span class="fuwu">服</span><span class="rew">7日内服务'+servercount+'次</span></div></div></a></li>');
				});
				myScroll.refresh(); // Remember to refresh when contents are loaded (ie: on ajax completion)
			};
		},
		"json");
}


function choose(ticketid){
	if($(".choose_"+ticketid).hasClass("choosed")){
		$(".choose_"+ticketid).removeClass("choosed");
		$(".choose_"+ticketid).removeClass("left2");
		$(".choose_"+ticketid).addClass("left1");
		$(".choose1_"+ticketid).removeClass("left2");
		$(".choose1_"+ticketid).addClass("left1");
		$(".choose1_"+ticketid).removeClass("after2");
		$(".choose1_"+ticketid).addClass("after1");
		
		$("#ticketid")[0].value = "-2";
	}else{
		var thelist = $("#thelist")[0];
		for(var i=0;i<thelist.children.length;i++){
			var li = thelist.children[i];
			if(hasClass(li,"choosed")){
				removeClass(li,"choosed");
			}
			if(hasClass(li,"left2")){
				removeClass(li,"left2");
				addClass(li,"left1");
			}
			if(hasClass(li,"after2")){
				removeClass(li,"after2");
				addClass(li,"after1");
			}
		}
		$(".choose_"+ticketid).addClass("choosed");
		$(".choose_"+ticketid).removeClass("left1");
		$(".choose_"+ticketid).addClass("left2");
		$(".choose1_"+ticketid).removeClass("left1");
		$(".choose1_"+ticketid).addClass("left2");
		$(".choose1_"+ticketid).removeClass("after1");
		$(".choose1_"+ticketid).addClass("after2");
		
		$("#ticketid")[0].value = ticketid;
	}
}

//每次移除一个class
function removeClass(currNode, curClass){
	var oldClass,newClass1 = "";
    oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
    if(oldClass !== null) {
	   oldClass = oldClass.split(" ");
	   for(var i=0;i<oldClass.length;i++){
		   if(oldClass[i] != curClass){
			   if(newClass1 == ""){
				   newClass1 += oldClass[i]
			   }else{
				   newClass1 += " " + oldClass[i];
			   }
		   }
	   }
	}
	currNode.className = newClass1; //IE 和FF都支持
}

//每次添加一个class
function addClass(currNode, newClass){
    var oldClass;
    oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
    if(oldClass !== null) {
	   newClass = oldClass+" "+newClass; 
	}
	currNode.className = newClass; //IE 和FF都支持
}
//检测是否包含当前class
function hasClass(currNode, curClass){
	var oldClass;
	oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
	if(oldClass !== null){
		oldClass = oldClass.split(" ");
		for(var i=0;i<oldClass.length;i++){
		   if(oldClass[i] == curClass){
			   return true;
		   }
	   }
	}
	return false;
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
