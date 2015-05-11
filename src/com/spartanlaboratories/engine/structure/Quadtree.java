package com.spartanlaboratories.engine.structure;

import java.util.ArrayList;

import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.VisibleObject;

/**<b> One of the most important classes in the entire engine: The Quadtree</b>
 * <p>
 * This object is meant to store all the existing game object and their locations. Then it is able to 
 * retrieve a list of those that are within a certain area. This is crucial for many parts of the engine
 * that iterate on lists of actor as they are no longer required to check the entire list of everything that
 * currently exists. Massive decrease in time wasted on iteration by classes such as actor because the pathing
 * will now only check for nearby actors. Most optimal retrieval time achievable is log4(N) where N is the 
 * number of object in the quadtree and by extension the time for iteration on the entire list.
 * @author Spartak
 * @version Pre-A
 *
 * @param <Number> A Type of Number
 * @param <Element> The type of class that this quadtree will be containing
 */
final public class Quadtree<Number extends Comparable, Element> extends StructureObject{
	private Node root;
	Quadtree(Engine engine){
		super(engine);
	}
	private class Node {
		Number x, y;
		Node northWest, northEast, southEast, southWest;
		Element element;
		
		Node(Number x, Number y, Element element) {
			this.x = x;
			this.y = y;
			this.element = element;
		}
	}
	
	//************************* insertion ******************************
	void insert(Number x, Number y, Element element) {
		root = insert(root, x, y, element);
	}
	
	private Node insert(Node node, Number x, Number y, Element element) {
		if(node == null) return new Node(x, y, element);
		
		if(node.element == null) {
			node.element = element;
			return node;
		}
		
		boolean lessX = x.compareTo(node.x) < 0;
		boolean lessY = y.compareTo(node.y) < 0;
		
			 if( lessX && !lessY) node.northWest = insert(node.northWest, x, y, element);
		else if(!lessX && !lessY) node.northEast = insert(node.northEast, x, y, element);
		else if(!lessX &&  lessY) node.southEast = insert(node.southEast, x, y, element);
		else if( lessX &&  lessY) node.southWest = insert(node.southWest, x, y, element);
		return node;
	}
	
	//************************* retrieval ******************************
	public ArrayList<Element> retrieveBox(Number minX, Number minY, Number maxX, Number maxY) {
		ArrayList<Element> arrayList = new ArrayList<Element>();
		retrieveBox(root, minX, minY, maxX, maxY, arrayList);
		return arrayList;
	}
	
	@SuppressWarnings("unchecked")
	private void retrieveBox(Node node, Number minX, Number minY, Number maxX, Number maxY, ArrayList<Element> arrayList) {
		if(node == null) return;
		
		boolean lessMinX = minX.compareTo(node.x) < 0;
		boolean lessMinY = minY.compareTo(node.y) < 0;
		boolean lessMaxX = maxX.compareTo(node.x) < 0;
		boolean lessMaxY = maxY.compareTo(node.y) < 0;
		
		if(lessMinX && lessMinY && !lessMaxX && !lessMaxY && node.element != null) arrayList.add(node.element);
		if( lessMinX && !lessMaxY) retrieveBox(node.northWest, minX, minY, maxX, maxY, arrayList);
		if(!lessMaxX && !lessMaxY) retrieveBox(node.northEast, minX, minY, maxX, maxY, arrayList);
		if(!lessMaxX &&  lessMinY) retrieveBox(node.southEast, minX, minY, maxX, maxY, arrayList);
		if( lessMinX &&  lessMinY) retrieveBox(node.southWest, minX, minY, maxX, maxY, arrayList);
	}
	
	//************************* deletion ******************************
	public void remove(Number x, Number y, Element element) {
		remove(root, x, y, element);
	}
	private void remove(Node node, Number x, Number y, Element element) {
		if(node == null)return;
		else if(node.element == element){
			node.element = null;
			return;
		}
		
		boolean lessX = x.compareTo(node.x) < 0;
		boolean lessY = y.compareTo(node.y) < 0;
		
			 if( lessX && !lessY) remove(node.northWest, x, y, element);
		else if(!lessX && !lessY) remove(node.northEast, x, y, element);
		else if(!lessX &&  lessY) remove(node.southEast, x, y, element);
		else if( lessX &&  lessY) remove(node.southWest, x, y, element);
	}
	public void clear(){
		root = null;
		if(engine.util.everySecond(1))engine.tracker.log("Inserting " + engine.allActors.size() + " visible objects");
		for(VisibleObject a: engine.visibleObjects)
			engine.qt.insert(a.getLocation().x, a.getLocation().y, a);
	}
	public ArrayList<Alive> getAlivesAroundMe(Alive alive, int area){
		ArrayList<VisibleObject> box = engine.qt.retrieveBox(alive.getLocation().x - area, 
				alive.getLocation().y - area, alive.getLocation().x + area,alive.getLocation().y + area);
		ArrayList<Alive> ala = new ArrayList<Alive>();
		for(VisibleObject a : box)
			if(Alive.class.isAssignableFrom(a.getClass()))
				ala.add((Alive) a);
		return ala;
	}
	public ArrayList<Alive> getAlivesAroundLocation(Location location){
		ArrayList<VisibleObject> ala = engine.qt.retrieveBox(location.x - 100, location.y - 100, location.x + 100, location.y + 100);
		ArrayList<Alive> all = new ArrayList<Alive>();
		for(VisibleObject a:ala)if(Alive.class.isAssignableFrom(a.getClass()))all.add((Alive)a);
		return all;
	}
}
