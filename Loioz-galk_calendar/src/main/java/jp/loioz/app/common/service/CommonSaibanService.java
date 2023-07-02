package jp.loioz.app.common.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.seasar.doma.DomaNullPointerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.Keys;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.app.common.validation.accessDB.CommonScheduleValidator;
import jp.loioz.app.common.word.builder.naibu.Wn0008WordBuider;
import jp.loioz.app.common.word.builder.naibu.Wn0009WordBuider;
import jp.loioz.app.common.word.config.WordConfig;
import jp.loioz.app.common.word.dto.naibu.Wn0008WordDto;
import jp.loioz.app.common.word.dto.naibu.Wn0009WordDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKanyoshaDairininViewDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKanyoshaViewDto;
import jp.loioz.app.user.saibanManagement.form.SaibanScheduleInputForm;
import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import jp.loioz.app.user.schedule.service.ScheduleCommonService;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.bean.AnkenRelatedKanyoshaBean;
import jp.loioz.bean.AnkenRelatedParentSaibanBean;
import jp.loioz.bean.AnkenRelatedSaibanKeijiBean;
import jp.loioz.bean.SaibanAnkenBean;
import jp.loioz.bean.SaibanRelatedKanyoshaBean;
import jp.loioz.bean.SaibanTantoBean;
import jp.loioz.bean.SaibanTojishaBean;
import jp.loioz.common.constant.ChohyoWordConstant;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.HeigoHansoType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SchedulePermission;
import jp.loioz.common.constant.CommonConstant.ShutteiType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TojishaHyoki;
import jp.loioz.common.constant.CommonConstant.TojishaHyokiType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MRoomDao;
import jp.loioz.dao.MSaibanshoDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TGyomuHistoryDao;
import jp.loioz.dao.TPersonAddLawyerDao;
import jp.loioz.dao.TSaibanAddKeijiDao;
import jp.loioz.dao.TSaibanCustomerDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.dao.TSaibanLimitDao;
import jp.loioz.dao.TSaibanLimitRelationDao;
import jp.loioz.dao.TSaibanRelatedKanyoshaDao;
import jp.loioz.dao.TSaibanSaibankanDao;
import jp.loioz.dao.TSaibanTantoDao;
import jp.loioz.dao.TSaibanTreeDao;
import jp.loioz.dao.TScheduleAccountDao;
import jp.loioz.dao.TScheduleDao;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenRelatedParentSaibanDto;
import jp.loioz.dto.KanyoshaTojishaDto;
import jp.loioz.dto.SaibanTantoAccountDto;
import jp.loioz.dto.SaibankanDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MRoomEntity;
import jp.loioz.entity.MSaibanshoEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TAnkenTantoEntity;
import jp.loioz.entity.TGyomuHistoryEntity;
import jp.loioz.entity.TPersonAddLawyerEntity;
import jp.loioz.entity.TSaibanAddKeijiEntity;
import jp.loioz.entity.TSaibanCustomerEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TSaibanJikenEntity;
import jp.loioz.entity.TSaibanLimitEntity;
import jp.loioz.entity.TSaibanLimitRelationEntity;
import jp.loioz.entity.TSaibanRelatedKanyoshaEntity;
import jp.loioz.entity.TSaibanSaibankanEntity;
import jp.loioz.entity.TSaibanTantoEntity;
import jp.loioz.entity.TSaibanTreeEntity;
import jp.loioz.entity.TScheduleAccountEntity;
import jp.loioz.entity.TScheduleEntity;

/**
 * 共通裁判用処理クラス
 *
 */
@Component
public class CommonSaibanService extends DefaultService {

	/** 共通案件情報関連サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 共通アカウントサービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 共通予定サービスクラス */
	@Autowired
	private ScheduleCommonService scheduleCommonService;

	/** 共通予定バリデーションサービスクラス */
	@Autowired
	private CommonScheduleValidator commonScheduleValidator;

	/** 共通帳票サービスクラス */
	@Autowired
	private CommonChohyoService commonChohyoService;

	/** 共通予定バリデーションサービスクラス */
	@Autowired
	private MessageService messageService;

	/** ワード出力用の設定ファイル */
	@Autowired
	private WordConfig wordConfig;

	@Autowired
	private Logger logger;

	/** アカウントマスタ情報 */
	@Autowired
	private MAccountDao mAccountDao;

	/** 案件-顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 案件-担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 裁判Daoクラス */
	@Autowired
	private TSaibanDao tSaibanDao;

	/** 裁判-裁判官用のDaoクラス */
	@Autowired
	private TSaibanSaibankanDao tSaibanSaibankanDao;

	/** 裁判-担当者Daoクラス */
	@Autowired
	private TSaibanTantoDao tSaibanTantoDao;

	/** 裁判-顧客 */
	@Autowired
	private TSaibanCustomerDao tSaibanCustomerDao;

	/** 裁判-関与者 */
	@Autowired
	private TSaibanRelatedKanyoshaDao tSaibanRelatedKanyoshaDao;

	/** 裁判-期日紐付け */
	@Autowired
	private TSaibanLimitRelationDao tSaibanLimitRelationDao;

	/** 裁判-ツリー */
	@Autowired
	private TSaibanTreeDao tSaibanTreeDao;

	/** 業務履歴Daoクラス */
	@Autowired
	private TGyomuHistoryDao tGyomuHistoryDao;

	/** 裁判事件Daoクラス */
	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	/** 刑事裁判付帯Daoクラス */
	@Autowired
	private TSaibanAddKeijiDao tSaibanAddKeijiDao;

	/** 裁判期日Daoクラス */
	@Autowired
	private TSaibanLimitDao tSaibanLimitDao;

	/** 予定Daoクラス */
	@Autowired
	private TScheduleDao tScheduleDao;

	/** 予定アカウントDaoクラス */
	@Autowired
	private TScheduleAccountDao tScheduleAccountDao;

	/** 会議室Daoクラス */
	@Autowired
	private MRoomDao mRoomDao;

	/** 裁判所マスタDaoクラス */
	@Autowired
	private MSaibanshoDao mSaibanshoDao;

	/** テナント情報Daoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 名簿-弁護士付帯情報Dao */
	@Autowired
	private TPersonAddLawyerDao tPersonAddLawyerDao;

	/** 反訴被告 */
	private static final String HANSO_HIKOKU = "(反訴被告)";
	/** 反訴原告 */
	private static final String HANSO_GENKOKU = "(反訴原告)";
	/** 反訴●● */
	private static final String HANSO_SOMETHING = "(反訴●●)";
	/** 代理人弁護士 */
	private static final String DAIRININ_BENGOSHI = "代理人弁護士";
	/** 被告人 */
	private static final String HIKOKUNIN = "被告人";
	/** 訴訟代理人弁護士 */
	private static final String SOSHO_DAIRININ_BENGOSHI = "訴訟代理人弁護士 ";
	/** ら */
	private static final String OTHERS = "ら";

	/** 裁判当事者名MapのKey */
	public static final String SAIBAN_TOJISHA_LABEL_MAP_TOJISHA_KEY = "tojisha";
	public static final String SAIBAN_TOJISHA_LABEL_MAP_AITEGATA_KEY = "aitegata";

	// ===============================================================
	// publicメソッド
	// ===============================================================

	/**
	 * 裁判Entityを取得
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public TSaibanEntity getSaibanEntity(Long saibanSeq) {

		TSaibanEntity entity = tSaibanDao.selectBySeq(saibanSeq);

		// 裁判情報が存在しない場合
		if (entity == null) {
			throw new DataNotFoundException("裁判情報が存在しません。[saibanSeq=" + saibanSeq + "]");
		}

		return entity;
	}

	/**
	 * 対象の裁判に親裁判が存在する場合は、親裁判の裁判SEQを返却する。<br>
	 * 対象の裁判が親裁判の場合は、引数の裁判SEQをそのまま返却する。
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public Long getParentSaibanSeq(Long saibanSeq) {

		TSaibanTreeEntity tSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(saibanSeq);
		if (tSaibanTreeEntity == null) {
			throw new DataNotFoundException("裁判情報が取得できませんでした。");
		}

		Long parentSaibanSeq = tSaibanTreeEntity.getParentSeq();

		if (parentSaibanSeq == null) {
			// 親裁判の場合
			return saibanSeq;
		} else {
			// 子裁判の場合
			return parentSaibanSeq;
		}
	}

	/**
	 * 裁判SEQと分野を取得する
	 * 
	 * @param ankenId
	 * @param branchNo
	 * @return
	 */
	public SaibanAnkenBean getSaibanAnken(Long ankenId, Long branchNo) {
		SaibanAnkenBean bean = tSaibanDao.selectByAnkenIdAndBranchNo(ankenId, branchNo);
		if (bean == null) {
			// 裁判が削除されている
			throw new DataNotFoundException("ankenId:" + ankenId + " branchNo:" + branchNo);
		}
		return bean;
	}

	/**
	 * 案件に紐づく裁判（親裁判）を取得する
	 * 
	 * <pre>
	 * 民事用
	 * </pre>
	 * 
	 * @param ankenId
	 */
	public List<AnkenRelatedParentSaibanDto> getAnkenRelatedSaibanMinji(Long ankenId) {

		// 案件に紐づく裁判（親裁判）を取得する
		List<AnkenRelatedParentSaibanBean> ankenRelatedParentSaibanBeanList = tSaibanDao.selectParentSaibanMinjiByAnkenId(ankenId);

		// 表示用に加工
		List<AnkenRelatedParentSaibanDto> ankenParentSaibanDtoList = this.setAnkenRelatedParentSaibanMinjiBean2Dto(ankenRelatedParentSaibanBeanList);

		return ankenParentSaibanDtoList;
	}

	/**
	 * 案件に紐づく裁判（本起訴）を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	public List<AnkenRelatedParentSaibanDto> getAnkenRelatedSaibanKeiji(Long ankenId) {

		// 案件に紐づく裁判（本起訴事件）を取得する
		List<AnkenRelatedSaibanKeijiBean> ankenRelatedSaibanKeijiBean = tSaibanDao.selectSaibanKeijiByAnkenId(ankenId);

		// 表示用に加工
		List<AnkenRelatedParentSaibanDto> ankenRelatedSaibanKeijiDtoList = this.setAnkenRelatedParentSaibanKeijiBean2Dto(ankenRelatedSaibanKeijiBean);

		return ankenRelatedSaibanKeijiDtoList;
	}

	/**
	 * 対象裁判の裁判官情報を取得する
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public List<SaibankanDto> getSaibankanList(Long saibanSeq) {

		List<TSaibanSaibankanEntity> saibanSaibankanEntityList = tSaibanSaibankanDao.selectBySaibanSeq(saibanSeq);

		List<SaibankanDto> saibankanDtoList = saibanSaibankanEntityList.stream()
				.map(entity -> {
					SaibankanDto dto = new SaibankanDto();
					dto.setSaibankanName(entity.getSaibankanName());
					return dto;
				})
				.collect(Collectors.toList());

		return saibankanDtoList;
	}

	/**
	 * 対象裁判の裁判官情報を、対象の裁判官情報で更新する
	 * 
	 * @param saibanSeq
	 * @param saibankanDtoList
	 * @throws AppException
	 */
	public void updateSaibankanList(Long saibanSeq, List<SaibankanDto> saibankanDtoList) throws AppException {

		if (saibankanDtoList == null) {
			throw new IllegalArgumentException("更新データのDtoListがNULLです");
		}

		if (saibankanDtoList.size() > CommonConstant.SAIBAN_SAIBANKAN_ADD_LIMIT) {
			throw new IllegalArgumentException("更新データのDtoListが上限値を超えています");
		}

		// 現在登録されている裁判官
		List<TSaibanSaibankanEntity> saibanSaibankanEntityList = tSaibanSaibankanDao.selectBySaibanSeq(saibanSeq);

		// DtoListから空入力のものは除外する
		List<SaibankanDto> saibankanDtoListNotIncludeEmpty = saibankanDtoList.stream()
				.filter(e -> StringUtils.isNotEmpty(e.getSaibankanName()))
				.map(e -> SaibankanDto.builder()
						.saibankanName(e.getSaibankanName())
						.build())
				.collect(Collectors.toList());

		if (LoiozCollectionUtils.isNotEmpty(saibanSaibankanEntityList) && CollectionUtils.isEmpty(saibankanDtoListNotIncludeEmpty)) {
			// 登録データがあり、入力データがない場合 -> 削除処理を行う
			this.deleteSaibankanEntity(saibanSaibankanEntityList);
			return;
		}

		int inputSaibankanCount = saibankanDtoListNotIncludeEmpty.size();
		int dbSaibankanCount = saibanSaibankanEntityList.size();

		if (inputSaibankanCount == dbSaibankanCount) {
			// 入力件数が現在の登録件数と同じ場合 -> 更新処理を行う

			// entityをDtoの値で更新する
			this.updateSaibankanEntityWithDtoVal(saibankanDtoListNotIncludeEmpty, saibanSaibankanEntityList);
			return;

		} else if (inputSaibankanCount > dbSaibankanCount) {
			// 入力件数が現在の登録件数より大きい場合 -> 更新と登録を行う

			// DBにすでに登録されている件数分のDtoは更新
			List<SaibankanDto> updateDto = saibankanDtoListNotIncludeEmpty.subList(0, dbSaibankanCount);
			// entityをDtoの値で更新する
			this.updateSaibankanEntityWithDtoVal(updateDto, saibanSaibankanEntityList);

			// まだ登録されていない件数分のDtoは登録
			List<SaibankanDto> insertDto = saibankanDtoListNotIncludeEmpty.subList(dbSaibankanCount, inputSaibankanCount);
			this.insertSaibankanEntityWithDtoVal(insertDto, saibanSeq);

			return;

		} else {
			// 入力件数が現在の登録件数より小さい場合 -> 更新と削除を行う

			// 入力件数分は更新
			List<TSaibanSaibankanEntity> updateEintityList = saibanSaibankanEntityList.subList(0, inputSaibankanCount);
			// entityをDtoの値で更新する
			this.updateSaibankanEntityWithDtoVal(saibankanDtoListNotIncludeEmpty, updateEintityList);

			// 入力がなかった件数分は削除
			List<TSaibanSaibankanEntity> deleteEintityList = saibanSaibankanEntityList.subList(inputSaibankanCount, dbSaibankanCount);
			this.deleteSaibankanEntity(deleteEintityList);

			return;
		}
	}

	/**
	 * Dtoの値でEntityを登録する
	 * 
	 * @param saibankanDtoList
	 * @param saibanSeq 裁判官を登録する裁判の裁判Seq
	 * @throws AppException
	 */
	public void insertSaibankanEntityWithDtoVal(List<SaibankanDto> saibankanDtoList, Long saibanSeq) throws AppException {

		if (CollectionUtils.isEmpty(saibankanDtoList)) {
			return;
		}

		if (saibankanDtoList.size() > CommonConstant.SAIBAN_SAIBANKAN_ADD_LIMIT) {
			throw new IllegalArgumentException("登録データのDtoListが上限値を超えています");
		}

		List<TSaibanSaibankanEntity> insertEintityList = saibankanDtoList.stream()
				.map(dto -> {
					TSaibanSaibankanEntity entity = new TSaibanSaibankanEntity();
					entity.setSaibanSeq(saibanSeq);
					entity.setSaibankanName(dto.getSaibankanName());
					return entity;
				})
				.collect(Collectors.toList());
		// 登録
		this.insertSaibankanEntity(insertEintityList);
	}

	/**
	 * 裁判の担当弁護士、担当事務の情報を格納したMapを取得する。（編集用）
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public Map<TantoType, List<AnkenTantoSelectInputForm>> getTantoListMapForEdit(Long saibanSeq) {
		boolean isForEdit = true;
		Long ankenId = null;
		return this.getTantoListMap(saibanSeq, ankenId, isForEdit);
	}

	/**
	 * 裁判の担当弁護士、担当事務の情報を格納したMapを取得する。（登録用）
	 * 
	 * @return
	 */
	public Map<TantoType, List<AnkenTantoSelectInputForm>> getTantoListMapForRegist(Long ankenId) {
		boolean isForEdit = false;
		Long saibanSeq = null;
		return this.getTantoListMap(saibanSeq, ankenId, isForEdit);
	}

