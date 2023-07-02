package jp.loioz.app.user.info.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.TInfoReadHistoryDao;
import jp.loioz.entity.TInfoReadHistoryEntity;

/**
 * お知らせ既読履歴情報用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InfoReadHistoryService extends DefaultService {

	/* お知らせ既読履歴情報用のDaoクラス */
	@Autowired
	private TInfoReadHistoryDao tInfoReadHistoryDao;

	/* ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * お知らせ履歴登録
	 *
	 * @param accountSeq
	 * @param infoMgtSeq
	 */
	public void save(Long accountSeq, Long infoMgtSeq) throws AppException {

		try {

			// 既読履歴取得
			int selectCount = tInfoReadHistoryDao.selectRegistCount(accountSeq, infoMgtSeq);

			if (selectCount == 0) {
				// 既読履歴登録
				TInfoReadHistoryEntity entity = new TInfoReadHistoryEntity();
				entity.setAccountSeq(accountSeq);
				entity.setInfoMgtSeq(infoMgtSeq);
				int insertEntityCount = tInfoReadHistoryDao.insert(entity);

				if (insertEntityCount != 1) {
					// 登録処理に失敗した場合
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
				}
			}

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
		}

	}

}