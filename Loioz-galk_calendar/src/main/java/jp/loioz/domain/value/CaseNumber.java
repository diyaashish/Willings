package jp.loioz.domain.value;

import org.seasar.doma.Column;
import org.seasar.doma.Embeddable;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.entity.TSaibanJikenEntity;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * 事件番号
 */
@Value
@RequiredArgsConstructor
@Embeddable
public class CaseNumber {

	/** 元号 */
	@Column(name = "jiken_gengo")
	EraType gengo;

	/** 年 */
	@Column(name = "jiken_year")
	String year;

	/** 符号 */
	@Column(name = "jiken_mark")
	String mark;

	/** 番号 */
	@Column(name = "jiken_no")
	String no;

	/**
	 * 引数から事件番号を作成
	 * 
	 * @param ankenName
	 * @param bunyaId
	 * @return
	 */
	public static CaseNumber of(EraType gengo, String year, String mark, String no) {
		return new CaseNumber(gengo, year, mark, no);
	}

	/**
	 * 裁判-事件エンティティから事件番号に変換する
	 *
	 * @param entity エンティティ
	 * @return 事件番号
	 */
	public static CaseNumber fromEntity(TSaibanJikenEntity entity) {
		if (entity == null) {
			return null;
		}
		return new CaseNumber(EraType.of(entity.getJikenGengo()), entity.getJikenYear(), entity.getJikenMark(), entity.getJikenNo());
	}

	@Override
	public String toString() {
		if (gengo == null || StringUtils.isAllEmpty(year, mark, no)) {
			// 全て未入力の場合
			return CommonConstant.BLANK;

		} else {
			String displayYear = "";
			String displayMark = "";
			String displayNo = "";

			if (!StringUtils.isEmpty(year)) {
				// 1年は元年と表示する
				if (year.equals("1")) {
					displayYear = String.format("%s%s年", gengo.getVal(), "元");	
				} else {
					displayYear = String.format("%s%s年", gengo.getVal(), year);
				}
			}

			if (!StringUtils.isEmpty(mark)) {
				displayMark = String.format("（%s）", mark);
			}

			if (!StringUtils.isEmpty(no)) {
				displayNo = String.format("第%s号", no);
			}

			String caseNumber = displayYear + displayMark + displayNo;
			return caseNumber;
		}
	}
}
