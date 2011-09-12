package com.feup.sfs.facility;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;

import com.feup.sfs.block.Block;
import com.feup.sfs.exceptions.FactoryInitializationException;

public class Rail extends Conveyor {
	private int railSize;
	private double railPosition = 0;
	
	public Rail(Properties properties, int id) throws FactoryInitializationException {
		super(properties, id);

		setRailSize(new Integer(properties.getProperty("facility."+id+".rail.size")).intValue());
		
		railPosition = -railSize / 2;
		
		addDigitalOut(new SimpleDigitalOut(false), "Rail -");
		addDigitalOut(new SimpleDigitalOut(false), "Rail +");
		addDigitalIn(new SimpleDigitalIn(false), "Rail - Sensor");	
		addDigitalIn(new SimpleDigitalIn(false), "Rail + Sensor");	
	}

	@Override
	public String getName() {
		return "Rail";
	}
	
	private void paintConveyor(Graphics g){
		super.paint(g);
		paintLight(g, false, 0, getDigitalOut(2), 1);
		paintLight(g, true, 1, getDigitalIn(sensors), 1);
		paintLight(g, true, 2, getDigitalIn(sensors + 1), 1);
		paintLight(g, false, 3, getDigitalOut(3), 1);
	}
	
	public void paint(Graphics g){
		
		double oldRailPosition = railPosition;
		railPosition = 0;
		Rectangle bounds = getBounds();
		railPosition = oldRailPosition;
		g.setColor(Color.darkGray);
		
		double pixelSize = getFactory().getPixelSize();
		int railSize = (int) (this.railSize / pixelSize);
		
		if (getOrientation()==Orientation.VERTICAL) {
			g.drawLine(bounds.x - railSize / 2 + bounds.width / 2, bounds.y + bounds.height / 4, bounds.x + railSize / 2 + bounds.width / 2, bounds.y + bounds.height / 4);
			g.drawLine(bounds.x - railSize / 2 + bounds.width / 2, bounds.y + 3 * bounds.height / 4, bounds.x + railSize / 2 + bounds.width / 2 , bounds.y + 3 * bounds.height / 4);
		} else {
			g.drawLine(bounds.x + bounds.width / 4, bounds.y - railSize / 2 + bounds.height / 2, bounds.x + bounds.width / 4, bounds.y + railSize / 2 + bounds.height / 2);
			g.drawLine(bounds.x + 3 * bounds.width / 4, bounds.y - railSize / 2 + bounds.height / 2, bounds.x + 3 * bounds.width / 4, bounds.y + railSize / 2 + bounds.height / 2);
		}
	
		paintConveyor(g);
	}

	@Override
	public Point2D.Double getSensorPosition(int i) {
		if (getOrientation()==Orientation.VERTICAL) return getSensorPosition(i, railPosition, 0);
		else return getSensorPosition(i, 0, railPosition);
	}
	
	@Override
	public void doStep(boolean conveyorBlocked){
		boolean movedLeft = false;
		boolean movedRight = false;
		if (facilityError) return;
		boolean forcing = false;

		super.doStep(isRailMovingLeft() || isRailMovingLeft());
		
		double speed = getFactory().getConveyorSpeed()*getFactory().getSimulationTime()/1000;
		
		if (isRailMovingLeft() && !isRailMovingRight()) {railPosition -= speed; movedLeft = true;}
		if (isRailMovingRight() && !isRailMovingLeft()) {railPosition += speed; movedRight = true;}
		
		if (railPosition < -railSize / 2) {railPosition = -railSize / 2; forcing = true; movedLeft = false;}
		if (railPosition > railSize / 2) {railPosition = railSize / 2; forcing = true; movedRight = false;}
		
		if (railPosition <= -railSize / 2) setDigitalIn(sensors, true); else setDigitalIn(sensors, false);
		if (railPosition >= railSize / 2) setDigitalIn(sensors + 1, true); else setDigitalIn(sensors + 1, false);
		
		if (movedLeft || movedRight) {
			ArrayList<Block> blocks = getFactory().getBlocks();
			for (Block block : blocks) {
				if (getBounds().intersects(block.getBounds())){
					if (movedLeft && getOrientation()==Orientation.VERTICAL) block.setMoveLeft(true);
					if (movedRight && getOrientation()==Orientation.VERTICAL) block.setMoveRight(true);
					if (movedLeft && getOrientation()==Orientation.HORIZONTAL) block.setMoveTop(true);
					if (movedRight && getOrientation()==Orientation.HORIZONTAL) block.setMoveBottom(true);
				}
			}
		}
		
		isForcing(forcing);
	}
	
	private boolean isRailMovingLeft() {
		return getDigitalOut(2) && !getDigitalOut(3);
	}

	private boolean isRailMovingRight() {
		return getDigitalOut(3) && !getDigitalOut(2);
	}

	public void setRailSize(int railSize) {
		this.railSize = railSize;
	}

	public int getRailSize() {
		return railSize;
	}
		
	@Override
	public Rectangle getBounds() {
		double pixelSize = getFactory().getPixelSize();
		int x = getOrientation()==Orientation.VERTICAL?(int) (getCenterX()/pixelSize - width/2/pixelSize + railPosition/pixelSize):(int) (getCenterX()/pixelSize - length/2/pixelSize); 
		int y = getOrientation()==Orientation.VERTICAL?(int) (getCenterY()/pixelSize - length/2/pixelSize):(int) (getCenterY()/pixelSize - width/2/pixelSize + railPosition/pixelSize);
		int w = getOrientation()==Orientation.VERTICAL?(int) (width/pixelSize):(int) (length/pixelSize);
		int h = getOrientation()==Orientation.VERTICAL?(int) (length/pixelSize):(int) (width/pixelSize);
		return new Rectangle(x, y, w, h);
	}
}