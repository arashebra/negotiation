package extraAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import extraAnalysis.extraAnlysisGUI.MyMultiTournamentModel;
import genius.core.Bid;
import genius.core.analysis.MultilateralAnalysis;
import genius.core.boaframework.BoaParty;
import genius.core.events.BrokenPartyException;
import genius.core.events.NegotiationEvent;
import genius.core.events.SessionEndedNormallyEvent;
import genius.core.events.SessionFailedEvent;
import genius.core.logging.AgentStatistics;
import genius.core.logging.AgentsStatistics;
import genius.core.logging.StatisticsLogger;
import genius.core.parties.NegotiationPartyInternal;
import genius.core.session.Participant;
import genius.core.utility.UtilitySpace;
import genius.core.xml.XmlWriteStream;
/**
 * @author Arash Ebrahimnezhad 
 * @Email arash.ebrah@gmail.com
 *
 */
public class MyStatisticsLogger extends StatisticsLogger {

	protected AgentsStatistics myAgentStats = new AgentsStatistics(new ArrayList<AgentStatistics>());
	
	public MyStatisticsLogger(OutputStream out)
			throws FileNotFoundException, XMLStreamException {
		super(out);
		// TODO Auto-generated constructor stub
		
		
		
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void notifyChange(NegotiationEvent e) {

		try {

			if (e instanceof SessionEndedNormallyEvent) {
				SessionEndedNormallyEvent e1 = (SessionEndedNormallyEvent) e;
				 
				
				
				MultilateralAnalysis analysis = e1.getAnalysis();
				Bid agreedbid = analysis.getAgreement();
				double nashdist = analysis.getDistanceToNash();
				double welfare = analysis.getSocialWelfare();
				double paretoDist = analysis.getDistanceToPareto();
				
				

				/////////////////////////////////////////////
				/////////////////////////////////////////////
				if ( !MyMultiTournamentModel.selectedAnalysisItem.equals("Non") ) // It Must Be Changed
					extraAnalysis(e1);
				/////////////////////////////////////////////
				/////////////////////////////////////////////
				
				
				for (NegotiationPartyInternal party : e1.getParties()) {
					String name = party.getParty().getClass().getCanonicalName();
					if (agreedbid == null) {
						myAgentStats =  myAgentStats.withStatistics(name, 0, 0, nashdist, welfare, paretoDist);
					} else {
						myAgentStats =  myAgentStats.withStatistics(name, party.getUtility(agreedbid),
								party.getUtilityWithDiscount(agreedbid), nashdist, welfare, paretoDist);
					}
				}
			} else if (e instanceof SessionFailedEvent) {
				BrokenPartyException e1 = ((SessionFailedEvent) e).getException();

				for (Participant party : ((SessionFailedEvent) e).getException().getConfiguration().getParties()) {
					Double reservationvalue = 0d;
					try {
						UtilitySpace utilspace = party.getProfile().create();
						reservationvalue = utilspace.getReservationValue();
					} catch (Exception ex) {
						System.out.println("Failed to read profile of " + party + ". using 0");
					}

					agentStats = agentStats.withStatistics(party.getStrategy().getClassDescriptor(), reservationvalue,
							reservationvalue, 1d, 0d, 1d);
				}
			}
			// other events are only giving details we dont need here.

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
	private void extraAnalysis(SessionEndedNormallyEvent e1)
	{


		//if your Agent use BOA Framework
		try{
			// Add Your Code For Extra Analysis
			Analysis extra1 = new Analysis(e1);
			extra1.makeExtraLog();

			
			
		}catch (Exception e) {
			System.err.println("Agent does not use BOA framework!!!");
		}
		

	}
	

}
