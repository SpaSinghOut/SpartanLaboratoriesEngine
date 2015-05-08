
package com.spartanlaboratories.engine.game;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.spartanlaboratories.engine.structure.Camera;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Util;

public class VisibleObject extends GameObject{
	private double height;
	private double width;
	private Texture texture;
	public Shape shape;
	public Util.Color color;
	public Util.Color defaultColor;
	public boolean solid;
	boolean immobile;
	ArrayList<Effect> effects = new ArrayList<Effect>();
	public boolean resetTexture;
	public VisibleObject(Engine engine){
		super(engine);
		engine.visibleObjects.add(this);
		shape = Shape.QUAD;
	}
	public enum Shape{
		QUAD, TRI,;
	}
	protected boolean tick(){
		return super.tick();
	}
	protected void setTexture() throws IOException{
		if(this.getClass() == Hero.class){
			String heroNameString = ((Hero)this).heroType.toString().toLowerCase();
			if(heroNameString != "none")
				texture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream(Constants.versionString + "/res/" + heroNameString + ".jpg"));
		}
		else if(this.getClass() == Creep.class)
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(Constants.versionString + "/res/radiant creep.png"));
		else if(this.getClass() == Tower.class)
			if(((Tower)this).faction == Alive.Faction.RADIANT)
				texture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream(Constants.versionString + "/res/radiant tower.jpg"));
			else if(((Tower)this).faction == Alive.Faction.DIRE)
				texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(Constants.versionString + "/res/dire tower.png"));
	}
	public void setTexture(Texture setTexture){
		texture = setTexture;
	}
	public void setTexture(String format, String namePath){
		try {
			texture = TextureLoader.getTexture(format, ResourceLoader.getResourceAsStream(namePath));
		} catch (IOException e) {
			System.out.println("A texture was set improperly");
			e.printStackTrace();
		}
	}
	public float[] getRGB() throws Util.NullColorException{
		if(color == null)throw engine.util.new NullColorException(engine, this);
		return engine.util.getRGB(color);
	}
	public Texture getTexture(){
		Texture rTexture = texture;
		try {
			rTexture = texture == null ? TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("res/black.jpg")) : texture;
		} catch (IOException e) {
			System.out.println("getTexture exception");
		}
		return rTexture;
	}
	public Texture getTextureNE() {
		return texture;
	}
	protected boolean drawMe(Camera camera, float[] RGB){
		if(camera.canSeeObject(this)){
			if(resetTexture){
				try {
					setTexture();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			engine.util.drawActor(this, RGB, camera);
			return true;
		}
		return false;
	}
	public boolean drawMe (Camera camera) throws Util.NullColorException{
		return !active?active:drawMe(camera, getRGB());
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	protected void updateComponentLocation(){
		
	}
	/**
	 * copy
	 */
	public VisibleObject copy(){
		VisibleObject vo = new VisibleObject(engine);
		super.copyTo(vo);
		vo.height = height;
		vo.shape = shape;
		vo.width = width;
		vo.texture = texture;
		vo.color = color;
		vo.defaultColor = defaultColor;
		vo.solid = solid;
		vo.immobile = immobile;
		for(Effect e:effects)vo.effects.add(e);
		vo.resetTexture = resetTexture;
		return vo;
	}
	protected void copyTo(VisibleObject vo){
		super.copyTo(vo);
		vo.height = height;
		vo.shape = shape;
		vo.width = width;
		vo.texture = texture;
		vo.color = color;
		vo.defaultColor = defaultColor;
		vo.solid = solid;
		vo.immobile = immobile;
		for(Effect e:effects)vo.effects.add(e);
		vo.resetTexture = resetTexture;
	}
	@Override
	public void doOnTick() {
		// TODO Auto-generated method stub
		
	}
	
}
