package com.feup.sfs.facility;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;

import com.feup.sfs.block.Block;
import com.feup.sfs.exceptions.FactoryInitializationException;
import com.feup.sfs.modbus.ModbusSlave;

public class Machine extends Conveyor {
	private double rot = 300;
	private boolean wasWorking = false;
	
	int tools[] = new int[3];
	
	public Machine(Properties properties, int id) throws FactoryInitializationException {
		super(properties, id);
		
		tools[0] = new Integer(properties.getProperty("facility." + id + ".tool1")).intValue();
		tools[1] = new Integer(properties.getProperty("facility." + id + ".tool2")).intValue();
		tools[2] = new Integer(properties.getProperty("facility." + id + ".tool3")).intValue();
		
		ModbusSlave.getSimpleProcessImage().addDigitalOut(new SimpleDigitalOut(false));//R-
		ModbusSlave.getSimpleProcessImage().addDigitalOut(new SimpleDigitalOut(false));//R+
		ModbusSlave.getSimpleProcessImage().addDigitalOut(new SimpleDigitalOut(false));//Tool
		ModbusSlave.getSimpleProcessImage().addDigitalIn(new SimpleDigitalIn(false));  //Tool Sensor
		
		wasWorking = false;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
				
		paintLight(g, false, 0, getDigitalOut(2), 1);
		paintLight(g, true, 1, getDigitalIn(1), 1);
		paintLight(g, false, 2, getDigitalOut(3), 1);
		paintLight(g, false, 3, getDigitalOut(4), 1);
	}
	
	@Override
	public void paintTop(Graphics g) {
		Rectangle bounds = getBounds();
		int centerX = bounds.x + bounds.width / 2;
		int centerY = bounds.y + bounds.height / 2;
		int mWidth = (int) (1.5 / getFactory().getPixelSize()); 
		int mHeight = (int) (0.4 / getFactory().getPixelSize()); 
		int toolSize = (int) (0.3 / getFactory().getPixelSize()); 
		double cosine1 = Math.cos((rot+90)*Math.PI/180);
		double cosine2 = Math.cos((rot+210)*Math.PI/180);
		double cosine3 = Math.cos((rot-30)*Math.PI/180);
		double sine1 = Math.sin((rot+90)*Math.PI/180);
		double sine2 = Math.sin((rot+210)*Math.PI/180);
		double sine3 = Math.sin((rot-30)*Math.PI/180);

		if (orientation == Direction.VERTICAL) {
			if (isToolWorking()) g.setColor(Color.red); else g.setColor(Color.darkGray);
			g.fillRect(bounds.x - mHeight * 3, centerY - mHeight *2, mWidth * 2 / 3, mWidth);	// Base
			g.fillRect(bounds.x - mHeight, centerY - mHeight / 2, bounds.width / 2 + mHeight / 2, mHeight);	// Arm
			g.fillRect(centerX - mHeight / 2, centerY - mWidth / 2, mHeight, mWidth);	// Tools
			g.setColor(getFactory().getToolColor(tools[0]));
			if (sine1 > 0) g.fillOval(centerX - toolSize / 2, (int) ((centerY - toolSize / 2) + mWidth * cosine1 / 2), (int)(toolSize + toolSize * sine1 / 3), (int)(toolSize + toolSize * sine1 / 3));
			g.setColor(getFactory().getToolColor(tools[1]));
			if (sine2 > 0) g.fillOval(centerX - toolSize / 2, (int) ((centerY - toolSize / 2) + mWidth * cosine2 / 2), (int)(toolSize + toolSize * sine2 / 3), (int)(toolSize + toolSize * sine2 / 3));
			g.setColor(getFactory().getToolColor(tools[2]));
			if (sine3 > 0) g.fillOval(centerX - toolSize / 2, (int) ((centerY - toolSize / 2) + mWidth * cosine3 / 2), (int)(toolSize + toolSize * sine3 / 3), (int)(toolSize + toolSize * sine3 / 3));
		}

		if (orientation == Direction.HORIZONTAL) {
			if (isToolWorking()) g.setColor(Color.red); else g.setColor(Color.darkGray);
			g.fillRect(centerX - mHeight * 2, bounds.y - mHeight * 3, mWidth, mWidth * 2 / 3);	// Arm
			g.fillRect(centerX - mHeight / 2, bounds.y - mHeight, mHeight, bounds.height / 2 + mHeight / 2);	// Arm
			g.fillRect(centerX - mWidth / 2, centerY - mHeight / 2, mWidth, mHeight);	// Tools
			g.setColor(getFactory().getToolColor(tools[0]));
			if (sine1 > 0) g.fillOval((int) (centerX - toolSize / 2 + mWidth * cosine1 / 2), centerY - toolSize / 2, (int)(toolSize + toolSize * sine1 / 3), (int)(toolSize + toolSize * sine1 / 3));
			g.setColor(getFactory().getToolColor(tools[1]));
			if (sine2 > 0) g.fillOval((int) (centerX - toolSize / 2 + mWidth * cosine2 / 2), centerY - toolSize / 2, (int)(toolSize + toolSize * sine2 / 3), (int)(toolSize + toolSize * sine2 / 3));
			g.setColor(getFactory().getToolColor(tools[2]));
			if (sine3 > 0) g.fillOval((int) (centerX - toolSize / 2 + mWidth * cosine3 / 2), centerY - toolSize / 2, (int)(toolSize + toolSize * sine3 / 3), (int)(toolSize + toolSize * sine3 / 3));
		}		
	}

