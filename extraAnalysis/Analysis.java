package extraAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import extraAnalysis.extraAnlysisGUI.MyMultiTournamentModel;
import genius.core.Domain;
import genius.core.boaframework.BoaParty;
import genius.core.boaframework.OpponentModel;
import genius.core.config.MultilateralTournamentConfiguration;
import genius.core.events.SessionEndedNormallyEvent;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.ValueDiscrete;
import genius.core.parties.NegotiationParty;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.EvaluatorDiscrete;
import genius.core.utility.UtilitySpace;
import genius.core.xml.XmlWriteStream;
import genius.gui.agentrepository.AgentRepositoryUI;

/**
 * @author Arash Ebrahimnezhad 
 * @Email arash.ebrah@gmail.com
 *
 */
public class Analysis {

	private MultilateralTournamentConfiguration config;
	
	// party1 is the party witch we want to measure it 
	private static SessionEndedNormallyEvent e;
	
	private static HashMap<String, HashMap> hdata = new HashMap<String,HashMap>();
	
	public static HashMap<String, HashMap> getHdata() {
		return hdata;
	}

	private static int negotiationNumber = 0;
	
	// Map for Log
	protected Map data = new HashMap<>();
	
	private HashMap<String,Double> measurmentData;
	
	
	public Analysis() {

	}
	
	
	public Analysis(MultilateralTournamentConfiguration config) {
		this.config = config;
	}

	
	public Analysis(SessionEndedNormallyEvent e) {
		this.e = e;

	}

	
	protected NegotiationParty getParty(int index) {
		return e.getParties().get(index).getParty();
	}
	
	
	protected BoaParty getBoaParty(int index) {
		return (BoaParty)(getParty(index));
	}

	
	protected Domain getDomain()
	{
		return getRealUtilitySpace(0).getDomain();
	}
	

	protected OpponentModel getOpponentModel(int index) {
		return getBoaParty(index).getOpponentModel();
	}
	

	protected AbstractUtilitySpace getEstimatedAbstractUtilitySpace(int index) {
		return getOpponentModel(index).getOpponentUtilitySpace();
	}


	protected UtilitySpace getRealUtilitySpace(int index) {
		return e.getParties().get(index).getUtilitySpace();
	}


	protected double[] getEstimatedWeights(int index) {
		
		UtilitySpace realUtilitySpace = getRealUtilitySpace(index);
		double[] estimatedWeights = new double[realUtilitySpace.getDomain().getIssues().size()];
		
		for( Issue issue : realUtilitySpace.getDomain().getIssues())
			estimatedWeights[issue.getNumber()-1] = ((AdditiveUtilitySpace)getEstimatedAbstractUtilitySpace(index)).getWeight(issue);
		
		return estimatedWeights;
	}

	
	protected double[] getRealWeights(int index) {
		
		UtilitySpace realUtilitySpace = getRealUtilitySpace(index);
		double[] realWeights = new double[realUtilitySpace.getDomain().getIssues().size()];
		
		for( Issue issue : realUtilitySpace.getDomain().getIssues())
			realWeights[issue.getNumber()-1] = ((AdditiveUtilitySpace)getRealUtilitySpace(index)).getWeight(issue);
		
		return realWeights;
	}

	
	protected HashMap<String, HashMap<String,Double>> getRealValues(int index)
	{
		AbstractUtilitySpace abstractUtilitySpace = (AbstractUtilitySpace)getRealUtilitySpace(index);
		
		return getValues(abstractUtilitySpace);
	    
	}
	
	
	protected HashMap<String, HashMap<String,Double>> getEstimatedValues(int index)
	{
		
		AbstractUtilitySpace abstractUtilitySpace = getEstimatedAbstractUtilitySpace(index);
		
		return getValues(abstractUtilitySpace);
	    
	}
	
	
	protected HashMap<String, HashMap<String,Double>> getValues(AbstractUtilitySpace abstractUtilitySpace)
	{
		HashMap<String, HashMap<String,Double>> allValues = new HashMap<>();
		
		AdditiveUtilitySpace additiveUtilitySpace = (AdditiveUtilitySpace) abstractUtilitySpace;

		List<Issue> issues = additiveUtilitySpace.getDomain().getIssues();

		for (Issue issue : issues) {
			
			HashMap<String,Double> issueValues = new HashMap<String,Double>();
			
		    int issueNumber = issue.getNumber();

		    // Assuming that issues are discrete only
		    IssueDiscrete issueDiscrete = (IssueDiscrete) issue;
		    EvaluatorDiscrete evaluatorDiscrete = (EvaluatorDiscrete) additiveUtilitySpace.getEvaluator(issueNumber);

		    for (ValueDiscrete valueDiscrete : issueDiscrete.getValues()) {
		        issueValues.put(valueDiscrete.getValue(), (double)evaluatorDiscrete.getValue(valueDiscrete));
		    }
		    
		    allValues.put(issue.getName(), issueValues);
		}
		
		return allValues;
	}
	
	
	public void extraLogger() throws FileNotFoundException, XMLStreamException
	{

		writeExtraLog("TournomentLog",hdata);

		try {

			if ( !config.getPartyBItems().isEmpty() ) {

				// PoolA Data
				String[] poolA = getPoolA();
				
				// PoolB Data
				String[] poolB = getPoolB();
				
				// Domain's name Data
				String[] poolDomain = getPoolDomain();
				
			    
				if (poolA.length != 0 && poolB.length != 0 && poolDomain.length != 0)
					new FinalAnalysis(poolA, poolB, poolDomain).getTable1();
			}
			
		} catch(Exception e) {
			
		}
		
		
		data.clear();
		hdata.clear();
		negotiationNumber = 0;
		
	}

	
	public void writeExtraLog(String logName,HashMap<String, HashMap> data)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

