package jp.loioz.common.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import jp.loioz.app.common.form.SelectOptionForm;

/**
 * 画面に情報を渡すFormオブジェクトを扱うUtilクラス
 */
public class FormUtil {

	/**
	 * 年の選択オプションのリストを取得する。<br>
	 * （選択肢の値はstartYearの値から1ずつ増加したものとなる。）
	 *
	 * @param startYear 選択肢として生成する値の開始年
	 * @param endYear 選択肢として生成する値の終了年
	 * @param isAsc 選択肢の並び順を昇順とするかどうか
	 * @param labelSuffix 選択肢のラベル文字列に付与するサフィックス (例: labelSuffix="年" -> ラベル:2018年)
	 * @return List<SelectOptionForm> セレクトボックスを構築するoptionのリスト
	 */
	public static List<SelectOptionForm> getSelectYearOptions(int startYear, int endYear, boolean isAsc, String labelSuffix) {

		if (startYear < 1 || startYear > endYear) {
			return Collections.emptyList();
		}

		if (labelSuffix == null) {
			labelSuffix = "";
		}

		List<SelectOptionForm> selectOptions = new ArrayList<>();

		// 開始年からの昇順で選択肢のListを生成する
		for (int year = startYear; year <= endYear; year++) {

			String optionValueStr = String.valueOf(year);

			SelectOptionForm option = new SelectOptionForm(optionValueStr, optionValueStr + labelSuffix);
			selectOptions.add(option);
		}

		if (!isAsc) {
			// 昇順指定ではない場合はリストの並びを逆順（降順）にする
			Collections.reverse(selectOptions);
		}

		return selectOptions;
	}

	/**
	 * プルダウンの追加処理
	 * 
	 * @param pullDown 追加先のプルダウンリスト
	 * @param entity プルダウンに追加するデータ
	 * @param valueMaper バリュー値マッパー
	 * @param labelMaper ラベル値マッパー
	 * @return
	 */
	public static <T> List<SelectOptionForm> deletedIncludeOptionForm(List<SelectOptionForm> pullDown, T entity, Function<T, Long> valueMaper, Function<T, String> labelMaper) {

		SelectOptionForm sof = new SelectOptionForm(valueMaper.apply(entity), labelMaper.apply(entity));

		if (!existsDuplication(pullDown, sof)) {
			pullDown.add(sof);
		}

		return pullDown;
	}

	/**
	 * プルダウンの追加処理
	 * 
	 * @param pullDown 追加先のプルダウンリスト
	 * @param entities プルダウンに追加するデータ
	 * @param valueMaper バリュー値マッパー
	 * @param labelMaper ラベル値マッパー
	 * @return
	 */
	public static <T> List<SelectOptionForm> deletedIncludeOptionForm(List<SelectOptionForm> pullDown, List<T> entities, Function<T, Long> valueMaper, Function<T, String> labelMaper) {

		for (T entity : entities) {
			SelectOptionForm sof = new SelectOptionForm(valueMaper.apply(entity), labelMaper.apply(entity));
			if (!existsDuplication(pullDown, sof)) {
				pullDown.add(sof);
			}
		}

		return pullDown;
	}

	/**
	 * プルダウンの追加時に重複データが存在するかしないか
	 * 
	 * @param pullDown
	 * @param addOption
	 * @return
	 */
	private static boolean existsDuplication(List<SelectOptionForm> pullDown, SelectOptionForm addOption) {
		return pullDown.stream().anyMatch(e -> Objects.equals(e.getValue(), addOption.getValue()));
	}
}
