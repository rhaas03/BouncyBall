package resources;

import java.awt.*;
import javax.swing.*;

public class ComingSoon {
	private JPanel env;
	
	public ComingSoon(JPanel e) {
		env = e;
	}
	
	public void drawComingSonn(Graphics2D g2d) {
		drawInfo(g2d);
	}
	
	private void drawInfo(Graphics2D g2d) {
		g2d.setColor(Color.RED);
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 32f));
		FontMetrics fm = g2d.getFontMetrics();
		int fontY = 100;
		String s = "CURRENTLY IN PROGRESS!";
		g2d.drawString(s, env.getWidth()/2 - fm.stringWidth(s)/2, fontY);
		
		smile(g2d, fontY);
	}
	
	private void smile(Graphics2D g2d, int y) {
		int circleD = 100;
		int circleX = env.getWidth()/2 - circleD/2;
		int circleY = y + 5;

		//BACK
		g2d.setColor(Color.YELLOW);
		g2d.fillOval(circleX, circleY, circleD, circleD);
		g2d.setColor(Color.BLACK);
		g2d.drawOval(circleX, circleY, circleD, circleD);
		
		//EYES
		g2d.fillOval(circleX + 20, circleY + 30, 10, 10);
		g2d.fillOval(circleX + circleD - 30, circleY + 30, 10, 10);
		
		//SMILE
		Stroke oldS = g2d.getStroke();
		g2d.setStroke(new BasicStroke(5));
		g2d.drawArc(circleX + 20, circleY + circleD - 60, 60, 40, 190, 160);
		g2d.setStroke(oldS);
	}
}
