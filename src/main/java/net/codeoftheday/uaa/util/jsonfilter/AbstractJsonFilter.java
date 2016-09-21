package net.codeoftheday.uaa.util.jsonfilter;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

public abstract class AbstractJsonFilter extends SimpleBeanPropertyFilter {

	protected abstract String getFilterIdentifier();

}
