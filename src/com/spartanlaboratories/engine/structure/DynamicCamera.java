package com.spartanlaboratories.engine.structure;

import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.ui.Gui;
import com.spartanlaboratories.engine.util.Location;
import com.spartanlaboratories.engine.util.Rectangle;

public class DynamicCamera{
	// FIELD DECLARATIONS
	
	private Rectangle world;
	private Rectangle monitor;
	private final Location standardZoomValues = new Location();
	private final Location zoomBind = new Location(0,1000);
	private double zoomLevel;
	// CONSTRUCTORS
	
	public DynamicCamera(Rectangle world, Rectangle monitor){
		this.world = world.copy();
		this.monitor = monitor.copy();
		setStandardZoom();
		zoomLevel = 1;
	}
	
	// BASIC GETTERS AND SETTERS
	
	public Rectangle getWorldArea(){
		return world.copy();
	}
	public Rectangle getMonitorArea(){
		return monitor.copy();
	}
	public void setMonitorStats(Location center, Location size){
		monitor = new Rectangle(center, size.x, size.y);
	}
	public void setWorldLocation(Location center){
		world.setCenter(center);
	}
	public void changeWorldLocation(Location locChange){
		Location location = world.getCenter();
		location.change(locChange);
		world.setCenter(location);
	}
	private void setWorldStats(Location center, Location size){
		world = new Rectangle(center, size.x, size.y);
	}
	private void setWorldSize(Location newSize){
		world.setSize(newSize);
	}
	private void magnifyWorld(double d){
		setWorldSize(new Location(world.getSize().x * d, world.getSize().y * d));
	}
	public void setMonitorSize(Location newSize){
		monitor.setSize(newSize);
	}
	
	//*********************************   ZOOM METHODS   ************************************//
	
	
	public void zoomOut(){
		zoomOut(1.05);
	}
	public void zoomIn(){
		zoomIn(1.05);
	}
	public void zoomOut(double ratio){
		magnifyWorld(ratio);
		updateZoom(1/ratio);
	}
	public void zoomIn(double ratio){
		zoomOut(1/ratio);
	}
	public void setZoom(double newZoomValue){
		toStandardZoom();
		zoomIn(newZoomValue);
	}
	public void toStandardZoom(){
		zoomOut(zoomLevel);
	}
	public void setStandardZoom(){
		standardZoomValues.setCoords(world.getSize().x, world.getSize().y);
	}
	public void setStandardZoomRelative(double ratio){
		magnifyWorld(ratio);
		setStandardZoom();
	}
	private void updateZoom(){
		zoomLevel = world.getSize().x / standardZoomValues.x;
	}
	private void updateZoom(double ratio){
		zoomLevel *= ratio;
	}
	
	
	//**********************************   END ZOOM METHODS   ******************************//
	
	// DYNAMIC CAMERA SPECIFIC METHODS
	public boolean isWorldBound(Location location){
		return withinWorldXBounds(location) && withinWorldYBounds(location);
	}
	public boolean isMonitorBound(Location location){
		return withinMonitorXBounds(location) && withinMonitorYBounds(location);
	}
	private boolean withinWorldXBounds(Location location){
		return withinWorldXBounds(location.x);
	}
	private boolean withinWorldYBounds(Location location){
		return withinWorldXBounds(location.y);
	}
	private boolean withinMonitorXBounds(Location location){
		return withinMonitorXBounds(location.x);
	}
	private boolean withinMonitorYBounds(Location location){
		return withinMonitorXBounds(location.y);
	}
	private boolean withinWorldXBounds(double x){
		return x < world.getXMax() && x > world.getXMin();
	}
	private boolean withinWorldYBounds(double y){
		return y > world.getYMin() && y < world.getYMax();
	}
	private boolean withinMonitorXBounds(double x){
		return x < monitor.getXMax() && x > monitor.getXMin();
	}
	private boolean withinMonitorYBounds(double y){
		return y < monitor.getYMax() && y > monitor.getYMin();
	}
	
	
	// ENGINE INTEGRATION
	public boolean isAtWorldLocation(Location worldLocation){
		return world.getCenter().equals(worldLocation);
	}
	public void printTo(Console console){
		console.out(toString());
	}
	public boolean isOwnedBy(Human human){
		return human.cameras.contains(this);
	}
	public boolean isOnMonitor(Gui gui){
		return
				monitor.getXMin() < gui.screenY &&
				monitor.getXMax() > 0 			&&
				monitor.getYMin() > 0 			&&
				monitor.getYMax() < gui.screenX;
	}
	public boolean canSeeObjectPartially(VisibleObject visibleObject){
		Rectangle area = visibleObject.getAreaCovered();
		return withinWorldXBounds(area.getXMin())
				|| withinWorldXBounds(area.getXMax())
				|| withinWorldYBounds(area.getXMin())
				|| withinWorldYBounds(area.getYMax());
	}
	public boolean canSeeObjectCenter(VisibleObject visibleObject){
		return isWorldBound(visibleObject.getLocation());
	}
	public boolean canSeeObjectWholly(VisibleObject visibleObject){
		Rectangle area = visibleObject.getAreaCovered();
		return withinWorldXBounds(area.getXMin())
				&& withinWorldXBounds(area.getXMax())
				&& withinWorldYBounds(area.getXMin())
				&& withinWorldYBounds(area.getYMax());
	}
	
	// Object class method overrides and other generic API
	public String toString(){
		return "In world: " + world.getCenter().toString() + world.getSize().toString() + 
				" On monitor: " + monitor.getCenter().toString() + monitor.getSize().toString();
	}
	public DynamicCamera copy(){
		return new DynamicCamera(world, monitor);
	}
	public void print(){
		System.out.println(toString());
	}
	public boolean equals(DynamicCamera camera){
		return world.equals(camera.getWorldArea()) && monitor.equals(camera.getMonitorArea());
	}
}
