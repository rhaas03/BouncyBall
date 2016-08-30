package resources;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

public class LevelBox {
	private int x, y, d;
	
	@SuppressWarnings("unused")
	private JPanel window;
	
	private int level;
	
	public static final int ROUNDED = 1;
	public static final int NORMAL = 2;
	@SuppressWarnings("unused")
	private int type;
	
	private Color bg, fg;
	private Font font;
	private boolean enabled = true;
	
	Object rect;

	public LevelBox(int x, int y, int d, int level, int type, Color bg, Color fg, float fontSize, JPanel window) {
		this.x = x;
		this.y = y;
		this.d = d;
		this.level = level;
		this.type = type;
		this.bg = bg;
		this.fg = fg;
		this.window = window;
		this.font = window.getFont().deriveFont(fontSize);
		
		if(type == ROUNDED)
			rect = new RoundRectangle2D.Double(this.x, this.y, this.d, this.d, 20, 20);
		if(type == NORMAL)
			rect = new Rectangle(this.x, this.y, this.d, this.d);
	}
	
	public void drawLevelBox(Graphics2D g2d) {
		g2d.setColor(bg);
		Shape shape = (Shape)rect;
		g2d.fill(shape);
		
		if(enabled)
			g2d.setColor(fg);
		else
			g2d.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 100));
		g2d.setFont(font.deriveFont(Font.BOLD));
		
		FontMetrics fm = g2d.getFontMetrics();
		String s = Integer.toString(level);
		g2d.drawString(s, (int)(shape.getBounds().getCenterX() - fm.stringWidth(s)/2),
				(int)(shape.getBounds().getCenterY() + fm.getAscent()/2 - fm.getDescent()/2));
		
		
	}
	
	public boolean mouseRectCollision(int mouseX, int mouseY) {
		Shape shape = (Shape)rect;
		Rectangle mouse = new Rectangle(mouseX, mouseY, 1, 1);
		
		if(shape.intersects(mouse)) return true;
		else return false;
	}
	
	public void changeBackground(Color c) {bg = c;}
	public void changeForeground(Color c) {fg = c;}
	public int getLevel() {return level;}
	
	public Shape getObject() {
		return (Shape)rect;
	}
	
	public void setEnabled(boolean e) {
		enabled = e;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
}
