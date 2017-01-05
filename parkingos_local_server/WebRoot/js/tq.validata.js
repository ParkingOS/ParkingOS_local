/*TQcheck 2012-04-15 By Ft
 每做一个修正都请在此备注。
*/
var TFormCheckSign;
var Tcheck = function(f,r,t,tipo,rid){new TQcheck(f,r,t,tipo,rid)};
//验证规则
var Fcheck ={
        msgid: [],
        ermsg: [],
        sucid: [],
        sucmg: [],
        dtype: {
            email: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
            english: /^[A-Za-z]+$/,
            chinese: /^[\u0391-\uFFE5]+$/,
            url: /^(http:\/\/)?[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/,
            ip: /^(0|[1-9]\d?|[0-1]\d{2}|2[0-4]\d|25[0-5]).(0|[1-9]\d?|[0-1]\d{2}|2[0-4]\d|25[0-5]).(0|[1-9]\d?|[0-1]\d{2}|2[0-4]\d|25[0-5]).(0|[1-9]\d?|[0-1]\d{2}|2[0-4]\d|25[0-5])$/,
            zip: /^[1-9]\d{5}$/,
            qq: /^[1-9]\d{4,15}$/,
            number:/^[0-9]\d{0,12}$/,
            qqnum:/^[0-9]\d{4,15}$/,
            mobile:/^((\+86)?(1[4358]\d{9})|(01[4358]\d{9}))|(400\d{7})$/,
            doub:/^\d{1,8}(\.\d{1,3})?$/,
            alpha: /^[0-9a-zA-Z\_]+$/,
            phone: /^((\+86)?(0[1-9]{1}\d{9,10})|([2-9]\d{6,7}))|(400\d{7})$/,
            callnum:/^((1[4358]\d{9})|(01[4358]\d{9}))|(0[1-9]{1}\d{9,10})|([2-9]\d{6,7})|(400\d{7})$/,
            uploadtqfiles:'this.chkfile(v,r)',
            limit: 'this.chklimit(v.length,r.min,r.max)',
            numlimit: 'this.chknumlimit(v,r.min,r.max)',
            ajax: 'this.chkajax(v,r)',
            match: 'this.chkmatch(v,r.to)',
            date: 'this.chkdate(v)',
            rang: 'this.chkrang(v,r.min,r.max)',
            filter: 'this.chkfilter(v,r.accept)',
            group: 'this.chkgroup(e,r)',
            select: 'this.chkgroup(e,r)',
			cactic:'this.chkcactic()'
        }
};
TQcheck = function(f,r,t,tipo,rid){
	this.fo = f;
	this.ru = r;
	this.rid = rid&&rid!=""?rid:null;
	(t=="sub")?this.subform(f,tipo):this.checkform(f)
};
TQcheck.prototype = {
	chkcactic:function(){return true},
    chklimit: function(val, min, max) {
        min = min ? min: Number.MIN_VALUE;
        max = max ? max: Number.MAX_VALUE;
        return (val >= min && val <= max)
    },
    chknumlimit: function(val, min, max) {
    	if(isNaN(parseInt(min))||isNaN(parseInt(min)))return true;
        min = parseInt(min);
        max = parseInt(max);
        val = parseInt(val);
        return (val >= min && val <= max)
    },
    chkrang: function(val, min, max) {
        return this.chklimit(val, min, max)
    },
    chkfile:function(v){
    	return true;
    },
    chkajax: function(val, r) {
		var value_span = "value_span_"+this.fo+"_"+r.name;
		if(T("#"+value_span).innerHTML!=val){
			T(""+this.fo+"_"+r.name+"_t").html('验证中...').acls('chkaj');
			var ret = T.A.sendData(r.url + val+"&r="+Math.random());
			if(ret != "0"){
				if(ret == "1"){return false};
				var ret = eval(ret);
				//ret = [{total:1,url:"ajaxclient.do?action=getmodel&visitor_id=",rows:[{kefuname:'姚秀东',id:'8990421'}]}]
				
				var rows = ret[0].rows;
				var url = ret[0].url;
				var tipstr = "已存在下列数据（点击查看详情）<br>";
				
				for(var kk = 0;kk<rows.length;kk++){
					var name = rows[kk].kefuname;
					var id = rows[kk].id;
					tipstr += "<span style=\"padding-left:3px;padding-top:3px;\">";
					tipstr += "ID:"+id;
					tipstr += "&nbsp;&nbsp;所属人:"+name;
					tipstr += "<input type=\"button\" class=\"button\" style=\"margin-left:5px;\" onclick=\"show_visitor('"+id+"')\" value=\"查看详情\" title=\"点击查看详情\">"
					tipstr += "</span><br />";
				};
				
				Twin({
					Id:"ajaxtips_win",
					Title:"已存在的数据",
					Width:300,
					Height:170,
					Mask:false,
					//Content:"",
					sysfun:function(obj){
						var div = document.createElement("div");
						div.style.width = obj.offsetWidth + "px";
						div.style.height = obj.offsetHeight + "px";
						div.style.overflow = "auto";
						div.innerHTML = tipstr;
						obj.appendChild(div);
						div = null
					}
				})
				/*
					[{total:1,url:"ajaxclient.do?action=getmodel&visitor_id=",rows:[{kefuname:'姚秀东',id:'8990421'}}]
				*/
			//if(ret > 0){
			//	return false
			}else{
				//if(this.rid!=null){
					//if(T("#"+this.rid)&&T("#"+this.rid).value==""){
						T("#"+value_span).innerHTML=val;
					//}
				//};
				return true
			};
		}else{
		 return true	
		}
    },
    chkmatch: function(val, el) {
        var to = T(el).val();
        return (val == to)
    },
    chkdate: function(val) {
        var r = val.replace(/(^\s*)|(\s*$)/g, '').match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
        if (!r) return false;
        var d = new Date(r[1], r[3] - 1, r[4]);
        return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4])
    },
    chkfilter: function(val, r) {
        return new RegExp('^.+\.(?=EXT)(EXT)$'.replace(/EXT/g, r.split(/\s*,\s*/).join('|')), 'gi').test(val)
    },
    chkgroup: function(e, r) {
        var min = r.min || 1,
        max = r.max || e.length,
        o,
        cn = 0;
        for (var i = 0; o = e[i]; i++) {
            if (o.checked != undefined) {
                if (o.checked) cn++
            } else {
                var nosel = r.noselected || '';
                if (o.selected && nosel == o.value) return false;
                if (o.selected) cn++
            }
        }
        return (cn >= min && cn <= max)
    },
    checkform: function(f) {
		var t = this;
        var frm = document.forms[f];
        if (t.ru.length == 0) return false;
        for (var i = 0; i < this.ru.length; i++) {
            var r = t.ru[i];
			var elementId = f +"_"+ r.name;
            if (!T(frm[r.name])) {
            	T.Aremove(t.ru,r)
                //alert('字段' + r.name + '不存在');
                return
            };
            if (!T(""+this.fo+"_"+r.name+"_t")){
            	T.Aremove(t.ru,r)
                //alert('提示用的Span元素 '+this.fo+'_'+r.name+'_t 不存在');
                return
            };
			if(r.type&&r.type.indexOf("ajax")!=-1){
				var span = document.createElement("span");
				span.id = "value_span_"+ frm[r.name].id;
				span.style.display = "none";
				span.innerHTML = frm[r.name].value;
				T(frm[r.name].parentNode).apch(span);
			};
			
		var sign= document.createElement("a");
		sign.innerHTML = "<span class='musttip' style='float:right;background:#c00;height:16px;width:4px;cursor:help' title='此项必须填写'>&nbsp;</span>";
		if(frm[r.name].length && (r.type=="checkbox"||r.type=="radio"||r.type=="group")){
			var frmn = frm[r.name];
			try{r.requir?T(frmn[frmn.length-1].parentNode.parentNode.firstChild).apch(sign,"pr"):""}catch(e){};
			for(j=0;j<frmn.length;j++)
			{
				(T.ie)?
				T(frmn[j]).blur(function(){t.blurcheckbox(t.ru,this,frm)}):
				T.bind(frmn[j],"click",function(){t.blurcheckbox(t.ru,this,frm)})//webkit不支持checkbox焦点事件
			};
		}else{
			try{r.requir?T(frm[r.name].parentNode.parentNode.firstChild).apch(sign,"pr"):""}catch(e){};
			(T.ie)?
			T(frm[r.name]).addentex("blur",function(){t.blurchk(t.ru,this)},this):
			T.bind(frm[r.name],"blur",function(){t.blurchk(t.ru,this)})
		};
    };
        /*T(frm).submit(function(){//普通提交
            var c = Fcheck;
            c.msgid = [];
            c.ermsg = [];
            c.sucid = [];
            c.sucmg = [];
            for (var i = 0; i < t.ru.length; i++) {
                var r = t.ru[i],
                el = frm[r.name];
                if (el.disabled) continue;
                var msgs = t.formchk(el, r);
				if (msgs.length > 0) {for (var n in msgs){t.adderrs(""+t.fo+"_"+r.name+"_t", msgs[n]); }}
                else{t.addsucc(""+t.fo+"_"+r.name+"_t", r.okmsg)}
            }
            if (c.ermsg.length > 0) {
                t.showerrs(c.msgid, c.ermsg);
                t.showsucc(c.sucid, c.sucmg);
                return false
            } else return true
        })
		*/
    },
    subform: function(f,tipo) {
		var t = this;
        var frm = document.forms[f];
        if (t.ru&&t.ru.length == 0) return false;
		var c = Fcheck;
		c.msgid = [];
		c.ermsg = [];
		c.sucid = [];
		c.sucmg = [];
		for (var i = 0; i < t.ru.length; i++) {
			var r = t.ru[i],
			el = frm[r.name];
			if (el.disabled) continue;
			var msgs = t.formchk(el, r);
			if (msgs.length > 0) {
				for (var n in msgs){
					t.adderrs(""+t.fo+"_"+r.name+"_t", msgs[n]);
					T(frm[r.name]).acls("error",true); 
				}
			}else{
				t.addsucc(""+t.fo+"_"+r.name+"_t",r.okmsg);
			}
		};
		if(c.ermsg.length > 0){
			t.showerrs(c.msgid, c.ermsg);
			TFormCheckSign = 0;
			T.loadTip(1,"表单验证有误，请检查！",2,tipo)
			return false;
		}else{
			t.showsucc(c.sucid, c.sucmg);
			TFormCheckSign = 1;
			return true
		};
    },
    blurchk: function(ru,thi){
        for (var i = 0; i < ru.length; i++) {
            var r = ru[i];
            if (r.name == thi.name || r.name == thi.id) break
        };
		if(thi.disabled==false){
        var c = Fcheck,
        msgs = this.formchk(thi, r),
        okmg = r.okmsg || '验证通过';
        c.ermsg = msgs;
        c.msgid = [""+this.fo+"_"+r.name+"_t"];
        c.sucmg = [okmg];
        if (msgs.length > 0){this.showerrs(c.msgid, c.ermsg);T(thi.id).acls("error",true)}
        else{this.showsucc(c.msgid, c.sucmg);T(thi.id).rcls("error")}
		}
    },
    blurcheckbox: function(ru,thi,f){
		var f = f[thi.name];
        for (var i = 0; i < ru.length; i++) {
            var r = ru[i];
            if (r.name == thi.name || r.name == thi.id) break
        };
		if(thi.disabled!=true){
			var c = Fcheck,
			msgs = this.formchk(f, r),
			okmg = r.okmsg || '验证通过';
			c.ermsg = msgs;
			c.msgid = [""+this.fo+"_"+r.name+"_t"];
			c.sucmg = [okmg];
			if (msgs.length > 0){this.showerrs(c.msgid, c.ermsg)}
        else{this.showsucc(c.msgid, c.sucmg)}
		}
    },
    formchk: function(e,r){
        var einfo = [];
		var _isnl = T(e).isnl();
        if (!r.warn) r.warn = T(""+this.fo+"_"+r.name+"_t").html();
        if (r.requir && e.length == undefined && _isnl) einfo.push('必填内容，不能为空');
        else if (r.requir && e.length != undefined && _isnl) einfo.push('必选内容，请选择');
        else if (!r.requir && (e.length != undefined || _isnl)) return [];
        else {
            if (!r.type) return [];
            var v = T.trim(e.value),
            bool;
            var types = r.type.split('|'),
            warns = r.warn.split('|');
            for (var i = 0; i < types.length; i++) {
                switch (types[i]) {
                case 'email':
                case 'english':
                case 'chinese':
                case 'url':
                case 'ip':
                case 'zip':
                case 'number':
                case 'qqnum':
                case 'doub':
                case 'qq':
                case 'mobile':
                case 'alpha':
                case 'phone':
                case 'callnum':
                    bool = this.chkregex(v,Fcheck.dtype[types[i]]);
                    break;
                case 'cusre':
                    bool = this.chkregex(v, new RegExp(r.regexp, 'g'));
                    break;
                case 'cusfn':
                    bool = eval(r.cusfunc);
                    break;
                default:
                    bool = eval(Fcheck.dtype[types[i]]);
                    break
                }
                if (!bool) {
                    einfo.push(warns[i]);
                    break
                }
            }
        }
        return einfo
    },
    showerrs: function(id, msg) {
        for (var i = 0; i < msg.length; i++){T(id[i]).stcs("display","block");T(id[i]).html(msg[i]).acls('chker')}
    },
    showsucc: function(id, msg) {
        for (var i = 0; i < id.length;i++){T(id[i]).stcs("display","none");T(id[i]).html(msg[i]).acls('chkok')}
    },
    chkregex: function(val, reg) {
        return reg.test(val)
    },
    adderrs: function(e, m) {
        Fcheck.msgid.push(e);
        Fcheck.ermsg.push(m)
    },
    addsucc: function(e, m) {
        m = m || '验证通过';
        Fcheck.sucid.push(e);
        Fcheck.sucmg.push(m)
    }
}