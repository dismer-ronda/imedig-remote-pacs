package es.pryades.imedig.cloud.common.lazy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import es.pryades.imedig.cloud.core.dal.PacientesManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Paciente;
import es.pryades.imedig.cloud.dto.PacienteQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class PacienteLazyProvider implements DataProvider<Paciente>
{
	private ImedigContext ctx;
	private PacienteQuery query;
	private PacientesManager manager;
	
	public PacienteLazyProvider(ImedigContext ctx)
	{
		this.ctx = ctx;
		query = new PacienteQuery();
		manager = (PacientesManager)IOCManager.getInstanceOf( PacientesManager.class );
	}

	@Override
	public int count( Criteria criteria )
	{
		settingQueryFilter( criteria.getFilter() );
		
		try
		{
			return manager.getNumberOfRows( ctx, query );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	private void settingQueryFilter(String filter){
		if (!StringUtils.isBlank( filter )){
			query.setFiltro( filter );
		}else{
			query.setFiltro( null );
		}
	}

	@Override
	public List<Paciente> find( Criteria criteria, int startIndex, int offset, List<OrderByColumn> columns )
	{
		query.setPageSize(offset);
		query.setPageNumber(startIndex);
		settingQueryFilter(criteria.getFilter());

		try
		{
			return (List<Paciente>)manager.getPageLazy(ctx, query);
		}
		catch ( Throwable e )
		{
			return new ArrayList<>();
		}
	}

}
