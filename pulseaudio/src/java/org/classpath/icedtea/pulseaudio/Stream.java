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

import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;

/**
 * 
 * This class encapsulates a pa_stream object and provides easier access to the
 * native functions
 * 
 */
public class Stream {

	public interface StateListener {
		public void update();
	}

	public interface CorkListener {
		public void update();
	}

	public interface WriteListener {
		public void update();
	}

	public interface ReadListener {
		public void update();
	}

	public interface OverflowListener {
		public void update();
	}

	public interface UnderflowListener {
		public void update();
	}

	public interface PlaybackStartedListener {
		public void update();
	}

	public interface LatencyUpdateListener {
		public void update();
	}

	public interface MovedListener {
		public void update();
	}

	public interface UpdateTimingInfoListener {
		public void update();
	}

	public interface SuspendedListener {
		public void update();
	}

	public static enum State {
		UNCONNECTED, CREATING, READY, FAILED, TERMINATED,
	}

	public static enum Format {
		PA_SAMPLE_U8, PA_SAMPLE_ULAW, PA_SAMPLE_ALAW, PA_SAMPLE_S16LE, PA_SAMPLE_S16BE, PA_SAMPLE_FLOAT32LE, PA_SAMPLE_FLOAT32BE, PA_SAMPLE_S32LE, PA_SAMPLE_S32BE
	}

	public static final String DEFAULT_DEVICE = null;

	@SuppressWarnings("unused")
	private byte[] streamPointer;

	static {
		System.loadLibrary("pulse-java");
	}

	private Format format;

	private List<StateListener> stateListeners;
	private List<WriteListener> writeListeners;
	private List<ReadListener> readListeners;
	private List<OverflowListener> overflowListeners;
	private List<UnderflowListener> underflowListeners;
	private List<PlaybackStartedListener> playbackStartedListeners;
	private List<LatencyUpdateListener> latencyUpdateListeners;
	private List<MovedListener> movedListeners;
	private List<SuspendedListener> suspendedListeners;
	private List<CorkListener> corkListeners;

	private native void native_pa_stream_new(byte[] contextPointer,
			String name, String format, int sampleRate, int channels);

	private native void native_pa_stream_unref();

	private native int native_pa_stream_get_state();

	private native byte[] native_pa_stream_get_context();

	private native int native_pa_stream_get_index();

	private native int native_pa_stream_get_device_index();

	private native String native_pa_stream_get_device_name();

	private native int native_pa_stream_is_suspended();

	private native int native_pa_stream_connect_playback(String name,
			int bufferMaxLength, int bufferTargetLength,
			int bufferPreBuffering, int bufferMinimumRequest,
			int bufferFragmentSize, int flags, byte[] volumePointer,
			byte[] sync_streamPointer);

	private native int native_pa_stream_connect_record(String name,
			int bufferMaxLength, int bufferTargetLength,
			int bufferPreBuffering, int bufferMinimumRequest,
			int bufferFragmentSize, int flags, byte[] volumePointer,
			byte[] sync_streamPointer);

	private native int native_pa_stream_disconnect();

	private native int native_pa_stream_write(byte[] data, int offset,
			int length);

	private native byte[] native_pa_stream_peek();

	private native int native_pa_stream_drop();

	private native int native_pa_stream_writable_size();

	private native int native_pa_stream_readable_size();

	private native byte[] native_pa_stream_drain();

	private native byte[] native_pa_stream_updateTimingInfo();

	public native int bytesInBuffer();

	/*
	 * pa_operation pa_stream_update_timing_info (pa_stream *p,
	 * pa_stream_success_cb_t cb, void *userdata) Request a timing info
	 * structure update for a stream.
	 */

	private native int native_pa_stream_is_corked();

	private native byte[] native_pa_stream_cork(int b);

	private native byte[] native_pa_stream_flush();

	/*
	 * pa_operation pa_stream_prebuf (pa_stream *s, pa_stream_success_cb_t cb,
	 * void *userdata) Reenable prebuffering as specified in the pa_buffer_attr
	 * structure.
	 */

	private native byte[] native_pa_stream_trigger();

	/* returns an operationPointer */
	private native byte[] native_pa_stream_set_name(String name);

	/* Return the current playback/recording time */
	private native long native_pa_stream_get_time();

	/* Return the total stream latency */
	private native long native_pa_stream_get_latency();

	/*
	 * const pa_timing_info * pa_stream_get_timing_info (pa_stream *s) Return
	 * the latest raw timing data structure.
	 */

	native StreamSampleSpecification native_pa_stream_get_sample_spec();

	/*
	 * const pa_channel_map * pa_stream_get_channel_map (pa_stream *s) Return a
	 * pointer to the stream's channel map. const
	 * 
	 */
	native StreamBufferAttributes native_pa_stream_get_buffer_attr();

