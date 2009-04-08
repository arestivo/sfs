package com.feup.sfs.facility;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;

import com.feup.sfs.exceptions.FactoryInitializationException;
import com.feup.sfs.modbus.ModbusSlave;

public class Rail extends Conveyor {
	private int railSize;
	private double railPosition = 0;
	
	public Rail(Properties properties, int id) throws FactoryInitializationException {
		super(properties, id);

		setRailSize(new Integer(properties.getProperty("facility."+id+".rail.size")).intValue());
		
		ModbusSlave.getSimpleProcessImage().addDigitalOut(new SimpleDigitalOut(false));//R-
		ModbusSlave.getSimpleProcessImage().addDigitalOut(new SimpleDigitalOut(true));//R+
		ModbusSlave.getSimpleProcessImage().addDigitalIn(new SimpleDigitalIn(false)); // R- Sensor	
		ModbusSlave.getSimpleProcessImage().addDigitalIn(new SimpleDigitalIn(false)); // R+ Sensor	
	}
	
	private void paintConveyor(Graphics g){
		super.paint(g);
		paintLight(g, false, 0, getDigitalOut(2), 1);
		paintLight(g, true, 1, getDigitalIn(1), 1);
		paintLight(g, true, 2, getDigitalIn(2), 1);
		paintLight(g, false, 3, getDigitalOut(3), 1);
	}
	
	public void paint(Graphics g){
		
		Rectangle bounds = getBounds();
		g.setColor(Color.darkGray);
		
		double pixelSize = getFactory().getPixelSize();
		int railSize = (int) (this.railSize / pixelSize);
		
		if (getOrientation()==Direction.VERTICAL) {
			g.drawLine(bounds.x - railSize / 2 + bounds.width / 2, bounds.y + bounds.height / 4, bounds.x + railSize / 2 + bounds.width / 2, bounds.y + bounds.height / 4);
			g.drawLine(bounds.x - railSize / 2 + bounds.width / 2, bounds.y + 3 * bounds.height / 4, bounds.x + railSize / 2 + bounds.width / 2 , bounds.y + 3 * bounds.height / 4);
		} else {
			g.drawLine(bounds.x + bounds.width / 4, bounds.y - railSize / 2 + bounds.height / 2, bounds.x + bounds.width / 4, bounds.y + railSize / 2 + bounds.height / 2);
			g.drawLine(bounds.x + 3 * bounds.width / 4, bounds.y - railSize / 2 + bounds.height / 2, bounds.x + 3 * bounds.width / 4, bounds.y + railSize / 2 + bounds.height / 2);
		}
	
		if (getOrientation()==Direction.VERTICAL) {
			double oldCenter = getCenterX();
			setCenterX(oldCenter + railPosition);
			paintConveyor(g);
			setCenterX(oldCenter);
		} else {
			double oldCenter = getCenterY();
			setCenterY(oldCenter + railPosition);
			paintConveyor(g);
			setCenterY(oldCenter);			
		}
	}

	@Override
	public void doStep(boolean conveyorBlocked){
		if (facilityError) return;
		boolean forcing = false;
		super.doStep(true);
		
		double speed = getFactory().getConveyorSpeed()*getFactory().getSimulationTime()/1000;
		
		if (isRailMovingLeft()) railPosition -= speed;
		if (isRailMovingRight()) railPosition += speed;
		
		if (railPosition < -railSize / 2) {railPosition = -railSize / 2; forcing = true;}
		if (railPosition > railSize / 2) {railPosition = railSize / 2; forcing = true;}
		
		isForcing(forcing);
	}
	
	private boolean isRailMovingLeft() {
		return getDigitalOut(2) && !getDigitalOut(3);
	}

	private boolean isRailMovingRight() {
		return getDigitalOut(3) && !getDigitalOut(2);
	}

	@Override
	public String getName() {
		return "Rail";
	}

	@Override
	public int getNumberDigitalIns() {return 3;}

	@Override
	public int getNumberDigitalOuts() {return 4;}

	public void setRailSize(int railSize) {
		this.railSize = railSize;
	}

	public int getRailSize() {
		return railSize;
	}

	
}
