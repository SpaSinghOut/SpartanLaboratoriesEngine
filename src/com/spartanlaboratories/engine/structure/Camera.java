package com.spartanlaboratories.engine.structure;

import java.util.ArrayList;

import com.spartanlaboratories.engine.game.Actor;
import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.util.Location;

public interface Camera {
	public final ArrayList<Quad> quads = new ArrayList<Quad>();
	public void generateQuad(VisibleObject visibleObject);
	public default void clearQuads(){
		quads.clear();
	}
	public Actor unitAt(Location monitorLocation);
	public void handleClick(int mouseButton);
	public void mouseAt(Location monitorLocation);
	public Location getWorldLocation(Location locationOnScreen);
	public ArrayList<VisibleObject> getQualifiedObjects();
}
