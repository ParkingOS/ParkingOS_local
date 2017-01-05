/*Layout Fucntion 2012-05-25 By Ft
*/
var logourl = false;
var theight = 0//theight!="undefined"?theight:5;//顶部高度
var bheight = 25;//底部版权高度
var lwidth = function(){//左栏宽度
	var lw = 216;
	if(parseInt(T.gcok("layout_l_divv"))&&parseInt(T.gcok("layout_l_divv"))==1){return 1}
	if(parseInt(T.gcok("layout_l_div"))){
		lw = T.gww(300) - parseInt(T.gcok("layout_l_div"))<200?T.gww(300) - 200:parseInt(T.gcok("layout_l_div"));
	};
	return lw
};
var r_theight = 0;//右栏顶部高度
var r_bheight = function(){//右栏底部高度
	if(typeof(l_r_b_h)!='undefined')
		return l_r_b_h||0;
	var rh = 0;
	var ah = T.gwh(200) - theight -  r_theight - bheight;
	rh = ah * 0.35;
	if(parseInt(T.gcok("layoutr_b_divv"))&&parseInt(T.gcok("layoutr_b_divv"))==1){return 0}
	if(parseInt(T.gcok("layoutr_b_div"))){
		rh = T.gwh(200) - r_theight - bheight -  T.gcok("layoutr_b_div") < 150?T.gwh(200) - r_theight - bheight - 150:T.gcok("layoutr_b_div");
	}else{
		rh = T.gwh(200) - r_theight - bheight -  rh < 150?T.gwh(200) - r_theight - bheight - 150:rh;
	};
	return rh;
};
var l_theight = l_theight||50;//左栏顶部高度
var l_mheight = 87;//左栏中部高度
var l_fheight = 0;//左栏底高度
var dragxW = 4;//X拖动宽
var dragyH = 4;

var layoutContainer = document.getElementById("alllayout");
var layoutT = document.createElement("div");//上
var layoutL = document.createElement("div");//左
var layoutX = document.createElement("div");//X拖动
var layoutR = document.createElement("div");//右
var layoutB = document.createElement("div");//下
theight?layoutContainer.appendChild(layoutT):"";
layoutContainer.appendChild(layoutL);
layoutContainer.appendChild(layoutX);
layoutContainer.appendChild(layoutR);
bheight>0?layoutContainer.appendChild(layoutB):"";
//X拖动
layoutX.className = "layout_x";
layoutX.style.width = dragxW + "px";
if(parseInt(( T.gwh(200) - theight - bheight - 1 ))>0)
	layoutX.style.height =( T.gwh(200) - theight - bheight - 1 )+ "px";
var xspanMT = (T.gwh(200) - theight - bheight)/2 - 100;
var xspanTit = lwidth() > 1 ?"关闭左栏":"打开左栏";
layoutX.style.cursor = lwidth() > 1?"e-resize":"default";
layoutX.innerHTML = "<span title=\""+xspanTit+"\" class=\"switch_x_o\" onmousedown=\"layoutX_close(this)\" style=\"margin-top:"+xspanMT+"px\"></span>";
lwidth() > 1 ?DragFun(layoutX,0,[{f:function(){_clientT.FO();}},{f:function(){_clientF?_clientF.SizeForm():"";}}]):"";

//上
layoutT.className = "layout_t";
layoutT.style.width = T.gww(300)+"px";
layoutT.style.height = theight +"px";
//左
layoutL.id = "layout_l_div";
layoutL.className = "layout_l";
layoutL.style.width = lwidth() + "px";
//layoutL.style.display = lwidth() > 1 ?"block":"none";

//layoutL.style.height = T.gwh(200) - theight - bheight - 2 +"px";
if(( T.gwh(200) - theight - bheight)>0)
	layoutL.style.height = T.gwh(200) - theight - bheight +"px";
var layoutL_T = document.createElement("div");
var layoutL_B = document.createElement("div");
var layoutL_M = document.createElement("div");
layoutL_T.className = "layoutl_t";
layoutL_T.style.height = l_theight + "px";
layoutL_M.className = "layoutl_m";
layoutL_M.style.height = l_mheight + "px";
layoutL_B.className = "layoutl_b";
//layoutL_B.style.height = T.gwh(200) - theight - bheight - l_theight - l_mheight - 4 + "px";
if(T.gwh(200)>0)
 layoutL_B.style.height = T.gwh(200) - theight - bheight - l_theight - l_fheight - l_mheight - 4 + "px";


var layoutL_B_T = document.createElement("div");
layoutL_B_T.className = "title";
layoutL_B.appendChild(layoutL_B_T);

var layoutL_B_B = document.createElement("div");
layoutL_B_B.id = "layoutl_b_b_div";
layoutL_B_B.className = "layoutl_b_b";
//layoutL_B_B.style.height = T.gwh(200) - theight - bheight - l_theight - l_fheight - l_mheight - 30 + "px";
if(T.gwh(200)>0)
layoutL_B_B.style.height = T.gwh(200) - theight - bheight - l_theight  - l_fheight - l_mheight - 35 + "px";
layoutL_B.appendChild(layoutL_B_B);

parseInt(l_theight)>0?layoutL.appendChild(layoutL_T):"";
layoutL.appendChild(layoutL_M);
layoutL.appendChild(layoutL_B);

if(l_fheight>0){
	var layoutL_F = document.createElement("div");
	layoutL_F.className = "layoutl_f";
	layoutL_F.style.height = l_fheight + "px";

	var layoutL_F_T = document.createElement("div");
	layoutL_F_T.className = "title";
	layoutL_F_T.innerHTML = "加载中"
	layoutL_F.appendChild(layoutL_F_T);
	
	var layoutL_F_B = document.createElement("div");
	layoutL_F_B.className = "layoutl_f_b";
	layoutL_F_B.style.height =l_fheight - 30 + "px";
	layoutL_F.appendChild(layoutL_F_B);
	
	layoutL.appendChild(layoutL_F);
};

