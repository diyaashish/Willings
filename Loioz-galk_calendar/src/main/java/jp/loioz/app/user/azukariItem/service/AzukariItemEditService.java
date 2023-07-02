package jp.loioz.app.user.azukariItem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.validation.accessDB.CommonAnkenValidator;
import jp.loioz.app.user.azukariItem.form.AzukariItemEditForm;
import jp.loioz.common.constant.CommonConstant.AzukariItemStatus;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenAzukariItemDao;
import jp.loioz.dao.TCustomerCommonDao;
import jp.loioz.dto.AzukariItemEditDto;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import jp.loioz.entity.TAnkenAzukariItemEntity;

/**
 * 預り品編集画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AzukariItemEditService extends DefaultService {

	/** 共通アカウントサービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 案件のDB整合性バリデータークラス */
	@Autowired
	private CommonAnkenValidator commonAnkenValidator;
	
	/** 預り品用のDaoクラス */
	@Autowired
	private TAnkenAzukariItemDao tAnkenAzukariItemDao;

	/** 顧客井共通のDaoクラス */
	@Autowired
	private TCustomerCommonDao tCustomerCommonDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期データ設定
	 *
	 * @param ankenId
	 * @param ankenItemSeq
	 * @return 画面表示情報
	 * @throws AppException
	 */
	public AzukariItemEditForm setInitData(Long ankenId, Long ankenItemSeq) throws AppException {

		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		AzukariItemEditForm form = new AzukariItemEditForm();

		/* 案件に紐づく顧客、関与者一覧を取得する */
		List<CustomerKanyoshaPulldownDto> customerList = tCustomerCommonDao.selectCustomerByAnkenId(ankenId);
		List<CustomerKanyoshaPulldownDto> kanyoshaList = tCustomerCommonDao.selectKanyoshaPulldownByAnkenId(ankenId);
		form.setCustomerList(customerList);
		form.setKanyoshaList(kanyoshaList);
		if (ankenItemSeq == null) {
			// 新規作成時は、返却日を現在日に設定する
			AzukariItemEditDto azukariItemEditDto = new AzukariItemEditDto();
			azukariItemEditDto.setAzukariDate(DateUtils.parseToString(LocalDate.now(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			form.setAzukariItemDto(azukariItemEditDto);
			return form;
		}

		// DBからankenItemSeqに対応する登録情報を呼び出す
		TAnkenAzukariItemEntity tAnkenAzukariItemEntity = tAnkenAzukariItemDao.selectBySeq(ankenItemSeq);

		// 排他エラーチェック
		if (tAnkenAzukariItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// Entity -> Dto
		AzukariItemEditDto azukariItemEditDto = this.convert2Dto(tAnkenAzukariItemEntity, accountNameMap);
		form.setAzukariItemDto(azukariItemEditDto);

		return form;
	}

	/**
	 * 新規登録処理
	 *
	 * @param form
	 * @throws AppException
	 */
	public void save(AzukariItemEditForm form) throws AppException {

		/* 画面情報の取得 */
		AzukariItemEditDto dto = form.getAzukariItemDto();

		// 関与者SEQの妥当性チェック
		MessageEnum errorMsgEnum = this.validatedKanyosha(form.getAnkenId(), dto.getAzukariFromKanyoshaSeq(), dto.getReturnToKanyoshaSeq());
		if (errorMsgEnum != null) {
			throw new AppException(errorMsgEnum, null);
		}
		
		/* DtoからEntityに変換する */
		TAnkenAzukariItemEntity tAnkenAzukariItemEntity = this.convertTAnkenAzukariItemEntity(dto);

		/* 案件IDを設定 */
		tAnkenAzukariItemEntity.setAnkenId(form.getAnkenId());

		try {
			/* 登録処理 */
			tAnkenAzukariItemDao.insert(tAnkenAzukariItemEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00012, ex);
		}
	}

	/**
	 * 更新処理
	 *
	 * @param form
	 * @throws AppException
	 */
	public void update(AzukariItemEditForm form) throws AppException {

		/* 画面情報の取得 */
		AzukariItemEditDto dto = form.getAzukariItemDto();

		// 関与者SEQの妥当性チェック
		MessageEnum errorMsgEnum = this.validatedKanyosha(form.getAnkenId(), dto.getAzukariFromKanyoshaSeq(), dto.getReturnToKanyoshaSeq());
		if (errorMsgEnum != null) {
			throw new AppException(errorMsgEnum, null);
		}
		
		/* DBに登録されている口座情報の取得 */
		TAnkenAzukariItemEntity tAnkenAzukariItemEntity = tAnkenAzukariItemDao.selectBySeq(dto.getAnkenItemSeq());

		/* 排他エラーチェック */
		if (tAnkenAzukariItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* DtoからEntityに変換する */
		this.setTAnkenAzukariItemEntity(tAnkenAzukariItemEntity, dto);

		try {
			/* 更新処理 */
			tAnkenAzukariItemDao.update(tAnkenAzukariItemEntity);

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
	 * @param form
	 * @throws AppException
	 */
	public void delete(AzukariItemEditForm form) throws AppException {

		/* 画面情報の取得 */
		AzukariItemEditDto dto = form.getAzukariItemDto();

		/* DBに登録されている口座情報の取得 */
		TAnkenAzukariItemEntity tAnkenAzukariItemEntity = tAnkenAzukariItemDao.selectBySeq(dto.getAnkenItemSeq());

		/* 排他エラーチェック */
		if (tAnkenAzukariItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			/* 物理削除処理 */
			tAnkenAzukariItemDao.delete(tAnkenAzukariItemEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * entity -> dto
	 * 
	 * @param entity
	 * @param accountNameMap
	 * @return
	 */
	private AzukariItemEditDto convert2Dto(TAnkenAzukariItemEntity entity, Map<Long, String> accountNameMap) {
		AzukariItemEditDto azukariItemEditDto = new AzukariItemEditDto();

		azukariItemEditDto.setAnkenItemSeq(entity.getAnkenItemSeq());
		azukariItemEditDto.setAzukariStatus(entity.getAzukariStatus());
		azukariItemEditDto.setHinmoku(entity.getHinmoku());
		azukariItemEditDto.setAzukariCount(entity.getAzukariCount());
		azukariItemEditDto.setHokanPlace(entity.getHokanPlace());
		azukariItemEditDto.setAzukariDate(DateUtils.parseToString(entity.getAzukariDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		azukariItemEditDto.setAzukariFromType(entity.getAzukariFromType());
		azukariItemEditDto.setAzukariFromCustomerId(entity.getAzukariFromCustomerId());
		azukariItemEditDto.setAzukariFromKanyoshaSeq(entity.getAzukariFromKanyoshaSeq());
		azukariItemEditDto.setAzukariFrom(entity.getAzukariFrom());
		azukariItemEditDto.setReturnLimitDate(DateUtils.parseToString(entity.getReturnLimitDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		azukariItemEditDto.setReturnDate(DateUtils.parseToString(entity.getReturnDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		azukariItemEditDto.setReturnTo(entity.getReturnTo());
		azukariItemEditDto.setReturnToType(entity.getReturnToType());
		azukariItemEditDto.setReturnToCustomerId(entity.getReturnToCustomerId());
		azukariItemEditDto.setReturnToKanyoshaSeq(entity.getReturnToKanyoshaSeq());
		azukariItemEditDto.setRemarks(entity.getRemarks());
		azukariItemEditDto.setVersionNo(entity.getVersionNo());
		azukariItemEditDto.setCreatedAt(DateUtils.parseToString(entity.getCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		azukariItemEditDto.setCreatedByName(accountNameMap.get(entity.getCreatedBy()));
		azukariItemEditDto.setLastEditAt(DateUtils.parseToString(entity.getLastEditAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		azukariItemEditDto.setLastEditByName(accountNameMap.get(entity.getLastEditBy()));

		return azukariItemEditDto;
	}

	/**
	 * Dto -> Entity の変換処理
	 *
	 * @param azukariItemEditDto
	 * @return tAnkenAzukariItemEntity
	 */
	private TAnkenAzukariItemEntity convertTAnkenAzukariItemEntity(AzukariItemEditDto azukariItemEditDto) {
		TAnkenAzukariItemEntity tAnkenAzukariItemEntity = new TAnkenAzukariItemEntity();
		tAnkenAzukariItemEntity.setHinmoku(azukariItemEditDto.getHinmoku());
		tAnkenAzukariItemEntity.setHokanPlace(azukariItemEditDto.getHokanPlace());
		tAnkenAzukariItemEntity.setRemarks(azukariItemEditDto.getRemarks());
		tAnkenAzukariItemEntity.setAzukariFromType(azukariItemEditDto.getAzukariFromType());
		if (TargetType.CUSTOMER.equalsByCode(azukariItemEditDto.getAzukariFromType())) {
			tAnkenAzukariItemEntity.setAzukariFromCustomerId(azukariItemEditDto.getAzukariFromCustomerId());
			tAnkenAzukariItemEntity.setAzukariFromKanyoshaSeq(null);
			tAnkenAzukariItemEntity.setAzukariFrom(null);
		} else if (TargetType.KANYOSHA.equalsByCode(azukariItemEditDto.getAzukariFromType())) {
			tAnkenAzukariItemEntity.setAzukariFromCustomerId(null);
			tAnkenAzukariItemEntity.setAzukariFromKanyoshaSeq(azukariItemEditDto.getAzukariFromKanyoshaSeq());
			tAnkenAzukariItemEntity.setAzukariFrom(null);
		} else {
			tAnkenAzukariItemEntity.setAzukariFromCustomerId(null);
			tAnkenAzukariItemEntity.setAzukariFromKanyoshaSeq(null);
			tAnkenAzukariItemEntity.setAzukariFrom(azukariItemEditDto.getAzukariFrom());
		}
		tAnkenAzukariItemEntity.setReturnToType(azukariItemEditDto.getReturnToType());
		if (TargetType.CUSTOMER.equalsByCode(azukariItemEditDto.getReturnToType())) {
			tAnkenAzukariItemEntity.setReturnToCustomerId(azukariItemEditDto.getReturnToCustomerId());
			tAnkenAzukariItemEntity.setReturnToKanyoshaSeq(null);
			tAnkenAzukariItemEntity.setReturnTo(null);
		} else if (TargetType.KANYOSHA.equalsByCode(azukariItemEditDto.getReturnToType())) {
			tAnkenAzukariItemEntity.setReturnToCustomerId(null);
			tAnkenAzukariItemEntity.setReturnToKanyoshaSeq(azukariItemEditDto.getReturnToKanyoshaSeq());
			tAnkenAzukariItemEntity.setReturnTo(null);
		} else {
			tAnkenAzukariItemEntity.setReturnToCustomerId(null);
			tAnkenAzukariItemEntity.setReturnToKanyoshaSeq(null);
			tAnkenAzukariItemEntity.setReturnTo(azukariItemEditDto.getReturnTo());
		}
		/* 数量変換 */
		if (StringUtils.isNotEmpty(azukariItemEditDto.getAzukariCount())) {
			tAnkenAzukariItemEntity.setAzukariCount(azukariItemEditDto.getAzukariCount());
		}

		/* 時間変換処理 */
		tAnkenAzukariItemEntity.setAzukariDate(DateUtils.parseToLocalDate(azukariItemEditDto.getAzukariDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenAzukariItemEntity.setReturnLimitDate(DateUtils.parseToLocalDate(azukariItemEditDto.getReturnLimitDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenAzukariItemEntity.setReturnDate(DateUtils.parseToLocalDate(azukariItemEditDto.getReturnDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		/* 入力情報をもとにステータスを設定する */
		tAnkenAzukariItemEntity.setAzukariStatus(this.getStatusCd(azukariItemEditDto));

		return tAnkenAzukariItemEntity;
	}

	/**
	 * 画面入力情報をEntityに設定する。
	 *
	 * @param dto
	 * @param entity
	 */
	public void setTAnkenAzukariItemEntity(TAnkenAzukariItemEntity entity, AzukariItemEditDto azukariItemEditDto) {

		entity.setHinmoku(azukariItemEditDto.getHinmoku());
		entity.setHokanPlace(azukariItemEditDto.getHokanPlace());
		entity.setAzukariFromType(azukariItemEditDto.getAzukariFromType());
		if (TargetType.CUSTOMER.equalsByCode(azukariItemEditDto.getAzukariFromType())) {
			entity.setAzukariFromCustomerId(azukariItemEditDto.getAzukariFromCustomerId());
			entity.setAzukariFromKanyoshaSeq(null);
			entity.setAzukariFrom(null);
		} else if (TargetType.KANYOSHA.equalsByCode(azukariItemEditDto.getAzukariFromType())) {
			entity.setAzukariFromCustomerId(null);
			entity.setAzukariFromKanyoshaSeq(azukariItemEditDto.getAzukariFromKanyoshaSeq());
			entity.setAzukariFrom(null);
		} else {
			entity.setAzukariFromCustomerId(null);
			entity.setAzukariFromKanyoshaSeq(null);
			entity.setAzukariFrom(azukariItemEditDto.getAzukariFrom());
		}

		entity.setReturnToType(azukariItemEditDto.getReturnToType());
		if (TargetType.CUSTOMER.getCd().equals(azukariItemEditDto.getReturnToType())) {
			entity.setReturnToCustomerId(azukariItemEditDto.getReturnToCustomerId());
			entity.setReturnToKanyoshaSeq(null);
			entity.setReturnTo(null);
		} else if (TargetType.KANYOSHA.getCd().equals(azukariItemEditDto.getReturnToType())) {
			entity.setReturnToCustomerId(null);
			entity.setReturnToKanyoshaSeq(azukariItemEditDto.getReturnToKanyoshaSeq());
			entity.setReturnTo(null);
		} else {
			entity.setReturnToCustomerId(null);
			entity.setReturnToKanyoshaSeq(null);
			entity.setReturnTo(azukariItemEditDto.getReturnTo());
		}
		entity.setRemarks(azukariItemEditDto.getRemarks());
		entity.setLastEditAt(LocalDateTime.now());
		Long accountSeq = SessionUtils.getLoginAccountSeq();
		entity.setLastEditBy(accountSeq);
		entity.setAzukariCount(azukariItemEditDto.getAzukariCount());

		/* 時間変換処理 */
		entity.setAzukariDate(DateUtils.parseToLocalDate(azukariItemEditDto.getAzukariDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		entity.setReturnLimitDate(DateUtils.parseToLocalDate(azukariItemEditDto.getReturnLimitDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		entity.setReturnDate(DateUtils.parseToLocalDate(azukariItemEditDto.getReturnDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		/* 入力情報をもとにステータスを設定する */
		entity.setAzukariStatus(this.getStatusCd(azukariItemEditDto));
	}

	/**
	 * ステータスコードの取得処理
	 *
	 * @param azukariItemEditDto
	 * @return ステータスコード
	 */
	private String getStatusCd(AzukariItemEditDto azukariItemEditDto) {
		AzukariItemStatus status;

		if (StringUtils.isNotEmpty(azukariItemEditDto.getReturnDate())) {
			status = AzukariItemStatus.HENKYAKU;
		} else if (StringUtils.isNotEmpty(azukariItemEditDto.getAzukariDate())) {
			status = AzukariItemStatus.HOKAN;
		} else {
			status = AzukariItemStatus.SHUSHU;
		}

		return status.getCd();
	}
	
	/**
	 * 預り元、返却先の関与者SEQの値の妥当性の検証する
	 * 
	 * @param ankenId
	 * @param azukariFromKanyoshaSeq
	 * @param returnToKanyoshaSeq
	 * @return 検証NGの場合はエラーメッセージ情報を返却。検証OKの場合はNULLを返却。
	 */
	private MessageEnum validatedKanyosha(Long ankenId, Long azukariFromKanyoshaSeq, Long returnToKanyoshaSeq) {
		
		if (ankenId == null) {
			throw new IllegalArgumentException("案件IDが指定されていません。");
		}
		
		// 預り元関与者の妥当性チェック
		if (azukariFromKanyoshaSeq != null) {
			// NULLではない場合は値の存在チェック
			boolean  isValid = commonAnkenValidator.isValidRelatingKanyosha(ankenId, azukariFromKanyoshaSeq);
			if (!isValid) {
				// 楽観ロックエラー
				return MessageEnum.MSG_E00025;
			}
		}
		
		// 返却先関与者の妥当性チェック
		if (returnToKanyoshaSeq != null) {
			// NULLではない場合は値の存在チェック
			boolean  isValid = commonAnkenValidator.isValidRelatingKanyosha(ankenId, returnToKanyoshaSeq);
			if (!isValid) {
				// 楽観ロックエラー
				return MessageEnum.MSG_E00025;
			}
		}
		
		return null;
	}
}