package com.spartanlaboratories.engine.game;

import com.spartanlaboratories.engine.structure.Camera;
import com.spartanlaboratories.engine.structure.StandardCamera;
import com.spartanlaboratories.engine.util.Location;

public interface AcceptsInput {
	public void leftClick(Location locationOnScreen, StandardCamera camera);
	public void keyPress(String key);
	public void rightClick(Location locationOnScreen, Camera camera);
}
