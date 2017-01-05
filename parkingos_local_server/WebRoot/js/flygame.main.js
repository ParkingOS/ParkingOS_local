/**
 * Created by Godwin on 2015/7/30.
 * 优化之后，update的时候计算运行轨迹。
 */
var My = {};
My.debug = false;
My.follow = false;
My.path = false;
var Utils = {};

Utils.KEY_FLY_BIRD = 'fly_bird';
Utils.KEY_PLANE_EMPTY = 'plane_empty';
Utils.KEY_PLANE_CASH = 'plane_cash';
Utils.KEY_PLANE_GIFT = 'plane_gift';
Utils.KEY_PLANE_TICKET = 'plane_ticket';
Utils.isFire = false;
Utils.isTween = false;
Utils.isBullet = false;
Utils.speedScale = 1;
Utils.isDebug = false;//本地测试使用
Utils.isScore = false;

Utils.getData = function(){
    if (!Utils.isDebug){
        return getData();
    }

    var data = {
        "bullet_count":5,//子弹数量
        "plane_empty":0,//空飞机
        "plane_gift":0,//礼品飞机
        "plane_ticket":0,//劵
        "plane_cash":0,//现金飞机
        "fly_bird":true//是否有鸟
    };

    return data;
};
Utils.noBullets = function(game){
    if(!Utils.isDebug){
        //game.paused = true;
        noBullets();
    }
};

Utils.getScroe = function(game, name, bulletCount){
    if(!Utils.isDebug){
        //game.paused = true;
        getScroe(name, bulletCount);
    }
};

Utils.hitEmpty = function(){
    if(!Utils.isDebug){
        hitEmpty();
    }
};


Utils.getTargetLeftTopPoint = function(target){
    var node = {x:0, y:0};
    node.x = target.x - target.width / 2;
    node.y = target.y - target.height / 2;
    return node;
};

Utils.getTargetRightTopPoint = function(target){
    var node = {x:0, y:0};
    node.x = target.x + target.width / 2;
    node.y = target.y - target.height / 2;
    return node;
};

Utils.clearArray = function(A){
    while(A.length > 0) {
        A.pop();
    }
};

var Bullet = function (game, key){
    Phaser.Sprite.call(this, game, 0, 0, key);
    game.physics.arcade.enableBody(this);
    this.anchor.set(0.5);
    this.checkWorldBounds = true;
    this.outOfBoundsKill = true;
    this.exists = false;
    this.isFly = false;
    this.oldY = 0;
    this.oldX = 0;
    this.currentAngle = 0;
    this.currentDistance = 0;
    this.maxDistance = 150;
    this.baseSpeed = 580;
    this.tspeed = 1000;
};

Bullet.prototype = Object.create(Phaser.Sprite.prototype);
Bullet.prototype.constructor = Bullet;

Bullet.prototype.show = function(node){
    this.reset(node.x,node.y);
    this.body.gravity.y = 0;
    this.rotation = 0;
    this.isFly = false;
    Utils.isFire = false;
    Utils.isBullet = true;
    this.oldX = node.x;
    this.oldY = node.y;
    this.currentAngle = Math.PI / 2;
    this.currentDistance = 0;
    this.body.setSize(this.width, this.height, -4, -4);
    return this;
};

Bullet.prototype.fire = function(){
    if (this.currentAngle < Math.PI / 6 || this.currentAngle > Math.PI * 5 / 6 || this.currentDistance < 10) {
        return false;
    }

    this.body.gravity.y = 900 * Utils.speedScale;
    this.isFly = true;
    Utils.isFire = true;

    var speed = this.baseSpeed + this.currentDistance * this.tspeed / this.maxDistance;
    speed *= Utils.speedScale;

    this.game.physics.arcade.velocityFromRotation(this.currentAngle, -speed, this.body.velocity);
    return true;
};

