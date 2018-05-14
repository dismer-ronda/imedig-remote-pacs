package es.pryades.imedig.wiewer.echo3;

public interface ModalContainer 
{
	public void notifyCommand( String command, ModalContainer child );
}
