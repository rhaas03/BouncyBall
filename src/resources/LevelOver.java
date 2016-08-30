package resources;

import java.awt.*;

import javax.swing.*;

public class LevelOver {
	private JPanel env;
	private Rectangle bg, back, forward, bg2;
	
	private float fontSize1 = 60;
	private float fontSize2 = 22;
	
	private Color backColor = Color.WHITE, forwardColor = Color.WHITE;
	
	public LevelOver(JPanel e) {
		env = e;
		
		int buttonWidth;
		int buttonHeight;
		
		if(env.getWidth() > 400) {
			bg = new Rectangle(50, 50, env.getWidth() - 100, env.getHeight() - 100);
			buttonWidth = 170;
			buttonHeight = 75;
		}
		else {
			fontSize1 = 30;
			fontSize2 = 16;
			buttonWidth = 100;
			buttonHeight = 50;
			bg = new Rectangle(25, 50, env.getWidth() - 50, 150);
		}
		
		bg2 = bg;
		int buttonY = (int)bg.getY() + (int)bg.getHeight() - buttonHeight - 20;
		back = new Rectangle((int)bg.getX() + 20, buttonY, buttonWidth, buttonHeight);
		forward = new Rectangle((int)bg.getX() + (int)bg.getWidth() - buttonWidth - 20, buttonY,
				buttonWidth, buttonHeight);
	}
	
	public void drawLevelOver(Graphics2D g2d) {
		//Background rect
		g2d.setColor(new Color(0, 0, 0, 200));
		g2d.fill(bg);
		g2d.setColor(new Color(255, 255, 255, 150));
		g2d.fill(bg2);
		
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, fontSize1));
		FontMetrics fm = g2d.getFontMetrics();
		String s = "WELL DONE!";
		g2d.setColor(Color.RED);
		g2d.drawString(s, (int)bg.getCenterX() - fm.stringWidth(s)/2, (int)bg.getY() + 10 + fm.getAscent());
		
		g2d.setColor(Color.BLACK);
		g2d.fill(back);
		g2d.fill(forward);
		
		g2d.setColor(backColor);
		g2d.setFont(g2d.getFont().deriveFont(fontSize2));
		fm = g2d.getFontMetrics();
		s = "Back to";
		g2d.drawString(s, (int)back.getCenterX() - fm.stringWidth(s)/2, (int)back.getY() + 5 + fm.getAscent());
		s = "Levels Menu";
		g2d.drawString(s, (int)back.getCenterX() - fm.stringWidth(s)/2,
				(int)back.getY() + 5 + fm.getAscent() + fm.getHeight());
		
		g2d.setColor(forwardColor);
		s = "Forward to";
		g2d.drawString(s, (int)forward.getCenterX() - fm.stringWidth(s)/2, (int)forward.getY() + 5 + fm.getAscent());
		s = "Next Level";
		g2d.drawString(s, (int)forward.getCenterX() - fm.stringWidth(s)/2,
				(int)forward.getY() + 5 + fm.getAscent() + fm.getHeight());
	}
	
	public void setBackColor(Color c) {
		backColor = c;
	}
	public void setForwardColor(Color c) {
		forwardColor = c;
	}
	
	public boolean backCol(int mouseX, int mouseY) {
		Rectangle mouse = new Rectangle(mouseX, mouseY, 1, 1);
		return mouse.intersects(back);
	}
	public boolean forwardCol(int mouseX, int mouseY) {
		Rectangle mouse = new Rectangle(mouseX, mouseY, 1, 1);
		return mouse.intersects(forward);
	}
}
