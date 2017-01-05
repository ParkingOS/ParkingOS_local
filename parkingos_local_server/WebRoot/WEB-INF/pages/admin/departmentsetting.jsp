<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>功能权限设置</title>
<link href="css/zTreeStyle.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="js/anlysis/style.css?v=20100302" />
<script src="js/jquery.js" type="text/javascript"></script>
<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/jquery.ztree.core-3.5.js" type="text/javascript"></script>
<script src="js/jquery.ztree.excheck-3.5.js" type="text/javascript"></script>
<script src="js/jquery.ztree.exedit-3.5.js" type="text/javascript"></script>
<script src="js/jquery.ztree.exhide-3.5.js" type="text/javascript"></script>
<script type="text/javascript">
		var deparments = ${deparments}
		var setting = {
			view: {
				selectedMulti: false
			},
			edit: {
				enable: true,
				showRemoveBtn: false,
				showRenameBtn: false
			},
			data: {
				keep: {
					parent:true,
					leaf:true
				},
				simpleData: {
					enable: true
				}
			},
			callback: {
				beforeDrag: beforeDrag,
				beforeRemove: beforeRemove,
				beforeRename: beforeRename,
				onRemove: onRemove
			}
		};
		var className = "dark";
		function beforeDrag(treeId, treeNodes) {
			return false;
		}
		function beforeRemove(treeId, treeNode) {
			className = (className === "dark" ? "":"dark");
			return confirm("确认删除部门 -- " + treeNode.name + " 吗？");
		}
		function onRemove(e, treeId, treeNode) {
		}
		function beforeRename(treeId, treeNode, newName) {
			if (newName.length == 0) {
				alert("部门名称不能为空.");
				var zTree = $.fn.zTree.getZTreeObj("treeDemo");
				setTimeout(function(){zTree.editName(treeNode)}, 10);
				return false;
			}
			return true;
		}
		function createTree(jsonData) {
			$.fn.zTree.init($("#treeDemo"), setting, jsonData);
		}
		
		function add(e) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			var isParent = e.data.isParent;
			var nodes = zTree.getSelectedNodes();
			var treeNode = nodes[0];
			var hasChildren = treeNode.check_Child_State;//判断是否有子节点
			var childid = -1;//新节点ID
			if(hasChildren == -1){
				var curNodeId = treeNode.id+"";
				childid = parseInt(curNodeId + "1");
			}else{
				var allchildrenNodes = treeNode.children;
				for(var i=0;i<allchildrenNodes.length;i++){
					var childrenid = parseInt(allchildrenNodes[i].id);
					if(childrenid > childid){
						childid = childrenid;
					}
				}
				childid = childid + 1;
			}
			if (treeNode) {
				treeNode = zTree.addNodes(treeNode, {id:(childid), pId:treeNode.id, isParent:isParent, name:"请编辑名称"});
			}
			if (treeNode) {
				zTree.editName(treeNode[0]);
			} else {
				T.loadTip(1,"叶节点不允许添加子节点！",2,"");
			}
		};
		function edit() {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
			nodes = zTree.getSelectedNodes(),
			treeNode = nodes[0];
			if (nodes.length == 0) {
				T.loadTip(1,"请先选择一个部门！",2,"");
				return;
			}
			zTree.editName(treeNode);
		};
		function remove(e) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
			nodes = zTree.getSelectedNodes(),
			treeNode = nodes[0];
			if (nodes.length == 0) {
				alert("请先选择一个部门");
				return;
			}
			var hasChildren = treeNode.check_Child_State;//判断是否有子节点
			if(hasChildren == -1){
				var isremove  = confirm('确定要删除该部门吗?');
				if(isremove){
					treeNode.isHidden = true;
					zTree.refresh();
					removeDepartment(treeNode);
				}
			}else{
				var isChildrenClear = true;
				var children = treeNode.children;
				for(var i=0;i<children.length;i++){
					var child = children[i];
					if(!child.isHidden){
						isChildrenClear = false;
					}
				}
				if(isChildrenClear){
					var isremove  = confirm('确定要删除该部门吗?');
					if(isremove){
						treeNode.isHidden = true;
						zTree.refresh();
						removeDepartment(treeNode);
					}
				}else{
					T.loadTip(1,"请先删除子节点！",2,"");
				}
			}
		};
		
		function removeDepartment(treeNode){
			var nodeid = -1;
			var pid = -1;
			if(treeNode){
				nodeid = treeNode.id;
				pid = treeNode.pId;
			}
			var result = T.A.sendData("authsetting.do?action=removedepartment&nodeid="+nodeid+"&pid="+pid);
			if(result == 1){
				T.loadTip(1,"操作成功！",2,"");
			}else{
				T.loadTip(1,"操作失败！",2,"");
			}
		}
		
		function saveNodeName(){
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			var nodes = zTree.getSelectedNodes();
			var treeNode = nodes[0];
			var nodeid = -1;
			var pid = -1;
			var nodename = "";
			if(treeNode){
				var nodeid = treeNode.id;
				nodename = treeNode.name;
				pid = treeNode.pId;
				var isparent = treeNode.isParent;
			}
			var result = T.A.sendData("authsetting.do?action=departmentsave&nodeid="+nodeid+"&pid="+pid+"&nodename="+nodename+"&isparent="+isparent);
			if(result == 1){
				T.loadTip(1,"操作成功！",2,"");
			}else{
				T.loadTip(1,"操作失败！",2,"");
			}
		}
		$(document).ready(function(){
			createTree(deparments);
			$("#addParent").bind("click", {isParent:true}, add);
			$("#addLeaf").bind("click", {isParent:false}, add);
			$("#edit").bind("click", edit);
			$("#remove").bind("click", remove);
		});
	</script>
</head>
<body>
<div id="data_container">
<!--顶部开始-->
<div class="top">
<ul class="title"><li class="parentmenu">部门设置</li>
</ul>
<ul class="search">
<div style="margin-left:57px">
<input id="addParent" class="managebutton" type="button" value="添加父节点"/>&nbsp;
<input id="addLeaf" class="managebutton" type="button" value="添加子节点"/>&nbsp;
<input id="edit" class="managebutton" type="button" value="编辑部门"/>&nbsp;
<input id="remove" class="managebutton" type="button" value="删除节点"/>
</div>
</ul>
</div>
<!--顶部结束-->
<!--工具栏开始-->
<!--工具栏结束-->
<div class="float_clear"></div><!--清除浮动-->
<div class="zTreeDemoBackground left">
		<ul id="treeDemo" class="ztree" style="margin-left:50px;"></ul>
	</div>
</body>
</html>
