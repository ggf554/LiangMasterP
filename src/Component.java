import java.awt.Graphics;

public class Component {

	private String descruption;
	private int startX,startY,width,hight;
	
	
	public Component () {
		startX=0;
		startY=0;
		width=0;
		hight=0;
	}
	public Component (int sx, int sy, int w,int h) {
		startX=sx;
		startY=sy;
		width=w;
		hight=h;
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
