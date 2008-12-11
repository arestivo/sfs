package com.feup.sfs.warehouse;

import com.feup.sfs.facility.WarehouseIn;

public class OrderIn extends Order{
	private WarehouseIn in;
	
	public OrderIn(int time, WarehouseIn in) {
		super(time);
		this.setIn(in);
	}
	
	public void setIn(WarehouseIn in) {
		this.in = in;
	}

	public WarehouseIn getIn() {
		return in;
	}
}
