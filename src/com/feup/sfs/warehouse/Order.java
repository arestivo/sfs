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
