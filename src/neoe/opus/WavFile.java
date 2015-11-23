package neoe.opus;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class WavFile {

	public byte channels = 1;
	private ByteArrayOutputStream ba;
	public byte RECORDER_BPP = 16;
	public int sampleRate;
	private String fn;
	public int totalAudioLen;
	public byte[] data;
	public int byteRate;
	private String format;

	/** for read header */
	public WavFile() {
	}

	/**
	 * @param fn
	 *            filename for OUTPUT!, will be overwrite!
	 * @param sampleRate
	 * @throws IOException
	 */
	public WavFile(String fn, int sampleRate, byte channels) throws IOException {
		// touch file

		File f = new File(fn);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
		out.close();
		this.fn = fn;
		this.sampleRate = sampleRate;
		this.channels = channels;
		this.ba = new ByteArrayOutputStream();
	}

	public void write(byte[] bs, int len) throws IOException {
		ba.write(bs, 0, len);
	}

	public void parseHeader(String fn) throws IOException {
		int len = (int) new File(fn).length();
		FileInputStream in = new FileInputStream(fn);
		byte[] header = new byte[44];
		in.read(header);
		parseHeader(header);

		if (!"RIFF,WAVEfmt ".equals(format)) {
			in.close();
			throw new RuntimeException("not a wav file:" + fn);
		}

		data = new byte[len - 44];
		int len2 = in.read(data);
		in.close();
		assert len2 == len - 44;
	}

	public void parseHeader(byte[] header) {
		format = toStr(header, 0, 4) + "," + toStr(header, 8, 8);
		channels = header[22];
		sampleRate = toIntLE(header, 24);
		byteRate = toIntLE(header, 28);
		RECORDER_BPP = header[34];
		totalAudioLen = toIntLE(header, 40);
		System.out.printf("%s,channels:%d,SampleRate:%d,BPP:%d," + "AudioLenBytes:%d,Len:%.1fsec, byteRate=%d\n",
				format, channels, sampleRate, RECORDER_BPP, totalAudioLen, totalAudioLen / 2.0 / sampleRate / channels,
				byteRate);
	}

	private int toIntLE(byte[] b, int p) {
		return (b[p] & 0xff) | (b[p + 1] << 8 & 0xff00) | (b[p + 2] << 16 & 0xff0000) | (b[p + 3] << 24 & 0xff000000);
	}

	private String toStr(byte[] bs, int off, int len) {
		return new String(bs, off, len);
	}

	public void close() throws IOException {
		byte[] header = new byte[44];
		int totalAudioLen = ba.size();
		int totalDataLen = totalAudioLen + header.length - 8;
		int byteRate = sampleRate * (RECORDER_BPP / 8) * channels;
		OutputStream out = new FileOutputStream(fn);
		// http://soundfile.sapp.org/doc/WaveFormat/

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (sampleRate & 0xff);
		header[25] = (byte) ((sampleRate >> 8) & 0xff);
		header[26] = (byte) ((sampleRate >> 16) & 0xff);
		header[27] = (byte) ((sampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (channels * 2); // block align
		header[33] = 0;
		header[34] = RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
		ba.writeTo(out);
		out.close();
		System.out.printf("write %d bytes to %s\n", totalDataLen, fn);
		ba = null;
		fn = null;
	}

	public static void main(String[] args) throws IOException {
		WavFile wav = new WavFile();
		wav.parseHeader("E:/1115/8k16bitpcm.wav");
	}
}
