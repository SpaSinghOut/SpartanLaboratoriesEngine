package com.spartanlaboratories.engine.ui;

import javax.swing.JButton;

import com.spartanlaboratories.engine.structure.Engine;

public class StateChangingButton extends JButton{

	protected State state;
	protected Gui owner;
	Engine engine;
	
	protected StateChangingButton(Gui setOwner){
		owner = setOwner;
		owner.addComponent("Hero", this);
		state = State.IDLE;
		engine = owner.owner.engine;
	}
	
	public enum State{
		USE, LEVEL, IDLE;
	}

	public void setState(State setState){
		state = setState;
	}
}
