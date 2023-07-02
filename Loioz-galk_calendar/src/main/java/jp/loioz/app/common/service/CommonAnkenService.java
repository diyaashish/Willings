package jp.loioz.app.common.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.user.ankenManagement.dto.AnkenKanyoshaDairininViewDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenKanyoshaViewDto;
import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import jp.loioz.app.user.schedule.service.ScheduleCommonService;
import jp.loioz.bean.AnkenBean;
import jp.loioz.bean.AnkenKanryoFlgGroupByAnkenBean;
import jp.loioz.bean.AnkenKanyoshaBean;
import jp.loioz.bean.AnkenRelatedKanyoshaBean;
import jp.loioz.bean.AnkenTantoAccountBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BengoType;
import jp.loioz.common.constant.CommonConstant.BunyaType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAccgDocDao;
import jp.loioz.dao.TAnkenAddKeijiDao;
import jp.loioz.dao.TAnkenAzukariItemDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenJikenDao;
import jp.loioz.dao.TAnkenKoryuDao;
import jp.loioz.dao.TAnkenRelatedKanyoshaDao;
import jp.loioz.dao.TAnkenSekkenDao;
import jp.loioz.dao.TAnkenSosakikanDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.dao.TFeeDao;
import jp.loioz.dao.TGyomuHistoryAnkenDao;
import jp.loioz.dao.TGyomuHistoryCustomerDao;
import jp.loioz.dao.TKaikeiKirokuDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TKeijiAnkenCustomerDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dao.TPersonAddLawyerDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TScheduleDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.domain.value.SaibanId;
import jp.loioz.dto.AnkenDto;
import jp.loioz.dto.AnkenDto.Aitegata;
import jp.loioz.dto.AnkenDto.Customer;
import jp.loioz.dto.AnkenDto.Higaisha;
import jp.loioz.dto.AnkenDto.Kyohansha;
import jp.loioz.dto.AnkenDto.TojishaKanyosha;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.PersonInfoForAnkenDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAccgDocEntity;
import jp.loioz.entity.TAnkenAddKeijiEntity;
import jp.loioz.entity.TAnkenAzukariItemEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TAnkenJikenEntity;
import jp.loioz.entity.TAnkenKoryuEntity;
import jp.loioz.entity.TAnkenRelatedKanyoshaEntity;
import jp.loioz.entity.TAnkenSekkenEntity;
import jp.loioz.entity.TAnkenSosakikanEntity;
import jp.loioz.entity.TAnkenTantoEntity;
import jp.loioz.entity.TDepositRecvEntity;
import jp.loioz.entity.TFeeEntity;
import jp.loioz.entity.TGyomuHistoryAnkenEntity;
import jp.loioz.entity.TGyomuHistoryCustomerEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TKeijiAnkenCustomerEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;
import jp.loioz.entity.TPersonAddLawyerEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TScheduleEntity;

