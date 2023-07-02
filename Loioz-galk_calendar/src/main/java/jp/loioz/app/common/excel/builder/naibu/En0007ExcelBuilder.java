package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.ShiharaiKeikakuExcelDto;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.ExcelShiharaiKeikakuListDto;
import lombok.Setter;

/**
 * 支払い計画一覧のbuilderクラス
 */
@Setter
public class En0007ExcelBuilder extends AbstractMakeExcelBuilder<ShiharaiKeikakuExcelDto> {

	/**
	 * Builder用DTO
	 */
	private ShiharaiKeikakuExcelDto shiharaiKeikakuExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShiharaiKeikakuExcelDto createNewTargetBuilderDto() {
		return new ShiharaiKeikakuExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_7, response);
	}

	/**
	 * Excelファイル内にデータを出力する
	 *
	 * @param book 出力先のExcelワークブック情報
	 */
	@Override
	protected void setExcelData(XSSFWorkbook workbook) {

		// シート情報の取得
		Sheet sheet = workbook.getSheetAt(0);

		ShiharaiKeikakuExcelDto shiharaiKeikakuExcelDto = this.shiharaiKeikakuExcelDto;

		// 出力日
		setVal(sheet, 1, "I", LocalDate.now());

		// 名前を設定
		setVal(sheet, 2, "A", shiharaiKeikakuExcelDto.getName());

		// 振込先設定を設定
		// 銀行名
		setVal(sheet, 6, "C", StringUtils.defaultString(shiharaiKeikakuExcelDto.getGinkoName()));
		// 支店名
		setVal(sheet, 7, "C", StringUtils.defaultString(shiharaiKeikakuExcelDto.getShitenName()) + "("
				+ StringUtils.defaultString(shiharaiKeikakuExcelDto.getShitenNo()) + ")");
		// 口座番号
		setVal(sheet, 8, "C", "(" + Optional.ofNullable(shiharaiKeikakuExcelDto.getKozaType())
				.map(type -> CommonConstant.KozaType.of(type).getVal()).orElse("") + ")"
				+ StringUtils.defaultString(shiharaiKeikakuExcelDto.getKozaNo()));
		// 口座名義
		setVal(sheet, 9, "C", shiharaiKeikakuExcelDto.getKozaName());

		// データの取得
		List<ExcelShiharaiKeikakuListDto> shiharaiYoteiList = shiharaiKeikakuExcelDto.getShiharaiYoteiList();

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
			if (addRowNum % 2 != 0) {
				// 奇数の場合
				addRowNum = (addRowNum / 2) + 1;

			} else {
				// 偶数の場合
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
}
