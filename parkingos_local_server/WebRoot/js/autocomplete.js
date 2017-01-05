var parentbject; 
window.city_suggest = function() { 
    this.Remoreurl = ''; // 远程URL地址 
    this.object = ''; 
    this.id2 = ''; 
    this.taskid = 0; 
    this.delaySec = 0; // 默认延迟多少毫秒出现提示框 
    this.lastkeys_val = 0; 
    this.lastinputstr = ''; 
    /** 
    * 初始化类库 
    */ 
    this.init_zhaobussuggest = function() { 
        var objBody = document.getElementsByTagName("body").item(0); 
        var objiFrame = document.createElement("iframe"); 
        var objplatform = document.createElement("div"); 
        objiFrame.setAttribute('id', 'getiframe'); 
        objiFrame.setAttribute('frameborder', '0'); 
        objiFrame.style.zindex = '9'; 
        objiFrame.style.position = 'absolute'; 
        //objiFrame.style.display = 'none'; 
        objplatform.setAttribute('id', 'getplatform'); 
        objplatform.setAttribute('align', 'left'); 
        objBody.appendChild(objiFrame); 
        objiFrame.document.body.appendChild(objplatform); 
        if (!document.all) { 
            window.document.addEventListener("click", this.hidden_suggest, false); 
        } else { 
            window.document.attachEvent("onclick", this.hidden_suggest); 
        } 
    } 

    /***************************************************fill_div()*********************************************/ 
    //函数功能：动态填充div的内容，该div显示所有的提示内容 
    //函数参数：allplat 一个字符串数组，包含了所有可能的提示内容 
    this.fill_div = function(allplat) { 
        var msgplat = ''; 
        var all = ''; 
        var spell = ''; 
        var chinese = ''; 
        var platkeys = this.object.value; 
        platkeys = this.ltrim(platkeys); 
        if (!platkeys) { 
            msgplat += '<table class="hint" width="153"><tr align="left"><td class="tdleft" height="10" align="left">中文/拼音/代码或&uarr;&darr;选择</td></tr>& lt;/table><table width="153" height="2"><tr><td></td></tr></table>'; 
            for (i = 0; i < allplat.length; i++) { 
                all = allplat[i].split(","); 
                spell = all[0]; 
                chinese = all[1]; 
                szm = all[2]; 
                msgplat += '<table class="mout" width="153"><tr onclick="parentbject.add_input_text(\'' + chinese + '\',\'' + szm + '\');parentbject.hidden_suggest();"><td class="tdleft" height="10" align="left">' + spell + 
           '</td><td class="tdright" align="right">' + chinese + '</td><td style="display:none">' + szm + '</td></tr></table>'; 
            } 
        } 
        else { 
            if (allplat.length < 1 || !allplat[0]) { 
                msgplat += '<table class="hint" width="153"><tr align="left"><td class="tdleft" height="10" align="left">对不起，找不到：' + platkeys + '</td></tr></table><table width="153" height="2"><tr><td></td></tr></table>'; 

            } 
            else { 
                msgplat += '<table class="hint" width="153"><tr align="left"><td class="tdleft" height="10" align="left">' + platkeys + '，按拼音排序</td></tr></table><table width="153" height="2"><tr><td></td></tr></table>'; 
                for (i = 0; i < allplat.length; i++) { 
                    all = allplat[i].split(","); 
                    spell = all[0]; 
                    chinese = all[1]; 
                    szm = all[2]; 
                    msgplat += '<table class="mout" width="153"><tr onclick="parentbject.add_input_text(\'' + chinese + '\',\'' + szm + '\');parentbject.hidden_suggest();"><td class="tdleft" height="10" align="left">' + spell + 
           '</td><td class="tdright" align="right">' + chinese + '</td><td style="display:none">' + szm + '</td></tr></table>'; 
                } 
            } 
        } 
        document.getElementById("getplatform").innerHTML = msgplat; 

        var nodes = document.getElementById("getplatform").childNodes; 
        nodes[0].className = "hint"; 
        if (allplat.length >= 1 && allplat[0]) { 
            nodes[2].className = "selected"; 
        } 
        this.lastkeys_val = 0; 
        for (var i = 2; i < nodes.length; i++) { 
            nodes[i].onmouseover = function() { 
                this.className = "mover"; 
            } 

            nodes[i].onmouseout = function() { 
                if (parentbject.lastkeys_val == (parentIndexOf(this) - 2)) { this.className = "selected"; } 
                else { this.className = "mout"; } 
            } 
        } 
        document.getElementById("getiframe").style.width = document.getElementById("getplatform").clientWidth + 2; 
        document.getElementById("getiframe").style.height = document.getElementById("getplatform").clientHeight + 2; 
    } 

    /***************************************************fix_div_coordinate*********************************************/ 
    //函数功能：控制提示div的位置，使之刚好出现在文本输入框的下面 
    this.fix_div_coordinate = function() { 
        var leftpos = 0; 
        var toppos = 0; 
        aTag = this.object; 
        do { 
            aTag = aTag.offsetParent; 
            leftpos += aTag.offsetLeft; 
            toppos += aTag.offsetTop; 
        } while (aTag.tagName != "BODY"); 
        document.getElementById("getiframe").style.width = this.object.offsetWidth + 'px'; 
        if (document.layers) { 
            document.getElementById("getiframe").style.left = this.object.offsetLeft + leftpos + "px"; 
            document.getElementById("getiframe").style.top = this.object.offsetTop + toppos + this.object.offsetHeight + 2 + "px"; 
        } else { 
            document.getElementById("getiframe").style.left = this.object.offsetLeft + leftpos + "px"; 
            document.getElementById("getiframe").style.top = this.object.offsetTop + toppos + this.object.offsetHeight + 'px'; 
        } 
        document.getElementById("getplatform").style.width = '154px'; //this.object.offsetWidth + 'px'; 
        if (document.layers) { 
            document.getElementById("getplatform").style.left = this.object.offsetLeft + leftpos + "px"; 
            document.getElementById("getplatform").style.top = this.object.offsetTop + toppos + this.object.offsetHeight + 2 + "px"; 
        } else { 
            document.getElementById("getplatform").style.left = this.object.offsetLeft + leftpos + "px"; 
            document.getElementById("getplatform").style.top = this.object.offsetTop + toppos + this.object.offsetHeight + 'px'; 
        } 
    } 

    /***************************************************hidden_suggest*********************************************/ 
    //函数功能：隐藏提示框 
    this.hidden_suggest = function() { 
        this.lastkeys_val = 0; 
        document.getElementById("getiframe").style.visibility = "hidden"; 
        document.getElementById("getplatform").style.visibility = "hidden"; 

        //实现始发地,目的地,出发日期 联动 
        //this.object.id == "DepartureCity" 是错误的 所以添加了错误处理代码 
        if (this.object.id == "DepartureCity") { 
            if (document.getElementById("ArrivalCity").value == "中文/拼音/代码") { 
                document.getElementById("ArrivalCity").value = ""; 
            } 
            document.getElementById("ArrivalCity").click(); 
            document.getElementById("ArrivalCity").focus(); 
        } 
    } 

    /******************************** 
    ***************show_suggest*********************************************/ 
    //函数功能：显示提示框 
    this.show_suggest = function() { 
        document.getElementById("getiframe").style.visibility = "visible"; 
        document.getElementById("getplatform").style.visibility = "visible"; 
    } 

    this.is_showsuggest = function() { 
        if (document.getElementById("getplatform").style.visibility == "visible") return true; else return false; 
    } 

    this.sleep = function(n) { 
        var start = new Date().getTime(); //for opera only 
        while (true) if (new Date().getTime() - start > n) break; 
    } 

    this.ltrim = function(strtext) { 
        return strtext.replace(/[$&\|\^*%#@! ]+/, ''); 
    } 

    /***************************************************add_input_text*********************************************/ 
    //函数功能：当用户选中时填充相应的城市名字 

    this.add_input_text = function(keys, szm) { 
        keys = this.ltrim(keys) 
        this.object.value = keys; 
        var id = this.object.id; 
        document.getElementById(this.id2).value = szm; 
        document.getElementById(id).style.color = "#000000"; 
        document.getElementById(id).value = keys; 
    } 

    /***************************************************keys_handleup*********************************************/ 
    //函数功能：用于处理当用户用向上的方向键选择内容时的事件 
    this.keys_handleup = function() { 
        if (this.lastkeys_val > 0) this.lastkeys_val--; 
        var nodes = document.getElementById("getplatform").childNodes; 
        if (this.lastkeys_val < 0) this.lastkeys_val = nodes.length - 1; 
        var b = 0; 
        for (var i = 2; i < nodes.length; i++) { 
            if (b == this.lastkeys_val) { 
                nodes[i].className = "selected"; 
                this.add_input_text(nodes[i].childNodes[0].childNodes[0].childNodes[1].innerHTML, nodes[i].childNodes[0].childNodes[0].childNodes[2].innerHTML); 
            } else { 
                nodes[i].className = "mout"; 
            } 
            b++; 
        } 
    } 

    /***************************************************keys_handledown*********************************************/ 
    //函数功能：用于处理当用户用向下的方向键选择内容时的事件 
    this.keys_handledown = function() { 
        this.lastkeys_val++; 
        var nodes = document.getElementById("getplatform").childNodes; 
        if (this.lastkeys_val >= nodes.length - 2) { 
            this.lastkeys_val--; 
            return; 
        } 
        var b = 0; 
        for (var i = 2; i < nodes.length; i++) { 
            if (b == this.lastkeys_val) { 
                nodes[i].className = "selected"; 
                this.add_input_text(nodes[i].childNodes[0].childNodes[0].childNodes[1].innerHTML, nodes[i].childNodes[0].childNodes[0].childNodes[2].innerHTML); 
            } else { 
                nodes[i].className = "mout"; 
            } 
            b++; 
        } 
    } 

    this.ajaxac_getkeycode = function(e) { 
        var code; 
        if (!e) var e = window.event; 
        if (e.keyCode) code = e.keyCode; 
        else if (e.which) code = e.which; 
        return code; 
    } 

    /***************************************************keys_enter*********************************************/ 
    //函数功能：用于处理当用户回车键选择内容时的事件 
    this.keys_enter = function() { 
        var nodes = document.getElementById("getplatform").childNodes; 
        for (var i = 2; i < nodes.length; i++) { 
            if (nodes[i].className == "selected") { 
                this.add_input_text(nodes[i].childNodes[0].childNodes[0].childNodes[1].innerHTML, nodes[i].childNodes[0].childNodes[0].childNodes[2].innerHTML); 
            } 
        } 
        this.hidden_suggest(); 
    } 

    /***************************************************display*********************************************/ 
    //函数功能：入口函数，将提示层div显示出来 
    //输入参数：object 当前输入所在的对象，如文本框 
    //输入参数：e IE事件对象 
    this.display = function(object, id2, e) { 
        this.id2 = id2; 
        if (!document.getElementById("getplatform")) this.init_zhaobussuggest(); 
        if (!e) e = window.event; 
        e.stopPropagation; 
        e.cancelBubble = true; 
        if (e.target) targ = e.target; else if (e.srcElement) targ = e.srcElement; 
        if (targ.nodeType == 3) targ = targ.parentNode; 

        var inputkeys = this.ajaxac_getkeycode(e); 
        switch (inputkeys) { 
            case 38: //向上方向键 
                this.keys_handleup(object.id); 
                return; break; 
            case 40: //向下方向键 
                if (this.is_showsuggest()) this.keys_handledown(object.id); else this.show_suggest(); 
                return; break; 
            case 39: //向右方向键 
                return; break; 
            case 37: //向左方向键 
                return; break; 
            case 13: //对应回车键 
                this.keys_enter(); 
                return; break; 
            case 18: //对应Alt键 
                this.hidden_suggest(); 
                return; break; 
            case 27: //对应Esc键 
                this.hidden_suggest(); 
                return; break; 
        } 
        this.object = object; 
        if (window.opera) this.sleep(100); //延迟0.1秒 
        parentbject = this; 
        if (this.taskid) window.clearTimeout(this.taskid); 
        this.taskid = setTimeout("parentbject.localtext();", this.delaySec) 
    } 
    //函数功能：从本地js数组中获取要填充到提示层div中的文本内容 
    this.localtext = function() { 
        var id = this.object.id; 
        var suggestions = ""; 
        suggestions = this.getSuggestionByName(); 
        suggestions = suggestions.substring(0, suggestions.length - 1); 

        parentbject.show_suggest(); 
        parentbject.fill_div(suggestions.split(';')); 
        parentbject.fix_div_coordinate(); 
    } 
    /***************************************************getSuggestionByName*********************************************/ 
    //函数功能：从本地js数组中获取要填充到提示层div中的城市名字 
    this.getSuggestionByName = function() { 
        platkeys = this.object.value; 
        var str = ""; 
        platkeys = this.ltrim(platkeys); 
        if (!platkeys) { 
            for (i = 0; i < commoncitys.length; i++) { 
                str += commoncitys[i][2] + "," + commoncitys[i][1] + "," + commoncitys[i][0] + ";"; 
            } 
            return str; 
        } 
        else { 
            platkeys = platkeys.toUpperCase(); 
            for (i = 0; i < citys.length; i++) { 
                if (this.getLeftStr(citys[i][0], platkeys.length).toUpperCase() == platkeys || 
       (citys[i][1].toUpperCase().indexOf(platkeys) != -1) || 
       this.getLeftStr(citys[i][2], platkeys.length).toUpperCase() == platkeys || 
       this.getLeftStr(citys[i][3], platkeys.length).toUpperCase() == platkeys) 
                    str += citys[i][2] + "," + citys[i][1] + "," + citys[i][0] + ";"; 
            } 
            return str; 
        } 
    } 
    /***************************************************getLeftStr************* *************************************/ 
    //函数功能：得到左边的字符串 
    this.getLeftStr = function(str, len) { 

        if (isNaN(len) || len == null) { 
            len = str.length; 
        } 
        else { 
            if (parseInt(len) < 0 || parseInt(len) > str.length) { 
                len = str.length; 
            } 
        } 
        return str.substr(0, len); 
    } 
    /***************************************************parentIndexOf************* *************************************/ 
    //函数功能：得到子结点在父结点的位置 
    function parentIndexOf(node) { 
        for (var i = 0; i < node.parentNode.childNodes.length; i++) { 
            if (node == node.parentNode.childNodes[i]) { return i; } 
        } 
    } 
} 
function showSearch(obj, type) { 
    if (type) { 
        if (obj.value == "") { 
            obj.style.color = "#C1C1C1"; 
            obj.value = "中文/拼音/代码"; 
        } 
    } else { 
        if (obj.value == "中文/拼音/代码") { 
            obj.style.color = "#000000"; 
            obj.value = ""; 
        } 
    } 
} 

///////////////////////////////////////////////////////////////////////// 


