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

package com.feup.sfs.block;

import java.awt.Color;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

public class BlockType {
	public enum SHAPE {
		CIRCLE, SQUARE, ROUNDED_SQUARE
	};

	private String name;
	private Color color;
	private SHAPE shape;
	private int id;

	private static Hashtable<Integer, BlockType> types = new Hashtable<Integer, BlockType>();

	public BlockType(int id, String name, Color color, SHAPE shape) {
		setId(id);
		setName(name);
		setColor(color);
		setShape(shape);
	}

	private void setShape(SHAPE shape) {
		this.shape = shape;
	}

	public static void addType(int id, String name, String color, String shapeName) {
		SHAPE shape;
		if (shapeName.equals("circle"))
			shape = SHAPE.CIRCLE;
		else if (shapeName.equals("square"))
			shape = SHAPE.SQUARE;
		else
			shape = SHAPE.ROUNDED_SQUARE;
		BlockType type = new BlockType(id, name, Color.decode("0x" + color), shape);
		types.put(new Integer(id), type);
	}

	public static BlockType getBlockType(int id) {
		if (!types.containsKey(new Integer(id))) {
			BlockType defunct = new BlockType(-1, "Defunct", Color.black, SHAPE.ROUNDED_SQUARE);
			return defunct;
		}
		return types.get(new Integer(id));
	}

	public static int getNumberBlockTypes() {
		return types.size();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static BlockType getBlockType(String name) {
		Enumeration<BlockType> elements = types.elements();
		while (elements.hasMoreElements()) {
			BlockType type = elements.nextElement();
			if (type.getName().equalsIgnoreCase(name))
				return type;
		}
		BlockType defunct = new BlockType(-1, "Defunct", Color.black, SHAPE.ROUNDED_SQUARE);
		return defunct;
	}

	public static Collection<BlockType> getBlockTypes() {
		return types.values();
	}

	public SHAPE getShape() {
		return shape;
	}
}
