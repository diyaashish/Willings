package jp.loioz.app.user.ankenList.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.ankenList.form.AnkenListSearchForm;
import jp.loioz.app.user.ankenList.form.AnkenListSearchForm.AdvisorAnkenListSearchForm;
import jp.loioz.app.user.ankenList.form.AnkenListSearchForm.AllAnkenListSearchForm;
import jp.loioz.app.user.ankenList.form.AnkenListSearchForm.KeijiSaibanListSearchForm;
import jp.loioz.app.user.ankenList.form.AnkenListSearchForm.MinjiSaibanListSearchForm;
import jp.loioz.app.user.ankenList.form.AnkenListSearchForm.MyAnkenListSearchForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenListMenu;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.condition.AnkenListSearchCondition;
import jp.loioz.domain.condition.AnkenListSortCondition;
import jp.loioz.domain.condition.SaibanListSearchCondition;
import jp.loioz.domain.condition.SaibanListSortCondition;

/**
 * 案件一覧画面の共通サービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AnkenListCommonService extends DefaultService {

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * searchForm から案件一覧情報の検索条件を作成します
	 * 
	 * @param searchForm
	 * @return
	 */
	public AnkenListSearchCondition createAnkenSearchCondition(AnkenListSearchForm searchForm) {

		AnkenListSearchCondition ankenListSearchCondition = new AnkenListSearchCondition();
		// 検索条件：案件ステータス
		List<String> ankenStatusList = null;
		// 検索条件：検索ワード
		String searchWord = null;
		// 検索条件：分野
		String bunyaId = null;
		// 検索条件：担当弁護士
		Long tantoLaywer = null;
		// 検索条件：担当事務
		Long tantoJimu = null;
		// 検索条件：案件登録日
		String ankenCreateDateFrom = null;
		String ankenCreateDateTo = null;
		// 検索条件：受任日
		String jyuninDateFrom = null;
		String jyuninDateTo = null;

		// 選択中メニューが「すべての案件」の場合
		if (AnkenListMenu.All_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			AllAnkenListSearchForm allAnkenListSearchForm = searchForm.getAllAnkenListSearchForm();
			if (CommonConstant.ANKEN_STATUS_INCOMPLETE_CD.equals(allAnkenListSearchForm.getAnkenStatus())) {
				// ステータスが「完了、不受任以外」の場合、ステータスをセットしなおす
				ankenStatusList = List.of(
						AnkenStatus.SODAN.getCd(),
						AnkenStatus.SHINKOCHU.getCd(),
						AnkenStatus.SEISAN_MACHI.getCd(),
						AnkenStatus.KANRYO_MACHI.getCd());
			} else if (CommonConstant.ANKEN_STATUS_ALL_CD.equals(allAnkenListSearchForm.getAnkenStatus())) {
				// ステータスが「すべて」の場合、ステータスをセットしなおす
				ankenStatusList = List.of(
						AnkenStatus.SODAN.getCd(),
						AnkenStatus.SHINKOCHU.getCd(),
						AnkenStatus.SEISAN_MACHI.getCd(),
						AnkenStatus.KANRYO_MACHI.getCd(),
						AnkenStatus.KANRYO.getCd(),
						AnkenStatus.FUJUNIN.getCd());
			} else {
				ankenStatusList = List.of(allAnkenListSearchForm.getAnkenStatus());
			}
			searchWord = allAnkenListSearchForm.getSearchWord();
			bunyaId = allAnkenListSearchForm.getBunyaId();
			tantoLaywer = allAnkenListSearchForm.getTantoLaywer();
			tantoJimu = allAnkenListSearchForm.getTantoJimu();
			ankenCreateDateFrom = allAnkenListSearchForm.getAnkenCreateDateFrom();
			ankenCreateDateTo = allAnkenListSearchForm.getAnkenCreateDateTo();
			jyuninDateFrom = allAnkenListSearchForm.getJyuninDateFrom();
			jyuninDateTo = allAnkenListSearchForm.getJyuninDateTo();
		}

		// 選択中メニューが「自分の担当案件」の場合
		if (AnkenListMenu.MY_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			MyAnkenListSearchForm myAnkenListSearchForm = searchForm.getMyAnkenListSearchForm();
			// ステータスが完了、不受任以外の場合、対象ステータスをセットしなおす
			if (CommonConstant.ANKEN_STATUS_INCOMPLETE_CD.equals(myAnkenListSearchForm.getAnkenStatus())) {
				ankenStatusList = List.of(
						AnkenStatus.SODAN.getCd(),
						AnkenStatus.SHINKOCHU.getCd(),
						AnkenStatus.SEISAN_MACHI.getCd(),
						AnkenStatus.KANRYO_MACHI.getCd());
			} else if (CommonConstant.ANKEN_STATUS_ALL_CD.equals(myAnkenListSearchForm.getAnkenStatus())) {
				// ステータスが「すべて」の場合、ステータスをセットしなおす
				ankenStatusList = List.of(
						AnkenStatus.SODAN.getCd(),
						AnkenStatus.SHINKOCHU.getCd(),
						AnkenStatus.SEISAN_MACHI.getCd(),
						AnkenStatus.KANRYO_MACHI.getCd(),
						AnkenStatus.KANRYO.getCd(),
						AnkenStatus.FUJUNIN.getCd());
			} else {
				ankenStatusList = List.of(myAnkenListSearchForm.getAnkenStatus());
			}
			searchWord = myAnkenListSearchForm.getSearchWord();
			bunyaId = myAnkenListSearchForm.getBunyaId();
			tantoLaywer = myAnkenListSearchForm.getTantoLaywer();
			tantoJimu = myAnkenListSearchForm.getTantoJimu();
			ankenCreateDateFrom = myAnkenListSearchForm.getAnkenCreateDateFrom();
			ankenCreateDateTo = myAnkenListSearchForm.getAnkenCreateDateTo();
			jyuninDateFrom = myAnkenListSearchForm.getJyuninDateFrom();
			jyuninDateTo = myAnkenListSearchForm.getJyuninDateTo();
		}

		// 選択中メニューが「顧問案件」の場合
		if (AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			AdvisorAnkenListSearchForm advisorAnkenListSearchForm = searchForm.getAdvisorAnkenListSearchForm();
			// ステータスが完了、不受任以外の場合、対象ステータスをセットしなおす
			if (CommonConstant.ANKEN_STATUS_INCOMPLETE_CD.equals(advisorAnkenListSearchForm.getAnkenStatus())) {
				ankenStatusList = List.of(
						AnkenStatus.SODAN.getCd(),
						AnkenStatus.SHINKOCHU.getCd(),
						AnkenStatus.SEISAN_MACHI.getCd(),
						AnkenStatus.KANRYO_MACHI.getCd());
			} else if (CommonConstant.ANKEN_STATUS_ALL_CD.equals(advisorAnkenListSearchForm.getAnkenStatus())) {
				// ステータスが「すべて」の場合、ステータスをセットしなおす
				ankenStatusList = List.of(
						AnkenStatus.SODAN.getCd(),
						AnkenStatus.SHINKOCHU.getCd(),
						AnkenStatus.SEISAN_MACHI.getCd(),
						AnkenStatus.KANRYO_MACHI.getCd(),
						AnkenStatus.KANRYO.getCd(),
						AnkenStatus.FUJUNIN.getCd());
			} else {
				ankenStatusList = List.of(advisorAnkenListSearchForm.getAnkenStatus());
			}
			searchWord = advisorAnkenListSearchForm.getSearchWord();
			bunyaId = advisorAnkenListSearchForm.getBunyaId();
			tantoLaywer = advisorAnkenListSearchForm.getTantoLaywer();
			tantoJimu = advisorAnkenListSearchForm.getTantoJimu();
			ankenCreateDateFrom = advisorAnkenListSearchForm.getAnkenCreateDateFrom();
			ankenCreateDateTo = advisorAnkenListSearchForm.getAnkenCreateDateTo();
			jyuninDateFrom = advisorAnkenListSearchForm.getJyuninDateFrom();
			jyuninDateTo = advisorAnkenListSearchForm.getJyuninDateTo();
		}

		// 検索条件の案件ステータスがあればセット
		if (!LoiozCollectionUtils.isEmpty(ankenStatusList)) {
			ankenListSearchCondition.setAnkenStatusList(ankenStatusList);
		}
		// 検索条件の検索ワードがあればセット（全角スペース、半角スペースを除去する）
		if (!StringUtils.isEmpty(searchWord)) {
			ankenListSearchCondition.setSearchWord(StringUtils.removeSpaceCharacter(searchWord));
		}
		// 検索条件の分野があればセット
		if (!StringUtils.isEmpty(bunyaId)) {
			ankenListSearchCondition.setBunyaId(bunyaId);
		}
		// 検索条件の担当弁護士があればセット
		if (tantoLaywer != null) {
			ankenListSearchCondition.setTantoLaywer(tantoLaywer);
		}
		// 検索条件の担当事務があればセット
		if (tantoJimu != null) {
			ankenListSearchCondition.setTantoJimu(tantoJimu);
		}
		// 検索条件の案件登録日があればセット
		if (ankenCreateDateFrom != null) {
			ankenListSearchCondition.setAnkenCreateDateFrom(ankenCreateDateFrom);
		}
		if (ankenCreateDateTo != null) {
			ankenListSearchCondition.setAnkenCreateDateTo(ankenCreateDateTo);
		}
		// 検索条件の受任日があればセット
		if (jyuninDateFrom != null) {
			ankenListSearchCondition.setJyuninDateFrom(jyuninDateFrom);
		}
		if (jyuninDateTo != null) {
			ankenListSearchCondition.setJyuninDateTo(jyuninDateTo);
		}
		// 選択中メニューをセット
		ankenListSearchCondition.setSelectedAnkenListMenu(AnkenListMenu.of(searchForm.getSelectedAnkenListMenu()));

		return ankenListSearchCondition;
	}

	/**
	 * searchForm から案件一覧情報のソート条件を作成します
	 * 
	 * @param searchForm
	 * @return
	 */
	public AnkenListSortCondition createAnkenSortCondition(AnkenListSearchForm searchForm) {

		AnkenListSortCondition ankenListSortCondition = new AnkenListSortCondition();

		// 選択中のメニューによって、ソートキーを取得するフォームを変える
		if (AnkenListMenu.All_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// すべての案件
			ankenListSortCondition.setAnkenListSortItem(searchForm.getAllAnkenListSearchForm().getAllAnkenListSortKey());
			ankenListSortCondition.setSortOrder(searchForm.getAllAnkenListSearchForm().getAllAnkenListSortOrder());
		} else if (AnkenListMenu.MY_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 自分の担当案件
			ankenListSortCondition.setAnkenListSortItem(searchForm.getMyAnkenListSearchForm().getMyAnkenListSortKey());
			ankenListSortCondition.setSortOrder(searchForm.getMyAnkenListSearchForm().getMyAnkenListSortOrder());
		} else if (AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 顧問取引先の案件
			ankenListSortCondition.setAnkenListSortItem(searchForm.getAdvisorAnkenListSearchForm().getAdvisorAnkenListSortKey());
			ankenListSortCondition.setSortOrder(searchForm.getAdvisorAnkenListSearchForm().getAdvisorAnkenListSortOrder());
		} else {
			// 対象のリストなし
		}

		return ankenListSortCondition;
	}

	/**
	 * searchForm から裁判一覧情報の検索条件を作成します
	 * 
	 * @param searchForm
	 * @return
	 */
	public SaibanListSearchCondition createSaibanSearchCondition(AnkenListSearchForm searchForm) {

		SaibanListSearchCondition saibanListSearchCondition = new SaibanListSearchCondition();

		// 検索条件：分野
		String bunyaId = null;
		// 検索条件：検索ワード
		String searchWord = null;
		// 検索条件： 裁判手続き
		List<String> saibanStatusList = null;
		// 検索条件：担当弁護士
		Long tantoLaywerId = null;
		// 検索条件：担当事務
		Long tantoJimuId = null;
		// 検索条件：申立日／起訴日From
		String saibanStartDateFrom = null;
		// 検索条件：申立日／起訴日To
		String saibanStartDateTo = null;
		// 検索条件：終了日／判決日From
		String saibanEndDateFrom = null;
		// 検索条件：終了日／判決日To
		String saibanEndDateTo = null;

		// 選択中メニューが「民事裁判」の場合
		if (AnkenListMenu.MINJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			MinjiSaibanListSearchForm minjiSaibanListSearchForm = searchForm.getMinjiSaibanListSearchForm();
			bunyaId = minjiSaibanListSearchForm.getBunyaId();
			searchWord = minjiSaibanListSearchForm.getSearchWord();
			if (CommonConstant.SAIBAN_STATUS_INCOMPLETE_CD.equals(minjiSaibanListSearchForm.getSaibanStatus())) {
				// 裁判手続きが終了以外の場合、裁判ステータスに「準備中」、「進行中」をセットする
				saibanStatusList = List.of(
						SaibanStatus.PREPARING.getCd(),
						SaibanStatus.PROGRESSING.getCd());
			} else if (CommonConstant.SAIBAN_STATUS_ALL_CD.equals(minjiSaibanListSearchForm.getSaibanStatus())) {
				// 裁判手続きがすべての場合、裁判ステータスに「準備中」、「進行中」、「完了」をセットする
				saibanStatusList = List.of(
						SaibanStatus.PREPARING.getCd(),
						SaibanStatus.PROGRESSING.getCd(),
						SaibanStatus.CLOSED.getCd());
			} else {
				saibanStatusList = List.of(minjiSaibanListSearchForm.getSaibanStatus());
			}
			tantoLaywerId = minjiSaibanListSearchForm.getTantoLaywerId();
			tantoJimuId = minjiSaibanListSearchForm.getTantoJimuId();
			saibanStartDateFrom = minjiSaibanListSearchForm.getSaibanStartDateFrom();
			saibanStartDateTo = minjiSaibanListSearchForm.getSaibanStartDateTo();
			saibanEndDateFrom = minjiSaibanListSearchForm.getSaibanEndDateFrom();
			saibanEndDateTo = minjiSaibanListSearchForm.getSaibanEndDateTo();
		}

		// 選択中メニューが「刑事裁判」の場合
		if (AnkenListMenu.KEIJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			KeijiSaibanListSearchForm keijiSaibanListSearchForm = searchForm.getKeijiSaibanListSearchForm();
			bunyaId = keijiSaibanListSearchForm.getBunyaId();
			searchWord = keijiSaibanListSearchForm.getSearchWord();
			if (CommonConstant.SAIBAN_STATUS_INCOMPLETE_CD.equals(keijiSaibanListSearchForm.getSaibanStatus())) {
				// 裁判手続きが終了以外の場合、裁判ステータスに「準備中」、「進行中」をセットする
				saibanStatusList = List.of(
						SaibanStatus.PREPARING.getCd(),
						SaibanStatus.PROGRESSING.getCd());
			} else if (CommonConstant.SAIBAN_STATUS_ALL_CD.equals(keijiSaibanListSearchForm.getSaibanStatus())) {
				// 裁判手続きがすべての場合、裁判ステータスに「準備中」、「進行中」、「完了」をセットする
				saibanStatusList = List.of(
						SaibanStatus.PREPARING.getCd(),
						SaibanStatus.PROGRESSING.getCd(),
						SaibanStatus.CLOSED.getCd());
			} else {
				saibanStatusList = List.of(keijiSaibanListSearchForm.getSaibanStatus());
			}
			tantoLaywerId = keijiSaibanListSearchForm.getTantoLaywerId();
			tantoJimuId = keijiSaibanListSearchForm.getTantoJimuId();
			saibanStartDateFrom = keijiSaibanListSearchForm.getSaibanStartDateFrom();
			saibanStartDateTo = keijiSaibanListSearchForm.getSaibanStartDateTo();
			saibanEndDateFrom = keijiSaibanListSearchForm.getSaibanEndDateFrom();
			saibanEndDateTo = keijiSaibanListSearchForm.getSaibanEndDateTo();
		}

		// 検索条件の分野があればセット
		if (!StringUtils.isEmpty(bunyaId)) {
			saibanListSearchCondition.setBunyaId(bunyaId);
		}
		// 検索条件の検索ワードがあればセット（全角スペース、半角スペースを除去する）
		if (!StringUtils.isEmpty(searchWord)) {
			saibanListSearchCondition.setSearchWord(StringUtils.removeSpaceCharacter(searchWord));
		}
		// 検索条件の裁判手続きがあればセット
		if (!LoiozCollectionUtils.isEmpty(saibanStatusList)) {
			saibanListSearchCondition.setSaibanStatusList(saibanStatusList);
		}
		// 検索条件の担当弁護士があればセット
		if (tantoLaywerId != null) {
			saibanListSearchCondition.setTantoLaywerId(tantoLaywerId);
		}
		// 検索条件の担当事務があればセット
		if (tantoJimuId != null) {
			saibanListSearchCondition.setTantoJimuId(tantoJimuId);
		}
		// 検索条件の申立日／起訴日があればセット
		if (!StringUtils.isEmpty(saibanStartDateFrom)) {
			saibanListSearchCondition.setSaibanStartDateFrom(saibanStartDateFrom);
		}
		if (!StringUtils.isEmpty(saibanStartDateTo)) {
			saibanListSearchCondition.setSaibanStartDateTo(saibanStartDateTo);
		}
		// 検索条件の終了日／判決日があればセット
		if (!StringUtils.isEmpty(saibanEndDateFrom)) {
			saibanListSearchCondition.setSaibanEndDateFrom(saibanEndDateFrom);
		}
		if (!StringUtils.isEmpty(saibanEndDateTo)) {
			saibanListSearchCondition.setSaibanEndDateTo(saibanEndDateTo);
		}
		// 選択中メニューをセット
		saibanListSearchCondition.setSelectedAnkenListMenu(AnkenListMenu.of(searchForm.getSelectedAnkenListMenu()));

		return saibanListSearchCondition;
	}

	/**
	 * searchForm から裁判一覧情報のソート条件を作成します
	 * 
	 * @param searchForm
	 * @return
	 */
	public SaibanListSortCondition createSaibanSortCondition(AnkenListSearchForm searchForm) {

		SaibanListSortCondition saibanListSortCondition = new SaibanListSortCondition();

		// 選択中のメニューによって、ソートキーを取得するフォームを変える
		if (AnkenListMenu.MINJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			saibanListSortCondition.setSaibanListSortItem(searchForm.getMinjiSaibanListSearchForm().getMinjiSaibanListSortKey());
			saibanListSortCondition.setSortOrder(searchForm.getMinjiSaibanListSearchForm().getMinjiSaibanListSortOrder());
		} else {
			saibanListSortCondition.setSaibanListSortItem(searchForm.getKeijiSaibanListSearchForm().getKeijiSaibanListSortKey());
			saibanListSortCondition.setSortOrder(searchForm.getKeijiSaibanListSearchForm().getKeijiSaibanListSortOrder());
		}
		return saibanListSortCondition;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}