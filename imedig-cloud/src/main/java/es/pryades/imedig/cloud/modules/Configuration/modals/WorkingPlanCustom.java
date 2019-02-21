package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DayPlan;

public class WorkingPlanCustom extends AbstractWorkingPlan
{
	private static final long serialVersionUID = -1723666805512076721L;

	public WorkingPlanCustom( ImedigContext ctx, List<DayPlan<String>> diaryPlan )
	{
		super( ctx, diaryPlan );
	}

	@Override
	protected List<Integer> initDays()
	{
		return Arrays.asList( Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY );
	}

}
