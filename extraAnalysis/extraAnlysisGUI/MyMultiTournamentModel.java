package extraAnalysis.extraAnlysisGUI;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;

import genius.core.config.MultilateralTournamentConfiguration;
import genius.core.listener.DefaultListenable;
import genius.core.listener.Listener;
import genius.core.repository.ParticipantRepItem;
import genius.core.repository.ProfileRepItem;
import genius.gui.panels.SingleSelectionModel;
import genius.gui.tournament.MultiTournamentModel;
/**
 * @author Arash Ebrahimnezhad 
 * @Email arash.ebrah@gmail.com
 *
 */
public class MyMultiTournamentModel extends DefaultListenable<MyMultilateralTournamentConfiguration> {
	
	
	public static String selectedAnalysisItem = "Non";// It Must Be Changed later!!!
	
	private final SingleSelectionModel<ExtraAnalysisRepItem> extraAnalysisDataTypeModel;
	
	public MyMultiTournamentModel()
	{

		ArrayList l = new ArrayList<>();
		l.add("Non");
		try (Stream<Path> paths = Files.walk(Paths.get("extraAnalysisClass"))) {
			paths.filter(Files::isRegularFile).forEach((n) -> l.add(n.getFileName()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		extraAnalysisDataTypeModel = new SingleSelectionModel<ExtraAnalysisRepItem>( l );
		
		addConstraints();
		
	}

	public SingleSelectionModel<ExtraAnalysisRepItem> getExtraAnalysisDataTypeModel() {

		return extraAnalysisDataTypeModel;
	}


	/**
	 * connecting listeners that check the constraints between the fields in the
	 * model
	 */
	private void addConstraints() {
		// protocol has major impact on the submodels
		extraAnalysisDataTypeModel.addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
			}

			@Override
			public void contentsChanged(ListDataEvent e) {

				selectedAnalysisItem = extraAnalysisDataTypeModel.getSelectedItem().toString();
				
			}
		});



	}





	
}
