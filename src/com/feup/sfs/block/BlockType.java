package com.feup.sfs.block;

import java.awt.Color;
import java.util.Hashtable;

public class BlockType {
	private String name;
	private Color color;
	private int id;
	
	private static Hashtable<Integer, BlockType> types = new Hashtable<Integer, BlockType>();
	
	public BlockType(int id, String name, Color color) {
		setId(id);
		setName(name);
		setColor(color);
	}

	public static void addType(int id, String name, String color){
		BlockType type = new BlockType(id, name, Color.decode("0x"+color));
		types.put(new Integer(id), type);
	}
	
	public static BlockType getBlockType(int id){
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
}
