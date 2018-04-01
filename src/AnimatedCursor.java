import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class AnimatedCursor {
	JFrame window;
	
	private AnimatedCursor() {
		window = new JFrame();
		window.setSize(800, 600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		Runnable a = new Animate();
		Thread t = new Thread(a);
		t.start();
	}
	
	private class Animate implements Runnable {
	
		private Cursor getCursor(String file) {
			BufferedImage image = null;
			
			try {
				image = ImageIO.read(new File(file));
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			
			return Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0,0), "Image Cursor");
		}
		
		public void run() {
			Cursor[] cursors = {getCursor("Farmer1.gif"), getCursor("Farmer2.gif"), getCursor("Farmer3.gif"),
					getCursor("Farmer4.gif"), getCursor("Farmer5.gif")};
			
			boolean up = true;
			int i = 0;
			
			
			while (true) {
				window.setCursor(cursors[i]);
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				
				if (i == cursors.length-1) up = false;
				if (i == 0) up = true;
				
				if (up) i++; else i--;
			}
		}
	}
	
	public static void main(String[] args) {
		new AnimatedCursor();
	}
}
