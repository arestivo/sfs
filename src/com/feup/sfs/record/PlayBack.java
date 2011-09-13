package com.feup.sfs.record;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.feup.sfs.factory.Factory;
import com.feup.sfs.modbus.ModbusSlave;

public class PlayBack {
	private HashMap<Long, ArrayList<String>> commands = new HashMap<Long, ArrayList<String>>();
	
	public PlayBack(String file) {
		BufferedReader br = null;
		boolean randomSeed = false;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		} catch (FileNotFoundException e) {	
			System.out.println("Error reading file: " + file);
		}
		try {
			while (br != null && br.ready()) {
				String line = br.readLine();
				if (!randomSeed) {
					int seed = new Integer(line).intValue();
					Factory.setRandomSeed(seed);
					randomSeed = true;
					continue;
				}
				int pos = line.indexOf(' ');
				Long time = new Long(line.substring(0, pos));
				String command = line.substring(pos + 1);

				ArrayList<String> current = commands.get(time);
				if (current == null) current = new ArrayList<String>();
				current.add(command);
				commands.put(time, current);
			}
		} catch (IOException e) {
			System.out.println("Error reading file: " + file);
		}
	}

	public void play(long time) {
		ArrayList<String> cmds = commands.get(new Long(time));
		if (cmds == null) return;
		for (String cmd : cmds) {
			playCommand(cmd);
		}
	}

	private void playCommand(String cmd) {
		int pos = cmd.indexOf(' ');
		String type = cmd.substring(0,pos);
		cmd = cmd.substring(pos + 1);
		pos = cmd.indexOf(' ');
		if (type.equals("OUT") || type.equals("REG")) {
			int reg = new Integer(cmd.substring(0,pos)).intValue();
			int value = new Integer(cmd.substring(pos + 1)).intValue();
			if (type.equals("OUT"))	ModbusSlave.getSimpleProcessImage().getDigitalOut(reg).set(value==1);
			if (type.equals("REG"))	ModbusSlave.getSimpleProcessImage().getRegister(reg).setValue(value);
		}
		if (type.equals("ADD")) {
			double x = new Double(cmd.substring(0, cmd.indexOf(' '))).doubleValue(); cmd = cmd.substring(cmd.indexOf(' ') + 1);
			double y = new Double(cmd.substring(0, cmd.indexOf(' '))).doubleValue(); cmd = cmd.substring(cmd.indexOf(' ') + 1);
			int t = new Integer(cmd).intValue();
			Factory.getInstance().addBlock(t, x, y);
		}
	}
}
