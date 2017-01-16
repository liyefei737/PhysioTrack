package welfareSM;

import static welfareSM.WelfareStatus.*;

public class IndividualWelfareTracker {
	private WelfareStatus state;
	private boolean heartRateValid;
	private boolean breathRateValid;
	private boolean skinTempValid;
	private boolean coreTempValid;
	
	public IndividualWelfareTracker()
	{
		state = GREY;
		heartRateValid = false;
		breathRateValid= false;
		skinTempValid= false;
		coreTempValid= false;
	}
	
	public void setPhysioParamThresholds(){
		
	}
	
	public WelfareStatus calculateWelfareStatus(){
		if (!heartRateValid && !breathRateValid && !skinTempValid && !coreTempValid)
			return GREY;
		return state;
	}
	
	public WelfareStatus getState(){
		return state;
	}
	
}
