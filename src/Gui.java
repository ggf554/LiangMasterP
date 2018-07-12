import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Gui extends JFrame implements MouseListener, MouseMotionListener, ActionListener {

	Graphics g;

	int startX, endX, startY, endY;

	Vector wireInfo = null;//set of vector information for wires
	Pointer endFlag = new Pointer(-1, -1, -1);
	
	FileInputStream picIn = null;
	FileOutputStream picOut = null;
	ObjectInputStream OIn = null;
	ObjectOutputStream OOut = null;
	
	JPanel p1;
	JPanel toolPanel;
	
	JButton openPic, savePic; //save or load the picture
	FileDialog openPicture, savePicture;
	
	
	Component c = new Component();

	public Gui() {
		super();
		
		wireInfo = new Vector();
		
		toolPanel = new JPanel();
		openPic = new JButton("Open file");
		savePic = new JButton("Save file");
		
		openPic.addActionListener(this);
		savePic.addActionListener(this);
		
		openPicture = new FileDialog(this, "open file", FileDialog.LOAD);
		savePicture = new FileDialog(this, "save file", FileDialog.SAVE);
		openPicture.setVisible(false);
		savePicture.setVisible(false);
		openPicture.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				openPicture.setVisible(false);
			}
		});
		savePicture.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				savePicture.setVisible(false);
			}
		});
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		
		toolPanel.add(openPic);
		toolPanel.add(savePic);
		
		
		this.add(toolPanel,BorderLayout.NORTH);
		
		
		p1 = new JPanel();

		this.setTitle("Drawing things");
		this.add(p1);
		this.addMouseListener(this);
		this.setSize(900, 600);
		this.setVisible(true);
		this.setLocation(200, 100);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addMouseMotionListener(this);
		g = this.getGraphics();

	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Pointer p1,p2;
		int n = wireInfo.size();
		for(int i=0; i<n; i++) {
			p1 = (Pointer) wireInfo.elementAt(i);
			p2 = (Pointer) wireInfo.elementAt(i+1);
			System.out.println("!!"+p1.x);
			Line2D line2 = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
			g2d.draw(line2);
		}
		
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public boolean mouseIn(int x, int y) {

		if (x >= c.getX() && x <= c.getX() + c.getWidth() && y >= c.getY() && y <= c.getY() + c.getHight()) {

			return true;

		}

		return false;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == openPic) {//open the picture
			openPicture.setVisible(true);
			if (openPicture.getFile() != null) {
				
				repaint();
				try {
					wireInfo.removeAllElements();
					File filein = new File(openPicture.getDirectory(), openPicture.getFile());
					picIn = new FileInputStream(filein);
					OIn = new ObjectInputStream(picIn);
					wireInfo = (Vector) OIn.readObject();
					OIn.close();
					repaint();
					
				} catch (ClassNotFoundException IOe2) {
					repaint();
					
					System.out.println("can not read object");
				} catch (IOException IOe) {
					repaint();
					
					System.out.println("can not read file");
				}
			}
		
			
		}
		
		if (e.getSource() == savePic) {//save the picture
				savePicture.setVisible(true);
				try {
					File fileout = new File(savePicture.getDirectory(), savePicture.getFile());
					picOut = new FileOutputStream(fileout);
					OOut = new ObjectOutputStream(picOut);
					OOut.writeObject(wireInfo);
					OOut.close();
				} catch (IOException IOe) {
					System.out.println("can not write object");
				}
			
		}

		
	}

	int dragx, dragy;
	boolean first = true;

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

//		Pointer p1;
//		int x = (int) e.getX();
//		int y = (int) e.getY();
//		p1 = new Pointer(x, y, 1);
//		wireInfo.addElement(p1);
//		System.out.println(wireInfo);
		
		
		if (mouseIn(e.getX(), e.getY())) {

			c.cleanComponent(g);

			if (first) {
				dragx = e.getX() - c.getX();
				dragy = e.getY() - c.getY();
				first = false;
			}

			c.setX(e.getX() - dragx);
			c.setY(e.getY() - dragy);

			c.paintComponent(g);
			
		} else {
			first = true;
		}

		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	int x,y;
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		Pointer p2;
		x = (int) e.getX();
		y = (int) e.getY();
		p2 = new Pointer(x, y, 1);
		wireInfo.addElement(p2);
	
		
		if (!mouseIn(e.getX(), e.getY())) {
			startX = e.getX();
			startY = e.getY();
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		Pointer p3;
		x = (int) e.getX();
		y = (int) e.getY();
		p3 = new Pointer(x, y, 1);
		wireInfo.addElement(p3);
		System.out.println(wireInfo);
		repaint();
		
		if (!mouseIn(e.getX(), e.getY())) {
			endX = e.getX();
			endY = e.getY();

			c.setX(startX);
			c.setY(startY);
			if (endX > startX) {
				c.setWidth(endX - startX);
			} else {
				c.setX(endX);
				c.setWidth(startX - endX);
			}

			if (endY > startY) {
				c.setHight(endY - startY);
			} else {
				c.setY(endY);
				c.setHight(startY - endY);
			}
			
			c.paintComponent(g);
		}

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
