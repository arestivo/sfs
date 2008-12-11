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
