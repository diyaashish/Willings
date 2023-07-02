package jp.loioz.app.common.word.builder.naibu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;

import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.app.common.word.common.AbstractMakeWordBuilder;
import jp.loioz.app.common.word.dto.naibu.Wn0008WordDto;
import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.app.common.word.templateDataEnum.naibu.Wn0008WordEnum;
import jp.loioz.common.constant.ChohyoConstatnt.OutputWordTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.SaibanTantoAccountDto;
import lombok.Setter;

/**
 * 口頭弁論期日請書を出力するBuilderクラス
 */
@Setter
public class Wn0008WordBuider extends AbstractMakeWordBuilder<Wn0008WordDto> {

	/**
	 * Builder用DTO
	 */
	private Wn0008WordDto wn0008WordDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wn0008WordDto createNewTargetBuilderDto() {
		return new Wn0008WordDto();
	}

	/** 帳票共通サービス */
	@Autowired
	CommonChohyoService commonChohyoService;

	/**
	 * 指定のフォーマットのWordファイルを生成する
	 *
	 * @param response
	 * @throws IOException
	 * @throws Exception
	 */
	public void makeWordFile(HttpServletResponse response) throws IOException, Exception {

		super.makeWordFile(OutputWordTemplate.WORD_NAIBU_8, response, CommonConstant.BLANK);
	}

	/**
	 * Wordファイル内にデータを出力する
	 *
	 * @param doc 出力先のWordファイル情報
	 */
	@Override
	protected void setWordData(XWPFDocument doc) {

		// ■FAX送付書
		String atesaki1 = wn0008WordDto.getAtesaki1();
		String atesaki2 = wn0008WordDto.getAtesaki2();
		String faxNo = StringUtils.convertHalfToFullNum(wn0008WordDto.getFaxNo());
		String tenantZipCode = wn0008WordDto.getTenantZipCode();
		String tenantAddress1 = wn0008WordDto.getTenantAddress1();
		String tenantAddress2 = wn0008WordDto.getTenantAddress2();
		String tenantName = wn0008WordDto.getTenantName();
		String tenantTelNo = wn0008WordDto.getTenantTelNo();
		String tenantFaxNo = wn0008WordDto.getTenantFaxNo();

		String jikenNo1 = wn0008WordDto.getJikenNo1();
		String jikenName1 = wn0008WordDto.getJikenName1();

		// ■期日請書
		String tojishaUp = wn0008WordDto.getTojishaUp();
		String tojishaMid = wn0008WordDto.getTojishaMid();
		String tojishaLow = wn0008WordDto.getTojishaLow();
		String saibansho = wn0008WordDto.getSaibansho();

		String tojishaDairinin = wn0008WordDto.getTojishaDairinin();
		String limitCount = wn0008WordDto.getLimitCount();
		String limitDate = StringUtils.convertHalfToFullNum(wn0008WordDto.getLimitDate());

		// Enumをリストにまとめる
		List<WordEnum> wn0008WordEnums = new ArrayList<WordEnum>();
		wn0008WordEnums.add(Wn0008WordEnum.TXT_OUTPUT_DATE.setValue(DateUtils.getDateToJaDate()));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_ATESAKI1.setValue(atesaki1));

		wn0008WordEnums.add(Wn0008WordEnum.TXT_FAXNO.setValue(faxNo));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_TENANT_ZIPCODE.setValue(tenantZipCode));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_TENANT_ADDRESS1.setValue(tenantAddress1));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_TENANT_ADDRESS2.setValue(tenantAddress2));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_TENANT_NAME.setValue(tenantName));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_TENANT_TELNO.setValue(tenantTelNo));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_TENANT_FAXNO.setValue(tenantFaxNo));

		wn0008WordEnums.add(Wn0008WordEnum.TXT_JIKEN_NO1.setValue(jikenNo1));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_JIKEN_NAME1.setValue(jikenName1));

		wn0008WordEnums.add(Wn0008WordEnum.TXT_TOJISHA_UP.setValue(tojishaUp));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_TOJISHA_MID.setValue(tojishaMid));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_TOJISHA_LOW.setValue(tojishaLow));

		wn0008WordEnums.add(Wn0008WordEnum.TXT_SAIBANSHO.setValue(saibansho));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_TOJISHA_DAIRININ.setValue(tojishaDairinin));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_LIMIT_COUNT.setValue(limitCount));
		wn0008WordEnums.add(Wn0008WordEnum.TXT_LIMIT_DATE.setValue(limitDate));

		// データ未設定の場合の削除対象
		List<WordEnum> deleteWordEnum = new ArrayList<WordEnum>();

		// 宛先２
		WordEnum enumAtesaki2 = Wn0008WordEnum.TXT_ATESAKI2.setValue(atesaki2);
		wn0008WordEnums.add(enumAtesaki2);
		deleteWordEnum.add(enumAtesaki2);

		// 行を削除
		this.deleteRows(doc, deleteWordEnum);

		// Enumのデータをドキュメントに書き込む
		this.writeWord(doc, wn0008WordEnums, wn0008WordDto.getSaibanTantoAccountDtoList());
	}

	/**
	 * Wordファイルに指定のデータを書き込む（置換する）
	 *
	 * @param doc
	 * @param datas
	 * @param saibanTantoAccountDtoList
	 * @return 書き込み（置換）が完了したドキュメント
	 */
	public XWPFDocument writeWord(XWPFDocument doc, List<WordEnum> datas, List<SaibanTantoAccountDto> saibanTantoAccountDtoList) {

		// 通常の書き込み処理
		writeWord(doc, datas);

		// 書き込みテーブルの準備
		int rowSize = saibanTantoAccountDtoList.size();

		// テーブルの取得
		XWPFTable table = doc.getTableArray(0);
		if (table != null) {

			// 行コピー
			List<XWPFTableRow> tableRowList = tableRowCopy(table, rowSize);

			// 担当弁護士、担当事務の書き込み処理
			writeDocxTantoForSaiban(table, rowSize, tableRowList, saibanTantoAccountDtoList);

		}

		return doc;
	}

}
