package es.pryades.imedig.viewer;

public interface ModalContainer 
{
	public void notifyCommand( String command, ModalContainer child );
}