	native byte[] native_pa_stream_set_buffer_attr(StreamBufferAttributes info);

	private native byte[] native_pa_stream_update_sample_rate(int rate);

	public native byte[] native_setVolume(float newValue);

	/*
	 * pa_operation pa_stream_proplist_update (pa_stream *s, pa_update_mode_t
	 * mode, pa_proplist *p, pa_stream_success_cb_t cb, void *userdata) Update
	 * the property list of the sink input/source output of this stream, adding
	 * new entries. pa_operation * pa_stream_proplist_remove (pa_stream *s,
	 * const char *const keys[], pa_stream_success_cb_t cb, void *userdata)
	 * Update the property list of the sink input/source output of this stream,
	 * remove entries. int pa_stream_set_monitor_stream (pa_stream *s, uint32_t
	 * sink_input_idx) For record streams connected to a monitor source: monitor
	 * only a very specific sink input of the sink. uint32_t
	 * pa_stream_get_monitor_stream (pa_stream *s) Return what has been set with
	 * pa_stream_set_monitor_stream() ebfore.
	 */

	public Stream(byte[] contextPointer, String name, Format format,
			int sampleRate, int channels) {
		// System.out.println("format: " + format.toString());

		stateListeners = new LinkedList<StateListener>();
		writeListeners = new LinkedList<WriteListener>();
		readListeners = new LinkedList<ReadListener>();
		overflowListeners = new LinkedList<OverflowListener>();
		underflowListeners = new LinkedList<UnderflowListener>();
		playbackStartedListeners = new LinkedList<PlaybackStartedListener>();
		latencyUpdateListeners = new LinkedList<LatencyUpdateListener>();
		movedListeners = new LinkedList<MovedListener>();
		suspendedListeners = new LinkedList<SuspendedListener>();
		corkListeners = new LinkedList<CorkListener>();
		this.format = format;

		StreamSampleSpecification spec = new StreamSampleSpecification(format,
				sampleRate, channels);

		native_pa_stream_new(contextPointer, name, spec.getFormat().toString(),
				spec.getRate(), spec.getChannels());
	}

	public void addStateListener(StateListener listener) {
		synchronized (stateListeners) {
			stateListeners.add(listener);
		}
	}

	public void removeStateListener(StateListener listener) {
		synchronized (stateListeners) {
			stateListeners.remove(listener);
		}

	}

	public void addWriteListener(WriteListener listener) {
		synchronized (writeListeners) {
			writeListeners.add(listener);
		}
	}

	public void removeWriteListener(WriteListener listener) {
		synchronized (writeListeners) {
			writeListeners.remove(listener);
		}
	}

	public void addReadListener(ReadListener listener) {
		synchronized (readListeners) {
			readListeners.add(listener);
		}
	}

	public void removeReadListener(ReadListener listener) {
		synchronized (readListeners) {
			readListeners.remove(listener);
		}
	}

	public void addOverflowListener(OverflowListener listener) {
		synchronized (overflowListeners) {
			overflowListeners.add(listener);
		}
	}

	public void removeOverflowListener(OverflowListener listener) {
		synchronized (overflowListeners) {
			overflowListeners.remove(listener);
		}
	}

	public void addUnderflowListener(UnderflowListener listener) {
		synchronized (underflowListeners) {
			underflowListeners.add(listener);
		}
	}

	public void removeUnderflowListener(UnderflowListener listener) {
		synchronized (underflowListeners) {
			underflowListeners.remove(listener);
		}
	}

	public void addCorkListener(CorkListener listener) {
		synchronized (corkListeners) {
			corkListeners.add(listener);
		}
	}

	public void removeCorkListener(CorkListener listener) {
		synchronized (corkListeners) {
			corkListeners.remove(listener);
		}
	}

	public void addPlaybackStartedListener(PlaybackStartedListener listener) {
		synchronized (playbackStartedListeners) {
			playbackStartedListeners.add(listener);
		}
	}

	public void removePlaybackStartedListener(PlaybackStartedListener listener) {
		synchronized (playbackStartedListeners) {
			playbackStartedListeners.remove(listener);
		}
	}

	public void addLatencyUpdateListener(LatencyUpdateListener listener) {
		synchronized (latencyUpdateListeners) {
			latencyUpdateListeners.add(listener);
		}
	}

	public void removeLatencyUpdateListener(LatencyUpdateListener listener) {
		synchronized (playbackStartedListeners) {
			latencyUpdateListeners.remove(listener);
		}
	}

	public void addMovedListener(MovedListener listener) {
		synchronized (movedListeners) {
			movedListeners.add(listener);
		}
	}

	public void removeMovedListener(MovedListener listener) {
		synchronized (movedListeners) {
			movedListeners.remove(listener);
		}
	}

