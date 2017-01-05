/**
 * Created by GT on 2015/8/27.
 */
/**
 * ������
 * @param x
 * @param y
 * @constructor
 */
var Point = function(x,y){
    this.x = x || 0;
    this.y = y || 0;
};
Point.prototype.set = function(x,y){
    this.x = x || 0;
    this.y = y || 0;
};

var Callback = function(func,ctx){
    this.func = func || function(){};
    this.ctx = ctx || null;
};
Callback.prototype.c = function(p,angle,d){
    this.func.call(this.ctx, p,angle,d);
};


var Utils = {
    scale: 1,
    scaleReal:0,
    planeSpeed:1,
    STAGE_W:0,
    speedScale:0,
    STAGE_H:0,
    HALF_W:0,
    HALF_H:0,
    SLINGSHOT_W:0,
    SLINGSHOT_H:0,
    SLINGSHOT_X:0,
    SLINGSHOT_Y:0,
    BULLET_W:0,
    BULLET_H:0,
    BULLET_X:0,
    BULLET_Y:0,
    CLOTH_W:0,
    CLOTH_H:0,
    pathFactory:null,
    currentDistance:0,
    maxDistance:150,
    isTween:false,
    isBullet:false,
    isBulletFire:false,
    pause:false,
    radianToDegreesFactor : 180 / Math.PI,
    degreeToRadiansFactor : Math.PI / 180,

    clearArray : function(A){
        while(A.length > 0) {
            A.pop();
        }
    },
    radToDeg:function  (radians) {
        return radians * this.radianToDegreesFactor;
    },
    degToRad : function(degrees) {
        return degrees * this.degreeToRadiansFactor;
    },
    /**
     * �����������ĽǶ�ֵ��PI/2 ~ -PI/2
     * @param point1
     * @param point2
     * @returns {number}
     */
    angleBetweenPoints : function (point1, point2) {
        return Math.atan2(point2.y - point1.y, point2.x - point1.x);
    },
    distance: function (p1,p2) {

        var dx = p1.x - p2.x;
        var dy = p1.y - p2.y;

        return Math.sqrt(dx * dx + dy * dy);

    },
    velocityFromRotation: function (rotation, speed, point) {

        if (speed === undefined) { speed = 60; }
        point = point || new Point();

        return point.set((Math.cos(rotation) * speed), (Math.sin(rotation) * speed));

    },

    /**
     * �����ɻ��켣
     * @param v
     * @param k
     * @returns {*}
     */
    catmullRomInterpolation: function (v, k) {

        var m = v.length - 1;
        var f = m * k;
        var i = Math.floor(f);

        if (v[0] === v[m])
        {
            if (k < 0)
            {
                i = Math.floor(f = m * (1 + k));
            }

            return this.catmullRom(v[(i - 1 + m) % m], v[i], v[(i + 1) % m], v[(i + 2) % m], f - i);
        }
        else
        {
            if (k < 0)
            {
                return v[0] - (this.catmullRom(v[0], v[0], v[1], v[1], -f) - v[0]);
            }

            if (k > 1)
            {
                return v[m] - (this.catmullRom(v[m], v[m], v[m - 1], v[m - 1], f - m) - v[m]);
            }

            return this.catmullRom(v[i ? i - 1 : 0], v[i], v[m < i + 1 ? m : i + 1], v[m < i + 2 ? m : i + 2], f - i);
        }

    },

    catmullRom: function (p0, p1, p2, p3, t) {

        var v0 = (p2 - p0) * 0.5, v1 = (p3 - p1) * 0.5, t2 = t * t, t3 = t * t2;

        return (2 * p1 - 2 * p2 + v0 + v1) * t3 + (-3 * p1 + 3 * p2 - 2 * v0 - v1) * t2 + v0 * t + p1;
    },
    /**
     *  ���¼���canvas����
     * @param canvasID
     */
    resizeCanvas:function(canvasID){
        var clientWidth = Math.max(window.innerWidth, document.documentElement.clientWidth);
        var clientHeight = Math.max(window.innerHeight, document.documentElement.clientHeight);

        //if (clientWidth >= 1080){
        //    clientWidth = clientHeight * 0.6;
        //}

        var c = document.getElementById(canvasID);
        c.width = clientWidth;
        c.height = clientHeight;

        this.STAGE_W = clientWidth;
        this.STAGE_H = clientHeight;
        var sc = clientWidth / 1080;
        this.scale = sc > 1 ? 1 : sc;
        this.scaleReal = sc;
        //alert(clientWidth);
        this.maxDistance *= this.scale;

        return clientWidth > 980;
    },

    rectangleToRect:function(r){
        return new createjs.Graphics.Rect(r.x, r.y, r.width, r.height);
    }

};

