package jp.loioz.common.utility;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

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
public class ValidateUtils_isDecimalHigh {

	// 比較元
	private BigDecimal source;

	// 比較対象
	private BigDecimal target;

	@Before
	public void setUp() {
		source = new BigDecimal(0);
		target = new BigDecimal(0);
	}

	// 比較元 < 比較対象
	@Test
	public void case1() throws Exception {
		source = new BigDecimal(1000);
		target = new BigDecimal(1001);
		assertEquals(true, ValidateUtils.isDecimalHigh(source, target));
	}

	// 同値
	@Test
	public void case2() throws Exception {
		source = new BigDecimal(1000);
		target = new BigDecimal(1000);
		assertEquals(false, ValidateUtils.isDecimalHigh(source, target));
	}

	// 比較元 > 比較対象
	@Test
	public void case3() throws Exception {
		source = new BigDecimal(1000);
		target = new BigDecimal(999);
		assertEquals(false, ValidateUtils.isDecimalHigh(source, target));
	}

	// 比較元null
	@Test
	public void case4() throws Exception {
		source = null;
		target = new BigDecimal(0);
		assertEquals(false, ValidateUtils.isDecimalHigh(source, target));
	}

	// 比較対象null
	@Test
	public void case5() throws Exception {
		source = new BigDecimal(0);
		target = null;
		assertEquals(false, ValidateUtils.isDecimalHigh(source, target));
	}

	// どっちもnulll
	@Test
	public void case6() throws Exception {
		source = null;
		target = null;
		assertEquals(false, ValidateUtils.isDecimalHigh(source, target));
	}

}
