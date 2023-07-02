package jp.loioz.app.common.word.builder.naibu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import jp.loioz.app.common.word.common.AbstractMakeWordBuilder;
import jp.loioz.app.common.word.dto.naibu.Wn0007WordDto;
import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.app.common.word.templateDataEnum.naibu.Wn0007WordEnum;
import jp.loioz.common.constant.ChohyoConstatnt.OutputWordTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.AnkenTantoAccountDto;
import lombok.Setter;

/**
 * 送付書（FAX）- 捜査機関情報、公判担当検察官情報を出力するBuilderクラス
 */
@Setter
public class Wn0007WordBuilder extends AbstractMakeWordBuilder<Wn0007WordDto> {

	/**
	 * Builder用DTO
	 */
	private Wn0007WordDto wn0007WordDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wn0007WordDto createNewTargetBuilderDto() {
		return new Wn0007WordDto();
	}

	/**
	 * 指定のフォーマットのWordファイルを生成する
	 *
	 * @param response
	 * @throws IOException
	 * @throws Exception
	 */
	public void makeWordFile(HttpServletResponse response) throws IOException, Exception {

		super.makeWordFile(OutputWordTemplate.WORD_NAIBU_7, response, CommonConstant.BLANK);
	}

	/**
	 * Wordファイル内にデータを出力する
	 *
	 * @param doc 出力先のWordファイル情報
	 */
	@Override
	protected void setWordData(XWPFDocument doc) {

		// DtoからWordへの書き込みデータを取得
		String id = wn0007WordDto.getId();
		String zipCode = wn0007WordDto.getTenantZipCode();
		String atesaki1 = wn0007WordDto.getAtesaki1();
		String atesaki2 = wn0007WordDto.getAtesaki2();
		String atesaki3 = wn0007WordDto.getAtesaki3();
		String faxNo = StringUtils.convertHalfToFullNum(wn0007WordDto.getFaxNo());
		String createdAt = wn0007WordDto.getCreatedAt();
		String tenantZipCode = wn0007WordDto.getTenantZipCode();
		String tenantAddress1 = wn0007WordDto.getTenantAddress1();
		String tenantAddress2 = wn0007WordDto.getTenantAddress2();
		String tenantName = wn0007WordDto.getTenantName();
		String tenantTelNo = wn0007WordDto.getTenantTelNo();
		String tenantFaxNo = wn0007WordDto.getTenantFaxNo();

		// Enumをリストにまとめる
		List<WordEnum> wn0007WordEnums = new ArrayList<>();

		// Dtoの値をWord書き込み用のEnumに変換
		wn0007WordEnums.add(Wn0007WordEnum.TXT_ID.setValue(id));
		wn0007WordEnums.add(Wn0007WordEnum.TXT_TENANT_ZIPCODE.setValue(zipCode));
		wn0007WordEnums.add(Wn0007WordEnum.TXT_ATESAKI1.setValue(atesaki1));
		wn0007WordEnums.add(Wn0007WordEnum.TXT_FAXNO.setValue(faxNo));
		wn0007WordEnums.add(Wn0007WordEnum.TXT_CREATED_AT.setValue(createdAt));
		wn0007WordEnums.add(Wn0007WordEnum.TXT_TENANT_ZIPCODE.setValue(tenantZipCode));
		wn0007WordEnums.add(Wn0007WordEnum.TXT_TENANT_ADDRESS1.setValue(tenantAddress1));
		wn0007WordEnums.add(Wn0007WordEnum.TXT_TENANT_ADDRESS2.setValue(tenantAddress2));
		wn0007WordEnums.add(Wn0007WordEnum.TXT_TENANT_NAME.setValue(tenantName));
		wn0007WordEnums.add(Wn0007WordEnum.TXT_TENANT_TELNO.setValue(tenantTelNo));
		wn0007WordEnums.add(Wn0007WordEnum.TXT_TENANT_FAXNO.setValue(tenantFaxNo));

		// 空文字の場合、削除するKEYを設定
		List<WordEnum> deleteWordEnum = new ArrayList<WordEnum>();

		// 宛先2
		WordEnum enumAtesaki2 = Wn0007WordEnum.TXT_ATESAKI2.setValue(atesaki2);
		wn0007WordEnums.add(enumAtesaki2);
		deleteWordEnum.add(enumAtesaki2);

		// 宛先3
		WordEnum enumAtesaki3 = Wn0007WordEnum.TXT_ATESAKI3.setValue(atesaki3);
		wn0007WordEnums.add(enumAtesaki3);
		deleteWordEnum.add(enumAtesaki3);

		// 行を削除
		this.deleteRows(doc, deleteWordEnum);

		// Enumのデータをドキュメントに書き込む
		this.writeWord(doc, wn0007WordEnums, wn0007WordDto.getAnkenTantoAccountDtoList());
	}

	/**
	 * Wordファイルに指定のデータを書き込む（置換する）
	 *
	 * @param doc
	 * @param datas
	 * @param tantoLawyerList
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