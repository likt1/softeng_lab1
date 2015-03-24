package tracing.views;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Timer {
	long startTime;
	long endTimer;
	
	public void StartTimer(){
		startTime = System.nanoTime();
	}
	
	public void EndTimer(){
		endTimer = System.nanoTime();
	}
	
	
	public String CheckTimer()
	{
		NumberFormat formatter = new DecimalFormat("#0.00");
		String formTime = formatter.format((endTimer - startTime)/1000000000);
		return formTime;
	}
	
	
}