	public void addSuspendedListener(SuspendedListener listener) {
		synchronized (suspendedListeners) {
			suspendedListeners.add(listener);
		}
	}

	public void removeSuspendedListener(SuspendedListener listener) {
		synchronized (suspendedListeners) {
			suspendedListeners.remove(listener);
		}
	}
		

	public Stream.State getState() {
		int state = native_pa_stream_get_state();
		switch (state) {
		case 0:
			return State.UNCONNECTED;
		case 1:
			return State.CREATING;
		case 2:
			return State.READY;
		case 3:
			return State.FAILED;
		case 4:
			return State.TERMINATED;
		default:
			throw new IllegalStateException("invalid stream state");
		}

	}

	public byte[] getContextPointer() {
		return native_pa_stream_get_context();
	}

	public int getSinkInputIndex() {
		return native_pa_stream_get_index();
	}

	/**
	 * 
	 * @return the index of the sink or source this stream is connected to in
	 *         the server
	 */
	public int getDeviceIndex() {
		return native_pa_stream_get_device_index();
	}

	/**
	 * 
	 * @return the name of the sink or source this stream is connected to in the
	 *         server
	 */
	public String getDeviceName() {
		return native_pa_stream_get_device_name();
	}

	/**
	 * if the sink or source this stream is connected to has been suspended.
	 * 
	 * @return
	 */
	public boolean isSuspended() {
		return (native_pa_stream_is_suspended() != 0);
	}

	/**
	 * Connect the stream to a sink
	 * 
	 * @param deviceName
	 *            the device to connect to. use
	 *            <code>null</code for the default device
	 * @throws LineUnavailableException
	 */
	public void connectForPlayback(String deviceName,
			StreamBufferAttributes bufferAttributes, byte[] syncStreamPointer)
			throws LineUnavailableException {

		int returnValue = native_pa_stream_connect_playback(deviceName,
				bufferAttributes.getMaxLength(), bufferAttributes
						.getTargetLength(), bufferAttributes.getPreBuffering(),
				bufferAttributes.getMinimumRequest(), bufferAttributes
						.getFragmentSize(), 0, null, syncStreamPointer);
		if (returnValue < 0) {
			throw new LineUnavailableException(
					"Unable To connect a line for playback");
		}
	}

	/**
	 * Connect the stream to a source.
	 * 
	 * @throws LineUnavailableException
	 * 
	 */
	public void connectForRecording(String deviceName,
			StreamBufferAttributes bufferAttributes)
			throws LineUnavailableException {

		int returnValue = native_pa_stream_connect_record(deviceName,
				bufferAttributes.getMaxLength(), bufferAttributes
						.getTargetLength(), bufferAttributes.getPreBuffering(),
				bufferAttributes.getMinimumRequest(), bufferAttributes
						.getFragmentSize(), 0, null, null);
		if (returnValue < 0) {
			throw new LineUnavailableException(
					"Unable to connect line for recording");
		}
	}

	/**
	 * Disconnect a stream from a source/sink.
	 */
	public void disconnect() {
		int returnValue = native_pa_stream_disconnect();
		assert (returnValue == 0);
	}

	/**
	 * Write data to the server
	 * 
	 * @param data
	 * @param length
	 * @return
	 */
	public int write(byte[] data, int offset, int length) {
		return native_pa_stream_write(data, offset, length);
	}

	/**
	 * Read the next fragment from the buffer (for recording).
	 * 
	 * 
	 * @param data
	 */
	public byte[] peek() {
		return native_pa_stream_peek();
	}

	/**
	 * 
	 * Remove the current fragment on record streams.
	 */
	public void drop() {
		native_pa_stream_drop();
	}

	/**
	 * Return the number of bytes that may be written using write().
	 * 
	 * @return
	 */
	public int getWritableSize() {
		return native_pa_stream_writable_size();
	}

	/**
	 * Return the number of bytes that may be read using peek().
	 * 
	 * @return
	 */
	public int getReableSize() {
		return native_pa_stream_readable_size();
	}

	/**
	 * Drain a playback stream
	 * 
	 * @return
	 */
	public Operation drain() {
		Operation drainOperation = new Operation(native_pa_stream_drain());
		return drainOperation;
	}

	public Operation updateTimingInfo() {
		Operation updateOperation = new Operation(
				native_pa_stream_updateTimingInfo());
		return updateOperation;
	}

	/**
	 * this function is called whenever the state changes
	 */
	@SuppressWarnings("unused")
	private void stateCallback() {
		synchronized (stateListeners) {
			for (StateListener listener : stateListeners) {
				listener.update();
			}
		}
	}

	@SuppressWarnings("unused")
	private void writeCallback() {
		synchronized (writeListeners) {
			for (WriteListener listener : writeListeners) {
				listener.update();
			}
		}
	}

