// This file is an automatically generated file, please do not edit this file, modify the WrapperGenerator.java file instead !

package sun.awt.X11;

import sun.misc.*;

import java.util.logging.*;
public class XTimeCoord extends XWrapperBase { 
	private Unsafe unsafe = XlibWrapper.unsafe; 
	private final boolean should_free_memory;
	public static int getSize() { return 8; }
	public int getDataSize() { return getSize(); }

	long pData;

	public long getPData() { return pData; }


	XTimeCoord(long addr) {
		log.finest("Creating");
		pData=addr;
		should_free_memory = false;
	}


	XTimeCoord() {
		log.finest("Creating");
		pData = unsafe.allocateMemory(getSize());
		should_free_memory = true;
	}


	public void dispose() {
		log.finest("Disposing");
		if (should_free_memory) {
			log.finest("freeing memory");
			unsafe.freeMemory(pData); 
	}
		}
	public long get_time() { log.finest("");return (Native.getULong(pData+0)); }
	public void set_time(long v) { log.finest(""); Native.putULong(pData+0, v); }
	public short get_x() { log.finest("");return (Native.getShort(pData+4)); }
	public void set_x(short v) { log.finest(""); Native.putShort(pData+4, v); }
	public short get_y() { log.finest("");return (Native.getShort(pData+6)); }
	public void set_y(short v) { log.finest(""); Native.putShort(pData+6, v); }


	String getName() {
		return "XTimeCoord"; 
	}


	String getFieldsAsString() {
		String ret="";

		ret += ""+"time = " + get_time() +", ";
		ret += ""+"x = " + get_x() +", ";
		ret += ""+"y = " + get_y() +", ";
		return ret;
	}


}



