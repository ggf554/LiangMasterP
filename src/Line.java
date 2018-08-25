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
	
	public List<Pointer> comLineMove(ArrayList wireInfo, int moveIndex) {
		
		int n = wireInfo.size();
		List<Pointer> l = new ArrayList<Pointer>();
		l.clear();
		lineInd.clear();
		Pointer p1, p2, p3;
		p1 = (Pointer)wireInfo.get(moveIndex);
		p2 = (Pointer)wireInfo.get(moveIndex+1);
		
		for(int i=0; i<n-1;i++) {
			p3 = (Pointer)wireInfo.get(i);
			if (p3.x >= p1.x && p3.x <= p2.x && p3.y >= p1.y && p3.y <= p2.y) {
				if(p3.toolFlag == 1) {
					
					l.add(p3);
					lineInd.add(i);
				}
			}
		}
		
		
		
		return l;
	}
	
	/**
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return true: p1,p2,p3 are both wire pointer
	 */
	public boolean isLineDot(Pointer p1, Pointer p2,Pointer p3) {
		
		if(p1.toolFlag == 1 && p2.toolFlag ==1 && p3.toolFlag ==1) {
			
			return true;
			
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param p1  component start pointer
	 * @param p2 component end pointer
	 * @param line the pointer of the wire
	 * @return true: the pointer of the wire inside the component
	 */
	public boolean lineInComponent(Pointer p1,Pointer p2,Pointer line) {
		
		
			
		
		if(line.x>= p1.x&&line.x<=p2.x) {
			if(line.y>=p1.y && line.y <= p2.y) {
					return true;
			}
		}
	
		return false;
	}
	
	
	
	
}
