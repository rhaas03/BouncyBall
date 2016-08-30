import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import resources.LevelBox;

public class LevelMenu implements MouseMotionListener, MouseListener {
	private Environment env;

	private ArrayList<LevelBox> boxes = new ArrayList<LevelBox>();
	private boolean firstCreateLevels = true;

	private int ml;
	private int availableLevels = 0;

	public LevelMenu(Environment e) {
		env = e;
		ml = env.getMouseLine();

		checkAvailableLevels();
	}

	public void drawLevels(Graphics2D g2d) {
		if(firstCreateLevels)
			createLevels();

		g2d.setColor(Color.RED);

		for(LevelBox element : boxes)
			element.drawLevelBox(g2d);

		g2d.setColor(Color.BLACK);
	}

	private void createLevels() {
		//FINAL y WILL EQUAL 240

		int x = 10;
		int y = ml + 10;
		int d = 60;
		int levelCounter = 1;
		boxes.add(new LevelBox(x, y, d, levelCounter, LevelBox.ROUNDED, Color.BLUE, Color.WHITE, 20, env));
		x += boxes.get(0).getObject().getBounds2D().getWidth() + 10;

		//Row One
		while(x + d < env.frame.getWidth()) {
			levelCounter++;
			boxes.add(new LevelBox(x, y, d, levelCounter, LevelBox.ROUNDED, Color.BLUE, Color.WHITE, 20, env));
			x += boxes.get(0).getObject().getBounds2D().getWidth() + 10;
		}

		x = 10;
		y += boxes.get(0).getObject().getBounds2D().getHeight() + 10;

		//Row Two
		while(x + d < env.frame.getWidth()) {
			levelCounter++;
			boxes.add(new LevelBox(x, y, d, levelCounter, LevelBox.ROUNDED, Color.BLUE, Color.WHITE, 20, env));
			x += boxes.get(0).getObject().getBounds2D().getWidth() + 10;
		}

		x = 10;
		y += boxes.get(0).getObject().getBounds2D().getHeight() + 10;

		//Row Three
		while(x + d < env.frame.getWidth()) {
			levelCounter++;
			boxes.add(new LevelBox(x, y, d, levelCounter, LevelBox.ROUNDED, Color.BLUE, Color.WHITE, 20, env));
			x += boxes.get(0).getObject().getBounds2D().getWidth() + 10;
		}

		//Better Fit Frame
		y += boxes.get(0).getObject().getBounds2D().getHeight() + 10;

		for(int i = availableLevels + 1; i < boxes.size(); i++) {
			boxes.get(i).setEnabled(false);
		}

		firstCreateLevels = false;
	}

	public void mouseMoved(MouseEvent e) {
		if(env.getScreen() == Environment.LEVELSCREEN) {
			boolean handCursor = false;
			for(LevelBox element : boxes) {
				if(element.mouseRectCollision(e.getX(), e.getY())) {
					element.changeBackground(Color.BLACK);
					if(element.isEnabled()) handCursor = true;
					break;
				}
				else element.changeBackground(Color.BLUE);
			}

			if(handCursor) env.setCursor(new Cursor(Cursor.HAND_CURSOR));
			else env.setCursor(Cursor.getDefaultCursor());
		}
	}

	public void updateLevels() {
		boxes.clear();
		firstCreateLevels = true;
	}

	public void mousePressed(MouseEvent e) {
		if(env.getScreen() == Environment.LEVELSCREEN) {
			for(LevelBox element : boxes) {
				if(element.mouseRectCollision(e.getX(), e.getY())) {
					if(element.isEnabled()) {
						env.addHistory(env.getScreen(), env.getPreferredSize());
						if(element.getLevel() == 1) {
							env.setScreen(Environment.LEVELONE);
							env.changeSize(500, 300);
						}
						else if(element.getLevel() == 2) {
							env.setScreen(Environment.LEVELTWO);
							env.changeSize(500, 300);
						}
						else if(element.getLevel() == 3) {
							env.setScreen(Environment.LEVELTHREE);
							env.changeSize(500, 300);
						}
						else if(element.getLevel() == 4) {
							env.setScreen(Environment.LEVELFOUR);
							env.changeSize(500, 300);
						}
						else if(element.getLevel() == 5) {
							env.setScreen(Environment.LEVELFIVE);
							env.changeSize(500, 300);
						}
						else if(element.getLevel() == 6) {
							env.setScreen(Environment.LEVELSIX);
							env.changeSize(500, 300);
						}
						else if(element.getLevel() == 7) {
							env.setScreen(Environment.LEVELSEVEN);
							env.changeSize(300, 500);
						}
						else if(element.getLevel() == 8) {
							env.setScreen(Environment.LEVELEIGHT);
							env.changeSize(500, 300);
						}
						else if(element.getLevel() == 9) {
							env.setScreen(Environment.LEVELNINE);
							env.changeSize(500, 300);
						}

						env.setLevel(element.getLevel());
						env.updateListeners();
						element.changeBackground(Color.BLUE);
					}
				}
			}

			env.setCursor(Cursor.getDefaultCursor());
		}

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

	public void checkAvailableLevels() {
		try {
			FileInputStream file = new FileInputStream(Environment.LEVELSFILE);
			ObjectInputStream ois = new ObjectInputStream(file);
			availableLevels = (Integer)ois.readObject();
			updateLevels();
			ois.close();
		}
		catch(FileNotFoundException e) {
			try {
				Environment.LEVELSFILE.getParentFile().mkdirs();
				Environment.LEVELSFILE.createNewFile();
			}
			catch(IOException e1) {
				System.out.println("IOException");
			}
			checkAvailableLevels();
		}
		catch(IOException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e) {
			env.saveLevel(0);
		}
	}

	public int getAvailableLevels() {
		return availableLevels;
	}

	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
}
