package jp.loioz.app.common.word.builder.naibu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import jp.loioz.app.common.word.common.AbstractMakeWordBuilder;
import jp.loioz.app.common.word.dto.naibu.Wn0009WordDto;
import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.app.common.word.templateDataEnum.naibu.Wn0009WordEnum;
import jp.loioz.common.constant.ChohyoConstatnt.OutputWordTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.SaibanTantoAccountDto;
import lombok.Setter;

/**
 * 公判期日請書を出力するBuilderクラス
 */
@Setter
public class Wn0009WordBuider extends AbstractMakeWordBuilder<Wn0009WordDto> {
	/**
	 * Builder用DTO
	 */
	private Wn0009WordDto wn0009WordDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wn0009WordDto createNewTargetBuilderDto() {
		return new Wn0009WordDto();
	}

	/**
	 * 指定のフォーマットのWordファイルを生成する
	 *
	 * @param response
	 * @throws IOException
	 * @throws Exception
	 */
	public void makeWordFile(HttpServletResponse response) throws IOException, Exception {

		super.makeWordFile(OutputWordTemplate.WORD_NAIBU_9, response, CommonConstant.BLANK);
	}

	/**
	 * Wordファイル内にデータを出力する
	 *
	 * @param doc 出力先のWordファイル情報
	 */
	@Override
	protected void setWordData(XWPFDocument doc) {

		// DtoからWordへの書き込みデータを取得
		List<WordEnum> wn0009WordEnums = new ArrayList<WordEnum>();
		wn0009WordEnums.add(Wn0009WordEnum.OUTPUT_DATE);// 出力日をadd

		// *********************************
		// ■FAX送付書
		// *********************************
		String atesaki1 = wn0009WordDto.getAtesaki1();
		String atesaki2 = wn0009WordDto.getAtesaki2();
		String faxNo = StringUtils.convertHalfToFullNum(wn0009WordDto.getFaxNo());
		String tenantZipCode = wn0009WordDto.getTenantZipCode();
		String tenantAddress1 = wn0009WordDto.getTenantAddress1();
		String tenantAddress2 = wn0009WordDto.getTenantAddress2();
		String tenantName = wn0009WordDto.getTenantName();
		String tenantTelNo = wn0009WordDto.getTenantTelNo();
		String tenantFaxNo = wn0009WordDto.getTenantFaxNo();
		String jikenNo1 = wn0009WordDto.getJikenNo1();
		String jikenName1 = wn0009WordDto.getJikenName1();
		// *********************************
		// ■期日請書
		// *********************************
		String hikokunin = wn0009WordDto.getHikokunin();
		String saibansho = wn0009WordDto.getSaibansho();
		String bengonin = wn0009WordDto.getBengonin();
		String limitDate = StringUtils.convertHalfToFullNum(wn0009WordDto.getLimitDate());

		// Dtoの値をWord書き込み用のEnumに変換
		// Enumをリストにadd
		wn0009WordEnums.add(Wn0009WordEnum.OUTPUT_DATE);
		wn0009WordEnums.add(Wn0009WordEnum.TXT_ATESAKI1.setValue(atesaki1));
		wn0009WordEnums.add(Wn0009WordEnum.TXT_FAXNO.setValue(faxNo));
		wn0009WordEnums.add(Wn0009WordEnum.TXT_TENANT_ZIPCODE.setValue(tenantZipCode));
		wn0009WordEnums.add(Wn0009WordEnum.TXT_TENANT_ADDRESS1.setValue(tenantAddress1));
		wn0009WordEnums.add(Wn0009WordEnum.TXT_TENANT_ADDRESS2.setValue(tenantAddress2));
		wn0009WordEnums.add(Wn0009WordEnum.TXT_TENANT_NAME.setValue(tenantName));
		wn0009WordEnums.add(Wn0009WordEnum.TXT_TENANT_TELNO.setValue(tenantTelNo));
		wn0009WordEnums.add(Wn0009WordEnum.TXT_TENANT_FAXNO.setValue(tenantFaxNo));

		wn0009WordEnums.add(Wn0009WordEnum.TXT_HIKOKUNIN.setValue(hikokunin));

		wn0009WordEnums.add(Wn0009WordEnum.TXT_JIKEN_NO1.setValue(jikenNo1));
		wn0009WordEnums.add(Wn0009WordEnum.TXT_JIKEN_NAME1.setValue(jikenName1));

		wn0009WordEnums.add(Wn0009WordEnum.TXT_SAIBANSHO.setValue(saibansho));

		wn0009WordEnums.add(Wn0009WordEnum.TXT_BENGONIN.setValue(bengonin));
		wn0009WordEnums.add(Wn0009WordEnum.TXT_LIMIT_DATE.setValue(limitDate));

		// データ未設定の場合の削除対象
		List<WordEnum> deleteWordEnum = new ArrayList<WordEnum>();

		// 宛先２
		WordEnum enumAtesaki2 = Wn0009WordEnum.TXT_ATESAKI2.setValue(atesaki2);
		wn0009WordEnums.add(enumAtesaki2);
		deleteWordEnum.add(enumAtesaki2);

		// 行を削除
		this.deleteRows(doc, deleteWordEnum);

		// Enumのデータをドキュメントに書き込む
		this.writeWord(doc, wn0009WordEnums, wn0009WordDto.getSaibanTantoAccountDtoList());
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
