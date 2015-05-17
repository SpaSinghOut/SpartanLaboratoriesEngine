package com.spartanlaboratories.engine.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;

import com.spartanlaboratories.engine.game.Hero;
import com.spartanlaboratories.engine.game.Hero.HeroType;
import com.spartanlaboratories.engine.structure.Engine;

public class HeroChoice extends JMenuItem implements MouseListener{
	Hero.HeroType represents;
	Gui owner;
	public HeroChoice(Hero.HeroType setHeroType, Gui gui){
		setText(setHeroType.toString());
		represents = setHeroType;
		addMouseListener(this);
		owner = gui;
	}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0){}
	public void mousePressed(MouseEvent arg0) {
		owner.owner.getHero().heroType = represents;
		owner.getLayeredPane().remove(owner.menuBar);
		owner.engine.out(owner.owner + " picked: " + represents);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
