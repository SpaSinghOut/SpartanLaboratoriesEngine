package com.spartanlaboratories.engine;

import java.awt.event.KeyEvent;

import org.lwjgl.opengl.GL11;

import com.spartanlaboratories.engine.game.Ability;
import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Hero;
import com.spartanlaboratories.engine.structure.Console;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.DynamicCamera;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Human;
import com.spartanlaboratories.engine.structure.Map;
import com.spartanlaboratories.engine.structure.StandardCamera;
import com.spartanlaboratories.engine.structure.Tracker;
import com.spartanlaboratories.engine.structure.Util;
import com.spartanlaboratories.engine.ui.AbilityButton;
import com.spartanlaboratories.engine.util.Location;
import com.spartanlaboratories.engine.util.Rectangle;

class UnitTest extends Map{

	private UnitTest(Engine engine) {
		super(engine);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeSpawnPoints() {
		
	}

	@Override
	public void init() {
		trackerInitialization();														// Initializes the tracker
		
		// Creates a human. As of version 2.0.1 this needs to be done explicitly.
		Human human = new Human(engine, Alive.Faction.RADIANT);			
		// Creates a reference to the human's console
		Console console = human.gui.console;
		
		// Creation of the Alives that are going to be tested.
		Hero unit = new Hero<Spell>(engine, Hero.HeroType.RAZOR, human);
		// Setting the basic parameters that are needed for the alive to operate.
		unit.setWidth(60);
		unit.setHeight(60);
		unit.changeBaseSpeed(300);
		unit.setColor(Util.Color.WHITE);
		unit.setLocation(0,0);
		unit.changeStat(Constants.damage, 30);
		// Making the alive be one of the units controlled by the human object created earlier.
		human.addUnit(unit);
		
		// Now that the unit exists it needs to have a spell that it can cast.
		Spell spell = new Spell	("fireball", unit);
		new AbilityButton(spell,human.gui);
		spell.levelAbility();
		
		
		// Camera stuff.
		DynamicCamera camera1 = new DynamicCamera(engine), camera2 = new DynamicCamera(engine);
		human.setCamera(0, camera1);
		//human.setCamera(1, camera2);
		// While the following function call is not concise it is in its declaration: Camera.setDrawArea(Rectangle rectangle);
		camera1.setDrawArea(new Rectangle(new Location(camera1.getMonitorArea().getCenter().x * 0.5,camera1.getMonitorArea().getCenter().y), 
														camera1.getMonitorArea().getSize().x * 0.5, camera1.getMonitorArea().getSize().y * 0.8));
		camera2.setDrawArea(new Rectangle(new Location(camera2.getMonitorArea().getCenter().x * 1.5,camera2.getMonitorArea().getCenter().y), 
														camera2.getMonitorArea().getSize().x * 0.5, camera2.getMonitorArea().getSize().y));
		// The cameras print their information to the console
		camera1.printTo(console);
		camera2.printTo(console);
		// Making Camera 1 go the the newly bound unit when the space key is pressed.
		camera1.addObjectKeyBind(unit, KeyEvent.VK_SPACE);
		
		camera2.setZoomAbsolute(new Location(2,2));
		camera1.setStandardZoomRelative(2);
		camera1.toStandardZoom();
		camera2.setZoomMouseImpact(.1d);
		camera2.holdPointOnZoom(true);
		
		camera1.setCameraSpeed(0);
		camera1.setCameraAcceleration(0);
		camera1.setPanningRange(0);
		
		// Creating another alive. This one will one have tests be performed on it, however it is needed as the subject of interaction for the 
		// previously created alive.
		
		/*
		Alive unit2 = new Alive(engine, Alive.Faction.DIRE);
		unit2.setWidth(40);
		unit2.setHeight(40);
		unit2.setColor(Util.Color.WHITE);
		unit2.setTexture("res/iron branch.png");
		unit2.setLocation(100,0);
		unit2.changeStat(Constants.maxHealth, 300);
		unit2.changeStat(Constants.health, 300);
		*/
		
		//unit.setTexture("res/radiant creep.png");
		
	}
	private void trackerInitialization(){
		int newEntity = engine.tracker.addEntity();
		engine.tracker.initialize();
		engine.tracker.setEntityTracked(newEntity, true);
		engine.tracker.clearTrackedEntities();
		//engine.tracker.initialize(Tracker.TrackerPreset.PRESET_RUN);
		//engine.tracker.setNotifyPeriod(5);
	}
	/*@Override
	public void drawMap(StandardCamera camera){
		try {
			System.out.println(Alive.allAlives.get(0).drawMe(camera));
		} catch (NullColorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	@Override
	protected void drawBorder() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void update() {
	}

	public static void main(String[] args){
		Engine engine = new Engine();
		engine.typeHandler.newEntry("map", new UnitTest(engine));
		engine.init();
		engine.start();
	}
}
class Spell extends Ability{

	public Spell(String abilityName, Hero setOwner) {
		super(abilityName, setOwner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void cast() {
		System.out.println("The fireball was cast");
		owner.goTo(new Location(owner.getLocation().x + 100, owner.getLocation().y + 100));
	}
	
}
