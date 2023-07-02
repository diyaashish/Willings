package jp.loioz.app.user.officeAccountSetting.service;

import java.util.List;
import java.util.stream.Collectors;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonPlanService;
import jp.loioz.app.user.officeAccountSetting.form.OfficeAccountSearchForm;
import jp.loioz.app.user.officeAccountSetting.form.OfficeAccountSettingForm;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountStatus;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AccountListDto;
import jp.loioz.entity.MAccountEntity;

/**
 * アカウント管理画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = AppException.class)
public class OfficeAccountListService extends DefaultService {

	/** アカウント情報管理用のDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 契約プランの共通サービス */
	@Autowired
	private CommonPlanService commonPlanService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 一覧情報の取得
	 */
	public OfficeAccountSettingForm search(OfficeAccountSettingForm viewForm, OfficeAccountSearchForm searchForm) {

		// 一覧情報の取得
		Page<AccountListDto> page = this.getPageList(searchForm);

		// viewにセット
		viewForm.setCount(page.getTotalElements());
		viewForm.setPage(page);
		viewForm.setAccountList(page.getContent());

		// ライセンス数の設定
		Long useableLicenseCount = commonPlanService.getNowUseableLicenseCount(SessionUtils.getTenantSeq());
		Long usingLicenseCount = commonPlanService.getNowUsingLicenseCount();
		viewForm.setUseableLicenseCount(useableLicenseCount);
		viewForm.setUsingLicenseCount(usingLicenseCount);

		return viewForm;
	}

	/**
	 * アカウント情報の取得
	 *
	 * @param searchForm
	 * @return
	 */
	public Page<AccountListDto> getPageList(OfficeAccountSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// アカウント情報を取得
		List<MAccountEntity> mAccountEntity = mAccountDao.selectConditions(searchForm, options);

		// Entity -> Dto
		List<AccountListDto> accountList = mAccountEntity.stream()
				.map(entity -> this.convertEntity2Dto(entity))
				.collect(Collectors.toList());

		// 取得したデータをページ情報にセット
		Page<AccountListDto> page = new PageImpl<>(accountList, pageable, options.getCount());

		return page;
	}

	/**
	 * アカウント EntityをDtoに変換
	 *
	 * @param mRoomEntity
	 * @return
	 */
	public AccountListDto convertEntity2Dto(MAccountEntity entity) {

		AccountListDto dto = new AccountListDto();

		dto.setAccountSeq(entity.getAccountSeq());
		dto.setAccountId(entity.getAccountId());
		dto.setAccountName(PersonName.fromEntity(entity).getName());
		dto.setAccountNameKana(PersonName.fromEntity(entity).getNameKana());
		dto.setAccountMailAddress(entity.getAccountMailAddress());
		dto.setAccountType(AccountType.of(entity.getAccountType()));
		dto.setAccountKengen(AccountKengen.of(entity.getAccountKengen()));
		dto.setAccountOwnerFlg(SystemFlg.codeToBoolean(entity.getAccountOwnerFlg()));
		dto.setAccountColor(entity.getAccountColor());
		dto.setAccountStatus(AccountStatus.of(entity.getAccountStatus()));
		dto.setCreatedAt(DateUtils.parseToString(entity.getCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED));
		dto.setUpdatedAt(DateUtils.parseToString(entity.getUpdatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED));
		dto.setVersionNo(entity.getVersionNo());

		return dto;
	}
}