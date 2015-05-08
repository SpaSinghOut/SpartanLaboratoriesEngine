package com.spartanlaboratories.engine.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import com.spartanlaboratories.engine.game.Buff;
import com.spartanlaboratories.engine.structure.Engine;

public class BuffIcon extends JLabel implements MouseListener{
	public Buff buff;
	Gui gui;
	int ordinal;
	static final int width = 64;
	static final int height = 32;
	public BuffIcon(Gui setGui, int setOrdinal){
		gui = setGui;
		ordinal = setOrdinal;
		this.setSize(width, height);
		this.setBackground(Color.YELLOW);
		this.setForeground(Color.BLACK);
		this.setLocation((int)(gui.engine.getScreenDimensions().x * .27 + (width+1) * ordinal),
				(int)(gui.engine.getScreenDimensions().y * .67));
		setBuff(null);
		addMouseListener(this);
	}
	public void setBuff(Buff setBuff){
		buff = setBuff;
		setVisible(buff == null ? false : true);
		setText(buff == null ? "" : String.valueOf(buff.count) + buff.buffName.toString());
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		gui.out(buff.buffName.toString());
		gui.showDecription(true, buff.buffName.toString());
	}
	@Override
	public void mouseExited(MouseEvent e) {
		gui.showDecription(false, "");
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