//右
layoutR.className = "layout_r";
if(T.gwh(200)>0){
	layoutR.style.width = T.gww(300) - lwidth() - dragxW - 2 + "px";
	layoutR.style.height = T.gwh(200) - theight - bheight - 2 +"px";
}
var layoutR_T = document.createElement("div");
var layoutR_B = document.createElement("div");
var layoutY = document.createElement("div");
var layoutR_M = document.createElement("div");

layoutR_T.className = "layoutr_t";
layoutR_T.style.height = r_theight + "px";
layoutR_M.id = "layoutr_m_div";
layoutR_M.innerHTML = "内容加载中,请稍后...";
layoutR_M.className = "layoutr_m";
//layoutR_M.style.height = T.gwh(200) - theight - bheight - r_theight - r_bheight() - 4 + "px";
if(T.gwh(200)>0)
layoutR_M.style.height = T.gwh(200) - theight - bheight - r_theight - r_bheight() - dragyH - 2 + "px";
layoutR_B.id = "layoutr_b_div";
layoutR_B.innerHTML = "内容加载中,请稍后...";
layoutR_B.className = "layoutr_b";
layoutR_B.style.height = r_bheight()>0?r_bheight() + "px":"";
layoutR_B.style.display = r_bheight()>0?"block":"none";

r_theight>0?layoutR.appendChild(layoutR_T):"";
layoutR.appendChild(layoutR_M);

//Y拖动
layoutY.className = "layout_y";
layoutY.style.width = "100%";
layoutY.style.height = dragyH + "px";
var yspanTit = r_bheight() > 0?"关闭底栏":"打开底栏";
layoutY.style.cursor = r_bheight() > 0?"s-resize":"default";
layoutY.innerHTML = "<span title=\""+yspanTit+"\" class=\"switch_y_o\" onmousedown=\"layoutY_close(this)\">&nbsp;</span>";

layoutR.appendChild(layoutY);
r_bheight() > 0 ?DragFun(layoutY,1,[{f:function(){_clientT.FO();}},{f:function(){_clientF?_clientF.SizeForm():"";}}]):"";

layoutR.appendChild(layoutR_B);
//下
if(bheight>0){
layoutB.className = "layout_b";
layoutB.style.width = T.gww(300)+"px";
layoutB.style.height = bheight +"px";
layoutB.innerHTML = "北京商之讯软件有限公司"
};
/*监听调整布局大小*/
function ResizeLayout(){
		layoutT.style.width = T.gww(300)+"px";
		layoutL.style.width = lwidth() +"px";
		if(T.gwh(200)>0){
			layoutL.style.height = T.gwh(200) - theight - bheight  +"px";
			layoutL_B_B.style.height = T.gwh(200) - theight - bheight - l_theight - l_fheight - l_mheight - 35 + "px";
			layoutL_B.style.height = T.gwh(200) - theight - bheight - l_theight - l_fheight -  l_mheight - 4 + "px";
			layoutX.style.height = T.gwh(200) - theight - bheight - 1 + "px";
			layoutR.style.width = T.gww(300) - lwidth() - dragxW - 2 + "px";
			layoutR.style.height = T.gwh(200) - theight - bheight  +"px";
			layoutR_M.style.height = T.gwh(200) - theight - bheight - r_theight - r_bheight() - dragyH - 2 + "px";
		}
		r_bheight()>0?layoutR_B.style.height = r_bheight() + "px":"";
		bheight>0?layoutB.style.width = T.gww(300)+"px":"";
		T("#Layout_yDrag")?T("#Layout_yDrag").style.top = T.gpos(layoutR_B).y - 3 + "px":"";
		//alert(T.gwh(200) - theight - bheight - r_theight - r_bheight() - dragyH - 2 )
};
T.bind(window,"resize",ResizeLayout);
/*关闭左栏*/
function layoutX_close(t){
	if(layoutL.style.width == "1px"){
		T.scok(layoutL.id+"v","");
		layoutX.style.cursor = "e-resize";
		DragFun(layoutX,0,[{f:function(){_clientT.FO();}},{f:function(){_clientF?_clientF.SizeForm():"";}}]);
		t.title = "关闭左栏";
		t.className = "switch_x_o";
		//layoutL.style.display = "block"
	}else{
		T.scok(layoutL.id+"v","1");
		layoutX.onmousedown = null;
		layoutX.style.cursor = "default";
		t.title = "打开左栏";
		t.className = "switch_x_c";
		layoutL.style.width == "1px"
	};
	ResizeLayout();
	_clientT.FO();
	_clientF?_clientF.SizeForm():"";
	T.cancelBub();
};
/*关闭右下栏*/
function layoutY_close(t){
	if(layoutR_B.style.display == "none"){
		T.scok(layoutR_B.id+"v","");
		layoutY.style.cursor = "s-resize";
		DragFun(layoutY,1,[{f:function(){_clientT.FO();}},{f:function(){_clientF?_clientF.SizeForm():"";}}]);
		t.title = "关闭底栏";
		t.className = "switch_y_o";
		layoutR_B.style.display = "block"
	}else{
		T.scok(layoutR_B.id+"v","1");
		layoutY.onmousedown = null;
		layoutY.style.cursor = "default";
		t.title = "打开底栏";
		t.className = "switch_y_c";
		layoutR_B.style.display = "none";
	};
	ResizeLayout();
	_clientT.FO();
	_clientF?_clientF.SizeForm():"";
	T.cancelBub();
};
/*初始化*/
clientT();
