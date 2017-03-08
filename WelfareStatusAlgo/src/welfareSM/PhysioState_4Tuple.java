package welfareSM;

import static welfareSM.WelfareStatus.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PhysioState_4Tuple {
	public WelfareStatus heartState;
	public WelfareStatus breathState;
	public WelfareStatus skinState;
	public WelfareStatus coreState;
	
	public PhysioState_4Tuple(){
		heartState = GREY;
		breathState = GREY;
		skinState = GREY;
		coreState = GREY;
	}

	public Collection<WelfareStatus> values() {
		List<WelfareStatus> states = new ArrayList<WelfareStatus>();
		if (heartState == null)
			states.add(GREY);
		else
			states.add(heartState);
		
		if (breathState == null)
			states.add(GREY);
		else
			states.add(breathState);
		
		if (coreState == null)
			states.add(GREY);
		else
			states.add(coreState);
		
		if (skinState == null)
			states.add(GREY);
		else
			states.add(skinState);
		return states;
	}

}
