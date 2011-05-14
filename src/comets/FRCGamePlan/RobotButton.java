package comets.FRCGamePlan;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class RobotButton extends Button {

	private String teamNumber;
	
	public RobotButton(Context context) {
		super(context);
	}
	
	public RobotButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public RobotButton(Context context, AttributeSet attrs, int defStyle) {
		super (context, attrs, defStyle);
	}
	
	public void setTeamNumber(String tn) {
		teamNumber = tn;
	}
	
	public String getTeamNumber() {
		return teamNumber;
	}
	
}
