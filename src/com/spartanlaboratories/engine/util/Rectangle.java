package com.spartanlaboratories.engine.util;

public class Rectangle extends Measurement<Rectangle>{
	public Location northWest, northEast, southWest, southEast;
	private Location size;
	public Line top, right, bottom, left;
	private double xMin, xMax, yMin, yMax;
	private double xRadius, yRadius;
	private Location center;
	public Rectangle(Location topLeftCorner, Location size){
		this(new Location(topLeftCorner.x + size.x / 2, topLeftCorner.y - size.y / 2), size.x, size.y);
	}
	public Rectangle(Location center, double width, double height){
		this.center = center;
		northWest = new Location();
		northEast = new Location();
		southWest = new Location();
		southEast = new Location();
		top = new Line(0,0);
		right = new Line(0,0);
		bottom = new Line(0,0);
		left = new Line(0,0);
		setSize(new Location(width,height));
	}
	public Rectangle(double xMin, double xMax, double yMin, double yMax){
		this(new Location(xMin, xMax), new Location(Math.abs(xMax - xMin),Math.abs(yMax - yMin)));
	}
	public String toString(){
		return "(" + center.x + "," + center.y + ") (" + size.x + "," + size.y + ")";
	}
	public void duplicate(Rectangle rectangle){
		center.duplicate(rectangle.getCenter());
		setSize(rectangle.getSize());
	}
	public Rectangle copy() {
		return new Rectangle(northWest, size);
	}
	public void clear(){
		setSize(new Location(0,0));
	}
	@Override
	public void copyTo(Rectangle element) {
		element = copy();
	}
	public Location getSize(){
		return size.copy();
	}
	public void setSize(Location location){
		size = new Location(location);
		xRadius = size.x / 2;
		yRadius = size.y / 2;
		xMin = center.x - xRadius;
		xMax = center.x + xRadius;
		yMin = center.y - yRadius;
		yMax = center.y + yRadius;
		northWest.setCoords(xMin, yMax);
		northEast.setCoords(xMax, yMax);
		southWest.setCoords(xMin, yMin);
		southEast.setCoords(xMax, yMin);
		top = new Line(northWest, northEast);
		bottom = new Line(southWest, southEast);
		right = new Line(northEast, southEast);
		left = new Line(northWest, southWest);
	}
	public Location getCenter(){
		return center.copy();
	}
	public void setCenter(Location newCenter){
		center = new Location(newCenter);
		setSize(size);
	}
	public double getXMin(){
		return xMin;
	}
	public double getXMax(){
		return xMax;
	}
	public double getYMin(){
		return yMin;
	}
	public double getYMax(){
		return yMax;
	}
	public void setXMin(double newXMin){
		center.setCoords((xMax + newXMin)/2, center.y);
		setSize(new Location(Math.abs(xMax - newXMin), size.y));
	}
	public void setXMax(double newXMax){
		center.setCoords((newXMax + xMin)/2, center.y);
		setSize(new Location(Math.abs(xMax - newXMax), size.y));
	}
	public void setYMin(double newYMin){
		center.setCoords(center.x, (newYMin+yMax) / 2);
		setSize(new Location(size.x,Math.abs(yMax - newYMin)));
	}
	public void setYMax(double newYMax){
		center.setCoords(center.x, (newYMax+yMin) / 2);
		setSize(new Location(size.x,Math.abs(newYMax - yMin)));
	}
	@Override
	public boolean equals(Rectangle element) {
		return center.equals(element.getCenter())&&size.equals(element.getSize());
	}
	public static void main(String[] args){
		System.out.println("Performing unit test for the class: Rectangle");
		Location center = new Location();
		Rectangle r = new Rectangle(center, 10, 10);
		System.out.println("Created a rectangle using the center + size constructor.");
		System.out.printf("Set the center to %s and the size to 10 by 10\n", center);
		System.out.println("********** Testing Rectangle Values **********");
		r.showValues();
	}
	public void showValues(){
		System.out.printf("The rectangle center is: %s\n", getCenter());
		System.out.printf("The rectangle size is: %s\n", getSize());
		System.out.println("          Corner points");
		System.out.println("NorthWest: " + northWest);
		System.out.println("NorthEast: " + northEast);
		System.out.println("SouthWest: " + southWest);
		System.out.println("SouthEast: " + southEast);
		System.out.println("          Edge values");
		System.out.println("Minimum x: " + getXMin());
		System.out.println("Maximum x: " + getXMax());
		System.out.println("Minimum y: " + getYMin());
		System.out.println("Maximum y: " + getYMax());
	}
}