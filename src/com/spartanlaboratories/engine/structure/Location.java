package com.spartanlaboratories.engine.structure;
/**
 * Stores a Point with the coordinates (x,y) which will most often designate an object's location in the world. 
 * Has a variety of methods of modifying those coordinates as well as converting them to represent an object's location on screen and visa versa.
 * @author Spartak
 * 
 */
public class Location{
	/**The x coordinate of this Location 
	 * @see #changeX(double)
	 * */
	public double x;
	/**The y coordinate of this Location 
	 * @see #changeY(double)
	 */
	public double y;
	/**
	 * Creates a location with the given coordinates. Meant to be used when creating a new Location 
	 * at a specific point. When changing the location of an already existing point 
	 * use <a href = "Location.html"> the set coordinates function</a>, or {@link #change(double, double)}
	 * @param setX the new value of the horizontal coordinate
	 * @param setY the new value of the vertical coordinate
	 * @see #Location()
	 * @see #Location(Location)
	 * @see #Location(Location, Camera)
	 */
	public Location(double setX, double setY){
		x = setX;
		y = setY;
	}
	/**
	 * Creates a new Location which copies the coordinates of the given location. This does NOT 
	 * make this location reference the parameter, 
	 * @param location the location whose coordinates will be copied
	 * @see #Location()
	 * @see #Location(double, double)
	 * @see #Location(Location, Camera)
	 */
	public Location(Location location){
		x = location!=null?location.x:0;
		y = location!=null?location.y:0;
	}
	/**
	 * Creates a location at the given point on the screen as seem by the given camera.
	 * The Location will be created as a "real" location and not an on-screen one.
	 * @param locationOnScreen the location whose coordinates will be converted to "real" coordinates
	 * @param camera the camera that is viewing the location
	 * @see #Location() 
	 * @see #Location(double, double) 
	 * @see #Location(Location)
	 */
	public Location(Location locationOnScreen, Camera camera){
		setFromScreen(locationOnScreen, camera);
	}
	/**
	 * Creates a <a href="Location.html">Location</a> with the coordinates (0,0)
	 * @see #Location(Location)
	 * @see #Location(double, double)
	 * @see #Location(Location, Camera)
	 */
	public Location() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Changes the {@link #x} coordinate by the given value. Does what {@link #changeY(double)} does for the the {@link #y} coordinate
	 * @param xChange the net change of the {@link #x} coordinate
	 * @see #change(Location)
	 */
	public void changeX(double xChange){
		x += xChange;
	}
	/**
	 * Changes the {@link #y} coordinate by the given value. Does what {@link #changeX(double)} does for the {@link #x} coordinate
	 * @param yChange the net change of the {@link #y} coordinate
	 * @see #change(Location)
	 */
	public void changeY(double yChange){
		y += yChange;
	}
	/**
	 * Sets the coordinates of the location to match those of the given location. This is a "Hard" location
	 * set because the location will instantly change to match the parameter, for smoother change use 
	 * {@link #change(double,double)} or {@link #change(Location)}
	 * @param location the location whose coordinates will be copied
	 * @see #setCoords(double, double)
	 */
	public void duplicate(Location location){
		x = location.x;
		y = location.y;
	}
	/**
	 * Sets the coordinates of this location to the ones passed in.This is a "Hard" location
	 * set because the location will instantly change to match the parameter, for smoother change use 
	 * {@link #change(double, double)} or {@link #change(Location)}
	 * @param newX the new {@link #x} coordinate
	 * @param newY the new {@link #y} coordinate
	 */
	public void setCoords(double newX, double newY){
		x = newX; y = newY;
	}
	/**
	 * Changes the coordinates of this location by the values stored in the location given as a 
	 * parameter. This will not set this location equal no the paramater nor will it link their
	 * references. 
	 * @param locChange the location by the coordinates of which this locations coordinates will be modified
	 * @see #change(double, double)
	 */
	public void change(Location locChange){
		changeX(locChange.x);
		changeY(locChange.y);
	}
	/**
	 * Changes the coordinates of this location by the specified amounts
	 * @param changeInX the net amount by which the x coordinate will be changed
	 * @param changeInY the net amount by which the y coordinate will be changed
	 */
	public void change(double changeInX, double changeInY){
		changeX(changeInX);
		changeY(changeInY);
	}
	/**
	 * Returns at what point on the screen this location will be as seen by the given camera
	 * @param camera the camera that will be viewing this location
	 * @return this location's coordinates on a screen stored in a new location
	 */
	public Location getLocationOnScreen(Camera camera){ 
		return new Location(camera.monitorLocation.x - camera.worldLocation.x + x,
				camera.monitorLocation.y - camera.worldLocation.y + y);
	}
	public static Location getLocationInWorld(Location locationOnScreen, Camera camera){
		return new Location(camera.worldLocation.x + locationOnScreen.x - camera.monitorLocation.x,
				camera.worldLocation.y - locationOnScreen.y + camera.monitorLocation.y);
	}
	/**
	 * Sets the coordinates of this location to be what the coordinates of the passed location are in the "real" world. For example if a location on screen
	 * is passed in with the coordinates (x1,y1) then first of all based on the camera the passed in location's coordinates will be converted to their "real"
	 * values which are say (x2,y2) and then this location's coordinates will be set to (x2, y2)
	 * 
	 * @param locationOnScreen the location on a screen whose "real" world coordinates will be assigned to this location
	 * @param camera the camera that is viewing the passed location on screen
	 * @see #getLocationInWorld(), {@link #Location(Location, Camera)}
	 */
	public void setFromScreen(Location locationOnScreen, Camera camera){
		setCoords(camera.worldLocation.x + locationOnScreen.x - camera.monitorLocation.x,
			camera.worldLocation.y - locationOnScreen.y + camera.monitorLocation.y);
	}
	/**
	 * Changes the coordinates of the current location by the negatives of the coordinates of the passed in location. For example if the passed in locatioin has
	 * the coordinates (x1,y1) this function is equivalent of calling <a href="Location.html#change(double,double)">change(-x1,-y1)</a>
	 * @param previousChange the location by the opposite of which this location is being changed
	 */
	public void revert(Location previousChange){
		x -= previousChange.x;
		y -= previousChange.y;
	}
}
