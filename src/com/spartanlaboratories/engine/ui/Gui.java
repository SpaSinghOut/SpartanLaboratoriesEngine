package com.spartanlaboratories.engine.ui;

import javax.swing.*;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.spartanlaboratories.engine.game.Ability;
import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Hero;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Human;
import com.spartanlaboratories.engine.structure.SLEImproperInputException;
import com.spartanlaboratories.engine.util.Location;

import java.awt.Component;
import java.awt.Font;
import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;

public class Gui extends JFrame implements KeyListener, MouseListener{
	public HashMap<String, ArrayList<Component>> interfaceElements = new HashMap<String, ArrayList<Component>>();
	public static String[] componentVisibilities = { "Actor", "Alive", "Hero", "Special"};
	Engine engine;
	public final int screenX;
	public final int screenY;
	JLabel HUD_component_level, HUD_component_health_number, HUD_component_health_bar,
	HUD_component_damage,HUD_component_custom_stats[],
	HUD_component_mana_bar, HUD_component_mana_number, HUD_component_move_speed, 
	HUD_component_attack_speed, HUD_component_armor, HUD_component_gold, HUD_component_pause;
	public JLabel HUD_component_clock;
	LevelUpButton levelUpButton;
	public ArrayList<BuffIcon> buffs = new ArrayList<BuffIcon>();
	JTextArea description, rsBox;JScrollPane scrollPane;
	ArrayList<AbilityButton> abilityButtons = new ArrayList<AbilityButton>();
	Human owner;
	public GraphicalConsole console;
	JMenuBar menuBar;
	public Canvas canvas = new Canvas();
	 //For the following: *** CONSTRUCTOR INITIALIZATION ONLY *** (dependent on gui being initialized)
	final int healthBarMaxWidth;
	final int healthBarHeight;
	StateChangingButton abilityPoints;
	//End of constuctor initialization only
	Shop shop;//not graphics dependent but requires outside initialization;
	protected int numberOfCustomStats;
	protected int[] customStats;
	public Gui(Human setOwner){
		// Sets the owner of the gui to be the passed in human object.
		owner = setOwner;
		// Sets the engine that this gui refereces to be the same as the engine of the human that owns this gui.
		engine = owner.engine;
		// Sets the display dimensions as decided on by the engine prior to the creation of this object.
		screenX = (int) engine.getScreenDimensions().x;
		screenY = (int) engine.getScreenDimensions().y;
		// Makes this frame "decorated" which mean that it will look like a window.
		// "Undecorated" would be preferred, however it causes problems and needs looking into.
		setUndecorated(false);
		
		// Sets up this window's keyboard and mouse listeners.
		getContentPane().addKeyListener(this);
		getContentPane().addMouseListener(this);
		// Creates the canvas which is the bridge between the window and the custom opengl graphics.
		canvas.setLocation(0,0);
		canvas.setSize(screenX, screenY);
		canvas.setVisible(true);
		canvas.setEnabled(true);
		
		// Sets various window properties.
		setSize(screenX,screenY);
		getContentPane().add(canvas,0);
		getLayeredPane().add(getContentPane(),0);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		// Handles window content properties.
		/* Really needs to be better organized.
		 * The same hierarchy of operations are being performed on different levels of function calls and 
		 * not as a result of encapsulation. */
		healthBarMaxWidth = (int) (engine.getScreenDimensions().x / 3);
		healthBarHeight = (int) (engine.getScreenDimensions().y / 30);
		customStats = new int[numberOfCustomStats];
		initHUD();
		setVisible(true);
	}
	/**
	 * Sets default parameters for the items shop and adds it the the
	 * HUD. Is only to be called after the shop has been initialized, otherwise it will throw a null pointer exception.
	 */
	final protected void initShop(){
		shop.setLocation((int)(screenX * 0.85), (int)(screenY * 0.111));
		shop.setSize((int)(screenX * .15), (int)(screenY * 0.032));
		shop.setVisible(true);
		getLayeredPane().add(shop);
	}
	final private void initHUD() {
		// Handles the creation and the settion of various properties of HUD elements.
		abilityPoints = new StateChangingButton(this);
		HUD_component_level = new JLabel(((Integer)(1)).toString());
		HUD_component_level.setLocation((int)(screenX * .236),(int)(screenY * .939));
		HUD_component_level.setSize((int)(screenX * 0.065), (int)(screenY * 0.021));
		HUD_component_level.setVisible(true);
		HUD_component_level.setBackground(java.awt.Color.BLUE);
		HUD_component_level.setForeground(java.awt.Color.YELLOW);
		HUD_component_level.setOpaque(true); 
		HUD_component_health_number = new JLabel("0");
		HUD_component_health_number.setLocation((int)(screenX / 3),
				(int)(screenY * .735));
		HUD_component_health_number.setSize(healthBarMaxWidth, healthBarHeight);
		HUD_component_health_number.setOpaque(false);
		HUD_component_health_number.setForeground(java.awt.Color.BLACK);
		HUD_component_health_bar = new JLabel();
		HUD_component_health_bar.setLocation(HUD_component_health_number.getLocation());
		HUD_component_health_bar.setSize(HUD_component_health_number.getSize());
		HUD_component_health_bar.setBackground(java.awt.Color.RED);
		HUD_component_health_bar.setOpaque(true);
		HUD_component_mana_number = new JLabel("0");
		HUD_component_mana_number.setLocation((int)(engine.getScreenDimensions().x / 3),
				(int)(engine.getScreenDimensions().y * .777));
		HUD_component_mana_number.setSize(healthBarMaxWidth, healthBarHeight);
		HUD_component_mana_number.setOpaque(false);
		HUD_component_mana_number.setForeground(java.awt.Color.BLACK);
		HUD_component_mana_bar = new JLabel();
		HUD_component_mana_bar.setLocation(HUD_component_mana_number.getLocation());
		HUD_component_mana_bar.setSize(HUD_component_mana_number.getSize());
		HUD_component_mana_bar.setBackground(java.awt.Color.BLUE);
		HUD_component_mana_bar.setOpaque(true);
		rsBox = new JTextArea("activity");
		rsBox.setLocation(0,0);
		rsBox.setSize((int)(screenX * .258), (int)(screenY * .469));
		rsBox.setVisible(true);
		rsBox.setEditable(false);rsBox.setLineWrap(true);
		scrollPane = new JScrollPane(rsBox);
		scrollPane.setLocation((int)(screenX * .673), (int) (screenY * .737));
		scrollPane.setSize((int)(screenX * .327), (int)(screenY * .219));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setVisible(false);
		HUD_component_damage = new JLabel();
		HUD_component_damage.setLocation((int)(screenX * .27), (int)(screenY * .766));
		HUD_component_damage.setSize((int)(screenX * .042), (int)(screenY * 0.023));
		HUD_component_damage.setBackground(java.awt.Color.WHITE);
		HUD_component_damage.setForeground(java.awt.Color.BLACK);
		HUD_component_armor = new JLabel();
		HUD_component_armor.setLocation((int)(screenX * .27), (int)(screenY * .7425));
		HUD_component_armor.setSize((int)(screenX * .042), (int)(screenY * 0.023));
		HUD_component_attack_speed = new JLabel();
		HUD_component_attack_speed.setLocation((int)(engine.getScreenDimensions().x * .27),
				(int)(engine.getScreenDimensions().y * .72));
		HUD_component_attack_speed.setSize((int)(screenX * .042), (int)(screenY * 0.023));
		HUD_component_attack_speed.setBackground(java.awt.Color.WHITE);
		HUD_component_attack_speed.setForeground(java.awt.Color.BLACK);
		HUD_component_custom_stats = new JLabel[numberOfCustomStats];
		int yOffset = 0;
		for(JLabel l: HUD_component_custom_stats){
			l = new JLabel();
			l.setLocation(percentX(27), percentY(76.5 + (yOffset+=3.5)));
			l.setSize(percentX(4), percentY(2.4));
			l.setBackground(java.awt.Color.WHITE);
			l.setForeground(java.awt.Color.RED);
			l.setOpaque(true);
			l.setVisible(true);
		}
		HUD_component_move_speed = new JLabel(((Integer)(1)).toString());
		HUD_component_move_speed.setLocation((int)(screenX * .27),(int)(screenY * .9));
		HUD_component_move_speed .setSize(80, 25);
		HUD_component_move_speed .setVisible(true);
		HUD_component_move_speed .setBackground(java.awt.Color.YELLOW);
		HUD_component_move_speed .setForeground(java.awt.Color.BLACK);
		HUD_component_gold = new JLabel();
		HUD_component_gold.setLocation((int)(screenX * .87), (int)(screenY * .712));
		HUD_component_gold.setSize((int)(screenX * .051), (int)(screenY * 0.028));
		HUD_component_gold.setForeground(java.awt.Color.YELLOW);
		HUD_component_gold.setOpaque(true);
		HUD_component_gold.setBackground(java.awt.Color.BLUE);
		HUD_component_clock = new JLabel();
		HUD_component_clock.setSize((int)(screenX * .03635), (int)(screenY * 0.04672));
		HUD_component_clock.setLocation((int)(screenX * .5 - HUD_component_clock.getSize().width / 2), 0);
		HUD_component_clock.setBackground(Color.GRAY);
		HUD_component_clock.setForeground(Color.BLUE);
		HUD_component_pause = new JLabel("PAUSE");
		HUD_component_pause.setLocation((int)(screenX / 3), (int)(screenY * 3/8));
		HUD_component_pause.setSize((int)(screenX / 3), (int)(screenY * .25));
		HUD_component_pause.setFont(new Font("Arial", Font.PLAIN, 32));
		HUD_component_pause.setVisible(false);
		HUD_component_pause.setOpaque(false);
		description = new JTextArea("test");
		description.setSize((int)(screenX * .113), (int)(screenY * .131));
		description.setLocation((int)(screenX * .345),(int)(screenY * .622));
		description.setVisible(false);
		addComponent("Alive", HUD_component_level);
		addComponent("Alive", HUD_component_health_number);
		addComponent("Alive", HUD_component_health_bar);
		addComponent("Alive", HUD_component_damage);
		addComponent("Alive", HUD_component_mana_number);
		addComponent("Alive", HUD_component_mana_bar);
		addComponent("Special", description);
		addComponent("Alive", HUD_component_move_speed);
		addComponent("Alive", HUD_component_attack_speed);
		addComponent("Special", scrollPane);
		addComponent("Alive", HUD_component_armor);
		levelUpButton = new LevelUpButton(this);
		addComponent("Alive", HUD_component_gold);
		addComponent("Actor", HUD_component_clock);
		addComponent("Special", HUD_component_pause);
		for(JLabel l: HUD_component_custom_stats)addComponent("Alive", l);
		console = new GraphicalConsole(this);
		for(int i = 0; i < 10; i++){
			buffs.add(new BuffIcon(this, i));
			addComponent("Special", buffs.get(i));
		}
	}
	void addComponent(String string, Component component){
		getLayeredPane().add(component);
		if(!interfaceElements.containsKey(string))
			interfaceElements.put(string, new ArrayList<Component>());
		interfaceElements.get(string).add(component);
		component.setVisible(false);
	}
	private int percentX(double d){return (Integer)((int)(d * screenX / 100));}
	private int percentY(double d){return (Integer)((int)(d * screenY / 100));}
	public void tick(){
		if(!console.consoleInput.hasFocus())
			getContentPane().requestFocusInWindow();
			;
		if(owner.selectedUnit==null||!Alive.class.isAssignableFrom(owner.selectedUnit.getClass()))return;
		
		Alive a = (Alive) owner.selectedUnit;
		for(int i = 0; i < buffs.size(); i++)
			if(a.getBuffs().size() > i && a.getBuffs().get(i) != null)
				buffs.get(i).setBuff(a.getBuffs().get(i));
			else buffs.get(i).setBuff(null);
	}
	public void render(){
		
		if(owner.selectedUnit==null)return;
		//do this regardless of whether the selected unit is alive or not
		
		if(!Alive.class.isAssignableFrom(owner.selectedUnit.getClass()))return;
		
		// Do these only if the selected unit is alive
		Alive a = (Alive)owner.selectedUnit;
		String string = ("  lvl:" + (Integer)(int)(a.getStat(Constants.level))).toString() + "   " + ("xp:" + (Integer)(int)(a.getStat(Constants.experience))).toString() + "/" + ((Integer)(int)(a.getStat(Constants.level) * 100 + 100));
		HUD_component_level.setText(string);
		string = "                " + 
		((Integer)(int)a.getStat(Constants.health)).toString() + "/" + 
		((Integer)(int)a.getStat(Constants.maxHealth));
		HUD_component_health_number.setFont(
		new Font(HUD_component_health_number.getFont().getName(), Font.BOLD, 20));
		HUD_component_health_number.setText(string);
		HUD_component_health_number.setOpaque(false);
		HUD_component_health_bar.setSize((int)(a.getRatio("health") * healthBarMaxWidth), healthBarHeight);
		string = "dmg: " + ((Integer)(int)a.getStat(Constants.damage)).toString();
		HUD_component_damage.setText(string);
		for(int i = 0; i < HUD_component_custom_stats.length; i++){
			string = "    " + convert(customStats[i]!=0?customStats[i]:0);
			HUD_component_custom_stats[i].setText(string);
		}
		levelUpButton.update();
		string = ((Integer)(int)a.getStat(Constants.mana)).toString() + "/" + 
					((Integer)(int)a.getStat(Constants.maxMana));
		HUD_component_mana_bar.getGraphics().setFont(HUD_component_health_number.getFont());
		string =  "                " + 
				((Integer)(int)a.getStat(Constants.mana)).toString() + "/" + 
				((Integer)(int)a.getStat(Constants.maxMana));
		HUD_component_mana_number.setFont(HUD_component_health_number.getFont());
		HUD_component_mana_number.setText(string);
		string = "ms: " + ((Integer)(int)a.getSpeed()).toString();
		HUD_component_move_speed.setText(string);
		string = "as: " + ((Integer)(int)a.getStat(Constants.attackSpeed)).toString();
		HUD_component_attack_speed.setText(string);
		scrollPane.getViewport().setViewPosition(new Point(scrollPane.getBounds().x, 999999999));
		string = "arm: " + ((Integer)(int)(a.getStat(Constants.armor)));
		HUD_component_armor.setText(string);
		string = "gold: " + ((Integer)(int)(a.getStat(Constants.gold)));
		HUD_component_gold.setText(string);
		string = "     " + String.format("%02d" , engine.tickCount/60/60) + ":" + String.format("%02d", engine.tickCount/60%60);
		HUD_component_clock.setText(string);

		if(!Hero.class.isAssignableFrom(a.getClass()))return;
		
		//Do this if the selected unit is a Hero
		Hero hero = (Hero)a;
		renderInventoryUsingSlick(hero);
		for(AbilityButton ab: abilityButtons){
			if(ab.correspondingAbility.state == Ability.State.READY)
				{
				ab.setBackground(engine.util.getAsJavaColor(ab.correspondingAbility.abilityStats.color));
				}
				else 
				ab.setBackground(Color.BLACK);
		}
	}
	public Component add(Component component){
		return super.add(component);
	}
	private String convert(double i){
		return ((Double)i).toString();
	}
	public void renderInventoryUsingSlick(Hero hero) {
		for(int i = 0; i < hero.inventory.size(); i++)if(hero.inventory.getItemInSlot(i) != null){
			org.newdawn.slick.Color.white.bind();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0,0);
				GL11.glVertex2f((int)(screenX * .68) + i * (int)(screenX * 0.0345), (int)(screenY * .697));
				GL11.glTexCoord2f(1,0);
				GL11.glVertex2f((int)(screenX * .68) + (int)(screenX * .0345) * (i+1), (int)(screenY * .697));
				GL11.glTexCoord2f(1,1);
				GL11.glVertex2f((int)(screenX * .68) + (int)(screenX * .0345) * (i+1), (int)(screenY * .697) + (int)(screenY * 0.0595));
				GL11.glTexCoord2f(0,1);
				GL11.glVertex2f((int)(screenX * .68) + i * (int)(screenX * 0.0345), (int)(screenY * .697) + (int)(screenY * 0.0595));
			GL11.glEnd();
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	public void showDecription(boolean b, String sDescription){
		if(b){
			description.setText(sDescription);
			description.setVisible(true);
		}
		else description.setVisible(false);
	}
	public void out(String string){
		rsBox.append("\n" + string);
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		switch(arg0.getKeyCode()){
		case KeyEvent.VK_LEFT:case KeyEvent.VK_RIGHT:case KeyEvent.VK_DOWN:	case KeyEvent.VK_UP:
			owner.getPrimaryCamera().handleKeyPress(arg0);;
			break;
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		switch(arg0.getKeyCode()){
		case KeyEvent.VK_T:
			levelUpButton.mouseClicked(new MouseEvent(
			this,MouseEvent.MOUSE_PRESSED,System.nanoTime(), 0,levelUpButton.getLocation().x, levelUpButton.getLocation().y, 1,true));
			out("t");
			break;
		case KeyEvent.VK_Q:
			this.abilityButtons.get(0).mouseClicked(new MouseEvent(this, 0,0,0,0,0,0,false));
			break;
		case KeyEvent.VK_W:
			this.abilityButtons.get(1).mouseClicked(new MouseEvent(this, 0,0,0,0,0,0,false));
			break;
		case KeyEvent.VK_E:
			this.abilityButtons.get(2).mouseClicked(new MouseEvent(this, 0,0,0,0,0,0,false));
			break;
		case KeyEvent.VK_R:
			this.abilityButtons.get(3).mouseClicked(new MouseEvent(this, 0,0,0,0,0,0,false));
			break;
		case KeyEvent.VK_SPACE:
			try{
				owner.setSelectedUnit(owner.controlledUnits()[0]);
			}catch(ArrayIndexOutOfBoundsException e){
				console.out("Spacebar cannot make the player focus on a unit because the player does not own one");
			}
			try {
				owner.coveringCamera(owner.getMouseLocation()).handleKeyPress(arg0);
			} catch (SLEImproperInputException e) {
				System.out.println("Spacebar was pressed while the mouse was outside the scope of any camera.");
				System.out.println("Using the default camera instead");
				owner.getPrimaryCamera().handleKeyPress(arg0);
				e.printStackTrace();
			}
			break;
		case KeyEvent.VK_ESCAPE:
			engine.running = false;
			break;
		case KeyEvent.VK_F3:
			toggleConsole();
			break;
		case KeyEvent.VK_P:case KeyEvent.VK_F11:
			togglePause();
			break;
		}
	}
	private void togglePause(){
		engine.pause = !engine.pause;
		HUD_component_pause.setVisible(engine.pause);
		HUD_component_pause.setOpaque(false);
	}
	private void toggleConsole(){
		boolean b = !scrollPane.isVisible();
		scrollPane.setVisible(b);
		console.consoleInput.setVisible(b);
	}
	public void keyTyped(KeyEvent arg0) {}
	Location getHalfScreenSize(){
		return new Location(screenX / 2, screenY / 2);
	}
	public void clearInterface() {
		for(int i = 0; i < interfaceElements.size(); i++)
			if(!componentVisibilities[i].equals("Special") && interfaceElements.containsKey(componentVisibilities[i]))
				for(Component c: interfaceElements.get(componentVisibilities[i])){
					c.setVisible(false);
					c.update(c.getGraphics());
					c.update(getGraphics());
					update(getGraphics());
				}
	}
	public void setInterfaceVisibilty(String string) {
		switch(string){
		case "Hero":
			if(interfaceElements.containsKey("Hero"))
			for(Component c:interfaceElements.get("Hero"))
				c.setVisible(true);
		case "Alive": 
			if(interfaceElements.containsKey("Alive"))
			for(Component c:interfaceElements.get("Alive"))
				c.setVisible(true);
		case "Actor":
			if(interfaceElements.containsKey("Actor"))
			for(Component c:interfaceElements.get("Actor"))
				c.setVisible(true);
			
		break;
		default: throw new IllegalArgumentException();
		}
		this.update(getGraphics());
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		int button = 3;
		switch(e.getButton()){
		case MouseEvent.BUTTON1:
			button = 1;
			break;
		case MouseEvent.BUTTON2:
			button = 3;
			break;
		case MouseEvent.BUTTON3:
			button = 2;
			break;
		}
		console.out(owner.getMouseLocationG());
		owner.setMouseButtonDown(button, true);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		int button = 3;
		switch(e.getButton()){
		case MouseEvent.BUTTON1:
			button = 1;
			break;
		case MouseEvent.BUTTON2:
			button = 3;
			break;
		case MouseEvent.BUTTON3:
			button = 2;
			break;
		}
		console.out(String.valueOf(button));
		owner.setMouseButtonDown(button, false);
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}