Bullet.prototype.move = function(downPoint, point){
    var angle = this.game.math.angleBetweenPoints(downPoint,point);
    if (angle < Math.PI / 6 || angle > Math.PI * 5 / 6) {
        return;
    }
    this.currentAngle = angle;
    var distance = this.game.math.distance(downPoint.x,downPoint.y, point.x,point.y);
    distance = distance * 1 / 3;
    distance = Math.min(distance, this.maxDistance);
    this.currentDistance = distance;

    this.x = this.oldX + Math.cos(angle) * distance;
    this.y = this.oldY + Math.sin(angle) * distance;
    this.rotation = angle - Math.PI / 2;
};

Bullet.prototype.update = function(){
};

var Weapon = {
};

Weapon.SingleBullet = function (game,count) {

    Phaser.Group.call(this, game, game.world, 'SingleBullet', false, true, Phaser.Physics.ARCADE);

    this.cacheBullet = null;
    this.bulletShowPoint = {x:0,y:0};
    this.showCallback = function(){};
    this.showCallbackContext = null;
    this.bulletEmptyCallback = function(){};
    this.bulletEmptyCallbackContext = null;
    this.bulletCount = count;

    //for (var i = 0; i < count; i++)
    //{
    //    var bullet = new Bullet(game, 'bullet');
    //    this.add(bullet, true);
    //}

    var bullet = new Bullet(game, 'bullet');

    this.add(bullet, true);
    this.cacheBullet = bullet;
    this.bullet = bullet;
    this.cacheBullet.events.onKilled.add(function(){
        Utils.isBullet = false;
        this.bulletCount--;
        if (!Utils.isTween){
            this.show();
        }
    }, this);

    return this;

};

Weapon.SingleBullet.prototype = Object.create(Phaser.Group.prototype);
Weapon.SingleBullet.prototype.constructor = Weapon.SingleBullet;

Weapon.SingleBullet.prototype.fire = function () {
    if (this.checkHasBulletFly()) {return ;}

    if (this.cacheBullet == null) {return ;}
    return this.cacheBullet.fire();
};

Weapon.SingleBullet.prototype.move = function(downPoint, point){
    if (this.cacheBullet != null){
        this.cacheBullet.move(downPoint, point);
    }
};

Weapon.SingleBullet.prototype.show = function(){

    this.cacheBullet = this.getFirstExists(false);
    if (this.cacheBullet == null || this.bulletCount <= 0) {
        this.bulletCount = 0;
        this.cacheBullet = null;
        //初始化没有子弹也要调用。
        if (!Utils.isScore){
            Utils.noBullets(this.game);
        }
    } else {
        this.cacheBullet.show(this.bulletShowPoint);
        //this.cacheBullet.events.onKilled.removeAll(this);
    }
    this.showCallback.call(this.showCallbackContext,this.bulletCount);
    return this.cacheBullet;
};

Weapon.SingleBullet.prototype.checkHasBulletFly = function(){
    //for (var i = 0; i < this.length; i ++) {
    //    if (this.getChildAt(i).exists) {
    //        if (this.getChildAt(i).isFly) {
    //            return true;
    //        }
    //    }
    //}
    //return false;
    return Utils.isFire;
};

Weapon.SingleBullet.prototype.setShowPoint = function(node){
    this.bulletShowPoint.x = node.x;
    this.bulletShowPoint.y = node.y;
};
Weapon.SingleBullet.prototype.setShowCallback = function(func,context){
    this.showCallback = func;
    this.showCallbackContext = context;
};
Weapon.SingleBullet.prototype.setBulletEmptyCallback = function(func,context){
    this.bulletEmptyCallback = func;
    this.bulletEmptyCallbackContext = context;
};

