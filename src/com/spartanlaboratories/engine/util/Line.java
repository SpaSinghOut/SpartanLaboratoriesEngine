package com.spartanlaboratories.engine.util;

import javax.lang.model.util.Elements;


public class Line extends Measurement<Line>{
	double slope, yInt;
	public Line(Location p, Location q){
		slope = ( q.y - p.y ) / ( q.x - p.x );
		yInt = q.y - slope * q.x;
	}
	public Line( double slope, double yIntercept){
		this.slope = slope;
		yInt = yIntercept;
	}
	public Line( Location p, double slope){
		this.slope = slope;
		yInt = p.y - slope * p.x;
	}
	@Override
	public String toString() {
		return String.format("y = %.2fx " + (yInt>0?"+":"-") + " %.2f", slope, Math.abs(yInt));
	}
	@Override
	public void duplicate(Line element) {
		slope = element.slope;
		yInt = element.yInt;
	}
	@Override
	public Line copy() {
		return new Line(slope, yInt);
	}
	
	public void clear() {
		slope = 0;
		yInt = 0;
	}
	@Override
	public void copyTo(Line element) {
		element = copy();
	}
	@Override
	public boolean equals(Line element) {
		return slope == element.slope && yInt == element.yInt;
	}
}
