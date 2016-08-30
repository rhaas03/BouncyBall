import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import resources.StaticBall;

public class NewBallDialog extends JDialog implements ActionListener {
	private LevelEditor le;

	private final String[] options = {
		"Basic Settings",
		"Advanced Settings"
	};

	private BasicPane basicPane = new BasicPane();
	private AdvancedPane advancedPane = new AdvancedPane();
	private JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	private JPanel containerPane = new JPanel(new BorderLayout());

	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

	private JButton cancel = new JButton("Cancel");
	private JButton apply = new JButton("Apply");

	private StaticBall ball;

	public static final int PROPERTIES = 1;

	public NewBallDialog(Frame owner, String title, LevelEditor l) {
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		le = l;
		basicOps();
		pack();
		setVisible(true);
	}

	public NewBallDialog(Frame owner, String title, LevelEditor l, int type, StaticBall b) {
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		le = l;
		this.ball = b;
		basicOps();

		if(type == PROPERTIES) {
			basicPane.xField.setText(Integer.toString(ball.getX()));
			basicPane.yField.setText(Integer.toString(ball.getY()));
			basicPane.diameterField.setText(Integer.toString(ball.getD()));
			advancedPane.rlSpeedField.setText(Double.toString(ball.getRLSpeed()));
			advancedPane.defaultUpSpeedField.setText(Double.toString(ball.getDefaultUpSpeed()));
		}

		pack();
		setVisible(true);
	}

	private void basicOps() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(270, 100));
		setResizable(false);

		try {
			//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e) {
			e.printStackTrace();
		}

		tabbedPane.add(options[0], basicPane);
		tabbedPane.add(options[1], advancedPane);
		tabbedPane.setFocusable(false);
		containerPane.add(tabbedPane, BorderLayout.CENTER);

		add(containerPane);
		createButtons();
		add(buttonPane);

		setLocation(le.frame.getX(), le.frame.getY());

		basicPane.xField.addActionListener(this);
		basicPane.yField.addActionListener(this);
		basicPane.diameterField.addActionListener(this);
		advancedPane.rlSpeedField.addActionListener(this);
		advancedPane.defaultUpSpeedField.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == cancel)
			dispose();
		else if(source == apply || source == basicPane.xField || source == basicPane.yField || source == basicPane.diameterField ||
			source == advancedPane.rlSpeedField || source == advancedPane.defaultUpSpeedField) {
			boolean showedOnce = true;
			if(basicPane.xField != null && basicPane.yField != null && basicPane.diameterField != null) {
				try {
					int x = Integer.parseInt(basicPane.xField.getText());
					int y = Integer.parseInt(basicPane.yField.getText());
					int d = Integer.parseInt(basicPane.diameterField.getText());

					double rlSpeed, defaultUpSpeed;

					if(!advancedPane.rlSpeedField.getText().equals(""))
						rlSpeed = Double.parseDouble(advancedPane.rlSpeedField.getText());
					else
						rlSpeed = StaticBall.STANDARD_RLSPEED;

					if(!advancedPane.defaultUpSpeedField.getText().equals(""))
						defaultUpSpeed = Double.parseDouble(advancedPane.defaultUpSpeedField.getText());
					else
						defaultUpSpeed = StaticBall.STANDARD_DEFAULT_UPSPEED;

					le.ball = new StaticBall(x, y, d, rlSpeed, defaultUpSpeed, le.env.getBallColor(), le);
					le.repaint();

					dispose();
				} catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error Creating Ball"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
				}
			}
			else{
				if(showedOnce)
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character ", "Error Creating Ball"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
			}
		}
	}

	private void createButtons() {
		cancel.addActionListener(this);
		cancel.setFocusable(false);
		apply.setFocusable(false);
		apply.addActionListener(this);
		buttonPane.add(apply);
		buttonPane.add(cancel);
	}

	private class BasicPane extends JPanel {
		private JTextField xField, yField, diameterField;

		public BasicPane() {
			int inset = 10;
			setBorder(new EmptyBorder(inset + inset, inset, inset + inset, inset));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			GridLayout grid = new GridLayout(3, 2);
			grid.setVgap(15);
			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
			TitledBorder titled = BorderFactory.createTitledBorder(
					BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), options[0]);
			titled.setTitleFont(titled.getTitleFont().deriveFont(16f));
			titled.setTitleJustification(TitledBorder.LEFT);
			container.setBorder(titled);

			int inset2 = 10;
			JPanel containerTwo = new JPanel(grid);
			containerTwo.setBorder(new EmptyBorder(inset2, inset2, inset2, inset2));

			xField = new JTextField(10);
			xField.addKeyListener(escape());

			containerTwo.add(new JLabel("X Coord: "));
			containerTwo.add(xField);

			yField = new JTextField(10);
			yField.addKeyListener(escape());

			containerTwo.add(new JLabel("Y Coord: "));
			containerTwo.add(yField);

			diameterField = new JTextField(10);
			diameterField.addKeyListener(escape());

			containerTwo.add(new JLabel("Diameter: "));
			containerTwo.add(diameterField);

			container.add(containerTwo);
			add(container);
		}
	}

	private class AdvancedPane extends JPanel {
		private JTextField rlSpeedField, defaultUpSpeedField;

		public AdvancedPane() {
			int inset = 10;
			setBorder(new EmptyBorder(inset + inset, inset, inset + inset + 20, inset));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			GridLayout grid = new GridLayout(2, 2);
			grid.setVgap(10);
			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
			TitledBorder titled = BorderFactory.createTitledBorder(
					BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), options[1]);
			titled.setTitleFont(titled.getTitleFont().deriveFont(16f));
			titled.setTitleJustification(TitledBorder.LEFT);
			container.setBorder(titled);

			int inset2 = 10;
			JPanel containerTwo = new JPanel(grid);
			containerTwo.setBorder(new EmptyBorder(inset2, inset2, inset2, inset2));

			rlSpeedField = new JTextField(10);
			rlSpeedField.addKeyListener(escape());

			containerTwo.add(new JLabel("Right/Left Speed: "));
			containerTwo.add(rlSpeedField);

			defaultUpSpeedField = new JTextField(10);
			defaultUpSpeedField.addKeyListener(escape());

			containerTwo.add(new JLabel("Up Speed: "));
			containerTwo.add(defaultUpSpeedField);

			container.add(containerTwo);
			JLabel l1 = new JLabel("Note: These fields should be decimal values less than or around 5.0");
			JLabel l2 = new JLabel("Up Speed shold be greater than 2.0 or problems may arise.");
			l1.setAlignmentX(CENTER_ALIGNMENT);
			l2.setAlignmentX(CENTER_ALIGNMENT);
			l1.setFont(getFont().deriveFont(12f));
			l2.setFont(getFont().deriveFont(12f));
			container.add(l1);
			container.add(l2);

			add(container);
		}
	}

	public KeyListener escape() {
		return new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
					dispose();
			}
		};
	}
}
