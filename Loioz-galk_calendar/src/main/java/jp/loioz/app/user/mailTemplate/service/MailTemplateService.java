package jp.loioz.app.user.mailTemplate.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.user.mailTemplate.dto.MailTemplateListItemDto;
import jp.loioz.app.user.mailTemplate.form.MailTemplateViewForm;
import jp.loioz.common.constant.CommonConstant.MailTemplateType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.exception.AppException;
import jp.loioz.dao.MMailTemplateDao;
import jp.loioz.entity.MMailTemplateEntity;

/**
 * メールテンプレート画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MailTemplateService {

	/** メールテンプレート */
	@Autowired
	private MMailTemplateDao mMailTemplateDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * メールテンプレート一覧 フラグメントViewの作成
	 * 
	 * @return
	 * @throws AppException
	 */
	public MailTemplateViewForm.MailTemlpateListViewForm createMailTemlpateListViewForm() {

		// 画面表示用オブジェクトを作成
		var viewForm = new MailTemplateViewForm.MailTemlpateListViewForm();

		// DBデータ取得・加工・設定
		List<MMailTemplateEntity> mMailTemplateEntities = mMailTemplateDao.selectAll();
		List<MailTemplateListItemDto> templateList = mMailTemplateEntities.stream().map(this::convertEntity2Dto).collect(Collectors.toList());

		viewForm.setTemplateList(templateList);

		return viewForm;
	}

	/**
	 * Entity -> dto
	 * 
	 * @param MMailTemplateEntity
	 * @return MailTemplateListItemDto
	 */
	private MailTemplateListItemDto convertEntity2Dto(MMailTemplateEntity entity) {

		var dto = new MailTemplateListItemDto();
		dto.setMailTemplateSeq(entity.getMailTemplateSeq());
		dto.setMailTemplateType(MailTemplateType.of(entity.getTemplateType()));
		dto.setMailTemplateTitle(entity.getTemplateTitle());
		dto.setDefaultUse(SystemFlg.codeToBoolean(entity.getDefaultUseFlg()));

		return dto;
	}

}