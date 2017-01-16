package welfareSM;

import java.util.ArrayList;
import java.util.List;
import static welfareSM.WelfareStatus.*;

public class GroupWelfare {
	private ArrayList<IndividualWelfareTracker> people;
	
	GroupWelfare(){
		people = new ArrayList<IndividualWelfareTracker>();
	}
	
	GroupWelfare(float HRmin, float HRmax, 
			float BRmin, float BRmax, float STmin, float STmax, float CTmin, float CTmax){
		people = new ArrayList<IndividualWelfareTracker>();
	}
	
	public IndividualWelfareTracker addPerson(){
		IndividualWelfareTracker p = new IndividualWelfareTracker();
		people.add(p);
		return p;		
	}
	
	public void removePerson(){
		
	}
	
	public void addPeopleList(){
		
		
	}
	
	public List<WelfareStatus> getGroupWelfareStatus(){
		ArrayList<WelfareStatus> peopleStates = new ArrayList<WelfareStatus>();
		for (IndividualWelfareTracker iwt : people) {
			peopleStates.add(iwt.getState());
		}
		return peopleStates;
	}
	
	public void calcNewGroupWelfareStatus(){
		for (IndividualWelfareTracker iwt:people)
			iwt.calculateWelfareStatus();
	}
}
