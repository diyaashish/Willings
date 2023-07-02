package jp.loioz.app.user.saibanManagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.saibanManagement.form.SaibanTsuikisoEditInputForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.entity.TSaibanJikenEntity;

/**
 * 裁判管理(刑事弁護)画面：追起訴モーダルのサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanTsuikisoEditService extends DefaultService {

	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	/**
	 * 画面表示情報の作成
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 画面表示情報
	 */
	public SaibanTsuikisoEditInputForm createViewForm(Long saibanSeq) {

		SaibanTsuikisoEditInputForm inputForm = new SaibanTsuikisoEditInputForm();

		inputForm.setSaibanSeq(saibanSeq);
		inputForm.setJikenYear(DateUtils.getYearToJa());

		return inputForm;
	}

	/**
	 * 表示データの取得
	 *
	 * @param inputForm
	 * @return 画面表示情報
	 */
	public SaibanTsuikisoEditInputForm setData(SaibanTsuikisoEditInputForm inputForm) {

		// 更新元データの取得
		TSaibanJikenEntity tSaibanJikenEntity = tSaibanJikenDao.selectByJikenSeq(inputForm.getJikenSeq());

		// Entity ⇒ Dto
		inputForm.setJikenSeq(tSaibanJikenEntity.getJikenSeq());
		inputForm.setJikenGengo(tSaibanJikenEntity.getJikenGengo());
		inputForm.setJikenYear(tSaibanJikenEntity.getJikenYear());
		inputForm.setJikenMark(tSaibanJikenEntity.getJikenMark());
		inputForm.setJikenNo(tSaibanJikenEntity.getJikenNo());
		inputForm.setJikenName(tSaibanJikenEntity.getJikenName());

		// Formにセット
		inputForm.setSaibanSeq(tSaibanJikenEntity.getSaibanSeq());

		return inputForm;
	}

	/**
	 * 登録処理
	 *
	 * @param inputForm
	 */
	public void regist(SaibanTsuikisoEditInputForm inputForm) {

		// エンティティの作成
		TSaibanJikenEntity registEntity = new TSaibanJikenEntity();

		// 登録するデータの作成
		registEntity.setSaibanSeq(inputForm.getSaibanSeq());
		registEntity.setJikenGengo(inputForm.getJikenGengo());
		registEntity.setJikenYear(inputForm.getJikenYear());
		registEntity.setJikenMark(inputForm.getJikenMark());
		registEntity.setJikenNo(inputForm.getJikenNo());
		registEntity.setJikenName(inputForm.getJikenName());

		// 登録処理
		tSaibanJikenDao.insert(registEntity);
	}

	/**
	 * 更新処理
	 *
	 * @param inputForm
	 */
	public void update(SaibanTsuikisoEditInputForm inputForm) {

		// 更新されるデータの取得
		TSaibanJikenEntity updateEntity = tSaibanJikenDao.selectByJikenSeq(inputForm.getJikenSeq());

		// 更新するデータを作成
		updateEntity.setJikenGengo(inputForm.getJikenGengo());
		updateEntity.setJikenYear(inputForm.getJikenYear());
		updateEntity.setJikenMark(inputForm.getJikenMark());
		updateEntity.setJikenNo(inputForm.getJikenNo());
		updateEntity.setJikenName(inputForm.getJikenName());

		// 更新処理
		tSaibanJikenDao.update(updateEntity);
	}

	/**
	 * 削除処理
	 *
	 * @param inputForm
	 */
	public void delete(SaibanTsuikisoEditInputForm inputForm) {

		// 削除データの取得
		TSaibanJikenEntity deleteEntity = tSaibanJikenDao.selectByJikenSeq(inputForm.getJikenSeq());

		// 削除処理
		tSaibanJikenDao.delete(deleteEntity);
	}

	/**
	 * 追起訴可能上限の検証
	 * 
	 * @param saibanSeq
	 * @return 検証結果
	 */
	public boolean isJikenAddLimitOverValid(Long saibanSeq) {

		if (saibanSeq == null) {
			return true;
		}

		List<TSaibanJikenEntity> tSaibanJikenEntities = tSaibanJikenDao.selectBySaibanSeq(saibanSeq);
		if (tSaibanJikenEntities.size() >= CommonConstant.JIKEN_INFO_ADD_LIMIT) {
			return false;
		}

		return true;
	}

}
