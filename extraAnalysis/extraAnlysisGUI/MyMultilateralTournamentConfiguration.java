package extraAnalysis.extraAnlysisGUI;

import java.util.List;

import genius.core.Deadline;
import genius.core.config.MultilateralTournamentConfiguration;
import genius.core.listener.DefaultListenable;
import genius.core.persistent.PersistentDataType;
import genius.core.repository.MultiPartyProtocolRepItem;
import genius.core.repository.ParticipantRepItem;
import genius.core.repository.PartyRepItem;
import genius.core.repository.ProfileRepItem;

/**
 * @author Arash Ebrahimnezhad 
 * @Email arash.ebrah@gmail.com
 *
 */
public class MyMultilateralTournamentConfiguration extends DefaultListenable<MultilateralTournamentConfiguration> {

	private ExtraAnalysisRepItem extraAnalysisItem;
	
	public MyMultilateralTournamentConfiguration( ExtraAnalysisRepItem extraAnalysisItem ) {

		this.extraAnalysisItem = extraAnalysisItem;
		
	}
	
	
	public ExtraAnalysisRepItem getExtraAnalysisItem() {
		return extraAnalysisItem;
	}

}
