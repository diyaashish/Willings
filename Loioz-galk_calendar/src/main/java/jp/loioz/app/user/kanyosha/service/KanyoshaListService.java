package jp.loioz.app.user.kanyosha.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.app.user.kanyosha.dto.KanyoshaListDto;
import jp.loioz.app.user.kanyosha.form.KanyoshaListForm;
import jp.loioz.bean.KanyoshaListBean;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TPersonContactDao;
import jp.loioz.domain.condition.KanyoshaListSearchCondition;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.TKanyoshaEntity;
import jp.loioz.entity.TPersonContactEntity;

/**
 * 関与者一覧画面のserviceクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class KanyoshaListService extends DefaultService {

	/** 共通顧客サービス */
	@Autowired
	private CommonPersonService commonCustomerService;

	/** 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 顧客連絡先Daoクラス */
	@Autowired
	private TPersonContactDao tCustomerContactDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 関与者一覧画面の表示情報を作成する
	 *
	 * @return 画面表示情報
	 */
	public KanyoshaListForm createViewForm(Long customerId, Long ankenId) {

		KanyoshaListForm form = new KanyoshaListForm();
		if (ankenId == null) {
			return form;
		}

		// 検索用オブジェクトをbuild
		KanyoshaListSearchCondition kanyoshaListSearchCondition = KanyoshaListSearchCondition.builder().ankenId(ankenId).build();

		// 一覧情報を取得する
		List<KanyoshaListBean> kanyoshaListBeanList = tKanyoshaDao.selectKanyoshaBySearchConditions(kanyoshaListSearchCondition);
		List<KanyoshaListDto> kanyoshaListDtoList = this.convert2Dto(kanyoshaListBeanList);

		form.setAnkenId(ankenId);
		form.setKanyoshaDtoList(kanyoshaListDtoList);
		return form;
	}

	/**
	 * 表示順変更処理
	 *
	 * @param kanyoshaSeqListStr
	 * @param indexListStr
	 * @param ankenId
	 * @throws AppException
	 */
	public void dispOrder(String kanyoshaSeqListStr, String indexListStr, Long ankenId) throws AppException {
		String[] kanyoshaSeqArray = kanyoshaSeqListStr.split(",");
		String[] indexArray = indexListStr.split(",");

		List<TKanyoshaEntity> tKanyoshaEntities = tKanyoshaDao.selectKanyoshaListByAnkenId(ankenId);
		Map<Long, TKanyoshaEntity> kanyoshaSeqToMap = tKanyoshaEntities.stream().collect(Collectors.toMap(TKanyoshaEntity::getKanyoshaSeq, Function.identity()));

		// 関与者数が異なれば排他エラーを呼ぶ
		if (kanyoshaSeqArray.length != kanyoshaSeqToMap.size()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 変更があるレコードのみ順次更新する
		List<TKanyoshaEntity> updateEntities = new ArrayList<TKanyoshaEntity>();
		for (int i = 0; i < indexArray.length; i++) {

			// 関与者IDの取得
			Integer dispOrder = Integer.valueOf((indexArray[i]));
			Long kanyoshaSeq = Long.parseLong(kanyoshaSeqArray[dispOrder]);

			// 変更するEntitiyを取得
			TKanyoshaEntity entity = kanyoshaSeqToMap.get(kanyoshaSeq);

			// 既にデータがなければ排他エラーを呼ぶ
			if (entity == null) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			// 表示順を再設定
			entity.setDispOrder(dispOrder.longValue() + 1);
			updateEntities.add(entity);
		}

		try {
			// 更新処理
			tKanyoshaDao.batchUpdate(updateEntities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * List<KanyoshaListBean> -> List<KanyoshaListDto>
	 * 
	 * @param beans
	 * @return
	 */
	public List<KanyoshaListDto> convert2Dto(List<KanyoshaListBean> beans) {

		// 取得データから名簿IDを取得
		List<Long> personIdList = beans.stream().map(KanyoshaListBean::getPersonId).collect(Collectors.toList());

		// 名簿IDをキーとして、連絡先情報Mapを作成
		List<TPersonContactEntity> tCustomerContactEntities = tCustomerContactDao.selectPersonContactByPersonIdList(personIdList);
		Map<Long, List<TPersonContactEntity>> personIdToMap = tCustomerContactEntities.stream().collect(Collectors.groupingBy(TPersonContactEntity::getPersonId));

		// 画面表示用オブジェクトに変換
		List<KanyoshaListDto> kanyoshaListDtoList = beans.stream().map(e -> {

			KanyoshaListDto dto = new KanyoshaListDto();
			PersonName personName = new PersonName(e.getCustomerNameSei(), e.getCustomerNameMei(), e.getCustomerNameSeiKana(), e.getCustomerNameMeiKana());
			List<TPersonContactEntity> personIdCustomerContactEntities = personIdToMap.getOrDefault(e.getPersonId(), Collections.emptyList());

			dto.setKanyoshaSeq(e.getKanyoshaSeq());
			dto.setPersonId(e.getPersonId());
			dto.setKanyoshaType(CustomerType.of(e.getCustomerType()));
			dto.setKanyoshaName(personName.getName());
			dto.setKanyoshaNameKana(personName.getNameKana());
			dto.setKankei(e.getKankei());
			dto.setPersonAttribute(PersonAttribute.of(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType()));
			dto.setZipCode(e.getZipCode());
			dto.setAddress1(e.getAddress1());
			dto.setAddress2(e.getAddress2());
			dto.setAddressRemarks(e.getAddressRemarks());
			dto.setTelNo(commonCustomerService.getYusenTelNo(personIdCustomerContactEntities));
			dto.setFaxNo(commonCustomerService.getYusenFaxNo(personIdCustomerContactEntities));
			dto.setMailAddress(commonCustomerService.getYusenMailAddress(personIdCustomerContactEntities));
			dto.setHasKoza(!StringUtils.isAllEmpty(e.getGinkoName(), e.getShitenName(), e.getShitenNo(), e.getKozaType(), e.getKozaNo(), e.getKozaName(), e.getKozaNameKana()));
			dto.setRemarks(e.getKanyoshaRemarks());
			dto.setJimushoName(e.getJimushoName());
			dto.setDispOrder(e.getDispOrder());
			return dto;
		}).collect(Collectors.toList());

		return kanyoshaListDtoList;

	}

}