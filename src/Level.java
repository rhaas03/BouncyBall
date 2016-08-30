import java.awt.*;

public abstract class Level {
	public abstract void addListeners();
	public abstract void removeListeners();
	public abstract void drawLevel(Graphics2D g2d);
}
