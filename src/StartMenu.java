import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class StartMenu implements MouseMotionListener, MouseListener{
	private Environment env;
	private int ml;

	private FontMetrics fm;

	public StartMenu(Environment environment) {
		env = environment;
		env.setMouseLine(20);
		ml = env.getMouseLine();
	}

	public void drawMenu(Graphics2D g2d) {
		fm = g2d.getFontMetrics();
		drawTitleText(g2d);
		drawMenuOptions(g2d);
		signature(g2d);
	}

	private int y = 0;

	private void drawTitleText(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);

		String gameTitle1 = "Bouncy";
		String gameTitle2 = "Ball";

		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 50f));
		fm = g2d.getFontMetrics();

		y = ml + fm.getAscent();

		//int x = env.getWidth()/2 - stringWidth/2;
		int x = 30;
		g2d.drawString(gameTitle1, x, y);

		y += fm.getAscent();
		g2d.drawString(gameTitle2, x, y);
		y += fm.getDescent() + 20;
	}

	private RoundRectangle2D play, settings, about, editor;

	private int playX, settingsX, aboutX, editX;
	private Color pC = Color.BLACK, sC = Color.BLACK, aC = Color.BLACK, eC = Color.BLACK;

	private void drawMenuOptions(Graphics2D g2d) {
		int playWidth = 250;
		int height = 40;
		int angle = 30;

		//Color bgColor = new Color(45, 148, 52);
		Color bgColor = new Color(19, 73, 252);

		play = new RoundRectangle2D.Double(env.getWidth()/2 - playWidth/2, y, playWidth, height, angle, angle);
		g2d.setColor(bgColor);
		g2d.fill(play);
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, (height - 15f)));
		fm = g2d.getFontMetrics();
		g2d.setColor(pC);
		playX = (int)(play.getX() + play.getArcWidth()/2);
		g2d.drawString("Play", playX,(y + 10) + fm.getAscent() - fm.getDescent());
		y += play.getHeight() + 10;

		settings = new RoundRectangle2D.Double(env.getWidth()/2 - playWidth/2, y, playWidth, height, angle, angle);
		g2d.setColor(bgColor);
		g2d.fill(settings);
		g2d.setColor(sC);
		settingsX = (int)(settings.getX() + settings.getArcWidth()/2);
		g2d.drawString("Settings", settingsX,(y + 10) + fm.getAscent() - fm.getDescent());
		y += settings.getHeight() + 10;

		about = new RoundRectangle2D.Double(env.getWidth()/2 - playWidth/2, y, playWidth, height, angle, angle);
		g2d.setColor(bgColor);
		g2d.fill(about);
		g2d.setColor(aC);
		aboutX = (int)(about.getX() + about.getArcWidth()/2);
		g2d.drawString("About", aboutX,(y + 10) + fm.getAscent() - fm.getDescent());
		y += about.getHeight() + 10;

		editor = new RoundRectangle2D.Double(env.getWidth()/2 - playWidth/2, y, playWidth, height, angle, angle);
		g2d.setColor(bgColor);
		g2d.fill(editor);
		g2d.setColor(eC);
		editX = (int)(editor.getX() + editor.getArcWidth()/2);
		g2d.drawString("Custom Levels", editX,(y + 10) + fm.getAscent() - fm.getDescent());
		y += about.getHeight() + 10;
	}

	private void signature(Graphics2D g2d) {
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 16f));
		String by = "by Ryan Haas";
		fm = g2d.getFontMetrics();

		g2d.setColor(Color.BLACK);
		g2d.drawString(by, env.getWidth() - fm.stringWidth(by) - 2, env.getHeight() - fm.getDescent());
	}

	private boolean mouseRoundRect(int mouseX, int mouseY, RoundRectangle2D roundRect) {
		Rectangle mouse = new Rectangle(mouseX, mouseY, 1, 1);

		try {
			if(roundRect.intersects(mouse)) return true;
			else return false;
		}
		catch(NullPointerException e) {
			return false;
		}
	}

	public void mouseMoved(MouseEvent e) {
		boolean keepGoing = true;
		boolean handCursor = false;

		if(mouseRoundRect(e.getX(), e.getY(), play) && keepGoing) {
			pC = Color.WHITE;
			keepGoing = false;
			handCursor = true;
		}
		else pC = Color.BLACK;

		if(mouseRoundRect(e.getX(), e.getY(), settings)) {
			sC = Color.WHITE;
			keepGoing = false;
			handCursor = true;
		}
		else sC = Color.BLACK;

		if(mouseRoundRect(e.getX(), e.getY(), about)) {
			aC = Color.WHITE;
			keepGoing = false;
			handCursor = true;
		}
		else aC = Color.BLACK;

		if(mouseRoundRect(e.getX(), e.getY(), editor)) {
			eC = Color.WHITE;
			keepGoing = false;
			handCursor = true;
		}
		else eC = Color.BLACK;

		if(handCursor) env.setCursor(new Cursor(Cursor.HAND_CURSOR));
		else env.setCursor(Cursor.getDefaultCursor());
	}

	public void mousePressed(MouseEvent e) {
		if(mouseRoundRect(e.getX(), e.getY(), play)) {
			env.addHistory(env.getScreen(), env.getPreferredSize());
			env.setScreen(Environment.LEVELSCREEN);
			env.changeSize(500, 240);
			env.levelsScreen.checkAvailableLevels();
			env.levelsScreen.updateLevels();

			env.removeSample();
			env.updateListeners();

			pC = Color.BLACK;
			Runtime.getRuntime().gc();
		}
		if(mouseRoundRect(e.getX(), e.getY(), settings)) {
			env.addHistory(env.getScreen(), env.getPreferredSize());
			env.setScreen(Environment.SETTINGSSCREEN);
			env.changeSize(env.getPreferredSize().width + 50, env.getPreferredSize().height + 20);

			env.removeSample();
			env.checkSettings();
			env.addSettings();
			env.updateListeners();

			sC = Color.BLACK;
			Runtime.getRuntime().gc();
		}
		if(mouseRoundRect(e.getX(), e.getY(), about)) {
			env.addHistory(env.getScreen(), env.getPreferredSize());
			env.setScreen(Environment.ABOUTSCREEN);
			env.changeSize(env.getPreferredSize().width + 80, env.getPreferredSize().height);

			env.removeSample();
			env.addAbout();
			env.updateListeners();

			aC = Color.BLACK;
			Runtime.getRuntime().gc();
		}

		if(mouseRoundRect(e.getX(), e.getY(), editor)) {
			new EditorMenu(env.frame.getX(), env.frame.getY(), env);
			env.setMusic(false);
			env.frame.setVisible(false);;
			Runtime.getRuntime().gc();
		}

		env.setCursor(Cursor.getDefaultCursor());
		env.repaint();
	}

	public void removeListeners() {
		env.removeMouseListener(this);
		env.removeMouseMotionListener(this);
	}

	public void addListeners() {
		env.addMouseListener(this);
		env.addMouseMotionListener(this);
	}

	public void mouseDragged(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
