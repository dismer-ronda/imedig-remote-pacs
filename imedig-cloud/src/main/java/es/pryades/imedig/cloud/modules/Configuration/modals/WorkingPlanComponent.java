package es.pryades.imedig.cloud.modules.Configuration.modals;

import java.util.List;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.DayPlan;

public class WorkingPlanComponent extends VerticalLayout
{
	private static final long serialVersionUID = 6332866195262982216L;
	
	private final ImedigContext ctx;
	private TabSheet tabSheet;
	private WorkingPlanEveryDay everyDay;
	private WorkingPlanMondayFriday mondayFriday;
	private WorkingPlanCustom custom;
	private AbstractWorkingPlan selected;
	
	public WorkingPlanComponent(ImedigContext ctx, List<DayPlan<String>> diaryPlan)
	{
		super( );
		this.ctx = ctx;
		
		setWidth( "100%" );
		
		buildComponents(diaryPlan);
	}
	
	
	private void buildComponents(List<DayPlan<String>> diaryPlan){
		
		tabSheet = new TabSheet();
		tabSheet.setWidth( "100%" );
		tabSheet.setTabsVisible( false );
		
		everyDay = new WorkingPlanEveryDay( ctx, diaryPlan );
		tabSheet.addTab( everyDay );
		
		mondayFriday = new WorkingPlanMondayFriday( ctx, diaryPlan );
		tabSheet.addTab( mondayFriday );

		custom = new WorkingPlanCustom( ctx, diaryPlan );
		tabSheet.addTab( custom );

		select(Constants.SCHEDULER_ALL_EQUALS);
		
		addComponent( tabSheet );
	}
	
	public void select(int type){
		switch ( type )
		{
			case Constants.SCHEDULER_ALL_EQUALS:
				selected = everyDay;
				break;
			case Constants.SCHEDULER_ALL_WEEK_DAYS:
				selected = mondayFriday;
				break;
			case Constants.SCHEDULER_CUSTOM:
				selected = custom;
				break;

			default:
				break;
		}
		
		tabSheet.setSelectedTab( selected );
	}
	
	public boolean isValid(){
		return selected.isValid();
	}
	
	public List<DayPlan<String>> getWeekPlan(){
		return selected.getWeekPlan();
	}
}