Utils.isDebug = false;//���ز���ʹ��
Utils.isScore = false;
Utils.getData = function(){
    if (!Utils.isDebug){
        return getData();
    }

    var data = {
        "bullet_count":5,//�ӵ�����
        "plane_empty":8,//�շɻ�
        "plane_gift":3,//��Ʒ�ɻ�
        "plane_ticket":3,//��
        "plane_cash":3,//�ֽ��ɻ�
        "fly_bird":true//�Ƿ�����
    };

    return data;
};
Utils.noBullets = function(){
    if(!Utils.isDebug && !Utils.isScore){
        //Utils.pause = true;
        noBullets();
    } else {
        console.log('bullet is null');
    }
};

Utils.getScroe = function(name, bulletCount){
    if(!Utils.isDebug){
        //Utils.pause = true;
        Utils.isScore = true;
        getScroe(name, bulletCount);
    } else {
        console.log('^_^ hit plane >> ' + name);
    }
};

Utils.hitEmpty = function(){
    if(!Utils.isDebug){
        hitEmpty();
    } else {
        console.log('hit empty plane - -')
    }
};

var PlanePathFactory = function(width) {
    this.baseY = 200 * Utils.scale;
    this.templatePoint = [];
    this.width = width;
    this.ty = 60 * Utils.scale;
    this.widthCount = 9;
    this.paths = [];
    var tx = this.width / (this.widthCount - 1);

    //this.templatePoint[-2] = new Array();
    //for (var i = -1; i < this.widthCount + 1; i ++){
    //    var node = {x:i * tx, y:this.baseY - this.ty * 2};
    //    this.templatePoint[-2][i] = node;
    //}

    this.templatePoint[-1] = new Array();
    for (var i = -4; i < this.widthCount + 4; i ++){
        var node = {x:i * tx, y:this.baseY - this.ty};
        this.templatePoint[-1][i] = node;
    }
    this.templatePoint[0] = new Array();
    for (var i = -4; i < this.widthCount+4; i ++){
        var node = {x:i * tx, y:this.baseY};
        this.templatePoint[0][i] = node;
    }
    this.templatePoint[1] = new Array();
    for (var i = -4; i < this.widthCount + 4; i ++){
        var node = {x:i * this.width / (this.widthCount - 1), y:this.baseY + this.ty};
        this.templatePoint[1][i] = node;
    }

    //this.templatePoint[2] = new Array();
    //for (var i = -1; i < this.widthCount + 1; i ++){
    //    var node = {x:i * tx, y:this.baseY + this.ty * 2};
    //    this.templatePoint[2][i] = node;
    //}

};

PlanePathFactory.prototype.constructor = PlanePathFactory;
PlanePathFactory.prototype.createPath = function(indexs){
    var path = {
        x:[],
        y:[],
        paths:[]
    };

    for (var i = 0; i < indexs.v.length; i ++){
        var node = this.templatePoint[indexs.v[i]][indexs.h[i]];
        path.x[i] = node.x;
        path.y[i] = node.y;
    }

    return path;
};

PlanePathFactory.prototype.getLevelPath = function(index, level){
    var path = this.paths[index];
    var newpath = {
            x:path.x.slice(0),
            y:path.y.slice(0),
            paths:path.paths.slice(0)
        },
        incrementY = level * this.ty,
    //ix = 0,
    //x = 1 / this.width,
    //px,
    //py,
    //node,
        i;

    for (i = 0; i < newpath.y.length; i++){
        newpath.y[i] += incrementY;
    }

    //for (i = 0; i < 1; i += x){
    //    px = Utils.catmullRomInterpolation(newpath.x, i);
    //    py = Utils.catmullRomInterpolation(newpath.y, i);
    //    node = {x:px, y:py, angle:0};
    //    if (ix > 0) {
    //        node.angle = Utils.angleBetweenPoints(newpath.paths[ix - 1], node);
    //    }
    //
    //    newpath.paths.push(node);
    //    ix++;
    //}

    return newpath;
};
PlanePathFactory.prototype.initPath = function(){
    var indexs = [
            //1
            {
                v:[ 0, -1, 1, -1, 1, -1, 0, 1, -1, 1, -1, 1,  0],
                h:[-1,  0, 2,  4, 6,  8, 9, 8,  6, 4,  2, 0, -1]
            },
            //2
            {
                v:[ 0, -1, 1, -1, 0, 1, -1, 1,  0],
                h:[-1,  1, 4,  7, 9, 7,  4, 1, -1]
            },
            //3
            {
                v:[ 0, -1, 1, -1, 0, 1, -1, 1,  0],
                h:[-1,  0, 4,  8, 9, 8,  4, 0, -1]
            },
            //4
            {
                v:[ 0, -1, 1, -1, 0, 1, -1, 1,  0],
                h:[-1,  0, 5,  8, 9, 8,  3, 0, -1]
            },
            //5
            {
                v:[ 0, -1, 1, -1, 0, 1, -1, 1,  0],
                h:[-1,  0, 6,  8, 9, 8,  2, 0, -1]
            },
            //6
            {
                v:[ 0, -1, 1, -1, 1, -1, 1,  0],
                h:[-1,  0, 7,  8, 9,  2, 1, -1]
            },
            //7
            {
                v: [ 0, -1, 1, 0, -1, 1,  0],
                h: [-1,  0, 8, 9,  8, 0, -1]
            },
            //8
            {
                v: [ 0, -1, 0, -1, 1, -1, 0, 1, 0, 1, -1, 1,  0],
                h: [-1,  0, 1,  2, 5,  8, 9, 8, 7, 6,  3, 1, -1]
            },
            //9
            {
                v: [ 0, -1, 1, -1, 1, 0, -1, 1, 0, 1,  0],
                h: [-1,  1, 2,  4, 8, 9,  7, 5, 4, 3, -1]
            }
        ],
        i,
        N = indexs.length,
        index;

    for (i = 0; i < N; i++){
        index = indexs[i];
        this.paths.push(this.createPath(index));
    }

    var birdIndex = {
        v: [ 0, -1, 0, -1, 0, -1, 0, -1, 0, -1,  0],
        h: [-4, -2, 1,  5, 8, 12, 8,  5, 1, -2, -4]
    };
    this.paths[-1] = this.createPath(birdIndex);

    //clear templatePoint
    Utils.clearArray(this.templatePoint);
};
/**
 * Created by GT on 2015/8/27.
 */
