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
import java.util.Collection;

import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;

import com.feup.sfs.factory.Factory;
import com.feup.sfs.modbus.ModbusSlave;

public abstract class Facility {
	private int id;
	
	protected int digitalOutStart;
	protected int digitalInStart;
	private int registerStart;
	
	private int timeForcing;
	
	protected boolean facilityError; 
	
	public Factory getFactory(){
		return Factory.getInstance();
	}
	
	public Facility(int id){
		this.id = id;
		
		digitalInStart = ModbusSlave.getSimpleProcessImage().getDigitalInCount();
		digitalOutStart = ModbusSlave.getSimpleProcessImage().getDigitalOutCount();
		registerStart = ModbusSlave.getSimpleProcessImage().getRegisterCount();
		
		timeForcing = 0;
		facilityError = false;
	}

	public boolean getDigitalIn(int i) {
		return ModbusSlave.getSimpleProcessImage().getDigitalIn(digitalInStart + i).isSet();
	}
	
	public boolean getDigitalOut(int i) {
		return ModbusSlave.getSimpleProcessImage().getDigitalOut(digitalOutStart + i).isSet();
	}

	protected void setDigitalIn(int i, boolean b) {
		((SimpleDigitalIn)ModbusSlave.getSimpleProcessImage().getDigitalIn(digitalInStart + i)).set(b);
	}

	protected void setDigitalOut(int i, boolean b) {
		((SimpleDigitalOut)ModbusSlave.getSimpleProcessImage().getDigitalOut(digitalOutStart + i)).set(b);
	}
	
	public int getRegister(int i){
		return ModbusSlave.getSimpleProcessImage().getRegister(registerStart + i).getValue();
	}
	
	public int getId(){
		return id;
	}
	
	public abstract void paint(Graphics g);

	public abstract void doStep(boolean conveyorBlocked);
	
	public abstract Rectangle getBounds();

	protected void paintLight(Graphics g, boolean type, int position,
			boolean value, int line) {
				int x = position * 8 + 2;
				int y = line * 8 + 2;
				if (value) g.setColor(Color.green); else g.setColor(Color.red);
				if (type) g.fillOval(getBounds().x + x, getBounds().y + y, 5, 5);
				else g.fillRect(getBounds().x + x, getBounds().y + y, 5, 5);
			}
	
	protected void isForcing(boolean forcing) {
		if (facilityError) return;
		if (forcing) timeForcing += getFactory().getSimulationTime();
		else timeForcing = 0;
		if (timeForcing >= getFactory().getErrorTime()) facilityError = true;
	}

	public void paintTop(Graphics g) {
	}

	public String getType() {
		return getName() + " (" + digitalInStart + ":" + (digitalInStart + getNumberDigitalIns() - 1) + " - " + digitalOutStart + ":" + (digitalOutStart + getNumberDigitalOuts() - 1) + ")";
	}

	public abstract String getName();
	
	public abstract int getNumberDigitalIns();
	public abstract int getNumberDigitalOuts();
	public abstract int getNumberRegisters();

	public int getFirstDigitalIn() {
		return digitalInStart;
	}

	public int getFirstDigitalOut() {
		return digitalOutStart;
	}
	
	public int getFirstRegister() {
		return registerStart;
	}

	public abstract Collection<String> getActions();

	public abstract void doAction(String actionName);
}
