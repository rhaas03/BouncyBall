import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.*;

import resources.*;

public class LevelEditor extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
	public Environment env;
	public JFrame frame;
	public Container canvas;

	public static final int EDITLEVEL = 2;

	private String nameOfDoc = "Untitled";
	private File docLoc;

	public ArrayList<DragablePlatform> platforms = new ArrayList<DragablePlatform>();
	public ArrayList<StarPolygon> stars = new ArrayList<StarPolygon>();
	private Dimension levelSize;
	public StaticBall ball;

	private boolean firstSave;
	public boolean showGrid = true;

	private Timer refresh = new Timer(1000/60, this);

	private int platformToDelete = -1;
	private int starToDelete = -1;

	private int preX, preY, pIndex, sIndex;
	private boolean collidedWithPlat = false;
	private boolean collidedWithStar = false;
	private boolean collidedWithBall = false;

	public LevelEditor(int xLoc, int yLoc, Environment env) {
		this.env = env;
		basicOps(xLoc, yLoc);
		frame.pack();
		frame.setVisible(true);
		frame.requestFocus();
		firstSave = true;
		refresh.start();
	}
	public LevelEditor(int xLoc, int yLoc, Environment env, int type, File objFile) {
		this.env = env;
		basicOps(xLoc, yLoc);
		frame.pack();
		frame.setVisible(true);
		frame.requestFocus();

		if(type == EDITLEVEL) {
			try {
				FileInputStream fis = new FileInputStream(objFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				CustomSave cs = (CustomSave)ois.readObject();
				for(DragablePlatform dp : cs.getPlatforms())
					platforms.add(dp);
				stars = cs.getStars();
				ball = cs.getBall();
				levelSize = cs.getLevelSize();

				canvas.remove(this);
				setPreferredSize(levelSize);
				canvas = frame.getContentPane();
				canvas.add(this);
				frame.pack();

				docLoc = objFile;
				nameOfDoc = docLoc.getAbsolutePath();
				frame.setTitle("Bouncy Ball Custom Level Editor - " + nameOfDoc);

				ois.close();
			} catch(Exception e) {
				//e.printStackTrace();
				System.out.println("Error Loading File");
				JOptionPane.showMessageDialog(frame, "Could Not Load Level", "Error", JOptionPane.ERROR_MESSAGE);
				firstSave = true;
			}
		}

		refresh.start();
	}

	private void basicOps(int xLoc, int yLoc) {
		frame = new JFrame("Bouncy Ball Custom Level Editor - " + nameOfDoc);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(xLoc, yLoc);

		ImageIcon fav = new ImageIcon(getClass().getResource("/resources/favicon.png"));
		frame.setIconImage(fav.getImage());

		setPreferredSize(new Dimension(602, 402));
		frame.setMinimumSize(new Dimension(300, 200));
		canvas = frame.getContentPane();
		canvas.add(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		addMenu();
	}

	//File Menu
	private JMenuItem newFile = new JMenuItem("New");
	private JMenuItem open = new JMenuItem("Open File");
	private JMenuItem save = new JMenuItem("Save");
	private JMenuItem saveAs = new JMenuItem("Save As...");
	private JMenuItem returnToMenu = new JMenuItem("Return To Menu");
	private JMenuItem exit = new JMenuItem("Exit");

	//Edit Menu
	private JMenuItem levelDimensions = new JMenuItem("Level Dimensions");
	private JMenuItem renameLevel = new JMenuItem("Rename");
	private JMenuItem clear = new JMenuItem("Clear All");

	//Object Menu
	private JMenuItem newPlatform = new JMenuItem("New Platform");
	private JMenuItem addStar = new JMenuItem("Star");
	private JMenuItem setBallLoc = new JMenuItem("Ball");

	//Run Menu
	private JMenuItem test = new JMenuItem("Test");

	//Mouse Menu
	private JMenuItem mouseNewPlatform = new JMenuItem("New Platform");
	private JMenuItem mouseAddStar = new JMenuItem("Add Star");
	private JMenuItem platformProperties = new JMenuItem("Properties");
	private JMenuItem deletePlatform = new JMenuItem("Delete Platform");
	private JMenuItem starProperties = new JMenuItem("Properties");
	private JMenuItem deleteStar = new JMenuItem("Delete Star");
	private JMenuItem mouseSetBallLoc = new JMenuItem("Set Ball Location");
	private JMenuItem ballProperties = new JMenuItem("Properties");
	private JMenuItem deleteBall = new JMenuItem("Delete Ball");

	//Window Menu
	//private JMenuItem preferences = new JMenuItem("Preferences");
	private JCheckBoxMenuItem showGridItem = new JCheckBoxMenuItem("Show Grid", showGrid);
	private JCheckBoxMenuItem lockSize = new JCheckBoxMenuItem("Lock Level Dimensions");

	private JPopupMenu mouseMenu = new JPopupMenu();
	private JPopupMenu platformMouseMenu = new JPopupMenu();
	private JPopupMenu starMouseMenu = new JPopupMenu();
	private JPopupMenu ballMouseMenu = new JPopupMenu();

	private Object [] allObjects = {frame, platforms, stars, ball, levelSize, newFile, open, save, saveAs, returnToMenu,
			exit, levelDimensions, renameLevel, newPlatform, addStar, test, mouseNewPlatform, mouseAddStar, platformProperties, deletePlatform,
			starProperties, deleteStar, showGridItem, mouseMenu, platformMouseMenu, starMouseMenu, mouseSetBallLoc, setBallLoc, ballProperties,
			deleteBall};

	private void addMenu() {
		JMenuBar mb = new JMenuBar();
		mb.setBackground(Color.WHITE);
		frame.setJMenuBar(mb);

		String spacing = "";
		JMenu blankMenu = new JMenu(spacing); blankMenu.setFocusable(false); blankMenu.setEnabled(false);
		JMenu blankMenu2 = new JMenu(spacing); blankMenu2.setFocusable(false); blankMenu2.setEnabled(false);
		JMenu blankMenu3 = new JMenu(spacing); blankMenu3.setFocusable(false); blankMenu3.setEnabled(false);
		JMenu blankMenu4 = new JMenu(spacing); blankMenu4.setFocusable(false); blankMenu4.setEnabled(false);

		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu objectMenu = new JMenu("Object");
		JMenu runMenu = new JMenu("Run");
		JMenu windowMenu = new JMenu("Window");
		mb.add(fileMenu);
		mb.add(blankMenu);
		mb.add(editMenu);
		mb.add(blankMenu2);
		mb.add(objectMenu);
		mb.add(blankMenu3);
		mb.add(runMenu);
		mb.add(blankMenu4);
		mb.add(windowMenu);

		newFile.setPreferredSize(new Dimension(300, newFile.getPreferredSize().height));
		newFile.addActionListener(this);
		open.addActionListener(this);
		save.addActionListener(this);
		saveAs.addActionListener(this);
		exit.addActionListener(this);
		returnToMenu.addActionListener(this);
		test.addActionListener(this);
		newPlatform.addActionListener(this);
		deletePlatform.addActionListener(this);
		addStar.addActionListener(this);
		addStar.setPreferredSize(new Dimension(250, addStar.getPreferredSize().height));
		mouseAddStar.addActionListener(this);
		deleteStar.addActionListener(this);
		test.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		UIManager.getLookAndFeelDefaults().put("menuItem.acceleratorForeground", Color.GREEN);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newPlatform.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		addStar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
		setBallLoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		clear.addActionListener(this);
		setBallLoc.addActionListener(this);
		mouseSetBallLoc.addActionListener(this);
		deleteBall.addActionListener(this);
		ballProperties.addActionListener(this);
		platformProperties.addActionListener(this);
		starProperties.addActionListener(this);
		levelDimensions.addActionListener(this);
		renameLevel.addActionListener(this);

		fileMenu.add(newFile);
		fileMenu.add(open);
		fileMenu.addSeparator();
		fileMenu.add(save);
		fileMenu.add(saveAs);
		fileMenu.addSeparator();
		fileMenu.add(returnToMenu);
		fileMenu.add(exit);

		editMenu.add(levelDimensions);
		editMenu.add(renameLevel);
		editMenu.addSeparator();
		editMenu.add(clear);

		objectMenu.add(newPlatform);
		objectMenu.add(addStar);
		objectMenu.add(setBallLoc);

		runMenu.add(test);

		windowMenu.add(showGridItem);
		showGridItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showGrid = !showGrid;
				repaint();
			}
		});
		showGridItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));

		windowMenu.add(lockSize);
		lockSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(lockSize.isSelected())
					frame.setResizable(false);
				else
					frame.setResizable(true);
			}
		});
		lockSize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));

		for(int x = 0; x < mb.getMenuCount(); x++) {
			JMenu temp = mb.getMenu(x);
			temp.setFont(getFont().deriveFont(Font.PLAIN));
			for(int i = 0; i < temp.getItemCount(); i++) {
				if(temp.getItem(i) instanceof JMenuItem)
					temp.getItem(i).setFont(getFont().deriveFont(Font.PLAIN));
			}
		}

		mouseMenu.add(mouseNewPlatform);
		mouseMenu.add(mouseAddStar);
		mouseMenu.add(mouseSetBallLoc);
		mouseNewPlatform.addActionListener(this);
		mouseMenu.setPreferredSize(new Dimension(200, mouseMenu.getPreferredSize().height));

		platformMouseMenu.add(platformProperties);
		platformMouseMenu.add(deletePlatform);
		platformMouseMenu.setPreferredSize(new Dimension(200, platformMouseMenu.getPreferredSize().height));

		starMouseMenu.add(starProperties);
		starMouseMenu.add(deleteStar);
		starMouseMenu.setPreferredSize(new Dimension(200, starMouseMenu.getPreferredSize().height));

		ballMouseMenu.add(ballProperties);
		ballMouseMenu.add(deleteBall);
		ballMouseMenu.setPreferredSize(new Dimension(200, ballMouseMenu.getPreferredSize().height));

		for(int x = 0; x < mouseMenu.getComponentCount(); x++)
			mouseMenu.getComponent(x).setFont(getFont().deriveFont(Font.PLAIN));
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == newFile) {
			frame.dispose();
			new LevelEditor(frame.getX(), frame.getY(), env);
		}
		else if(source == open) {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("Object Files(.ser)", "ser"));
			int returnVal = fc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.println(fc.getSelectedFile().getName());
			}
		}
		else if(source == save) {
			if(ball != null && stars.size() > 0)
				saveDialog();
			else {
				JOptionPane.showMessageDialog(frame, "Requirements not met.\nMust set Ball Location and have 1 star", "Requirements Not Met", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(source == saveAs) {
			if(ball != null && stars.size() > 0) {
				firstSave = true;
				saveDialog();
			}
			else
				JOptionPane.showMessageDialog(frame, "Requirements not met.\nMust set Ball Location and have 1 star", "Requirements Not Met",
						JOptionPane.ERROR_MESSAGE);
		}
		else if(source == exit) System.exit(0);
		else if(source == returnToMenu) {
			frame.dispose();
			refresh.stop();
			refresh = null;
			destroyAllObjects();
			Runtime.getRuntime().gc();
			new EditorMenu(frame.getX(), frame.getY(), env);
		}
		else if(source == mouseNewPlatform || source == newPlatform)
			new NewPlatformDialog(frame, "New Platform", this);
		else if(source == refresh) repaint();
		else if(source == deletePlatform) platforms.remove(platformToDelete);
		else if(source == addStar || source == mouseAddStar)
			new NewStarDialog(frame, "New Star", this);
		else if(source == setBallLoc || source == mouseSetBallLoc)
			new NewBallDialog(frame, "New Ball", this);
		else if(source == deleteStar) stars.remove(starToDelete);
		else if(source == clear) {
			ball = null;
			stars.clear();
			platforms.clear();
		}
		else if(source == ballProperties)
			new NewBallDialog(frame, "Ball Properties", this, NewBallDialog.PROPERTIES, ball);
		else if(source == deleteBall) ball = null;
		else if(source == platformProperties)
			new NewPlatformDialog(frame, "Platform Properties", this, NewPlatformDialog.PROPERTIES,
					platforms.get(pIndex), pIndex);
		else if(source == starProperties)
			new NewStarDialog(frame, "Star Properties", this, NewStarDialog.PROPERTIES, stars.get(sIndex), sIndex);
		else if(source == test) {
			if(ball != null && stars.size() > 0) {
				Point loc = frame.getLocationOnScreen();
				ArrayList<Platform> tempPlats = new ArrayList<Platform>();
				for(int i = 0; i < platforms.size(); i++) {
					int pType = platforms.get(i).getType();
					if(pType == Platform.NORMAL || pType == Platform.DEAD) {
						DragablePlatform dp = platforms.get(i);
						tempPlats.add(new Platform(dp.getX(), dp.getY(), dp.getW(), dp.getH(), dp.getType()));
					}
					else if(pType == Platform.BOOST) {
						DragablePlatform dp = platforms.get(i);
						tempPlats.add(new Platform(dp.getX(), dp.getY(), dp.getW(), dp.getH(), dp.getType(), dp.getBoost()));
					}
					else if(pType == Platform.MOVING) {
						DragablePlatform dp = platforms.get(i);
						tempPlats.add(new Platform(dp.getX(), dp.getY(), dp.getW(), dp.getH(), dp.getType(), dp.getHorizVerti(), dp.getMax()));
					}
				}

				levelSize = getSize();
				new LevelTester(tempPlats, stars, ball, levelSize, loc);
			}
			else {
				JOptionPane.showMessageDialog(frame, "Requirements not met.\nMust set Ball Location and have 1 star", "Requirements Not Met",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(source == levelDimensions) {
			JTextField wField = new JTextField(5);
			JTextField hField = new JTextField(5);
			JPanel tempPane = new JPanel();
			tempPane.add(new JLabel("Width (300 minimum): "));
			tempPane.add(wField);
			tempPane.add(Box.createHorizontalStrut(15));
			tempPane.add(new JLabel("Height (200 minimum): "));
			tempPane.add(hField);

			int result = JOptionPane.showConfirmDialog(null, tempPane, "Please Enter Level Dimensions", JOptionPane.OK_CANCEL_OPTION);
			boolean showOnce = true;
			if(result == JOptionPane.OK_OPTION) {
				try {
					if(!wField.getText().equals("") && !hField.getText().equals("")) {
						int w = Integer.parseInt(wField.getText());
						int h = Integer.parseInt(hField.getText());

						System.out.println("New: " + w + "x" + h);

						Dimension temp = new Dimension(w+ 2, h + 2);

						canvas.remove(this);
						setPreferredSize(temp);
						canvas = frame.getContentPane();
						canvas.add(this);
						frame.pack();
						System.out.println("Preferred: " + getPreferredSize().width +"x" + getPreferredSize().height);
					}
					else {
						JOptionPane.showMessageDialog(frame, "Empty Field or Improper Character", "Error",
								JOptionPane.ERROR_MESSAGE);
						showOnce = false;
					}

				}catch (NumberFormatException nfe) {
					if(showOnce)
						JOptionPane.showMessageDialog(frame, "Empty Field or Improper Character", "Error",
								JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if(source == renameLevel) {
			if(!firstSave) {
				JTextField newName = new JTextField(20);
				JPanel tempPane = new JPanel();
				tempPane.add(new JLabel("Rename To: "));
				tempPane.add(newName);

				int result = JOptionPane.showConfirmDialog(null, tempPane, "Please Enter Level Dimensions", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION) {
					File file = docLoc;
					String nn = docLoc.getParent() + "\\" + newName.getText();
					if(!nn.endsWith(".ser")) nn += ".ser";
					File newFile = new File(nn);
					file.renameTo(newFile);
					docLoc = newFile;
					nameOfDoc = newFile.getAbsolutePath();
					frame.setTitle("Bouncy Ball Custom Level Editor - " + nameOfDoc);
				}
			}
			else
				JOptionPane.showMessageDialog(frame, "Level has not yet been saved", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveDialog() {
		if(firstSave) {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("Bouncy Ball File(.bb4)", "BB4"));
			fc.setSelectedFile(new File(nameOfDoc));
			int returnVal = fc.showSaveDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				String filename = fc.getSelectedFile().toString();
				//if(!filename.endsWith(".ser")) filename += ".ser";
				if(!filename.endsWith(".bb4")) filename += ".bb4";

				try {
					File f = new File(filename);
					f.createNewFile();
					FileOutputStream fos = new FileOutputStream(f);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					levelSize = new Dimension(getWidth(), getHeight());
					oos.writeObject(new CustomSave(platforms, stars, ball, levelSize));
					oos.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				firstSave = false;
				if(!fc.getSelectedFile().getAbsolutePath().endsWith(".bb4"))
					nameOfDoc = fc.getSelectedFile().getAbsolutePath() + ".bb4";
				else
					nameOfDoc = fc.getSelectedFile().getAbsolutePath();

				docLoc = fc.getSelectedFile();

				if(!nameOfDoc.endsWith(".bb4")) frame.setTitle("Bouncy Ball Custom Level Editor - " + nameOfDoc + ".bb4");
				else frame.setTitle("Bouncy Ball Custom Level Editor - " + nameOfDoc);
				fc = null;
			}
		}
		else {
			try {
				File f = new File(nameOfDoc);
				FileOutputStream fos = new FileOutputStream(f);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				levelSize = new Dimension(getWidth(), getHeight());
				oos.writeObject(new CustomSave(platforms, stars, ball, levelSize));
				oos.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if(showGrid) {
			//draw grid lines
			Stroke defaultS = g2d.getStroke();
			Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
			g2d.setStroke(dashed);
			g2d.setFont(new Font("", Font.BOLD, 12));
			for(int x = 0; x <= getWidth(); x+=50) {
				g2d.setColor(Color.GRAY);
				g2d.drawLine(x, 0, x, getHeight());
				g2d.setColor(Color.BLACK);
				FontMetrics fm = g2d.getFontMetrics();
				g2d.drawString(Integer.toString(x), x - fm.stringWidth(Integer.toString(x)), 12);
			}
			for(int y = 0; y <= getHeight(); y+=50) {
				g2d.setColor(Color.GRAY);
				g2d.drawLine(0, y, getWidth(), y);
				g2d.setColor(Color.BLACK);
				g2d.drawString(Integer.toString(y), 2, y - 1);
			}

			g2d.setStroke(defaultS);
		}

		for(DragablePlatform p : platforms)
			p.drawPlatform(g2d);
		for(StarPolygon s : stars) {
			g2d.setColor(Color.YELLOW);
			g2d.fill(s);
			g2d.setColor(Color.BLACK);
			g2d.draw(s);
		}

		if(ball != null) ball.drawBall(g2d);
	}

	public void mousePressed(MouseEvent e) {
		if(mouseMenu.isShowing())
			mouseMenu.setVisible(false);
		if(platformMouseMenu.isShowing())
			platformMouseMenu.setVisible(false);
		if(starMouseMenu.isShowing())
			starMouseMenu.setVisible(false);
		if(ballMouseMenu.isShowing())
			ballMouseMenu.setVisible(false);

		boolean hasOne = false;

		if(!collidedWithPlat && !hasOne) {
			for(int x = 0; x < platforms.size(); x++) {
				if(mouseCollideWithPlatform(e.getX(), e.getY(), platforms.get(x))) {
					collidedWithPlat = true;
					pIndex = x;
					platformToDelete = x;

					preX = platforms.get(x).getX() - e.getX();
					preY = platforms.get(x).getY() - e.getY();
					hasOne = true;
					break;
				}
			}
		}
		if(!collidedWithStar && !hasOne) {
			for(int x = 0; x < stars.size(); x++) {
				if(mouseCollidedWithStar(e.getX(), e.getY(), stars.get(x))) {
					collidedWithStar = true;
					sIndex = x;
					starToDelete = x;

					preX = e.getX();
					preY = e.getY();
					hasOne = true;
					break;
				}
			}
		}
		if(!collidedWithBall && !hasOne) {
			if(ball != null) {
				if(mouseCollidedWithBall(e.getX(), e.getY(), ball)) {
					collidedWithBall = true;
					preX = ball.getX() - e.getX();
					preY = ball.getY() - e.getY();
					hasOne = true;
				}
			}
		}
		if(e.getButton() == MouseEvent.BUTTON3) {
			if(!collidedWithPlat && !collidedWithStar && !collidedWithBall)
				mouseMenu.show(e.getComponent(), e.getX(), e.getY());
			else if(collidedWithPlat)
				platformMouseMenu.show(e.getComponent(), e.getX(), e.getY());
			else if(collidedWithStar)
				starMouseMenu.show(e.getComponent(), e.getX(), e.getY());
			else if(collidedWithBall)
				ballMouseMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	public void mouseDragged(MouseEvent e) {
		if(collidedWithPlat) {
			Platform p = platforms.get(pIndex);
			int newXLoc = preX + e.getX();
			int newYLoc = preY + e.getY();
			p.setLocation(newXLoc, newYLoc);
		}
		if(collidedWithStar) {
			StarPolygon s = stars.get(sIndex);
			s.translate(e.getX() - preX, e.getY() - preY);
			preX = e.getX();
			preY = e.getY();
		}
		if(collidedWithBall) {
			int newXLoc = preX + e.getX();
			int newYLoc = preY + e.getY();
			ball.setLocation(newXLoc, newYLoc);
		}

		repaint();
	}

	private boolean mouseCollideWithPlatform(int mouseX, int mouseY, Platform p) {
		Rectangle mouse = new Rectangle(mouseX, mouseY, 1, 1);
		Rectangle platform = (Rectangle) p.getShape();
		Area mA = new Area(mouse);
		Area pA = new Area(platform);
		pA.intersect(mA);
		return !pA.isEmpty();
	}

	private boolean mouseCollidedWithStar(int mouseX, int mouseY, StarPolygon s) {
		Rectangle mouse = new Rectangle(mouseX, mouseY, 1, 1);
		Area mA = new Area(mouse);
		Area sA = new Area(s);
		sA.intersect(mA);
		return !sA.isEmpty();
	}

	private boolean mouseCollidedWithBall(int mouseX, int mouseY, BouncyBall b) {
		Rectangle mouse = new Rectangle(mouseX, mouseY, 1, 1);
		Area mA = new Area(mouse);
		Area bA = new Area(b.getShape());
		bA.intersect(mA);
		return !bA.isEmpty();
	}

	private void destroyAllObjects() {
		for(int x = 0; x < allObjects.length; x++)
			allObjects[x] = null;
		allObjects = null;
	}

	public void mouseMoved(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
		collidedWithPlat = false;
		collidedWithStar = false;
		collidedWithBall = false;
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
}