	/**
	 * 裁判の担当弁護士、担当事務の選択肢情報を格納したMapを取得する。（編集用）<br>
	 * （既存の裁判担当の登録データを考慮する）
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	public Map<TantoType, List<SelectOptionForm>> getTantoSelectOptListMapForEdit(Long saibanSeq, Long ankenId) {
		boolean isForEdit = true;
		return this.getTantoSelectOptListMap(saibanSeq, ankenId, isForEdit);
	}

	/**
	 * 裁判の担当弁護士、担当事務の選択肢情報を格納したMapを取得する。（登録用）<br>
	 * （既存の裁判担当の登録データを考慮しない）
	 * 
	 * @param ankenId
	 * @return
	 */
	public Map<TantoType, List<SelectOptionForm>> getTantoSelectOptListMapForRegist(Long ankenId) {
		boolean isForEdit = false;
		Long saibanSeq = null;
		return this.getTantoSelectOptListMap(saibanSeq, ankenId, isForEdit);
	}

	/**
	 * 裁判SEQをキーとして、担当者を取得し、表示優先順位が最も高い担当者を表示します
	 *
	 * @param type
	 * @param saibanSeq
	 * @return 優先順位が最も高い裁判担当者名
	 */
	public String getPrioritySaibanTantoName(TantoType type, Long saibanSeq) {

		List<TSaibanTantoEntity> tSaibanTantoEntities = tSaibanTantoDao.selectBySaibanSeq(saibanSeq);
		return this.getPrioritySaibanTantoName(type, tSaibanTantoEntities);
	}

	/**
	 * 表示優先順位が最も高い担当者を表示します
	 *
	 * @param type
	 * @param saibanEntities
	 * @return 優先順位が最も高い裁判担当者名
	 */
	public String getPrioritySaibanTantoName(TantoType type, List<TSaibanTantoEntity> saibanEntities) {

		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		Long priorityTantoSeq = this.getPrioritySaibanTantoSeq(type, saibanEntities);
		return accountNameMap.get(priorityTantoSeq);
	}

	/**
	 * 裁判-担当者リストから、条件に該当するデータを取得します
	 *
	 * <pre>
	 * 優先度
	 * → 種別ごとに判定。メイン担当フラグがONの場合、その人が優先
	 *     該当者がいない場合、枝番(BranchNo)が一番小さいデータ
	 * </pre>
	 *
	 * @param type 担当種別(弁護士、事務)
	 * @param saibanEntities 裁判IDで絞り込まれていること
	 * @return 上記条件にマッチしたentity
	 */
	public Long getPrioritySaibanTantoSeq(TantoType type, List<TSaibanTantoEntity> saibanEntities) {

		// アカウント種別でフィルタリング
		List<TSaibanTantoEntity> saibanTantoOfType = saibanEntities.stream()
				.filter(entity -> Objects.equals(entity.getTantoType(), type.getCd()))
				.collect(Collectors.toList());

		// 登録されていない場合はnullを返却
		if (saibanTantoOfType.isEmpty()) {
			return null;
		}

		// メイン担当フラグを持っている人がいるか確認する。
		boolean isMainUser = saibanTantoOfType.stream()
				.anyMatch(entity -> SystemFlg.codeToBoolean(entity.getSaibanMainTantoFlg()));

		// true の場合、メイン担当フラグがONのアカウントSEQ内のブランチNoが一番若いデータのSEQretrun;
		// falseの場合、枝番が一番若いアカウントSEQをreturn
		Optional<TSaibanTantoEntity> matchData;
		if (isMainUser) {
			matchData = saibanTantoOfType.stream()
					.filter(entity -> SystemFlg.codeToBoolean(entity.getSaibanMainTantoFlg()))
					.sorted(Comparator.comparing(TSaibanTantoEntity::getTantoTypeBranchNo))
					.findFirst();
		} else {
			matchData = saibanTantoOfType.stream()
					.min(Comparator.comparingLong(TSaibanTantoEntity::getTantoTypeBranchNo));
		}

		// 条件に一致するデータを返却
		if (matchData.isPresent()) {
			return matchData.get().getAccountSeq();
		} else {
			return null;
		}
	}

	/**
	 * 併合・反訴等のステータスを判定し、Map型で返却します
	 *
	 * <pre>
	 * 格納されている各種データのKey
	 * 本訴事件 : "isHonso"
	 * 基本事件 : "isKihon"
	 * 併合事件 : "isHeigo"
	 * 反訴事件 : "isHanso"
	 * </pre>
	 *
	 * @param TSaibanTreeEntity
	 * @return 各ステータスの判定結果
	 */
	public Map<String, Boolean> connectStatus(TSaibanTreeEntity entity) {

		Map<String, Boolean> connectStatus = new HashMap<>();

		if (entity == null) {
			return connectStatus;
		}

		connectStatus.put(CommonConstant.ConnectStatusMapKey.KIHON.getCd(), SystemFlg.codeToBoolean(entity.getKihonFlg()));
		connectStatus.put(CommonConstant.ConnectStatusMapKey.HONSO.getCd(), SystemFlg.codeToBoolean(entity.getHonsoFlg()));
		connectStatus.put(CommonConstant.ConnectStatusMapKey.HEIGO.getCd(),
				CommonConstant.HeigoHansoType.HEIGO.equalsByCode(entity.getConnectType()));
		connectStatus.put(CommonConstant.ConnectStatusMapKey.HANSO.getCd(),
				CommonConstant.HeigoHansoType.HANSO.equalsByCode(entity.getConnectType()));

		return connectStatus;
	}

	/**
	 * 裁判の当事者名Mapを取得する<br>
	 * 
	 * <pre>
	 * value側のMapKeyは、
	 * ・当事者の場合：{@link CommonSaibanService.SAIBAN_TOJISHA_LABEL_MAP_TOJISHA_KEY}
	 * ・相手方の場合：{@link CommonSaibanService.SAIBAN_AITEGATA_LABEL_MAP_TOJISHA_KEY}
	 * </pre>
	 * 
	 * @param saibanSeq
	 * @return key: saibanSeq, value:当事者、相手方(刑事の場合はなし)
	 */
	public Map<Long, Map<String, String>> getSaibanTojishaNameMap(List<Long> saibanSeq) {

		Map<Long, Map<String, String>> saibanTojishaNameMap = new HashMap<>();

		List<SaibanTojishaBean> saibanTojishaAllBeanList = tSaibanDao.selectSaibanTojishaBeanBySaibanSeqList(saibanSeq);
		if (CollectionUtils.isEmpty(saibanTojishaAllBeanList)) {
			return saibanTojishaNameMap;
		}

		// 刑事情報を取得
		List<TSaibanAddKeijiEntity> tSaibanAddKeijiEntities = tSaibanAddKeijiDao.selectBySaibanSeq(saibanSeq);
		Set<Long> keijiSaibanSeqSet = tSaibanAddKeijiEntities.stream().map(TSaibanAddKeijiEntity::getSaibanSeq).collect(Collectors.toSet());

		Map<Long, List<SaibanTojishaBean>> saibanSeqToTojishaAllBeanMap = saibanTojishaAllBeanList.stream().collect(Collectors.groupingBy(SaibanTojishaBean::getSaibanSeq));

		for (Map.Entry<Long, List<SaibanTojishaBean>> entry : saibanSeqToTojishaAllBeanMap.entrySet()) {
			Map<String, String> dispTojishaLabelMap = new HashMap<>();
			boolean isKeiji = keijiSaibanSeqSet.contains(entry.getKey());
			if (isKeiji) {
				dispTojishaLabelMap = this.generateKeijiSaibanTojishaLabelMap(entry.getValue());
			} else {
				dispTojishaLabelMap = this.generateMinjiSaibanTojishaLabelMap(entry.getValue());
			}

			// Mapに追加
			saibanTojishaNameMap.put(entry.getKey(), dispTojishaLabelMap);
		}
		return saibanTojishaNameMap;
	}

	/**
	 * 民事裁判の当事者表示名Mapを作成する
	 * 
	 * @param minjiBeans
	 * @return
	 */
	private Map<String, String> generateMinjiSaibanTojishaLabelMap(List<SaibanTojishaBean> minjiBeans) {

		Map<String, String> tojishaLabelMap = new HashMap<>();

		// 共同訴訟人（顧客含む）
		List<SaibanTojishaBean> kyodososhoninData = minjiBeans.stream()
				.filter(e -> !SystemFlg.codeToBoolean(e.getDairiFlg()))
				.filter(e -> (e.getKanyoshaType() == null || KanyoshaType.KYODOSOSHONIN.equalsByCode(e.getKanyoshaType())))
				.collect(Collectors.toList());

		String minjiTojishaLabel = "";
		if (!ListUtils.isEmpty(kyodososhoninData)) {
			if (kyodososhoninData.stream().anyMatch(e -> SystemFlg.codeToBoolean(e.getMainFlg()))) {
				SaibanTojishaBean main = kyodososhoninData.stream().filter(e -> SystemFlg.codeToBoolean(e.getMainFlg())).findFirst().get();
				minjiTojishaLabel += new PersonName(main.getNameSei(), main.getNameMei(), main.getNameSeiKana(), main.getNameMeiKana()).getName();
			} else {
				SaibanTojishaBean firstCreated = kyodososhoninData.stream().sorted(Comparator.comparing(SaibanTojishaBean::getCreatedAt)).findFirst().get();
				minjiTojishaLabel += new PersonName(firstCreated.getNameSei(), firstCreated.getNameMei(), firstCreated.getNameSeiKana(), firstCreated.getNameMeiKana()).getName();
			}

			if (kyodososhoninData.size() > 1) {
				minjiTojishaLabel += " 外" + (kyodososhoninData.size() - 1) + "名";
			}
		}

		// 相手方
		List<SaibanTojishaBean> aitegataData = minjiBeans.stream()
				.filter(e -> !SystemFlg.codeToBoolean(e.getDairiFlg()))
				.filter(e -> KanyoshaType.AITEGATA.equalsByCode(e.getKanyoshaType()))
				.collect(Collectors.toList());

		String minjiAitegataLabel = "";
		if (!ListUtils.isEmpty(aitegataData)) {
			if (aitegataData.stream().anyMatch(e -> SystemFlg.codeToBoolean(e.getMainFlg()))) {
				SaibanTojishaBean main = aitegataData.stream().filter(e -> SystemFlg.codeToBoolean(e.getMainFlg())).findFirst().get();
				minjiAitegataLabel += new PersonName(main.getNameSei(), main.getNameMei(), main.getNameSeiKana(), main.getNameMeiKana()).getName();
			} else {
				SaibanTojishaBean firstCreated = aitegataData.stream().sorted(Comparator.comparing(SaibanTojishaBean::getCreatedAt)).findFirst().get();
				minjiAitegataLabel += new PersonName(firstCreated.getNameSei(), firstCreated.getNameMei(), firstCreated.getNameSeiKana(), firstCreated.getNameMeiKana()).getName();
			}

			if (aitegataData.size() > 1) {
				minjiAitegataLabel += " 外" + (aitegataData.size() - 1) + "名";
			}
		}

		tojishaLabelMap.put(SAIBAN_TOJISHA_LABEL_MAP_TOJISHA_KEY, minjiTojishaLabel);
		tojishaLabelMap.put(SAIBAN_TOJISHA_LABEL_MAP_AITEGATA_KEY, minjiAitegataLabel);

		return tojishaLabelMap;
	}

	/**
	 * 刑事裁判の当事者表示名Mapを作成する
	 * 
	 * @param keijiBeans
	 * @return
	 */
	private Map<String, String> generateKeijiSaibanTojishaLabelMap(List<SaibanTojishaBean> keijiBeans) {

		Map<String, String> tojishaLabelMap = new HashMap<>();

		List<SaibanTojishaBean> kyohanshaData = keijiBeans.stream()
				.filter(e -> !SystemFlg.codeToBoolean(e.getDairiFlg()))
				.filter(e -> (e.getKanyoshaType() == null || KanyoshaType.KYOHANSHA.equalsByCode(e.getKanyoshaType())))
				.collect(Collectors.toList());

		if (ListUtils.isEmpty(kyohanshaData)) {
			return tojishaLabelMap;
		}

		String keijiTojishaLabel = "";

		if (kyohanshaData.stream().anyMatch(e -> SystemFlg.codeToBoolean(e.getMainFlg()))) {
			SaibanTojishaBean main = kyohanshaData.stream().filter(e -> SystemFlg.codeToBoolean(e.getMainFlg())).findFirst().get();
			keijiTojishaLabel += new PersonName(main.getNameSei(), main.getNameMei(), main.getNameSeiKana(), main.getNameMeiKana()).getName();
		} else {
			SaibanTojishaBean firstCreated = kyohanshaData.stream().sorted(Comparator.comparing(SaibanTojishaBean::getCreatedAt)).findFirst().get();
			keijiTojishaLabel += new PersonName(firstCreated.getNameSei(), firstCreated.getNameMei(), firstCreated.getNameSeiKana(), firstCreated.getNameMeiKana()).getName();
		}

		if (kyohanshaData.size() > 1) {
			keijiTojishaLabel += " 外" + (kyohanshaData.size() - 1) + "名";
		}

		tojishaLabelMap.put(SAIBAN_TOJISHA_LABEL_MAP_TOJISHA_KEY, keijiTojishaLabel);
		tojishaLabelMap.put(SAIBAN_TOJISHA_LABEL_MAP_AITEGATA_KEY, "");

		return tojishaLabelMap;
	}

	/**
	 * 裁判期日に紐づくデータか判断する
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public boolean existsSaibanLimit(Long saibanSeq) {
		// 紐付けテーブルのデータが存在したら、期日に紐付いているデータ
		List<TSaibanLimitRelationEntity> tSaibanLimitEntities = tSaibanLimitRelationDao.selectBySaibanSeq(saibanSeq);
		if (!ListUtils.isEmpty(tSaibanLimitEntities)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 裁判に顧客が紐付いているかどうか
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public boolean existsSaibanCustomer(Long saibanSeq) {
		// 裁判に顧客が紐付いているかどうか
		List<TSaibanCustomerEntity> tSaibanCustomerEntities = tSaibanCustomerDao.selectBySaibanSeq(saibanSeq);
		if (!ListUtils.isEmpty(tSaibanCustomerEntities)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 裁判に関与者が紐付いているかどうか
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public boolean existsSaibanTreeRelation(Long saibanSeq) {
		// 裁判に関与者が紐付いているかどうか
		TSaibanTreeEntity tSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(saibanSeq);
		if (tSaibanTreeEntity == null) {
			// データがない場合は検証できないのでfalse;
			return false;
		}

		if (tSaibanTreeEntity.getParentSeq() != null) {
			// 対象裁判に対して、親の裁判が存在する
			return true;
		}

		if (SystemFlg.codeToBoolean(tSaibanTreeEntity.getHonsoFlg())) {
			// 対象裁判が、本訴の場合 -> 小裁判が存在する
			return true;
		}
		if (SystemFlg.codeToBoolean(tSaibanTreeEntity.getKihonFlg())) {
			// 対象裁判が、基本事件の場合 -> 小裁判が存在する
			return true;
		}

		return false;
	}

	/**
	 * 対象裁判に紐付けた業務履歴が存在するかどうか
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public boolean existsSaibanGyomuHistory(Long saibanSeq) {
		// 裁判に関与者が紐付いているかどうか
		List<TGyomuHistoryEntity> tGyomuHistoryEntities = tGyomuHistoryDao.selectBySaibanSeq(saibanSeq);
		if (!ListUtils.isEmpty(tGyomuHistoryEntities)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 裁判SEQに紐づく関与者関係者Bean情報を取得する
	 *
	 * @param saibanSeq
	 * @param kanyoshaType
	 * @param systemFlg
	 * @return
	 */
	public List<SaibanRelatedKanyoshaBean> getSaibanRelatedKanyoshaBeanList(Long saibanSeq, KanyoshaType kanyoshaType, SystemFlg systemFlg) {
		return tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaBeanByParams(saibanSeq, kanyoshaType.getCd(), systemFlg.getCd());
	}

