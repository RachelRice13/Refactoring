import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuAndNavigation implements ActionListener {
	EmployeeDetails parent;
	FileMenuActions fileMenuActions;
	JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById, searchBySurname, listAll, closeApp;
		
	public MenuAndNavigation(EmployeeDetails parent) {
		this.parent = parent;
		fileMenuActions = new FileMenuActions(parent);
	}
	
	public JMenuBar menuBar() {
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

		closeMenu.add(closeApp = addMenuItem("Close", KeyEvent.VK_F4));

		return menuBar;
	}
		
	private JMenuItem addMenuItem(String title, int keyEvent) {
		JMenuItem menuItem = new JMenuItem(title);
		menuItem.addActionListener(this);
		menuItem.setMnemonic(keyEvent);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.CTRL_MASK));
		return menuItem;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == open && this.parent.checkInput() && !this.parent.checkForChanges()) {
			fileMenuActions.openFile();
		} else if (e.getSource() == save) {
			if (this.parent.checkInput() && !this.parent.checkForChanges())
				fileMenuActions.saveFile();
			this.parent.changeMadeForTextfield = false;
		} else if (e.getSource() == saveAs) {
			if (this.parent.checkInput() && !this.parent.checkForChanges())
				fileMenuActions.saveFileAs();
			this.parent.changeMadeForTextfield = false;
		} else if (e.getSource() == closeApp && this.parent.checkInput() && !this.parent.checkForChanges()) {
			this.parent.exitApp();
		} else if (e.getSource() == create && this.parent.checkInput() && !this.parent.checkForChanges()) 
			new AddRecordDialog(parent);
		 else if (e.getSource() == modify && this.parent.checkInput() && !this.parent.checkForChanges()) 
			 this.parent.editDetails();
		 else if (e.getSource() == delete && this.parent.checkInput() && !this.parent.checkForChanges()) 
			 this.parent.deleteRecord();
		 else if (e.getSource() == firstItem  && this.parent.checkInput() && !this.parent.checkForChanges()) {
			 this.parent.firstLastNavigateRecord("First");
			 this.parent.displayRecords(this.parent.currentEmployee);
		} else if (e.getSource() == prevItem && this.parent.checkInput() && !this.parent.checkForChanges()) {
			this.parent.nextPrevoiusNavigateRecord("Previous");
			this.parent.displayRecords(this.parent.currentEmployee);
		} else if (e.getSource() == nextItem && this.parent.checkInput() && !this.parent.checkForChanges()) {
			this.parent.nextPrevoiusNavigateRecord("Next");
			this.parent.displayRecords(this.parent.currentEmployee);
		} else if (e.getSource() == lastItem && this.parent.checkInput() && !this.parent.checkForChanges()) {
			this.parent.firstLastNavigateRecord("Last");
			this.parent.displayRecords(this.parent.currentEmployee);
		} else if (e.getSource() == searchById && this.parent.checkInput() && !this.parent.checkForChanges() && this.parent.isSomeoneToDisplay()) {
			new SearchByDialog(parent, "ID");
		} else if (e.getSource() == searchBySurname && this.parent.checkInput() && !this.parent.checkForChanges() && this.parent.isSomeoneToDisplay()) {
			new SearchByDialog(parent, "Surname");
		} else if (e.getSource() == listAll  && this.parent.checkInput() && !this.parent.checkForChanges() && this.parent.isSomeoneToDisplay()) {
			new EmployeeSummaryDialog(this.parent.getAllEmloyees());
		}
	}
	
}
