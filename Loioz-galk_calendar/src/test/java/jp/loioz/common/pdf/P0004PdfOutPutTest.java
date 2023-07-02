package jp.loioz.common.pdf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import jp.loioz.app.common.pdf.builder.P0004PdfBuilder;
import jp.loioz.app.common.pdf.config.PdfConfig;
import jp.loioz.app.common.pdf.data.P0004Data;
import jp.loioz.common.service.pdf.PdfService;
import jp.loioz.common.utility.PdfUtils;

/**
 * Pn0004テスト出力
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class P0004PdfOutPutTest {

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
	private P0004PdfBuilder builder;

	/**
	 * テストメソッドのセットアップ処理
	 */
	@Before
	public void setUpBuilder() {
		builder = new P0004PdfBuilder();
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

		P0004Data testData = this.createTestData();
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
	private P0004Data createTestData() throws Exception {

		P0004Data testData = new P0004Data();

		testData.setOutputDate("2022年09月07日");
		testData.setAnkenName("件名：破産再生案件A");

		testData.setTimeChargeTotalMinutes("420分");
		testData.setTimeChargeTotalAmount(PdfUtils.toDispTotalAmount(new BigDecimal(70000)));

		List<P0004Data.TimeChargeListItem> timeChargeList = new ArrayList<>();
		timeChargeList.add(new P0004Data.TimeChargeListItem("2021年09月15日", PdfUtils.newLineToBreak("活動内容"), "30", PdfUtils.toDispAmount(new BigDecimal(10000)), PdfUtils.toDispAmount(new BigDecimal(5000))));
		timeChargeList.add(new P0004Data.TimeChargeListItem("2022年02月01日", PdfUtils.newLineToBreak("メール対応"), "90", PdfUtils.toDispAmount(new BigDecimal(10000)), PdfUtils.toDispAmount(new BigDecimal(15000))));
		timeChargeList.add(new P0004Data.TimeChargeListItem("2022年03月10日", PdfUtils.newLineToBreak("あああ\r\nあああ"), "60", PdfUtils.toDispAmount(new BigDecimal(10000)), PdfUtils.toDispAmount(new BigDecimal(10000))));
		timeChargeList.add(new P0004Data.TimeChargeListItem("2022年04月16日", PdfUtils.newLineToBreak("メール対応"), "120", PdfUtils.toDispAmount(new BigDecimal(10000)), PdfUtils.toDispAmount(new BigDecimal(20000))));
		timeChargeList.add(new P0004Data.TimeChargeListItem("2022年07月03日", PdfUtils.newLineToBreak("メール対応"), "120", PdfUtils.toDispAmount(new BigDecimal(10000)), PdfUtils.toDispAmount(new BigDecimal(20000))));

		testData.setTimeChargeList(timeChargeList);

		return testData;
	}

}
