package com.spartanlaboratories.engine.game;

import java.io.IOException;
import java.util.ArrayList;

import com.spartanlaboratories.engine.structure.StandardCamera;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Util;
import com.spartanlaboratories.engine.util.Location;

/**
 * The Creep object is a special type of alive that is meant to behave like a mindless monster or "creep"
 * that simply follows a set of directions.
 * @author Spartak
 * @since Pre-A
 */
public class Creep extends Alive{
	private boolean[] checkPoints = new boolean[engine.map.numberOfMovePoints];
	/**
	 * An ArrayList that contains all of the currently active creeps
	 */
	public static ArrayList<Creep> allCreeps = new ArrayList<Creep>();
	/**
	 * A constants that is used as the default for the size and width of creeps
	 */
	public static final int creepSize = 25;
	private Location[] movePoints;
	private MovementRule creepMovementRule;
	private static MovementRule globalMovementRule;
	private AggressionRule aggressionRule;
	private int followsRuleSet;
	private Location sentry;
	private CMPRule cmprule;
	/**
	 * A subset of rules for creeps to follow if their movement type is constant move points.
	 * @author Spartak
	 * @since A1
	 */
	public enum CMPRule{
		/***/CYCLE, DIEFREE, RANDOMCYCLE, RANDOMDIE, RANDOMNOREPEAT,DIEBOUND,;
		public void set(Creep c){
			c.cmprule = this;
		}
	}
	/**
	 * The type of movement that a creep has.
	 * @author Spartak
	 * @since A1
	 */
	public enum MovementRule{
		/**Creep will not reconfigure its movement target unless explicitly told to do so externally.*/NONE, 
		/**Creep will store its current location as a location to return to. It might leave it if it aggros on something of if it has another movement
		 * target but will always return to the sentry(guard) point. */SENTRY,
		/**Creep will do its best to move randomly*/RANDOM, 
		/**Creep will follow a set of constant move points. Its behaviour after completing the set is configured by {@link Creep.CMPRule}.*/CMP, 
		/**Makes the creep follow a CMP rule set and sets {@link Creep.CMPRule} to CYCLE*/PATROL,;
		/**
		 * Configures the passed in Creep to have this movement rule.
		 * @param c The Creep that will have its movement rule changed to this one.
		 */
		public void set(Creep c){
			c.creepMovementRule = this;
			switch(this){
			case CMP:
				c.keepTarget = false;
				CMPRule.DIEFREE.set(c);
				c.movePoints = c.engine.map.movePoints[c.faction.ordinal()][c.followsRuleSet];
				c.setTarget(c.getNextMovePoint());
				break;
			case NONE:
				c.setTarget(null);
				break;
			case SENTRY:
				c.setTarget(c.getLocation());
				c.sentry.duplicate(c.target);
				c.keepTarget = false;
				break;
			case RANDOM:
				c.keepTarget = false;
				break;
			case PATROL:
				CMP.set(c);
				CMPRule.CYCLE.set(c);
				break;
			}
		}
	}
	protected enum AggressionRule{
		GANDHI, RETALIATION, MASSMURDER, PSYCOPATH,;
	}
	public Creep(Engine engine, Faction setFaction) {
		super(engine, setFaction);
		setWidth(Creep.creepSize);
		setHeight(Creep.creepSize);
		changeStat(Constants.maxHealth, 300);
		changeStat(Constants.health, 300);
		changeBaseSpeed(250);
		needToMove = false;
		childSetsOwnMovement = false;
		changeStat(Constants.visibilityRange, 450);
		changeStat(Constants.attackRange, 12);
		changeStat(Constants.startingDamage, 30);
		changeStat(Constants.experienceGiven, 62);
		changeStat(Constants.healthRegen, 0.3);
		changeStat(Constants.goldGiven, ((int)(Math.random() * 8)) + 36); 
		switch(faction){
		case RADIANT:
			defaultColor = Util.Color.LIGHTBLUE;
			break;
		case DIRE:
			defaultColor = Util.Color.RED;
			break;
			default: defaultColor = Util.Color.ORANGE;
		}
		color = Util.Color.WHITE;
		allCreeps.add(this);
		initializeWithDefaultRules();
		try {
			setTexture();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean tick(){
		switch(creepMovementRule){
		case CMP:case PATROL:
			//creepAI();
			if(reachedPoint()){
				checkPoint();
				setTarget(getNextMovePoint());
			}
			break;
		case NONE:
			break;
		case RANDOM:
			if(reachedTarget())
				target.setCoords(getLocation().x + Math.random()*100, getLocation().y + Math.random() * 100);
			break;
		case SENTRY:
			if(target == null)target = sentry;
			break;
		}
		return super.tick();
	}
	private Alive checkForPotentialAttackTarget(){
		Alive potentialAttackTarget = null;
		for(Alive a: Alive.allAlives){
			if(a.faction != this.faction 
			&& engine.util.getRealCentralDistance(this, a) < getStat(Constants.visibilityRange)){
				if(potentialAttackTarget == null)potentialAttackTarget = a;
				else{
					if(engine.util.getRealCentralDistance(potentialAttackTarget, this)>
					engine.util.getRealCentralDistance(a, this))potentialAttackTarget = a;
				}
			}
		}
		return potentialAttackTarget;
	}
	private Location getNextMovePoint(){
		for(int i = 0; i < engine.map.numberOfMovePoints; i++){
			if(!checkPoints[i]){
				return movePoints[i];
			}
		}
		for(boolean b: checkPoints)b = false;
		switch(cmprule){
		case CYCLE:
			return movePoints[0];
		case DIEBOUND:
			sentry.duplicate(target);
		case DIEFREE:
			return keepTarget?target:null;
		default: return null;
		}
	}
	private void checkPoint(){
		for(int i = 0; i < engine.map.numberOfMovePoints; i++){
			if(!checkPoints[i]){
				checkPoints[i] = true;
				return;
			}
		}
	}
	private void creepAI(){
		if(getAttackTarget() != null && engine.util.everySecond(2)){
			Alive alive =  checkForPotentialAttackTarget(); 
			if(alive != null)
			if(engine.util.getRealCentralDistance(this, alive)
			<  engine.util.getRealCentralDistance(this, getAttackTarget()))
				aggroOn(alive);
		}
		if(getAttackTarget() == null || !getAttackTarget().alive){
			Alive pat = checkForPotentialAttackTarget();
			if(pat != null)aggroOn(pat);
			else target = getNextMovePoint();
		}
		if(engine.util.everySecond(2) && getAttackTarget() != null 
		&& !this.isVisible(getAttackTarget()) && isInForgetfulState()){
			aggroOn(null);
			setTarget(getNextMovePoint());
		}
		if(target == null)setTarget(getNextMovePoint());
	}
	private boolean reachedPoint(){
		Location movePoint = getNextMovePoint();
		if(movePoint == null)return true;
		if(getLocation().x < movePoint.x + getWidth()
		&& getLocation().x > movePoint.x - getWidth()
		&& getLocation().y < movePoint.y + getHeight()
		&& getLocation().y > movePoint.y - getHeight())
			return true;
		return false;
	}
	public boolean drawMe(StandardCamera camera) throws Util.NullColorException{
		float[] rgb = getRGB();
		rgb[0] *= getRatio("health");
		rgb[1] *= getRatio("health");
		rgb[2] *= getRatio("health");
		return drawMe(camera, rgb);
	}
	/**Returns a new Creep that is a copy of this Creep
	 * @return c a new Creep that is a copy of this Creep
	 * @see #copyTo(Creep)
	 */
	public Creep copy(){
		Creep c = new Creep(engine, faction);
		copyTo(c);
		return c;
	}
	protected void copyTo(Creep c){
		super.copyTo(c);
		c.checkPoints = new boolean[checkPoints.length];
		for(int i = 0; i < checkPoints.length; i++)
			c.checkPoints[i] = checkPoints[i];
		creepMovementRule.set(c);
		c.aggressionRule = aggressionRule;
		cmprule.set(c);
		c.sentry = sentry==null?null:new Location(sentry);
		c.followsRuleSet(followsRuleSet);//will set both followsRuleSet and movePoints
	}
	public void initializeWithDefaultRules(){
		if(globalMovementRule != null)globalMovementRule.set(this);
		else MovementRule.NONE.set(this);
	}
	public void setRule(MovementRule movementRule){
		movementRule.set(this);
	}
	public static void setGlobalRule(MovementRule movementRule){
		globalMovementRule = movementRule;
	}
	/**
	 * Makes this creep follow the passed in set of 
 	 *	constant movement point rules.
	 * @param ruleSet this Creep's new set of cmp rules 
	 */
	public void followsRuleSet(int ruleSet){
		followsRuleSet = ruleSet;
		movePoints = engine.map.movePoints[faction.ordinal()][followsRuleSet];
	}
}
