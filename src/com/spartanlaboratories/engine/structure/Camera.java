package com.spartanlaboratories.engine.structure;

import com.spartanlaboratories.engine.util.Location;

public interface Camera {
	public default Quad generateQuad(){
		return new Quad(new Location[4], new Location[4]);
	}
}
