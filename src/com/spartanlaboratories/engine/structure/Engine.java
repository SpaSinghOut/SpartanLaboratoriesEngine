 package com.spartanlaboratories.engine.structure;

import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

import com.spartanlaboratories.engine.game.Actor;
import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Aura;
import com.spartanlaboratories.engine.game.Buff;
import com.spartanlaboratories.engine.game.Creep;
import com.spartanlaboratories.engine.game.Effect;
import com.spartanlaboratories.engine.game.GameObject;
import com.spartanlaboratories.engine.game.Missile;
import com.spartanlaboratories.engine.game.Tower;
import com.spartanlaboratories.engine.game.VisibleObject;

/**
 * The Spartan Laboratories Game Engine
 * @author Spartak
 *
 */
public class Engine{
	/**
	 * Contols whether or not the program is running. Does NOT pause the program when set to false, instead it will terminate it, for the pausing function
	 * refer to {@link #pause}
	 * Manual modification of this is best  avoided as even if this was used to terminate the program it would not terminate correctly.
	 */
	public boolean running;
	public boolean concurrency;
	public final int heroPickSecondDelay = 0;
	public ArrayList<Controller> controllers = new ArrayList<Controller>();
	public ArrayList<Missile> missiles = new ArrayList<Missile>();
	public ArrayList<Actor> allActors = new ArrayList<Actor>();
	public Map map; //DO NOT INITIALIZE HERE, will not work due to opengl context requirement
	public int tickCount;
	/**
	 * One of the variables that controls the state of execution. Setting this to true would 
	 * "pause" execution, stopping the ticks of all game objects but continuing the rendering. Pressing the p key will pause as well as prompt the user
	 * that execution is paused.
	 */
	public boolean pause;
	public Quadtree<Double, VisibleObject>  qt = new Quadtree<Double, VisibleObject>(this);
	public Tracker tracker = new Tracker(this);
	public Util util = new Util(this);
	/** The rate at which the game updates in updates/second 
	 * */
	public static int tickRate;
	public ArrayList<VisibleObject> visibleObjects = new ArrayList<VisibleObject>();
	public TypeHandler<StructureObject> typeHandler;
	public Thread logic;
	
