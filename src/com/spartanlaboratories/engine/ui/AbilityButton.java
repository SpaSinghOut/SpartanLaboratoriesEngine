package com.spartanlaboratories.engine.ui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.lwjgl.input.Mouse;

import com.spartanlaboratories.engine.game.Ability;
import com.spartanlaboratories.engine.game.Castable;
import com.spartanlaboratories.engine.game.Hero;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Human;
import com.spartanlaboratories.engine.structure.Human.LeftClickState;

public class AbilityButton extends StateChangingButton implements MouseListener{
	public Ability correspondingAbility;
	private static int numButtons = 0;
	final double abilityButtonWidth = engine.getScreenDimensions().x * .0341;
	final double abilityButtonHeight = engine.getScreenDimensions().y * .057;
	static long leveledUseProtection;
	public AbilityButton(Ability setCorrespondingAbility, Gui setOwner){
		super(setOwner);
		owner.abilityButtons.add(this);
		correspondingAbility = setCorrespondingAbility;
		setLocation((int) (engine.getScreenDimensions().x * .4 + abilityButtonWidth * 1.5 * numButtons++), (int) (engine.getScreenDimensions().y * .837));
		setSize((int)(abilityButtonWidth), (int) abilityButtonHeight);
		setBackground(engine.util.getAsJavaColor(correspondingAbility.abilityStats.color));
		setOpaque(true);
		setVisible(true);
		addMouseListener(this);
	}
	
	public void setOwner(Gui gui){
		owner = gui;
	}
	public void setBorder(boolean b){
		Rectangle bound = getBounds();
		int offset = 3;
		if(b)getBorder().paintBorder(this, getGraphics(), 0, 0, bound.width, bound.height);
		else offset = 0;
		getBorder().paintBorder(this, getGraphics(), offset, offset, bound.width - offset * 2, bound.height - offset * 2);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(engine.tickCount > leveledUseProtection + 0.5 * engine.tickRate)
		switch(state){
		case USE:
			if(correspondingAbility.abilityStats.castType == Castable.CastType.POINTTARGET
			|| correspondingAbility.abilityStats.castType == Castable.CastType.ALIVETARGET
			|| correspondingAbility.abilityStats.castType == Castable.CastType.CHANNELING){
				((Human)owner.owner).selectedAbility = correspondingAbility;
				((Human)owner.owner).leftClickState = LeftClickState.ABILITYUSE;
				owner.out("Player's left click state is set to ability use");
			}
			//else if(correspondingAbility.castType == Ability.CastType.TOGGLE)
			else if(correspondingAbility.abilityStats.castType == Castable.CastType.INSTANT || 
					correspondingAbility.abilityStats.castType == Castable.CastType.TOGGLE)
				((Hero)owner.owner.selectedUnit).castSpell(correspondingAbility);
			break;
		case LEVEL:
			//prevents leveling the ability if it is maxed
			if(correspondingAbility.level == correspondingAbility.abilityStats.levelRequirements.length)
				break;
			//prevents leveling the ability if hero level requirements are not met
			else if(((Hero)owner.owner.selectedUnit).getStat(Constants.level) >= 
			correspondingAbility.abilityStats.levelRequirements[correspondingAbility.level] ){
				correspondingAbility.levelAbility();
				leveledUseProtection = engine.tickCount;
				mouseExited(new MouseEvent(this, 0,System.nanoTime(),0,Mouse.getX(),Mouse.getY(),0, true));
				if(arg0.getX() != 0)mouseEntered(new MouseEvent(this, 1,System.nanoTime(),0,Mouse.getX(), Mouse.getY(),0, true));
			}
			break;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		owner.showDecription(true, correspondingAbility.abilityStats.name + " level: " + correspondingAbility.level);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		owner.showDecription(false, "");
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
}
