import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.*;

import resources.LimitTextField;
import resources.SettingsSave;

public class Settings extends JPanel implements ActionListener {
	private Environment env;
	private boolean showCustomColor = false;
	private boolean wasCustom = false;

	private String[] enabledDisabled = {"Enabled", "Disabled"};
	private String[] colorOptions = {"Blue", "Black", "Yellow", "Cyan", "Red", "Green", "Gray", "Magenta", "Random", "Custom"};
	private Color[] listOfColors = {Color.BLUE, Color.BLACK, Color.YELLOW, Color.CYAN, Color.RED, Color.GREEN,
			Color.GRAY, Color.MAGENTA};

	private JComboBox<String> antiSelector = new JComboBox<String>(enabledDisabled);
	private JComboBox<String> colorSelector = new JComboBox<String>(colorOptions);
	private JComboBox<String> musicSelector = new JComboBox<String>(enabledDisabled);

	private JTextField customR = new JTextField(0), customG = new JTextField(0), customB = new JTextField(0);

	private JButton cancel, apply;

	public Settings(Environment e) {
		env = e;
		setLocation(0, env.getMouseLine());
		setSize(new Dimension(env.getWidth(), env.getHeight() - env.getMouseLine()));
		setOpaque(true);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		int inset = 10;
		setBorder(new EmptyBorder(inset, inset, inset, inset));

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				env.frame.requestFocus();
			}
		});

		checkSettings();
		makeLayout();
		addButtons();
	}

	private void addButtons() {
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.setBorder(new EmptyBorder(5, 5, 5, 10));
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setFocusable(false);
		buttonPane.add(cancel);
		apply = new JButton("Apply");
		apply.addActionListener(this);
		apply.setFocusable(false);
		buttonPane.add(apply);

		add(buttonPane);
	}

	private void makeLayout() {
		GridLayout grid = new GridLayout(3, 2);
		int bottomInset = 0;
		int vgap = 10;
		if(showCustomColor)
			grid = new GridLayout(4, 2);
		grid.setVgap(vgap);
		JPanel settingsPane = new JPanel(grid);
		TitledBorder blankTitled = BorderFactory.createTitledBorder(
				new EmptyBorder(10, 10, bottomInset, 10), "Settings");
		blankTitled.setTitleFont(blankTitled.getTitleFont().deriveFont(24f));
		settingsPane.setBorder(blankTitled);


		Font labelFont = new Font("", Font.PLAIN, 14);

		settingsPane.add(new JLabel("Antialiasing: ")).setFont(labelFont);
		settingsPane.add(antiSelector);
		antiSelector.setFocusable(false);

		settingsPane.add(new JLabel("Ball Color: ")).setFont(labelFont);
		settingsPane.add(colorSelector);
		colorSelector.setFocusable(false);
		colorSelector.addActionListener(this);

		if(showCustomColor) {
			settingsPane.add(new JLabel("Custom Color: ")).setFont(labelFont);
			GridLayout customGrid = new GridLayout(3, 2);
			customGrid.setVgap(2);
			JPanel customPane = new JPanel(customGrid);

			customPane.add(new JLabel("R (0-255): ")).setFont(labelFont);
			customPane.add(customR);
			customR.setDocument(new LimitTextField(3));
			customPane.add(new JLabel("G (0-255): ")).setFont(labelFont);
			customPane.add(customG);
			customG.setDocument(new LimitTextField(3));
			customPane.add(new JLabel("B (0-255): ")).setFont(labelFont);
			customPane.add(customB);
			customB.setDocument(new LimitTextField(3));

			if(wasCustom) {
				customR.setText(Integer.toString(env.getBallColor().getRed()));
				customG.setText(Integer.toString(env.getBallColor().getGreen()));
				customB.setText(Integer.toString(env.getBallColor().getBlue()));
			}

			settingsPane.add(customPane);
			revalidate();
		}

		settingsPane.add(new JLabel("Music: ")).setFont(labelFont);
		settingsPane.add(musicSelector);
		musicSelector.setFocusable(false);
		add(settingsPane);
	}

	private void checkSettings() {
		Color bC = env.getBallColor();
		if(!env.getCustom()) {
			for(int x = 0; x < listOfColors.length; x++) {
				if((bC.getRed() == listOfColors[x].getRed()) && (bC.getGreen() == listOfColors[x].getGreen()) &&
						(bC.getBlue() == listOfColors[x].getBlue())) {
					colorSelector.setSelectedIndex(x);
					break;
				}
				else
					colorSelector.setSelectedIndex(colorOptions.length - 2);
			}
		}
		else {
			showCustomColor = true;
			wasCustom = true;
			colorSelector.setSelectedIndex(colorOptions.length - 1);
		}

		if(env.getAntialias()) antiSelector.setSelectedIndex(0);
		else antiSelector.setSelectedIndex(1);

		if(env.getMusic()) musicSelector.setSelectedIndex(0);
		else musicSelector.setSelectedIndex(1);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == colorSelector) {
			if(colorSelector.getSelectedItem().equals(colorOptions[colorOptions.length - 1])) {
				showCustomColor = true;
				removeAll();
				makeLayout();
				addButtons();
				colorSelector.setSelectedIndex(colorOptions.length-1);
			}
			else {
				showCustomColor = false;
				int index = colorSelector.getSelectedIndex();
				removeAll();
				makeLayout();
				addButtons();
				colorSelector.setSelectedIndex(index);
				revalidate();
			}
		}
		else if(source == cancel)
			env.moveBackScreen();
		else if(source == apply) {
			if(antiSelector.getSelectedIndex() == 0)
				env.setAntialias(true);
			else
				env.setAntialias(false);

			if(colorSelector.getSelectedIndex() == colorOptions.length - 2) {
				Random random = new Random();
				int r = random.nextInt(256);
				int g = random.nextInt(256);
				int b = random.nextInt(256);
				env.setBallColor(new Color(r, g, b));
			}
			else if(colorSelector.getSelectedIndex() == colorOptions.length - 1) {
				try {
					int r, g, b;
					if(customR.getText().equals("")) r = 0;
					else r = Integer.parseInt(customR.getText());

					if(customG.getText().equals("")) g = 0;
					else g = Integer.parseInt(customG.getText());

					if(customB.getText().equals("")) b = 0;
					else b = Integer.parseInt(customB.getText());

					if(r > 255) r = 255; if(r < 0) r = 0;
					if(g > 255) g = 255; if(g < 0) g = 0;
					if(b > 255) b = 255; if(b < 0) b = 0;

					env.setBallColor(new Color(r, g, b));
				} catch(NumberFormatException nfe) {
					nfe.printStackTrace();
				}
			}
			else
				env.setBallColor(listOfColors[colorSelector.getSelectedIndex()]);

			if(musicSelector.getSelectedIndex() == 0)
				env.setMusic(true);
			else
				env.setMusic(false);

			if(!showCustomColor)
				env.saveSettings(new SettingsSave(env.getAntialias(), env.getBallColor(), env.getMusic(), false));
			else
				env.saveSettings(new SettingsSave(env.getAntialias(), env.getBallColor(), env.getMusic(), true));
			env.moveBackScreen();
		}
	}
}