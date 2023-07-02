package jp.loioz.app.user.taskManagement.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.taskManagement.dto.TaskAnkenAddModalDto;
import jp.loioz.app.user.taskManagement.form.TaskAnkenAddModalSearchForm;
import jp.loioz.app.user.taskManagement.form.TaskAnkenAddModalViewForm;
import jp.loioz.bean.AnkenAitegataCntBean;
import jp.loioz.bean.AnkenPersonCntBean;
import jp.loioz.bean.AnkenTantoLawyerJimuBean;
import jp.loioz.bean.TaskAnkenAddModalBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.AnkenListDao;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TTaskAnkenDao;
import jp.loioz.domain.condition.TaskAnkenAddModalSearchCondition;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TTaskAnkenEntity;

/**
 * 案件タスク追加モーダルのサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TaskAnkenAddService extends DefaultService {

	/** アカウントマスタDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件一覧Daoクラス */
	@Autowired
	private AnkenListDao ankenListDao;

	/** 案件タスクDaoクラス */
	@Autowired
	private TTaskAnkenDao tTaskAnkenDao;

	/** 案件担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 案件タスク追加検索条件フォームを初期化します。
	 * 
	 * @return
	 */
	public void initModalSearchForm(TaskAnkenAddModalSearchForm searchForm) {

		// 分野のプルダウン情報（利用停止を含まない）を検索条件Formにセット
		List<BunyaDto> bunyaList = commonBunyaService.getBunyaList();
		searchForm.setBunyaList(bunyaList);

		// 担当弁護士、担当事務のプルダウン情報を検索条件Formにセット
		List<MAccountEntity> accountList = mAccountDao.selectAll();
		searchForm.setTantoLawyerList(accountList.stream().filter(entity -> AccountType.LAWYER.equalsByCode(entity.getAccountType())).collect(Collectors.toList()));
		searchForm.setTantoJimuList(accountList.stream().filter(entity -> AccountType.JIMU.equalsByCode(entity.getAccountType())).collect(Collectors.toList()));

		searchForm.setAnkenId(null);
		searchForm.setAnkenName(null);
		searchForm.setBunyaId(null);
		searchForm.setName(null);
		searchForm.setTantoJimu(null);
		searchForm.setTantoLaywer(null);
	}

	/**
	 * 案件タスク追加検索条件フォームに表示用のデータを設定します。<br>
	 * （分野、担当弁護士、担当事務、プルダウン用リストデータ）
	 * 
	 * @param searchForm
	 */
	public void setDisplayData(TaskAnkenAddModalSearchForm searchForm) {
		// 分野のプルダウン情報（利用停止を含まない）を検索条件Formにセット
		List<BunyaDto> bunyaList = commonBunyaService.getBunyaList();
		searchForm.setBunyaList(bunyaList);

		// 担当弁護士、担当事務のプルダウン情報を検索条件Formにセット
		List<MAccountEntity> accountList = mAccountDao.selectAll();
		searchForm.setTantoLawyerList(accountList.stream().filter(entity -> AccountType.LAWYER.equalsByCode(entity.getAccountType())).collect(Collectors.toList()));
		searchForm.setTantoJimuList(accountList.stream().filter(entity -> AccountType.JIMU.equalsByCode(entity.getAccountType())).collect(Collectors.toList()));
	}

	/**
	 * 案件タスク追加表示フォームを作成します。
	 * 
	 * @param searchForm モーダルで表示する案件の検索条件
	 * @param loginAccountSeq ログインユーザーのアカウントSEQ
	 * @return
	 */
	public TaskAnkenAddModalViewForm createModalViewForm(TaskAnkenAddModalSearchForm searchForm, Long loginAccountSeq) {
		TaskAnkenAddModalViewForm viewForm = new TaskAnkenAddModalViewForm();

		// 検索条件を作成
		TaskAnkenAddModalSearchCondition searchCondition = searchForm.toTaskAnkenAddModalSearchCondition();

		// 案件の検索（案件ID、案件の顧客先頭１人分のpersonId、登録日を取得する）
		List<TaskAnkenAddModalBean> ankenList = tAnkenDao.selectAnkenIdPersonIdBySearchConditions(searchCondition, loginAccountSeq);

		// 案件の先頭１人分の顧客名、顧客数を取得する
		this.setAnkenListPersonName(ankenList);

		// 案件の分野、案件名を取得する
		this.setBunyaIdAnkenName(ankenList);

		// 案件の相手方、相手方数を取得する
		this.setAnkenListAitegata(ankenList);

		// 案件の担当弁護士、担当事務を取得する
		this.setAnkenListTantoLaywerJimu(ankenList);

		// Dto変換しviewFormにセットする
		viewForm.setTaskAnkenAddList(convertTaskAnkenAddModalBean2Dto(ankenList));

		// 取得したデータ件数が上限に達しているか
		if (!ankenList.isEmpty() && ankenList.size() >= CommonConstant.OVER_VIEW_LIMIT) {
			viewForm.setOverViewCntFlg(true);
		} else {
			viewForm.setOverViewCntFlg(false);
		}
		return viewForm;
	}

	/**
	 * 案件タスク追加登録をおこないます。<br>
	 * 
	 * @param ankenId
	 * @throws AppException
	 */
	public void registAnkenTaskAdd(Long ankenId) throws AppException {

		// パラメータの案件IDが空の場合
		if (ankenId == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00007, null);
		}

		TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
		if (tAnkenEntity == null) {
			// 案件が削除されていたら、楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 既に案件タスク追加のデータが存在する
		List<TTaskAnkenEntity> taskAnkenList = tTaskAnkenDao.selectTaskAnkenByAccountSeqAnkenId(SessionUtils.getLoginAccountSeq(), ankenId);
		if (LoiozCollectionUtils.isNotEmpty(taskAnkenList)) {
			// 案件タスク追加データが存在するため楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 既に案件タスク追加の登録上限に達している場合
		List<TTaskAnkenEntity> list = tTaskAnkenDao.selectTaskAnkenByAccountSeq(SessionUtils.getLoginAccountSeq());
		if (!list.isEmpty() && list.size() >= CommonConstant.ANKEN_TASK_ADD_REGIST_LIMIT) {
			// 案件タスク追加の登録上限に達しているためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00174, null, "案件タスク", String.valueOf(CommonConstant.ANKEN_TASK_ADD_REGIST_LIMIT));
		}

		// 案件タスク情報を作成
		TTaskAnkenEntity entity = new TTaskAnkenEntity();
		entity.setAccountSeq(SessionUtils.getLoginAccountSeq());
		entity.setAnkenId(ankenId);

		// 登録
		int insertCount = tTaskAnkenDao.insert(entity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 案件に関する顧客情報をセットします。<br>
	 * （顧客先頭１人分の名前、顧客の合計数）
	 * 
	 * @param ankenList
	 */
	private void setAnkenListPersonName(List<TaskAnkenAddModalBean> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<AnkenPersonCntBean> customerBeanList = tAnkenDao.selectAnkenPersonByAnkenId(ankenIdList);
		for (TaskAnkenAddModalBean bean : ankenList) {
			Optional<AnkenPersonCntBean> optBean = customerBeanList.stream().filter(customerBean -> customerBean.getAnkenId().equals(bean.getAnkenId())).findFirst();
			if (optBean.isPresent()) {
				bean.setCustomerName(optBean.get().getCustomerName());
				bean.setNumberOfCustomer(optBean.get().getNumberOfCustomer());
			}
		}
	}

	/**
	 * 案件に関する分野、案件名をセットします。
	 * 
	 * @param ankenList
	 */
	private void setBunyaIdAnkenName(List<TaskAnkenAddModalBean> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		// 分野情報引当用Mapを取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));
		// 案件IDから案件情報を取得
		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<TAnkenEntity> ankenEntityList = tAnkenDao.selectById(ankenIdList);
		for (TaskAnkenAddModalBean bean : ankenList) {
			Optional<TAnkenEntity> optBean = ankenEntityList.stream().filter(ankenEntity -> ankenEntity.getAnkenId().equals(bean.getAnkenId().asLong())).findFirst();
			if (optBean.isPresent()) {
				bean.setAnkenName(optBean.get().getAnkenName());
				bean.setBunyaId(optBean.get().getBunyaId());
				bean.setBunyaName(commonBunyaService.getBunyaName(bunyaMap.get(optBean.get().getBunyaId())));
			}
		}
	}

	/**
	 * 案件に関する相手方情報をセットします。<br>
	 * （相手方先頭１人分の名前、相手方の合計数）
	 * 
	 * @param ankenList
	 */
	private void setAnkenListAitegata(List<TaskAnkenAddModalBean> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<AnkenAitegataCntBean> aitegataBeanList = ankenListDao.selectAnkenAitegataByAnkenId(ankenIdList);
		for (TaskAnkenAddModalBean bean : ankenList) {
			Optional<AnkenAitegataCntBean> optBean = aitegataBeanList.stream().filter(aitegataBean -> aitegataBean.getAnkenId().asLong().equals(bean.getAnkenId().asLong())).findFirst();
			if (optBean.isPresent()) {
				bean.setAitegataName(optBean.get().getAitegataName());
				bean.setNumberOfAitegata(optBean.get().getNumberOfAitegata());
			}
		}
	}

	/**
	 * 案件に関する担当弁護士、担当事務をセットします。
	 * 
	 * @param ankenList
	 */
	private void setAnkenListTantoLaywerJimu(List<TaskAnkenAddModalBean> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<AnkenTantoLawyerJimuBean> tantoLaywerJimuBeanList = tAnkenTantoDao.selectAnkenTantoLaywerJimuById(ankenIdList);
		for (TaskAnkenAddModalBean bean : ankenList) {
			// 担当弁護士の担当者名
			Optional<AnkenTantoLawyerJimuBean> lawyerOptBean = tantoLaywerJimuBeanList.stream().filter(tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(bean.getAnkenId().asLong()) && TantoType.LAWYER.equalsByCode(tantoLaywerJimuBean.getTantoType())).findFirst();
			if (lawyerOptBean.isPresent()) {
				bean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			// 担当事務の担当者名
			Optional<AnkenTantoLawyerJimuBean> jimuOptBean = tantoLaywerJimuBeanList.stream().filter(tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(bean.getAnkenId().asLong()) && TantoType.JIMU.equalsByCode(tantoLaywerJimuBean.getTantoType())).findFirst();
			if (jimuOptBean.isPresent()) {
				bean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}

	/**
	 * 案件タスク追加モーダル用BeanをDtoに変換します。
	 * 
	 * @param taskAnkenAddModalBeanList
	 * @return
	 */
	private List<TaskAnkenAddModalDto> convertTaskAnkenAddModalBean2Dto(List<TaskAnkenAddModalBean> taskAnkenAddModalBeanList) {
		return taskAnkenAddModalBeanList.stream().map(bean -> {
			TaskAnkenAddModalDto dto = new TaskAnkenAddModalDto();

			dto.setAitegataName(bean.getAitegataName());
			dto.setAnkenId(bean.getAnkenId());
			dto.setAnkenName(bean.getAnkenName());
			dto.setBunyaName(bean.getBunyaName());
			dto.setCustomerName(bean.getCustomerName());
			dto.setNumberOfAitegata(bean.getNumberOfAitegata());
			dto.setNumberOfCustomer(bean.getNumberOfCustomer());
			dto.setPersonId(bean.getPersonId());
			dto.setTantoJimuName(bean.getTantoJimuName());
			dto.setTantoLaywerName(bean.getTantoLaywerName());
			dto.setCreatedAt(bean.getCreatedAt());
			return dto;
		}).collect(Collectors.toList());
	}

}
