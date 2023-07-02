package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.SeisanshoExcelSetDto;
import jp.loioz.common.constant.ChohyoConstatnt;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.constant.ChohyoWordConstant;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.utility.ExcelUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.AnkenTantoAccountDto;
import jp.loioz.dto.ExcelJippiMeisaiDto;
import jp.loioz.dto.ExcelJippiMeisaiShukkinListDto;
import jp.loioz.dto.ExcelLawyerHoshuDto;
import jp.loioz.dto.ExcelSeikyushoDto;
import jp.loioz.dto.ExcelSeisanshoDto;
import jp.loioz.dto.ExcelShiharaiKeikakuDto;
import jp.loioz.dto.ExcelShiharaiKeikakuListDto;
import jp.loioz.dto.ExcelSofushoDto;
import jp.loioz.dto.ExcelTimeChargeDto;
import jp.loioz.dto.ExcelTimeChargeListDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 精算書一式のbuilderクラス
 */
@Setter
public class En0009ExcelBuilder extends AbstractMakeExcelBuilder<SeisanshoExcelSetDto> {

	@Getter
	@AllArgsConstructor
	public enum SeisanshoSet {

		// テンプレートのシート番号とシート名
		SOFUSHO(0, "送付書"),
		SEISANSHO(1, "精算書"),
		SEIKYUSHO_IKKATSU(2, "請求書 (一括）"),
		SEIKYUSHO_BUNKATSU(3, "請求書(分割)"),
		JIPPIMEISAI(4, "出金明細書"),
		SHIHARAIKEIKAKU(5, "支払計画表"),
		TIMECHARGE(6, "タイムチャージ計算書"),;

		/** シート番号 */
		private int sheetIndex;

		/** シート名 */
		private String sheetName;
	}

	/** 送付書：行追加 */
	private static final int SOUFUSHO_ADD_ROW = 2;
	/** 送付書：弁護士表示位置 */
	private static final int SOUFUSHO_BENGOSHI_ADD_ROW_IDX = 20;
	/** 送付書：書類表示位置 */
	private static final int SOUFUSHO_SHORUI_ROW_IDX = 7;

	/** 精算書／請求書：弁護士表示位置 */
	private static final int SEIKYU_SEISAN_BENGOSHI_ADD_ROW_IDX = 9;
	/** 精算書／請求書：入金合計表示位置 */
	private static final int SEIKYU_SEISAN_NYUKIN_ROW_IDX = 3;

	/**
	 * フォントサイズの設定
	 */
	private static final int FONT_SIZE_9 = 180;
	private static final int FONT_SIZE_10 = 200;
	private static final int FONT_SIZE_11 = 220;
	private static final int FONT_SIZE_12 = 240;

	/**
	 * Builder用DTO
	 */
	private SeisanshoExcelSetDto seisanshoExcelSetDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SeisanshoExcelSetDto createNewTargetBuilderDto() {
		return new SeisanshoExcelSetDto();
	}