	/**
	 * 裁判-関与者関係者の画面表示Dtoリストを作成する
	 * 
	 * @param kanyoshaBeanList
	 * @param dairininKanyoshaBeanList
	 * @return
	 */
	public List<SaibanKanyoshaViewDto> generateKanyoshaViewDto(List<SaibanRelatedKanyoshaBean> kanyoshaBeanList, List<SaibanRelatedKanyoshaBean> dairininKanyoshaBeanList) {

		Set<Long> personSeqSet = kanyoshaBeanList.stream().map(SaibanRelatedKanyoshaBean::getPersonId).collect(Collectors.toSet());
		Set<Long> dairininPersonSeqSet = dairininKanyoshaBeanList.stream().map(SaibanRelatedKanyoshaBean::getPersonId).collect(Collectors.toSet());

		List<Long> margedParsonIdList = LoiozCollectionUtils.mergeLists(new ArrayList<>(personSeqSet), new ArrayList<>(dairininPersonSeqSet));

		List<TPersonAddLawyerEntity> tPersonAddLawyerEntities = tPersonAddLawyerDao.selectCustomerTypeLawyerAddInformationByPersonId(margedParsonIdList);
		Map<Long, TPersonAddLawyerEntity> personIdToPersonAddLawyerEntityMap = tPersonAddLawyerEntities.stream().collect(Collectors.toMap(TPersonAddLawyerEntity::getPersonId, Function.identity()));

		// 関与者SEQをキーとした、代理人情報のマップを作成する
		Map<Long, SaibanKanyoshaDairininViewDto> kanyoshaSeqToDairininMap = dairininKanyoshaBeanList.stream().collect(Collectors.toMap(SaibanRelatedKanyoshaBean::getKanyoshaSeq, e -> {
			SaibanKanyoshaDairininViewDto saibanKanyoshaDairininViewDto = new SaibanKanyoshaDairininViewDto();
			saibanKanyoshaDairininViewDto.setKanyoshaSeq(e.getKanyoshaSeq());
			saibanKanyoshaDairininViewDto.setPersonId(PersonId.of(e.getPersonId()));
			saibanKanyoshaDairininViewDto.setCustomerType(CustomerType.of(e.getCustomerType()));
			saibanKanyoshaDairininViewDto.setPersonAttribute(PersonAttribute.of(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType()));
			saibanKanyoshaDairininViewDto.setKanyoshaName(new PersonName(e.getCustomerNameSei(), e.getCustomerNameMei(), null, null).getName());
			saibanKanyoshaDairininViewDto.setKankei(e.getKankei());
			saibanKanyoshaDairininViewDto.setRemarks(e.getRemarks());
			saibanKanyoshaDairininViewDto.setJimushoName(personIdToPersonAddLawyerEntityMap.getOrDefault(e.getPersonId(), new TPersonAddLawyerEntity()).getJimushoName());
			return saibanKanyoshaDairininViewDto;
		}));

		// 案件関与者関係者情報リストを作成
		List<SaibanKanyoshaViewDto> saibanKanyoshaViewDtoList = kanyoshaBeanList.stream().map(e -> {
			SaibanKanyoshaViewDto saibanKanyoshaViewDto = new SaibanKanyoshaViewDto();
			saibanKanyoshaViewDto.setKanyoshaSeq(e.getKanyoshaSeq());
			saibanKanyoshaViewDto.setSaibanSeq(e.getSaibanSeq());
			saibanKanyoshaViewDto.setPersonId(PersonId.of(e.getPersonId()));
			saibanKanyoshaViewDto.setRelatedKanyoshaSeq(e.getRelatedKanyoshaSeq());
			saibanKanyoshaViewDto.setCustomerId(e.getCustomerId() != null ? CustomerId.of(e.getCustomerId()) : null);
			saibanKanyoshaViewDto.setCustomerType(CustomerType.of(e.getCustomerType()));
			saibanKanyoshaViewDto.setPersonAttribute(PersonAttribute.of(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType()));
			saibanKanyoshaViewDto.setKanyoshaType(e.getKanyoshaType());
			saibanKanyoshaViewDto.setKanyoshaName(new PersonName(e.getCustomerNameSei(), e.getCustomerNameMei(), null, null).getName());
			saibanKanyoshaViewDto.setSaibanTojishaHyoki(e.getSaibanTojishaHyoki());
			saibanKanyoshaViewDto.setMainFlg(SystemFlg.codeToBoolean(e.getMainFlg()));
			saibanKanyoshaViewDto.setDairiFlg(e.getDairiFlg());
			saibanKanyoshaViewDto.setKankei(e.getKankei());
			saibanKanyoshaViewDto.setRemarks(e.getRemarks());
			saibanKanyoshaViewDto.setSaibanKanyoshaDairininViewDto(kanyoshaSeqToDairininMap.get(e.getRelatedKanyoshaSeq()));
			return saibanKanyoshaViewDto;
		}).collect(Collectors.toList());

		return saibanKanyoshaViewDtoList;
	}

	/**
	 * 初期状態の裁判顧客情報を登録する<br>
	 * （初期状態は、案件で登録されていて完了・不受任ステータスでない顧客とする）
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @throws AppException
	 */
	public void registInitSaibanCustomer(Long saibanSeq, Long ankenId) throws AppException {

		// 案件に紐づく顧客一覧を取得
		List<AnkenCustomerRelationBean> ankenCustomerBean = tAnkenCustomerDao.selectRelation(ankenId, null);
		List<Long> ankenCustomerIdList = ankenCustomerBean.stream()
				.filter(bean -> !AnkenStatus.KANRYO.equalsByCode(bean.getAnkenStatus())
						&& !AnkenStatus.FUJUNIN.equalsByCode(bean.getAnkenStatus()))
				.map(bean -> bean.getCustomerId())
				.collect(Collectors.toList());

		// 登録
		this.registSaibanCustomer(saibanSeq, ankenCustomerIdList);
	}

	/**
	 * 初期状態の裁判の関与者情報を登録する。<br>
	 * （裁判の初期の関与者は、案件に登録されている関与者とする） （このメソッドでは、関与者と合わせて、関与者に紐づく代理人情報も登録する）
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @throws AppException
	 */
	public void registInitSaibanRelatedKanyosha(Long saibanSeq, Long ankenId, KanyoshaType kanyoshaType) throws AppException {

		// 案件の関与者情報を取得（代理人は含まない）
		List<AnkenRelatedKanyoshaBean> ankenKanyoshaBeanList = commonAnkenService.getAnkenRelatedKanyoshaBeanList(ankenId, kanyoshaType, SystemFlg.FLG_OFF);

		// 登録する関与者の情報を作成

		// 案件の関与者情報をもとに、初期登録する裁判関与者情報を作成
		List<TSaibanRelatedKanyoshaEntity> insertKanyoshaEntitys = ankenKanyoshaBeanList.stream()
				.map(bean -> {
					TSaibanRelatedKanyoshaEntity insertEntity = new TSaibanRelatedKanyoshaEntity();
					insertEntity.setSaibanSeq(saibanSeq);
					insertEntity.setKanyoshaSeq(bean.getKanyoshaSeq());
					insertEntity.setKanyoshaType(kanyoshaType.getCd());
					insertEntity.setRelatedKanyoshaSeq(bean.getRelatedKanyoshaSeq());
					insertEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
					insertEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());
					return insertEntity;
				})
				.collect(Collectors.toList());

		// 登録する関与者の代理人情報を作成

		// 重複は除去する（同じ関与者IDの代理人データは複数作成しない）
		Set<Long> dairininKanyoshaSeqList = ankenKanyoshaBeanList.stream()
				.filter(bean -> bean.getRelatedKanyoshaSeq() != null)
				.map(bean -> bean.getRelatedKanyoshaSeq())
				.collect(Collectors.toSet());

