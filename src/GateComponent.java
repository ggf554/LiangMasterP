import java.awt.Graphics;

public class GateComponent {

	private String name;
	private int startX, startY, width, hight;
	private int inPort,outPort;

	public GateComponent(String n, int in,int out) {
		this.name = n;
		this.inPort = in;
		this.outPort = out;
		startX = 300;
		startY = 200;
		width = 100;
		hight = 100;

	}

	

	public void setX(int x) {
		startX = x;
	}

	public void setY(int y) {
		startY = y;
	}

	public void setWidth(int w) {
		width = w;
	}

	public void setHight(int h) {
		hight = h;
	}
	
	public void setinPort(int in) {
		inPort = in;
	}
	
	public void setoutPort(int out) {
		outPort = out;
	}
	
	public void setname(String n) {
		name = n;
	}
	

	
	public int getX() {
		return startX;
	}

	public int getY() {
		return startY;
	}

	public int getWidth() {
		return width;
	}

	public int getHight() {
		return hight;
	}
	
	public int getinPort() {
		return inPort;
	}
	
	public int getoutPort() {
		return outPort;
	}

	public String getName() {
		return name;
	}
	

}
