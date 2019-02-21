package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.vaadin.ui.CheckBox;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DayPlan;

public class WorkingPlanEveryDay extends AbstractWorkingPlan
{
	private static final long serialVersionUID = 2862304694031974673L;

	public WorkingPlanEveryDay( ImedigContext ctx, List<DayPlan<String>> diaryPlan )
	{
		super( ctx, diaryPlan );
	}

	@Override
	protected List<Integer> initDays()
	{
		return Arrays.asList( Calendar.MONDAY);
	}

	
	@Override
	protected CheckBox getCheckBox( int day )
	{
		CheckBox checkBox = new CheckBox( ctx.getString( "words.every.days" ) );
		checkBox.setData( day );
		checkBox.setValue( true );
		checkBox.setReadOnly( true );
		
		return checkBox;
	}
}
