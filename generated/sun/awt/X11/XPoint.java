// This file is an automatically generated file, please do not edit this file, modify the WrapperGenerator.java file instead !

package sun.awt.X11;

import sun.misc.*;

import java.util.logging.*;
public class XPoint extends XWrapperBase { 
	private Unsafe unsafe = XlibWrapper.unsafe; 
	private final boolean should_free_memory;
	public static int getSize() { return 4; }
	public int getDataSize() { return getSize(); }

	long pData;

	public long getPData() { return pData; }


	XPoint(long addr) {
		log.finest("Creating");
		pData=addr;
		should_free_memory = false;
	}


	XPoint() {
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
	public short get_x() { log.finest("");return (Native.getShort(pData+0)); }
	public void set_x(short v) { log.finest(""); Native.putShort(pData+0, v); }
	public short get_y() { log.finest("");return (Native.getShort(pData+2)); }
	public void set_y(short v) { log.finest(""); Native.putShort(pData+2, v); }


	String getName() {
		return "XPoint"; 
	}


	String getFieldsAsString() {
		String ret="";

		ret += ""+"x = " + get_x() +", ";
		ret += ""+"y = " + get_y() +", ";
		return ret;
	}


}



