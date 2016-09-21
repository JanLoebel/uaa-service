package net.codeoftheday.uaa.testutils.utils.jsonbuilder;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;

public class DefaultJsonBuilderTest {

	@Test
	public void empty() {
		final JsonBuilder builder = DefaultJsonBuilder.jsonBuilder();
		assertThat(builder.build(), equalTo(Collections.EMPTY_MAP));
	}

	@Test
	public void simpleTest() {
		final JsonBuilder builder = DefaultJsonBuilder.jsonBuilder();
		builder.put("key", "value");
		assertThat(builder.build(), hasEntry("key", "value"));
	}

	public void objectTest() {
		final JsonBuilder builder = DefaultJsonBuilder.jsonBuilder();
		builder.newObject("object").put("object-key", "object-value").buildObject();

		final Map<String, Object> result = builder.build();
		assertThat(result, hasKey("object"));

		@SuppressWarnings("unchecked")
		final Map<String, Object> resultObject = (Map<String, Object>) result.get("object");
		assertThat(resultObject, hasEntry("object-key", "object-value"));

	}

	@Test(expected = IllegalStateException.class)
	public void rootBuildObjectFail() {
		DefaultJsonBuilder.jsonBuilder().buildObject();
	}

	@Test(expected = IllegalStateException.class)
	public void objectBuilderJsonFail() {
		DefaultJsonBuilder.jsonBuilder().newObject("anykey").build();
	}

}