var ResourceLoader = function(){
    createjs.LoadQueue.call(this, false);
    this.ID_BG = 'bg';
    this.ID_SLINGSHOT = 'slingshot';
    this.ID_BULLET = 'bullet';
    this.ID_BULLET_COUNT = 'bullet_count';
    this.ID_PLANE_CASH = 'plane_cash';
    this.ID_PLANE_EMPTY = 'plane_empty';
    this.ID_PLANE_GIFT = 'plane_gift';
    this.ID_PLANE_TICKET = 'plane_ticket';
    this.ID_PLANE_BIRD = 'fly_bird';
    this.ID_CLOTH = 'cloth';

    this.manifest = [
        {src:'bg.png', id:this.ID_BG},
        {src:'slingshot.png',id:this.ID_SLINGSHOT},
        {src:'bullet.png',id:this.ID_BULLET},
        {src:'bullet_count.png',id:this.ID_BULLET_COUNT},
        {src:'cloth.png',id:this.ID_CLOTH},
        {src:'plane_cash.json', id:this.ID_PLANE_CASH, type:'spritesheet'},
        {src:'plane_empty.json', id:this.ID_PLANE_EMPTY, type:'spritesheet'},
        {src:'plane_gift.json', id:this.ID_PLANE_GIFT, type:'spritesheet'},
        {src:'plane_ticket.json', id:this.ID_PLANE_TICKET, type:'spritesheet'},
        {src:'birdsheet.json', id:this.ID_PLANE_BIRD, type:'spritesheet'}
    ];
    this.basePath = 'images/flygame/';

};

ResourceLoader.prototype = Object.create(createjs.LoadQueue.prototype);
ResourceLoader.prototype.constructor = ResourceLoader;
/**
 * ��ʼ������Դ
 */
ResourceLoader.prototype.startLoad = function(){
    this.loadManifest(this.manifest,true,this.basePath);
};

var StaticContainer = function(){
    createjs.Container.call(this);
};
StaticContainer.prototype = Object.create(createjs.Container.prototype);
StaticContainer.prototype.constructor = StaticContainer;

StaticContainer.prototype.initChild = function(){
    var loader = this.stage.loader;
    var bmp = loader.getResult(loader.ID_BG),
        scaleX = Utils.STAGE_W / bmp.width,
        scaleY = Utils.STAGE_H / bmp.height;

    bg = new createjs.Bitmap(bmp);
    bg.setTransform(0,0,scaleX,scaleY);
    this.addChild(bg);

    bmp = loader.getResult(loader.ID_SLINGSHOT);
    Utils.SLINGSHOT_W = bmp.width * Utils.scale;
    Utils.SLINGSHOT_H = bmp.height * Utils.scale;

    Utils.SLINGSHOT_X = Utils.HALF_W;
    Utils.SLINGSHOT_Y = Utils.STAGE_H - 300 * Utils.scale;

    var slingshot = new createjs.Bitmap(bmp);
    slingshot.setTransform(Utils.SLINGSHOT_X, Utils.SLINGSHOT_Y,Utils.scale, Utils.scale);
    slingshot.regX = bmp.width / 2;
    slingshot.regY = bmp.height / 2;
    this.addChild(slingshot);
};

