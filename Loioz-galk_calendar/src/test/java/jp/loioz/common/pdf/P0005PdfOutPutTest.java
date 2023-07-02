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

import jp.loioz.app.common.pdf.builder.P0005PdfBuilder;
import jp.loioz.app.common.pdf.config.PdfConfig;
import jp.loioz.app.common.pdf.data.P0005Data;
import jp.loioz.common.service.pdf.PdfService;
import jp.loioz.common.utility.PdfUtils;

/**
 * Pn0005テスト出力
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class P0005PdfOutPutTest {

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
	private P0005PdfBuilder builder;

	/**
	 * テストメソッドのセットアップ処理
	 */
	@Before
	public void setUpBuilder() {
		builder = new P0005PdfBuilder();
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

		P0005Data testData = this.createTestData();
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
	private P0005Data createTestData() throws Exception {

		P0005Data testData = new P0005Data();

		testData.setOutputDate("2022年09月07日");
		testData.setAnkenName("件名：破産再生案件A");

		testData.setInstallmentsCount("11回");
		testData.setSeikyuTotal("¥ 1,000,000 -");

		testData.setPaymentDestination(PdfUtils.newLineToBreak("" +
				"ロイオズ銀行　新宿支店（支店番号●●●●）\r\n" +
				"ロイオズ銀行　新宿支店（支店番号●●●●）\r\n" +
				"普通口座　１１１１１１１　口座名義　カ）ロイオズ"));

		List<P0005Data.StatementScheduleListItem> statementScheduleLeftList = new ArrayList<P0005Data.StatementScheduleListItem>(30);
		List<P0005Data.StatementScheduleListItem> statementScheduleRightList = new ArrayList<P0005Data.StatementScheduleListItem>(30);

		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(1, "2021年09月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(1000000))));
		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(2, "2021年10月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(900000))));
		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(3, "2021年11月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(800000))));
		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(4, "2021年12月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(700000))));
		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(5, "2022年01月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(600000))));
		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(6, "2022年02月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(500000))));
		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(7, "2022年03月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(400000))));
		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(8, "2022年04月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(300000))));
		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(9, "2022年05月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(200000))));
		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(10, "2022年06月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(100000))));
		statementScheduleLeftList.add(new P0005Data.StatementScheduleListItem(11, "2022年07月01日", PdfUtils.toDispAmount(new BigDecimal(100000)), PdfUtils.toDispAmount(new BigDecimal(0))));

		// 空セルを表示するため、表示する一覧プロパティに空データの追加
		PdfUtils.appendEmptyListItem(statementScheduleLeftList, P0005Data.StatementScheduleListItem::new, P0005Data.LIST_ROW_SIZE);
		PdfUtils.appendEmptyListItem(statementScheduleRightList, P0005Data.StatementScheduleListItem::new, P0005Data.LIST_ROW_SIZE);

		testData.setStatementScheduleLeftList(statementScheduleLeftList);
		testData.setStatementScheduleRightList(statementScheduleRightList);

		return testData;
	}

}
