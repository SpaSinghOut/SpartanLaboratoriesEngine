package com.spartanlaboratories.engine.game;

import java.util.ArrayList;

import com.spartanlaboratories.engine.structure.StandardCamera;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Human;
import com.spartanlaboratories.engine.structure.Util;
import com.spartanlaboratories.engine.structure.Util.NullColorException;
import com.spartanlaboratories.engine.util.Location;

public class Alive extends Actor{
	static int experienceRange = 650;
	boolean invulnerable;
	int invulnerabilityCount;
	double damageMultiplier;
	private double[] stats;
	public Faction faction;
	public Alive attackTarget;
	private AttackState attackState;
	protected ArrayList<Buff> buffs = new ArrayList<Buff>();
	public static ArrayList<Alive> allAlives = new ArrayList<Alive>();
	VisibleObject healthBar;
	Alive lastHitter;
	public boolean alive;
	Buff mainUAM;
	boolean missile;
	MissileStats attackMissileType;
	boolean noRetraction;
	private boolean[] permissions = new boolean[Constants.numberOfPermissions];
	public ItemList inventory;
	public Alive(Engine engine, Faction setFaction){
		super(engine);
		stats = new double[Constants.statsSize + Constants.numConstantStats];
		damageMultiplier = 1;
		stats[Constants.level] = 1;
		shape = Actor.Shape.QUAD;
		faction = setFaction;
		setAttackState(AttackState.NONE);
		needToMove = false;
		initHealthBar();
		alive = true;
		setStat(Constants.maxHealth, 1);
		setStat(Constants.health, 1);
		allAlives.add(this);
		for(int i = 0; i < permissions.length; i++)
			permissions[i] = true;
		//for(boolean b:permissions)b=true;
		attackOrientedInit();
		resetTexture = false;
		solid = true;
		attackMissileType = new MissileStats("auto");
	}
	protected void initHealthBar(){
		healthBar = new VisibleObject(engine);
		healthBar.solid = false;
		switch(faction){
		case RADIANT:
			healthBar.setColor(Util.Color.GREEN);
			break;
		case DIRE:
			healthBar.setColor(Util.Color.RED);
			break;
		case NEUTRAL:
			healthBar.setColor(Util.Color.YELLOW);
			break;
		}
	}
	public enum AttackState{
		NONE, SELECTED, MOVING, ANIMATION, RETRACTION, WAIT,;
	}
	public enum Direction{
		LEFT, RIGHT, UP, DOWN,;
	}
	public enum Faction{
		RADIANT, DIRE, NEUTRAL,;
	}
	public enum DamageType{
		PHYSICAL, MAGICAL, PURE, UNIVERSAL, HPREMOVAL,;
	}
	public boolean tick(){
		if (invulnerabilityCount-->0);
		else invulnerable = false;
		regen();
		for(Buff buff: getBuffs())if(!buff.tick())engine.addToDeleteList(buff);
		needToMove = permissions[Constants.movementAllowed];
		if(permissions[Constants.autoAttackAllowed])configureAttack();
		if(attackState == AttackState.ANIMATION || 
		attackState == AttackState.RETRACTION || attackState == AttackState.WAIT)
			changePermissions(Constants.movementAllowed, false);
		else changePermissions(Constants.movementAllowed, true);
		alive = getStat(Constants.health) > 0;
		return super.tick() && alive;
	}
	public void heal(int heal){
		stats[Constants.health] += heal;
	}
	public void setFaction(Faction setFaction){
		faction = setFaction;
	}
	public void dealDamage(Alive attacking, double damageDealt, DamageType setDamageType){
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONDEALINGDAMAGE)
				b.trigger(attackTarget);
		double calculateRealDamage = damageDealt < 0 ? 0 : damageDealt;
		attacking.takeDamage(this, calculateRealDamage, setDamageType );
	}
	public void takeDamage(Alive attacker, double d, DamageType damageType){
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONTAKINGDAMAGE)
				b.trigger(attackTarget);
		d *= 1 - ( getStat(Constants.armor) * .06 ) / ( 1 + getStat(Constants.armor) * .06);
		changeStat(Constants.health, -d);
		if(stats[Constants.health] <= 0 && alive){
			lastHitter = attacker;
			die();
			attacker.kill(this);
		}
	}
	final public void setStat(int stat, double newValue){
		stats[stat] = newValue;
	}
	final protected boolean isVisible(Actor seen){
		return (engine.util.getRealCentralDistance(this, seen) < this.stats[Constants.visibilityRange]);
	}
	final protected boolean isAttackTargetWithinAttackRange(){
		return (engine.util.getRealCentralDistance(this, attackTarget) < stats[Constants.attackRange] + getWidth() / 2 + attackTarget.getWidth() / 2);
	}
	final protected void issueAttack(Alive attacking){
		if(attacking.attackState == AttackState.NONE || attacking.attackState == AttackState.SELECTED ||
		attacking.attackState == AttackState.MOVING)
			if(attacking.faction != this.faction)
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONATTACKDECLARATION)
				b.trigger(attackTarget);
	}
	final  protected void getTargeted(Alive attacker){
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONBEINGTARGETED)
				b.trigger(attackTarget);
	}
	protected void doAttack(Alive attacking){
		if(mainUAM != null && mainUAM.activationTrigger == Buff.TriggerType.ORBATTACK)
			mainUAM.trigger(attacking);
		else if(mainUAM != null && mainUAM.activationTrigger == Buff.TriggerType.ORBHIT)
			mainUAM.costTrigger();
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONATTACK)
				b.trigger(attacking);
		if(missile){
			Missile attackMissile = new Missile(attackMissileType, this, attacking);
			attackMissile.setAuto(true);
			attackMissile.setDamage(getStat(Constants.damage));
		}
		else attacking.getAttacked(this);
	}
	protected void getAttacked(Alive attacker){
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONBEINGATTACKED)
				b.trigger(attacker);
		if(100d * Math.random() > getStat(Constants.evasion)){
			attacker.hit(this);getHit(attacker);
		}
	}
	protected void hit(Alive attacking){
		if(mainUAM != null && mainUAM.activationTrigger != Buff.TriggerType.ORBATTACK)
			this.mainUAM.trigger(attacking);
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONHIT)
				b.trigger(attacking);
		attacking.getHit(this);
		dealDamage(attacking, getStat(Constants.damage), DamageType.PHYSICAL);
	}
	protected void getHit(Alive attacker){
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONBEINGHIT)
				b.trigger(attackTarget);
	}
	
	/*
	 * no use for this right now as i do not have any spell or even the mechanic set up to target alives
	 * directly. before i start implementing the following function i have to make sure i first create
	 * a function like the one above only with another argument that being the spell target.
	 * only from that function will the below function be called
	 */
	protected void getSpellTargeted(){
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONSPELLTARGETED)
				b.trigger(this);
	}
	protected void getSpellAffected(Ability ability, Alive caster){
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONSPELLAFFECTED)
				b.trigger(caster);		
	}
	public void changeStat(int stat, double netChange){
		switch(stat){
		case Constants.experience:
			stats[stat] += netChange;
			changeStat(Constants.level, levelsAchieved());
			break;
		case Constants.level:
			stats[stat] += netChange;
			changeStat(Constants.abilityPoints, netChange);
			break;
		case Constants.baseAttackSpeed:
			stats[stat] += netChange;
			changeStat(Constants.attackSpeed, netChange);
			break;
		case Constants.health:
			stats[Constants.health] += netChange;
			if(stats[Constants.health] > getStat(Constants.maxHealth))
				stats[Constants.health] = getStat(Constants.maxHealth);
			break;
		case Constants.mana:
			stats[Constants.mana] += netChange;
			if(stats[Constants.mana] > getStat(Constants.maxMana))
				stats[Constants.mana] = getStat(Constants.maxMana);
			else if(stats[Constants.mana] < 0)
				stats[Constants.mana] = 0;
			break;
		case Constants.damage:
			if(this.getClass() == Hero.class){
			}
			stats[stat] += netChange;
			break;
		case Constants.startingDamage:
			stats[stat] += netChange;
			changeStat(Constants.baseDamage, netChange);
			break;
		case Constants.baseDamage:
			stats[stat] += netChange;
			changeStat(Constants.damage, netChange);
			break;
		case Constants.bonusDamage:
			stats[stat] += netChange;
			changeStat(Constants.damage, netChange);
			break;
		case Constants.maxHealth:
			stats[stat] += netChange;
			if(stats[stat] < 1)stats[stat] = 1;
			if(stats[Constants.health] > stats[stat])stats[Constants.health] = stats[stat];
			break;
		case Constants.maxMana:
			stats[stat] += netChange;
			if(stats[stat] < 1)stats[stat] = 1;
			if(stats[Constants.mana] > stats[stat])stats[Constants.mana] = stats[stat];
			break;
		default:
			stats[stat] += netChange;
			break;
		}
	}
	protected double levelsAchieved(){
		final double levelRequirement = 100 + 100 * getStat(Constants.level);
		if(getStat(Constants.experience) > levelRequirement){
			changeStat(Constants.experience, -levelRequirement);
			return 1 + levelsAchieved();
		}
		return 0;
	}
	public double getStat(int stat){
		switch(stat){
		case Constants.health:
			return stats[Constants.health];
		case Constants.maxHealth:
			if(stats[Constants.maxHealth] > 1)return stats[Constants.maxHealth];
			else return 1;
		case Constants.maxMana:
			if(stats[Constants.maxMana] > 1)return stats[Constants.maxMana];
			else return 1;
		default:
			return stats[stat];
		}
	}
	public void kill(Alive fallen){
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONKILL)
				b.trigger(attackTarget);
	}
	public void die(){
		alive = false;
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONDEATH)
				b.trigger(attackTarget);
		ArrayList<Hero> receivers = new ArrayList<Hero>();
		for(Alive a : Alive.allAlives){
			if(a.getClass() == Hero.class 
			&& a.faction != this.faction 
			&& engine.util.getRealCentralDistance(a, this) < Alive.experienceRange)
				receivers.add((Hero)a);
		}
		if(!receivers.contains(lastHitter) && lastHitter.getClass() == Hero.class)
			receivers.add((Hero)lastHitter);
		for(Hero a: receivers)if(a != null)
			a.changeStat(Constants.experience,
					getStat(Constants.experienceGiven) 
							/ (receivers.size()));
		double goldGiven = getStat(Constants.goldGiven) * 0.9 + (int)(Math.random() * (getStat(Constants.goldGiven) * 0.2));
		lastHitter.changeStat(Constants.gold, goldGiven );
		if(lastHitter.getClass() == Hero.class)
			engine.out(lastHitter.toString() + " received: " + goldGiven);
	}
	protected void setAttackState(AttackState setAttackState){
		attackState = setAttackState;
	}
	public AttackState getAttackState(){
		return attackState;
	}
	protected void resetAnimationCD(){
		this.stats[Constants.animationCD] = this.stats[Constants.baseAttackTime]
				/ (this.stats[Constants.attackSpeed]  / 100) * (int)engine.tickRate
				* this.stats[Constants.baseAnimationTime];
	}
	protected void resetRetractionCD(){
		stats[Constants.retractionCD] = (int)(this.stats[Constants.animationCD] / 3);
	}
	protected void resetAttackCD(){
		this.stats[Constants.attackCD] =  (int)(engine.tickRate / 5);
	}
	protected void resetAllAttackCDs(){
		this.resetAnimationCD();
		this.resetAttackCD();
		this.resetRetractionCD();
	}
	protected boolean isInForgetfulState(){
		switch(attackState){
		case ANIMATION:
			return false;
		default: return true;
		}
	}
	public float getRatio(String ratioType){
		switch(ratioType){
		case "health":
			return (((float)(stats[Constants.health])) / ((float)(stats[Constants.maxHealth])));
		case "mana":
			return (((float)(stats[Constants.mana])) / ((float)(stats[Constants.maxMana])));
		case "animation":
			return (((float)(stats[Constants.animationCD])) / ((float)(stats[Constants.baseAnimationTime])));
		case "experience":
			return (float)((stats[Constants.experience]) / (stats[Constants.level] * 100 + 100));
		}
		return 0.0f;
	}
	@Override
	protected boolean drawMe(StandardCamera camera, float[] RGB){
		if(!super.drawMe(camera, RGB) || !alive)return false;
		try {
			healthBar.drawMe(camera);
		} catch (NullColorException e){}
		for(Buff b: getBuffs())
			b.drawMe(camera);
		return true;
	}
	/**
	 * Makes this Alive consider the passed in Alive as its attack target. Will change the alive's attack state to selected.
	 * Might trigger buffs.
	 * @param setTarget - The Alive that will become the attack target of this Alive
	 */
	public void aggroOn(Alive setTarget){
		if(setTarget == attackTarget)return;
		if(setTarget != null)setAttackState(AttackState.SELECTED);
		else{
			setAttackState(AttackState.NONE);
			resetAllAttackCDs();
		}
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONAGGRO)
				b.trigger(setTarget);
		attackTarget = setTarget;
	}
	public Buff[] getBuffsOfType(String bn){
		Buff[] ba = new Buff[getBuffs().size()];
		int i = 0;
		for(Buff b:getBuffs())if(b.buffName == bn)
			ba[i++] = b;
		return ba;
	}
	public void changePermissions(int permission, boolean allowed){
		switch(permission){
		case Constants.movementAllowed:
			if(allowed)
				for(Buff b:getBuffs())
					if(b.active && b.buffName == "stun")allowed = false;
			permissions[permission] = allowed;
			needToMove = allowed;
			break;
		case Constants.spellCastAllowed:
			if(allowed)for(Buff b:getBuffs())if(b.buffName == "stun" || 
			b.buffName == "silence");
			else permissions[permission] = allowed;
		case Constants.channelingAllowed:
			if(allowed)for(Buff b:getBuffs())if(b.buffName == "stun" || 
			b.buffName == "silence");
			else permissions[permission] = allowed;
			break;
		case Constants.autoAttackAllowed:
			if(allowed)for(Buff b:getBuffs())if(b.buffName == "stun");
			else permissions[permission] = allowed;
			break;
		default: permissions[permission] = allowed;
		}
	}
	public boolean getPermissions(int permission) {
		return permissions[permission];
	}
	public Alive getAttackTarget() {
		return attackTarget;
	}
	protected boolean hasBuffType(String setBuffType){
		for(Buff b: getBuffs())if(b.buffName == setBuffType)return true;
		return false;
	}
	public boolean hasAttackTarget(){
		return attackTarget != null;
	}

	// ******  MODIFICATION NEEDED!!!! ******
	// the getter method should give a buff array to avoid external list modification
	public ArrayList<Buff> getBuffs() {
		return  buffs;
	}
	public void setWidth(double width){
		super.setWidth(width);
		healthBar.setWidth(width);
	}
	public void setHeight(double height){
		super.setHeight(height);
		healthBar.setHeight((int)(height * .25));
	}
	protected void updateComponentLocation(){
		super.updateComponentLocation();
		healthBar.setLocation(
				getLocation().x - getWidth() / 2 + getRatio("health") * getWidth() / 2,
				getLocation().y - getHeight() / 2 - healthBar.getHeight() / 2);
	}
	public void goTo(Location setTarget){
		super.goTo(setTarget);
		changePermissions(Constants.movementAllowed, true);
	}
	/**
	 * Copies this object
	 * @return A new Alive that is a copy of this Alive
	 */
	public Alive copy(){
		Alive a = new Alive(engine, faction);
		copyTo(a);
		return a;
	}
	protected void copyTo(Alive a){
		super.copyTo(a);
		a.alive = alive;
		a.attackState = attackState;
		a.attackTarget = attackTarget;
		for(Buff b: buffs)a.buffs.add(b);
		a.faction = faction;
		a.invulnerabilityCount = invulnerabilityCount;
		a.invulnerable = invulnerable;
		a.lastHitter = lastHitter;
		a.mainUAM = mainUAM;
		a.missile = missile;
		a.attackMissileType = attackMissileType;
		a.noRetraction = noRetraction;
		a.permissions = permissions;
		a.stats = new double[stats.length];
		for(int i = 0; i < stats.length; i++)a.stats[i] = stats[i];
	}
	public void rightClick(Location locationOnScreen, StandardCamera camera){
		Human owner = (Human)this.owner;
		Actor selected = owner.selected(locationOnScreen);
		Alive targetedUnit = (Alive) (selected==null?null:Alive.class.isAssignableFrom(selected.getClass())?selected:null);
		if(targetedUnit!=null)aggroOn(targetedUnit);
		else super.rightClick(locationOnScreen, camera);
	}
	private void regen(){
		if(getClass() == Hero.class){
			if(stats[Constants.healthRegen] > 0)
				changeStat(Constants.health, stats[Constants.healthRegen] / engine.tickRate);
			if(stats[Constants.manaRegen] > 0)
				changeStat(Constants.mana, stats[Constants.manaRegen] / engine.tickRate);
		}
		else if(getClass() == Creep.class){
			if(stats[Constants.healthRegen] > 0)
				changeStat(Constants.health, stats[Constants.healthRegen] / engine.tickRate);
		}
	}
	private void attackOrientedInit() {
	permissions[Constants.autoAttackAllowed] = true;
	changeStat(Constants.baseAttackTime, 1.7);
	changeStat(Constants.baseAttackSpeed, 100);
	changeStat(Constants.baseAnimationTime, 1);
	this.resetAllAttackCDs();
	}
	private void configureAttack(){
		needToMove = attackTarget == null && target != null;
		changePermissions(Constants.movementAllowed, attackTarget == null && target != null);
		if(attackTarget == null || !attackTarget.alive){
			if(getAttackState() != AttackState.NONE)
				setAttackState(Alive.AttackState.NONE);
			needToMove = true;
		}
		else if(attackState == AttackState.NONE && attackTarget == null){
			needToMove = true;
		}
		else if(attackState == AttackState.NONE && attackTarget != null && attackTarget.active){
			aggroOn(attackTarget);
		}
		else if(attackState == AttackState.NONE && attackTarget != null && !attackTarget.active){
			attackTarget = null;
		}
		else if(attackState == AttackState.SELECTED){
			if(attackTarget == null){
				needToMove = true;
				this.setAttackState(Alive.AttackState.NONE);
				target = null;
				return;
			}
			if(!isVisible(attackTarget)){
				attackTarget = null;
				needToMove = true;
				target = null;
			}
			else if(isVisible(attackTarget)){
				if(isAttackTargetWithinAttackRange()){
					issueAttack(attackTarget);
					attackTarget.getTargeted(this);
					setAttackState(Alive.AttackState.ANIMATION);
				}
				else if(!isAttackTargetWithinAttackRange()){
					System.out.println(getStat(Constants.attackRange));
					setAttackState(AttackState.MOVING);
					needToMove = true;
				}
			}
		}
		else if(this.attackState == Alive.AttackState.MOVING){
			if(attackTarget == null){
				needToMove = true;
				setAttackState(Alive.AttackState.NONE);
				return;
			}
			if(!isAttackTargetWithinAttackRange()){
			needToMove = true;
			target = attackTarget.getLocation();
			}
			else {
				target = null;
				issueAttack(attackTarget);
				setAttackState(Alive.AttackState.ANIMATION);
			}
		}
		else if(this.attackState == Alive.AttackState.ANIMATION && stats[Constants.animationCD] >   0){
			stats[Constants.animationCD]--;
		}
		else if(this.attackState == Alive.AttackState.ANIMATION && stats[Constants.animationCD] <= 0){
			resetAnimationCD();
			setAttackState(Alive.AttackState.RETRACTION);
			this.doAttack(attackTarget);
		}
		else if(attackState == AttackState.RETRACTION && stats[Constants.retractionCD] > 0){
			if(noRetraction)this.setAttackState(AttackState.WAIT);
			stats[Constants.retractionCD]--;
		}
		else if(attackState == AttackState.RETRACTION && stats[Constants.retractionCD] <= 0){
			this.resetRetractionCD();
			this.setAttackState(Alive.AttackState.WAIT);
		}
		else if(attackState == AttackState.WAIT && stats[Constants.attackCD] > 0){
			this.stats[Constants.attackCD]--;
		}
		else if(attackState == AttackState.WAIT && stats[Constants.attackCD] == 0){
			if(attackTarget != null)setAttackState(AttackState.SELECTED);
			else setAttackState(AttackState.NONE);
			this.resetAttackCD();
		}
	}
}
