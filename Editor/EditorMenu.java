import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalButtonUI;

import resources.CustomSave;
import resources.DragablePlatform;
import resources.Platform;

public class EditorMenu extends JPanel implements ActionListener, MouseListener {
	private Environment env;
	private JButton newLevel, editPrevious, backToBB, play;
	private JFrame frame;

	private Timer moveText = new Timer(1000/60, this);

	private boolean moveNL = false;
	private boolean moveEP = false;
	private boolean moveBTBB = false;
	private boolean movePlay = false;

	public EditorMenu(int xLoc, int yLoc, Environment e) {
		frame = new JFrame("Bouncy Ball Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(xLoc, yLoc);
		frame.setResizable(false);
		ImageIcon fav = new ImageIcon(getClass().getResource("/resources/favicon.png"));
		frame.setIconImage(fav.getImage());
		setPreferredSize(new Dimension(400, 275));
		setLayout(null);
		env = e;

		Container canvas = frame.getContentPane();
		canvas.add(this);
		frame.pack();

		addButtons();

		frame.setVisible(true);
		moveText.start();
	}

	private void addButtons() {
		JLabel title = new JLabel("Bouncy Ball Custom Level Editor");
		title.setFont(getFont().deriveFont(20f));
		title.setBounds(10, 10, getWidth() - 20, title.getPreferredSize().height + 10);
		title.setForeground(Color.BLACK);
		title.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, Color.BLACK));

		Insets buttonMargin = new Insets(4, 2, 4, 0);
		Color bg = new Color(45, 148, 52);

		String newLevelText = "<html><span style='color: rgb(0, 255, 255); font-weight: bold;'>CREATE</span> New Custom Level</html>";
		String editPreviousText = "<html><span style='color: rgb(0, 255, 255); font-weight: bold;'>EDIT</span> Custom Level</html>";
		String backToBBText = "<html><span style='color: rgb(0, 255, 255); font-weight: bold;'>RETURN</span> to Bouncy Ball</html>";
		String playText = "<html><span style='color: rgb(0, 255, 255); font-weight: bold;'>PLAY</span> Custom Level</html>";

		newLevel = new JButton(newLevelText);
		newLevel.setFont(getFont().deriveFont(Font.PLAIN, 18f));
		newLevel.setMargin(buttonMargin);
		newLevel.setBounds(20, 70, newLevel.getPreferredSize().width + 40, newLevel.getPreferredSize().height);
		newLevel.setFocusable(false);
		newLevel.setHorizontalAlignment(SwingConstants.LEFT);
		newLevel.setBackground(bg);
		newLevel.setForeground(Color.BLACK);
		newLevel.addActionListener(this);
		newLevel.addMouseListener(this);
		newLevel.setUI(new MetalButtonUI());

		editPrevious = new JButton(editPreviousText);
		editPrevious.setFont(newLevel.getFont());
		editPrevious.setMargin(buttonMargin);
		editPrevious.setBounds(newLevel.getX(), 120, newLevel.getPreferredSize().width + 40, editPrevious.getPreferredSize().height);
		editPrevious.setFocusable(false);
		editPrevious.setHorizontalAlignment(SwingConstants.LEFT);
		editPrevious.setBackground(bg);
		editPrevious.setForeground(Color.BLACK);
		editPrevious.addActionListener(this);
		editPrevious.addMouseListener(this);
		editPrevious.setUI(new MetalButtonUI());

		play = new JButton(playText);
		play.setFont(newLevel.getFont());
		play.setMargin(buttonMargin);
		play.setBounds(newLevel.getX(), 170, newLevel.getPreferredSize().width  + 40, play.getPreferredSize().height);
		play.setFocusable(false);
		play.setHorizontalAlignment(SwingConstants.LEFT);
		play.setBackground(bg);
		play.setForeground(Color.BLACK);
		play.addActionListener(this);
		play.addMouseListener(this);
		play.setUI(new MetalButtonUI());

		backToBB = new JButton(backToBBText);
		backToBB.setFont(newLevel.getFont());
		backToBB.setMargin(buttonMargin);
		backToBB.setBounds(newLevel.getX(), 220, newLevel.getPreferredSize().width  + 40, backToBB.getPreferredSize().height);
		backToBB.setFocusable(false);
		backToBB.setHorizontalAlignment(SwingConstants.LEFT);
		backToBB.setBackground(bg);
		backToBB.setForeground(Color.BLACK);
		backToBB.addActionListener(this);
		backToBB.addMouseListener(this);
		backToBB.setUI(new MetalButtonUI());

