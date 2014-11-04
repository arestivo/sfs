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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Properties;

import com.feup.sfs.block.Block;
import com.feup.sfs.block.BlockType;
import com.feup.sfs.facility.WarehouseIn;
import com.feup.sfs.facility.WarehouseOut;
import com.feup.sfs.factory.Factory;

public class Warehouse {
	public enum Direction {
		VERTICAL, HORIZONTAL
	}

	private Factory factory;

	private double centerX;
	private double centerY;
	private double width;
	private double length;
	private Direction orientation;

	private boolean error;
	private LinkedList<Order> orders = new LinkedList<Order>();

	private Hashtable<Integer, Integer> stocks = new Hashtable<Integer, Integer>();

	private int mintime;
	private int maxtime;

	public Warehouse(Factory factory, int id, double centerX, double centerY, double width, double length, String orientation, Properties properties) {
		this.factory = factory;
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = width;
		this.length = length;
		this.orientation = (orientation.equals("vertical")) ? Direction.VERTICAL : Direction.HORIZONTAL;

		for (int i = 1; i <= BlockType.getNumberBlockTypes(); i++) {
			Integer stock;
			String strStock = properties.getProperty("warehouse." + id + ".block." + i + ".stock");
			if (strStock == null)
				stock = new Integer(0);
			else
				stock = new Integer(strStock);
			stocks.put(new Integer(i), stock);
		}

		this.mintime = new Integer(properties.getProperty("warehouse." + id + ".mintime")).intValue();
		this.maxtime = new Integer(properties.getProperty("warehouse." + id + ".maxtime")).intValue();

		error = false;
	}

	public void paint(Graphics g) {
		g.setColor(Color.lightGray);
		Rectangle bounds = getBounds();
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		if (error)
			g.setColor(Color.red);
		else if (orders.size() > 0)
			g.setColor(Color.green);
		else
			g.setColor(Color.black);
		g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

		int line = 0;
		int column = 0;
		for (int i = 1; i <= stocks.size(); i++) {
			Color c = BlockType.getBlockType(i).getColor();
			g.setColor(c);
			for (int j = 0; j < stocks.get(new Integer(i)).intValue(); j++) {
				g.fillRect(bounds.x + column * 5 + 4, bounds.y + line * 5 + 4, 4, 4);
				column++;
				if (column * 5 + 12 > bounds.getWidth()) {
					column = 0;
					line++;
				}
			}
			if (column * 5 + 12 > bounds.getWidth()) {
				column = 0;
				line++;
			}
		}
	}

	public Rectangle getBounds() {
		double pixelSize = factory.getPixelSize();
		int x = getOrientation() == Direction.VERTICAL ? (int) (centerX / pixelSize - width / 2 / pixelSize) : (int) (centerX / pixelSize - length / 2 / pixelSize);
		int y = getOrientation() == Direction.VERTICAL ? (int) (centerY / pixelSize - length / 2 / pixelSize) : (int) (centerY / pixelSize - width / 2 / pixelSize);
		int w = getOrientation() == Direction.VERTICAL ? (int) (width / pixelSize) : (int) (length / pixelSize);
		int h = getOrientation() == Direction.VERTICAL ? (int) (length / pixelSize) : (int) (width / pixelSize);
		return new Rectangle(x, y, w, h);
	}

	private Direction getOrientation() {
		return orientation;
	}

	public void addOrder(WarehouseOut out, int blocktype) {
		Order o = new OrderOut(blocktype, Factory.generateRandom(mintime, maxtime), out);
		orders.add(o);
	}

	public void addOrder(WarehouseIn in) {
		Order o = new OrderIn(Factory.generateRandom(mintime, maxtime), in);
		orders.add(o);
	}

	public void doStep() {
		if (error)
			return;
		if (orders.size() > 0) {
			if (orders.get(0) instanceof OrderOut) {
				OrderOut o = (OrderOut) orders.get(0);
				if (o.removeTime(Factory.getInstance().getSimulationTime())) {
					if (!o.getOut().isClear()) {
						error = true;
						return;
					}
					orders.removeFirst();
					if (stocks.get(new Integer(o.getBlocktype()).intValue()) > 0) {
						Factory.getInstance().addBlock(o.getBlocktype(), o.getOut().getCenterX(), o.getOut().getCenterY());
						Integer i = new Integer(stocks.get(new Integer(o.getBlocktype()).intValue()) - 1);
						stocks.put(new Integer(o.getBlocktype()), i);
					}
				}
			} else if (orders.get(0) instanceof OrderIn) {
				OrderIn o = (OrderIn) orders.get(0);
				if (o.removeTime(Factory.getInstance().getSimulationTime())) {
					if (o.getIn().isSensorActive(0)) {
						Block b = Factory.getInstance().getBlockAt(o.getIn().getBounds().x + o.getIn().getBounds().width / 2, o.getIn().getBounds().y + o.getIn().getBounds().height / 2);
						if (b != null) {
							Factory.getInstance().removeBlock(b);
							int currentStock = stocks.get(new Integer(b.getType()));
							stocks.put(new Integer(b.getType()), currentStock + 1);
						}
					}

					orders.removeFirst();
				}
			}
		}

	}

}
