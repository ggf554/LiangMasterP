import java.awt.Graphics;

public class Component {

	private String descruption;
	private int startX,startY,width,hight;
	
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
	
	
	
}
