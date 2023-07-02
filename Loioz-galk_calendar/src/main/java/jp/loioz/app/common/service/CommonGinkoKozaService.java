package jp.loioz.app.common.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.GinkoKozaEditForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.dto.KozaDto;
import jp.loioz.entity.TGinkoKozaEntity;

/**
 * 口座情報関連の共通サービス処理
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonGinkoKozaService extends DefaultService {

	/** 口座情報管理用のDaoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	/**
	 * 銀行口座新規作成用入力フォームを作成
	 * 
	 * @return
	 */
	public GinkoKozaEditForm createGinkoKozaEditForm() {

		GinkoKozaEditForm ginkoKozaEditForm = new GinkoKozaEditForm();
		ginkoKozaEditForm.setKozaDto(new KozaDto());
		ginkoKozaEditForm.setIsNew(true);

		return ginkoKozaEditForm;
	}

	/**
	 * 銀行口座編集用入力フォームを作成
	 * 
	 * @param kozaSeq
	 * @return
	 * @throws AppException
	 */
	public GinkoKozaEditForm createGinkoKozaEditForm(Long kozaSeq) throws AppException {

		TGinkoKozaEntity tGinkoKozaEntity = tGinkoKozaDao.selectByGinkoAccountSeq(kozaSeq);
		if (tGinkoKozaEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		GinkoKozaEditForm ginkoKozaEditForm = new GinkoKozaEditForm();
		KozaDto dto = new KozaDto();

		dto.setGinkoAccountSeq(tGinkoKozaEntity.getGinkoAccountSeq());
		dto.setBranchNo(tGinkoKozaEntity.getBranchNo());
		dto.setLabelName(tGinkoKozaEntity.getLabelName());
		dto.setGinkoName(tGinkoKozaEntity.getGinkoName());
		dto.setShitenName(tGinkoKozaEntity.getShitenName());
		dto.setShitenNo(tGinkoKozaEntity.getShitenNo());
		dto.setKozaType(KozaType.of(tGinkoKozaEntity.getKozaType()));
		dto.setKozaTypeName(DefaultEnum.getVal(KozaType.of(tGinkoKozaEntity.getKozaType())));
		dto.setKozaNo(tGinkoKozaEntity.getKozaNo());
		dto.setKozaName(tGinkoKozaEntity.getKozaName());
		dto.setKozaNameKana(tGinkoKozaEntity.getKozaNameKana());
		dto.setDefaultUse(SystemFlg.codeToBoolean(tGinkoKozaEntity.getDefaultUseFlg()));

		ginkoKozaEditForm.setKozaDto(dto);
		ginkoKozaEditForm.setIsNew(false);
		ginkoKozaEditForm.setIsDeleted(tGinkoKozaEntity.getDeletedAt() != null && tGinkoKozaEntity.getDeletedBy() != null);

		return ginkoKozaEditForm;
	}

	/**
	 * 事務所口座情報の新規登録処理
	 *
	 * @param kozaDto
	 * @throws AppException
	 */
	public void insertOfficeKoza(KozaDto kozaDto) throws AppException {

		// テナントSEQの取得
		Long tenantSeq = SessionUtils.getTenantSeq();

		// 枝番の発行
		long maxBranchNo = tGinkoKozaDao.selectMaxTenantBranchNo(tenantSeq);
		Long branchNo = maxBranchNo + 1;

		TGinkoKozaEntity tGinkoKozaEntity = new TGinkoKozaEntity();
		tGinkoKozaEntity.setTenantSeq(tenantSeq);
		tGinkoKozaEntity.setLabelName(kozaDto.getLabelName());
		tGinkoKozaEntity.setBranchNo(branchNo);
		tGinkoKozaEntity.setGinkoName(kozaDto.getGinkoName());
		tGinkoKozaEntity.setShitenName(kozaDto.getShitenName());
		tGinkoKozaEntity.setShitenNo(kozaDto.getShitenNo());
		tGinkoKozaEntity.setKozaType(DefaultEnum.getCd(kozaDto.getKozaType()));
		tGinkoKozaEntity.setKozaNo(kozaDto.getKozaNo());
		tGinkoKozaEntity.setKozaName(kozaDto.getKozaName());
		tGinkoKozaEntity.setKozaNameKana(kozaDto.getKozaNameKana());

		boolean isDefaultUse = kozaDto.isDefaultUse();
		tGinkoKozaEntity.setDefaultUseFlg(SystemFlg.booleanToCode(isDefaultUse));

		try {
			// 登録処理
			tGinkoKozaDao.insert(tGinkoKozaEntity);

			if (isDefaultUse) {
				// 登録データが既定フラグがONの場合のみ更新処理を実行
				this.updateTenantDefaultUseFlgOff(tenantSeq, tGinkoKozaEntity.getGinkoAccountSeq());
			}

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * アカウント口座情報の新規登録処理
	 *
	 * @param kozaDto
	 * @throws AppException
	 */
	public void insertAccountKoza(KozaDto kozaDto) throws AppException {

		// アカウントSEQの取得
		Long accountSeq = SessionUtils.getLoginAccountSeq();

		// 枝番の発行
		long maxBranchNo = tGinkoKozaDao.selectMaxAccountBranchNo(accountSeq);
		Long branchNo = maxBranchNo + 1;

		TGinkoKozaEntity tGinkoKozaEntity = new TGinkoKozaEntity();
		tGinkoKozaEntity.setAccountSeq(accountSeq);
		tGinkoKozaEntity.setLabelName(kozaDto.getLabelName());
		tGinkoKozaEntity.setBranchNo(branchNo);
		tGinkoKozaEntity.setGinkoName(kozaDto.getGinkoName());
		tGinkoKozaEntity.setShitenName(kozaDto.getShitenName());
		tGinkoKozaEntity.setShitenNo(kozaDto.getShitenNo());
		tGinkoKozaEntity.setKozaType(DefaultEnum.getCd(kozaDto.getKozaType()));
		tGinkoKozaEntity.setKozaNo(kozaDto.getKozaNo());
		tGinkoKozaEntity.setKozaName(kozaDto.getKozaName());
		tGinkoKozaEntity.setKozaNameKana(kozaDto.getKozaNameKana());

		boolean isDefaultUse = kozaDto.isDefaultUse();
		tGinkoKozaEntity.setDefaultUseFlg(SystemFlg.booleanToCode(isDefaultUse));

		try {
			// 登録処理
			tGinkoKozaDao.insert(tGinkoKozaEntity);

			if (isDefaultUse) {
				// 登録データが既定フラグがONの場合のみ更新処理を実行
				this.updateAccountDefaultUseFlgOff(accountSeq, tGinkoKozaEntity.getGinkoAccountSeq());
			}

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 事務所口座・アカウント口座情報の更新処理
	 *
	 * @param kozaDto
	 * @throws AppException
	 */
	public void updateKoza(KozaDto kozaDto) throws AppException {

		TGinkoKozaEntity tGinkoKozaEntity = tGinkoKozaDao.selectByGinkoAccountSeq(kozaDto.getGinkoAccountSeq());

		tGinkoKozaEntity.setLabelName(kozaDto.getLabelName());
		tGinkoKozaEntity.setGinkoName(kozaDto.getGinkoName());
		tGinkoKozaEntity.setShitenName(kozaDto.getShitenName());
		tGinkoKozaEntity.setShitenNo(kozaDto.getShitenNo());
		tGinkoKozaEntity.setKozaType(DefaultEnum.getCd(kozaDto.getKozaType()));
		tGinkoKozaEntity.setKozaNo(kozaDto.getKozaNo());
		tGinkoKozaEntity.setKozaName(kozaDto.getKozaName());
		tGinkoKozaEntity.setKozaNameKana(kozaDto.getKozaNameKana());

		boolean isDefaultUse = kozaDto.isDefaultUse();
		tGinkoKozaEntity.setDefaultUseFlg(SystemFlg.booleanToCode(isDefaultUse));

		try {
			// 更新処理
			tGinkoKozaDao.update(tGinkoKozaEntity);

			if (isDefaultUse && isAccountGinkoKoza(tGinkoKozaEntity)) {
				// 登録データが既定フラグがONの場合 && アカウント口座の場合 -> アカウント口座のフラグOFF更新処理を実行
				this.updateAccountDefaultUseFlgOff(tGinkoKozaEntity.getAccountSeq(), tGinkoKozaEntity.getGinkoAccountSeq());
			}

			if (isDefaultUse && isTenantGinkoKoza(tGinkoKozaEntity)) {
				// 登録データが既定フラグがONの場合 && テナント口座の場合 -> テナント口座のフラグOFF更新処理を実行
				this.updateTenantDefaultUseFlgOff(tGinkoKozaEntity.getTenantSeq(), tGinkoKozaEntity.getGinkoAccountSeq());
			}

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 事務所口座・アカウント口座情報の再開処理
	 *
	 * @param kozaDto
	 * @throws AppException
	 */
	public void restartKoza(KozaDto kozaDto) throws AppException {

		TGinkoKozaEntity tGinkoKozaEntity = tGinkoKozaDao.selectByGinkoAccountSeq(kozaDto.getGinkoAccountSeq());

		tGinkoKozaEntity.setDeletedAt(null);
		tGinkoKozaEntity.setDeletedBy(null);

		try {
			tGinkoKozaDao.update(tGinkoKozaEntity);
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 事務所口座・アカウント口座情報の論理削除処理
	 *
	 * @param ginkoAccountSeq
	 * @throws AppException
	 */
	public void deleteAccountGinkoKoza(Long ginkoAccountSeq) throws AppException {
		
		TGinkoKozaEntity tGinkoKozaEntity = tGinkoKozaDao.selectByGinkoAccountSeq(ginkoAccountSeq);
		if (tGinkoKozaEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 削除フラグを立てる
		tGinkoKozaEntity.setFlgDelete();
		// 既定フラグをoffにする
		tGinkoKozaEntity.setDefaultUseFlg(SystemFlg.FLG_OFF.getCd());
		try {
			tGinkoKozaDao.update(tGinkoKozaEntity);
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 口座情報がアカウントに付属するものかどうか
	 * 
	 * @param entity
	 * @return
	 */
	public boolean isAccountGinkoKoza(TGinkoKozaEntity entity) {
		return entity.getAccountSeq() != null;
	}

	/**
	 * 口座情報がテナントに付属するものかどうか
	 * 
	 * @param entity
	 * @return
	 */
	public boolean isTenantGinkoKoza(TGinkoKozaEntity entity) {
		return entity.getTenantSeq() != null;
	}

	/**
	 * 事務所に紐づく口座追加上限チェック
	 *
	 * @param tenantSeq
	 * @return
	 */
	public boolean isOfficeGinkoKozaAdd(Long tenantSeq) {
		boolean error = false;
		// 登録上限チェック
		List<TGinkoKozaEntity> tGinkoKozaEntityList = tGinkoKozaDao.selectKozaListByTenantSeq(tenantSeq);
		if (tGinkoKozaEntityList.size() >= CommonConstant.GINKO_KOZA_ADD_LIMIT_FOR_WEB) {
			error = true;
		}
		return error;
	}

	/**
	 * アカウントに紐づく口座追加上限チェック
	 *
	 * @param accountSeq
	 * @return
	 */
	public boolean isAccountGinkoKozaAdd(Long accountSeq) {
		boolean error = false;
		// 登録上限チェック
		List<TGinkoKozaEntity> tGinkoKozaEntityList = tGinkoKozaDao.selectKozaListByAccountSeq(accountSeq);
		if (tGinkoKozaEntityList.size() >= CommonConstant.GINKO_KOZA_ADD_LIMIT_FOR_WEB) {
			error = true;
		}
		return error;
	}

	/**
	 * 事務所に紐づく銀行口座の登録有無を判定する
	 *
	 * @param accountSeq アカウント連番
	 * @return 銀行口座登録有無
	 */
	public boolean isRegistOfficeGinkoKoza(Long accountSeq) {
		boolean isRegist = false;
		int countKoza = tGinkoKozaDao.countKozaByTenantSeq(accountSeq);
		// 登録チェック
		if (countKoza > 0) {
			isRegist = true;
		}
		return isRegist;
	}

	/**
	 * アカウントの口座既定使用フラグをOFFにする
	 * 
	 * @param accountSeq アカウントSEQ
	 * @param excludeGinkoKozaSeq 除外対象
	 */
	private void updateAccountDefaultUseFlgOff(Long accountSeq, Long excludeGinkoAccountSeq) throws AppException {

		// アカウントに紐づくデータを取得
		List<TGinkoKozaEntity> tGinkoKozaEntities = tGinkoKozaDao.selectKozaListByAccountSeq(accountSeq);
		// 除外対象を除く、すべての口座情報の既定使用フラグをOFFに設定
		List<TGinkoKozaEntity> updateEntities = tGinkoKozaEntities.stream()
				.filter(e -> !Objects.equals(excludeGinkoAccountSeq, e.getGinkoAccountSeq()))
				.peek(e -> e.setDefaultUseFlg(SystemFlg.FLG_OFF.getCd()))
				.collect(Collectors.toList());

		try {
			// 更新処理
			tGinkoKozaDao.update(updateEntities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * テナントの口座既定使用フラグをOFFにする
	 * 
	 * @param tenantSeq テナントSEQ
	 * @param excludeGinkoKozaSeq 除外対象
	 */
	private void updateTenantDefaultUseFlgOff(Long tenantSeq, Long excludeGinkoAccountSeq) throws AppException {

		// テナントに紐づくデータを取得
		List<TGinkoKozaEntity> tGinkoKozaEntities = tGinkoKozaDao.selectKozaListByTenantSeq(tenantSeq);
		// 除外対象を除く、すべての口座情報の既定使用フラグをOFFに設定
		List<TGinkoKozaEntity> updateEntities = tGinkoKozaEntities.stream()
				.filter(e -> !Objects.equals(excludeGinkoAccountSeq, e.getGinkoAccountSeq()))
				.peek(e -> e.setDefaultUseFlg(SystemFlg.FLG_OFF.getCd()))
				.collect(Collectors.toList());

		try {
			// 更新処理
			tGinkoKozaDao.update(updateEntities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}
}
