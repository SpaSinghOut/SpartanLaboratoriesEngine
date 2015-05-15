package com.spartanlaboratories.engine.structure;

import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.structure.Util.NullColorException;

class UnitTest extends Map{

	private UnitTest(Engine engine) {
		super(engine);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeSpawnPoints() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		Human human = new Human(engine, Alive.Faction.RADIANT);
		Alive unit = new Alive(human.engine, human.faction);
		unit.setLocation(unit.getLocation());
		unit.setWidth(20);
		unit.setHeight(20);
		unit.color = Util.Color.WHITE;
		human.addUnit(unit);
		human.getPrimaryCamera().worldLocation.duplicate(unit.getLocation());
		
		Console console = human.gui.console;
		console.showLocationOf(unit);
		console.showLocation(human.getPrimaryCamera().worldLocation);
		
	}
	/*@Override
	public void drawMap(Camera camera){
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
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args){
		Engine engine = new Engine();
		engine.typeHandler.newEntry("map", new UnitTest(engine));
		engine.init();
		engine.start();
	}
}
