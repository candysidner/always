package edu.wpi.always.cm;

import org.picocontainer.*;

public interface SchemaFactory {

	long getUpdateDelay();
	
	Class<? extends Schema> getSchemaType();

	Schema create(PicoContainer container);

}