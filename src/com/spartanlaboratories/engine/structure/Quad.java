package com.spartanlaboratories.engine.structure;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import com.spartanlaboratories.engine.util.Location;

public final class Quad {
	public Texture texture;
	public Util.Color color;
	public Location[] quadValues = new Location[4], textureValues = new Location[4];
	public Quad(Location[] quadValues, Location[] textureValues){
		this.quadValues = quadValues;
		this.textureValues = textureValues;
	}
}
