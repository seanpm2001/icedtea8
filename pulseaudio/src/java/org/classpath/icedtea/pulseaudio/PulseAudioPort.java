/* PulseAudioClip.java
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

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.Port;

public abstract class PulseAudioPort extends PulseAudioLine implements Port,
		PulseAudioPlaybackLine {

	private String name;

	/*
	 * Variable used in native code
	 */
	@SuppressWarnings("unused")
	private byte[] contextPointer;
	@SuppressWarnings("unused")
	private int channels;

	private EventLoop eventLoop;

	private float volume;
	private boolean muted;

	private PulseAudioMuteControl muteControl;
	private PulseAudioVolumeControl volumeControl;

	static {
		System.loadLibrary("pulse-java");
	}

	public PulseAudioPort(String name) {
		this.name = name;
		this.eventLoop = EventLoop.getEventLoop();
		this.contextPointer = eventLoop.getContextPointer();

		updateVolumeInfo();

		volumeControl = new PulseAudioVolumeControl(this, eventLoop);
		controls.add(volumeControl);
		muteControl = new PulseAudioMuteControl(this, volumeControl);
		controls.add(muteControl);

		/*
		 * unlike other lines, Ports must either be open or close
		 * 
		 * close = no sound. open = sound
		 * 
		 */
		open();

		// System.out.println("Opened Target Port " + name);
	}

	public abstract byte[] native_setVolume(float newValue);

	public abstract byte[] native_updateVolumeInfo();

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean value) {
		muted = value;
	}

	public float getVolume() {

		// FIXME need to query system for volume
		return this.volume;
	}

	public void setVolume(float value) {
		this.volume = value;

	}

	public synchronized void updateVolumeInfo() {
		Operation op;
		synchronized (eventLoop.threadLock) {
			op = new Operation(native_updateVolumeInfo());
		}

		op.waitForCompletion();
		op.releaseReference();
	}

	public void update_channels_and_volume(int channels, float volume) {
		this.channels = channels;
		this.volume = volume;
	}

	@Override
	public void close() {

		native_setVolume((float) 0);
		isOpen = false;
		fireLineEvent(new LineEvent(this, LineEvent.Type.CLOSE,
				AudioSystem.NOT_SPECIFIED));
	}

	@Override
	public abstract javax.sound.sampled.Line.Info getLineInfo();

	@Override
	public void open() {
		if (isOpen) {
			return;
		}
		native_setVolume(volume);
		isOpen = true;
		fireLineEvent(new LineEvent(this, LineEvent.Type.OPEN,
				AudioSystem.NOT_SPECIFIED));
	}

	public String getName() {
		return this.name;
	}

}
