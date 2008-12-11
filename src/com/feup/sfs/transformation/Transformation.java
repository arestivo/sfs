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
