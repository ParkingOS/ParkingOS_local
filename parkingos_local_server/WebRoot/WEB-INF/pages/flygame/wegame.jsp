<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<html>
<meta name="viewport">
<script>
    var dpr = window.devicePixelRatio || 1;
    var docEl = document.documentElement;
    var metaEl = document.querySelector('meta[name="viewport"]');
    var scale = 1 / dpr;
    // 设置viewport，进行缩放，达到高清效果
    var screenWidth = window.screen.width;
    if (screenWidth * dpr < 980){
        scale = screenWidth / 980;
    }
    scale = scale.toFixed(2);
    metaEl.setAttribute('content', 'width=' + dpr * docEl.clientWidth + ',initial-scale=' + scale + ',maximum-scale=' + scale + ', minimum-scale=' + scale + ',user-scalable=no');
</script>
		
</head></html>