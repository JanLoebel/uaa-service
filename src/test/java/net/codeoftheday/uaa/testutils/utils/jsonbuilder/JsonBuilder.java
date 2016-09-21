package net.codeoftheday.uaa.testutils.utils.jsonbuilder;

import java.util.Map;

public interface JsonBuilder {

	JsonBuilder put(String key, Object value);

	// Object
	JsonBuilder newObject(String key);

	JsonBuilder buildObject();

	Map<String, Object> build();

}
