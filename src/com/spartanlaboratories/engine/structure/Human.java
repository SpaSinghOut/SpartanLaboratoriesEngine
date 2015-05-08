package com.spartanlaboratories.engine.structure;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.*;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.spartanlaboratories.engine.game.Ability;
import com.spartanlaboratories.engine.game.Actor;
import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Hero;
import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.game.Alive.Faction;
import com.spartanlaboratories.engine.structure.Util.Color;
import com.spartanlaboratories.engine.ui.Gui;
/**
 * A human version of a unit controller
 * @author spart_000
 * @see <a href="Controller.html">Controller</a>
 */
final public class Human extends Controller{
	boolean showMap;
	static int abilityButtonHeight = 50;
	static int abilityButtonWidth = 50;
	static Location[] abilityButtonLocations = new Location[3];
	//declaring the camera and the camera's speed attribute
	ArrayList<Camera> cameras = new ArrayList<Camera>();
	int rightClickMove;
	/**
	 * Sets the selected unit to the one passed in. Game mechanics and graphic user interface elements that require a target will reference this unit.
	 * If set to null, user interface elements that are not considered "Special" will disappear.
	 * @param actor - the new selected unit.
	 */
	public void setSelectedUnit(Actor actor){
		super.setSelectedUnit(actor);
		if(Hero.class.isAssignableFrom(actor.getClass()))gui.setInterfaceVisibilty("Hero");
		else if(Alive.class.isAssignableFrom(actor.getClass()))gui.setInterfaceVisibilty("Alive");
		else if(Actor.class.isAssignableFrom(actor.getClass()))gui.setInterfaceVisibilty("Actor");
		portrait.active = true;
		portrait.setTexture(actor.getTexture());
	}
	/**
	 * The state of the mouse's button 1
	 * @see <a href="Human.LeftClickState.html">LeftClickState</a>
	 */
	public LeftClickState leftClickState;
	/**
	 * The current selected ability, will be cast if the human has the proper leftClickState and the first mouse button is pressed.
	 */
	public Ability selectedAbility;
	/**
	 * This human's graphical user interface
	 */
	public Gui gui;
	int heroSelect;
	Alive hoverSelected;
	boolean outsideClickHeard;
	int tickTracker;
	/**
	 * The colored background of the minimap
	 */
	public VisibleObject mapBackground;
	VisibleObject portrait;
	/**
	 * Creates a human with a hero as its main controlled unit
	 * @param engine the game engine
	 * @param setFaction the faction of this controller, this will also be the faction of all the units 
	 * controlled by this controller
	 */
	public Human(Engine engine, Alive.Faction setFaction){
		super(engine, setFaction);
		cameras.add(new Camera(engine, new Location(500,500)));
		for(int i = 0; i < 3; i++){
			int length = abilityButtonLocations.length;
			abilityButtonLocations[i] = new Location(
					engine.getScreenDimensions().x - 
					(length + 14) * abilityButtonWidth +
					(i * 1.5 + 1) * abilityButtonWidth,
					abilityButtonHeight);
		}
		leftClickState = LeftClickState.DEFAULT;
		gui = new Gui(this);
		heroSelect = 0;
		outsideClickHeard = false;
		mapBackground = new VisibleObject(engine);
		mapBackground.setWidth(gui.screenX * 0.1);
		mapBackground.setHeight(gui.screenX * 0.1);
		mapBackground.color = Util.Color.YELLOW;
		mapBackground.setLocation(mapBackground.getWidth() / 2, gui.screenY - mapBackground.getHeight() / 2);
		portrait = new VisibleObject(engine);
		portrait.setWidth(gui.screenX * 0.1);
		portrait.setHeight(gui.screenY * 0.2);
		portrait.setLocation(gui.screenX * 0.219, gui.screenY * 0.862);
		try {
			portrait.setTexture(TextureLoader.getTexture("jpg", ResourceLoader.getResourceAsStream(Constants.versionString + "/res/iron branch.jpg")));
		} catch (IOException e){}
		showMap = false;
		portrait.active = false;
	}
	/**
	 * Performs one update on human input and interface related elements.
	 */
	final public void tick(){
		try {
			listenForInput();
		} catch (SLEImproperInputException e) {}
		super.tick();
		gui.tick();
	}
	private int preventInputOverflow;
	private void listenForInput() throws SLEImproperInputException{
		for(Camera camera: cameras)if(camera.edgePanRules.panOn)edgePan(camera);
		//START UNIT SELECTION
		if(engine.tracker.trackedEntities[Tracker.ALG_UNIT_SELECTION])engine.tracker.giveStartTime(Tracker.ALG_UNIT_SELECTION);
		/*checks if the left mouse button is pressed down and attempts to find a unit under the mouse and set that as the selected unit
		 */
		Location mouseClickLocation = new Location(Mouse.getX(), Mouse.getY());//declares the location which the mouse is at
		if(Mouse.isButtonDown(0)&&--preventInputOverflow<=0){
			checkForMapClick(mouseClickLocation);
			Actor clicked = selected(mouseClickLocation);
			gui.clearInterface();
			portrait.active = false; 
			if(clicked != null){
				if(selectedUnit!=null)selectedUnit.resetTexture = false;
				setSelectedUnit(clicked);
				selectedUnit.resetTexture = true;
				preventInputOverflow = engine.tickRate / 6;
			}
			
			if(engine.tracker.trackedEntities[Tracker.ALG_UNIT_SELECTION])engine.tracker.giveEndTime(Tracker.ALG_UNIT_SELECTION);
		//END UNIT SELECTION
			/* There are two main reasons for checking if leftClickState is set to ability use 
			 * 1. to make sure that the player's click only signifies an ability cast if the player has chosen to cast an ability
			 * 2. to make sure that after an ability has been cast it does not get cast again because the player is still holding down the button
			 */
			if(leftClickState == LeftClickState.ABILITYUSE){
				Hero caster = (Hero)selectedUnit;
				gui.out("hero, " + caster.heroType + ", casts spell: " + selectedAbility.abilityStats);
				caster.castSpell(selectedAbility);
			}
			leftClickState = LeftClickState.DEFAULT;
		}	
		
		//the following moves and/or aggros the player using the rightmouseclick
		if(Mouse.isButtonDown(1)){
			if(hasControlOf(selectedUnit))
				selectedUnit.rightClick(mouseClickLocation, coveringCamera(mouseClickLocation));
		}
	}
	private Camera coveringCamera(Location locationOnScreen) throws SLEImproperInputException{
		for(Camera c:cameras)
			if(c.withinBounds(locationOnScreen))return c;
		throw new SLEImproperInputException(engine.tracker, "The Human type unit controller: " + engine.controllers.indexOf(this)
				+ " attempted a world based mouse action that was outside of the scope of any camera.");
	}
	private void checkForMapClick(Location mouseClickLocation){
		if(mouseClickLocation.x < mapBackground.getLocation().x + mapBackground.getWidth() / 2 
		&& mouseClickLocation.x > mapBackground.getLocation().x - mapBackground.getWidth() / 2 
		&&engine.getScreenDimensions().y - mouseClickLocation.y > mapBackground.getLocation().y - mapBackground.getHeight() / 2
		&&engine.getScreenDimensions().y - mouseClickLocation.y < mapBackground.getLocation().y + mapBackground.getHeight() / 2)
			getPrimaryCamera().worldLocation.setCoords((mouseClickLocation.x - (mapBackground.getLocation().x - mapBackground.getWidth() / 2)) / (mapBackground.getWidth()) * engine.getWrap().x,
			(engine.getScreenDimensions().y - mouseClickLocation.y - (mapBackground.getLocation().y - mapBackground.getHeight() / 2)) / (mapBackground.getHeight()) * engine.getWrap().y);
	}
	public Actor selected(Location clicked){
		final int searchRange = 100;
		Location b = new Location(0, 0);
		try {
			b.setFromScreen(clicked,coveringCamera(clicked));
		} catch (SLEImproperInputException e) {
			engine.tracker.printAndLog("selected() was called to search outside the scope of any camera and abandoned");
			return null;
		}
		for(VisibleObject a : engine.qt.retrieveBox(b.x - searchRange, b.y - searchRange, b.x + searchRange, b.y + searchRange)){
			if(!a.equals(selectedUnit) && Actor.class.isAssignableFrom(a.getClass()) && engine.util.checkPointCollision(a, b)){
				return (Actor)(a);
			}
		}
		return null;
	}
	/**<ul>
	 * <b>LeftClickState</b><p><code>&#8195;public enum LeftClickState</code><p>
	 * The possible states that determine the action that is to be undertaken should the left mouse button be pressed.
	 * @author Spartak
	 *
	 */
	public enum LeftClickState{
		DEFAULT, ABILITYUSE, ABILITYTAKE,;
	}
	
