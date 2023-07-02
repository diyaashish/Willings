package jp.loioz.app.common.word.builder.naibu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import jp.loioz.app.common.word.common.AbstractMakeWordBuilder;
import jp.loioz.app.common.word.dto.naibu.Wn0004WordDto;
import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.app.common.word.templateDataEnum.naibu.Wn0004WordEnum;
import jp.loioz.common.constant.ChohyoConstatnt.OutputWordTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.AnkenTantoAccountDto;
import lombok.Setter;

/**
 * 送付書（FAX用）を出力するBuilderクラス
 */
@Setter
public class Wn0004WordBuilder extends AbstractMakeWordBuilder<Wn0004WordDto> {

	/**
	 * Builder用DTO
	 */
	private Wn0004WordDto wn0004WordDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wn0004WordDto createNewTargetBuilderDto() {
		return new Wn0004WordDto();
	}

	/**
	 * 指定のフォーマットのWordファイルを生成する
	 *
	 * @param response
	 * @throws IOException
	 * @throws Exception
	 */
	public void makeWordFile(HttpServletResponse response) throws IOException, Exception {

		super.makeWordFile(OutputWordTemplate.WORD_NAIBU_4, response, CommonConstant.BLANK);
	}

	/**
	 * Wordファイル内にデータを出力する
	 *
	 * @param doc 出力先のWordファイル情報
	 */
	@Override
	protected void setWordData(XWPFDocument doc) {

		// DtoからWordへの書き込みデータを取得
		String id = wn0004WordDto.getId();
		String zipCode = wn0004WordDto.getTenantZipCode();
		String atesaki1 = wn0004WordDto.getAtesaki1();
		String atesaki2 = wn0004WordDto.getAtesaki2();
		String faxNo = StringUtils.convertHalfToFullNum(wn0004WordDto.getFaxNo());
		String createdAt = wn0004WordDto.getCreatedAt();
		String tenantZipCode = wn0004WordDto.getTenantZipCode();
		String tenantAddress1 = wn0004WordDto.getTenantAddress1();
		String tenantAddress2 = wn0004WordDto.getTenantAddress2();
		String tenantName = wn0004WordDto.getTenantName();
		String tenantTelNo = wn0004WordDto.getTenantTelNo();
		String tenantFaxNo = wn0004WordDto.getTenantFaxNo();

		// Enumをリストにまとめる
		List<WordEnum> wn0004WordEnums = new ArrayList<>();

		// Dtoの値をWord書き込み用のEnumに変換
		wn0004WordEnums.add(Wn0004WordEnum.TXT_ID.setValue(id));
		wn0004WordEnums.add(Wn0004WordEnum.TXT_TENANT_ZIPCODE.setValue(zipCode));
		wn0004WordEnums.add(Wn0004WordEnum.TXT_ATESAKI1.setValue(atesaki1));
		wn0004WordEnums.add(Wn0004WordEnum.TXT_FAXNO.setValue(faxNo));
		wn0004WordEnums.add(Wn0004WordEnum.TXT_CREATED_AT.setValue(createdAt));
		wn0004WordEnums.add(Wn0004WordEnum.TXT_TENANT_ZIPCODE.setValue(tenantZipCode));
		wn0004WordEnums.add(Wn0004WordEnum.TXT_TENANT_ADDRESS1.setValue(tenantAddress1));
		wn0004WordEnums.add(Wn0004WordEnum.TXT_TENANT_ADDRESS2.setValue(tenantAddress2));
		wn0004WordEnums.add(Wn0004WordEnum.TXT_TENANT_NAME.setValue(tenantName));
		wn0004WordEnums.add(Wn0004WordEnum.TXT_TENANT_TELNO.setValue(tenantTelNo));
		wn0004WordEnums.add(Wn0004WordEnum.TXT_TENANT_FAXNO.setValue(tenantFaxNo));

		// 空文字の場合、削除するKEYを設定
		List<WordEnum> deleteWordEnum = new ArrayList<WordEnum>();

		// 宛先2
		WordEnum enumAtesaki2 = Wn0004WordEnum.TXT_ATESAKI2.setValue(atesaki2);
		wn0004WordEnums.add(enumAtesaki2);
		deleteWordEnum.add(enumAtesaki2);

		// 行を削除
		this.deleteRows(doc, deleteWordEnum);

		// Enumのデータをドキュメントに書き込む
		this.writeWord(doc, wn0004WordEnums, wn0004WordDto.getAnkenTantoAccountDtoList());
	}

	/**
	 * Wordファイルに指定のデータを書き込む（置換する）
	 *
	 * @param doc
	 * @param datas
	 * @param ankenTantoAccountDtoList
	 * @return 書き込み（置換）が完了したドキュメント
	 */
	public XWPFDocument writeWord(XWPFDocument doc, List<WordEnum> datas, List<AnkenTantoAccountDto> ankenTantoAccountDtoList) {

		// 通常の書き込み処理
		writeWord(doc, datas);

		// 書き込みテーブルの準備
		int rowSize = ankenTantoAccountDtoList.size();

		// テーブルの取得
		XWPFTable table = doc.getTableArray(0);
		if (table != null) {

			// 行コピー
			List<XWPFTableRow> tableRowList = tableRowCopy(table, rowSize);

			// 担当弁護士、担当事務の書き込み処理
			writeDocxTanto(table, rowSize, tableRowList, ankenTantoAccountDtoList);

		}
		return doc;
	}
}