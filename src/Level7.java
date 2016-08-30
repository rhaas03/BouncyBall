import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.swing.Timer;

import resources.*;

public class Level7 extends Level implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
	private Environment env;
	private BouncyBall bb;
	private LevelOver lo;

	private ArrayList<Platform> platforms = new ArrayList<Platform>();
	private ArrayList<StarPolygon> stars = new ArrayList<StarPolygon>();

	private double starAngle;

	private Timer check = new Timer(1000/60, this);

	public Level7(Environment e) {
		env = e;
		bb = new BouncyBall(20, 410, 12, 1.75, 3.85, env.getBallColor(), env);

		addPlatforms();
		addStars();
		check.start();
	}

	public void drawLevel(Graphics2D g2d) {
		AffineTransform old = g2d.getTransform();

		for(Platform p : platforms)
			p.drawPlatform(g2d);
		for(StarPolygon s : stars) {
			int centerXStar = 0, centerYStar = 0;
			for(int i = 0; i < s.npoints; i ++) {
				centerXStar += s.xpoints[i];
				centerYStar += s.ypoints[i];
			}
			centerXStar /= s.npoints;
			centerYStar /= s.npoints;
			g2d.rotate(starAngle, centerXStar, centerYStar);
			g2d.setColor(Color.YELLOW);
			g2d.fill(s);
			g2d.setColor(Color.BLACK);
			g2d.draw(s);
			g2d.setTransform(old);
		}

		bb.drawBall(g2d);

		if(lo != null)
			lo.drawLevelOver(g2d);
	}

	private void addPlatforms() {
		//Format
		//new Platform(int x, int y, int w, int h, int type)
		//new Platform(int x, int y, int w, int h, int type, int horizVerti, int max)
		int height = 10;
		platforms.add(new Platform(0, env.getHeight() - 20, env.getWidth(), height, Platform.NORMAL));
		platforms.add(new Platform(110, 95, 50, height, Platform.MOVING, Platform.VERTICAL, env.getHeight() - 20));
		platforms.add(new Platform(10, 85, 100, height, Platform.NORMAL));

	}
	private void addStars() {
		//Format
		//new StarPolygon(int x, int y - size, int size, (2*size)/5, int vertexes, double startAngle)
		int size = 10;
		int startAngle = 55;
		int vertexes = 5;
		stars.add(new StarPolygon(40, 85 - size, size, (2*size)/5, vertexes, startAngle));
	}

	public void addListeners() {
		env.frame.addKeyListener(this);
		env.addMouseListener(this);
		env.addMouseMotionListener(this);
	}

	public void removeListeners() {
		env.frame.removeKeyListener(this);
		env.removeMouseListener(this);
		env.removeMouseMotionListener(this);
	}

	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		if(k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D)
			bb.setRight(true);
		if(k == KeyEvent.VK_LEFT ||k == KeyEvent.VK_A)
			bb.setLeft(true);
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
			boolean onAPlatform = false;
			boolean testMove = true;
			int platformNum = 0;
			for(int x = 0; x < platforms.size(); x++) {
				if(platforms.get(x).doesCollide(bb.getX() - bb.getD()/2, bb.getY() - bb.getD()/2, bb.getD())) {
					onAPlatform = true;
					platformNum = x;
					break;
				}
				else onAPlatform = false;
			}

			if(onAPlatform && bb.getDirection() == BouncyBall.DOWN && bb.getBottomOfBall() < platforms.get(platformNum).getY() + bb.getDownSpeed() + 2) {
				bb.setDirectionUp();
				if(platforms.get(platformNum).getType() == Platform.BOOST)
					bb.setUpSpeed(5);
				else bb.resetUpSpeed();
				testMove = false;
			}
			else if(onAPlatform && bb.getDirection() == BouncyBall.UP) bb.setDirectionDown();

			//Checks if it hits the side of the platform
			if(testMove) {
				if((onAPlatform && bb.getBottomOfBall() > platforms.get(platformNum).getY() + 5
						&& bb.getBottomOfBall() < platforms.get(platformNum).getBottomOfPlatform()) && platformNum != 0) {
					bb.moveABit(bb.getRLD());
				}
			}

			//Star Checks
			Ellipse2D userEllipse = new Ellipse2D.Double(bb.getX() - bb.getD()/2, bb.getY() - bb.getD()/2, bb.getD(), bb.getD());
			for(int x = 0; x < stars.size(); x++) {
				if(starCollision(stars.get(x), userEllipse))
					stars.remove(x);
			}

			//Checks to see if all the stars have been collected
			if(stars.isEmpty()) {
				//Move on to next level
				check.stop();
				bb.stopTimers();
				lo = new LevelOver(env);
				env.saveLevel(7);
			}

			//Checks to see if ball left screen
			if(bb.getY() - bb.getD() > env.getHeight() + 120) {
				stars.clear();
				addStars();
				platforms.clear();
				addPlatforms();
				bb.reset();
			}

			if(starAngle < 360) starAngle += Environment.STAR_ROTATE_SPEED;
			else starAngle = 0;
		}

		env.repaint();
	}

	public boolean starCollision(Shape star, Shape user) {
		Area starA = new Area(star);
		Area userA = new Area(user);
		starA.intersect(userA);
		return !starA.isEmpty();
	}

	public void mousePressed(MouseEvent e) {
		if(lo != null) {
			if(lo.backCol(e.getX(), e.getY())) {
				env.moveBackScreen();
				lo = null;
			}
			else if(lo.forwardCol(e.getX(), e.getY())) {
				env.setScreen(Environment.LEVELEIGHT);
				env.changeSize(500, 300);
				env.setLevel(8);
				env.updateListeners();
				lo = null;
			}
		}
	}
	public void mouseMoved(MouseEvent e) {
		if(lo != null) {
			if(lo.backCol(e.getX(), e.getY()))
				lo.setBackColor(Color.BLUE);
			else
				lo.setBackColor(Color.WHITE);
			if(lo.forwardCol(e.getX(), e.getY()))
				lo.setForwardColor(Color.BLUE);
			else
				lo.setForwardColor(Color.WHITE);
		}
	}

	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	public void keyTyped(KeyEvent e) {}
}
