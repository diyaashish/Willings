package jp.loioz.app.user.myAccountKozaList.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.myAccountKozaList.form.MyAccountKozaListForm;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.dto.KozaDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TGinkoKozaEntity;

/**
 * 個人口座の設定画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MyAccountKozaListService extends DefaultService {

	/** アカウント情報Dao */
	@Autowired
	private MAccountDao mAccountDao;

	/** 口座情報管理用のDaoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @return form
	 * @throws AppException
	 */
	public MyAccountKozaListForm createViewForm() throws AppException {

		Long accountSeq = SessionUtils.getLoginAccountSeq();
		MyAccountKozaListForm viewForm = new MyAccountKozaListForm();

		// 有効なアカウント情報の取得
		MAccountEntity mAccountEntity = mAccountDao.selectEnabledAccountByAccountSeq(accountSeq);
		if (mAccountEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// ***********************************
		// 口座情報を設定する
		// ***********************************
		List<TGinkoKozaEntity> tGinkoKozaEntities = tGinkoKozaDao.selectKozaListByAccountSeq(accountSeq);
		List<KozaDto> kozaDtoList = tGinkoKozaEntities.stream().map(this::convertEntitiy2Dto).collect(Collectors.toList());

		viewForm.setKozaDtoList(kozaDtoList);
		return viewForm;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 口座情報 Entity -> Dto
	 *
	 * @param entity
	 * @return
	 */
	private KozaDto convertEntitiy2Dto(TGinkoKozaEntity entity) {

		KozaDto dto = new KozaDto();
		dto.setGinkoAccountSeq(entity.getGinkoAccountSeq());
		dto.setBranchNo(entity.getBranchNo());
		dto.setLabelName(entity.getLabelName());
		dto.setGinkoName(entity.getGinkoName());
		dto.setShitenName(entity.getShitenName());
		dto.setShitenNo(entity.getShitenNo());
		dto.setKozaType(KozaType.of(entity.getKozaType()));
		dto.setKozaTypeName(DefaultEnum.getVal(KozaType.of(entity.getKozaType())));
		dto.setKozaNo(entity.getKozaNo());
		dto.setKozaName(entity.getKozaName());
		dto.setKozaNameKana(entity.getKozaNameKana());
		dto.setDefaultUse(SystemFlg.codeToBoolean(entity.getDefaultUseFlg()));
		dto.setDeletedFlg(entity.getDeletedAt() != null && entity.getDeletedBy() != null);

		return dto;
	}

}