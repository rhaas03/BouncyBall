import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Timer;

import resources.*;

public class Level1 extends Level implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
	private Environment env;
	private BouncyBall bb;
	private LevelOver lo;
	private int newZero;

	private double starAngle;

	private ArrayList<Platform> platforms = new ArrayList<Platform>();
	private ArrayList<StarPolygon> stars = new ArrayList<StarPolygon>();
	private Timer check = new Timer(1000/60, this);

	public Level1(Environment e) {
		env = e;
		newZero = env.getMouseLine();
		bb = new BouncyBall(20, 210, 15, env.getBallColor(), env);

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

		drawTutorialText(g2d);

		bb.drawBall(g2d);
		g2d.setTransform(old);

		if(lo != null)
			lo.drawLevelOver(g2d);
	}

	private void drawTutorialText(Graphics2D g2d) {
		//Welcoming Text
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 18f));
		FontMetrics fm = g2d.getFontMetrics();
		int fontY = newZero + 50;
		String s = "Tutorial: Collect all the stars to win.";
		g2d.drawString(s, 10, fontY);
		fontY += fm.getHeight();
		s = "Use Arrow Keys or WASD to the move ball.";
		g2d.drawString(s, 10 + fm.stringWidth("Tutorial: "), fontY);
	}

	private void addPlatforms() {
		//Format
		//new Platform(int x, int y, int w, int h, int type)
		//new Platform(int x, int y, int w, int h, int type, int horizVerti, int max)
		int height = 12;
		platforms.add(new Platform(0, env.getHeight() - 20, env.getWidth(), height, Platform.NORMAL));
		platforms.add(new Platform(60, 245, 150, height, Platform.NORMAL));
	}

	private void addStars() {
		//Format
		//new StarPolygon(int x, int y - size, int size, (2*size)/5, int vertexes, double startAngle)
		int size = 15;
		int startAngle = 55;
		int vertexes = 5;
		stars.add(new StarPolygon(420, 275 - size, size, (2*size)/5, vertexes, startAngle));
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
			//Platform Checks
			boolean onAPlatform = false;
			boolean testMove = true;
			int platformNum = 0;

			Iterator<Platform> platformIterator = platforms.iterator();
			int counter = 0;

			while(platformIterator.hasNext()) {
				if(platformIterator.next().doesCollide(bb.getX() - bb.getD()/2, bb.getY() - bb.getD()/2, bb.getD())) {
					onAPlatform = true;
					platformNum = counter;
					break;
				}
				else {
					onAPlatform = false;
					counter++;
				}
			}

			if(onAPlatform && bb.getDirection() == BouncyBall.DOWN && bb.getBottomOfBall() < platforms.get(platformNum).getY() + bb.getDownSpeed() + 2) {
				bb.setDirectionUp();
				if(platforms.get(platformNum).getType() == Platform.BOOST)
					bb.setUpSpeed(6);
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
				env.saveLevel(1);
			}

			//Checks to see if ball left screen
			if(bb.getY() - bb.getD() > env.getHeight() + 120) {
				stars.clear();
				addStars();
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
				env.setScreen(Environment.LEVELTWO);
				env.setLevel(2);
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
