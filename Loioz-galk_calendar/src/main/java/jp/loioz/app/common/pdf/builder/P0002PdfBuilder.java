package jp.loioz.app.common.pdf.builder;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import jp.loioz.app.common.pdf.common.AbstractMakePdfBuilder;
import jp.loioz.app.common.pdf.data.P0002Data;
import jp.loioz.common.constant.ChohyoConstatnt.OutputPdfTemplate;
import jp.loioz.dto.FileContentsDto;
import lombok.Setter;

/**
 * 精算書PDF出力のBuilderクラス
 */
@Setter
public class P0002PdfBuilder extends AbstractMakePdfBuilder<P0002Data> {

	/**
	 * 精算書PDFを作成し、HttpResponseに書き込みを行う
	 * 
	 * @param response
	 * @throws IOException
	 * @throws Exception
	 */
	public void makePdfFile(HttpServletResponse response) throws IOException, Exception {
		super.makePdfFile(OutputPdfTemplate.PDF_2, response);
	}

	/**
	 * 精算書PDFを作成し、バイトデータを返却する
	 *
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public FileContentsDto makePdfContentsDto() throws IOException, Exception {
		return super.makePdfContentsDto(OutputPdfTemplate.PDF_2);
	}

	// ================================================================
	// 以下、帳票テスト出力用メソッド
	// PDFテンプレートの作成・修正時に画面操作を行わずに出力処理を行うために作成
	// ================================================================

	/**
	 * PDFテスト出力用メソッド<br>
	 * 本メソッドは開発時のみ利用することを想定してため、JUnitによるテストコードから呼び出す
	 * 作成されたPDFはsrc/main/resources/pdf/testOutput内に格納される
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	public void makePdfFileTest() throws IOException, Exception {
		super.makePdfFileTest(OutputPdfTemplate.PDF_2);
	}

}
