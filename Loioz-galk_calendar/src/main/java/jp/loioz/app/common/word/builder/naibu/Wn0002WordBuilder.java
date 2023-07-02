package jp.loioz.app.common.word.builder.naibu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import jp.loioz.app.common.word.common.AbstractMakeWordBuilder;
import jp.loioz.app.common.word.dto.naibu.Wn0002WordDto;
import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.app.common.word.templateDataEnum.naibu.Wn0002WordEnum;
import jp.loioz.common.constant.ChohyoConstatnt.OutputWordTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.dto.AnkenTantoAccountDto;
import jp.loioz.dto.AzukariItemListDto;
import jp.loioz.dto.AzukariItemUserDto;
import lombok.Data;
import lombok.Setter;

/**
 * 預リ証を出力するbuilderクラス
 *
 */
@Setter
public class Wn0002WordBuilder extends AbstractMakeWordBuilder<Wn0002WordDto> {
	public final static String PERIOD = ".";

	/**
	 * 1部分の預り証データ
	 */
	@Data
	class AzukariShoCopyDto {

		public AzukariShoCopyDto(List<WordEnum> datas, List<AnkenTantoAccountDto> tantoLawyerList,
				List<AzukariItemListDto> himokuList, String atesaki1, String atesaki2) {

			this.datas = datas;
			this.ankenTantoLawyerList = tantoLawyerList;
			this.himokuList = himokuList;
			this.atesaki1 = atesaki1;
			this.atesaki2 = atesaki2;

		}

		List<WordEnum> datas = new ArrayList<>();
		List<AnkenTantoAccountDto> ankenTantoLawyerList = new ArrayList<>();
		List<AzukariItemListDto> himokuList = new ArrayList<>();
		String atesaki1;
		String atesaki2;

	}

	/**
	 * Builder用DTO
	 */
	private Wn0002WordDto wn0002WordDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wn0002WordDto createNewTargetBuilderDto() {
		return new Wn0002WordDto();
	}

	/**
	 * 指定のフォーマットのWordファイルを生成する
	 *
	 * @param response
	 * @throws IOException
	 * @throws Exception
	 */
	public void makeWordFile(HttpServletResponse response) throws IOException, Exception {
		super.makeWordFile(OutputWordTemplate.WORD_NAIBU_2, response, wn0002WordDto.getName());
	}

	/**
	 * Wordファイル内にデータを出力する
	 *
	 * @param doc 出力先のWordファイル情報
	 * @throws IOException 
	 */
	@Override
	protected void setWordData(XWPFDocument doc) throws IOException {

		// DtoからWordへの書き込みデータを取得
		String ankenId = wn0002WordDto.getAnkenId();
		String createAt = wn0002WordDto.getCreatedAt();
		String tenantZipCode = wn0002WordDto.getTenantZipCode();
		String tenantAddress1 = wn0002WordDto.getTenantAddress1();
		String tenantAddress2 = wn0002WordDto.getTenantAddress2();
		String tenantName = wn0002WordDto.getTenantName();
		String tenantTelNo = wn0002WordDto.getTenantTelNo();
		String tenantFaxNo = wn0002WordDto.getTenantFaxNo();
		List<AnkenTantoAccountDto> tantoLawyerList = wn0002WordDto.getAnkenTantoAccountDtoList();
		List<AzukariItemUserDto> azukariUserList = wn0002WordDto.getAzukariUserList();

		List<Object> azukariShoDtoList = new ArrayList<>();

		// 複数部の書き込みを行う場合は、複数のDtoをaddする
		for (AzukariItemUserDto dto : azukariUserList) {
			// Enumをリストにまとめる
			List<WordEnum> wn0002WordEnums = new ArrayList<>();
			wn0002WordEnums.add(Wn0002WordEnum.TXT_ANKENID.setValue(ankenId));
			wn0002WordEnums.add(Wn0002WordEnum.TXT_CREATED_AT.setValue(createAt));
			wn0002WordEnums.add(Wn0002WordEnum.TXT_TENANT_ZIPCODE.setValue(tenantZipCode));
			wn0002WordEnums.add(Wn0002WordEnum.TXT_TENANT_ADDRESS1.setValue(tenantAddress1));
			wn0002WordEnums.add(Wn0002WordEnum.TXT_TENANT_ADDRESS2.setValue(tenantAddress2));
			wn0002WordEnums.add(Wn0002WordEnum.TXT_TENANT_NAME.setValue(tenantName));
			wn0002WordEnums.add(Wn0002WordEnum.TXT_TENANT_TELNO.setValue(tenantTelNo));
			wn0002WordEnums.add(Wn0002WordEnum.TXT_TENANT_FAXNO.setValue(tenantFaxNo));

			// 預り証を出力
			String azukarishoAtesaki1 = CommonConstant.BLANK;
			String azukarishoAtesaki2 = CommonConstant.BLANK;

			if (CommonConstant.TargetType.FREE.equalsByCode(dto.getAzukariFromType())) {
				// 自由入力の場合

				// 個人か法人か判定できないため敬称を入力しない（ユーザーが自分で必要なら入れる）
				azukarishoAtesaki1 = dto.getAzukariFrom();

			} else {
				// 宛先１、２のリストを取得
				List<String> atesakiNameList = dto.getAtesakiNameList();
				azukarishoAtesaki1 = atesakiNameList.get(0);
				azukarishoAtesaki2 = atesakiNameList.get(1);
			}

			// 預り証データを生成
			AzukariShoCopyDto azukariShoDto = new AzukariShoCopyDto(wn0002WordEnums, tantoLawyerList,
					dto.getAzukariHimokuList(), azukarishoAtesaki1, azukarishoAtesaki2);

			azukariShoDtoList.add(azukariShoDto);
		}

		// データをドキュメントに書き込む
		super.writeWordMultiCopy(doc, azukariShoDtoList);
	}

