package extraAnalysis;

import java.util.HashMap;

/**
 * @author Arash Ebrahimnezhad 
 * @Email arash.ebrah@gmail.com
 *
 */
public class MyAnalysis extends Analysis {

	
	public HashMap<String,Double> getRMS(int index1, int index2) {
		
		HashMap<String,Double> hm = new HashMap<String,Double>();
		
		double[] realWeights = getRealWeights(index1);
		double[] estimatedWeights = getEstimatedWeights(index2);
		
		double temp1 = 0;
		for (int i = 0; i < estimatedWeights.length; i++) {
			double temp2 = realWeights[i]-estimatedWeights[i];
			temp1 += temp2*temp2;
		}
		hm.put("RMS", (double)(Math.sqrt(temp1))/(estimatedWeights.length));

		return hm;
	}

	
	public HashMap<String,Double> getTest(int index1, int index2) {
		HashMap<String,Double> hm = new HashMap<String,Double>();
		hm.put("Test", 0.3);
		
		return hm;
	}
	
}