var PlanePathFactory = function(game) {
    this.baseY = 200 * Utils.speedScale;
    this.templatePoint = [];
    this.width = game.width;
    this.ty = 60 * Utils.speedScale;
    this.widthCount = 9;
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

PlanePathFactory.prototype.getLevelPath = function(game, path, level){
    var newpath = {
            x:path.x.slice(0),
            y:path.y.slice(0),
            paths:path.paths.slice(0)
        },
        incrementY = level * this.ty,
        i;

    for (i = 0; i < newpath.y.length; i++){
        newpath.y[i] += incrementY;
    }

    if (My.path){
        var ix = 0;
        var x = 1 / this.width;

        for (var i = 0; i < 1; i += x){
            var px = game.math.catmullRomInterpolation(newpath.x, i);
            var py = game.math.catmullRomInterpolation(newpath.y, i);
            var node = {x:px, y:py, angle:0};
            if (ix > 0) {
                node.angle = game.math.angleBetweenPoints(newpath.paths[ix - 1], node);
            }

            newpath.paths.push(node);
            ix++;
        }
    }

    return newpath;
};

var PaperPlane = function(game, key, speed,level){
    Phaser.Sprite.call(this, game, 0, 0, key);
    this.anchor.set(0.5);
    game.physics.arcade.enableBody(this);

    this.exists = false;

    this.pi = 0;
    this.path = null;
    this.name = key;
    this.lastPoint = null;
    this.speed = speed || 1;
    this.pathCount = game.width / this.speed;
    this.level = level;

    return this;
};

PaperPlane.prototype = Object.create(Phaser.Sprite.prototype);
PaperPlane.prototype.constructor = PaperPlane;

PaperPlane.prototype.update = function(){
    if (this.path == null) return;
    var px = this.game.math.catmullRomInterpolation(this.path.x, this.pi/this.pathCount),
        py = this.game.math.catmullRomInterpolation(this.path.y, this.pi/this.pathCount),
        point = {x:px,y:py},
        angle = this.lastPoint === null ? 0:this.game.math.angleBetweenPoints(this.lastPoint, point);
    this.lastPoint = point;
    this.x = px;
    this.y = py;

    if (this.pi > this.pathCount / 2) {
        if (this.name == Utils.KEY_FLY_BIRD){
            this.animations.play('left', 10, true);
        } else {
            this.frame = 1;
            this.rotation = angle - Math.PI;
        }
    } else {
        if (this.name == Utils.KEY_FLY_BIRD){
            this.animations.play('right', 10, true);
        } else {
            this.frame = 0;
            this.rotation = angle;
        }
    }

    this.pi++;
    if (this.pi >= this.pathCount) {
        this.pi = 0;
    }
};

PaperPlane.prototype.drawPath = function(bmp){
    for (var i = 0; i < this.path.paths.length; i++) {
        bmp.rect(this.path.paths[i].x, this.path.paths[i].y, 1, 1, '#6A1B9A');
    }

    for (var i = 0; i < this.path.x.length; i++) {
        bmp.rect(this.path.x[i] - 3, this.path.y[i] - 3, 6,6, 'rgba(255, 0, 0, 1)');
    }
};

PaperPlane.prototype.show = function(path){
    this.reset(0,0);
    this.path = path;
    this.pi = this.game.rnd.integerInRange(this.pathCount /4, this.pathCount * 3 / 4);
    this.body.setSize(this.width / 2, this.height / 2, 0, 0);

    if (this.name == Utils.KEY_FLY_BIRD){
        this.animations.add('left',[0,1,2]);
        this.animations.add('right',[3,4,5]);
    }

    this.events.onKilled.add(this.killCallback, this);
};

PaperPlane.prototype.killCallback = function(){
    //销毁自己
    Utils.clearArray(this.path.paths);
    Utils.clearArray(this.path.x);
    Utils.clearArray(this.path.y);
    this.path = null;
};

var Planes = function(game, data){
    Phaser.Group.call(this, game, game.world, 'SingleBullet', false, true, Phaser.Physics.ARCADE);

    this.pathFactory = new PlanePathFactory(game);
    this.paths = new Array();

    this.addPlanes(data);
    this.initPath();

    return this;
};

Planes.prototype = Object.create(Phaser.Group.prototype);
Planes.prototype.constructor = Planes;

Planes.prototype.addPlanes = function(data,autoShow){
    var plane;
    if (data.fly_bird){
        this.birdPlane = new PaperPlane(game, Utils.KEY_FLY_BIRD, 1, 0);
        this.add(this.birdPlane, true);
    }

    if (data.plane_cash > 0) {
        for (var i = 0; i < data.plane_cash; i++){
            plane = new PaperPlane(game, Utils.KEY_PLANE_CASH,2,0);
            this.add(plane, true);
        }
    }

    if (data.plane_gift > 0) {
        for (var i = 0; i < data.plane_gift; i++){
            plane = new PaperPlane(game, Utils.KEY_PLANE_GIFT, 1.5, 1);
            this.add(plane, true);
        }
    }

    plane = new PaperPlane(game, Utils.KEY_PLANE_EMPTY, 1,1);
    this.add(plane, true);

    if (data.plane_ticket > 0) {
        for (var i = 0; i < data.plane_ticket; i++){
            plane = new PaperPlane(game, Utils.KEY_PLANE_TICKET, 1.2,2);
            this.add(plane, true);
        }
    }

    plane = new PaperPlane(game, Utils.KEY_PLANE_EMPTY,0.8,3);
    this.add(plane, true);

    if (data.plane_empty > 0) {
        for (var i = 0; i < data.plane_empty - 2; i++){
            if (i % 2 == 0){
                plane = new PaperPlane(game, Utils.KEY_PLANE_EMPTY,1,4);
            } else {
                plane = new PaperPlane(game, Utils.KEY_PLANE_EMPTY,0.8,5);
            }
            this.add(plane, true);
        }
    }

    if (autoShow) {
        this.show(true);
    }
};

Planes.prototype.initPath = function(){
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
        this.paths.push(this.pathFactory.createPath(index));
    }

    var birdIndex = {
        v: [ 0, -1, 0, -1, 0, -1, 0, -1, 0, -1,  0],
        h: [-4, -2, 1,  5, 8, 12, 8,  5, 1, -2, -4]
    };
    this.paths[-1] = this.pathFactory.createPath(birdIndex);

    //clear templatePoint
    Utils.clearArray(this.pathFactory.templatePoint);

};

