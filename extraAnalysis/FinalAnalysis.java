package extraAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Arash Ebrahimnezhad 
 * @Email arash.ebrah@gmail.com
 *
 */
public class FinalAnalysis extends Analysis {

	// Columns of Table
	private String[] poolA;
	
	// Rows of Table
	private String[] poolB;
	
	// Domains 
	private String[] poolDomain;

	// Table1 all Data
	private ArrayList< HashMap<String, HashMap> > arrHdataTable1 = new ArrayList<>();
	
	// Table2 Data
	private ArrayList< HashMap<String, HashMap> > arrHdataTable2 = new ArrayList<>();
	
	
	public FinalAnalysis(String[] poolA, String[] poolB, String[] poolDomain) {
		super();
		this.poolA = poolA;
		this.poolB = poolB;
		this.poolDomain = poolDomain;

	}

	/*
	 * کنار هم چیدن عناصر لازم برای جدول اول
	 */
	public void getTable1()
	{
		
		for(String col : poolA) {
			for(String row : poolB) {
				
				HashMap<String, HashMap> hdataTable1 = new HashMap<String,HashMap>();
				for (Entry<String, HashMap> entry : getHdata().entrySet())  
					if( getColRowName(entry.getKey(),0,2).equals(col+row) )
						hdataTable1.put(entry.getKey(),entry.getValue());
				
				
				arrHdataTable1.add(hdataTable1);
				//hdataTable1.clear();
			}
		}
		calTable1(arrHdataTable1);
	}
	
	/*
	 * کنار هم چیدن عناصر لازم برای جدول دوم
	 */
	public void getTable2()
	{
		
		for(String col : poolA) {
			for (String row : poolDomain) {
				HashMap<String, HashMap> hdataTable2 = new HashMap<String,HashMap>();
				for (Entry<String, HashMap> entry : getHdata().entrySet())  
					if( getColRowName(entry.getKey(),0,3).equals(col+row.split("_")[0]) )
						hdataTable2.put(entry.getKey(),entry.getValue());
				arrHdataTable2.add(hdataTable2);
				hdataTable2.clear();
			}
		}
		
	}
	
	
	/*
	 * انجام محاسبات برای ساخت نهایی جدول اول
	 * مجموع را برای ما بدست میاورد
	 */
	public void calTable1(ArrayList< HashMap<String, HashMap> > arrHdataTable1)
	{
		
		HashMap<String, HashMap> hmFinal = new HashMap<>();
		
		double temp = 0;
		
		for (HashMap<String, HashMap> h : arrHdataTable1) {
			HashMap<String,Double> hm = new HashMap<>();
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!=>"+h.size());
			for (Entry<String, HashMap> entry : h.entrySet()) { 
	            //System.out.println("Key = " + entry.getKey()+"==>"+entry.getValue()+"="+entry.getValue());
	            String[] s = entry.getKey().split("_"); 
	            for (Object e2 : ((HashMap)entry.getValue()).entrySet()) {
	            	if( isContain( ((String)((Map.Entry)e2).getKey()).split("_")[1], poolA ) ){
	            		//System.out.println("               "+((Map.Entry)e2).getKey() +"=>"+((Map.Entry)e2).getValue());
	            		String name = s[0]+"_"+s[2]+"_"+((Map.Entry)e2).getKey().toString().split("_")[0];
	            		if(hm.size()<2){
	            			hm.put(name, (double)((Map.Entry)e2).getValue());
	            		} else {
	            			hm.put(name, (double)hm.get(name)+((double)((Map.Entry)e2).getValue()));
	            		}
	            	}
	            }
	            String[] finalName = entry.getKey().split("_");
	            hmFinal.put(finalName[0]+"_"+finalName[2], hm);
			}

            
		}
		//System.out.println("@@@@@@@@=>"+hmFinal);
		writeExtraLog("Table1",hmFinal);
	}
	
	
	
	
	
	
	
	private String getColRowName(String k, int index1, int index2)
	{
		String[] s = k.split("_");
		return s[index1]+s[index2];
	}
	
	
	private boolean isContain(String s, String[] SS) {
		for (String string : SS) 
			if(s.equals(string))
				return true;
		
		return false;
	}
	
}
