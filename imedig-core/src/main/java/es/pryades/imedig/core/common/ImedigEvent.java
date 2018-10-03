package es.pryades.imedig.core.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImedigEvent implements Serializable {
	private static final long serialVersionUID = 193119880836607450L;
	
	private Object id;
	private Object target;
}
