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
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;
import net.wimpi.modbus.procimg.SimpleInputRegister;

import com.feup.sfs.factory.Factory;
import com.feup.sfs.modbus.ModbusSlave;

public abstract class Facility {
	private int id;
	
	private int digitalOutStart, digitalOutEnd;
	private int digitalInStart, digitalInEnd;
	private int registerStart, registerEnd;

	private LinkedList<String> digitalOutNames = new LinkedList<String>();
	private LinkedList<String> digitalInNames = new LinkedList<String>();
	private LinkedList<String> registerNames = new LinkedList<String>();
	
	private int timeForcing;
	
	protected boolean facilityError;

	protected String name;
	private String alias; 
	
	public Factory getFactory(){
		return Factory.getInstance();
	}
	
	public Facility(Properties properties, int id, String name){
		this.id = id;
		this.name = name;
		
		alias = properties.getProperty("facility."+id+".alias", name + " #" + id);

		digitalInStart = digitalInEnd = ModbusSlave.getSimpleProcessImage().getDigitalInCount();
		digitalOutStart = digitalOutEnd = ModbusSlave.getSimpleProcessImage().getDigitalOutCount();
		registerStart = registerEnd = ModbusSlave.getSimpleProcessImage().getRegisterCount();
		
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

	protected void paintLight(Graphics g, boolean type, int position, boolean value, int line) {
		double pixelSize = Factory.getInstance().getPixelSize();
		int x = (int) ((position + 0.5) * .2 / pixelSize);
		int y = (int) ((line + 0.5) * .2 / pixelSize);
		if (value) g.setColor(Color.green); else g.setColor(Color.red);
		g.fillRect(getBounds().x + x, getBounds().y + y, (int)(.1 / pixelSize), (int)(.1 / pixelSize));
		g.setColor(Color.black);
		if (type) g.drawRect(getBounds().x + x - 1, getBounds().y + y - 1, (int)(.1 / pixelSize) + 1, (int)(.1 / pixelSize) + 1);
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
		return "#" + getId() + " " + getName() + getMessage() + " (" + digitalInStart + ":" + (digitalInStart + getNumberDigitalIns() - 1) + " - " + digitalOutStart + ":" + (digitalOutStart + getNumberDigitalOuts() - 1) + ")";
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return "";
	}
	
	protected void addDigitalOut(SimpleDigitalOut simpleDigitalOut, String name) {
		ModbusSlave.getSimpleProcessImage().addDigitalOut(simpleDigitalOut);
		digitalOutNames.add(name);
		digitalOutEnd++;
	}

	protected void addDigitalIn(SimpleDigitalIn simpleDigitalIn, String name) {
		ModbusSlave.getSimpleProcessImage().addDigitalIn(simpleDigitalIn);
		digitalInNames.add(name);
		digitalInEnd++;
	}

	protected void addRegister(SimpleInputRegister simpleInputRegister, String name) {
		ModbusSlave.getSimpleProcessImage().addRegister(simpleInputRegister);
		registerNames.add(name);
		registerEnd++;
	}

	public int getNumberDigitalIns() { return digitalInEnd - digitalInStart; }
	public int getNumberDigitalOuts() {	return digitalOutEnd - digitalOutStart; }
	public int getNumberRegisters() { return registerEnd - registerStart; }

	public int getFirstDigitalIn() { return digitalInStart; }
	public int getFirstDigitalOut() { return digitalOutStart; }
	public int getFirstRegister() {	return registerStart; }

	public final Collection<String> getActions() {
		return digitalOutNames;
	}

	public final void doAction(String actionName) {
		int out = getDigitalOutForName(actionName);
		setDigitalOut(out, !getDigitalOut(out));
	}

	private int getDigitalOutForName(String actionName) {
		return digitalOutNames.indexOf(actionName);
	}

	public void stop() {
		int ndo = getNumberDigitalOuts();
		for (int i = 0; i < ndo; i++) setDigitalOut(i, false);
	}

	public void writeMap(PrintStream ps) {
		ps.println("-----------------------------------------");
		ps.println("Facility #" + getId() + " : " + getName());
		ps.println("");
		ps.println("  Digital Outs");
		ps.println("  ------------");
		for (int i = digitalOutStart; i < digitalOutEnd; i++)
			ps.println ("   " + i + " : " + digitalOutNames.get(i - digitalOutStart));
		ps.println("");
		ps.println("  Digital Ins");
		ps.println("  -----------");
		for (int i = digitalInStart; i < digitalInEnd; i++)
			ps.println ("   " + i + " : " + digitalInNames.get(i - digitalInStart));
		if (registerStart != registerEnd) {
			ps.println("");
			ps.println("  Registers");
			ps.println("  ---------");
			for (int i = registerStart; i < registerEnd; i++)
				ps.println ("   " + i + " : " + registerNames.get(i - registerStart));
		}
	}

	public void writeCsv(PrintStream ps) {
		for (int i = digitalOutStart; i < digitalOutEnd; i++)
			ps.println (getId() + "," + getAlias() + "," + getName() + ",O," + digitalOutNames.get(i - digitalOutStart) + "," + i);
		for (int i = digitalInStart; i < digitalInEnd; i++)
			ps.println (getId() + "," + getAlias() + "," + getName() + ",I," + digitalInNames.get(i - digitalInStart) + "," + i);
		for (int i = registerStart; i < registerEnd; i++)
			ps.println (getId() + "," + getAlias() + "," + getName() + ",R," + registerNames.get(i - registerStart) + "," + i);
	}
	
	private String getAlias() {
		return alias;
	}

	public boolean canAddBlocks() {
		return true;
	}
}
