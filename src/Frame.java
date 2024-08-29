import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Frame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Frame() throws IOException {
		this.add(new Panel());	//needs to come first, or focus will be set on JFrame and keylisteners will be assholes
		this.setTitle("Galaga");
		this.setIconImage(ImageIO.read(Frame.class.getResourceAsStream("/Textures/PlayerShip.png")));
		this.setVisible(true);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}
}