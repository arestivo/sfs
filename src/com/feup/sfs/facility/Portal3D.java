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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;

import com.feup.sfs.exceptions.FactoryInitializationException;
import com.feup.sfs.factory.Factory;

public class Portal3D extends Facility{
	private static double wallthickness = 0.3;
	
	private double centerX;
	private double centerY;
	protected double width;
	protected double height;
	protected int sensorsx;
	protected int sensorsy;
	protected double positionx;
	protected double positiony;
	
	public Portal3D(Properties properties, int id) throws FactoryInitializationException {
		super(id);
		this.name = "Portal3D";

		setCenterX(new Double(properties.getProperty("facility."+id+".center.x")).doubleValue());
		setCenterY(new Double(properties.getProperty("facility."+id+".center.y")).doubleValue());
		width = new Double(properties.getProperty("facility."+id+".width")).doubleValue();
		height = new Double(properties.getProperty("facility."+id+".height")).doubleValue();
		sensorsx = new Integer(properties.getProperty("facility."+id+".sensorsx", "1")).intValue();
		sensorsy = new Integer(properties.getProperty("facility."+id+".sensorsy", "1")).intValue();

		positionx = Factory.generateRandom(0, width);
		positiony = Factory.generateRandom(0, height);
		
		addDigitalOut(new SimpleDigitalOut(false), "MotorX +");
		addDigitalOut(new SimpleDigitalOut(false), "MotorX -");
		addDigitalOut(new SimpleDigitalOut(false), "MotorY +");
		addDigitalOut(new SimpleDigitalOut(false), "MotorY -");
		
		for (int i = 0; i < sensorsx; i++)
			addDigitalIn(new SimpleDigitalIn(false), "Sensor X " + i);

		for (int i = 0; i < sensorsy; i++)
			addDigitalIn(new SimpleDigitalIn(false), "Sensor Y " + i);
	}
	
	@Override
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		Rectangle bounds = getBounds();
		double pixelSize = getFactory().getPixelSize();
		int wt = (int) (wallthickness / pixelSize);

		g2.setStroke(new BasicStroke(wt));
		g2.setColor(Color.lightGray);
		g2.drawRect(bounds.x - wt, bounds.y - wt, bounds.width + wt * 2, bounds.height + wt * 2);		
		g2.setStroke(new BasicStroke(1));

		if (facilityError) g2.setColor(Color.red);
		else g2.setColor(Color.black);
		g2.drawRect(bounds.x - wt, bounds.y - wt, bounds.width + wt * 2, bounds.height + wt * 2);
		
		g2.setStroke(new BasicStroke(wt));
		g2.setColor(Color.darkGray);
		g2.drawLine((int)(bounds.x + positionx / pixelSize), bounds.y - wt, (int)(bounds.x + positionx / pixelSize), bounds.y + bounds.height + wt);
		g2.drawLine(bounds.x - wt, (int)(bounds.y + positiony / pixelSize), bounds.x + bounds.width + wt, (int)(bounds.y + positiony / pixelSize));
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.white);
		g2.drawLine((int)(bounds.x + positionx / pixelSize), bounds.y - wt, (int)(bounds.x + positionx / pixelSize), bounds.y + bounds.height + wt);
		g2.drawLine(bounds.x - wt, (int)(bounds.y + positiony / pixelSize), bounds.x + bounds.width + wt, (int)(bounds.y + positiony / pixelSize));

		g2.setStroke(new BasicStroke(wt));
		g2.setColor(Color.darkGray);
		g2.drawRect((int)(bounds.x + positionx / pixelSize - 1 / pixelSize), (int)(bounds.y + positiony / pixelSize - 1 / pixelSize), (int)(2 / pixelSize), (int)(2 / pixelSize));
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.white);
		g2.drawRect((int)(bounds.x + positionx / pixelSize - 1 / pixelSize), (int)(bounds.y + positiony / pixelSize - 1 / pixelSize), (int)(2 / pixelSize), (int)(2 / pixelSize));
		
		g2.setColor(Color.orange);
		
		for (int i = 0; i < sensorsx; i++) {
			Point sp = getSensorBoundsX(i);
			g.fillRect(sp.x - wt/2 + 1, sp.y - wt/2 + 1, wt - 2, wt - 2);
		}

		for (int i = 0; i < sensorsy; i++) {
			Point sp = getSensorBoundsY(i);
			g.fillRect(sp.x - wt/2 + 1, sp.y - wt/2 + 1, wt - 2, wt - 2);
		}