var RubberContainer = function(){
    createjs.Container.call(this);
    this.rubber = new createjs.Shape();
    this.cloth = null;
    this.target = new Point();
    this.left = new Point();
    this.right = new Point();
    this.center = new Point();
    this.w = 0;
    this.h = 0;

    this.tweenComplete = function(){};
    this.tweenCompleteContext = this;
};
RubberContainer.prototype = Object.create(createjs.Container.prototype);
RubberContainer.prototype.constructor = RubberContainer;
RubberContainer.prototype.initChild = function(){
    this.left.x = Utils.SLINGSHOT_X - Utils.SLINGSHOT_W/2 + 14 * Utils.scale;
    this.left.y = Utils.SLINGSHOT_Y - Utils.SLINGSHOT_H / 2 + 33 * Utils.scale;
    this.right.x = Utils.SLINGSHOT_X + Utils.SLINGSHOT_W / 2 - 13 * Utils.scale;
    this.right.y = this.left.y;

    var loader = this.stage.loader;
    var bmp = loader.getResult(loader.ID_CLOTH);
    this.w =  bmp.width;
    this.h =  bmp.height;
    Utils.CLOTH_W = bmp.width * Utils.scale;
    Utils.CLOTH_H = bmp.height * Utils.scale;

    this.cloth = new createjs.Bitmap(bmp);

    this.target.x = Utils.BULLET_X;
    this.target.y = Utils.BULLET_Y;
    this.addChild(this.cloth);

    this.center.set(Utils.HALF_W, this.left.y);

    this.addChild(this.rubber);
    this.drawRubber();
};

RubberContainer.prototype.loop = function(evt){
};

RubberContainer.prototype.drawRubber = function(target,angle,d){
    var g = this.rubber.graphics;
    g.clear();
    g.setStrokeStyle(10 * Utils.scale, 'round', 'round');
    g.beginStroke("#F0F");
    g.beginFill("#F0F");
    //g.rect(this.center.x,this.center.y,1,1);
    //g.rect(Utils.SLINGSHOT_X,Utils.SLINGSHOT_Y,1,1);
    if (!angle){
        angle = Math.PI / 2;
    }

    if (!d){
        d = 0;
    }

    var ro = angle - Math.PI / 2;
    if (target){
        this.target = target;
    }
    var h = Utils.BULLET_H/2 + Utils.CLOTH_H/2 - 5 * Utils.scale;
    this.target.x = this.target.x + h * Math.cos(angle);
    this.target.y = this.target.y + h * Math.sin(angle);

    //console.log(Utils.radToDeg(ro));
    //g.rect(0, Utils.STAGE_H * 2 / 3,Utils.STAGE_W,1);

    g.beginStroke("#93690A");
    g.beginFill("#93690A");

    h = this.w /2 - 8;
    h = h * Utils.scale;

    g.moveTo(this.left.x, this.left.y);
    g.lineTo(this.target.x - h * Math.cos(ro), this.target.y - h * Math.sin(ro));

    g.moveTo(this.right.x,this.right.y);
    g.lineTo(this.target.x + h * Math.cos(ro), this.target.y + h * Math.sin(ro));

    this.cloth.setTransform(this.target.x, this.target.y,Utils.scale,Utils.scale);
    this.cloth.rotation = Utils.radToDeg(ro);
    this.cloth.regX = this.w / 2;
    this.cloth.regY = this.h / 2;
};

RubberContainer.prototype.up = function(evt){
    var tx = this.target.x - this.center.x,
        ty = this.target.y - this.center.y,
        lt = new Point(this.center.x - tx, this.center.y - ty),
        rc = new Point(this.center.x + tx / 2, this.center.y + ty / 2),
        lc = new Point(this.center.x - tx / 2, this.center.y - ty / 2),
        c = new Point(Utils.BULLET_X, Utils.BULLET_Y),
        t = 50 + 150 * Utils.currentDistance / Utils.maxDistance;

    var tween = createjs.Tween.get(this.target).to({x:lt.x,y:lt.y},t * 4)
        .to({x:rc.x,y:rc.y}, t * 3)
        .to({x:lc.x,y:lc.y}, t * 2)
        .to({x: c.x,y: c.y}, t * 2.5);
    Utils.isTween = true;
    tween.on("change", function(){
        this.drawRubber();
    },this);

    tween.call(this.tweenComplete,[],this.tweenCompleteContext);
};
/**
 * Created by GT on 2015/8/27.
 */
var Planes = function(){
    createjs.Container.call(this);
    Utils.pathFactory = new PlanePathFactory(Utils.STAGE_W);
};

