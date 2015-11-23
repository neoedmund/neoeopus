package neoe.opus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class WavTargetDataLine implements TargetDataLine {
	InputStream in;

	public WavTargetDataLine(String fn) throws IOException {
		WavFile wav = new WavFile();
		wav.parseHeader(fn);
		in = new FileInputStream(fn);
		in.skip(44); // skip wav header
	}

	@Override
	public int available() {
		try {
			return in.available();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void drain() {

	}

	@Override
	public void flush() {

	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public AudioFormat getFormat() {
		return null;
	}

	@Override
	public int getFramePosition() {

		return 0;
	}

	@Override
	public float getLevel() {

		return 0;
	}

	@Override
	public long getLongFramePosition() {

		return 0;
	}

	@Override
	public long getMicrosecondPosition() {

		return 0;
	}

	@Override
	public boolean isActive() {

		return false;
	}

	@Override
	public boolean isRunning() {

		return false;
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public void addLineListener(LineListener arg0) {

	}

	@Override
	public void close() {

	}

	@Override
	public Control getControl(Type arg0) {

		return null;
	}

	@Override
	public Control[] getControls() {

		return null;
	}

	@Override
	public javax.sound.sampled.Line.Info getLineInfo() {

		return null;
	}

	@Override
	public boolean isControlSupported(Type arg0) {

		return false;
	}

	@Override
	public boolean isOpen() {

		return false;
	}

	@Override
	public void open() throws LineUnavailableException {

	}

	@Override
	public void removeLineListener(LineListener arg0) {

	}

	@Override
	public void open(AudioFormat format) throws LineUnavailableException {

	}

	@Override
	public void open(AudioFormat format, int bufferSize)
			throws LineUnavailableException {

	}

	@Override
	public int read(byte[] b, int off, int len) {
		try {
			return in.read(b, off, len);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
