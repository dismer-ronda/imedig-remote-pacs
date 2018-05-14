package es.pryades.imedig.core.common;

import java.util.ArrayList;

import es.pryades.imedig.cloud.dto.ImedigDto;
import es.pryades.imedig.cloud.dto.Query;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public interface ImedigMapper
{
    public void addRow( ImedigDto row );
    public void setRow( ImedigDto row );
    
    public void delRow( ImedigDto row );
    
    public int getNumberOfRows( Query query );

    public ArrayList<ImedigDto> getRows( Query query );
    public ArrayList<ImedigDto> getPage( Query query );
    
    public ImedigDto getLastRow( ImedigDto query );
    public ImedigDto getRow( ImedigDto row );
    public ImedigDto getNextRow( ImedigDto query );
}
