var getObj = function(id) {
	return document.getElementById(id);
};


function loaddata(){
	url = "parklalaanly.do?action=query&qdate="+ getObj('qdate').value +"&btime="+ 
		getObj('btime').value +"&etime="+ getObj('etime').value +"&comid="+comid;
	$.post(url, function(result) {
		hiddlecontent(result);
	});
}


function handleData(data){
	
	for(var i=0;i<data.length;i++){
		if(data[i]==0&&i!=0){
			data[i]=data[i-1];
		}
	}
	return data;
}

function hiddlecontent(result) {
	
	var data1 = new Array();
	var data2 = new Array();
	var data3 = new Array();
	var yAxisTitle = "车位数";
	var titleText = "车位变化趋势";
	var subtitleText = "";
	xAxisTitle = "选定时间段的天:"+getObj('qdate').value;
	var xAxisCategories = new Array();
	$.each(eval(result), function(i, laladate) {
		if (xAxisCategories[i] == "") {

		} else {
			xAxisCategories[i] = laladate.time;
			data1[i] = parseInt(laladate.total);
			data2[i] = parseInt(laladate.shared);
			data3[i] = parseInt(laladate.used);
		}
	});
	
	data2=handleData(data2);
	
	$(document).ready(
			function() {
				var chart = new Highcharts.Chart( {
					chart : {
						renderTo : "chart_container",
						defaultSeriesType : "line",
						plotBorderColor : "#e0e0e0",
						plotBorderWidth : 1
					//zoomType: "xy" // 是否及放大方向
					},
					title : {
						text : titleText,
						style : {
							font : 'bold 16px  宋体, sans-serif',
							color : '#000'
						}
					},
					subtitle : {
						text : subtitleText,
						style : {
							font : 'normal 12px  宋体, sans-serif',
							color : '#999'
						}
					},
					legend : {
						enabled : true
					},
					xAxis : {
						title : {
							text : xAxisTitle,
							style : {
								font : 'normal 12px 宋体, sans-serif',
								color : '#000',
								margin : '7px000'
							}
						},
						categories : xAxisCategories,
						labels : {
							rotation : -40, //坐标值显示的倾斜度
							align : 'right',
							style : {
								font : 'normal 10px Verdana, sans-serif'
						}
					}
				},
				yAxis : {
					min : 0,
					maxPadding : 0,
					title : {
						text : yAxisTitle,
						style : {
							font : 'normal 12px 宋体, sans-serif',
							color : '#000'
						}
					}
				},
				tooltip : {
					//enabled : true,
					formatter : function() {
						return "<b>" + this.series.name + "</b><br/>"
								+ this.x + ": " + this.y + "个";
					}
				},
				plotOptions : {
					line : {
						dataLabels : {
							enabled : true
						},
						enableMouseTracking : true
					}
				},
				series : [ {
					name : "总车位数",
					data : data1
				}, {
					name : "分享车位数",
					data : data2
				}, {
					name : "占用车位数",
					data : data3
				}]
			});
		});
}