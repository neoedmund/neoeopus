package neoe.opus;

import javax.sound.sampled.AudioFormat;

public class Audio {
	public static final AudioFormat getFormat(int rate) {
		return new AudioFormat(encoding, rate, sampleBits, channels,
				(sampleBits / 8) * channels, rate, bigEndian);
	}
	public static final AudioFormat getFormat(int rate, int channels, int framerate) {
		return new AudioFormat(encoding, rate, sampleBits, channels,
				(sampleBits / 8) * channels, framerate, bigEndian);
	}


	static final AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
	static final int channels = 1;
	static final int sampleBits = 16;
	static final boolean bigEndian = false;

}
