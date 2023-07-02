package jp.loioz.common.pdf;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import jp.loioz.app.common.pdf.builder.P0003PdfBuilder;
import jp.loioz.app.common.pdf.config.PdfConfig;
import jp.loioz.app.common.pdf.data.P0003Data;
import jp.loioz.common.service.pdf.PdfService;

/**
 * Pn0003テスト出力
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class P0003PdfOutPutTest {

	/** PDFConfig */
	@Autowired
	private PdfConfig config;

	/** PDFBoxService */
	@Autowired
	private PdfService pdfService;

	// =========================================================
	// 出力セットアップ項目
	// =========================================================

	/** builderクラス */
	private P0003PdfBuilder builder;

	/**
	 * テストメソッドのセットアップ処理
	 */
	@Before
	public void setUpBuilder() {
		builder = new P0003PdfBuilder();
		builder.setConfig(config);
		builder.setPdfService(pdfService);
	}

	/**
	 * PDFのテスト出力処理
	 *
	 * @throws Exception
	 */
	@Test
	public void testMakePdfFile() throws Exception {

		P0003Data testData = this.createTestData();
		builder.setPdfData(testData);

		try {
			builder.makePdfFileTest();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * テストデータの作成
	 * 
	 * @return
	 */
	private P0003Data createTestData() throws Exception {

		P0003Data testData = new P0003Data();

		testData.setOutputDate("2022年09月07日");
		testData.setAnkenName("件名：破産再生案件A");

		testData.setNyukinTotal("¥ 1,602,482");
		testData.setShukkinTotal("¥ 1,602,482");
		testData.setNyushukkinTotal("¥ 0");

		List<P0003Data.JippiListItem> jippiList = new ArrayList<>();
		jippiList.add(this.createItemData("2022年09月07日", "実費預り金", "2,000", "", "摘要"));
		jippiList.add(this.createItemData("2022年09月08日", "実費預り金", "999,999,999", "", "摘要"));
		jippiList.add(this.createItemData("2022年09月09日", "交通費", "", "10,000", "摘要"));
		jippiList.add(this.createItemData("2022年09月10日", "印紙", "1,000", "", "摘要"));
		jippiList.add(this.createItemData("2022年09月11日", "印紙", "1,000", "", "摘要"));

		testData.setJippiList(jippiList);

		return testData;
	}

	/**
	 * 実費明細項目テストデータの作成
	 * 
	 * @param date
	 * @param name
	 * @param isFree
	 * @param nyukin
	 * @param shukkin
	 * @param remarks
	 * @return
	 */
	private P0003Data.JippiListItem createItemData(String date, String name, String nyukin, String shukkin, String remarks) {
		var item = new P0003Data.JippiListItem();
		item.setJippiDate(date);
		item.setKomokuName(name);
		item.setNyukinGaku(nyukin);
		item.setShukkinGaku(shukkin);
		item.setTekiyo(remarks);
		return item;
	}

}
