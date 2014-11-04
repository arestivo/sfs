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
import java.awt.Graphics;
import java.awt.Rectangle;

import com.feup.sfs.block.BlockType.SHAPE;
import com.feup.sfs.factory.Factory;

public class Block {

	private Factory factory;
	private int type;
	private int nextType;

	private double moveLeft;
	private double moveTop;
	private double moveBottom;
	private double moveRight;

	private double centerX;
	private double centerY;
	private double oldCenterX;
	private double oldCenterY;

	private long duration;
	private long currentWork = 0;

	private boolean onTheFloor = false;
	private int timeOnTheFloor = 0;

	private double height;

	public Block(Factory factory, int type, double centerX, double centerY) {
		this.factory = factory;
		this.type = type;
		this.setHeight(1);
		this.setNextType(-1);
		this.setCenterX(centerX);
		this.setCenterY(centerY);
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	public Factory getFactory() {
		return factory;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void resetMovements() {
		moveBottom = 0;
		moveLeft = 0;
		moveRight = 0;
		moveTop = 0;
	}

	public void doStep() {
		if (onTheFloor) {
			timeOnTheFloor += getFactory().getSimulationTime();
			return;
		}
		oldCenterX = getCenterX();
		oldCenterY = getCenterY();
		
		setCenterX(getCenterX() - moveLeft + moveRight);
		setCenterY(getCenterY() - moveTop + moveBottom);
	}

	public void undoStep() {
		setCenterX(oldCenterX);
		setCenterY(oldCenterY);
	}

	private Color getColor(int id) {
		BlockType type = BlockType.getBlockType(id);
		return type.getColor();
	}

	public void paint(Graphics g) {
		g.setColor(getColor(type));

		SHAPE shape = BlockType.getBlockType(type).getShape();

		switch (shape) {
		case ROUNDED_SQUARE:
			g.fillRoundRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height, getBounds().width / 2, getBounds().height / 2);
			break;
		case SQUARE:
			g.fillRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
			break;
		case CIRCLE:
			g.fillOval(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
			break;
		}

		if (nextType != -1) {
			g.setColor(Color.red);
			g.drawOval(getBounds().x - 1, getBounds().y - 1, getBounds().width + 2, getBounds().height + 2);
		}

		if (nextType != -1) {
			g.setColor(getColor(nextType));
			int angle = (int) (360 * ((double) (duration - currentWork) / duration));
			while (angle < 0)
				angle += 360;
			while (angle > 360)
				angle -= 360;
			angle = 360 - angle;
			g.fillArc(getBounds().x, getBounds().y, getBounds().width, getBounds().height, 0, angle);
		}

		g.setColor(Color.black);
		switch (shape) {
		case ROUNDED_SQUARE:
			g.drawRoundRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height, getBounds().width / 2, getBounds().height / 2);
			break;
		case SQUARE:
			g.drawRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
			break;
		case CIRCLE:
			g.drawOval(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
			break;
		}
	}

	public Rectangle getBounds() {
		double blockSize = factory.getBlockSize();
		if (onTheFloor)
			blockSize /= 2;
		else
			blockSize *= height;
		double pixelSize = factory.getPixelSize();
		int x = (int) ((getCenterX() - blockSize / 2) / pixelSize);
		int y = (int) ((getCenterY() - blockSize / 2) / pixelSize);
		int w = (int) (blockSize / pixelSize);
		int h = (int) (blockSize / pixelSize);
		return new Rectangle(x, y, w, h);
	}

	public void setMoveLeft(double m) {
		moveLeft = m;
	}

	public void setMoveRight(double m) {
		moveRight = m;
	}

	public void setMoveTop(double m) {
		moveTop = m;
	}

	public void setMoveBottom(double m) {
		moveBottom = m;
	}

	public double getDistanceTo(double x, double y) {
		double xpart = getCenterX() - x; xpart *= xpart;
		double ypart = getCenterY() - y; ypart *= ypart;
		return Math.sqrt(xpart + ypart);
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public double getCenterX() {
		return centerX;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	public double getCenterY() {
		return centerY;
	}

	public void setNextType(int nextType) {
		this.nextType = nextType;
	}

	public int getNextType() {
		return nextType;
	}

	public void doWork(int tool, long step) {
		if (type == 0)
			return;
		int transformation = getFactory().getTransformation(type, tool);
		duration = getFactory().getTransformationTime(type, tool);
		if (nextType != -1 && nextType != transformation) {
			type = 0;
			nextType = -1;
		} else if (nextType == -1)
			nextType = transformation;
		if (nextType == transformation)
			currentWork += step;
		if (Math.abs(currentWork) > duration + duration / 10) {
			type = 0;
			nextType = -1;
		}
	}

	public void stopWork() {
		if (type == 0) {
			nextType = -1;
			return;
		}
		if (Math.abs(currentWork - duration) < duration / 10) {
			type = nextType;
			nextType = -1;
			currentWork = 0;
		} else if (currentWork > duration + duration / 10) {
			type = 0;
			nextType = -1;
			currentWork = 0;
		}
	}

	public void setOnTheFloor(boolean onTheFloor) {
		this.onTheFloor = onTheFloor;
	}

	public boolean isOnTheFloor() {
		return onTheFloor;
	}

	public int getTimeOnTheFloor() {
		return timeOnTheFloor;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getHeight() {
		return height;
	}

	public Rectangle getCenterBounds() {
		return new Rectangle(getBounds().x + getBounds().width / 4, getBounds().y + getBounds().height / 4, getBounds().width / 2, getBounds().height / 2);
	}
}
