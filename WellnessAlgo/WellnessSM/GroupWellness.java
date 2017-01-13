package WellnessSM;

import static WellnessSM.WellnessState.*;
import java.util.List;

public class GroupWellness {
	private WellnessState aggState;
	private List<IndividualWellness> people;
	
	GroupWellness(){
		aggState = GREY;
	}
	
	public WellnessState getState(){
		return aggState;
	}

}
