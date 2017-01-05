package com.zld.bean;

public class CarType {
private String id;
private String name;
public String getCarTypeID() {
	return id;
}
public void setCarTypeID(String carTypeID) {
	this.id = carTypeID;
}
public String getCarTypeName() {
	return name;
}
public void setCarTypeName(String carTypeName) {
	this.name = carTypeName;
}
@Override
public String toString() {
	return "CatType [carTypeID=" + id + ", carTypeName=" + name
			+ "]";
}

}
