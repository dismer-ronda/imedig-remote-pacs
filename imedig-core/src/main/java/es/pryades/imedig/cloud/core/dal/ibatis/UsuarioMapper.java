package es.pryades.imedig.cloud.core.dal.ibatis;

import es.pryades.imedig.cloud.dto.Usuario;
import es.pryades.imedig.core.common.ImedigMapper;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public interface UsuarioMapper extends ImedigMapper
{
    public void setPassword( Usuario usuario );
    public void setIntentos( Usuario usuario  );
    public void setEstado( Usuario usuario  );
}
