package com.feup.sfs.facility;

import java.awt.Graphics;
import java.util.Properties;

import net.wimpi.modbus.procimg.SimpleDigitalOut;

import com.feup.sfs.exceptions.FactoryInitializationException;
import com.feup.sfs.factory.Factory;
import com.feup.sfs.modbus.ModbusSlave;
import com.feup.sfs.warehouse.Warehouse;

public class WarehouseIn extends Conveyor{
	private int warehouse;
	private boolean lastvalue = false;
	
	public WarehouseIn(Properties properties, int id)	throws FactoryInitializationException {
		super(properties, id);
		this.warehouse = new Integer(properties.getProperty("facility."+id+".warehouse")).intValue();
		ModbusSlave.getSimpleProcessImage().addDigitalOut(new SimpleDigitalOut(false));
	}

	public void doStep(boolean conveyorBlocked){
		super.doStep(conveyorBlocked);
		if (facilityError) return;
		boolean newvalue = isInOn();
		if (!lastvalue && newvalue) {
			Warehouse w = Factory.getInstance().getWarehouse(warehouse);
			w.addOrder(this);
		}
		lastvalue = newvalue;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintLight(g, false, 0, isInOn(), 1);
	}

	private boolean isInOn() {
		return getDigitalOut(2);
	}

	@Override
	public String getName() {
		return "Warehouse In";
	}

	@Override
	public int getNumberDigitalIns() {return 1;}

	@Override
	public int getNumberDigitalOuts() {return 3;}

	
}
