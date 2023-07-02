package jp.loioz.common.constant;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.loioz.common.constant.CommonConstant.TojishaHyoki;
import lombok.NoArgsConstructor;

/**
 * 定数Map
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ConstantMaps {

	/** 事件符号->略称変換マップ */
	public static final Map<String, String> JIKEN_MARK_ABBREVIATION = new LinkedHashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			this.put("イ", "和解");
			this.put("ロ", "督促");
			this.put("ハ", "簡裁訴訟");
			this.put("手ハ", "手形小切");
			this.put("少コ", "少額訴訟");
			this.put("少エ", "少額異議");
			this.put("ハレ", "控訴提起");
			this.put("ハツ", "飛躍提起");
			this.put("少テ", "特別上告");
			this.put("ニ", "再審");
			this.put("ヘ", "公示催告");
			this.put("ト", "保全命令");
			this.put("ハソ", "抗告提起");
			this.put("借", "借地非訟");
			this.put("ノ", "一般調停");
			this.put("ユ", "宅地等調停");
			this.put("セ", "農事調停");
			this.put("メ", "商事調停");
			this.put("ス", "鉱害調停");
			this.put("交", "交通調停");
			this.put("公", "公害等調停");
			this.put("特ノ", "特定調停");
			this.put("少ル", "少額執行");
			this.put("ア", "過料");
			this.put("キ", "共助");
			this.put("サ", "民事雑");
			this.put("ワ", "訴訟");
			this.put("手ワ", "手形小切手");
			this.put("ワネ", "控訴提起");
			this.put("ワオ", "飛躍提起");
			this.put("ワ受", "飛躍受理");
			this.put("カ", "再審");
			this.put("ヘ", "公示催告");
			this.put("ヨ", "保全命令");
			this.put("レ", "控訴");
			this.put("レツ", "上告提起");
			this.put("ソ", "抗告");
			this.put("ソラ", "抗告提起");
			this.put("チ", "民事非訟");
			this.put("ヒ", "商事非訟");
			this.put("借チ", "借地非訟");
			this.put("シ", "罹災等");
			this.put("配チ", "保護命令");
			this.put("労", "労働審判");
			this.put("ノ", "一般調停");
			this.put("ユ", "宅地等調停");
			this.put("セ", "農事調停");
			this.put("メ", "商事調停");
			this.put("ス", "鉱害調停");
			this.put("交", "交通調停");
			this.put("公", "公害等調停");
			this.put("特ノ", "特定調停");
			this.put("リ", "配当等");
			this.put("ヌ", "執行");
			this.put("ル", "執行");
			this.put("ケ", "競売等");
			this.put("ナ", "担保実行");
			this.put("財チ", "財産開示");
			this.put("ヲ", "執行雑");
			this.put("企", "担保実行");
			this.put("フ", "破産");
			this.put("再", "再生");
			this.put("再イ", "小規模再");
			this.put("再ロ", "給与等再");
			this.put("ミ", "会社更生");
			this.put("承", "承認援助");
			this.put("船", "責任制限");
			this.put("油", "責任制限");
			this.put("集", "簡易確定");
			this.put("集ワ", "確定異議");
			this.put("ホ", "過料");
			this.put("エ", "共助");
			this.put("仲", "仲裁");
			this.put("モ", "民事雑");
			this.put("人", "人身保護");
			this.put("人モ", "人身保護雑");
			this.put("ワ", "訴訟");
			this.put("ネ", "控訴");
			this.put("ネオ", "上告提起");
			this.put("ネ受", "上告受理");
			this.put("ラ", "抗告");
			this.put("ラク", "特別抗告");
			this.put("ラ許", "許可抗告");
			this.put("ム", "再審");
			this.put("ツ", "上告");
			this.put("ツテ", "特別上告提");
			this.put("ノ", "一般調停");
			this.put("ユ", "宅地等調停");
			this.put("セ", "農事調停");
			this.put("メ", "商事調停");
			this.put("ス", "鉱害調停");
			this.put("交", "交通調停");
			this.put("公", "公害等調停");
			this.put("ウ", "民事雑");
			this.put("人ナ", "人身保護");
			this.put("人ウ", "人身保護雑");
			this.put("オ", "上告");
			this.put("受", "上告受理");
			this.put("テ", "特別上告");
			this.put("ク", "特別抗告");
			this.put("許", "許可抗告");
			this.put("ヤ", "再審");
			this.put("マ", "民事雑");
			this.put("家", "家事審判");
			this.put("家イ", "家事調停");
			this.put("家ホ", "人事訴訟");
			this.put("家ヘ", "家裁訴訟");
			this.put("家ヌ", "子返還");
			this.put("家ニ", "家事抗告提");
			this.put("家ト", "民事控訴");
			this.put("家チ", "民事再審");
			this.put("家リ", "保全命令");
			this.put("家ハ", "家事共助");
			this.put("家ロ", "家事雑");
			this.put("少", "少年保護");
			this.put("少ハ", "準少年保");
			this.put("少イ", "刑事");
			this.put("少ニ", "少年審判");
			this.put("少ロ", "少年審判雑");
			this.put("少ホ", "刑事雑");
			this.put("秩は", "秩序違反");
			this.put("い", "略式");
			this.put("ろ", "公判請求");
			this.put("は", "尋問請求");
			this.put("に", "証拠保全");
			this.put("ほ", "再審請求");
			this.put("へ", "共助");
			this.put("と", "刑事補償");
			this.put("ち", "費用免除");
			this.put("り", "交通即決");
			this.put("ぬ", "費用補償");
			this.put("る", "雑事件");
			this.put("こ", "費用負担");
			this.put("秩い", "秩序違反");
			this.put("わ", "公判請求");
			this.put("か", "尋問請求");
			this.put("よ", "証拠保全");
			this.put("た", "再審");
			this.put("れ", "共助");
			this.put("そ", "刑事補償");
			this.put("つ", "起訴強制");
			this.put("ね", "費用免除");
			this.put("な", "費用補償");
			this.put("む", "雑事件");
			this.put("え", "費用負担");
			this.put("損", "賠償命令");
			this.put("秩ろ", "秩序違反");
			this.put("う", "控訴");
			this.put("の", "第一審");
			this.put("お", "再審");
			this.put("く", "抗告");
			this.put("ら", "抗告受理");
			this.put("や", "費用補償");
			this.put("ま", "刑事補償");
			this.put("け", "決定異議");
			this.put("ふ", "費用免除");
			this.put("て", "雑事件");
			this.put("秩に", "秩序違反");
			this.put("秩ほ", "秩序違反");
			this.put("秩へ", "秩序違反");
			this.put("あ", "上告");
			this.put("さ", "非常上告");
			this.put("き", "再審");
			this.put("ゆ", "上告受理");
			this.put("め", "移送許可");
			this.put("み", "判決訂正");
			this.put("し", "特別抗告");
			this.put("ひ", "費用補償");
			this.put("も", "刑事補償");
			this.put("せ", "費用免除");
			this.put("す", "雑事件");
			this.put("秩と", "秩序違反");
			this.put("秩ち", "秩序違反");
			this.put("収い", "没収取消");
			this.put("収ろ", "没収取消");
			this.put("収は", "没収取消");
			this.put("収に", "一審没収取消");
			this.put("収ほ", "控訴没収取消");
			this.put("収へ", "一審没収取消");
			this.put("収と", "上告没収取消");
			this.put("行ア", "共助");
			this.put("行イ", "雑事件");
			this.put("行ウ", "行訴");
			this.put("行ヌ", "行控訴提起");
			this.put("行エ", "行飛躍提起等");
			this.put("行ネ", "行飛躍受理");
			this.put("行オ", "行再審");
			this.put("行カ", "行抗告提");
			this.put("行キ", "行共助");
			this.put("行ク", "行雑事件");
			this.put("行ケ", "一審行訴");
			this.put("行コ", "行控訴");
			this.put("行サ", "行上告提起");
			this.put("行ノ", "行上告受申");
			this.put("行シ", "行特上告提");
			this.put("行ス", "行抗告");
			this.put("行セ", "行特抗告提");
			this.put("行ハ", "行許抗告提");
			this.put("行ソ", "行再審");
			this.put("行タ", "行雑事件");
			this.put("行チ", "一審行訴");
			this.put("行ツ", "行上告");
			this.put("行ヒ", "行上告受理");
			this.put("行テ", "行特別上告");
			this.put("行ト", "行特別抗告");
			this.put("行フ", "行許可抗告");
			this.put("行ナ", "行再審");
			this.put("行ニ", "行雑事件");
		}
	};

	/** 当事者表記変換マップ */
	public static final Map<String, List<String>> TOJISHA_HYOKI_MAP = new LinkedHashMap<String, List<String>>() {
		private static final long serialVersionUID = 1L;
		{
			this.put(TojishaHyoki.GENKOKU.getCd(), Arrays.asList(TojishaHyoki.HIKOKU.getCd()));
			this.put(TojishaHyoki.MOSHITATENIN.getCd(), Arrays.asList(TojishaHyoki.AITEGATA.getCd()));
			this.put(TojishaHyoki.KOSONIN.getCd(), Arrays.asList(TojishaHyoki.HIKOSONIN.getCd()));
			this.put(TojishaHyoki.JOKOKUNIN.getCd(), Arrays.asList(TojishaHyoki.HIJOKOKUNIN.getCd()));
			this.put(TojishaHyoki.HANSO_GENKOKU.getCd(), Arrays.asList(TojishaHyoki.HANSO_HIKOKU.getCd()));
			this.put(TojishaHyoki.SAIKENSHA.getCd(), Arrays.asList(TojishaHyoki.SAIMUSHA.getCd(), TojishaHyoki.DAISAN_SAIMUSHA.getCd()));
			this.put(TojishaHyoki.KOKOKUNIN.getCd(), Arrays.asList(TojishaHyoki.HIKOKOKUNIN.getCd()));
			this.put(TojishaHyoki.SAISHIN_GENKOKU.getCd(), Arrays.asList(TojishaHyoki.SAISHIN_HIKOKU.getCd()));
			this.put(TojishaHyoki.SAISEI_SAIMUSHA.getCd(), Arrays.asList(""));
			this.put(TojishaHyoki.KOSEI_KAISHA.getCd(), Arrays.asList(""));

			// ※以下、対の関係
			this.put(TojishaHyoki.HIKOKU.getCd(), Arrays.asList(TojishaHyoki.GENKOKU.getCd()));
			this.put(TojishaHyoki.AITEGATA.getCd(), Arrays.asList(TojishaHyoki.MOSHITATENIN.getCd()));
			this.put(TojishaHyoki.HIKOSONIN.getCd(), Arrays.asList(TojishaHyoki.KOSONIN.getCd()));
			this.put(TojishaHyoki.HIJOKOKUNIN.getCd(), Arrays.asList(TojishaHyoki.JOKOKUNIN.getCd()));
			this.put(TojishaHyoki.HANSO_HIKOKU.getCd(), Arrays.asList(TojishaHyoki.HANSO_GENKOKU.getCd()));
			this.put(TojishaHyoki.SAIMUSHA.getCd(), Arrays.asList(TojishaHyoki.SAIKENSHA.getCd()));
			this.put(TojishaHyoki.DAISAN_SAIMUSHA.getCd(), Arrays.asList(TojishaHyoki.SAIKENSHA.getCd()));
			this.put(TojishaHyoki.HIKOKOKUNIN.getCd(), Arrays.asList(TojishaHyoki.KOKOKUNIN.getCd()));
			this.put(TojishaHyoki.SAISHIN_HIKOKU.getCd(), Arrays.asList(TojishaHyoki.SAISHIN_GENKOKU.getCd()));
		}
	};
}
