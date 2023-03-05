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

public class EmployeeDetails extends JFrame implements ItemListener, DocumentListener, WindowListener {
	private FileMenuActions fileMenuActions = new FileMenuActions(EmployeeDetails.this);
	private PageLayoutSetup pageLayoutSetup = new PageLayoutSetup(EmployeeDetails.this);
	private MenuAndNavigation menuAndNavigation = new MenuAndNavigation(EmployeeDetails.this);
	private static final DecimalFormat inactiveCurrencyFieldFormat = new DecimalFormat("\u20ac ###,###,##0.00");
	private static final DecimalFormat activeCurrencyFieldFormat = new DecimalFormat("0.00");
	public long currentByteStart = 0;
	public RandomFile application = new RandomFile();
	public FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
	public File currentFile;
	public boolean changeMadeForTextfield = false;
	boolean changesMadeForFile = false;
	public JButton saveChange, cancelChange;
	public JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	public JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	public static EmployeeDetails frame = new EmployeeDetails();
	Font font1 = new Font("SansSerif", Font.BOLD, 16);
	String generatedFileName;
	Employee currentEmployee;
	String[] gender = {"", "M", "F"};
	String[] department = { "", "Administration", "Production", "Transport", "Management" };
	String[] fullTime = { "", "Yes", "No" };

	private JPanel detailsPanel() {
		JPanel empDetails = pageLayoutSetup.detailsPanel();
		setFontsAndBAckgrounds(empDetails);
		return empDetails;
	}
	
	private void setFontsAndBAckgrounds(JPanel empDetails) {
		JTextField field;
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
	}
	
	public void displayRecords(Employee thisEmployee) {
		int countGender = 0;
		int countDep = 0;
		boolean found = false;

		pageLayoutSetup.searchByIdField.setText("");
		pageLayoutSetup.searchBySurnameField.setText("");

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

	public void deleteRecord() {
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

	public Vector<Object> getAllEmloyees() {
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

	public void editDetails() {
		if (isSomeoneToDisplay()) {
			salaryField.setText(activeCurrencyFieldFormat.format(currentEmployee.getSalary()));
			changeMadeForTextfield = false;
			setEnabled(true);
		} 
	}

	public boolean isSomeoneToDisplay() {
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

	public boolean checkForChanges() {
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

	public boolean checkInput() {
		boolean valid = true;
		
		if (ppsField.isEditable() && ppsField.getText().trim().isEmpty() && correctPps(ppsField.getText().trim(), currentByteStart)) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
		
		validateTextFieldInputs(valid, surnameField);
		validateTextFieldInputs(valid, firstNameField);
		validateJComboBoxInputs(valid, genderCombo);
		validateJComboBoxInputs(valid, departmentCombo);
		validateJComboBoxInputs(valid, fullTimeCombo);
				
		try {
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
		
		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");

		return valid;
	}

	private void validateTextFieldInputs(boolean valid, JTextField textfield) {
		if (textfield.isEditable() && textfield.getText().trim().isEmpty()) {
			textfield.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
	}
	
	private void validateJComboBoxInputs(boolean valid, JComboBox<String> comboBox) {
		if (comboBox.getSelectedIndex() == 0 && comboBox.isEnabled()) {
			comboBox.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
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
		pageLayoutSetup.searchByIdField.setEnabled(search);
		pageLayoutSetup.searchBySurnameField.setEnabled(search);
		pageLayoutSetup.searchId.setEnabled(search);
		pageLayoutSetup.searchSurname.setEnabled(search);
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

	public void exitApp() {
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

	private void createContentPane() {
		setTitle("Employee Details");
		createRandomFile();
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuAndNavigation.menuBar());
		dialog.add(pageLayoutSetup.searchPanel(), "width 400:400:400, growx, pushx");
		dialog.add(pageLayoutSetup.navigPanel(), "width 150:150:150, wrap");
		dialog.add(pageLayoutSetup.buttonPanel(), "growx, pushx, span 2,wrap");
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
