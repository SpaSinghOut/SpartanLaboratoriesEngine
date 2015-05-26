package com.spartanlaboratories.engine.game;

public interface Castable {
	enum CastType{
		POINTTARGET, ALIVETARGET, INSTANT, PASSIVE, CHANNELING, TOGGLE,;
		boolean isTimeBased(){
			if(this == POINTTARGET || this == ALIVETARGET ||
				this == INSTANT || this == CHANNELING)return true;
			else return false;
		}
	}
	public void activate();
}
