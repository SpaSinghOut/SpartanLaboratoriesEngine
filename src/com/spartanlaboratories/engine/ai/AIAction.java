package com.spartanlaboratories.engine.ai;

import java.util.ArrayList;

import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Creep;
import com.spartanlaboratories.engine.game.Hero;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Location;

public class AIAction {
	AI owner;
	ActionName actionName;
	int[] parameters;
	boolean actionStarted;
	public AIAction(AI setOwner, ActionName setActionName, int[] setParameters){
		owner = setOwner;
		actionName = setActionName;
		parameters = setParameters;
		actionStarted = false;
	}
	enum ActionName{
		MOVETO, MOVEBY, FINDCREEPS,WATCH, LASTHIT,;
	}
	public void tick(){
		if(!actionStarted)start();
		if(!isComplete()){
			doAction();
		}
		else owner.actionQueue.completeAction(this);
	}
	private void start(){
		actionStarted = true;
		switch(actionName){
		case MOVETO:
			owner.getHero().setTarget(new Location(parameters[0], parameters[1]));
			break;
		case MOVEBY:
			parameters[0] += owner.getHero().getLocation().x;parameters[1] += owner.getHero().getLocation().y;
			owner.getHero().setTarget(new Location(parameters[0], parameters[1]));
			break;
		case FINDCREEPS:
			if(isComplete())return;
			int[] location = { 2500, 1000 };
			owner.actionQueue.insert(owner.actionQueue.indexOf(this), new AIAction(owner, ActionName.MOVETO, location));
			owner.engine.out("starting to look for creeps");
			break;
		case WATCH:
			owner.engine.out("starting to watch");break;
		case LASTHIT:
			owner.getHero().aggroOn(Creep.allCreeps.get(parameters[0]));
			break;
		}
	}
	private void doAction(){
		switch(actionName){
		case MOVETO:
			break;
		case FINDCREEPS:
			int[] location = {-100,0};
			owner.actionQueue.insert(owner.actionQueue.indexOf(this), new AIAction(owner,ActionName.MOVEBY, location));
			break;
		case WATCH:
			ArrayList<Alive> alives = owner.engine.qt.getAlivesAroundMe(owner.getHero(), (int)owner.getHero().getStat(Constants.visibilityRange));
			int creepCount = 0;
			for(Alive c: owner.engine.qt.getAlivesAroundMe(owner.getHero(), (int)owner.getHero().getStat(Constants.visibilityRange))){
				if(c.getClass() == Hero.class && c.faction != owner.getHero().faction){
					owner.selectedUnit = c;
					owner.getHero().castSpell(owner.getHero().abilities.get(1));
					owner.getHero().aggroOn(c);
					return;
				}
				else if(c.getClass() == Creep.class)
					if(c.faction != owner.getHero().faction){//there is no need to check for visibility here because using the quadtree ensured visibility
						creepCount++;
						if(c.getRatio("health") < .5){
							int[] indexOfCreep = {Creep.allCreeps.indexOf(c)};
							owner.actionQueue.insert(owner.actionQueue.indexOf(this), new AIAction(owner, ActionName.LASTHIT, indexOfCreep));
							this.actionStarted = false;
							//Head.out(actionName + " is delayed");
							return;
						}
					}
				if(creepCount == 0){
					//Head.out("inserting a find creeps action");
					owner.actionQueue.insert(owner.actionQueue.indexOf(this), new AIAction(owner, ActionName.FINDCREEPS, new int[0]));
				}
			}
			break;
			default:break;
		}
	}
	private boolean isComplete(){
		boolean completion = false;
		switch(actionName){
		case MOVETO:
			if(owner.getHero().getLocation().x == parameters[0] && owner.getHero().getLocation().y == parameters[1]){
				completion = true;
			}
			break;
		case MOVEBY:
			if(owner.getHero().getLocation().x == parameters[0] && owner.getHero().getLocation().y == parameters[1]){
				completion = true;
			}
			break;
		case FINDCREEPS:
			int creepCount = 0;
			ArrayList<Alive> alives = owner.engine.qt.getAlivesAroundMe(owner.getHero(), (int)owner.getHero().getStat(Constants.visibilityRange));
			for(Alive a: alives)if(a.active && a.alive && a.getClass() == Creep.class && a.faction != owner.getHero().faction)creepCount++;
			if(creepCount >= 1)completion = true;
			break;
		case WATCH:
			return false;
		case LASTHIT:
			/*there has to be a distance check here even though alive should deaggro if the attackTarget is not visible
			 * because despite the deaggro the last hit action will not consider itself finished unless it sees for itself 
			 * that the attack target is too far away
			 */
			Alive attackTarget = owner.getHero().attackTarget;
			if(attackTarget == null || !attackTarget.alive || 
			owner.engine.util.getRealCentralDistance(owner.getHero(), owner.getHero().attackTarget) > owner.getHero().getStat(Constants.visibilityRange)){
				owner.getHero().aggroOn(null);
				completion = true;
			}
		default:break;
		}
		//Head.out(actionName + " completion is: " + completion);
		return completion;
	}
}
