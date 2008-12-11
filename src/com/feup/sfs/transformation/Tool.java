package com.feup.sfs.transformation;

import java.awt.Color;

public class Tool {
	private Color color;

	public Tool(String color) {
		this.color = Color.decode("0x" + color);
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}