	/**
	 * 指定のフォーマットのExcelファイルを生成する
	 *
	 * @param formTemplate
	 * @param response
	 * @throws Exception
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		this.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_9, response);
	}

	/**
	 * Excelファイル内にデータを出力する
	 *
	 * @param book 出力先のExcelワークブック情報
	 */
	@Override
	protected void setExcelData(XSSFWorkbook book) {

		// 作成されるシートを記録
		Map<Long, String> indexList = new HashMap<Long, String>();

		// 不要なシートを削除
		indexList = removeSheets(book, indexList);

		// 送付書が必要な場合のみ出力
		if (seisanshoExcelSetDto.isOutPutSohushoSheet()) {
			indexList.entrySet().stream().sorted(Map.Entry.comparingByKey());
			this.createSofusho(book, book.getSheet(SeisanshoSet.SOFUSHO.getSheetName()), indexList, seisanshoExcelSetDto.getSofushoData());
		}

		// 精算書が必要かどうか
		if (seisanshoExcelSetDto.isOutPutSeisanshoSheet()) {
			this.createSeisansho(book, book.getSheet(SeisanshoSet.SEISANSHO.getSheetName()), seisanshoExcelSetDto.getSeisanshoData(),
					seisanshoExcelSetDto.getSofushoData(), seisanshoExcelSetDto.isSeisanZeroFlag());
		}

		// 請求書（一括）が必要かどうか
		if (seisanshoExcelSetDto.isOutPutSeikyushoIkkatsuSheet()) {
			this.createSeikyusho(book, book.getSheet(SeisanshoSet.SEIKYUSHO_IKKATSU.getSheetName()), seisanshoExcelSetDto.getSeikyushoData(),
					seisanshoExcelSetDto.getSofushoData(), null);
		}

		// 支払計画表が必要かどうか(請求書分割で支払計画表データを用いるため先に作成)
		if (seisanshoExcelSetDto.isOutPutShiharaiKeikakuSheet()) {
			this.createShiharaiKeikaku(book, book.getSheet(SeisanshoSet.SHIHARAIKEIKAKU.getSheetName()),
					seisanshoExcelSetDto.getShiharaiKeikakuData(), seisanshoExcelSetDto.getSofushoData());
		}

		// 請求書（分割）が必要かどうか
		if (seisanshoExcelSetDto.isOutPutSeikyushoBunkatsuSheet()) {
			this.createSeikyusho(book, book.getSheet(SeisanshoSet.SEIKYUSHO_BUNKATSU.getSheetName()), seisanshoExcelSetDto.getSeikyushoData(),
					seisanshoExcelSetDto.getSofushoData(), seisanshoExcelSetDto.getShiharaiKeikakuData().getShiharaiYoteiList().size());
		}

		// 実費明細が必要かどうか
		if (seisanshoExcelSetDto.isOutPutJippiMesaiSheet()) {
			this.createShukkinMesai(book, book.getSheet(SeisanshoSet.JIPPIMEISAI.getSheetName()), seisanshoExcelSetDto.getJippiMeisaiData(),
					seisanshoExcelSetDto.getSofushoData());
		}

		// タイムチャージ計算書が必要かどうか
		if (seisanshoExcelSetDto.isOutPutTimeChargeSheet()) {
			this.createTimeCharge(book, book.getSheet(SeisanshoSet.TIMECHARGE.getSheetName()), seisanshoExcelSetDto.getTimeChargeData(),
					seisanshoExcelSetDto.getSofushoData());
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 出力しないシートを削除する
	 *
	 * @param book
	 * @param indexList
	 * @return<Long, String>indexList
	 */
	private Map<Long, String> removeSheets(XSSFWorkbook book, Map<Long, String> indexList) {

		// 各種ステータスの設定
		String seisanshoStatus = ChohyoConstatnt.SEISANSHO;
		String seikyuStatus = ChohyoConstatnt.SEIKYUSHO;
		String jippiMeisaiStatus = ChohyoConstatnt.SHUKKINMEISAISHO;
		String shiharaiKeikakuStatus = ChohyoConstatnt.SHIHARAIKEIKAKUHYO;
		String timeChargeStatus = ChohyoConstatnt.TIMECHARGEKEISANSHO;

		// タイムチャージ計算書が必要かどうか
		if (!seisanshoExcelSetDto.isOutPutTimeChargeSheet()) {
			ExcelUtils.removeSheetByIndex(book, SeisanshoSet.TIMECHARGE.getSheetIndex());
		} else {
			// タイムチャージ計算書が作られた場合
			indexList.put(Long.parseLong("6"), timeChargeStatus);
		}
		// 支払計画が必要かどうか
		if (!seisanshoExcelSetDto.isOutPutShiharaiKeikakuSheet()) {
			ExcelUtils.removeSheetByIndex(book, SeisanshoSet.SHIHARAIKEIKAKU.getSheetIndex());
		} else {
			// 支払計画が作られた場合
			indexList.put(Long.parseLong("5"), shiharaiKeikakuStatus);
		}
		// 出金明細書が必要かどうか
		if (!seisanshoExcelSetDto.isOutPutJippiMesaiSheet()) {
			ExcelUtils.removeSheetByIndex(book, SeisanshoSet.JIPPIMEISAI.getSheetIndex());
		} else {
			// 請求書が作られた場合
			indexList.put(Long.parseLong("4"), jippiMeisaiStatus);
		}

		// 請求書(分割)が必要かどうか
		if (!seisanshoExcelSetDto.isOutPutSeikyushoBunkatsuSheet()) {
			ExcelUtils.removeSheetByIndex(book, SeisanshoSet.SEIKYUSHO_BUNKATSU.getSheetIndex());
		} else {
			// 請求書が作られた場合
			indexList.put(Long.parseLong("3"), seikyuStatus);
		}
		// 請求書(一括)が必要かどうか
		if (!seisanshoExcelSetDto.isOutPutSeikyushoIkkatsuSheet()) {
			ExcelUtils.removeSheetByIndex(book, SeisanshoSet.SEIKYUSHO_IKKATSU.getSheetIndex());
		} else {
			// 請求書が作られた場合
			indexList.put(Long.parseLong("2"), seikyuStatus);
		}
		// 精算書が必要かどうか
		if (!seisanshoExcelSetDto.isOutPutSeisanshoSheet()) {
			ExcelUtils.removeSheetByIndex(book, SeisanshoSet.SEISANSHO.getSheetIndex());
		} else {
			// 精算書が作られた場合
			indexList.put(Long.parseLong("1"), seisanshoStatus);
		}
		// 送付書が必要かどうか
		if (!seisanshoExcelSetDto.isOutPutSohushoSheet()) {
			ExcelUtils.removeSheetByIndex(book, SeisanshoSet.SOFUSHO.getSheetIndex());
		}
		return indexList;

	}

	/**
	 * 送付書の作成
	 *
	 * @param workbook
	 * @param sheet
	 * @param statusList 送付書類の出力データ
	 * @param sofushoData 出力データ
	 */
	private void createSofusho(Workbook workbook, Sheet sheet, Map<Long, String> statusList, ExcelSofushoDto sofushoData) {

		// ■お客様情報を設定

		// 顧客ID
		setVal(sheet, 0, "M", Double.valueOf(sofushoData.getId().toString()));
		// 郵便番号
		String zipCode = sofushoData.getZipCode();
		if (StringUtils.isNotEmpty(zipCode)) {
			// 郵便番号あり
			setVal(sheet, 1, "A", StringUtils.defaultString(ChohyoWordConstant.POST_MARK + StringUtils.defaultString(zipCode)));

		} else {
			// 郵便番号(空入力)
			setVal(sheet, 1, "A", StringUtils.defaultString(ChohyoWordConstant.POST_MARK));

		}
		// 住所1
		setVal(sheet, 2, "A", StringUtils.defaultString(sofushoData.getAddress1()));
		// 住所2
		setVal(sheet, 3, "A", StringUtils.defaultString(sofushoData.getAddress2()));
		// 宛先
		setVal(sheet, 4, "A", StringUtils.defaultString(sofushoData.getName1()));
		setVal(sheet, 5, "A", StringUtils.defaultString(sofushoData.getName2()));

		// 出力日
		setVal(sheet, 12, "A", LocalDate.now());

		// ■事務所情報を設定
		// 郵便番号
		setVal(sheet, 14, "J", (ChohyoWordConstant.POST_MARK + StringUtils.defaultString(sofushoData.getTenantZipCode())));

		// 事務所の住所１
		short tenantAddress1Height = rowHeight(sofushoData.getTenantAddress1());
		sheet.getRow(15).setHeight(tenantAddress1Height);
		setVal(sheet, 15, "J", (StringUtils.defaultString(sofushoData.getTenantAddress1())));

		// 事務所の住所２
		short tenantAddress2Height = rowHeight(sofushoData.getTenantAddress2());
		sheet.getRow(16).setHeight(tenantAddress2Height);
		setVal(sheet, 16, "J", (StringUtils.defaultString(sofushoData.getTenantAddress2())));

		// 事務所名
		short tenantNameHeight = rowHeight(sofushoData.getTenantName());
		sheet.getRow(17).setHeight(tenantNameHeight);
		setVal(sheet, 17, "J", (StringUtils.defaultString(sofushoData.getTenantName())));

		// 電話番号
		setVal(sheet, 18, "K", StringUtils.defaultString(sofushoData.getTenantTelNo()));
		// FAX番号
		setVal(sheet, 19, "K", StringUtils.defaultString(sofushoData.getTenantFaxNo()));

		// 挿入対象行
		int insertRow = SOUFUSHO_BENGOSHI_ADD_ROW_IDX;

		// 最終行
		int lastRow = sheet.getLastRowNum();

		// 出力対象弁護士の設定
		List<AnkenTantoAccountDto> ankenBengoshi = sofushoData.getAnkenTanto();

		// 共通フォント
		Font font = workbook.createFont();
		font.setFontName(OutputConstant.FONT_MINCHO);
		font.setFontHeight((short) FONT_SIZE_12);

		int bengoshiWriteEndRowIdx = this.addRowAnkenTanto(workbook, sheet, insertRow, lastRow, ankenBengoshi, font, true);

		// 送付書類の設定
		int colNum = 1;
		int maxColNum = 6;

		// 送付書類の挿入行の設定
		int shoruiIinsertRow = SOUFUSHO_BENGOSHI_ADD_ROW_IDX + bengoshiWriteEndRowIdx + SOUFUSHO_SHORUI_ROW_IDX;

		for (Entry<Long, String> entry : statusList.entrySet()) {

			// 挿入行を取得
			setVal(sheet, shoruiIinsertRow, "D", Double.valueOf(colNum));
			setVal(sheet, shoruiIinsertRow, "E", StringUtils.defaultString(entry.getValue()));

			colNum++;
			shoruiIinsertRow++;
		}
		// 行削除
		int delColNum = maxColNum - colNum;
		for (int i = 0; i < delColNum; i++) {
			deleteRow(sheet, shoruiIinsertRow + i);
		}

	}

	/**
	 * 精算書シートの作成
	 *
	 * @param workbook
	 * @param sheet
	 * @param seisanshoData
	 * @param sofushoData
	 * @param seisanZeroFlag
	 */
	private void createSeisansho(Workbook workbook, Sheet sheet, ExcelSeisanshoDto seisanshoData, ExcelSofushoDto sofushoData,
			boolean seisanZeroFlag) {

		// 宛名エリアの書き込み
		int bengoshiWriteEndRowIdx = commonSeisanSeikyuAtenaAreaWrite(workbook, sheet, sofushoData);

		// 出力日
		setVal(sheet, 1, "L", LocalDate.now());

		// ■精算額の設定
		setVal(sheet, 7, "D", seisanshoData.getHenkinGaku().doubleValue());

		// 案件名の設定
		int ankenNameInsertRowCount = writeAnkenName(workbook, sheet, 10, sofushoData.getAnkenName(), bengoshiWriteEndRowIdx);

		// 入金合計
		int nyukinRowIdx = SEIKYU_SEISAN_BENGOSHI_ADD_ROW_IDX + bengoshiWriteEndRowIdx + ankenNameInsertRowCount + SEIKYU_SEISAN_NYUKIN_ROW_IDX;
		// ■入金額の合計
		setVal(sheet, nyukinRowIdx, "F", seisanshoData.getNyukinTotal().doubleValue());

		// ■弁護士報酬の設定
		int hoshuRowIdx = nyukinRowIdx + 2;
		// 報酬明細の行数を追加
		List<ExcelLawyerHoshuDto> hoshuMeisaiDtoList = seisanshoData.getHoshuMeisaiDtoList();
		int addHoshuRowSize = 8;
		boolean hoshuZeroFlg = false;
		if (LoiozCollectionUtils.isNotEmpty(hoshuMeisaiDtoList)) {
			int meisaiSize = hoshuMeisaiDtoList.size();
			// 追加行よりも明細が多いか判定
			if (meisaiSize > addHoshuRowSize) {
				addHoshuRowSize = meisaiSize;
			}
		} else {
			// 報酬ゼロ
			hoshuZeroFlg = true;
		}

		// 行追加(8を指定すると、10行報酬行ができる)
		addRowsFormatCopy(hoshuRowIdx + 1, hoshuRowIdx + 1, workbook, sheet, addHoshuRowSize);
		// 弁護士報酬の書き込み
		this.writeBengoshiHoshu(workbook, sheet, hoshuRowIdx, hoshuMeisaiDtoList, addHoshuRowSize);

		// ■小計、出金合計、弁護士報酬合計の設定

		// ■消費税
		int zeiRowIdx = hoshuRowIdx + addHoshuRowSize + 2;
		int zeiRowIdx2 = zeiRowIdx + 1;

		String dispTaxTotal8 = seisanshoData.getDispTaxTotalEight();
		String dispTaxTotal10 = seisanshoData.getDispTaxTotalTen();

		// 消費税の行を追加する必要がある判定
		boolean delFlg = false;
		if (CommonConstant.ZERO.equals(dispTaxTotal8) || CommonConstant.ZERO.equals(dispTaxTotal10)) {
			// 消費税8%、10%のいずれかあるいは両方が0の場合
			delFlg = true;
			deleteRowShift(sheet, zeiRowIdx2);
		}
		if (!CommonConstant.ZERO.equals(dispTaxTotal8)) {
			// 8%の場合
			setVal(sheet, zeiRowIdx, "D", "消費税(8％)");
			setVal(sheet, zeiRowIdx, "F", seisanshoData.getTaxTotalEight().doubleValue());
		}
		// 削除した場合、行の位置を修正
		if (delFlg) {
			zeiRowIdx2 = zeiRowIdx;
		}
		if (!CommonConstant.ZERO.equals(dispTaxTotal10)) {
			// 10%の場合
			setVal(sheet, zeiRowIdx2, "D", "消費税(10％)");
			setVal(sheet, zeiRowIdx2, "F", seisanshoData.getTaxTotalTen().doubleValue());
		}
		if (CommonConstant.ZERO.equals(dispTaxTotal8) && CommonConstant.ZERO.equals(dispTaxTotal10)) {
			// 8%も10%も０円の場合（免税）
			setVal(sheet, zeiRowIdx2, "D", "消費税");
			setVal(sheet, zeiRowIdx2, "F", BigDecimal.ZERO.doubleValue());
		}

		// ■源泉徴収の合計
		int gensenRowIdx = zeiRowIdx2 + 1;
		if (seisanshoData.getDispGensenTotal() != null || !CommonConstant.ZERO.equals(seisanshoData.getDispGensenTotal())) {
			// 源泉徴収の合計
			setVal(sheet, gensenRowIdx, "F", -seisanshoData.getGensenTotal().doubleValue());
		}
		// 報酬がゼロの場合は空文字で設定
		if (hoshuZeroFlg) {
			setVal(sheet, gensenRowIdx, "F", CommonConstant.BLANK);

		}

		// ■小計
		int shokeiRowIdx = gensenRowIdx + 1;
		setVal(sheet, shokeiRowIdx, "F", seisanshoData.getShokei().doubleValue());

		// ■ 出金合計(実費等)
		int shukkinSumRowIdx = shokeiRowIdx + 2;
		setVal(sheet, shukkinSumRowIdx, "F", seisanshoData.getShukkinTotal().doubleValue());

		// ■ 差引精算額 ①-②-③
		int sashihikiRowIdx = shukkinSumRowIdx + 2;
		setVal(sheet, sashihikiRowIdx, "F", seisanshoData.getSaishihikiTotal().doubleValue());

		// ■精算口座
		int seisanKozaRowIdx = sashihikiRowIdx + 4;
		if (seisanZeroFlag) {
			// 0円精算の場合

			setVal(sheet, seisanKozaRowIdx++, "D", CommonConstant.BLANK);
			setVal(sheet, seisanKozaRowIdx++, "D", CommonConstant.BLANK);
			setVal(sheet, seisanKozaRowIdx++, "D", CommonConstant.BLANK);
			setVal(sheet, seisanKozaRowIdx++, "D", CommonConstant.BLANK);

		} else {
			// 通常の精算の場合

			// 銀行名
			setVal(sheet, seisanKozaRowIdx++, "D", StringUtils.defaultString(seisanshoData.getGinkoName()));

			// 支店名 + 支店番号
			String shitenName = StringUtils.null2blank(seisanshoData.getShitenName());
			String shitenNo = StringUtils.null2blank(seisanshoData.getShitenNo());
			if (StringUtils.isNotEmpty(shitenNo)) {
				shitenNo = "（" + StringUtils.convertHalfToFullNum(shitenNo) + "）";
			}
			setVal(sheet, seisanKozaRowIdx++, "D", shitenName + shitenNo);

			// 口座種別
			String kozaType = seisanshoData.getKozaType();
			String kozaTypeName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(kozaType)) {
				kozaTypeName = "（" + KozaType.of(kozaType).getVal() + "）";
			}
			String kozaNo = StringUtils.null2blank(seisanshoData.getKozaNo());
			if (StringUtils.isNotEmpty(kozaNo)) {
				kozaNo = StringUtils.convertHalfToFullNum(kozaNo);
			}
			String kozaNoCell = kozaTypeName + kozaNo;
			// 口座No
			setVal(sheet, seisanKozaRowIdx++, "D", kozaNoCell);
			// 口座名義
			setVal(sheet, seisanKozaRowIdx++, "D", StringUtils.null2blank(seisanshoData.getKozaName()));

		}
	}

	/**
	 * 請求書シートの作成
	 *
	 * @param workbook
	 * @param sheet
	 * @param seikyushoData
	 * @param sofushoData
	 * @param shiharaiCount
	 */
	private void createSeikyusho(Workbook workbook, Sheet sheet, ExcelSeikyushoDto seikyushoData, ExcelSofushoDto sofushoData,
			Integer shiharaiCount) {

		// 出力日
		setVal(sheet, 1, "L", LocalDate.now());

		// ■請求額の設定
		setVal(sheet, 7, "D", seikyushoData.getSeikyuGaku().doubleValue());

		int bengoshiWriteEndRowIdx = commonSeisanSeikyuAtenaAreaWrite(workbook, sheet, sofushoData);

		// 案件名の設定
		int ankenNameInsertRowCount = writeAnkenName(workbook, sheet, 10, sofushoData.getAnkenName(), bengoshiWriteEndRowIdx);

		// 入金合計
		int nyukinRowIdx = SEIKYU_SEISAN_BENGOSHI_ADD_ROW_IDX + bengoshiWriteEndRowIdx + ankenNameInsertRowCount + SEIKYU_SEISAN_NYUKIN_ROW_IDX;
		// ■入金額の合計
		setVal(sheet, nyukinRowIdx, "F", seikyushoData.getNyukinTotal().doubleValue());

		// ■弁護士報酬の設定
		int hoshuRowIdx = nyukinRowIdx + 2;
		// 報酬明細の行数を追加
		List<ExcelLawyerHoshuDto> hoshuMeisaiDtoList = seikyushoData.getHoshuMeisaiDtoList();
		int addHoshuRowSize = 8;
		boolean hoshuZeroFlg = false;
		if (LoiozCollectionUtils.isNotEmpty(hoshuMeisaiDtoList)) {
			int meisaiSize = hoshuMeisaiDtoList.size();
			// 追加行よりも明細が多いか判定
			if (meisaiSize > addHoshuRowSize) {
				addHoshuRowSize = meisaiSize;
			}
		} else {
			// 報酬ゼロ
			hoshuZeroFlg = true;
		}

		// 行追加(8を指定すると、10行報酬行ができる)
		addRowsFormatCopy(hoshuRowIdx + 1, hoshuRowIdx + 1, workbook, sheet, addHoshuRowSize);
		// 弁護士報酬の書き込み
		this.writeBengoshiHoshu(workbook, sheet, hoshuRowIdx, hoshuMeisaiDtoList, addHoshuRowSize);

		// ■小計、出金合計、弁護士報酬合計の設定

		// ■消費税
		int zeiRowIdx = hoshuRowIdx + addHoshuRowSize + 2;
		int zeiRowIdx2 = zeiRowIdx + 1;

		String dispTaxTotal8 = seikyushoData.getDispTaxTotalEight();
		String dispTaxTotal10 = seikyushoData.getDispTaxTotalTen();

		// 消費税の行を追加する必要がある判定
		boolean delFlg = false;
		if (CommonConstant.ZERO.equals(dispTaxTotal8) || CommonConstant.ZERO.equals(dispTaxTotal10)) {
			// 消費税8%、10%のいずれかあるいは両方が0の場合
			delFlg = true;
			deleteRowShift(sheet, zeiRowIdx2);
		}
		if (!CommonConstant.ZERO.equals(dispTaxTotal8)) {
			// 8%の場合
			setVal(sheet, zeiRowIdx, "D", "消費税(8％)");
			setVal(sheet, zeiRowIdx, "F", seikyushoData.getTaxTotalEight().doubleValue());
		}
		// 削除した場合、行の位置を修正
		if (delFlg) {
			zeiRowIdx2 = zeiRowIdx;
		}
		if (!CommonConstant.ZERO.equals(dispTaxTotal10)) {
			// 10%の場合
			setVal(sheet, zeiRowIdx2, "D", "消費税(10％)");
			setVal(sheet, zeiRowIdx2, "F", seikyushoData.getTaxTotalTen().doubleValue());
		}
		if (CommonConstant.ZERO.equals(dispTaxTotal8) && CommonConstant.ZERO.equals(dispTaxTotal10)) {
			// 8%も10%も０円の場合（免税）
			setVal(sheet, zeiRowIdx2, "D", "消費税");
			setVal(sheet, zeiRowIdx2, "F", BigDecimal.ZERO.doubleValue());
		}

		// ■源泉徴収の合計
		int gensenRowIdx = zeiRowIdx2 + 1;
		if (seikyushoData.getDispGensenTotal() != null || !CommonConstant.ZERO.equals(seikyushoData.getDispGensenTotal())) {
			// 源泉徴収の合計
			setVal(sheet, gensenRowIdx, "F", -seikyushoData.getGensenTotal().doubleValue());
		}
		// 報酬がゼロの場合は空文字で設定
		if (hoshuZeroFlg) {
			setVal(sheet, gensenRowIdx, "F", CommonConstant.BLANK);

		}

		// ■小計
		int shokeiRowIdx = gensenRowIdx + 1;
		setVal(sheet, shokeiRowIdx, "F", seikyushoData.getShokei().doubleValue());

		// ■ 出金合計(実費等)
		int shukkinSumRowIdx = shokeiRowIdx + 2;
		setVal(sheet, shukkinSumRowIdx, "F", seikyushoData.getShukkinTotal().doubleValue());

		// ■ 差引精算額 ①-②-③
		int sashihikiRowIdx = shukkinSumRowIdx + 2;
		setVal(sheet, sashihikiRowIdx, "F", seikyushoData.getSaishihikiTotal().doubleValue());

		// ■事務所口座情報
		int jimushoKozaRowIdx = sashihikiRowIdx + 4;

		// 銀行名
		setVal(sheet, jimushoKozaRowIdx++, "D", StringUtils.null2blank(sofushoData.getJimushoKozaGinkoName()));

		// 支店名 + 支店番号
		String shitenName = StringUtils.null2blank(sofushoData.getJimushoKozashitenName());
		String shitenNo = StringUtils.null2blank(sofushoData.getJimushoKozashitenNo());
		if (StringUtils.isNotEmpty(shitenNo)) {
			shitenNo = "（" + StringUtils.convertHalfToFullNum(shitenNo) + "）";
		}
		setVal(sheet, jimushoKozaRowIdx++, "D", shitenName + shitenNo);

		// 口座種別
		String kozaType = sofushoData.getJimushoKozaType();
		String kozaTypeName = CommonConstant.BLANK;
		if (StringUtils.isNotEmpty(kozaType)) {
			kozaTypeName = "（" + KozaType.of(kozaType).getVal() + "）";
		}
		String kozaNo = StringUtils.null2blank(sofushoData.getJimushokozaNo());
		if (StringUtils.isNotEmpty(kozaNo)) {
			kozaNo = StringUtils.convertHalfToFullNum(kozaNo);
		}
		String kozaNoCell = kozaTypeName + kozaNo;
		// 口座No
		setVal(sheet, jimushoKozaRowIdx++, "D", kozaNoCell);
		// 口座名義
		setVal(sheet, jimushoKozaRowIdx++, "D", StringUtils.null2blank(sofushoData.getJimushoKozaName()));

		// 分割支払いの場合分割支払い回数を記載
		if (shiharaiCount != null) {
			int tokkiJiko = jimushoKozaRowIdx + 2;
			String sentence = sheet.getRow(tokkiJiko).getCell(1).getStringCellValue().replace("●", shiharaiCount.toString());
			setVal(sheet, tokkiJiko, "B", StringUtils.null2blank(sentence));
		}

	}

	/**
	 * 宛名エリアの書き込み処理
	 *
	 * @param workbook
	 * @param sheet
	 * @param sofushoData
	 */
	private int commonSeisanSeikyuAtenaAreaWrite(Workbook workbook, Sheet sheet, ExcelSofushoDto sofushoData) {

		// ■顧客情報を設定
		if (StringUtils.isNotEmpty(sofushoData.getName2())) {
			setVal(sheet, 2, "B", sofushoData.getName1() + CommonConstant.SPACE + sofushoData.getName2());
		} else {
			setVal(sheet, 2, "B", sofushoData.getName1());
		}

		// ■事務所情報を設定
		// 郵便番号
		setVal(sheet, 3, "J", ("〒" + StringUtils.defaultString(sofushoData.getTenantZipCode())));

		// 事務所の住所１
		short tenantAddress1Height = rowHeight(sofushoData.getTenantAddress1(), 270, 20);
		sheet.getRow(4).setHeight(tenantAddress1Height);
		setVal(sheet, 4, "J", (StringUtils.defaultString(sofushoData.getTenantAddress1())));

		// 事務所の住所２
		short tenantAddress2Height = rowHeight(sofushoData.getTenantAddress2(), 270, 20);
		sheet.getRow(5).setHeight(tenantAddress2Height);
		setVal(sheet, 5, "J", (StringUtils.defaultString(sofushoData.getTenantAddress2())));

		// 事務所名
		short tenantNameHeight = rowHeight(sofushoData.getTenantName(), 250, 20);
		sheet.getRow(6).setHeight(tenantNameHeight);
		setVal(sheet, 6, "J", (StringUtils.defaultString(sofushoData.getTenantName())));

		// 電話番号
		setVal(sheet, 7, "K", StringUtils.defaultString(sofushoData.getTenantTelNo()));
		// FAX番号
		setVal(sheet, 8, "K", StringUtils.defaultString(sofushoData.getTenantFaxNo()));

		// 挿入対象行
		int insertRow = SEIKYU_SEISAN_BENGOSHI_ADD_ROW_IDX;
		// 最終行
		int lastRow = sheet.getLastRowNum();

		// 出力対象弁護士の設定
		List<AnkenTantoAccountDto> ankenBengoshi = sofushoData.getAnkenTanto();

		// 共通フォント
		Font font = workbook.createFont();
		font.setFontName(OutputConstant.FONT_MINCHO);
		font.setFontHeight((short) FONT_SIZE_10);

		// ■弁護士、担当事務の書き込み
		return this.addRowAnkenTanto(workbook, sheet, insertRow, lastRow, ankenBengoshi, font, false);
	}

	/**
	 * 出金明細シートの作成
	 *
	 * @param workbook
	 * @param sheet
	 * @param JippiMesaiData
	 * @param sofushoData
	 */
	private void createShukkinMesai(Workbook workbook, Sheet sheet, ExcelJippiMeisaiDto jippiMesaiData, ExcelSofushoDto sofushoData) {

		// 出力日
		setVal(sheet, 1, "A", LocalDate.now());

		// ■顧客情報を設定
		if (StringUtils.isNotEmpty(sofushoData.getName2())) {
			setVal(sheet, 2, "A", sofushoData.getName1() + CommonConstant.SPACE + sofushoData.getName2());
		} else {
			setVal(sheet, 2, "A", sofushoData.getName1());
		}

		// 発生日で並び替え（nullのデータは最後にする）
		List<ExcelJippiMeisaiShukkinListDto> shukkinList = jippiMesaiData.getShukkinList().stream()
				.sorted(Comparator.comparing(ExcelJippiMeisaiShukkinListDto::getHasseiDate, Comparator.nullsLast(LocalDate::compareTo)))
				.collect(Collectors.toList());

		int addRowSize = 30;
		if (LoiozCollectionUtils.isNotEmpty(shukkinList)) {
			int meisaiSize = shukkinList.size();
			if (meisaiSize > addRowSize) {
				addRowSize = meisaiSize + 5;
			}
		}

		// 行追加
		addRows(7, 7, workbook, sheet, addRowSize);

		// 明細初期値
		int rowIdx = 7;
		int wirteEndIdx = rowIdx + addRowSize;
		for (ExcelJippiMeisaiShukkinListDto shukkin : shukkinList) {
			if (shukkin.getHasseiDate() != null) {
				setVal(sheet, rowIdx, "A", shukkin.getHasseiDate());
			}
			setVal(sheet, rowIdx, "B", shukkin.getShukkinGaku().doubleValue());
			setVal(sheet, rowIdx, "C", StringUtils.defaultString(shukkin.getShukkinKomokuName()));
			setVal(sheet, rowIdx, "D", StringUtils.defaultString(shukkin.getTaxType()));
			setVal(sheet, rowIdx, "E", StringUtils.defaultString(shukkin.getTekiyo()));
			rowIdx++;
		}

		// 出金の合計
		setVal(sheet, wirteEndIdx, "B", jippiMesaiData.getShukkinTotal().doubleValue());
	}

	/**
	 * 支払計画シートの作成
	 *
	 * @param workbook
	 * @param sheet
	 * @param shiharaiKeikakuData
	 * @param sofushoData
	 */
	private void createShiharaiKeikaku(Workbook workbook, Sheet sheet, ExcelShiharaiKeikakuDto shiharaiKeikakuData, ExcelSofushoDto sofushoData) {

		// 出力日
		setVal(sheet, 1, "I", LocalDate.now());

		// ■顧客名の設定
		if (StringUtils.isNotEmpty(sofushoData.getName2())) {
			setVal(sheet, 2, "A", sofushoData.getName1() + CommonConstant.SPACE + sofushoData.getName2());
		} else {
			setVal(sheet, 2, "A", sofushoData.getName1());
		}

		// ■振込先設定を設定

		// 銀行名
		setVal(sheet, 6, "C", StringUtils.defaultString(sofushoData.getJimushoKozaGinkoName()));

		// 支店名 + 支店番号
		String shitenName = StringUtils.null2blank(sofushoData.getJimushoKozashitenName());
		String shitenNo = StringUtils.null2blank(sofushoData.getJimushoKozashitenNo());
		if (StringUtils.isNotEmpty(shitenNo)) {
			shitenNo = "（" + StringUtils.convertHalfToFullNum(shitenNo) + "）";
		}
		setVal(sheet, 7, "C", shitenName + shitenNo);

		// 口座種別
		String kozaType = sofushoData.getJimushoKozaType();
		String kozaTypeName = CommonConstant.BLANK;
		if (StringUtils.isNotEmpty(kozaType)) {
			kozaTypeName = "（" + KozaType.of(kozaType).getVal() + "）";
		}
		String kozaNo = StringUtils.null2blank(sofushoData.getJimushokozaNo());
		if (StringUtils.isNotEmpty(kozaNo)) {
			kozaNo = StringUtils.convertHalfToFullNum(kozaNo);
		}
		String kozaNoCell = kozaTypeName + kozaNo;
		// 口座No
		setVal(sheet, 8, "C", kozaNoCell);
		// 口座名義
		setVal(sheet, 9, "C", StringUtils.null2blank(sofushoData.getJimushoKozaName()));

		// データの取得
		List<ExcelShiharaiKeikakuListDto> shiharaiYoteiList = shiharaiKeikakuData.getShiharaiYoteiList();

		// 明細初期値
		int startRow = 12;

		// デフォルト設定の支払計画行数（片側）
		final int OUTPUT_ROW = 30;
		int index = 1;
		// 追加した行のカウント変数
		int addRowCount = 0;
		// デフォルトの支払い計画行数を超えるか判定
		if (OUTPUT_ROW * 2 < shiharaiYoteiList.size()) {
			// 追加すべき行数を定義
			int addRowNum = (shiharaiYoteiList.size() - (OUTPUT_ROW * 2));
			// 奇数の場合
			if (addRowNum % 2 != 0) {
				addRowNum = (addRowNum / 2) + 1;
			}
			// 偶数の場合
			else {
				addRowNum = addRowNum / 2;

			}
			// 追加すべき行数まで行追加
			while (addRowNum > addRowCount) {
				// 行追加
				this.addRow((startRow + OUTPUT_ROW + addRowCount), startRow, workbook, sheet);
				addRowCount++;
			}
		}

		for (ExcelShiharaiKeikakuListDto dto : shiharaiYoteiList) {
			if (index < OUTPUT_ROW + addRowCount + 1) {
				setVal(sheet, startRow, "A", index);
				if (dto.getNyushukkinYoteiDate() != null) {
					setVal(sheet, startRow, "B", dto.getNyushukkinYoteiDate());
				}
				setVal(sheet, startRow, "C", StringUtils.defaultString(dto.getDispKingaku()) + OutputConstant.YEN);
				setVal(sheet, startRow, "D", StringUtils.defaultString(dto.getDispZankin()) + OutputConstant.YEN);
			} else {
				setVal(sheet, startRow, "F", index);
				if (dto.getNyushukkinYoteiDate() != null) {
					setVal(sheet, startRow, "G", dto.getNyushukkinYoteiDate());
				}
				setVal(sheet, startRow, "H", StringUtils.defaultString(dto.getDispKingaku()) + OutputConstant.YEN);
				setVal(sheet, startRow, "I", StringUtils.defaultString(dto.getDispZankin()) + OutputConstant.YEN);
			}

			if (index == OUTPUT_ROW + addRowCount) {
				startRow = 12;
			} else {
				startRow++;
			}

			index++;
		}

	}

	/**
	 * タイムチャージシートの作成
	 *
	 * @param workbook
	 * @param sheet
	 * @param timeChargeData
	 * @param sofushoData
	 */
	private void createTimeCharge(Workbook workbook, Sheet sheet, ExcelTimeChargeDto timeChargeData, ExcelSofushoDto sofushoData) {

		/** タイムチャージ計算書の表示開始行(Excel : 6行目) */
		final int START_ROW_IDX = 5;

		/** 標準の最大データ行数 */
		final int MAX_DATA_ROW_SIZE = 23;

		/** テンプレートの行数 */
		final int DEFAULT_LAST_ROW_NUM = 27;

		/** 合計の行位置 */
		final int SUM_ROW_NUM_IDX = 29;

		// 出力日
		setVal(sheet, 1, "E", LocalDate.now());

		// 顧客名の設定
		if (StringUtils.isNotEmpty(sofushoData.getName2())) {
			setVal(sheet, 2, "A", sofushoData.getName1() + CommonConstant.SPACE + sofushoData.getName2());
		} else {
			setVal(sheet, 2, "A", sofushoData.getName1());
		}

		// タイムチャージリスト設定
		List<ExcelTimeChargeListDto> dataList = timeChargeData.getTimeChargeList();
		int timeChargeListSize = dataList.size();

		int addRowSize = 0;
		if (timeChargeListSize > MAX_DATA_ROW_SIZE) {
			// タイムチャージの実績 > 帳票のテンプレートの行数より多い場合：行を追加
			addRowSize = timeChargeListSize - MAX_DATA_ROW_SIZE;

			// 行追加
			addRowsFormatCopy(DEFAULT_LAST_ROW_NUM, START_ROW_IDX, workbook, sheet, addRowSize + 1);

		}

		int rowIdx = START_ROW_IDX;
		for (ExcelTimeChargeListDto dto : dataList) {
			// データの設定
			int colIdx = 0;

			// 日付
			if (dto.getHasseiDate() != null) {
				setVal(sheet, rowIdx, colIdx++, dto.getHasseiDate());
			} else {
				colIdx++;
			}
			
			// 時間(From~To)
			String time = CommonConstant.BLANK;
			if (StringUtils.isNoneEmpty(dto.getTimeChargeStartTime())
					|| StringUtils.isNoneEmpty(dto.getTimeChargeEndTime())) {
				time = dto.getTimeChargeStartTime().toString().substring(11)
						+ "～"
						+ dto.getTimeChargeEndTime().toString().substring(11);
			}
			setVal(sheet, rowIdx, colIdx++, time);
			
			// 活動設定
			setVal(sheet, rowIdx, colIdx++, dto.getKatsudo());
			// 時間設定(分)
			setVal(sheet, rowIdx, colIdx++, dto.getTime());
			// 単価設定
			setVal(sheet, rowIdx, colIdx++, dto.getTimeChargeTanka().doubleValue());
			// 小計設定
			setVal(sheet, rowIdx, colIdx++, dto.getShukkinGaku().doubleValue());

			rowIdx++;
		}

		// 合計時間
		setVal(sheet, SUM_ROW_NUM_IDX + addRowSize, "E", timeChargeData.getTotalTime());
		// 報酬額
		setVal(sheet, SUM_ROW_NUM_IDX + addRowSize + 1, "E", timeChargeData.getHoshuTotal().doubleValue());

	}

	/**
	 * 行の高さを設定
	 *
	 * @param val
	 * @return
	 */
	private static short rowHeight(String val) {
		// フォントサイズ12 は 高さ：300, 20文字で改行
		return rowHeight(val, 300, 20);
	}

	/**
	 * 行の高さを設定
	 *
	 * @param val
	 * @param height
	 * @param returnCharSize
	 * @return
	 */
	private static short rowHeight(String val, int height, int returnCharSize) {
		List<String> array = new ArrayList<String>();
		array.add(val);
		return rowHeight(array, height, returnCharSize);
	}

	/**
	 * @param list
	 * @return
	 */
	private static short rowHeight(List<String> list, int height, int returnCharSize) {
		int maxLength = 0;
		for (String val : list) {
			int len = val.length();
			if (len > maxLength) {
				maxLength = len;
			}
		}
		int rowNum = (maxLength / returnCharSize) + 1;
		int rowHeihght = rowNum * height;
		return (short) rowHeihght;
	}

	/**
	 * 対象エクセルシートに案件担当：弁護士、事務を挿入
	 *
	 * @param workbook
	 * @param sheet
	 * @param insertRow
	 * @param lastRow
	 * @param list
	 * @return
	 */
	private int addRowAnkenTanto(Workbook workbook, Sheet sheet, int insertRow, int lastRow, List<AnkenTantoAccountDto> list, Font font,
			boolean dispFlg) {

		// 弁護士／担当事セル：左寄せインデント1
		CellStyle bengoshiJimuCellStyle = workbook.createCellStyle();
		bengoshiJimuCellStyle.setAlignment(HorizontalAlignment.LEFT);
		bengoshiJimuCellStyle.setIndention((short) 1);
		bengoshiJimuCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		bengoshiJimuCellStyle.setFont(font);

		// 弁護士名セル：均等割りを適用
		CellStyle bengoshiCellStyle = workbook.createCellStyle();
		bengoshiCellStyle.setAlignment(HorizontalAlignment.DISTRIBUTED);
		bengoshiCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		bengoshiCellStyle.setFont(font);

		// 事務名セル：左寄せ
		CellStyle jimuCellStyle = workbook.createCellStyle();
		jimuCellStyle.setAlignment(HorizontalAlignment.LEFT);
		jimuCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		jimuCellStyle.setFont(font);

		// 担当セル：左寄せ
		CellStyle tantoCellStyle = workbook.createCellStyle();
		tantoCellStyle.setAlignment(HorizontalAlignment.LEFT);
		tantoCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		tantoCellStyle.setFont(font);

		int increment = 0;
		if (LoiozCollectionUtils.isNotEmpty(list)) {

			for (AnkenTantoAccountDto dto : list) {
				// 挿入行から最終行までを追加行数分シフト
				sheet.shiftRows(insertRow, lastRow, SOUFUSHO_ADD_ROW);
				// 挿入行を作成
				sheet.createRow(insertRow);
				sheet.createRow(insertRow + 1).setHeightInPoints(15);// 空行も書き込みが可能なように行の作成を行う

				// 挿入行を取得
				Row row = sheet.getRow(insertRow);

				// 担当（弁護士、事務）情報
				List<String> tantoList = dto.getTantoDispList();

				// 弁護士／担当事務
				int cellStratIdx = 9;
				row.createCell(cellStratIdx).setCellStyle(bengoshiJimuCellStyle);
				setVal(sheet, row.getRowNum(), cellStratIdx++, tantoList.get(0));

				// 名前（売上計上先、弁護士は均等割りで表示）
				if (!TantoType.JIMU.equalsByCode(dto.getTantoType())) {
					// 弁護士、売上計上先
					row.createCell(cellStratIdx).setCellStyle(bengoshiCellStyle);
				} else {
					// 事務、その他
					row.createCell(cellStratIdx).setCellStyle(jimuCellStyle);
				}
				setVal(sheet, row.getRowNum(), cellStratIdx++, tantoList.get(1));

				// (担当)
				row.createCell(cellStratIdx).setCellStyle(tantoCellStyle);
				if (dispFlg) {
					setVal(sheet, row.getRowNum(), cellStratIdx++, tantoList.get(2));
				} else {
					setVal(sheet, row.getRowNum(), cellStratIdx++, CommonConstant.BLANK);
				}

				// 挿入行を増加
				insertRow += SOUFUSHO_ADD_ROW;
				// 追加行数分最終行を増加
				lastRow += SOUFUSHO_ADD_ROW;
				// カウント変数を増加
				increment += SOUFUSHO_ADD_ROW;
			}

		}

		return increment;
	}

	/**
	 * 案件名の表示
	 * 
	 * ※ システム上で追加した行に対して書き込みが必要になるケースであるため、
	 * 通常のデータ書き込みとは異なり、スタイルの作成などを行う。
	 * 
	 * @param workbook
	 * @param sheet
	 * @param writeRowNum
	 * @param ankenName
	 * @param bengoshiInsertRowCount
	 * @return
	 */
	private int writeAnkenName(Workbook workbook, Sheet sheet, int writeRowNum, String ankenName, int bengoshiInsertRowCount) {

		int isertRowCount = 0;

		if (StringUtils.isEmpty(ankenName)) {
			return isertRowCount;
		}

		// 表示用案件名
		String dispAnkenName = "件名：" + ankenName;

		// 1行に書き込める文字列の最大値
		int writeStrLengthCount = 18;

		// 案件名を 18文字/行 に変換
		int wirteRowCount = Double.valueOf(Math.ceil((double) dispAnkenName.length() / writeStrLengthCount)).intValue();

		// 案件名書き込みに必要な行数
		int needInsertRowCount = wirteRowCount + 1;// 1は余白分

		// 弁護士書き込み時の追加された行数より、案件名による書き込み行数が多い場合のみ行を追加する
		if (needInsertRowCount > bengoshiInsertRowCount) {
			// 挿入対象行
			int insertRowIdx = SEIKYU_SEISAN_BENGOSHI_ADD_ROW_IDX + bengoshiInsertRowCount;

			// 最終行
			int lastRow = sheet.getLastRowNum();

			// 挿入行から最終行までを追加行数分シフト
			sheet.shiftRows(insertRowIdx, lastRow, needInsertRowCount - bengoshiInsertRowCount);

			// 追加する行を作成する
			for (int i = 0; i < needInsertRowCount - bengoshiInsertRowCount; i++) {
				sheet.createRow(insertRowIdx + i).setHeightInPoints(15);
			}

			isertRowCount = needInsertRowCount - bengoshiInsertRowCount;
		}

		int colIdx = CellReference.convertColStringToIndex("B");

		// 共通フォント
		Font font = workbook.createFont();
		font.setFontName(OutputConstant.FONT_MINCHO);
		font.setFontHeight((short) FONT_SIZE_11);

		// 案件名セルスタイル
		CellStyle ankenNameCellStyle = workbook.createCellStyle();
		ankenNameCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		ankenNameCellStyle.setWrapText(true);
		ankenNameCellStyle.setFont(font);

		sheet.getRow(writeRowNum).createCell(colIdx).setCellStyle(ankenNameCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(writeRowNum, writeRowNum + wirteRowCount - 1, colIdx, CellReference.convertColStringToIndex("G")));
		setVal(sheet, writeRowNum, colIdx, dispAnkenName);

		return isertRowCount;
	}

	/**
	 * 弁護士報酬欄の設定
	 *
	 * @param sheet
	 * @param list
	 * @param currentRow
	 * @return currentRow
	 */
	private void writeBengoshiHoshu(Workbook workbook, Sheet sheet, int insertRowIdx, List<ExcelLawyerHoshuDto> hoshuMeisaiDtoList, int addHoshuRowSize) {

		Font font = workbook.createFont();
		font.setFontName(OutputConstant.FONT_MEIRYO);
		font.setFontHeight((short) FONT_SIZE_9);

		// 金額
		CellStyle kingakuCellStyle = workbook.createCellStyle();
		kingakuCellStyle.setAlignment(HorizontalAlignment.RIGHT);
		kingakuCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		kingakuCellStyle.setFont(font);

		int listSize = hoshuMeisaiDtoList.size();

		for (int i = 0; i < addHoshuRowSize; i++) {

			// 挿入行を取得
			Row row = sheet.getRow(insertRowIdx);
			int rowNum = row.getRowNum();

			if (i < listSize) {
				ExcelLawyerHoshuDto dto = hoshuMeisaiDtoList.get(i);
				String komokuName = null;
				// 報酬項目が存在するか判定
				if (dto.getLawyerHoshu() != null) {
					komokuName = dto.getLawyerHoshu().getVal();
				}
				// 報酬項目名を設定
				setVal(sheet, rowNum, "D", StringUtils.null2blank(komokuName));

				// 報酬金額欄を取得
				setVal(sheet, rowNum, "F", dto.getTotalKingaku().doubleValue());
				setVal(sheet, rowNum, "G", ChohyoConstatnt.JP_EN);

				// 備考欄に適用消費税率を記載
				setVal(sheet, rowNum, "H", dto.getTaxRateDisp().doubleValue());

			} else {

				// 報酬項目名を設定
				setVal(sheet, rowNum, "D", CommonConstant.BLANK);

				// 報酬金額欄を取得
				setVal(sheet, rowNum, "F", CommonConstant.BLANK);
				setVal(sheet, rowNum, "G", ChohyoConstatnt.JP_EN);

				// 備考欄に適用消費税率を記載
				setVal(sheet, rowNum, "H", CommonConstant.BLANK);

			}
			insertRowIdx++;
		}

	}

}
