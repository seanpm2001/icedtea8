// This file is an automatically generated file, please do not edit this file, modify the WrapperGenerator.java file instead !

package sun.awt.X11;

import sun.misc.*;

import java.util.logging.*;
public class XkbStateNotifyEvent extends XWrapperBase { 
	private Unsafe unsafe = XlibWrapper.unsafe; 
	private final boolean should_free_memory;
	public static int getSize() { return 104; }
	public int getDataSize() { return getSize(); }

	long pData;

	public long getPData() { return pData; }


	public XkbStateNotifyEvent(long addr) {
		log.finest("Creating");
		pData=addr;
		should_free_memory = false;
	}


	public XkbStateNotifyEvent() {
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
	public int get_type() { log.finest("");return (Native.getInt(pData+0)); }
	public void set_type(int v) { log.finest(""); Native.putInt(pData+0, v); }
	public long get_serial() { log.finest("");return (Native.getULong(pData+8)); }
	public void set_serial(long v) { log.finest(""); Native.putULong(pData+8, v); }
	public boolean get_send_event() { log.finest("");return (Native.getBool(pData+16)); }
	public void set_send_event(boolean v) { log.finest(""); Native.putBool(pData+16, v); }
	public long get_display() { log.finest("");return (Native.getLong(pData+24)); }
	public void set_display(long v) { log.finest(""); Native.putLong(pData+24, v); }
	public long get_time() { log.finest("");return (Native.getULong(pData+32)); }
	public void set_time(long v) { log.finest(""); Native.putULong(pData+32, v); }
	public int get_xkb_type() { log.finest("");return (Native.getInt(pData+40)); }
	public void set_xkb_type(int v) { log.finest(""); Native.putInt(pData+40, v); }
	public int get_device() { log.finest("");return (Native.getInt(pData+44)); }
	public void set_device(int v) { log.finest(""); Native.putInt(pData+44, v); }
	public int get_changed() { log.finest("");return (Native.getInt(pData+48)); }
	public void set_changed(int v) { log.finest(""); Native.putInt(pData+48, v); }
	public int get_group() { log.finest("");return (Native.getInt(pData+52)); }
	public void set_group(int v) { log.finest(""); Native.putInt(pData+52, v); }
	public int get_base_group() { log.finest("");return (Native.getInt(pData+56)); }
	public void set_base_group(int v) { log.finest(""); Native.putInt(pData+56, v); }
	public int get_latched_group() { log.finest("");return (Native.getInt(pData+60)); }
	public void set_latched_group(int v) { log.finest(""); Native.putInt(pData+60, v); }
	public int get_locked_group() { log.finest("");return (Native.getInt(pData+64)); }
	public void set_locked_group(int v) { log.finest(""); Native.putInt(pData+64, v); }
	public int get_mods() { log.finest("");return (Native.getInt(pData+68)); }
	public void set_mods(int v) { log.finest(""); Native.putInt(pData+68, v); }
	public int get_base_mods() { log.finest("");return (Native.getInt(pData+72)); }
	public void set_base_mods(int v) { log.finest(""); Native.putInt(pData+72, v); }
	public int get_latched_mods() { log.finest("");return (Native.getInt(pData+76)); }
	public void set_latched_mods(int v) { log.finest(""); Native.putInt(pData+76, v); }
	public int get_locked_mods() { log.finest("");return (Native.getInt(pData+80)); }
	public void set_locked_mods(int v) { log.finest(""); Native.putInt(pData+80, v); }
	public int get_compat_state() { log.finest("");return (Native.getInt(pData+84)); }
	public void set_compat_state(int v) { log.finest(""); Native.putInt(pData+84, v); }
	public byte get_grab_mods() { log.finest("");return (Native.getByte(pData+88)); }
	public void set_grab_mods(byte v) { log.finest(""); Native.putByte(pData+88, v); }
	public byte get_compat_grab_mods() { log.finest("");return (Native.getByte(pData+89)); }
	public void set_compat_grab_mods(byte v) { log.finest(""); Native.putByte(pData+89, v); }
	public byte get_lookup_mods() { log.finest("");return (Native.getByte(pData+90)); }
	public void set_lookup_mods(byte v) { log.finest(""); Native.putByte(pData+90, v); }
	public byte get_compat_lookup_mods() { log.finest("");return (Native.getByte(pData+91)); }
	public void set_compat_lookup_mods(byte v) { log.finest(""); Native.putByte(pData+91, v); }
	public int get_ptr_buttons() { log.finest("");return (Native.getInt(pData+92)); }
	public void set_ptr_buttons(int v) { log.finest(""); Native.putInt(pData+92, v); }
	public int get_keycode() { log.finest("");return (Native.getInt(pData+96)); }
	public void set_keycode(int v) { log.finest(""); Native.putInt(pData+96, v); }
	public byte get_event_type() { log.finest("");return (Native.getByte(pData+97)); }
	public void set_event_type(byte v) { log.finest(""); Native.putByte(pData+97, v); }
	public byte get_req_major() { log.finest("");return (Native.getByte(pData+98)); }
	public void set_req_major(byte v) { log.finest(""); Native.putByte(pData+98, v); }
	public byte get_req_minor() { log.finest("");return (Native.getByte(pData+99)); }
	public void set_req_minor(byte v) { log.finest(""); Native.putByte(pData+99, v); }


	String getName() {
		return "XkbStateNotifyEvent"; 
	}


	String getFieldsAsString() {
		String ret="";

		ret += ""+"type = " + XlibWrapper.eventToString[get_type()] +", ";
		ret += ""+"serial = " + get_serial() +", ";
		ret += ""+"send_event = " + get_send_event() +", ";
		ret += ""+"display = " + get_display() +", ";
		ret += ""+"time = " + get_time() +", ";
		ret += ""+"xkb_type = " + get_xkb_type() +", ";
		ret += ""+"device = " + get_device() +", ";
		ret += ""+"changed = " + get_changed() +", ";
		ret += ""+"group = " + get_group() +", ";
		ret += ""+"base_group = " + get_base_group() +", ";
		ret += ""+"latched_group = " + get_latched_group() +", ";
		ret += ""+"locked_group = " + get_locked_group() +", ";
		ret += ""+"mods = " + get_mods() +", ";
		ret += ""+"base_mods = " + get_base_mods() +", ";
		ret += ""+"latched_mods = " + get_latched_mods() +", ";
		ret += ""+"locked_mods = " + get_locked_mods() +", ";
		ret += ""+"compat_state = " + get_compat_state() +", ";
		ret += ""+"grab_mods = " + get_grab_mods() +", ";
		ret += ""+"compat_grab_mods = " + get_compat_grab_mods() +", ";
		ret += ""+"lookup_mods = " + get_lookup_mods() +", ";
		ret += ""+"compat_lookup_mods = " + get_compat_lookup_mods() +", ";
		ret += ""+"ptr_buttons = " + get_ptr_buttons() +", ";
		ret += ""+"keycode = " + get_keycode() +", ";
		ret += ""+"event_type = " + get_event_type() +", ";
		ret += ""+"req_major = " + get_req_major() +", ";
		ret += ""+"req_minor = " + get_req_minor() +", ";
		return ret;
	}


}



