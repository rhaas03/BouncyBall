import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import resources.DragablePlatform;
import resources.Platform;

public class NewPlatformDialog extends JDialog implements ActionListener {
	private LevelEditor le;

	public static final int PROPERTIES = 1;
	private final String[] options = {
			"Standard Platform",
			"Boost Platform",
			"Vertical Moving Platform",
			"Horizontal Moving Platform",
			"Death Platform"
	};
	private JComboBox<String> platformSelector = new JComboBox<String>(options);

	private JButton cancel = new JButton("Cancel");
	private JButton apply = new JButton("Apply");

	private CardLayout cl = new CardLayout();
	private JPanel panelContainer = new JPanel(cl);

	private StandardPlatform standard = new StandardPlatform(this);
	private BoostPlatform boost = new BoostPlatform(this);
	private VerticalPlatform vertical = new VerticalPlatform(this);
	private HorizontalPlatform horizontal = new HorizontalPlatform(this);
	private DeathPlatform death = new DeathPlatform(this);

	private final Font mainFont = new Font("", Font.PLAIN, 14);

	private boolean propertyChange = false;
	private int changeIndex = -1;

	public NewPlatformDialog(Frame owner, String title, LevelEditor l) {
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		le = l;
		basicOps();
		pack();
		setVisible(true);
	}
	public NewPlatformDialog(Frame owner, String title, LevelEditor l, int type, Platform p, int index) {
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		le = l;
		basicOps();

		if(type == PROPERTIES) {
			if(p.getType() == Platform.NORMAL) {
				standard.setValues(p.getX(), p.getY(), p.getW(), p.getH());
				platformSelector.setSelectedIndex(0);
			}
			else if(p.getType() == Platform.BOOST) {
				boost.setValues(p.getX(), p.getY(), p.getW(), p.getH(), p.getBoost());
				platformSelector.setSelectedIndex(1);
			}
			else if(p.getType() == Platform.MOVING) {
				if(p.getHorizVerti() == Platform.VERTICAL) {
					vertical.setValues(p.getX(), p.getY(), p.getW(), p.getH(), p.getMax());
					platformSelector.setSelectedIndex(2);
				}
				else if(p.getHorizVerti() == Platform.HORIZONTAL) {
					horizontal.setValues(p.getX(), p.getY(), p.getW(), p.getH(), p.getMax());
					platformSelector.setSelectedIndex(3);
				}
			}
			else if(p.getType() == Platform.DEAD) {
				death.setValues(p.getX(), p.getY(), p.getW(), p.getH());
				platformSelector.setSelectedIndex(4);
			}
		}

		propertyChange = true;
		changeIndex = index;
		platformSelector.setEnabled(false);
		pack();
		setVisible(true);
	}

