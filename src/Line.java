import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * the wire
 * Ax + By + C = 0
 * @author huafei
 *
 */
public class Line {

	double A;
	double B;
	double C;
	int lineIndex =0;
	List<Integer> lineInd = new ArrayList<Integer>();
	
	public Line(double a, double b, double c) {
		this.A = a;
		this.B = b;
		this.C = c;
		
		
	}
	
	public List<Pointer> comLineMove(Vector wireInfo, int moveIndex) {
		System.out.println("1112111111");
		int n = wireInfo.size();
		List<Pointer> l = new ArrayList<Pointer>();
		l.clear();
		lineInd.clear();
		Pointer p1, p2, p3;
		p1 = (Pointer)wireInfo.elementAt(moveIndex);
		p2 = (Pointer)wireInfo.elementAt(moveIndex+1);
		System.out.println("1111");
		for(int i=0; i<n-1;i++) {
			p3 = (Pointer)wireInfo.elementAt(i);
			if (p3.x >= p1.x && p3.x <= p2.x && p3.y >= p1.y && p3.y <= p2.y) {
				if(p3.toolFlag == 1) {
					System.out.println("1133311");
					l.add(p3);
					lineInd.add(i);
				}
			}
		}
		
		
		
		return l;
	}
	
	
	
	
}
