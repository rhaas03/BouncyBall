package resources;

import java.awt.Dimension;
import java.io.*;
import java.util.ArrayList;

public class CustomSave implements Serializable {
	private ArrayList<DragablePlatform> platforms;
	private ArrayList<StarPolygon> stars;

	private StaticBall ball;
	private Dimension levelSize;

	public CustomSave(ArrayList<DragablePlatform> platforms, ArrayList<StarPolygon> stars, StaticBall ball, Dimension levelSize){
		this.platforms = platforms; this.stars = stars; this.ball = ball; this.levelSize = levelSize;
	}

	public ArrayList<DragablePlatform> getPlatforms() {
		return platforms;
	}
	public ArrayList<StarPolygon> getStars() {
		return stars;
	}
	public StaticBall getBall() {
		return ball;
	}
	public Dimension getLevelSize() {
		return levelSize;
	}
}
