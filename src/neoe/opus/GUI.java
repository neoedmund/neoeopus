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
		lb.setBorder(BorderFactory.createMatteBorder(50, 50, 50, 50, Color.RED));
		lb.setDropTarget(new DropTarget() {
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt
							.getTransferable().getTransferData(
									DataFlavor.javaFileListFlavor);
					for (File file1 : droppedFiles) {
						final File file = file1;
						String fn = file.getName().toLowerCase();
						if (fn.endsWith(".wav")) {
							new Thread() {
								public void run() {
									lb.setText("Converting to "
											+ file.getName() + ".neoe.opus");
									adjustWindow(f);
									encode(file, f);
									lb.setText(S1);
									adjustWindow(f);
								}
							}.start();
						} else if (fn.endsWith(".neoe.opus")) {
							new Thread() {
								public void run() {
									lb.setText("Playing");
									adjustWindow(f);
									play(file, f);
									lb.setText(S1);
									adjustWindow(f);
								}
							}.start();

						} else {
							info(f, "Only accept '.wav' or '.neoe.opus' files");
						}
						break;
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
		} catch (Throwable ex) {
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
		} catch (Throwable ex) {
			ex.printStackTrace();
			info(f, ex.getMessage());
		}

	}

	private void adjustWindow(JFrame f) {
		f.pack();
		f.setLocationRelativeTo(null);
	}

}
