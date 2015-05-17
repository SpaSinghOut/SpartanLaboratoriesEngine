package com.spartanlaboratories.engine.util;

import com.spartanlaboratories.engine.structure.Location;

public class Line {
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
}
