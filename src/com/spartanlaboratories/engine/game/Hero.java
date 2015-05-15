package com.spartanlaboratories.engine.game;

import java.io.IOException;
import java.util.ArrayList;

import com.spartanlaboratories.engine.structure.Camera;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Controller;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Location;
import com.spartanlaboratories.engine.structure.Util;

public class Hero<Element extends Ability> extends Alive{
	private int inventorySize = 6;
	int numberOfAbilities = 3;
	public ArrayList<Element> abilities = new ArrayList<Element>();
	public HeroType heroType;
	VisibleObject manaBar;
	boolean initialized;
	Ability equippedSpell;
	public enum HeroType{
		RAZOR(true,600),
		;
		boolean ranged;
		double range;
		HeroType(boolean setRanged, double setRange){
			ranged = setRanged;
			range = setRange;
		}
	}
	public Hero(Engine engine, HeroType setHeroType, Controller controller) {
		super(engine, controller.faction);
		manaBar = new VisibleObject(engine);
		manaBar.color = Util.Color.BLUE;
		setWidth(35);
		setHeight(35);
		defaultColor = Util.Color.WHITE;
		color = defaultColor;
		shape = Actor.Shape.QUAD;
		setLocation(new Location(faction == Alive.Faction.RADIANT ? 500: 2500, 750));
		childSetsOwnMovement = true;
		setAttackState(AttackState.NONE);
		childSetsOwnMovement = false;
		initStats();
		inventory = new ItemList(inventorySize, ItemList.Type.INVENTORY, this);
		initHeroType(HeroType.RAZOR);
	}
	public void initHeroType(HeroType setHeroType){
		heroType = setHeroType;
		changeStat(Constants.mana, getStat(Constants.maxMana) - getStat(Constants.mana));
		missile = heroType.ranged;
		changeStat(Constants.attackRange, heroType.range);
		initialized = true;
	}
	public boolean tick(){
		for(Ability a: abilities)a.tick();
		return super.tick();
	}
	
	public void castSpell(Ability ability){
		if(ability.state != Ability.State.READY && ability.abilityStats.castType != Castable.CastType.TOGGLE)
			return;
		if(ability.abilityStats.castType == Castable.CastType.ALIVETARGET){
			if(owner.selectedUnit == null || !Alive.class.isAssignableFrom(owner.selectedUnit.getClass()))return;
			ability.setTarget((Alive)owner.selectedUnit);
		}
		ability.activate();
		for(Buff b: getBuffs())
			if(b.activationTrigger == Buff.TriggerType.ONSPELLCAST)
				b.trigger(this);
	}
	@Override
	public void die(){
		super.die();
		owner.setRespawnTimer(2);
		Alive.allAlives.remove(this);
	}
	@Override
	public boolean drawMe(Camera fromCamera)throws Util.NullColorException{
		if(!super.drawMe(fromCamera, getRGB()))return false;
		engine.util.drawActor(manaBar, manaBar.color, fromCamera);
		return true;
	}
	public void setWidth(double width){
		super.setWidth(width);
		manaBar.setWidth(width);
	}
	public void setHeight(double height){
		super.setHeight(height);
		manaBar.setHeight((int)(height * 0.25));
	}
	protected void updateComponentLocation(){
		super.updateComponentLocation();
		manaBar.setLocation(healthBar.getLocation());
		manaBar.setWidth(getWidth() * getRatio("mana"));
		healthBar.changeLocation(0, -healthBar.getHeight());
	}
	public void addAbility(Element element){
		abilities.add(element);
		element.owner = this;
	}
	public ArrayList<Element> getAbilities(){
		ArrayList<Element> abilities = this.abilities;
		return abilities;
	}
	public void copyTo(Hero hero){
		hero.equippedSpell = equippedSpell;
		hero.inventorySize = inventorySize;
		hero.numberOfAbilities = numberOfAbilities;
		hero.abilities = new ArrayList<Element>();
		for(Element e:abilities)hero.abilities.add(e);
	}
	public void leftClick(Location locationOnScreen, Camera camera){
		castSpell(equippedSpell);
	}
	private void initStats(){
		changeBaseSpeed(300);
		changeStat(Constants.visibilityRange, 900);
		changeStat(Constants.maxHealth, Constants.baseHealth);
		changeStat(Constants.health, getStat(Constants.maxHealth));
		changeStat(Constants.maxMana, 300);
		changeStat(Constants.mana, getStat(Constants.maxMana));
		changeStat(Constants.manaRegen, 1);
		changeStat(Constants.healthRegen, 0.1);
		changeStat(Constants.experienceGiven, 200);
		changeStat(Constants.startingDamage, 0);
		changeStat(Constants.abilityPoints, 1);
		changeStat(Constants.attackSpeed, 0);
	}
	private void debug(){
		System.out.println("Hero: " + "Attack state: " + getAttackState());
		System.out.println("Hero: " + "immobile: " + immobile);
		System.out.println("Hero: " + "needToMove: " + needToMove);
	}
}
