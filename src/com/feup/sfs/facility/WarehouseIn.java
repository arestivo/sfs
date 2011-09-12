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

import java.awt.Graphics;
import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleDigitalOut;

import com.feup.sfs.exceptions.FactoryInitializationException;
import com.feup.sfs.factory.Factory;
import com.feup.sfs.warehouse.Warehouse;

public class WarehouseIn extends Conveyor{
	private int warehouse;
	private boolean lastvalue = false;
	
	public WarehouseIn(Properties properties, int id)	throws FactoryInitializationException {
		super(properties, id, "WarehouseIn");
		
		this.warehouse = new Integer(properties.getProperty("facility."+id+".warehouse")).intValue();
		
		addDigitalOut(new SimpleDigitalOut(false), "Warehouse In");
	}

	public void doStep(boolean conveyorBlocked){
		super.doStep(conveyorBlocked);
		if (facilityError) return;
		boolean newvalue = isInOn();
		if (!lastvalue && newvalue) {
			Warehouse w = Factory.getInstance().getWarehouse(warehouse);
			w.addOrder(this);
		}
		lastvalue = newvalue;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintLight(g, false, 0, isInOn(), 1);
	}

	private boolean isInOn() {
		return getDigitalOut(2);
	}
}
