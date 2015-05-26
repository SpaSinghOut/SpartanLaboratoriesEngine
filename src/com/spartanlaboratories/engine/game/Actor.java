package com.spartanlaboratories.engine.game;

import com.spartanlaboratories.engine.structure.StandardCamera;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Controller;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.util.Location;

public class Actor extends VisibleObject implements AcceptsInput{
	public static String className;
	protected Location target;
	private double baseSpeed;
	private double speedModifier;
	private double uniqueSpeedModifier;
	boolean needToMove;
	boolean childSetsOwnMovement;
	private static int pushOrder;
	protected MovementType movementType;
	protected Alive homingTarget;
	private final double maxRotation = 18;
	public Controller owner;
	boolean keepTarget;
	//the higher the following value is the slower everything in the world will be moving
	private static final double globalMoveSpeedModifier = 1.5;
	public Actor(Engine engine){
		super(engine);
		engine.allActors.add(this);
		active = true;
		if(shape == null)shape = Shape.QUAD;
		pushOrder = 0;
		movementType = MovementType.LOCATIONBASED;
		speedModifier = 1;
		uniqueSpeedModifier = 1;
	}
	
	protected enum MovementType{
		LOCATIONBASED, DIRECTIONBASED, HOMING;
	}
	public boolean tick(){
		if(target!=null){
			if(!childSetsOwnMovement && !immobile)
				setMovement(target);
			if(atTarget()&&!keepTarget)
				setTarget(null);
			if(needToMove && !immobile)
				switch(movementType){
				case LOCATIONBASED:
					moveToALocation();
					break;
				case DIRECTIONBASED:
					moveInADirection();
					break;
				case HOMING:
					setMovement(homingTarget.getLocation());
					home();
					break;
				}
			pushOrder = 0;
		}
		return super.tick();
	}
	Location locChange;
	/**
	 * Updates the direction in which this actor is heading
	 * @param setTarget the target location of this actor
	 */
	public void setMovement(Location setTarget){
		double xChange;
		double yChange;
		double hypotenuse = Math.sqrt(Math.abs(Math.pow((getLocation().x - setTarget.x),2) + Math.pow((getLocation().y - setTarget.y), 2))) ;
		xChange = -( getTrueSpeed() * (getLocation().x - setTarget.x) / hypotenuse);
		yChange = -( getTrueSpeed() * (getLocation().y - setTarget.y) / hypotenuse);
		locChange = new Location(xChange, yChange);
	}
	/**
	 * Attemps once to move once to move towards its target
	 * 
	 * @return
	 * A boolean value that signifies the success of the movement
	 */
	public boolean move(){
		if(immobile)return false;
		changeLocation(locChange);
		if(Alive.class.isAssignableFrom(getClass()) && anythingInTheWay()){
			changeLocation(-locChange.x, -locChange.y);
			return false;
		}
		return true;
	}
	public void push(Actor whom, int order){
		if(pushOrder<3 && !whom.immobile)pushOrder++;else return;
		whom.locChange.x = locChange.x;
		whom.locChange.y = locChange.y;
		whom.move();
	}
	/**
	 * Changes the speed of the actor by a flat amount, best used for non-percentage based speed modifiers.
	 * @param speedChange the amount by which the speed is to be modified
	 */
	public void changeBaseSpeed(double speedChange){
		baseSpeed += speedChange;
	}
	public void addToSpeedModifier(double d){
		speedModifier += d;
	}
	/**
	 * Attempts to make the passed in speed modifier the unique speed modifier. If no current speed modifier is present then it accepts the parameter as
	 * the unique speed modifier, otherwise only accepts the parameter if it is greater than the current unique speed modifier.
	 * @param usm a percentage based modifier in the form of a decimal (12% to 0.12),<br>. 0 will not have any effect, -1 will reset the modifier.
	 */
	public void setUniqueSpeedModifier(double usm){
		if(usm > uniqueSpeedModifier)uniqueSpeedModifier = 1 + usm;
		else if(usm == -1)uniqueSpeedModifier = 1;
	}
	/**
	 * Gets the apparent visual speed of this actor. This is the format in which all speed values should be presented to the user.
	 * @return the speed of the actor in units/second (pixels/second)
	 */
	public double getSpeed(){
		double speed = baseSpeed * speedModifier * uniqueSpeedModifier;
		if(speed <= 522 && speed >= 10);
		else if(speed > 522)speed = 522;
		else if(speed < 10)speed = 10;
		return speed;
	}
	/**
	 * Returns the distance that is covered by this actor in one tick.
	 * @return actor's speed in units/tick
	 */
	protected double getTrueSpeed(){
		double speed = getSpeed() / engine.tickRate / globalMoveSpeedModifier;
		return speed;
	}
	/**
	 * Sets this actor's target location to a copy of the one passed in
	 * <p>
	 * Does not change any at all movement related actor properties with the exception of setting need to move to false 
	 * if the passed in target location is null.
	 * <p>
	 * If the actor does not think that it needs to move to the target location, it will remain so and visa versa; to make
	 * the actor want to move as well use goTo(Location) instead
	 * 
	 * @param target the new target location
	 */
	public void setTarget(Location target){
		this.target = target!=null?new Location(target):null;
		if(this.target == null){
			needToMove = false;
		}
	}
	public void setHomingTarget(Location homingTarget){
		target = homingTarget;
	}
	/**
	 * Changes the target location of the actor to a copy of the one passed in as well as modifies actor
	 * properties to ready it up for movement
	 * @param setTarget the new target location
	 */
	public void goTo(Location setTarget){
		needToMove = true;
		setTarget(setTarget);
		setMovement(target);
	}
	/**
	 * Checks if this actor's target location is within its bounds (if the actor is touching its target)
	 * @return a boolean value that shows whether or not this is true
	 */
	protected boolean reachedTarget(){
		return getLocation().x < target.x + getWidth() / 2
		&& getLocation().x > target.x - getWidth() / 2
		&& getLocation().y < target.y + getHeight() / 2
		&& getLocation().y > target.y - getHeight() / 2;
	}
	private boolean atTarget(){
		return getLocation().equals(target);
	}
	/**
	 * <b>copy</b>
	 */
	public Actor copy(){
		Actor a = new Actor(engine);
		copyTo(a);		
		return a;
	}
	protected void copyTo(Actor a){
		super.copyTo(a);
		a.target = new Location(target);
		a.locChange = new Location(locChange);
		a.needToMove = needToMove;
		a.baseSpeed = baseSpeed;
		a.speedModifier = speedModifier;
		a.uniqueSpeedModifier = uniqueSpeedModifier;
		a.childSetsOwnMovement = childSetsOwnMovement;
		a.homingTarget = homingTarget;
		a.movementType = movementType;
	}
	@Override
	public void rightClick(Location locationOnScreen, StandardCamera camera) {
		setTarget(Location.getLocationInWorld(locationOnScreen, camera));
	}
	@Override
	public void leftClick(Location locationOnScreen, StandardCamera camera) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPress(String key) {
		// TODO Auto-generated method stub
		
	}
	private boolean anythingInTheWay() {
		boolean selfCheck, collisionCheck;
		for(VisibleObject a : engine.qt.retrieveBox(getLocation().x - getWidth() * 2,getLocation().y - getHeight() * 2,getLocation().x + getWidth() * 2,getLocation().y + getHeight() * 2)){
			selfCheck = a != this;
			collisionCheck = engine.util.checkForCollision(this, a);
			if(a.active && a.solid && selfCheck && collisionCheck)
				return true;
		}
		return false;
	}
	private boolean anythingInTheWay(double distanceCheck){
		for(Actor a : engine.allActors){
			if(a.active && a.solid && a!= this && a.getLocation() != target)
				for(int i = 0; i < distanceCheck; i++)if(engine.util.checkForCollision(this, a) || 
			(engine.util.checkPointCollision(a, new Location(getLocation().x + i * locChange.x, getLocation().y + i * locChange.y)) 
			)){
				return true;
			}
		}
		return false;
	}
	private void moveToALocation(){
		/*
		 * Store the current location of this object to be able to check if this actor moved
		 * after the first attempt at movement
		 */
		Location old = new Location(getLocation());
		int rotation = 0;
		//if closer to target than own speed teleport there
		if(engine.util.getRealCentralDistance(this, target) < getTrueSpeed()){
			System.out.println("test");
			setLocation(target);
			return;
		}
		//if farther:
		else do{
			//first attempt at movement
			if(move())return;//if succeeded then return
			//if failed then path
			double angle;
			angle = locChange.y > 0 || (locChange.y == 0 && locChange.x > 0) ?
					Math.acos(locChange.x / getTrueSpeed()) : 2 * Math.PI - Math.acos(locChange.x / getTrueSpeed());
					setMovement(new Location(getLocation().x + Math.cos(angle + Math.PI / maxRotation * rotation) * getTrueSpeed(),
					getLocation().y + Math.sin(angle + Math.PI / maxRotation * rotation) * getTrueSpeed()));
					move();
		}while(old.x == getLocation().x && old.y == getLocation().y && ++rotation < maxRotation / 2);
		
	}
	private void moveInADirection(){
		move();
	}
	private void home(){
		setMovement(target);
		moveToALocation();
	}
}