Planes.prototype.show = function(showHide){
    var path,
        N = -1,
        PN = this.paths.length,
        index = -1,
        i,
        plane,
        level;

    N = this.length;
    for (i=0; i<N; i++){
        plane = this.getChildAt(i);

        if (showHide && plane.exists) {
            continue;
        }

        index = this.game.rnd.integerInRange(0,PN-1);

        if (Utils.KEY_FLY_BIRD == plane.name){
            index = -1;
        }

        level = plane.level;


        path = this.pathFactory.getLevelPath(this.game, this.paths[index], level);
        plane.show(path);
    }

};

Planes.prototype.drawPath = function(bmp){
    for (var i=0; i<this.length; i++){
        if (this.getChildAt(i).exists) {
            this.getChildAt(i).drawPath(bmp);
        }
    }
};

var SizeUtils = function(game){
    this.W = game.width;
    this.H = game.height;

    this.halfW = this.W / 2;
    this.halfH = this.H / 2;

    this.scalex = 1 / this.W;
    this.scaley = 1 / this.H;

    this.slingshotX = this.halfW;
    this.slingshotY = this.H - this.getRealY(300);

    this.halfPI = Math.PI / 2;

    this.planeCountX = this.getRealX(60);
    this.planeCountY = this.getRealY(60);

    this.planeCountHintX = this.getRealX(50+80);
    this.planeCountHintY = this.getRealY(50);

    this.personCountX = this.getRealX(60 + 200);
    this.personCountY = this.planeCountY;

    this.personCountHintX = this.getRealX(50+200+80);
    this.personCountHintY = this.planeCountHintY;

    this.bulletCountX = this.planeCountX;
    this.bulletCountY = this.H - this.getRealY(50);

    this.bulletCountHintX = this.getRealX(50+80+20);
    this.bulletCountHintY = this.bulletCountY;
    this.maxDistance = 200;

    //alert(1 / this.W);
    //alert(1 / this.H)
};

