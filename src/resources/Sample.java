package resources;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

import javax.swing.*;

public class Sample extends JPanel implements ActionListener {
	private Ball ball;
	private Timer update = new Timer(1000/60, this);

	public Sample(int x, int y, Color c) {
		setPreferredSize(new Dimension(50, 50));
		//setBackground(new Color(0, 0, 0, 0));
		setOpaque(false);

		setBounds(x, y, getPreferredSize().width, getPreferredSize().height);

		ball = new Ball(getWidth()/2, c);

		update.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		ball.drawBall(g2d);
	}

	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	private class Ball implements ActionListener {
		private double x, y;
		private int d;
		private Color[] colorArr;

		private final int up = 1;
		private final int down = 2;
		private int direction = down;

		private double initSpeed = .1/2;
		private double downSpeed = initSpeed;
		private double upSpeed;

		private double isi = .1;
		private double speedIncrement = isi;

		private int floor;

		private Timer move = new Timer(1000/60, this);

		private Ball(double x, Color c) {
			d = 12;
			this.x = x;
			this.y = d/2;

			Color[] temp = {Color.WHITE, c};
			colorArr = temp;

			floor = getHeight();

			move.start();
		}

		private void drawBall(Graphics2D g2d) {
			g2d.setColor(Color.BLACK);
			g2d.drawLine(0, floor - 1, getWidth(), floor - 1);

			Point2D center = new Point2D.Float((int)x, (int)y);
			float radius = d/2;
			float[] dist = {0.0f, 1.0f};
			RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colorArr);
			g2d.setPaint(rgp);

			g2d.fillOval((int)x - d/2, (int)y - d/2, d, d);
			g2d.setColor(Color.BLACK);
			g2d.drawOval((int)x - d/2, (int)y - d/2, d, d);
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			if(source == move) {
				checkBounce();
				if(direction == down) {
					y += downSpeed;
					downSpeed += speedIncrement;
				}
				else if(direction == up) {
					y -= upSpeed;
					upSpeed -= speedIncrement;
				}
			}

			repaint();
		}

		private double defaultUpSpeed = 4.85/2;

		private void checkBounce() {
			if(y + d/2 >= floor) {
				y = floor - d/2;
				direction = up;

				upSpeed = defaultUpSpeed;
				speedIncrement = isi;
				downSpeed = initSpeed;
			}
			else if (upSpeed <= 0) {
				direction = down;

				upSpeed = initSpeed;
				downSpeed = initSpeed;
				speedIncrement = isi;
			}
		}
	}
}