	/**
	 * Wordファイルに指定のデータを書き込む（置換する）
	 */
	@Override
	protected void writeWordSingleCopy(XWPFDocument doc, Object copyDtoObj, int cnt) {

		AzukariShoCopyDto copyDto = (AzukariShoCopyDto) copyDtoObj;
		copyDto.getDatas().add(Wn0002WordEnum.TXT_ATESAKI1.setValue(copyDto.getAtesaki1()));

		WordEnum enumAtesaki2 = Wn0002WordEnum.TXT_ATESAKI2.setValue(copyDto.getAtesaki2());
		copyDto.getDatas().add(enumAtesaki2);

		// 空文字の場合、削除するKEYを設定
		List<WordEnum> deleteWordEnum = new ArrayList<WordEnum>();
		deleteWordEnum.add(enumAtesaki2);
		// 行を削除
		this.deleteRows(doc, deleteWordEnum);

		this.writeWord(doc, copyDto.getDatas(), copyDto.getAnkenTantoLawyerList(), cnt, copyDto.getHimokuList());
	}

	/**
	 * Wordファイルに指定のデータを書き込む（置換する）
	 *
	 * @param doc
	 * @param datas
	 * @param list
	 * @param tantoLawyerList
	 * @param string
	 * @return 書き込み（置換）が完了したドキュメント
	 */
	XWPFDocument writeWord(XWPFDocument doc, List<WordEnum> datas, List<AnkenTantoAccountDto> ankenTantoAccountDtoList, int cnt,
			List<AzukariItemListDto> hinmokuList) {
		// 通常の書き込み処理
		writeWord(doc, datas);

		// 書き込みテーブルの準備
		int rowSize = ankenTantoAccountDtoList.size();

		// 弁護士テーブルの取得
		XWPFTable table = doc.getTableArray(cnt * 2);
		// 品目テーブルの取得
		XWPFTable hinmokuTable = doc.getTableArray(cnt * 2 + 1);

		if (table != null) {

			// 行コピー
			List<XWPFTableRow> tableRowList = tableRowCopy(table, rowSize);

			// 担当弁護士の書き込み処理
			writeDocxTanto(table, rowSize, tableRowList, ankenTantoAccountDtoList);
		}
		rowSize = hinmokuList.size();
		if (hinmokuTable != null) {

			// 行コピー
			List<XWPFTableRow> tableRowList = tableRowCopy(hinmokuTable, rowSize);

			// 品目の書き込み処理
			for (int i = 0; i < hinmokuList.size(); i++) {
				// 品目の先頭につける受領番号を取得
				Integer numID = i + 1;
				hinmokuList.get(i).setHinmoku(numID.toString() + PERIOD + hinmokuList.get(i).getHinmoku());

			}
			writeDocxItem(hinmokuTable, rowSize, tableRowList, hinmokuList);
		}

		return doc;

	}
}
