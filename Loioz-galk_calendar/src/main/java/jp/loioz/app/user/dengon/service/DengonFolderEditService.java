package jp.loioz.app.user.dengon.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.dengon.form.DengonFolderEditForm;
import jp.loioz.app.user.dengon.form.DengonMoveForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TrashBoxFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TDengonAccountStatusDao;
import jp.loioz.dao.TDengonDao;
import jp.loioz.dao.TDengonFolderDao;
import jp.loioz.dao.TDengonFolderInDao;
import jp.loioz.dto.DengonFolderDto;
import jp.loioz.entity.TDengonAccountStatusEntity;
import jp.loioz.entity.TDengonEntity;
import jp.loioz.entity.TDengonFolderEntity;
import jp.loioz.entity.TDengonFolderInEntity;

/**
 * カスタムフォルダー編集画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DengonFolderEditService extends DefaultService {

	/* メッセージステータス管理用のDaoクラス */
	@Autowired
	private TDengonAccountStatusDao tDengonAccountStatusDao;

	/* メッセージ用のDaoクラス */
	@Autowired
	private TDengonDao tDengonDao;

	/* カスタムフォルダー管理用のDaoクラス */
	@Autowired
	private TDengonFolderDao tDengonFolderDao;

	/* カスタムフォルダー中身管理用のDaoクラス */
	@Autowired
	private TDengonFolderInDao tDengonFolderInDao;

	/* ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期登録画面表示
	 *
	 * @return new FolderEditForm()
	 */
	public DengonFolderEditForm createViewForm() {

		DengonFolderEditForm form = new DengonFolderEditForm();

		// セレクトボックスの表示要素を取得する
		List<DengonFolderDto> folderSelectList = tDengonFolderDao.selectCustomeFolderAsSelectOptions(SessionUtils.getLoginAccountSeq(), true, false);
		form.setFolderSelectList(folderSelectList);

		// セレクトボックスの表示範囲を設定する
		form.setCreatableParentFolder(this.creatableParentFolderCount());
		form.setCreatableSubFolder(this.creatableSubFolderCount());

		return form;
	}

	/**
	 * 編集画面表示
	 *
	 * @param dengonFolderSeq
	 * @return 遷移先の画面
	 */
	public DengonFolderEditForm selectFolderEdit(Long dengonFolderSeq) {

		DengonFolderEditForm form = this.createViewForm();

		/* dengonFolderSeqがnullの場合新規登録画面を表示する */
		if (dengonFolderSeq == null) {
			return form;
		}

		// 選択したフォルダ情報を取得
		DengonFolderDto dto = tDengonFolderDao.selectFolderEdit(SessionUtils.getLoginAccountSeq(), dengonFolderSeq);
		form.setDto(dto);

		// 選択したフォルダSEQを除外する
		List<DengonFolderDto> dengonFolderDtoList = form.getFolderSelectList();

		// 除外後のフォルダリスト
		List<DengonFolderDto> viewDengonFolderDtoList = dengonFolderDtoList.stream()
				.filter(entity -> !entity.getDengonFolderSeq().equals(dengonFolderSeq)).collect(Collectors.toList());
		form.setFolderSelectList(viewDengonFolderDtoList);

		// セレクトボックスの表示範囲を設定する
		form.setCreatableParentFolder(form.isCreatableParentFolder() || Objects.isNull(dto.getParentDengonFolderSeq()));
		form.setCreatableSubFolder(form.isCreatableSubFolder() || !Objects.isNull(dto.getParentDengonFolderSeq()));

		return form;
	}

	/**
	 * 新規登録処理
	 *
	 * @param form
	 * @throws AppException
	 */
	public Long regist(DengonFolderEditForm form) throws AppException {

		/* 画面情報の取得 */
		DengonFolderDto dto = form.getDto();

		/* カスタムフォルダー情報の設定 */
		TDengonFolderEntity entity = this.convertTDengonFolderEntity(dto);

		/* 登録件数 */
		int insertEntityCount = 0;

		try {
			/* 登録処理 */
			insertEntityCount = tDengonFolderDao.insert(entity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00012, ex);
		}

		if (insertEntityCount != 1) {
			// 登録処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		return entity.getDengonFolderSeq();
	}

	/**
	 * カスタムフォルダーをごみ箱に入れる
	 *
	 * @param dengonFolderSeq
	 * @throws AppException
	 */
	public void trashedFolder(Long dengonFolderSeq) throws AppException {

		/* DBに登録されているカスタムフォルダー情報の取得 */
		TDengonFolderEntity entity = tDengonFolderDao.selectBySeq(dengonFolderSeq);

		/* ごみ箱フラグの変更 */
		entity.setTrashedFlg(SystemFlg.FLG_ON.getCd());

		/* 親フォルダSEQの削除 */
		entity.setParentDengonFolderSeq(null);

		/* 更新件数 */
		int updateEntityCount = 0;
		int[] updateEntitysCount = null;

		try {
			/* カスタムフォルダの更新処理 */
			updateEntityCount = tDengonFolderDao.update(entity);

			/* カスタムフォルダ内のメッセージのごみ箱フラグの更新処理 */
			List<TDengonAccountStatusEntity> tDengonAccountStatusEntityList = tDengonAccountStatusDao
					.selectListByDengonFolderSeq(SessionUtils.getLoginAccountSeq(), dengonFolderSeq);

			if (tDengonAccountStatusEntityList != null && tDengonAccountStatusEntityList.size() > 0) {

				/* ごみ箱フラグを変更する */
				for (TDengonAccountStatusEntity tDengonAccountStatusEntity : tDengonAccountStatusEntityList) {
					tDengonAccountStatusEntity.setTrashedFlg("1");
				}

				/* 更新処理 */
				updateEntitysCount = tDengonAccountStatusDao.update(tDengonAccountStatusEntityList);

				if (updateEntitysCount == null || Arrays.stream(updateEntitysCount).sum() != updateEntitysCount.length) {
					// 更新処理に失敗した場合

					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);

					throw new AppException(MessageEnum.MSG_E00014, null);
				}
			}

			// サブフォルダが存在する場合にそのサブフォルダもごみ箱に送る
			List<TDengonFolderEntity> childFolderList = tDengonFolderDao.selectChildFolderListBySeq(dengonFolderSeq);
			if (childFolderList != null && !childFolderList.isEmpty()) {
				this.trashedMultiFolder(childFolderList);
			}

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (updateEntityCount != 1) {
			// 更新処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 複数のカスタムフォルダーをごみ箱に入れる
	 *
	 * @param dengonFolderSeq
	 * @throws AppException
	 */
	public void trashedMultiFolder(List<TDengonFolderEntity> entitys) throws AppException {

		/* ごみ箱フラグの変更 */
		entitys.stream().forEach(e -> e.setTrashedFlg(SystemFlg.FLG_ON.getCd()));

		/* 親フォルダSEQの削除 */
		entitys.stream().forEach(e -> e.setParentDengonFolderSeq(null));

		/* 更新件数 */
		int[] updateEntitysCount = null;

		try {
			/* カスタムフォルダの更新処理 */
			updateEntitysCount = tDengonFolderDao.update(entitys);

			if (updateEntitysCount == null || Arrays.stream(updateEntitysCount).sum() != updateEntitysCount.length) {
				// 更新処理に失敗した場合

				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);

				throw new AppException(MessageEnum.MSG_E00014, null);
			}

			/* カスタムフォルダ内のメッセージのごみ箱フラグの更新処理 */
			List<Long> dengonFolderSeqList = entitys.stream().map(TDengonFolderEntity::getDengonFolderSeq).collect(Collectors.toList());
			List<TDengonAccountStatusEntity> tDengonAccountStatusEntityList = tDengonAccountStatusDao
					.selectListByDengonFolderSeqList(SessionUtils.getLoginAccountSeq(), dengonFolderSeqList);

			if (tDengonAccountStatusEntityList != null && tDengonAccountStatusEntityList.size() > 0) {

				/* ごみ箱フラグを変更する */
				for (TDengonAccountStatusEntity tDengonAccountStatusEntity : tDengonAccountStatusEntityList) {
					tDengonAccountStatusEntity.setTrashedFlg(SystemFlg.FLG_ON.getCd());
				}

				/* 更新処理 */
				updateEntitysCount = tDengonAccountStatusDao.update(tDengonAccountStatusEntityList);

				if (updateEntitysCount == null || Arrays.stream(updateEntitysCount).sum() != updateEntitysCount.length) {
					// 更新処理に失敗した場合

					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);

					throw new AppException(MessageEnum.MSG_E00014, null);
				}
			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	/**
	 * カスタムフォルダーをごみ箱から取り出す
	 *
	 * @param dengonFolderSeq
	 * @throws AppException
	 */
	public void removeFolder(Long dengonFolderSeq) throws AppException {

		/* DBに登録されているカスタムフォルダー情報の取得 */
		TDengonFolderEntity tDengonFolderEntity = tDengonFolderDao.selectBySeq(dengonFolderSeq);

		/* ごみ箱フラグの変更 */
		tDengonFolderEntity.setTrashedFlg(SystemFlg.FLG_OFF.getCd());

		/* 更新件数 */
		int updateEntityCount = 0;

		try {
			/* 更新処理 */
			updateEntityCount = tDengonFolderDao.update(tDengonFolderEntity);

			/* カスタムフォルダ内のメッセージのごみ箱フラグの更新処理 */
			List<TDengonAccountStatusEntity> tDengonAccountStatusEntityList = tDengonAccountStatusDao
					.selectListByDengonFolderSeq(SessionUtils.getLoginAccountSeq(), dengonFolderSeq);

			if (tDengonAccountStatusEntityList != null && tDengonAccountStatusEntityList.size() > 0) {

				/* ごみ箱フラグを変更する */
				for (TDengonAccountStatusEntity tDengonAccountStatusEntity : tDengonAccountStatusEntityList) {
					tDengonAccountStatusEntity.setTrashedFlg(SystemFlg.FLG_OFF.getCd());
				}

				/* 更新処理 */
				int[] updateEntitysCount = tDengonAccountStatusDao.update(tDengonAccountStatusEntityList);

				if (updateEntitysCount == null || Arrays.stream(updateEntitysCount).sum() != updateEntitysCount.length) {
					// 更新処理に失敗した場合

					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

					throw new AppException(MessageEnum.MSG_E00013, null);
				}
			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (updateEntityCount != 1) {
			// 更新処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 更新処理
	 *
	 * @param form
	 * @throws AppException
	 */
	public void update(DengonFolderEditForm form) throws AppException {

		/* 画面情報の取得 */
		DengonFolderDto dto = form.getDto();

		/* DBに登録されているカスタムフォルダー情報の取得 */
		Long currentDengonFolderSeq = form.getCurrentDengonFolderSeq();
		// フォルダを取得
		TDengonFolderEntity entity = tDengonFolderDao.selectBySeq(currentDengonFolderSeq);

		if (entity == null) {
			// データが存在しない
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + CommonConstant.SPACE + ALREADY_NO_DATA);
			throw new DataNotFoundException("データ取得に失敗 : " + currentDengonFolderSeq, null);

		}
		// 入力した情報を設定
		entity.setDengonFolderName(dto.getDengonFolderName());
		entity.setParentDengonFolderSeq(dto.getParentDengonFolderSeq());

		// 更新用
		List<TDengonFolderEntity> updateEntityList = new ArrayList<TDengonFolderEntity>();
		updateEntityList.add(entity);

		// 子フォルダを取得
		List<TDengonFolderEntity> childFolderEntityList = tDengonFolderDao.selectChildFolderListBySeq(currentDengonFolderSeq);
		if (dto.getParentDengonFolderSeq() != null) {
			if (LoiozCollectionUtils.isNotEmpty(childFolderEntityList)) {
				// 子フォルダの親SEQを更新
				childFolderEntityList.forEach(e -> e.setParentDengonFolderSeq(dto.getParentDengonFolderSeq()));
				updateEntityList.addAll(childFolderEntityList);
			}
		}

		try {

			/* 更新処理 */
			tDengonFolderDao.update(updateEntityList);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	/**
	 * 削除処理
	 *
	 * @param dengonFolderSeq
	 * @throws AppException
	 */
	public void delete(Long dengonFolderSeq) throws AppException {

		/* DBに登録されているカスタムフォルダー情報の取得 */
		TDengonFolderEntity entity = tDengonFolderDao.selectBySeq(dengonFolderSeq);

		/* カスタムフォルダーに登録されているカスタムフォルダー中身情報の取得 */
		List<TDengonFolderInEntity> entitys = tDengonFolderInDao.selectListByDengonFolderSeq(dengonFolderSeq);

		/* 排他エラーチェック */
		if (entity == null || entitys == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 中身情報の総数を数える */
		int entitysCount = entity != null ? entitys.size() : 0;

		/* 削除件数 */
		int deleteEntityCount = 0;
		int[] deleteEntitysCount = null;

		try {
			/* 論理削除処理 */
			entity.setFlgDelete();
			deleteEntityCount = tDengonFolderDao.update(entity);

			/* 物理削除処理 */
			if (entitysCount != 0) {
				deleteEntitysCount = tDengonFolderInDao.delete(entitys);
			}

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}

		if (deleteEntityCount != 1 || entitysCount != 0 && (Arrays.stream(deleteEntitysCount).sum() != deleteEntitysCount.length)) {
			// 削除処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * フォルダ数上限の判定関数
	 *
	 * @return 判定結果
	 */
	public boolean creatableParentFolderCount() {
		return tDengonFolderDao.countFolder(SessionUtils.getLoginAccountSeq(), true) < CommonConstant.DENGON_FOLDER_ADD_LIMIT;
	}

	/**
	 * サブフォルダ数上限の判定関数
	 *
	 * @return 判定結果
	 */
	public boolean creatableSubFolderCount() {
		return tDengonFolderDao.countFolder(SessionUtils.getLoginAccountSeq(), false) < CommonConstant.DENGON_FOLDER_ADD_LIMIT;
	}

	/**
	 * フォルダ移動モーダル初期表示データの取得
	 *
	 * @return
	 */
	public DengonMoveForm createDengonMoveForm(Long dengonSeq) {
		// viewForm初期化
		DengonMoveForm viewForm = new DengonMoveForm();
		if (dengonSeq != null) {
			TDengonEntity tDengonEntity = tDengonDao.selectByDengonSeq(dengonSeq);
			if (tDengonEntity.getSendAccountSeq().equals(SessionUtils.getLoginAccountSeq()) && TrashBoxFlg.IS_TRASH.getCd().equals(tDengonEntity.getSendTrashedFlg())) {
				viewForm.setSendMailFlg(true);
			} else {
				List<DengonFolderDto> dengonFolderDtoList = tDengonFolderDao.selectCustomeFolderAsSelectOptions(SessionUtils.getLoginAccountSeq(), null, false);
				viewForm.setCustomeFolderList(dengonFolderDtoList);
				viewForm.setSendMailFlg(false);
			}
		} else {
			List<DengonFolderDto> dengonFolderDtoList = tDengonFolderDao.selectCustomeFolderAsSelectOptions(SessionUtils.getLoginAccountSeq(), null, false);
			viewForm.setCustomeFolderList(dengonFolderDtoList);
			viewForm.setSendMailFlg(false);
		}
		viewForm.setDengonSeq(dengonSeq);
		return viewForm;
	}

	/**
	 * メッセージをカスタムフォルダに移動させる
	 *
	 * @param dengonSeq
	 * @param dengonFolderSeq
	 * @throws AppException
	 */
	public void moveFolder(String dengonSeq, Long dengonFolderSeq) throws AppException {

		/* dengonSeqをカンマ区切りで分割しLong型に変換する */
		List<Long> dengonSeqList = Arrays.asList(dengonSeq.split(",")).stream()
				.map(Long::parseLong).collect(Collectors.toList());

		if (dengonFolderSeq == null) {
			/* dengonFolderSeqがnullの場合は登録情報のみ削除し、受信BOXに移動させる */
			this.deleteDengonList(dengonSeqList);

			// ごみ箱フラグを変更する
			List<TDengonAccountStatusEntity> tDengonAccountStatusEntityList = tDengonAccountStatusDao
					.selectListByKey(SessionUtils.getLoginAccountSeq(), dengonSeqList);
			tDengonAccountStatusEntityList.stream().forEach(e -> e.setTrashedFlg(SystemFlg.FLG_OFF.getCd()));

			int updateEntitysCount[] = tDengonAccountStatusDao.update(tDengonAccountStatusEntityList);

			if (updateEntitysCount == null || Arrays.stream(updateEntitysCount).sum() != updateEntitysCount.length) {
				// 更新処理に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

				throw new AppException(MessageEnum.MSG_E00013, null);
			}
		} else {

			/* 両方ともnullでないならカスタムフォルダ中身情報を登録する */
			this.saveFolderIn(dengonSeqList, dengonFolderSeq);

		}

	}

	/**
	 * カスタムフォルダ中身情報を登録する
	 *
	 * @param dengonSeq
	 * @param dengonFolderSeq
	 * @throws AppException
	 */
	public void saveFolderIn(List<Long> dengonSeqList, Long dengonFolderSeq) throws AppException {

		/* カスタムフォルダ情報を取得する */
		TDengonFolderEntity tDengonFolderEntity = tDengonFolderDao.selectBySeq(dengonFolderSeq);

		/* 排他エラーチェック */
		if (tDengonFolderEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 既に登録済みの情報があれば削除する */
		this.deleteDengonList(dengonSeqList);

		/* カスタムフォルダ中身情報を作成する */
		List<TDengonFolderInEntity> tDengonFolderInEntityList = this.convertTDengonFolderInEntityList(dengonSeqList, dengonFolderSeq);

		/* 登録件数 */
		int[] insertEntitysCount = null;

		try {
			/* 登録処理 */
			insertEntitysCount = tDengonFolderInDao.insert(tDengonFolderInEntityList);

			// 移動先のカスタムフォルダのごみ箱フラグに合わせて、メッセージのごみ箱フラグを設定する
			List<TDengonAccountStatusEntity> tDengonAccountStatusEntityList = tDengonAccountStatusDao
					.selectListByKey(SessionUtils.getLoginAccountSeq(), dengonSeqList);
			tDengonAccountStatusEntityList.stream().forEach(e -> e.setTrashedFlg(tDengonFolderEntity.getTrashedFlg()));

			int[] updateEntitysCount = tDengonAccountStatusDao.update(tDengonAccountStatusEntityList);
			if (updateEntitysCount == null || Arrays.stream(updateEntitysCount).sum() != updateEntitysCount.length) {
				// 更新処理に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

				throw new AppException(MessageEnum.MSG_E00013, null);
			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00012, ex);
		}

		if (insertEntitysCount == null || Arrays.stream(insertEntitysCount).sum() != insertEntitysCount.length) {
			// 登録処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * ごみ箱内の送信メッセージを送信BOXへ移動する
	 *
	 * @param sendDengonSeqList
	 * @throws AppException
	 */
	public void moveSendBox(String sendDengonSeqList) throws AppException {

		/* 文字列型のSEQリストをList<Long>に変換する */
		List<Long> dengonSeqList = StringUtils.toListLong(sendDengonSeqList);

		/* DBに登録されているメッセージアカウントステータス情報の取得 */
		List<TDengonEntity> entitys = tDengonDao.selectListByDengonSeqList(dengonSeqList);

		// ごみ箱から送信BOXに変更
		entitys.stream().forEach(e -> e.setSendTrashedFlg(SystemFlg.FLG_OFF.getCd()));

		// メッセージを更新
		tDengonDao.update(entitys);

	}

	/**
	 * カスタムフォルダ中身情報を削除する
	 *
	 * @param dengonSeq
	 * @throws AppException
	 */
	public void deleteDengonList(List<Long> dengonSeqList) throws AppException {

		/* ログインアカウントSEQの取得 */
		Long accountSeq = SessionUtils.getLoginAccountSeq();

		/* DBに登録されているカスタムフォルダー中身情報の取得 */
		List<TDengonFolderInEntity> tDengonFolderInEntityList = tDengonFolderInDao.selectListByDengonSeqList(dengonSeqList, accountSeq);

		/* 情報が存在しなければ処理を終える */
		if (tDengonFolderInEntityList == null) {
			return;
		}

		/* 削除件数 */
		int[] deleteEntitysCount = null;

		try {
			/* 物理削除処理 */
			deleteEntitysCount = tDengonFolderInDao.delete(tDengonFolderInEntityList);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}

		if (deleteEntitysCount == null || Arrays.stream(deleteEntitysCount).sum() != deleteEntitysCount.length) {
			// 削除処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 新規登録時の Dto -> Entity の変換処理
	 *
	 * @param dto
	 * @return entity
	 */
	private TDengonFolderEntity convertTDengonFolderEntity(DengonFolderDto dto) {

		TDengonFolderEntity entity = new TDengonFolderEntity();
		entity.setAccountSeq(SessionUtils.getLoginAccountSeq());
		entity.setDengonFolderName(dto.getDengonFolderName());
		entity.setParentDengonFolderSeq(dto.getParentDengonFolderSeq());
		entity.setTrashedFlg(SystemFlg.FLG_OFF.getCd());

		return entity;
	}

	/**
	 * 入力情報をカスタムフォルダ中身情報リストに変換する
	 *
	 * @param dengonSeqList
	 * @param dengonFolderSeq
	 * @return tDengonFolderInEntityList
	 */
	private List<TDengonFolderInEntity> convertTDengonFolderInEntityList(List<Long> dengonSeqList, Long dengonFolderSeq) {

		List<TDengonFolderInEntity> tDengonFolderInEntityList = new ArrayList<TDengonFolderInEntity>();
		for (Long dengonSeq : dengonSeqList) {
			TDengonFolderInEntity tDengonFolderInEntity = new TDengonFolderInEntity();
			tDengonFolderInEntity.setDengonSeq(dengonSeq);
			tDengonFolderInEntity.setDengonFolderSeq(dengonFolderSeq);

			tDengonFolderInEntityList.add(tDengonFolderInEntity);
		}

		return tDengonFolderInEntityList;
	}

}