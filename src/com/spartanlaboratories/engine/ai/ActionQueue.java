package com.spartanlaboratories.engine.ai;

import java.util.ArrayList;
/**
 * The engine's ai based extension of the java standard library's utility object ArrayList
 * @author Spartak
 */
public class ActionQueue extends ArrayList<AIAction> {
	/**
	 * @serialField
	 */
	private static final long serialVersionUID = 8010768973496375876L;
	/**
	 * Insert the passed in <a href="AIAction.html">AIAction</a> at the specified index.
	 * @param index the place in the ActionQueue at which the action is to be placed.
	 * @param action the action that is being inserted at the specified index.
	 */
	public void insert(int index, AIAction action){
		int size = size();
		add(action);
		for(int i = size; i > index; i--){
			set(i, get(i - 1));
		}
		set(index, action);
	}
	/**
	 * Returns a boolean values designating the presence of any at all <a href="AIAction.html">AIActions</a> within this ActionQueue.
	 * @return <b>true</b> if this action queue contains one or more <a href="AIAction.html">AIActions</a>, <br>
	 * <b>false</b> if this action queue is completely empty
	 */
	public boolean containsActions(){
		return !isEmpty();
	}
	/**
	 * Removes the passed in <a href="AIAction.html">AIAction</a> from this action queue
	 * @param action the <a href="AIAction.html">AIAction</a> that is to be removed from this action queue
	 */
	public void completeAction(AIAction action){
		remove(action);
	}
	/**
	 * Returns whether or not this action queue contains an <a href="AIAction.html">AIAction</a> of a certain type
	 * @param actionName the type of <a href="AIAction.html">AIAction</a> that this action queue is being searched for
	 * @return a boolean value which represents the presence of the specified type of <a href="AIAction.html">AIAction</a> in this action queue<br>
	 * <b>true</b> if this action queue does contain elements of the specified type<br>
	 * <b>false</b> if this action queue does not contain any elements of the specified type
	 */
	public boolean containsType(AIAction.ActionName actionName){
		for(AIAction action: this){
			if(((AIAction)action).actionName == actionName)return true;
		}
		return false;
	}
}