SizeUtils.prototype.constructor = SizeUtils;

SizeUtils.prototype.getRealX = function(targetPix){
    return this.scalex * targetPix * this.W;
};
SizeUtils.prototype.getRealY = function(targetPix){
    return this.scaley * targetPix * this.H;
};

/**
 * 核心游戏state
 * @constructor
 */
var PhaserGame = function (data) {
    Phaser.State.call(this);
    this.data = data;

    this.sizeutils = null;
    this.speed = 300;

    this.planes = null;//飞机

    this.weapon = null;//子弹
    this.msg = '';
    this.t;

    this.p1Text;

    this.moveMaxDistance = 200;
    this.moveDistance = 0;
    this.lastY = 0;
    this.firstY = 0;
    this.p1 = null;
    //this.bulletCount = 10;

    this.bmp = null;
    this.g = null;
    this.slingshot = null;
    //this.aliveBullet = null;
    this.cloth = null;

    this.leftPoint = null;
    this.rightPoint = null;
    this.targetPoint = {};
    this.templateTargetPoint = {};
    this.showTween = false;

    this.bulletCountHint = null;
    this.planeCountHint = null;
    this.personCountHint = null;
};

PhaserGame.prototype = Object.create(Phaser.State.prototype);
PhaserGame.prototype.constructor = PaperPlane;


