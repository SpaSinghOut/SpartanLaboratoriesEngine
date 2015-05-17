package com.spartanlaboratories.engine.game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Human;
import com.spartanlaboratories.engine.structure.Location;
import com.spartanlaboratories.engine.structure.Util;

/**
 * The Ability class is this engine's prototype and utility class for creating Hero abilities or spells. This class gets its basic information from 
 * an xml file called Abilities.xml which needs to be defined prior to the usage of this class. 
 * 
 * @author Spartak
 *
 */

public abstract class Ability implements Castable{
	public class AbilityStats{
		int CD;										//The cool down of this ability
		int manaCost;								//The amount of mana required to use this ability
		Util.Color color;					//The main color of this ability
		int duration;								//How long this ability lasts
		CastType castType;					//The way in which this ability is cast
		int[] levelRequirements;				//A list of the hero levels at which this ability can have more points put towards it
		Ability owner;
		public String name;
		AbilityStats(String abilityName) throws XMLStreamException, FileNotFoundException{
			FileInputStream fw = new FileInputStream("Abilities.xml");
			XMLInputFactory xml = XMLInputFactory.newInstance();
			XMLStreamReader reader = xml.createXMLStreamReader(fw);
			while(reader.hasNext()){
				if(reader.isStartElement() && reader.getLocalName() == "Ability"){
					reader.next();
					if(reader.isCharacters())reader.next();
					String text = reader.getLocalName().toLowerCase();
					if(reader.isStartElement()&&text.equals("tname")){
						reader.next();
						if(!(reader.isStartElement()&&reader.isEndElement())){
							if(reader.getText().toLowerCase().equals(abilityName.toLowerCase())){
								startParse(reader);
								return;
							}
						}
						else System.out.println("The <tName> tag should be followed by the ability's name");
					}
					else System.out.println("Ability declaration should be immediately followed by the <tName> tag");
				}
				reader.next();
			}
			System.out.println("Ability was not found in the Abilities.xml file");
		}
		private void startParse(XMLStreamReader reader) throws XMLStreamException {
			while(reader.hasNext()){
				reader.next();
				if(reader.isStartElement()){
					System.out.println("reading the local name as " + reader.getLocalName().toLowerCase());
					switch(reader.getLocalName()){
					case "iDuration":
						reader.next();
						duration = Integer.parseInt(reader.getText()) * Engine.tickRate;
						break;
					case "iCooldown":
						reader.next();
						CD = Integer.parseInt(reader.getText()) * Engine.tickRate;
						break;
					case "iManaCost":
						reader.next();
						manaCost = Integer.parseInt(reader.getText());
						break;
					case "tLevellingType":
						reader.next();
						if(reader.getText().toLowerCase().equals("normal"))LevellingType.setLevellingType(this,LevellingType.NORMAL);
						else if(reader.getText().toLowerCase().equals("ultimate"))LevellingType.setLevellingType(this, LevellingType.ULTIMATE);
						else if(reader.getText().toLowerCase().equals("default"))LevellingType.setLevellingType(this, LevellingType.DEFAULT);
						else System.out.println("levelling type not found");
						break;
					case "tCastType":
						reader.next();
						if(reader.getText().toLowerCase().equals("pointtarget"))castType = CastType.POINTTARGET;
						else if(reader.getText().toLowerCase().equals("instant"))castType= CastType.INSTANT;
						else if(reader.getText().toLowerCase().equals("alivetarget"))castType = CastType.ALIVETARGET;
						else if(reader.getText().toLowerCase().equals("channeling"))castType = CastType.CHANNELING;
						else if(reader.getText().toLowerCase().equals("passive"))castType = CastType.PASSIVE;
						else if(reader.getText().toLowerCase().equals("toggle"))castType = CastType.TOGGLE;
						else System.out.println("Cast type not found.");
						System.out.println("read the ability cast type as: " + reader.getText().toLowerCase() + " and set  cast type to " + castType);
						break;
					case "tColor":
						reader.next();
						if(reader.getText().toLowerCase().equals("red"))color = Util.Color.RED;
						else if(reader.getText().toLowerCase().equals("green"))color = Util.Color.GREEN;
						else if(reader.getText().toLowerCase().equals("blue"))color = Util.Color.BLUE;
						else if(reader.getText().toLowerCase().equals("yellow"))color = Util.Color.YELLOW;
						else if(reader.getText().toLowerCase().equals("purple"))color = Util.Color.PURPLE;
						else System.out.println("invalid color");
						break;
					default: System.out.println("undefined field name: " + reader.getLocalName());
					}
				}
				else if(reader.isEndElement() && reader.getLocalName().equals("Ability")){
					System.out.println("Found end element: " + reader.getLocalName());
					break;
				}
			}
			
		}
		void setOwner(Ability ability){
			owner = ability;
		}
	}
	enum LevellingType{
		DEFAULT,
		NORMAL,
		ULTIMATE,;
		static void setLevellingType(AbilityStats setAbility, LevellingType setLevellingType){
			setAbility.levelRequirements = getLevelRequirements(setLevellingType);
		}
		private static int[] getLevelRequirements(LevellingType setLevellingType){
			int[] levelRequirements = new int[0];
			switch(setLevellingType){
			case NORMAL:
				levelRequirements = new int[4];
				for(int i = 0; i < 4; i++)levelRequirements[i] = 2 * (i + 1) - 1;
				break;
			case ULTIMATE:
				levelRequirements = new int[3];
				for(int i = 0; i < 3; i++)levelRequirements[i] = 5 * (i + 1) + 1;
				break;
			case DEFAULT:
				levelRequirements = new int[1];
				levelRequirements[0] = 1;
				break;
			}
			return levelRequirements;
		}
	}
	/**
	 * The Hero that owns this ability.
	 */
	public Hero owner;
	/**
	 * The amount of time remaining until this ability is once again off of cooldown.
	 */
	public int CDRemaining;							//The amount of time remaining until this ability is off of cool down
	/**
	 * Describes the current condition of this ability. (is it ready to be cast, is it active or on cooldown, etc.)
	 */
	public State state;								//Is the ability ready, on cool down, etc.
	/**
	 * The amount of time remaining until this ability is no longer active.
	 */
	public int durationLeft;							//How much time this ability has left until it ends
	private Alive target;						//If this ability targets an Alive what is that target
	private Location targetLocation;			//and what is that Alive's location
	public int level;									//The amount of skill points that were put into this ability
	private int castControl;							//Is used to prevent toggle skills from activating multiple times per click/press
	private AbilityStats abilityStats;
	public Ability(String abilityName, Hero setOwner){
		owner = setOwner;
		try {
			abilityStats = new AbilityStats(abilityName);
		} catch (FileNotFoundException | XMLStreamException e) {
			owner.engine.tracker.printAndLog("Something went wrong with ability initialization");
			e.printStackTrace();
		}
		level = 0;
		castControl = 0;
		state = State.DOWN;
		abilityStats.setOwner(this);
	}
	public enum State{
		READY,DOWN,CHANNELING,ACTIVE ;
	}
	
