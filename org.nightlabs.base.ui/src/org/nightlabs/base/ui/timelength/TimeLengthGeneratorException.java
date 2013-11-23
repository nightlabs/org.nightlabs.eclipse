package org.nightlabs.base.ui.timelength;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TimeLengthGeneratorException extends Exception
{
	public enum Mode {
		INVALID_FORMAT,
		INVALID_NUMBER_FORMAT
	}

	private String valueStr;
	private String symbol;
	private String field;
	private Mode mode;

	/**
	 * @param valueStr
	 * @param symbol
	 * @param field
	 * @param mode
	 */
	public TimeLengthGeneratorException(Mode mode, String valueStr, String symbol,
			String field)
	{
		this.valueStr = valueStr;
		this.symbol = symbol;
		this.field = field;
		this.mode = mode;
	}

	/**
	 * Returns the valueStr.
	 * @return the valueStr
	 */
	public String getValueStr() {
		return valueStr;
	}

	/**
	 * Returns the symbol.
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Returns the field.
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * Returns the mode.
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

}
