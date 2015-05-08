package com.spartanlaboratories.engine.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

import com.spartanlaboratories.engine.structure.Console;
import com.spartanlaboratories.engine.structure.Constants;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Location;
import com.spartanlaboratories.engine.structure.ObsoleteCommandException;

public class GraphicalConsole extends Console implements MouseListener{
	JTextField consoleInput;
	int consoleInputHeight, consoleInputWidth;
	Gui gui;
	String[] commandNames;
	public GraphicalConsole(Gui setGui){
		super(setGui.engine, setGui.owner);
		gui = setGui;
		consoleInput = new JTextField("write something here");
		consoleInput.addKeyListener(new ConsoleKeyListener(this));
		consoleInput.addMouseListener(this);
		consoleInput.setLocation(gui.scrollPane.getLocation().x, gui.scrollPane.getLocation().y + gui.scrollPane.getSize().height);
		consoleInputWidth = gui.scrollPane.getSize().width;
		consoleInputHeight = (int)(gui.screenY * (0.025));
		consoleInput.setSize(consoleInputWidth, consoleInputHeight);
		consoleInput.setVisible(false);
		gui.addComponent("Special", consoleInput);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		char[] realString = consoleInput.getText().toCharArray(), testString = "write something here".toCharArray();
		boolean match = true;
		for(int i = 0; i < testString.length; i++)if(i < realString.length && realString[i] != testString[i])match = false;
		if(match)consoleInput.setText("");
	}
	public void takeCommand(){
		switch(execution){
		case 0:
			try {
				oldTakeCommand(consoleInput.getText());
			} catch (ObsoleteCommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 1:
			takeCommand(consoleInput.getText());
			break;
		default:break;
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void out(String string){
		gui.out(string);
	}
}
