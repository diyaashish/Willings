package jp.loioz.app.common.validation.accessDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.dto.KozaDto;
import jp.loioz.entity.TGinkoKozaEntity;
import lombok.NonNull;

/**
 * 口座情報の共通バリデートクラス
 */
@Component
public class CommonGinkoKozaValidator {

	/** 銀行口座Daoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	/**
	 * 口座情報更新時にlabelNameのNULLを許容するかどうか
	 * 
	 * @param inputData
	 * @return 検証結果
	 */
	public boolean labelNameRequwiredPermitValidate(@NonNull KozaDto inputData) {

		// labelNameが空 && 他の項目がどれか一つでも入力がある場合は許容しない
		if (StringUtils.isEmpty(inputData.getLabelName())
				&& !StringUtils.isAllEmpty(
						inputData.getGinkoName(),
						inputData.getShitenNo(),
						inputData.getShitenName(),
						DefaultEnum.getCd(inputData.getKozaType()),
						inputData.getKozaNo(),
						inputData.getKozaName(),
						inputData.getKozaNameKana())) {
			return false;
		}

		// 口座情報が登録されていないの場合はここで検証を終わる
		if (inputData.getGinkoAccountSeq() == null) {
			return true;
		}

		// 更新前のlabelNameが登録されている && NULLもしくは空に更新されるケースを判定
		TGinkoKozaEntity tGinkoKozaEntity = tGinkoKozaDao.selectByGinkoAccountSeq(inputData.getGinkoAccountSeq());
		if (StringUtils.isNotEmpty(tGinkoKozaEntity.getLabelName()) && StringUtils.isEmpty(inputData.getLabelName())) {
			return false;
		}

		return true;
	}

}
