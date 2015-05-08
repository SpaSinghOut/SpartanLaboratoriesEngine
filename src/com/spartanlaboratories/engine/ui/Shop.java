package com.spartanlaboratories.engine.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Item;
import com.spartanlaboratories.engine.structure.Constants;

final class Shop<Element extends Item> extends JMenuBar {
	private ArrayList<JMenu> menues = new ArrayList<JMenu>();
	Gui parent;
	Shop(Gui parent){
		this.parent = parent;
		parent.add(this);
	}
	public void newShopMenu(String menuName){
		JMenu m = new JMenu(menuName);
		add(m);
		menues.add(m);
	}
	
	public void newShopItem(String menuName, Element item){
		for(JMenu m: menues)if(m.getText() == menuName)m.add(new ShopIcon<Element>(parent,item));
	}
	public class ShopIcon<Element extends Item> extends JMenuItem implements MouseListener{
		Item represents;
		Gui parent;
		public ShopIcon(Gui gui, Element element){
			super(element.itemName);
			parent = gui;
			addMouseListener(this);
			represents = element;
		}
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {
			Alive a = (Alive)parent.owner.selectedUnit;
			if(a.getStat(Constants.gold) > represents.cost){
				a.inventory.addItem(1, represents);
				a.changeStat(Constants.gold, -represents.cost);
			} 
		}
	}

}
