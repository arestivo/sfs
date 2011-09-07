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
import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;

import com.feup.sfs.block.Block;
import com.feup.sfs.exceptions.FactoryInitializationException;

public class Pusher extends Conveyor {
	protected double currentPushPosition = 0;
	protected boolean invert = false;
	
	public Pusher(Properties properties, int id) throws FactoryInitializationException {
		super(properties, id);
		
		if (properties.containsKey("facility."+id+".invert") && properties.getProperty("facility."+id+".invert").equals("true"))
			invert = true;
		
		addDigitalOut(new SimpleDigitalOut(false), "Push -");
		addDigitalOut(new SimpleDigitalOut(false), "Push +");
		addDigitalIn(new SimpleDigitalIn(false), "Push - Sensor");	
		addDigitalIn(new SimpleDigitalIn(false), "Push + Sensor");	
	}
	
	public void paint(Graphics g){
		super.paint(g);

		Rectangle bounds = getBounds();
		g.setColor(Color.darkGray);
		
		double pixelSize = getFactory().getPixelSize();
		double blockSize = getFactory().getBlockSize() / pixelSize;

		if (getOrientation()==Direction.VERTICAL) {
			int centerY = bounds.y + bounds.height / 2; 
			if (!invert) {
				g.fillRect((int)(bounds.x - blockSize / 4 + bounds.width * currentPushPosition / width), (int)(centerY - blockSize / 2), (int)(blockSize / 4), (int)blockSize);
				g.fillRect((int)(bounds.x - blockSize / 4), (int)(centerY - blockSize / 8), (int)(bounds.width * currentPushPosition / width + 1), (int)blockSize / 4);
			} else {
				g.fillRect((int)(bounds.x + bounds.width * (width - currentPushPosition) / width), (int)(centerY - blockSize / 2), (int)(blockSize / 4), (int)blockSize);
				g.fillRect((int)(bounds.x + bounds.width + blockSize / 4) - (int)(bounds.width * currentPushPosition / width + 1), (int)(centerY - blockSize / 8), (int)(bounds.width * currentPushPosition / width + 1), (int)blockSize / 4);
			}
		} else {
			int centerX = bounds.x + bounds.width / 2; 
			if (!invert) {
				g.fillRect((int)(centerX - blockSize / 2), (int)(bounds.y - blockSize / 4 + bounds.height * currentPushPosition / width), (int)blockSize, (int)(blockSize / 4));
				g.fillRect((int)(centerX - blockSize / 8), (int)(bounds.y - blockSize / 4), (int)blockSize / 4, (int)(bounds.height * currentPushPosition / width + 1));
			} else {
				g.fillRect((int)(centerX - blockSize / 2), (int)(bounds.y + bounds.height * (width - currentPushPosition) / width), (int)blockSize, (int)(blockSize / 4));
				g.fillRect((int)(centerX - blockSize / 8), (int)(bounds.y + bounds.height + blockSize / 4) - (int)(bounds.height * currentPushPosition / width + 1), (int)blockSize / 4, (int)(bounds.height * currentPushPosition / width + 1));				
			}
		}
		
		paintLight(g, false, 0, getDigitalOut(2), 1);
		paintLight(g, true, 1, getDigitalIn(sensors), 1);
		paintLight(g, true, 2, getDigitalIn(sensors + 1), 1);
		paintLight(g, false, 3, getDigitalOut(3), 1);
	}

	public Rectangle getPusherBounds() {
		Rectangle bounds = getBounds();
		double pixelSize = getFactory().getPixelSize();
		double blockSize = getFactory().getBlockSize() / pixelSize;
		int centerY = bounds.y + bounds.height / 2; 
		int centerX = bounds.x + bounds.width / 2; 

		if (getOrientation()==Direction.VERTICAL && !invert) 
			return new Rectangle((int)(bounds.x - blockSize / 4 + bounds.width * currentPushPosition / width), (int)(centerY - blockSize / 2), (int)(blockSize / 4), (int)blockSize);
		else if (getOrientation()==Direction.VERTICAL && invert) 
			return new Rectangle((int)(bounds.x + bounds.width * (width - currentPushPosition) / width), (int)(centerY - blockSize / 2), (int)(blockSize / 4), (int)blockSize);
		else if (getOrientation()==Direction.HORIZONTAL && !invert) 
			return new Rectangle((int)(centerX - blockSize / 2), (int)(bounds.y - blockSize / 4 + bounds.height * currentPushPosition / width), (int)blockSize, (int)(blockSize / 4));
		else 
			return new Rectangle((int)(centerX - blockSize / 2), (int)(bounds.y + bounds.height * (width - currentPushPosition) / width), (int)blockSize, (int)(blockSize / 4));
	}
	
	@Override
	public void doStep(boolean conveyorBlocked){
		if (facilityError) return;
		boolean forcing = false;
		super.doStep(currentPushPosition != 0);
		if (isPushing() && !isRetracting()) {
			currentPushPosition += getFactory().getPushSpeed() * getFactory().getSimulationTime() / 1000.0;
			if (currentPushPosition > width) {currentPushPosition = width; forcing = true;}
		}
		if (!isPushing() && isRetracting()) {
			currentPushPosition -= getFactory().getPushSpeed() * getFactory().getSimulationTime() / 1000.0;
			if (currentPushPosition < 0) {currentPushPosition = 0; forcing = true;}
		}
		if (currentPushPosition == 0)   setDigitalIn(sensors, true); else setDigitalIn(sensors, false);
		if (currentPushPosition == width) setDigitalIn(sensors + 1, true); else setDigitalIn(sensors + 1, false);
		
		ArrayList<Block> blocks = getFactory().getBlocks();
		for (Block block : blocks) {
			if (getPusherBounds().intersects(block.getBounds())){
				if (getOrientation()==Direction.VERTICAL && !invert) block.setMoveRight(true);
				if (getOrientation()==Direction.VERTICAL && invert) block.setMoveLeft(true);
				if (getOrientation()==Direction.HORIZONTAL && !invert) block.setMoveBottom(true);
				if (getOrientation()==Direction.HORIZONTAL && invert) block.setMoveTop(true);
			}
		}
		isForcing(forcing);
	}

	private boolean isPushing() {
		return getDigitalOut(3);
	}

	private boolean isRetracting() {
		return getDigitalOut(2);
	}
		
	@Override
	public String getName() {
		return "Pusher";
	}
}