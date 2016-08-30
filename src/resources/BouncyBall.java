package resources;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

import javax.swing.*;

public class BouncyBall implements ActionListener, Serializable {
	public static final double STANDARD_RLSPEED = 2.25;
	public static final double STANDARD_DEFAULT_UPSPEED = 4.25;

	private double x, y, initX, initY;
	private int d;
	private Color[] colorArray;

	public static final int UP = 1;
	public static final int DOWN = 2;
	private int direction = DOWN;

	private double initSpeed = .1/2;
	private double downSpeed = initSpeed;
	private double upSpeed;

	public static int RIGHTINT = 1;
	public static int LEFTINT = 2;
	public static int STOPPED = 3;
	private int rlDirection = STOPPED;

	public static boolean RIGHTBOOL = false;
	public static boolean LEFTBOOL = false;

	private double isi = .2;
	private double speedIncrement = isi;

	private double rlSpeed;

	private Timer move = new Timer(1000/60, this);

	private boolean moveABitToTheRight = false;
	private boolean moveABitToTheLeft = false;
	private int pixelCounter = 0;

	private double defaultUpSpeed;

	private int farBound;

	public BouncyBall(double x, double y, int d, Color c, JPanel e) {
		this.x = x;
		this.d = d;
		this.y = y;
		initX = x;
		initY = y;
		rlSpeed = STANDARD_RLSPEED;
		defaultUpSpeed = STANDARD_DEFAULT_UPSPEED;

		Color[] temp = {Color.WHITE, c};
		colorArray = temp;

		startTimers();
	}
	public BouncyBall(double x, double y, int d, double rlSpeed, double defaultUpSpeed, Color c, JPanel e) {
		this.x = x;
		this.d = d;
		this.y = y;
		initX = x;
		initY = y;
		this.rlSpeed = rlSpeed;
		this.defaultUpSpeed = defaultUpSpeed;

		Color[] temp = {Color.WHITE, c};
		colorArray = temp;

		farBound = e.getPreferredSize().width + 6;

		startTimers();
	}
	/*public BouncyBall(double x, double y, int d, Color c, JPanel e, Object o) {
		this.x = x;
		this.d = d;
		this.y = y;
		initX = x;
		initY = y;
		rlSpeed = 2.25;

		Color[] temp = {Color.WHITE, c};
		colorArray = temp;

		env = e;
	}*/
	public BouncyBall(double x, double y, int d, double rlSpeed, double defaultUpSpeed, Color c, JPanel e, Object o) {
		this.x = x;
		this.d = d;
		this.y = y;
		initX = x;
		initY = y;
		this.rlSpeed = rlSpeed;
		this.defaultUpSpeed = defaultUpSpeed;

		Color[] temp = {Color.WHITE, c};
		colorArray = temp;
	}

	public void startTimers() {
		move.start();
	}

	public void stopTimers() {
		move.stop();
	}

	public void drawBall(Graphics2D g2d) {
		Point2D center = new Point2D.Float((int)x, (int)y);
		float radius;
		if(d > 1)
			radius = d/2;
		else
			radius = 1;
		float[] dist = {0.0f, 1.0f};
		RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colorArray);
		g2d.setPaint(rgp);

		g2d.fillOval((int)x - d/2, (int)y - d/2, d, d);
		g2d.setColor(Color.BLACK);
		g2d.drawOval((int)x - d/2, (int)y - d/2, d, d);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if(source == move) {
			checkBounce();
			if(direction == DOWN) {
				y += downSpeed;
				downSpeed += speedIncrement;
			}
			else if(direction == UP) {
				y -= upSpeed;
				upSpeed -= speedIncrement;
			}

			//if(rlDirection == RIGHTINT) x += rlSpeed;
			//if(rlDirection == LEFTINT) x -= rlSpeed;
			if(RIGHTBOOL)
				if(x + d/2 < farBound)
					x += rlSpeed;
			if(LEFTBOOL)
				if(x - d/2 > 0)
					x -= rlSpeed;
			int move = 5;
			if(moveABitToTheRight) {

				if(pixelCounter < move*5) {
					x += move;
					pixelCounter += move;
				}
				else {
					pixelCounter = 0;
					moveABitToTheRight = false;
				}
			}
			if(moveABitToTheLeft) {
				if(pixelCounter < 5 * move) {
					x -= move;
					pixelCounter += move;
				}
				else {
					pixelCounter = 0;
					moveABitToTheLeft = false;
				}
			}
		}
	}

	public void checkBounce() {
		if (upSpeed <= 0) {
			direction = DOWN;

			upSpeed = defaultUpSpeed;
			downSpeed = initSpeed;
			speedIncrement = isi;
		}
	}

	public int getX() {return (int)x;}
	public int getY() {return (int)y;}
	public int getD() {return d;}

	public void reset() {
		downSpeed = initSpeed;
		speedIncrement = isi;

		x = initX;
		y = initY;
	}

	public void setDirectionDown() {
		direction = DOWN;
		upSpeed = defaultUpSpeed;
		downSpeed = initSpeed;
		speedIncrement = isi;
	}
	public void setDirectionUp() {
		direction = UP;
		speedIncrement = isi;
		downSpeed = initSpeed;
	}

	public int getDirection() {
		return direction;
	}

	public void setUpSpeed(double speed) {
		upSpeed = speed;
	}
	public void resetUpSpeed() {
		upSpeed = defaultUpSpeed;
	}
	public int getRLD() {
		return rlDirection;
	}

	public void moveABit(int direct) {
		if(direct == LEFTINT)
			moveABitToTheRight = true;
		else if(direct == RIGHTINT)
			moveABitToTheLeft = true;
	}

	public int getBottomOfBall() {
		return (int)y + d/2;
	}

	public double getDownSpeed() {return downSpeed;}
	public double getUpSpeed() {return upSpeed;}

	//public void setRight(int r) {rlDirection = r;}
	//public void setLeft(int l) {rlDirection = l;}
	public void setRight(boolean b) {
		RIGHTBOOL = b;
		if(RIGHTBOOL)
			rlDirection = RIGHTINT;
		else if(LEFTBOOL);
		else rlDirection = STOPPED;
	}
	public void setLeft(boolean b) {
		LEFTBOOL = b;
		if(LEFTBOOL)
			rlDirection = LEFTINT;
		else if(RIGHTBOOL);
		else rlDirection = STOPPED;
	}

	public Shape getShape() {
		return new Ellipse2D.Double(x - d/2, y - d/2, d, d);
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public double getDefaultUpSpeed() {
		return defaultUpSpeed;
	}
	public double getRLSpeed() {
		return rlSpeed;
	}
}
