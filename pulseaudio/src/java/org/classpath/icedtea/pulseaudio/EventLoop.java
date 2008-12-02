/* EventLoop.java
   Copyright (C) 2008 Red Hat, Inc.

This file is part of IcedTea.

IcedTea is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License as published by
the Free Software Foundation, version 2.

IcedTea is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with IcedTea; see the file COPYING.  If not, write to
the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version.
 */

package org.classpath.icedtea.pulseaudio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.classpath.icedtea.pulseaudio.ContextEvent.Type;
import org.classpath.icedtea.pulseaudio.Debug.DebugLevel;

/*
 * any methods that can obstruct the behaviour of pa_mainloop should run
 * synchronized
 * 
 * 
 */

public class EventLoop implements Runnable {

	/*
	 * the threadLock object is the object used for synchronizing the
	 * non-thread-safe operations of pulseaudio's c api
	 */
	public Object threadLock = new Object();

	private static EventLoop instance = null;

	private List<ContextListener> contextListeners;
	// private List<SourceDataLine> lines;
	private String name;
	private String serverString;

	private int status;
	// private boolean eventLoopIsRunning = false;

	public Semaphore finished = new Semaphore(0);

	private List<String> targetPortNameList = new ArrayList<String>();
	private List<String> sourcePortNameList = new ArrayList<String>();

	/*
	 * JNI stuff
	 * 
	 * Do not synchronize the individual functions, synchronize
	 * block/method/lines around the call
	 */

	private native void native_setup(String appName, String server);

	private native int native_iterate(int timeout);

	private native void native_shutdown();

	private native void native_set_sink_volume(byte[] streamPointer, int volume);

	/*
	 * These fields hold pointers
	 */
	private byte[] contextPointer;
	private byte[] mainloopPointer;

	/*
	 * 
	 */

	static {
		SecurityWrapper.loadNativeLibrary();
	}

	private EventLoop() {
		contextListeners = new ArrayList<ContextListener>();
		threadLock = new Object();
	}

	synchronized public static EventLoop getEventLoop() {
		if (instance == null) {
			instance = new EventLoop();
		}
		return instance;
	}

	public void setAppName(String name) {
		this.name = name;
	}

	public void setServer(String serverString) {
		this.serverString = serverString;
	}

	@Override
	public void run() {
		native_setup(this.name, this.serverString);

		Debug.println(DebugLevel.Info, "Eventloop.run(): eventloop starting");

		/*
		 * Perhaps this loop should be written in C doing a Java to C call on
		 * every iteration of the loop might be slow
		 */
		while (true) {
			synchronized (threadLock) {
				// timeout is in milliseconds
				// timout = 0 means dont block
				native_iterate(100);

				if (Thread.interrupted()) {
					native_shutdown();

					// clean up the listeners
					synchronized (contextListeners) {
						contextListeners.clear();
					}

					Debug.println(DebugLevel.Info,
							"EventLoop.run(): event loop terminated");

					return;

				}
			}
		}

	}

	public void addContextListener(ContextListener contextListener) {
		synchronized (contextListeners) {
			contextListeners.add(contextListener);
		}
	}

	public void removeContextListener(ContextListener contextListener) {
		synchronized (contextListeners) {
			contextListeners.remove(contextListener);
		}
	}

	public int getStatus() {
		return this.status;
	}

	public void update(int status) {
		synchronized (threadLock) {
			// System.out.println(this.getClass().getName()
			// + ".update() called! status = " + status);
			this.status = status;
			switch (status) {
			case 0:
				fireEvent(new ContextEvent(Type.UNCONNECTED));
				break;
			case 1:
				fireEvent(new ContextEvent(Type.CONNECTING));
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				fireEvent(new ContextEvent(Type.READY));
				break;
			case 5:
				fireEvent(new ContextEvent(Type.FAILED));
				Debug.println(DebugLevel.Warning,
						"EventLoop.update(): Context failed");
				break;
			case 6:
				fireEvent(new ContextEvent(Type.TERMINATED));
				break;
			default:

			}
		}
	}

	private void fireEvent(final ContextEvent e) {
		// System.out.println(this.getClass().getName() + "firing event: "
		// + e.getType().toString());

		synchronized (contextListeners) {
			// System.out.println(contextListeners.size());
			for (ContextListener listener : contextListeners) {
				listener.update(e);
			}
		}

	}

	public void setVolume(byte[] streamPointer, int volume) {
		synchronized (threadLock) {
			native_set_sink_volume(streamPointer, volume);
		}
	}

	public byte[] getContextPointer() {
		return contextPointer;
	}

	public byte[] getMainLoopPointer() {
		return mainloopPointer;
	}

	private native byte[] nativeUpdateTargetPortNameList();

	private native byte[] nativeUpdateSourcePortNameList();

	protected synchronized List<String> updateTargetPortNameList() {
		targetPortNameList = new ArrayList<String>();
		Operation op;
		synchronized (this.threadLock) {
			op = new Operation(nativeUpdateTargetPortNameList());
		}

		op.waitForCompletion();

		assert (op.getState() == Operation.State.Done);

		op.releaseReference();
		return targetPortNameList;
	}

	protected synchronized List<String> updateSourcePortNameList() {
		sourcePortNameList = new ArrayList<String>();
		Operation op;
		synchronized (this.threadLock) {
			op = new Operation(nativeUpdateSourcePortNameList());
		}

		op.waitForCompletion();

		assert (op.getState() == Operation.State.Done);

		op.releaseReference();
		return sourcePortNameList;
	}

	public void source_callback(String name) {
		sourcePortNameList.add(name);
	}

	public void sink_callback(String name) {
		targetPortNameList.add(name);
	}

}
