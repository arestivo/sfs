package com.feup.sfs.facility;

import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleInputRegister;

import com.feup.sfs.block.Block;
import com.feup.sfs.exceptions.FactoryInitializationException;
import com.feup.sfs.factory.Factory;
import com.feup.sfs.modbus.ModbusSlave;

public class WarehouseOut extends Conveyor{
	private int warehouse;
	private int lastValue;
	
	public WarehouseOut(Properties properties, int id)	throws FactoryInitializationException {
		super(properties, id);
		ModbusSlave.getSimpleProcessImage().addRegister(new SimpleInputRegister(0));
		this.warehouse = new Integer(properties.getProperty("facility."+id+".warehouse")).intValue();
		this.lastValue = 0;
	}

	public void doStep(boolean conveyorBlocked){
		super.doStep(conveyorBlocked);
		if (facilityError) return;
		int newValue = getRegister(0);
		if (newValue != 0 && lastValue == 0) Factory.getInstance().getWarehouse(warehouse).addOrder(this, newValue);
		lastValue = newValue;
	}

	public boolean isClear() {
		for (Block block : Factory.getInstance().getBlocks()) {
			if (block.getDistanceTo(getCenterX(), getCenterY()) < Factory.getInstance().getBlockSize()) return false;
		} 
		return true;
	}
	
	@Override
	public String getName() {
		return "Warehouse Out";
	}
	
	@Override
	public int getNumberDigitalIns() {return 1;}

	@Override
	public int getNumberDigitalOuts() {return 2;}
	
	@Override
	public int getNumberRegisters() { return 1;}		
}
