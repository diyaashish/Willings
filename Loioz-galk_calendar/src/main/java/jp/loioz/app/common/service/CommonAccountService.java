package jp.loioz.app.common.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.KozaRelateType;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TGinkoKozaEntity;

/**
 * アカウント情報関連の共通サービス処理
 *
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class CommonAccountService extends DefaultService {

	@Autowired
	private MAccountDao mAccountDao;

	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	/**
	 * アカウントSEQから名前を返却する
	 *
	 * @param customerId 顧客ID
	 * @return 案件選択モーダル画面用の案件リスト
	 */
	public String getAccountName(Long accountSeq) {

		// ------------------------------------------------------------
		// アカウント情報
		// ------------------------------------------------------------
		// アカウント情報を取得
		MAccountEntity mAccountEntity = mAccountDao.selectBySeq(accountSeq);
		return PersonName.fromEntity(mAccountEntity).getName();
	}

	/**
	 * アカウント名Mapを取得します。
	 * 
	 * <pre>
	 * ※ 無効なアカウントの名前も取得するので、一覧などの表示のみで使用すること
	 * </pre>
	 * 
	 * @key アカウントSEQ
	 * @value アカウント名
	 * 
	 * @return 全てのアカウント名MAP
	 */
	public Map<Long, String> getAccountNameMap() {

		// ------------------------------------------------------------
		// アカウント情報
		// ------------------------------------------------------------
		// アカウント情報を取得
		List<MAccountEntity> mAccountEntities = mAccountDao.selectAll();

		// アカウント名Map
		Map<Long, String> accountNameMap = mAccountEntities.stream()
				.collect(Collectors.toMap(
						MAccountEntity::getAccountSeq,
						data -> PersonName.fromEntity(data).getName()));

		return accountNameMap;
	}

	/**
	 * アカウントSEQをキーとした口座情報Mapを取得します
	 * 
	 * @return
	 */
	public Map<Long, List<GinkoKozaDto>> getAccountSeqKeyAccountKozaMap() {

		Map<Long, String> accountNameMap = getAccountNameMap();
		List<TGinkoKozaEntity> accountKozaEntities = tGinkoKozaDao.selectAccountKoza();

		// KozaDto変換条件
		Function<TGinkoKozaEntity, GinkoKozaDto> kozaMapper = entity -> {
			GinkoKozaDto ginkoKozaDto = new GinkoKozaDto();
			ginkoKozaDto.setGinkoAccountSeq(entity.getGinkoAccountSeq());
			ginkoKozaDto.setBranchNo(entity.getBranchNo());
			ginkoKozaDto.setLabelName(entity.getLabelName());
			ginkoKozaDto.setGinkoName(entity.getGinkoName());
			ginkoKozaDto.setShitenName(entity.getShitenName());
			ginkoKozaDto.setShitenNo(entity.getShitenNo());
			ginkoKozaDto.setKozaType(entity.getKozaType());
			ginkoKozaDto.setKozaNo(entity.getKozaNo());
			ginkoKozaDto.setKozaName(entity.getKozaName());
			ginkoKozaDto.setAccountSeq(entity.getAccountSeq());
			ginkoKozaDto.setAccountName(accountNameMap.get(entity.getAccountSeq()));
			ginkoKozaDto.setKozaRelateType(KozaRelateType.ACCOUNT);
			return ginkoKozaDto;
		};

		// 口座連番をキーとして、Mapを作成
		Map<Long, List<GinkoKozaDto>> accountKozaMap = accountKozaEntities.stream()
				.map(kozaMapper)
				.filter(dto -> !dto.isNoneDisp())
				.collect(Collectors.groupingBy(GinkoKozaDto::getAccountSeq));

		return accountKozaMap;

	}

	/**
	 * 有効なアカウントを返す
	 * 
	 * @param mAccountEntitiesAll
	 * @return
	 */
	public List<MAccountEntity> extractEnabledAccountList(List<MAccountEntity> mAccountEntitiesAll) {

		// 有効アカウントを抽出
		List<MAccountEntity> enabledAccountList = mAccountEntitiesAll.stream()
				.filter(entity -> CommonConstant.AccountStatus.ENABLED.equalsByCode(entity.getAccountStatus())
						&& entity.getDeletedAt() == null && entity.getDeletedBy() == null)
				.collect(Collectors.toList());

		return enabledAccountList;
	}

	/**
	 * アカウント名Mapを返す
	 * 
	 * @param mAccountEntities
	 * @return
	 */
	public Map<Long, String> getAccountNameMap(List<MAccountEntity> mAccountEntities) {

		// アカウント名Map
		Map<Long, String> accountNameMap = mAccountEntities.stream()
				.collect(Collectors.toMap(
						MAccountEntity::getAccountSeq,
						data -> PersonName.fromEntity(data).getName()));

		return accountNameMap;
	}

}
