package neoe.opus;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class libopus {
	static final int OPUS_APPLICATION_VOIP = 2048;
	static final int OPUS_APPLICATION_AUDIO = 2049;
	public static int opusFramesizeInMs60 = 60;
	public static int opusFramesizeInMs10 = 10;
	public int channels = 1;
	public static int rate48k = 48000;

	static {
		Native.register(Platform.isWindows() ? "libopus-0" : "libopus-0");
	}

	public static native String opus_get_version_string();

	public static native int opus_encoder_get_size(int ch);

	/**
	 * 
	 * @param fs
	 *            Sampling rate of input signal (Hz) This must be one of 8000,
	 *            12000, 16000, 24000, or 48000.
	 * @param ch
	 *            Number of channels (1 or 2) in input signal
	 * @param opt
	 *            Coding mode (OPUS_APPLICATION_VOIP/OPUS_APPLICATION_AUDIO/
	 *            OPUS_APPLICATION_RESTRICTED_LOWDELAY)
	 * @param err
	 *            Error codes
	 * @return
	 */
	public static native Pointer opus_encoder_create(int fs, int ch, int opt,
			IntByReference err);

	public static native void opus_encoder_destroy(Pointer enc);

	public static native void opus_decoder_destroy(Pointer dec);

	/**
	 * 
	 * @param st
	 * @param pcm
	 *            opus_int16*: Input signal (interleaved if 2 channels). length
	 *            is frame_size*channels*sizeof(opus_int16)
	 * @param frame_size
	 *            int: Number of samples per channel in the input signal. This
	 *            must be an Opus frame size for the encoder's sampling rate.
	 *            For example, at 48 kHz the permitted values are 120, 240, 480,
	 *            960, 1920, and 2880. Passing in a duration of less than 10 ms
	 *            (480 samples at 48 kHz) will prevent the encoder from using
	 *            the LPC or hybrid modes.
	 * @param data
	 *            unsigned char*: Output payload. This must contain storage for
	 *            at least max_data_bytes.
	 * @param max_data_bytes
	 *            opus_int32: Size of the allocated memory for the output
	 *            payload. This may be used to impose an upper limit on the
	 *            instant bitrate, but should not be used as the only bitrate
	 *            control. Use OPUS_SET_BITRATE to control the bitrate.
	 * @return The length of the encoded packet (in bytes) on success or a
	 *         negative error code (see Error codes) on failure.
	 */
	public static native int opus_encode(Pointer st, byte[] pcm,
			int frame_size, byte[] data, int max_data_bytes);

	// OpusDecoder * opus_decoder_create (opus_int32 Fs, int channels, int
	// *error)
	public static native Pointer opus_decoder_create(int fs, int channel,
			IntByReference err);

	/**
	 * 
	 * @param st
	 *            OpusDecoder*: Decoder state
	 * @param data
	 *            char*: Input payload. Use a NULL pointer to indicate packet
	 *            loss
	 * @param len
	 *            opus_int32: Number of bytes in payload*
	 * @param pcm
	 *            opus_int16*: Output signal (interleaved if 2 channels). length
	 *            is frame_size*channels*sizeof(opus_int16)
	 * @param frame_size
	 *            Number of samples per channel of available space in pcm. If
	 *            this is less than the maximum packet duration (120ms; 5760 for
	 *            48kHz), this function will not be capable of decoding some
	 *            packets. In the case of PLC (data==NULL) or FEC
	 *            (decode_fec=1), then frame_size needs to be exactly the
	 *            duration of audio that is missing, otherwise the decoder will
	 *            not be in the optimal state to decode the next incoming
	 *            packet. For the PLC and FEC cases, frame_size must be a
	 *            multiple of 2.5 ms.
	 * @param decode_fec
	 *            int: Flag (0 or 1) to request that any in-band forward error
	 *            correction data be decoded. If no such data is available, the
	 *            frame is decoded as if it were lost.
	 * @return Number of decoded samples or Error codes
	 */
	public static native int opus_decode(Pointer st, byte[] data, int len,
			byte[] pcm, int frame_size, int decode_fec);

	private Pointer/* OpusEncoder */enc;
	private Pointer/* OpusDecoder */dec;
	private int frame_size;
	private int OPUS_APPLICATION;
	private int opusFramesizeInMs;
	private int sampleRate;

	public libopus() {
		this(1, OPUS_APPLICATION_VOIP, opusFramesizeInMs60, rate48k);
	}

	public libopus(int channels, int OPUS_APPLICATION, int opusFramesizeInMs,
			int sampleRate) {
		this.OPUS_APPLICATION = OPUS_APPLICATION;
		this.channels = channels;
		this.opusFramesizeInMs = opusFramesizeInMs;
		this.sampleRate = sampleRate;
		say("opus_get_version_string=" + libopus.opus_get_version_string());
		frame_size = sampleRate * opusFramesizeInMs / 1000;
		say("opus_framesize=" + frame_size);
	}

	void openEnc() {

		IntByReference err = new IntByReference();
		enc = libopus.opus_encoder_create(sampleRate, channels,
				OPUS_APPLICATION, err);
		if (err.getValue() != 0) {
			say("openEnc,err=" + err.getValue());
		} else {
			say("openEnc,ok:" + enc);
		}
		if (isNULL(enc)) {
			say("enc=NULL," + enc);
			return;
		}
	}

	void openDec() {
		IntByReference err = new IntByReference();
		dec = libopus.opus_decoder_create(sampleRate, channels, err);
		if (err.getValue() != 0) {
			say("openDec,err=" + err.getValue());
		} else {
			say("openDec,ok:" + dec);
		}
		if (isNULL(dec)) {
			say("dec=NULL," + dec);
			return;
		}
	}

	void closeEnc() {
		opus_encoder_destroy(enc);
	}

	void closeDec() {
		opus_decoder_destroy(dec);
	}

	static boolean isNULL(Pointer p) {
		return p == null || Pointer.NULL == p;
	}

	private static void say(String s) {
		System.out.println(s);
	}

	public int encode(byte[] data, byte[] out) {
		return opus_encode(enc, data, frame_size, out, out.length);
	}

	public int decode(byte[] data, int len, byte[] out, boolean lost_prev) {
		// System.out.println("decode,len="+len+",framesize="+frame_size);
		return channels
				* 2
				* opus_decode(dec, data, len, out, frame_size, lost_prev ? 1
						: 0);
	}

	public static int getOpusSampleRate(int rate) {
		int coding_rate;
		if (rate > 24000)
			coding_rate = 48000;
		else if (rate > 16000)
			coding_rate = 24000;
		else if (rate > 12000)
			coding_rate = 16000;
		else if (rate > 8000)
			coding_rate = 12000;
		else
			coding_rate = 8000;
		return coding_rate;
	}

	public static int getOpusFrameBytes(int sampleRate, int channels,
			int opusFramesizeInMs) {
		return (channels * sampleRate * Audio.sampleBits / 8
				* opusFramesizeInMs / 1000);
	}
}
