package com.spartanlaboratories.engine.structure;

import java.awt.Point;
import java.util.ArrayList;

import org.lwjgl.input.*;

import com.spartanlaboratories.engine.game.Ability;
import com.spartanlaboratories.engine.game.Actor;
import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Hero;
import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.ui.Gui;
import com.spartanlaboratories.engine.util.Location;
/**
 * A human version of a unit controller. 
 * As of version 2.0.0 the utility of the Human object has tended to specialize on the input received from the mouse and keyboard as well as 
 * the handling of the graphical display of the world through the Camera objects owned by this object.
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
	private boolean[] mouseButtonDown = new boolean[3];
	/**
	 * This human's graphical user interface
	 */
	public Gui gui;
	int heroSelect;
	Alive hoverSelected;
	int tickTracker;
	VisibleObject portrait;
	private int preventInputOverflow;
	/**
	 * Sets the selected unit to the one passed in. Game mechanics and graphic user interface elements that require a target will reference this unit.
	 * If set to null, user interface elements that are not considered "Special" will disappear.
	 * @param actor - the new selected unit.
	 */
	public void setSelectedUnit(Actor actor){
		super.setSelectedUnit(actor);
		selectedUnit.resetTexture = true;
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
	 * Creates a human with a hero as its main controlled unit
	 * @param engine the game engine
	 * @param setFaction the faction of this controller, this will also be the faction of all the units 
	 * controlled by this controller
	 */
	public Human(Engine engine, Alive.Faction setFaction){
		super(engine, setFaction);
		cameras.add(new StandardCamera(engine, new Location(500,500)));
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
		portrait = new VisibleObject(engine);
		portrait.setWidth(gui.screenX * 0.1);
		portrait.setHeight(gui.screenY * 0.2);
		portrait.setLocation(gui.screenX * 0.219, gui.screenY * 0.862);
		/*try {
			portrait.setTexture(TextureLoader.getTexture("jpg", ResourceLoader.getResourceAsStream(Constants.versionString + "/res/iron branch.jpg")));
		} catch (IOException e){}*/
		showMap = false;
		portrait.active = false;
	}
	/**
	 * Performs one update on human input and interface related elements.
	 */
	final public void tick(){
		for(Camera c:cameras)
			for(VisibleObject vo: c.getQualifiedObjects())if(vo.active)
				c.generateQuad(vo);
		try {
			listenForInput();
		} catch (SLEImproperInputException e) {
			e.printStackTrace();
		}
		super.tick();
		gui.tick();
	}
	void drawQuads(){
		for(Camera c:cameras){
			for(Quad q: Camera.quads)
				engine.util.drawQuad(q);
			c.clearQuads();
		}
	}
	/**
	 * Use {@link Camera#unitAt(Location)} instead
	 * 
	 * @deprecated
	 * @param clicked
	 * @return
	 */
	public Actor selected(Location clicked){
		final int searchRange = 100;
		Location b = new Location(0, 0);
		try {
			b.duplicate(coveringCamera(clicked).getWorldLocation(clicked));
		} catch (SLEImproperInputException e) {
			engine.tracker.printAndLog("selected() was called to search outside the scope of any camera and abandoned");
			return null;
		}
		for(VisibleObject a : engine.qt.retrieveBox(b.x - searchRange, b.y - searchRange, b.x + searchRange, b.y + searchRange))
			if(Actor.class.isAssignableFrom(a.getClass()) && engine.util.checkPointCollision(a, b))
				return (Actor)(a);
		return null;
	}
	/**
	 * The possible states that determine the action that is to be undertaken should the left mouse button be pressed.
	 * 
	 * @author Spartak
	 */
	public enum LeftClickState{
		DEFAULT, ABILITYUSE, ABILITYTAKE,;
	}
	public void drawMe() {
		engine.tracker.giveStartTime(Tracker.REND_HUMAN_GUI);
		gui.render();
		engine.tracker.giveEndTime(Tracker.REND_HUMAN_GUI);
		engine.tracker.giveStartTime(Tracker.REND_QUADS);
		drawQuads();
		engine.tracker.giveEndTime(Tracker.REND_QUADS);
		engine.tracker.giveStartTime(Tracker.REND_PORTRAIT);
		if(portrait.active)engine.util.drawOnScreen(portrait,Util.Color.WHITE,portrait.getLocation());
		engine.tracker.giveEndTime(Tracker.REND_PORTRAIT);
	}
	/**
	 * Gets the current location of the mouse on the screen.
	 * @return A location that represents the location of the cursor on the screen
	 */
	public Location getMouseLocationG(){
		Point p = gui.getMousePosition();
		return p == null ? null : new Location(p.getX(), p.getY());
	}
	public Location getMouseLocation(){
		return new Location(Mouse.getX(), Mouse.getY());
	}
	/**
	 * Returns the location in the "real world" to which the mouse cursor is pointing. Since it does not take in a StandardCamera object as an argument it assumes
	 * that the cursor location is within the scope of a camera contained by this Human's list of owned cameras. Therefore it is possible that this method will
	 * act out and throw an exception if the engine user is not using the default camera implementation. (if no actions are taken with the cameras list then
	 * it is very unlikely that this method will throw an exception as the entire display is covered by the default camera.)
	 * @deprecated
	 * @return A location in the "real world" at which the cursor is located
	 * @throws SLEImproperInputException - If the location of the mouse is not covered by any camera
	 */
	public Location getMouseInWorld() throws SLEImproperInputException{
		return coveringCamera(getMouseLocationG()).getWorldLocation(getMouseLocationG());
	}
	/**
	 * Returns the first camera is this Human's list of cameras
	 * @return A camera object that is the first in this Human's list of camera objects
	 */
	public Camera getPrimaryCamera() {
		return cameras.get(0);
	}
	/**
	 * Sets the camera reference at the given index to the given camera. If the index is greater than or equal to the number of cameras in the list will add
	 * the parameter to the cameras list instead.
	 *  Does NOT save the old camera. Does reference and not duplicate the passed in camera.
	 * @param camera the camera that will become this Human's  primary StandardCamera.
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
	public Camera coveringCamera(Location locationOnScreen) throws SLEImproperInputException{
		for(Camera c:cameras)
			if(c.coversMonitorLocation(locationOnScreen)){
				return c;
			}
		throw new SLEImproperInputException(engine.tracker, "The Human type unit controller: " + engine.controllers.indexOf(this)
				+ " attempted a world based mouse action that was outside of the scope of any camera: " + locationOnScreen);
	}
	public void setMouseButtonDown(int button, boolean isDown){
		mouseButtonDown[button - 1] = isDown;
	}
	private void listenForInput() throws SLEImproperInputException{
		Location mouseLocation = getMouseLocation();//declares the location at which the mouse is
		int dWheel = Mouse.getDWheel();
		if(dWheel != 0)
			coveringCamera(mouseLocation).handleMouseWheel(dWheel, mouseLocation);
		for(Camera c: cameras)c.handleMouseLocation(mouseLocation);
		//START UNIT SELECTION
		engine.tracker.giveStartTime(Tracker.ALG_UNIT_SELECTION);
		/*checks if the left mouse button is pressed down and attempts to find a unit under the mouse and set that as the selected unit
		 */
		if(Mouse.isButtonDown(0) &&--preventInputOverflow<=0){
			Actor clicked = coveringCamera(mouseLocation).unitAt(mouseLocation);
			gui.clearInterface();
			portrait.active = false; 
			if(clicked != null){
				setSelectedUnit(clicked);
				preventInputOverflow = engine.tickRate / 7;
			}
			
			engine.tracker.giveEndTime(Tracker.ALG_UNIT_SELECTION);
		//END UNIT SELECTION
			/* There are two main reasons for checking if leftClickState is set to ability use 
			 * 1. to make sure that the player's click only signifies an ability cast if the player has chosen to cast an ability
			 * 2. to make sure that after an ability has been cast it does not get cast again because the player is still holding down the button
			 */
			if(leftClickState == LeftClickState.ABILITYUSE){
				Hero caster = (Hero)selectedUnit;
				caster.castSpell(selectedAbility);
			}
			leftClickState = LeftClickState.DEFAULT;
		}	
		
		//the following moves and/or aggros the player using the rightmouseclick
		if(Mouse.isButtonDown(1) && hasControlOf(selectedUnit))
			selectedUnit.rightClick(mouseLocation, coveringCamera(mouseLocation));
	}
}