Planes.prototype = Object.create(createjs.Container.prototype);
Planes.prototype.constructor = Planes;
Planes.prototype.initChild = function(data){
    var loader = this.stage.loader;
    var plane = null;
    if (data.fly_bird){
        plane = new PaperPlane(loader.ID_PLANE_BIRD, loader,1,0);
        this.addChild(plane);
    }

    if (data.plane_cash > 0) {
        for (var i = 0; i < data.plane_cash; i++){
            plane = new PaperPlane(loader.ID_PLANE_CASH, loader, 2, 0);
            this.addChild(plane);
        }
    }

    if (data.plane_gift > 0) {
        for (var i = 0; i < data.plane_gift; i++){
            plane = new PaperPlane(loader.ID_PLANE_GIFT, loader,1.5, 1);
            this.addChild(plane);
        }
    }

    plane = new PaperPlane(loader.ID_PLANE_EMPTY, loader,1, 1);
    this.addChild(plane);

    if (data.plane_ticket > 0) {
        for (var i = 0; i < data.plane_ticket; i++){
            plane = new PaperPlane(loader.ID_PLANE_TICKET, loader,1.2, 2);
            this.addChild(plane);
        }
    }

    plane = new PaperPlane(loader.ID_PLANE_EMPTY, loader,0.8, 1);
    this.addChild(plane);

    if (data.plane_empty > 0) {
        for (var i = 0; i < data.plane_empty-2; i++){
            if (i % 2 == 0){
                plane = new PaperPlane(loader.ID_PLANE_EMPTY, loader,1,4);
            } else {
                plane = new PaperPlane(loader.ID_PLANE_EMPTY, loader,0.8,5);
            }
            this.addChild(plane);
        }
    }

    Utils.pathFactory.initPath();
};

Planes.prototype.loop = function(evt){
    var N = this.children.length,
        i;

    for (i = 0; i < N; i ++){
        this.getChildAt(i).loop(evt);
    }

};
Planes.prototype.show = function(){
    var i,
        plane,
        N = this.children.length;
    for (i=0; i<N; i++){
        plane = this.getChildAt(i);
        this.showOne(plane);
    }
};

Planes.prototype.showOne = function(plane,reShow){
    var reshow = reShow || false;
    var path,
        PN = Utils.pathFactory.paths.length,
        index,
        loader = this.stage.loader;
    index = Math.floor(Math.random() * PN);

    if (plane.name == loader.ID_PLANE_BIRD){
        index = -1;
    }
    path = Utils.pathFactory.getLevelPath(index, plane.level);
    plane.show(path,reshow);
};

Planes.prototype.drawPath = function(g){
    var N = this.children.length;
    for(var i = 0; i < N; i++){
        this.getChildAt(i).drawPath(g);
    }
};

var PaperPlane = function(key,loader,speed,level){
    var s = loader.getResult(key);
    createjs.Sprite.call(this,s);
    this.x = 0;
    this.y = 0;
    this.scaleX = Utils.scale;
    this.scaleY = Utils.scale;
    var b = this.getTransformedBounds();
    this.regX = b.width/2;
    this.regY = b.height/2;
    this.setBounds(0 + b.width / 3, 0 + b.height / 3, b.width / 3, b.height / 3);
    this.visible = false;
    this.path = null;
    this.pi = 0;
    this.w = b.width;
    this.h = b.height;
    this.name = key;
    this.lastPoint = null;
    this.speed = speed || 1;
    this.pathCount = Utils.STAGE_W / this.speed;
    console.log('pathCount = ' + this.pathCount);
    this.level = level;
};

PaperPlane.prototype = Object.create(createjs.Sprite.prototype);
PaperPlane.prototype.constructor = PaperPlane;
PaperPlane.prototype.loop = function(evt){
    if (this.path == null) return;

    var px = Utils.catmullRomInterpolation(this.path.x, this.pi/this.pathCount);
    var py = Utils.catmullRomInterpolation(this.path.y, this.pi/this.pathCount);
    var point = new Point(px,py);
    var angle = this.lastPoint === null ? 0:Utils.angleBetweenPoints(this.lastPoint,point);
    this.lastPoint = point;
    this.x = px;
    this.y = py;

    if (this.pi > this.pathCount / 2){
        this.rotation = Utils.radToDeg(angle - Math.PI);
    } else {
        this.rotation = Utils.radToDeg(angle);
    }
    this.pi++;
    if (this.pi >= this.pathCount){
        this.pi = 0;
    }

    if (px > Utils.STAGE_W + this.w){
        this.gotoAndPlay('left');
    } else if (px < 0 - this.w){
        this.gotoAndPlay('right');
    }

};
PaperPlane.prototype.show = function(path, reshow){
    this.visible = true;
    if (reshow){
        this.pi = 0;
        Utils.clearArray(this.path.paths);
        Utils.clearArray(this.path.x);
        Utils.clearArray(this.path.y);
    } else {
        this.pi = Math.floor(Math.random() * this.pathCount * 2 / 4) + this.pathCount/4;
    }

    this.path = path;

    if (this.pi < this.pathCount/2) {
        this.gotoAndPlay('right');
    } else if(this.pi > this.pathCount/2){
        this.gotoAndPlay('left');
    }
};
PaperPlane.prototype.drawPath = function(g){
    for (var i = 0; i < this.path.paths.length; i++) {
        g.rect(this.path.paths[i].x, this.path.paths[i].y, 1, 1);
    }

    for (var i = 0; i < this.path.x.length; i++) {
        g.rect(this.path.x[i] - 3, this.path.y[i] - 3, 6,6);
    }
};
PaperPlane.prototype.getB = function(){
    var b = this.getTransformedBounds();
    var x = b.x + b.width/2 - this.w / 4;
    var y = b.y + b.height/2 - this.h / 4;
    return new createjs.Rectangle(x, y, this.w/2, this.h/2);
};
/**
 * Created by GT on 2015/8/27.
 */
