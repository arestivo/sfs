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

package com.feup.sfs.warehouse;

import com.feup.sfs.facility.WarehouseOut;

public class OrderOut extends Order{
	private int blocktype;
	private WarehouseOut out;
	
	public OrderOut(int blocktype, int time, WarehouseOut out) {
		super(time);
		this.blocktype = blocktype;
		this.setOut(out);
	}

	public void setBlocktype(int blocktype) {
		this.blocktype = blocktype;
	}
	
	public int getBlocktype() {
		return blocktype;
	}
	
	public void setOut(WarehouseOut out) {
		this.out = out;
	}

	public WarehouseOut getOut() {
		return out;
	}
}