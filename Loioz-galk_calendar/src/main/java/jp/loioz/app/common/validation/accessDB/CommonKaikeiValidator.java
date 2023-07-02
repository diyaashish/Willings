package jp.loioz.app.common.validation.accessDB;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MNyushukkinKomokuDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TKaikeiKirokuDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.entity.MNyushukkinKomokuEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;

/**
 * TODO 新会計 
 *
 */
@Component
public class CommonKaikeiValidator {

	@Autowired
	MNyushukkinKomokuDao mNyushukkinKomokuDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 会計記録Daoクラス */
	@Autowired
	private TKaikeiKirokuDao tKaikeiKirokuDao;

	/** 入出金予定Daoクラス */
	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	/** 会計共通サービスクラス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/**
	 * 会計記録が顧客と案件に紐づくかを検証します。
	 *
	 * @param kaikeiKirokuSeq 検証する会計記録のデータ
	 * @param customerId 確認したい顧客ID
	 * @param ankenId 確認したい案件ID
	 * @return 検証結果
	 */
	public boolean isValidRelatingKaikeiKiroku(List<Long> kaikeiKirokuSeq, Long customerId, Long ankenId) {

		// 引数がNULLの場合検証しない
		if (ListUtils.isEmpty(kaikeiKirokuSeq) || CommonUtils.anyNull(customerId, ankenId)) {
			return true;
		}

		// 条件 (取得したデータが引数の顧客IDと案件IDに紐付いている)
		Function<TKaikeiKirokuEntity, Boolean> condition = entity -> {
			return Objects.equals(entity.getCustomerId(), customerId) && Objects.equals(entity.getAnkenId(), ankenId);
		};

		// データの取得
		List<TKaikeiKirokuEntity> tKaikeiKirokuEntities = tKaikeiKirokuDao.selectBySeqList(kaikeiKirokuSeq);

		// 取得したすべてのデータが上記の条件に合致するかを検証する
		if (tKaikeiKirokuEntities == null) {
			// データの取得ができなかった場合
			return false;

		} else if (tKaikeiKirokuEntities.stream().allMatch(entity -> condition.apply(entity))) {
			// すべて合致した場合、正常なのでtrue
			return true;
		} else {
			// どれか一つでも合致しなかった場合、エラーなので
			return false;
		}
	}

	/**
	 * 案件IDがプール可能かどうかを検証します
	 * 
	 * @param customerId 紐付いている顧客ID
	 * @param poolSouceAnkenId プール元の案件ID
	 * @param poolTargetAnkenId プール先の案件ID
	 * @return
	 */
	public boolean isValidCanKaikeiPool(Long customerId, Long poolSouceAnkenId, Long poolTargetAnkenId) {

		// NULLの場合、検証しない
		if (CommonUtils.anyNull(customerId, poolSouceAnkenId, poolTargetAnkenId)) {
			return true;
		}

		// プール可能案件を取得

		// 案件情報、プール可能案件を取得・設定
		List<TAnkenCustomerEntity> tAnkenCutomerEntities = tAnkenCustomerDao.selectByCustomerId(customerId);

		// プール可能案件条件
		Predicate<TAnkenCustomerEntity> statusFilter = e -> !AnkenStatus.isSeisanComp(e.getAnkenStatus());
		Predicate<TAnkenCustomerEntity> targetAnkenFilter = e -> !Objects.equals(e.getAnkenId(), poolSouceAnkenId);

		// プール可能案件IDを抽出
		List<Long> ankenIdList = tAnkenCutomerEntities.stream()
				.filter(targetAnkenFilter)
				.filter(statusFilter)
				.map(TAnkenCustomerEntity::getAnkenId)
				.collect(Collectors.toList());

		// プール可能案件を取得・加工・設定
		List<TAnkenEntity> tAnkenEntities = tAnkenDao.selectById(ankenIdList);

		// データの取得ができなかった場合はfalse
		if (tAnkenEntities == null) {
			return false;
		}

		// true：問題なし false：不整合
		boolean result = tAnkenEntities.stream().anyMatch(entity -> Objects.equals(entity.getAnkenId(), poolTargetAnkenId));

		if (result) {
			// 問題ないので、tureで返却
			return true;
		} else {
			// 不整合な値なので、falseで返却
			return false;
		}

	}

