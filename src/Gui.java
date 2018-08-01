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

public class Gui extends JFrame implements MouseListener, MouseMotionListener, ActionListener, ItemListener {

	Graphics g;

	int count = 0;
	int startX, endX, startY, endY;
	int toolFlag = 0; // the tool flag
	// (-1 -- endflag); (0 -- select); (1 -- line)
	// (2 -- clean up); (3 -- component)

	Color red = new Color(255, 0, 0);
	boolean mouseOver = false;
	boolean inputFlag = true;
	int comFlag = 0; // the component flag
	// (0 -- not selected); (1 -- or gate); (2 -- and gate)
	// (3 -- xor gate); (4 -- inv gate); (5 -- dff gate)

	Vector wireInfo = null;// set of vector information for wires
	Pointer endFlag = new Pointer(-1, -1, -1, 0);

	Choice comChoice;
	FileInputStream picIn = null;
	FileOutputStream picOut = null;
	ObjectInputStream OIn = null;
	ObjectOutputStream OOut = null;

	JPanel p1;
	JPanel toolPanel;

	Button drLine, drRect, clean, select;

	Button openPic, savePic; // save or load the picture
	FileDialog openPicture, savePicture;

	orGateComponent orGate = new orGateComponent();
	Line l;
	Pointer pointer1, pointer2;
	int moveIndex;

