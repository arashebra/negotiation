package extraAnalysis.extraAnlysisGUI;

import genius.core.repository.RepItem;
/**
 * @author Arash Ebrahimnezhad 
 * @Email arash.ebrah@gmail.com
 *
 */
public class ExtraAnalysisRepItem implements RepItem {

	private String extraAnalysisClassName;
	
	

	public ExtraAnalysisRepItem(String extraAnalysisClassName) {
		super();
		this.extraAnalysisClassName = extraAnalysisClassName;
	}



	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return extraAnalysisClassName;
	}

}
