import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class PageLayoutSetup implements ActionListener {
	EmployeeDetails parent;
	JButton add, edit, deleteButton, displayAll, first, previous, next, last, searchId, searchSurname;
	JTextField searchByIdField, searchBySurnameField;
	
	public PageLayoutSetup(EmployeeDetails parent) {
		this.parent = parent;
	}
	
	public JPanel detailsPanel() {
		JPanel empDetails = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel();
		String fieldDimensions = "growx, pushx, wrap";

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(this.parent.idField = addTextFieldToEmpDetails(empDetails, "ID"), fieldDimensions);
		this.parent.idField.setEditable(false);
		empDetails.add(this.parent.ppsField = addTextFieldToEmpDetails(empDetails, "PPS Number"), fieldDimensions);
		empDetails.add(this.parent.surnameField = addTextFieldToEmpDetails(empDetails, "Surname"), fieldDimensions);
		empDetails.add(this.parent.firstNameField = addTextFieldToEmpDetails(empDetails, "First Name"), fieldDimensions);
		empDetails.add(this.parent.genderCombo = addJComboBoxToEmpDetails(empDetails, "Gender", this.parent.gender), fieldDimensions);
		empDetails.add(this.parent.departmentCombo = addJComboBoxToEmpDetails(empDetails, "Department", this.parent.department), fieldDimensions);
		empDetails.add(this.parent.salaryField = addTextFieldToEmpDetails(empDetails, "Salary"), fieldDimensions);
		empDetails.add(this.parent.fullTimeCombo = addJComboBoxToEmpDetails(empDetails, "Full Time", this.parent.fullTime), fieldDimensions);
		
		buttonPanel.add(this.parent.saveChange = addButton("Save", "Save Changes"));
		this.parent.saveChange.setVisible(false);
		buttonPanel.add(this.parent.cancelChange = addButton("Cancel", "Cancel edit"));
		this.parent.cancelChange.setVisible(false);

		empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");
		
		return empDetails;
	}
	
	public JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();
		String buttonWidth = "growx, pushx";

		buttonPanel.add(add = addButton("Add Record", "Add new Employee Record"), buttonWidth);
		buttonPanel.add(edit = addButton("Edit Record", "Edit current Employee"), buttonWidth);
		buttonPanel.add(deleteButton = addButton("Delete Record", "Delete current Employee"), buttonWidth + ", wrap");
		buttonPanel.add(displayAll = addButton("List all Records", "List all Registered Employees"), buttonWidth);	
		
		return buttonPanel;
	}
	
	public JButton addButton(String buttonLabel, String tooltip) {
		JButton button = new JButton(buttonLabel);
		button.addActionListener(this);
		button.setToolTipText(tooltip);
		return button;
	}
		
	public JPanel navigPanel() {
		JPanel navigPanel = new JPanel();
		navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));
		
		navigPanel.add(first = addNavButton("first.png", "first"));
		navigPanel.add(previous = addNavButton("prev.png", "previous"));
		navigPanel.add(next = addNavButton("next.png", "next"));
		navigPanel.add(last = addNavButton("last.png", "last"));
		
		return navigPanel;
	}
	
	private JButton addNavButton(String image, String type) {
		JButton button = new JButton(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH)));
		button.setPreferredSize(new Dimension(17, 17));
		button.addActionListener(this);
		button.setToolTipText("Display " + type + " Record");
		return button;
	}

	public JPanel searchPanel() {
		JPanel searchPanel = new JPanel(new MigLayout());
		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		String textFieldWidth = "width 200:200:200, growx, pushx";
		String buttonWidth = "width 35:35:35, height 20:20:20, growx, pushx, wrap";
		
		searchPanel.add(new JLabel("Search by ID:"), "growx, pushx");
		searchPanel.add(searchByIdField = addSearchTextField(), textFieldWidth);
		searchPanel.add(searchId = addButton("Go", "Search Employee By Id"), buttonWidth);
		
		searchPanel.add(new JLabel("Search by Surname:"), "growx, pushx");
		searchPanel.add(searchBySurnameField = addSearchTextField(), textFieldWidth);
		searchPanel.add(searchSurname = addButton("Go", "Search Employee By Surname"), buttonWidth);
		
		return searchPanel;
	}
	
	private JTextField addSearchTextField() {
		JTextField textField = new JTextField(20);
		textField.addActionListener(this);
		textField.setDocument(new JTextFieldLimit(20));
		return textField;
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
	
	private void cancelChange() {
		this.parent.setEnabled(false);
		this.parent.displayRecords(this.parent.currentEmployee);
	}
	
	public void searchEmployeeById() {
		boolean found = false;

		try {
			if (this.parent.isSomeoneToDisplay()) {
				this.parent.firstLastNavigateRecord("First");
				int firstId = this.parent.currentEmployee.getEmployeeId();
				
				if (searchByIdField.getText().trim().equals(this.parent.idField.getText().trim()))
					found = true;
				else if (searchByIdField.getText().trim().equals(Integer.toString(this.parent.currentEmployee.getEmployeeId()))) {
					found = true;
					this.parent.displayRecords(this.parent.currentEmployee);
				} else {
					this.parent.nextPrevoiusNavigateRecord("Next");;
					while (firstId != this.parent.currentEmployee.getEmployeeId()) {
						if (Integer.parseInt(searchByIdField.getText().trim()) == this.parent.currentEmployee.getEmployeeId()) {
							found = true;
							this.parent.displayRecords(this.parent.currentEmployee);
							break;
						} else
							this.parent.nextPrevoiusNavigateRecord("Next");;
					} 
				}
					
				if (!found)
					JOptionPane.showMessageDialog(null, "Employee not found!");
			} 
		} catch (NumberFormatException e) {
			searchByIdField.setBackground(new Color(255, 150, 150));
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		} 
		searchByIdField.setBackground(Color.WHITE);
		searchByIdField.setText("");
	}

	public void searchEmployeeBySurname() {
		boolean found = false;

		if (this.parent.isSomeoneToDisplay()) {
			this.parent.firstLastNavigateRecord("First");
			String firstSurname = this.parent.currentEmployee.getSurname().trim();

			if (searchBySurnameField.getText().trim().equalsIgnoreCase(this.parent.surnameField.getText().trim()))
				found = true;
			else if (searchBySurnameField.getText().trim().equalsIgnoreCase(this.parent.currentEmployee.getSurname().trim())) {
				found = true;
				this.parent.displayRecords(this.parent.currentEmployee);
			} else {
				this.parent.nextPrevoiusNavigateRecord("Next");;
				while (!firstSurname.trim().equalsIgnoreCase(this.parent.currentEmployee.getSurname().trim())) {
					if (searchBySurnameField.getText().trim().equalsIgnoreCase(this.parent.currentEmployee.getSurname().trim())) {
						found = true;
						this.parent.displayRecords(this.parent.currentEmployee);
						break;
					} else
						this.parent.nextPrevoiusNavigateRecord("Next");;
				} 
			} 

			if (!found)
				JOptionPane.showMessageDialog(null, "Employee not found!");
		}
		searchBySurnameField.setText("");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add && this.parent.checkInput() && !this.parent.checkForChanges())
			new AddRecordDialog(parent);
		else if (e.getSource() == edit && this.parent.checkInput() && !this.parent.checkForChanges()) 
			this.parent.editDetails();
		else if (e.getSource() == deleteButton && this.parent.checkInput() && !this.parent.checkForChanges()) 
			this.parent.deleteRecord();
		else if (e.getSource() == displayAll && this.parent.checkInput() && !this.parent.checkForChanges() && this.parent.isSomeoneToDisplay()) 
			new EmployeeSummaryDialog(this.parent.getAllEmloyees());
		else if (e.getSource() == first && this.parent.checkInput() && !this.parent.checkForChanges()) {
			this.parent.firstLastNavigateRecord("First");
			this.parent.displayRecords(this.parent.currentEmployee);
		} else if (e.getSource() == previous && this.parent.checkInput() && !this.parent.checkForChanges()) {
			this.parent.nextPrevoiusNavigateRecord("Previous");
			this.parent.displayRecords(this.parent.currentEmployee);
		} else if (e.getSource() == next && this.parent.checkInput() && !this.parent.checkForChanges()) {
			this.parent.nextPrevoiusNavigateRecord("Next");
			this.parent.displayRecords(this.parent.currentEmployee);
		} else if ( e.getSource() == last && this.parent.checkInput() && !this.parent.checkForChanges()) {
			this.parent.firstLastNavigateRecord("Last");
			this.parent.displayRecords(this.parent.currentEmployee);
		} else if (e.getSource() == this.parent.saveChange && this.parent.checkInput() && !this.parent.checkForChanges()) {
			
		} else if (e.getSource() == this.parent.cancelChange)
			cancelChange();
		else if (e.getSource() == searchId || e.getSource() == searchByIdField)
			searchEmployeeById();
		else if (e.getSource() == searchSurname || e.getSource() == searchBySurnameField)
			searchEmployeeBySurname();
		
	}
	
}
