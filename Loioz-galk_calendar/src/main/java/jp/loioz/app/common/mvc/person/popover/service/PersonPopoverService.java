package jp.loioz.app.common.mvc.person.popover.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.mvc.person.popover.form.PersonPopoverViewForm;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.common.constant.CommonConstant.AllowType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TPersonAddHojinDao;
import jp.loioz.dao.TPersonAddKojinDao;
import jp.loioz.dao.TPersonAddLawyerDao;
import jp.loioz.dao.TPersonContactDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.PersonId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.TPersonAddHojinEntity;
import jp.loioz.entity.TPersonAddKojinEntity;
import jp.loioz.entity.TPersonAddLawyerEntity;
import jp.loioz.entity.TPersonContactEntity;
import jp.loioz.entity.TPersonEntity;
import lombok.NonNull;

/**
 * 名簿ポップオーバーサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PersonPopoverService extends DefaultService {

	/** 名簿情報Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 名簿連絡先情報Daoクラス */
	@Autowired
	private TPersonContactDao tPersonContactDao;

	/** 名簿個人付帯情報Daoクラス */
	@Autowired
	private TPersonAddKojinDao tPersonAddKojinDao;

	/** 名簿法人付帯情報Daoクラス */
	@Autowired
	private TPersonAddHojinDao tPersonAddHojinDao;

	/** 名簿弁護士付帯情報Daoクラス */
	@Autowired
	private TPersonAddLawyerDao tPersonAddLawyerDao;

	/** 共通サービス：名簿 */
	@Autowired
	private CommonPersonService commonPersonService;

	// =========================================================================
	// 名簿ポップオーバー
	// =========================================================================
	/**
	 * 名簿ポップオーバーの表示用オブジェクトを作成する
	 * 
	 * @param personId
	 * @return
	 */
	public PersonPopoverViewForm createPersonPopoverViewForm(@NonNull Long personId) throws AppException {
		PersonPopoverViewForm viewForm = new PersonPopoverViewForm();

		// 名簿情報を取得する
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		List<TPersonContactEntity> tPersonContactEntities = tPersonContactDao.selectPersonContactByPersonId(personId);
		if (personEntity == null) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		PersonName personName = PersonName.fromEntity(personEntity);

		// 名簿情報
		viewForm.setPersonId(PersonId.of(personEntity.getPersonId()));
		viewForm.setCustomerFlg(personEntity.getCustomerFlg());
		viewForm.setAdvisorFlg(personEntity.getAdvisorFlg());
		viewForm.setCustomerType(personEntity.getCustomerType());
		viewForm.setCustomerName(personName.getName());
		viewForm.setCustomerNameKana(personName.getNameKana());
		// 住所情報（居住地 or 所在地）
		viewForm.setZipCode(personEntity.getZipCode());
		viewForm.setAddress1(personEntity.getAddress1());
		viewForm.setAddress2(personEntity.getAddress2());
		// 連絡先情報
		if (StringUtils.isNotEmpty(personEntity.getContactType())) {
			viewForm.setAllowType(AllowType.of(personEntity.getContactType()));
		}
		if (StringUtils.isNotEmpty(personEntity.getContactRemarks())) {
			viewForm.setAllowTypeRemarks(personEntity.getContactRemarks());
		}
		viewForm.setTelNo(commonPersonService.getYusenTelNo(tPersonContactEntities));
		viewForm.setFaxNo(commonPersonService.getYusenFaxNo(tPersonContactEntities));
		viewForm.setMailAddress(commonPersonService.getYusenMailAddress(tPersonContactEntities));

		// 個人の場合のみ
		if (CustomerType.KOJIN.equalsByCode(personEntity.getCustomerType())) {
			TPersonAddKojinEntity tCustomerAddKojinEntity = tPersonAddKojinDao.selectPersonAddKojinByPersonId(personId);
			viewForm.setGender(DefaultEnum.getVal(Gender.of(tCustomerAddKojinEntity.getGenderType())));

			boolean isDead = SystemFlg.codeToBoolean(StringUtils.defaultString(tCustomerAddKojinEntity.getDeathFlg()));
			Integer age = null;
			if (!(isDead && Objects.isNull(tCustomerAddKojinEntity.getDeathDate()))) {
				// 亡くなっているが、死亡日が入力されていないケース以外は、年齢を算出
				age = DateUtils.getAgeNumber(tCustomerAddKojinEntity.getBirthday(), tCustomerAddKojinEntity.getDeathDate());
			}
			viewForm.setAge(age);
			viewForm.setDead(isDead);
		}

		// 法人の場合のみ
		if (CustomerType.HOJIN.equalsByCode(personEntity.getCustomerType())) {
			TPersonAddHojinEntity tCustomerAddHojinEntity = tPersonAddHojinDao.selectPersonAddHojinByPersonId(personId);
			viewForm.setDaihyoName(tCustomerAddHojinEntity.getDaihyoName());
			viewForm.setDaihyoNameKana(tCustomerAddHojinEntity.getDaihyoNameKana());
			viewForm.setDaihyoPositionName(tCustomerAddHojinEntity.getDaihyoPositionName());
			viewForm.setTantoName(tCustomerAddHojinEntity.getTantoName());
			viewForm.setTantoNameKana(tCustomerAddHojinEntity.getTantoNameKana());
		}

		// 弁護士の場合のみ
		if (CustomerType.LAWYER.equalsByCode(personEntity.getCustomerType())) {
			TPersonAddLawyerEntity tPersonAddLawyerEntity = tPersonAddLawyerDao.selectPersonAddLawyerByPersonId(personId);
			viewForm.setJimushoName(tPersonAddLawyerEntity.getJimushoName());
		}

		return viewForm;
	}

}
