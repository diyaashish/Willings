package jp.loioz.common.utility;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.form.SelectOptionForm;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FormUtil_getSelectYearOptions_Test {

	//初期値
	private List<SelectOptionForm> sof;
	
	//セットアップ
	@Before
	public void setUp() {
		this.sof = new ArrayList<SelectOptionForm>();
	}
	
	/** 正常値 */
	@Test
	public void case1() throws Exception{
		List<SelectOptionForm> target = FormUtil.getSelectYearOptions(1990,1995,true,null);
		sof.add(new SelectOptionForm(String.valueOf(1990),String.valueOf(1990)));
		sof.add(new SelectOptionForm(String.valueOf(1991),String.valueOf(1991)));
		sof.add(new SelectOptionForm(String.valueOf(1992),String.valueOf(1992)));
		sof.add(new SelectOptionForm(String.valueOf(1993),String.valueOf(1993)));
		sof.add(new SelectOptionForm(String.valueOf(1994),String.valueOf(1994)));
		sof.add(new SelectOptionForm(String.valueOf(1995),String.valueOf(1995)));
		assertTrue(Objects.deepEquals(target, sof));
	}
	
	/** 昇順・降順チェック */
	@Test
	public void case2() throws Exception{
		List<SelectOptionForm> target = FormUtil.getSelectYearOptions(1990,1995,false,null);
		sof.add(new SelectOptionForm(String.valueOf(1995),String.valueOf(1995)));
		sof.add(new SelectOptionForm(String.valueOf(1994),String.valueOf(1994)));
		sof.add(new SelectOptionForm(String.valueOf(1993),String.valueOf(1993)));
		sof.add(new SelectOptionForm(String.valueOf(1992),String.valueOf(1992)));
		sof.add(new SelectOptionForm(String.valueOf(1991),String.valueOf(1991)));
		sof.add(new SelectOptionForm(String.valueOf(1990),String.valueOf(1990)));
		assertTrue(Objects.deepEquals(target, sof));
	}
	
	/** 送りがなチェック　年 */
	@Test
	public void case3() throws Exception{
		List<SelectOptionForm> target = FormUtil.getSelectYearOptions(1990,1995,true,"年");
		sof.add(new SelectOptionForm(String.valueOf(1990),String.valueOf(1990)+"年"));
		sof.add(new SelectOptionForm(String.valueOf(1991),String.valueOf(1991)+"年"));
		sof.add(new SelectOptionForm(String.valueOf(1992),String.valueOf(1992)+"年"));
		sof.add(new SelectOptionForm(String.valueOf(1993),String.valueOf(1993)+"年"));
		sof.add(new SelectOptionForm(String.valueOf(1994),String.valueOf(1994)+"年"));
		sof.add(new SelectOptionForm(String.valueOf(1995),String.valueOf(1995)+"年"));
		assertTrue(Objects.deepEquals(target, sof));
	}
	
	/** 送りがなチェック 空文字*/
	@Test
	public void case4() throws Exception{
		List<SelectOptionForm> target = FormUtil.getSelectYearOptions(1990,1995,true," ");
		sof.add(new SelectOptionForm(String.valueOf(1990),String.valueOf(1990)+" "));
		sof.add(new SelectOptionForm(String.valueOf(1991),String.valueOf(1991)+" "));
		sof.add(new SelectOptionForm(String.valueOf(1992),String.valueOf(1992)+" "));
		sof.add(new SelectOptionForm(String.valueOf(1993),String.valueOf(1993)+" "));
		sof.add(new SelectOptionForm(String.valueOf(1994),String.valueOf(1994)+" "));
		sof.add(new SelectOptionForm(String.valueOf(1995),String.valueOf(1995)+" "));
		assertTrue(Objects.deepEquals(target, sof));
	}
	
	/**  開始年 < 1 */
	@Test
	public void case5() throws Exception{
		List<SelectOptionForm> target = FormUtil.getSelectYearOptions(0,2019,true,null);
		assertTrue(target.isEmpty());
	}
	
	/**  開始年 = 終了年 */
	@Test
	public void case6() throws Exception{
		List<SelectOptionForm> target = FormUtil.getSelectYearOptions(2019,2019,true,null);
		sof.add(new SelectOptionForm(String.valueOf(2019),String.valueOf(2019)));
		assertTrue(Objects.deepEquals(target, sof));
	}
	
	/**  開始年 > 終了年 */
	@Test
	public void case7() throws Exception{
		List<SelectOptionForm> target = FormUtil.getSelectYearOptions(2019,2010,true,null);
		assertTrue(target.isEmpty());
	}
	
}
