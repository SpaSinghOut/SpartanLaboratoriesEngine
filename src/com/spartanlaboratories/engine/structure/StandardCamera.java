package com.spartanlaboratories.engine.structure;

import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.util.Location;
/**
 * <h1>The StandardCamera Object</h1>
 * Extends: <a href="StructureObject.html">Structure Object</a>
 * <p>Used by the <a href="Human.html">Human</a> object for viewing visible objects
 * @author Spartak
 */
public class StandardCamera extends StructureObject{
	public EdgePanRules edgePanRules = new EdgePanRules();
	class EdgePanRules{
		boolean panOn;
		boolean panAll;
		boolean panScreen;
		int panningSpeed;
		int panningRange;
		public EdgePanRules(){
			panningSpeed = 20;
			panningRange = 60;
		}
		void setPan(boolean pan){
			panOn = pan;
		}
	}
	/**
	 * A {@linkplain Location} that designates the center of this StandardCamera's point of view in the "real" world
	 */
	public Location worldLocation;
	/**
	 * A <a href = "Location.html">Location</a> that designates where the center viewpoint of this camera is located on the monitor.
	 */
	public Location monitorLocation;
	/**
	 * A Location that stores how far away from the {@link #worldLocation} objects can be seen by this StandardCamera. The x coordinate represents the total width of view
	 * of the StandardCamera and the y coordinate represents the total height of view of the StandardCamera. 
	 * So if this Location's coordinates are (x,y) then the camera will be 
	 * able to see objects that are x/2 to the right or left of the {@link #worldLocation} and y/2 up or down from the 
	 * {@link #worldLocation}. When a camera is 
	 * created the dimensions location is set to the resolution of the monitor (unless a custom display size was specified) and it is usually better 
	 * to be left that way unless a single display is being used to render the view of multiple Cameras.
	 * 
	 * @see #monitorLocation
	 */
	public Location dimensions;
	private double additionalSpeed, acceleration;
	/**<h1>The StandardCamera Constructor</h1>
	 * <p>
	 * Creates a camera at the specified location and creates default location values
	 * @param engine
	 * the game engine
	 * @param worldLocation
	 * the location in the "real" world at which the camera is located
	 */
	public StandardCamera(Engine engine, Location worldLocation){
		super(engine);
		this.worldLocation = worldLocation;
		dimensions = new Location(engine.getScreenDimensions().x, engine.getScreenDimensions().y);
		monitorLocation = new Location(engine.getScreenDimensions().x / 2, engine.getScreenDimensions().y / 2);
		edgePanRules.panningSpeed = 600 / engine.tickRate;
		additionalSpeed = 0;
		acceleration = 1;
	}
	/**
	 * increases camera speed by camera acceleration and gives back the combined speed
	 * @return the total current camera speed
	 */
	public int getCameraSpeed(){
		return (int) (edgePanRules.panningSpeed + (additionalSpeed += acceleration));
	}
	/**
	 * Sets the camera speed back to default
	 */
	public void resetCameraSpeed(){
		additionalSpeed = 0;
	}
	/**
	 * Checks whether the camera is able to see the given object. Identical to {@link #withinBounds(VisibleObject)}.
	 * 
	 * @param vo - The <a href="VisibleObject.html">Visible Object</a> the visibility of which is being tested
	 * @return a boolean value which represents the object's visibility
	 */
	public boolean canSeeObject(VisibleObject vo){
		double
		wx = worldLocation.x, wy = worldLocation.y, dx = dimensions.x, dy = dimensions.y, 
		vx = vo.getLocation().x, vy = vo.getLocation().y,
		vxmin = vx - vo.getWidth() / 2, vxmax = vx + vo.getWidth() / 2,
		vymin = vy - vo.getHeight() / 2, vymax = vy + vo.getHeight() / 2;
		boolean 
		xmintest = vxmax > wx - dx / 2, xmaxtest = vxmin < wx + dx / 2, 
		ymintest = vymax > wy - dy / 2, ymaxtest = vymin < wy + dy / 2;
		return xmintest||xmaxtest||ymintest||ymaxtest;
	}
	/**
	 * Returns whether or not the parameter is completely covered by the view of this camera.
	 * 
	 * @param vo - The parameter the visibility of which is being tested.
	 * @return {@code true} if all of the passed in visible object is visible.
	 * 	<br> {@code false} if any part of the passed in visible object is not visible.
	 */
	public boolean fullyWithinBounds(VisibleObject vo){
		Location loc = vo.getLocation().getScreenCoords(this);
		double x = loc.x, y = loc.y;
		return xMinBound(x - vo.getWidth() / 2)
				&& xMaxBound(x + vo.getWidth() / 2)
				&& yMinBound(y - vo.getWidth() / 2)
				&& yMaxBound(y + vo.getWidth() / 2)
				;
	}
	/**
	 * Checks if any part of the passed in object is within the bounds of this camera. Should be identical 
	 * in functionality to {@link #canSeeObject(VisibleObject)}. The difference is this method uses the parameter's location on screen rather than the 
	 * real world location. This method provides an alternate way of checking the visibility of an object and should be used in case the algorith in 
	 * {@link #canSeeObject(VisibleObject)} is broken.
	 * 
	 * @param vo - The {@link VisibleObject} whose presence within the borders of this camera is being tested.
	 * @return - A boolean value that represents whether or not this the passed in object is within borders.
	 */
	public boolean withinBounds(VisibleObject vo){
		Location loc = vo.getLocation().getScreenCoords(this);
		double x = loc.x, y = loc.y;
		return xMinBound(x + vo.getWidth() / 2)
				|| xMaxBound(x - vo.getWidth() / 2)
				|| yMinBound(y + vo.getWidth() / 2)
				|| yMaxBound(y - vo.getWidth() / 2)
				;
	}
	public boolean withinBounds(Location l){
		return xBound(l.x)&&yBound(l.y);
	}
	public boolean xBound(double x){
		return xMinBound(x)&&xMaxBound(x);
	}
	public boolean yBound(double y){
		return yMinBound(y)&&yMaxBound(y);
	}
	public boolean xMinBound(double x){
		return x > monitorLocation.x - dimensions.x / 2;
	}
	public boolean xMaxBound(double x){
		return x < monitorLocation.x + dimensions.x / 2;
	}
	public boolean yMinBound(double y){
		return y > monitorLocation.y - dimensions.y / 2;
	}
	public boolean yMaxBound(double y){
		return y < monitorLocation.y + dimensions.y / 2;
	}
}