PhaserGame.prototype = {

    init: function () {
        this.game.renderer.renderSession.roundPixels = true;

        this.physics.startSystem(Phaser.Physics.ARCADE);
        this.game.stage.backgroundColor = '#204090';

    },

    preload: function () {
        this.load.image('bg', 'images/flygame/bg.png');
        this.load.image('slingshot','images/flygame/slingshot.png');
        this.load.image('bullet','images/flygame/bullet.png');

        this.load.spritesheet(Utils.KEY_FLY_BIRD,'images/flygame/birdsheet.png', 138, 138, 6);

        this.load.spritesheet(Utils.KEY_PLANE_EMPTY,'images/flygame/plane_empty.png', 116, 59, 2);
        this.load.spritesheet(Utils.KEY_PLANE_CASH,'images/flygame/plane_cash.png', 116, 59, 2);
        this.load.spritesheet(Utils.KEY_PLANE_GIFT,'images/flygame/plane_gift.png', 116, 59, 2);
        this.load.spritesheet(Utils.KEY_PLANE_TICKET,'images/flygame/plane_ticket.png', 116, 59, 2);

        this.load.image('bullet_count','images/flygame/bullet_count.png');
        this.load.image('plane_count','images/flygame/plane_count.png');
        this.load.image('cloth','images/flygame/cloth.png');
        this.load.image('person','images/flygame/person.png');
    },

    init:function(){
        this.p1 = this.game.input.pointer1;
    },

    create: function () {
        //游戏背景，拉伸填充游戏界面
        var bg = this.add.image(0, 0, 'bg');
        bg.scale.x = this.game.width / bg.width;
        bg.scale.y = this.game.height / bg.height;

        this.sizeutils = new SizeUtils(this.game);

        //弹弓
        this.slingshot = this.add.image(this.sizeutils.slingshotX, this.sizeutils.slingshotY, 'slingshot');
        this.slingshot.anchor.set(0.5);

        this.leftPoint = Utils.getTargetLeftTopPoint(this.slingshot);
        this.rightPoint = Utils.getTargetRightTopPoint(this.slingshot);
        this.templateTargetPoint.x = this.targetPoint.x = this.sizeutils.slingshotX;
        this.templateTargetPoint.y = this.targetPoint.y = this.sizeutils.slingshotY - this.sizeutils.getRealY(50);
        this.templateTargetPoint.currentAngle = this.targetPoint.currentAngle = this.sizeutils.halfPI;

        this.add.image(this.sizeutils.bulletCountX, this.sizeutils.bulletCountY, 'bullet_count').anchor.set(0.5);
        //this.add.image(this.sizeutils.planeCountX, this.sizeutils.planeCountY, 'plane_count').anchor.set(0.5);

        //this.planeCountHint = this.add.text(this.sizeutils.planeCountHintX, this.sizeutils.planeCountHintY, "2", {font:'40pt verdana', fill: '#000', fontWeight:'bold'});
        this.bulletCountHint = this.add.text(this.sizeutils.bulletCountHintX, this.sizeutils.bulletCountHintY, "2", {font:'40pt verdana', fill: '#000', fontWeight:'600'});

        this.bulletCountHint.anchor.set(0.5, 0.5);
        //this.planeCountHint.anchor.set(0.5, 0.4);

        this.bmp = this.add.bitmapData(this.game.width, this.game.height);
        this.bmp.addToWorld();
        this.cloth = this.add.sprite(0, 0, 'cloth');
        this.cloth.anchor.set(0.5);
        this.g = this.add.graphics(0, 0);

        var ty = this.targetPoint.y;
        Utils.speedScale = (ty / 1400).toFixed(2);
        console.log('speedScale = ' + Utils.speedScale);
        console.log('speedScale = ' + ty);

        this.planes = new Planes(this.game, this.data);
        this.planes.show();

        //test
        if (My.path){
            this.planes.drawPath(this.bmp);
        }

        this.weapon = new Weapon.SingleBullet(this.game, this.data.bullet_count);
        this.weapon.setShowPoint(this.targetPoint);
        this.weapon.setShowCallback(this.showCallback, this);
        //this.weapon.setBulletEmptyCallback(this.bulletEmptyCallback, this);


        this.weapon.show();

        this.input.onDown.add(function(){
        },this);

        this.input.onUp.add(function(){
            if (this.weapon.checkHasBulletFly()) return;
            if (this.p1.position.y < this.sizeutils.halfH) return;
            if (this.p1.positionDown.y < this.sizeutils.halfH) return;

            if (this.weapon.fire(this.p1.positionDown, this.p1.position)) {
                var tween = this.add.tween(this.targetPoint);
                this.showTween = true;
                Utils.isTween = true;
                this.preTween(tween);
            }
        },this);

        this.game.time.advancedTiming = true;


        this.t = this.game.add.text(316, 66, '', {font:'26pt Helvetica', fill: '#000'});
    },

    update: function () {
        if (Utils.isFire){
            console.log('check overlap');
            this.game.physics.arcade.overlap(this.planes, this.weapon, this.overlapCallback, null, this);
        }

    },

    render:function(){
        this.bulletCountHint.text = '' + this.weapon.bulletCount;

        if (this.p1.active && this.p1.y > this.sizeutils.halfH && this.p1.positionDown.y > this.sizeutils.halfH) {
            if (!this.weapon.checkHasBulletFly()) {
                this.weapon.move(this.p1.positionDown, this.p1.position);

                if (this.weapon.cacheBullet != null){
                    this.targetPoint.x = this.weapon.cacheBullet.x;
                    this.targetPoint.y = this.weapon.cacheBullet.y;
                    this.targetPoint.currentAngle = this.weapon.cacheBullet.currentAngle;
                    this.targetPoint.distance = this.weapon.cacheBullet.currentDistance;
                    this.drawRubberBand();
                }
            }
        }

        if (this.showTween){
            this.drawRubberBand();
        }

        if(Utils.isDebug){
            this.t.text = 'fps:' + this.game.time.fps;
        }

        if (My.debug){
            for (var i=0;i<this.planes.length;i++){
                this.game.debug.body(this.planes.getChildAt(i));
            }
        }

    },

    drawRubberBand:function(){
        var g = this.g,
            target = this.targetPoint,
            left = this.leftPoint,
            right = this.rightPoint,
            ro = target.currentAngle - Math.PI / 2;

        g.clear();

        g.beginFill(0x6A1B9A);

        var h = this.weapon.bullet.height / 2 + this.cloth.height/2 - 5;
        var x = target.x + h * Math.cos(target.currentAngle);
        var y = target.y + h * Math.sin(target.currentAngle);

        g.endFill();

        this.game.context.lineCap = "round";
        this.game.context.lineJoin = "round";
        g.lineStyle(10, 0x93690A);
        g.beginFill(0x93690A);

        h = this.cloth.width / 2 - 8;

        g.moveTo(left.x + 14, left.y + 33);
        g.lineTo(x - h * Math.cos(ro), y - h * Math.sin(ro));

        g.moveTo(right.x - 13, right.y + 33);
        g.lineTo(x + h * Math.cos(ro), y + h * Math.sin(ro));

        g.endFill();

        this.cloth.rotation = ro;

        this.cloth.x = x;
        this.cloth.y = y;

    },

    overlapCallback: function(plane,bullet){
        var name = plane.name;

        plane.kill();
        bullet.kill();

        if (name == Utils.KEY_PLANE_EMPTY){
            //hitEmpty();
            Utils.hitEmpty();
        } else {
            Utils.isScore = true;
            Utils.getScroe(this.game,name, this.weapon.bulletCount);
            //getScroe(name, this.weapon.bulletCount);
        }

        this.planes.show(true);
    },

    showCallback:function(bulletCount){
        this.drawRubberBand();
        //recordBullets(bulletCount);
    },
    bulletEmptyCallback:function(){
        Utils.noBullets(this.game);
        //noBullets();
    },

    preTween:function(tween){
        var distance = this.weapon.cacheBullet.currentDistance,
            maxDis = this.weapon.cacheBullet.maxDistance,
            during = (distance / maxDis) * 2000 + 2000,
            slingMiddle = {x:this.sizeutils.halfW, y: Utils.getTargetLeftTopPoint(this.slingshot).y + 20},
            leftTop = {x:0,y:0},
            rightBottomHalf = {x:0,y:0},
            leftTopHalf = {x:0,y:0};

        leftTop.x = slingMiddle.x - (this.targetPoint.x - slingMiddle.x);
        leftTop.y = slingMiddle.y - (this.targetPoint.y - slingMiddle.y);
        rightBottomHalf.x = slingMiddle.x + (this.targetPoint.x - slingMiddle.x) / 2;
        rightBottomHalf.y = slingMiddle.y + (this.targetPoint.y - slingMiddle.y) / 2;
        leftTopHalf.x = slingMiddle.x - (this.targetPoint.x - slingMiddle.x) / 2;
        leftTopHalf.y = slingMiddle.y - (this.targetPoint.y - slingMiddle.y) / 2;

        tween.to({x:[leftTop.x,rightBottomHalf.x,leftTopHalf.x,slingMiddle.x, this.templateTargetPoint.x],y:[leftTop.y,rightBottomHalf.y,leftTopHalf.y,slingMiddle.y,this.templateTargetPoint.y]}, during,null,true);
        tween.onComplete.add(function(){
            this.targetPoint.x = this.templateTargetPoint.x;
            this.targetPoint.y = this.templateTargetPoint.y;
            this.targetPoint.currentAngle = this.templateTargetPoint.currentAngle;
            this.showTween = false;
            Utils.isTween = false;
            if (!Utils.isBullet){
                this.weapon.show();
            }
            this.drawRubberBand();
        },this);
    }

};

var game,
    coreState,
    data;

function startGame() {
    data = Utils.getData();
    game = new Phaser.Game('100%', '100%', 1, 'game_div');
    coreState = new PhaserGame(data);
    game.state.add('Game', coreState, true);
}
function resumeGame(){
    Utils.isScore = false;
    if (coreState.weapon.bulletCount > 0) {
        //game.paused = false;
    } else {
        //noBullets();
        Utils.noBullets();
    }
}

if (Utils.isDebug){
    startGame();
}
