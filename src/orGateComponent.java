import java.awt.Graphics;

public class orGateComponent {

	private String descruption;
	private int startX, startY, width, hight;

	public orGateComponent() {
		startX = 200;
		startY = 200;
		width = 30;
		hight = 30;
	}



	public void paintComponent(Graphics graphics) {

		try {

			graphics.drawRect(startX, startY, width, hight);

		} catch (Exception e) {

		}
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

}