	private int xDisplay;
	private int yDisplay;
	private final Location wrap = new Location(3000,2000);
	private ArrayList<GameObject> deleteThis = new ArrayList<GameObject>();
	private final ResolutionMode resolutionMode = ResolutionMode.SCAN;
	static long time;
	private double initializationProgress;
	/**
	 * <h1>Creates an engine</h1>
	 * <p>
	 * Creates an engine
	 */
	public Engine(){
		typeHandler = new TypeHandler<StructureObject>();
		running = true;
		switch(resolutionMode){
		case DEFAULT:
			setResolutionToDefault();
			break;
		case CUSTOM:
			setCustomResolution();
			break;
		case SCAN:
			scanAndSetResolution();
			break;
		}
		try {
			Display.setDisplayMode(new DisplayMode((int)xDisplay,(int)yDisplay));
			Display.create();
			Display.setVSyncEnabled(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			GL11.glEnable(GL11.GL_BLEND);
        	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, (int)xDisplay, (int)yDisplay, 0, 1, -1);
		} catch (LWJGLException e) {
		} 
	}
	class InitializationThread implements Runnable{
		public void run(){
			
		}
	}
	class RunThread implements Runnable{
		int x;
		RunThread(int x){
			this.x = x;
		}
		public void run(){
			if(x==1){
				while(true)
				if(System.nanoTime() > time + 1000000000 / Engine.tickRate){
					time += 1000000000 / Engine.tickRate;
					if(!pause)tick();
				}
			}
			else {
				while(true)
				if(System.nanoTime() > time + 1000000000 / Engine.tickRate){
					time += 1000000000 / Engine.tickRate;
					render();
				}
			}
		}
	}
	enum ResolutionMode{
		DEFAULT, CUSTOM, SCAN,;
	}
	public class TypeHandler<Type extends StructureObject>{
		private HashMap<String, StructureObject> typeGetter = new HashMap<String,StructureObject>();
		public void newEntry(String string, Type type){
			typeGetter.put(string, type);
		}
		public StructureObject getEntry(String string){
			return typeGetter.get(string);
		}
	}
	public void init(){
		tickRate = 60;
		tracker = new Tracker(this);
		tracker.initialize();
		SLEXMLException.engine = this;
		map = (Map) typeHandler.typeGetter.get("map");
		map.init();
		tickCount = 0;
		
		time = System.nanoTime();
	}
	public void start(){
		run();
		tracker.closeWriter();
		Display.destroy();
		((Human)controllers.get(0)).gui.dispatchEvent(new WindowEvent(((Human)controllers.get(0)).gui, WindowEvent.WINDOW_CLOSING));
	}
	private void setResolutionToDefault(){
		xDisplay = 1920;
		yDisplay = 1080;
	}
	private void setCustomResolution(){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Put in the width of the screen:");
		xDisplay = scanner.nextInt();
		System.out.println("Put in the height of the screen:");
		yDisplay = scanner.nextInt();
		scanner.close();
	}
	private void scanAndSetResolution(){
		xDisplay = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
		yDisplay = (int)(0.98f * GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight());
	}
	private void run(){
		if(concurrency){
			logic = new Thread(new RunThread(1));
			logic.start();
		}
		//new Thread(new RunThread(2)).start();
		
		while(running)
		if(System.nanoTime() > time + 1000000000 / Engine.tickRate){
			time += 1000000000 / Engine.tickRate;
			if(!concurrency && tickCount++ > Engine.tickRate * heroPickSecondDelay)
				if(!pause)tick();
			render();
		}
		
	}
	private void tick(){
		if(tracker.trackedEntities[Tracker.FUNC_TICK])tracker.giveStartTime(Tracker.FUNC_TICK);
		if(tracker.trackedEntities[Tracker.FUNC_QUADTREE_RESET])tracker.giveStartTime(Tracker.FUNC_QUADTREE_RESET);
		qt.clear();
		if(tracker.trackedEntities[Tracker.FUNC_QUADTREE_RESET])tracker.giveEndTime(Tracker.FUNC_QUADTREE_RESET);
		if(tracker.trackedEntities[Tracker.FUNC_HERO_OWNER_TICK])tracker.giveStartTime(Tracker.FUNC_HERO_OWNER_TICK);
		for(Controller heroOwner: controllers)heroOwner.tick();
		if(tracker.trackedEntities[Tracker.FUNC_HERO_OWNER_TICK])tracker.giveEndTime(Tracker.FUNC_HERO_OWNER_TICK);
		if(tracker.trackedEntities[Tracker.FUNC_MAP_TICK])tracker.giveStartTime(Tracker.FUNC_MAP_TICK);
		map.tick();
		if(tracker.trackedEntities[Tracker.FUNC_MAP_TICK])tracker.giveEndTime(Tracker.FUNC_MAP_TICK);
		if(tracker.trackedEntities[Tracker.FUNC_MISSILE_TICK])tracker.giveStartTime(Tracker.FUNC_MISSILE_TICK);
		tickMissiles();
		if(tracker.trackedEntities[Tracker.FUNC_MISSILE_TICK])tracker.giveEndTime(Tracker.FUNC_MISSILE_TICK);
		if(tracker.trackedEntities[Tracker.FUNC_AURA_TICK])tracker.giveStartTime(Tracker.FUNC_AURA_TICK);
		tickAuras();
		if(tracker.trackedEntities[Tracker.FUNC_AURA_TICK])tracker.giveEndTime(Tracker.FUNC_AURA_TICK);
		if(tracker.trackedEntities[Tracker.FUNC_ACTOR_DELETION])tracker.giveStartTime(Tracker.FUNC_ACTOR_DELETION);
		deleteStuff();
		if(tracker.trackedEntities[Tracker.FUNC_ACTOR_DELETION])tracker.giveEndTime(Tracker.FUNC_ACTOR_DELETION);
		try {
			tracker.tick();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(tracker.trackedEntities[Tracker.FUNC_TICK])tracker.giveEndTime(Tracker.FUNC_TICK);
	}	
	private void render(){
		//System.out.println("render test");
		if(tracker.trackedEntities[Tracker.FUNC_RENDER])tracker.giveStartTime(Tracker.FUNC_RENDER);
		for(Controller heroOwner:controllers)if(Human.class.isAssignableFrom(heroOwner.getClass()))render(((Human)heroOwner));
		if(tracker.trackedEntities[Tracker.FUNC_RENDER])tracker.giveEndTime(Tracker.FUNC_RENDER);
	}
	
	private void render(Human player){
		try {
			Display.setParent(player.gui.canvas);
		} catch (LWJGLException e1) {
			e1.printStackTrace();
		}
		GL11.glClearColor(0.0f,0.0f,0.0f,0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		tracker.giveStartTime(Tracker.REND_MISSILE);
		drawSpells(player.getPrimaryCamera());
		tracker.giveEndTime(Tracker.REND_MISSILE);
		tracker.giveStartTime(Tracker.REND_MAP);
		map.drawMap(player.getPrimaryCamera());
		tracker.giveEndTime(Tracker.REND_MAP);
		tracker.giveStartTime(Tracker.REND_HUMAN);
		player.drawMe(player.getPrimaryCamera());
		tracker.giveEndTime(Tracker.REND_HUMAN);
		Display.update();
	}
	private void tickMissiles(){
		for(Missile spell: missiles){
			if(!spell.tick())addToDeleteList(spell);
		}
	}
	private void tickAuras(){
		for(Aura a: Aura.auras)a.tick();
	}
	private void drawSpells(Camera camera){
		for(Missile missile : missiles){
			util.drawActor(missile, missile.color, camera);	
		}
	}
	public Location getScreenDimensions(){
		return new Location(xDisplay, yDisplay);
	}
	static double getRandom(){
		return Math.random();
	}
	public Location getWrap(){
		return wrap;
	}
	public ArrayList<Missile> getSpells(){
		return missiles;
	}
	private void deleteStuff(){
		for(GameObject a: deleteThis){
			if(a.getClass() == Missile.class && missiles.contains(a))
				missiles.remove(a);
			else if(a.getClass() == Buff.class)((Buff)(a)).owner.getBuffs().remove(a);
			else if(a.getClass() == Creep.class)Creep.allCreeps.remove(a);
			else if(a.getClass() == Effect.class);
			else if(a.getClass() == Tower.class);
			if(Alive.class.isAssignableFrom(a.getClass()))Alive.allAlives.remove(a);
			if(Actor.class.isAssignableFrom(a.getClass()))allActors.remove(a);
			if(VisibleObject.class.isAssignableFrom(a.getClass()))visibleObjects.remove(a);
		}
		deleteThis.clear();
	}
	public ArrayList<GameObject> getDeleteList(){
		return deleteThis;
	}
	public void addToDeleteList(GameObject poorGuy){
		deleteThis.add(poorGuy);
	}
	
	public void out(String string){
		for(Controller p: controllers)if(p.getClass() == Human.class)((Human)(p)).gui.out(string);
	}
}
