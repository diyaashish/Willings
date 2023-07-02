package jp.loioz.app.user.saibanManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.saibanManagement.form.SaibanJikenEditInputForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.entity.TSaibanJikenEntity;

/**
 * 裁判管理画面：事件情報の編集サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanJikenEditService extends DefaultService {

	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	@Autowired
	private Logger logger;

	/**
	 * 画面表示情報の作成
	 * 
	 * @param jikenSeq
	 * @return
	 */
	public SaibanJikenEditInputForm createViewForm(Long jikenSeq) {

		SaibanJikenEditInputForm inputForm = new SaibanJikenEditInputForm();
		TSaibanJikenEntity tSaibanJikenEntity = tSaibanJikenDao.selectByJikenSeq(jikenSeq);

		inputForm.setJikenSeq(tSaibanJikenEntity.getJikenSeq());
		inputForm.setJikenGengo(tSaibanJikenEntity.getJikenGengo());
		inputForm.setJikenYear(tSaibanJikenEntity.getJikenYear());
		inputForm.setJikenMark(tSaibanJikenEntity.getJikenMark());
		inputForm.setJikenNo(tSaibanJikenEntity.getJikenNo());
		inputForm.setJikenName(tSaibanJikenEntity.getJikenName());

		return inputForm;
	}

	/**
	 * 裁判事件情報の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateSaibanJiken(SaibanJikenEditInputForm inputForm) throws AppException {

		TSaibanJikenEntity tSaibanJikenEntity = tSaibanJikenDao.selectByJikenSeq(inputForm.getJikenSeq());
		if (tSaibanJikenEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		tSaibanJikenEntity.setJikenGengo(inputForm.getJikenGengo());
		tSaibanJikenEntity.setJikenYear(inputForm.getJikenYear());
		tSaibanJikenEntity.setJikenMark(inputForm.getJikenMark());
		tSaibanJikenEntity.setJikenNo(inputForm.getJikenNo());
		tSaibanJikenEntity.setJikenName(inputForm.getJikenName());

		try {
			// 更新処理
			tSaibanJikenDao.update(tSaibanJikenEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

}