	private void basicOps() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(270, 100));
		setResizable(false);
		setLocation(le.frame.getX(), le.frame.getY());
		int borderInset = 10;
		((JComponent) getContentPane()).setBorder(new EmptyBorder(borderInset, borderInset, borderInset/2, borderInset));

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e) {
			e.printStackTrace();
		}

		JPanel selectorPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		platformSelector.setFocusable(false);
		platformSelector.setFont(mainFont);
		platformSelector.addActionListener(this);
		selectorPane.add(new JLabel("Platform Type: ")).setFont(mainFont);
		selectorPane.add(platformSelector);

		add(selectorPane);
		add(panelContainer);
		panelContainer.add(standard, options[0]);
		panelContainer.add(boost, options[1]);
		panelContainer.add(vertical, options[2]);
		panelContainer.add(horizontal, options[3]);
		panelContainer.add(death, options[4]);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(apply);
		buttonPane.add(cancel);
		cancel.addActionListener(this);
		cancel.setFocusable(false);
		apply.addActionListener(this);
		apply.setFocusable(false);

		add(buttonPane);

		cl.show(panelContainer, options[0]);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == cancel) dispose();
		else if(source == platformSelector) cl.show(panelContainer, options[platformSelector.getSelectedIndex()]);
		else if(source == apply) applyFunction();
	}

	public void applyFunction() {
		if(platformSelector.getSelectedItem().equals(options[0])) {
			Object[] fields = standard.getValues();
			JTextField xField = (JTextField) fields[0];
			JTextField yField = (JTextField) fields[1];
			JTextField wField = (JTextField) fields[2];
			JTextField hField = (JTextField) fields[3];
			boolean showedOnce = true;
			if(xField != null && yField != null && wField != null && hField != null) {
				try {
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int w = Integer.parseInt(wField.getText());
					int h = Integer.parseInt(hField.getText());

					if(!propertyChange)
						le.platforms.add(new DragablePlatform(x, y, w, h, Platform.NORMAL));
					else
						le.platforms.set(changeIndex, new DragablePlatform(x, y, w, h, Platform.NORMAL));


					dispose();
				} catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error Creating Ball"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
				}
			}
			else{
				if(showedOnce)
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character ", "Error Creating Platform"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
			}
		}
		else if(platformSelector.getSelectedItem().equals(options[1])) {
			Object[] fields = boost.getValues();
			JTextField xField = (JTextField) fields[0];
			JTextField yField = (JTextField) fields[1];
			JTextField wField = (JTextField) fields[2];
			JTextField hField = (JTextField) fields[3];
			JTextField bField = (JTextField) fields[4];
			boolean showedOnce = true;
			if(xField != null && yField != null && wField != null && hField != null && bField != null) {
				try {
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int w = Integer.parseInt(wField.getText());
					int h = Integer.parseInt(hField.getText());
					double b = Double.parseDouble(bField.getText());

					if(!propertyChange)
						le.platforms.add(new DragablePlatform(x, y, w, h, Platform.BOOST, b));
					else
						le.platforms.set(changeIndex, new DragablePlatform(x, y, w, h, Platform.BOOST, b));

					dispose();
				} catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error Creating Ball"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
				}
			}
			else{
				if(showedOnce)
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character ", "Error Creating Platform"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
			}
		}
		else if(platformSelector.getSelectedItem().equals(options[2])) {
			Object[] fields = vertical.getValues();
			JTextField xField = (JTextField) fields[0];
			JTextField yField = (JTextField) fields[1];
			JTextField wField = (JTextField) fields[2];
			JTextField hField = (JTextField) fields[3];
			JTextField otherYField = (JTextField) fields[4];
			boolean showedOnce = true;
			if(xField != null && yField != null && wField != null && hField != null && otherYField != null) {
				try {
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int w = Integer.parseInt(wField.getText());
					int h = Integer.parseInt(hField.getText());
					int oY = Integer.parseInt(otherYField.getText());

					if(!propertyChange)
						le.platforms.add(new DragablePlatform(x, y, w, h, Platform.MOVING, Platform.VERTICAL, oY, "Dont Move"));
					else
						le.platforms.set(changeIndex, new DragablePlatform(x, y, w, h, Platform.MOVING, Platform.VERTICAL, oY, "Dont Move"));

					dispose();
				} catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error Creating Ball"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
				}
			}
			else{
				if(showedOnce)
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character ", "Error Creating Platform"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
			}
		}
		else if(platformSelector.getSelectedItem().equals(options[3])) {
			Object[] fields = horizontal.getValues();
			JTextField xField = (JTextField) fields[0];
			JTextField yField = (JTextField) fields[1];
			JTextField wField = (JTextField) fields[2];
			JTextField hField = (JTextField) fields[3];
			JTextField otherXField = (JTextField) fields[4];
			boolean showedOnce = true;
			if(xField != null && yField != null && wField != null && hField != null && otherXField != null) {
				try {
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int w = Integer.parseInt(wField.getText());
					int h = Integer.parseInt(hField.getText());
					int oX = Integer.parseInt(otherXField.getText());

					if(!propertyChange)
						le.platforms.add(new DragablePlatform(x, y, w, h, Platform.MOVING, Platform.HORIZONTAL, oX, "Dont Move"));
					else
						le.platforms.set(changeIndex, new DragablePlatform(x, y, w, h, Platform.MOVING, Platform.HORIZONTAL, oX, "Dont Move"));

					dispose();
				} catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error Creating Platform"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
				}
			}
			else{
				if(showedOnce)
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character ", "Error Creating Platform 2"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
			}
		}
		else if(platformSelector.getSelectedItem().equals(options[4])) {
			Object[] fields = death.getValues();
			JTextField xField = (JTextField) fields[0];
			JTextField yField = (JTextField) fields[1];
			JTextField wField = (JTextField) fields[2];
			JTextField hField = (JTextField) fields[3];
			boolean showedOnce = true;
			if(xField != null && yField != null && wField != null && hField != null) {
				try {
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int w = Integer.parseInt(wField.getText());
					int h = Integer.parseInt(hField.getText());

					if(!propertyChange)
						le.platforms.add(new DragablePlatform(x, y, w, h, Platform.DEAD));
					else
						le.platforms.set(changeIndex, new DragablePlatform(x, y, w, h, Platform.DEAD));

					dispose();
				} catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error Creating Ball"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
				}
			}
			else{
				if(showedOnce)
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character ", "Error Creating Platform"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
			}
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