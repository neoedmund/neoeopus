package neoe.opus;

/**
 * 1 channel, 2 byte per frame, LE 16bit
 *
 */
public class PCM {

	public static boolean isSilent(byte[] pcm, int off, int len) {
		float s = 0;
		for (int i = 0; i < len / 2; i++) {
			short v = getLEShort(pcm[off + i * 2], pcm[off + i * 2 + 1]);
			s += Math.abs(pcmShortToFloat(v));
		}
		s /= len / 2;
		System.out.println("silence=" + s);
		return s < 0.01;
	}

	/**
	 * 
	 * @param v
	 * @return -1..1
	 */
	public static float pcmShortToFloat(short v) {
		return v / ((float) 0x8000);
	}

	public static short getLEShort(byte b1, byte b2) {
		short v = (short) ((b1 & 0xff) | ((b2 << 8) & 0xff00));
		return v;
	}

	public static void mix(byte[] buf1, int off1, byte[] buf2, int off2,
			byte[] bufOut, int off3, int len) {
		float[] fs = new float[len / 2];
		float min = 0, max = 0;
		for (int i = 0; i < len / 2; i++) {
			float a = pcmShortToFloat(getLEShort(buf1[off1 + i * 2], buf1[off1
					+ i * 2 + 1]));
			float b = pcmShortToFloat(getLEShort(buf2[off2 + i * 2], buf2[off2
					+ i * 2 + 1]));
			float c = a + b; // sum!
			fs[i] = c;
			min = Math.min(min, c);
			max = Math.max(max, c);
		}

		// adjust when overflow
		if (max > 1.01f || min < -1.01f) {
			float h = Math.max(Math.abs(max), Math.abs(min));
			System.out.println("Scale:" + h);
			for (int i = 0; i < len / 2; i++) {
				fs[i] /= h;
			}
		}

		for (int i = 0; i < len / 2; i++) {
			float c = fs[i];
			short v = pcmFloatToShort(c);
			bufOut[off3 + i * 2] = (byte) (v & 0xff);
			bufOut[off3 + i * 2 + 1] = (byte) ((v >> 8) & 0xff);
		}
	}

	public static short pcmFloatToShort(float c) {
		int x = Math.round(c * 0x8000);
		return (short) x;
	}


}
