package jp.loioz.app.user.azukariItem.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.app.common.word.builder.naibu.Wn0001WordBuilder;
import jp.loioz.app.common.word.builder.naibu.Wn0002WordBuilder;
import jp.loioz.app.common.word.config.WordConfig;
import jp.loioz.app.common.word.dto.naibu.Wn0001WordDto;
import jp.loioz.app.common.word.dto.naibu.Wn0002WordDto;
import jp.loioz.app.user.azukariItem.form.AzukariItemDownloadForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TAnkenAzukariItemDao;
import jp.loioz.dto.AnkenTantoAccountDto;
import jp.loioz.dto.AzukariItemListDto;
import jp.loioz.dto.AzukariItemUserDto;
import jp.loioz.entity.MTenantEntity;

/**
 * 預り品一覧画面のserviceクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AzukariItemDocService extends DefaultService {

	/** ワード出力用の設定ファイル */
	@Autowired
	private WordConfig wordConfig;

	/** 共通帳票のサービスクラス */
	@Autowired
	private CommonChohyoService commonChohyoService;

	/** 預り品用のDaoクラス */
	@Autowired
	private TAnkenAzukariItemDao tAnkenAzukariItemDao;

	/** 事務所情報Daoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 預り証出力
	 *
	 * @param azukariSeqList
	 * @param ankenId
	 * @param atesaki
	 * @throws Exception
	 */
	public void createAzukariSho(HttpServletResponse response, AzukariItemDownloadForm azukariItemDownloadForm) throws Exception {
		// 預り品ごとで預り元が同一の場合にまとめる処理を行う
		List<AzukariItemListDto> azukariList = tAnkenAzukariItemDao.selectAzukariItemListByAnkenItemSeq(azukariItemDownloadForm.getAzukariSeqList());
		List<AzukariItemUserDto> azukariUserList = new ArrayList<>();
		for (int i = 0; i < azukariList.size(); i++) {
			AzukariItemListDto azukariItemListDto = azukariList.get(i);
			// 比較不要か確認
			if (azukariItemListDto.getNoComparisonRequired()) {
				continue;
			}
			// azukariUserListのDTOに対してデータを設定
			AzukariItemUserDto azukariItemUserDto = new AzukariItemUserDto();
			azukariItemUserDto.getAnkenItemSeqList().add(azukariItemListDto.getAnkenItemSeq());
			azukariItemUserDto.setAzukariFrom(azukariItemListDto.getAzukariFrom());
			azukariItemUserDto.setAzukariFromCustomerId(azukariItemListDto.getAzukariFromCustomerId());
			azukariItemUserDto.setAzukariFromKanyoshaSeq(azukariItemListDto.getAzukariFromKanyoshaSeq());
			azukariItemUserDto.setAzukariFromType(azukariItemListDto.getAzukariFromType());
			for (int j = i + 1; j < azukariList.size(); j++) {
				AzukariItemListDto azukariItemListDtoForCompare = azukariList.get(j);
				// 預元のデータタイプが等しいか比較
				if (azukariItemListDto.getAzukariFromType().equals(azukariItemListDtoForCompare.getAzukariFromType())) {
					// 預元が顧客の場合

					if (azukariItemListDto.getAzukariFromCustomerId() != null) {
						// 同一預り元か判定
						if (azukariItemListDto.getAzukariFromCustomerId().equals(azukariItemListDtoForCompare.getAzukariFromCustomerId())) {
							azukariItemUserDto.getAnkenItemSeqList().add(azukariItemListDtoForCompare.getAnkenItemSeq());
							azukariItemListDtoForCompare.setNoComparisonRequired(true);
						}

					} else if (azukariItemListDto.getAzukariFromKanyoshaSeq() != null) {
						// 預元が関与者の場合

						// 同一預り元か判定
						if (azukariItemListDto.getAzukariFromKanyoshaSeq().equals(azukariItemListDtoForCompare.getAzukariFromKanyoshaSeq())) {
							azukariItemUserDto.getAnkenItemSeqList().add(azukariItemListDtoForCompare.getAnkenItemSeq());
							azukariItemListDtoForCompare.setNoComparisonRequired(true);
						}

					} else if (azukariItemListDto.getAzukariFrom() != null) {
						// 預元が自由入力の場合

						// 同一預り元か判定
						if (azukariItemListDto.getAzukariFrom().equals(azukariItemListDtoForCompare.getAzukariFrom())) {
							azukariItemUserDto.getAnkenItemSeqList().add(azukariItemListDtoForCompare.getAnkenItemSeq());
							azukariItemListDtoForCompare.setNoComparisonRequired(true);
						}

					} else {
						// その他（ここには来ない想定）
					}
				}
			}

			// 宛先名リスト
			List<String> atesakiNameList = commonChohyoService.getAtesakiNameList(azukariItemUserDto.getAzukariFromCustomerId(), azukariItemUserDto.getAzukariFromKanyoshaSeq());
			azukariItemUserDto.setAtesakiNameList(atesakiNameList);

			// 預かり品目リスト
			List<AzukariItemListDto> azukariHimokuList = tAnkenAzukariItemDao.selectAzukariItemListByAnkenItemSeq(azukariItemUserDto.getAnkenItemSeqList());
			azukariItemUserDto.setAzukariHimokuList(azukariHimokuList);

			azukariUserList.add(azukariItemUserDto);
		}

		// 2 預り元ごとにwordファイルを作成→ただしwordファイルは1ファイルに対して連続して作成する
		if (azukariItemDownloadForm.getAzukariSeqList().isEmpty()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		Wn0002WordBuilder wn0002WordBuilder = new Wn0002WordBuilder();

		// ■1.DTOを定義
		Wn0002WordDto wn0002WordDto = wn0002WordBuilder.createNewTargetBuilderDto();

		// ■2.WordConfigを設定
		wn0002WordBuilder.setConfig(wordConfig);

		// ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定
		wn0002WordDto.setAnkenId(StringUtils.null2blank(azukariItemDownloadForm.getAnkenId().toString()));

		String dateStr = DateUtils.getDateToJaDate();
		wn0002WordDto.setCreatedAt(dateStr);

		// テナント情報の取得
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		wn0002WordDto.setTenantZipCode(StringUtils.null2blank(mTenantEntity.getTenantZipCd()));
		wn0002WordDto.setTenantTelNo(StringUtils.null2blank(mTenantEntity.getTenantTelNo()));
		wn0002WordDto.setTenantFaxNo(StringUtils.null2blank(mTenantEntity.getTenantFaxNo()));
		wn0002WordDto.setTenantAddress1(StringUtils.null2blank(mTenantEntity.getTenantAddress1()));
		wn0002WordDto.setTenantAddress2(StringUtils.null2blank(mTenantEntity.getTenantAddress2()));
		wn0002WordDto.setTenantName(StringUtils.null2blank(mTenantEntity.getTenantName()));

		wn0002WordDto.setAzukariUserList(azukariUserList);

		// 担当弁護士を取得
		List<AnkenTantoAccountDto> dispAnkenTanto = commonChohyoService.dispAnkenTantoBengoshi(azukariItemDownloadForm.getAnkenId());
		wn0002WordDto.setAnkenTantoAccountDtoList(dispAnkenTanto);
		wn0002WordBuilder.setWn0002WordDto(wn0002WordDto);

		try {
			// Wordファイルの出力処理
			wn0002WordBuilder.makeWordFile(response);

		} catch (IOException e) {
			throw new AppException(MessageEnum.MSG_E00034, e);
		} catch (Exception e) {
			throw new AppException(MessageEnum.MSG_E00034, e);
		}
	}

	/**
	 * 受領証出力
	 *
	 * @param juryoSeqList
	 * @param ankenId
	 * @throws Exception
	 */
	public void createJuryoSho(HttpServletResponse response, AzukariItemDownloadForm azukariItemDownloadForm) throws Exception {

		if (azukariItemDownloadForm.getAzukariSeqList().isEmpty()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// Word出力用のBuilderを準備
		Wn0001WordBuilder wn0001WordBuilder = new Wn0001WordBuilder();
		wn0001WordBuilder.setConfig(wordConfig);

		// Builder用のDtoにWordへの出力データを設定
		Wn0001WordDto wn0001WordDto = wn0001WordBuilder.createNewTargetBuilderDto();

		// Dtoに値を設定する
		wn0001WordDto.setAnkenId(StringUtils.null2blank(azukariItemDownloadForm.getAnkenId().toString()));

		// 弁護士情報の取得
		List<AnkenTantoAccountDto> dispAnkenTantoBengoshi = commonChohyoService.dispAnkenTantoBengoshi(azukariItemDownloadForm.getAnkenId());
		List<String> lawyerNameList = dispAnkenTantoBengoshi.stream().map(entity -> {
			return "弁護士" + CommonConstant.FULL_SPACE + entity.getAccountNameSei() + CommonConstant.FULL_SPACE + entity.getAccountNameMei()
					+ CommonConstant.FULL_SPACE + "宛";
		}).collect(Collectors.toList());
		wn0001WordDto.setLawyerNameList(lawyerNameList);

		// 出力日
		wn0001WordDto.setCreatedAt(DateUtils.getDateToJaDate());

		// 預り品情報の取得
		List<AzukariItemListDto> azukariItemDtoList = tAnkenAzukariItemDao.selectAzukariItemListByAnkenItemSeq(azukariItemDownloadForm.getAzukariSeqList());
		wn0001WordDto.setHinmoku(azukariItemDtoList);

		wn0001WordBuilder.setWn0001WordDto(wn0001WordDto);

		try {
			// Wordファイルの出力処理
			wn0001WordBuilder.makeWordFile(response);

		} catch (IOException e) {
			throw new AppException(MessageEnum.MSG_E00034, e);
		} catch (Exception e) {
			throw new AppException(MessageEnum.MSG_E00034, e);
		}

	}

}