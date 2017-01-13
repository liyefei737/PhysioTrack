package WellnessSM;
import static WellnessSM.WellnessState.*;

public class IndividualWellness {
	private WellnessState state;
	//HR
	//BR
	//ST
	//CT
	//MOTION
	//POSTURE
	
	public IndividualWellness()
	{
		state = GREY;
	}
	
	public WellnessState getState(){
		return state;
	}
	public void setState(WellnessState wsState){
		state = wsState;
	}

}
