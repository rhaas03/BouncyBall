import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import resources.StarPolygon;

public class NewStarDialog extends JDialog implements ActionListener {
	private LevelEditor le;

	private JButton cancel = new JButton("Cancel");
	private JButton apply = new JButton("Apply");

	public static final int PROPERTIES = 1;

	private JTextField xField, yField, sizeField;

	private boolean propertyChange = false;
	private int changeIndex = -1;

	public NewStarDialog(Frame owner, String title, LevelEditor l) {
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		le = l;
		basicOps();
		pack();
		setVisible(true);
	}

	public NewStarDialog(Frame owner, String title, LevelEditor l, int type, StarPolygon sp, int index) {
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		le = l;
		basicOps();

		if(type == PROPERTIES) {
			int xAvg = 0;
			int yAvg = 0;
			for(int i = 0; i < sp.npoints; i++) {
				xAvg += sp.xpoints[i];
				yAvg += sp.ypoints[i];
			}
			xAvg /= sp.npoints;
			yAvg /= sp.npoints;

			xField.setText(Integer.toString(xAvg));
			yField.setText(Integer.toString(yAvg + sp.getSize()));
			sizeField.setText(Integer.toString(sp.getSize()));
		}

		propertyChange = true;
		changeIndex = index;

		pack();
		setVisible(true);
	}

	private void basicOps() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(200, 100));
		setResizable(false);
		setLocation(le.frame.getX(), le.frame.getY());

		int inset = 10;
		((JComponent) getContentPane()).setBorder(new EmptyBorder(inset, inset, inset, inset));

		GridLayout grid = new GridLayout(3, 2);
		grid.setVgap(10);

		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		TitledBorder titled = BorderFactory.createTitledBorder(
				BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), "Properties");
		titled.setTitleFont(titled.getTitleFont().deriveFont(16f));
		titled.setTitleJustification(TitledBorder.LEFT);
		container.setBorder(titled);

		JPanel containerTwo = new JPanel(grid);
		containerTwo.setBorder(new EmptyBorder(inset, inset, inset, inset));

		xField = new JTextField(10);
		xField.addKeyListener(escape());

		containerTwo.add(new JLabel("X Coord: "));
		containerTwo.add(xField);

		yField = new JTextField(10);
		yField.addKeyListener(escape());

		containerTwo.add(new JLabel("Y Coord: "));
		containerTwo.add(yField);

		sizeField = new JTextField(10);
		sizeField.addKeyListener(escape());

		containerTwo.add(new JLabel("Size: "));
		containerTwo.add(sizeField);

		container.add(containerTwo);
		add(container);

		xField.addActionListener(this);
		yField.addActionListener(this);
		sizeField.addActionListener(this);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(apply);
		buttonPane.add(cancel);
		apply.addActionListener(this);
		cancel.addActionListener(this);

		add(buttonPane);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == cancel) dispose();
		if(source == apply || source == xField || source == yField || source == sizeField) {
			boolean showedOnce = true;
			if(xField != null && yField != null && sizeField != null) {
				try {
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int size = Integer.parseInt(sizeField.getText());

					if(!propertyChange)
						le.stars.add(new StarPolygon(x, y - size, size, (2*size)/5, 5, 55));
					else
						le.stars.set(changeIndex, new StarPolygon(x, y - size, size, (2*size)/5, 5, 55));

					le.repaint();

					dispose();
				}catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error Creating Star"
							, JOptionPane.ERROR_MESSAGE);
					showedOnce = false;
				}
			}
			else {
				if(showedOnce)
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error Creating Star"
							, JOptionPane.ERROR_MESSAGE);
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
