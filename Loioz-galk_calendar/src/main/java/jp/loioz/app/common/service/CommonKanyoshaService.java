package jp.loioz.app.common.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.validation.accessDB.CommonKanyoshaValidator;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.TAnkenAzukariItemDao;
import jp.loioz.dao.TAnkenRelatedKanyoshaDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TSaibanRelatedKanyoshaDao;
import jp.loioz.entity.TAnkenAzukariItemEntity;
import jp.loioz.entity.TAnkenRelatedKanyoshaEntity;
import jp.loioz.entity.TKanyoshaEntity;
import jp.loioz.entity.TSaibanRelatedKanyoshaEntity;
import lombok.NonNull;

/**
 * 関与者情報の共通サービス処理
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonKanyoshaService extends DefaultService {

	/** 案件-関与者の関連情報Dao */
	@Autowired
	private TAnkenRelatedKanyoshaDao tAnkenRelatedKanyoshaDao;

	/** 裁判-関与者の関連情報Dao */
	@Autowired
	private TSaibanRelatedKanyoshaDao tSaibanRelatedKanyoshaDao;

	/** 案件-預かり品情報Daoクラス */
	@Autowired
	private TAnkenAzukariItemDao tAnkenAzukariItemDao;

	/** 関与者一覧用のDaoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 共通関与者バリデートクラス */
	@Autowired
	private CommonKanyoshaValidator commonKanyoshaValidator;

	/** ロガー */
	@Autowired
	private Logger logger;

	/**
	 * 案件-関与者関係者に引数の関与者SEQを持つデータが存在するか
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	public boolean existsRelatedAnken(@NonNull Long kanyoshaSeq) {
		List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByKanyoshaSeq(kanyoshaSeq);
		if (!CollectionUtils.isEmpty(tAnkenRelatedKanyoshaEntities)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 裁判-関与者関係者に引数の関与者SEQを持つデータが存在するか
	 *
	 * @param kanyoshaSeq
	 * @return
	 */
	public boolean existsRelatedSaiban(@NonNull Long kanyoshaSeq) {
		List<TSaibanRelatedKanyoshaEntity> tSaibanRelatedKanyoshaEntities = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaByKanyoshaSeq(kanyoshaSeq);
		if (!CollectionUtils.isEmpty(tSaibanRelatedKanyoshaEntities)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 預かり品の返却先に該当関与者が設定されたデータが存在するかどうか
	 *
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	public boolean existsRelatedAzukariItem(@NonNull Long kanyoshaSeq) {
		List<TAnkenAzukariItemEntity> tAnkenAzukariItemEntities = tAnkenAzukariItemDao.selectReturnToKanyoshaByKanyoshaSeq(kanyoshaSeq);
		if (!CollectionUtils.isEmpty(tAnkenAzukariItemEntities)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 関与者共通の削除前チェック処理
	 * 
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public Map<String, Object> deleteCommonKanyoshaBeforeCheck(Long kanyoshaSeq) {

		Map<String, Object> response = new HashMap<>();

		// 各種存在チェック
		boolean existsAnkenRelateInfo = this.existsRelatedAnken(kanyoshaSeq);
		boolean existsSaibanRelateInfo = this.existsRelatedSaiban(kanyoshaSeq);
		boolean existsRelateAzukariItemInfo = this.existsRelatedAzukariItem(kanyoshaSeq);

		if (existsAnkenRelateInfo || existsSaibanRelateInfo || existsRelateAzukariItemInfo) {
			StringBuilder sb = new StringBuilder("");
			sb.append("選択した関与者は以下の画面で利用されています。削除してもよろしいですか？");
			// どの画面で使用されているかを表示する
			if (existsAnkenRelateInfo) {
				sb.append(CommonConstant.CRLF_CODE + "・案件情報画面");
			}
			if (existsSaibanRelateInfo) {
				sb.append(CommonConstant.CRLF_CODE + "・裁判管理画面");
			}
			if (existsRelateAzukariItemInfo) {
				sb.append(CommonConstant.CRLF_CODE + "・預り品画面");
			}
			// 作成した確認用メッセージを表示
			response.put("message", sb.toString());
			response.put("needConfirm", true);
		} else {
			response.put("needConfirm", false);
		}

		return response;
	}

	/**
	 * 関与者共通の削除処理
	 * 
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public void deleteCommonKanyoshaInfo(Long kanyoshaSeq) throws AppException {

		if (!commonKanyoshaValidator.canDeleteKanyosha(kanyoshaSeq)) {
			// 削除不可データが存在するが削除処理を実行したケース
			// DB整合性チェックを呼び出し元で行うことを想定しているので、ここではエラーとする
			throw new RuntimeException("削除処理がDB整合性に影響するケース");
		}
	
		// 関与者情報の取得
		TKanyoshaEntity tKanyoshaEntity = tKanyoshaDao.selectKanyoshaByKanyoshaSeq(kanyoshaSeq);
		if (tKanyoshaEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	
		// 案件IDを取得
		Long ankenId = tKanyoshaEntity.getAnkenId();
	
		try {
			// 関与者情報の削除
			tKanyoshaDao.delete(tKanyoshaEntity);
	
			// 削除後にソート番号を治す
			List<TKanyoshaEntity> tKanyoshaEntities = tKanyoshaDao.selectKanyoshaListByAnkenId(ankenId);
			// DispOrderでソート
			Collections.sort(tKanyoshaEntities, Comparator.comparing(TKanyoshaEntity::getDispOrder));
			long counta = 1L;
			for (TKanyoshaEntity entity : tKanyoshaEntities) {
				entity.setDispOrder(counta);
				counta++;
			}
			tKanyoshaDao.batchUpdate(tKanyoshaEntities);
	
			// 案件-関与者関係者の更新
			this.deleteAnkenRelatedKanyoshaInfo(kanyoshaSeq, ankenId);
	
			// 裁判-関与者関係者の更新
			this.deleteSaibanRelatedKanyoshaInfo(kanyoshaSeq, ankenId);
	
			// 預かり品の更新
			List<TAnkenAzukariItemEntity> tAnkenAzukariItemEntities = tAnkenAzukariItemDao.selectReturnToKanyoshaByKanyoshaSeq(kanyoshaSeq);
			for (TAnkenAzukariItemEntity entity : tAnkenAzukariItemEntities) {
				entity.setReturnToKanyoshaSeq(null);
				entity.setReturnToType(TargetType.CUSTOMER.getCd());// デフォルト値を設定
			}
			tAnkenAzukariItemDao.update(tAnkenAzukariItemEntities);
	
		} catch (OptimisticLockingFailureException e) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 案件-関与者関係者の削除処理
	 *
	 * @param kanyoshaSeq
	 * @param ankenId
	 */
	private void deleteAnkenRelatedKanyoshaInfo(Long kanyoshaSeq, Long ankenId) {

		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kanyoshaSeq);
		if (tAnkenRelatedKanyoshaEntity == null) {
			// 案件-関与者関係者のデータがないのでなにもしない
			return;
		}

		if (SystemFlg.codeToBoolean(tAnkenRelatedKanyoshaEntity.getDairiFlg())) {
			// 代理人の場合

			// 該当の関与者を代理人として設定しているデータを取得
			List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectKanyoshaDairininByParams(ankenId, tAnkenRelatedKanyoshaEntity.getKanyoshaSeq());
			for (TAnkenRelatedKanyoshaEntity entity : tAnkenRelatedKanyoshaEntities) {
				entity.setRelatedKanyoshaSeq(null);
			}

			// データの削除
			tAnkenRelatedKanyoshaDao.delete(tAnkenRelatedKanyoshaEntity);

			// 紐付いていたデータを更新
			tAnkenRelatedKanyoshaDao.update(tAnkenRelatedKanyoshaEntities);

		} else {
			// 相手方、共犯者、被害者の場合

			if (tAnkenRelatedKanyoshaEntity.getRelatedKanyoshaSeq() == null) {
				// 該当の関与者に代理人がを設定されていない場合は、削除処理のみ行い終了
				tAnkenRelatedKanyoshaDao.delete(tAnkenRelatedKanyoshaEntity);
				return;
			}

			// 該当の関与者に設定されている代理人が、他の関与者にも設定されているか確認する
			List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectKanyoshaDairininByParams(ankenId, tAnkenRelatedKanyoshaEntity.getRelatedKanyoshaSeq());

			// 取得したデータは、今回削除する親データも含まれているので、そのデータは除外する
			List<TAnkenRelatedKanyoshaEntity> removedDeleteEntityRelatedEntities = tAnkenRelatedKanyoshaEntities.stream()
					.filter(e -> !Objects.equals(e.getKanyoshaSeq(), tAnkenRelatedKanyoshaEntity.getKanyoshaSeq()))
					.collect(Collectors.toList());

			// データの件数が０件の場合 -> 今回の削除によって、役割がなくなるので代理人の関与者関係者データも削除
			if (removedDeleteEntityRelatedEntities.size() == 0) {
				TAnkenRelatedKanyoshaEntity deleteDairninEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, tAnkenRelatedKanyoshaEntity.getRelatedKanyoshaSeq());

				// 該当データと紐付いていた代理人のデータを削除
				tAnkenRelatedKanyoshaDao.delete(tAnkenRelatedKanyoshaEntity);
				tAnkenRelatedKanyoshaDao.delete(deleteDairninEntity);

			} else {
				// 該当データのみ削除
				tAnkenRelatedKanyoshaDao.delete(tAnkenRelatedKanyoshaEntity);
			}
		}
	}

	/**
	 * 裁判-関与者関係者の削除処理
	 * 
	 * @param ankenId
	 * @param kanyoshaSeq
	 */
	private void deleteSaibanRelatedKanyoshaInfo(Long kanyoshaSeq, Long ankenId) {

		// 案件に紐づく裁判-関与者関係者情報を取得する
		List<TSaibanRelatedKanyoshaEntity> tSaibanRelatedKanyoshaEntities = tSaibanRelatedKanyoshaDao.selectByAnkenId(ankenId);

		// 案件には複数の裁判が紐づくことが可能なので、裁判SEQをキーとしたマップを作成
		Map<Long, List<TSaibanRelatedKanyoshaEntity>> saibanSeqToSaibanRelatedKanyoshaMap = tSaibanRelatedKanyoshaEntities.stream().collect(Collectors.groupingBy(TSaibanRelatedKanyoshaEntity::getSaibanSeq));

		// 裁判ごとにデータ処理を実行する
		for (Map.Entry<Long, List<TSaibanRelatedKanyoshaEntity>> entry : saibanSeqToSaibanRelatedKanyoshaMap.entrySet()) {

			if (entry.getValue().stream().noneMatch(e -> Objects.equals(kanyoshaSeq, e.getKanyoshaSeq()))) {
				// 裁判-関与者に該当の関与者が存在しない場合は、何もせずcontinue
				continue;
			}

			// 裁判SEQをキーとしたデータ内の関与者ID取得なので、取得データは一意になる想定(該当関与者が存在しないケースはcontinueされているのでnullのケースもない)
			TSaibanRelatedKanyoshaEntity deleteSaibanRelatedEntity = entry.getValue().stream().filter(e -> Objects.equals(kanyoshaSeq, e.getKanyoshaSeq())).findFirst().get();

			if (SystemFlg.codeToBoolean(deleteSaibanRelatedEntity.getDairiFlg())) {
				// 代理人の場合

				// 該当の関与者を代理人として設定しているデータを取得、紐付け関与者はnullに設定
				List<TSaibanRelatedKanyoshaEntity> updateSaibanRelatedEntities = entry.getValue().stream()
						.filter(e -> Objects.equals(deleteSaibanRelatedEntity.getKanyoshaSeq(), e.getRelatedKanyoshaSeq()))
						.peek(e -> e.setRelatedKanyoshaSeq(null))
						.collect(Collectors.toList());

				// 削除処理と更新処理
				tSaibanRelatedKanyoshaDao.delete(deleteSaibanRelatedEntity);
				tSaibanRelatedKanyoshaDao.update(updateSaibanRelatedEntities);

			} else {
				// 相手方、共犯者、その他当事者の場合

				// 該当の関与者の代理人が代理人として登録しているデータを取得
				List<TSaibanRelatedKanyoshaEntity> relatedEntities = entry.getValue().stream()
						.filter(e -> {
							return Objects.equals(deleteSaibanRelatedEntity.getRelatedKanyoshaSeq(), e.getRelatedKanyoshaSeq())
									&& !Objects.equals(deleteSaibanRelatedEntity.getKanyoshaSeq(), e.getKanyoshaSeq());
						}).collect(Collectors.toList());

				if (relatedEntities.size() == 0) {

					// 代理人として登録している関与者データを取得
					Optional<TSaibanRelatedKanyoshaEntity> deleteDairninEntityOptional = entry.getValue().stream().filter(e -> Objects.equals(deleteSaibanRelatedEntity.getRelatedKanyoshaSeq(), e.getKanyoshaSeq())).findFirst();

					// 該当データと紐付いていた代理人のデータを削除
					tSaibanRelatedKanyoshaDao.delete(deleteSaibanRelatedEntity);
					deleteDairninEntityOptional.ifPresent(tSaibanRelatedKanyoshaDao::delete);
				} else {
					// 削除処理
					tSaibanRelatedKanyoshaDao.delete(deleteSaibanRelatedEntity);
				}
			}
		}
	}
}