var Bullet = function(bmp){
    createjs.Bitmap.call(this, bmp);
    this.speed = new Point(0,0);
    this.gravity = new Point(0,0);
    this.w = bmp.width;
    this.h = bmp.height;
    //��ָ���µĵط�
    this.positionDown = new Point();
    //��ָ�ƶ�ʱʱλ��
    this.position = new Point();

    this.cAngle = 0;
    this.cDistance = 0;
    this.maxDistance = Utils.maxDistance;
    this.baseSpeed = 600;
    this.tspeed = 1000;
    this.baseGravityY = 800;

    this.moveCallback = null;
    this.setBounds(this.w / 10, this.h / 10,this.w * 4/5, this.h * 4 / 5);

    this.isDown = false;
};

Bullet.prototype = Object.create(createjs.Bitmap.prototype);
Bullet.prototype.constructor = Bullet;

Bullet.prototype.loop = function(evt){
    if (!Utils.isBulletFire){
        return;
    }

    //ʱ��t����
    var deltaS = evt.delta / 1000;
    //�ٶȱ���
    var s = this.speed.y * deltaS + this.gravity.y * deltaS * deltaS / 2;
    s = s * Utils.speedScale;
    this.speed.x = this.speed.x + this.gravity.x * deltaS;
    this.speed.y = this.speed.y + this.gravity.y * deltaS;
    this.x = this.x + this.speed.x * deltaS;
    this.y = this.y + s;
};

Bullet.prototype.down = function(evt){
    this.isDown = true;
    this.positionDown.set(evt.stageX,evt.stageY);
    this.position.set(evt.stageX,evt.stageY);
};
Bullet.prototype.up = function(evt){
    Utils.currentDistance = this.cDistance;
    this.gravity.y = this.baseGravityY;
    var s = this.baseSpeed + this.cDistance * this.tspeed / this.maxDistance;
    //s = s * Utils.speedScale;
    Utils.velocityFromRotation(this.cAngle, -s, this.speed);
    Utils.isBulletFire = true;
    console.log('speed y' + this.speed.y);
};

Bullet.prototype.move = function(evt){
    if (!this.isDown){
        return;
    }

    this.position.set(evt.stageX,evt.stageY);
    this.cAngle = Utils.angleBetweenPoints(this.positionDown,this.position);

    if (this.cAngle < Math.PI / 6 || this.cAngle > Math.PI * 5 / 6) {
        return;
    }

    this.cDistance = Utils.distance(this.positionDown,this.position);
    this.cDistance = this.cDistance / 3;
    this.cDistance = Math.min(this.cDistance, this.maxDistance);

    this.x = Utils.BULLET_X + Math.cos(this.cAngle) * this.cDistance;
    this.y = Utils.BULLET_Y + Math.sin(this.cAngle) * this.cDistance;

    if (this.moveCallback != null){
        this.moveCallback.c(new Point(this.x,this.y),this.cAngle, this.cDistance);
    }

    this.rotation = Utils.radToDeg(this.cAngle - Math.PI/2);
};


Bullet.prototype.reset = function(){
    this.speed = new Point(0,0);
    this.gravity = new Point(0,0);
    this.visible = true;
    this.setTransform(Utils.BULLET_X, Utils.BULLET_Y,Utils.scale,Utils.scale);
    this.regX = this.w / 2;
    this.regY = this.h / 2;
    this.cDistance = 0;
    this.cAngle = 0;
    Utils.isBullet = true;
    Utils.isBulletFire = false;
};

Bullet.prototype.getB = function(){
    return this.getTransformedBounds();
};

var Bullets = function(){
    createjs.Container.call(this);
    this.baseSpeed = 20;
    this.cacheBullet = null;
    this.s = null;
    this.bulletHint = null;
    this.moveCallback = null;
    this.showCallback = null;
    this.bulletCount = 0;
};

