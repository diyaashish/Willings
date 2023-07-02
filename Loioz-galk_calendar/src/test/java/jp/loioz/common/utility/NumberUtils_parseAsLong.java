package jp.loioz.common.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class NumberUtils_parseAsLong {

	// Stringオブジェクト
	private String strVal;

	@Before
	public void setUp() {
		strVal = new String("");
	}

	// 整数 境界値 (桁数)
	@Test
	public void case1() throws Exception {
		strVal = String.valueOf(Long.MAX_VALUE);
		assertEquals(LoiozNumberUtils.parseAsLong(strVal), Long.valueOf(Long.MAX_VALUE));
	}

	// 不の数 境界値 境界値 (桁数)
	@Test
	public void case2() throws Exception {
		strVal = String.valueOf(Long.MIN_VALUE);
		assertEquals(LoiozNumberUtils.parseAsLong(strVal), Long.valueOf(Long.MIN_VALUE));
	}

	// 0
	@Test
	public void case3() throws Exception {
		strVal = String.valueOf(0);
		assertEquals(LoiozNumberUtils.parseAsLong(strVal), Long.valueOf(0));
	}

	// 限界桁数超過 変換エラーチェック(正の数)
	@Test
	public void case4() throws Exception {
		strVal = String.valueOf(Long.MAX_VALUE) + "1";
		try {
			LoiozNumberUtils.parseAsLong(strVal);
		} catch (NumberFormatException e) {
			assertTrue(true);
		}
	}

	// 限界桁数超過 変換エラーチェック(負の数)
	@Test
	public void case5() throws Exception {
		strVal = String.valueOf(Long.MIN_VALUE) + "1";
		try {
			LoiozNumberUtils.parseAsLong(strVal);
		} catch (NumberFormatException e) {
			assertTrue(true);
		}
	}

	// 文字列数値以外
	@Test
	public void case6() throws Exception {
		strVal = String.valueOf("あああ");
		try {
			LoiozNumberUtils.parseAsLong(strVal);
		} catch (NumberFormatException e) {
			assertTrue(true);
		}
	}

	// 不等号のみ
	@Test
	public void case7() throws Exception {
		strVal = String.valueOf("-");
		try {
			LoiozNumberUtils.parseAsLong(strVal);
		} catch (NumberFormatException e) {
			assertTrue(true);
		}
	}

}
