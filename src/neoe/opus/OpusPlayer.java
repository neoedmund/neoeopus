package neoe.opus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class OpusPlayer {

	public SourceDataLine sourceLine;
	private String fn;
	private int sampleRate;
	boolean ok = true;
	OpusFile of;

	public OpusPlayer(String fn) throws Exception {
		this.fn = fn;
		of = new OpusFile();
		of.parseHeader(fn);
		if (!of.format.equals(OpusFile.OPUS)) {
			System.out.println("Format not support!");
			ok = false;
		}
		if (of.ver != OpusFile.VER) {
			System.out.println("Warn:Opusfile ver not support! need:"
					+ OpusFile.VER + ", but got:" + of.ver);
		}
		this.sampleRate = of.sampleRate;
		int byteRate = sampleRate * of.channels * 2;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				Audio.getFormat(sampleRate, of.channels, byteRate));		
		System.out.println(info);
		sourceLine = (SourceDataLine) AudioSystem.getLine(info);
		sourceLine.open();
		sourceLine.start();
		System.out.println(sourceLine);
	}

	public void play() throws IOException {
		if (fn == null) {
			System.out.println("filename not set");
			return;
		}
		InputStream in = new FileInputStream(fn);
		in.skip(OpusFile.HEAD_LEN);
		int cnt = 0;
		byte[] outBuf = new byte[of.unitPCMByte];
		byte[] inBuf = new byte[of.maxUnitByte];
		libopus opus = new libopus(of.channels, libopus.OPUS_APPLICATION_AUDIO,
				of.unitMs, of.sampleRate);
		opus.openDec();
		while (true) {
			int v1 = in.read();
			if (v1 < 0) {
				System.out.println("EOF");
				break;
			}
			cnt++;
			int v2 = in.read();
			int len = v1 & 0xff | ((v2 & 0xff) << 8);
			int r = in.read(inBuf, 0, len);
			if (r < len) {
				System.out.println("Unexpected EOF");
				break;
			}

			int decoded = opus.decode(inBuf, len, outBuf, false);
			if (cnt < 10) {
				System.out.println("read " + len + "->" + decoded);
				PCM.isSilent(outBuf, 0, decoded);
			}
			sourceLine.write(outBuf, 0, decoded);
		}
		System.out.println("played blocks=" + cnt);
		in.close();
		sourceLine.drain();
		sourceLine.close();
		opus.closeDec();
	}

	public static void main(String[] args) throws Exception {
		new OpusPlayer("E:/1115/music_orig.wav.opus").play();
	}
}
