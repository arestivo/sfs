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

public class Order {
	private int timeTillReady;

	public Order(int time) {
		this.timeTillReady = time;
	}

	public void setTimeTillReady(int timeTillReady) {
		this.timeTillReady = timeTillReady;
	}

	public int getTimeTillReady() {
		return timeTillReady;
	}

	public boolean removeTime(long l) {
		timeTillReady -= l;
		return (timeTillReady <= 0);
	}
}
