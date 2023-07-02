package jp.loioz.app.common.validation.accessDB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jp.loioz.dao.TAccgDocDao;
import jp.loioz.dao.TAccgInvoiceDao;
import jp.loioz.dao.TAccgStatementDao;
import jp.loioz.dao.TAnkenAzukariItemDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dao.TSeisanKirokuDao;
import jp.loioz.entity.TAccgDocEntity;
import jp.loioz.entity.TAccgInvoiceEntity;
import jp.loioz.entity.TAccgStatementEntity;
import jp.loioz.entity.TAnkenAzukariItemEntity;
import jp.loioz.entity.TKanyoshaEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;
import jp.loioz.entity.TSeisanKirokuEntity;
import lombok.NonNull;

/**
 * アカウント情報のDB整合性バリデータークラス
 */
@Component
public class CommonKanyoshaValidator {

	/** 案件-預かり品情報Daoクラス */
	@Autowired
	private TAnkenAzukariItemDao tAnkenAzukariItemDao;

	/** 入出金予定Dao */
	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	/** 精算記録Dao */
	@Autowired
	private TSeisanKirokuDao tSeisanKirokuDao;

	/** 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 会計書類Daoクラス */
	@Autowired
	private TAccgDocDao tAccgDocDao;

	/** 請求書Daoクラス */
	@Autowired
	private TAccgInvoiceDao tAccgInvoiceDao;

	/** 精算書Daoクラス */
	@Autowired
	private TAccgStatementDao tAccgStatementDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 関与者が削除可能かどうか
	 * 
	 * <pre>
	 * 関与者削除する際に
	 * ・会計データが登録されていないか
	 * ・預かり元として登録されていないか
	 * を確認する
	 *</pre>
	 * kanyoshaSeqは暗黙的に案件を一意となるので、対象範囲は同一案件のデータとなる
	 * 
	 * ※注意 <br>
	 * 削除が可能だが、他画面で紐づけ登録している場合は、そのテーブルも更新が必要
	 * <li>案件管理画面</li>
	 * <li>裁判管理画面</li>
	 * <li>預かり品一覧</li>
	 *
	 * @param kanyoshaSeq 暗黙的に案件は一意となるので、チェック時の対象範囲は同一案件のデータとなる
	 * @return
	 */
	public boolean canDeleteKanyosha(@NonNull Long kanyoshaSeq) {

		// 会計に紐づくデータを取得 関与者紐づけがされている関与者は必ず該当案件に紐づいている関与者のみ
		List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities = tNyushukkinYoteiDao.selectNyushukkinYoteiByKanyoshaSeq(kanyoshaSeq);
		List<TSeisanKirokuEntity> tSeisanKirokuEntities = tSeisanKirokuDao.selectSeisanKirokuByKanyoshaSeq(kanyoshaSeq);

		// 預かり元に登録されているデータの取得
		List<TAnkenAzukariItemEntity> tAnkenAzukariItemEntities = tAnkenAzukariItemDao.selectAzukariFromKanyoshaByKanyoshaSeq(kanyoshaSeq);

		if (CollectionUtils.isEmpty(tNyushukkinYoteiEntities) // 入出金に紐づいていない
				&& CollectionUtils.isEmpty(tSeisanKirokuEntities) // 精算に紐づいていない
				&& CollectionUtils.isEmpty(tAnkenAzukariItemEntities) // 預り元データに紐づいていない
				&& !this.existsAccgDataByKanyoshaSeq(kanyoshaSeq)) // 新会計に紐づいていない
		{
			// 削除可能
			return true;
		}

		return false;
	}

	/**
	 * 関与者情報が新会計で利用されているデータが存在するかどうか
	 * 
	 * <pre>
	 * 該当関与者の名簿IDが、請求先名簿 もしくは 返却先名簿として登録されており、
	 * 該当関与者の案件と同一案件の会計書類データである場合
	 * => 新会計で利用されている関与者情報 
	 * </pre>
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	private boolean existsAccgDataByKanyoshaSeq(Long kanyoshaSeq) {

		// 関与者情報の取得
		TKanyoshaEntity tKanyoshaEntity = tKanyoshaDao.selectKanyoshaByKanyoshaSeq(kanyoshaSeq);
		Long personId = tKanyoshaEntity.getPersonId();

		// 関与者に紐づく名簿IDが請求先/精算先顧客に設定されている会計書類Seqを取得する
		List<TAccgInvoiceEntity> tAccgInvoiceEntities = tAccgInvoiceDao.selectInvoiceByBillToPersonId(personId);
		List<TAccgStatementEntity> tAccgStatementEntities = tAccgStatementDao.selectStatementByRefundToPersonId(personId);

		Set<Long> accgDocSeqList = new HashSet<>();
		accgDocSeqList.addAll(tAccgInvoiceEntities.stream().map(TAccgInvoiceEntity::getAccgDocSeq).collect(Collectors.toList()));
		accgDocSeqList.addAll(tAccgStatementEntities.stream().map(TAccgStatementEntity::getAccgDocSeq).collect(Collectors.toList()));

		if (CollectionUtils.isEmpty(accgDocSeqList)) {
			// 該当関与者SEQは新会計で利用されていないので、falseを返却
			return false;
		}

		// 会計書類をキーとしてデータ取得
		// この会計書類データは当メソッドで取得した関与者情報に紐づく案件以外のデータも含まれる
		List<TAccgDocEntity> tAccgDocEntities = tAccgDocDao.selectAccgDocByAccgDocSeq(new ArrayList<>(accgDocSeqList));

		if (tAccgDocEntities.stream().anyMatch(e -> Objects.equals(e.getAnkenId(), tKanyoshaEntity.getAnkenId()))) {
			// 会計書類データの案件IDと関与者情報の案件IDが一致する場合データが存在する場合
			// 対象の関与者が新会計で利用されているデータである -> trueを返却
			return true;
		}

		return false;
	}

}
