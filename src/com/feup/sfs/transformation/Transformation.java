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

package com.feup.sfs.transformation;

public class Transformation {
	private int initial;
	private int tool;
	private int result;
	private int duration;

	public Transformation(int initial, int result, int tool, int duration) {
		this.initial = initial;
		this.tool = tool;
		this.result = result;
		this.setDuration(duration);
	}
	
	public void setInitial(int initial) {
		this.initial = initial;
	}
	
	public int getInitial() {
		return initial;
	}
	
	public void setTool(int tool) {
		this.tool = tool;
	}
	
	public int getTool() {
		return tool;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
	public int getResult() {
		return result;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}
}
