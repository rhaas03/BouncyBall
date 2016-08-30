import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

import java.util.ArrayList;

import resources.BouncyBall;
import resources.StarPolygon;
import resources.Platform;
import resources.StaticBall;

public class LevelTester extends JPanel implements ActionListener, KeyListener {
	private JFrame frame;

	private ArrayList<Platform> platforms = new ArrayList<Platform>();
	private ArrayList<StarPolygon> stars = new ArrayList<StarPolygon>();

	private ArrayList<Platform> initPlatforms = new ArrayList<Platform>();
	private ArrayList<StarPolygon> initStars = new ArrayList<StarPolygon>();

	private BouncyBall bb;

	private double starRotateAngle;

	private Timer check = new Timer(1000/60, this);

	public LevelTester(ArrayList<Platform> p, ArrayList<StarPolygon> s, StaticBall b, Dimension d, Point po) {
		frame = new JFrame("Custom Level Tester");
		frame.addWindowListener(closer());
		setPreferredSize(d);
		frame.setResizable(false);
		frame.setLocation(po);
		frame.addKeyListener(this);

		for(Platform plat: p)
			platforms.add(plat);
		for(StarPolygon sta : s)
			stars.add(sta);
		for(Platform plat : platforms)
			initPlatforms.add(plat);
		for(StarPolygon sta : stars)
			initStars.add(sta);

		bb = new BouncyBall(b.getX(), b.getY(), b.getD(), b.getRLSpeed(), b.getDefaultUpSpeed(), Color.BLUE, this);

		Container canvas = frame.getContentPane();
		canvas.add(this);

		frame.pack();
		check.start();
		frame.setVisible(true);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d  = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		drawLevel(g2d);
	}

	private void drawLevel(Graphics2D g2d) {
		AffineTransform old = g2d.getTransform();
		for(Platform p : platforms)
			p.drawPlatform(g2d);
		for(StarPolygon s : stars) {
			int centerX = 0, centerY = 0;
			for(int i = 0; i < s.npoints; i++) {
				centerX += s.xpoints[i];
				centerY += s.ypoints[i];
			}
			centerX /= s.npoints;
			centerY /= s.npoints;
			g2d.rotate(starRotateAngle, centerX, centerY);
			g2d.setColor(Color.YELLOW);
			g2d.fill(s);
			g2d.setColor(Color.BLACK);
			g2d.draw(s);
			g2d.setTransform(old);
		}

		bb.drawBall(g2d);
		g2d.setTransform(old);
	}

	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		if(k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) {
			bb.setRight(true);
		}
		if(k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) {
			bb.setLeft(true);
		}
	}

	public void keyReleased(KeyEvent e) {
		int k = e.getKeyCode();
		if(k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D)
			bb.setRight(false);
		if(k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A)
			bb.setLeft(false);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == check) {
			boolean hasCollided = false;
			boolean bump = false;
			int index = 0;

			for(int i = 0; i < platforms.size(); i++) {
				Platform tempPlat = platforms.get(i);
				if(tempPlat.doesCollide(bb.getX() - bb.getD()/2, (int)((bb.getY() - bb.getD()/2) + bb.getDownSpeed() - tempPlat.getMoveSpeed()), bb.getD())) {
					if(tempPlat.getType() == Platform.DEAD)
						reset();
					else {
						hasCollided = true;
						index = i;
					}
					break;
				}
				else
					hasCollided = false;
			}

			if(hasCollided && bb.getDirection() == BouncyBall.DOWN && bb.getBottomOfBall() - bb.getDownSpeed() < platforms.get(index).getY()) {
				bb.setDirectionUp();
				if(platforms.get(index).getType() == Platform.BOOST)
					bb.setUpSpeed(platforms.get(index).getBoost());
				else
					bb.resetUpSpeed();
			}
			else if(hasCollided && bb.getDirection() == BouncyBall.UP && bb.getBottomOfBall() + bb.getUpSpeed() > platforms.get(index).getY()) bb.setDirectionDown();
			else if(hasCollided) bump = true;

			if(bump) {
				if(bb.getBottomOfBall() > platforms.get(index).getY() && bb.getBottomOfBall() < platforms.get(index).getBottomOfPlatform())
					bb.moveABit(bb.getRLD());
			}

			Ellipse2D userEllipse = new Ellipse2D.Double(bb.getX() - bb.getD()/2, bb.getY() - bb.getD()/2, bb.getD(), bb.getD());
			for(int x = 0; x < stars.size(); x++) {
				if(starCollision(stars.get(x), userEllipse))
					stars.remove(x);
			}

			if(stars.isEmpty()) {
				check.stop();
				bb.stopTimers();
			}

			if(bb.getY() - bb.getD() > getHeight() + 120)
				reset();

			if(starRotateAngle < 360) starRotateAngle += Environment.STAR_ROTATE_SPEED;
			else starRotateAngle = 0;
		}

		repaint();
	}

	private void reset() {
		stars.clear();
		platforms.clear();
		for(StarPolygon s : initStars)
			stars.add(s);
		for(Platform p : initPlatforms)
			platforms.add(p);
		bb.reset();
	}

	private boolean starCollision(Shape star, Shape user) {
		Area starA = new Area(star);
		Area userA = new Area(user);
		starA.intersect(userA);
		return !starA.isEmpty();
	}

	private WindowListener closer() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				check.stop();
				check = null;
				Runtime.getRuntime().gc();
				frame.dispose();
			}
		};
	}

	public void keyTyped(KeyEvent e) {}
}
