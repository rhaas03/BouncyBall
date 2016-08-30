package resources;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.Serializable;

import javax.swing.Timer;

public class Platform implements ActionListener, Serializable {
	private int x, y, w, h;
	private double boost;
	private int startX, startY;

	private int type;
	public static final int NORMAL = 0;
	public static final int BOOST = 1;
	public static final int MOVING = 2;
	public static final int DEAD = 3;

	private int horizVerti;
	public static final int HORIZONTAL = 1;
	public static final int VERTICAL = 2;

	private int direction;
	private final int RIGHT = 1;
	private final int LEFT = 2;
	private final int UP = 3;
	private  final int DOWN = 4;

	private int moveSpeed = 0;

	private int max, maxDistance;

	private Timer move = new Timer(1000/60, this);

	private int subBox;

	public Platform(int x, int y, int w, int h, int type) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.type = type;
	}

	public Platform(int x, int y, int w, int h, int type, double boost) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.type = type;
		this.boost = boost;
	}

	public Platform(int x, int y, int w, int h, int type, int horizVerti, int max) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.type = type;
		this.horizVerti = horizVerti;
		this.max = max;
		maxDistance = Math.abs(max - y - h);
		startX = x;
		startY = y;
		moveSpeed = 1;

		if(horizVerti == HORIZONTAL) {
			subBox = h;
			if(this.max < startX) {
				this.max = startX;
				startX = max;
				direction = LEFT;
			}
			else direction = RIGHT;
			this.x += subBox;
		}
		else if(horizVerti == VERTICAL) {
			subBox = w;
			if(max < startY) {
				this.max = startY;
				startY = max;
				direction = UP;
			}
			else direction = DOWN;
		}

		move.start();
	}

	public Platform(int x, int y, int w, int h, int type, int horizVerti, int max, Object dontMove) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.type = type;
		this.horizVerti = horizVerti;
		this.max = max;
		startX = x;
		startY = y;

		if(horizVerti == HORIZONTAL) {
			subBox = h;
			maxDistance = Math.abs(max - x - h);
			if(this.max < startX) {
				this.max = startX;
				startX = max;
				direction = LEFT;
			}
			else direction = RIGHT;
			this.x += subBox;
		}
		else if(horizVerti == VERTICAL) {
			subBox = w;
			maxDistance = Math.abs(max - y);
			if(max < startY) {
				this.max = startY;
				startY = max;
				direction = UP;
			}
			else direction = DOWN;
		}
	}

	public void drawPlatform(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(x, y, w, h);

		if(type == NORMAL) g2d.setColor(new Color(60, 60, 60));
		else if(type == BOOST) g2d.setColor(new Color(0, 209, 205));
		else if(type == MOVING) {
			Stroke initStroke = g2d.getStroke();
			Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
			g2d.setStroke(dashed);
			if(horizVerti == HORIZONTAL) {
				g2d.drawRect(startX, y, subBox, h);
				g2d.drawRect(max - subBox, y, subBox, h);
			}
			else if(horizVerti == VERTICAL) {
				g2d.drawRect(x, startY - h, subBox, h);
				g2d.drawRect(x, max - h, subBox, h);
			}
			g2d.setStroke(initStroke);
			g2d.setColor(Color.WHITE);
		}
		else if(type == DEAD)g2d.setColor(Color.RED);

		g2d.fillRect(x + 2, y + 2, w - 4, h - 4);
	}

	public boolean doesCollide(int userX, int userY, int userD) {
		Rectangle platform = new Rectangle(x, y, w, h);
		Ellipse2D user = new Ellipse2D.Double(userX, userY, userD, userD);
		Area pA = new Area(platform);
		Area uA = new Area(user);
		pA.intersect(uA);

		if(!pA.isEmpty()) return true;
		else return false;
	}

	public int getX() {return x;}
	public int getY() {return y;}
	public int getW() {return w;}
	public int getH() {return h;}
	public int getType() {
		return type;
	}
	public int getBottomOfPlatform() {
		return y + h;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == move) {
			if(horizVerti == HORIZONTAL) {
				if(direction == RIGHT) {
					if(x + moveSpeed + w  + subBox < max)
						x += moveSpeed;
					else
						direction = LEFT;
				}
				else  if(direction == LEFT) {
					if(x - moveSpeed - subBox > startX)
						x -= moveSpeed;
					else
						direction = RIGHT;
				}
			}
			else if(horizVerti == VERTICAL) {
				if(direction == DOWN) {
					if(y + moveSpeed + h + h < max)
						y += moveSpeed;
					else
						direction = UP;
				}
				else if(direction == UP) {
					if(y - moveSpeed > startY)
						y -= moveSpeed;
					else
						direction = DOWN;
				}
			}
		}
	}

	public void setLocation(int x, int y) {
		if(type != MOVING) {
			this.x = x;
			this.y = y;
		}
		else {
			if(horizVerti == VERTICAL) {
				this.x = x;
				this.y = y;
				startX = x;
				startY = y;
				max = y + maxDistance;
			}
			if(horizVerti == HORIZONTAL) {
				this.x = x;
				this.y = y;
				startX = x - subBox;
				startY = y;
				max = x + maxDistance;
			}
		}
	}

	public Shape getShape() {
		return new Rectangle(x, y, w, h);
	}

	public double getBoost() {
		return boost;
	}

	public int getHorizVerti() {
		return horizVerti;
	}

	public int getMax() {
		return max;
	}

	public int getMoveSpeed() {
		if(direction == UP)
			return - moveSpeed;
		else
			return moveSpeed;
	}
}