		add(title);
		add(newLevel);
		add(editPrevious);
		add(backToBB);
		add(play);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == newLevel) {
			frame.dispose();
			moveText.stop();
			moveText = null;
			new LevelEditor(frame.getX(), frame.getY(), env);
		}
		else if(source == editPrevious) {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new FileNameExtensionFilter("Bouncy Ball File(.bb4)", "BB4"));
			int returnVal = jfc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				frame.dispose();
				moveText.stop();
				moveText = null;
				new LevelEditor(frame.getX(), frame.getY(), env, LevelEditor.EDITLEVEL, jfc.getSelectedFile());
			}
		}
		else if(source == play) {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new FileNameExtensionFilter("Bouncy Ball File(.bb4)", "BB4"));
			int returnVal = jfc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					FileInputStream fis = new FileInputStream(jfc.getSelectedFile());
					ObjectInputStream ois = new ObjectInputStream(fis);
					CustomSave cs = (CustomSave)ois.readObject();
					ArrayList<Platform> temp = new ArrayList<Platform>();
					for(DragablePlatform dp : cs.getPlatforms()) {
						if(dp.getType() == Platform.NORMAL || dp.getType() == Platform.DEAD)
							temp.add(new Platform(dp.getX(), dp.getY(), dp.getW(), dp.getH(), dp.getType()));
						else if(dp.getType() == Platform.BOOST)
							temp.add(new Platform(dp.getX(), dp.getY(), dp.getW(), dp.getH(), dp.getType(), dp.getBoost()));
						else if(dp.getType() == Platform.MOVING)
							temp.add(new Platform(dp.getX(), dp.getY(), dp.getW(), dp.getH(), dp.getType(), dp.getHorizVerti(), dp.getMax()));
					}
					ois.close();
					new LevelTester(temp, cs.getStars(), cs.getBall(), cs.getLevelSize(), frame.getLocationOnScreen());

				} catch(Exception ex) {
					//ex.printStackTrace();
					System.out.println("Error Loading File");
					JOptionPane.showMessageDialog(frame, "Could Not Load Level", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if(source == backToBB) {
			frame.dispose();
			moveText.stop();
			moveText = null;
			env.frame.setVisible(true);
			env.checkSettings();
			env.checkMusic();
			Runtime.getRuntime().gc();
		}

		if(source == moveText) {
			int max = 10;
			int speed = 2;
			if(moveNL) {
				if(newLevel.getMargin().left < max)
					newLevel.setMargin(new Insets(newLevel.getMargin().top, newLevel.getMargin().left + speed, newLevel.getMargin().bottom,
							newLevel.getMargin().right));
			}
			else {
				if(newLevel.getMargin().left > 5)
					newLevel.setMargin(new Insets(newLevel.getMargin().top, newLevel.getMargin().left - speed, newLevel.getMargin().bottom,
							newLevel.getMargin().right));
			}

			if(moveEP) {
				if(editPrevious.getMargin().left < max)
					editPrevious.setMargin(new Insets(editPrevious.getMargin().top, editPrevious.getMargin().left + speed,
							editPrevious.getMargin().bottom, editPrevious.getMargin().right));
			}
			else {
				if(editPrevious.getMargin().left > 5)
					editPrevious.setMargin(new Insets(editPrevious.getMargin().top, editPrevious.getMargin().left - speed,
							editPrevious.getMargin().bottom, editPrevious.getMargin().right));
			}

			if(moveBTBB) {
				if(backToBB.getMargin().left < max)
					backToBB.setMargin(new Insets(backToBB.getMargin().top, backToBB.getMargin().left + speed,
							backToBB.getMargin().bottom, backToBB.getMargin().right));
			}
			else {
				if(backToBB.getMargin().left > 5)
					backToBB.setMargin(new Insets(backToBB.getMargin().top, backToBB.getMargin().left - speed,
							backToBB.getMargin().bottom, backToBB.getMargin().right));
			}

			if(movePlay) {
				if(play.getMargin().left < max)
					play.setMargin(new Insets(play.getMargin().top, play.getMargin().left + speed,
							play.getMargin().bottom, play.getMargin().right));
			}
			else {
				if(play.getMargin().left > 5)
					play.setMargin(new Insets(play.getMargin().top, play.getMargin().left - speed,
							play.getMargin().bottom, play.getMargin().right));
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		Object source = e.getSource();
		if(source == newLevel) {
			newLevel.setBackground(new Color(76, 175, 82));
			moveNL = true;
		}
		else if(source == editPrevious) {
			editPrevious.setBackground(new Color(76, 175, 82));
			moveEP = true;
		}
		else if(source == backToBB) {
			backToBB.setBackground(new Color(76, 175, 82));
			moveBTBB = true;
		}
		else if(source == play) {
			play.setBackground(new Color(76, 175, 82));
			movePlay = true;
		}
	}

	public void mouseExited(MouseEvent e) {
		Object source = e.getSource();
		if(source == newLevel) {
			newLevel.setBackground(new Color(45, 148, 52));
			moveNL = false;
		}
		else if(source == editPrevious) {
			editPrevious.setBackground(new Color(45, 148, 52));
			moveEP = false;
		}
		else if(source == backToBB) {
			backToBB.setBackground(new Color(45, 148, 52));
			moveBTBB = false;
		}
		else if(source == play) {
			play.setBackground(new Color(45, 148, 52));
			movePlay = false;
		}
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
}
