package neoe.opus;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OpusFile {

	public byte channels = 1;
	private ByteArrayOutputStream ba;
	public byte BPP = 16;
	public int sampleRate;
	private String fn;
	// public byte[] data;
	public byte ver;
	public int totalAudioLen;
	public int maxUnitByte;
	public int unitMs;
	public int unitPCMByte;
	public int unitCnt;
	public String format;
	static final int HEAD_LEN = 28;

	public static final byte VER = 1;
	public static final String OPUS = "OPUS";

	/** for read header */
	public OpusFile() {
	}

	/**
	 * @param fn
	 *            filename for OUTPUT!, will be overwrite!
	 * @param sampleRate
	 * @throws IOException
	 */
	public OpusFile(String fn, int sampleRate, byte channels, int unitMs)
			throws IOException {
		// touch file
		File f = new File(fn);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
		out.close();
		this.fn = fn;
		this.sampleRate = sampleRate;
		this.channels = channels;
		this.unitMs = unitMs;
		this.unitPCMByte = sampleRate * channels * (BPP / 8) * unitMs / 1000;
		System.out.println("unitPCMByte=" + unitPCMByte);
		this.ba = new ByteArrayOutputStream();
	}

	public void write(byte[] bs, int len) throws IOException {
		if (len > maxUnitByte)
			maxUnitByte = len;
		unitCnt++;
		ba.write(len & 0xff);
		ba.write((len >> 8) & 0xff);
		ba.write(bs, 0, len);
	}

	public void parseHeader(String fn) throws IOException {
		// int len = (int) new File(fn).length();
		FileInputStream in = new FileInputStream(fn);
		byte[] header = new byte[HEAD_LEN];
		in.read(header);
		parseHeader(header);
		
		if (!format.equals(OPUS)){
			in.close();
			throw new RuntimeException("not a neoe/opus file:"+fn);
		}
		// data = new byte[len - HEAD_LEN];
		// int len2 = in.read(data);
		in.close();
		// assert len2 == len - HEAD_LEN;
	}

	public void parseHeader(byte[] header) {
		format = toStr(header, 0, 4);
		ver = header[4];
		channels = header[5];
		BPP = header[6];
		sampleRate = toIntLE(header, 8);
		totalAudioLen = toIntLE(header, 12);
		maxUnitByte = toIntLE(header, 16);
		unitMs = toIntLE(header, 20);
		unitCnt = toIntLE(header, 24);
		unitPCMByte = sampleRate * channels * (BPP / 8) * unitMs / 1000;
		int byteRate = sampleRate * channels * BPP / 8;
		System.out.printf("%s,ver:%d,channels:%d,SampleRate:%d,BPP:%d,"
				+ "AudioLenBytes:%d, byteRate=%d,\n maxUnitByte=%d,"
				+ "unitMs=%d,unitCnt=%d,unitPCMByte=%d, len=%.1f sec\n",
				format, ver, channels, sampleRate, BPP, totalAudioLen,
				byteRate, maxUnitByte, unitMs, unitCnt, unitPCMByte, unitMs
						* unitCnt / 1000.0);
	}

	private static int toIntLE(byte[] b, int p) {
		return (b[p] & 0xff) | (b[p + 1] << 8 & 0xff00)
				| (b[p + 2] << 16 & 0xff0000) | (b[p + 3] << 24 & 0xff000000);
	}

	private static String toStr(byte[] bs, int off, int len) {
		return new String(bs, off, len);
	}

	public void close() throws IOException {
		byte[] header = new byte[HEAD_LEN];
		int totalAudioLen = ba.size();

 
		header[0] = 'O';  
		header[1] = 'P';
		header[2] = 'U';
		header[3] = 'S';
		header[4] = VER;
		header[5] = (byte) channels;
		header[6] = BPP; // bits per sample
		header[7] = HEAD_LEN;
		header[8] = (byte) (sampleRate & 0xff);
		header[9] = (byte) ((sampleRate >> 8) & 0xff);
		header[10] = (byte) ((sampleRate >> 16) & 0xff);
		header[11] = (byte) ((sampleRate >> 24) & 0xff);
		header[12] = (byte) (totalAudioLen & 0xff);
		header[13] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[14] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[15] = (byte) ((totalAudioLen >> 24) & 0xff);
		header[16] = (byte) (maxUnitByte & 0xff);
		header[17] = (byte) ((maxUnitByte >> 8) & 0xff);
		header[18] = (byte) ((maxUnitByte >> 16) & 0xff);
		header[19] = (byte) ((maxUnitByte >> 24) & 0xff);
		header[20] = (byte) (unitMs & 0xff);
		header[21] = (byte) ((unitMs >> 8) & 0xff);
		header[22] = (byte) ((unitMs >> 16) & 0xff);
		header[23] = (byte) ((unitMs >> 24) & 0xff);
		header[24] = (byte) (unitCnt & 0xff);
		header[25] = (byte) ((unitCnt >> 8) & 0xff);
		header[26] = (byte) ((unitCnt >> 16) & 0xff);
		header[27] = (byte) ((unitCnt >> 24) & 0xff);
		//assert HEAD_LEN == 28;
		OutputStream out = new FileOutputStream(fn);
		out.write(header, 0, HEAD_LEN);
		ba.writeTo(out);
		out.close();
		System.out.printf("write %d bytes to %s\n", totalAudioLen + HEAD_LEN,
				fn);
		ba = null;
		fn = null;
	}

	public static void main(String[] args) throws IOException {
		OpusFile of = new OpusFile();
		of.parseHeader("E:/1115/music_orig.opus");
	}
}
