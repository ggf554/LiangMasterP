import java.io.Serializable;

/**
 * support class
 * record the x,y coordinate and tool using
 * @author huafei
 *
 */

public class Pointer implements Serializable {
	int x, y;// x and y coordinate
	int nb;//different kinds of component

	public Pointer(int x, int y, int nb) {
		this.x = x;
		this.y = y;
		this.nb = nb;
	}
}