	public void drawMe(Camera camera) {
		engine.tracker.giveStartTime(Tracker.REND_HUMAN_GUI);
		gui.render();
		engine.tracker.giveEndTime(Tracker.REND_HUMAN_GUI);
		engine.tracker.giveStartTime(Tracker.REND_HUMAN_PORTRAITS);
		int r = 0, d = 0;
		// ****************************************   PLAYER ICONS ************************************************ //
		/*
		for(Controller ho: engine.controllers)
			if(ho.selectedUnit.faction == Alive.Faction.RADIANT)
				engine.util.drawOnScreen(ho.getHero(), Util.Color.WHITE, 
				new Location(gui.screenX / 2 - gui.HUD_component_clock.getSize().width / 2 - ho.getHero().getWidth() * ++r,ho.getHero().getHeight() / 2));
			else engine.util.drawOnScreen(ho.getHero(), Util.Color.WHITE,
				new Location(gui.screenX / 2 + gui.HUD_component_clock.getSize().width / 2 + ho.getHero().getWidth() * ++d,ho.getHero().getHeight() / 2));
		engine.tracker.giveEndTime(Tracker.REND_HUMAN_PORTRAITS);
		*/
		if(portrait.active)engine.util.drawOnScreen(portrait,Util.Color.WHITE,portrait.getLocation());
		if(!showMap)return;
		engine.tracker.giveStartTime(Tracker.REND_HUMAN_MAP);
		engine.util.drawOnScreen(mapBackground, Color.YELLOW, mapBackground.getLocation());
		for(Actor a: engine.allActors)if(Alive.class.isAssignableFrom(a.getClass()))engine.util.drawOnMap(this,a);
		engine.tracker.giveEndTime(Tracker.REND_HUMAN_MAP);
	}
	/**
	 * Gets the current location of the mouse on the screen.
	 * @return A location that represents the location of the cursor on the screen
	 */
	public Location getMouseLocation(){
		return new Location(Mouse.getX(), Mouse.getY());
	}
	/**
	 * Returns the location in the "real world" to which the mouse cursor is pointing. Since it does not take in a Camera object as an argument it assumes
	 * that the cursor location is within the scope of a camera contained by this Human's list of owned cameras. Therefore it is possible that this method will
	 * act out and throw an exception if the engine user is not using the default camera implementation. (if no actions are taken with the cameras list then
	 * it is very unlikely that this method will throw an exception as the entire display is covered by the default camera.)
	 * @return A location in the "real world" at which the cursor is located
	 * @throws SLEImproperInputException
	 */
	public Location getMouseInWorld() throws SLEImproperInputException{
		return new Location(getMouseLocation(), coveringCamera(getMouseLocation()));
	}
	/*
	 * An extension of the mouse listener that governs over the position of the Human's Camera and where it is located by moving it in a 
	 * direction based on the location of the cursor on the screen
	 */
	private void edgePan(Camera camera){
		int edgePanningRange = camera.edgePanRules.panningRange;
		int speed = camera.getCameraSpeed();
		if(Mouse.getX() < edgePanningRange)camera.worldLocation.changeX(speed);
		else if(Mouse.getX() > engine.getScreenDimensions().x - edgePanningRange)camera.worldLocation.changeX(speed);
		if(Mouse.getY() < edgePanningRange)camera.worldLocation.changeY(speed);
		else if(Mouse.getY() > engine.getScreenDimensions().y - edgePanningRange)camera.worldLocation.changeY(speed);
		if(Mouse.getX() > edgePanningRange && Mouse.getX() < engine.getScreenDimensions().x - edgePanningRange &&
			Mouse.getY() > edgePanningRange && Mouse.getY() < engine.getScreenDimensions().y - edgePanningRange)
			camera.resetCameraSpeed();
	}
	/**<b><ul>getPrimaryCamera<p><code></b>&#8195;public Camera getPrimaryCamera()<p></code>
	 * Returns the first camera is this Human's list of cameras
	 * @return A camera object that is the first in this Human's list of camera objects
	 */
	public Camera getPrimaryCamera() {
		return cameras.get(0);
	}
	/**
	 * Binds the passed in camera object to be this Human's primary Camera. Does NOT save the old camera. Does reference and not duplicate the passed in camera.
	 * @param camera the camera that will become this Human's  primary Camera.
	 * @param index - the location in the list of cameras at which this camera is to be placed
	 */
	public void setCamera(int index, Camera camera) {
		if(index < cameras.size())
			cameras.set(index, camera);
		else 
			cameras.add(camera);
	}
	public boolean hasControlOf(Actor o){
		return controlledUnits.contains(o)&&o!=null;
	}
}