	public void tick(){
		if(abilityStats.castType.isTimeBased() && level > 0){
			if(--durationLeft == 0)terminate();
			if(--CDRemaining <= 0)state = State.READY;
			if(owner.getStat(Constants.mana) > abilityStats.manaCost && CDRemaining <= 0 && level > 0)this.state = State.READY;
			else state = State.DOWN;
			if(state == State.CHANNELING)channel();
		}
		castControl--;
	}
	public void activate(){
		CDRemaining = abilityStats.CD;
		durationLeft = abilityStats.duration;
		switch(abilityStats.castType){
		case INSTANT:
			activate(true);
			state = State.DOWN;
			break;
		case PASSIVE:
			activate(true);
			state = State.READY;
			break;
		case POINTTARGET:
			if(owner.owner.getClass() == Human.class){
				targetLocation.setFromScreen(((Human)owner.owner).getMouseLocation(), ((Human)owner.owner).getPrimaryCamera());
			}
			activate(targetLocation);
			state = State.DOWN;
			break;
		case ALIVETARGET:
			activate(owner.owner.selectedUnit.getLocation());
			state = State.DOWN;
			break;
		case CHANNELING:
			if(owner.getPermissions(Constants.channelingAllowed)
			&& owner.getPermissions(Constants.spellCastAllowed)){
				state = State.CHANNELING;
				if(owner.owner.getClass() == Human.class){
					targetLocation.setFromScreen(((Human)owner.owner).getMouseLocation(), ((Human)owner.owner).getPrimaryCamera());
				}
				state = State.DOWN;
			}
			break;
		case TOGGLE:
			if(castControl <= 0){
				if(state == State.READY){
					state = State.ACTIVE;
					activate(true);
				}
				else if(state == State.ACTIVE){
					state = State.READY;
					activate(false);
				}
				castControl = owner.owner.engine.tickRate / 2;
			}
			break;
		}
	}
	private void activate(Location setLocation){
		owner.changeStat(Constants.mana, -(abilityStats.manaCost));
		if(abilityStats.castType == Castable.CastType.ALIVETARGET)target = (Alive) owner.owner.selectedUnit;
		cast();
	}
	@SuppressWarnings("incomplete-switch")
	private void activate(boolean b){
		owner.changeStat(Constants.mana, -(abilityStats.manaCost));
		cast();
	}
	public abstract void cast();
	private void channel(){
		if(!owner.getPermissions(Constants.channelingAllowed)){
			endChannel();
			return;
		}
	}
	void endChannel(){
		
	}
	private void terminate(){
		for(Effect a: owner.effects)owner.owner.engine.addToDeleteList(a);
		owner.effects.clear();
		state = State.DOWN;
		endChannel();
	}
	public void setTarget(Alive setTarget){
		target = setTarget;
	}
	public void levelAbility(){
		owner.changeStat(Constants.abilityPoints, -1);
		if(abilityStats.castType == Castable.CastType.TOGGLE)
			state = State.READY;
		level++;
		if(abilityStats.castType == Castable.CastType.PASSIVE)
			activate();
	}
}