Bullets.prototype = Object.create(createjs.Container.prototype);
Bullets.prototype.constructor = Bullets;
Bullets.prototype.initChild = function(bullet_count){
    this.bulletCount = bullet_count;
    var loader = this.stage.loader;
    this.show();
    var bmp = loader.getResult(loader.ID_BULLET_COUNT);
    var bulletCount = new createjs.Bitmap(bmp);
    bulletCount.x = 100 * Utils.scale;
    bulletCount.y = Utils.STAGE_H - 80*Utils.scale;
    bulletCount.regX = bmp.width/2;
    bulletCount.regY = bmp.height/2;
    this.addChild(bulletCount);

    this.bulletHint = new createjs.Text('' + bullet_count,'38pt verdana','#000');
    this.bulletHint.x = (80 + 100) * Utils.scale;
    this.bulletHint.y = bulletCount.y;
    this.bulletHint.regY = bmp.height/2;
    this.addChild(this.bulletHint);
};

Bullets.prototype.show = function(){
    var loader = this.stage.loader,
        bmp,
        bullet;
    bmp = loader.getResult(loader.ID_BULLET);
    Utils.BULLET_W = bmp.width * Utils.scale;
    Utils.BULLET_H = bmp.height * Utils.scale;

    bullet = new Bullet(bmp);
    Utils.BULLET_X = Utils.SLINGSHOT_X;
    Utils.BULLET_Y = Utils.SLINGSHOT_Y - 50 * Utils.scale;
    bullet.setTransform(Utils.BULLET_X, Utils.BULLET_Y,Utils.scale,Utils.scale);
    bullet.visible = false;
    this.addChild(bullet);
    bullet.moveCallback = this.moveCallback;
    this.cacheBullet = bullet;
    this.reShow();
    if (this.showCallback){
        //this.showCallback.c(new Point(bullet.x,bullet.y));
    }

    Utils.speedScale = (Utils.BULLET_Y - Utils.pathFactory.baseY) / 1370;
};

Bullets.prototype.reShow = function(){
    //this.bulletCount--;
    console.log('bullet count ' + this.bulletCount);
    if (this.bulletCount <= 0){
        this.bulletCount = 0;
        Utils.noBullets();
    } else{
        this.cacheBullet.reset();
    }
};

Bullets.prototype.loop = function(evt){
    if (this.cacheBullet != null){
        this.cacheBullet.loop(evt);
    }

    if (this.bulletHint != null){
        this.bulletHint.text = '' + this.bulletCount;
    }
};
Bullets.prototype.down = function(evt){
    if (this.cacheBullet != null) {
        this.cacheBullet.down(evt);
    }
};
Bullets.prototype.up = function(evt){
    if (this.cacheBullet != null) {
        this.cacheBullet.up(evt);
    }
};
Bullets.prototype.move = function(evt){
    if (this.cacheBullet != null) {
        this.cacheBullet.move(evt);
    }
};
/**
 * Created by GT on 2015/8/27.
 */
/**
 * ������Ϸ��̨
 * @param canvasID
 * @constructor
 */
var CoreStage = function(canvasID, data){
    createjs.Stage.call(this, canvasID);
    Utils.STAGE_W = this.canvas.width;
    Utils.STAGE_H = this.canvas.height;
    Utils.HALF_W = Utils.STAGE_W / 2;
    Utils.HALF_H = Utils.STAGE_H / 2;
    this.data = data;
    this.loader = new ResourceLoader();
    this.loader.on('complete',this.initChild,this);

    this.staticContainer = new StaticContainer();
    this.bullets = new Bullets();
    this.planes = new Planes();
    this.rubber = new RubberContainer();
    this.debugShape = new createjs.Shape();
    this.fps = new createjs.Text("", "36px Arial", "#777");
    this.movetext = new createjs.Text("", "26px Arial", "#777");
    this.updateFlag = 0;
    this.overlapFlag = 0;
};

CoreStage.prototype = Object.create(createjs.Stage.prototype);
CoreStage.prototype.constructor = CoreStage;
CoreStage.prototype.initChild = function(){
    this.addChild(this.staticContainer);
    this.staticContainer.initChild();

    this.addChild(this.bullets);
    this.bullets.initChild(this.data.bullet_count);
    //this.bullets.show();

    this.addChild(this.planes);
    this.planes.initChild(this.data);
    this.planes.show();

    this.addChild(this.rubber);
    this.rubber.initChild();

    if (Utils.isDebug){
        this.fps.x = Utils.HALF_W;
        this.fps.y = 50;
        this.addChild(this.fps);
        this.movetext.x = 100;
        this.movetext.y = 100;
        this.addChild(this.movetext);
        this.addChild(this.debugShape);
    }

    this.update();
};
CoreStage.prototype.startLoop = function(){
    createjs.Ticker.framerate = 60;
    createjs.Ticker.on('tick',this.loop,this);
    createjs.Touch.enable(this,true);
    this.on('stagemousedown', this.down, this);
    this.on('stagemouseup', this.up, this);
    this.on('stagemousemove', this.move, this);
    var func = function(p,angle,d){
        this.rubber.drawRubber(p,angle,d);
    };
    this.bullets.moveCallback = new Callback(func, this);
    this.bullets.showCallback = new Callback(func, this);
    this.rubber.tweenComplete = function(){
        Utils.isTween = false;
        if (!Utils.isBullet){
            this.bullets.reShow();
        }
    };
    this.rubber.tweenCompleteContext = this;
};
CoreStage.prototype.loop = function(evt){
    if (Utils.pause){
        return;
    }
    this.bullets.loop(evt);
    this.planes.loop(evt);
    this.update(evt);

    //this.debug();
    this.fps.text = 'fps:'+createjs.Ticker.getMeasuredFPS().toFixed(2);

    //this.updateFlag++;
    //if (this.updateFlag % 2 == 0){
    //    this.update(evt);
    //    this.updateFlag = 0;
    //}

    if (Utils.isBulletFire){
        this.overlapFlag++;
        if (this.overlapFlag % 2 == 0){
            this.overlapFlag = 0;
            this.overlap();
        }
        this.checkOutBounds();
    }
};

