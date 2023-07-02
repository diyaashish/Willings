package jp.loioz.common.utility;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class DateUtils_parseDateToJaDateTest {

	private SimpleDateFormat sdf;

	@Before
	public void setUp() {
		sdf = new SimpleDateFormat("yyyy/MM/dd");
	}

	@Test
	public void case1() throws ParseException {
		// 大正初め
		Date target = sdf.parse("1912/7/30");
		assertThat(DateUtils.parseDateToJaDate(target), is("大正1年7月30日"));
	}

	@Test
	public void case2() throws ParseException {
		// 大正終わり
		Date target = sdf.parse("1926/12/24");
		assertThat(DateUtils.parseDateToJaDate(target), is("大正15年12月24日"));
	}

	@Test
	public void case3() throws ParseException {
		// 昭和初め
		Date target = sdf.parse("1926/12/25");
		assertThat(DateUtils.parseDateToJaDate(target), is("昭和1年12月25日"));
	}

	@Test
	public void case4() throws ParseException {
		// 昭和終わり
		Date target = sdf.parse("1989/1/7");
		assertThat(DateUtils.parseDateToJaDate(target), is("昭和64年1月7日"));
	}

	@Test
	public void case5() throws ParseException {
		// 平成初め
		Date target = sdf.parse("1989/1/8");
		assertThat(DateUtils.parseDateToJaDate(target), is("平成1年1月8日"));
	}

	@Test
	public void case6() throws ParseException {
		// 令和初め
		Date target = sdf.parse("2019/5/1");
		assertThat(DateUtils.parseDateToJaDate(target), is("令和1年5月1日"));
	}

}
