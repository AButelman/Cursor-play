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

public class FallingCursor implements MouseListener, MouseMotionListener{
	private JFrame window;
	private Draw panel;
	private boolean isMouseIn;
	private boolean isMousePressed;
	private int button;
	private Color color = new Color(127, 0, 200);
	private int mouseX;
	private int mouseY;
	
	final int LIMIT = 60;
	int movRadius = LIMIT;
	double ellipseRadius = 50;
	double angleChange = 0.21;
	
	float transparency = 1f;
	
	public FallingCursor(){
		window = new JFrame("Left button to go faster, right to go slower. Exit and reenter to change the color.");
		panel = new Draw();
		
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
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) { 
		isMousePressed = true;
		button = e.getButton();
		if (button == 1) {
			angleChange += 0.02;
		} else if (button == 3) {
			angleChange -= 0.02;
		}
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
		mouseX = e.getX();
		mouseY = e.getY();
		
		Runnable runab = new Move();
		Thread t = new Thread(runab);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}

	@Override
	public void mouseExited(MouseEvent e) {	
		isMouseIn = false; 
	}

	
	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");
		new FallingCursor();
	}

	private class Move implements Runnable {
		@Override
		public void run() {
			double angle = 0;
			boolean falling = true;
			
			while (isMouseIn) {
				
				if (movRadius <= 5 || ellipseRadius <= 1) {
					falling = false;
				} else if (movRadius == LIMIT) {
					falling = true;
				}
				
				if (angle >= (Math.PI * 2)) {	// Vuelta completa 
					angle = 0;
					if (falling) {
						movRadius -= 5;
					} else {
						movRadius += 5;
					}
				}
				
				if (falling) ellipseRadius -= 0.13; else ellipseRadius += 0.13;
				if (falling) transparency -= 0.001; else transparency += 0.001;
				
				if (falling) {
					panel.x = mouseX + (int) (Math.cos(angle) * movRadius) - ((int) ellipseRadius) / 2;
					panel.y = mouseY + (int) (Math.sin(angle) * movRadius) - ((int) ellipseRadius) / 2;
				} else {
					panel.x = mouseX + (int) (Math.sin(angle) * movRadius) - ((int) ellipseRadius) / 2;
					panel.y = mouseY + (int) (Math.cos(angle) * movRadius) - ((int) ellipseRadius) / 2;
				}
				
				angle += angleChange;
				
				panel.repaint();
				
				
				try {
					Thread.sleep(1000 / 30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class Draw extends JComponent {
		public int x = 0;
		public int y = 0;
		
		public void paint(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
			g2d.setColor(color);
			
			Shape drawEllipse = new Ellipse2D.Double (this.x, this.y,
						(int) ellipseRadius, (int) ellipseRadius);
			
			if (isMouseIn) {
				g2d.fill(drawEllipse);
			} 
		}
	}
}
