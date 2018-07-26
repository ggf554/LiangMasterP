import java.io.Serializable;

/**
 * support class
 * record the x,y coordinate and tool using
 * @author huafei
 *
 */

public class Pointer implements Serializable {
	int x, y;// x and y coordinate
	int toolFlag;//different kinds of tool
	int componentFlag;//different kinds of component 
	public Pointer(int x, int y, int tool , int component) {
		
		this.x = x;
		this.y = y;
		this.toolFlag = tool;
		this.componentFlag = component;
	}
}