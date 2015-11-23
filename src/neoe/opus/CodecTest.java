package neoe.opus;

import java.io.File;
import java.io.IOException;

public class CodecTest {

	WavTargetDataLine line;
	private String fn;

	public CodecTest(String fn) throws Exception {
		this.fn = fn;
		init();
	}

	private void init() throws Exception {

		System.out.println("Get Wav file line");
		line = new WavTargetDataLine(fn);

	}

	public void run() throws IOException {
		WavFile wav = new WavFile();
		wav.parseHeader(fn);
		int app = wav.channels > 1 ? libopus.OPUS_APPLICATION_AUDIO : libopus.OPUS_APPLICATION_VOIP;
		int opusFramesizeInMs = wav.channels > 1 ? libopus.opusFramesizeInMs60 : libopus.opusFramesizeInMs60;

		int opusSampleRate = libopus.getOpusSampleRate(wav.sampleRate);
		int bufSize = libopus.getOpusFrameBytes(opusSampleRate, wav.channels, opusFramesizeInMs);

		System.out.println("opus input buf size=" + bufSize);
		byte[] pcmData = new byte[bufSize];
		byte[] pcmData2 = new byte[bufSize];
		byte[] opusData = new byte[bufSize];
		String fn2 = new File(fn).getAbsolutePath() + ".re.wav";
		String fno = new File(fn).getAbsolutePath() + ".neoe.opus";
		WavFile wav2 = new WavFile(fn2, wav.sampleRate, wav.channels);
		OpusFile of = new OpusFile(fno, wav.sampleRate, wav.channels, opusFramesizeInMs);
		int inlen;
		int cnt = 0;

		libopus opus;

		opus = new libopus(wav.channels, app, opusFramesizeInMs, opusSampleRate);
		opus.openDec();
		opus.openEnc();
		while (true) {
			if ((inlen = line.read(pcmData, 0, bufSize)) < bufSize) {
				say("read=EOF, drop:" + inlen);
				break;
			}
			try {

				int encoded = opus.encode(pcmData, opusData);
				int decoded = opus.decode(opusData, encoded, pcmData2, false);
				if (cnt < 10)
					System.out.printf("%d->%d->%d\n", pcmData.length, encoded, decoded);
				of.write(opusData, encoded);
				wav2.write(pcmData2, decoded);

				cnt++;
			} catch (Throwable e) {
				e.printStackTrace();
			}

		}

		say(String.format("voice %d sectors", cnt));
		opus.closeEnc();
		opus.closeDec();
		wav2.close();
		of.close();
		new WavFile().parseHeader(fn2);
		new OpusFile().parseHeader(fno);
	}

	private static void say(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) throws Exception {
		CodecTest self = new CodecTest("E:/1115/music_orig.wav");
		self.run();
	}
}
