/*
 * This file is part of ShopFloorSimulator.
 * 
 * ShopFloorSimulator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ShopFloorSimulator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ShopFloorSimulator.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.feup.sfs.facility;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;

import com.feup.sfs.block.Block;
import com.feup.sfs.exceptions.FactoryInitializationException;
import com.feup.sfs.factory.Factory;
import com.feup.sfs.modbus.ModbusSlave;

public class Machine extends Conveyor {
	private double rot = 300;
	private double tx = 0;
	private double ty = 0;
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

		ModbusSlave.getSimpleProcessImage().addDigitalOut(new SimpleDigitalOut(false));//X-
		ModbusSlave.getSimpleProcessImage().addDigitalOut(new SimpleDigitalOut(false));//X+
		ModbusSlave.getSimpleProcessImage().addDigitalIn(new SimpleDigitalIn(false));  //X- Sensor
		ModbusSlave.getSimpleProcessImage().addDigitalIn(new SimpleDigitalIn(false));  //X+ Sensor

		ModbusSlave.getSimpleProcessImage().addDigitalOut(new SimpleDigitalOut(false));//Y-
		ModbusSlave.getSimpleProcessImage().addDigitalOut(new SimpleDigitalOut(false));//Y+
		ModbusSlave.getSimpleProcessImage().addDigitalIn(new SimpleDigitalIn(false));  //Y- Sensor
		ModbusSlave.getSimpleProcessImage().addDigitalIn(new SimpleDigitalIn(false));  //Y+ Sensor
		
		wasWorking = false;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
				
		paintLight(g, false, 0, getDigitalOut(2), 1);		
		paintLight(g, true, 1, getDigitalIn(sensors), 1);
		paintLight(g, false, 2, getDigitalOut(3), 1);
		paintLight(g, false, 3, getDigitalOut(4), 1);

		paintLight(g, false, 0, getDigitalOut(5), 2);
		paintLight(g, false, 3, getDigitalOut(6), 2);
		paintLight(g, true, 1, getDigitalIn(sensors + 1), 2);
		paintLight(g, true, 2, getDigitalIn(sensors + 2), 2);
	
		paintLight(g, false, 0, getDigitalOut(7), 3);
		paintLight(g, false, 3, getDigitalOut(8), 3);
		paintLight(g, true, 1, getDigitalIn(sensors + 3), 3);
		paintLight(g, true, 2, getDigitalIn(sensors + 4), 3);
}
	
	@Override
	public void paintTop(Graphics g) {
		Rectangle bounds = getBounds();
		int centerX = bounds.x + bounds.width / 2;
		int centerY = bounds.y + bounds.height / 2;
		int dFront = (int) (tx / getFactory().getPixelSize());
		int dSide = (int) (ty / getFactory().getPixelSize());
		int mWidth = (int) (1.5 / getFactory().getPixelSize()); 
		int mHeight = (int) (0.4 / getFactory().getPixelSize()); 
		int toolSize = (int) (0.3 / getFactory().getPixelSize()); 
		double cosine2 = Math.cos((rot+90)*Math.PI/180);
		double cosine3 = Math.cos((rot+210)*Math.PI/180);
		double cosine1 = Math.cos((rot-30)*Math.PI/180);
		double sine2 = Math.sin((rot+90)*Math.PI/180);
		double sine3 = Math.sin((rot+210)*Math.PI/180);
		double sine1 = Math.sin((rot-30)*Math.PI/180);

		if (orientation == Direction.VERTICAL) {
			if (isToolWorking()) g.setColor(Color.green); else g.setColor(Color.darkGray);
			g.fillRect(bounds.x - mHeight * 3, centerY - mHeight *2, mWidth * 2 / 3, mWidth);	// Base
			g.fillRect(bounds.x - mHeight, dSide + centerY - mHeight / 2, bounds.width / 2 + mHeight / 2 + dFront, mHeight);	// Arm
			g.fillRect(dFront + centerX - mHeight / 2, dSide + centerY - mWidth / 2, mHeight, mWidth);	// Tools
			g.setColor(getFactory().getToolColor(tools[0]));
			if (sine1 > 0) g.fillOval(dFront + centerX - toolSize / 2, (int) ((dSide + centerY - toolSize / 2) + mWidth * cosine1 / 2), (int)(toolSize + toolSize * sine1 / 3), (int)(toolSize + toolSize * sine1 / 3));
			g.setColor(getFactory().getToolColor(tools[1]));
			if (sine2 > 0) g.fillOval(dFront +centerX - toolSize / 2, (int) ((dSide + centerY - toolSize / 2) + mWidth * cosine2 / 2), (int)(toolSize + toolSize * sine2 / 3), (int)(toolSize + toolSize * sine2 / 3));
			g.setColor(getFactory().getToolColor(tools[2]));
			if (sine3 > 0) g.fillOval(dFront +centerX - toolSize / 2, (int) ((dSide + centerY - toolSize / 2) + mWidth * cosine3 / 2), (int)(toolSize + toolSize * sine3 / 3), (int)(toolSize + toolSize * sine3 / 3));
		}

		if (orientation == Direction.HORIZONTAL) {
			if (isToolWorking()) g.setColor(Color.green); else g.setColor(Color.darkGray);
			g.fillRect(centerX - mHeight * 2, bounds.y - mHeight * 3, mWidth, mWidth * 2 / 3);	// Base
			g.fillRect(centerX - mHeight / 2, bounds.y - mHeight, mHeight, dFront + bounds.height / 2 + mHeight / 2);	// Arm
			g.fillRect(dSide + centerX - mWidth / 2, dFront + centerY - mHeight / 2, mWidth, mHeight);	// Tools
			g.setColor(getFactory().getToolColor(tools[0]));
			if (sine1 > 0) g.fillOval((int) (dSide + centerX - toolSize / 2 + mWidth * cosine1 / 2), dFront + centerY - toolSize / 2, (int)(toolSize + toolSize * sine1 / 3), (int)(toolSize + toolSize * sine1 / 3));
			g.setColor(getFactory().getToolColor(tools[1]));
			if (sine2 > 0) g.fillOval((int) (dSide + centerX - toolSize / 2 + mWidth * cosine2 / 2), dFront + centerY - toolSize / 2, (int)(toolSize + toolSize * sine2 / 3), (int)(toolSize + toolSize * sine2 / 3));
			g.setColor(getFactory().getToolColor(tools[2]));
			if (sine3 > 0) g.fillOval((int) (dSide + centerX - toolSize / 2 + mWidth * cosine3 / 2), dFront + centerY - toolSize / 2, (int)(toolSize + toolSize * sine3 / 3), (int)(toolSize + toolSize * sine3 / 3));
		}		
	}

	private boolean isToolWorking() {
		return getDigitalOut(4);
	}

	@Override
	public void doStep(boolean conveyorBlocked) {
		if (facilityError) return;
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

		boolean forcing = false;

		if (isMovingFront()) {
			tx += 1 * getFactory().getToolMoveSpeed();
			if (tx >= 0.5) {tx = 0.5; forcing = true;}
		}

		if (isMovingBack()) {
			tx -= 1 * getFactory().getToolMoveSpeed();
			if (tx <= -0.5) {tx = -0.5; forcing = true;}
		}

		if (tx == -0.5) setDigitalIn(sensors + 1, true); else setDigitalIn(sensors + 1, false);
		if (tx == 0.5) setDigitalIn(sensors + 2, true); else setDigitalIn(sensors + 2, false);

		if (isMovingRight()) {
			ty += 1 * getFactory().getToolMoveSpeed();
			if (ty >= 0.5) {ty = 0.5; forcing = true;}
		}

		if (isMovingLeft()) {
			ty -= 1 * getFactory().getToolMoveSpeed();
			if (ty <= -0.5) {ty = -0.5; forcing = true;}
		}

		if (ty == -0.5) setDigitalIn(sensors + 3, true); else setDigitalIn(sensors + 3, false);
		if (ty == 0.5) setDigitalIn(sensors + 4, true); else setDigitalIn(sensors + 4, false);
		
		isForcing(forcing);
		
		if (inPlaceTool(0)) setDigitalIn(sensors, true);
		else if (inPlaceTool(1)) setDigitalIn(sensors, true);
		else if (inPlaceTool(2)) setDigitalIn(sensors, true);
		else setDigitalIn(sensors, false);
		

	}

	private boolean inPlaceTool(int i) {
		if (i == 0) return Math.abs(rot - 300) <= 10;
		if (i == 1) return Math.abs(rot - 180) <= 10;
		if (i == 2) return Math.abs(rot - 60) <= 10;
		return false;
	}

	private boolean isMovingFront() {
		return getDigitalOut(5) && !getDigitalOut(6);
	}

	private boolean isMovingBack() {
		return getDigitalOut(6) && !getDigitalOut(5);
	}

	private boolean isMovingLeft() {
		return getDigitalOut(7) && !getDigitalOut(8);
	}

	private boolean isMovingRight() {
		return getDigitalOut(8) && !getDigitalOut(7);
	}
	
	private boolean isRotatingClockWise() {
		return getDigitalOut(2) && !getDigitalOut(3);
	}

	private boolean isRotatingAntiClockWise() {
		return getDigitalOut(3) && !getDigitalOut(2);
	}
	
	@Override
	public String getName() {
		if (inPlaceTool(0)) return "Machine (T1:"+tools[0]+") " + Factory.getInstance().getTransformations(tools[0]);
		if (inPlaceTool(1)) return "Machine (T2:"+tools[1]+") " + Factory.getInstance().getTransformations(tools[1]);
		if (inPlaceTool(2)) return "Machine (T3:"+tools[2]+") " + Factory.getInstance().getTransformations(tools[2]);
		return "Machine";
	}
	
	@Override
	public int getNumberDigitalIns() {return sensors + 5;}

	@Override
	public int getNumberDigitalOuts() {return 9;}

	@Override
	public Collection<String> getActions() {
		ArrayList<String> actions = new ArrayList<String>();
		actions.add("Motor +");
		actions.add("Motor -");
		actions.add("Rotate +");
		actions.add("Rotate -");
		actions.add("Tool");
		actions.add("X+");
		actions.add("X-");
		actions.add("Z+");
		actions.add("Z-");
		return actions;
	}

	@Override
	public void doAction(String actionName) {
		if (actionName.equals("Rotate +")) setDigitalOut(2, !getDigitalOut(2));
		if (actionName.equals("Rotate -")) setDigitalOut(3, !getDigitalOut(3));
		if (actionName.equals("Tool")) setDigitalOut(4, !getDigitalOut(4));
		if (actionName.equals("X+")) setDigitalOut(5, !getDigitalOut(5));
		if (actionName.equals("X-")) setDigitalOut(6, !getDigitalOut(6));
		if (actionName.equals("Z+")) setDigitalOut(7, !getDigitalOut(7));
		if (actionName.equals("Z-")) setDigitalOut(8, !getDigitalOut(8));
		super.doAction(actionName);
	}

}