		// bit strange to set up logging system here... move it?
		logName = String.format("tournament-%s-%s.log", dateFormat.format(new Date()), logName);

		File theDir = new File("extraLog");
		if (!theDir.exists()) {
		    try{
		        theDir.mkdir();
		    } 
		    catch(SecurityException se){
		    }  
		}
		try{
			OutputStream out = new FileOutputStream("extraLog/"+logName+".xml");

			XmlWriteStream xmlWriteStream = new XmlWriteStream(out, "Tournament");
			
			for (String agentName : data.keySet()) {

				xmlWriteStream.write(agentName, data.get(agentName));
			}

			xmlWriteStream.flush();
			xmlWriteStream.close();

		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	
	protected void setLogData()	{

		negotiationNumber++;
		String agentName1 = getBoaParty(0).getOpponentModel().getName().replaceAll(" ", "");
		String agentName2 = getBoaParty(1).getOpponentModel().getName().replaceAll(" ", "");
		String domainName[] = getDomain().getName().split("/");
		
		hdata.put(agentName1+"_Vs_"+agentName2+"_"+domainName[domainName.length-1]+negotiationNumber, (HashMap) data);
		
		e = null;
	}

	
	public void makeExtraLog()
			throws FileNotFoundException, XMLStreamException {

		try {
			getMeasure(0,1);
			getMeasure(1,0);
		} catch (MalformedURLException | InstantiationException
				| IllegalAccessException | ClassNotFoundException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

			
		//////////////
		setLogData();
		//////////////
		
	}
	
	
	public void getMeasure(int index1, int index2) throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException {

		File file = new File("D:/Tutorials/uni/Research/MyGenius/NegotiatorGUI/bin/extraAnalysis/");
		String AnalysisClassName = MyMultiTournamentModel.selectedAnalysisItem;
		Object obj = loadClassfile("extraAnalysis."+ AnalysisClassName.substring(0, AnalysisClassName.length() - 6),file);

		Method[] methods = obj.getClass().getDeclaredMethods();
		
		for (int i = 0; i < methods.length; i++) {
			
			measurmentData = (HashMap<String, Double>) methods[i].invoke(obj, index1,index2);
			//////////////
			setAnalysData(index1, index2,measurmentData.keySet().toArray()[0].toString());
			//////////////
		}


		
	}
		
	
	public void setAnalysData(int index1, int index2,String measerment) {
		String agentName1 = getBoaParty(index1).getOpponentModel().getName().replaceAll(" ", "");
		String agentName2 = getBoaParty(index2).getOpponentModel().getName().replaceAll(" ", "");

		data.put(measerment+"_"+agentName1+"_"+index1 , measurmentData.values().toArray()[0]);
		
	}
	
	
	private String[] getPoolA()
	{
		String[] poolA = new String[config.getPartyItems().size()] ;
		
		for(int i = 0; i < config.getPartyItems().size(); i++) {
			
			String[] s = config.getPartyItems().get(i).toString().split("BOA");
			String ss = s[3].substring(s[3].indexOf("(")+1, s[3].indexOf(")"));
			String[] opponentModelName = ss.split("\\."); 
			poolA[i] = opponentModelName[3].replace(" ", "");
			
		}
		return poolA;
	}
	
	
	private String[] getPoolB()
	{
		String[] poolB = new String[config.getPartyBItems().size()]; 
		
		for(int i = 0; i < config.getPartyBItems().size(); i++) {
			
			String[] s = config.getPartyBItems().get(i).toString().split("BOA");
			String ss = s[3].substring(s[3].indexOf("(")+1, s[3].indexOf(")"));
			String[] opponentModelName = ss.split("\\."); 
			poolB[i] = opponentModelName[3].replace(" ", "");
			
		}
		return poolB;
	}
	
	private String[] getPoolDomain()
	{
		ArrayList<String> l = new ArrayList<String>();

	    Iterator it = hdata.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();

	        String s[] = pair.getKey().toString().split("_");
	        
	        String ss = "";
	        for(int i = 3; i < s.length; i++)
	        	ss += s[i];

	        ss = ss.subSequence(0, ss.lastIndexOf(".")).toString()+".xml";

	        if( l.isEmpty() || l.indexOf(ss) == -1 )
	        	l.add(ss);

	    }
		
		
	    String[] poolDomain = new String[l.size()] ;
	    for(int i=0; i< l.size(); i++)
	    	poolDomain[i] = l.get(i);
	    
	    return poolDomain;
	}
	
	
	/**
	 * Method of loadClassfile is for load class.
	 * 
	 * @author dmytro
	 */
	
	/**
	 * Try to load an object with given classnamem from a given packagedir
	 * 
	 * @param classname
	 *            the exact class name, eg "examplepackage.example"
	 * @param packagedir
	 *            the root directory of the classes to be loaded. If you add the
	 *            given classname to it, you should end up at the correct
	 *            location for the class file. Eg,
	 *            "/Volumes/Users/wouter/Desktop/genius/".
	 * @return the loaded class object.
	 * @throws MalformedURLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private Object loadClassfile(String classname, File packagedir)
			throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		try {
			java.lang.ClassLoader loader = AgentRepositoryUI.class.getClassLoader();
			URLClassLoader urlLoader = new URLClassLoader(new URL[] { packagedir.toURI().toURL() }, loader);
			Class<?> theclass;
			theclass = urlLoader.loadClass(classname);
			return (Object) theclass.newInstance();
		} catch (ClassNotFoundException e) {
			// improve on the standard error message...
			throw new ClassNotFoundException(
					"AnaLysis Class, " + classname + " is not available in directory '" + packagedir + "'", e);
		}

	}
	
	
}

