package welfareSM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
	private static String heartRate = "heartRate";
	private static String breathRate = "breathRate";
	private static String skinTemp = "skinTemp";
	private static String coreTemp = "coreTemp";
	
	private static double epsilon = 0.00000001;
	
	public static boolean Equals(double a, double b){
		if (Math.abs(a-b) < epsilon)
			return true;
		return false;
	}
	
	public static PhysioMeasure_4Tuple parseJSON(JSONArray lastMinuteOfData){
		PhysioMeasure_4Tuple physioMeasures = new PhysioMeasure_4Tuple();
		int len = lastMinuteOfData.length();
		if (len == 0) return null;
		
		//only need most recent heart & breath rate, as they are calculated infrequently
		JSONObject lastEntry = null;
		try {
			lastEntry = lastMinuteOfData.getJSONObject(len - 1);
		}
		catch (JSONException e){
			//heart and breath rate were already 0 on init
		}
		
		try{ physioMeasures.heartRate = lastEntry.getInt(heartRate); }
		catch (JSONException e) { ; }
		
		try { physioMeasures.breathRate = lastEntry.getInt(breathRate);}
		catch (JSONException e){ ; }
		
		//however need to average temperature values over the array
		JSONObject curr = null;
		float skinSum = 0, coreSum = 0;
		int numValidSkinValues = 0, numValidCoreValues = 0;
		
		for (int i = 0; i < len; i++ )
		{
			try { curr = lastMinuteOfData.getJSONObject(i); }
			catch (JSONException e){ continue; } 
			try{
				skinSum += curr.getInt(skinTemp);
				numValidSkinValues++;
			}
			catch (JSONException e){
				//don't count value
			}
			try {
				coreSum += curr.getInt(coreTemp);
				numValidCoreValues++;
			} catch( JSONException e){
				//don't count value
			}
		}
		
		if (coreSum != 0 && numValidCoreValues != 0)
			physioMeasures.coreTemp = coreSum/numValidCoreValues;
		
		if (skinSum != 0 && numValidSkinValues != 0)
			physioMeasures.skinTemp = skinSum/numValidSkinValues;
		
		return physioMeasures;
	}
}
