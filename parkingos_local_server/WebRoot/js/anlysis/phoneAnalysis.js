var getObj = function(id) {
	return document.getElementById(id);
};
function setVisiable() {
	// 初始化时间控件

	getObj("rangeSelect").value = "currentMon";
	var rangeV = getObj("rangeSelect").value;
	if (rangeV == 2 || rangeV == 3 || rangeV == 'currentWeek'
			|| rangeV == 'preWeek' || rangeV == 'currentMon'
			|| rangeV == 'preMon' || rangeV == 'currentSeason'
			|| rangeV == 'preSeason' || rangeV == 'currentYear'
			|| rangeV == 'preYear') {
		getObj("seconddateinput").style.display = "";
	} else {
		getObj("seconddateinput").style.display = "none";
	}

	var myDate = new Date();
	var year = myDate.getFullYear();
	var month = myDate.getMonth() + 1;
	if (month < 10) {
		month = "0" + month;
	}
	var firstDay = year + "-" + month + "-01";
	firstDay = firstDay.toString('yyyy-MM-dd');
	getObj("startDateSelect_field").value = firstDay;
	getObj("endDateSelect_field").value = myDate.toString('yyyy-MM-dd');

}
function hiddlecontent(str) {

	var url = "";
	var value = document.getElementById("rangeSelect").value;
	if (str == "currentMon") {
		url = "workImage.do?action=phoneAnalysis&rangeSelect=currentMon&startDateSelect="
				+ getObj('startDateSelect_field').value
				+ "&endDateSelect="
				+ getObj('endDateSelect_field').value;
	} else {

		if (value == 4 || value == 1 || value == 5 || value == "currentDay"
				|| value == "preDay" || value == "currentDay1"
				|| value == "preDay1") {

			url = "workImage.do?action=phoneAnalysis&department_id="
					+ getObj('department_id').value + "&kefu_uin="
					+ getObj('kefu_uin').value + "&startDateSelect="
					+ getObj('startDateSelect_field').value + "&rangeSelect="
					+ getObj('rangeSelect').value;
		} else {

			url = "workImage.do?action=phoneAnalysis&department_id="
					+ getObj('department_id').value + "&kefu_uin="
					+ getObj('kefu_uin').value + "&startDateSelect="
					+ getObj('startDateSelect_field').value + "&endDateSelect="
					+ getObj('endDateSelect_field').value + "&rangeSelect="
					+ getObj('rangeSelect').value;
		}
	}
	$.post(url, function(result) {
		var data1 = new Array();
		var data2 = new Array();
		var data3 = new Array();
		var data4 = new Array();
		var data5 = new Array();
		var data6 = new Array();
		var data7 = new Array();
		var data8 = new Array();

		var xAxisCategories = new Array();
		var yAxisTitle = "电话数(个)";
		var titleText = "电话量变化趋势";
		var subtitleText = "";
		var range = document.getElementById("rangeSelect").value;

		if (range == '4' || range == '1' || range == 'preDay'
				|| range == 'currentDay' || range == 'currentDay1'
				|| range == 'preDay1') {
			xAxisTitle = "选定天中的小时";
		} else if (range == 'preYear' || range == 'currentYear') {
			xAxisTitle = "选定时间段的月";
		} else if (range == '5') {
			xAxisTitle = "选定月的天";
		} else {
			xAxisTitle = "选定时间段的天";
		}
		if (eval(result) == undefined || eval(result) == "") {

		} else {
			$.each(eval(result)[0], function(i, phone) {
				if (xAxisCategories[i] == "") {

				} else {
					xAxisCategories[i] = phone.timeStr;
					data1[i] = phone.phoneTotal;
					data2[i] = phone.freeCallTotal;
					data3[i] = phone.total400;
					data4[i] = phone.comeinPhoneTotal;
					data5[i] = phone.outCallTotal;
					data6[i] = phone.comeinPhoneAbandonedcallTotal
					data7[i] = phone.outcallAbandonedcallTotal;
					data8[i] = phone.ioAbandonedcallTotal;
				}
			});
		}
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
								rotation : -45,
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
							enabled : true,
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
							name : "总电话量",
							data : data1
						}, {
							name : "免费电话",
							data : data2
						}, {
							name : "400电话",
							data : data3
						}, {
							name : "直线呼入",
							data : data4
						}, {
							name : "外呼电话",
							data : data5
						}, {
							name : "呼入未接通",
							data : data6
						}, {
							name :　"呼出未接通",
							data : data7
						}, {
							name : "未接通总量",
							data : data8
						} ]
					});
				});
	});
}