package com.spartanlaboratories.engine.game;

import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Human;


public class ItemList{
	private int[] stats = {Constants.bonusDamage, Constants.health, Constants.mana, 
	Constants.manaRegen, Constants.healthRegen, Constants.armor, Constants.attackSpeed,};
	private Item[] items;
	Type type;
	Alive owner;
	ItemList(int setSize, Type setType, Alive setOwner){
		items = new Item[setSize];
		type = setType;
		owner = setOwner;
	}
	enum Type{
		INVENTORY, STASH, NOATTS, NOSTATS,;
	}
	public void addItem(int slot, Item item){
		if(items[slot - 1] == null)	{
			items[slot - 1] = item;
		}
		else {
			((Human)((Hero)owner).owner).gui.out("Slot number: " + slot + " is full. Attemting to add to first free slot");
			if(addIntoNextFirstSlot(item) == 0)((Human)((Hero)owner).owner).gui.out("Unable to add item to inventory. Inventory is full");
			return;
		}
		switch(type){
		case STASH:
			break;
		case NOSTATS:
			break;
		case NOATTS:
			for(int i: stats){
				owner.changeStat(i, item.stats[i]);
			}
			break;
		case INVENTORY:
			for(int i = 0; i < Constants.statsSize; i++)owner.changeStat(i, item.stats[i]);
			break;
		}
		((Human)((Hero)owner).owner).gui.out("added item into slot: " + slot);
	}
	public void removeItem(int slot){
		slot--;
		switch(type){
		case STASH:
			break;
		case NOSTATS:
			break;
		case NOATTS:
			for(int i: stats){
				owner.changeStat(i, -1 * items[slot].stats[i]);
			}
			break;
		case INVENTORY:
			for(int i = 0; i < Constants.statsSize; i++)owner.changeStat(i, -1 * items[slot].stats[i]);
			break;
		}
		items[slot] = null;
	}
	public Item getItemInSlot(int itemSlot){
		return items[itemSlot];
	}
	public int size(){
		return items.length;
	}
	private int addIntoNextFirstSlot(Item item){
		/*Attempts to add the passed item into the first available inventory slot
		 * 	if successful returns the slot number, if not returns 0
		 */
		for(int i = 0; i < items.length; i++)if(items[i] == null){
			addItem(i + 1, item);
			return i;
		}
		return 0;
	}
	public boolean isFull(){
		for(Item i: items)if(i == null)return false;return true;
	}
}
	
