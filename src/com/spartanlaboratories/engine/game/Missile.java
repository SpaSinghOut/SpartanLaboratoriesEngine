package com.spartanlaboratories.engine.game;

import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.util.Location;

public class Missile extends Actor{
	double damage;
	public Alive parent;
	MissileStats missileType;
	boolean auto;
	boolean penetrating;
	/**
	 * The Constructor that is to be used if specification of a custom target location is needed
	 * @param setSpellName - Represents the generic stats of the missile that is being created
	 * @param setParent	- The Alive that created this missile
	 * @param passX	- The x coordinate of this missile's target location
	 * @param passY - The y coordinate of this missile's target location
	 */
	public Missile(MissileStats setSpellName,  Alive setParent, double passX, double passY){
		super(setParent.engine);
		genericMissileInit(setSpellName, setParent);
		setLocation(new Location(setParent.getLocation().x, setParent.getLocation().y));
		goTo(new Location(passX, passY));
		penetrating = missileType.penetrating;
	}
	//the default constructor that will create a non-homing missile at the target location with the given target
	public Missile(MissileStats setSpellName,  Alive setParent, Location startingLocation, Location setTarget){
		super(setParent.engine);
		genericMissileInit(setSpellName, setParent);
		setLocation(startingLocation);
		goTo(setTarget);
		setMovement(target);
	}
	/**
	 * The best constructor to use when creating a homing missile
	 * 
	 * @param setMissileType - The type of missile that is going to be created.
	 * @param setParent - The parent of the missile
	 * @param setHomingTarget - The homing target of the missile
	 */
	public Missile(MissileStats setMissileType,  Alive setParent, Alive setHomingTarget){
		super(setParent.engine);
		genericMissileInit(setMissileType, setParent);
		setLocation(new Location(setParent.getLocation().x, setParent.getLocation().y));
		homingTarget = setHomingTarget;
		goTo(homingTarget.getLocation());
	}
	/**
	 * Creates an "auto-attack" missile
	 * @param setParent - the creator of this missile
	 * @param setHomingTarget - the alive towards which this missile will be flying
	 */
	public Missile(Alive setParent, Alive setHomingTarget){
		super(setParent.engine);
		auto = true;
		genericMissileInit(new MissileStats("auto"), setParent);
		setLocation(setParent.getLocation());
		homingTarget = setHomingTarget;
		goTo(homingTarget.getLocation());
	}
	private void genericMissileInit(MissileStats mt, Alive setParent){
		missileType = mt;
		setWidth(missileType.width);
		setHeight(missileType.height);
		parent = setParent;
		changeBaseSpeed(missileType.speed);
		engine.missiles.add(this);
		setColor(missileType.color);
		needToMove = true;
		childSetsOwnMovement = true;
		shape = Actor.Shape.QUAD;
		damage = !auto ? missileType.damage : setParent.getStat(Constants.damage);
		movementType = missileType.homing ? Actor.MovementType.HOMING : Actor.MovementType.DIRECTIONBASED;
	}
	public boolean tick(){
		active = engine.map.withinBorders(this);
		if(!missileType.homing)
			for(Alive a: Alive.allAlives){
				if(engine.util.checkForCollision(a, this) && a.faction != parent.faction && a.active)
					onCollision(a);
			}
		else if(missileType.homing){
			setTarget(homingTarget.getLocation());
			setMovement(homingTarget.getLocation());
			if(engine.util.checkForCollision(this, homingTarget))
				onCollision(homingTarget);
		}
		if(engine.util.missileDeath(this) && !missileType.homing && !penetrating){
			return false;
		}
		return super.tick();
	}
	public void setOwner(Alive newOwner){
		parent = newOwner;
	}
	private void onCollision(Alive gotHit){
		if(auto)gotHit.getAttacked(parent);
		else {
			//missileType.onCollision(this, gotHit);
			parent.dealDamage(gotHit, damage, Alive.DamageType.MAGICAL);
		}
		if(!penetrating)active = false;
		
	}
	public void setAuto(boolean setAuto){
		auto = setAuto;
	}
	public void setDamage(double d){
		damage = d;
	}
}
