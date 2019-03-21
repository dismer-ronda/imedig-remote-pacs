package es.pryades.imedig.cloud.modules.Configuration.tabs;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.common.FilteredContentCloseable;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Query;
import es.pryades.imedig.cloud.vto.StudyVto;
import es.pryades.imedig.cloud.vto.controlers.StudyControlerVto;
import es.pryades.imedig.cloud.vto.refs.StudyVtoFieldRef;
import es.pryades.imedig.core.common.ModalParent;
import es.pryades.imedig.core.common.QueryFilterRef;
import es.pryades.imedig.core.common.TableImedigPaged;
import es.pryades.imedig.pacs.dto.Study;
import es.pryades.imedig.pacs.dto.query.StudyQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class StudiesListTab extends FilteredContentCloseable implements ModalParent
{
	private static final long serialVersionUID = -8196252452099428486L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( StudiesListTab.class );

	private BeanItem<StudiesListTab> bi;

	@Getter @Setter	private Integer queryDate;	
	@Getter @Setter private String queryPatient;

	/**
	 * 
	 * @param ctx
	 * @param resource
	 */
	public StudiesListTab( ImedigContext ctx )
	{
		super( ctx );
	}

	public String getTabTitle()
	{
		return getContext().getString( "words.studies" );
	}

	public Component getQueryFecha()
	{
		ComboBox combo = new ComboBox();
		combo.setWidth( "120px" );
		combo.setNullSelectionAllowed( false );
		combo.setTextInputAllowed( false );
		combo.addItem( 0 );
		combo.setItemCaption( 0, getContext().getString( "words.all" ) );
		combo.addItem( 1 );
		combo.setItemCaption( 1, getContext().getString( "words.today" ) );
		combo.addItem( 2 );
		combo.setItemCaption( 2, getContext().getString( "words.yesterday" ) );
		combo.addItem( 3 );
		combo.setItemCaption( 3, getContext().getString( "words.lastweek" ) );
		combo.addItem( 4 );
		combo.setItemCaption( 4, getContext().getString( "words.lastmonth" ) );
		combo.addItem( 5 );
		combo.setItemCaption( 5, getContext().getString( "words.lastyear" ) );
		combo.setPropertyDataSource( bi.getItemProperty( "queryDate" ) );
		
		Label label = new Label( getContext().getString( "words.date" ) );

		HorizontalLayout rowFecha = new HorizontalLayout();
		rowFecha.addComponent( label );
		rowFecha.setComponentAlignment( label, Alignment.MIDDLE_LEFT );
		rowFecha.addComponent( combo );
		rowFecha.setComponentAlignment( combo, Alignment.MIDDLE_LEFT );
		rowFecha.setSpacing( true );
		rowFecha.setMargin( false );

		return rowFecha;
	}

	public Component getQueryPaciente()
	{
		TextField text = new TextField( bi.getItemProperty( "queryPatient" ) );
		text.setNullRepresentation( "" );
		
		Label label = new Label( getContext().getString( "words.patient" ) );
		
		HorizontalLayout row = new HorizontalLayout();
		row.addComponent( label );
		row.setComponentAlignment( label, Alignment.MIDDLE_LEFT );
		row.addComponent( text );
		row.setComponentAlignment( text, Alignment.MIDDLE_LEFT );
		row.setSpacing( true );
		row.setMargin( false );

		return row;
	}

	public Component getQueryComponent()
	{
		bi = new BeanItem<StudiesListTab>( this );
		
		queryDate = 4;
		queryPatient = "";
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setSpacing( true );
		//row1.setMargin( new MarginInfo( true, false, false, false ) );
		row1.addComponent( getQueryFecha() );
		row1.addComponent( getQueryPaciente() );
		
		return row1;
	}

	public TableImedigPaged getTableRows()
	{
		return new TableImedigPaged( StudyVto.class, new StudyVto(), new StudyVtoFieldRef(), new QueryFilterRef( new StudyQuery() ), getContext(), Constants.DEFAULT_PAGE_SIZE );
	}
	
	public String[] getVisibleCols()
	{
		return StudyControlerVto.getVisibleCols();
	}
	
	public String getResouceKey()
	{
		return "studiesConfig.tableStudies";
	}

	@Override
	public void onSelectedRow( Object row  )
	{
		setEnabledDelete( row != null );
	}

	public void onDeleteRow( final Object row )
	{
		ConfirmDialog.show( UI.getCurrent(), 
				context.getString( "words.confirm" ), 
				getContext().getString( getResouceKey() + ".confirm.delete" ), 
				context.getString( "Generic.Yes" ), 
				context.getString( "Generic.No" ), 
				new ConfirmDialog.Listener() 
				{
					private static final long serialVersionUID = -3142429497962370163L;

					public void onClose(ConfirmDialog dialog) 
		            {
		                if ( dialog.isConfirmed() ) 
		                {
		            		Study study = (Study)row;
		            		
		                    Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin invoke \"dcm4chee.archive:service=ContentEditService\" moveStudyToTrash " + study.getStudy_iuid() );
		                    Utils.cmdExec( "/opt/dcm4chee/bin/twiddle.sh -u admin -p admin invoke \"dcm4chee.archive:service=ContentEditService\" emptyTrash" );

		                    refreshVisibleContent();
		                } 
		            }
		        });
	}
	
	public void setDateFilter( StudyQuery queryObj )
	{
		switch ( queryDate.intValue() )
		{
			case 1:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;
			
			case 2:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getYesterdayDate("yyyy-MM-dd") + " 00:00:00" )  );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getYesterdayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;
			
			case 3:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getLastWeekDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;

			case 4:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getLastMonthDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;

			case 5:
				queryObj.setFrom_date( Timestamp.valueOf( Utils.getLastYearDate("yyyy-MM-dd") + " 00:00:00" ) );
				queryObj.setTo_date( Timestamp.valueOf( Utils.getTodayDate("yyyy-MM-dd") + " 23:59:59" ) );
			break;
			
			default:
				queryObj.setFrom_date( null );
				queryObj.setTo_date( null );
			break;
		}
	}
	
	@Override
	public Query getQueryObject()
	{
		StudyQuery queryObj = new StudyQuery();

		setDateFilter( queryObj );
		if ( !queryPatient.isEmpty() )
			queryObj.setPat_name( queryPatient );

		return queryObj;
	}

	@Override
	public void onAddRow()
	{
	}

	@Override
	public void onModifyRow( Object row )
	{
	}

	@Override
	public boolean isDeleteAvailable()
	{
		return true;
	}
}
