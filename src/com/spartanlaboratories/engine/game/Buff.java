package com.spartanlaboratories.engine.game;

import java.util.ArrayList;

import com.spartanlaboratories.engine.structure.StandardCamera;
import com.spartanlaboratories.engine.structure.Human;
import com.spartanlaboratories.engine.ui.BuffIcon;

/**
 * An object that is to be contained inside an <a href="Alive.html">Alive's</a> <a href="Alive.buffs.html">buffs</a> list, it handles all of the 
 * temporary and permanent stat changes and special effects of an Alive object.
 * @author Spartak
 *
 */
public abstract class Buff extends GameObject{
	boolean limitedByTime;
	int timeRemaining;
	public String buffName;
	public Alive owner;
	double intensity;
	//boolean active;
	TriggerType activationTrigger, costTrigger;
	Alive target;
	ArrayList<Buff> children = new ArrayList<Buff>();
	Missile onMissile;
	Ability ability;
	StackingType stackingType;
	String[] restrictions;
	public int count;
	ArrayList<Alive> hitList = new ArrayList<Alive>();
	enum TriggerType{
		ONATTACK, ONATTACKDECLARATION, ONHIT, ONBEINGATTACKED, ONBEINGHIT, 
		ONDEALINGDAMAGE, ONTAKINGDAMAGE, ONPHYSICAL, ONMAGICAL, UNIQUEATTACKMODIFIER, 
		ORBATTACK, ORBHIT, TIMED, NOTRIGGER, ONBEINGTARGETED, ONDEATH, ONKILL,
		ONSPELLCAST, ONSPELLTARGETED, ONSPELLAFFECTED, ONABILITYLEVEL, ONAGGRO,;
	}
	public Buff(Alive setOwner, String setBuffType, Ability parentAbility){
		super(setOwner.engine);
		genericBuffInit(setOwner,setBuffType);
		ability = parentAbility;
		setIntensity(parentAbility.level);
		timeLimitConfig(ability.abilityStats.duration);
		this.onPlacement();
	}
	public Buff(Alive setOwner, String setBuffType, int buffDurationInTicks){
		super(setOwner.engine);
		genericBuffInit(setOwner,setBuffType);
		timeLimitConfig(buffDurationInTicks);
		this.onPlacement();
	}
	private Buff(Alive setOwner, String setBuffType, int buffDurationInTicks, double setIntensity){
		super(setOwner.engine);
		genericBuffInit(setOwner, setBuffType);
		timeLimitConfig(buffDurationInTicks);
		intensity = setIntensity;
		this.onPlacement();
	}
	private void genericBuffInit(Alive setOwner, String setBuffType){
		buffName = setBuffType;
		owner = setOwner;
		stackTypeInit();
		if(!this.stackCheck()){
			this.active = false;
			return;
		}
		if(owner.getClass() == Hero.class)
			target = (Alive) ((Hero)owner).owner.selectedUnit;
		owner.getBuffs().add(this);
		triggerConfig();
		if(owner.getClass() == Hero.class)if(((Hero)owner).owner.getClass() == Human.class)
		for(BuffIcon bi: ((Human)((Hero)owner).owner).gui.buffs)if(bi.buff == null){
			bi.setBuff(this);
			break;
		}
		ownedObjects = new ArrayList<GameObject>();
	}
	enum StackingType{
		COUNT, STACK, NONE, RESTRICTION,;
	}
	private void timeLimitConfig(int setTimeLimit) {
		timeRemaining = setTimeLimit;
		limitedByTime = true;
	}
	private void triggerConfig(){
	}
	public final boolean tick(){
		if(onMissile != null)return onMissile.active;
		if(limitedByTime)timeRemaining--;
		if(limitedByTime && timeRemaining <= 0){
			destroy();
			return super.tick();
		}
		for(GameObject e: ownedObjects)if(!e.tick())engine.addToDeleteList(e);;
		setMySelfToUAM();
		return active;
	}
	private void setMySelfToUAM() {
		if(activationTrigger == TriggerType.UNIQUEATTACKMODIFIER &&
				owner.mainUAM == null)
					owner.mainUAM = this;
		else if((activationTrigger == TriggerType.ORBATTACK || activationTrigger == TriggerType.ORBATTACK) 
			&& (owner.mainUAM == null || owner.mainUAM.activationTrigger == TriggerType.UNIQUEATTACKMODIFIER))
					owner.mainUAM = this;
	}
	protected abstract void onPlacement();
	public abstract void doOnRemoval();
	public void destroy(){
		active = false;
		if(owner.getClass() == Hero.class)if(((Hero)owner).owner.getClass() == Human.class)
		for(BuffIcon bi: ((Human)((Hero)owner).owner).gui.buffs)if(bi.buff == this)
			bi.setBuff(null);
		doOnRemoval();
	}
	public void trigger(Alive  triggerTarget){
		
	}
	public void drawMe(StandardCamera camera){
		for(GameObject e: ownedObjects){
			engine.util.drawActor(((VisibleObject)e), ((Effect)e).color, camera);
		}
	}
	public void setIntensity(double d){
		if(d == 1){
			int g;
			g= 5;
			if(g == 5);
		}
	}
	public Alive getTarget(){
		return target;
	}
	public void setTarget(Alive setTarget){
		target = setTarget;
	}

	public static void removeBuff(Alive a, String bn){
		for(Buff b: a.getBuffs())if(b.buffName == bn)b.active = false;
	}
	public void costTrigger(){
		
	}
	private void stackTypeInit(){
		
	}
	private boolean stackCheck(){
		switch(stackingType){
		case RESTRICTION:
			for(Buff onTarget: owner.getBuffs())for(String restricted: restrictions)
				if(onTarget.buffName == restricted)return false;
			break;
		case NONE:
			for(Buff onTarget: owner.getBuffs())if(onTarget.buffName == buffName)
				return false;
			break;
		default:
			break;
		}
		return true;
	}
	/**
	 * Should be defined to return a copy of this buff.
	 */
	public abstract GameObject copy();
	protected void updateComponentLocation() {
		// TODO Auto-generated method stub
	}
	public abstract void update();
}
