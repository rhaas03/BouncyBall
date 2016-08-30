package resources;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.JPanel;

public class StaticBall extends BouncyBall implements Serializable {
	public StaticBall(double x, double y, int d, double rlSpeed, double defaultUpSpeed, Color c, JPanel e) {
		super(x, y, d, rlSpeed, defaultUpSpeed, c, e, "dont move");
	}
}
