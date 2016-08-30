package resources;

import java.io.Serializable;

public class DragablePlatform extends Platform implements Serializable{
	public DragablePlatform(int x, int y, int w, int h, int type) {
		super(x, y, w, h, type);
	}
	/*public DragablePlatform(int x, int y, int w, int h, int type, int horizVerti, int max) {
		super(x, y, w, h, type, horizVerti, max);
	}*/
	public DragablePlatform(int x, int y, int w, int h, int type, double boost) {
		super(x, y, w, h, type, boost);
	}
	public DragablePlatform(int x, int y, int w, int h, int type, int horizVerti, int max, Object dontMove) {
		super(x, y, w, h, type, horizVerti, max, dontMove);
	}
}
