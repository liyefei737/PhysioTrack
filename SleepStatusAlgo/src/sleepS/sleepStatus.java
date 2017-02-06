package sleepS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static sleepS.SleepState.*;

public class sleepStatus {	
		
	public static DateStatePair CalculateSleepStatus(JSONArray last9SecondsData){
		Map<String,float[]> accCollection = processJSONArray(last9SecondsData);

		if (accCollection.size() < 9){
			return new DateStatePair(null, NOT_ENOUGH_INFO);
		}
		
		//take magnitude of acc at each time epoch
		
		List<Float> accMagnitude = new ArrayList<Float>();
		int i = 0;
		String dateN = null;
		for (Map.Entry<String, float[]> entry : accCollection.entrySet()) {
			accMagnitude.add(i,(float) Math.sqrt(Math.pow(entry.getValue()[0], 2) + Math.pow(entry.getValue()[0], 2) + Math.pow(entry.getValue()[0], 2)));
			if (i == 4)
				dateN = entry.getKey();
			i++;
		}
		float modAcc = (float) (0.04 * accMagnitude.get(0)
                        + 0.04 * accMagnitude.get(1)
                        + 0.2 * accMagnitude.get(2)
                        + 0.2 * accMagnitude.get(3)
                        + 2 * accMagnitude.get(4)
                        + 0.2 * accMagnitude.get(5)
                        + 0.2 * accMagnitude.get(6)
                        + 0.04 * accMagnitude.get(7)
                        + 0.04 * accMagnitude.get(8));
        if (modAcc > 400.0)
        	return new DateStatePair(dateN, WAKE);
        else
        	return new DateStatePair(dateN, SLEEP);

    }

	public static Map<String,float[]> processJSONArray(JSONArray last9SecondsData){
		if (last9SecondsData == null)
			return null;
		
		Map<String, float[]> accCollection = new HashMap<String,float[]>();
		
		int j = 0;
		String strData, firstTStampMilli = "000";
		JSONObject jData;
		try {
			strData = last9SecondsData.getString(0);
			jData = new JSONObject(strData);
		
			firstTStampMilli = jData.getString("_id").substring(jData.getString("_id").length() -3);
		} catch (JSONException e) {
			//
		}
		int len = last9SecondsData.length();
		for(int i = 0; i < len;i++){
			try {
				strData = last9SecondsData.getString(i);
				jData = new JSONObject(strData);
				String sDateTime  = jData.getString("_id");
				if (sDateTime.substring(sDateTime.length() - 3).equals(firstTStampMilli)){
					//take even seconds
					float [] accArray = new float[3];
					accArray[0] = Float.valueOf(jData.getString("accX"));
					accArray[1] = Float.valueOf(jData.getString("accY"));
					accArray[2] = Float.valueOf(jData.getString("accZ"));
					accCollection.put(sDateTime, accArray);
					j++;
					if (j > 8)
						len = 0;
				}
			} catch (JSONException e) {
				
			}
			
		}
		
		return accCollection;
	}
}


