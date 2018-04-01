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

public class FloatingCursor implements MouseListener, MouseMotionListener{
	private JFrame window;
	private Draw panel;
	private boolean isMouseIn;
	private boolean isMousePressed;
	private int button;
	private Color color = new Color(127, 0, 200);
	private int mouseX;
	private int mouseY;
	private int vel = 5;
	
	int differenceY = 125;
	int verticalLimit = 50;
	int topLimit;
	int bottomLimit;
	int directionY = 1;
	
	int differenceX = - 100;
	int horizLimit = 50;
	int leftLimit;
	int rightLimit;
	int directionX = 1;
	
	public FloatingCursor(){
		window = new JFrame("Left button to go faster, right to go slower. Exit and reenter to change the color.");
		panel = new Draw();
		
		topLimit = -verticalLimit - (panel.height / 2);
		bottomLimit = verticalLimit - (panel.height / 2);
		leftLimit = -horizLimit - (panel.width / 2);
		rightLimit = horizLimit - (panel.width / 2);
		
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
		mouseX = e.getX();
		mouseY = e.getY();
		window.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		window.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) { 
		isMousePressed = true;
		button = e.getButton();
		if (button == 1) {
			vel--;
		} else if (button == 3) {
			vel++;
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
		t.setPriority(10);
		t.start();
		window.repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {	
		isMouseIn = false; 
		window.repaint();
	}

	
	public static void main(String[] args) {
		new FloatingCursor();
	}

	private class Move implements Runnable {
		@Override
		public void run() {
			
			
			while (isMouseIn) {
				
				panel.x = mouseX + differenceX;
				panel.y = mouseY + differenceY;
				
				differenceY += directionY;
				
				if (differenceY >= bottomLimit) {
					directionY = -1;
				}
				
				if (differenceY <= topLimit) {
					directionY = 1;
				}
				
				differenceX += directionX;
				
				if (differenceX >= rightLimit) {
					directionX = -1;
				}
				
				if (differenceX <= leftLimit) {
					directionX = 1;
				}
				
				if (vel < 1) { vel = 1; }
				window.repaint();
				
				try {
					Thread.sleep(vel);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class Draw extends JPanel {
		public int x = 0;
		public int y = 0;
		public int width = 30;
		public int height = 30;
		
		public void paint(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(color);
			
			Shape drawEllipse = new Ellipse2D.Float(this.x, this.y,
						this.width, this.height);
			
			if (isMouseIn) {
				g2d.fill(drawEllipse);
			} 
		}
	}
}
