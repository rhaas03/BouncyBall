package resources;

import java.awt.*;
import java.io.*;
public class SettingsSave implements Serializable{

	private boolean antialias;
	private Color ballColor;
	private boolean music;
	private boolean isCustom;
	public SettingsSave(boolean antialias, Color ballColor, boolean music) {
		this.antialias = antialias;
		this.ballColor = ballColor;
		this.music = music;
	}
	public SettingsSave(boolean antialias, Color ballColor, boolean music, boolean isCustom) {
		this.antialias = antialias;
		this.ballColor = ballColor;
		this.music = music;
		this.isCustom = isCustom;
	}

	public boolean getAntialias() {
		return antialias;
	}
	public Color getBallColor() {
		return ballColor;
	}
	public boolean getMusic() {
		return music;
	}
	public boolean getCustom() {
		return isCustom;
	}
}
