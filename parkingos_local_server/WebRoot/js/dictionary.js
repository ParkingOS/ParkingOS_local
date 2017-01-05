
function Dictionary()
{
    this._obj = {};
    this.count = 0;
}
Dictionary.prototype.add = function(key,value)
{
    if(typeof(this._obj[key]) != "undefined")
    {
        throw new Error("keyWork [" + key + "] is exists!");
    }
    this._obj[key] = value
    this.count += 1;
};
Dictionary.prototype.remove = function(key)
{
    if(typeof(this._obj[key]) == "undefined")
    {
        this._obj[key]=null;
    }
    delete this._obj[key];
    this.count -= 1;
};
Dictionary.prototype.removeAll = function()
{
    this._obj = {};
    this.count = 0;
};
Dictionary.prototype.exists = function(key)
{
    if(typeof(this._obj[key]) == "undefined")
    {
        return false;
    }
    return true;
};
Dictionary.prototype.item = function(key)
{
    if(typeof(this._obj[key]) == "undefined")
    {
        this._obj[key]=null;
    }
    return this._obj[key];
};
Dictionary.prototype.keys = function()
{
    var keys = [];
    for(var p in this._obj)
    {
        keys[keys.length] = p;
    }
    return keys;
};
Dictionary.prototype.values = function()
{
    var values = [];
    for(var p in this._obj)
    {
        values[values.length] = this._obj[p];
    }
    return values;
};