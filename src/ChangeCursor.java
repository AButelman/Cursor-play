import javax.swing.*;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class ChangeCursor implements MouseListener, MouseMotionListener{
	private JFrame window;
	private Draw panel;
	private boolean isMouseIn;
	private boolean isMousePressed;
	private int button;
	private Color color = new Color(127, 0, 200);
	
	public ChangeCursor(){
		window = new JFrame("Left button to grow, right button to shrink. Exit and reenter to change the color.");
		panel = new Draw();
		
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");

		window.setCursor(blankCursor);

		window.add(panel);
		window.addMouseListener(this);
		window.addMouseMotionListener(this);
		window.add(panel, BorderLayout.CENTER);
		window.setSize(800, 500);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		panel.x = e.getX();
		panel.y = e.getY();
		window.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		panel.x = e.getX();
		panel.y = e.getY();
		window.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) { 
		isMousePressed = true;
		button = e.getButton();
		
		Runnable runa = new Grow();
		Thread t = new Thread(runa);
		t.start();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isMousePressed = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) { 
		isMouseIn = true;
		int r = (int) (Math.random() * 256);
		int g = (int) (Math.random() * 256);
		int b = (int) (Math.random() * 256);
		color = new Color(r, g, b);
		window.repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {	
		isMouseIn = false; 
		window.repaint();
	}

	
	public static void main(String[] args) {
		new ChangeCursor();
	}

	private class Grow implements Runnable {
		@Override
		public void run() {
			while (isMousePressed && isMouseIn) {
				
				if (button == 1) {
					
					panel.width++;
					panel.height++;
					
					if (panel.x - (panel.width / 2) <= 0) { break; }
					if (panel.x + (panel.width / 2) >= panel.getWidth()) { break; }
					if (panel.y - (panel.height / 2) <= 0) { break; }
					if (panel.y + (panel.height / 2) >= panel.getHeight()) { break; }
				} else if (button == 3){
					panel.width--;
					panel.height--;
					
					if (panel.width <= 5) { panel.width = 5; }
					if (panel.height <= 5) { panel.height = 5; }
				}

				window.repaint();
				
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class Draw extends JPanel {
		public int x = 0;
		public int y = 0;
		public int width = 10;
		public int height = 10;
		
		public void paint(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(color);
			
			Shape drawEllipse = new Ellipse2D.Float((this.x - this.width /2), (this.y - this.height /2)
					, this.width, this.height);
			
			if (isMouseIn) {
				g2d.fill(drawEllipse);
			} 
		}
	}
}
