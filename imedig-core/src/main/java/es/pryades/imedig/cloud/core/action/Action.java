package es.pryades.imedig.cloud.core.action;

public interface Action <S, D> {
	S getSource();
	
	D getData();
}
