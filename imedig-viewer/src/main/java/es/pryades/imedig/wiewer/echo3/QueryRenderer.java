package es.pryades.imedig.wiewer.echo3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.CheckBox;
import nextapp.echo.app.Color;
import nextapp.echo.app.Component;
import nextapp.echo.app.Label;
import nextapp.echo.app.Row;
import nextapp.echo.app.Table;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.TableLayoutData;
import nextapp.echo.app.table.TableCellRenderer;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.viewer.Study;

/**
 * Rellena las tablas de las consultas de informes
 * 
 * @author ricky
 * @version 1.0
 * @since 2007
 * 
 */
public class QueryRenderer implements TableCellRenderer 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3733056254912302300L;

	protected ResourceBundle resourceBundle;
	
	private ArrayList<String> selectedStudies;
	private ArrayList<CheckBox> checkBoxes;
	
	/**
	 * 
	 * Constructor DetPacInformeCellRenderer
	 * 
	 * @param newMvc
	 */
	public QueryRenderer(ResourceBundle resourceBundle) 
	{
		this.resourceBundle = resourceBundle;
		
	    selectedStudies = new ArrayList<String>();
	    checkBoxes = new ArrayList<CheckBox>();
	}

	public Component getHeaderCell( int col )
	{
		Row r = new Row();

		Color b = new Color( Colors.INT_METRO_TEAL );
		
		r.setAlignment( new Alignment( Alignment.LEFT, Alignment.TOP ) );
		
		TableLayoutData layout = new TableLayoutData();
		layout.setBackground( b );
		
		r.setLayoutData( layout );
		
		Label c;
		
		switch ( col )
		{
			case 0:
				c = AppUtils.getLabel( "", -1 );
			break;

			case 1:
				c = AppUtils.getLabel( resourceBundle.getString("QueryForm.StudyDate"), -1 );
			break;

			case 2:
				c = AppUtils.getLabel( resourceBundle.getString("QueryForm.Modality"), -1 );
			break;
			
			case 3:
				c = AppUtils.getLabel( resourceBundle.getString("QueryForm.PatientId"), -1 );
			break;
			
			case 4:
				c = AppUtils.getLabel( resourceBundle.getString("QueryForm.PatientName"), -1 );
			break;
			
			case 5:
				c = AppUtils.getLabel( resourceBundle.getString("QueryForm.Age"), -1 );
			break;

			case 6:
				c = AppUtils.getLabel( resourceBundle.getString("QueryForm.Referrer"), -1 );
			break;
			
			default:
				c = new Label( "" );
			break;
		}
		
		c.setForeground( Color.WHITE );
		r.add( c );

		return r;
	}
	
	public Component getBodyCell( Table table, Object value, int col, int row )
	{
		Study study = ((QueryTableModel) table.getModel()).getRow( row );
		
		if ( study != null )
		{
			Row r = new Row();

			Color b =/* (row % 2 == 0) ?new Color( Colors.INT_METRO_TEAL ) :*/  Color.LIGHTGRAY ;
			
			r.setAlignment( new Alignment( Alignment.LEFT, Alignment.TOP ) );
			
			TableLayoutData layout = new TableLayoutData();
			layout.setBackground( b );
			
			r.setLayoutData( layout );
			
			Component c;
	
			switch ( col )
			{
				case 0:
					c = new CheckBox();
					
					c.set( "StudyInstanceUID", study.getStudyInstanceUID() );
					
					((CheckBox)c).addActionListener( 
					new ActionListener() 
					{
						private static final long serialVersionUID = 1L;
				
						public void actionPerformed( ActionEvent e ) 
						{
							onStudyCheck( e );
						}
					}
					);

					checkBoxes.add( (CheckBox)c );
				break;
			
				case 1:
					c = AppUtils.getLabel( study.getStudyDate(), -1 );
				break;
	
				case 2:
					c = AppUtils.getLabel( study.getModalitiesInStudy(), -1 );
				break;
				
				case 3:
					c = AppUtils.getLabel( study.getPatientID(), -1 );
				break;
				
				case 4:
					c = AppUtils.getLabel( study.getPatientName(), 20 );
				break;
				
				case 5:
					String birthDate = study.getPatientBirthDate();
					String age = "";
					
					if ( birthDate != null )
					{
						SimpleDateFormat f = new SimpleDateFormat( "dd/MM/yyyy" );
						
						Date dob;
	
						try 
						{
							dob = f.parse( birthDate );
						
							if ( dob != null )
								age = Utils.getAge( dob, resourceBundle.getString( "Generic.Month" ), resourceBundle.getString( "Generic.Year" ) );
							
						} 
						catch ( ParseException e ) 
						{
						}
					}
	
					c = AppUtils.getLabel( age, -1 );
				break;
	
				case 6:
					c = AppUtils.getLabel( study.getReferringPhysicianName(), 20 );
				break;
				
				default:
					c = new Label( "" );
				break;
			}
	
			r.add( c );

			return r;
		}
			
		return null;
	}
	
	/**
	 * 
	 * Método sobreescrito 'getTableCellRendererComponent'
	 * 
	 * @param table
	 * @param value
	 * @param col
	 * @param row
	 * @return
	 * @see nextapp.echo.app.table.TableCellRenderer#getTableCellRendererComponent(nextapp.echo.app.Table,
	 *      java.lang.Object, int, int)
	 */
	
	public Component getTableCellRendererComponent( Table table, Object value, int col, int row ) 
	{
		if ( row == -1 )
			return getHeaderCell( col );
		else
			return getBodyCell( table, value, col, row );
	}

	private void onClear( ActionEvent e ) 
	{
	}

	private void onStudyCheck( ActionEvent e ) 
	{
		CheckBox c = (CheckBox)e.getSource();

		if ( c.isSelected() )
			selectedStudies.add( (String)c.get( "StudyInstanceUID" ) );
		else
			selectedStudies.remove( (String)c.get( "StudyInstanceUID" ) );
	}

	public ArrayList<String> getSelectedStudies() {
		return selectedStudies;
	}
	
	public void clearSelected()
	{
		for ( CheckBox check : checkBoxes )
		{
			check.setSelected( false );
		}
		
		selectedStudies.clear();
	}
}
