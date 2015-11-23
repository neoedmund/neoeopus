package neoe.opus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Visual {
	BufferedImage img;

	public void draw(byte[] buf, int off, int len) {
		final int H = 128;
		if (img == null) {
			img = new BufferedImage(len / 2, H, BufferedImage.TYPE_BYTE_BINARY);
		}
		Graphics2D g = img.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, len / 2, H);
		g.setColor(Color.BLACK);
		float s = 0;
		for (int i = 0; i < len / 2; i++) {
			// -1..1
			float a = PCM.pcmShortToFloat(PCM.getLEShort(buf[off + i * 2], buf[off + i * 2 + 1]));
			s += Math.abs(a);
			// 0..H
			int v = Math.round(H / 2 * (a + 1));
			g.drawLine(i, H / 2, i, H - v);
		}
		System.out.printf("avg=%.3f\n", s / (len / 2));
		g.dispose();
	}

	public void savePng(String fn) throws IOException {
		ImageIO.write(img, "PNG", new File(fn));
	}

}
