import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileMenuActions {
	EmployeeDetails parent;
	
	public FileMenuActions(EmployeeDetails parent) {
		this.parent = parent;
	}

	public void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");

		fc.setFileFilter(this.parent.datfilter);
		File newFile; 
		
		if (this.parent.currentFile.length() != 0 || this.parent.changeMadeForTextfield) {
			int returnVal = JOptionPane.showOptionDialog(this.parent.frame, "Do you want to save changes?", "Save",JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();
			} 
		} 

		int returnVal = fc.showOpenDialog(parent);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();

			if (this.parent.currentFile.getName().equals(this.parent.generatedFileName))
				this.parent.currentFile.delete();
			this.parent.currentFile = newFile;
			this.parent.application.openReadFile(this.parent.currentFile.getAbsolutePath());
			this.parent.firstLastNavigateRecord("First");
			this.parent.displayRecords(this.parent.currentEmployee);
			this.parent.application.closeReadFile();
		} 
	}

	public void saveFile() {
		if (this.parent.currentFile.getName().equals(this.parent.generatedFileName))
			saveFileAs();
		else {
			if (this.parent.changeMadeForTextfield) {
				int returnVal = JOptionPane.showOptionDialog(this.parent.frame, "Do you want to save changes?", "Save", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				
				if (returnVal == JOptionPane.YES_OPTION) {
					if (!this.parent.idField.getText().equals("")) {
						this.parent.application.openWriteFile(this.parent.currentFile.getAbsolutePath());
						this.parent.currentEmployee = getChangedDetails();
						this.parent.application.changeRecords(this.parent.currentEmployee, this.parent.currentByteStart);
						this.parent.application.closeWriteFile();// close file for writing
					} 
				} 
			} 
			this.parent.displayRecords(this.parent.currentEmployee);
			this.parent.setEnabled(false);
		} 
	}
	
	public void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");
		fc.setFileFilter(this.parent.datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			if (!checkFileName(newFile)) {
				newFile = new File(newFile.getAbsolutePath() + ".dat");
				this.parent.application.createFile(newFile.getAbsolutePath());
			} else
				this.parent.application.createFile(newFile.getAbsolutePath());

			try {
				Files.copy(this.parent.currentFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
				if (this.parent.currentFile.getName().equals(this.parent.generatedFileName))
					this.parent.currentFile.delete();
				this.parent.currentFile = newFile;
			} catch (IOException e) {}
		} 
		this.parent.changesMadeForFile = false;
	}
	
	private Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;
		
		if (((String) this.parent.fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(this.parent.idField.getText()), this.parent.ppsField.getText().toUpperCase(), this.parent.surnameField.getText().toUpperCase(), this.parent.firstNameField.getText().toUpperCase(), this.parent.genderCombo.getSelectedItem().toString().charAt(0), this.parent.departmentCombo.getSelectedItem().toString(), Double.parseDouble(this.parent.salaryField.getText()), fullTime);
		return theEmployee;
	}
	
	private boolean checkFileName(File fileName) {
		boolean checkFile = false;
		int length = fileName.toString().length();

		if (fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't')
			checkFile = true;
		return checkFile;
	}


}
