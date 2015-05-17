package com.spartanlaboratories.engine.util;

import com.spartanlaboratories.engine.structure.Location;

public class Rectangle {
	public Location northWest, northEast, southWest, southEast;
	public Location size;
	public Line top, right, bottom, left;
	double xMin, xMax, yMin, yMax;
	public Rectangle(Location topLeftCorner, Location size){
		northWest.duplicate(topLeftCorner);
		this.size.duplicate(size);
		northEast = new Location(topLeftCorner.x + size.x, topLeftCorner.y);
		southWest = new Location(topLeftCorner.x, topLeftCorner.y + size.y);
		southEast = new Location(topLeftCorner.x + size.x, topLeftCorner.y + size.y);
		top = new Line(northWest, northEast);
		right = new Line(northEast, southEast);
		bottom = new Line(southWest, southEast);
		left = new Line(northWest, southWest);
		xMin = southWest.x;
		xMax = northEast.x;
		yMin = northWest.y;
		yMax = southEast.y;
	}
}
