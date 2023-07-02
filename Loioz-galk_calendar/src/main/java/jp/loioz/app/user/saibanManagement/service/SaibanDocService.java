package jp.loioz.app.user.saibanManagement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonSaibanService;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.app.common.word.builder.naibu.Wn0008WordBuider;
import jp.loioz.app.common.word.config.WordConfig;
import jp.loioz.app.common.word.dto.naibu.Wn0008WordDto;
import jp.loioz.app.common.word.dto.naibu.Wn0009WordDto;
import jp.loioz.common.constant.ChohyoWordConstant;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TojishaHyoki;
import jp.loioz.common.constant.CommonConstant.TojishaHyokiType;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MSaibanshoDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.dao.TSaibanLimitDao;
import jp.loioz.dao.TSaibanTreeDao;
import jp.loioz.dao.TScheduleDao;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.SaibanId;
import jp.loioz.dto.KanyoshaTojishaDto;
import jp.loioz.dto.SaibanTantoAccountDto;
import jp.loioz.entity.MSaibanshoEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TSaibanJikenEntity;
import jp.loioz.entity.TSaibanLimitEntity;
import jp.loioz.entity.TSaibanTreeEntity;
import jp.loioz.entity.TScheduleEntity;

/**
 * 裁判管理・送付書出力のコントローラークラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanDocService extends DefaultService {

	/** ワード出力用の設定ファイル */
	@Autowired
	private WordConfig wordConfig;

	/** 共通裁判サービスクラス */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/** 共通帳票サービス */
	@Autowired
	public CommonChohyoService commonChohyoService;

	/** 事務所情報用のDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 裁判Daoクラス */
	@Autowired
	private TSaibanDao tSaibanDao;

	/** 裁判所Daoクラス */
	@Autowired
	private MSaibanshoDao mSaibanshoDao;

	/** 裁判事件Daoクラス */
	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	/** スケジュールDaoクラス */
	@Autowired
	private TScheduleDao tScheduleDao;

	/** 裁判ツリーDaoクラス */
	@Autowired
	private TSaibanTreeDao tSaibanTreeDao;

	/** 裁判期日Daoクラス */
	@Autowired
	private TSaibanLimitDao tSaibanLimitDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	/** 反訴被告 */
	private static final String HANSO_HIKOKU = "(反訴被告)";
	/** 反訴原告 */
	private static final String HANSO_GENKOKU = "(反訴原告)";
	/** 反訴●● */
	private static final String HANSO_SOMETHING = "(反訴●●)";
	/** 代理人弁護士 */
	private static final String DAIRININ_BENGOSHI = "代理人弁護士";
	/** ら */
	private static final String OTHERS = "ら";

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 口頭弁論期日請書-帳票出力(Wn0008)
	 *
	 * @param response
	 * @param ankenId
	 * @param branchNumber
	 */
	public void docKotoBenron(HttpServletResponse response, Long ankenId, Long branchNumber, Long scheduleSeq) {

		SaibanId saibanId = new SaibanId(ankenId, branchNumber);

		// ■1.BuilderとDTOを定義
		Wn0008WordBuider wn0008WordBuider = new Wn0008WordBuider();
		Wn0008WordDto wn0008WordDto = wn0008WordBuider.createNewTargetBuilderDto();

		// ■2.WordConfigを設定
		wn0008WordBuider.setConfig(this.wordConfig);

		// ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定
		this.getWn0008WordDate(saibanId, scheduleSeq, wn0008WordDto);
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

	// ==========================================================================
	// privateメソッド
	// ==========================================================================

	/**
	 * 口頭弁論期日請書（WN0008Word）のデータ取得処理
	 *
	 * @param saibanId
	 * @param scheduleSeq
	 * @param wn0008WordDto
	 */
	private void getWn0008WordDate(SaibanId saibanId, Long scheduleSeq, Wn0008WordDto wn0008WordDto) {

		TSaibanEntity targetSaiban = tSaibanDao.selectBySaibanId(saibanId);// 表示している裁判情報
		TScheduleEntity tScheduleEntity = tScheduleDao.selectBySeq(scheduleSeq);// 選択したスケジュール
		if (CommonUtils.anyNull(targetSaiban, tScheduleEntity)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + CommonConstant.SPACE + ALREADY_UPDATED_TO_OTHER_USER);
			throw new DataNotFoundException("データ取得に失敗 : " + scheduleSeq + " or " + saibanId, null);
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
	 * 口頭弁論期日請書-送付書(FAX用)のデータ取得処理
	 *
	 * @param targetSaiban
	 * @param wn0008WordDto
	 */
	private void setSouhushoFaxDataWn0008(TSaibanEntity targetSaiban, Wn0008WordDto wn0008WordDto) {
		this.setSouhushoFaxData(targetSaiban, wn0008WordDto, null);
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
		if (!LoiozCollectionUtils.isEmpty(genkokuList)) {
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
		Map<String, Boolean> parentConnectStatus = commonSaibanService.connectStatus(targetTreeEntity);

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
		if (!LoiozCollectionUtils.isEmpty(hikokuList)) {
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
		if (customerTojishaList.size() > 1) {
			sbTojishaDairinin.append(OTHERS);
		}
		sbTojishaDairinin.append(CommonConstant.SPACE);
		sbTojishaDairinin.append(DAIRININ_BENGOSHI);
		sbTojishaDairinin.append(CommonConstant.SPACE);
		if (targetCustomerTojisha.getCustomerId() != null) {
			// 裁判に紐づく担当弁護士を表示
			Optional.ofNullable(commonSaibanService.getPrioritySaibanTantoName(TantoType.LAWYER, targetSaibanSeq))
					.ifPresent(str -> sbTojishaDairinin.append(StringUtils.defaultString(str)));
		}

		// ■代理人情報を設定
		wn0008WordDto.setTojishaDairinin(sbTojishaDairinin.toString());

		// ■裁判期日情報
		TSaibanLimitEntity tSaibanLimitEntity = tSaibanLimitDao.selectBySeq(saibanLimitSeq);
		wn0008WordDto.setLimitCount(LoiozNumberUtils.parseAsString(tSaibanLimitEntity.getLimitDateCount()));
		wn0008WordDto.setLimitDate(DateUtils.parseLocalDateTimeToJaDate(tSaibanLimitEntity.getLimitAt()));

	}

}