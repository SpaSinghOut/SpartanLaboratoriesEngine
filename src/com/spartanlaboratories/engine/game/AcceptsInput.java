package com.spartanlaboratories.engine.game;

import com.spartanlaboratories.engine.structure.Camera;
import com.spartanlaboratories.engine.structure.Location;

public interface AcceptsInput {
	public void rightClick(Location locationOnScreen, Camera camera);
	public void leftClick(Location locationOnScreen, Camera camera);
	public void keyPress(String key);
}
