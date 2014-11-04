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

	public static void init(int port, boolean loopback) {
		spi = new SimpleProcessImage();
		ModbusSlave.port = port;
		ModbusSlave.loopback = loopback;
	}

	public static SimpleProcessImage getSimpleProcessImage() {
		return spi;
	}

	public static void start() {
		ModbusCoupler.getReference().setProcessImage(spi);
		ModbusCoupler.getReference().setMaster(false);
		ModbusCoupler.getReference().setUnitID(1);
		listener = new ModbusTCPListener(5);
		listener.setPort(port);
		if (loopback) {
			try {
				listener.setAddress(InetAddress.getByName("127.0.0.1"));
			} catch (UnknownHostException e) {
			}
		}
		new Thread(new Runnable() {
			public void run() {
				listener.start();
			}
		}).start();
	}

}
