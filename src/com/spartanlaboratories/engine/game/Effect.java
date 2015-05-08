package com.spartanlaboratories.engine.game;

public class Effect extends VisibleObject {
	int timeLeft;
	GameObject owner;
	public Effect(double setWidth, double setHeight, boolean setSolid, int setTimeLeft, GameObject setOwner) {
		super(setOwner.engine);
		timeLeft = (int) (setTimeLeft * engine.tickRate);
		owner = setOwner;
		//needToMove = true;
		//changeBaseSpeed(210);
	}
	public boolean tick(){
		if(timeLeft-- > 0){
			active = false;
			engine.addToDeleteList(this);
		}
		return super.tick();
	}
}
