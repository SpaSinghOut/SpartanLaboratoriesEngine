
package com.spartanlaboratories.engine.game;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.spartanlaboratories.engine.structure.StandardCamera;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Util;
import com.spartanlaboratories.engine.structure.Util.NullColorException;
import com.spartanlaboratories.engine.util.Location;
import com.spartanlaboratories.engine.util.Rectangle;

public class VisibleObject extends GameObject{
	public Shape shape;
	private Util.Color color;
	public Util.Color defaultColor;
	public boolean solid;
	public boolean immobile;
	public boolean resetTexture;
	private double height;
	private double width;
	private Texture texture;
	private final Rectangle areaCovered = new Rectangle(new Location(), 0, 0);
	ArrayList<Effect> effects = new ArrayList<Effect>(); // Maybe unused
	private TextureInfo textureInfo = new TextureInfo();
	public enum Shape{
		QUAD, TRI,;
	}
	private class TextureInfo{
		boolean updateNeeded;
		String textureFormat;
		String namePath;
	}
	public VisibleObject(Engine engine){
		super(engine);
		engine.visibleObjects.add(this);
		shape = Shape.QUAD;
		textureInfo = new TextureInfo();
	}
	@Override
	public boolean tick(){
		if(textureInfo.updateNeeded || resetTexture)updateTexture();
		return super.tick();
	}
	/**
	 * Performs actions that should be taken every time the engine is updated. Does nothing at this
	 * level in the game object tree and is meant to be overridden by subclasses that wish to perform 
	 * some sort of action every time the game updates. It is preferable that this method is the one
	 * that is being overriden by subclasses rather than {@link #tick()} which should be left to the 
	 * engine.
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	/** Returns the width of this object. 
	 * 
	 * @return -the width of this object.
	 * @see #setWidth(double)
	 * @see #getHeight()
	 */
	public double getWidth() {
		return width;
	}
	/**Sets the width value of this object. The width value cannot be accessed directly and must be 
	 * changed through this function. It is important that the width of a new {@link VisibleObject}
	 * is set because otherwise it will default to a value of 0 and cause the entire object to not 
	 * be visible.
	 * 
	 * @param width - The new width of this {@link VisibleObject}
	 * @see #getWidth()
	 * @see #setHeight(double)
	 */
	public void setWidth(double width) {
		this.width = width;
		areaCovered.setSize(new Location(width, height));
	}
	/**
	 * Returns the height value of this {@link VisibleObject}
	 * @return the height value of this {@link VisibleObject}
	 * @see #setHeight(double)
	 * @see #getWidth()
	 */
	public double getHeight() {
		return height;
	}
	public Rectangle getAreaCovered(){
		return areaCovered.copy();
	}
	/** Sets the height value of this object. The height value cannot be accessed directly and must be 
	 * changed through this function. It is important that the height of a new {@link VisibleObject}
	 * is set because otherwise it will default to a value of 0 and cause the entire object to not 
	 * be visible.
	 * 
	 * @param height - The new height of this object.
	 * @see #setWidth(double)
	 * @see #getHeight()
	 */
	public void setHeight(double height) {
		this.height = height;
		areaCovered.setSize(new Location(width, height));
	}
	/**
	 * Returns a float array that is generated from this object's color value. If that color value
	 * has not been initialize this method will throw a NullColorException.
	 * 
	 * @return A generated rbg value from this object's color.
	 * @throws Util.NullColorException - If the color of this object is null
	 */
	public float[] getRGB() throws Util.NullColorException{
		if(color == null)throw engine.util.new NullColorException(engine, this);
		return engine.util.getRGB(color);
	}
	public void setTexture(Texture setTexture){
		texture = setTexture;
	}
	/**
	 * Sets this object's texture to a resource that is found by using the information given
	 * by the parameters. The first argument should be the format of the texture (the file extension) and the second parameter should be the full name and
	 * location of the texture file. 
	 * @param format - The format of the texture.
	 * @param pathName - The full location and file name
	 * @return true
	 */
	public boolean setTexture(String format, String pathName){
		textureInfo.updateNeeded = true;
		textureInfo.textureFormat = format;
		textureInfo.namePath = pathName;
		return true;
	}
	/**
	 * Experimental, notify if doesn't work. Attempts to set the texture of this object 
	 * by using the string that was passed in as the location and name of the file.
	 * <p>
	 * Example: if the full name of the file (including the extension) is "test.jpg" and it is inside a folder named "resources" then the string
	 * that should be passed in as an argument when calling this function should be "resources/test.jpg".
	 * @param pathName - A String objects that represents the location and name of the texture.
	 * @return A boolean value that represents whether or not the function succeeded at setting the 
	 * texture.
	 */
	public boolean setTexture(String pathName){
		if(!pathName.contains("."))return false;
		String txt = "";
		for(int i = pathName.indexOf(".");i < pathName.length();)
			txt += pathName.toCharArray()[i++];
		return setTexture(txt, pathName);
	}
	/**
	 * Returns this object's texture value if it has been initialized. If it has not then this method
	 * will return a blank white texture.
	 * 
	 * @return - This object's texture.
	 */
	public Texture getTexture(){
		Texture rTexture = texture;
		try {
			rTexture = texture == null ? TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("res/black.jpg")) : texture;
		} catch (IOException e) {
			System.out.println("getTexture exception");
		}
		return rTexture;
	}
	/**
	 * Returns the exact texture value of this object. Can return a null value if that happens to
	 * be the case.
	 * 
	 * @return - This object's texture.
	 */
	public Texture getTextureNE() {
		return texture;
	}
	/**
	 * Returns a copy of this visible object. Night not work perfectly if this object contains 
	 * instances of other objects as those objects themselves might get referenced and not copied.
	 */
	public VisibleObject copy(){
		VisibleObject vo = new VisibleObject(engine);
		super.copyTo(vo);
		copyTo(vo);
		return vo;
	}
	/**
	 * Returns a duplicate of this objects color. This method is preferable over trying to copy the 
	 * color value of an object as this method will return a value that is not a reference to this 
	 * object's color.
	 * 
	 * @return A new color value that is the same as the color of this object. White by default.
	 * @see #color 
	 * @see #defaultColor
	 */
	public Util.Color duplicateColor(){
		switch(color){
		case BLACK:
			return Util.Color.BLACK;
		case BLUE:
			return Util.Color.BLUE;
		case GRAY:
			return Util.Color.GRAY;
		case GREEN:
			return Util.Color.GREEN;
		case LIGHTBLUE:
			return Util.Color.LIGHTBLUE;
		case ORANGE:
			return Util.Color.ORANGE;
		case PINK:
			return Util.Color.PINK;
		case PURPLE:
			return Util.Color.PURPLE;
		case RED:
			return Util.Color.RED;
		case WHITE:
			return Util.Color.WHITE;
		case YELLOW:
			return Util.Color.YELLOW;
		default:
			return Util.Color.WHITE;
		}
	}
	/** 
	 * Changes the color of this object back to its default value.
	 * 
	 * @see #color
	 * @see #defaultColor
	 */
	public void resetColor(){
		color = defaultColor;
	}
	public Util.Color getColor(){
		return color != null ? color : Util.Color.WHITE;
	}
	public void setColor(Util.Color color){
		this.color = color;
	}
	/**
	 * Draws this object as viewed by the given camera. Will return true only if the object is both
	 * active and can be seen by the camera (so if it is actually drawn.
	 * 
	 * @deprecated
	 * @param camera - The camera that will be viewing this object.
	 * @return - A boolean value that signifies the success of the drawing 
	 * @throws Util.NullColorException - if the color of this object is null
	 * 
	 */
	public boolean drawMe (StandardCamera camera) throws Util.NullColorException{
		return !active?active:drawMe(camera, getRGB());
	}
	/**
	 * Draws this object at a slightly lower level access to other object drawing functions. (Meaning
	 * it directly accesses the drawing functions in the Util class). This method generally should not be
	 * called from subclasses as this is already called by the VisibleObject class, instead use: 
	 * {@link #drawMe(StandardCamera)}. This can be 
	 * overwritten but would mean that the user has to deal with the Util class drawing function. It 
	 * is suggested that overrides of this method call their super.
	 * <p>
	 * This method is deprecated! No longer does any visible object or any of its components have to be explicitly drawn.
	 * The method will remain here for a few versions just in case the new system works imperfectly and
	 * needs to be augmented by explicit draw function calls.
	 * 
	 * @deprecated
	 * @param camera - The {@link StandardCamera} that is viewing this object.
	 * @param RGB - The RBG values that designate the color of this object.
	 * @return A boolean that designates if the drawing of this object was successful.
	 */
	protected boolean drawMe(StandardCamera camera, float[] RGB){
		if(textureInfo.updateNeeded || resetTexture)updateTexture();
		if(camera.canSeeObject(this)){
			engine.util.drawVO(this, RGB, camera);
			return true;
		}
		return false;
	}
	protected void copyTo(VisibleObject vo){
		super.copyTo(vo);
		vo.height = height;
		vo.shape = shape;
		vo.width = width;
		vo.texture = texture;
		vo.color = duplicateColor();
		vo.defaultColor = duplicateColor();
		vo.solid = solid;
		vo.immobile = immobile;
		for(Effect e:effects)vo.effects.add(e);
		vo.resetTexture = resetTexture;
	}
	@Override
	protected void updateComponentLocation(){
		areaCovered.setCenter(getLocation());
	}
	protected void setTexture() throws IOException{
		if(this.getClass() == Hero.class){
			String heroNameString = ((Hero)this).heroType.toString().toLowerCase();
			if(heroNameString != "none")
				setTexture(Constants.versionString + "/res/" + heroNameString + ".jpg");
		}
		else if(this.getClass() == Creep.class)
			setTexture(Constants.versionString + "/res/radiant creep.png");
		else if(this.getClass() == Tower.class)
			if(((Tower)this).faction == Alive.Faction.RADIANT)
				setTexture(Constants.versionString + "/res/radiant tower.jpg");
			else if(((Tower)this).faction == Alive.Faction.DIRE)
				setTexture(Constants.versionString + "/res/dire tower.png");
	}
	private void updateTexture(){
		try {
			texture = TextureLoader.getTexture(textureInfo.textureFormat, ResourceLoader.getResourceAsStream(textureInfo.namePath));
		}catch (IOException e) {
			System.out.println("A texture was set improperly");
			e.printStackTrace();
		}finally{
			resetTexture = false;
			textureInfo.updateNeeded = false;
		}
	}
}