/*		
		paintLight(g, false, 0, isMotorPlusOn(), 0);
		
		for (int i = 0; i < sensors; i++)
			paintLight(g, true, 1 + i, isSensorActive(i), 0);
		
		paintLight(g, false, 1 + sensors, isMotorMinusOn(), 0);*/
		
	}


	private Point getSensorBoundsX(int i) {
		double sp = getSensorPositionX(i);
		double pixelSize = getFactory().getPixelSize();
		Rectangle bounds = getBounds();

		return new Point((int)(sp / pixelSize), (int)(bounds.y + positiony / pixelSize));
	}

	private Point getSensorBoundsY(int i) {
		double sp = getSensorPositionY(i);
		double pixelSize = getFactory().getPixelSize();
		Rectangle bounds = getBounds();

		return new Point((int)(bounds.x + positionx / pixelSize), (int)(sp / pixelSize));
	}

	public double getSensorPositionX(int i) {
		return getCenterX() + width / sensorsx * i - width / 2 + width / sensorsx / 2;
	}

	public double getSensorPositionY(int i) {
		return getCenterY() + height / sensorsy * i - height / 2 + height / sensorsy / 2;
	}

	
	@Override
	public void doStep(boolean conveyorBlocked){
		double speed = getFactory().getConveyorSpeed()*getFactory().getSimulationTime()/1000;

		if (getDigitalOut(0) && !getDigitalOut(1)) positionx += speed;
		if (getDigitalOut(1) && !getDigitalOut(0)) positionx -= speed;
		if (getDigitalOut(2) && !getDigitalOut(3)) positiony += speed;
		if (getDigitalOut(3) && !getDigitalOut(2)) positiony -= speed;
		
		if (positionx < 0) positionx = 0;
		if (positionx > width) positionx = width;
		if (positiony < 0) positiony = 0;
		if (positiony > height) positiony = height;
		
/*		if (facilityError) return;
		ArrayList<Block> blocks = getFactory().getBlocks();
		boolean middleSensor[] = new boolean[sensors];
		for (Block block : blocks) {
			if (!conveyorBlocked && getBounds().intersects(block.getBounds())){

				if (isRunningLeft()) block.setMoveLeft(true);
				if (isRunningRight()) block.setMoveRight(true);
				if (isRunningTop()) block.setMoveTop(true);
				if (isRunningBottom()) block.setMoveBottom(true);
			}
			for (int i = 0; i < sensors; i++) {
				Point2D.Double sp = getSensorPosition(i);
				if (block.getDistanceTo(sp.x, sp.y) < getFactory().getSensorRadius()) 
					middleSensor[i] = true;
			}
		}
		for (int i = 0; i < sensors; i++) setDigitalIn(i, middleSensor[i]);*/
	}
	
	@Override
	public Rectangle getBounds() {
		double pixelSize = getFactory().getPixelSize();
		int x = (int) (getCenterX()/pixelSize - width/2/pixelSize); 
		int y = (int) (getCenterY()/pixelSize - height/2/pixelSize);
		int w = (int) (width/pixelSize);
		int h = (int) (height/pixelSize);
		return new Rectangle(x, y, w, h);
	}
	
	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public double getCenterX() {
		return centerX;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	public double getCenterY() {
		return centerY;
	}
	
	@Override
	public boolean canAddBlocks() {
		return false;
	}
}