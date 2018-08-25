import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;

public class DesignNewComponent extends JFrame implements ActionListener {
	private JTextField textField;
	private JComboBox comboBox, comboBox_1;
	private String[] portNumbers = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
	private Button btnNewButton, btnCancel;

	public DesignNewComponent() {

		this.setTitle("Design new Component");

		this.setSize(500, 300);
		this.setVisible(true);
		this.setLocation(300, 200);
		getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Component Name: ");
		lblNewLabel.setBounds(32, 32, 192, 29);
		getContentPane().add(lblNewLabel);

		JLabel lblInputPort = new JLabel("Number of Input Port: ");
		lblInputPort.setBounds(32, 86, 192, 29);
		getContentPane().add(lblInputPort);

		JLabel lblOutputPort = new JLabel("Number of Output Port: ");
		lblOutputPort.setBounds(32, 147, 192, 29);
		getContentPane().add(lblOutputPort);

		textField = new JTextField();
		textField.setBounds(234, 36, 114, 21);
		getContentPane().add(textField);
		textField.setText("");
		textField.setColumns(10);

		comboBox = new JComboBox(portNumbers);
		comboBox.setMaximumRowCount(5);
		comboBox.setSelectedIndex(0);
		comboBox.setBounds(234, 151, 60, 21);
		getContentPane().add(comboBox);

		comboBox_1 = new JComboBox(portNumbers);
		comboBox_1.setSelectedIndex(0);
		comboBox_1.setMaximumRowCount(5);
		comboBox_1.setBounds(234, 90, 60, 21);
		getContentPane().add(comboBox_1);

		btnNewButton = new Button("Creat new component");
		btnNewButton.setBounds(71, 206, 207, 25);
		getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(this);

		btnCancel = new Button("Cancel");
		btnCancel.setBounds(283, 206, 147, 25);
		getContentPane().add(btnCancel);
		btnCancel.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == btnCancel) {
			this.dispose();
		}

		if (e.getSource() == btnNewButton) {
			if (textField.getText().equals("")) {
				JOptionPane.showInternalMessageDialog(getContentPane(),
						"Please enter the name, number of inport and outport for the component", "information",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				try {
					File writename = new File("ComponentsDetail.txt");
					writename.createNewFile();
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(writename, true)));

					out.append(textField.getText() + " " + comboBox.getSelectedIndex() + " "
							+ comboBox_1.getSelectedIndex() + "\r\n");
					out.flush();
					out.close();
					JOptionPane.showInternalMessageDialog(getContentPane(),
							"New component created, please reload the program to use the new component", "information",
							JOptionPane.INFORMATION_MESSAGE);
					
					this.dispose();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
		}

	}
}