	@SuppressWarnings("unused")
	private void readCallback() {
		synchronized (readListeners) {
			for (ReadListener listener : readListeners) {
				listener.update();
			}
		}
	}

	@SuppressWarnings("unused")
	private void overflowCallback() {
		System.out.println("overflowCallback called");
		synchronized (overflowListeners) {
			for (OverflowListener listener : overflowListeners) {
				listener.update();
			}
		}
	}

	@SuppressWarnings("unused")
	private void underflowCallback() {
		synchronized (underflowListeners) {
			for (UnderflowListener listener : underflowListeners) {
				listener.update();
			}
		}
	}

	/**
	 * callback function that is called when a the server starts playback after
	 * an underrun or on initial startup
	 */
	@SuppressWarnings("unused")
	private void playbackStartedCallback() {
		synchronized (playbackStartedListeners) {
			for (PlaybackStartedListener listener : playbackStartedListeners) {
				listener.update();
			}
		}
	}

	/**
	 * called whenever a latency information update happens
	 */
	@SuppressWarnings("unused")
	private void latencyUpdateCallback() {
		synchronized (latencyUpdateListeners) {
			for (LatencyUpdateListener listener : latencyUpdateListeners) {
				listener.update();
			}
		}
	}

	/**
	 * whenever the stream is moved to a different sink/source
	 */
	@SuppressWarnings("unused")
	private void movedCallback() {
		synchronized (movedListeners) {
			for (MovedListener listener : movedListeners) {
				listener.update();
			}
		}
	}

	@SuppressWarnings("unused")
	private void corkCallback() {
		synchronized (corkListeners) {
			for (CorkListener listener : corkListeners) {
				listener.update();
			}
		}
	}

	/**
	 * whenever the sink/source this stream is connected to is suspended or
	 * resumed
	 */
	@SuppressWarnings("unused")
	private void suspendedCallback() {
		synchronized (suspendedListeners) {
			for (SuspendedListener listener : suspendedListeners) {
				listener.update();
			}
		}
	}

	public boolean isCorked() {
		int corked = native_pa_stream_is_corked();
		if (corked < 0) {
			throw new IllegalStateException("Unable to determine state");
		}
		return corked == 0 ? false : true;
	}

	/**
	 * Pause (or resume) playback of this stream temporarily.
	 * 
	 * @param cork
	 * @return
	 */
	public Operation cork(boolean cork) {
		int yes = cork ? 1 : 0;
		Operation corkOperation = new Operation(native_pa_stream_cork(yes));
		return corkOperation;
	}

	public Operation cork() {
		return cork(true);
	}

	public Operation unCork() {
		return cork(false);
	}

	/**
	 * Flush the playback buffer of this stream.
	 * 
	 * @return
	 */
	public Operation flush() {
		Operation flushOperation = new Operation(native_pa_stream_flush());
		return flushOperation;
	}

	/*
	 * Operation pa_stream_prebuf (pa_stream *s, pa_stream_success_cb_t cb, void
	 * *userdata)
	 * 
	 * Reenable prebuffering as specified in the pa_buffer_attr structure.
	 */

	/**
	 * Request immediate start of playback on this stream.
	 */
	public Operation triggerStart() {
		Operation triggerOperation = new Operation(native_pa_stream_trigger());
		return triggerOperation;
	}

	/**
	 * set the stream's name
	 * 
	 * @param name
	 * @return
	 */
	public Operation setName(String name) {
		Operation setNameOperation = new Operation(
				native_pa_stream_set_name(name));
		return setNameOperation;
	}

	public long getTime() {
		return native_pa_stream_get_time();
	}

	/**
	 * @returns the total stream latency in microseconds
	 */
	public long getLatency() {
		return native_pa_stream_get_latency();
	}

	/*
	 * const pa_timing_info * pa_stream_get_timing_info (pa_stream *s) Return
	 * the latest raw timing data structure.
	 * 
	 */

	public Format getFormat() {
		return format;
	}

	/*
	 * const pa_channel_map * pa_stream_get_channel_map (pa_stream *s) Return a
	 * pointer to the stream's channel map.
	 */

	public StreamBufferAttributes getBufferAttributes() {
		return native_pa_stream_get_buffer_attr();
	}

	public Operation setBufferAtrributes(StreamBufferAttributes attr) {
		return new Operation(native_pa_stream_set_buffer_attr(attr));
	}

	/**
	 * Change the stream sampling rate during playback.
	 * 
	 */

	Operation updateSampleRate(int rate) {
		return new Operation(native_pa_stream_update_sample_rate(rate));

	}

	public byte[] getStreamPointer() {
		return streamPointer;
	}

	public void free() {
		native_pa_stream_unref();
	}

}