	private boolean isToolWorking() {
		return getDigitalOut(4);
	}

	@Override
	public void doStep(boolean conveyorBlocked) {
		if (isToolWorking()) {
			wasWorking = true;
			ArrayList<Block> blocks = getFactory().getBlocks();
			for (Block block : blocks) {
				if (block.getDistanceTo(getCenterX(), getCenterY()) < getFactory().getSensorRadius()) { 
					if (inPlaceTool(0)) block.doWork(tools[0], getFactory().getSimulationTime());
					if (inPlaceTool(1)) block.doWork(tools[1], getFactory().getSimulationTime());
					if (inPlaceTool(2)) block.doWork(tools[2], getFactory().getSimulationTime());
				}
			}
			return;
		}
		
		if (wasWorking) {
			ArrayList<Block> blocks = getFactory().getBlocks();
			for (Block block : blocks) {
				if (block.getDistanceTo(getCenterX(), getCenterY()) < getFactory().getSensorRadius()) { 
					if (inPlaceTool(0)) block.stopWork();
					if (inPlaceTool(1)) block.stopWork();
					if (inPlaceTool(2)) block.stopWork();
				}
			}			
			wasWorking = false;
		}
		
		super.doStep(conveyorBlocked);
		
		if (isRotatingClockWise()) rot -= getFactory().getToolRotationSpeed()*getFactory().getSimulationTime()/1000;
		if (isRotatingAntiClockWise()) rot += getFactory().getToolRotationSpeed()*getFactory().getSimulationTime()/1000;
		if (rot > 360) rot-= 360;
		if (rot < 0) rot+= 360;
		
		if (inPlaceTool(0)) setDigitalIn(1, true);
		else if (inPlaceTool(1)) setDigitalIn(1, true);
		else if (inPlaceTool(2)) setDigitalIn(1, true);
		else setDigitalIn(1, false);
		

	}

	private boolean inPlaceTool(int i) {
		if (i == 0) return Math.abs(rot - 300) <= 10;
		if (i == 1) return Math.abs(rot - 180) <= 10;
		if (i == 2) return Math.abs(rot - 60) <= 10;
		return false;
	}

	private boolean isRotatingClockWise() {
		return getDigitalOut(2) && !getDigitalOut(3);
	}

	private boolean isRotatingAntiClockWise() {
		return getDigitalOut(3) && !getDigitalOut(2);
	}
	
	@Override
	public String getName() {
		return "Machine";
	}
	
	@Override
	public int getNumberDigitalIns() {return 2;}

	@Override
	public int getNumberDigitalOuts() {return 5;}


}
