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
import jp.loioz.app.common.word.dto.naibu.Wn0003WordDto;
import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.app.common.word.templateDataEnum.naibu.Wn0003WordEnum;
import jp.loioz.common.constant.ChohyoConstatnt.OutputWordTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.dto.AnkenTantoAccountDto;
import lombok.Setter;

/**
 * ========== | Builder作成者が実装する必要があるクラス・メソッド |==========
 *
 * <pre>
 * ■1.Builder（新規作成）Wordファイルの作成、出力処理を行うクラス。
 * 下記のメソッドを実装する。
 * ーmakeWordFile Wordファイルの生成 ー
 * setWordDataDtoが保持するデータをEnumに設定し、Wordへの書き込み処理をコールする
 *
 * ■2.Dto（新規作成） Builderの外の処理からBuilderに書き込みデータを渡すためのDataTransferObject
 * Wordに書き込むデータを保持するプロパティを用意する。
 *
 * ■3.Enum（新規作成） 共通のWord書き込み処理へ引数として渡すクラス
 * Wordのテンプレートファイルに予め記載をしておくKey文字列と、Key文字列と置換を行う書き込み値を保持する
 *
 * ■4.OutputWordTemplate（追記）
 * 利用するテンプレートファイル名などの情報を定義する。
 * </pre>
 *
 * ========== | 呼び出し側のプログラムでの実装例 | ==========
 *
 * <pre>
 * ■1.BuilderとDTOを定義（必須）
 * Wn0003WordBuilder wn0003WordBuilder = new WN0003WordBuilder();
 * Wn0003WordDto wn0003WordDto = wn0003WordBuilder.createNewTargetBuilderDto();
 *
 * ■2.WordConfigを設定（必須）
 * wn0003WordBuilder.setConfig(wordConfig);
 *
 * ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定する（必須）
 * wn0003WordDto.setValue(value);
 * wn0003WordBuilder.wn0003WordDto(wn0003WordDto);
 *
 * ■4.Excel作成(必須)
 * try {
 * 	wn0003WordBuilder.makeWordFile(response);
 * } catch (Exception e) {
 * 	// ※サンプルのため、簡易的なエラーハンドリング
 * 	logger.error("Wordファイルの生成に失敗しました。");
 * }
 * </pre>
 *
 * ============================================
 */

/**
 * 送付書（一般）を出力するBuilderクラス
 */
@Setter
public class Wn0003WordBuilder extends AbstractMakeWordBuilder<Wn0003WordDto> {

	/**
	 * Builder用DTO
	 */
	private Wn0003WordDto wn0003WordDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wn0003WordDto createNewTargetBuilderDto() {
		return new Wn0003WordDto();
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

		super.makeWordFile(OutputWordTemplate.WORD_NAIBU_3, response, CommonConstant.BLANK);
	}

	/**
	 * Wordファイル内にデータを出力する
	 *
	 * @param doc 出力先のWordファイル情報
	 */
	@Override
	protected void setWordData(XWPFDocument doc) {

		// DtoからWordへの書き込みデータを取得
		String id = wn0003WordDto.getId();
		String zipCode = wn0003WordDto.getZipCode();
		String address1 = wn0003WordDto.getAddress1();
		String address2 = wn0003WordDto.getAddress2();
		String atesaki1 = wn0003WordDto.getAtesaki1();
		String atesaki2 = wn0003WordDto.getAtesaki2();
		String createdAt = wn0003WordDto.getCreatedAt();
		String tenantZipCode = wn0003WordDto.getTenantZipCode();
		String tenantAddress1 = wn0003WordDto.getTenantAddress1();
		String tenantAddress2 = wn0003WordDto.getTenantAddress2();
		String tenantName = wn0003WordDto.getTenantName();
		String tenantTelNo = wn0003WordDto.getTenantTelNo();
		String tenantFaxNo = wn0003WordDto.getTenantFaxNo();

		// Dtoの値をWord書き込み用のEnumに変換
		List<WordEnum> wn0003WordEnums = new ArrayList<WordEnum>();

		// Enumをリストにまとめる
		wn0003WordEnums.add(Wn0003WordEnum.TXT_ID.setValue(id));
		wn0003WordEnums.add(Wn0003WordEnum.TXT_ZIPCODE.setValue(zipCode));
		wn0003WordEnums.add(Wn0003WordEnum.TXT_ADDRESS1.setValue(address1));
		wn0003WordEnums.add(Wn0003WordEnum.TXT_ATESAKI1.setValue(atesaki1));

		wn0003WordEnums.add(Wn0003WordEnum.TXT_CREATED_AT.setValue(createdAt));
		wn0003WordEnums.add(Wn0003WordEnum.TXT_TENANT_ZIPCODE.setValue(tenantZipCode));
		wn0003WordEnums.add(Wn0003WordEnum.TXT_TENANT_ADDRESS1.setValue(tenantAddress1));
		wn0003WordEnums.add(Wn0003WordEnum.TXT_TENANT_ADDRESS2.setValue(tenantAddress2));
		wn0003WordEnums.add(Wn0003WordEnum.TXT_TENANT_NAME.setValue(tenantName));
		wn0003WordEnums.add(Wn0003WordEnum.TXT_TENANT_TELNO.setValue(tenantTelNo));
		wn0003WordEnums.add(Wn0003WordEnum.TXT_TENANT_FAXNO.setValue(tenantFaxNo));

		// 空文字の場合、削除するKEYを設定
		List<WordEnum> deleteWordEnum = new ArrayList<WordEnum>();

		// 住所２
		WordEnum enumAddress2 = Wn0003WordEnum.TXT_ADDRESS2.setValue(address2);
		wn0003WordEnums.add(enumAddress2);
		deleteWordEnum.add(enumAddress2);

		// 宛先2
		WordEnum enumAtesaki2 = Wn0003WordEnum.TXT_ATESAKI2.setValue(atesaki2);
		wn0003WordEnums.add(enumAtesaki2);
		deleteWordEnum.add(enumAtesaki2);

		// 行を削除
		this.deleteRows(doc, deleteWordEnum);

		// Enumのデータをドキュメントに書き込む
		this.writeWord(doc, wn0003WordEnums, wn0003WordDto.getAnkenTantoAccountDtoList());
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