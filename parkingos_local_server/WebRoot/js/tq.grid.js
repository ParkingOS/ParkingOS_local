(function($) {
    $.addFlex = function(t, p) {
        if (t.grid){return false;}; //如果Grid已经存在则返回
        var dic;
        //增加行的字典数据存储
        dic = $.extend(
            new Dictionary(),
            dic);
        // 引用默认属性
        p = $.extend({
            height: 300, //高度，单位为px
            width: 700, //宽度值，auto表示根据每列的宽度自动计算
            striped: true, //是否显示斑纹效果，默认是奇偶交互的形式
            novstripe: false,
            minwidth: 20, //列的最小宽度
            minheight: 80, //列的最小高度
            url: false, //方式对应的url地址
            method: 'POST', //数据发送方式
            dataType: 'json', // 数据加载的类型,json
			errormsg: '异常错误，请重试',
            usepager: true, //是否分页
            nowrap: true, //是否不换行
            page: 1, //默认当前页
            total: 0, //总页面数
            useRp: false, //是否可以动态设置每页显示的结果数
            rp: 10, // 每页默认的结果数
			rpOptions: [10,15,20,30,50], //可选择设定的每页结果数
            title: false, //是否包含标题
            //pagestat: '显示{from}到{to}，总共 {total} 条记录', //显示当前页和总页面的样式
            pagestat: '总共 {total} 条记录', //显示当前页和总页面的样式
            procmsg: '正在请求数据，请稍候 ...', //正在处理的提示信息
            query: '', //搜索查询的条件
            qtype: '', //搜索查询的类别
            qop: "Eq", //搜索的操作符
            nomsg: '<font style="color:#D34319;font-weight:bold;">暂无符合条件的数据</font>', //无结果的提示信息
			minColToggle: 1, //允许显示的最小列数
            showToggleBtn: true, //是否列设置
			hideOnSubmit: true, //是否在回调时显示遮盖
            showTableToggleBtn: false, //显示隐藏Grid 
            autoload: true, //自动加载
            blockOpacity: 0.5, //透明度设置
            onChangeSort: false, //当改变排序时
            onSuccess: false, //成功后执行
            onSubmit: false, // 调用自定义的计算函数
			showcheckbox: true,//是否显示第一列的checkbox（用于全选）
            rowhandler: false, //是否启用行的扩展事情功能,在生成行时绑定事件，如双击，右键等
            rowbinddata: true,//配合上一个操作，如在双击事件中获取该行的数据
            extParam: false,//添加extParam参数可将外部参数动态注册到grid，实现如查询等操作
			dragcolumn:true,//改变列顺序
			sortname: "id",
			sortorder: "asc",
			showoperationswidth:0,
            //Style
            gridClass: "tq-grid",
            onrowchecked: false,//在每一行的的checkbox选中状态发生变化时触发某个事件
            ishistorydata : false,
            historydata : {},//保存行数据
			gridname:"tq"//表格名称， 保存cookies时区分
        }, p);

        $(t)
		.show()
		.attr({ cellPadding: 0, cellSpacing: 0, border: 0 })
		.removeAttr('width');

        //create grid class
        var g = {
			ie9Fix: function() {//修复IE9
					if($(".bDiv").height() != $(".bDiv table").height() && p.height=='auto') {
					   $(".bDiv").css({height: $(".bDiv table").height() + 18});
					}
					},

            hset: {},
			scok:function(name,value)
			{
				var Days = 30;
				var exp  = new Date();
				exp.setTime(exp.getTime() + Days*24*60*60*1000);
				document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
			},
			gcok:function(name)
			{
				var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
				if(arr != null) return unescape(arr[2]);
				return null;
			},
            dragStart: function(dragtype, e, obj) { //default drag function start

			 if (dragtype == 'colMove') //column header drag
                {
                    this.hset = $(this.hDiv).offset();
                    this.hset.right = this.hset.left + $('table', this.hDiv).width();
                    this.hset.bottom = this.hset.top + $('table', this.hDiv).height();
                    this.dcol = obj;
                    this.dcoln = $('th', this.hDiv).index(obj);

                    this.colCopy = document.createElement("div");
                    this.colCopy.className = "colCopy";
                    this.colCopy.innerHTML = obj.innerHTML;
                    if ($.browser.msie) {
                        this.colCopy.className = "colCopy ie";
                    }

                    $(this.colCopy).css({ position:'absolute',display: 'none', textAlign: obj.align });//float:'left', 
                    $('body').append(this.colCopy);
                    $(this.cDrag).hide();

                }
                $('body').noSelect();
            },

            reSize: function() {
                this.gDiv.style.width = p.width;
                this.bDiv.style.height = p.height;
            },
            dragEnd: function() {
                if (this.colresize) {
                    var n = this.colresize.n;
                    var nw = this.colresize.nw;
                    //$('th:visible div:eq(' + n + ')', this.hDiv).css('width', nw);
                    $('th:visible:eq(' + n + ') div', this.hDiv).css('width', nw);

                    $('tr', this.bDiv).each(
									function() {
									    //$('td:visible div:eq(' + n + ')', this).css('width', nw);
									    $('td:visible:eq(' + n + ') div', this).css('width', nw);
									}
								);
                    this.hDiv.scrollLeft = this.bDiv.scrollLeft;
                    $('div:eq(' + n + ')', this.cDrag).siblings().show();
                    $('.dragging', this.cDrag).removeClass('dragging');
                    this.colresize = false;
                }
                else if (this.vresize) {
                    this.vresize = false;
                }
                else if (this.colCopy) {
                    $(this.colCopy).remove();
                    if (this.dcolt != null) {
						if(this.dcolt > 0 ){
                        if (this.dcoln > this.dcolt)
                        { $('th:eq(' + this.dcolt + ')', this.hDiv).before(this.dcol); }
                        else
                        { $('th:eq(' + this.dcolt + ')', this.hDiv).after(this.dcol); }
                        this.switchCol(this.dcoln, this.dcolt);
                        $(this.cdropleft).remove();
                        $(this.cdropright).remove();
                    }
				}
                    this.dcol = null;
                    this.hset = null;
                    this.dcoln = null;
                    this.dcolt = null;
                    this.colCopy = null;
                    $('.thMove', this.hDiv).removeClass('thMove');
                    $(this.cDrag).show();
                }
                $('body').css('cursor', 'default');
                $('body').noSelect(false);
            },
            toggleCol: function(cid, visible) {//显示隐藏列
                var ncol = $("th[axis='col" + cid + "']", this.hDiv)[0];
                var n = $('thead th', g.hDiv).index(ncol);
                var cb = $('input[value=' + cid + ']', g.nDiv)[0];
				var colcookie;
				if(g.gcok(cb.name)==1){colcookie=true}else if(g.gcok(cb.name)==0){colcookie=false}else{colcookie=ncol.hide};
                if (visible == null){
                    visible = colcookie;
                }
                if ($('input:checked', g.nDiv).length < p.minColToggle && !visible) return false;
                if (visible){
                    ncol.hide = false;
					g.scok(cb.name,0);
                    $(ncol).show();
                    cb.checked = true;
                }
                else{
                    ncol.hide = true;
                    $(ncol).hide();
					g.scok(cb.name,1);
                    cb.checked = false;
                }
                $('tbody tr', t).each
							(
								function() {
								    if (visible)
								        $('td:eq(' + n + ')', this).show();
								    else
								        $('td:eq(' + n + ')', this).hide();
								}
							);
                return visible;
            },
            switchCol: function(cdrag, cdrop) { //switch columns
                $('tbody tr', t).each
					(
						function() {
						    if (cdrag > cdrop)
						        $('td:eq(' + cdrop + ')', this).before($('td:eq(' + cdrag + ')', this));
						    else
						        $('td:eq(' + cdrop + ')', this).after($('td:eq(' + cdrag + ')', this));
						}
					);
                //switch order in nDiv
                if (cdrag > cdrop)
                    $('tr:eq(' + cdrop + ')', this.nDiv).before($('tr:eq(' + cdrag + ')', this.nDiv));
                else
                    $('tr:eq(' + cdrop + ')', this.nDiv).after($('tr:eq(' + cdrag + ')', this.nDiv));
                if ($.browser.msie && $.browser.version < 7.0) $('tr:eq(' + cdrop + ') input', this.nDiv)[0].checked = true;
                this.hDiv.scrollLeft = this.bDiv.scrollLeft;
            },
            scroll: function() {
                this.hDiv.scrollLeft = this.bDiv.scrollLeft;
            },
            hideLoading: function() {
                $('.pReload', this.pDiv).removeClass('loading');
                if (p.hideOnSubmit) $(g.block).remove();
                $('.pPageStat', this.pDiv).html(p.errormsg);
                this.loading = false;
            },
            addRowToDic: function(row, ths) {
                if (row != null) {
                    var dicKey = row.id;
                    var dicValue = new Dictionary();
                    var dicValueKey, dicValueValue;
                    dicValue.add('id', row.id); //增加为id的一行。
                    $(ths).each(function() {
                        var idx = $(this).attr('axis').substr(3);
                        dicValueKey = $(this).attr('abbr');
                        if (dicValueKey) {
                            dicValueValue = row.cell[idx];
                            dicValue.add(dicValueKey, dicValueValue);
                        }

                    });
                    dic.add(dicKey, dicValue);
                }
            },
            addDataToDic: function(rows) {
                if (rows != null) {
                    var self = this;
                    var ths = $('thead tr:first th', g.hDiv);
                    $(rows).each(function() {
                        self.addRowToDic(this, ths);
                    });
                }
            },
            addData: function(data) { //parse data                
                if (p.preProcess)
                { data = p.preProcess(data); }
                $('.pReload', this.pDiv).removeClass('loading');
                this.loading = false;

                if (!data) {
                    $('.pPageStat', this.pDiv).html(p.errormsg);
                    return false;
                }
                var temp = p.total;
                p.total = data.total;
                if (p.total < 0) {
                    p.total = temp;
                }
                if (p.total == 0) {
                    $('tr, a, td, div', t).unbind();
                    $(t).empty();
                    p.pages = 1;
                    p.page = 1;
                    this.buildpager();
                    $('.pPageStat', this.pDiv).html(p.nomsg);
                    if (p.hideOnSubmit) $(g.block).remove();
                    return false;
                }

                p.pages = Math.ceil(p.total / p.rp);
                p.page = data.page;
                this.buildpager();

                var ths = $('thead tr:first th', g.hDiv);
                var thsdivs = $('thead tr:first th div', g.hDiv);
                var tbhtml = [];
                
                //添加值到dic
                dic.removeAll();
                this.addDataToDic(data.rows);
                
                tbhtml.push("<tbody>");
                if (p.dataType == 'json') {
                    if (data.rows != null) {
                        $.each(data.rows, function(i, row) {
                            tbhtml.push("<tr id='", "row", row.id, "'");

                            if (i % 2 && p.striped) {
                                tbhtml.push(" class='erow'");
                            }
                            if (p.rowbinddata) {
                               // tbhtml.push("ch='", row.cell.join("_FG$SP_"), "'");
                            	
                            	var rCell = row.cell.join("_FG$SP_");
                                //tbhtml.push("ch='", rCell, "'");
                                //if(p.ishistorydata){
            					//	var id = 'id'+row.id;
            		            //  	p.historydata.id=rCell;
                                //}
                                
                            }
                            tbhtml.push(">");
                            var trid = row.id;
                            $(ths).each(function(j) {
                                var tddata = "";
                                var tdclass = "";
                                tbhtml.push("<td align='", this.align, "'");
                                var idx = $(this).attr('axis').substr(3);

                                if (p.sortname && p.sortname == $(this).attr('abbr')) {
                                    tdclass = 'sorted';
                                }
								if(g.gcok(p.gridname+this.abbr.substr(1))==1){
									tbhtml.push(" style='display:none;'");
								};
								 if ((g.gcok(p.gridname+this.abbr.substr(1))==null||g.gcok(p.gridname+this.abbr.substr(1))=="")&&this.hide){tbhtml.push(" style='display:none;'");};
                                var width = thsdivs[j].style.width;
                                var div = [];
                                div.push("<div style='text-align:", this.align, ";width:", width, ";");
                                if (p.nowrap == false) {
                                    div.push("white-space:normal");
                                };
                                div.push("'>");
                                if (idx == "-1") { //checkbox
                                    div.push("<input type='checkbox' id='chk_", row.id, "' class='itemchk' value='", row.id, "'/>");
                                    if (tdclass != "") {
                                        tdclass += " chboxtd";
                                    } else {
                                        tdclass += "chboxtd";
                                    }
                                }
                                else if (idx == "-2") { //操作
									if (p.operation_obj) {
										var otDiv = document.createElement('div');
										//alert(p.operation_obj.length);
										for (i = 0; i < p.operation_obj.length; i++) {
											var obtn = p.operation_obj[i];
											//alert(obtn.slice);
											if (obtn != "" && !obtn.separator) {
												var obtnDiv = document.createElement('div');
												obtnDiv.className = 'fbutton';
												obtnDiv.innerHTML =(obtn.name)? "<div onclick=\"operateItem('"+obtn.name+"',"+row.id+");\"><span></span>" + obtn.displayname + "</div>":"";
												if (obtn.title) {
													obtnDiv.title = obtn.title;
												}
												if (obtn.bclass){ $('span', obtnDiv).addClass(obtn.bclass);}
												obtnDiv.Oonpress = obtn.Oonpress;
												obtnDiv.name = obtn.name;
												
												if(obtn.Oonpress){
													$(obtnDiv).click
														(
															function() {
																this.Oonpress(this.name,row.id);
															}
														);
												}
												$(otDiv).append(obtnDiv);
												if ($.browser.msie && $.browser.version < 7.0) {
													$(obtnDiv).hover(function() { $(this).addClass('fbOver'); }, function() { $(this).removeClass('fbOver'); });
												}
							
											} else {
												$(otDiv).append("<div class='btnseparator'></div>");
											}
										}
										//$(g.gDiv).prepend(otDiv);
										div.push(otDiv.innerHTML);
									}
										
									}
                                else {
                                    var divInner = row.cell[idx] || "&nbsp;";
                                    if (this.process) {
                                        divInner = this.process(divInner,trid);
                                    }
                                    div.push(divInner);
                                }
                                div.push("</div>");
                                if (tdclass != "") {
                                    tbhtml.push(" class='", tdclass, "'");
                                }
                                tbhtml.push(">", div.join(""), "</td>");
                            });
                            tbhtml.push("</tr>");
                        }
					    );
                    }
                } 
                tbhtml.push("</tbody>");
                $(t).html(tbhtml.join(""));
                this.addRowProp();
                if (p.onSuccess) p.onSuccess();
                if (p.hideOnSubmit) $(g.block).remove(); //$(t).show();
                this.hDiv.scrollLeft = this.bDiv.scrollLeft;
                if ($.browser.opera) $(t).css('visibility', 'visible');

            },
            changeSort: function(th) { //change sortorder

                if (this.loading) return true;
                if (p.sortname == $(th).attr('abbr')) {
                    if (p.sortorder == 'asc') p.sortorder = 'desc';
                    else p.sortorder = 'asc';
                }

                $(th).addClass('sorted').siblings().removeClass('sorted');
                $('.sdesc', this.hDiv).removeClass('sdesc');
                $('.sasc', this.hDiv).removeClass('sasc');
                $('div', th).addClass('s' + p.sortorder);
                p.sortname = $(th).attr('abbr');

                if (p.onChangeSort)
                    p.onChangeSort(p.sortname, p.sortorder);
                else
                    this.populate();

            },
            buildpager: function() { //rebuild pager based on new properties

                $('.pcontrol input', this.pDiv).val(p.page);
                $('.pcontrol span', this.pDiv).html(p.pages);

                var r1 = (p.page - 1) * p.rp + 1;
                var r2 = r1 + p.rp - 1;

                if (p.total < r2) r2 = p.total;

                var stat = p.pagestat;

                stat = stat.replace(/{from}/, r1);
                stat = stat.replace(/{to}/, r2);
                stat = stat.replace(/{total}/, p.total);
                $('.pPageStat', this.pDiv).html(stat);
            },
            populate: function() { //get latest data 
                if (this.loading) return true;
                if (p.onSubmit) {
                    var gh = p.onSubmit();
                    if (!gh) return false;
                }
                this.loading = true;
                if (!p.url) return false;
                $('.pPageStat', this.pDiv).html(p.procmsg);
                $('.pReload', this.pDiv).addClass('loading');
                $(g.block).css({ top: g.bDiv.offsetTop });
                if (p.hideOnSubmit) $(this.gDiv).prepend(g.block); //$(t).hide();
                if ($.browser.opera) $(t).css('visibility', 'hidden');
                if (!p.newp) p.newp = 1;
                if (p.page > p.pages) p.page = p.pages;
				var fieldslist = "";
				for(var i=0;i<p.colModel.length;i++){
					fieldslist += p.colModel[i].name+"__";
				};
				fieldslist = fieldslist.substr(0, fieldslist.length-2)
                var param = {page:p.newp, rp: p.rp, sortname: p.sortname, sortorder: p.sortorder, fieldsstr:fieldslist};
                if (p.extParam) {param = $.extend(param, p.extParam);}
                $.ajax({
                    type: p.method,
                    url: p.url,
                    data: param,
                    dataType: p.dataType,
                    success: function(data) { if (data != null && data.error != null) { if (p.onError) { p.onError(data); g.hideLoading(); } } else { g.addData(data); } },
                    error: function(data) { try { if (p.onError) { p.onError(data); } else { alert("获取数据发生异常;") } g.hideLoading(); } catch (e) { } }
                });
            },
            doSearch: function() {
                var queryType = $('select[name=qtype]', g.sDiv).val();
                var qArrType = queryType.split("$");
                var index = -1;
                if (qArrType.length != 3) {
                    p.qop = "Eq";
                    p.qtype = queryType;
                }
                else {
                    p.qop = qArrType[1];
                    p.qtype = qArrType[0];
                    index = parseInt(qArrType[2]);
                }
                p.query = $('input[name=q]', g.sDiv).val();
                //添加验证代码
                if (p.query != "" && p.searchitems && index >= 0 && p.searchitems.length > index) {
                    if (p.searchitems[index].reg) {
                        if (!p.searchitems[index].reg.test(p.query)) {
                            alert("你的输入不符合要求!");
                            return;
                        }
                    }
                }
                p.newp = 1;
                this.populate();
            },
            changePage: function(ctype) { //change page

                if (this.loading) return true;

                switch (ctype) {
                    case 'first': p.newp = 1; break;
                    case 'prev': if (p.page > 1) p.newp = parseInt(p.page) - 1; break;
                    case 'next': if (p.page < p.pages) p.newp = parseInt(p.page) + 1; break;
                    case 'last': p.newp = p.pages; break;
                    case 'input':
                        var nv = parseInt($('.pcontrol input', this.pDiv).val());
                        if (isNaN(nv)) nv = 1;
                        if (nv < 1) nv = 1;
                        else if (nv > p.pages) nv = p.pages;
                        $('.pcontrol input', this.pDiv).val(nv);
                        p.newp = nv;
                        break;
                }

                if (p.newp == p.page) return false;

                if (p.onChangePage)
                    p.onChangePage(p.newp);
                else
                    this.populate();

            },
            cellProp: function(n, ptr, pth) {
                var tdDiv = document.createElement('div');
                if (pth != null) {
                    if (p.sortname == $(pth).attr('abbr') && p.sortname) {
                        this.className = 'sorted';
                    }
                    $(tdDiv).css({ textAlign: pth.align, width: $('div:first', pth)[0].style.width });
                    if (pth.hide) $(this).css('display', 'none');
                }
                if (p.nowrap == false) $(tdDiv).css('white-space', 'normal');

                if (this.innerHTML == '') this.innerHTML = '&nbsp;';

                //tdDiv.value = this.innerHTML; //store preprocess value
                tdDiv.innerHTML = this.innerHTML;

                var prnt = $(this).parent()[0];
                var pid = false;
                if (prnt.id) pid = prnt.id.substr(3);
                if (pth != null) {
                    if (pth.process)
                    { pth.process(tdDiv, pid); }
                }
                $("input.itemchk", tdDiv).each(function() {
                    $(this).click(function() {
                        if (this.checked) {
                            $(ptr).addClass("trSelected");
                        }
                        else {
                            $(ptr).removeClass("trSelected");
                        }
                        if (p.onrowchecked) {
                            p.onrowchecked.call(this);
                        }
                    });
                });
                $(this).empty().append(tdDiv).removeAttr('width'); //wrap content
                //add editable event here 'dblclick',如果需要可编辑在这里添加可编辑代码 
            },
            addCellProp: function() {
                var $gF = this.cellProp;

                $('tbody tr td', g.bDiv).each
					(
						function() {
						    var n = $('td', $(this).parent()).index(this);
						    var pth = $('th:eq(' + n + ')', g.hDiv).get(0);
						    var ptr = $(this).parent();
						    $gF.call(this, n, ptr, pth);
						}
					);
                $gF = null;
            },
            updateRowData: function(rowId,index,value) {
                var ths = $('thead tr:first th', g.hDiv);
                var tr = $("#row" + rowId, g.bDiv).eq(0);
                $(ths).each(function(i) {
                    var idx = $(this).attr('axis').substr(3);
                    //更新行
                   if(idx==index){
                    	var td = $('td', $(tr)).get(i);
                    	if (td) {
                    		var width = parseFloat($(td).css('width')) - 4;
                            $(td).html("<div style='text-algin:left;width:"+width+"px;'>"+value+"</div>")
                            //$(td).text(value);
                        }
                    }
                });
            },
            getCellDatas: function(id) {
            	//var id = 'id'+id
                //return p.historydata.id.split("_FG$SP_");
                var array = new Array();
                var ths = $('thead tr:first th', g.hDiv);
                var trdata;
                if (dic.exists(id)) {
                    trdata = dic.item(id);
                }
                if (trdata) {
                    $.each(ths, function(i) {
                        var idx = $(this).attr('axis').substr(3);
                        var name = $(this).attr('abbr');
                        if (idx && name) {
                            var txt = trdata.item(name);
                            array[idx] = txt;
                        }
                    });
                }
                return array;
            },
            getCheckedRows: function() {
            	var _idstr = "";
                var ids = [];
                $(":checkbox:checked", g.bDiv).each(function() {
                    ids.push($(this).val());
                });
                var idsLength = ids.length;
                for(i=0;i<idsLength;i++){
                	if(i == 0){
                		_idstr = ids[i];//p.historydata[ids[i]].split("_FG$SP_")[0];//
                	}else{
                		_idstr = _idstr + "_" + ids[i];//p.historydata[ids[i]].split("_FG$SP_")[0];//
                	}
                }
                return _idstr;
            },
             getRowsId: function() {
                var ids = [];
                $(":checkbox", g.bDiv).each(function() {
                    ids.push($(this).val());
                });
                return ids;
            },
            getCellDim: function(obj) // get cell prop for editable event
            {
                var ht = parseInt($(obj).height());
                var pht = parseInt($(obj).parent().height());
                var wt = parseInt(obj.style.width);
                var pwt = parseInt($(obj).parent().width());
                var top = obj.offsetParent.offsetTop;
                var left = obj.offsetParent.offsetLeft;
                var pdl = parseInt($(obj).css('paddingLeft'));
                var pdt = parseInt($(obj).css('paddingTop'));
                return { ht: ht, wt: wt, top: top, left: left, pdl: pdl, pdt: pdt, pht: pht, pwt: pwt };
            },
            rowProp: function() {
                if (p.rowhandler) {
                    p.rowhandler(this);
                }
                if ($.browser.msie && $.browser.version < 7.0) {
                    $(this).hover(function() { $(this).addClass('trOver'); }, function() { $(this).removeClass('trOver'); });
                }
            },
            addRowProp: function() {//添加行选中事件
                var $gF = this.rowProp;
                $('tbody tr', g.bDiv).each(
                    function() {
                        $("input.itemchk", this).each(function() {
                            var ptr = $(this).parent().parent().parent();
                            $(this).click(function() {
                                if (this.checked) {
                                    ptr.addClass("trSelected");
                                }
                                else {
                                    ptr.removeClass("trSelected");
                                }
                                if (p.onrowchecked) {
                                    p.onrowchecked.call(this);
                                }
                            });
                        });
                        $gF.call(this);
                    }
                );
                $gF = null;
            },
            checkAllOrNot: function(parent) {
                var ischeck = $(this).attr("checked");
                $('tbody tr', g.bDiv).each(function() {
                    if (ischeck) {
                        $(this).addClass("trSelected");
                    }
                    else {
                        $(this).removeClass("trSelected");
                    }
                });
                $("input.itemchk", g.bDiv).each(function() {
                    this.checked = ischeck;
                    //Raise Event
                    if (p.onrowchecked) {
                        p.onrowchecked.call(this);
                    }
                });
            },
            pager: 0
        };

        //create model if any
        if (p.colModel) {
            thead = document.createElement('thead');
            tr = document.createElement('tr');
            if (p.showcheckbox) {
                var cth = jQuery('<th/>');
                var cthch = jQuery('<input type="checkbox"/>');
                cthch.addClass("noborder");
                cth.addClass("cth").attr({ 'axis': "col-1", width: "16", "isch": false }).append(cthch);
                $(tr).append(cth);
            }
            if (p.operation_obj) {
                var cth = jQuery('<th/>');
                var is_show_operation = true;
                if(p.showoperationswidth > 0 ){ // 存在这使用固定宽度
                	 cth.addClass("cth").attr({ 'axis': "col-2", width: p.showoperationswidth, "isch": false }).append('<center>操作</center>');
                }else{
	                for (i = 0; i < p.operation_obj.length; i++) {
						var obtn = p.operation_obj[i];
						if (!obtn.separator && obtn.width) {
							p.showoperationswidth = p.showoperationswidth + obtn.width;
						}else{
							break;
						}
					}
					if(p.showoperationswidth > 0 ) // 使用每个操作项设定宽度的宽度和
						 cth.addClass("cth").attr({ 'axis': "col-2", width: p.showoperationswidth, "isch": false }).append('<center>操作</center>');
					else if(p.showoperationswidth < 0 ) // 无任何操作不添加操作项
						 is_show_operation = false;
					else // 按操作项目来设定操作宽度 最多每个操作项名不超过4个汉字
						 cth.addClass("cth").attr({ 'axis': "col-2", width: (p.operation_obj.length-1)*80, "isch": false }).append('<center>操作</center>');
                }
                if(is_show_operation)
                	$(tr).append(cth);
            }
            for (i = 0; i < p.colModel.length; i++) {
                var cm = p.colModel[i];
                var th = document.createElement('th');

                th.innerHTML = cm.display;

                if (cm.name)// if (cm.name && cm.sortable)
                    $(th).attr('abbr', "_"+cm.name);

                //th.idx = i;
                $(th).attr('axis', 'col' + i);

                if (cm.align)
                    th.align = cm.align;

                if (cm.width)
                    $(th).attr('width', cm.width);

                if (cm.hide || cm.fhide) {
                    th.hide = true;
                }
                
                if (cm.toggle != undefined) {
                    th.toggle = cm.toggle
                }
                
                if (cm.process) {
                    th.process = cm.process;
                }

                $(tr).append(th);
            }
            $(thead).append(tr);
            $(t).prepend(thead);
        } // end if p.colmodel	

        //init divs
        g.gDiv = document.createElement('div'); //创建表格主框架
        g.mDiv = document.createElement('div'); //创建表格标题
        g.hDiv = document.createElement('div'); //创建表格列标题
        g.bDiv = document.createElement('div'); //创建表格内容列
        g.cDrag = document.createElement('div'); //create column drag
        g.block = document.createElement('div'); //创建加载过渡层
        g.nDiv = document.createElement('div'); //create column show/hide popup
        g.tDiv = document.createElement('div'); //创建工具栏
        g.sDiv = document.createElement('div'); //创建表格内筛选
		g.clDiv = document.createElement('div');//创建列显示按钮


        if (p.usepager) g.pDiv = document.createElement('div'); //create pager container
        g.hTable = document.createElement('table');

        //set gDiv
        g.gDiv.className = p.gridClass;
        if (p.width != 'auto') g.gDiv.style.width = p.width + 'px';
        //add conditional classes
        if ($.browser.msie)
            $(g.gDiv).addClass('ie');

        if (p.novstripe)
            $(g.gDiv).addClass('novstripe');

        $(t).before(g.gDiv);
        $(g.gDiv)
		.append(t)
		;

        //表格工具栏设置
        if (p.buttons) {
            g.tDiv.className = 'tDiv';
            var tDiv2 = document.createElement('div');
            tDiv2.className = 'tDiv2';
            for (i = 0; i < p.buttons.length; i++) {
                var btn = p.buttons[i];
                if (!btn.separator) {
                    var btnDiv = document.createElement('div');
                    btnDiv.className = 'fbutton';
                    if(btn.name != ""){
	                    btnDiv.innerHTML = "<div id='toole_botton" +i+ "'><span></span>" + btn.displayname + "</div>";
                    }
                    if (btn.title) {
                        btnDiv.title = btn.title;
                    }
                    if (btn.bclass)
                        $('span', btnDiv)
							.addClass(btn.bclass);
                    btnDiv.onpress = btn.onpress;
                    btnDiv.name = btn.name;
                    if (btn.onpress) {
                        $(btnDiv).click
							(
								function() {
								    this.onpress(this.name, g.gDiv);
								}
							);
                    }
                    $(tDiv2).append(btnDiv);
                    if ($.browser.msie && $.browser.version < 7.0) {
                        $(btnDiv).hover(function() { $(this).addClass('fbOver'); }, function() { $(this).removeClass('fbOver'); });
                    }

                } else {
                    $(tDiv2).append("<div class='btnseparator'></div>");
                }
            }
			if (p.showToggleBtn){
				g.clDiv.className = 'fbutton';
				g.clDiv.innerHTML = "<div id='toole_botton_lsz' title='调整列显示(设置将保存于本地电脑)'><span class='columnset'></span>列设置</div>";
				$(tDiv2).append(g.clDiv);
			}
            $(g.tDiv).append(tDiv2);
            $(g.tDiv).append("<div style='clear:both'></div>");
            $(g.gDiv).prepend(g.tDiv);
        }

        //set hDiv
        g.hDiv.className = 'hDiv';
        $(t).before(g.hDiv);

        //set hTable
        g.hTable.cellPadding = 0;
        g.hTable.cellSpacing = 0;
        $(g.hDiv).append('<div class="hDivBox"></div>');
        $('div', g.hDiv).append(g.hTable);
        var thead = $("thead:first", t).get(0);
        if (thead) $(g.hTable).append(thead);
        thead = null;

        if (!p.colmodel) var ci = 0;

        //设置表格标题			
        $('thead tr:first th', g.hDiv).each
			(
			 	function() {
			 	    var thdiv = document.createElement('div');
			 	    if ($(this).attr('abbr')) {
			 	        $(this).click(
								function(e) {
								    if (!$(this).hasClass('thOver')) return false;
								    var obj = (e.target || e.srcElement);
								    if (obj.href || obj.type) return true;
								    g.changeSort(this);
								}
							);

			 	        if ($(this).attr('abbr') == p.sortname) {
			 	            this.className = 'sorted';
			 	            thdiv.className = 's' + p.sortorder;
			 	        }
			 	    };
					if(g.gcok(p.gridname+this.abbr.substr(1))==1) $(this).hide();
			 	    if((g.gcok(p.gridname+this.abbr.substr(1))==null||g.gcok(p.gridname+this.abbr.substr(1))=="")&&this.hide) $(this).hide();
			 	    if(!p.colmodel && !$(this).attr("isch")) {
			 	        $(this).attr('axis', 'col' + ci++);
			 	    };
			 	    $(thdiv).css({ textAlign: this.align, width: this.width + 'px' });
			 	    thdiv.innerHTML = this.innerHTML;

			 	    $(this).empty().append(thdiv).removeAttr('width');
			 	    if (!$(this).attr("isch")) {
			 	        $(this).mousedown(function(e) {
							if(p.dragcolumn){g.dragStart('colMove', e, this);}
			 	        })
						.hover(
							function() {

							    if (!g.colresize && !$(this).hasClass('thMove') && !g.colCopy) $(this).addClass('thOver');

							    if ($(this).attr('abbr') != p.sortname && !g.colCopy && !g.colresize && $(this).attr('abbr')) $('div', this).addClass('s' + p.sortorder);
							    else if ($(this).attr('abbr') == p.sortname && !g.colCopy && !g.colresize && $(this).attr('abbr')) {
							        var no = '';
							        if (p.sortorder == 'asc') no = 'desc';
							        else no = 'asc';
							        $('div', this).removeClass('s' + p.sortorder).addClass('s' + no);
							    }

							    if (g.colCopy) {
							        var n = $('th', g.hDiv).index(this);
							        if (n == g.dcoln) return false;
							        if (n < g.dcoln){$(this).append(g.cdropleft);}
							        else{$(this).append(g.cdropright);}
							        g.dcolt = n;

							    } else if (!g.colresize) {
							        var thsa = $('th:visible', g.hDiv);
							        var nv = -1;
							        for (var i = 0, j = 0, l = thsa.length; i < l; i++) {
							            if ($(thsa[i]).css("display") != "none") {
							                if (thsa[i] == this) {
							                    nv = j;
							                    break;
							                }
							                j++;
							            }
							        }
							    }

							},
							function() {
							    $(this).removeClass('thOver');
							    if ($(this).attr('abbr') != p.sortname) $('div', this).removeClass('s' + p.sortorder);
							    else if ($(this).attr('abbr') == p.sortname) {
							        var no = '';
							        if (p.sortorder == 'asc'){no = 'desc';}else{no = 'asc';}
							        $('div', this).addClass('s' + p.sortorder).removeClass('s' + no);
							    }
							    if (g.colCopy) {
							        $(g.cdropleft).remove();
							        $(g.cdropright).remove();
							        g.dcolt = null;
							    }
							}); 
			 	    }
			 	}
			);

        //set bDiv
        g.bDiv.className = 'bDiv';
        $(t).before(g.bDiv);
        $(g.bDiv)
		.css({ height: (p.height == 'auto') ? 'auto' : p.height + "px" })
		.scroll(function(e) { g.scroll() })
		.append(t)
		;

        if (p.height == 'auto') {
            $('table', g.bDiv).addClass('autoht');
        }

        //add td properties
        if (p.url == false || p.url == "") {
            g.addCellProp();
            g.addRowProp();
           // g.addRowProp().mouseleave(function(){ g.ie9Fix(); });//修复IE9
        }


        //add strip		
        if (p.striped)
            $('tbody tr:odd', g.bDiv).addClass('erow');

        // add pager
        if (p.usepager) {
            g.pDiv.className = 'pDiv';
            g.pDiv.innerHTML = '<div class="pDiv2"></div>';
            $(g.bDiv).after(g.pDiv);
            var html = '<div class="pGroup"><div class="pFirst pButton" title="转到第一页"><span></span></div><div class="pPrev pButton" title="转到上一页"><span></span></div> </div><div class="btnseparator"></div> <div class="pGroup"><span class="pcontrol">当前第 <input class="txt" type="text" size="1" value="1" title="输入页码，按回车键跳转"/> 页&nbsp;&nbsp;共<span> 1 </span>页</span></div><div class="btnseparator"></div><div class="pGroup"> <div class="pNext pButton" title="转到下一页"><span></span></div><div class="pLast pButton" title="转到最后一页"><span></span></div></div><div class="btnseparator"></div><div class="pGroup"> <div class="pReload pButton" title="刷新数据"><span></span></div> </div> <div class="btnseparator"></div><div class="pGroup"><span class="pPageStat"></span></div>';
            $('div', g.pDiv).html(html);

            $('.pReload', g.pDiv).click(function() { g.populate() });
            $('.pFirst', g.pDiv).click(function() { g.changePage('first') });
            $('.pPrev', g.pDiv).click(function() { g.changePage('prev') });
            $('.pNext', g.pDiv).click(function() { g.changePage('next') });
            $('.pLast', g.pDiv).click(function() { g.changePage('last') });
            if ($.browser.msie && (parseInt($.browser.version) <= 7)){
				$('.pcontrol input', g.pDiv).keypress(function(e) { if (e.keyCode == 13) g.changePage('input') })
			}else{
				$('.pcontrol input', g.pDiv).keydown(function(e) { if (e.keyCode == 13) g.changePage('input') })
			};
            if ($.browser.msie && $.browser.version < 7) $('.pButton', g.pDiv).hover(function() { $(this).addClass('pBtnOver'); }, function() { $(this).removeClass('pBtnOver'); });

            if (p.useRp) {
                var opt = "";
                for (var nx = 0; nx < p.rpOptions.length; nx++) {
                    if (p.rp == p.rpOptions[nx]) sel = 'selected="selected"'; else sel = '';
                    opt += "<option value='" + p.rpOptions[nx] + "' " + sel + " >" + p.rpOptions[nx] + "&nbsp;&nbsp;</option>";
                };
                $('.pDiv2', g.pDiv).prepend("<div class='pGroup'>每页 <select name='rp'>" + opt + "</select>条</div> <div class='btnseparator'></div>");
                $('select', g.pDiv).change(
					function() {
					    if (p.onRpChange)
					        p.onRpChange(+this.value);
					    else {
					        p.newp = 1;
					        p.rp = +this.value;
					        g.populate();
					    }
					}
				);
            }

            //添加搜索模块
            if (p.searchitems) {
                /*显示/隐藏搜素模块
				$('.tDiv2', g.tDiv).after("<div class='btnseparator'></div><div class='fbutton'><div><span class='pSearch' title='筛选当前表格内数据(包括分页)'>筛选</span></div> </div>");
                $('.pSearch', g.tDiv).click(function() { $(g.sDiv).slideToggle('fast', function() { $('.sDiv:visible input:first', g.gDiv).trigger('focus'); }); });
				*/
                //搜索模块
                g.sDiv.className = 'sDiv';

                sitems = p.searchitems;

                var sopt = "";
                var op = "Eq";
                for (var s = 0; s < sitems.length; s++) {
                    if (p.qtype == '' && sitems[s].isdefault == true) {
                        p.qtype = sitems[s].name;
                        sel = 'selected="selected"';
                    } else sel = '';
                    if (sitems[s].operater == "Like") {
                        op = "Like";
                    }
                    else {
                        op = "Eq";
                    }
                    sopt += "<option value='" + sitems[s].name + "$" + op + "$" + s + "' " + sel + " >" + sitems[s].display + "&nbsp;&nbsp;</option>";
                }

               
                var Squcikdatetime = "";
				if(p.Squcikdt_initializes){
					Squcikdt_initializes_ = p.Squcikdt_initializes;
					for (var n = 0; n < Squcikdt_initializes_.length; n++) {
						if (Squcikdt_initializes_[n].isdefault == true) {
							sel = 'selected="selected"';
						} else sel = '';
						Squcikdatetime += "<option value='" + Squcikdt_initializes_[n].opvalue + "' " + sel + " >" + Squcikdt_initializes_[n].Squcikdt + "</option>";
					}
				}
				
			    var Sdatetimestart = "";
			    var Sdatetimeend = "";
				if(p.Sdt_initializes){
				var search_initialize = p.Sdt_initializes;
                var m = search_initialize.length
						if( m == 1 )
						{
							alert(m)
                   		 Sdatetimestart = "<li><input type=\"text\" id=\"pre_date\" name=\"pre_date\" class=\"Wdate\" value='"+search_initialize[0].initializes_date+"' onclick=\"WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm:ss\'})\"\/><\/li>";
						}
						 if( m == 2){
                   		 Sdatetimestart = "<li><input type=\"text\" id=\"pre_date\" name=\"pre_date\" class=\"Wdate\" value='"+search_initialize[0].initializes_date+"' onclick=\"WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm:ss\'})\"\/><\/li>";
                   		 Sdatetimeend = "<li style=\"line-height:26px;\">至<\/li><li><input type=\"text\" id=\"end_date\" name=\"end_date\" class=\"Wdate\" value='"+search_initialize[1].initializes_date+"' onclick=\"WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm:ss\'})\"\/><\/li>";
						}

				}
				
                if (p.qtype == '') p.qtype = sitems[0].name;
				var aaa = "<div class=\"sDiv2\"><li><select name=\"quicktime_sel\">"+Squcikdatetime+"<\/select><\/li>"+Sdatetimestart+""+Sdatetimeend+"<li  style=\"line-height:22px;\">&nbsp;按<\/li><li><select name=\"qtype\">" + sopt + "<\/select><\/li><li><input type=\"text\"  name=\"q\" class=\"l-text\"\/><\/li><li><input type=\"image\" style=\"border:none;cursor:pointer\" src=\"images\/form\/button_search.gif\" name=\"qsubmitbtn\" value=\"查询\" \/><\/li><!--<input type=\"button\" name=\"qsubmitbtn\" value=\"查询\" \/><input type=\"button\" name=\"qclearbtn\" value=\"取消查询\" \/>--><\/div>";
                $(g.sDiv).append(aaa);

                $('input[name=q],select[name=qtype]', g.sDiv).keydown(function(e) { if (e.keyCode == 13) g.doSearch(); });
                $('input[name=qsubmitbtn]', g.sDiv).click(function() { g.doSearch(); });
                //$('input[name=qclearbtn]', g.sDiv).click(function() { $('input[name=q]', g.sDiv).val(''); p.query = ''; g.doSearch(); });//取消查询
                $(g.tDiv).after(g.sDiv);
            }

        }
        $(g.pDiv, g.sDiv).append("<div style='clear:both'></div>");


        // 添加表格标题
        if (p.title) {
            g.mDiv.className = 'mDiv';
            g.mDiv.innerHTML = '<div class="ftitle">' + p.title + '</div>';
            $(g.gDiv).prepend(g.mDiv);
            if (p.showTableToggleBtn) {
                $(g.mDiv).append('<div class="ptogtitle" title="Minimize/Maximize Table"><span></span></div>');
                $('div.ptogtitle', g.mDiv).click
					(
					 	function() {
					 	    $(g.gDiv).toggleClass('hideBody');
					 	    $(this).toggleClass('vsble');
					 	}
					);
            }
        }

        //setup cdrops
        g.cdropleft = document.createElement('span');
        g.cdropleft.className = 'cdropleft';
        g.cdropright = document.createElement('span');
        g.cdropright.className = 'cdropright';

        //add block
        g.block.className = 'gBlock';
        var blockloading = $("<div/>");
        blockloading.addClass("loading");
        $(g.block).append(blockloading);
        var gh = $(g.bDiv).height();
        var gtop = g.bDiv.offsetTop;
        $(g.block).css(
		{
		    width: g.bDiv.style.width,
		    height: gh,
		    position: 'relative',
		    marginBottom: (gh * -1),
		    zIndex: 1,
		    top: gtop,
		    left: '0px'
		}
		);
        $(g.block).fadeTo(0, p.blockOpacity);

      
	  if(p.showcheckbox){// 全选
	   $('th div', g.hDiv).each
			(
			 	function() {
			 	    var kcol = $("th[axis='col0']", g.hDiv)[0];
			 	    if (kcol == null) return;
			 	    var chkall = $("input[type='checkbox']", this);
			 	    if (chkall.length > 0) {
			 	        chkall[0].onclick = g.checkAllOrNot;
			 	        return;
			 	    }
			 	}
			);
	  }
		
		
		if (p.showToggleBtn){// 列设置
        if ($('th', g.hDiv).length) {
			var Lwid;
			var Thei;
			if(g.clDiv){Lwid = g.clDiv.offsetLeft; Thei = g.hDiv.offsetTop;}else{Lwid=0;Thei=0;}
            g.nDiv.className = 'nDiv';
            g.nDiv.innerHTML = "<table cellpadding='0' cellspacing='0' style='overflow-x:hidden'><tbody></tbody></table>";
            $(g.nDiv).css(
			{
			    marginBottom: (gh * -1),
			    display: 'none',
				left:Lwid,
			    top:Thei,
				height:p.colModel.length*29<g.bDiv.offsetHeight?p.colModel.length*29:g.bDiv.offsetHeight
			}
			).noSelect();

			var chk = 'checked="checked"';
			for (i = 0; i < p.colModel.length; i++){
			 var cm = p.colModel[i];
			 if(cm.fhide == true){
			 	continue;
			 };
			if(g.gcok(p.gridname+cm.name)==1){chk=""}else if(g.gcok(p.gridname+cm.name)==0){}else{(cm.hide == true)?chk="":""};
			$('tbody', g.nDiv).append('<tr><td class="ndcol1"><input type="checkbox" ' + chk + ' name="' + p.gridname + '' + cm.name + '" class="togCol noborder" value="' + i + '" /></td><td class="ndcol2"  style="cursor:pointer;">' + cm.display + '</td></tr>');
				chk = 'checked="checked"';
			}

            if ($.browser.msie && $.browser.version < 7.0)
                $('tr', g.nDiv).hover
				(
				 	function() { $(this).addClass('ndcolover'); },
					function() { $(this).removeClass('ndcolover'); }
				);

            $(g.clDiv).click//列调整
			(
			 	function() {
					$(g.nDiv).height(p.colModel.length*29<g.bDiv.offsetHeight?p.colModel.length*29:g.bDiv.offsetHeight)
			 	    $(g.nDiv).toggle(); return true;					
			 	}
			);
            $('td.ndcol2', g.nDiv).click
			(
			 	function(){
			 	    if ($('input:checked', g.nDiv).length <= p.minColToggle && $(this).prev().find('input')[0].checked) return false;
			 	    g.toggleCol($(this).prev().find('input').val());
			 	}
			);
            $('input.togCol', g.nDiv).click
			(
			 	function() {
			 	    if ($('input:checked', g.nDiv).length < p.minColToggle && this.checked == false) return false;
					g.toggleCol($(this).val());
			 	}
			);
            $(g.gDiv).prepend(g.nDiv);
		};
        }

        // add flexigrid events
        $(g.bDiv)
		.hover(function() { $(g.nDiv).hide(); }, function() { if (g.multisel){g.multisel = false;}})
		;
        $(g.gDiv)
		.hover(function() { }, function() { $(g.nDiv).hide(); })
		;

        //add document events
        $(document)
		.mouseup(function(e) { g.dragEnd() })
		.hover(function() { }, function() { g.dragEnd() });
		//function(){this.ie9Fix()}//修复IE9
		

        //browser adjustments
        if ($.browser.msie && $.browser.version < 7.0) {
            $('.hDiv,.bDiv,.mDiv,.pDiv,.vGrip,.tDiv, .sDiv', g.gDiv)
			.css({ width: '100%' });
            $(g.gDiv).addClass('ie6');
            if (p.width != 'auto') $(g.gDiv).addClass('ie6fullwidthbug');
        }


        //make grid functions accessible
        t.p = p;
        t.grid = g;

        // load data
        if (p.url && p.autoload) {
            g.populate();
        }

        return t;

    };

    var docloaded = false;

    $(document).ready(function() { docloaded = true });

    $.fn.flexigrid = function(p) {

        return this.each(function() {
            if (!docloaded) {
                $(this).hide();
                var t = this;
                $(document).ready
					(
						function() {
						    $.addFlex(t, p);
						}
					);
            } else {
                $.addFlex(this, p);
            }
        });

    }; //end flexigrid

    $.fn.flexReload = function(p) { // function to reload grid

        return this.each(function() {
            if (this.grid && this.p.url) this.grid.populate();
        });

    }; //end flexReload
    //重新指定宽度和高度
    $.fn.flexResize = function(w, h) {
        var p = { width: w, height: h };
        return this.each(function() {
            if (this.grid) {
                $.extend(this.p, p);
                this.grid.reSize();
            }
        });
    };
    $.fn.ChangePage = function(type) {
        return this.each(function() {
            if (this.grid) {
                this.grid.changePage(type);
            }
        })
    };
    $.fn.flexOptions = function(p) { //function to update general options

        return this.each(function() {
            if (this.grid) $.extend(this.p, p);
        });

    }; //end flexOptions
    $.fn.GetOptions = function() {
        if (this[0].grid) {
            return this[0].p;
        }
        return null;
    };
    // 获取选中的行，返回选中行的ID，仅在checkbox模式有效
    $.fn.getCheckedRows = function() {
        if (this[0].grid) {
            return this[0].grid.getCheckedRows();
        }
        return [];
    };
    // 获取选中的行，返回选中行的所有数据的Dictionary
    $.fn.getRowsId = function() {
        if (this[0].grid) {
            return this[0].grid.getRowsId();
        }
        return [];
    };
    //获取所有选中行的IDS，任何模式有效
    $.fn.getSelectedRowsIds = function() {
        if (this[0].grid) {
            return this[0].grid.getSelectedRowsIds();
        }
        return [];
    };
    $.fn.flexToggleCol = function(cid, visible) { // function to reload grid

        return this.each(function() {
            if (this.grid) this.grid.toggleCol(cid, visible);
        });

    }; //end flexToggleCol

    $.fn.flexAddData = function(data) { // function to add data to grid

        return this.each(function() {
            if (this.grid) this.grid.addData(data);
        });

    };

    $.fn.noSelect = function(p) { //no select plugin
        if (p == null)
            prevent = true;
        else
            prevent = p;

        if (prevent) {

            return this.each(function() {
                if ($.browser.msie || $.browser.safari) $(this).bind('selectstart', function() { return false; });
                else if ($.browser.mozilla) {
                    $(this).css('MozUserSelect', 'none');
                    $('body').trigger('focus');
                }
                else if ($.browser.opera) $(this).bind('mousedown', function() { return false; });
                else $(this).attr('unselectable', 'on');
            });

        } else {


            return this.each(function() {
                if ($.browser.msie || $.browser.safari) $(this).unbind('selectstart');
                else if ($.browser.mozilla) $(this).css('MozUserSelect', 'inherit');
                else if ($.browser.opera) $(this).unbind('mousedown');
                else $(this).removeAttr('unselectable', 'on');
            });

        };

    }; //end noSelect
    $.fn.updateRowData = function(rowId,index,value) {
        if (this[0].grid) {
            this[0].grid.updateRowData(rowId,index,value);
        }
    };
    //获取指定id行的数据，数组格式
    $.fn.getCellDatas = function(id) {
        if (this[0].grid) {
            return this[0].grid.getCellDatas(id);
        }
        return [];
    };
    $.fn.getGridJsonData = function() {
        if (this[0].grid) {
            return this[0].grid.getGridJsonData();
        }
        return '';
    };
    //在表格中追加一行
    $.fn.insertNewRow = function(row) {
        if (this[0].grid) {
            var self = this[0];

            var ths = $('thead tr:first th', self.hDiv);
            self.grid.addRowToDic(row, ths);
            //默认的Table是正文表格，先插入一行。
            var r = self.insertRow(0);
            r.id = 'row' + row.id;
            $(r).addClass = 'erow';
            if (self.p.rowbinddata)
                $(r).attr({ 'CH': row.cell.join("_FG$SP_") });

            //hTable是标题表格。遍历其中的td，逐一增加内容td
            $.each(ths, function(i) {
                var idx = $(this).attr('axis').substr(3);
                if (i == 0 && self.p.showcheckbox) {
                    var chk = r.insertCell(0);
                    if (!self.p.url && self.p.url.length == 0){
                        chk.innerHTML = "<input type='checkbox' id='chk_" + row.id + "' class='itemchk' value='" + row.id + "'/>";
					}else{
                     　　chk.innerHTML = "<input type='hidden' id='chk_" + row.id + "' value='" + row.id + "'/>";
					}
                }
                else {
                    var col = r.insertCell(i);
                    var width;
                    var align = this.align;
                    $("div", this).each(function() {
                        width = this.style.width;
                        return false;
                    });
                    var divHtml = "<div style='text-align:" + align + ";width:" + width + ";";
                    if (self.p.nowrap == false) {
                        divHtml += "white-space:normal;";
                    }
                    divHtml += "'/>" + row.cell[idx] + "</div>";
                    col.innerHTML = divHtml;
                    if ($(this).css("display") == "none") {
                        $(col).css({ 'display': "none" });
                    }
                }
            });
            var $gF = self.grid.rowProp;
            var $cf = self.grid.checkhandler;
            if (self.p.showcheckbox) {
                $('.itemchk', $(r)).click(function(e) {
                    $cf.call(r);
                    e.stopPropagation();
                });
            }
            if (self.p.selectedonclick) {
                $(r).click($cf);
            }
            $gF.call(r);
            self.p.rpoffset++;
            self.grid.buildpager();
        }
    };
    //删除指定的行
    $.fn.deleteRow = function(ids) {
        if (this[0].grid) {
            var tb = this[0];
            $(ids).each(function() {
                var self = tb.grid.jQueryEscape(this);
                $('#row' + self, tb).remove();
                //删除对应字典
                tb.grid.removeRowFromDic(this);
                tb.p.rpoffset--;
            });
            this[0].grid.buildpager();
        }
    };
    //判断指定Id的行是否存在
    $.fn.containsRowId = function(rowid) {
        if (this[0].grid) {
            var grid = this[0].grid;
            return grid.containsRowId(rowid);
        }
        else {
            return false;
        }

    };
    //为Options设置新的ExtParams
    $.fn.setNewExtParam = function(np) {
        this.each(function() {
            if (this.grid) {
                var op = this.p;
                var $extParam = $(op.extParam);
                var curr;
                $(np).each(function() {
                    var has = false;
                    curr = this;
                    $extParam.each(function() {
                        if (curr.name == this.name) {
                            this.value = curr.value;
                            has = true;
                            return false; //重新赋值后跳出循环
                        }
                    });
                    if (!has) {
                        //增加新的值
                        op.extParam.push({ name: curr.name, value: curr.value });
                        has = false;
                    }
                });
            }
            $.extend(this.p, op);
        });
    };
    
})(jQuery);