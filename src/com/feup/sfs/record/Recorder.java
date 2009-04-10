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

package com.feup.sfs.record;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Vector;

import com.feup.sfs.facility.Facility;
import com.feup.sfs.factory.Factory;

public class Recorder {
	private boolean initialized = false;
	private Vector<Integer> inputs = new Vector<Integer>();;
	private Vector<Integer> outputs = new Vector<Integer>();;
	private Vector<Integer> registers = new Vector<Integer>();;
	private PrintStream ps = null;
	
	public Recorder(String file) throws FileNotFoundException {
		System.out.println("Recording to: " + file);
		ps = new PrintStream(new FileOutputStream(file));
		int seed = Factory.generateRandom(0, 100000);
		Factory.setRandomSeed(seed);
		ps.println(seed);
	}
	
	private void init() {
		ArrayList<Facility> facilities = Factory.getInstance().getFacilities();
		for (Facility facility : facilities) {
			for (int d = 0; d < facility.getNumberDigitalIns(); d++)
				inputs.add(new Integer(facility.getDigitalIn(d)?1:0));

			for (int d = 0; d < facility.getNumberDigitalOuts(); d++)
				outputs.add(new Integer(facility.getDigitalOut(d)?1:0));

			for (int d = 0; d < facility.getNumberRegisters(); d++)
				registers.add(new Integer(facility.getRegister(d)));
		}
		initialized = true;
	}
	
	private void printDeltas(long l) {
		ArrayList<Facility> facilities = Factory.getInstance().getFacilities();
		int i = 0, o = 0, r = 0;
		for (Facility facility : facilities) {
			for (int d = 0; d < facility.getNumberDigitalIns(); d++) {
				int v = facility.getDigitalIn(d)?1:0;
				if (inputs.elementAt(i).intValue()!=v) {
					ps.println(l + " IN " + i + " " + v);
					inputs.setElementAt(new Integer(v), i);
				}
				i++;
			}
			for (int d = 0; d < facility.getNumberDigitalOuts(); d++) {
				int v = facility.getDigitalOut(d)?1:0;
				if (outputs.elementAt(o).intValue()!=v) {
					ps.println(l + " OUT " + o + " " + v);
					outputs.setElementAt(new Integer(v), o);
				}
				o++;
			}
			for (int d = 0; d < facility.getNumberRegisters(); d++) {
				int v = facility.getRegister(d);
				if (registers.elementAt(r).intValue()!=v) {
					ps.println(l + " REG " + r + " " + v);
					registers.setElementAt(new Integer(v), r);
				}
				r++;
			}

		}		
	}
	
	public void record(long l) {
		if (!initialized) init();
		else printDeltas(l);
	}
}
