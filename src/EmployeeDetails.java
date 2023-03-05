/* * 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements ActionListener, ItemListener, DocumentListener, WindowListener {
	private FileMenuActions fileMenuActions = new FileMenuActions(EmployeeDetails.this);
	
	private static final DecimalFormat inactiveCurrencyFieldFormat = new DecimalFormat("\u20ac ###,###,##0.00");
	private static final DecimalFormat activeCurrencyFieldFormat = new DecimalFormat("0.00");
	public long currentByteStart = 0;
	public RandomFile application = new RandomFile();
	public FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
	public File currentFile;
	public boolean changeMadeForTextfield = false;
	boolean changesMadeForFile = false;
	private JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById, searchBySurname, listAll, closeApp;
	private JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname, saveChange, cancelChange;
	public JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	public JTextField idField, ppsField, surnameField, firstNameField, salaryField, searchByIdField, searchBySurnameField;
	public static EmployeeDetails frame = new EmployeeDetails();
	Font font1 = new Font("SansSerif", Font.BOLD, 16);
	String generatedFileName;
	Employee currentEmployee;
	String[] gender = {"", "M", "F"};
	String[] department = { "", "Administration", "Production", "Transport", "Management" };
	String[] fullTime = { "", "Yes", "No" };

	private JMenuBar menuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		recordMenu = new JMenu("Records");
		recordMenu.setMnemonic(KeyEvent.VK_R);
		navigateMenu = new JMenu("Navigate");
		navigateMenu.setMnemonic(KeyEvent.VK_N);
		closeMenu = new JMenu("Exit");
		closeMenu.setMnemonic(KeyEvent.VK_E);

		menuBar.add(fileMenu);
		menuBar.add(recordMenu);
		menuBar.add(navigateMenu);
		menuBar.add(closeMenu);
		
		fileMenu.add(open = addMenuItem("Open", KeyEvent.VK_O));
		fileMenu.add(save = addMenuItem("Save", KeyEvent.VK_S));
		fileMenu.add(saveAs = addMenuItem("Save As", KeyEvent.VK_F2));
		
		recordMenu.add(create = addMenuItem("Create new Record", KeyEvent.VK_N));
		recordMenu.add(modify = addMenuItem("Modify Record", KeyEvent.VK_E));		
		recordMenu.add(delete = new JMenuItem("Delete Record")).addActionListener(this);

		navigateMenu.add(firstItem = new JMenuItem("First")).addActionListener(this);
		navigateMenu.add(prevItem = new JMenuItem("Previous")).addActionListener(this);
		navigateMenu.add(nextItem = new JMenuItem("Next")).addActionListener(this);
		navigateMenu.add(lastItem = new JMenuItem("Last")).addActionListener(this);
		navigateMenu.addSeparator();
		navigateMenu.add(searchById = new JMenuItem("Search by ID")).addActionListener(this);
		navigateMenu.add(searchBySurname = new JMenuItem("Search by Surname")).addActionListener(this);
		navigateMenu.add(listAll = new JMenuItem("List all Records")).addActionListener(this);

		closeMenu.add(saveAs = addMenuItem("Close", KeyEvent.VK_F4));

		return menuBar;
	}
		
	private JMenuItem addMenuItem(String title, int keyEvent) {
		JMenuItem menuItem = new JMenuItem(title);
		menuItem.addActionListener(this);
		menuItem.setMnemonic(keyEvent);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.CTRL_MASK));
		return menuItem;
	}

	private JPanel searchPanel() {
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
	
	private JButton addButton(String buttonLabel, String tooltip) {
		JButton button = new JButton(buttonLabel);
		button.addActionListener(this);
		button.setToolTipText(tooltip);
		return button;
	}
	
	private JPanel navigPanel() {
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

	private JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();
		String buttonWidth = "growx, pushx";

		buttonPanel.add(add = addButton("Add Record", "Add new Employee Record"), buttonWidth);
		buttonPanel.add(edit = addButton("Edit Record", "Edit current Employee"), buttonWidth);
		buttonPanel.add(deleteButton = addButton("Delete Record", "Delete current Employee"), buttonWidth + ", wrap");
		buttonPanel.add(displayAll = addButton("List all Records", "List all Registered Employees"), buttonWidth);	
		
		return buttonPanel;
	}

	private JTextField addTextFieldToEmpDetails(JPanel panel, String label) {
		JTextField textfield = new JTextField(20);
		panel.add(new JLabel(label + ":"), "growx, pushx");
		return textfield;
	}
	
	private JComboBox<String> addJComboBoxToEmpDetails(JPanel panel, String label, String[] options) {
		JComboBox<String> comboBox = new JComboBox<String>(options);
		panel.add(new JLabel(label + ":"), "growx, pushx");
		return comboBox;
	}
	
	private JPanel detailsPanel() {
		JPanel empDetails = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel();
		JTextField field;
		String fieldDimensions = "growx, pushx, wrap";

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(idField = addTextFieldToEmpDetails(empDetails, "ID"), fieldDimensions);
		idField.setEditable(false);
		empDetails.add(ppsField = addTextFieldToEmpDetails(empDetails, "PPS Number"), fieldDimensions);
		empDetails.add(surnameField = addTextFieldToEmpDetails(empDetails, "Surname"), fieldDimensions);
		empDetails.add(firstNameField = addTextFieldToEmpDetails(empDetails, "First Name"), fieldDimensions);
		empDetails.add(genderCombo = addJComboBoxToEmpDetails(empDetails, "Gender", gender), fieldDimensions);
		empDetails.add(departmentCombo = addJComboBoxToEmpDetails(empDetails, "Department", department), fieldDimensions);
		empDetails.add(salaryField = addTextFieldToEmpDetails(empDetails, "Salary"), fieldDimensions);
		empDetails.add(fullTimeCombo = addJComboBoxToEmpDetails(empDetails, "Full Time", fullTime), fieldDimensions);
		
		buttonPanel.add(saveChange = addButton("Save", "Save Changes"));
		saveChange.setVisible(false);
		buttonPanel.add(cancelChange = addButton("Cancel", "Cancel edit"));
		cancelChange.setVisible(false);

		empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");

		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(font1);
			if (empDetails.getComponent(i) instanceof JTextField) {
				field = (JTextField) empDetails.getComponent(i);
				field.setEditable(false);
				
				if (field == ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
					field.setDocument(new JTextFieldLimit(20));
				field.getDocument().addDocumentListener(this);
			} else if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Color.WHITE);
				empDetails.getComponent(i).setEnabled(false);
				((JComboBox<String>) empDetails.getComponent(i)).addItemListener(this);
				((JComboBox<String>) empDetails.getComponent(i)).setRenderer(new DefaultListCellRenderer() {
					public void paint(Graphics g) {
						setForeground(new Color(65, 65, 65));
						super.paint(g);
					}
				});
			} 
		} 
		return empDetails;
	}
	
	public void displayRecords(Employee thisEmployee) {
		int countGender = 0;
		int countDep = 0;
		boolean found = false;

		searchByIdField.setText("");
		searchBySurnameField.setText("");

		if (thisEmployee == null || thisEmployee.getEmployeeId() == 0) {
		} else {
			while (!found && countGender < gender.length - 1) {
				if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gender[countGender]))
					found = true;
				else
					countGender++;
			} 
			found = false;

			while (!found && countDep < department.length - 1) {
				if (thisEmployee.getDepartment().trim().equalsIgnoreCase(department[countDep]))
					found = true;
				else
					countDep++;
			} 
			idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
			ppsField.setText(thisEmployee.getPps().trim());
			surnameField.setText(thisEmployee.getSurname().trim());
			firstNameField.setText(thisEmployee.getFirstName());
			genderCombo.setSelectedIndex(countGender);
			departmentCombo.setSelectedIndex(countDep);
			salaryField.setText(inactiveCurrencyFieldFormat.format(thisEmployee.getSalary()));
			
			if (thisEmployee.getFullTime() == true)
				fullTimeCombo.setSelectedIndex(1);
			else
				fullTimeCombo.setSelectedIndex(2);
		}
		changeMadeForTextfield = false;
	}
	
	public void firstLastNavigateRecord(String option) {
		if (isSomeoneToDisplay()) {
			application.openReadFile(currentFile.getAbsolutePath());
			if(option.equals("First"))
				currentByteStart = application.getFirst();
			else 
				currentByteStart = application.getLast();
			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();
			
			if (currentEmployee.getEmployeeId() == 0)
				if(option.equals("First"))
					nextPrevoiusNavigateRecord("Next");
				else
					nextPrevoiusNavigateRecord("Previous");
					
		} 
	}

	public void nextPrevoiusNavigateRecord(String option) {
		if (isSomeoneToDisplay()) {
			application.openReadFile(currentFile.getAbsolutePath());
			if(option.equals("Next"))
				currentByteStart = application.getNext(currentByteStart);
			else 
				currentByteStart = application.getPrevious(currentByteStart);
			currentEmployee = application.readRecords(currentByteStart);

			while (currentEmployee.getEmployeeId() == 0) {
				if(option.equals("Next"))
					currentByteStart = application.getNext(currentByteStart);
				else 
					currentByteStart = application.getPrevious(currentByteStart);
				currentEmployee = application.readRecords(currentByteStart);
			} 
			application.closeReadFile();
		} 
	}

	public void searchEmployeeById() {
		boolean found = false;

		try {
			if (isSomeoneToDisplay()) {
				firstLastNavigateRecord("First");
				int firstId = currentEmployee.getEmployeeId();
				
				if (searchByIdField.getText().trim().equals(idField.getText().trim()))
					found = true;
				else if (searchByIdField.getText().trim().equals(Integer.toString(currentEmployee.getEmployeeId()))) {
					found = true;
					displayRecords(currentEmployee);
				} else {
					nextPrevoiusNavigateRecord("Next");;
					while (firstId != currentEmployee.getEmployeeId()) {
						if (Integer.parseInt(searchByIdField.getText().trim()) == currentEmployee.getEmployeeId()) {
							found = true;
							displayRecords(currentEmployee);
							break;
						} else
							nextPrevoiusNavigateRecord("Next");;
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

		if (isSomeoneToDisplay()) {
			firstLastNavigateRecord("First");
			String firstSurname = currentEmployee.getSurname().trim();

			if (searchBySurnameField.getText().trim().equalsIgnoreCase(surnameField.getText().trim()))
				found = true;
			else if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
				found = true;
				displayRecords(currentEmployee);
			} else {
				nextPrevoiusNavigateRecord("Next");;
				while (!firstSurname.trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
					if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
						found = true;
						displayRecords(currentEmployee);
						break;
					} else
						nextPrevoiusNavigateRecord("Next");;
				} 
			} 

			if (!found)
				JOptionPane.showMessageDialog(null, "Employee not found!");
		}
		searchBySurnameField.setText("");
	}

	public int getNextFreeId() {
		int nextFreeId = 0;
		
		if (currentFile.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		else {
			firstLastNavigateRecord("Last");
			nextFreeId = currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}

	private Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;
		
		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(), surnameField.getText().toUpperCase(), firstNameField.getText().toUpperCase(), genderCombo.getSelectedItem().toString().charAt(0), departmentCombo.getSelectedItem().toString(), Double.parseDouble(salaryField.getText()), fullTime);
		return theEmployee;
	}

	public void addRecord(Employee newEmployee) {
		application.openWriteFile(currentFile.getAbsolutePath());
		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();
	}

	private void deleteRecord() {
		if (isSomeoneToDisplay()) {
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

			if (returnVal == JOptionPane.YES_OPTION) {
				application.openWriteFile(currentFile.getAbsolutePath());
				application.deleteRecords(currentByteStart);
				application.closeWriteFile();

				if (isSomeoneToDisplay()) {
					nextPrevoiusNavigateRecord("Next");;
					displayRecords(currentEmployee);
				} 
			} 
		}
	}

	private Vector<Object> getAllEmloyees() {
		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;
		long byteStart = currentByteStart;
		int firstId;

		firstLastNavigateRecord("First");
		firstId = currentEmployee.getEmployeeId();
		do {
			empDetails = new Vector<Object>();
			empDetails.addElement(new Integer(currentEmployee.getEmployeeId()));
			empDetails.addElement(currentEmployee.getPps());
			empDetails.addElement(currentEmployee.getSurname());
			empDetails.addElement(currentEmployee.getFirstName());
			empDetails.addElement(new Character(currentEmployee.getGender()));
			empDetails.addElement(currentEmployee.getDepartment());
			empDetails.addElement(new Double(currentEmployee.getSalary()));
			empDetails.addElement(new Boolean(currentEmployee.getFullTime()));
			allEmployee.addElement(empDetails);
			nextPrevoiusNavigateRecord("Next");;
		} while (firstId != currentEmployee.getEmployeeId());
		currentByteStart = byteStart;

		return allEmployee;
	}

	private void editDetails() {
		if (isSomeoneToDisplay()) {
			salaryField.setText(activeCurrencyFieldFormat.format(currentEmployee.getSalary()));
			changeMadeForTextfield = false;
			setEnabled(true);
		} 
	}

	private void cancelChange() {
		setEnabled(false);
		displayRecords(currentEmployee);
	}

	private boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		application.openReadFile(currentFile.getAbsolutePath());
		someoneToDisplay = application.isSomeoneToDisplay();
		application.closeReadFile();

		if (!someoneToDisplay) {
			currentEmployee = null;
			idField.setText("");
			ppsField.setText("");
			surnameField.setText("");
			firstNameField.setText("");
			salaryField.setText("");
			genderCombo.setSelectedIndex(0);
			departmentCombo.setSelectedIndex(0);
			fullTimeCombo.setSelectedIndex(0);
			JOptionPane.showMessageDialog(null, "No Employees registered!");
		}
		return someoneToDisplay;
	}

	public boolean correctPps(String pps, long currentByte) {
		boolean ppsExist = false;

		if (pps.length() == 8 || pps.length() == 9) {
			if (Character.isDigit(pps.charAt(0)) && Character.isDigit(pps.charAt(1)) && Character.isDigit(pps.charAt(2)) && Character.isDigit(pps.charAt(3)) && Character.isDigit(pps.charAt(4)) && Character.isDigit(pps.charAt(5)) && Character.isDigit(pps.charAt(6))	&& Character.isLetter(pps.charAt(7))&& (pps.length() == 8 || Character.isLetter(pps.charAt(8)))) {
				application.openReadFile(currentFile.getAbsolutePath());
				ppsExist = application.isPpsExist(pps, currentByte);
				application.closeReadFile();
			} else
				ppsExist = true;
		} else
			ppsExist = true;

		return ppsExist;
	}

	private boolean checkForChanges() {
		boolean anyChanges = false;

		if (changeMadeForTextfield) {
			saveChanges();
			anyChanges = true;
		} else {
			setEnabled(false);
			displayRecords(currentEmployee);
		} 

		return anyChanges;
	}

	private boolean checkInput() {
		boolean valid = true;
		
		if (ppsField.isEditable() && ppsField.getText().trim().isEmpty()) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
		if (ppsField.isEditable() && correctPps(ppsField.getText().trim(), currentByteStart)) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
		if (surnameField.isEditable() && surnameField.getText().trim().isEmpty()) {
			surnameField.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
		if (firstNameField.isEditable() && firstNameField.getText().trim().isEmpty()) {
			firstNameField.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
		if (genderCombo.getSelectedIndex() == 0 && genderCombo.isEnabled()) {
			genderCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
		if (departmentCombo.getSelectedIndex() == 0 && departmentCombo.isEnabled()) {
			departmentCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
		
		try {// try to get values from text field
			Double.parseDouble(salaryField.getText());

			if (Double.parseDouble(salaryField.getText()) < 0) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} 
		} catch (NumberFormatException num) {
			if (salaryField.isEditable()) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} 
		} 
		
		if (fullTimeCombo.getSelectedIndex() == 0 && fullTimeCombo.isEnabled()) {
			fullTimeCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} 

		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");

		if (ppsField.isEditable())
			setToWhite();

		return valid;
	}

	private void setToWhite() {
		ppsField.setBackground(UIManager.getColor("TextField.background"));
		surnameField.setBackground(UIManager.getColor("TextField.background"));
		firstNameField.setBackground(UIManager.getColor("TextField.background"));
		salaryField.setBackground(UIManager.getColor("TextField.background"));
		genderCombo.setBackground(UIManager.getColor("TextField.background"));
		departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}

	public void setEnabled(boolean booleanValue) {
		boolean search;
		if (booleanValue)
			search = false;
		else
			search = true;
		
		ppsField.setEditable(booleanValue);
		surnameField.setEditable(booleanValue);
		firstNameField.setEditable(booleanValue);
		genderCombo.setEnabled(booleanValue);
		departmentCombo.setEnabled(booleanValue);
		salaryField.setEditable(booleanValue);
		fullTimeCombo.setEnabled(booleanValue);
		saveChange.setVisible(booleanValue);
		cancelChange.setVisible(booleanValue);
		searchByIdField.setEnabled(search);
		searchBySurnameField.setEnabled(search);
		searchId.setEnabled(search);
		searchSurname.setEnabled(search);
	}

	private void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		
		if (returnVal == JOptionPane.YES_OPTION) {
			application.openWriteFile(currentFile.getAbsolutePath());
			currentEmployee = getChangedDetails();
			application.changeRecords(currentEmployee, currentByteStart);
			application.closeWriteFile();
			changesMadeForFile = false;
		} 
		displayRecords(currentEmployee);
		setEnabled(false);
	}

	private void exitApp() {
		if (currentFile.length() != 0) {
			if (changesMadeForFile) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				
				if (returnVal == JOptionPane.YES_OPTION) {
					fileMenuActions.saveFile();
					if (currentFile.getName().equals(generatedFileName))
						currentFile.delete();
					System.exit(0);
				} else if (returnVal == JOptionPane.NO_OPTION) {
					if (currentFile.getName().equals(generatedFileName))
						currentFile.delete();
					System.exit(0);
				} 
			} else {
				if (currentFile.getName().equals(generatedFileName))
					currentFile.delete();
				System.exit(0);
			}
		} else {
			if (currentFile.getName().equals(generatedFileName))
				currentFile.delete();
			System.exit(0);
		} 
	}

	private String generateFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();
		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		String generatedfileName = fileName.toString();
		return generatedfileName;
	}

	private void createRandomFile() {
		generatedFileName = generateFileName() + ".dat";
		currentFile = new File(generatedFileName);
		application.createFile(currentFile.getName());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeApp && checkInput() && !checkForChanges()) {
			exitApp();
		} else if (e.getSource() == open && checkInput() && !checkForChanges()) {
			fileMenuActions.openFile();
		} else if (e.getSource() == save) {
			if (checkInput() && !checkForChanges())
				fileMenuActions.saveFile();
			changeMadeForTextfield = false;
		} else if (e.getSource() == saveAs) {
			if (checkInput() && !checkForChanges())
				fileMenuActions.saveFileAs();
			changeMadeForTextfield = false;
		} else if (e.getSource() == searchById && checkInput() && !checkForChanges() && isSomeoneToDisplay()) {
			new SearchByDialog(EmployeeDetails.this, "ID");
		} else if (e.getSource() == searchBySurname && checkInput() && !checkForChanges() && isSomeoneToDisplay()) {
			new SearchByDialog(EmployeeDetails.this, "Surname");
		} else if (e.getSource() == searchId || e.getSource() == searchByIdField)
			searchEmployeeById();
		else if (e.getSource() == searchSurname || e.getSource() == searchBySurnameField)
			searchEmployeeBySurname();
		else if (e.getSource() == saveChange && checkInput() && !checkForChanges()) {
			
		} else if (e.getSource() == cancelChange)
			cancelChange();
		else if (e.getSource() == firstItem || e.getSource() == first && checkInput() && !checkForChanges()) {
			firstLastNavigateRecord("First");
			displayRecords(currentEmployee);
		} else if (e.getSource() == prevItem || e.getSource() == previous && checkInput() && !checkForChanges()) {
			nextPrevoiusNavigateRecord("Previous");
			displayRecords(currentEmployee);
		} else if (e.getSource() == nextItem || e.getSource() == next && checkInput() && !checkForChanges()) {
			nextPrevoiusNavigateRecord("Next");
			displayRecords(currentEmployee);
		} else if (e.getSource() == lastItem || e.getSource() == last && checkInput() && !checkForChanges()) {
			firstLastNavigateRecord("Last");
			displayRecords(currentEmployee);
		} else if (e.getSource() == listAll || e.getSource() == displayAll && checkInput() && !checkForChanges() && isSomeoneToDisplay()) {
			new EmployeeSummaryDialog(getAllEmloyees());
		} else if (e.getSource() == create || e.getSource() == add && checkInput() && !checkForChanges()) {
			new AddRecordDialog(EmployeeDetails.this);
		} else if (e.getSource() == modify || e.getSource() == edit && checkInput() && !checkForChanges()) {
			editDetails();
		} else if (e.getSource() == delete || e.getSource() == deleteButton && checkInput() && !checkForChanges()) {
			deleteRecord();
		} else if (e.getSource() == searchBySurname && checkInput() && !checkForChanges()) {
			new SearchByDialog(EmployeeDetails.this, "Surname");
		}
	}

	private void createContentPane() {
		setTitle("Employee Details");
		createRandomFile();
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar());
		dialog.add(searchPanel(), "width 400:400:400, growx, pushx");
		dialog.add(navigPanel(), "width 150:150:150, wrap");
		dialog.add(buttonPanel(), "growx, pushx, span 2,wrap");
		dialog.add(detailsPanel(), "gap top 30, gap left 150, center");

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}

	private static void createAndShowGUI() {
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.createContentPane();
		frame.setSize(760, 600);
		frame.setLocation(250, 200);
		frame.setVisible(true);
	}

	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public void changedUpdate(DocumentEvent d) {
		changeMadeForTextfield = true;
		new JTextFieldLimit(20);
	}

	public void insertUpdate(DocumentEvent d) {
		changeMadeForTextfield = true;
		new JTextFieldLimit(20);
	}

	public void removeUpdate(DocumentEvent d) {
		changeMadeForTextfield = true;
		new JTextFieldLimit(20);
	}

	public void itemStateChanged(ItemEvent e) {
		changeMadeForTextfield = true;
	}

	public void windowClosing(WindowEvent e) {
		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}
