package jp.loioz.app.common.word.builder.naibu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import jp.loioz.app.common.word.common.AbstractMakeWordBuilder;
import jp.loioz.app.common.word.dto.naibu.Wn0006WordDto;
import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.app.common.word.templateDataEnum.naibu.Wn0006WordEnum;
import jp.loioz.common.constant.ChohyoConstatnt.OutputWordTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.dto.AnkenTantoAccountDto;
import lombok.Setter;

/**
 * 送付書（一般）- 捜査機関情報、公判担当検察官情報を出力するBuilderクラス
 */
@Setter
public class Wn0006WordBuilder extends AbstractMakeWordBuilder<Wn0006WordDto> {

	/**
	 * Builder用DTO
	 */
	private Wn0006WordDto wn0006WordDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wn0006WordDto createNewTargetBuilderDto() {
		return new Wn0006WordDto();
	}

	/**
	 * 指定のフォーマットのWordファイルを生成する
	 *
	 * @param response
	 * @throws IOException
	 * @throws Exception
	 */
	public void makeWordFile(HttpServletResponse response) throws IOException, Exception {

		super.makeWordFile(OutputWordTemplate.WORD_NAIBU_6, response, CommonConstant.BLANK);
	}

	/**
	 * Wordファイル内にデータを出力する
	 *
	 * @param doc 出力先のWordファイル情報
	 */
	@Override
	protected void setWordData(XWPFDocument doc) {

		// DtoからWordへの書き込みデータを取得
		String id = wn0006WordDto.getId();
		String zipCode = wn0006WordDto.getZipCode();
		String address1 = wn0006WordDto.getAddress1();
		String address2 = wn0006WordDto.getAddress2();
		String atesaki1 = wn0006WordDto.getAtesaki1();
		String atesaki2 = wn0006WordDto.getAtesaki2();
		String atesaki3 = wn0006WordDto.getAtesaki3();
		String createdAt = wn0006WordDto.getCreatedAt();
		String tenantZipCode = wn0006WordDto.getTenantZipCode();
		String tenantAddress1 = wn0006WordDto.getTenantAddress1();
		String tenantAddress2 = wn0006WordDto.getTenantAddress2();
		String tenantName = wn0006WordDto.getTenantName();
		String tenantTelNo = wn0006WordDto.getTenantTelNo();
		String tenantFaxNo = wn0006WordDto.getTenantFaxNo();

		// Enumをリストにまとめる
		List<WordEnum> wn0006WordEnums = new ArrayList<>();

		// Dtoの値をWord書き込み用のEnumに変換
		wn0006WordEnums.add(Wn0006WordEnum.TXT_ID.setValue(id));
		wn0006WordEnums.add(Wn0006WordEnum.TXT_ZIPCODE.setValue(zipCode));
		wn0006WordEnums.add(Wn0006WordEnum.TXT_ADDRESS1.setValue(address1));
		wn0006WordEnums.add(Wn0006WordEnum.TXT_ATESAKI1.setValue(atesaki1));
		wn0006WordEnums.add(Wn0006WordEnum.TXT_CREATED_AT.setValue(createdAt));
		wn0006WordEnums.add(Wn0006WordEnum.TXT_TENANT_ZIPCODE.setValue(tenantZipCode));
		wn0006WordEnums.add(Wn0006WordEnum.TXT_TENANT_ADDRESS1.setValue(tenantAddress1));
		wn0006WordEnums.add(Wn0006WordEnum.TXT_TENANT_ADDRESS2.setValue(tenantAddress2));
		wn0006WordEnums.add(Wn0006WordEnum.TXT_TENANT_NAME.setValue(tenantName));
		wn0006WordEnums.add(Wn0006WordEnum.TXT_TENANT_TELNO.setValue(tenantTelNo));
		wn0006WordEnums.add(Wn0006WordEnum.TXT_TENANT_FAXNO.setValue(tenantFaxNo));

		// 空文字の場合、削除するKEYを設定
		List<WordEnum> deleteWordEnum = new ArrayList<WordEnum>();

		// 住所２
		WordEnum enumAddress2 = Wn0006WordEnum.TXT_ADDRESS2.setValue(address2);
		wn0006WordEnums.add(enumAddress2);
		deleteWordEnum.add(enumAddress2);

		// 宛先2
		WordEnum enumAtesaki2 = Wn0006WordEnum.TXT_ATESAKI2.setValue(atesaki2);
		wn0006WordEnums.add(enumAtesaki2);
		deleteWordEnum.add(enumAtesaki2);

		// 宛先3
		WordEnum enumAtesaki3 = Wn0006WordEnum.TXT_ATESAKI3.setValue(atesaki3);
		wn0006WordEnums.add(enumAtesaki3);
		deleteWordEnum.add(enumAtesaki3);

		// 行を削除
		this.deleteRows(doc, deleteWordEnum);

		// Enumのデータをドキュメントに書き込む
		this.writeWord(doc, wn0006WordEnums, wn0006WordDto.getAnkenTantoAccountDtoList());
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