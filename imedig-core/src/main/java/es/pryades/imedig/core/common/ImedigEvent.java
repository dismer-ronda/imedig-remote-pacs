package es.pryades.imedig.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImedigEvent {
	private Object id;
	private Object target;
}
