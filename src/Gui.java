import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Gui extends JFrame implements MouseListener,MouseMotionListener,ActionListener{
	
	Graphics g;
	
	int startX,endX,startY,endY;
	
	JPanel p1;
	
	Component c;
	public Gui() {
		p1 = new JPanel();
		
		this.setTitle("Drawing things");
		this.add(p1);
		this.addMouseListener(this);
		this.setSize(400,400);
		this.setVisible(true);
		this.setLocation(200, 100);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		g = this.getGraphics();
	}

	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		startX=e.getX();
		startY=e.getY();
		System.out.println(startX + "@@");
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		endX=e.getX();
		endY=e.getY();
		System.out.println(startX +" "+ startY +" "+ (endX-startX) +" "+ (endY-startY));
		
		c = new Component(startX,startY,(endX-startX),(endY-startY));
		
		c.paintComponent(g);
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	


	
	
}
