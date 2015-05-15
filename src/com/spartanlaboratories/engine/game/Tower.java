package com.spartanlaboratories.engine.game;

import java.io.IOException;

import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Location;
import com.spartanlaboratories.engine.structure.Util;

public class Tower extends Alive {
	private static final int towerHP = 1600;
	private static final int towerSize = 85;
	public Tower(Engine engine, Faction setFaction) {
		super(engine, setFaction);
		setWidth(towerSize);
		setHeight(towerSize);
		changePermissions(Constants.movementAllowed, false);
		if(setFaction == Alive.Faction.RADIANT){
			setLocation(100,1000);
			color = Util.Color.WHITE;
			changeStat(Constants.maxHealth, towerHP);
			changeStat(Constants.health, towerHP);
		}
		else if(setFaction == Alive.Faction.DIRE){
			setLocation(new Location(engine.getWrap().x - 100, 1000));
			color = Util.Color.WHITE;
			changeStat(Constants.maxHealth, towerHP);
			changeStat(Constants.health, towerHP);
		}
		changeStat(Constants.visibilityRange, 700);
		changeStat(Constants.attackRange, 700);
		immobile = true;
		changeStat(Constants.startingDamage, 70);
		changeStat(Constants.baseAnimationTime, 1);
		changeStat(Constants.baseAttackTime, 1);
		changeStat(Constants.attackSpeed, 100);
		missile = true;
		attackMissileType = new MissileStats("auto");
		changePermissions(Constants.autoAttackAllowed, true);
		//Don't remember if this actually does anything
		noRetraction = true;
	}
	public boolean tick(){
		if(!super.tick())return false;
		if(attackTarget != null)
			if(!attackTarget.active || 
			engine.util.getRealCentralDistance(this, attackTarget) > getStat(Constants.visibilityRange))
				attackTarget = null;
		if(attackTarget == null || !attackTarget.active || !attackTarget.alive)findAttackTarget();
		return active;
	}
	private void findAttackTarget(){
		Alive potentialAttackTarget = null;
		for(Alive a: Alive.allAlives){
			if(a.faction != this.faction && getStat(Constants.visibilityRange) > engine.util.getRealCentralDistance(a, this)){
				if(potentialAttackTarget == null)potentialAttackTarget = a;
				else{
					if(engine.util.getRealCentralDistance(potentialAttackTarget, this)>
					engine.util.getRealCentralDistance(a, this))
						potentialAttackTarget = a;
				}
			}
		}
		aggroOn(potentialAttackTarget);
	}
	
}
