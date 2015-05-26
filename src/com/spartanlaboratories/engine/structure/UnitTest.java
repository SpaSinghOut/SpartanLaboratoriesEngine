package com.spartanlaboratories.engine.structure;

import com.spartanlaboratories.engine.game.Ability;
import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Hero;
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
		engine.tracker.initialize(Tracker.TrackerPreset.PRESET_REND_MAP);
		// TODO Auto-generated method stub
		Human human = new Human(engine, Alive.Faction.RADIANT);
		Alive unit = new Hero<Spell>(engine, Hero.HeroType.RAZOR, human);
		unit.setWidth(60);
		unit.setHeight(60);
		unit.changeBaseSpeed(300);
		unit.color = Util.Color.WHITE;
		//unit.setTexture("test.png");
		unit.setLocation(0,0);
		human.addUnit(unit);
		human.getPrimaryCamera().worldLocation.setCoords(0,0);
		
		Console console = human.gui.console;
		console.showLocationOf(unit);
		console.showLocation(human.getPrimaryCamera().worldLocation);
		
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
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
	
}