CoreStage.prototype.down = function(evt){
    if (evt.stageY < Utils.STAGE_H*2 / 3){
        return;
    }
    this.bullets.down(evt);
};

CoreStage.prototype.up = function(evt){
    if (this.bullets.cacheBullet.positionDown.y < Utils.STAGE_H*2/3){
        return;
    }

    if (this.bullets.cacheBullet.position.y < Utils.STAGE_H*2/3){
        return;
    }

    if (!Utils.isBulletFire){
        this.bullets.up(evt);
        this.rubber.up(evt);
    }
};

CoreStage.prototype.move = function(evt){
    if (!Utils.isBulletFire){
        this.bullets.move(evt);
    }
};

/**
 * �����Ƿ���ײ
 */
CoreStage.prototype.overlap = function(){
    var a = this.bullets.cacheBullet,
        planes = this.planes.children,
        i,
        p;

    if (a == null || planes == null) {
        return;
    }

    if (Array.isArray(planes) && planes.length > 0){
        for (i = 0; i < planes.length; i++){
            p = planes[i];
            if (a.visible && p.visible && a.getB().intersects(p.getB())){
                console.log('overlap');
                a.visible = false;
                p.visible = false;
                Utils.isBullet = false;
                this.bullets.bulletCount--;
                if (!Utils.isTween){
                    this.bullets.reShow();
                }
                this.planes.showOne(p,true);

                if (p.name == this.loader.ID_PLANE_EMPTY){
                    Utils.hitEmpty();
                } else {
                    Utils.getScroe(p.name, this.bullets.bulletCount);
                }

                return;
            }
        }
    }

};

/**
 * �����ӵ��Ƿ�����
 */
CoreStage.prototype.checkOutBounds = function(){
    var b = this.bullets.cacheBullet;
    if (b == null || !b.visible){
        return;
    }

    var bound = b.getTransformedBounds();

    if (bound.x + bound.width < 0 || bound.y + bound.height < 0 || bound.x > Utils.STAGE_W || bound.y > Utils.STAGE_H){
        console.log('bullet is outbounds!!');
        b.visible = false;
        Utils.isBullet = false;
        this.bullets.bulletCount--;
        if (!Utils.isTween){
            this.bullets.reShow();
        }
    }
};

/**
 * debug�����ɻ�·�ߺ���ײ�߽�
 */
CoreStage.prototype.debug = function(){
    var g = this.debugShape.graphics;
    g.clear();
    g.setStrokeStyle(1, 'round', 'round');
    g.beginStroke("#F0F");

    var planes = this.planes.children,
        i,
        p;

    if (planes == null) {
        return;
    }

    if (Array.isArray(planes) && planes.length > 0){
        for (i = 0; i < planes.length; i++){
            p = planes[i];
            if (p.visible){
                g.append(Utils.rectangleToRect(p.getB()));
            }
        }
    }

    if (this.bullets.cacheBullet != null && this.bullets.cacheBullet.visible) {
        g.append(Utils.rectangleToRect(this.bullets.cacheBullet.getB()));
    }

    g.beginStroke('#01DF01');
    this.planes.drawPath(g);
    g.rect(Utils.STAGE_W/2,0,1,Utils.STAGE_H);

    g.beginStroke('#DF013A');
    g.rect(Utils.SLINGSHOT_X,Utils.SLINGSHOT_Y,3,3);
};
/**
 * Created by GT on 2015/8/27.
 */
var stage,data;
function startGame(){
    data = Utils.getData();
    if (!Utils.resizeCanvas('game_canvas')){
        //alert("��ʱ��֧�ָû��͡�");
    }
    stage = new CoreStage('game_canvas', data);
    stage.loader.startLoad();
    stage.startLoop();
}

function resumeGame(){
    Utils.isScore = false;
    if (stage.bullets.bulletCount > 0){
        //Utils.pause = false;
    } else {
        Utils.noBullets();
    }
}

if(Utils.isDebug){
    window.onload = function(){
        startGame();
    };
}