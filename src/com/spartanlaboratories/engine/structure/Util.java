package com.spartanlaboratories.engine.structure;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.opengl.Texture;

import com.spartanlaboratories.engine.game.Actor;
import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Missile;
import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.util.Location;
/**
 * <b> The Engine's Utility Class </b>
 * <p>
 * Hold a variety of utility methods that don't have a place within an object.
 *
 * @author Spartak
 *
 */
public class Util extends StructureObject{
	/**
	 * Thrown if an object is being drawn that has not had its color value initialized. Sets this 
	 * object's color to white.
	 * 
	 * @author Spartak
	 *
	 */
	public class NullColorException extends Exception {
		public NullColorException(Engine engine, VisibleObject vo){
			engine.tracker.printAndLog("A Null Color Exception has occured. A Visible Object of type: " + vo.getClass().getName()
										+ " contains a null color value");
			engine.tracker.printAndLog("Setting this object's color to white");
			vo.color = Util.Color.WHITE;
		}
	}
	private Texture texture;
	Util(Engine engine){
		super(engine);
	}
	public boolean checkForCollision(VisibleObject first, VisibleObject second){
		if((first.getLocation().x < (second.getLocation().x + second.getWidth() / 2 + first.getWidth() / 2) &&
			first.getLocation().x > (second.getLocation().x - second.getWidth() / 2 - first.getWidth() / 2)) &&
			(first.getLocation().y < (second.getLocation().y + second.getHeight() / 2 + first.getHeight() / 2)&&
			first.getLocation().y > (second.getLocation().y - second.getHeight() / 2 - first.getHeight() / 2))){
			return true;
		}
		return false;
	}
	public void drawActor(VisibleObject v, StandardCamera camera){
		drawVO(v, getRGB(v.color), camera);
	}
	public void drawActor(VisibleObject vo, Util.Color color, StandardCamera camera){
		drawVO(vo, getRGB(color), camera);
	}
	final public void drawVO(VisibleObject vo, float[] RGB, StandardCamera camera){
		boolean hasTexture = setTexture(vo);
		GL11.glColor3f(RGB[0], RGB[1], RGB[2]);
		GL11.glBegin(GL11.GL_QUADS);
		if(!camera.fullyWithinBounds(vo)){
			drawVOEdge(vo, camera);
			return;
		}
			double x = vo.getLocation().getScreenCoords(camera).x, y = vo.getLocation().getScreenCoords(camera).y;
			if(hasTexture)GL11.glTexCoord2f(0,0);
			GL11.glVertex2d(x - vo.getWidth() / 2, y - vo.getHeight() / 2);
			if(hasTexture)GL11.glTexCoord2f((float)(texture.getWidth()),0);
			GL11.glVertex2d(x + vo.getWidth() / 2, y - vo.getHeight() / 2);
			if(hasTexture)GL11.glTexCoord2f((float)(texture.getWidth()),(float)(texture.getHeight()));
			GL11.glVertex2d(x + vo.getWidth() / 2, y + vo.getHeight() / 2);
			if(hasTexture)GL11.glTexCoord2f(0,(float)(texture.getHeight()));
			GL11.glVertex2d(x - vo.getWidth() / 2, y + vo.getHeight() / 2);
			GL11.glEnd();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	final private void drawVOEdge(VisibleObject actor, StandardCamera camera){
		if(actor.shape == Actor.Shape.QUAD){
			float textureWidth = texture != null ? (float) (texture.getWidth() ) : 1, 
				  textureHeight = texture != null ? (float) (texture.getHeight() ) : 1;
			double x = actor.getLocation().getScreenCoords(camera).x, y = actor.getLocation().getScreenCoords(camera).y;
			boolean bindCheckX, bindCheckY; double trueX, fakeX, trueY, fakeY;
			double ratioX, ratioY;
			float xMin, xMax, yMin, yMax;
			ratioX = actor.getWidth() / 
					(camera.xBound(x - actor.getWidth() / 2) && camera.xBound(x + actor.getWidth() / 2)?
					actor.getWidth():
					!camera.xMinBound(x - actor.getWidth() / 2)?
						x + actor.getWidth() / 2 - camera.monitorLocation.x + camera.dimensions.x / 2:
						camera.monitorLocation.x + camera.dimensions.x / 2 - x + actor.getWidth() / 2);
			ratioY = actor.getHeight() / 
					(camera.yBound(y - actor.getHeight() / 2) && camera.yBound(y + actor.getHeight() / 2)?
					actor.getHeight():
					!camera.yMinBound(y - actor.getHeight() / 2)?
						y + actor.getHeight() / 2 - camera.monitorLocation.y + camera.dimensions.y / 2:
						camera.monitorLocation.y + camera.dimensions.y / 2 - y + actor.getHeight() / 2);
			xMin = (float) (camera.xBound(x - actor.getWidth() / 2) && camera.xBound(x + actor.getWidth() / 2)?
					0:
					!camera.xMaxBound(x + actor.getWidth() / 2)?
						0:
						textureWidth *(1f - 1f/ratioX));
			xMax = (float) (camera.xBound(x - actor.getWidth() / 2) && camera.xBound(x + actor.getWidth() / 2)?
					textureWidth:
					!camera.xMinBound(x - actor.getWidth() / 2)?
						textureWidth:
						textureWidth / ratioX);
			yMin = (float) (camera.yBound(y - actor.getHeight() / 2) && camera.yBound(y + actor.getHeight() / 2)?
					0:
					!camera.yMaxBound(y + actor.getHeight() / 2)?
						0:
						textureHeight *(1f - 1f/ratioY));
			yMax = (float) (camera.yBound(y - actor.getHeight() / 2) && camera.yBound(y + actor.getHeight() / 2)?
					textureHeight:
					!camera.yMinBound(y - actor.getHeight() / 2)?
						textureHeight:
						textureHeight / ratioY);
				trueX = x - actor.getWidth() / 2;trueY = y - actor.getHeight() / 2;
				fakeX = camera.monitorLocation.x - camera.dimensions.x / 2;
				fakeY = camera.monitorLocation.y - camera.dimensions.y / 2;
				bindCheckX = camera.xMinBound(trueX);
				bindCheckY = camera.yMinBound(trueY);
				GL11.glTexCoord2f(xMin,yMin);
				GL11.glVertex2d(bindCheckX?trueX:fakeX, bindCheckY?trueY:fakeY);
				trueX = x + actor.getWidth() / 2;
				fakeX = camera.monitorLocation.x + camera.dimensions.x / 2;
				bindCheckX = camera.xMaxBound(trueX);
				GL11.glTexCoord2f(xMax,yMin);
				GL11.glVertex2d(bindCheckX?trueX:fakeX, bindCheckY?trueY:fakeY);
				trueY = y + actor.getHeight() / 2;
				fakeY = camera.monitorLocation.y + camera.dimensions.y / 2;
				bindCheckY = camera.yMaxBound(trueY);
				GL11.glTexCoord2f(xMax,yMax);
				GL11.glVertex2d(bindCheckX?trueX:fakeX, bindCheckY?trueY:fakeY);
				trueX = x - actor.getWidth() / 2;
				fakeX = camera.monitorLocation.x - camera.dimensions.x / 2;
				bindCheckX = camera.xMinBound(trueX);				
				GL11.glTexCoord2f(xMin,yMax);
				GL11.glVertex2d(bindCheckX?trueX:fakeX,bindCheckY?trueY:fakeY);
				GL11.glEnd();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			}
		else if(actor.shape == Actor.Shape.TRI){
			GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glVertex2d(actor.getLocation().getScreenCoords(camera).x - actor.getWidth() / 2, actor.getLocation().getScreenCoords(camera).y - actor.getHeight() / 2);
				GL11.glVertex2d(actor.getLocation().getScreenCoords(camera).x + actor.getWidth() / 2, actor.getLocation().getScreenCoords(camera).y - actor.getHeight() / 2);
				GL11.glVertex2d(actor.getLocation().getScreenCoords(camera).x - actor.getWidth() / 2, actor.getLocation().getScreenCoords(camera).y + actor.getHeight() / 2);
			GL11.glEnd();
		}
	} 
	public void drawOnScreen(VisibleObject vo, Util.Color color, Location locationOnScreen){
		float[] RGB = getRGB(color);
		setTexture(vo);
		float textureWidth = texture != null ? (float) (texture.getWidth() ) : 1, 
				  textureHeight = texture != null ? (float) (texture.getHeight() ) : 1;
				  if(vo.shape == Actor.Shape.QUAD){
					  setTexture(vo);
					GL11.glBegin(GL11.GL_QUADS);
					GL11.glColor3f(RGB[0], RGB[1], RGB[2]);
					GL11.glTexCoord2f(0,0);
					GL11.glVertex2d(locationOnScreen.x - vo.getWidth() / 2,locationOnScreen.y - vo.getHeight() / 2);
					GL11.glTexCoord2f(textureWidth,0);
					GL11.glVertex2d(locationOnScreen.x + vo.getWidth() / 2, locationOnScreen.y - vo.getHeight() / 2);
					GL11.glTexCoord2f(textureWidth,textureHeight);
					GL11.glVertex2d(locationOnScreen.x + vo.getWidth() / 2, locationOnScreen.y + vo.getHeight() / 2);
					GL11.glTexCoord2f(0,textureHeight);
					GL11.glVertex2d(locationOnScreen.x - vo.getWidth() / 2, locationOnScreen.y + vo.getHeight() / 2);
					GL11.glEnd();
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
				}
			else if(vo.shape == Actor.Shape.TRI){
				GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glColor3f(RGB[0], RGB[1], RGB[2]);
				GL11.glVertex2d(locationOnScreen.x - vo.getWidth() / 2, locationOnScreen.y - vo.getHeight() / 2);
				GL11.glVertex2d(locationOnScreen.x + vo.getWidth() / 2, locationOnScreen.y - vo.getHeight() / 2);
				GL11.glVertex2d(locationOnScreen.x - vo.getWidth() / 2, locationOnScreen.y + vo.getHeight() / 2);
			GL11.glEnd();
			}
	}
	public enum Color{
		RED, GREEN, BLUE, YELLOW, PURPLE, PINK, BLACK,GRAY, WHITE, LIGHTBLUE, ORANGE,;
	}
	public boolean everySecond(double secondRate){
		if(engine.tickCount % (engine.tickRate * secondRate) == 0)return true;
		return false;
	}
	public double getInverse(boolean x, double a){
		if(x){
			return ((double) (engine.getScreenDimensions().x/2 + (engine.getScreenDimensions().x/2 - a)));
		}
		else {
			return ((double) (engine.getScreenDimensions().y/2 + (engine.getScreenDimensions().y/2 - a )));
		}
	}
	public double getXDistance(Actor a, Actor b){
		if(a.getLocation().x > b.getLocation().x)
			return a.getLocation().x
					- b.getLocation().x;
		else if(a.getLocation().x < b.getLocation().x)return b.getLocation().x - a.getLocation().x;
		return 0;
	}
	public double getXDistance(Actor a, Location target) {
		if(a.getLocation().x > target.x){
			return a.getLocation().x
					- target.x;
		}
		else if(a.getLocation().x < target.x){
			return target.x - a.getLocation().x;
		}
		return 0;
	}
	public double getYDistance(Actor a, Actor b){
		if(a.getLocation().y > b.getLocation().y)return a.getLocation().y - b.getLocation().y;
		else if(a.getLocation().y < b.getLocation().y)return b.getLocation().y - a.getLocation().y;
		return 0;
	}
	public double getYDistance(Actor a, Location target) {
		if(a.getLocation().y > target.y){
			return a.getLocation().y - target.y;
		}
		else if(a.getLocation().y < target.y){
			return target.y - a.getLocation().y;
		}
		return 0;
	}
	public double getLongestAxialCentralDistance(Actor a, Actor b){
		double xDist = getXDistance(a,b);
		double yDist = getYDistance(a,b);
		if(xDist > yDist)return xDist;
		else if(yDist > xDist)return yDist;
		return xDist;
	}
	public double getRealCentralDistance(Actor a, Actor b){
		double xDist = getXDistance(a,b);
		double yDist = getYDistance(a,b);
		double distance = 0;
		distance = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
		return distance;
	}
	public double getRealCentralDistance(Actor a, Location l){
		double xDist = getXDistance(a,l);
		double yDist = getYDistance(a,l);
		double distance = 0;
		distance = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
		return distance;
	}
	public double getDistanceTangent(Actor a, Actor b){
		return getYDistance(a,b) / getXDistance(a,b);
	}
	public boolean missileDeath(Missile a) {
		for(Actor b : engine.allActors){
			if(b != null && b != a && b != a.parent && b.solid && checkForCollision(a, b)){
				if(b.getClass() == Alive.class)if(((Alive)b).getStat(Constants.health) < 0)return false;
				return true;
			}
		}
		return false;
	}
	public java.awt.Color getAsJavaColor(Util.Color color){
		switch(color){
		case GREEN:
			return java.awt.Color.GREEN;
		case RED:
			return java.awt.Color.RED;
		case BLUE:
			return java.awt.Color.BLUE;
		case WHITE:
			return java.awt.Color.WHITE;
		case YELLOW:
			return java.awt.Color.YELLOW;
		case PINK:
			return java.awt.Color.PINK;
		case ORANGE:
			return java.awt.Color.ORANGE;
		}
		return java.awt.Color.BLACK;
	}
	public float[] getRGB(Util.Color color){
		float[] RGB = new float[3];
		switch(color){
		case GREEN:
			RGB[0] = 0;
			RGB[1] = 1;
			RGB[2] = 0;
			break;
		case RED:
			RGB[0] = 1;
			RGB[1] = 0;
			RGB[2] = 0;
			break;
		case WHITE:
			RGB[0] = 1.0f;
			RGB[1] = 1.0f;
			RGB[2] = 1.0f;
			break;
		case BLACK:
			RGB[0] = 0.0f;
			RGB[1] = 0.0f;
			RGB[2] = 0.0f;
			break;
		case BLUE:
			RGB[0] = 0.0f;
			RGB[1] = 0.0f;
			RGB[2] = 1.0f;
			break;
		case GRAY:
			RGB[0] = 0.8f;
			RGB[1] = 0.8f;
			RGB[2] = 0.8f;
			break;
		case PINK:
			RGB[0] = 2.0f;
			RGB[1] = 0.0f;
			RGB[2] = 1.0f;
			break;
		case PURPLE:
			RGB[0] = 0.5f;
			RGB[1] = 0.5f;
			RGB[2] = 1.0f;
			break;
		case LIGHTBLUE:
			RGB[0] = 0.2f;
			RGB[1] = 0.4f;
			RGB[2] = 0.8f;
			break;
		case YELLOW:
			RGB[0] = 1.0f;
			RGB[1] = 1.0f;
			RGB[2] = 0.0f;
			break;
		case ORANGE:
			RGB[0] = 1f;
			RGB[1] = .6f;
			RGB[2] = 0.2f;
			break;
		default:
			break;
		}
		return RGB;
	}
	public boolean checkPointCollision(VisibleObject a, Location l){
		if(l.x > a.getLocation().x - (a.getWidth() / 2) 
		&& l.x < a.getLocation().x + (a.getWidth() / 2)
		&& l.y > a.getLocation().y - (a.getHeight() / 2)
		&& l.y < a.getLocation().y + (a.getHeight() / 2))
		return true;
		return false;
	}
	private boolean setTexture(VisibleObject vo){
		//System.out.println(texture.getTextureID());
		Texture newTexture = vo.getTextureNE();
		if(newTexture == null)return false;
		//GL13.glActiveTexture(GL13.GL_TEXTURE0);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, GL11.glGenTextures());
		if(texture == null || !texture.equals(newTexture)){
			texture = newTexture;
			texture.bind();
		}
		return true;
	}
	public void drawOnMap(Human player, Actor actor){
		Location savedLocation = new Location(actor.getLocation().x, actor.getLocation().y);							//save actor location
		actor.setWidth(actor.getWidth() / Constants.mapMultiplicationFactor); actor.setHeight(actor.getHeight() / Constants.mapMultiplicationFactor);//actor size decrease
		double xRatio = actor.getLocation().x / engine.getWrap().x, yRatio = actor.getLocation().y / engine.getWrap().y;
		actor.setLocation(player.mapBackground.getLocation().x + ( -0.5 + xRatio) * player.mapBackground.getWidth()
		, player.mapBackground.getLocation().y + ( -0.5 + yRatio) * player.mapBackground.getHeight());
		drawOnScreen(actor, Color.WHITE, actor.getLocation());
		actor.setWidth(actor.getWidth() * Constants.mapMultiplicationFactor);actor.setHeight(actor.getHeight() * Constants.mapMultiplicationFactor);//reverse actor size decrease
		actor.setLocation(savedLocation.x, savedLocation.y);												//revert actor location
	}
}
