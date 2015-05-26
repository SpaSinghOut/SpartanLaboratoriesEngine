package com.spartanlaboratories.engine.game;

import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.spartanlaboratories.engine.structure.Constants;

public abstract class Item implements Castable{
	double[] stats;
	public int cost;
	public String itemName;
	public Texture texture;
	public Item(double[] setStats){
		stats = setStats;
	}
	public Item(){
		stats = new double[Constants.statsSize];
		defineStats();
		try {
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/"+itemName+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	protected abstract void defineStats();
	public abstract void activate();
}
