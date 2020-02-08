package extraAnalysis.extraAnlysisGUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
/**
 * @author Arash Ebrahimnezhad 
 * @Email arash.ebrah@gmail.com
 *
 */


public class extraAnalysisPanel extends JPanel {

	
    JTable table;
    
	public extraAnalysisPanel() {

		setLayout(new BorderLayout());
		table = new JTable(new DefaultTableModel(new Object[]{"Class Name", "Path"}, 0));
		JScrollPane scrollpane = new JScrollPane(table);
		add(scrollpane, BorderLayout.CENTER);

		PopClickListener popuplistener = new PopClickListener(table);
		scrollpane.addMouseListener(popuplistener);
		table.addMouseListener(popuplistener);
		
		
		try (Stream<Path> paths = Files.walk(Paths.get("extraAnalysisClass"))) {
			
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				paths.filter(Files::isRegularFile).forEach((n) -> model.addRow(new Object[]{n.getFileName(), n}));
			    
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		
		
	}
	
	
	/**
	 * Right mouse click menu.
	 *
	 */
	class PopClickListener extends MouseAdapter {
		private JTable table;

		public PopClickListener(JTable table) {
			this.table = table;
		}

		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		private void doPop(MouseEvent e) {
			PopUp menu = new PopUp(table);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	@SuppressWarnings("serial")
	class PopUp extends JPopupMenu {

		public PopUp(JTable table) {
			add(new JMenuItem(new AddAction(table)));
			//add(new JMenuItem(new EditAction(table)));
			add(new JMenuItem(new RemoveAction(table)));
		}
	}
	
	/**
	 * Action to add an item to the boaparty repo
	 *
	 */
	@SuppressWarnings("serial")
	class AddAction extends AbstractAction {

		
		public AddAction(JTable table) {
			super("add item");
			//putValue(SHORT_DESCRIPTION, "add a new boa item");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setDialogTitle("Select an .class");
			jfc.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".class", "class");
			jfc.addChoosableFileFilter(filter);

			int returnValue = jfc.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {

				String path = jfc.getSelectedFile().getPath();
				String[] pathArray = path.split(Pattern.quote(File.separator)); 

				Path source = Paths.get(path);
				
				// check existence of directory
				File theDir = new File("extraAnalysisClass");
				if (!theDir.exists()) {
				    try{
				        theDir.mkdir();
				    } 
				    catch(SecurityException se){
				    }  
				}
				
				Path target = Paths.get("extraAnalysisClass\\"+pathArray[pathArray.length-1]);
				try {
					Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
					model.addRow(new Object[]{pathArray[pathArray.length-1], path});
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					

				//System.out.println(jfc.getSelectedFile().getPath());
				
			}

		}
	}
	
	/**
	 * Action to remove an item to the boaparty repo
	 *
	 */
	@SuppressWarnings("serial")
	class RemoveAction extends AbstractAction {

		DefaultTableModel model = (DefaultTableModel) table.getModel();


		public RemoveAction(JTable table) {
			super("remove item");

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedrow = table.getSelectedRow();
			if (selectedrow == -1)
				return;
			
			File file = new File("extraAnalysisClass\\"+model.getValueAt(selectedrow, 0).toString());
			
			if(file.delete())
				model.removeRow(selectedrow);
		}

	}

	
}
