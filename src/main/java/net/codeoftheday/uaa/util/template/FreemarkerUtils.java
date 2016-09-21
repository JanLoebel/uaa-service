package net.codeoftheday.uaa.util.template;

import java.util.List;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModelException;

public final class FreemarkerUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerUtils.class);

	private FreemarkerUtils() {
	}

	public static Object[] extractArray(@SuppressWarnings("rawtypes") final List arguments) {
		final Object[] result = new Object[arguments.size() - 1];
		for (int i = 1; i < arguments.size(); i++) {
			result[i - 1] = convertToString(arguments.get(i));
		}
		return result;
	}

	public static String convertToString(final Object obj) {
		if (null == obj) {
			return null;
		}

		if (obj instanceof String) {
			return obj.toString();
		}

		// Scalar
		if (obj instanceof SimpleScalar) {
			return ((SimpleScalar) obj).getAsString();
		}

		// String model
		if (obj instanceof StringModel) {
			return ((StringModel) obj).getAsString();
		}

		// Number
		if (obj instanceof SimpleNumber) {
			return ((SimpleNumber) obj).toString();
		}

		// Boolean
		if (obj instanceof TemplateBooleanModel) {
			try {
				return Boolean.toString(((TemplateBooleanModel) obj).getAsBoolean());
			} catch (final TemplateModelException e) {
				throw new RuntimeException(e);
			}
		}

		if (obj instanceof SimpleSequence) {
			try {
				final SimpleSequence sequence = (SimpleSequence) obj;
				final StringJoiner joiner = new StringJoiner(",");
				for (int i = 0; i < sequence.size(); i++) {
					joiner.add(convertToString(sequence.get(i)));
				}
				return joiner.toString();
			} catch (final TemplateModelException e) {
				throw new RuntimeException(e);
			}
		}

		LOGGER.info("Class {} is not implemented to get serialized to a string yet.", obj.getClass().getName());
		return obj.toString();
	}

}