/**
 * 案件情報関連の共通サービス処理
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonAnkenService extends DefaultService {

	@Autowired
	private ScheduleCommonService scheduleCommonService;

	@Autowired
	private CommonPersonService commonCustomerService;

	@Autowired
	private CommonBunyaService commonBunyaService;

	@Autowired
	private MAccountDao mAccountDao;

	@Autowired
	private TAnkenDao tAnkenDao;

	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	@Autowired
	private TPersonAddLawyerDao tPersonAddLawyerDao;

	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	@Autowired
	private TAnkenRelatedKanyoshaDao tAnkenRelatedKanyoshaDao;

	@Autowired
	private TSaibanDao tSaibanDao;

	@Autowired
	private TAnkenSekkenDao tAnkenSekkenDao;

	@Autowired
	private TAnkenKoryuDao tAnkenKoryuDao;

	@Autowired
	private TAnkenJikenDao tAnkenJikenDao;

	@Autowired
	private TAnkenSosakikanDao tAnkenSosakikanDao;

	@Autowired
	private TAnkenAddKeijiDao tAnkenAddKeijiDao;

	@Autowired
	private TKaikeiKirokuDao tKaikeiKirokuDao;

	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	@Autowired
	private TAnkenAzukariItemDao tAnkenAzukariItemDao;

	@Autowired
	private TGyomuHistoryAnkenDao tGyomuHistoryAnkenDao;

	@Autowired
	private TGyomuHistoryCustomerDao tGyomuHistoryCustomerDao;

	@Autowired
	private TKeijiAnkenCustomerDao tKeijiAnkenCustomerDao;

	@Autowired
	private TScheduleDao tScheduleDao;

	/** 報酬用のDaoクラス */
	@Autowired
	private TFeeDao tFeeDao;

	/** 預り金用のDaoクラス */
	@Autowired
	private TDepositRecvDao tDepositRecvDao;

	/** 会計書類Daoクラス */
	@Autowired
	private TAccgDocDao tAccgDocDao;

	/** メッセージサービス */
	@Autowired
	private MessageService messageService;

	@Autowired
	private Logger logger;

	/**
	 * 案件IDから民事案件かどうかを判別する
	 * 
	 * @param ankenId
	 * @return 判別結果
	 */
	public boolean isMinji(Long ankenId) {
		// 案件情報を取得
		TAnkenEntity anken = tAnkenDao.selectById(ankenId);
		if (anken == null) {
			throw new DataNotFoundException("案件情報が存在しません。[ankenId=" + ankenId + "]");
		}
		return !commonBunyaService.isKeiji(anken.getBunyaId()); // 民事以外は刑事
	}

	/**
	 * 案件IDから刑事案件かどうかを判別する
	 * 
	 * @param ankenId
	 * @return 判別結果
	 */
	public boolean isKeiji(Long ankenId) {
		// 案件情報を取得
		TAnkenEntity anken = tAnkenDao.selectById(ankenId);
		if (anken == null) {
			throw new DataNotFoundException("案件情報が存在しません。[ankenId=" + ankenId + "]");
		}
		return commonBunyaService.isKeiji(anken.getBunyaId());
	}

	/**
	 * 顧客に紐づく案件を追加上限チェック
	 *
	 * @param customerId
	 * @return
	 */
	public boolean isAnkenAdd(Long customerId) {
		boolean error = false;
		// 登録上限チェック
		List<TAnkenEntity> tAnkenEntityList = tAnkenDao.selectByCustomerId(customerId);
		if (tAnkenEntityList.size() >= CommonConstant.ANKEN_ADD_LIMIT) {
			error = true;
		}
		return error;
	}

	/**
	 * 選択された顧客が、削除可能かどうかを判定する
	 * 
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return ture：削除可能、false：削除不可
	 */
	public boolean isAbleToDeleteCustomer(Long ankenId, Long customerId) {

		// *****************************************************************************
		// ★★★ 削除不可の条件 ★★★
		// ① 案件に削除対象顧客しか紐づいていない場合。(案件：顧客 = １：１ の場合)
		// ② 日付データが存在する。(受任日、事件処理完了日、精算完了日、完了フラグON)
		// ③ 会計データが存在する。
		// ④ 預り品データが存在する。(預かり元が削除対象顧客であるデータ)
		// ⑤ 裁判データが存在する。
		//
		// ※ 顧客との紐付けを解除するための判定処理。 案件自体は削除されない。
		// *****************************************************************************

		// ① 案件に削除対象顧客しか紐づいていない場合。(案件：顧客 = １：１ の場合) の判定
		List<TAnkenCustomerEntity> ankenCustomerList = tAnkenCustomerDao.selectByAnkenId(ankenId);
		if (ankenCustomerList.size() == 1) {
			// 案件に紐づく顧客が１人（= 削除対象顧客のみ) の場合は削除できません。
			return false;
		}

		// ② 日付データが存在する。(受任日、事件処理完了日、精算完了日) の判定
		TAnkenCustomerEntity ankenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);
		LocalDate juninDate = ankenCustomerEntity.getJuninDate();
		LocalDate jikenCompDate = ankenCustomerEntity.getJikenKanryoDate();
		LocalDate seisanCompDate = ankenCustomerEntity.getKanryoDate();

		// ③ 完了フラグの判定
		boolean isComplete = false;
		String kanryoFlg = ankenCustomerEntity.getKanryoFlg();
		if (kanryoFlg != null) {
			if (kanryoFlg.equals(SystemFlg.FLG_ON.getCd())) {
				isComplete = true;
			}
		}

		if (juninDate != null || jikenCompDate != null || seisanCompDate != null
				|| isComplete) {
			// 初回面談日、受任日、事件処理完了日、精算完了日のいずれかが設定されている、
			// もしくは完了フラグがONの場合は、削除できません。
			return false;
		}

		// ③ 会計データが存在する。の判定
		List<TKaikeiKirokuEntity> kaikeiKirokuList = tKaikeiKirokuDao.selectByAnkenIdAndCustomerId(ankenId, customerId, false);
		List<TNyushukkinYoteiEntity> nyushukkinYoteiList = tNyushukkinYoteiDao.selectByAnkenIdAndCustomerId(ankenId, customerId, false);
		// 新会計報酬データ
		List<TFeeEntity> feeList = tFeeDao.selectFeeEntityByParams(ankenId, customerId);
		// 新会計預り金データ
		List<TDepositRecvEntity> depositRecvList = tDepositRecvDao.selectDepositRecvByParams(ankenId, customerId, null);
		// 新会計書類（請求書、精算書）データ
		List<TAccgDocEntity> accgDocList = tAccgDocDao.selectAccgDocByParams(ankenId, customerId);
		// 新会計請求書の請求先データ
		List<TAccgDocEntity> accgDocInvoiceList = tAccgDocDao.selectAccgDocInvoiceByBillToPersonId(ankenId, customerId);
		// 新会計精算書の返金先データ
		List<TAccgDocEntity> accgDocStatmentList = tAccgDocDao.selectAccgDocStatementByRefundToPersonId(ankenId, customerId);
		
		if (kaikeiKirokuList.size() > 0 || nyushukkinYoteiList.size() > 0 || feeList.size() > 0 || depositRecvList.size() > 0
				|| accgDocList.size() > 0 || accgDocInvoiceList.size() > 0 || accgDocStatmentList.size() > 0) {
			// 会計データが存在する場合は、削除できません。
			return false;
		}

		// ④ 預り品データが存在する。(預かり元・返却先が削除対象顧客であるデータ) の判定
		List<TAnkenAzukariItemEntity> azukariItemList = tAnkenAzukariItemDao.selectByAnkenIdAndCustomerId(ankenId,
				customerId);
		if (azukariItemList.size() > 0) {
			// 預かり品データが存在する場合は、削除できません。
			return false;
		}

		// ⑤ 裁判データが存在する。の判定
		List<TSaibanEntity> saibanList = tSaibanDao.selectByAnkenIdAndCustomerId(ankenId, customerId);
		if (saibanList.size() > 0) {
			// 裁判データが存在する場合は、削除できません。
			return false;
		}

		return true;
	}

	/**
	 * 分野の切替可否判定
	 *
	 * @param ankenId
	 * @return 判定結果 true:可能 ,false :不可能
	 */
	public boolean canChangeBunya(Long ankenId) {

		List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByAnkenId(ankenId);
		List<TSaibanEntity> tSaibanEntities = tSaibanDao.selectByAnkenId(ankenId);

		// 案件に紐づく顧客が１件以外の場合は、変更できない仕様
		if (tAnkenCustomerEntities.size() != 1) {
			return false;
		}

		// ーーーーーーーーーーーーー↓ 案件顧客情報は１件の場合 ↓ーーーーーーーーーーーーー

		// 案件ステータスが「相談」「面談予定」以外は、変更できない仕様
		TAnkenCustomerEntity entity = tAnkenCustomerEntities.stream().findFirst().get();
		if (!AnkenStatus.SODAN.equalsByCode(entity.getAnkenStatus())
				&& !AnkenStatus.MENDAN_YOTEI.equalsByCode(entity.getAnkenStatus())) {
			return false;
		}

		// 裁判が１件でも登録されている場合は、変更できない仕様
		if (!tSaibanEntities.isEmpty()) {
			return false;
		}

		return true;
	}

	/**
	 * 分野が変更になっているかどうかの判定
	 *
	 * @param ankenId 案件ID
	 * @param bunyaId 分野ID
	 * @return true：変更あり、false：変更なし
	 */
	public boolean isChangedBunya(Long ankenId, Long bunyaId) {

		TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
		boolean isKeijiForOrg = commonBunyaService.isKeiji(tAnkenEntity.getBunyaId());
		boolean isKeijiForNew = commonBunyaService.isKeiji(bunyaId);

		if ((isKeijiForOrg && isKeijiForNew) || (!isKeijiForOrg && !isKeijiForNew)) {
			// 刑事 ⇒ 刑事、民事 ⇒ 民事 の場合
			return false;

		} else {
			// 民事 ⇒ 刑事、刑事 ⇒ 民事 の場合
			return true;
		}
	}

	/**
	 * 売上計上先の登録件数チェック
	 * 
	 * @param tantoList
	 * @param result
	 * @param fieldName
	 */
	public void validateSalesOwnerCount(List<AnkenTantoSelectInputForm> tantoList, BindingResult result, String fieldName) {

		if (tantoList.stream().filter(e -> Objects.nonNull(e.getAccountSeq())).count() > CommonConstant.ANKEN_SALES_OWNER_ADD_LIMIT) {

			MessageEnum errorMsgEnum = MessageEnum.MSG_E00189;
			String[] errorMsgArgs = {"売上計上先", CommonConstant.ANKEN_SALES_OWNER_ADD_LIMIT + "名"};

			// 売上計上先の登録可能件数が1件のみ
			result.rejectValue(fieldName, errorMsgEnum.getMessageKey(), errorMsgArgs, messageService.getMessage(errorMsgEnum, SessionUtils.getLocale(), errorMsgArgs));
		}
	}

	/**
	 * 担当者の相関チェック
	 *
	 * @param tantoList
	 * @param result
	 * @param fieldName
	 * @param fieldMsgName
	 */
	public BindingResult validateTanto(List<AnkenTantoSelectInputForm> tantoList, BindingResult result, String fieldName, String fieldMsgName) {
		// key:アカウントSEQ、value:出現回数 でマッピング
		Map<Long, Long> tantoCountMap = tantoList.stream().filter(tanto -> tanto.getAccountSeq() != null)
				.collect(Collectors.groupingBy(AnkenTantoSelectInputForm::getAccountSeq, Collectors.counting()));

		MessageEnum errorMsgEnum = MessageEnum.MSG_E00038;
		String[] errorMsgArgs = {fieldMsgName};
		String errorMsg = messageService.getMessage(errorMsgEnum, SessionUtils.getLocale(), errorMsgArgs);

		int idx = 0;
		for (AnkenTantoSelectInputForm tanto : tantoList) {
			if (tanto.getAccountSeq() != null) {
				Long count = tantoCountMap.get(tanto.getAccountSeq());
				if (count > 1) {
					result.rejectValue(fieldName + "[" + idx + "]", errorMsgEnum.getMessageKey(), errorMsgArgs, errorMsg);
				}
				idx++;
			}
		}
		return result;
	}

	/**
	 * 名簿IDが顧客の案件、当事者・関与者に設定されている案件を取得します。<br>
	 * 取得順は顧客案件の未完了、完了、不受任、関与案件の未完了、完了です。<br>
	 * （関与案件のステータスは、顧客全員のステータスが完了していれば完了、それ以外は未完了です）<br>
	 * 
	 * @param personId
	 * @param options
	 * @return
	 */
	public List<AnkenDto> getAllAnkenRelatedToPersonId(Long personId, SelectOptions options) {

		List<AnkenDto> ankenList = this.generateAllAnkenListByPersonId(personId, options);

		// 取得したデータからDTOのリストを作成
		return ankenList.stream()
				.map(anken -> {
					return AnkenDto.builder()
							.transitionCustomerId(anken.getTransitionCustomerId())
							.ankenId(anken.getAnkenId())
							.bunya(anken.getBunya())
							.bunyaType(anken.getBunyaType())
							.ankenName(anken.getAnkenName())
							.ankenType(anken.getAnkenType())
							.customerList(anken.getCustomerList())
							.aitegataList(anken.getAitegataList())
							.kyohanshaList(anken.getKyohanshaList())
							.higaishaList(anken.getHigaishaList())
							.tojishaKanyoshaList(anken.getTojishaKanyoshaList())
							.ankenStatus(anken.getAnkenStatus())
							.ankenStatusName(anken.getAnkenStatusName())
							.isCompAnken(anken.isCompAnken())
							.saibanIdList(anken.getSaibanIdList())
							.saibanStatus(anken.getSaibanStatus())
							.build();
				})
				.collect(Collectors.toList());
	}

	/**
	 * 案件リストを生成する<br>
	 * isAllAnkenList が true の場合は、customerIdが顧客の全案件リスト<br>
	 * isAllAnkenList が false の場合は、完了、不受任を除いたcustomerIdが顧客の案件リスト<br>
	 * 
	 * @param customerId
	 * @param boolean isAllAnkenList
	 * @param boolean isCustomer
	 * @return List<AnkenDto>
	 */
	public List<AnkenDto> getAnkenListByCustomerId(Long customerId, boolean isAllAnkenList) {

		List<AnkenDto> ankenList = this.generateAnkenListByCustomerId(customerId);
		// 未完了の案件リスト
		List<AnkenDto> ankenIncompleteList = ankenList.stream()
				.filter(dto -> (!AnkenStatus.KANRYO.equalsByCode(dto.getAnkenStatus())
						&& !AnkenStatus.FUJUNIN.equalsByCode(dto.getAnkenStatus())))
				.sorted((a1, a2) -> a1.getAnkenId().asLong().compareTo(a2.getAnkenId().asLong()))
				.collect(Collectors.toList());
		// 完了の案件リスト
		List<AnkenDto> ankenCompletedList = ankenList.stream()
				.filter(dto -> AnkenStatus.KANRYO.equalsByCode(dto.getAnkenStatus()))
				.sorted((a1, a2) -> a1.getAnkenId().asLong().compareTo(a2.getAnkenId().asLong()))
				.collect(Collectors.toList());
		// 不受任の案件リスト
		List<AnkenDto> ankenRejectionList = ankenList.stream()
				.filter(dto -> AnkenStatus.FUJUNIN.equalsByCode(dto.getAnkenStatus()))
				.sorted((a1, a2) -> a1.getAnkenId().asLong().compareTo(a2.getAnkenId().asLong()))
				.collect(Collectors.toList());

		if (isAllAnkenList) {
			// 案件リスト（未完了の案件リスト + 完了の案件リスト + 不受任の案件リスト）
			List<AnkenDto> ankenSortedList = ankenIncompleteList;
			ankenSortedList.addAll(ankenCompletedList);
			ankenSortedList.addAll(ankenRejectionList);
			// 名簿IDに紐づく全案件リストを作成
			return ankenSortedList.stream()
					.map(anken -> {
						return AnkenDto.builder()
								.transitionCustomerId(anken.getTransitionCustomerId())
								.ankenId(anken.getAnkenId())
								.bunya(anken.getBunya())
								.bunyaType(anken.getBunyaType())
								.ankenName(anken.getAnkenName())
								.ankenType(anken.getAnkenType())
								.customerList(anken.getCustomerList())
								.aitegataList(anken.getAitegataList())
								.kyohanshaList(anken.getKyohanshaList())
								.higaishaList(anken.getHigaishaList())
								.tojishaKanyoshaList(anken.getTojishaKanyoshaList())
								.ankenStatus(anken.getAnkenStatus())
								.ankenStatusName(anken.getAnkenStatusName())
								.isCompAnken(anken.isCompAnken())
								.saibanIdList(anken.getSaibanIdList())
								.saibanStatus(anken.getSaibanStatus())
								.build();
					})
					.collect(Collectors.toList());
		} else {
			// 完了、不受任を除く案件リストを作成
			return ankenIncompleteList.stream()
					.map(anken -> {
						return AnkenDto.builder()
								.transitionCustomerId(anken.getTransitionCustomerId())
								.ankenId(anken.getAnkenId())
								.bunya(anken.getBunya())
								.bunyaType(anken.getBunyaType())
								.ankenName(anken.getAnkenName())
								.ankenType(anken.getAnkenType())
								.customerList(anken.getCustomerList())
								.aitegataList(anken.getAitegataList())
								.kyohanshaList(anken.getKyohanshaList())
								.higaishaList(anken.getHigaishaList())
								.tojishaKanyoshaList(anken.getTojishaKanyoshaList())
								.ankenStatus(anken.getAnkenStatus())
								.ankenStatusName(anken.getAnkenStatusName())
								.isCompAnken(anken.isCompAnken())
								.saibanIdList(anken.getSaibanIdList())
								.saibanStatus(anken.getSaibanStatus())
								.build();
					}).collect(Collectors.toList());
		}
	}

	/**
	 * 関与案件リストを生成する<br>
	 * isAllAnkenList が true の場合は、customerIdが関与者の全案件リスト<br>
	 * isAllAnkenList が false の場合は、完了、不受任を除いたcustomerIdが関与者の案件リスト<br>
	 *
	 * @param customerId
	 * @param boolean isAllAnkenList
	 * @return List<AnkenDto>
	 */
	public List<AnkenDto> getRelatedAnkenListByCustomerId(Long customerId, boolean isAllAnkenList) {

		List<AnkenDto> ankenList = this.generateRelatedAnkenListByCustomerId(customerId);
		// 未完了の案件リスト
		List<AnkenDto> ankenIncompleteList = ankenList.stream()
				.filter(dto -> (!AnkenStatus.KANRYO.equalsByCode(dto.getAnkenStatus())))
				.sorted((a1, a2) -> a1.getAnkenId().asLong().compareTo(a2.getAnkenId().asLong()))
				.collect(Collectors.toList());
		// 完了の案件リスト
		List<AnkenDto> ankenCompletedList = ankenList.stream()
				.filter(dto -> AnkenStatus.KANRYO.equalsByCode(dto.getAnkenStatus()))
				.sorted((a1, a2) -> a1.getAnkenId().asLong().compareTo(a2.getAnkenId().asLong()))
				.collect(Collectors.toList());

		if (isAllAnkenList) {
			// 案件リスト（未完了の案件リスト + 完了の案件リスト）
			List<AnkenDto> ankenSortedList = ankenIncompleteList;
			ankenSortedList.addAll(ankenCompletedList);
			// 全案件リストを作成
			return ankenSortedList.stream()
					.map(anken -> {
						return AnkenDto.builder()
								.transitionCustomerId(anken.getTransitionCustomerId())
								.ankenId(anken.getAnkenId())
								.bunya(anken.getBunya())
								.bunyaType(anken.getBunyaType())
								.ankenName(anken.getAnkenName())
								.ankenType(anken.getAnkenType())
								.customerList(anken.getCustomerList())
								.aitegataList(anken.getAitegataList())
								.kyohanshaList(anken.getKyohanshaList())
								.higaishaList(anken.getHigaishaList())
								.tojishaKanyoshaList(anken.getTojishaKanyoshaList())
								.ankenStatus(anken.getAnkenStatus())
								.ankenStatusName(anken.getAnkenStatusName())
								.isCompAnken(anken.isCompAnken())
								.saibanIdList(anken.getSaibanIdList())
								.saibanStatus(anken.getSaibanStatus())
								.build();
					})
					.collect(Collectors.toList());
		} else {
			// 未完了の案件リストを作成
			return ankenIncompleteList.stream()
					.map(anken -> {
						return AnkenDto.builder()
								.transitionCustomerId(anken.getTransitionCustomerId())
								.ankenId(anken.getAnkenId())
								.bunya(anken.getBunya())
								.bunyaType(anken.getBunyaType())
								.ankenName(anken.getAnkenName())
								.ankenType(anken.getAnkenType())
								.customerList(anken.getCustomerList())
								.aitegataList(anken.getAitegataList())
								.kyohanshaList(anken.getKyohanshaList())
								.higaishaList(anken.getHigaishaList())
								.tojishaKanyoshaList(anken.getTojishaKanyoshaList())
								.ankenStatus(anken.getAnkenStatus())
								.ankenStatusName(anken.getAnkenStatusName())
								.isCompAnken(anken.isCompAnken())
								.saibanIdList(anken.getSaibanIdList())
								.saibanStatus(anken.getSaibanStatus())
								.build();
					}).collect(Collectors.toList());
		}
	}

	/**
	 * 顧客IDから紐づく案件情報Dtoリストを生成する
	 *
	 * @param customerId 顧客ID
	 * @return 案件情報Dtoリスト
	 */
	public List<AnkenDto> generateAnkenDtoListByCustomerId(Long customerId) {

		// ------------------------------------------------------------
		// 案件情報
		// ------------------------------------------------------------
		List<TAnkenEntity> tAnkenEntityList = tAnkenDao.selectByCustomerId(customerId);

		// 案件IDリストを取得
		List<Long> ankenIdList = tAnkenEntityList.stream().map(TAnkenEntity::getAnkenId).collect(Collectors.toList());

		// ------------------------------------------------------------
		// 案件 - 顧客 情報
		// ------------------------------------------------------------
		// 顧客IDに紐づく案件-顧客情報一覧の取得
		List<TAnkenCustomerEntity> tAnkenCustomerEntityList = tAnkenCustomerDao.selectByCustomerId(customerId);

		// 案件IDから案件ステータスCDを取得するMap
		Map<Long, String> ankenStatusMap = tAnkenCustomerEntityList.stream()
				.collect(Collectors.toMap(TAnkenCustomerEntity::getAnkenId, TAnkenCustomerEntity::getAnkenStatus));

		// ------------------------------------------------------------
		// 裁判情報
		// ------------------------------------------------------------
		// 裁判ステータスの取得
		List<TSaibanEntity> tSaibanEntityList = tSaibanDao.selectByAnkenIdList(ankenIdList);

		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// ------------------------------------------------------------
		// 画面表示用に加工
		// ------------------------------------------------------------
		List<AnkenDto> ankenList = tAnkenEntityList.stream()
				.map(entity -> AnkenDto.builder()
						.transitionCustomerId(customerId)
						.ankenId(AnkenId.of(entity.getAnkenId()))
						.bunya(commonBunyaService.getBunyaName(bunyaMap.get(entity.getBunyaId())))
						.bunyaType(commonBunyaService.isKeiji(entity.getBunyaId()) ? BunyaType.KEIJI.getCd() : BunyaType.MINJI.getCd())
						.ankenName(StringUtils.isEmpty(entity.getAnkenName()) ? "(案件名未入力)" : entity.getAnkenName())
						.ankenType(AnkenType.of(entity.getAnkenType()))
						.customerList(null)
						.aitegataList(null)
						.kyohanshaList(null)
						.higaishaList(null)
						.tojishaKanyoshaList(null)
						.ankenStatus(ankenStatusMap.get(entity.getAnkenId()))
						.ankenStatusName(AnkenStatus.of(ankenStatusMap.get(entity.getAnkenId())).getVal())
						.isCompAnken(AnkenStatus.isComp(ankenStatusMap.get(entity.getAnkenId())))
						.saibanIdList(tSaibanEntityList.stream().filter(e -> e.getAnkenId().equals(entity.getAnkenId()))
								.map(SaibanId::fromEntity).collect(Collectors.toList()))
						.saibanStatus(tSaibanEntityList.stream().filter(e -> e.getAnkenId().equals(entity.getAnkenId()))
								.map(e -> "(" + SaibanStatus.calc(e.getSaibanStartDate(), e.getSaibanEndDate()).getVal() + ")")
								.collect(Collectors.joining("\r\n")))
						.build())
				.collect(Collectors.toList());

		return ankenList;
	}

	/**
	 * 案件-顧客情報から案件ステータスを判定します
	 *
	 * @param entity
	 * @return 案件ステータスのコード値
	 */
	public String getCurrentAnkenStatus(TAnkenCustomerEntity entity) {

		if (entity == null) {
			return null;
		}

		// エラーケース
		if (Objects.isNull(entity.getJuninDate()) && Objects.nonNull(entity.getJikenKanryoDate())) {
			throw new RuntimeException();
		}

		// 完了フラグをBoolean化
		boolean isCompleted = SystemFlg.codeToBoolean(entity.getKanryoFlg());

		// 不受任
		if (isCompleted && entity.getJuninDate() == null) {
			return AnkenStatus.FUJUNIN.getCd();
		}

		// 完了
		if (isCompleted) {
			return AnkenStatus.KANRYO.getCd();
		}

		// 以下、暗黙的に未完了

		// 完了待ち
		if (entity.getKanryoDate() != null) {
			return AnkenStatus.KANRYO_MACHI.getCd();
		}

		// 精算待ち
		if (entity.getJikenKanryoDate() != null) {
			return AnkenStatus.SEISAN_MACHI.getCd();
		}

		// 進行中
		if (entity.getJuninDate() != null) {
			return AnkenStatus.SHINKOCHU.getCd();
		}

		// 面談予定 ※現在初回面談は閉じられているが、過去データに該当データケースが残っている可能性がある
		if (entity.getShokaiMendanDate() != null) {
			return AnkenStatus.MENDAN_YOTEI.getCd();
		}

		// 相談中
		return AnkenStatus.SODAN.getCd();
	}

	/**
	 * 案件管理：担当者プルダウン
	 *
	 * @param accountEntityList 有効アカウント
	 * @param tantoType 担当種別
	 * @param ankenTantoAccountBeanList すでに設定されている案件担当
	 * @return 担当者プルダウン(すでに登録されているデータを含む)
	 */
	public List<SelectOptionForm> getTantoOptionList(List<MAccountEntity> accountEntityList, TantoType tantoType,
			List<AnkenTantoAccountBean> ankenTantoAccountBeanList) {

		// プルダウン用オブジェクト
		List<SelectOptionForm> sof = new ArrayList<>();

		// フィルター条件
		Predicate<MAccountEntity> filter = entity -> {
			if (TantoType.SALES_OWNER == tantoType) {
				return AccountType.LAWYER.equalsByCode(entity.getAccountType()) &&
						SystemFlg.FLG_ON.equalsByCode(entity.getAccountOwnerFlg());
			} else if (TantoType.LAWYER == tantoType) {
				return AccountType.LAWYER.equalsByCode(entity.getAccountType());
			} else if (TantoType.JIMU == tantoType) {
				return AccountType.JIMU.equalsByCode(entity.getAccountType());
			} else {
				// 想定外のケース
				throw new RuntimeException();
			}
		};

		// 種別ごとのプルダウンを作成
		List<MAccountEntity> typeOfList = accountEntityList.stream().filter(filter).collect(Collectors.toList());
		typeOfList.forEach(entity -> sof
				.add(new SelectOptionForm(entity.getAccountSeq(), PersonName.fromEntity(entity).getName())));

		// 登録データが無い場合はここで返却
		if (ListUtils.isEmpty(ankenTantoAccountBeanList)) {
			return sof;
		}

		// Mapper条件
		BiFunction<TantoType, List<AnkenTantoAccountBean>, List<Long>> typeByIdMapper = (type, entities) -> {
			return entities.stream()
					.filter(e -> type.equalsByCode(e.getTantoType()))
					.map(AnkenTantoAccountBean::getAccountSeq)
					.collect(Collectors.toList());
		};

		// 無条件にプルダウンに含めるユーザーをプルダウンに追加
		List<Long> includeAccountSeq = typeByIdMapper.apply(tantoType, ankenTantoAccountBeanList);
		List<Long> deletedAccountSeq = new ArrayList<>(
				LoiozCollectionUtils.subtract(includeAccountSeq, typeOfList
						.stream()
						.map(MAccountEntity::getAccountSeq)
						.collect(Collectors.toList())));
		List<MAccountEntity> deletedAccountEntities = mAccountDao.selectBySeq(deletedAccountSeq);
		deletedAccountEntities.forEach(entity -> sof
				.add(new SelectOptionForm(entity.getAccountSeq(), PersonName.fromEntity(entity).getName())));

		return sof;
	}

	/**
	 * 案件の売上計上先プルダウンを生成
	 * 
	 * @param ankenType
	 * @param ankenTantoAccountBeanList
	 * @param accountEntityList
	 * @return
	 */
	public List<SelectOptionForm> getSalesOwnerOptionList(AnkenType ankenType,
			List<AnkenTantoAccountBean> ankenTantoAccountBeanList, List<MAccountEntity> accountEntityList) {
		// 売上計上先
		List<SelectOptionForm> salesOwnerOptionList = this.getTantoOptionList(accountEntityList,
				TantoType.SALES_OWNER, ankenTantoAccountBeanList);

		// 担当弁護士
		List<SelectOptionForm> lawyerOptionList = this.getTantoOptionList(accountEntityList,
				TantoType.LAWYER, ankenTantoAccountBeanList);

		// 案件種別で判定
		if (AnkenType.JIMUSHO.equals(ankenType)) {
			// 事務所案件の場合：売上計上先は経営者権限がある弁護士のみ
			return salesOwnerOptionList;
		} else if (AnkenType.KOJIN.equals(ankenType)) {
			// 個人事件の場合：弁護士全員が選択可
			return lawyerOptionList;
		} else {
			// 想定外の案件種別
			throw new RuntimeException("想定外の案件種別 [ankenType=" + ankenType + "]");
		}
	}

	/**
	 * 案件の担当弁護士プルダウンを生成
	 * 
	 * @param ankenTantoAccountBeanList
	 * @param accountEntityList
	 * @return
	 */
	public List<SelectOptionForm> getLawyerOptionList(List<AnkenTantoAccountBean> ankenTantoAccountBeanList,
			List<MAccountEntity> accountEntityList) {
		// 担当弁護士
		List<SelectOptionForm> lawyerOptionList = this.getTantoOptionList(accountEntityList,
				TantoType.LAWYER, ankenTantoAccountBeanList);

		return lawyerOptionList;
	}

	/**
	 * 案件の担当事務プルダウンを生成
	 * 
	 * @param ankenTantoAccountBeanList
	 * @param accountEntityList
	 * @return
	 */
	public List<SelectOptionForm> getTantoJimuOptionList(List<AnkenTantoAccountBean> ankenTantoAccountBeanList,
			List<MAccountEntity> accountEntityList) {
		// 担当事務
		List<SelectOptionForm> tantoJimuOptionList = this.getTantoOptionList(accountEntityList,
				TantoType.JIMU, ankenTantoAccountBeanList);

		return tantoJimuOptionList;
	}

	/**
	 * 担当のフォーム情報を作成する
	 *
	 * @param entityList 案件担当DB取得値
	 * @return 担当フォーム情報
	 */
	public List<AnkenTantoSelectInputForm> createTantoList(List<TAnkenTantoEntity> entityList) {

		// DB取得値をフォーム情報に変換
		List<AnkenTantoSelectInputForm> itemList = entityList.stream()
				.map(entity -> new AnkenTantoSelectInputForm(entity.getAccountSeq(), SystemFlg.codeToBoolean(entity.getAnkenMainTantoFlg())))
				.filter(item -> !item.isEmpty())
				.collect(Collectors.toCollection(ArrayList::new));

		// 担当追加上限まで空データを追加
		for (int i = itemList.size(); i < CommonConstant.ANKEN_TANTO_ADD_LIMIT; i++) {
			itemList.add(new AnkenTantoSelectInputForm());
		}
		return itemList;
	}

	/**
	 * 案件ID案件担当リストを生成
	 *
	 * @param ankenId
	 * @return
	 */
	public List<AnkenTantoDto> getAnkenTantoListByAnkenId(Long ankenId) {
		// 案件IDリストを取得
		List<Long> ankenIdList = Arrays.asList(ankenId);
		List<AnkenTantoDto> ankenTantoList = tAnkenTantoDao.selectByAnkenIdListAndTantoType(ankenIdList);
		return ankenTantoList;
	}

	/**
	 * 優先担当者名を取得する
	 * 
	 * @param ankenTantoDtoList
	 * @param tantoType
	 * @return
	 */
	public AnkenTantoDto getMainTantoDto(List<AnkenTantoDto> ankenTantoDtoList, @NotNull TantoType tantoType) {

		// 引数のリストがなければ、null
		if (ListUtils.isEmpty(ankenTantoDtoList)) {
			return null;
		}

		// 指定した担当種別でFilter
		List<AnkenTantoDto> findByType = ankenTantoDtoList.stream().filter(e -> {
			return tantoType == TantoType.of(e.getTantoType());
		}).collect(Collectors.toList());

		// 担当種別が登録されていない場合はnull
		if (ListUtils.isEmpty(findByType)) {
			return null;
		}

		// メイン担当フラグを持っている人がいるか確認する。
		boolean isMainUser = findByType.stream()
				.anyMatch(entity -> SystemFlg.codeToBoolean(entity.getAnkenMainTantoFlg()));

		if (isMainUser) {
			return findByType.stream()
					.filter(entity -> SystemFlg.codeToBoolean(entity.getAnkenMainTantoFlg())) // メイン担当のみに絞り込み
					.sorted(Comparator.comparing(AnkenTantoDto::getTantoTypeBranchNo)) // ブランチNoでソート
					.findFirst().orElse(null); // 一番最初のアカウント名を返却
		} else {
			return findByType.stream()
					.sorted(Comparator.comparing(AnkenTantoDto::getTantoTypeBranchNo)) // ブランチNoでソート
					.findFirst().orElse(null);// 一番最初のアカウント名を返却
		}

	}

	/**
	 * 案件に紐づく関与者関係者Bean情報を取得する
	 * 
	 * @param ankenId
	 * @param kanyoshaType
	 * @param systemFlg
	 * @return
	 */
	public List<AnkenRelatedKanyoshaBean> getAnkenRelatedKanyoshaBeanList(Long ankenId, KanyoshaType kanyoshaType, SystemFlg systemFlg) {
		return tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaBeanByParams(ankenId, kanyoshaType.getCd(), systemFlg.getCd());
	}

	/**
	 * 案件-関与者関係者の画面表示Dtoリストを作成する
	 * 
	 * @param kanyoshaBeanList
	 * @param dairininKanyoshaBeanList
	 * @return
	 */
	public List<AnkenKanyoshaViewDto> generateKanyoshaViewDto(List<AnkenRelatedKanyoshaBean> kanyoshaBeanList, List<AnkenRelatedKanyoshaBean> dairininKanyoshaBeanList) {

		Set<Long> personSeqSet = kanyoshaBeanList.stream().map(AnkenRelatedKanyoshaBean::getPersonId).collect(Collectors.toSet());
		Set<Long> dairininPersonSeqSet = dairininKanyoshaBeanList.stream().map(AnkenRelatedKanyoshaBean::getPersonId).collect(Collectors.toSet());

		List<Long> margedParsonIdList = LoiozCollectionUtils.mergeLists(new ArrayList<>(personSeqSet), new ArrayList<>(dairininPersonSeqSet));

		List<TPersonAddLawyerEntity> tPersonAddLawyerEntities = tPersonAddLawyerDao.selectCustomerTypeLawyerAddInformationByPersonId(margedParsonIdList);
		Map<Long, TPersonAddLawyerEntity> personIdToPersonAddLawyerEntityMap = tPersonAddLawyerEntities.stream().collect(Collectors.toMap(TPersonAddLawyerEntity::getPersonId, Function.identity()));

		// 関与者SEQをキーとした、代理人情報のマップを作成する
		Map<Long, AnkenKanyoshaDairininViewDto> kanyoshaSeqToDairininMap = dairininKanyoshaBeanList.stream().collect(Collectors.toMap(AnkenRelatedKanyoshaBean::getKanyoshaSeq, e -> {
			AnkenKanyoshaDairininViewDto ankenKanyoshaDairininViewDto = new AnkenKanyoshaDairininViewDto();
			ankenKanyoshaDairininViewDto.setKanyoshaSeq(e.getKanyoshaSeq());
			ankenKanyoshaDairininViewDto.setPersonId(PersonId.of(e.getPersonId()));
			ankenKanyoshaDairininViewDto.setPersonAttribute(PersonAttribute.of(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType()));
			ankenKanyoshaDairininViewDto.setCustomerType(CustomerType.of(e.getCustomerType()));
			ankenKanyoshaDairininViewDto.setKanyoshaName(new PersonName(e.getCustomerNameSei(), e.getCustomerNameMei(), null, null).getName());
			ankenKanyoshaDairininViewDto.setKankei(e.getKankei());
			ankenKanyoshaDairininViewDto.setRemarks(e.getRemarks());
			ankenKanyoshaDairininViewDto.setJimushoName(personIdToPersonAddLawyerEntityMap.getOrDefault(e.getPersonId(), new TPersonAddLawyerEntity()).getJimushoName());
			return ankenKanyoshaDairininViewDto;
		}));

		// 案件関与者関係者情報リストを作成
		List<AnkenKanyoshaViewDto> ankenKanyoshaViewDtoList = kanyoshaBeanList.stream().map(e -> {
			AnkenKanyoshaViewDto ankenKanyoshaViewDto = new AnkenKanyoshaViewDto();
			ankenKanyoshaViewDto.setKanyoshaSeq(e.getKanyoshaSeq());
			ankenKanyoshaViewDto.setAnkenId(e.getAnkenId());
			ankenKanyoshaViewDto.setPersonId(e.getPersonId());
			ankenKanyoshaViewDto.setRelatedKanyoshaSeq(e.getRelatedKanyoshaSeq());
			ankenKanyoshaViewDto.setCustomerId(e.getCustomerId() != null ? CustomerId.of(e.getCustomerId()) : null);
			ankenKanyoshaViewDto.setPersonAttribute(PersonAttribute.of(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType()));
			ankenKanyoshaViewDto.setCustomerType(CustomerType.of(e.getCustomerType()));
			ankenKanyoshaViewDto.setKanyoshaType(e.getKanyoshaType());
			ankenKanyoshaViewDto.setKanyoshaName(new PersonName(e.getCustomerNameSei(), e.getCustomerNameMei(), null, null).getName());
			ankenKanyoshaViewDto.setKankei(e.getKankei());
			ankenKanyoshaViewDto.setRemarks(e.getRemarks());
			ankenKanyoshaViewDto.setAnkenKanyoshaDairininViewDto(kanyoshaSeqToDairininMap.get(e.getRelatedKanyoshaSeq()));
			return ankenKanyoshaViewDto;
		}).collect(Collectors.toList());

		return ankenKanyoshaViewDtoList;
	}

	/**
	 * 刑事・民事切替処理
	 *
	 * <pre>
	 * ================== 修正テーブル =======================
	 * 民事 → 刑事 
	 * ・登録：案件-刑事情報(私選)
	 * ・更新：案件基本情報(分野のみ)
	 * ・削除：案件-相手方情報
	 * 刑事 → 民事
	 * ・更新：案件基本情報(分野のみ)
	 * ・削除：案件-相手方情報
	 * 　　　　案件-勾留情報
	 * 　　　　案件-接見情報
	 * 　　　　案件-事件情報
	 * 　　　　案件-捜査機関情報
	 * 
	 * 民事 → 民事、刑事 → 刑事
	 * 　本メソッドでは想定しない
	 * </pre>
	 *
	 * ※本メソッドは、publicメソッドには変更しないこと。 １トランザクション内で分野切り替え、その後の更新を行う必要があるため
	 * Controllerからの呼び出しを行わないこと
	 *
	 * @param ankenId
	 * @param inputBunyaId
	 */
	public void changeBunya(Long ankenId, Long inputBunyaId) {

		// データの取得
		TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);

		// entityに分野を設定
		tAnkenEntity.setBunyaId(inputBunyaId);

		if (commonBunyaService.isKeiji(inputBunyaId)) {
			// 民事 → 刑事
			this.changeMinji2Keiji(tAnkenEntity);
		} else {
			// 刑事 → 民事
			this.changeKeiji2Minji(tAnkenEntity);
		}
	}

	/**
	 * 初回面談予定登録処理
	 *
	 * @param form フォーム入力値
	 */
	public void createShokaiMendanSchedule(ScheduleInputForm form) {

		Long ankenId = form.getAnkenId();
		Long customerId = form.getCustomerId();
		TAnkenCustomerEntity entity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 案件-顧客が存在しない場合
		if (entity == null) {
			throw new DataNotFoundException("案件-顧客が存在しません。[ankenId=" + ankenId + ", customerId=" + customerId + "]");
		}

		// 既に初回面談予定が紐づいている場合
		if (entity.getShokaiMendanScheduleSeq() != null) {
			throw new RuntimeException("既に予定を作成済みです。");
		}

		// 予定を登録
		Long scheduleSeq = scheduleCommonService.create(form);

		// 案件-顧客を更新
		entity.setShokaiMendanDate(form.getDateFrom());
		entity.setShokaiMendanScheduleSeq(scheduleSeq);
		entity.setAnkenStatus(this.getCurrentAnkenStatus(entity));
		tAnkenCustomerDao.update(entity);
	}

	/**
	 * 初回面談予定更新処理
	 *
	 * @param form フォーム入力値
	 */
	public void updateShokaiMendanSchedule(ScheduleInputForm form) {

		Long ankenId = form.getAnkenId();
		Long customerId = form.getCustomerId();
		TAnkenCustomerEntity entity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 案件-顧客が存在しない場合
		if (entity == null) {
			throw new DataNotFoundException("案件-顧客が存在しません。[ankenId=" + ankenId + ", customerId=" + customerId + "]");
		}

		// 予定を更新
		scheduleCommonService.update(form);

		// 案件-顧客を更新
		entity.setShokaiMendanDate(form.getDateFrom());
		entity.setAnkenStatus(this.getCurrentAnkenStatus(entity));
		tAnkenCustomerDao.update(entity);
	}

	/**
	 * 案件と顧客の紐づけを解除する
	 * 
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @throws AppException
	 */
	public void deleteAnkenCustomer(Long ankenId, Long customerId) throws AppException {

		// 案件IDと顧客IDに紐づく 案件-顧客情報を取得します。
		TAnkenCustomerEntity ankenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);
		if (ankenCustomerEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TKeijiAnkenCustomerEntity tKeijiAnkenCustomerEntity = tKeijiAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);
		List<TAnkenSekkenEntity> tAnkenSekkenEntities = tAnkenSekkenDao.selectByAnkenIdAndCustomerId(ankenId, customerId);
		List<TAnkenKoryuEntity> tAnkenKoryuEntities = tAnkenKoryuDao.selectByAnkenIdAndCustomerId(ankenId, customerId);
		List<TAnkenJikenEntity> tAnkenJikenEntities = tAnkenJikenDao.selectByAnkenIdCustomerId(ankenId, customerId);

		TScheduleEntity tScheduleEntity = null;
		if (ankenCustomerEntity.getShokaiMendanScheduleSeq() != null) {
			tScheduleEntity = tScheduleDao.selectBySeq(ankenCustomerEntity.getShokaiMendanScheduleSeq());
			tScheduleEntity.setAnkenId(null);
		}

		try {
			// 案件と顧客の紐づけを解除します。
			tAnkenCustomerDao.delete(ankenCustomerEntity);
			// 名簿属性のFlgの更新
			commonCustomerService.updatePersonAttributeFlgs(customerId);

			// 刑事案件-顧客情報の削除
			if (tKeijiAnkenCustomerEntity != null) {
				tKeijiAnkenCustomerDao.delete(tKeijiAnkenCustomerEntity);
			}

			// 顧客案件-接見情報の削除
			if (!ListUtils.isEmpty(tAnkenSekkenEntities)) {
				tAnkenSekkenDao.delete(tAnkenSekkenEntities);
			}

			// 顧客案件-勾留情報の削除
			if (!ListUtils.isEmpty(tAnkenKoryuEntities)) {
				tAnkenKoryuDao.delete(tAnkenKoryuEntities);
			}

			// 顧客案件-事件情報の削除
			if (!ListUtils.isEmpty(tAnkenJikenEntities)) {
				tAnkenJikenDao.delete(tAnkenJikenEntities);
			}

			// スケジュール情報の更新
			if (tScheduleEntity != null) {
				tScheduleDao.update(tScheduleEntity);
			}

		} catch (OptimisticLockingFailureException e) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, e);
		}
	}

	/**
	 * 業務履歴の紐づけを解除します。<br>
	 * 
	 * <pre>
	 * 履歴登録元が顧客の場合：案件との紐づけを解除
	 * 履歴登録元が案件の場合：顧客との紐づけを解除
	 * </pre>
	 * 
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @throws AppException
	 */
	public void deleteGyomuHistory(Long ankenId, Long customerId) throws AppException {

		// 履歴登録元が「顧客」の業務履歴
		deleteTransitionFromCustomer(ankenId, customerId);

		// 履歴登録元が「案件」の業務履歴
		deleteTransitionFromAnken(ankenId, customerId);
	}

	/**
	 * 初回面談予定削除処理
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 */
	public void deleteShokaiMendanSchedule(Long ankenId, Long customerId) {

		TAnkenCustomerEntity entity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 案件-顧客が存在しない場合
		if (entity == null) {
			throw new DataNotFoundException("案件-顧客が存在しません。[ankenId=" + ankenId + ", customerId=" + customerId + "]");
		}

		// 初回面談予定が紐づいていない場合
		if (entity.getShokaiMendanScheduleSeq() == null) {
			throw new RuntimeException("予定が存在しません。");
		}

		// 予定を削除
		scheduleCommonService.delete(entity.getShokaiMendanScheduleSeq());

		// 案件-顧客を更新
		entity.setShokaiMendanDate(null);
		entity.setShokaiMendanScheduleSeq(null);
		entity.setAnkenStatus(this.getCurrentAnkenStatus(entity));
		tAnkenCustomerDao.update(entity);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================
	/**
	 * 履歴登録元が「顧客」の業務履歴の紐づけを削除します。
	 * 
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @throws AppException
	 */
	private void deleteTransitionFromCustomer(Long ankenId, Long customerId) throws AppException {

		// 削除対象の顧客に紐づく業務履歴SEQリストを取得します。
		List<Long> gyomuHistorySeqList = tGyomuHistoryCustomerDao.selectSeqByCustomerId(customerId);

		// 業務履歴-案件情報を取得します。
		List<TGyomuHistoryAnkenEntity> deleteTargetEntityList = tGyomuHistoryAnkenDao
				.selectByAnkenIdAndGyomuHistorySeqList(ankenId, gyomuHistorySeqList);

		// 案件との紐づけを解除します。
		try {
			tGyomuHistoryAnkenDao.delete(deleteTargetEntityList);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * 履歴登録元が「案件」の業務履歴の紐づけを削除します。
	 * 
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @throws AppException
	 */
	private void deleteTransitionFromAnken(Long ankenId, Long customerId) throws AppException {

		// 案件に紐づく業務履歴SEQリストを取得します。
		List<Long> gyomuHistorySeqList = tGyomuHistoryAnkenDao.selectSeqByAnkenId(ankenId);

		// 業務履歴-顧客情報を取得します。
		List<TGyomuHistoryCustomerEntity> deleteTargetEntityList = tGyomuHistoryCustomerDao
				.selectByCustomerIdAndGyomuHistorySeqList(customerId, gyomuHistorySeqList);

		// 案件との紐づけを解除します。
		try {
			tGyomuHistoryCustomerDao.delete(deleteTargetEntityList);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * 民事 → 刑事の切替更新処理
	 *
	 * <pre>
	 * 更新：TAnkenEntity
	 * 削除：TAnkenRelatedKanyoshaEntity
	 * 登録：TAnkenAddKeijiEntity
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 */
	private void changeMinji2Keiji(TAnkenEntity tAnkenEntity) {

		// 削除するデータを取得する
		List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectByAnkenId(tAnkenEntity.getAnkenId());

		// 追加するデータを作成
		TAnkenAddKeijiEntity tAnkenAddKeijiEntity = new TAnkenAddKeijiEntity();
		tAnkenAddKeijiEntity.setAnkenId(tAnkenEntity.getAnkenId());
		tAnkenAddKeijiEntity.setLawyerSelectType(BengoType.SHISEN.getCd());

		// 更新処理
		tAnkenDao.update(tAnkenEntity);
		if (!tAnkenRelatedKanyoshaEntities.isEmpty()) {
			tAnkenRelatedKanyoshaDao.delete(tAnkenRelatedKanyoshaEntities);
		}
		tAnkenAddKeijiDao.insert(tAnkenAddKeijiEntity);
	}

	/**
	 * 刑事 → 民事の切替更新処理
	 *
	 * <pre>
	 * 更新：TAnkenEntity
	 * 削除：TAnkenAddKeijiEntity,TAnkenRelatedKanyoshaEntity,TAnkenSekkenEntity,TAnkenKoryuEntity,TAnkenJikenEntity,TAnkenSosakikanEntity
	 * 登録：なし
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 */
	private void changeKeiji2Minji(TAnkenEntity tAnkenEntity) {

		// 削除データの取得
		TAnkenAddKeijiEntity tAnkenAddKeijiEntity = tAnkenAddKeijiDao.selectByAnkenId(tAnkenEntity.getAnkenId());
		List<TKeijiAnkenCustomerEntity> tKeijiAnkenCustomerEntities = tKeijiAnkenCustomerDao.selectByAnkenId(tAnkenEntity.getAnkenId());
		List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectByAnkenId(tAnkenEntity.getAnkenId());
		List<TAnkenSekkenEntity> tAnkenSekkenEntities = tAnkenSekkenDao.selectByAnkenId(tAnkenEntity.getAnkenId());
		List<TAnkenKoryuEntity> tAnkenKoryuEntities = tAnkenKoryuDao.selectByAnkenId(tAnkenEntity.getAnkenId());
		List<TAnkenJikenEntity> tAnkenJikenEntities = tAnkenJikenDao.selectByAnkenId(tAnkenEntity.getAnkenId());
		List<TAnkenSosakikanEntity> tAnkenSosakikanEntities = tAnkenSosakikanDao.selectByAnkenId(tAnkenEntity.getAnkenId());

		// 更新処理
		tAnkenDao.update(tAnkenEntity);
		if (tAnkenAddKeijiEntity != null) {
			tAnkenAddKeijiDao.delete(tAnkenAddKeijiEntity);
		}
		if (!ListUtils.isEmpty(tKeijiAnkenCustomerEntities)) {
			tKeijiAnkenCustomerDao.batchDelete(tKeijiAnkenCustomerEntities);
		}
		if (!tAnkenRelatedKanyoshaEntities.isEmpty()) {
			tAnkenRelatedKanyoshaDao.delete(tAnkenRelatedKanyoshaEntities);
		}
		if (!tAnkenSekkenEntities.isEmpty()) {
			tAnkenSekkenDao.delete(tAnkenSekkenEntities);
		}
		if (!tAnkenKoryuEntities.isEmpty()) {
			tAnkenKoryuDao.delete(tAnkenKoryuEntities);
		}
		if (!tAnkenJikenEntities.isEmpty()) {
			tAnkenJikenDao.delete(tAnkenJikenEntities);
		}
		if (!tAnkenSosakikanEntities.isEmpty()) {
			tAnkenSosakikanDao.delete(tAnkenSosakikanEntities);
		}
	}

	/**
	 * 名簿IDが顧客の案件、当事者・関与者に設定されている案件を取得します。<br>
	 * 取得順は顧客案件の未完了、完了、不受任、関与案件の未完了、完了です。<br>
	 * （関与案件のステータスは、顧客全員のステータスが完了していれば完了、それ以外は未完了です）<br>
	 * 
	 * @param personId
	 * @param options
	 */
	private List<AnkenDto> generateAllAnkenListByPersonId(Long personId, SelectOptions options) {

		// ------------------------------------------------------------
		// 案件情報
		// ------------------------------------------------------------
		List<AnkenBean> ankenBeanList = tAnkenCustomerDao.selectAllAnkenRelatedToPersonId(personId, options);

		// 案件IDリストを取得
		List<Long> ankenIdList = ankenBeanList.stream().map(AnkenBean::getAnkenId).collect(Collectors.toList());

		// ------------------------------------------------------------
		// 案件 - 顧客 情報
		// ------------------------------------------------------------
		// 案件IDに紐づく顧客情報の取得
		List<PersonInfoForAnkenDto> customerNameList = tAnkenCustomerDao.selectPersonInfoForAnkenByAnkenId(ankenIdList);

		// ------------------------------------------------------------
		// 関与者情報
		// ------------------------------------------------------------
		// 案件IDに紐づく関与者情報の取得
		List<AnkenKanyoshaBean> ankenKanyoshaList = tKanyoshaDao.selectAnkenRelatedKanyoshaByAnkenIdList(ankenIdList);

		// 相手方
		List<AnkenKanyoshaBean> aitegataList = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.AITEGATA.equalsByCode(bean.getKanyoshaType()))
				.collect(Collectors.toList());
		// 共犯者
		List<AnkenKanyoshaBean> kyohanshaList = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.KYOHANSHA.equalsByCode(bean.getKanyoshaType()))
				.collect(Collectors.toList());
		// 被害者
		List<AnkenKanyoshaBean> higaishaList = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.HIGAISHA.equalsByCode(bean.getKanyoshaType()))
				.collect(Collectors.toList());

		// 当事者・関与者
		List<AnkenKanyoshaBean> tojishaKanyoshaList = ankenKanyoshaList.stream()
				.filter(bean -> StringUtils.isEmpty(bean.getKanyoshaType()))
				.collect(Collectors.toList());

		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// ------------------------------------------------------------
		// 画面表示用に加工
		// ------------------------------------------------------------
		List<AnkenDto> ankenList = ankenBeanList.stream()
				.map(entity -> AnkenDto.builder()
						.transitionCustomerId(personId)
						.ankenId(AnkenId.of(entity.getAnkenId()))
						.bunya(commonBunyaService.getBunyaName(bunyaMap.get(entity.getBunyaId())))
						.bunyaType(commonBunyaService.isKeiji(entity.getBunyaId()) ? BunyaType.KEIJI.getCd() : BunyaType.MINJI.getCd())
						.ankenName(StringUtils.isEmpty(entity.getAnkenName()) ? "(案件名未入力)" : entity.getAnkenName())
						.ankenType(AnkenType.of(entity.getAnkenType()))
						.customerList(customerNameList.stream().filter(dto -> entity.getAnkenId().equals(dto.getAnkenId()))
								.sorted(Comparator.comparing(PersonInfoForAnkenDto::getPersonId))
								.map(dto -> Customer.builder().personId(dto.getPersonId())
										.customerName(dto.getCustomerName())
										.build())
								.collect(Collectors.toList()))
						.aitegataList(aitegataList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
								.map(bean -> Aitegata.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
										.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
										.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
										.build())
								.collect(Collectors.toList()))
						.kyohanshaList(kyohanshaList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
								.map(bean -> Kyohansha.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
										.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
										.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
										.build())
								.collect(Collectors.toList()))
						.higaishaList(higaishaList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
								.map(bean -> Higaisha.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
										.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
										.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
										.build())
								.collect(Collectors.toList()))
						.tojishaKanyoshaList(tojishaKanyoshaList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getDispOrder))
								.map(bean -> TojishaKanyosha.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaName(bean.getKanyoshaName())
										.build())
								.collect(Collectors.toList()))
						.ankenStatus(entity.getAnkenStatus())
						// 関与案件の未完了ステータスは空で取得するため、ステータス名も空にする
						.ankenStatusName(StringUtils.isEmpty(entity.getAnkenStatus()) ? "" : AnkenStatus.of(entity.getAnkenStatus()).getVal())
						.isCompAnken(AnkenStatus.isComp(entity.getAnkenStatus()))
						.saibanIdList(null)
						.saibanStatus(null)
						.build())
				.collect(Collectors.toList());

		return ankenList;

	}

	/**
	 * customerIdが顧客の案件リストを生成する
	 *
	 * @param customerId 顧客ID
	 * @return
	 */
	private List<AnkenDto> generateAnkenListByCustomerId(Long customerId) {

		// ------------------------------------------------------------
		// 案件情報
		// ------------------------------------------------------------
		List<TAnkenEntity> tAnkenEntityList = tAnkenDao.selectByCustomerId(customerId);

		// 案件IDリストを取得
		List<Long> ankenIdList = tAnkenEntityList.stream().map(TAnkenEntity::getAnkenId).collect(Collectors.toList());

		// ------------------------------------------------------------
		// 案件 - 顧客 情報
		// ------------------------------------------------------------
		// 顧客IDに紐づく案件-顧客情報一覧の取得
		List<TAnkenCustomerEntity> tAnkenCustomerEntityList = tAnkenCustomerDao.selectByCustomerId(customerId);

		// 案件IDから案件ステータスCDを取得するMap
		Map<Long, String> ankenStatusMap = tAnkenCustomerEntityList.stream()
				.collect(Collectors.toMap(TAnkenCustomerEntity::getAnkenId, TAnkenCustomerEntity::getAnkenStatus));

		// 案件IDに紐づく顧客情報の取得
		List<PersonInfoForAnkenDto> customerNameList = tAnkenCustomerDao.selectPersonInfoForAnkenByAnkenId(ankenIdList);

		// ------------------------------------------------------------
		// 関与者情報
		// ------------------------------------------------------------
		// 案件IDに紐づく関与者情報の取得
		List<AnkenKanyoshaBean> ankenKanyoshaList = tKanyoshaDao.selectAnkenRelatedKanyoshaByAnkenIdList(ankenIdList);

		// 相手方
		List<AnkenKanyoshaBean> aitegataList = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.AITEGATA.equalsByCode(bean.getKanyoshaType()))
				.collect(Collectors.toList());
		// 共犯者
		List<AnkenKanyoshaBean> kyohanshaList = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.KYOHANSHA.equalsByCode(bean.getKanyoshaType()))
				.collect(Collectors.toList());
		// 被害者
		List<AnkenKanyoshaBean> higaishaList = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.HIGAISHA.equalsByCode(bean.getKanyoshaType()))
				.collect(Collectors.toList());

		// 当事者・関与者
		List<AnkenKanyoshaBean> tojishaKanyoshaList = ankenKanyoshaList.stream()
				.filter(bean -> StringUtils.isEmpty(bean.getKanyoshaType()))
				.collect(Collectors.toList());

		// ------------------------------------------------------------
		// 裁判情報
		// ------------------------------------------------------------
		// 裁判ステータスの取得
		List<TSaibanEntity> tSaibanEntityList = tSaibanDao.selectByAnkenIdList(ankenIdList);

		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// ------------------------------------------------------------
		// 画面表示用に加工
		// ------------------------------------------------------------
		List<AnkenDto> ankenList = tAnkenEntityList.stream()
				.map(entity -> AnkenDto.builder()
						.transitionCustomerId(customerId)
						.ankenId(AnkenId.of(entity.getAnkenId()))
						.bunya(commonBunyaService.getBunyaName(bunyaMap.get(entity.getBunyaId())))
						.bunyaType(commonBunyaService.isKeiji(entity.getBunyaId()) ? BunyaType.KEIJI.getCd() : BunyaType.MINJI.getCd())
						.ankenName(StringUtils.isEmpty(entity.getAnkenName()) ? "(案件名未入力)" : entity.getAnkenName())
						.ankenType(AnkenType.of(entity.getAnkenType()))
						.customerList(customerNameList.stream().filter(dto -> entity.getAnkenId().equals(dto.getAnkenId()))
								.sorted(Comparator.comparing(PersonInfoForAnkenDto::getPersonId))
								.map(dto -> Customer.builder().personId(dto.getPersonId())
										.customerName(dto.getCustomerName())
										.build())
								.collect(Collectors.toList()))
						.aitegataList(aitegataList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
								.map(bean -> Aitegata.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
										.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
										.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
										.build())
								.collect(Collectors.toList()))
						.kyohanshaList(kyohanshaList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
								.map(bean -> Kyohansha.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
										.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
										.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
										.build())
								.collect(Collectors.toList()))
						.higaishaList(higaishaList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
								.map(bean -> Higaisha.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
										.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
										.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
										.build())
								.collect(Collectors.toList()))
						.tojishaKanyoshaList(tojishaKanyoshaList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getDispOrder))
								.map(bean -> TojishaKanyosha.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaName(bean.getKanyoshaName())
										.build())
								.collect(Collectors.toList()))
						.ankenStatus(ankenStatusMap.get(entity.getAnkenId()))
						.ankenStatusName(AnkenStatus.of(ankenStatusMap.get(entity.getAnkenId())).getVal())
						.isCompAnken(AnkenStatus.isComp(ankenStatusMap.get(entity.getAnkenId())))
						.saibanIdList(tSaibanEntityList.stream().filter(e -> e.getAnkenId().equals(entity.getAnkenId()))
								.map(SaibanId::fromEntity).collect(Collectors.toList()))
						.saibanStatus(tSaibanEntityList.stream().filter(e -> e.getAnkenId().equals(entity.getAnkenId()))
								.map(e -> "(" + SaibanStatus.calc(e.getSaibanStartDate(), e.getSaibanEndDate()).getVal() + ")")
								.collect(Collectors.joining("\r\n")))
						.build())
				.collect(Collectors.toList());

		return ankenList;
	}

	/**
	 * customerIdが関与者の関連案件リストを生成する
	 *
	 * @param customerId 顧客ID
	 * @return
	 */
	private List<AnkenDto> generateRelatedAnkenListByCustomerId(Long customerId) {

		// ------------------------------------------------------------
		// 案件情報
		// ------------------------------------------------------------
		List<TAnkenEntity> tAnkenEntityList = tAnkenDao.selectRelatedAnkenByPersonId(customerId);

		// 案件IDリストを取得
		List<Long> ankenIdList = tAnkenEntityList.stream().map(TAnkenEntity::getAnkenId).collect(Collectors.toList());

		// ------------------------------------------------------------
		// 案件 - 顧客 情報
		// ------------------------------------------------------------
		// 案件IDに紐づく案件-顧客情報一覧の取得
		List<AnkenKanryoFlgGroupByAnkenBean> kanryoFlgList = tAnkenCustomerDao.selectKanryoFlgByAnkenId(ankenIdList);

		// 完了フラグをBooleanに変換する
		Function<AnkenKanryoFlgGroupByAnkenBean, Boolean> convertKanryoFlgFunc = e -> {
			return SystemFlg.codeToBoolean(e.getKanryoFlg());
		};

		// 案件ID、完了フラグのMapを作成。案件ステータスCDを取得するMap
		Map<Long, Boolean> ankenStatusMap = kanryoFlgList.stream()
				.collect(Collectors.toMap(AnkenKanryoFlgGroupByAnkenBean::getAnkenId, convertKanryoFlgFunc));

		// 案件IDに紐づく顧客情報の取得
		List<PersonInfoForAnkenDto> customerNameList = tAnkenCustomerDao.selectPersonInfoForAnkenByAnkenId(ankenIdList);

		// ------------------------------------------------------------
		// 関与者情報
		// ------------------------------------------------------------
		// 案件IDに紐づく関与者情報の取得
		List<AnkenKanyoshaBean> ankenKanyoshaList = tKanyoshaDao.selectAnkenRelatedKanyoshaByAnkenIdList(ankenIdList);

		// 相手方
		List<AnkenKanyoshaBean> aitegataList = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.AITEGATA.equalsByCode(bean.getKanyoshaType()))
				.collect(Collectors.toList());
		// 共犯者
		List<AnkenKanyoshaBean> kyohanshaList = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.KYOHANSHA.equalsByCode(bean.getKanyoshaType()))
				.collect(Collectors.toList());
		// 被害者
		List<AnkenKanyoshaBean> higaishaList = ankenKanyoshaList.stream()
				.filter(bean -> KanyoshaType.HIGAISHA.equalsByCode(bean.getKanyoshaType()))
				.collect(Collectors.toList());

		// 当事者・関与者
		List<AnkenKanyoshaBean> tojishaKanyoshaList = ankenKanyoshaList.stream()
				.filter(bean -> StringUtils.isEmpty(bean.getKanyoshaType()))
				.collect(Collectors.toList());

		// 分野情報
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// ------------------------------------------------------------
		// 画面表示用に加工
		// ------------------------------------------------------------
		List<AnkenDto> ankenList = tAnkenEntityList.stream()
				.map(entity -> AnkenDto.builder()
						.transitionCustomerId(customerId)
						.ankenId(AnkenId.of(entity.getAnkenId()))
						.bunya(commonBunyaService.getBunyaName(bunyaMap.get(entity.getBunyaId())))
						.bunyaType(commonBunyaService.isKeiji(entity.getBunyaId()) ? BunyaType.KEIJI.getCd() : BunyaType.MINJI.getCd())
						.ankenName(StringUtils.isEmpty(entity.getAnkenName()) ? "(案件名未入力)" : entity.getAnkenName())
						.ankenType(AnkenType.of(entity.getAnkenType()))
						.customerList(customerNameList.stream().filter(dto -> entity.getAnkenId().equals(dto.getAnkenId()))
								.sorted(Comparator.comparing(PersonInfoForAnkenDto::getPersonId))
								.map(dto -> Customer.builder().personId(dto.getPersonId())
										.customerName(dto.getCustomerName())
										.build())
								.collect(Collectors.toList()))
						.aitegataList(aitegataList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
								.map(bean -> Aitegata.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
										.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
										.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
										.build())
								.collect(Collectors.toList()))
						.kyohanshaList(kyohanshaList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
								.map(bean -> Kyohansha.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
										.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
										.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
										.build())
								.collect(Collectors.toList()))
						.higaishaList(higaishaList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getCreatedAt))
								.map(bean -> Higaisha.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaType(bean.getKanyoshaType())
										.dairiFlg(bean.getDairiFlg()).kanyoshaName(bean.getKanyoshaName())
										.relatedKanyoshaSeq(bean.getRelatedKanyoshaSeq())
										.build())
								.collect(Collectors.toList()))
						.tojishaKanyoshaList(tojishaKanyoshaList.stream().filter(bean -> entity.getAnkenId().equals(bean.getAnkenId()))
								.sorted(Comparator.comparing(AnkenKanyoshaBean::getDispOrder))
								.map(bean -> TojishaKanyosha.builder().kanyoshaSeq(bean.getKanyoshaSeq())
										.personId(bean.getPersonId()).kanyoshaName(bean.getKanyoshaName())
										.build())
								.collect(Collectors.toList()))
						// 完了フラグがONならステータス完了、OFFなら進行中としてセットする
						.ankenStatus(ankenStatusMap.get(entity.getAnkenId()) ? AnkenStatus.KANRYO.getCd() : AnkenStatus.SHINKOCHU.getCd())
						.ankenStatusName(ankenStatusMap.get(entity.getAnkenId()) ? AnkenStatus.of(AnkenStatus.KANRYO.getCd()).getVal() : "")
						.isCompAnken(AnkenStatus.isComp(ankenStatusMap.get(entity.getAnkenId()) ? AnkenStatus.KANRYO.getCd() : AnkenStatus.SHINKOCHU.getCd()))
						.saibanIdList(null)
						.saibanStatus(null)
						.build())

				.collect(Collectors.toList());

		return ankenList;
	}
}
