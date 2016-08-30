package resources;
import java.awt.*;
import java.io.Serializable;

public class StarPolygon extends Polygon implements Serializable {
	private boolean show = true;
	private int r;

    public StarPolygon(int x, int y, int r, int innerR, int vertexCount) {
        this(x, y, r, innerR, vertexCount, 0);
    }
    public StarPolygon(int x, int y, int r, int innerR, int vertexCount, double startAngle) {
        super(getXCoordinates(x, y, r, innerR,  vertexCount, startAngle)
              ,getYCoordinates(x, y, r, innerR, vertexCount, startAngle)
              ,vertexCount*2);
        this.r = r;
    }

    protected static int[] getXCoordinates(int x, int y, int r, int innerR, int vertexCount, double startAngle) {
        int res[]=new int[vertexCount*2];
        double addAngle=2*Math.PI/vertexCount;
        double angle=startAngle;
        double innerAngle=startAngle+Math.PI/vertexCount;
        for (int i=0; i<vertexCount; i++) {
            res[i*2]=(int)Math.round(r*Math.cos(angle))+x;
            angle+=addAngle;
            res[i*2+1]=(int)Math.round(innerR*Math.cos(innerAngle))+x;
            innerAngle+=addAngle;
        }
        return res;
    }

    protected static int[] getYCoordinates(int x, int y, int r, int innerR, int vertexCount, double startAngle) {
        int res[]=new int[vertexCount*2];
        double addAngle=2*Math.PI/vertexCount;
        double angle=startAngle;
        double innerAngle=startAngle+Math.PI/vertexCount;
        for (int i=0; i<vertexCount; i++) {
            res[i*2]=(int)Math.round(r*Math.sin(angle))+y;
            angle+=addAngle;
            res[i*2+1]=(int)Math.round(innerR*Math.sin(innerAngle))+y;
            innerAngle+=addAngle;
        }
        return res;
    }

    public void setShow(boolean s) {
    	show = s;
    }

    public boolean getShow() {
    	return show;
    }

    public void setLocation(int x, int y) {
    	for(int i = 0; i < xpoints.length; i++)
    		xpoints[i] = x;
    	for(int i = 0; i < ypoints.length; i++)
    		ypoints[i] = y;
    }

    public int getSize() {
    	return r;
    }
}
