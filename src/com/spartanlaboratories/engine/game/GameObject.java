package com.spartanlaboratories.engine.game;

import java.util.ArrayList;

import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.StructureObject;
import com.spartanlaboratories.engine.util.Location;
/**
 * One of the most basic of the engines object frameworks. Can be active or inactive, exists in the world, and can own other game objects.
 * @author Spartak
 * @since Pre-A
 */
public abstract class GameObject extends StructureObject{
	/**
	 * Determines whether or not this GameObject is active. If it is active then it will tick and draw, if it is not active then it will not.
	 * While this may vary between subclasses an object that is set to be inactive will most likely be deleted. Set to true on creation.
	 */
	public boolean active;
	public static ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	public boolean ticked;
	private Location location;
	/**
	 * A list of other game objects that this game object owns.
	 */
	protected ArrayList<GameObject> ownedObjects = new ArrayList<GameObject>();
	/**
	 * Creates a game object that uses the passed in engine to reference the world and structure objects.
	 * @param setEngine - The engine which this game object will be referencing
	 */
	public GameObject(Engine setEngine){
		super(setEngine);
		active = true;
		location = new Location();
		gameObjects.add(this);
	}
	/**
	 * Performs one update on the state of this game object. This method is called systematically by the engine and therefore it would be bad practice to 
	 * call the method externally.
	 * @return whether or not this object is active
	 */
	public boolean tick(){
		update();
		ticked = true;
		return active;
	}
	/**
	 * Returns a new GameObject that is a copy of this GameObject
	 * @return a new GameObject that is a copy of this GameObject
	 */
	public abstract GameObject copy();
	
	protected void copyTo(GameObject vo){
		vo.active = active;
		for(GameObject go: ownedObjects)vo.ownedObjects.add(go);
		vo.location = getLocation();
	}
	protected abstract void updateComponentLocation();
	/**
	 * <h1>Gets the location of the visible object </h1>
	 * Cannot be used for modification, instead use: {@link #setLocation(Location)}, {@link #changeLocation(Location)},
	 *  or {@link #changeLocation(double, double)}
	 * @return a copy of the visible object's location
	 */
	public Location getLocation() {
		return new Location(location);
	}
	/**
	 * Changes the coordinates of this object's location by the coordinates of the passed in location.
	 * @param locChange the location whose coordinates will be used as modifying values for this object's location's coordinates
	 * @see <a href="Location.html">Location</a>
	 */
	public void changeLocation(Location locChange){
		location.change(locChange);
		updateComponentLocation();
	}
	public void changeLocation(double x, double y){
		location.change(x, y);
		updateComponentLocation();
	}
	/**
	 * Makes the coordinates of the location of this object the same as those
	 * of the location given
	 * @param location The location the coordinates of which will be copied
	 */
	public void setLocation(Location location) {
		this.location.duplicate(location);
		updateComponentLocation();
	}
	public void setLocation(double x, double y) {
		location.setCoords(x,y);
		updateComponentLocation();
	}
	public abstract void update();
}
