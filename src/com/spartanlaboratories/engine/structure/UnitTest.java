package com.spartanlaboratories.engine.structure;

public class UnitTest extends Map{

	public UnitTest(Engine engine) {
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
		
	}

	@Override
	protected void drawBorder() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args){
		Engine engine = new Engine();
		engine.typeHandler.newEntry("map", new UnitTest(engine));
		engine.init();
		engine.start();
	}
}