	public Gui() {

		wireInfo = new Vector();

		toolPanel = new JPanel();

		comChoice = new Choice();
		comChoice.add("Select Components");
		comChoice.add("or gate");
		comChoice.add("and gate");
		comChoice.add("xor gate");
		comChoice.add("inv gate");
		comChoice.add("dff gate");

		comChoice.addItemListener(this);

		select = new Button("Select");
		clean = new Button("Clean");
		drLine = new Button("Wire");
		drRect = new Button("Add Component");

		openPic = new Button("Open file");
		savePic = new Button("Save file");

		openPic.addActionListener(this);
		savePic.addActionListener(this);
		select.addActionListener(this);
		clean.addActionListener(this);
		drLine.addActionListener(this);
		drRect.addActionListener(this);

		toolPanel.add(openPic);
		toolPanel.add(savePic);
		toolPanel.add(select);
		toolPanel.add(clean);
		toolPanel.add(drLine);
		toolPanel.add(drRect);
		toolPanel.add(comChoice);

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
		Pointer p1, p2;
		int n = wireInfo.size();
		if (toolFlag == 2) {
			g.clearRect(0, 0, getSize().width, getSize().height);
		}
		for (int i = 0; i < n - 1; i++) {
			p1 = (Pointer) wireInfo.elementAt(i);
			p2 = (Pointer) wireInfo.elementAt(i + 1);
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
						g2d.setColor(new Color(0, 0, 255));
					} else {

						g2d.setColor(new Color(0, 0, 0));
					}

					Rectangle2D rect = new Rectangle2D.Double(p1.x, p1.y, Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
					Rectangle2D portIn1 = new Rectangle2D.Double(p1.x, p1.y + Math.abs(p2.y - p1.y) / 2, 10, 10);
					Rectangle2D portIn21 = new Rectangle2D.Double(p1.x, p1.y + Math.abs(p2.y - p1.y) / 3, 10, 10);
					Rectangle2D portIn22 = new Rectangle2D.Double(p1.x, p1.y + 2 * Math.abs(p2.y - p1.y) / 3, 10, 10);
					Rectangle2D portOut1 = new Rectangle2D.Double(p1.x + Math.abs(p2.x - p1.x) - 10,
							p1.y + Math.abs(p2.y - p1.y) / 2, 10, 10);

					g2d.draw(rect);
					if (p1.componentFlag == 1) {
						g2d.drawString("Or Gate", p1.x + 25, p1.y + 50);
						g2d.draw(portIn21);
						g2d.draw(portIn22);
						g2d.draw(portOut1);
					} else if (p1.componentFlag == 2) {
						g2d.drawString("And Gate", p1.x + 25, p1.y + 50);
						g2d.draw(portIn21);
						g2d.draw(portIn22);
						g2d.draw(portOut1);
					} else if (p1.componentFlag == 3) {
						g2d.drawString("Xor Gate", p1.x + 25, p1.y + 50);
						g2d.draw(portIn21);
						g2d.draw(portIn22);
						g2d.draw(portOut1);
					} else if (p1.componentFlag == 4) {
						g2d.drawString("Inv Gate", p1.x + 25, p1.y + 50);
						g2d.draw(portIn1);
						g2d.draw(portOut1);
					} else if (p1.componentFlag == 5) {
						g2d.drawString("Dff Gate", p1.x + 25, p1.y + 50);
						g2d.draw(portIn1);
						g2d.draw(portOut1);
					}
					g2d.setColor(new Color(0, 0, 0));
					break;
				case (-1):
					i = i + 1;
					break;
				default:
				}
			}
		}

	}

	/**
	 * 
	 * @param x
	 *            mouse location
	 * @param y
	 *            mouse location
	 * @param li
	 *            the line
	 * @return the distance between mouse and the line
	 */
	public double distence(double x, double y, Line li) {
		return Math.abs(li.A * x + li.B * y + li.C) / Math.sqrt(li.A * li.A + li.B * li.B);
	}

	public Line equation(int x1, int y1, int x2, int y2) {
		double a, b, c;
		a = y2 - y1;
		b = x1 - x2;
		c = x2 * y1 - x1 * y2;
		return l = new Line(a, b, c);

	}

	public void update(Graphics g) {
		g.clearRect(0, 0, getSize().width, getSize().height);
		paint(g);
	}

	public boolean mouseIn(int x, int y) {

		int n = wireInfo.size();

		if (n != 0) {
			for (int i = 0; i < n - 1; i++) {
				pointer1 = (Pointer) wireInfo.elementAt(i);
				pointer2 = (Pointer) wireInfo.elementAt(i + 1);
				if (toolFlag != 1) {
					if (pointer1.toolFlag == pointer2.toolFlag) {
						switch (pointer1.toolFlag) {
						case 0:
							break;
						case 1:// line
							double dis;
							dis = distence(x, y, equation(pointer1.x, pointer1.y, pointer2.x, pointer2.y));

							if (dis < 3) {
								moveIndex = i;

								return true;
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
				} else {


					if (pointer1.toolFlag == pointer2.toolFlag) {
						switch (pointer1.toolFlag) {
						case 0:
							break;
						case 1:// line
							break;
						case 3:// component
							Rectangle2D portIn1 = new Rectangle2D.Double(pointer1.x, pointer1.y + Math.abs(pointer2.y - pointer1.y) / 2, 10, 10);
							Rectangle2D portIn21 = new Rectangle2D.Double(pointer1.x, pointer1.y + Math.abs(pointer2.y - pointer1.y) / 3, 10, 10);
							Rectangle2D portIn22 = new Rectangle2D.Double(pointer1.x, pointer1.y + 2 * Math.abs(pointer2.y - pointer1.y) / 3, 10, 10);
							Rectangle2D portOut1 = new Rectangle2D.Double(pointer1.x + Math.abs(pointer2.x - pointer1.x) - 10,
									pointer1.y + Math.abs(pointer2.y - pointer1.y) / 2, 10, 10);
							if(!inputFlag&&(pointer1.componentFlag == 1 ||pointer1.componentFlag ==2 || pointer1.componentFlag==3)) {
								if (inRect(x,y,portIn21)||inRect(x,y,portIn22)) {
									
								
									moveIndex = i;
									
									return true;

								}
							} else if(!inputFlag&&(pointer1.componentFlag == 4 ||pointer1.componentFlag ==5)) {
								if (inRect(x,y,portIn1)) {
								
									moveIndex = i;
									return true;

								}
							}
							if(inputFlag) {
								if (inRect(x,y,portOut1)) {
								
									moveIndex = i;
									return true;

								}
							}
							
							
							break;
						case (-1):
							i = i + 1;
							break;
						default:
						}
					}
				
					
				}
			}
		}

		return false;

	}
	
	/**
	 * 
	 * @param x the x coordinate of the mouse
	 * @param y the y coordinate of the mouse
	 * @param rect the test rectangle
	 * @return is the mouse in the target rectangle
	 */
	public boolean inRect(int x, int y,Rectangle2D rect ) {
		if(x >= rect.getX() && x <= rect.getMaxX() && y >= rect.getY() && y <=rect.getMaxY()) {
			return true;
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == select) {
			System.out.println("Select");
			toolFlag = 0;
			selectedBlock = false;
		}

		if (e.getSource() == clean) {
			System.out.println("Clean");
			toolFlag = 2;

			wireInfo.removeAllElements();

			update(g);
		}

		if (e.getSource() == drLine) {
			System.out.println("Draw wire");
			toolFlag = 1;
		}

		if (e.getSource() == drRect) {
			toolFlag = 3;
			if (comFlag == 0) {
				System.out.println("Please select a type of component to add");
			} else {
				Pointer p1, p2;
				switch (comFlag) {
				case 0:
					break;
				case 1: // or gate
					x = 320;
					y = 220;
					p1 = new Pointer(x, y, toolFlag, comFlag);
					wireInfo.addElement(p1);
					p2 = new Pointer(x + 100, y + 100, toolFlag, comFlag);
					wireInfo.addElement(p2);
					wireInfo.addElement(endFlag);
					repaint();
					System.out.println("or gate component added");
					break;
				case 2:// and gate
					x = 320;
					y = 220;
					p1 = new Pointer(x, y, toolFlag, comFlag);
					wireInfo.addElement(p1);
					p2 = new Pointer(x + 100, y + 100, toolFlag, comFlag);
					wireInfo.addElement(p2);
					wireInfo.addElement(endFlag);
					repaint();
					System.out.println("and gate component added");
					break;
				case 3: // xor gate
					x = 320;
					y = 220;
					p1 = new Pointer(x, y, toolFlag, comFlag);
					wireInfo.addElement(p1);
					p2 = new Pointer(x + 100, y + 100, toolFlag, comFlag);
					wireInfo.addElement(p2);
					wireInfo.addElement(endFlag);
					repaint();
					System.out.println("xor gate component added");
					break;
				case 4: // inv gate
					x = 320;
					y = 220;
					p1 = new Pointer(x, y, toolFlag, comFlag);
					wireInfo.addElement(p1);
					p2 = new Pointer(x + 100, y + 100, toolFlag, comFlag);
					wireInfo.addElement(p2);
					wireInfo.addElement(endFlag);
					repaint();
					System.out.println("inv gate component added");
					break;
				case 5: // dff
					x = 320;
					y = 220;
					p1 = new Pointer(x, y, toolFlag, comFlag);
					wireInfo.addElement(p1);
					p2 = new Pointer(x + 100, y + 100, toolFlag, comFlag);
					wireInfo.addElement(p2);
					wireInfo.addElement(endFlag);
					repaint();
					System.out.println("dff gate component added");
					break;
				default:
				}
			}
			comChoice.select(0);
			comFlag = 0;
		}

		if (e.getSource() == openPic) {// open the picture
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

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

		// Pointer p1;
		// int x = (int) e.getX();
		// int y = (int) e.getY();
		// p1 = new Pointer(x, y, 1);
		// wireInfo.addElement(p1);
		// System.out.println(wireInfo);
		//

		if (selectedBlock) {
			if (pointer1.toolFlag == 1) {
				Pointer p3 = new Pointer(e.getX() - dragx1, e.getY() - dragy1, pointer1.toolFlag,
						pointer1.componentFlag);
				Pointer p4 = new Pointer(e.getX() - dragx2, e.getY() - dragy2, pointer2.toolFlag,
						pointer2.componentFlag);
				wireInfo.set(moveIndex, p3);
				wireInfo.set(moveIndex + 1, p4);

				update(g);
			}
			if (pointer1.toolFlag == 3) {
				Pointer p3 = new Pointer(e.getX() - dragx1, e.getY() - dragy1, pointer1.toolFlag,
						pointer1.componentFlag);
				Pointer p4 = new Pointer(e.getX() - dragx2, e.getY() - dragy2, pointer2.toolFlag,
						pointer2.componentFlag);
				wireInfo.set(moveIndex, p3);
				wireInfo.set(moveIndex + 1, p4);

				update(g);
			}
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

		if (mouseIn(e.getX(), e.getY())) {

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

			if (mouseIn(e.getX(), e.getY())) {
				if (pointer1.toolFlag == 1) {
					dragx1 = e.getX() - pointer1.x;
					dragy1 = e.getY() - pointer1.y;
					dragx2 = e.getX() - pointer2.x;
					dragy2 = e.getY() - pointer2.y;

					selectedBlock = true;
					toolFlag = (-1);
					count = 0;
					update(g);
					System.out.println("The wire has been selected");
				}

				if (pointer1.toolFlag == 3) {

					dragx1 = e.getX() - pointer1.x;
					dragy1 = e.getY() - pointer1.y;
					dragx2 = e.getX() - pointer2.x;
					dragy2 = e.getY() - pointer2.y;

					selectedBlock = true;
					toolFlag = (-1);
					count = 0;
					update(g);
					System.out.println("the component has been selected");
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
			
			x = (int) e.getX();
			y = (int) e.getY();
			if(mouseIn(x,y)) {
				
				p2 = new Pointer(x, y, toolFlag, comFlag);
				wireInfo.addElement(p2);
				inputFlag = !inputFlag;
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

		switch (toolFlag) {
		case 0:// select
			wireInfo.addElement(endFlag);
			break;
		case 1:
			
			x = (int) e.getX();
			y = (int) e.getY();
			if(mouseIn(x,y)) {
				
				p3 = new Pointer(x, y, toolFlag, comFlag);
				wireInfo.addElement(p3);
				wireInfo.addElement(endFlag);
				inputFlag = !inputFlag;
				repaint();
			} 
			
			break;

		default:
		}

		if (count < 1) {

		} else {
			selectedBlock = false;
			count = 0;
		}
		update(g);

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

			if (comName == "or gate") {
				comFlag = 1; // or gate component selected

			} else if (comName == "and gate") {
				comFlag = 2; // and gate component selected

			} else if (comName == "xor gate") {
				comFlag = 3; // xor gate component selected

			} else if (comName == "inv gate") {
				comFlag = 4; // inv gate component selected

			} else if (comName == "dff gate") {
				comFlag = 5; // dff gate component selected

			}
		}

	}

}
