package jp.loioz.common.pdf;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import jp.loioz.app.common.pdf.builder.P0002PdfBuilder;
import jp.loioz.app.common.pdf.config.PdfConfig;
import jp.loioz.app.common.pdf.data.P0002Data;
import jp.loioz.common.service.pdf.PdfService;
import jp.loioz.common.utility.PdfUtils;

/**
 * Pn0002テスト出力
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class P0002PdfOutPutTest {

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
	private P0002PdfBuilder builder;

	/**
	 * テストメソッドのセットアップ処理
	 */
	@Before
	public void setUpBuilder() {
		builder = new P0002PdfBuilder();
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

		P0002Data testData = this.createTestData();
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
	private P0002Data createTestData() throws Exception {

		P0002Data testData = new P0002Data();

		testData.setTitle("精算書");
		testData.setOutputDate("2022年09月07日");
		testData.setSeisanNo("123456789");
		testData.setAtesakiName("テスト太郎　様 ");
		testData.setAtesakiDetail(PdfUtils.newLineToBreak("" +
				"〒100－0011\r\n" +
				"東京都新宿区新宿１－１－１\r\n" +
				"ｘｘｘｘｘｘｘｘビル７階\r\n" +
				"ご担当者様"));
		testData.setJimushoName("弁護士テスト事務所");
		testData.setLawyerName("弁護士一郎");

		testData.setSofumotoDetail(PdfUtils.newLineToBreak("" +
				"〒100－0011\r\n" +
				"東京都新宿区新宿１－１－１\r\n" +
				"ｘｘｘｘｘｘｘｘビル７階\r\n" +
				"電話：03-1111-1111\r\n" +
				"FAX：03-2222-2222\r\n" +
				"登録番号：T01234567810"));
		testData.setAnkenName("件名：破産再生案件A");
		testData.setSeisanKingaku("¥ 1,602,482 -");
		testData.setShiharaiLimitDate("2024年12年31日");
		testData.setPaymentDestination(PdfUtils.newLineToBreak("" +
				"ロイオズ銀行　新宿支店（支店番号●●●●）\r\n" +
				"普通口座　１１１１１１１　口座名義　カ）ロイオズ"));

//		testData.setStamp(getTestStamp());

		List<P0002Data.AzukariKingakuListItem> azukariKingakuList = new ArrayList<>();
		azukariKingakuList.add(new P0002Data.AzukariKingakuListItem("2021年09月01日", "預り金", "テストA", "1,000"));
		azukariKingakuList.add(new P0002Data.AzukariKingakuListItem("2021年09月03日", "預り金", "", "3,000"));
		azukariKingakuList.add(new P0002Data.AzukariKingakuListItem("2022年09月06日", "預り金", "テストF", "6,000"));
		testData.setAzukariKingakuList(azukariKingakuList);
		testData.setAzukariKingakuTotal("¥ 21,000");

		List<P0002Data.HoshuJippiKingakuListItem> hoshuJippiKingakuList = new ArrayList<>();
		hoshuJippiKingakuList.add(new P0002Data.HoshuJippiKingakuListItem("2021年09月01日", "日当", "テストA", "1,000"));
		hoshuJippiKingakuList.add(new P0002Data.HoshuJippiKingakuListItem("2021年09月02日", "成功報酬", "テストB", "2,000"));
		hoshuJippiKingakuList.add(new P0002Data.HoshuJippiKingakuListItem("2022年09月05日", "実費", "", "3,000"));
		hoshuJippiKingakuList.add(new P0002Data.HoshuJippiKingakuListItem("2022年09月06日", "着手金", "テストD", "4,000"));
		testData.setHoshuJippiKingakuList(hoshuJippiKingakuList);

		testData.setHoshuJippiSubTotal("¥ 1,354,513");
		testData.setHoshuJippiTaxTotal("¥ 21,542");
		testData.setHoshuJippiTotal("¥ 6,521,356");
		testData.setSeisanTotal("¥ 8,163,513");
		testData.setTenPercentTotal("321,213");
		testData.setTenPercentTaxTotal("52,321");
		testData.setEightPercentTotal("65,423");
		testData.setEightPercentTaxTotal("5,421");

		testData.setRemarks(PdfUtils.newLineToBreak("" +
				"ｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘ\r\n" +
				"ｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘ\r\n" +
				"ｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘ\r\n" +
				"ｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘｘ"));

		return testData;
	}

// テストで画像を読み込みたい場合にコメントアウトを外して使用する。
// （※テストメソッドで利用していない状態だと、未利用の警告が表示されるのでコメントアウトしている。）
//
//	private byte[] getTestStamp() {
//
//		File file = new File(".\\src\\main\\resources\\static\\img\\person_1.png");
//		try (InputStream is = new FileInputStream(file);) {
//			return is.readAllBytes();
//		} catch (Exception ex) {
//			throw new RuntimeException(ex);
//		}
//	}

}
