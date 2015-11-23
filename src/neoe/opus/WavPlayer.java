package neoe.opus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class WavPlayer {

	public SourceDataLine sourceLine;
	private String fn;
	private int sampleRate;

	public WavPlayer(String fn) throws Exception {
		this.fn = fn;
		WavFile wav = new WavFile();
		wav.parseHeader(fn);
		this.sampleRate = wav.sampleRate;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				Audio.getFormat(sampleRate, wav.channels, wav.byteRate));
		sourceLine = (SourceDataLine) AudioSystem.getLine(info);

		System.out.println(info);
		sourceLine.open();
		sourceLine.start();
	}

	public WavPlayer(int rate) throws Exception {

		this.sampleRate = rate;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				Audio.getFormat(rate));
		sourceLine = (SourceDataLine) AudioSystem.getLine(info);
		System.out.println(info);
		System.out.println(sourceLine.getBufferSize());
		sourceLine.open();
		sourceLine.start();
	}

	public void doPlay(byte[] buf, int off, int len) {
		System.out.println("write "+len);
		sourceLine.write(buf, off, len);// !
	}

	public void play() throws IOException {
		if (fn == null) {
			System.out.println("filename not set");
			return;
		}
		InputStream in = new FileInputStream(fn);
		in.skip(44);
		int len;
		int cnt = 0;
		byte[] outBuf = new byte[sampleRate * 2];
		while ((len = in.read(outBuf)) > 0) {
			cnt++;
			// System.out.println("write "+len);
			sourceLine.write(outBuf, 0, len);
		}
		System.out.println("played blocks=" + cnt);
		in.close();
		sourceLine.drain();
		sourceLine.close();
	}

	public static void main(String[] args) throws Exception {
		new WavPlayer("E:/1115/music_orig.wav").play();
		// new WavPlayer("a0.wav").play();
	}
}
