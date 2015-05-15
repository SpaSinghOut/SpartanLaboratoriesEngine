package com.spartanlaboratories.engine.game;

import java.util.ArrayList;

public class Aura<Element extends Buff> extends GameObject{
	Alive holder;
	double radius;
	Element toBePlaced;
	AffectedUnits affectedUnits;
	int intensity;
	public static ArrayList<Aura> auras = new ArrayList<Aura>();
	public Aura(Alive setHolder, double setRadius, Element setTBP, AffectedUnits au, int setIntensity){
		super(setHolder.engine);
		holder = setHolder;
		radius = setRadius;
		toBePlaced = setTBP;
		affectedUnits = au;
		auras.add(this);
		intensity = setIntensity;
	}
	enum AffectedUnits{
		ALL, HEROES, RANGEDHEROES, MELEEHEROES, RANGEDUNITS, MELEEUNITS,;
	}
	public boolean tick(){
		ArrayList<Alive> candidates = engine.qt.getAlivesAroundMe(holder, (int)radius);
		for(Alive a: sortThroughCandidates(candidates))
		if(engine.util.everySecond(2))
			nonGenericAdjustments(toBePlaced);
		return active;
	}
	private ArrayList<Alive> sortThroughCandidates(ArrayList<Alive> candidates){
		ArrayList<Alive> accepted = new ArrayList<Alive>();
		switch(affectedUnits){
		case ALL:
			accepted = candidates;
			break;
		case HEROES:
			for(Alive a: candidates)if(a.getClass() == Hero.class)accepted.add(a);
			break;
		case RANGEDHEROES:
			for(Alive a: candidates)
				if(a.getClass() == Hero.class && ((Hero)a).missile)
					accepted.add(a);
		default:
			accepted = candidates;	
			break;
		}
		return accepted;
	}
	private void nonGenericAdjustments(Buff buff){
		
	}
	@Override
	public GameObject copy() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void updateComponentLocation() {
		// TODO Auto-generated method stub
		
	}
	public void update() {}
}
