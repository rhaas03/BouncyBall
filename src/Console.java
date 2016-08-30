import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class Console extends JFrame implements ActionListener {
	private Environment env;

	private JTextPane display;
	private JTextField commandField;

	private String division = " - - - - - - - - - - - - - - - - - -\n";

	private Timer shutdownTimer = new Timer(1000, this);
	private Timer printHelpTimer = new Timer(5, this);
	private int shutdownCounter = 5;

	private KeyListener cancelListener = cancelSDWN();

	private String[] helpArray = {
		"help: lists available commands",
		"setLevel <num>: sets unlocked levels",
		"exit: closes console",
		"close: closes console",
		"version: returns version number",
		"fontsize <size>: Sets font size of console",
		"shutdown : closes game",
		"sdwn : closes game"
	};

	private Font defaultFont;

	public Console(Environment e) {
		super("Bouncy Ball Console");
		env = e;
		addWindowListener(closer());
		setPreferredSize(new Dimension(500, 350));
		setMinimumSize(new Dimension(550, 200));

		defaultFont = env.apple2Font.deriveFont(10f);
		defaultFont = new Font("Courier New", Font.PLAIN, 12);

		setLocation(env.frame.getX(), env.frame.getY());

		int inset = 5;
		EmptyBorder eb = new EmptyBorder(new Insets(inset, inset, inset, inset));

		display = new JTextPane();
		JScrollPane sp = new JScrollPane(display);
		sp.getVerticalScrollBar().setUnitIncrement(3);
		int gray = 250;
		display.setBackground(new Color(gray, gray, gray));
		display.setFont(display.getFont().deriveFont(Font.BOLD, 10f));
		display.setFocusable(false);
		display.setBorder(eb);
		display.setMargin(new Insets(5, 5, 5, 5));
		display.setFont(defaultFont);
		display.setCursor(Cursor.getDefaultCursor());
		appendToPane(display, "Welcome To: Bouncy Ball Console\n", Color.BLUE);
		appendToPane(display, "Type \"help\" for commands\n", Color.BLUE);
		appendToPane(display, division, Color.BLACK);

		commandField = new JTextField();
		commandField.addActionListener(this);
		commandField.setFont(display.getFont());
		commandField.requestFocus();
		commandField.setMaximumSize(commandField.getPreferredSize());

		getContentPane().add(sp, BorderLayout.CENTER);
		getContentPane().add(commandField, BorderLayout.SOUTH);

		pack();
		setVisible(true);
	}

	private int helpCounter = 0;

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == commandField) {
			command(commandField.getText());
		}
		else if(source == shutdownTimer) {
			appendToPane(display, "Shutting down in: " + shutdownCounter + "\n", error, defaultFont.deriveFont(Font.BOLD));
			if(shutdownCounter <= 0) System.exit(0);
			shutdownCounter--;
		}
		else if(source == printHelpTimer) {
			if(helpCounter < helpArray.length) {
				appendToPane(display, "\t" + helpArray[helpCounter] + "\n", goodResponse);
				helpCounter++;
			}
			else {
				helpCounter = 0;
				printHelpTimer.stop();
			}
		}
	}

	private final Color error = Color.RED;
	private final Color goodResponse = Color.GREEN.darker().darker();

	private void command(String com) {
		appendToPane(display, "User: " + com + "\n", Color.BLACK);
		if(com.toLowerCase().startsWith("setlevel ")) {
			String setString = com.substring(com.indexOf(" ") + 1);
			try {
				int setlevel = Integer.parseInt(setString);
				if(setlevel < 0) setlevel = 0;
				else if(setlevel > 21) setlevel = 21;
				appendToPane(display, "Setting Level To: " + setlevel + "\n", goodResponse);
				env.saveLevelManually(setlevel);
				env.updateLevelMenu();
			}
			catch(NumberFormatException nfe) {
				String text = "Error: No Level Set\n";
				appendToPane(display, text, error);
			}
			//else appendToPane(display, "Error: Improper Format. Format: setLevel(level)\n", error);
		}
		else if(com.toLowerCase().equals("exit") || com.equals("close")) {
			dispose();
			env.consoleOpen = false;
		}
		else if(com.toLowerCase().equals("help")) {
			appendToPane(display, "List of Available Commands:\n", goodResponse);
			printHelpTimer.start();
		}
		else if(com.toLowerCase().equals("version")) appendToPane(display, "Version " + About.VERSION, goodResponse);
		else if(com.toLowerCase().startsWith("fontsize ")) {
			String sizeString = com.substring(com.indexOf(" ") + 1);
			try {
				int fontsize = Integer.parseInt(sizeString);
				if(fontsize > 0) {
					defaultFont = defaultFont.deriveFont((float)fontsize);
					display.setFont(defaultFont);
					commandField.setFont(display.getFont());
					commandField.setMaximumSize(commandField.getPreferredSize());
					pack();
					revalidate();
					appendToPane(display, "Set font to " + fontsize + "\n", goodResponse);
				}
				else appendToPane(display, "Error: Font size must be greater than zero\n", error);
			} catch(NumberFormatException nfe) {
				appendToPane(display, "Error: No font set\n", error);
			}
		}
		else if(com.toLowerCase().equals("shutdown") || com.toLowerCase().equals("sdwn")) {
			appendToPane(display, "SHUTDOWN INITIATED - PRESS ANY KEY TO CANCEL\n", error);
			commandField.addKeyListener(cancelListener);
			shutdownTimer.start();
		}
		else if(com.toLowerCase().equals("sdwn0")) System.exit(0);
		else if(com.toLowerCase().startsWith("set music ")) {
			if(com.toLowerCase().substring(com.lastIndexOf(" ")).contains("on")) {
				env.setMusic(true);
				appendToPane(display, "Music On\n", goodResponse);
			}
			else if(com.toLowerCase().substring(com.lastIndexOf(" ")).contains("off")) {
				env.setMusic(false);
				appendToPane(display, "Music Off\n", goodResponse);
			}
		}
		else appendToPane(display, "Error: Unknown Command\n", error);

		commandField.setText("");
	}

	private void appendToPane(JTextPane pane, String text, Color color) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = pane.getDocument().getLength();
		pane.setCaretPosition(len);
        pane.setCharacterAttributes(aset, false);
		pane.replaceSelection(text);
	}

	private void appendToPane(JTextPane pane, String text, Color color, Font f) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, f.getFamily());
		aset = sc.addAttribute(aset, StyleConstants.FontSize, f.getSize());
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = pane.getDocument().getLength();
		pane.setCaretPosition(len);
        pane.setCharacterAttributes(aset, false);
		pane.replaceSelection(text);
		pane.setCharacterAttributes(aset, true);
	}

	private WindowListener closer() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				env.consoleOpen = false;
			}
		};
	}

	private KeyListener cancelSDWN() {
		return new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				shutdownTimer.stop();
				shutdownCounter = 5;
				commandField.removeKeyListener(cancelListener);
				appendToPane(display, "Shutdown Canceled\n", goodResponse, defaultFont);
			}
		};
	}
}
