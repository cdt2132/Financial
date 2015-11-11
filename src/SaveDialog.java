/**
 * SaveDialog creates a pop-up prompting the user to choose a directory 
 * where the CSV report will be saved
 * 
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build October 2015
 */

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SaveDialog extends JFrame {

	private JButton buttonBrowse;
	private String fileName; 

	/** Constructor */
	public SaveDialog() {
		super("Save As");
		setLayout(new FlowLayout());
		
		fileName = showSaveFileDialog();
		
	}

	
	/** Creates window with directories listed for user to choose from  */
	private String showSaveFileDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Choose a Directory");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		String fileName = ""; 
		int userSelection = fileChooser.showSaveDialog(this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			fileName = fileToSave.getAbsolutePath(); 
		}
		return fileName; 
	}
	/** encapsulates the fileName from showSaveFileDialog */
	public String getName(){
		return fileName;
	}
}