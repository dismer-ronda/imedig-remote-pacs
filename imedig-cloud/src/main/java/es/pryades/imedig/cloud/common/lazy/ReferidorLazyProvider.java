package es.pryades.imedig.cloud.common.lazy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import es.pryades.imedig.cloud.common.Constants;
import es.pryades.imedig.cloud.core.bll.UsuariosManager;
import es.pryades.imedig.cloud.core.dto.ImedigContext;
import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.cloud.dto.query.UsuarioQuery;
import es.pryades.imedig.cloud.ioc.IOCManager;

public class ReferidorLazyProvider implements DataProvider<Usuario>
{
	private ImedigContext ctx;
	private UsuarioQuery query;
	private UsuariosManager manager;
	
	public ReferidorLazyProvider(ImedigContext ctx)
	{
		this.ctx = ctx;
		query = new UsuarioQuery();
		query.setPerfiles( Arrays.asList( Constants.PROFILE_DOCTOR, Constants.PROFILE_IMGDOCTOR ) );
		manager = (UsuariosManager)IOCManager.getInstanceOf( UsuariosManager.class );
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

	@Override
	public List<Usuario> find( Criteria criteria, int startIndex, int offset, List<OrderByColumn> columns )
	{
		query.setPageSize(offset);
		query.setPageNumber(startIndex);
		settingQueryFilter(criteria.getFilter());

		try
		{
			return (List<Usuario>)manager.getPageLazy(ctx, query);
		}
		catch ( Throwable e )
		{
			return new ArrayList<>();
		}
	}

	private void settingQueryFilter(String filter){
		if (!StringUtils.isBlank( filter )){
			query.setNombre( filter );
		}else{
			query.setNombre( null );
		}
	}
}
