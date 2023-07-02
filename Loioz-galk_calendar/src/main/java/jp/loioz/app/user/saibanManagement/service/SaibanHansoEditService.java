package jp.loioz.app.user.saibanManagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.saibanManagement.form.SaibanHansoEditInputForm;
import jp.loioz.bean.SaibanAnkenBean;
import jp.loioz.common.constant.CommonConstant.HeigoHansoType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TojishaHyoki;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TSaibanCustomerDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.dao.TSaibanLimitDao;
import jp.loioz.dao.TSaibanLimitRelationDao;
import jp.loioz.dao.TSaibanRelatedKanyoshaDao;
import jp.loioz.dao.TSaibanTantoDao;
import jp.loioz.dao.TSaibanTreeDao;
import jp.loioz.dao.TScheduleDao;
import jp.loioz.entity.TSaibanCustomerEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TSaibanJikenEntity;
import jp.loioz.entity.TSaibanLimitEntity;
import jp.loioz.entity.TSaibanLimitRelationEntity;
import jp.loioz.entity.TSaibanRelatedKanyoshaEntity;
import jp.loioz.entity.TSaibanTantoEntity;
import jp.loioz.entity.TSaibanTreeEntity;
import jp.loioz.entity.TScheduleEntity;

/**
 * 裁判管理(民事)画面：反訴モーダルのサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanHansoEditService extends DefaultService {

	@Autowired
	private TSaibanDao tSaibanDao;

	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	@Autowired
	private TSaibanTantoDao tSaibanTantoDao;

	@Autowired
	private TSaibanCustomerDao tSaibanCustomerDao;

	@Autowired
	private TSaibanRelatedKanyoshaDao tSaibanRelatedKanyoshaDao;

	@Autowired
	private TSaibanTreeDao tSaibanTreeDao;

	@Autowired
	private TSaibanLimitDao tSaibanLimitDao;

	@Autowired
	private TScheduleDao tScheduleDao;

	@Autowired
	private TSaibanLimitRelationDao tSaibanLimitRelationDao;

	@Autowired
	private Logger logger;

	/**
	 * 画面表示情報の作成
	 *
	 * @param saibanSeq 裁判SEQ
	 * @param ankenId 案件ID
	 * @return 画面表示情報
	 */
	public SaibanHansoEditInputForm createViewForm(Long saibanSeq, Long ankenId) {

		SaibanHansoEditInputForm inputForm = new SaibanHansoEditInputForm();

		inputForm.setSaibanSeq(saibanSeq);
		inputForm.setAnkenId(ankenId);
		inputForm.setJikenYear(DateUtils.getYearToJa());

		return inputForm;
	}

	/**
	 * 登録処理
	 *
	 * @param inputForm
	 */
	public void regist(SaibanHansoEditInputForm inputForm) throws AppException {

		// 親裁判情報を取得する
		Long parentSaibanSeq = inputForm.getSaibanSeq();
		TSaibanEntity parentSaiban = tSaibanDao.selectBySeq(parentSaibanSeq);

		if (parentSaiban == null) {
			// 裁判情報が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00066, null);
		}

		// 裁判情報を登録
		SaibanAnkenBean createResult = createSaiban(inputForm.getAnkenId(), parentSaiban);
		Long childSaibanSeq = createResult.getSaibanSeq();

		// 裁判関連情報を登録
		createSaibanKanren(parentSaibanSeq, childSaibanSeq, inputForm);

		// 裁判ツリーを登録
		createSaibanTree(childSaibanSeq);

		// 裁判の親子関係を結びつけ
		connectSaiban(parentSaibanSeq, childSaibanSeq);

	}

	/**
	 * 裁判情報を登録する
	 *
	 * @param ankenId 案件ID
	 * @param form フォーム入力値
	 * @return 登録したデータ
	 */
	private SaibanAnkenBean createSaiban(Long ankenId, TSaibanEntity parentSaibanEntity) {

		// 現在の枝番の最大値を取得
		long currentMaxBranchNumber = tSaibanDao.getMaxBranchNo(ankenId);
		parentSaibanEntity.setSaibanBranchNo(currentMaxBranchNumber + 1L);

		// 裁判所IDが入力されている場合は、自由入力項目「裁判所名」はnullをセット
		if (Objects.nonNull(parentSaibanEntity.getSaibanshoId())) {
			parentSaibanEntity.setSaibanshoName(null);
		}

		tSaibanDao.insert(parentSaibanEntity);

		return tSaibanDao.selectByAnkenIdAndBranchNo(ankenId, parentSaibanEntity.getSaibanBranchNo());
	}

	/**
	 * 裁判に関連する情報を登録する
	 *
	 * @param parentSaibanSeq 親裁判SEQ
	 * @param childSaibanSeq 子裁判SEQ
	 * @param form フォーム入力値
	 */
	private void createSaibanKanren(Long parentSaibanSeq, Long childSaibanSeq, SaibanHansoEditInputForm form) {

		// 裁判-事件を登録
		insertJiken(childSaibanSeq, form);

		// 裁判-担当者を登録
		insertSaibanTanto(parentSaibanSeq, childSaibanSeq);

		// 裁判-顧客を登録
		insertSaibanCustomer(parentSaibanSeq, childSaibanSeq);

		// 裁判-関与者関係者を登録
		insertSaibanKanyosha(parentSaibanSeq, childSaibanSeq);

	}

	/**
	 * 裁判-事件を登録する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @param form フォーム入力値
	 */
	private void insertJiken(Long saibanSeq, SaibanHansoEditInputForm form) {

		TSaibanJikenEntity entity = new TSaibanJikenEntity();
		entity.setSaibanSeq(saibanSeq);
		entity.setJikenGengo(form.getJikenGengo());
		entity.setJikenYear(form.getJikenYear());
		entity.setJikenMark(form.getJikenMark());
		entity.setJikenNo(form.getJikenNo());
		entity.setJikenName(form.getJikenName());

		tSaibanJikenDao.insert(entity);
	}

	/**
	 * 裁判-担当者を登録する<br>
	 * 親裁判の担当者情報を子裁判の担当者情報として登録する
	 *
	 * @param parentSaibanSeq 親裁判SEQ
	 * @param parentSaibanSeq 子裁判SEQ
	 */
	private void insertSaibanTanto(Long parentSaibanSeq, Long childSaibanSeq) {

		// 親裁判の裁判-担当者を取得
		List<TSaibanTantoEntity> saibanTantoEntityList = tSaibanTantoDao.selectBySaibanSeq(parentSaibanSeq);

		// 親裁判の担当者情報を子裁判情報の担当者情報として登録
		saibanTantoEntityList.stream()
				.filter(entity -> (entity.getTantoType().equals(TantoType.LAWYER.getCd()) || entity.getTantoType().equals(TantoType.JIMU.getCd())))
				.forEach(entity -> {
					entity.setSaibanSeq(childSaibanSeq);
					tSaibanTantoDao.insert(entity);
				});
	}

	/**
	 * 裁判-顧客を登録する<br>
	 * 親裁判の顧客情報を子裁判の顧客情報として登録する
	 *
	 * @param parentSaibanSeq 親裁判SEQ
	 * @param parentSaibanSeq 子裁判SEQ
	 */
	private void insertSaibanCustomer(Long parentSaibanSeq, Long childSaibanSeq) {

		// 親裁判の裁判-顧客を取得
		List<TSaibanCustomerEntity> saibanCustomerEntityList = tSaibanCustomerDao.selectBySaibanSeq(parentSaibanSeq);

		// 親裁判の顧客情報を子裁判情報の担当者情報として登録
		saibanCustomerEntityList.stream()
				.forEach(entity -> {
					entity.setSaibanSeq(childSaibanSeq);
					entity.setMainFlg("0");
					entity.setSaibanTojishaHyoki(changeHansoTojishaHyokiCode(entity.getSaibanTojishaHyoki()));
					tSaibanCustomerDao.insert(entity);
				});
	}

	/**
	 * 裁判-関与者関係者を登録する
	 *
	 * @param parentSaibanSeq 親裁判SEQ
	 * @param parentSaibanSeq 子裁判SEQ
	 */
	private void insertSaibanKanyosha(Long parentSaibanSeq, Long childSaibanSeq) {

		// 親裁判の裁判-関与者関係者情報を取得
		List<TSaibanRelatedKanyoshaEntity> entityList = tSaibanRelatedKanyoshaDao.selectBySaibanSeq(parentSaibanSeq);

		// 親裁判の担当者情報を子裁判情報の担当者情報として登録
		entityList.stream()
				.filter(entity -> (entity.getKanyoshaType().equals(KanyoshaType.KYODOSOSHONIN.getCd()) || entity.getKanyoshaType().equals(KanyoshaType.AITEGATA.getCd())))
				.forEach(entity -> {
					entity.setSaibanSeq(childSaibanSeq);
					entity.setMainFlg("0");
					entity.setSaibanTojishaHyoki(changeHansoTojishaHyokiCode(entity.getSaibanTojishaHyoki()));
					tSaibanRelatedKanyoshaDao.insert(entity);
				});
	}

	/**
	 * 反訴裁判作成用に顧客などの当事者表記を変換する
	 *
	 * @param parentTojishaHyoki
	 * @return
	 */
	private String changeHansoTojishaHyokiCode(String parentTojishaHyoki) {

		// 当事者表記の変換条件
		if (TojishaHyoki.GENKOKU.equalsByCode(parentTojishaHyoki)) {
			// 原告 -> 反訴被告
			return TojishaHyoki.HANSO_HIKOKU.getCd();

		} else if (TojishaHyoki.HIKOKU.equalsByCode(parentTojishaHyoki)) {
			// 被告 -> 反訴原告
			return TojishaHyoki.HANSO_GENKOKU.getCd();

		} else if (TojishaHyoki.KOSONIN.equalsByCode(parentTojishaHyoki) || TojishaHyoki.HIKOSONIN.equalsByCode(parentTojishaHyoki)) {
			// 控訴人もしくは被控訴人 -> 未設定
			return null;

		} else {
			// それ以外は、変更しない
			return parentTojishaHyoki;
		}
	}

	/**
	 * 裁判ツリーを作成
	 *
	 * <pre>
	 * 裁判が作成されたタイミングで作成する事を想定とする
	 * </pre>
	 *
	 * @param saibanSeq
	 */
	private void createSaibanTree(Long saibanSeq) {

		TSaibanTreeEntity tSaibanTreeEntity = new TSaibanTreeEntity();

		tSaibanTreeEntity.setSaibanSeq(saibanSeq);
		tSaibanTreeEntity.setHonsoFlg(SystemFlg.FLG_OFF.getCd());
		tSaibanTreeEntity.setKihonFlg(SystemFlg.FLG_OFF.getCd());

		tSaibanTreeDao.insert(tSaibanTreeEntity);
	}

	/**
	 * 裁判情報の親子関係を更新(結び付ける)します
	 *
	 * @param parentSaibanSeq
	 * @param childSaibanSeq
	 */
	private void connectSaiban(Long parentSaibanSeq, Long childSaibanSeq) {

		// DB情報の取得
		TSaibanTreeEntity parentSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(parentSaibanSeq);
		TSaibanTreeEntity childSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(childSaibanSeq);
		List<TSaibanTreeEntity> grandChildTreeEntities = tSaibanTreeDao.selectChildBySaibanSeq(childSaibanSeq);

		// データ取得チェック
		if (parentSaibanTreeEntity == null || childSaibanTreeEntity == null) {
			throw new DataNotFoundException("裁判情報が取得できませんでした。");
		}

		// 反訴の場合

		// 親裁判のツリー情報を設定
		parentSaibanTreeEntity.setHonsoFlg(SystemFlg.FLG_ON.getCd());

		// 子裁判のツリー情報を設定
		childSaibanTreeEntity.setParentSeq(parentSaibanTreeEntity.getSaibanSeq());
		childSaibanTreeEntity.setConnectType(HeigoHansoType.HANSO.getCd());
		childSaibanTreeEntity.setKihonFlg(SystemFlg.FLG_OFF.getCd());
		childSaibanTreeEntity.setHonsoFlg(SystemFlg.FLG_OFF.getCd());

		// 一時的にできる孫裁判のツリー情報を設定
		for (TSaibanTreeEntity entity : grandChildTreeEntities) {
			// 親は子の兄弟にする
			entity.setParentSeq(parentSaibanTreeEntity.getSaibanSeq());
			entity.setConnectType(HeigoHansoType.HANSO.getCd());
			tSaibanTreeDao.update(entity);
		}

		// 子孫裁判を1つのリストにマージ
		List<TSaibanTreeEntity> descendantSaibanTreeEntities = new ArrayList<>();
		descendantSaibanTreeEntities.add(childSaibanTreeEntity);
		descendantSaibanTreeEntities.addAll(grandChildTreeEntities);

		// 親裁判に紐づく予定を子孫裁判に紐づけます
		connectToDescendant(parentSaibanTreeEntity.getSaibanSeq(), descendantSaibanTreeEntities);

		// 更新処理
		tSaibanTreeDao.update(parentSaibanTreeEntity);
		tSaibanTreeDao.update(childSaibanTreeEntity);
	}

	/**
	 * 親裁判に紐づく予定を子孫の裁判にも紐づけます
	 *
	 * @param parentSeq
	 * @param descendantSaibanTreeEntities
	 */
	private void connectToDescendant(Long parentSeq, List<TSaibanTreeEntity> descendantSaibanTreeEntities) {

		// 親裁判に紐づく裁判期日情報を取得します。
		List<TSaibanLimitEntity> saibanLimitEntityList = tSaibanLimitDao.selectBySaibanSeq(parentSeq);

		// 取得した裁判期日から、紐づく予定情報を取得します。
		List<Long> saibanLimitSeqList = saibanLimitEntityList.stream()
				.map(TSaibanLimitEntity::getSaibanLimitSeq)
				.collect(Collectors.toList());
		List<TScheduleEntity> scheduleEntityList = tScheduleDao.selectBySaibanLimitSeqList(saibanLimitSeqList);

		// 取得した予定情報から、親裁判の予定を子裁判に紐づける
		for (TScheduleEntity entity : scheduleEntityList) {

			// 日付判定用変数
			LocalDateTime limitDate = LocalDateTime.of(entity.getDateTo(), entity.getTimeTo());

			// 条件：予定終了日時 > 現在日時
			if (limitDate.isAfter(LocalDateTime.now())) {

				// 子孫裁判にも紐づける
				for (TSaibanTreeEntity descendantEntity : descendantSaibanTreeEntities) {
					TSaibanLimitRelationEntity grandChildSaibanLimitRelationEntity = new TSaibanLimitRelationEntity();
					grandChildSaibanLimitRelationEntity.setSaibanSeq(descendantEntity.getSaibanSeq());
					grandChildSaibanLimitRelationEntity.setSaibanLimitSeq(entity.getSaibanLimitSeq());
					tSaibanLimitRelationDao.insert(grandChildSaibanLimitRelationEntity);
				}
			}
		}

	}

}
