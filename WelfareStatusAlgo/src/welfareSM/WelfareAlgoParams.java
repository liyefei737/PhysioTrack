package welfareSM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WelfareAlgoParams {
	
	//default parameter ranges to define red,  yellow, green
	private static List<Integer> hrRangesDefault = Arrays.asList(30, 40, 70, 100);
	private static List<Integer> brRangesDefault = Arrays.asList(12, 18);
	private static List<Float> stRangesDefault = Arrays.asList(35.0f, 36.0f,37.8f, 39.0f);
	private static List<Float> ctRangesDefault = Arrays.asList(25.0f, 32.0f,36.0f, 37.0f);
	
	//min values to be considered valid. Lower than this, sensor is malfunctioning, or user is dead.
	public static int minHeartRate = 20;
	public static int minBreathRate = 0;
	public static double minSkinTemp = 0;
	public static double minCoreTemp = 15.0;
	
	//how sensitive the algo is to params
	private double hrWeight;
	private double brWeight;
	private double stWeight;
	private double ctWeight;

	private List<Integer> hrRange, brRange;
	private List<Float> stRange, ctRange;
	
	public WelfareAlgoParams(){
		hrRange = hrRangesDefault;
		brRange = brRangesDefault;
		stRange = stRangesDefault;
		ctRange = ctRangesDefault;
		
		hrWeight = 0.25;
		brWeight = 0.25;
		stWeight = 0.25;
		ctWeight = 0.25;
	}
	
	public WelfareAlgoParams(List<Integer> heartRateRange, double heartWeight, List<Integer> breathRateRange, double breathWeight, List<Float> skinTempRange, double skinWeight,
			List<Float> coreTempRange, double coreWeight){
		setPhysioParamThresholds(heartRateRange, breathRateRange, skinTempRange, coreTempRange);
		setPhysioParamWeights(heartWeight, breathWeight, skinWeight, coreWeight);
	}
	
	public void setPhysioParamThresholds(List<Integer> heartRateRange, List<Integer> breathRateRange, List<Float> skinTempRange, List<Float> coreTempRange ){
		hrRange = heartRateRange;
		brRange = breathRateRange;
		stRange = skinTempRange;
		ctRange = coreTempRange;
	}
	
	public boolean setPhysioParamWeights(double heartWeight, double breathWeight, double skinWeight, double coreWeight){
		hrWeight = heartWeight;
		brWeight = breathWeight;
		stWeight = skinWeight;
		ctWeight = coreWeight;
		
		double sumWeights = hrWeight + brWeight + stWeight + ctWeight;
		if (Utils.Equals(sumWeights, 0.0))
			return false;
		if (sumWeights > 1.0) {
			//normalize
			hrWeight /= sumWeights;
			brWeight /= sumWeights;
			stWeight /= sumWeights;
			ctWeight /= sumWeights;
		}
		return true;
	}
	
	public double getHrWeight() {
		return hrWeight;
	}

	public void setHrWeight(double hrWeight) {
		this.hrWeight = hrWeight;
	}

	public double getBrWeight() {
		return brWeight;
	}

	public void setBrWeight(double brWeight) {
		this.brWeight = brWeight;
	}

	public double getStWeight() {
		return stWeight;
	}

	public void setStWeight(double stWeight) {
		this.stWeight = stWeight;
	}

	public double getCtWeight() {
		return ctWeight;
	}

	public void setCtWeight(double ctWeight) {
		this.ctWeight = ctWeight;
	}

	public List<Object> getHrRangeObj() {
		return new ArrayList<Object>(hrRange);
	}
	
	public List<Integer> getHrRange() {
		return hrRange;
	}

	public void setHrRange(List<Integer> hrRange) {
		this.hrRange = hrRange;
	}

	public List<Object> getBrRangeObj() {
		return new ArrayList<Object>(brRange);
	}
	
	public List<Integer> getBrRange() {
		return brRange;
	}

	public void setBrRange(List<Integer> brRange) {
		this.brRange = brRange;
	}

	public List<Object> getStRangeObj() {
		return new ArrayList<Object>(stRange);
	}
	
	public List<Float> getStRange() {
		return stRange;
	}

	public void setStRange(List<Float> stRange) {
		this.stRange = stRange;
	}

	public List<Object> getCtRangeObj() {
		return new ArrayList<Object>(ctRange);
	}
	
	public List<Float> getCtRange() {
		return ctRange;
	}

	public void setCtRange(List<Float> ctRange) {
		this.ctRange = ctRange;
	}
}
