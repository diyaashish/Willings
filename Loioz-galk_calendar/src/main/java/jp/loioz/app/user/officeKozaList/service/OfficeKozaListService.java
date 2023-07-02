package jp.loioz.app.user.officeKozaList.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.officeKozaList.form.OfficeKozaListViewForm;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.dto.KozaDto;
import jp.loioz.entity.TGinkoKozaEntity;

/**
 * 事務所口座の設定画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OfficeKozaListService extends DefaultService {

	/** 口座情報管理用のDaoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @return form
	 * @throws AppException
	 */
	public OfficeKozaListViewForm createViewForm() throws AppException {

		// 事務所連番を取得
		Long tenantSeq = SessionUtils.getTenantSeq();

		// 事務所口座情報の設定
		List<TGinkoKozaEntity> tGinkoKozaEntityList = tGinkoKozaDao.selectKozaListByTenantSeq(tenantSeq);
		List<KozaDto> kozaDtoList = tGinkoKozaEntityList.stream().map(this::convertEntitiy2Dto).collect(Collectors.toList());

		OfficeKozaListViewForm viewForm = new OfficeKozaListViewForm();
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