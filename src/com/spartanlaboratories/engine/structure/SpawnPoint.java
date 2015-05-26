package com.spartanlaboratories.engine.structure;

import com.spartanlaboratories.engine.game.GameObject;
import com.spartanlaboratories.engine.util.Location;

public class SpawnPoint extends Location{
	private GameObject spawns;	
	public SpawnPoint(Location location, GameObject gameObject){
		super(location);
		spawns = gameObject;
		spawns.active = false;
	}
	public void spawn(){
		GameObject go = spawns.copy();
		go.setLocation(new Location(this));
		go.active = true;
	}
}
