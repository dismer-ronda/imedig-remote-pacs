package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.vaadin.ui.CheckBox;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DayPlan;

public class WorkingPlanMondayFriday extends AbstractWorkingPlan
{
	private static final long serialVersionUID = -2371919930023721254L;

	public WorkingPlanMondayFriday( ImedigContext ctx, List<DayPlan<String>> diaryPlan )
	{
		super( ctx, diaryPlan );
	}

	@Override
	protected List<Integer> initDays()
	{
		return Arrays.asList( Calendar.MONDAY, Calendar.SATURDAY, Calendar.SUNDAY);
	}

	@Override
	protected CheckBox getCheckBox( int day )
	{
		if (day == Calendar.MONDAY){
			CheckBox checkBox = new CheckBox( ctx.getString( "words.week.days" ) );
			checkBox.setData( day );
			checkBox.setValue( true );
			checkBox.setReadOnly( true );
			return checkBox;
		}
		
		return super.getCheckBox( day );
	}
}
