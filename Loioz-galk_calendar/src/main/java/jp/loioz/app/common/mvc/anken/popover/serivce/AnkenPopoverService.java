package jp.loioz.app.common.mvc.anken.popover.serivce;

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
import jp.loioz.app.common.mvc.anken.popover.form.AnkenPopoverSearchForm;
import jp.loioz.app.common.mvc.anken.popover.form.AnkenPopoverViewForm;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenDto;
import jp.loioz.entity.TPersonEntity;

/**
 * 案件ポップオーバーサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AnkenPopoverService extends DefaultService {

	/** 共通サービス：案件 */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 名簿Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	// =========================================================================
	// 案件ポップオーバー
	// =========================================================================
	/**
	 * 案件ポップオーバーの表示用オブジェクトを作成する
	 * 
	 * @param searchForm
	 * @return
	 */
	public AnkenPopoverViewForm createAnkenPopoverViewForm(AnkenPopoverSearchForm searchForm) throws AppException {
		AnkenPopoverViewForm viewForm = new AnkenPopoverViewForm();

		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 名簿情報が無い場合は楽観ロックエラー
		TPersonEntity personEntity = tPersonDao.selectById(searchForm.getPersonId());
		if (personEntity == null) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		viewForm.setPersonName(PersonName.fromEntity(personEntity).getName());
		viewForm.setPersonAttribute(PersonAttribute.of(personEntity.getCustomerFlg(), personEntity.getAdvisorFlg(), personEntity.getCustomerType()));

		// 案件情報を取得する
		List<AnkenDto> ankenList = commonAnkenService.getAllAnkenRelatedToPersonId(searchForm.getPersonId(), options);

		// 案件情報が無い場合は楽観ロックエラー
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 案件IDのリスト作成
		List<Long> ankenIdList = ankenList.stream().map(dto -> dto.getAnkenId().asLong()).collect(Collectors.toList());

		// ページ情報の作成
		Page<Long> page = new PageImpl<>(ankenIdList, pageable, options.getCount());

		viewForm.setAnkenList(ankenList);
		viewForm.setPersonId(searchForm.getPersonId());
		viewForm.setPager(page);
		return viewForm;
	}

}
