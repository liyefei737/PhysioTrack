package sleepS;

public class DateStatePair {
	private String date;
	private SleepState state;
	
	public DateStatePair(){
		
	}
	public DateStatePair(String newDate, SleepState newState){
		date = newDate;
		state = newState;
	}
	
	public String getDate(){
		return date;
	}
	public void setDate(String newDate){
		date = newDate;
	}
	public SleepState getState(){
		return state;
	}
	public void setState(SleepState newState){
		state = newState;
	}
}
