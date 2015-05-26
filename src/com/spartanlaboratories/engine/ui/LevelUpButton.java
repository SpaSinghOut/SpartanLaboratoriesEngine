package com.spartanlaboratories.engine.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.structure.Constants;

public class LevelUpButton extends StateChangingButton implements MouseListener{

	protected LevelUpButton(Gui setOwner) {
		super(setOwner);
		setLocation((int)(owner.screenX * .275),(int)(owner.screenY * .69));
		setSize((int) (owner.screenX * 0.06), (int) (owner.screenY * 0.03));
		setVisible(true);setOpaque(true);
		setBackground(Color.YELLOW);setForeground(Color.BLACK);
		addMouseListener(this);
		owner.getLayeredPane().add(this);
	}
	public void update(){
		Alive a = (Alive)owner.owner.selectedUnit;
		if(state == State.IDLE && a.getStat(Constants.abilityPoints) > 0)setState(State.USE);
		String string = " Level up: " + a.getStat(Constants.abilityPoints) + "!";
		setText(state == State.LEVEL?"choose ability" : string);
		if(a.getStat(Constants.abilityPoints) > 0)setVisible(true);
		else {
			setVisible(false);
			setState(StateChangingButton.State.IDLE);
		}
	}
	public void setState(State setState){
		Alive a = (Alive)owner.owner.selectedUnit;
		if(setState == State.LEVEL && !(a.getStat(Constants.abilityPoints) > 0))return;
		super.setState(setState);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		setState(state == State.LEVEL? State.USE:State.LEVEL);
		if(state == State.LEVEL)for(AbilityButton ab: owner.abilityButtons)ab.setState(State.LEVEL);
		else for(AbilityButton ab: owner.abilityButtons)ab.setState(State.USE);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
