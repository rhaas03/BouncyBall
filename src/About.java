import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.*;

public class About extends JPanel {
	private Environment env;
	public static final String VERSION = "4.6.0";

	private final String aboutText =
			"    Bouncy Ball is a game I created largely based off a mobile game called BounceBall. It is a game I played"
			+ " excessively, and really wanted to attempt to recreate it to a certain extent. I first started working"
			+ " on Bouncy Ball on March 17, 2016. I have started from scratch several times, and each time I did, called it"
			+ " a new version. Each version I completed allowed me to learn from what I had done previously, and what I could change"
			+ " or do differently. This is obviously the 4th version I have created, and is by far the most complete"
			+ " version I have made. Enjoy.";

	public About(Environment e) {
		env = e;
		setLocation(0, env.getMouseLine());
		setSize(new Dimension(env.getWidth(), env.getHeight() - env.getMouseLine()));
		setOpaque(true);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

		JTextArea aboutArea = new JTextArea(aboutText);
		aboutArea.setMaximumSize(new Dimension(getWidth() - 20, getHeight()));
		aboutArea.setEditable(false);
		aboutArea.setForeground(Color.BLACK);
		aboutArea.setFocusable(false);
		//aboutArea.setFont(new Font("", Font.PLAIN, 16));
		aboutArea.setFont(env.samsungFont.deriveFont(16f));
		aboutArea.setBackground(getBackground());

		aboutArea.setLineWrap(true);
		aboutArea.setWrapStyleWord(true);
		int inset = 10;
		aboutArea.setMargin(new Insets(inset, inset, 5, inset + 15));

		mainPane.add(aboutArea);

		GridLayout gl = new GridLayout(4, 2);
		int gap = 10;
		gl.setVgap(gap);
		gl.setHgap(gap + 20);
		JPanel infoContainer = new JPanel();
		infoContainer.setMaximumSize(new Dimension(getWidth() - 20, getHeight()));
		infoContainer.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));
		JPanel informationPane = new JPanel(gl);
		informationPane.setBorder(new EmptyBorder(0, 10, 10, 10));
		informationPane.setMaximumSize(new Dimension(getWidth() - 20, getHeight()));

		MatteBorder labelBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK);
		labelBorder = null;

		JLabel versionLabel = new JLabel("Version: ");
		versionLabel.setFont(aboutArea.getFont());
		versionLabel.setBorder(labelBorder);
		informationPane.add(versionLabel);
		JLabel versionInfo = new JLabel(VERSION);
		versionInfo.setFont(aboutArea.getFont());
		versionInfo.setBorder(labelBorder);
		informationPane.add(versionInfo);

		JLabel creatorLabel = new JLabel("Creator: " );
		creatorLabel.setFont(aboutArea.getFont());
		creatorLabel.setBorder(labelBorder);
		informationPane.add(creatorLabel);
		JLabel creatorInfo = new JLabel("Ryan Haas");
		creatorInfo.setBorder(labelBorder);
		creatorInfo.setFont(aboutArea.getFont());
		informationPane.add(creatorInfo);

		JLabel downloadLabel = new JLabel("Link to all downloads: ");
		downloadLabel.setFont(aboutArea.getFont());
		downloadLabel.setBorder(labelBorder);
		informationPane.add(downloadLabel);
		JLabel downloadLink = new JLabel("Downloads");
		downloadLink.setFont(aboutArea.getFont());
		downloadLink.setBorder(labelBorder);
		downloadLink.setForeground(Color.BLUE);
		informationPane.add(downloadLink);

		downloadLink.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				try {
					openWebpage(new URL("http://ryanhaas.weebly.com/bouncy-ball.html"));
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
			public void mouseEntered(MouseEvent e) {
				env.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent e) {
				env.setCursor(Cursor.getDefaultCursor());
			}
		});

		JLabel lastUpdatedLabel = new JLabel("Last Updated: ");
		lastUpdatedLabel.setFont(aboutArea.getFont());
		lastUpdatedLabel.setBorder(labelBorder);
		informationPane.add(lastUpdatedLabel);
		JLabel lastUpdatedInfo = new JLabel("July 31, 2016");
		lastUpdatedInfo.setFont(aboutArea.getFont());
		lastUpdatedInfo.setBorder(labelBorder);
		informationPane.add(lastUpdatedInfo);


		infoContainer.add(informationPane);
		mainPane.add(infoContainer);

		JScrollPane scrollPane = new JScrollPane(mainPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(3);
		add(scrollPane);
	}

	public static void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	public static void openWebpage(URL url) {
	    try {
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	}
}
