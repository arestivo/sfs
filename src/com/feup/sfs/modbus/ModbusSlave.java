package com.feup.sfs.modbus;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.procimg.SimpleProcessImage;

public class ModbusSlave {
	private static ModbusTCPListener listener = null;
	private static SimpleProcessImage spi = null;
	private static int port;
	private static boolean loopback;

	public static void init(int port, boolean loopback){
		spi = new SimpleProcessImage();
		ModbusSlave.port = port;
		ModbusSlave.loopback = loopback;
	}

	public static SimpleProcessImage getSimpleProcessImage(){
		return spi;
	}
	
	public static void start(){
		ModbusCoupler.getReference().setProcessImage(spi);
		ModbusCoupler.getReference().setMaster(false);
		ModbusCoupler.getReference().setUnitID(1);
		listener = new ModbusTCPListener(5);
		listener.setPort(port);
		if (loopback){
			try {
				listener.setAddress(InetAddress.getByName("127.0.0.1"));
			} catch (UnknownHostException e) {}
		}
		new Thread(new Runnable(){
			public void run() {
				listener.start();
			}
		}).start();
	}
	
}
