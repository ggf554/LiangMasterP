import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Gui extends JFrame implements MouseListener, MouseMotionListener, ActionListener, ItemListener {

	Graphics g;

	int portInMouseOver = -1;
	int portOutMouseOver = -1;
	int count = 0;
	int startX, endX, startY, endY;
	int toolFlag = 0; // the tool flag
	// (-1 -- endflag); (0 -- select); (1 -- line)
	// (2 -- clean up); (3 -- component); (4-- dot)

	ArrayList<Pointer> mycopy;
	Color red = new Color(255, 0, 0);
	boolean mouseOver = false;
	boolean inputFlag = true;
	boolean mouseOverPort = false;
	int comFlag = -1; // the component flag
	// (-1 -- not selected);
	ArrayList<Pointer> wireInfo = null;// set of vector information for wires
	Pointer endFlag = new Pointer(-10, -10, -1, -1);

	Choice comChoice;
	FileInputStream picIn = null;
	FileOutputStream picOut = null;
	ObjectInputStream OIn = null;
	ObjectOutputStream OOut = null;

	JPanel p1;
	JPanel toolPanel;

	Button drLine, drRect, clean, select, design, delete;

	Button openPic, savePic; // save or load the picture
	FileDialog openPicture, savePicture;

	GateComponent Compo;
	List<GateComponent> compoList = new ArrayList<GateComponent>();
	Line line;
	Pointer pointer1, pointer2;
	int moveIndex;
	boolean onLineCross = false;
	
	public void loadComponents() {
		try {
			String pathname = "ComponentsDetail.txt";
			File filename = new File(pathname);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
			BufferedReader br = new BufferedReader(reader);
			String lines = "";
			lines = br.readLine();
			while (lines != null) {

				String[] arr = lines.split("\\s+");
				Compo = new GateComponent(arr[0], Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
				compoList.add(Compo);

				lines = br.readLine();
			}

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Gui() {

		loadComponents();
		wireInfo = new ArrayList();

		toolPanel = new JPanel();

		comChoice = new Choice();
		comChoice.add("Select Components");
		for (int i = 0; i < compoList.size(); i++) {
			comChoice.add(compoList.get(i).getName());

		}

		comChoice.addItemListener(this);

		select = new Button("Select");
		clean = new Button("Clean");
		drLine = new Button("Wire");
		drRect = new Button("Add Component");
		design = new Button("Design Component");
		delete = new Button("Delete");
		openPic = new Button("Open file");
		savePic = new Button("Save file");

		openPic.addActionListener(this);
		savePic.addActionListener(this);
		select.addActionListener(this);
		clean.addActionListener(this);
		delete.addActionListener(this);
		design.addActionListener(this);
		drLine.addActionListener(this);
		drRect.addActionListener(this);

		toolPanel.add(clean);
		toolPanel.add(delete);
		toolPanel.add(select);
		toolPanel.add(drLine);
		toolPanel.add(drRect);
		toolPanel.add(comChoice);
		toolPanel.add(openPic);
		toolPanel.add(savePic);
		toolPanel.add(design);

		add(toolPanel, BorderLayout.NORTH);

		toolPanel.setVisible(true);
		this.setTitle("Drawing things");
		this.addMouseListener(this);
		this.setSize(900, 600);
		this.setVisible(true);
		this.setLocation(200, 100);
		validate();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addMouseMotionListener(this);
		g = this.getGraphics();

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

	}

	public void paint(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		Pointer p1, p2, p3;
		int n = wireInfo.size();
		if (toolFlag == 2) {
			g.clearRect(0, 0, getSize().width, getSize().height);
		}
		for (int i = 0; i < n - 2; i++) {
			p1 = (Pointer) wireInfo.get(i);
			p2 = (Pointer) wireInfo.get(i + 1);

			// draw the dot
			if (p1.toolFlag == 4) {

				if (selectedDot && moveIndex == i) {
					g2d.setColor(red);
				} else if (mouseOver && toolFlag == 0 && moveIndex == i) {
					g2d.setColor(new Color(0, 0, 255));
				} else {
					g2d.setColor(new Color(0, 0, 0));
				}

				g2d.fillOval(p1.x - 3, p1.y - 3, 6, 6);
				g2d.setColor(new Color(0, 0, 0));

			}

			if (p1.toolFlag == p2.toolFlag) {

				switch (p1.toolFlag) {
				case 0:

					break;
				case 1:// line

					if (selectedBlock && moveIndex == i) {

						g2d.setColor(red);

					} else if (mouseOver && toolFlag == 0 && moveIndex == i) {
						g2d.setColor(new Color(0, 0, 255));
					} else {
						g2d.setColor(new Color(0, 0, 0));
					}

					Line2D line2 = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
					g2d.draw(line2);

					g2d.setColor(new Color(0, 0, 0));
					break;
				case 3:// rectangle
					if (selectedBlock && moveIndex == i) {

						g2d.setColor(red);
					} else if (mouseOver && toolFlag == 0 && moveIndex == i) {
						g2d.setColor(new Color(0, 0, 225));

					} else if (mouseOver && toolFlag == 1 && moveIndex == i) {
						g2d.setColor(new Color(0, 0, 225));
					} else {

						g2d.setColor(new Color(0, 0, 0));
					}
					componentPortOn = false;
					if (moveIndex == i) {
						componentPortOn = true;
					}
					linePortColor(p1, p2, g2d);
					break;

				case (-1):
					i = i + 1;
					break;
				default:
				}
			}
		}

	}

	boolean componentPortOn;

	/**
	 * Drawing the in and out Port for the component
	 * 
	 * @param p1
	 *            Start pointer for the shape
	 * @param p2
	 *            end Pointer
	 * @param g2d
	 *            draw things
	 */
	public void linePortColor(Pointer p1, Pointer p2, Graphics2D g2d) {

		Rectangle2D rect = new Rectangle2D.Double(p1.x, p1.y, Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));

		g2d.draw(rect);

		Compo = compoList.get(p1.componentFlag);

		g2d.drawString(Compo.getName(), p1.x + 25, p1.y + 50);

		if (Compo.getinPort() != 0) {
			for (int i = 0; i < Compo.getinPort(); i++) {
				if (toolFlag == 1 && mouseOverPort && portInMouseOver == i && componentPortOn) {

					mouseOverPort = false;
					g2d.setColor(new Color(255, 0, 255));

				}

				Rectangle2D portIn = new Rectangle2D.Double(p1.x,
						p1.y + (i + 1) * Math.abs(p2.y - p1.y) / (Compo.getinPort() + 1), 10, 10);
				g2d.draw(portIn);

				if (toolFlag == 1 && portInMouseOver == i && componentPortOn) {
					g2d.setColor(new Color(0, 0, 255));
				}
			}
		}

		if (Compo.getoutPort() != 0) {
			for (int i = 0; i < Compo.getoutPort(); i++) {

				if (toolFlag == 1 && mouseOverPort && portOutMouseOver == i && componentPortOn) {

					mouseOverPort = false;
					g2d.setColor(new Color(255, 0, 255));

				}

				Rectangle2D portOut = new Rectangle2D.Double(p1.x + Math.abs(p2.x - p1.x) - 10,
						p1.y + (i + 1) * Math.abs(p2.y - p1.y) / (Compo.getoutPort() + 1), 10, 10);
				g2d.draw(portOut);

				if (toolFlag == 1 && portOutMouseOver == i && componentPortOn) {
					g2d.setColor(new Color(0, 0, 255));
				}
			}
		}

	}

	/**
	 * get the input to round 10
	 * @param x
	 * @return
	 */
	public int locationRound(int x) {
		double x1= x;
		
		x1 = x1/50;
		Math.round(x1);
		int x2 = (int) (x1 * 50);
		return x2;
	}
	
	/**
	 * check if the mouse in the componentPort
	 * 
	 * @param p1
	 *            start pointer for the component
	 * @param p2
	 *            end pointer
	 * @param x
	 *            mouse location
	 * @param y
	 *            mouse location
	 * @return true: in the port, false: not in the port
	 */
	public boolean inComponentPort(Pointer p1, Pointer p2, int x, int y) {
		portInMouseOver = -1;
		portOutMouseOver = -1;
		Compo = compoList.get(p1.componentFlag);
		if (Compo.getinPort() != 0) {
			for (int i = 0; i < Compo.getinPort(); i++) {
				Rectangle2D portIn = new Rectangle2D.Double(p1.x,
						p1.y + (i + 1) * Math.abs(p2.y - p1.y) / (Compo.getinPort() + 1), 10, 10);
				if (inRect(x, y, portIn)) {
					portInMouseOver = i;
					mouseOverPort = true;
					return true;
				}
			}
		}

		if (Compo.getoutPort() != 0) {
			for (int i = 0; i < Compo.getoutPort(); i++) {
				Rectangle2D portOut = new Rectangle2D.Double(p1.x + Math.abs(p2.x - p1.x) - 10,
						p1.y + (i + 1) * Math.abs(p2.y - p1.y) / (Compo.getoutPort() + 1), 10, 10);
				if (inRect(x, y, portOut)) {
					portOutMouseOver = i;
					mouseOverPort = true;
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 
	 * @param x
	 *            mouse location
	 * @param y
	 *            mouse location
	 * @param li
	 *            the line
	 * @return the distance between mouse and the wire
	 */
	public double distence(double x, double y, Line li) {
		return Math.abs(li.A * x + li.B * y + li.C) / Math.sqrt(li.A * li.A + li.B * li.B);
	}

	public Line equation(int x1, int y1, int x2, int y2) {
		double a, b, c;
		a = y2 - y1;
		b = x1 - x2;
		c = x2 * y1 - x1 * y2;
		return line = new Line(a, b, c);

	}

	public void update(Graphics g) {
		g.clearRect(0, 0, getSize().width, getSize().height);
		paint(g);
	}

	/**
	 * before the selection, check is the mouse near any target
	 * 
	 * @param x
	 *            mouse X
	 * @param y
	 *            mouse Y
	 * @return true, if mouse near any thing
	 */
	public boolean mouseIn(int x, int y) {

		int n = wireInfo.size();
		onLineCross = false;
		if (selectedBlock) {
			
		} else if (n != 0) {
			for (int i = 0; i < n - 2; i++) {
				pointer1 = (Pointer) wireInfo.get(i);
				pointer2 = (Pointer) wireInfo.get(i + 1);
				if (toolFlag != 1) { // select mode

					if (pointer1.toolFlag == 4) {// dot
						if (Math.abs(pointer1.x - x) <= 6 && Math.abs(pointer1.y - y) <= 6) {
							moveIndex = i;
							if(selectedDot) {
								return false;
							}
							return true;
						}
					} else if (pointer1.toolFlag == pointer2.toolFlag) {
						switch (pointer1.toolFlag) {
						case 0:
							break;
						case 1:// line

							double dis;

							dis = distence(x, y, equation(pointer1.x, pointer1.y, pointer2.x, pointer2.y));

							if (dis < 3) {
								if (Math.abs(pointer1.x - x) <= 6 && Math.abs(pointer1.y - y) <= 6) {

								} else if (Math.abs(pointer2.x - x) <= 6 && Math.abs(pointer2.y - y) <= 6) {

								} else {
									moveIndex = i;
									onLineCross = true;
								
									return true;
								}
							}

							break;

						case 3:// component
							if (x >= pointer1.x && x <= pointer2.x && y >= pointer1.y && y <= pointer2.y) {
								moveIndex = i;
								return true;
							}
							break;
						case (-1):
							i = i + 1;
							break;
						default:
						}
					}
				} else if (toolFlag == 1) { // Line mode

					if (pointer1.toolFlag == pointer2.toolFlag && pointer1.toolFlag == 1) {

						double dis;

						dis = distence(x, y, equation(pointer1.x, pointer1.y, pointer2.x, pointer2.y));

						if (dis < 3) {
							
								moveIndex = i;
								
							
								return true;
							
						}
						
					}

					
					if (pointer1.toolFlag == pointer2.toolFlag && pointer1.toolFlag == 3) {

						if (inComponentPort(pointer1, pointer2, x, y)) {
							moveIndex = i;
							return true;
						}
					}

				}
			}
		}

		return false;

	}

	/**
	 * 
	 * @param x
	 *            the x coordinate of the mouse
	 * @param y
	 *            the y coordinate of the mouse
	 * @param rect
	 *            the test rectangle
	 * @return is the mouse in the target rectangle
	 */
	public boolean inRect(int x, int y, Rectangle2D rect) {
		if (x >= rect.getX() && x <= rect.getMaxX() && y >= rect.getY() && y <= rect.getMaxY()) {
			return true;
		}
		return false;
	}

	boolean selectedDot = false;

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == select) {
			System.out.println("Select");
			toolFlag = 0;
			selectedBlock = false;
			selectedDot = false;
			onLineCross = false;
		}

		if (e.getSource() == delete) {
			System.out.println("delete");
			toolFlag = 5;

			if (selectedBlock) {
				selectedBlock = false;
				if (pointer1.toolFlag == 3) {

					wireInfo.remove(moveIndex);
					wireInfo.remove(moveIndex);
					System.out.println("The component has been deleted");

				} else if (pointer1.toolFlag == 1) {

					wireInfo.remove(moveIndex);
					wireInfo.remove(moveIndex - 1);
					System.out.println("The wire has been deleted");

				}
			}

			if (selectedDot) {
				selectedDot = false;
				wireInfo.remove(moveIndex);
				wireInfo.remove(moveIndex - 1);
				wireInfo.remove(moveIndex - 1);
				System.out.println("The dot has been deleted");
			}

			update(g);
		}

		if (e.getSource() == clean) {
			System.out.println("Clean");
			toolFlag = 2;

			wireInfo.removeAll(wireInfo);

			update(g);
		}

		if (e.getSource() == drLine) {
			System.out.println("Draw wire");
			toolFlag = 1;
		}

		if (e.getSource() == drRect) {
			toolFlag = 3;
			if (comFlag == -1) {
				System.out.println("Please select a type of component to add");
			} else {
				Pointer p1, p2;
				Compo = compoList.get(comFlag);
				p1 = new Pointer(Compo.getX(), Compo.getY(), toolFlag, comFlag);
				wireInfo.add(p1);
				p2 = new Pointer(Compo.getX() + Compo.getWidth(), Compo.getY() + Compo.getHight(), toolFlag, comFlag);
				wireInfo.add(p2);
				wireInfo.add(endFlag);
				repaint();
				System.out.println(Compo.getName() + " component added");

			}

			comChoice.select(0);
			comFlag = -1;
		}

		if (e.getSource() == design) {
			DesignNewComponent designNew = new DesignNewComponent();

		}

		if (e.getSource() == openPic) {// open the picture
			openPicture.setVisible(true);
			if (openPicture.getFile() != null) {

				repaint();
				try {
					wireInfo.removeAll(wireInfo);
					File filein = new File(openPicture.getDirectory(), openPicture.getFile());
					picIn = new FileInputStream(filein);
					OIn = new ObjectInputStream(picIn);
					wireInfo = (ArrayList) OIn.readObject();
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

		if (e.getSource() == savePic) {// save the picture
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

	int dragx1, dragy1, dragx2, dragy2;
	boolean first = true;
	boolean selectedBlock = false;
	boolean newNodeAdded = false;
	Pointer pp = null;
	int temIndex;
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

		
		
		
		if (selectedDot) {
			if(first) {
				first = false;
				pp = new Pointer(pointer1);
				temIndex = moveIndex;
			}
			
			mouseIn(locationRound(e.getX()),locationRound(e.getY()));
			if (pp.toolFlag == 4) {

				Pointer po1 = new Pointer(locationRound(e.getX()), locationRound(e.getY()), 1, pp.componentFlag);
				Pointer po2 = new Pointer(locationRound(e.getX()), locationRound(e.getY()), 4, pp.componentFlag);
				Pointer po3 = new Pointer(locationRound(e.getX()), locationRound(e.getY()), 1, pp.componentFlag);

				wireInfo.set(temIndex - 1, po1);
				wireInfo.set(temIndex, po2);
				wireInfo.set(temIndex + 1, po3);

				
				update(g);

			}
		}

		if (selectedBlock) {
			if (pointer1.toolFlag == 1) {
				

				Pointer po3 = new Pointer(locationRound(e.getX()), locationRound(e.getY()), pointer1.toolFlag, pointer1.componentFlag);
				if (!newNodeAdded) {
					wireInfo.add(moveIndex + 1, po3);
					newNodeAdded = true;
				} else {
					wireInfo.set(moveIndex + 1, po3);
				}

				update(g);
			}

			// selected a component, move it to new location
			if (pointer1.toolFlag == 3) {
				Pointer p3 = new Pointer(locationRound(e.getX() - dragx1 + pointer1.x), locationRound(e.getY() - dragy1 + pointer1.y),
						pointer1.toolFlag, pointer1.componentFlag);
				Pointer p4 = new Pointer(locationRound(e.getX() - dragx1 + pointer2.x),locationRound( e.getY() - dragy1 + pointer2.y),
						pointer2.toolFlag, pointer2.componentFlag);

				// wire fellow with component move
				for (int i = 0; i < wireInfo.size(); i++) {
					if (wireInfo.get(i).toolFlag == 1) {
						if (line.lineInComponent(wireInfo.get(moveIndex), wireInfo.get(moveIndex + 1),
								wireInfo.get(i))) {
							Pointer lineP = new Pointer(locationRound(e.getX() - dragx1 + mycopy.get(i).x),
									locationRound(e.getY() - dragy1 + mycopy.get(i).y), wireInfo.get(i).toolFlag,
									wireInfo.get(i).componentFlag);

							wireInfo.set(i, lineP);

						}
					}
				}

				wireInfo.set(moveIndex, p3);
				wireInfo.set(moveIndex + 1, p4);

				update(g);
			}
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

		if (mouseIn(locationRound(e.getX()), locationRound(e.getY()))) {

			mouseOver = true;
			update(g);
		} else {
			mouseOver = false;
			update(g);

		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (toolFlag == 0) {

			if (mouseIn(locationRound(e.getX()), locationRound(e.getY()))) {
				dragx1 = locationRound(e.getX());
				dragy1 = locationRound(e.getY());

				if (pointer1.toolFlag == 1) {

					
					selectedBlock = true;
					toolFlag = (-1);
					count = 0;
					update(g);
					System.out.println("The wire has been selected");

				}

				if (pointer1.toolFlag == 3) {

					

					mycopy = new ArrayList<Pointer>();

					mycopy = (ArrayList<Pointer>) wireInfo.clone();

					selectedBlock = true;
					toolFlag = (-1);
					count = 0;
					update(g);
					System.out.println("the component has been selected");
				}

				if (pointer1.toolFlag == 4) {

				

					selectedDot = true;
					toolFlag = (-1);
					count = 0;
					update(g);
					System.out.println("The dot has been selected");

				}
			}

		}
	}

	int x, y;

	@Override
	public void mousePressed(MouseEvent e) {

		// TODO Auto-generated method stub
		Pointer p2;
		switch (toolFlag) {
		case 1:// line

			x = (int) locationRound(e.getX());
			y = (int) locationRound(e.getY());
			if (mouseIn(x, y)) {

				p2 = new Pointer(x, y, toolFlag, comFlag);
				wireInfo.add(p2);

			}
			break;
		case 3:// rectangel

		default:

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

		count++;// select and move component count

		Pointer p3;
		
		if(onLineCross && selectedDot) {
			Pointer po1 = new Pointer(locationRound(e.getX()), locationRound(e.getY()), 1, pp.componentFlag);
			Pointer po2 = new Pointer(locationRound(e.getX()), locationRound(e.getY()), 4, pp.componentFlag);
			Pointer po3 = new Pointer(locationRound(e.getX()), locationRound(e.getY()), 1, pp.componentFlag);
			wireInfo.add(moveIndex + 1, po1);
			wireInfo.add(moveIndex + 1, po2);
			wireInfo.add(moveIndex + 1, po3);
		}

		if (newNodeAdded) {
			// Pointer po1 = new Pointer(e.getX(), e.getY(), pointer1.toolFlag,
			// pointer1.componentFlag);
			Pointer po2 = new Pointer(locationRound(e.getX()), locationRound(e.getY()), 4, pointer1.componentFlag);
			Pointer po3 = new Pointer(locationRound(e.getX()), locationRound(e.getY()), pointer1.toolFlag, pointer1.componentFlag);
			wireInfo.add(moveIndex + 1, po2);
			wireInfo.add(moveIndex + 1, po3);
		}
		newNodeAdded = false;

		switch (toolFlag) {
		case 0:// select
			wireInfo.add(endFlag);
			break;
		case 1:

			x = (int) locationRound(e.getX());
			y = (int) locationRound(e.getY());
			if (mouseIn(x, y)) {

				p3 = new Pointer(x, y, toolFlag, comFlag);
				wireInfo.add(p3);
				wireInfo.add(endFlag);

				repaint();
			}

			break;

		default:
		}

		if (count < 1) {

		} else {
			selectedDot = false;
			selectedBlock = false;
			count = 0;
		}
		update(g);
		first = true;

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub

		if (e.getSource() == comChoice) {
			String comName = comChoice.getSelectedItem();

			for (int i = 0; i < compoList.size(); i++) {
				if (comName == compoList.get(i).getName()) {
					comFlag = i;
					break;
				}
			}

		}

	}

}
