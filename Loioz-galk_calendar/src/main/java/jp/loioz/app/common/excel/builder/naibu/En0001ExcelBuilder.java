package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.GyomuHistoryExcelDto;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.AnkenDtoForGyomuHistory;
import jp.loioz.dto.GyomuHistoryDto;
import lombok.Setter;

/**
 * 業務履歴出力用クラス
 *
 */
@Setter
public class En0001ExcelBuilder extends AbstractMakeExcelBuilder<GyomuHistoryExcelDto> {
	/** 業務履歴一覧のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 5;

	/** 案件名が1行で表示できる長さ */
	private static final int LENGTH_OF_ANKEN_NAME_THAT_FITS_IN_ONE_CELL = 110;

	/**
	 * Builder用DTO
	 */
	private GyomuHistoryExcelDto gyomuHistoryExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GyomuHistoryExcelDto createNewTargetBuilderDto() {
		return new GyomuHistoryExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_1, response);
	}

	@Override
	/**
	 * Excelファイル内にデータを出力する
	 *
	 * @param book 出力先のExcelワークブック情報
	 */
	protected void setExcelData(XSSFWorkbook book) {
		// シート情報の取得
		Sheet sheet;
		if (CommonConstant.TransitionType.CUSTOMER.equalsByCode(gyomuHistoryExcelDto.getTransitionType())) {
			// 顧客軸の場合
			sheet = book.getSheetAt(0);
			book.removeSheetAt(1);

			// 出力日
			setVal(sheet, 1, "F", LocalDate.now());
			// 顧客ID
			setVal(sheet, 1, "A", gyomuHistoryExcelDto.getCustomerId());
			// 顧客名
			setVal(sheet, 1, "B", gyomuHistoryExcelDto.getCustomerName());
			// 案件ID
			setVal(sheet, 2, "A", gyomuHistoryExcelDto.getAnkenId());
			// 案件名
			setVal(sheet, 2, "B", gyomuHistoryExcelDto.getAnkenName());
			// 案件名が長い場合は行の高さを増やす
			if (gyomuHistoryExcelDto.getAnkenName().length() > LENGTH_OF_ANKEN_NAME_THAT_FITS_IN_ONE_CELL) {
				sheet.getRow(2).setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			}

		} else {
			// 案件軸の場合
			sheet = book.getSheetAt(1);
			book.removeSheetAt(0);

			// 出力日
			setVal(sheet, 1, "F", LocalDate.now());
			// 案件ID
			setVal(sheet, 1, "A", gyomuHistoryExcelDto.getAnkenId());
			// 案件名
			setVal(sheet, 1, "B", gyomuHistoryExcelDto.getAnkenName());
			// 案件名が長い場合は行の高さを増やす
			if (gyomuHistoryExcelDto.getAnkenName().length() > LENGTH_OF_ANKEN_NAME_THAT_FITS_IN_ONE_CELL) {
				sheet.getRow(1).setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			}
			// 顧客ID
			setVal(sheet, 2, "A", gyomuHistoryExcelDto.getCustomerId());
			// 顧客名
			setVal(sheet, 2, "B", gyomuHistoryExcelDto.getCustomerName());

		}

		// 業歴データ取得
		List<GyomuHistoryDto> dataList = gyomuHistoryExcelDto.getGyomuHistoryDtoList();

		// 行追加
		addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, dataList.size());
		boolean ankenFlg = false;
		if (TransitionType.ANKEN.equalsByCode(gyomuHistoryExcelDto.getTransitionType())) {
			ankenFlg = true;
		}

		int rowIdx = START_ROW_IDX;
		for (GyomuHistoryDto dto : dataList) {
			// データの設定
			int colIdx = 0;

			// 対応日時
			setVal(sheet, rowIdx, colIdx++, dto.getSupportedDateTime());
			// 件名
			setVal(sheet, rowIdx, colIdx++, dto.getSubject());
			// 本文
			setVal(sheet, rowIdx, colIdx++, dto.getMainText());

			if (ankenFlg) {
				// 案件軸の場合
				
				// 関連情報
				String saibanCustomerInfo = CommonConstant.BLANK;
				if (TransitionType.CUSTOMER.equalsByCode(dto.getTransitionType().getCd())) {
					// 履歴の登録元が顧客の場合（関連情報はハイフン）
					saibanCustomerInfo = CommonConstant.HYPHEN;
				} else if (CommonConstant.TransitionType.ANKEN.equalsByCode(dto.getTransitionType().getCd())) {
					// 履歴の登録元が案件の場合（関連情報を設定）
					
					// 裁判情報
					String saibanInfo = CommonConstant.BLANK;
					if (!StringUtils.isEmpty(dto.getSaibanBranchNo())) {
						saibanInfo = StringUtils.isEmpty(dto.getJikenName()) ? "(事件名未入力)" : dto.getJikenName();
						saibanInfo += CommonConstant.SPACE + dto.getCaseNumber().toString();
					}
					
					// 顧客情報
					String customerInfo = CommonConstant.BLANK;
					for (int i = 0; i < dto.getAnkenDtoList().size(); i++) {
						AnkenDtoForGyomuHistory ankenDto = dto.getAnkenDtoList().get(i);
						String customerName = ankenDto.getCustomerName() + CommonConstant.SPACE + "(" + ankenDto.getCustomerId() + ")";
						if (i == 0) {
							customerInfo = customerName;
						} else {
							customerInfo += CommonConstant.LINE_FEED_CODE + customerName;
						}
					}
					
					if (!StringUtils.isEmpty(saibanInfo) && !StringUtils.isEmpty(customerInfo)) {
						customerInfo = CommonConstant.LINE_FEED_CODE + customerInfo;
					}
					
					saibanCustomerInfo = saibanInfo + customerInfo;
				}
				setVal(sheet, rowIdx, colIdx++, saibanCustomerInfo);
				
				// 履歴登録元
				if (TransitionType.CUSTOMER.equalsByCode(dto.getTransitionType().getCd())) {
					// 顧客の場合
					setVal(sheet, rowIdx, colIdx++, dto.getCustomerName() + CommonConstant.SPACE + "(" + dto.getCustomerId().toString() + ")");
				} else if (CommonConstant.TransitionType.ANKEN.equalsByCode(dto.getTransitionType().getCd())) {
					// 履歴の登録元が案件の場合（案件軸なのでハイフン表示）
					setVal(sheet, rowIdx, colIdx++, CommonConstant.HYPHEN);
				}
				
			} else {
				// 顧客軸の場合
				
				// 関連の案件情報
				String ankenInfo = CommonConstant.BLANK;
				if (TransitionType.CUSTOMER.equalsByCode(dto.getTransitionType().getCd())) {
					// 履歴の登録元が顧客の場合（関連情報を設定）
					for (int i = 0; i < dto.getAnkenDtoList().size(); i++) {
						AnkenDtoForGyomuHistory ankenDto = dto.getAnkenDtoList().get(i);
						String ankenName = StringUtils.isEmpty(ankenDto.getAnkenName()) ? "(案件名未入力)" : ankenDto.getAnkenName();
						if (i == 0) {
							ankenInfo = ankenName + CommonConstant.SPACE + "(" + ankenDto.getAnkenId() + ")";
						} else {
							ankenInfo += CommonConstant.LINE_FEED_CODE + ankenName + CommonConstant.SPACE + "(" + ankenDto.getAnkenId() + ")";
						}
					}
				} else if (CommonConstant.TransitionType.ANKEN.equalsByCode(dto.getTransitionType().getCd())) {
					// 履歴の登録元が案件の場合（関連情報はハイフン）
					ankenInfo = CommonConstant.HYPHEN;
				}
				setVal(sheet, rowIdx, colIdx++, ankenInfo);
				
				// 履歴登録元の情報
				if (TransitionType.CUSTOMER.equalsByCode(dto.getTransitionType().getCd())) {
					// 履歴の登録元が顧客の場合（顧客軸なのでハイフン表示）
					setVal(sheet, rowIdx, colIdx++, CommonConstant.HYPHEN);
				} else if (CommonConstant.TransitionType.ANKEN.equalsByCode(dto.getTransitionType().getCd())) {
					// 履歴の登録元が案件の場合
					String ankenName = StringUtils.isEmpty(dto.getAnkenName()) ? "(案件名未入力)" : dto.getAnkenName();
					setVal(sheet, rowIdx, colIdx++, ankenName + CommonConstant.SPACE + "(" + dto.getAnkenId() + ")");
				}
			}

			// 登録者
			setVal(sheet, rowIdx, colIdx++, dto.getCreaterName());

			rowIdx++;
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
