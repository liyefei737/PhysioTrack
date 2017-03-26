package sleepS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static sleepS.SleepState.*;

public class SleepAlgo {	
		
	public static DateStatePair CalculateSleepStatus(JSONArray last7MinutesData){
		Map<String,Float> accCollection = processJSONArray(last7MinutesData);

		if (accCollection.size() < 7){
			return new DateStatePair(null, NOT_ENOUGH_INFO);
		}
		
		//take magnitude of acc at each time epoch
		
		List<Float> accMagnitude = new ArrayList<Float>();
		int i = 0;
		String dateN = null;
		for (Map.Entry<String, Float> entry : accCollection.entrySet()) {
			accMagnitude.add(i,entry.getValue());
			if (i == 4)
				dateN = entry.getKey();
			i++;
		}
		
		float modAcc = (float) 0.00001*(404 * accMagnitude.get(0)
                        + 598 * accMagnitude.get(1)
                        + 326 * accMagnitude.get(2)
                        + 441 * accMagnitude.get(3)
                        + 1408 * accMagnitude.get(4)
                        + 508 * accMagnitude.get(5)
                        + 350 * accMagnitude.get(6));
        if (modAcc < 1.0)
        	return new DateStatePair(dateN, SLEEP);
        else
        	return new DateStatePair(dateN, WAKE);

    }

	public static Map<String,Float> processJSONArray(JSONArray last7MinutesData){
		if (last7MinutesData == null)
			return null;
		
		Map<String, Float> accCollection = new HashMap<String,Float>();
		String strData = "";
		int j = 0;
		JSONObject jData;
		try {
			strData = last7MinutesData.getString(0);
			jData = new JSONObject(strData);
		
		} catch (JSONException e) {
			//
		}
		int len = last7MinutesData.length();
		for(int i = 0; i < len;i++){
			try {
				strData = last7MinutesData.getString(i);
				jData = new JSONObject(strData);
				String sDateTime  = jData.getString("_id");
				accCollection.put(sDateTime, Float.valueOf(jData.getString("accSum")));
			} catch (JSONException e) {
				
			}
			
		}
		
		return accCollection;
	}
	
	public static JSONArray RescoreSleep(JSONArray scoredData){
		//Rescore epochs based on webster's rules in Cole-Kripke
		
		//(a) After at least 4 minutes scored as wake, the next 1 minute scored as sleep is rescored wake; 
		//(b) after at least 10 minutes scored as wake, the next 3 minutes scored as sleep are rescored wake; 
		//(c) after at least 15 minutes scored as wake, the next 4 minutes scored as sleep are rescored wake; 
		//(d) 6 minutes or less scored as sleep surrounded by at least 10 minutes (before and after) scored as wake are rescored wake
		
		if (scoredData == null || scoredData.length() < 5){
			return null; // can't apply any rules 
		}
		
		List<JSONArray> sleepStateBins = new ArrayList<JSONArray>();
		
		int dataLen = scoredData.length();
		JSONObject jData = null;
		SleepState currBin = WAKE;
		try{
			jData = scoredData.getJSONObject(0);
			try{
				currBin = (SleepState)jData.get("sleep");
			}catch (Exception e){
				jData.put("sleep", SleepState.SLEEP);
				currBin = SleepState.SLEEP;
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		JSONArray jDataArray = new JSONArray();
		jDataArray.put(currBin);
		
		
		//group the data by consecutive equal states:
		//eg SLEEP, WAKE, WAKE, SLEEP, SLEEP becomes
		//(SLEEP), (WAKE, WAKE), (SLEEP, SLEEP)
		for (int i = 0; i < dataLen; i++){
			try {
				jData = scoredData.getJSONObject(i);
				SleepState currState; 
				try{
					currState = (SleepState)jData.get("sleep");
				}catch (Exception e){
					jData.put("sleep", SleepState.SLEEP);
					currState = SLEEP;
				}
				if (currState == currBin){
					jDataArray.put(jData);
				}
				else{
					sleepStateBins.add(jDataArray);
					jDataArray = new JSONArray();
					currBin = currState;
					jDataArray.put(currBin);
					jDataArray.put(jData);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		//identify areas for rescoring and do so
		sleepStateBins.add(jDataArray);
		int nextSleepRescore = 0;
		int numSleepStateBins = sleepStateBins.size();
		for (int i = 0; i < numSleepStateBins; i++){
			try{
				JSONArray currArray = sleepStateBins.get(i);
				SleepState binState = (SleepState)currArray.get(0);
				int len = currArray.length() - 1;
				if (binState == WAKE){
					if (len >= 15)
						nextSleepRescore += 4;
					else if (len >= 10)
						nextSleepRescore += 3;
					else if (len >= 4)
						nextSleepRescore += 1;
				}
				else {
					//start rescoring
					int numRescoresInBin = Math.min(nextSleepRescore, len);
					for (int j = 1; j <= numRescoresInBin; j++){
						nextSleepRescore--;
						jData = currArray.getJSONObject(j);
						jData.put("sleep", WAKE);
						currArray.put(j, jData);
					}
					sleepStateBins.remove(i);
					sleepStateBins.add(i, currArray);
				}
				
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < numSleepStateBins; i++){
			try{
				JSONArray currArray = sleepStateBins.get(i);
				SleepState binState = (SleepState)currArray.get(0);
				if (binState == SLEEP && currArray.length() - 1 <= 6){
					int surroundingWake = 0;
					if (i > 0){
						surroundingWake += sleepStateBins.get(i-1).length();
					}
					if (i < numSleepStateBins - 1){
						surroundingWake += sleepStateBins.get(i+1).length();
					}
					
					if (surroundingWake >= 10){
						for (int j = 1; j < currArray.length(); j++){
							jData = currArray.getJSONObject(j);
							if (jData.get("sleep") != WAKE){
								jData.put("sleep", WAKE);
								currArray.put(j, jData);
							}
						}
						sleepStateBins.remove(i);
						sleepStateBins.add(i, currArray);
					}
				}
			}catch (Exception e){
				
			}
		}
		
		
		//merge back into one JSON array
		
		JSONArray newScoredData = new JSONArray();
		int newIdx = 0;
		for (int i = 0; i < sleepStateBins.size(); i++){
			JSONArray currArray = sleepStateBins.get(i);
			for (int j = 1; j < currArray.length(); j++){
				try{
					newScoredData.put(newIdx, currArray.getJSONObject(j));
					newIdx++;
				} catch (Exception e) {
				}
				
			}
			
		}
		return newScoredData;
		
	}
}