		List<TSaibanRelatedKanyoshaEntity> insertDairininEntitys = dairininKanyoshaSeqList.stream()
				.map(dairininKanyoshaSeq -> {
					TSaibanRelatedKanyoshaEntity insertEntity = new TSaibanRelatedKanyoshaEntity();
					insertEntity.setSaibanSeq(saibanSeq);
					insertEntity.setKanyoshaSeq(dairininKanyoshaSeq);
					insertEntity.setKanyoshaType(kanyoshaType.getCd());
					insertEntity.setRelatedKanyoshaSeq(null);
					insertEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
					insertEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());
					return insertEntity;
				})
				.collect(Collectors.toList());

		// 関与者と代理人のentityをマージ
		List<TSaibanRelatedKanyoshaEntity> kanyoshaAndDairininEntitys = new ArrayList<>();
		kanyoshaAndDairininEntitys.addAll(insertKanyoshaEntitys);
		kanyoshaAndDairininEntitys.addAll(insertDairininEntitys);

		if (kanyoshaAndDairininEntitys.isEmpty()) {
			// 登録対象が存在しない場合は処理終了
			return;
		}

		int[] insertCount = {};

		try {

			// 裁判関与者を登録
			insertCount = tSaibanRelatedKanyoshaDao.insert(kanyoshaAndDairininEntitys);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

		if (insertCount.length != kanyoshaAndDairininEntitys.size()) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 裁判顧客を登録する
	 * 
	 * @param saibanSeq 対象裁判SEQ
	 * @param insertCustomerIdList 登録顧客IDリスト
	 * @throws AppException
	 */
	public void registSaibanCustomer(Long saibanSeq, List<Long> insertCustomerIdList) throws AppException {

		if (insertCustomerIdList.isEmpty()) {
			return;
		}

		List<TSaibanCustomerEntity> insertEntityList = insertCustomerIdList.stream()
				.map(id -> {
					TSaibanCustomerEntity entity = new TSaibanCustomerEntity();
					entity.setSaibanSeq(saibanSeq);
					entity.setCustomerId(id);
					entity.setMainFlg(SystemFlg.FLG_OFF.getCd());
					return entity;
				})
				.collect(Collectors.toList());

		int[] insertCount = {};

		try {

			// 裁判顧客登録
			insertCount = tSaibanCustomerDao.insert(insertEntityList);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

		if (insertCount.length != insertEntityList.size()) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 裁判顧客を削除する
	 * 
	 * @param saibanSeq
	 * @param customerId
	 * @throws AppException
	 */
	public void deleteSaibanCustomer(Long saibanSeq, Long customerId) throws AppException {

		TSaibanCustomerEntity entity = tSaibanCustomerDao.selectBySaibanSeqAndCustomerId(saibanSeq, customerId);
		if (entity == null) {
			// 既に削除されている -> 楽観ロックエラーとせず、何もしない（削除しようとした裁判顧客が既に削除されていたので削除処理終了とする）
			return;
		}

		// 件数チェック用
		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tSaibanCustomerDao.delete(entity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (deleteCount != 1) {
			// 削除件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 裁判期日削除処理<br>
	 *
	 * <pre>
	 * 裁判情報を削除する時用
	 * </pre>
	 *
	 * @param scheduleSeq 予定SEQ
	 * @param saibanLimitSeq 裁判期日SEQ
	 */
	public void deleteSaibanKijitsuSchedule(Long scheduleSeq, Long saibanLimitSeq) {

		// 削除対象の裁判期日情報
		TSaibanLimitEntity saibanLimitEntity = tSaibanLimitDao.selectBySeq(saibanLimitSeq);

		if (saibanLimitEntity == null) {
			// 裁判期日が存在しない（削除されている）場合は何もしない
			return;
		}

		// 裁判期日の削除
		tSaibanLimitDao.delete(saibanLimitEntity);

		// 裁判期日の紐付けの削除
		List<TSaibanLimitRelationEntity> saibanLimitRelationEntityList = tSaibanLimitRelationDao.selectBySaibanLimitSeq(saibanLimitSeq);
		saibanLimitRelationEntityList.forEach(tSaibanLimitRelationDao::delete);

		// 予定の削除
		if (scheduleSeq != null) {
			scheduleCommonService.delete(scheduleSeq);
		}
	}

	/**
	 * 画面独自のバリデーションチェック（登録）
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	public void saibanScheduleInputFormValidatedForRegist(SaibanScheduleInputForm form, BindingResult result) {
		// 共通のチェック
		this.saibanScheduleFormValidateCommon(form, result);
	}

	/**
	 * 画面独自のバリデーションチェック（更新）
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	public void saibanScheduleInputFormValidatedForUpdate(SaibanScheduleInputForm form, BindingResult result) {
		// 共通のチェック
		this.saibanScheduleFormValidateCommon(form, result);
		this.saibanScheduleFormValidateUpdate(form, result);
	}

	/**
	 * 登録時のDB整合性チェック
	 *
	 * @param form
	 * @param response
	 * @return エラーの有無
	 */
	public boolean acessDBValdateRegistForSchedule(SaibanScheduleInputForm form, Map<String, Object> response) {

		// DB整合性チェック
		Map<String, MessageEnum> errorMsgMap = new HashMap<>();
		boolean error = false;
		final String key = "messageKey";

		// エラーの判定と格納
		this.accessDBValidateForScheduleCommon(form, key, errorMsgMap);

		if (!errorMsgMap.isEmpty()) {
			response.put("succeeded", false);
			response.put("message", messageService.getMessage(errorMsgMap.get(key), SessionUtils.getLocale()));
			error = true;
		}
		return error;
	}

	/**
	 * 更新時のDB整合性チェック
	 *
	 * @param form
	 * @param response
	 * @return エラーの有無
	 */
	public boolean acessDBValdateUpdateForSchedule(SaibanScheduleInputForm form, Map<String, Object> response) {

		// DB整合性チェック
		Map<String, MessageEnum> errorMsgMap = new HashMap<>();
		boolean error = false;
		final String key = "messageKey";

		// エラーの判定と格納
		this.accessDBValidateForScheduleCommon(form, key, errorMsgMap);
		this.accessDBValidateForScheduleUpdate(form, key, errorMsgMap);

		if (!errorMsgMap.isEmpty()) {
			response.put("succeeded", false);
			response.put("message", messageService.getMessage(errorMsgMap.get(key), SessionUtils.getLocale()));
			error = true;
		}
		return error;
	}

	/**
	 * 裁判担当者を登録
	 * 
	 * @param saibanSeq
	 * @param tantoLawyerInputItemList
	 * @param tantoJimuInputItemList
	 */
	public void insertSaibanTanto(Long saibanSeq, List<AnkenTantoSelectInputForm> tantoLawyerInputItemList, List<AnkenTantoSelectInputForm> tantoJimuInputItemList) {

		// 登録処理（担当弁護士）
		this.insertSaibanTanto(saibanSeq, tantoLawyerInputItemList, TantoType.LAWYER);
		// 登録処理（担当事務）
		this.insertSaibanTanto(saibanSeq, tantoJimuInputItemList, TantoType.JIMU);
	}

	/**
	 * 裁判担当者を更新する
	 * 
	 * @param saibanSeq
	 * @param tantoLawyerInputItemList
	 * @param tantoJimuInputItemList
	 */
	public void updateSaibanTanto(Long saibanSeq, List<AnkenTantoSelectInputForm> tantoLawyerInputItemList, List<AnkenTantoSelectInputForm> tantoJimuInputItemList) {

		// 既にDBに登録されている情報
		List<TSaibanTantoEntity> tantoEntityList = tSaibanTantoDao.selectBySaibanSeq(saibanSeq);

		// 担当種別でグループ化
		Map<String, List<TSaibanTantoEntity>> groupedTanto = tantoEntityList.stream()
				.filter(entity -> StringUtils.isNotEmpty(entity.getTantoType()))
				.sorted(Comparator.comparing(TSaibanTantoEntity::getTantoTypeBranchNo))
				.collect(Collectors.groupingBy(TSaibanTantoEntity::getTantoType));

		// 更新処理（担当弁護士）
		this.updateSaibanTanto(saibanSeq, tantoLawyerInputItemList,
				groupedTanto.getOrDefault(TantoType.LAWYER.getCd(), Collections.emptyList()),
				TantoType.LAWYER);
		// 更新処理（担当事務）
		this.updateSaibanTanto(saibanSeq, tantoJimuInputItemList,
				groupedTanto.getOrDefault(TantoType.JIMU.getCd(), Collections.emptyList()),
				TantoType.JIMU);
	}

	// 共通系の処理

	/**
	 * 裁判期日登録処理
	 *
	 * @param saibanSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void registSaibanKijitsuSchedule(Long saibanSeq, SaibanScheduleInputForm inputForm) throws AppException {

		TSaibanEntity entity = tSaibanDao.selectBySeq(saibanSeq);

		if (entity == null) {
			// 裁判情報が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		SaibanScheduleInputForm form = new SaibanScheduleInputForm();
		BeanUtils.copyProperties(inputForm, form);
		form.setAnkenId(entity.getAnkenId());
		form.setSaibanLimit(true);
		form.setSaibanSeq(saibanSeq);
		if (ShutteiType.NOT_REQUIRED == form.getShutteiType()) {
			// 出廷不要の場合、プライバシー設定はデフォルト
			form.setOpenRange(SchedulePermission.ALL);
			form.setEditRange(SchedulePermission.ALL);
		}

		// 裁判期日を登録
		TSaibanLimitEntity saibanLimitEntity = new TSaibanLimitEntity();
		populateScheduleInputFormToSaibanLimitEntity(form).accept(saibanLimitEntity);
		saibanLimitEntity.setLimitDateCount(form.getSaibanLimitCount());
		tSaibanLimitDao.insert(saibanLimitEntity);

		Long saibanLimitSeq = saibanLimitEntity.getSaibanLimitSeq();

		// 裁判と紐づけます
		TSaibanLimitRelationEntity relation = new TSaibanLimitRelationEntity();
		relation.setSaibanSeq(saibanSeq);
		relation.setSaibanLimitSeq(saibanLimitSeq);
		tSaibanLimitRelationDao.insert(relation);

		// 子裁判にも紐づけます
		List<TSaibanEntity> childSaiban = tSaibanDao.selectByParentSaibanSeq(saibanSeq);
		childSaiban.forEach(child -> {
			TSaibanLimitRelationEntity childRelation = new TSaibanLimitRelationEntity();
			childRelation.setSaibanSeq(child.getSaibanSeq());
			childRelation.setSaibanLimitSeq(saibanLimitSeq);
			tSaibanLimitRelationDao.insert(childRelation);
		});

		// 出廷不要でない場合は予定を登録
		form.setSaibanLimitSeq(saibanLimitSeq);
		scheduleCommonService.create(form);
	}

	/**
	 * 裁判期日更新処理
	 *
	 * @param form フォーム入力値
	 * @throws AppException
	 */
	public void updateSaibanKijitsuSchedule(SaibanScheduleInputForm form) throws AppException {

		// 裁判期日を更新
		TSaibanLimitEntity saibanLimitEntity = tSaibanLimitDao.selectBySeq(form.getSaibanLimitSeq());

		if (saibanLimitEntity == null) {
			// 裁判期日が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		populateScheduleInputFormToSaibanLimitEntity(form).accept(saibanLimitEntity);
		tSaibanLimitDao.update(saibanLimitEntity);

		// 予定を更新
		Long scheduleSeq = form.getScheduleSeq();
		ShutteiType shutteiType = form.getShutteiType();
		if (scheduleSeq != null) {
			// 予定が存在する場合
			if (shutteiType != ShutteiType.NOT_REQUIRED) {
				// 出廷不要でなければ、そのまま予定を更新
				scheduleCommonService.update(form);

			} else {
				// 出廷不要の場合は、裁判期日と連動する項目のみ更新(要→不要に変更したケース)
				TScheduleEntity schedule = tScheduleDao.selectBySeq(scheduleSeq);
				schedule.setDateFrom(form.getDateFrom());
				schedule.setTimeFrom(form.getTimeFrom());
				schedule.setRoomId(null);
				schedule.setPlace(form.getPlace());
				schedule.setOpenRange(form.getOpenRange().getCd());
				tScheduleDao.update(schedule);
			}

		} else {
			// 予定が存在しない場合
			if (shutteiType != ShutteiType.NOT_REQUIRED) {
				// 出廷不要でなければ、予定を新規登録(不要→要に変更したケース)
				scheduleCommonService.create(form);

			} else {
				// 出廷不要の場合は何もしない
			}
		}
	}

	/**
	 * 裁判期日削除処理<br>
	 *
	 * @param saibanSeq
	 * @param scheduleSeq
	 * @param saibanLimitSeq
	 * @throws AppException
	 */
	public void deleteSaibanKijitsuSchedule(Long saibanSeq, Long scheduleSeq, Long saibanLimitSeq) throws AppException {

		// ***********************************************
		// 必要な情報の取得
		// ***********************************************
		// 削除対象の裁判期日情報
		TSaibanLimitEntity saibanLimitEntity = tSaibanLimitDao.selectBySeq(saibanLimitSeq);

		if (saibanLimitEntity == null) {
			// 裁判期日が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 項番+1の裁判期日情報
		TSaibanEntity saibanEntity = tSaibanDao.selectBySeq(saibanSeq);
		Long deleteCount = saibanLimitEntity.getLimitDateCount();
		List<TSaibanLimitEntity> updateEntityList = tSaibanLimitDao.selectBySaibanSeqAndLimitDateCount(saibanEntity.getSaibanSeq(), deleteCount);

		// ***********************************************
		// 削除処理
		// ***********************************************
		// 裁判期日
		tSaibanLimitDao.delete(saibanLimitEntity);

		// 裁判期日の紐付け
		List<TSaibanLimitRelationEntity> saibanLimitRelationEntityList = tSaibanLimitRelationDao.selectBySaibanLimitSeq(saibanLimitSeq);
		saibanLimitRelationEntityList.forEach(tSaibanLimitRelationDao::delete);

		// 予定
		if (scheduleSeq != null) {
			scheduleCommonService.delete(scheduleSeq);
		}

		// ***********************************************
		// 更新処理
		// ***********************************************
		if (!ListUtils.isEmpty(updateEntityList)) {

			for (TSaibanLimitEntity updateEntity : updateEntityList) {

				// 現在の回数
				Long orgCount = updateEntity.getLimitDateCount();
				String orgSubjectCount = "第" + orgCount + "回";
				// 新しく設定する回数
				Long newCount = orgCount - 1;
				String newSubjectCount = "第" + newCount + "回";

				// 裁判期日の回数書き換え
				updateEntity.setLimitDateCount(newCount);
				tSaibanLimitDao.update(updateEntity);

				// 予定表の件名書き換え
				TScheduleEntity updateScheduleEntity = tScheduleDao.selectBySaibanLimitSeq(updateEntity.getSaibanLimitSeq());
				String subject = updateScheduleEntity.getSubject();

				if (StringUtils.contains(subject, orgSubjectCount)) {
					// 件名に「第○回」が登録されている場合、書き換え
					String newSubject = subject.replaceAll(orgSubjectCount, newSubjectCount);
					updateScheduleEntity.setSubject(newSubject);
					tScheduleDao.update(updateScheduleEntity);
				}
			}
		}
	}

	/**
	 * 裁判関連情報を削除します。
	 *
	 * @param saibanSeq
	 */
	public void deleteMinjiSaibanRelated(Long saibanSeq) {

		// 子裁判と親裁判の関係を切る
		this.deleteSaibanTree(saibanSeq);

		// 期日関連を削除
		this.deleteSchedule(saibanSeq);

		// 裁判-顧客を削除
		this.deleteSaibanCustomer(saibanSeq);

		// 裁判-関与者関係者を削除
		this.deleteSaibanRelatedKanyosha(saibanSeq);

		// 裁判-担当を削除
		this.deleteSaibanTanto(saibanSeq);

		// 裁判-事件を削除
		this.deleteSaibanJiken(saibanSeq);

		// 業歴のひも付きを外す
		this.deleteSaibanRelateGyomuHistory(saibanSeq);
	}

	/**
	 * 刑事裁判関連情報を削除します。
	 *
	 * @param saibanSeq
	 */
	public void deleteKeijiSaibanRelated(Long saibanSeq) {

		// 期日関連を削除
		this.deleteSchedule(saibanSeq);

		// 刑事裁判付帯情報を削除
		this.deleteSaibanAddKeiji(saibanSeq);

		// 裁判-顧客を削除
		this.deleteSaibanCustomer(saibanSeq);

		// 裁判-関与者関係者を削除
		this.deleteSaibanRelatedKanyosha(saibanSeq);

		// 裁判-担当を削除
		this.deleteSaibanTanto(saibanSeq);

		// 裁判-事件を削除
		this.deleteSaibanJiken(saibanSeq);

		// 業務履歴のひも付きを外す
		this.deleteSaibanRelateGyomuHistory(saibanSeq);
	}

	/**
	 * 口頭弁論期日請書-帳票出力(Wn0008)
	 *
	 * @param response
	 * @param saibanSeq
	 * @param scheduleSeq
	 */
	public void outputKotoBenron(HttpServletResponse response, Long saibanSeq, Long scheduleSeq) {

		// ■1.BuilderとDTOを定義
		Wn0008WordBuider wn0008WordBuider = new Wn0008WordBuider();
		Wn0008WordDto wn0008WordDto = wn0008WordBuider.createNewTargetBuilderDto();

		// ■2.WordConfigを設定
		wn0008WordBuider.setConfig(this.wordConfig);

		// ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定
		this.getWn0008WordDate(saibanSeq, scheduleSeq, wn0008WordDto);
		wn0008WordBuider.setWn0008WordDto(wn0008WordDto);

		try {
			// Excelファイルの出力処理
			wn0008WordBuider.makeWordFile(response);

		} catch (Exception ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, ex);
			throw new RuntimeException("帳票出力エラー", ex);
		}

	}

	/**
	 * Wn0009帳票出力(公判期日請書)
	 *
	 * @param response
	 * @param ankenId
	 * @param branchNumber
	 */
	public void outputKohanKijitsu(HttpServletResponse response, Long saibanSeq, Long scheduleSeq) {

		// ■1.BuilderとDTOを定義
		Wn0009WordBuider wn0009WordBuider = new Wn0009WordBuider();
		Wn0009WordDto wn0009WordDto = wn0009WordBuider.createNewTargetBuilderDto();

		// ■2.WordConfigを設定
		wn0009WordBuider.setConfig(this.wordConfig);

		// ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定
		this.getWn0009WordDate(saibanSeq, scheduleSeq, wn0009WordDto);
		wn0009WordBuider.setWn0009WordDto(wn0009WordDto);

		try {
			// Excelファイルの出力処理
			wn0009WordBuider.makeWordFile(response);

		} catch (Exception ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, ex);
			throw new RuntimeException("帳票出力エラー", ex);
		}

	}

	/**
	 * 指定された代理人（代理人の関与者-関係者情報）について、対象裁判の関与者に代理人として設定されていない場合、<br>
	 * 代理人の関与者-関係者情報を削除する。<br>
	 * ※dairininKanyoshaSeqで取得される関与者-関係者情報が代理人のものではない場合はIllegalArgumentExceptionをスローする
	 * 
	 * <pre>
	 * ■用途
	 * 裁判の関与者（その他当事者、相手方、共犯者など）から代理人を外す（紐付けを解除）する場合に、
	 * 紐付け解除と合わせて、代理人の関係情報の削除を行う場合などにこのメソッドを利用する。
	 * 
	 * 対象の代理人がまだ他の関与者に利用されてる状態の場合は削除は行わない動作としている。
	 * </pre>
	 * 
	 * @param saibanSeq
	 * @param dairininKanyoshaSeq
	 * @throws AppException
	 * @throws IllegalArgumentException
	 */
	public void deleteSaibanRelatedKanyoshaDairininIfNotUsed(Long saibanSeq, Long dairininKanyoshaSeq) throws AppException {

		// 裁判に設定されている関与者（代理人は含まない）
		List<TSaibanRelatedKanyoshaEntity> saibanRelatedKanyoshaEntityList = tSaibanRelatedKanyoshaDao.selectNotDairininBySaibanSeq(saibanSeq);

		// 対象の代理人を利用している関与者が存在するかをチェック
		Optional<TSaibanRelatedKanyoshaEntity> kanyoshaEntityUseDairininOpt = saibanRelatedKanyoshaEntityList.stream()
				.filter(e -> Objects.equals(e.getRelatedKanyoshaSeq(), dairininKanyoshaSeq))
				.findAny();
		if (kanyoshaEntityUseDairininOpt.isPresent()) {
			// 存在する（対象の代理人が利用されている）ので、削除は行わない
			return;
		}

		// 代理人の関係者情報を削除

		TSaibanRelatedKanyoshaEntity dairininEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, dairininKanyoshaSeq);
		if (dairininEntity == null) {
			// 削除対象がすでに存在しない場合は、なにもしない
			return;
		}
		if (!SystemFlg.codeToBoolean(dairininEntity.getDairiFlg())) {
			// 関係情報が代理人のものではない場合
			throw new IllegalArgumentException("引数で指定された dairininKanyoshaSeq のデータは、代理人のものではないため削除不可とする。");
		}

		// 件数チェック用
		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tSaibanRelatedKanyoshaDao.delete(dairininEntity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (deleteCount != 1) {
			// 削除件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 共通-送付書(FAX用)のデータ取得処理
	 *
	 * @param targetSaiban
	 * @param wn0008WordDto
	 * @param wn0009WordDto
	 */
	private void setSouhushoFaxData(TSaibanEntity targetSaiban, Wn0008WordDto wn0008WordDto, Wn0009WordDto wn0009WordDto) {

		MSaibanshoEntity mSaibanshoEntity = null;

		// 裁判所情報のデータ加工
		if (targetSaiban.getSaibanshoId() != null) {
			mSaibanshoEntity = mSaibanshoDao.selectByIdIncludeDeleted(targetSaiban.getSaibanshoId());
			if (mSaibanshoEntity != null) {
				// IDで紐付いていた場合、その裁判所名でデータを上書き(このデータは帳票出力時以外では使用しない想定)
				targetSaiban.setSaibanshoName(mSaibanshoEntity.getSaibanshoName());
			}
		}

		String atesaki2 = CommonConstant.BLANK;
		boolean shokikanFlg = false;
		if (StringUtils.isNotEmpty(targetSaiban.getTantoShoki())) {
			atesaki2 = "ご担当書記官" + CommonConstant.FULL_SPACE + commonChohyoService.addKeishoSama(targetSaiban.getTantoShoki());
			shokikanFlg = true;
		}

		// 宛先に登録がない場合は敬称不要
		String atesaki1 = CommonConstant.BLANK;
		String saibansho = CommonConstant.BLANK;
		String saibanshoName = StringUtils.null2blank(targetSaiban.getSaibanshoName());
		String keizokuBuName = StringUtils.null2blank(targetSaiban.getKeizokuBuName());
		String keizokuKakariName = StringUtils.null2blank(targetSaiban.getKeizokuKakariName());
		if (!StringUtils.isAllEmpty(saibanshoName, keizokuBuName, keizokuKakariName)) {
			// 書記官が空欄の場合は裁判所に御中を挿入
			atesaki1 = saibanshoName + keizokuBuName + keizokuKakariName;
			saibansho = commonChohyoService.addKeishoOnchu(atesaki1);
			if (!shokikanFlg) {
				// 御中を付与
				atesaki1 = commonChohyoService.addKeishoOnchu(atesaki1);

			}
		}

		// *************************************
		// データの設定
		// *************************************
		if (wn0008WordDto != null) {
			// 口頭弁論期日請書（Wn0008）の場合

			wn0008WordDto.setFaxNo(StringUtils.null2blank(targetSaiban.getKeizokuBuFaxNo()));
			wn0008WordDto.setAtesaki1(atesaki1);
			wn0008WordDto.setAtesaki2(atesaki2);
			wn0008WordDto.setSaibansho(saibansho);

			// 事務所情報の取得
			MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
			wn0008WordDto.setTenantZipCode(StringUtils.null2blank(mTenantEntity.getTenantZipCd()));
			wn0008WordDto.setTenantAddress1(StringUtils.null2blank(mTenantEntity.getTenantAddress1()));
			wn0008WordDto.setTenantAddress2(StringUtils.null2blank(mTenantEntity.getTenantAddress2()));
			wn0008WordDto.setTenantName(StringUtils.null2blank(mTenantEntity.getTenantName()));
			wn0008WordDto.setTenantTelNo(StringUtils.null2blank(mTenantEntity.getTenantTelNo()));
			wn0008WordDto.setTenantFaxNo(StringUtils.null2blank(mTenantEntity.getTenantFaxNo()));

		} else if (wn0009WordDto != null) {
			// 公判期日請書（Wn0009）の場合

			wn0009WordDto.setFaxNo(StringUtils.null2blank(targetSaiban.getKeizokuBuFaxNo()));
			wn0009WordDto.setAtesaki1(atesaki1);
			wn0009WordDto.setAtesaki2(atesaki2);
			wn0009WordDto.setSaibansho(saibansho);

			// 事務所情報の取得
			MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
			wn0009WordDto.setTenantZipCode(StringUtils.null2blank(mTenantEntity.getTenantZipCd()));
			wn0009WordDto.setTenantAddress1(StringUtils.null2blank(mTenantEntity.getTenantAddress1()));
			wn0009WordDto.setTenantAddress2(StringUtils.null2blank(mTenantEntity.getTenantAddress2()));
			wn0009WordDto.setTenantName(StringUtils.null2blank(mTenantEntity.getTenantName()));
			wn0009WordDto.setTenantTelNo(StringUtils.null2blank(mTenantEntity.getTenantTelNo()));
			wn0009WordDto.setTenantFaxNo(StringUtils.null2blank(mTenantEntity.getTenantFaxNo()));

		}

	}

	/**
	 * メインとなる事件情報の事件名を取得する
	 * 
	 * @param tSaibanJikenEntities
	 * @return
	 */
	public String getMainJikenName(List<TSaibanJikenEntity> tSaibanJikenEntities) {
		return Optional.ofNullable(getMainJikenEntity(tSaibanJikenEntities))
				.map(TSaibanJikenEntity::getJikenName)
				.orElse("");
	}

	/**
	 * メインとなる事件情報の事件番号を取得する
	 * 
	 * @param tSaibanJikenEntities
	 * @return
	 */
	public CaseNumber getMainCaseNumber(List<TSaibanJikenEntity> tSaibanJikenEntities) {
		return Optional.ofNullable(getMainJikenEntity(tSaibanJikenEntities))
				.map(CaseNumber::fromEntity)
				.orElse(null);
	}

	/**
	 * メインとなる事件情報を取得する
	 * 
	 * @param tSaibanJikenEntities
	 * @return
	 */
	public TSaibanJikenEntity getMainJikenEntity(List<TSaibanJikenEntity> tSaibanJikenEntities) {
		return tSaibanJikenEntities.stream()
				.sorted(Comparator.comparing(TSaibanJikenEntity::getJikenSeq)) // 事件SEQが最初の物がメインとなる
				.findFirst().orElse(null);
	}

	/**
	 * サブ(追起訴)となる事件情報を取得する
	 * 
	 * @param tSaibanJikenEntities
	 * @return
	 */
	public List<TSaibanJikenEntity> getSubJikenEntities(List<TSaibanJikenEntity> tSaibanJikenEntities) {

		return tSaibanJikenEntities.stream()
				.sorted(Comparator.comparing(TSaibanJikenEntity::getJikenSeq)) // 事件SEQが最初の物がメインとなる
				.skip(1) // メイン事件をストリームから除外する
				.sorted(getSubJikenSortCondition())
				.collect(Collectors.toList());
	}

	/**
	 * 裁判 追起訴のソート順
	 * 
	 * @return
	 */
	public Comparator<TSaibanJikenEntity> getSubJikenSortCondition() {

		// 追起訴のソート条件1 -> 元号
		Function<TSaibanJikenEntity, Long> jikenGengoMapper = e -> LoiozNumberUtils.parseAsLong(e.getJikenGengo());
		Comparator<TSaibanJikenEntity> jikenGengoSort = Comparator.comparing(jikenGengoMapper);

		// 追起訴のソート条件2 -> 年度
		Function<TSaibanJikenEntity, Long> jikenYearMapper = e -> LoiozNumberUtils.parseAsLong(e.getJikenYear());
		Comparator<TSaibanJikenEntity> jikenYearSort = Comparator.comparing(jikenYearMapper);

		// 追起訴のソート条件3 -> 事件番号
		Comparator<TSaibanJikenEntity> jikenNoSort = Comparator.comparing(TSaibanJikenEntity::getJikenNo);

		// 追起訴のソート条件4 ->事件名
		Comparator<TSaibanJikenEntity> jikenNameSort = Comparator.comparing(TSaibanJikenEntity::getJikenName);

		return jikenGengoSort.thenComparing(jikenYearSort).thenComparing(jikenNoSort).thenComparing(jikenNameSort);
	}

	// =========================================================================
	// privateメソッド
	// =========================================================================

	/**
	 * 裁判に登録されている担当弁護士、担当事務の情報を格納したMapを取得する。
	 * 
	 * <pre>
	 * ※Map内の各エントリーのListの要素数は担当の登録上限数で固定となり、登録データが無い場合は、 初期状態のTantoデータが設定されている状態となる。
	 * 
	 * ■isForEditがtrueの場合 現在登録されている担当弁護士、担当事務の情報をもとにデータを作成する。
	 * 
	 * ■isForEditがfalseの場合 案件に登録されている担当弁護士、担当事務の情報をもとに初期状態のデータを作成する。
	 * 
	 * <pre>
	 * 
	 * @param saibanSeq isForEditがfalseの場合はNULLを許可（利用しない値となる）
	 * @param ankenId isForEditがtrueの場合はNULLを許可（利用しない値となる）
	 * @param isForEdit
	 * @return
	 */
	private Map<TantoType, List<AnkenTantoSelectInputForm>> getTantoListMap(Long saibanSeq, Long ankenId, boolean isForEdit) {

		List<AnkenTantoSelectInputForm> tantoLawyerList = null;
		List<AnkenTantoSelectInputForm> tantoJimuList = null;

		if (!isForEdit) {
			// 新規の場合（既存の裁判担当のデータが存在する可能性がない場合）

			// 案件から担当情報を取得
			List<AnkenTantoSelectInputForm> initSaibanTantoLawyerList = this.getInitSaibanTanto(TantoType.LAWYER, ankenId);
			List<AnkenTantoSelectInputForm> initSaibanTantoJimuList = this.getInitSaibanTanto(TantoType.JIMU, ankenId);

			// 担当弁護士
			tantoLawyerList = this.addEmptyDataToTantoList(initSaibanTantoLawyerList);
			// 担当事務
			tantoJimuList = this.addEmptyDataToTantoList(initSaibanTantoJimuList);
		} else {
			// 編集の場合（既存の裁判担当のデータが存在する可能性がある場合）

			// 裁判担当を取得
			List<SaibanTantoBean> saibanTantoBeanList = tSaibanTantoDao.selectBySeqJoinAccount(saibanSeq);

			// 裁判担当者を設定
			Map<String, List<SaibanTantoBean>> groupedTanto = saibanTantoBeanList.stream()
					.filter(bean -> StringUtils.isNotEmpty(bean.getTantoType()))
					.sorted(Comparator.comparing(SaibanTantoBean::getTantoTypeBranchNo))
					.collect(Collectors.groupingBy(SaibanTantoBean::getTantoType));

			// 担当弁護士
			tantoLawyerList = this.createTantoList(groupedTanto.getOrDefault(TantoType.LAWYER.getCd(), Collections.emptyList()));
			// 担当事務
			tantoJimuList = this.createTantoList(groupedTanto.getOrDefault(TantoType.JIMU.getCd(), Collections.emptyList()));
		}

		//
		// 担当情報をMapに格納して返却
		//

		Map<TantoType, List<AnkenTantoSelectInputForm>> tantoListMap = new HashMap<>();

		tantoListMap.put(TantoType.LAWYER, tantoLawyerList);
		tantoListMap.put(TantoType.JIMU, tantoJimuList);

		return tantoListMap;
	}

	/**
	 * 裁判の担当のフォーム情報を作成する
	 *
	 * @param beanList 裁判担当DB取得値
	 * @return 担当フォーム情報
	 */
	private List<AnkenTantoSelectInputForm> createTantoList(List<SaibanTantoBean> beanList) {

		// DB取得値をフォーム情報に変換
		List<AnkenTantoSelectInputForm> itemList = beanList.stream()
				.map(bean -> new AnkenTantoSelectInputForm(bean.getAccountSeq(), SystemFlg.codeToBoolean(bean.getSaibanMainTantoFlg())))
				.filter(item -> !item.isEmpty())
				.collect(Collectors.toCollection(ArrayList::new));

		return this.addEmptyDataToTantoList(itemList);
	}

	/**
	 * 担当リストに空データを追加する<br>
	 * ※既存の画面側の都合でリストが指定の要素数になるまでデータを追加しなければいけないため
	 * 
	 * @param tantoList
	 * @return
	 */
	private List<AnkenTantoSelectInputForm> addEmptyDataToTantoList(List<AnkenTantoSelectInputForm> tantoList) {

		// 担当追加上限まで空データを追加
		for (int i = tantoList.size(); i < CommonConstant.ANKEN_TANTO_ADD_LIMIT; i++) {
			tantoList.add(new AnkenTantoSelectInputForm());
		}

		return tantoList;
	}

	/**
	 * 裁判の担当弁護士、担当事務の選択肢情報を格納したMapを取得する。
	 * 
	 * <pre>
	 * ■isForEditがtrueの場合 選択肢情報には、現在案件に登録されている担当と、現在裁判に登録されている担当を合わせた情報を格納する。
	 * （つまり、裁判側で登録された担当が、案件側で削除された場合も、その担当情報を返却結果に含む。）
	 * 
	 * ■isForEditがfalseの場合 選択肢情報には、現在案件に登録されている担当情報のみを格納する。
	 * 
	 * <pre>
	 * 
	 * @param saibanSeq isForEditがfalseの場合はNULLを許可（利用しない値となる）
	 * @param ankenId NULLは許可しない
	 * @param isForEdit
	 * @return
	 */
	private Map<TantoType, List<SelectOptionForm>> getTantoSelectOptListMap(Long saibanSeq, Long ankenId, boolean isForEdit) {

		List<SelectOptionForm> resultLawyerOptionList = null;
		List<SelectOptionForm> resultJimuOptionList = null;

		//
		// 裁判の担当の候補を取得（案件に登録されている担当）
		//

		// 担当弁護士プルダウン
		List<SelectOptionForm> lawyerOptionList = this.getSaibanTantoOptForm(TantoType.LAWYER, ankenId);
		// 担当事務プルダウン
		List<SelectOptionForm> tantoJimuOptionList = this.getSaibanTantoOptForm(TantoType.JIMU, ankenId);

		if (!isForEdit) {
			// 新規の場合（既存の裁判担当のデータが存在する可能性がない場合）

			resultLawyerOptionList = lawyerOptionList;
			resultJimuOptionList = tantoJimuOptionList;
		} else {
			// 編集の場合（既存の裁判担当のデータが存在する可能性がある場合）

			//
			// 裁判の担当を取得（現在裁判に登録されている担当）
			//

			// 裁判担当情報を取得
			List<SaibanTantoBean> saibanTantoBeanList = tSaibanTantoDao.selectBySeqJoinAccount(saibanSeq);

			// 裁判担当情報を担当弁護士、担当事務ごとにMap化
			Map<String, List<SaibanTantoBean>> groupedTanto = saibanTantoBeanList.stream()
					.filter(bean -> StringUtils.isNotEmpty(bean.getTantoType()))
					.sorted(Comparator.comparing(SaibanTantoBean::getTantoTypeBranchNo))
					.collect(Collectors.groupingBy(SaibanTantoBean::getTantoType));

			// 裁判担当
			Stream<SelectOptionForm> saibanTantoLawyerStream = groupedTanto.getOrDefault(TantoType.LAWYER.getCd(), Collections.emptyList()).stream()
					.map(bean -> {
						return new SelectOptionForm(bean.getAccountSeq(), CommonUtils.nameFormat(bean.getAccountNameSei(), bean.getAccountNameMei()));
					});
			// 裁判事務
			Stream<SelectOptionForm> saibanJimuSelectOptions = groupedTanto.getOrDefault(TantoType.JIMU.getCd(), Collections.emptyList()).stream()
					.map(bean -> {
						return new SelectOptionForm(bean.getAccountSeq(), CommonUtils.nameFormat(bean.getAccountNameSei(), bean.getAccountNameMei()));
					});

			//
			// 案件に登録されている担当と、現在裁判に登録されている担当をマージ
			//

			List<SelectOptionForm> mergedLawyerOptionList = Stream.concat(lawyerOptionList.stream(), saibanTantoLawyerStream)
					.distinct()
					.collect(Collectors.toList());
			List<SelectOptionForm> mergedJimuOptionList = Stream.concat(tantoJimuOptionList.stream(), saibanJimuSelectOptions)
					.distinct()
					.collect(Collectors.toList());

			resultLawyerOptionList = mergedLawyerOptionList;
			resultJimuOptionList = mergedJimuOptionList;
		}

		//
		// 担当情報をMapに格納して返却
		//

		Map<TantoType, List<SelectOptionForm>> tantoSelectOptListMap = new HashMap<>();

		tantoSelectOptListMap.put(TantoType.LAWYER, resultLawyerOptionList);
		tantoSelectOptListMap.put(TantoType.JIMU, resultJimuOptionList);

		return tantoSelectOptListMap;
	}

	/**
	 * 裁判登録画面の担当情報の取得処理<br>
	 * （現在案件に登録されている担当を取得する）
	 * 
	 * @param tantoType 種別
	 * @param ankenId 案件ID
	 * @return
	 */
	private List<AnkenTantoSelectInputForm> getInitSaibanTanto(TantoType tantoType, Long ankenId) {
		List<MAccountEntity> mAccountEntities = mAccountDao.selectEnabledAccount();
		Map<Long, String> accountNameMap = mAccountEntities.stream()
				.collect(Collectors.toMap(
						MAccountEntity::getAccountSeq,
						data -> PersonName.fromEntity(data).getName()));
		List<TAnkenTantoEntity> tAnkenTantoEntities = tAnkenTantoDao.selectByAnkenId(ankenId);
		return tAnkenTantoEntities.stream()
				.filter(entity -> tantoType.equalsByCode(entity.getTantoType()))
				.filter(entity -> {
					// 取得できないは無効アカウントなのでOptionから排除する
					return !StringUtils.isEmpty(accountNameMap.get(entity.getAccountSeq()));
				})
				.map(entity -> new AnkenTantoSelectInputForm(entity.getAccountSeq(), SystemFlg.codeToBoolean(entity.getAnkenMainTantoFlg())))
				.collect(Collectors.toList());
	}

	/**
	 * 裁判管理画面の担当選択肢の取得処理<br>
	 * （現在案件に登録されている担当を取得する）
	 * 
	 * @param tantoType 種別
	 * @param ankenId 案件ID
	 * @return
	 */
	private List<SelectOptionForm> getSaibanTantoOptForm(TantoType tantoType, Long ankenId) {
		List<MAccountEntity> mAccountEntities = mAccountDao.selectEnabledAccount();
		Map<Long, String> accountNameMap = mAccountEntities.stream()
				.collect(Collectors.toMap(
						MAccountEntity::getAccountSeq,
						data -> PersonName.fromEntity(data).getName()));
		List<TAnkenTantoEntity> tAnkenTantoEntities = tAnkenTantoDao.selectByAnkenId(ankenId);
		return tAnkenTantoEntities.stream()
				.filter(entity -> tantoType.equalsByCode(entity.getTantoType()))
				.filter(entity -> {
					// 取得できないは無効アカウントなのでOptionから排除する
					return !StringUtils.isEmpty(accountNameMap.get(entity.getAccountSeq()));
				}).map(entity -> {
					return new SelectOptionForm(entity.getAccountSeq(), accountNameMap.get(entity.getAccountSeq()));
				}).collect(Collectors.toList());
	}

	/**
	 * 裁判期日関連の情報を削除します。
	 *
	 * @param saibanSeq 裁判SEQ
	 */
	private void deleteSchedule(Long saibanSeq) {

		// -------------------------------------------------
		// 裁判-期日情報
		// -------------------------------------------------
		List<Long> limitSeqList = new ArrayList<Long>();
		List<TSaibanLimitEntity> limitEntityList = tSaibanLimitDao.selectBySaibanSeq(saibanSeq);

		if (!ListUtils.isEmpty(limitEntityList)) {
			for (TSaibanLimitEntity limit : limitEntityList) {
				// 裁判期日SEQリストを設定します。
				limitSeqList.add(limit.getSaibanLimitSeq());
			}
		}
		// -------------------------------------------------
		// 予定表
		// -------------------------------------------------
		List<TScheduleEntity> scheduleList = tScheduleDao.selectBySaibanLimitSeqList(limitSeqList);

		if (!ListUtils.isEmpty(scheduleList)) {
			for (TScheduleEntity schedule : scheduleList) {
				// 裁判期日・期日の紐づけ・予定表を削除します。
				this.deleteSaibanKijitsuSchedule(schedule.getScheduleSeq(), schedule.getSaibanLimitSeq());
			}
		}
	}

	/**
	 * 裁判に紐づく業務履歴のひも付きを外す
	 *
	 * @param saibanSeq 裁判SEQ
	 */
	private void deleteSaibanRelateGyomuHistory(Long saibanSeq) {

		List<TGyomuHistoryEntity> relateGyomuHistory = tGyomuHistoryDao.selectBySaibanSeq(saibanSeq);
		for (TGyomuHistoryEntity entity : relateGyomuHistory) {
			entity.setSaibanSeq(null);
		}

		// 削除します。
		tGyomuHistoryDao.update(relateGyomuHistory);
	}

	/**
	 * 裁判-事件情報を削除します。
	 *
	 * @param saibanSeq 裁判SEQ
	 */
	private void deleteSaibanJiken(Long saibanSeq) {

		List<TSaibanJikenEntity> jikenList = tSaibanJikenDao.selectBySaibanSeq(saibanSeq);

		// 削除します。
		tSaibanJikenDao.delete(jikenList);
	}

	/**
	 * 裁判-担当情報を削除します。
	 *
	 * @param saibanSeq 裁判SEQ
	 */
	private void deleteSaibanTanto(Long saibanSeq) {

		List<TSaibanTantoEntity> tantoList = tSaibanTantoDao.selectBySaibanSeq(saibanSeq);

		// 削除します。
		tSaibanTantoDao.delete(tantoList);
	}

	/**
	 * 裁判-顧客情報を削除します。
	 *
	 * @param saibanSeq 裁判SEQ
	 */
	private void deleteSaibanCustomer(Long saibanSeq) {

		List<TSaibanCustomerEntity> customerList = tSaibanCustomerDao.selectBySaibanSeq(saibanSeq);

		// 削除します。
		tSaibanCustomerDao.delete(customerList);
	}

	/**
	 * 裁判-関与者関係者情報を削除します。
	 *
	 * @param saibanSeq 裁判SEQ
	 */
	private void deleteSaibanRelatedKanyosha(Long saibanSeq) {

		List<TSaibanRelatedKanyoshaEntity> relatedKanyoshaList = tSaibanRelatedKanyoshaDao.selectBySaibanSeq(saibanSeq);

		// 削除します。
		tSaibanRelatedKanyoshaDao.delete(relatedKanyoshaList);
	}

	/**
	 * 裁判ツリー削除
	 *
	 * @param saibanSeq 削除する裁判SEQ
	 */
	private void deleteSaibanTree(Long selectedSaibanSeq) {

		// 裁判情報を取得します。
		TSaibanTreeEntity saibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(selectedSaibanSeq);

		// 親と自分の関係を切る
		if (saibanTreeEntity != null && saibanTreeEntity.getParentSeq() != null) {
			disConnectSaiban(saibanTreeEntity.getParentSeq(), selectedSaibanSeq);
		}

		// 子裁判の情報を全て取得します。
		List<TSaibanTreeEntity> childSaibanTreeEntities = tSaibanTreeDao.selectChildBySaibanSeq(selectedSaibanSeq);
		// 子供を独立させる
		if (!childSaibanTreeEntities.isEmpty()) {
			childSaibanTreeEntities.forEach(entity -> {
				entity.setParentSeq(null);
				entity.setConnectType(null);
				relatedScheduleCopy(entity.getSaibanSeq());
				tSaibanTreeDao.update(entity);
			});
		}

		// 自分は削除
		TSaibanTreeEntity selectedSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(selectedSaibanSeq);
		tSaibanTreeDao.delete(selectedSaibanTreeEntity);
	}

	/**
	 * 裁判ツリー情報を更新(結び付けを外す)します
	 *
	 * @param parentSaibanSeq
	 * @param childSaibanSeq
	 */
	private void disConnectSaiban(Long parentSaibanSeq, Long childSaibanSeq) {

		// DB情報の取得
		TSaibanTreeEntity parentSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(parentSaibanSeq);
		TSaibanTreeEntity childSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(childSaibanSeq);

		// データ取得チェック
		if (parentSaibanTreeEntity == null || childSaibanTreeEntity == null) {
			throw new DataNotFoundException("裁判情報が取得できませんでした。");
		}

		// 他に子供の裁判が存在するかを確認します。
		List<TSaibanTreeEntity> childSaibanList = tSaibanTreeDao.selectChildBySaibanSeq(parentSaibanSeq);
		List<TSaibanTreeEntity> childSaibanTreeEntities = childSaibanList.stream()
				.filter(entity -> !Objects.equals(entity.getSaibanTreeSeq(), childSaibanTreeEntity.getSaibanTreeSeq()))
				.collect(Collectors.toList());
		boolean existsHeigo = childSaibanTreeEntities.stream().anyMatch(entity -> HeigoHansoType.HEIGO.equalsByCode(entity.getConnectType()));
		boolean existsHanso = childSaibanTreeEntities.stream().anyMatch(entity -> HeigoHansoType.HANSO.equalsByCode(entity.getConnectType()));

		// 一件でも存在する場合はflgONで設定する
		parentSaibanTreeEntity.setKihonFlg(SystemFlg.booleanToCode(existsHeigo));
		parentSaibanTreeEntity.setHonsoFlg(SystemFlg.booleanToCode(existsHanso));

		// 紐づけを外す裁判の更新データを設定
		childSaibanTreeEntity.setParentSeq(null);
		childSaibanTreeEntity.setConnectType(null);

		// 親裁判に関連する予定は切り離すときにコピーする
		relatedScheduleCopy(childSaibanTreeEntity.getSaibanSeq());

		// 更新処理
		tSaibanTreeDao.update(parentSaibanTreeEntity);
		tSaibanTreeDao.update(childSaibanTreeEntity);
	}

	/**
	 * 子裁判の裁判期日をコピーする処理
	 *
	 * @param childSaibanSeq
	 */
	private void relatedScheduleCopy(Long childSaibanSeq) {

		// 紐づきを外したい裁判の裁判-関連裁判情報を取得する
		List<TSaibanLimitRelationEntity> tSaibanLimitRelationEntities = tSaibanLimitRelationDao
				.selectBySaibanSeq(childSaibanSeq);

		// 紐づいている裁判期日がない場合は、何もせずに返却
		if (tSaibanLimitRelationEntities.isEmpty()) {
			return;
		}

		// 複合主キーデータのmapper条件
		Function<TSaibanLimitRelationEntity, Keys<Long, Long>> twoKeysMapper = entity -> {
			return new Keys<>(entity.getSaibanSeq(), entity.getSaibanLimitSeq());
		};

		// 複合主キーMap
		Map<Keys<Long, Long>, TSaibanLimitRelationEntity> twoKeysMap = tSaibanLimitRelationEntities.stream()
				.collect(Collectors.toMap(
						twoKeysMapper,
						Function.identity(),
						(former, later) -> later));

		// 取得した裁判期日Seqのリスト作成
		List<Long> saibanLimitSeqList = tSaibanLimitRelationEntities.stream()
				.map(TSaibanLimitRelationEntity::getSaibanLimitSeq)
				.collect(Collectors.toList());

		// 作成した裁判期日Seqリストをキーとして、裁判-期日情報Mapを作成します。
		List<TSaibanLimitEntity> tSaibanLimitEntities = tSaibanLimitDao.selectBySeq(saibanLimitSeqList);
		Map<Long, TSaibanLimitEntity> saibanLimitMap = tSaibanLimitEntities.stream()
				.collect(Collectors.toMap(
						TSaibanLimitEntity::getSaibanLimitSeq,
						Function.identity(),
						(former, latter) -> former));

		// 作成した裁判期日Seqリストをキーとして、予定表データを取得します
		List<TScheduleEntity> tScheduleEntities = tScheduleDao.selectBySaibanLimitSeqList(saibanLimitSeqList);

		// 親裁判に紐づいている予定をList化します。
		List<TScheduleEntity> unConnectSchedules = tScheduleEntities.stream()
				.filter(entity -> {
					return !Objects.equals(entity.getSaibanSeq(), childSaibanSeq);
				})
				.collect(Collectors.toList());

		// コピーするスケジュールに紐づく裁判-アカウント情報を取得します。@key:scheduleSeq
		List<Long> scheduleSeqList = unConnectSchedules.stream()
				.map(TScheduleEntity::getScheduleSeq)
				.collect(Collectors.toList());
		List<TScheduleAccountEntity> unConnectScheduleAccount = tScheduleAccountDao.selectByScheduleSeq(scheduleSeqList);
		Map<Long, List<TScheduleAccountEntity>> unConnectScheduleMap = unConnectScheduleAccount.stream()
				.collect(Collectors.groupingBy(
						TScheduleAccountEntity::getScheduleSeq));

		// 過去日の物だけコピーを作成(未来日の場合)
		for (TScheduleEntity entity : unConnectSchedules) {

			// 予定をコピーしない場合は関連情報を削除
			Keys<Long, Long> keys = new Keys<>(childSaibanSeq, entity.getSaibanLimitSeq());
			Optional.ofNullable(twoKeysMap.get(keys)).ifPresent(tSaibanLimitRelationDao::delete);

			// 分離の時は過去日の物だけコピーを作成
			LocalDateTime limitDate = LocalDateTime.of(entity.getDateTo(), entity.getTimeTo());
			if (limitDate.isAfter(LocalDateTime.now())) {
				// 条件：予定終了日時 > 現在日時の場合は、後続処理を行わない。
				continue;
			}

			// ＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
			// ■以下の処理は、スケジュールのコピーを行うDB更新ロジック
			// ＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊

			// 裁判期日情報を新たに作成(コピーして、不要項目をnullにする)
			TSaibanLimitEntity tSaibanLimitEntity = saibanLimitMap.get(entity.getSaibanLimitSeq());
			TSaibanLimitEntity tSaibanLimitInsertEntity = new TSaibanLimitEntity();
			BeanUtils.copyProperties(tSaibanLimitEntity, tSaibanLimitInsertEntity);
			tSaibanLimitInsertEntity.setSaibanLimitSeq(null);
			tSaibanLimitInsertEntity.setCreatedAt(null);
			tSaibanLimitInsertEntity.setCreatedBy(null);
			tSaibanLimitInsertEntity.setUpdatedAt(null);
			tSaibanLimitInsertEntity.setUpdatedBy(null);
			tSaibanLimitInsertEntity.setVersionNo(null);
			tSaibanLimitDao.insert(tSaibanLimitInsertEntity);

			// 裁判期日紐づき情報を新たに作成(↑で登録した裁判期日SEQと削除したデータの裁判SEQを設定)
			TSaibanLimitRelationEntity tSaibanLimitRelationInsertEntity = new TSaibanLimitRelationEntity();
			tSaibanLimitRelationInsertEntity.setSaibanLimitSeq(tSaibanLimitInsertEntity.getSaibanLimitSeq());
			tSaibanLimitRelationInsertEntity.setSaibanSeq(childSaibanSeq);
			tSaibanLimitRelationDao.insert(tSaibanLimitRelationInsertEntity);

			// スケジュールをコピー(裁判SEQと裁判期日SEQは、紐づくデータは子裁判の情報に変更)
			TScheduleEntity tScheduleEntity = new TScheduleEntity();
			BeanUtils.copyProperties(entity, tScheduleEntity);
			tScheduleEntity.setScheduleSeq(null);
			tScheduleEntity.setCreatedAt(null);
			tScheduleEntity.setCreatedBy(null);
			tScheduleEntity.setUpdatedAt(null);
			tScheduleEntity.setUpdatedBy(null);
			tScheduleEntity.setVersionNo(null);
			tScheduleEntity.setSaibanSeq(childSaibanSeq);
			tScheduleEntity.setSaibanLimitSeq(tSaibanLimitInsertEntity.getSaibanLimitSeq());
			tScheduleDao.insert(tScheduleEntity);

			// スケジュールの子テーブルも登録を行う
			List<TScheduleAccountEntity> tScheduleAccountEntities = unConnectScheduleMap.get(entity.getScheduleSeq());
			if (!CollectionUtils.isEmpty(tScheduleAccountEntities)) {
				for (TScheduleAccountEntity childEntity : tScheduleAccountEntities) {
					TScheduleAccountEntity tScheduleAccountEntitiy = new TScheduleAccountEntity();
					tScheduleAccountEntitiy.setScheduleSeq(tScheduleEntity.getScheduleSeq());
					tScheduleAccountEntitiy.setAccountSeq(childEntity.getAccountSeq());
					tScheduleAccountDao.insert(tScheduleAccountEntitiy);
				}
			}
		}
	}

	/**
	 * 刑事裁判付帯情報を削除します。
	 *
	 * @param saibanSeq 裁判SEQ
	 */
	private void deleteSaibanAddKeiji(Long saibanSeq) {

		TSaibanAddKeijiEntity addKeijiEntity = tSaibanAddKeijiDao.selectBySaibanSeq(saibanSeq);
		if (addKeijiEntity != null) {
			// 削除します。
			tSaibanAddKeijiDao.delete(addKeijiEntity);
		}
	}

	/**
	 * EntityにDtoの値を設定して更新する
	 * 
	 * <pre>
	 * 引数の2つのリスト（DtoとEntity）の要素数が同じでなければ実行不可とする。（IllegalArgumentExceptionをスローする。）
	 * </pre>
	 * 
	 * @param saibankanDtoList
	 * @param saibanSaibankanEntityList
	 * @throws AppException
	 * @throws IllegalArgumentException
	 */
	private void updateSaibankanEntityWithDtoVal(List<SaibankanDto> saibankanDtoList, List<TSaibanSaibankanEntity> saibanSaibankanEntityList) throws AppException {

		if (saibankanDtoList == null || saibanSaibankanEntityList == null) {
			throw new IllegalArgumentException("引数はNULLは許可しません");
		}

		int dtoCount = saibankanDtoList.size();
		int entityCount = saibanSaibankanEntityList.size();
		if (dtoCount != entityCount) {
			throw new IllegalArgumentException("リストの要素数は同じでなければいけません");
		}

		LoiozCollectionUtils.zipping(
				saibankanDtoList, saibanSaibankanEntityList,
				(dto, entity) -> {
					entity.setSaibankanName(dto.getSaibankanName());
				});

		// 更新
		this.updateSaibankanEntity(saibanSaibankanEntityList);
	}

	/**
	 * 裁判官情報を登録する
	 * 
	 * @param saibanSaibankanEntityList
	 * @throws AppException
	 */
	private void insertSaibankanEntity(List<TSaibanSaibankanEntity> saibanSaibankanEntityList) throws AppException {

		int[] insertCount = {};

		try {

			insertCount = tSaibanSaibankanDao.insert(saibanSaibankanEntityList);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

		if (insertCount.length != saibanSaibankanEntityList.size()) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 裁判官情報を更新する
	 * 
	 * @param saibanSaibankanEntityList
	 * @throws AppException
	 */
	private void updateSaibankanEntity(List<TSaibanSaibankanEntity> saibanSaibankanEntityList) throws AppException {

		// 更新件数
		int[] resultArray = null;

		try {
			// 更新
			resultArray = tSaibanSaibankanDao.update(saibanSaibankanEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00036, ex);
		}

		if (resultArray.length != saibanSaibankanEntityList.size()) {
			// 実際の更新件数と更新対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00036, null);
		}
	}

	/**
	 * 裁判官を削除する
	 * 
	 * @param saibanSaibankanEntityList
	 * @throws AppException
	 */
	private void deleteSaibankanEntity(List<TSaibanSaibankanEntity> saibanSaibankanEntityList) throws AppException {

		// 削除件数
		int[] deleteCountArray = null;

		try {
			// 削除
			deleteCountArray = tSaibanSaibankanDao.delete(saibanSaibankanEntityList);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}

		if (deleteCountArray.length != saibanSaibankanEntityList.size()) {
			// 実際の削除件数と削除対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 担当種別毎の裁判担当者を登録する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @param rawInputItemList 入力情報
	 * @param tantoType 担当種別
	 */
	private void insertSaibanTanto(Long saibanSeq, List<AnkenTantoSelectInputForm> rawInputItemList,
			TantoType tantoType) {

		// 画面からの入力情報
		List<AnkenTantoSelectInputForm> inputItemList = rawInputItemList.stream()
				.filter(item -> !item.isEmpty())
				.distinct()
				.limit(CommonConstant.ANKEN_TANTO_ADD_LIMIT)
				.collect(Collectors.toList());

		// i番目のデータの更新ルール
		AtomicLong i = new AtomicLong(0);
		BiConsumer<TSaibanTantoEntity, AnkenTantoSelectInputForm> entitySetter = (entity, inputItem) -> {
			entity.setAccountSeq(inputItem.getAccountSeq());
			entity.setTantoType(tantoType.getCd());
			entity.setSaibanMainTantoFlg(SystemFlg.booleanToCode(inputItem.isMainTanto()));
			entity.setTantoTypeBranchNo(i.getAndIncrement());
		};

		// 登録処理
		inputItemList.stream()
				.forEach(inputItem -> {
					// 担当者を新規登録
					TSaibanTantoEntity entity = new TSaibanTantoEntity();
					entity.setSaibanSeq(saibanSeq);
					entitySetter.accept(entity, inputItem);
					tSaibanTantoDao.insert(entity);
				});
	}

	/**
	 * 担当種別毎の裁判担当者を更新する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @param rawInputItemList 入力情報
	 * @param entityList DB情報
	 * @param tantoType 担当種別
	 */
	private void updateSaibanTanto(Long saibanSeq, List<AnkenTantoSelectInputForm> rawInputItemList,
			List<TSaibanTantoEntity> entityList, TantoType tantoType) {

		// 入力情報
		List<AnkenTantoSelectInputForm> inputItemList = rawInputItemList.stream()
				.filter(item -> !item.isEmpty())
				.distinct()
				.limit(CommonConstant.ANKEN_TANTO_ADD_LIMIT)
				.collect(Collectors.toList());

		// i番目のデータの更新ルール
		AtomicLong i = new AtomicLong(0);
		BiConsumer<TSaibanTantoEntity, AnkenTantoSelectInputForm> entitySetter = (entity, inputItem) -> {
			entity.setAccountSeq(inputItem.getAccountSeq());
			entity.setTantoType(tantoType.getCd());
			entity.setSaibanMainTantoFlg(SystemFlg.booleanToCode(inputItem.isMainTanto()));
			entity.setTantoTypeBranchNo(i.getAndIncrement());
		};

		// 更新処理
		LoiozCollectionUtils.zipping(entityList, inputItemList, (dbValue, inputItem) -> {
			if (inputItem != null) {
				if (dbValue != null) {
					// 入力データあり、既存データあり
					// 既存の担当者を入力データで上書き
					TSaibanTantoEntity entity = dbValue;
					entitySetter.accept(entity, inputItem);
					tSaibanTantoDao.update(entity);

				} else {
					// 入力データあり、既存データなし
					// 担当者を新規登録
					TSaibanTantoEntity entity = new TSaibanTantoEntity();
					entity.setSaibanSeq(saibanSeq);
					entitySetter.accept(entity, inputItem);
					tSaibanTantoDao.insert(entity);
				}

			} else {
				if (dbValue != null) {
					// 入力データなし、既存データあり
					// 担当者の数が減ったので削除
					tSaibanTantoDao.delete(dbValue);
				} else {
					// 入力データなし、既存データなし
					// 何もしない
				}
			}
		});
	}

	// 共通系の処理

	/**
	 * 画面独自のバリデーションチェック（共通）
	 *
	 * @param form
	 * @param response
	 * @return
	 */
	private void saibanScheduleFormValidateCommon(SaibanScheduleInputForm form, BindingResult result) {

		// *******************************************
		// ■日時項目のチェック
		// *******************************************
		// 日付(dateFromの必須)
		boolean isDateRequired = false;
		if (form.getDateFrom() == null) {
			result.rejectValue("dateFrom", null, messageService.getMessage(MessageEnum.VARIDATE_MSG_E00002, SessionUtils.getLocale()));
			isDateRequired = true;
		}

		// 日付がnullじゃない場合
		if (!isDateRequired) {
			LocalDateTime dateTimeFrom = LocalDateTime.of(form.getDateFrom(), form.getTimeFrom());
			LocalDateTime dateTimeTo = LocalDateTime.of(form.getDateFrom(), form.getTimeTo());
			// 日付の整合性チェック
			if (!DateUtils.isCorrectDateTime(dateTimeFrom, dateTimeTo) || dateTimeFrom.isEqual(dateTimeTo)) {
				result.rejectValue("dateFrom", null, messageService.getMessage(MessageEnum.MSG_E00060, SessionUtils.getLocale()));
			}
		}

		// *******************************************
		// ■場所項目のチェック
		// *******************************************

		// 必須（施設）
		if (form.isRoomSelected()) {
			Long roomId = form.getRoomId();
			if (roomId == null) {
				result.rejectValue("roomId", null, messageService.getMessage(MessageEnum.MSG_E00024, SessionUtils.getLocale(), "施設"));
			}
		}

		// *******************************************
		// ■参加者項目のチェック
		// *******************************************

		// 必須（参加者）※出廷不要の場合を除く
		List<Long> selectMember = form.getMember();
		if (ShutteiType.NOT_REQUIRED != form.getShutteiType() && selectMember.isEmpty()) {
			result.rejectValue("member", null, messageService.getMessage(MessageEnum.MSG_E00024, SessionUtils.getLocale(), "参加者"));
		}

		// *******************************************
		// ■裁判期日項目のチェック
		// *******************************************

		// 案件IDの必須チェック
		if (form.getAnkenId() == null) {
			result.rejectValue("ankenId", null, messageService.getMessage(MessageEnum.MSG_E00001, SessionUtils.getLocale()));
		}

		// 裁判SEQの必須チェック
		if (form.getSaibanSeq() == null) {
			result.rejectValue("saibanSeq", null, messageService.getMessage(MessageEnum.MSG_E00001, SessionUtils.getLocale()));
		}
	}

	/**
	 * 画面独自のバリデーションチェック（更新）
	 *
	 * @param form
	 * @param response
	 * @param errorList
	 * @return
	 */
	private void saibanScheduleFormValidateUpdate(SaibanScheduleInputForm form, BindingResult result) {

		// 裁判期日SEQの必須チェック
		if (form.getSaibanLimitSeq() == null) {
			result.rejectValue("saibanLimitSeq", null, messageService.getMessage(MessageEnum.MSG_E00001, SessionUtils.getLocale()));
		}
	}

	/**
	 * DB整合性チェック(共通)
	 *
	 * @param form
	 * @param key エラーを格納するMapのキー
	 * @param errorMsgMap エラー発生時にエラーメッセージを格納する
	 * @return 検証結果
	 */
	private void accessDBValidateForScheduleCommon(SaibanScheduleInputForm form, String key, Map<String, MessageEnum> errorMsgMap) {

		// すでにエラーが有る場合は、検証は行わない
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		// ****************************************
		// 施設IDが有効かどうか判定
		// ****************************************
		if (form.isRoomSelected()) {
			// 施設IDの整合性チェック
			if (!commonScheduleValidator.isRoomExistsValid(form.getScheduleSeq(), form.getRoomId())) {
				errorMsgMap.put(key, MessageEnum.MSG_E00025);
				return;
			}
		}

		// ****************************************
		// アカウントIDが有効かどうか判定
		// ****************************************
		// アカウントの整合性チェック
		if (!commonScheduleValidator.isAccountExistsValid(form.getScheduleSeq(), form.getMember())) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

	}

	/**
	 * DB整合性チェック(更新)
	 *
	 * @param form
	 * @param key
	 * @param errorMsgMap
	 */
	private void accessDBValidateForScheduleUpdate(ScheduleInputForm form, String key, Map<String, MessageEnum> errorMsgMap) {

		// すでにエラーが有る場合は、検証は行わない
		if (!errorMsgMap.isEmpty()) {
			return;
		}

		// ■案件ID(紐付けする値)が変更されていないか
		if (!commonScheduleValidator.existsAnkenForSchedule(form.getScheduleSeq(), form.getAnkenId())) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

		// ■裁判SEQ(紐付けする値)が変更されていないか
		if (!commonScheduleValidator.existsSaibanForSchedule(form.getScheduleSeq(), form.getSaibanSeq())) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

		// ■裁判期日SEQ(紐付けする値)が変更されていないか
		if (!commonScheduleValidator.existsSaibanLimitForSchedule(form.getScheduleSeq(), form.getSaibanLimitSeq())) {
			errorMsgMap.put(key, MessageEnum.MSG_E00025);
			return;
		}

	}

	/**
	 * 親裁判を表示用に加工（bean -> dto）
	 * 
	 * @param beanList
	 * @return
	 */
	private List<AnkenRelatedParentSaibanDto> setAnkenRelatedParentSaibanMinjiBean2Dto(List<AnkenRelatedParentSaibanBean> beanList) {

		List<AnkenRelatedParentSaibanDto> dtoList = beanList.stream()
				.map(bean -> AnkenRelatedParentSaibanDto.builder()
						.ankenId(bean.getAnkenId())
						.bunyaId(bean.getBunyaId())
						.saibanSeq(bean.getSaibanSeq())
						.saibanBranchNo(bean.getSaibanBranchNo())
						.jikenName(bean.getJikenName())
						.jikenNo(CaseNumber.of(EraType.of(bean.getJikenGengo()), bean.getJikenYear(), bean.getJikenMark(), bean.getJikenNo()))
						.saibanTreeSeq(bean.getSaibanTreeSeq())
						.connectType(bean.getConnectType())
						.honsoFlg(bean.getHonsoFlg())
						.kihonFlg(bean.getKihonFlg())
						.build())
				.collect(Collectors.toList());
		return dtoList;
	}

	/**
	 * 親裁判を表示用に加工（bean -> dto）
	 * 
	 * @param beanList
	 * @return
	 */
	private List<AnkenRelatedParentSaibanDto> setAnkenRelatedParentSaibanKeijiBean2Dto(List<AnkenRelatedSaibanKeijiBean> beanList) {

		List<AnkenRelatedParentSaibanDto> dtoList = beanList.stream()
				.map(bean -> AnkenRelatedParentSaibanDto.builder()
						.ankenId(bean.getAnkenId())
						.bunyaId(bean.getBunyaId())
						.saibanSeq(bean.getSaibanSeq())
						.jikenName(bean.getJikenName())
						.jikenNo(CaseNumber.of(EraType.of(bean.getJikenGengo()), bean.getJikenYear(), bean.getJikenMark(), bean.getJikenNo()))
						.build())
				.collect(Collectors.toList());
		return dtoList;
	}

	/**
	 * 予定のフォーム入力値を裁判期日Entityに反映させる
	 *
	 * @param form フォーム入力値
	 * @return
	 */
	private Consumer<TSaibanLimitEntity> populateScheduleInputFormToSaibanLimitEntity(ScheduleInputForm form) {
		return saibanLimitEntity -> {
			LocalDateTime limitAt = form.getDateFrom().atTime(form.getTimeFrom());
			saibanLimitEntity.setLimitAt(limitAt);
			if (form.isRoomSelected() && form.getRoomId() != null) {
				MRoomEntity room = mRoomDao.selectById(form.getRoomId());
				if (room != null) {
					saibanLimitEntity.setPlace(room.getRoomName());
				}
			} else {
				saibanLimitEntity.setPlace(form.getPlace());
			}
			saibanLimitEntity.setContent(form.getMemo());
			saibanLimitEntity.setShutteiType(DefaultEnum.getCd(form.getShutteiType()));
		};
	}

	/**
	 * 口頭弁論期日請書（WN0008Word）のデータ取得処理
	 *
	 * @param saibanId
	 * @param scheduleSeq
	 * @param wn0008WordDto
	 */
	private void getWn0008WordDate(Long saibanSeq, Long scheduleSeq, Wn0008WordDto wn0008WordDto) {
		// 表示している裁判情報
		TSaibanEntity targetSaiban = tSaibanDao.selectBySeq(saibanSeq);
		// 選択したスケジュール
		TScheduleEntity tScheduleEntity = tScheduleDao.selectBySeq(scheduleSeq);
		if (CommonUtils.anyNull(targetSaiban, tScheduleEntity)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + CommonConstant.SPACE + ALREADY_UPDATED_TO_OTHER_USER);
			throw new DataNotFoundException("予定の取得に失敗 : " + scheduleSeq, null);
		}

		// 出力時に必要なキーとなる裁判情報
		Long saibanLimitSeq = tScheduleEntity.getSaibanLimitSeq();

		/** FAX送信状 */
		this.setSouhushoFaxDataWn0008(targetSaiban, wn0008WordDto);

		/** 口頭弁論期日請書 */
		this.setJikenDataWn0008(targetSaiban, saibanLimitSeq, wn0008WordDto);

		// 担当弁護士、担当事務情報の取得
		List<SaibanTantoAccountDto> dispSaibanTanto = commonChohyoService.dispSaibanTantoBengoshiMainJimu(targetSaiban.getSaibanSeq());
		wn0008WordDto.setSaibanTantoAccountDtoList(dispSaibanTanto);
	}

	/**
	 * 公判期日請書（WN0009Word）のデータ取得処理
	 *
	 * @param scheduleSeq
	 * @param wn0009WordDto
	 */
	private void getWn0009WordDate(Long saibanSeq, Long scheduleSeq, Wn0009WordDto wn0009WordDto) {
		TSaibanEntity targetSaiban = tSaibanDao.selectBySeq(saibanSeq);// 表示している裁判情報
		TScheduleEntity tScheduleEntity = tScheduleDao.selectBySeq(scheduleSeq);
		if (tScheduleEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + CommonConstant.SPACE + ALREADY_UPDATED_TO_OTHER_USER);
			throw new DataNotFoundException("予定の取得に失敗 : " + scheduleSeq, null);
		}

		// 出力時に必要なキーとなる裁判情報
		Long saibanLimitSeq = tScheduleEntity.getSaibanLimitSeq();

		/** FAX送信状 */
		this.setSouhushoFaxDataWn0009(targetSaiban, wn0009WordDto);

		/** 公判期日請書 */
		this.setJikenDataWn0009(saibanSeq, saibanLimitSeq, wn0009WordDto);

		// 担当弁護士、担当事務情報の取得
		List<SaibanTantoAccountDto> dispSaibanTanto = commonChohyoService.dispSaibanTantoBengoshiMainJimu(targetSaiban.getSaibanSeq());
		wn0009WordDto.setSaibanTantoAccountDtoList(dispSaibanTanto);
	}

	/**
	 * 口頭弁論期日請書のデータ取得処理
	 *
	 * @param targetSaiban
	 * @param saibanLimitSeq
	 * @param wn0008WordDto
	 */
	private void setJikenDataWn0008(TSaibanEntity targetSaiban, Long saibanLimitSeq, Wn0008WordDto wn0008WordDto) {

		// データの取得
		Long targetSaibanSeq = targetSaiban.getSaibanSeq();

		// ■裁判ツリー情報
		List<TSaibanTreeEntity> allSaibanTreeEntity = new ArrayList<TSaibanTreeEntity>();

		// 親の階層データ
		TSaibanTreeEntity targetTreeEntity = tSaibanTreeDao.selectBySaibanSeq(targetSaibanSeq);
		// 子の階層データ
		List<TSaibanTreeEntity> childSaibanTreeEntities = tSaibanTreeDao.selectChildBySaibanSeq(targetSaibanSeq);

		allSaibanTreeEntity.add(targetTreeEntity);
		allSaibanTreeEntity.addAll(childSaibanTreeEntities);

		// ■裁判事件情報
		List<Long> childSaibanSeqList = childSaibanTreeEntities.stream().map(TSaibanTreeEntity::getSaibanSeq).collect(Collectors.toList());

		// 親事件の詳細を取得
		TSaibanJikenEntity targetSaibanJikenEntity = tSaibanJikenDao.selectSingleBySaibanSeq(targetSaibanSeq);
		// 子事件の詳細を取得
		List<TSaibanJikenEntity> childSaibanJikenEntities = tSaibanJikenDao.selectBySaibanSeq(childSaibanSeqList);
		// 親と子の事件をリスト化
		List<TSaibanJikenEntity> allSaibanJikenEntities = new ArrayList<TSaibanJikenEntity>();
		allSaibanJikenEntities.add(targetSaibanJikenEntity);
		allSaibanJikenEntities.addAll(childSaibanJikenEntities);

		String JikenNo1 = null;
		String jikenName1 = null;

		// 事件名が存在するか判定
		if (LoiozCollectionUtils.isNotEmpty(allSaibanJikenEntities)) {

			// 事件番号
			TSaibanJikenEntity tSaibanJikenEntity = allSaibanJikenEntities.get(0);
			JikenNo1 = CaseNumber.fromEntity(tSaibanJikenEntity).toString();

			// 事件名
			jikenName1 = StringUtils.null2blank(allSaibanJikenEntities.get(0).getJikenName());
			if (allSaibanJikenEntities.size() > 1) {
				jikenName1 = jikenName1 + ChohyoWordConstant.SPACE_OTHER + String.valueOf(allSaibanJikenEntities.size() - 1)
						+ ChohyoWordConstant.KEN;
			}

		}
		// 事件番号、事件名の設定
		wn0008WordDto.setJikenNo1(JikenNo1);
		wn0008WordDto.setJikenName1(jikenName1);

		// *************************************
		// ■当事者情報
		// *************************************
		List<KanyoshaTojishaDto> tojishaDtoList = tSaibanDao.selectTojishaForSaiban(targetSaibanSeq);

		// 当事者情報から顧客を抽出
		List<KanyoshaTojishaDto> customerTojishaList = tojishaDtoList.stream().filter(entity -> {
			return entity.getCustomerId() != null;
		}).collect(Collectors.toList());

		// 原告側のリストを作成
		List<KanyoshaTojishaDto> genkokuList = tojishaDtoList.stream().filter(dto -> {
			TojishaHyoki tojishaHyoki = TojishaHyoki.of(dto.getTojishaHyoki());
			if (tojishaHyoki != null) {
				return TojishaHyokiType.GENKOKU_SIDE == tojishaHyoki.getTojishaHyokiType();
			} else {
				return false;
			}
		}).collect(Collectors.toList());

		// 被告側のリストを作成
		List<KanyoshaTojishaDto> hikokuList = tojishaDtoList.stream().filter(dto -> {
			TojishaHyoki tojishaHyoki = TojishaHyoki.of(dto.getTojishaHyoki());
			if (tojishaHyoki != null) {
				return TojishaHyokiType.HIKOKU_SIDE == tojishaHyoki.getTojishaHyokiType();
			} else {
				return false;
			}
		}).collect(Collectors.toList());

		// 第三者のリストを作成
		List<KanyoshaTojishaDto> outSiderList = tojishaDtoList.stream().filter(dto -> {
			TojishaHyoki tojishaHyoki = TojishaHyoki.of(dto.getTojishaHyoki());
			if (tojishaHyoki != null) {
				return TojishaHyokiType.OUT_SIDER == tojishaHyoki.getTojishaHyokiType();
			} else {
				return false;
			}
		}).collect(Collectors.toList());

		// 当事者(関与者)情報

		// 当事者（原告側）
		KanyoshaTojishaDto mainGenkokuSide = new KanyoshaTojishaDto();
		if (!CollectionUtils.isEmpty(genkokuList)) {
			if (genkokuList.stream().anyMatch(e -> SystemFlg.codeToBoolean(e.getMainFlg()))) {
				mainGenkokuSide = genkokuList.stream().filter(e -> SystemFlg.codeToBoolean(e.getMainFlg())).findFirst().get();
			} else {
				mainGenkokuSide = genkokuList.stream().findFirst().get();
			}
		}
		StringBuilder sbTojishaUp = new StringBuilder();
		Optional<TojishaHyoki> mainGenkokuTojishaOptional = Optional.ofNullable(TojishaHyoki.of(mainGenkokuSide.getTojishaHyoki()));
		mainGenkokuTojishaOptional.ifPresent(hyoki -> sbTojishaUp.append(hyoki.getVal()));

		// 反訴の時は、表記名の後に「反訴●●」を表示
		// 状態を取得
		Map<String, Boolean> parentConnectStatus = this.connectStatus(targetTreeEntity);

		if (parentConnectStatus.get("isHonso")) {
			mainGenkokuTojishaOptional.ifPresent(hyoki -> {
				if (TojishaHyoki.GENKOKU == hyoki) {
					sbTojishaUp.append(HANSO_HIKOKU);
				} else {
					sbTojishaUp.append(HANSO_SOMETHING);
				}
			});
		}
		if (genkokuList.size() > 1) {
			sbTojishaUp.append(OTHERS);
			sbTojishaUp.append(CommonConstant.SPACE);
			sbTojishaUp.append(mainGenkokuSide.getName());
			sbTojishaUp.append(CommonConstant.SPACE);
			sbTojishaUp.append(ChohyoWordConstant.OTHER + String.valueOf(genkokuList.size() - 1) + ChohyoWordConstant.COUNT_PEOPLE);
		} else {
			if (!StringUtils.isEmpty(sbTojishaUp.toString())) {
				sbTojishaUp.append(CommonConstant.SPACE);
				sbTojishaUp.append(mainGenkokuSide.getName());
			}
		}

		// 当事者（被告側）
		KanyoshaTojishaDto mainHikokuSide = new KanyoshaTojishaDto();
		if (!CollectionUtils.isEmpty(hikokuList)) {
			if (hikokuList.stream().anyMatch(e -> SystemFlg.codeToBoolean(e.getMainFlg()))) {
				mainHikokuSide = hikokuList.stream().filter(e -> SystemFlg.codeToBoolean(e.getMainFlg())).findFirst().get();
			} else {
				mainHikokuSide = hikokuList.stream().findFirst().get();
			}
		}
		StringBuilder sbTojishaMid = new StringBuilder();
		Optional<TojishaHyoki> mainHikokuTojishaOptional = Optional.ofNullable(TojishaHyoki.of(mainHikokuSide.getTojishaHyoki()));
		mainHikokuTojishaOptional.ifPresent(hyoki -> sbTojishaMid.append(hyoki.getVal()));

		// 反訴の時は、表記名の後に「反訴●●」を表示
		if (parentConnectStatus.get("isHonso")) {
			mainHikokuTojishaOptional.ifPresent(hyoki -> {
				if (TojishaHyoki.HIKOKU == hyoki) {
					sbTojishaMid.append(HANSO_GENKOKU);
				} else {
					sbTojishaMid.append(HANSO_SOMETHING);
				}
			});
		}

		if (hikokuList.size() > 1) {
			sbTojishaMid.append(OTHERS);
			sbTojishaMid.append(CommonConstant.SPACE);
			sbTojishaMid.append(mainHikokuSide.getName());
			sbTojishaMid.append(CommonConstant.SPACE);
			sbTojishaMid.append(ChohyoWordConstant.OTHER + String.valueOf(hikokuList.size() - 1) + ChohyoWordConstant.COUNT_PEOPLE);
		} else {
			if (!StringUtils.isEmpty(sbTojishaMid.toString())) {
				sbTojishaMid.append(CommonConstant.SPACE);
				sbTojishaMid.append(mainHikokuSide.getName());
			}
		}

		// 当事者（第三者側）
		KanyoshaTojishaDto mainOutSider = outSiderList.stream().findFirst().orElse(new KanyoshaTojishaDto());
		StringBuilder sbTojishaLow = new StringBuilder();
		Optional.ofNullable(TojishaHyoki.of(mainOutSider.getTojishaHyoki())).ifPresent(hyoki -> sbTojishaLow.append(hyoki.getVal()));
		if (outSiderList.size() > 1) {
			sbTojishaLow.append(OTHERS);
			sbTojishaLow.append(CommonConstant.SPACE);
			sbTojishaLow.append(mainOutSider.getName());
			sbTojishaLow.append(CommonConstant.SPACE);
			sbTojishaLow.append(ChohyoWordConstant.OTHER + String.valueOf(outSiderList.size() - 1) + ChohyoWordConstant.COUNT_PEOPLE);
		} else {
			if (!StringUtils.isEmpty(sbTojishaLow.toString())) {
				sbTojishaLow.append(CommonConstant.SPACE);
				sbTojishaLow.append(mainOutSider.getName());
			}
		}

		// ■当事者情報を設定
		wn0008WordDto.setTojishaUp(sbTojishaUp.toString());
		wn0008WordDto.setTojishaMid(sbTojishaMid.toString());
		wn0008WordDto.setTojishaLow(sbTojishaLow.toString());

		// 代理人情報
		KanyoshaTojishaDto targetCustomerTojisha = customerTojishaList.stream().findFirst().orElse(new KanyoshaTojishaDto());
		StringBuilder sbTojishaDairinin = new StringBuilder();
		Optional.ofNullable(TojishaHyoki.of(targetCustomerTojisha.getTojishaHyoki())).ifPresent(hyoki -> sbTojishaDairinin.append(hyoki.getVal()));
		if (customerTojishaList.size() > 1 && StringUtils.isNotEmpty(sbTojishaDairinin.toString())) {
			sbTojishaDairinin.append(OTHERS);
		}
		sbTojishaDairinin.append(CommonConstant.SPACE);
		sbTojishaDairinin.append(DAIRININ_BENGOSHI);
		sbTojishaDairinin.append(CommonConstant.SPACE);
		if (targetCustomerTojisha.getCustomerId() != null) {
			// 裁判に紐づく担当弁護士を表示
			Optional.ofNullable(this.getPrioritySaibanTantoName(TantoType.LAWYER, targetSaibanSeq))
					.ifPresent(str -> sbTojishaDairinin.append(StringUtils.defaultString(str)));
		}

		// ■代理人情報を設定
		wn0008WordDto.setTojishaDairinin(sbTojishaDairinin.toString());

		// ■裁判期日情報
		TSaibanLimitEntity tSaibanLimitEntity = tSaibanLimitDao.selectBySeq(saibanLimitSeq);
		wn0008WordDto.setLimitCount(LoiozNumberUtils.parseAsString(tSaibanLimitEntity.getLimitDateCount()));
		wn0008WordDto.setLimitDate(DateUtils.parseLocalDateTimeToJaDate(tSaibanLimitEntity.getLimitAt()));

	}

	/**
	 * 公判期日請書のデータ取得処理
	 *
	 * @param saibanSeq
	 * @param saibanLimitSeq
	 * @param wn0009WordDto
	 */
	private void setJikenDataWn0009(Long saibanSeq, Long saibanLimitSeq, Wn0009WordDto wn0009WordDto) {

		// データの取得
		List<TSaibanJikenEntity> tSaibanJikenEntityList = tSaibanJikenDao.selectBySaibanSeq(saibanSeq);
		tSaibanJikenEntityList.sort(Comparator.comparing(TSaibanJikenEntity::getJikenSeq));

		String JikenNo1 = null;
		String jikenName1 = null;
		// 事件名が存在するか判定
		if (LoiozCollectionUtils.isNotEmpty(tSaibanJikenEntityList)) {

			// 事件情報
			TSaibanJikenEntity tSaibanJikenEntity = tSaibanJikenEntityList.get(0);
			JikenNo1 = CaseNumber.fromEntity(tSaibanJikenEntity).toString();
			jikenName1 = StringUtils.null2blank(tSaibanJikenEntityList.get(0).getJikenName());
			if (tSaibanJikenEntityList.size() > 1) {
				jikenName1 = jikenName1 + ChohyoWordConstant.SPACE_OTHER + String.valueOf(tSaibanJikenEntityList.size() - 1)
						+ ChohyoWordConstant.KEN;

			}
		}
		// 事件番号、事件名の設定
		wn0009WordDto.setJikenNo1(JikenNo1);
		wn0009WordDto.setJikenName1(jikenName1);

		// ■当事者(関与者)情報

		// 当事者（被告）
		StringBuilder sbTojishaHikoku = new StringBuilder();
		// 被告人の欄を作成
		List<String> hikokuList = tSaibanDao.selectHikokuName(saibanSeq);
		if (!CollectionUtils.isEmpty(hikokuList)) {
			if (hikokuList.size() > 1) {
				// 共犯者などが存在する場合
				sbTojishaHikoku.append(HIKOKUNIN);
				sbTojishaHikoku.append(CommonConstant.SPACE);
				sbTojishaHikoku.append(hikokuList.get(0));
				sbTojishaHikoku.append(CommonConstant.SPACE);
				sbTojishaHikoku.append(ChohyoWordConstant.OTHER + String.valueOf(hikokuList.size() - 1) + ChohyoWordConstant.COUNT_PEOPLE);
			} else {
				// 被告人単独の場合
				sbTojishaHikoku.append(HIKOKUNIN);
				sbTojishaHikoku.append(CommonConstant.SPACE);
				sbTojishaHikoku.append(hikokuList.get(0));
			}
		}
		// ■被告人
		wn0009WordDto.setHikokunin(sbTojishaHikoku.toString());

		// ■弁護人情報を設定
		List<KanyoshaTojishaDto> tojishaDtoList = tSaibanDao.selectTojishaForSaiban(saibanSeq);

		// 関与者だけのMapを作成
		Map<Long, KanyoshaTojishaDto> tojishaMapForKanyosha = tojishaDtoList.stream()
				.filter(dto -> dto.getKanyoshaSeq() != null)
				.collect(Collectors.toMap(
						KanyoshaTojishaDto::getKanyoshaSeq,
						Function.identity(),
						(formmer, latter) -> latter));

		// 代理人を覗いたListを作成
		List<KanyoshaTojishaDto> dairininNotIncludeTojishaList = tojishaDtoList.stream()
				.filter(dto -> !SystemFlg.codeToBoolean(dto.getDairiFlg()))
				.collect(Collectors.toList());

		// 筆頭当事者の判定
		KanyoshaTojishaDto mainTojisha = new KanyoshaTojishaDto();
		if (!CollectionUtils.isEmpty(dairininNotIncludeTojishaList)) {
			if (dairininNotIncludeTojishaList.stream().anyMatch(dto -> SystemFlg.codeToBoolean(dto.getMainFlg()))) {
				mainTojisha = dairininNotIncludeTojishaList.stream().filter(dto -> SystemFlg.codeToBoolean(dto.getMainFlg())).findFirst().get();
			} else {
				mainTojisha = dairininNotIncludeTojishaList.stream().findFirst().get();
			}
		}
		StringBuilder sbBengonin = new StringBuilder();
		sbBengonin.append(HIKOKUNIN);
		if (dairininNotIncludeTojishaList.size() > 1) {
			sbBengonin.append(OTHERS);
		}
		sbBengonin.append(SOSHO_DAIRININ_BENGOSHI);
		if (mainTojisha.getCustomerId() != null) {
			// 裁判に紐づく担当弁護士を表示
			Optional.ofNullable(this.getPrioritySaibanTantoName(TantoType.LAWYER, saibanSeq))
					.ifPresent(str -> sbBengonin.append(StringUtils.defaultString(str)));
		} else if (mainTojisha.getKanyoshaSeq() != null) {
			// mainTojishに紐づく関与者代理人
			Optional.ofNullable(tojishaMapForKanyosha.get(mainTojisha.getRelatedKanyoshaSeq()))
					.ifPresent(dto -> sbBengonin.append(StringUtils.defaultString(dto.getName())));
		} else {
			// なにもしない
		}
		// ■弁護人情報
		wn0009WordDto.setBengonin(sbBengonin.toString());

		// ■裁判期日情報
		TSaibanLimitEntity tSaibanLimitEntity = tSaibanLimitDao.selectBySeq(saibanLimitSeq);
		wn0009WordDto.setLimitCount(LoiozNumberUtils.parseAsString(tSaibanLimitEntity.getLimitDateCount()));
		wn0009WordDto.setLimitDate(DateUtils.parseLocalDateTimeToJaDate(tSaibanLimitEntity.getLimitAt()));

	}

	/**
	 * 口頭弁論期日請書-送付書(FAX用)のデータ取得処理
	 *
	 * @param targetSaiban
	 * @param wn0008WordDto
	 */
	private void setSouhushoFaxDataWn0008(TSaibanEntity targetSaiban, Wn0008WordDto wn0008WordDto) {
		this.setSouhushoFaxData(targetSaiban, wn0008WordDto, null);
	}

	/**
	 * 公判期日請書-送付書(FAX用)のデータ取得処理
	 *
	 * @param targetSaiban
	 * @param wn0009WordDto
	 */
	private void setSouhushoFaxDataWn0009(TSaibanEntity targetSaiban, Wn0009WordDto wn0009WordDto) {
		this.setSouhushoFaxData(targetSaiban, null, wn0009WordDto);
	}

}