	/**
	 * 会計記録 入金先 or 出金元の選択した口座情報のDB整合性を検証します
	 * 
	 * <pre>
	 * ■表示する銀行口座の仕様
	 * １．事務所の銀行口座
	 * ２．案件が「事務所案件」の場合：担当弁護士の銀行口座
	 * ３．案件が「個人案件」の場合：売上計上先の銀行口座
	 * <pre>
	 * 
	 * @param ankenId 案件ID(案件によって紐付け可能な口座情報が異なるため)
	 * @param ginkoKozaSeq 入力された値
	 * @return 検証結果
	 */
	public boolean isValidRelatingUsersKozaByAnken(Long ankenId, Long ginkoKozaSeq) {

		// NULLの場合、検証しない
		if (CommonUtils.anyNull(ankenId, ginkoKozaSeq)) {
			return true;
		}

		// 条件 引数の銀行口座SEQと一致するかどうか
		Function<GinkoKozaDto, Boolean> condition = dto -> {
			return Objects.equals(dto.getGinkoAccountSeq(), ginkoKozaSeq);
		};

		// プルダウンに表示している情報を取得する
		List<GinkoKozaDto> ginkoKozaDtoList = commonKaikeiService.getTenantAccountGinkoKozaList(ankenId);

		if (ginkoKozaDtoList == null) {
			// データの取得ができなかった場合
			return false;
		} else if (ginkoKozaDtoList.stream().anyMatch(dto -> condition.apply(dto))) {
			// どれか一つでも一致した場合、正常なのでtrue
			return true;
		} else {
			// どれにも当てはまらない場合
			return false;
		}

	}

	/**
	 * 入出金予定額が超えているかを検証します
	 * 
	 * @param nyushukkinYoteiSeq
	 * @param price
	 * @return
	 */
	public boolean isValidPlanOverPrice(Long nyushukkinYoteiSeq, BigDecimal price) {

		// nullの場合は検証しない
		if (nyushukkinYoteiSeq == null) {
			return true;
		}

		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(nyushukkinYoteiSeq);
		if (tNyushukkinYoteiEntity == null) {
			// 検証しない
			return true;
		}

		if (-1 == tNyushukkinYoteiEntity.getNyushukkinYoteiGaku().compareTo(price)) {
			// 予定額 < 金額(引数) の場合
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 入出金予定額が一致するかを検証します
	 * 
	 * @param nyushukkinYoteiSeq
	 * @param price
	 * @return
	 */
	public boolean isValidNonMatchedPlanPrice(Long nyushukkinYoteiSeq, BigDecimal price) {

		// nullの場合は検証しない
		if (nyushukkinYoteiSeq == null) {
			return true;
		}

		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(nyushukkinYoteiSeq);
		if (tNyushukkinYoteiEntity == null) {
			// 検証しない
			return true;
		}

		if (0 != tNyushukkinYoteiEntity.getNyushukkinYoteiGaku().compareTo(price)) {
			// 予定額と金額が一致しない場合
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 入出金項目IDが指定した種別のデータか検証する
	 * 
	 * @param nyushukkinKomokuId
	 * @param nyushukkinType
	 * @return 検証結果
	 */
	public boolean isValidNyuShukkinKomokuEqulasType(Long nyushukkinKomokuId, NyushukkinType nyushukkinType) {

		// 引数がnullの場合、検証しない
		if (nyushukkinKomokuId == null || nyushukkinType == null) {
			return true;
		}

		MNyushukkinKomokuEntity mNyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(nyushukkinKomokuId);
		if (mNyushukkinKomokuEntity == null || !nyushukkinType.equalsByCode(mNyushukkinKomokuEntity.getNyushukkinType())) {
			return false;
		} else {
			return true;
		}

	}

}
