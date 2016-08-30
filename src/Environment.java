import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.*;
import javax.swing.*;

import resources.Sample;
import resources.SettingsSave;

public class Environment extends JPanel implements MouseMotionListener, MouseListener, ActionListener, KeyListener {
	public JFrame frame = new JFrame();
	private Container canvas;

	//Various Screens
	private StartMenu start;
	public LevelMenu levelsScreen;
	private Sample sample;
	private Settings settings;
	private About aboutScreen;

	//All the levels
	private Level1 level1;
	private Level2 level2;
	private Level3 level3;
	private Level4 level4;
	private Level5 level5;
	private Level6 level6;
	private Level7 level7;
	private Level8 level8;
	private Level9 level9;

	private Timer refresh = new Timer(1000/60, this);

	//Integers to specify which screen
	private int screen = 0;
	public static final int STARTSCREEN = 1;
	public static final int LEVELSCREEN = 2;
	public static final int SETTINGSSCREEN = 3;
	public static final int ABOUTSCREEN = 4;

	public static final int LEVELONE = 6;
	public static final int LEVELTWO = 7;
	public static final int LEVELTHREE = 8;
	public static final int LEVELFOUR = 9;
	public static final int LEVELFIVE = 10;
	public static final int LEVELSIX = 11;
	public static final int LEVELSEVEN = 12;
	public static final int LEVELEIGHT = 13;
	public static final int LEVELNINE = 14;

	private boolean antialias = true;
	private boolean music = true;
	private boolean removed;
	private boolean customColor = false;
	public boolean consoleOpen = false;

	//mouseX and mouseY is for moving the frame, and the mouseLine is the divisor of the
	//head of the frame and the rest of the program
	private int mouseX, mouseY, mouseLine;

	//Variables that handle moving back a screen
	private ArrayList<Integer> screenHistory = new ArrayList<Integer>();
	private ArrayList<Dimension> dimensionHistory = new ArrayList<Dimension>();
	private int screenCounter = 0;
	private int dimensionCounter = 0;

	private static String path = Environment.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	public static final File LEVELSFILE = new File(path + "saveInfo/level.ser");
	public static final File SETTINGSFILE = new File(path + "saveInfo/settings.ser");
	private boolean toggleShowLocs = false;

	public static final double STAR_ROTATE_SPEED = .02;
	public Font apple2Font;
	public Font samsungFont;
	public Font comfortaa;
	public Font blackTuesday;
	public Font samsung2;

	private Clip musicClip;

	public Environment(int xLoc, int yLoc) {
		super(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int border = 1;
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(border, border, border, border, Color.BLACK));
		frame.setUndecorated(true);

		if(System.getProperty("os.name").toLowerCase().contains("windows"))
			frame.setLocation(xLoc, yLoc);
		else
			frame.setLocation(xLoc, yLoc + 20);

		frame.setTitle("Bouncy Ball: " + About.VERSION);
		frame.addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);

		ImageIcon fav = new ImageIcon(getClass().getResource("/resources/favicon.png"));
		frame.setIconImage(fav.getImage());

