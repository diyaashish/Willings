package jp.loioz.app.common.excel.builder.naibu;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jp.loioz.app.common.excel.common.AbstractMakeExcelBuilder;
import jp.loioz.app.common.excel.dto.TaskListExcelDto;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.ChohyoConstatnt.OutputExcelTemplate;
import jp.loioz.common.constant.CommonConstant.TaskMenu;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.TaskListDto;
import jp.loioz.dto.TaskListForTaskManagementDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * タスク一覧出力用クラス
 *
 */
@Setter
public class En0011ExcelBuilder extends AbstractMakeExcelBuilder<TaskListExcelDto> {

	/** タスク一覧のデータを入れる開始位置(Row) */
	public static final int START_ROW_IDX = 2;

	/** 今日のタスク画面用タイトル */
	public static final String TODAY_TASK_TITLE = "今日（{0}）のタスク";

	/** タイトルが1行で表示できる長さ */
	private static final int LENGTH_OF_TITLE_THAT_FITS_IN_ONE_CELL = 97;

	/**
	 * 一覧定義（通常）
	 */
	@Getter
	@AllArgsConstructor
	private enum TaskTableContent {

		KENMEI(0, "件名"),
		SHOUSAI(1, "詳細"),
		SHINTYOKU(2, "進捗"),
		LIMIT_DATE(3, "期日"),
		TODAY_TASK(4, "今日のタスク"),
		ANKEN_ID(5, "案件ID"),
		ANKEN_NAME(6, "案件名"),
		TANTO(7, "担当者");

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * 割り当てられたタスクの一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum AssignedTaskTableContent {

		IRAIMOTO(0, "依頼元"),
		KENMEI(1, "件名"),
		SHOUSAI(2, "詳細"),
		SHINTYOKU(3, "進捗"),
		LIMIT_DATE(4, "期日"),
		TODAY_TASK(5, "今日のタスク"),
		ANKEN_ID(6, "案件ID"),
		ANKEN_NAME(7, "案件名"),
		TANTO(8, "担当者");

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * 割り当てたタスクの一覧定義
	 */
	@Getter
	@AllArgsConstructor
	private enum AssignTaskTableContent {

		IRAISAKI(0, "依頼先"),
		KENMEI(1, "件名"),
		SHOUSAI(2, "詳細"),
		SHINTYOKU(3, "進捗"),
		LIMIT_DATE(4, "期日"),
		TODAY_TASK(5, "今日のタスク"),
		ANKEN_ID(6, "案件ID"),
		ANKEN_NAME(7, "案件名"),
		TANTO(8, "担当者");

		/** 列 */
		private int colIndex;

		/** 項目名 */
		private String itemName;
	}

	/**
	 * Builder用DTO
	 */
	private TaskListExcelDto taskListExcelDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskListExcelDto createNewTargetBuilderDto() {
		return new TaskListExcelDto();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeExcelFile(HttpServletResponse response) throws IOException, Exception {
		super.makeExcelFile(OutputExcelTemplate.EXCEL_NAIBU_11, response);
	}

	/**
	 * Excelファイルに出力データを設定する
	 *
	 * @param workbook
	 */
	@Override
	public void setExcelData(XSSFWorkbook workbook) throws Exception {

		// シート情報の取得
		Sheet sheet;
		if (TaskMenu.TODAY_TASK.equalsByCode(taskListExcelDto.getSelectedTaskListMenu())) {
			// 今日のタスク画面
			sheet = workbook.getSheetAt(0);
			workbook.removeSheetAt(2);
			workbook.removeSheetAt(1);
			String todayStr = DateUtils.parseToString(LocalDate.now(), DateUtils.DATE_JP_YYYY_M_D);
			// シート名
			workbook.setSheetName(0, MessageFormat.format(TODAY_TASK_TITLE, todayStr));
			// A1タイトル
			setVal(sheet, 0, "A", MessageFormat.format(TODAY_TASK_TITLE, todayStr));
			// 出力日
			setVal(sheet, 0, "H", LocalDate.now());
		} else if (TaskMenu.FUTURE_TASK.equalsByCode(taskListExcelDto.getSelectedTaskListMenu())) {
			// 期限付きのタスク画面
			sheet = workbook.getSheetAt(0);
			workbook.removeSheetAt(2);
			workbook.removeSheetAt(1);
			// シート名
			workbook.setSheetName(0, TaskMenu.FUTURE_TASK.getVal());
			// A1タイトル
			setVal(sheet, 0, "A", TaskMenu.FUTURE_TASK.getVal());
			// 出力日
			setVal(sheet, 0, "H", LocalDate.now());
		} else if (TaskMenu.ALL_TASK.equalsByCode(taskListExcelDto.getSelectedTaskListMenu())) {
			// すべてのタスク画面
			sheet = workbook.getSheetAt(0);
			workbook.removeSheetAt(2);
			workbook.removeSheetAt(1);
			// シート名
			workbook.setSheetName(0, TaskMenu.ALL_TASK.getVal());
			// A1タイトル
			setVal(sheet, 0, "A", TaskMenu.ALL_TASK.getVal());
			// 出力日
			setVal(sheet, 0, "H", LocalDate.now());
		} else if (TaskMenu.OVERDUE_TASK.equalsByCode(taskListExcelDto.getSelectedTaskListMenu())) {
			// 期限を過ぎたタスク画面
			sheet = workbook.getSheetAt(0);
			workbook.removeSheetAt(2);
			workbook.removeSheetAt(1);
			// シート名
			workbook.setSheetName(0, TaskMenu.OVERDUE_TASK.getVal());
			// A1タイトル
			setVal(sheet, 0, "A", TaskMenu.OVERDUE_TASK.getVal());
			// 出力日
			setVal(sheet, 0, "H", LocalDate.now());
		} else if (TaskMenu.ASSIGNED_TASK.equalsByCode(taskListExcelDto.getSelectedTaskListMenu())) {
			// 割り当てられたタスク画面
			sheet = workbook.getSheetAt(1);
			workbook.removeSheetAt(2);
			workbook.removeSheetAt(0);
			// シート名
			workbook.setSheetName(0, TaskMenu.ASSIGNED_TASK.getVal());
			// A1タイトル
			setVal(sheet, 0, "A", TaskMenu.ASSIGNED_TASK.getVal());
			// 出力日
			setVal(sheet, 0, "I", LocalDate.now());

		} else if (TaskMenu.ASSIGN_TASK.equalsByCode(taskListExcelDto.getSelectedTaskListMenu())) {
			// 割り当てたタスク画面
			sheet = workbook.getSheetAt(2);
			workbook.removeSheetAt(1);
			workbook.removeSheetAt(0);
			// シート名
			workbook.setSheetName(0, TaskMenu.ASSIGN_TASK.getVal());
			// A1タイトル
			setVal(sheet, 0, "A", TaskMenu.ASSIGN_TASK.getVal());
			// 出力日
			setVal(sheet, 0, "I", LocalDate.now());

		} else if (TaskMenu.CLOSE_TASK.equalsByCode(taskListExcelDto.getSelectedTaskListMenu())) {
			// 完了したタスク画面
			sheet = workbook.getSheetAt(0);
			workbook.removeSheetAt(2);
			workbook.removeSheetAt(1);
			// シート名
			workbook.setSheetName(0, TaskMenu.CLOSE_TASK.getVal());
			// A1タイトル
			setVal(sheet, 0, "A", TaskMenu.CLOSE_TASK.getVal());
			// 出力日
			setVal(sheet, 0, "H", LocalDate.now());
		} else if (TaskMenu.TASK_ANKEN.equalsByCode(taskListExcelDto.getSelectedTaskListMenu())) {
			// 案件タスク画面
			sheet = workbook.getSheetAt(0);
			workbook.removeSheetAt(2);
			workbook.removeSheetAt(1);
			// シート名
			workbook.setSheetName(0, getFileName() + "のタスク");
			// A1タイトル
			setVal(sheet, 0, "A", getFileName() + "のタスク");
			// タイトルの文字数が長い場合は行の高さを増やす
			if (getFileName().length() > LENGTH_OF_TITLE_THAT_FITS_IN_ONE_CELL) {
				sheet.getRow(0).setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			}
			// 出力日
			setVal(sheet, 0, "H", LocalDate.now());
		} else {
			// 検索結果タスク画面
			sheet = workbook.getSheetAt(0);
			workbook.removeSheetAt(2);
			workbook.removeSheetAt(1);
			// シート名
			workbook.setSheetName(0, TaskMenu.SEARCH_RESULTS_TASK.getVal() + "のタスク");
			// A1タイトル
			setVal(sheet, 0, "A", TaskMenu.SEARCH_RESULTS_TASK.getVal() + "のタスク");
			// 出力日
			setVal(sheet, 0, "H", LocalDate.now());
		}

		// データ部分作成
		makeBody(workbook, sheet);
	}


	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 一覧出力のデータ部分を作成する
	 * 
	 * @param workbook
	 * @param listSheet
	 */
	private void makeBody(XSSFWorkbook book, Sheet sheet) throws IOException {

		if (TaskMenu.ASSIGNED_TASK.equalsByCode(taskListExcelDto.getSelectedTaskListMenu())) {
			// 割り当てられたタスク画面

			// タスク一覧データ取得
			List<TaskListForTaskManagementDto> taskListForTaskManagementDtoList = taskListExcelDto.getTaskListForTaskManagementDtoList();

			// 行追加
			int lineCount = 0;
			for (TaskListForTaskManagementDto taskListForTaskManagementDto : taskListForTaskManagementDtoList) {
				List<TaskListDto> taskListDtoList = taskListForTaskManagementDto.getTaskList();
				lineCount += taskListDtoList.size();
			}
			addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, lineCount);

			// 一覧データ部分出力
			int rowIdx = START_ROW_IDX;
			for (TaskListForTaskManagementDto taskListForTaskManagementDto : taskListForTaskManagementDtoList) {
				List<TaskListDto> taskListDtoList = taskListForTaskManagementDto.getTaskList();
				for (TaskListDto taskListDto : taskListDtoList) {

					// 依頼元
					setVal(sheet, rowIdx, AssignedTaskTableContent.IRAIMOTO.getColIndex(), taskListForTaskManagementDto.getTaskAssignName());

					// 件名
					setVal(sheet, rowIdx, AssignedTaskTableContent.KENMEI.getColIndex(), taskListDto.getTitle());

					// 詳細
					setVal(sheet, rowIdx, AssignedTaskTableContent.SHOUSAI.getColIndex(), taskListDto.getContent());

					// 進捗
					setVal(sheet, rowIdx, AssignedTaskTableContent.SHINTYOKU.getColIndex(), taskListDto.getTaskStatus().getVal());

					// 期日
					DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
					setVal(sheet, rowIdx, AssignedTaskTableContent.LIMIT_DATE.getColIndex(), taskListDto.getLimitDateForDisplay() == null ? "" : taskListDto.getLimitDateForDisplay().format(dateTimeFormatter));

					// 今日のタスク
					if (taskListDto.getTodayTaskDate() != null) {
						setVal(sheet, rowIdx, AssignedTaskTableContent.TODAY_TASK.getColIndex(), LocalDate.now().isEqual(taskListDto.getTodayTaskDate()) ? "〇" : "");
					}

					// 案件名ID
					if (taskListDto.getAnkenId() != null && !taskListDto.getAnkenId().asLong().equals(Long.valueOf(0))) {
						setVal(sheet, rowIdx, AssignedTaskTableContent.ANKEN_ID.getColIndex(), taskListDto.getAnkenId().toString());
					}

					// 案件名
					if (taskListDto.getAnkenId() != null && !taskListDto.getAnkenId().asLong().equals(Long.valueOf(0))) {
						setVal(sheet, rowIdx, AssignedTaskTableContent.ANKEN_NAME.getColIndex(), (taskListDto.getAnkenName() == null || StringUtils.isEmpty(taskListDto.getAnkenName().getAnkenName())) ? "(案件名未入力)" : taskListDto.getAnkenName().toString());
					}

					// 担当者
					if (!LoiozCollectionUtils.isEmpty(taskListDto.getWorkerAccountList())) {
						List<String> tantoNameList = taskListDto.getWorkerAccountList().stream().map(SelectOptionForm::getLabel).collect(Collectors.toList());
						String tanto = String.join(", ", tantoNameList);
						setVal(sheet, rowIdx, AssignedTaskTableContent.TANTO.getColIndex(), tanto);
					}

					rowIdx++;
				}
			}

		} else if (TaskMenu.ASSIGN_TASK.equalsByCode(taskListExcelDto.getSelectedTaskListMenu())) {
			// 割り当てたタスク画面の一覧出力

			// タスク一覧データ取得
			List<TaskListForTaskManagementDto> taskListForTaskManagementDtoList = taskListExcelDto.getTaskListForTaskManagementDtoList();

			// 行追加
			int lineCount = 0;
			for (TaskListForTaskManagementDto taskListForTaskManagementDto : taskListForTaskManagementDtoList) {
				List<TaskListDto> taskListDtoList = taskListForTaskManagementDto.getTaskList();
				lineCount += taskListDtoList.size();
			}
			addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, lineCount);

			// 一覧データ部分出力
			int rowIdx = START_ROW_IDX;
			for (TaskListForTaskManagementDto taskListForTaskManagementDto : taskListForTaskManagementDtoList) {
				List<TaskListDto> taskListDtoList = taskListForTaskManagementDto.getTaskList();
				for (TaskListDto taskListDto : taskListDtoList) {

					// 依頼先
					setVal(sheet, rowIdx, AssignTaskTableContent.IRAISAKI.getColIndex(), taskListForTaskManagementDto.getTaskAssignName());

					// 件名
					setVal(sheet, rowIdx, AssignTaskTableContent.KENMEI.getColIndex(), taskListDto.getTitle());

					// 詳細
					setVal(sheet, rowIdx, AssignTaskTableContent.SHOUSAI.getColIndex(), taskListDto.getContent());

					// 進捗
					setVal(sheet, rowIdx, AssignTaskTableContent.SHINTYOKU.getColIndex(), taskListDto.getTaskStatus().getVal());

					// 期日
					DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
					setVal(sheet, rowIdx, AssignTaskTableContent.LIMIT_DATE.getColIndex(), taskListDto.getLimitDateForDisplay() == null ? "" : taskListDto.getLimitDateForDisplay().format(dateTimeFormatter));

					// 今日のタスク
					if (taskListDto.getTodayTaskDate() != null) {
						setVal(sheet, rowIdx, AssignTaskTableContent.TODAY_TASK.getColIndex(), LocalDate.now().isEqual(taskListDto.getTodayTaskDate()) ? "〇" : "");
					}

					// 案件名ID
					if (taskListDto.getAnkenId() != null && !taskListDto.getAnkenId().asLong().equals(Long.valueOf(0))) {
						setVal(sheet, rowIdx, AssignTaskTableContent.ANKEN_ID.getColIndex(), (taskListDto.getAnkenId().toString()));
					}

					// 案件名
					if (taskListDto.getAnkenId() != null && !taskListDto.getAnkenId().asLong().equals(Long.valueOf(0))) {
						setVal(sheet, rowIdx, AssignTaskTableContent.ANKEN_NAME.getColIndex(), (taskListDto.getAnkenName() == null || StringUtils.isEmpty(taskListDto.getAnkenName().getAnkenName())) ? "(案件名未入力)" : taskListDto.getAnkenName().toString());
					}

					// 担当者
					if (!LoiozCollectionUtils.isEmpty(taskListDto.getWorkerAccountList())) {
						List<String> tantoNameList = taskListDto.getWorkerAccountList().stream().map(SelectOptionForm::getLabel).collect(Collectors.toList());
						String tanto = String.join(", ", tantoNameList);
						setVal(sheet, rowIdx, AssignTaskTableContent.TANTO.getColIndex(), tanto);
					}

					rowIdx++;
				}
			}

		} else {
			// 今日のタスク、期限付きのタスク、すべてのタスク、期限を過ぎたタスク、完了したタスク、タスク検索画面の一覧出力

			// タスク一覧データ取得
			List<TaskListDto> dataList = taskListExcelDto.getTaskListDtoList();

			// 行追加
			addRows(START_ROW_IDX, START_ROW_IDX, book, sheet, dataList.size());

			// 一覧データ部分出力
			int rowIdx = START_ROW_IDX;
			for (TaskListDto dto : dataList) {

				// 件名
				setVal(sheet, rowIdx, TaskTableContent.KENMEI.getColIndex(), dto.getTitle());

				// 詳細
				setVal(sheet, rowIdx, TaskTableContent.SHOUSAI.getColIndex(), dto.getContent());

				// 進捗
				setVal(sheet, rowIdx, TaskTableContent.SHINTYOKU.getColIndex(), dto.getTaskStatus().getVal());

				// 期日
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				setVal(sheet, rowIdx, TaskTableContent.LIMIT_DATE.getColIndex(), dto.getLimitDateForDisplay() == null ? "" : dto.getLimitDateForDisplay().format(dateTimeFormatter));

				// 今日のタスク
				if (dto.getTodayTaskDate() != null) {
					setVal(sheet, rowIdx, TaskTableContent.TODAY_TASK.getColIndex(), LocalDate.now().isEqual(dto.getTodayTaskDate()) ? "〇" : "");
				}

				// 案件名ID
				if (dto.getAnkenId() != null && !dto.getAnkenId().asLong().equals(Long.valueOf(0))) {
					setVal(sheet, rowIdx, TaskTableContent.ANKEN_ID.getColIndex(), dto.getAnkenId().toString());
				}

				// 案件名
				if (dto.getAnkenId() != null && !dto.getAnkenId().asLong().equals(Long.valueOf(0))) {
					setVal(sheet, rowIdx, TaskTableContent.ANKEN_NAME.getColIndex(), (dto.getAnkenName() == null || StringUtils.isEmpty(dto.getAnkenName().getAnkenName())) ? "(案件名未入力)" : dto.getAnkenName().toString());
				}

				// 担当者
				if (!LoiozCollectionUtils.isEmpty(dto.getWorkerAccountList())) {
					List<String> tantoNameList = dto.getWorkerAccountList().stream().map(SelectOptionForm::getLabel).collect(Collectors.toList());
					String tanto = String.join(", ", tantoNameList);
					setVal(sheet, rowIdx, TaskTableContent.TANTO.getColIndex(), tanto);
				}

				rowIdx++;
			}
		}
	}
}
