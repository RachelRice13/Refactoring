/*
 * This is a dialog for searching Employees using either their Id or Surname, which is specified in the constructor parameters.
 * */

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public class SearchByDialog extends JDialog implements ActionListener {
	EmployeeDetails parent;
	JButton search, cancel;
	JTextField searchField;
	String type;
	
	public SearchByDialog(EmployeeDetails parent, String searchByType) {
		setTitle("Search by " + searchByType);
		setModal(true);
		this.parent = parent;
		type = searchByType;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(searchPane(searchByType));
		setContentPane(scrollPane);

		getRootPane().setDefaultButton(search);
		
		setSize(500, 190);
		setLocation(350, 250);
		setVisible(true);
	}
	
	public Container searchPane(String searchByType) {
		JPanel searchPanel = new JPanel(new GridLayout(3,1));
		JPanel textPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JLabel searchLabel;

		searchPanel.add(new JLabel("Search by " + searchByType));
	
		textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		textPanel.add(searchLabel = new JLabel("Enter " + searchByType + ":"));
		searchLabel.setFont(this.parent.font1);
		textPanel.add(searchField = new JTextField(20));
		searchField.setFont(this.parent.font1);
		searchField.setDocument(new JTextFieldLimit(20));

		buttonPanel.add(search = new JButton("Search"));
		search.addActionListener(this);
		search.requestFocus();
		
		buttonPanel.add(cancel = new JButton("Cancel"));
		cancel.addActionListener(this);
		
		searchPanel.add(textPanel);
		searchPanel.add(buttonPanel);

		return searchPanel;
	}

	public void actionPerformed(ActionEvent e) {	
		if(e.getSource() == search){
			if(type.equals("Surname")) {
				this.parent.searchBySurnameField.setText(searchField.getText());
				this.parent.searchEmployeeBySurname();
			} else {
				try {
					Double.parseDouble(searchField.getText());
					this.parent.searchByIdField.setText(searchField.getText());
					this.parent.searchEmployeeById();
				} catch (NumberFormatException num) {
					searchField.setBackground(new Color(255, 150, 150));
					JOptionPane.showMessageDialog(null, "Wrong ID format!");
				}
			}
			dispose();
		} else if(e.getSource() == cancel)
			dispose();
		
	}
}