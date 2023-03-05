/*
 * This is a dialog for adding new Employees and saving records to file
 * */

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class AddRecordDialog extends JDialog implements ActionListener {
	JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	JButton save, cancel;
	EmployeeDetails parent;
	
	public AddRecordDialog(EmployeeDetails parent) {
		setTitle("Add Record");
		setModal(true);
		this.parent = parent;
		this.parent.setEnabled(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane(dialogPane());
		setContentPane(scrollPane);
		
		getRootPane().setDefaultButton(save);
		
		setSize(500, 370);
		setLocation(350, 250);
		setVisible(true);
	}

	public Container dialogPane() {
		JPanel empDetails, buttonPanel;
		empDetails = new JPanel(new MigLayout());
		buttonPanel = new JPanel();
		JTextField field;
		String fieldDimensions = "growx, pushx, wrap";

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(idField = addTextFieldToEmpDetails(empDetails, "ID"), fieldDimensions);
		idField.setEditable(false);
		empDetails.add(ppsField = addTextFieldToEmpDetails(empDetails, "PPS Number"), fieldDimensions);
		empDetails.add(surnameField = addTextFieldToEmpDetails(empDetails, "Surname"), fieldDimensions);
		empDetails.add(firstNameField = addTextFieldToEmpDetails(empDetails, "First Name"), fieldDimensions);
		empDetails.add(this.parent.genderCombo = addJComboBoxToEmpDetails(empDetails, "Gender", this.parent.gender), fieldDimensions);
		empDetails.add(this.parent.departmentCombo = addJComboBoxToEmpDetails(empDetails, "Department", this.parent.department), fieldDimensions);
		empDetails.add(this.parent.salaryField = addTextFieldToEmpDetails(empDetails, "Salary"), fieldDimensions);
		empDetails.add(this.parent.fullTimeCombo = addJComboBoxToEmpDetails(empDetails, "Full Time", this.parent.fullTime), fieldDimensions);
		
		buttonPanel.add(save = addButton("Save", "Save Changes"));
		save.requestFocus();
		buttonPanel.add(cancel = addButton("Cancel", "Cancel Changes"));
		empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");
		
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(this.parent.font1);
			if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Color.WHITE);
			}
			else if(empDetails.getComponent(i) instanceof JTextField){
				field = (JTextField) empDetails.getComponent(i);
				if(field == ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
				field.setDocument(new JTextFieldLimit(20));
			}
		}
		idField.setText(Integer.toString(this.parent.getNextFreeId()));
		return empDetails;
	}
	
	public JButton addButton(String buttonLabel, String tooltip) {
		JButton button = new JButton(buttonLabel);
		button.addActionListener(this);
		button.setToolTipText(tooltip);
		return button;
	}
	
	public JTextField addTextFieldToEmpDetails(JPanel panel, String label) {
		JTextField textfield = new JTextField(20);
		panel.add(new JLabel(label + ":"), "growx, pushx");
		return textfield;
	}
	
	public JComboBox<String> addJComboBoxToEmpDetails(JPanel panel, String label, String[] options) {
		JComboBox<String> comboBox = new JComboBox<String>(options);
		panel.add(new JLabel(label + ":"), "growx, pushx");
		return comboBox;
	}

	public void addRecord() {
		boolean fullTime = false;
		Employee theEmployee;

		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;
		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(), surnameField.getText().toUpperCase(),firstNameField.getText().toUpperCase(), genderCombo.getSelectedItem().toString().charAt(0),departmentCombo.getSelectedItem().toString(), Double.parseDouble(salaryField.getText()), fullTime);
		this.parent.currentEmployee = theEmployee;
		this.parent.addRecord(theEmployee);
		this.parent.displayRecords(theEmployee);
	}

	public void setToWhite() {
		ppsField.setBackground(Color.WHITE);
		surnameField.setBackground(Color.WHITE);
		firstNameField.setBackground(Color.WHITE);
		salaryField.setBackground(Color.WHITE);
		genderCombo.setBackground(Color.WHITE);
		departmentCombo.setBackground(Color.WHITE);
		fullTimeCombo.setBackground(Color.WHITE);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == save) {
			if (parent.checkInput()) {
				addRecord();
				dispose();
				this.parent.changesMadeForFile = true;
			}else {
				JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
				setToWhite();
			}
		}
		else if (e.getSource() == cancel)
			dispose();
	}
}