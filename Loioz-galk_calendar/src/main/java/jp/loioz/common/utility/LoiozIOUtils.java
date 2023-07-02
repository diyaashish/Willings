package jp.loioz.common.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Input/Output用のUtilクラス。
 */
public class LoiozIOUtils {

	/**
	 * return org.apache.commons.compress.utils.IOUtils.copy(final InputStream input, final OutputStream output)
	 * 
	 * @param input
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public static long copy(final InputStream input, final OutputStream output) throws IOException {
		return org.apache.commons.compress.utils.IOUtils.copy(input, output);
	}

	/**
	 * return com.amazonaws.util.IOUtils.toByteArray(InputStream is)
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(InputStream is) throws IOException {
		return com.amazonaws.util.IOUtils.toByteArray(is);
	}
}
