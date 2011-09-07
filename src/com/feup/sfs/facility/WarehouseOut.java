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

import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleInputRegister;

import com.feup.sfs.block.Block;
import com.feup.sfs.exceptions.FactoryInitializationException;
import com.feup.sfs.factory.Factory;

public class WarehouseOut extends Conveyor{
	private int warehouse;
	private int lastValue;
	
	public WarehouseOut(Properties properties, int id)	throws FactoryInitializationException {
		super(properties, id);
		this.name = "Warehouse Out";
		
		addRegister(new SimpleInputRegister(0), "Warehouse Out");
		this.warehouse = new Integer(properties.getProperty("facility."+id+".warehouse")).intValue();
		this.lastValue = 0;
	}

	public void doStep(boolean conveyorBlocked){
		super.doStep(conveyorBlocked);
		if (facilityError) return;
		int newValue = getRegister(0);
		if (newValue != 0 && lastValue == 0) Factory.getInstance().getWarehouse(warehouse).addOrder(this, newValue);
		lastValue = newValue;
	}

	public boolean isClear() {
		for (Block block : Factory.getInstance().getBlocks()) {
			if (block.getDistanceTo(getCenterX(), getCenterY()) < Factory.getInstance().getBlockSize()) return false;
		} 
		return true;
	}
}