		canvas = frame.getContentPane();
		//Add any other JComponents Here
		canvas.add(this);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			URL sound = getClass().getResource("/resources/origin.wav");
			AudioInputStream ais = AudioSystem.getAudioInputStream(sound);
			musicClip = AudioSystem.getClip();
			musicClip.open(ais);
			FloatControl fc = (FloatControl)musicClip.getControl(FloatControl.Type.MASTER_GAIN);
			fc.setValue(-40);
			apple2Font = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/Apple2.ttf"));
			samsungFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/samsung.ttf"));
			comfortaa = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/comfortaa.ttf"));
			blackTuesday = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/black_tuesday.ttf"));
			samsung2 = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/samsung2.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(samsungFont);
			ge.registerFont(apple2Font);
			ge.registerFont(comfortaa);
			ge.registerFont(blackTuesday);
			ge.registerFont(samsung2);
		}
		catch(Exception e) {
			System.out.println("whoops: " + e);
		}

		checkSettings();
		checkMusic();

		frame.pack();

		start = new StartMenu(this);
		levelsScreen = new LevelMenu(this);
		sample = new Sample(125, 75, getBallColor());
		removed = false;
		screen = STARTSCREEN;

		add(sample);
		frame.setVisible(true);

		refresh.start();

		changeSize(new Dimension(300, 400));

		addHistory(screen, getPreferredSize());
		updateListeners();

		//System.out.println(path);
		//System.out.println(Environment.class.getResource("Environment.class"));
		Runtime.getRuntime().gc();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		if(antialias) {
			g2d.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}

		if(screen == STARTSCREEN)
			start.drawMenu(g2d);
		else if(screen == LEVELSCREEN)
			levelsScreen.drawLevels(g2d);
		else if(screen == LEVELONE)
			level1.drawLevel(g2d);
		else if(screen == LEVELTWO)
			level2.drawLevel(g2d);
		else if(screen == LEVELTHREE)
			level3.drawLevel(g2d);
		else if(screen == LEVELFOUR)
			level4.drawLevel(g2d);
		else if(screen == LEVELFIVE)
			level5.drawLevel(g2d);
		else if(screen == LEVELSIX)
			level6.drawLevel(g2d);
		else if(screen == LEVELSEVEN)
			level7.drawLevel(g2d);
		else if(screen == LEVELEIGHT)
			level8.drawLevel(g2d);
		else if(screen == LEVELNINE)
			level9.drawLevel(g2d);

		topMenu(g2d);
	}

	private Rectangle quit, mini, back;
	private int grayShade = 150;
	private Color qC = new Color(grayShade, grayShade, grayShade);
	private Color mC = new Color(grayShade, grayShade, grayShade);
	private Color bC = new Color(grayShade, grayShade, grayShade);

	private void topMenu(Graphics2D g2d) {
		FontMetrics fm = g2d.getFontMetrics();

		g2d.setColor(new Color(grayShade, grayShade, grayShade));
		g2d.fillRect(0, 0, getWidth(), mouseLine);

		int height = mouseLine - 4;
		int width = height + 6;
		quit = new Rectangle(getWidth() - width -  8, 2, width, height);
		mini = new Rectangle(getWidth() - (int)quit.getWidth() - width - 16, 2, width, height);
		back = new Rectangle(getWidth() - (int)quit.getWidth() - (int)mini.getWidth() - width - 24, 2, width, height);

		//g2d.setColor(Color.BLACK);
		g2d.setColor(qC);
		g2d.fill(quit);
		g2d.setColor(mC);
		g2d.fill(mini);
		if(screen != STARTSCREEN && screen != SETTINGSSCREEN) {
			g2d.setColor(bC);
			g2d.fill(back);
			g2d.setColor(Color.WHITE);
			g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 12f));
			fm = g2d.getFontMetrics();
			String arrow = "\u2190";
			g2d.drawString(arrow, (int)back.getCenterX() - fm.stringWidth(arrow)/2, (int)back.getCenterY() + fm.getAscent()/2 - fm.getDescent());
		}
		g2d.setColor(Color.WHITE);

		int minusW = (int)mini.getWidth() - 8;
		int minusH = 2;
		g2d.fillRect((int)mini.getX() + 4,
				(int)((mini.getY() + (mini.getY() + mini.getHeight()))/2) - minusH/2, minusW, minusH);

		AffineTransform old = g2d.getTransform();
		double rotateX = (int)(quit.getX() + (quit.getX() + quit.getWidth()))/2;
		double rotateY = (int)((quit.getY() + (quit.getY() + quit.getHeight()))/2);

		g2d.fillOval((int)rotateX, (int)rotateY, 1, 1);
		g2d.rotate(Math.toRadians(30), rotateX, rotateY);
		int quitW = width;
		int quitH = 2;
		g2d.fillRect((int)quit.getX(),
				(int)((quit.getY() + (quit.getY() + quit.getHeight()))/2) - quitH/2, quitW, quitH);
		g2d.setTransform(old);
		g2d.rotate(Math.toRadians(-30), rotateX, rotateY);
		g2d.fillRect((int)quit.getX(),
				(int)((quit.getY() + (quit.getY() + quit.getHeight()))/2) - quitH/2, quitW, quitH);
		g2d.setTransform(old);

		g2d.setFont(getFont().deriveFont(Font.BOLD, 16f));
		g2d.setColor(Color.BLACK);

		String headText = "Bouncy Ball: Version " + About.VERSION;
		if(screen != STARTSCREEN)
			g2d.drawString(headText, 5, fm.getHeight() - fm.getDescent() + 4);
		else
			g2d.drawString(headText, 5, fm.getHeight() - fm.getDescent());

	}

	private boolean isMoving = false;

	public void mousePressed(MouseEvent e) {
		if(e.getY() <= mouseLine && !mouseRect(e.getX(), e.getY(), mini) && !mouseRect(e.getX(), e.getY(), quit)) {
			mouseX = e.getX();
			mouseY = e.getY();
			isMoving = true;
		}

		if(mouseRect(e.getX(), e.getY(), mini)) {
			frame.setState(JFrame.ICONIFIED);
			mC = Color.WHITE;
		}
		if(mouseRect(e.getX(), e.getY(), quit))
			System.exit(0);

		if(screen != STARTSCREEN) {
			if(mouseRect(e.getX(), e.getY(), back)) {
				moveBackScreen();
				isMoving = false;
				bC = Color.WHITE;
			}
		}

		repaint();
	}

	public void mouseDragged(MouseEvent e) {
		if(isMoving) {
			int x = e.getXOnScreen();
			int y = e.getYOnScreen();

			frame.setLocation(x - mouseX, y - mouseY);
		}
	}

	public void mouseMoved(MouseEvent e) {
		int otherGray = grayShade + 50;
		int alpha = 100;
		if(mouseRect(e.getX(), e.getY(), mini))
			mC = new Color(otherGray, otherGray, otherGray, alpha);
		else
			mC = new Color(grayShade, grayShade, grayShade);

		if(mouseRect(e.getX(), e.getY(), quit))
			qC = Color.RED;
		else
			qC = new Color(grayShade, grayShade, grayShade);

		if(screen != STARTSCREEN) {
			if(mouseRect(e.getX(), e.getY(), back))
				bC = new Color(otherGray, otherGray, otherGray, alpha);
			else
				bC = new Color(grayShade, grayShade, grayShade);
		}

		if(toggleShowLocs)
			System.out.println(e.getX() + "x" + e.getY());

		repaint();
	}

	public void changeSize(int w, int h) {
		Dimension temp = new Dimension(w, h);

		canvas.remove(this);
		setPreferredSize(temp);
		canvas = frame.getContentPane();
		canvas.add(this);
		frame.pack();
	}
	public void changeSize(Dimension d) {
		setPreferredSize(d);
		canvas = frame.getContentPane();
		frame.pack();
	}

	public boolean mouseRect(int mouseX, int mouseY, Rectangle r) {
		Rectangle mouse = new Rectangle(mouseX, mouseY, 1, 1);

		try {
			if(r.intersects(mouse)) return true;
			else return false;
		}
		catch(NullPointerException e) {
			return false;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(!hasFocus() && !frame.hasFocus())
			grayShade = 200;
		else
			grayShade = 150;

		repaint();
	}

	private void addSample() {
		if(removed) {
			sample = new Sample(125, 75, getBallColor());
			add(sample);
			removed = false;
		}
	}
	public void removeSample() {
		if(!removed) {
			remove(sample);
			sample = null;
			removed = true;
		}
	}

	//Safeties
	private boolean removedSettings = true;
	private boolean removedAbout = true;

	public void addSettings() {
		if(removedSettings) {
			settings = new Settings(this);
			add(settings);
			frame.pack();
			removedSettings = false;
		}
	}
	private void removeSettings() {
		if(!removedSettings) {
			remove(settings);
			settings = null;
			removedSettings = true;
		}
	}

	public void addAbout() {
		if(removedAbout) {
			aboutScreen = new About(this);
			add(aboutScreen);
			frame.pack();
			removedAbout = false;
		}
	}
	private void removeAbout() {
		if(!removedAbout) {
			remove(aboutScreen);
			aboutScreen = null;
			removedAbout = true;
		}
	}

	private void setLevelsNull() {
		level1 = null;
		level2 = null;
		level3 = null;
		level4 = null;
		level5 = null;
		level6 = null;
		level7 = null;
		level8 = null;
		level9 = null;
	}

	public void setLevel(int l) {
		int level = l;
		setLevelsNull();
		if(level == 1)
			level1 = new Level1(this);
		else if(level == 2)
			level2 = new Level2(this);
		else if(level == 3)
			level3 = new Level3(this);
		else if(level == 4)
			level4 = new Level4(this);
		else if(level == 5)
			level5 = new Level5(this);
		else if(level == 6)
			level6 = new Level6(this);
		else if(level == 7)
			level7 = new Level7(this);
		else if(level == 8)
			level8 = new Level8(this);
		else if(level == 9)
			level9 = new Level9(this);
	}

	public void removeAllListeners() {
		start.removeListeners();
		levelsScreen.removeListeners();
		if(level1 != null)
			level1.removeListeners();
		if(level2 != null)
			level2.removeListeners();
		if(level3 != null)
			level3.removeListeners();
		if(level4 != null)
			level4.removeListeners();
		if(level5 != null)
			level5.removeListeners();
		if(level6 != null)
			level6.removeListeners();
		if(level7 != null)
			level7.removeListeners();
		if(level8 != null)
			level8.removeListeners();
		if(level9 != null)
			level9.removeListeners();
	}

	public void updateListeners() {
		removeAllListeners();

		if(screen == STARTSCREEN)
			start.addListeners();
		else if(screen == LEVELSCREEN)
			levelsScreen.addListeners();
		else if(screen == LEVELONE)
			level1.addListeners();
		else if(screen == LEVELTWO)
			level2.addListeners();
		else if(screen == LEVELTHREE)
			level3.addListeners();
		else if(screen == LEVELFOUR)
			level4.addListeners();
		else if(screen == LEVELFIVE)
			level5.addListeners();
		else if(screen == LEVELSIX)
			level6.addListeners();
		else if(screen == LEVELSEVEN)
			level7.addListeners();
		else if(screen == LEVELEIGHT)
			level8.addListeners();
		else if(screen == LEVELNINE)
			level9.addListeners();
	}

	public void moveBackScreen() {
		dimensionCounter -= 1;
		screenCounter -= 1;

		//Has to go before the screen is updated.
		if(screen == SETTINGSSCREEN)
			removeSettings();
		if(screen == ABOUTSCREEN)
			removeAbout();

		screen = screenHistory.get(screenCounter);
		updateListeners();

		if(screen == STARTSCREEN)
			addSample();
		if(screen == LEVELSCREEN) {
			levelsScreen.checkAvailableLevels();
			levelsScreen.updateLevels();
		}

		setLevelsNull();

		changeSize(dimensionHistory.get(dimensionCounter));

		repaint();
		frame.requestFocus();
		Runtime.getRuntime().gc();
	}

	public int getScreen() {return screen;}

	public void addHistory(int s, Dimension d) {
		dimensionHistory.add(d);
		dimensionCounter++;

		screenHistory.add(s);
		screenCounter++;
	}

	public void setScreen(int s) {screen = s;}
	public void setMouseLine(int ml) {mouseLine = ml;}
	public int getMouseLine() {return mouseLine;}

	public void mouseReleased(MouseEvent e) {
		isMoving = false;
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void saveLevel(int newLevel) {
		if(newLevel > levelsScreen.getAvailableLevels()) {
			try {
				FileOutputStream file = new FileOutputStream(LEVELSFILE);
				ObjectOutputStream oos = new ObjectOutputStream(file);
				oos.writeObject(newLevel);
				oos.close();
			}
			catch(IOException e) {
				System.out.println("Couldn't Save");
			}
		}
	}

	public void saveLevelManually(int level) {
		try {
			FileOutputStream file = new FileOutputStream(LEVELSFILE);
			ObjectOutputStream oos = new ObjectOutputStream(file);
			oos.writeObject(level);
			oos.close();
		}
		catch(IOException e) {
			System.out.println("Couldn't Save");
		}
	}

	public void saveSettings(Object ss) {
		try {
			FileOutputStream file = new FileOutputStream(SETTINGSFILE);
			ObjectOutputStream oos = new ObjectOutputStream(file);
			oos.writeObject(ss);
			oos.close();
		}
		catch(IOException e) {
			System.out.println("Couldn't Save");
		}
	}

	public void checkSettings() {
		try {
			FileInputStream file = new FileInputStream(SETTINGSFILE);
			ObjectInputStream ois = new ObjectInputStream(file);

			SettingsSave ss = (SettingsSave)ois.readObject();
			antialias = ss.getAntialias();
			ballColor = ss.getBallColor();
			music = ss.getMusic();
			customColor = ss.getCustom();
			ois.close();
		}
		catch(FileNotFoundException e) {
			try {
				SETTINGSFILE.getParentFile().mkdirs();
				SETTINGSFILE.createNewFile();
			}
			catch(IOException e1) {
				System.out.println("IOException");
			}
		}
		catch(IOException e) {
			saveSettings(new SettingsSave(antialias, ballColor, music));
		}
		catch(ClassNotFoundException e) {
			saveSettings(new SettingsSave(antialias, ballColor, music));
		}
	}

	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		if(k == KeyEvent.VK_M)
			toggleShowLocs = !toggleShowLocs;
		if(k == KeyEvent.VK_2)
			frame.setLocation(1930, 10);
		if(k == KeyEvent.VK_1)
			frame.setLocation(10, 10);
		if(k == KeyEvent.VK_BACK_QUOTE) {
			if(!consoleOpen) {
				new Console(this);
				consoleOpen = true;
			}
		}
		if(k == KeyEvent.VK_BACK_SPACE || k == KeyEvent.VK_DELETE) {
			if(screen != STARTSCREEN)
				moveBackScreen();
		}
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	private Color ballColor = Color.BLUE;
	public Color getBallColor() {return ballColor;}
	public void setBallColor(Color c) {ballColor = c;}

	public void setAntialias(boolean a) {antialias = a;}
	public boolean getAntialias() {return antialias;}

	public void checkMusic() {
		if(music)
			musicClip.loop(Clip.LOOP_CONTINUOUSLY);
		else
			musicClip.stop();
	}

	public boolean getMusic() {return music;}
	public void setMusic(boolean b) {
		music = b;
		checkMusic();
	}

	public void updateLevelMenu() {
		levelsScreen.checkAvailableLevels();
		levelsScreen.updateLevels();
	}

	public int getLevel() {
		return levelsScreen.getAvailableLevels();
	}

	public boolean getCustom() {
		return customColor;
	}

	public static void main(String[] args) {
		new Environment(10, 10);
	}
}
