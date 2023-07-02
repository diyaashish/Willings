package jp.loioz.common.utility.data;

import java.text.ParseException;
import java.text.ParsePosition;

/**
 * java.text.DecimalFormatのオーバーライド用クラス
 *
 */
public class LoiozDecimalFormat extends java.text.DecimalFormat {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/*
	 *	 Parses text from the beginning of the given string to produce a number. The
	 * method may not use the entire text of the given string.
	 * <p>
	 * See the {@link #parse(String, ParsePosition)} method for more information on
	 * number parsing.
	 *
	 * @param source A <code>String</code> whose beginning should be parsed.
	 * @return A <code>Number</code> parsed from the string.
	 * @exception ParseException if the beginning of the specified string cannot be
	 * parsed.
	 */
	/**
	 *
	 */
	@Override
	public Number parse(String source) throws ParseException {

		ParsePosition parsePosition = new ParsePosition(0);
		Number result = parse(source, parsePosition);
		if (parsePosition.getIndex() == 0) {
			throw new ParseException("Unparseable number: \"" + source + "\"",
					parsePosition.getErrorIndex());
		}
		return result;
	}
}
