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

public class Rotator extends Conveyor {
	protected double currentRotation = 0;
	protected boolean rotated = false;
	
	public Rotator(Properties properties, int id) throws FactoryInitializationException {
		super(properties, id);
		
		addDigitalOut(new SimpleDigitalOut(false));//R-
		addDigitalOut(new SimpleDigitalOut(false));//R+
		addDigitalIn(new SimpleDigitalIn(false)); // R- Sensor	
		addDigitalIn(new SimpleDigitalIn(false)); // R+ Sensor	
	}
	
	public void paint(Graphics g){
		super.paint(g);
		
		if (currentRotation != 0 && currentRotation != 90) {
			g.setColor(Color.darkGray);
			Rectangle r = getBounds();
			int x1, y1, x2, y2;
			double cosine = Math.cos((double)currentRotation*Math.PI/180);
			double sine = Math.sin((double)currentRotation*Math.PI/180);
			int length = Math.max(r.width, r.height);
	
			if (orientation == Direction.HORIZONTAL) {
				x1 = (int) (r.getCenterX() - length / 2 * cosine) ; x2 = (int) (r.getCenterX() + length /  2* cosine);
				y1 = (int) (r.getCenterY() - length / 2 * sine); y2 = (int) (r.getCenterY() + length / 2 * sine);
			} else {
				x1 = (int) (r.getCenterX() - length / 2 * sine) ; x2 = (int) (r.getCenterX() + length /  2* sine);
				y1 = (int) (r.getCenterY() - length / 2 * cosine); y2 = (int) (r.getCenterY() + length / 2 * cosine);				
			}
			
			g.drawLine(x1, y1, x2, y2);
		}
		
		paintLight(g, false, 0, getDigitalOut(2), 1);
		paintLight(g, true, 1, getDigitalIn(sensors), 1);
		paintLight(g, true, 2, getDigitalIn(sensors + 1), 1);
		paintLight(g, false, 3, getDigitalOut(3), 1);
	}
	
	@Override
	public void doStep(boolean conveyorBlocked){
		if (facilityError) return;
		boolean forcing = false;
		super.doStep(currentRotation != 0 && currentRotation !=90);
		if (isRotatingAntiClockwise() && !isRotatingClockwise()) {
			currentRotation += getFactory().getRotationSpeed()*getFactory().getSimulationTime()/1000;
			if (currentRotation >= 90) {currentRotation = 90; setRotated(true); forcing = true;}
		}
		if (!isRotatingAntiClockwise() && isRotatingClockwise()) {
			currentRotation -= getFactory().getRotationSpeed()*getFactory().getSimulationTime()/1000;
			if (currentRotation <= 0) {currentRotation = 0; setRotated(false); forcing = true;}
		}
		if (currentRotation == 0) setDigitalIn(sensors, true); else setDigitalIn(sensors, false);
		if (currentRotation == 90) setDigitalIn(sensors + 1, true); else setDigitalIn(sensors + 1, false);
		isForcing(forcing);
	}

	private void setRotated(boolean rotated) {
		if (rotated != this.rotated) {
			ArrayList<Block> blocks = getFactory().getBlocks();
			for (Block block : blocks) {
				if (getBounds().contains(block.getBounds().getCenterX(), block.getBounds().getCenterY())){
					double distX = getCenterX() - block.getCenterX();
					double distY = getCenterY() - block.getCenterY();
					block.setCenterX(getCenterX() - distY);
					block.setCenterY(getCenterY() - distX);
				}
			}
		}
		this.rotated = rotated;
	}

	private boolean isRotatingAntiClockwise() {
		return getDigitalOut(3);
	}

	private boolean isRotatingClockwise() {
		return getDigitalOut(2);
	}
	
	@Override 
	public Direction getOrientation(){
		if (!rotated) return orientation;
		if (orientation == Direction.VERTICAL) return Direction.HORIZONTAL;
		else return Direction.VERTICAL;
	}
	
	@Override
	public String getName() {
		return "Rotator";
	}

	@Override
	public Collection<String> getActions() {
		ArrayList<String> actions = new ArrayList<String>();
		actions.add("Motor +");
		actions.add("Motor -");
		actions.add("Rotate +");
		actions.add("Rotate -");
		return actions;
	}

	@Override
	public void doAction(String actionName) {
		if (actionName.equals("Rotate +")) setDigitalOut(2, !getDigitalOut(2));
		if (actionName.equals("Rotate -")) setDigitalOut(3, !getDigitalOut(3));
		super.doAction(actionName);
	}

}
