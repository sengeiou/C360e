package com.alfredbase.javabean.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class KDSDevice  implements Serializable {
	
	private static final long serialVersionUID = -5249415685356838590L;

	int device_id;
	String name;
	String mac;
	String IP;
	
	public int getDevice_id() {
		return device_id;
	}
	public void setDevice_id(int device_id) {
		this.device_id = device_id;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "KDSDevice [device_id=" + device_id + ", mac=" + mac + ", IP="
				+ IP + "]";
	}
	public KDSDevice clone() {
		KDSDevice newone = new KDSDevice();
		newone.setDevice_id(this.device_id);
		newone.setName(this.name);
		newone.setMac(this.mac);
		newone.setIP(this.IP);
		return newone;
		
	}
    public  Map<String, Object> toMap() {  
        Map<String, Object> map = new HashMap<String, Object>();  
        map.put("device_id", this.device_id);
        map.put("mac", this.mac);
        map.put("ip", this.IP);
        map.put("name", this.name);
        return map;   
    }  
}
