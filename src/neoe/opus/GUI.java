package neoe.opus;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class GUI {

	private static final String S1 = "dop a file here";

	public static void main(String[] args) {
		new GUI().run();
	}

	JLabel lb;

	public void run() {
		final JFrame f = new JFrame("Neoe OPUS");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		lb = new JLabel(S1);
		lb.setBorder(BorderFactory.createMatteBorder(30, 30, 30, 30, Color.RED));
		lb.setDropTarget(new DropTarget() {
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					for (File file : droppedFiles) {
						String fn = file.getName().toLowerCase();
						if (fn.endsWith(".wav")) {
							encode(file, f);
							info(f, "OK:" + file.getName() + ".neoe.opus");
						} else if (fn.endsWith(".neoe.opus")) {
							play(file, f);
							info(f, "Played:" + file.getName());
						} else {
							info(f, "Only accept '.wav' or '.neoe.opus' files");
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					info(f, ex.getMessage());
				}
			}
		});
		f.getContentPane().add(lb);
		adjustWindow(f);
		f.setVisible(true);
	}

	protected void play(File file, JFrame f) {
		try {
			new OpusPlayer(file.getAbsolutePath()).play();
		} catch (Exception ex) {
			ex.printStackTrace();
			info(f, ex.getMessage());
		}

	}

	public static void info(Component p, String msg) {
		JOptionPane.showMessageDialog(p, msg);
	}

	protected void encode(File file, JFrame f) {
		try {
			new CodecTest(file.getAbsolutePath()).run();
		} catch (Exception ex) {
			ex.printStackTrace();
			info(f, ex.getMessage());
		}

	}

	private void adjustWindow(JFrame f) {
		f.pack();
		f.setLocationRelativeTo(null);
	}

}
