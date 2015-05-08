package com.spartanlaboratories.engine.ai;

import java.util.ArrayList;

import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Hero;
import com.spartanlaboratories.engine.game.Alive.Faction;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Controller;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Location;

/**
 * Don't use.
 * @author spart_000
 *
 */
public class AI extends Controller{
	int abilityPoint;
	int[] levellingList;
	ActionQueue actionQueue = new ActionQueue();
	public AI(Engine engine, Faction setFaction) {
		super(engine, setFaction);
		getHero().heroType = Hero.HeroType.RAZOR;
		getHero().initHeroType(getHero().heroType);
		initAbilityLevellingList();
		int[] locations = {2500, 800, 2500,1200,2500,1000};
		for(int i = 0; i < locations.length / 2; i++){
			int[] subLocation = {locations[i * 2], locations[i * 2 + 1]};
			actionQueue.add(new AIAction(this, AIAction.ActionName.MOVETO, subLocation));
		}
		actionQueue.add(new AIAction(this, AIAction.ActionName.FINDCREEPS, new int[0]));
		actionQueue.add(new AIAction(this, AIAction.ActionName.WATCH, new int[0]));
	}
	public void tick(){
		super.tick();
		if(getHero().getStat(Constants.abilityPoints) > 0){
			engine.out(String.valueOf(getHero().getStat(Constants.abilityPoints)));
			getHero().abilities.get(getAbilityToLevel()).levelAbility();
		}
		if(actionQueue.size() > 0)actionQueue.get(0).tick();
		else{
			ArrayList<Alive> pat = engine.qt.getAlivesAroundMe(this.getHero(), (int)this.getHero().getStat(Constants.visibilityRange));
			for(Alive a: pat){
				if(getHero().faction != a.faction){
					if(!getHero().hasAttackTarget())
						getHero().aggroOn(a);
					else{
						if(engine.util.getRealCentralDistance(getHero(), a) < engine.util.getRealCentralDistance(getHero(), getHero().attackTarget))
						getHero().aggroOn(a);
						else if(!getHero().attackTarget.alive)getHero().aggroOn(a);
					}
					if(a.getClass() == Hero.class){
						selectedUnit = a;
						getHero().castSpell(getHero().abilities.get(1));
					}
				}
			}
		}
	}
	private int getAbilityToLevel(){
		return levellingList[abilityPoint++];
	}
	private void initAbilityLevellingList(){
		levellingList = new int[12];
		levellingList[0] = 1;
		levellingList[1] = 0;
		levellingList[2] = 1;
		levellingList[3] = 0;
		levellingList[4] = 1;
		levellingList[5] = 3;
		levellingList[6] = 1;
		levellingList[7] = 0;
		levellingList[8] = 2;
		levellingList[9] = 0;
		levellingList[10] = 3;
		levellingList[11] = 2;
	}
	public void respawn(){
		super.respawn();
		getHero().setTarget(new Location(500, 1000));
		engine.out(String.valueOf(getHero().getPermissions(Constants.movementAllowed)));
	}
}
