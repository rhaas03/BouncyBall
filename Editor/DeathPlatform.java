import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class DeathPlatform extends JPanel implements ActionListener {
	private JTextField xField, yField, widthField, heightField;
	private final Font mainFont = new Font("", Font.PLAIN, 14);
	private NewPlatformDialog npd;

	public DeathPlatform(NewPlatformDialog npd) {
		this.npd = npd;
		int inset = 10;
		setBorder(new EmptyBorder(inset, inset, inset, inset));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		GridLayout grid = new GridLayout(4, 2);
		grid.setVgap(10);
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		TitledBorder titled = BorderFactory.createTitledBorder(
				BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK), "Properties");
		titled.setTitleFont(mainFont);
		titled.setTitleJustification(TitledBorder.CENTER);
		container.setBorder(titled);

		JPanel containerTwo = new JPanel(grid);
		inset = 20;
		containerTwo.setBorder(new EmptyBorder(inset, inset, inset, inset));

		xField = new JTextField(10);
		xField.addActionListener(this);
		xField.addKeyListener(npd.escape());
		containerTwo.add(new JLabel("X Coord: "));
		containerTwo.add(xField);

		yField = new JTextField(10);
		yField.addActionListener(this);
		yField.addKeyListener(npd.escape());
		containerTwo.add(new JLabel("Y Coord: "));
		containerTwo.add(yField);

		widthField = new JTextField(10);
		widthField.addActionListener(this);
		widthField.addKeyListener(npd.escape());
		containerTwo.add(new JLabel("Width: "));
		containerTwo.add(widthField);

		heightField = new JTextField(10);
		heightField.addActionListener(this);
		heightField.addKeyListener(npd.escape());
		containerTwo.add(new JLabel("Height: "));
		containerTwo.add(heightField);

		container.add(containerTwo);
		add(container);
	}

	public Object[] getValues() {
		return new Object[] {
				xField, yField, widthField, heightField
		};
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == xField || source == yField || source == widthField || source == heightField)
			npd.applyFunction();
	}

	public void setValues(int x, int y, int w, int h) {
		xField.setText(Integer.toString(x));
		yField.setText(Integer.toString(y));
		widthField.setText(Integer.toString(w));
		heightField.setText(Integer.toString(h));
	}
}
