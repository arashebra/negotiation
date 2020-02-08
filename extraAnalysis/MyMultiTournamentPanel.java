package extraAnalysis;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import extraAnalysis.extraAnlysisGUI.MyMultiTournamentModel;
import extraAnalysis.extraAnlysisGUI.MyMultiTournamentSettingsPanel;
import genius.core.config.MultilateralTournamentConfiguration;
import genius.core.events.NegotiationEvent;
import genius.core.events.TournamentEndedEvent;
import genius.core.exceptions.InstantiateException;
import genius.core.listener.Listener;
import genius.core.logging.XmlLogger;
import genius.core.session.TournamentManager;
import genius.gui.negosession.MultiPartyDataModel;
import genius.gui.progress.MultiPartyTournamentProgressUI;
import genius.gui.progress.MultipartyNegoEventLogger;
import genius.gui.tournament.MultiTournamentModel;
import genius.gui.tournament.MultiTournamentPanel;
/**
 * @author Arash Ebrahimnezhad 
 * @Email arash.ebrah@gmail.com
 *
 */
public class MyMultiTournamentPanel extends MultiTournamentPanel {


	
	private static final String LOGDIR = "log";
	private TournamentManager manager;
	private XmlLogger xmlLogger;
	private MultipartyNegoEventLogger myLogger;
	private MultiPartyDataModel dataModel;
	private MyStatisticsLogger statisticsLogger;
	
	private MultiTournamentModel model; 

	
	public MyMultiTournamentPanel() {
		setLayout(new BorderLayout());
		model = new MultiTournamentModel();
		add(new MyMultiTournamentSettingsPanel(model), BorderLayout.CENTER);

		model.addListener(new Listener<MultilateralTournamentConfiguration>() {
			@Override
			public void notifyChange(MultilateralTournamentConfiguration config) {
				// called by model when the model is ready for run.
				try {
					runTournament(config);
				} catch (XMLStreamException | IOException | InstantiateException e) {
					JOptionPane.showMessageDialog(MyMultiTournamentPanel.this, "Failed to run tournament:" + e);
					e.printStackTrace();

				}
			}
		});


		
		
	}

	/**
	 * Start a new tournament and log the results to panels and files. Replaces
	 * our panel with MultiPartyTournamentProgressUI.
	 * 
	 * @param config
	 *            a {@link MultilateralTournamentConfiguration}
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws InstantiateException
	 */
	private void runTournament(MultilateralTournamentConfiguration config)
			throws XMLStreamException, IOException, InstantiateException {

		manager = new TournamentManager(config);

		removeAll();
		// init data model, GUI, logger.
		int numPartiesWithMediator = config.getNumPartiesPerSession();
		if (config.getProtocolItem().getHasMediator()) {
			numPartiesWithMediator++;
		}
		dataModel = new MultiPartyDataModel(numPartiesWithMediator);

		MultiPartyTournamentProgressUI progressUI = new MultiPartyTournamentProgressUI(dataModel);
		add(progressUI, BorderLayout.CENTER);

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String logName = config.getProfileItems().get(0).getDomain().getName();
		// bit strange to set up logging system here... move it?
		logName = String.format("log/tournament-%s-%s.log", dateFormat.format(new Date()), logName);
		new File(LOGDIR).mkdir();
		xmlLogger = new XmlLogger(new FileOutputStream(logName + ".xml"), "Tournament");
		statisticsLogger = new MyStatisticsLogger(new FileOutputStream(logName + "Stats.xml"));

		myLogger = new MultipartyNegoEventLogger(logName, config.getNumPartiesPerSession(), dataModel);
		dataModel.addTableModelListener(myLogger);

		manager.addListener(progressUI);
		manager.addListener(dataModel);
		manager.addListener(xmlLogger);
		manager.addListener(statisticsLogger);
		// manager.addListener(new ConsoleLogger()); //only works combined when
		// you disable TournamentManager useConsoleOut(false);

		
		manager.addListener(new Listener<NegotiationEvent>() {
			@Override
			public void notifyChange(NegotiationEvent e) {
				if (e instanceof TournamentEndedEvent) {
					
					try {
						
						///////////////////////////////////
						//System.out.println("===>"+MyModel.getExtraAnalysisDataTypeModel().getSelectedItem());
				
						if ( !MyMultiTournamentModel.selectedAnalysisItem.equals("Non") ) // It Must Be Changed
							new Analysis(config).extraLogger();
						
					} catch (FileNotFoundException | XMLStreamException er) {
						// TODO Auto-generated catch block
						er.printStackTrace();
					}
					
					finishTournament();
				}
			}

		});

		manager.start(); // runs the manager thread async
	}

	private void finishTournament() {
		
		if (myLogger != null) {
			if (dataModel != null)
				dataModel.removeTableModelListener(myLogger);
			myLogger.close();
		}
		if (xmlLogger != null) {
			if (manager != null)
				manager.removeListener(xmlLogger);
			try {
				xmlLogger.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (statisticsLogger != null) {
			statisticsLogger.close();
		}

		
	}

	/**
	 * simple stub to run this stand-alone (for testing).
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame gui = new JFrame();
		gui.setLayout(new BorderLayout());
		gui.getContentPane().add(new MultiTournamentPanel(), BorderLayout.CENTER);
		gui.pack();
		gui.setVisible(true);
	}
	
	
}
