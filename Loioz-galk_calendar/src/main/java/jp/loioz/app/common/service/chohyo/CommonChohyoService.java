package jp.loioz.app.common.service.chohyo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.app.common.word.builder.naibu.Wn0003WordBuilder;
import jp.loioz.app.common.word.builder.naibu.Wn0004WordBuilder;
import jp.loioz.app.common.word.builder.naibu.Wn0005WordBuilder;
import jp.loioz.app.common.word.builder.naibu.Wn0006WordBuilder;
import jp.loioz.app.common.word.builder.naibu.Wn0007WordBuilder;
import jp.loioz.app.common.word.config.WordConfig;
import jp.loioz.app.common.word.dto.naibu.Wn0003WordDto;
import jp.loioz.app.common.word.dto.naibu.Wn0004WordDto;
import jp.loioz.app.common.word.dto.naibu.Wn0005WordDto;
import jp.loioz.app.common.word.dto.naibu.Wn0006WordDto;
import jp.loioz.app.common.word.dto.naibu.Wn0007WordDto;
import jp.loioz.app.user.seisansho.service.SeisanshoDocService;
import jp.loioz.bean.AnkenTantoAccountBean;
import jp.loioz.bean.KanyoshaBean;
import jp.loioz.bean.KanyoshaContactBean;
import jp.loioz.bean.PersonBean;
import jp.loioz.bean.SaibanTantoAccountBean;
import jp.loioz.common.constant.ChohyoConstatnt;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TransferAddressType;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MSosakikanDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TAnkenSosakikanDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TPersonContactDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSaibanAddKeijiDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanTantoDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenTantoAccountDto;
import jp.loioz.dto.ExcelTimeChargeDto;
import jp.loioz.dto.ExcelTimeChargeListDto;
import jp.loioz.dto.SaibanTantoAccountDto;
import jp.loioz.entity.MSosakikanEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TAnkenSosakikanEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TPersonContactEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSaibanAddKeijiEntity;

/**
 * 帳票全般の共通サービスクラス
 *
 */
@Service
public class CommonChohyoService extends DefaultService {

	/** 案件主担当フラグ */
	public static final String ANKEN_TANTO_FLG_ON = "2";

	/** 送付書共通MAPのKEY */
	public static final String TXT_ZIPCODE_KEY = "TXT_ZIPCODE";
	public static final String TXT_ADDRESS1_KEY = "TXT_ADDRESS1";
	public static final String TXT_ADDRESS2_KEY = "TXT_ADDRESS2";
	public static final String TXT_FAX_NO_KEY = "TXT_FAX_NO";

	/** 送付書（捜査機関）共通MAPのKEY */
	public static final String TXT_SK_ATESAKI1_KEY = "TXT_SK_ATESAKI1";
	public static final String TXT_SK_ATESAKI2_KEY = "TXT_SK_ATESAKI2";
	public static final String TXT_SK_ATESAKI3_KEY = "TXT_SK_ATESAKI3";

	/** 送付書（代理人、法人）共通MAPのKEY */
	public static final String TXT_DH_ATESAKI1_KEY = "TXT_DH_ATESAKI1";
	public static final String TXT_DH_ATESAKI2_KEY = "TXT_DH_ATESAKI2";

	/** 名簿情報共通サービス */
	@Autowired
	CommonPersonService commonCustomerService;

	/** 精算書共通サービス */
	@Autowired
	SeisanshoDocService seisanshoDocService;

	/** 帳票共通サービス */
	@Autowired
	CommonChohyoService commonChohyoService;

	/** 会計共通サービス */
	@Autowired
	CommonKaikeiService commonKaikeiService;

	/** ワード出力用の設定ファイル */
	@Autowired
	private WordConfig wordConfig;

	/** 事務所情報Daoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 名簿情報Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 名簿情報-連絡先のDaoクラス */
	@Autowired
	private TPersonContactDao tPersonContactDao;

	/** 案件担当のDaoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 案件-捜査機関のDaoクラス */
	@Autowired
	private TAnkenSosakikanDao tAnkenSosakikanDao;

	/** 捜査機関マスタのDaoクラス */
	@Autowired
	private MSosakikanDao mSosakikanDao;

	/** 裁判情報のDaoクラス */
	@Autowired
	TSaibanDao tSaibanDao;

	/** 刑事裁判付帯情報用のDaoクラス */
	@Autowired
	TSaibanAddKeijiDao tSaibanAddKeijiDao;

	/** 裁判担当者情報用のDaoクラス */
	@Autowired
	private TSaibanTantoDao tSaibanTantoDao;

	/** 関与者情報のDaoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/**
	 * 送付書(一般)を出力する
	 *
	 * @param response
	 * @param personId
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @throws Exception
	 */
	public void outputSouhushoIppan(HttpServletResponse response, Long personId, Long ankenId, Long kanyoshaSeq)
			throws Exception {

		// ■1.BuilderとDTOを定義
		Wn0003WordBuilder wn0003WordBuilder = new Wn0003WordBuilder();
		Wn0003WordDto wn0003WordDto = wn0003WordBuilder.createNewTargetBuilderDto();

		// ■2.WordConfigを設定
		wn0003WordBuilder.setConfig(wordConfig);

		// ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定
		// ID
		wn0003WordDto.setId(StringUtils.null2blank(personId));

		// 宛先情報
		Map<String, String> atesakiMap = this.getAtesakiMap(personId, kanyoshaSeq);
		wn0003WordDto.setZipCode(atesakiMap.get(TXT_ZIPCODE_KEY));
		wn0003WordDto.setAddress1(atesakiMap.get(TXT_ADDRESS1_KEY));
		wn0003WordDto.setAddress2(atesakiMap.get(TXT_ADDRESS2_KEY));

		// 宛先名
		List<String> atesakiNameList = this.getAtesakiNameList(personId, kanyoshaSeq);
		wn0003WordDto.setAtesaki1(atesakiNameList.get(0));
		wn0003WordDto.setAtesaki2(atesakiNameList.get(1));

		// 現在日付を和暦文字列で取得
		String dateStr = DateUtils.getDateToJaDate();
		wn0003WordDto.setCreatedAt(dateStr);

		// 事務所情報
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());

		wn0003WordDto.setTenantZipCode(StringUtils.null2blank(mTenantEntity.getTenantZipCd()));
		wn0003WordDto.setTenantAddress1(StringUtils.null2blank(mTenantEntity.getTenantAddress1()));
		wn0003WordDto.setTenantAddress2(StringUtils.null2blank(mTenantEntity.getTenantAddress2()));
		wn0003WordDto.setTenantName(StringUtils.null2blank(mTenantEntity.getTenantName()));
		wn0003WordDto.setTenantTelNo(StringUtils.null2blank(mTenantEntity.getTenantTelNo()));
		wn0003WordDto.setTenantFaxNo(StringUtils.null2blank(mTenantEntity.getTenantFaxNo()));

		// 担当弁護士、担当事務情報の取得
		// UI改善対応で名簿情報画面からの出力時に案件を指定しなくなった。案件IDがない場合は担当弁護士、担当事務情報は空とする。
		List<AnkenTantoAccountDto> dispAnkenTanto = List.of();
		if (ankenId != null) {
			dispAnkenTanto = this.dispAnkenTantoBengoshiMainJimu(ankenId);
		}
		wn0003WordDto.setAnkenTantoAccountDtoList(dispAnkenTanto);

		wn0003WordBuilder.setWn0003WordDto(wn0003WordDto);

		try {
			// Wordファイルの出力処理
			wn0003WordBuilder.makeWordFile(response);

		} catch (Exception e) {
			throw new RuntimeException(e);

		}

	}

	/**
	 * 送付書(一般)-捜査機関を出力する
	 *
	 * @param response
	 * @param ankenId
	 * @param sosakikanSeq
	 * @throws Exception
	 */
	public void outputSouhushoIppanForSosaKikan(HttpServletResponse response, Long ankenId, Long sosakikanSeq)
			throws Exception {
		outputSouhushoIppanForSosaKikanKohan(response, ankenId, sosakikanSeq, null);
	}

	/**
	 * 送付書(一般)-公判担当検察官情報を出力する
	 *
	 * @param response
	 * @param ankenId
	 * @param saibanSeq
	 * @throws Exception
	 */
	public void outputSouhushoIppanForKohan(HttpServletResponse response, Long ankenId, Long saibanSeq)
			throws Exception {
		outputSouhushoIppanForSosaKikanKohan(response, ankenId, null, saibanSeq);
	}

	/**
	 * 送付書(FAX)を出力する
	 *
	 * @param response
	 * @param personId
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @throws Exception
	 */
	public void outputSouhushoFax(HttpServletResponse response, Long personId, Long ankenId, Long kanyoshaSeq)
			throws Exception {

		// ■1.BuilderとDTOを定義
		Wn0004WordBuilder wn0004WordBuilder = new Wn0004WordBuilder();
		Wn0004WordDto wn0004WordDto = wn0004WordBuilder.createNewTargetBuilderDto();

		// ■2.WordConfigを設定
		wn0004WordBuilder.setConfig(wordConfig);

		// ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定
		// ID
		wn0004WordDto.setId(StringUtils.null2blank(personId));

		// 宛先情報
		Map<String, String> atesakiMap = this.getAtesakiMap(personId, kanyoshaSeq);
		wn0004WordDto.setFaxNo(atesakiMap.get(TXT_FAX_NO_KEY));

		// 宛先名
		List<String> atesakiNameList = this.getAtesakiNameList(personId, kanyoshaSeq);
		wn0004WordDto.setAtesaki1(atesakiNameList.get(0));
		wn0004WordDto.setAtesaki2(atesakiNameList.get(1));

		// 現在日付を和暦文字列で取得
		String dateStr = DateUtils.getDateToJaDate();
		wn0004WordDto.setCreatedAt(dateStr);

		// 事務所情報
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());

		wn0004WordDto.setTenantZipCode(StringUtils.null2blank(mTenantEntity.getTenantZipCd()));
		wn0004WordDto.setTenantAddress1(StringUtils.null2blank(mTenantEntity.getTenantAddress1()));
		wn0004WordDto.setTenantAddress2(StringUtils.null2blank(mTenantEntity.getTenantAddress2()));
		wn0004WordDto.setTenantName(StringUtils.null2blank(mTenantEntity.getTenantName()));
		wn0004WordDto.setTenantTelNo(StringUtils.null2blank(mTenantEntity.getTenantTelNo()));
		wn0004WordDto.setTenantFaxNo(StringUtils.null2blank(mTenantEntity.getTenantFaxNo()));

		// 担当弁護士、担当事務情報の取得
		// UI改善対応で名簿情報画面からの出力時に案件を指定しなくなった。案件IDがない場合は担当弁護士、担当事務情報は空とする。
		List<AnkenTantoAccountDto> dispAnkenTanto = List.of();
		if (ankenId != null) {
			dispAnkenTanto = this.dispAnkenTantoBengoshiMainJimu(ankenId);
		}
		wn0004WordDto.setAnkenTantoAccountDtoList(dispAnkenTanto);

		wn0004WordBuilder.setWn0004WordDto(wn0004WordDto);

		try {
			// Wordファイルの出力処理
			wn0004WordBuilder.makeWordFile(response);

		} catch (Exception e) {
			throw new RuntimeException(e);

		}

	}

	/**
	 * 送付書(FAX)-捜査機関を出力する
	 *
	 * @param response
	 * @param ankenId
	 * @param sosakikanSeq
	 * @throws Exception
	 */
	public void outputSouhushoFaxForSosaKikan(HttpServletResponse response, Long ankenId, Long sosakikanSeq)
			throws Exception {
		this.outputSouhushoFaxForSosaKikanKohan(response, ankenId, sosakikanSeq, null);
	}

	/**
	 * 送付書(FAX)-捜査機関を出力する
	 *
	 * @param response
	 * @param ankenId
	 * @param sosakikanSeq
	 * @throws Exception
	 */
	public void outputSouhushoFaxForKohan(HttpServletResponse response, Long ankenId, Long saibanSeq)
			throws Exception {
		this.outputSouhushoFaxForSosaKikanKohan(response, ankenId, null, saibanSeq);
	}

	/**
	 * 送付書(委任契約書)を出力する
	 *
	 * @param response
	 * @param personId
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @throws Exception
	 */
	public void outputSouhushoInin(HttpServletResponse response, Long personId, Long ankenId, Long kanyoshaSeq)
			throws Exception {

		// ■1.BuilderとDTOを定義
		Wn0005WordBuilder wn0005WordBuilder = new Wn0005WordBuilder();
		Wn0005WordDto wn0005WordDto = wn0005WordBuilder.createNewTargetBuilderDto();

		// ■2.WordConfigを設定
		wn0005WordBuilder.setConfig(wordConfig);

		// ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定
		// ID
		wn0005WordDto.setId(StringUtils.null2blank(personId));

		// 宛先情報
		Map<String, String> atesakiMap = this.getAtesakiMap(personId, kanyoshaSeq);
		wn0005WordDto.setZipCode(atesakiMap.get(TXT_ZIPCODE_KEY));
		wn0005WordDto.setAddress1(atesakiMap.get(TXT_ADDRESS1_KEY));
		wn0005WordDto.setAddress2(atesakiMap.get(TXT_ADDRESS2_KEY));
		// 宛先名
		List<String> atesakiNameList = this.getAtesakiNameList(personId, kanyoshaSeq);
		wn0005WordDto.setAtesaki1(atesakiNameList.get(0));
		wn0005WordDto.setAtesaki2(atesakiNameList.get(1));

		// 現在日付を和暦文字列で取得
		String dateStr = DateUtils.getDateToJaDate();
		wn0005WordDto.setCreatedAt(dateStr);

		// 事務所情報
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());

		wn0005WordDto.setTenantZipCode(StringUtils.null2blank(mTenantEntity.getTenantZipCd()));
		wn0005WordDto.setTenantAddress1(StringUtils.null2blank(mTenantEntity.getTenantAddress1()));
		wn0005WordDto.setTenantAddress2(StringUtils.null2blank(mTenantEntity.getTenantAddress2()));
		wn0005WordDto.setTenantName(StringUtils.null2blank(mTenantEntity.getTenantName()));
		wn0005WordDto.setTenantTelNo(StringUtils.null2blank(mTenantEntity.getTenantTelNo()));
		wn0005WordDto.setTenantFaxNo(StringUtils.null2blank(mTenantEntity.getTenantFaxNo()));

		// 担当弁護士、担当事務情報の取得
		// UI改善対応で名簿情報画面からの出力時に案件を指定しなくなった。案件IDがない場合は担当弁護士、担当事務情報は空とする。
		List<AnkenTantoAccountDto> dispAnkenTanto = List.of();
		if (ankenId != null) {
			dispAnkenTanto = this.dispAnkenTantoBengoshiMainJimu(ankenId);
		}
		wn0005WordDto.setAnkenTantoAccountDtoList(dispAnkenTanto);

		wn0005WordBuilder.setWn0005WordDto(wn0005WordDto);

		try {
			// Wordファイルの出力処理
			wn0005WordBuilder.makeWordFile(response);

		} catch (Exception e) {
			throw new RuntimeException(e);

		}

	}

	/**
	 * 名簿情報を返す
	 * 
	 * @param personId
	 * @return
	 */
	public TPersonEntity getPersonEntity(Long personId) {
		return tPersonDao.selectPersonByPersonId(personId);
	}

	/**
	 * 宛先を返す（宛先１のみ）
	 *
	 * @param customerId
	 * @param personId
	 * @return
	 */
	public String getAtesakiName(Long personId, Long kanyoshaSeq) {
		List<String> nameList = this.getAtesakiNameList(personId, kanyoshaSeq);
		return nameList.get(0);
	}

	/**
	 * 宛先を返す(宛先１、宛先２）
	 *
	 * @param personId
	 * @param kanyoshaSeq
	 * @return
	 */
	public List<String> getAtesakiNameList(Long personId, Long kanyoshaSeq) {
		List<String> nameList = new ArrayList<String>();

		if (personId != null) {
			// 請求先が顧客
			// 宛先が顧客の場合
			PersonBean customerKojinHojinBean = tPersonDao.selectPersonAddDataByPersonId(personId);

			// 顧客名
			String customerName = new PersonName(
					customerKojinHojinBean.getCustomerNameSei(),
					customerKojinHojinBean.getCustomerNameMei(),
					customerKojinHojinBean.getCustomerNameSeiKana(),
					customerKojinHojinBean.getCustomerNameMeiKana()).getNameFullSpace();

			// 顧客区分判定
			String customerType = customerKojinHojinBean.getCustomerType();
			if (CustomerType.KOJIN.equalsByCode(customerType)) {
				// 個人の場合
				nameList.add(this.addKeishoSama(customerName));
				nameList.add(CommonConstant.BLANK);
			} else if (CustomerType.HOJIN.equalsByCode(customerType)) {
				// 企業・団体の場合
				nameList = this.addKeishoHojinList(customerName, customerKojinHojinBean.getDaihyoName(), customerKojinHojinBean.getTantoName());
			} else {
				// 弁護士の場合
				nameList = this.addKeishoLawyerList(customerName, customerKojinHojinBean.getJimushoName());
			}

		} else if (kanyoshaSeq != null) {
			// 請求先が関与者

			// 関与者情報を取得
			KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
			String kanyoshaName = new PersonName(
					kanyoshaBean.getCustomerNameSei(),
					kanyoshaBean.getCustomerNameMei(),
					kanyoshaBean.getCustomerNameSeiKana(),
					kanyoshaBean.getCustomerNameMeiKana()).getNameFullSpace();

			String customerType = kanyoshaBean.getCustomerType();
			if (CustomerType.KOJIN.equalsByCode(customerType)) {
				// 個人の場合
				nameList.add(this.addKeishoSama(kanyoshaName));
				nameList.add(CommonConstant.BLANK);
			} else if (CustomerType.HOJIN.equalsByCode(customerType)) {
				// 企業・団体の場合
				nameList = this.addKeishoHojinList(kanyoshaName, kanyoshaBean.getDaihyoName(), kanyoshaBean.getTantoName());
			} else if (CustomerType.LAWYER.equalsByCode(customerType)) {
				// 弁護士の場合
				nameList = this.addKeishoLawyerList(kanyoshaName, kanyoshaBean.getJimushoName());
			}

		} else {
			// 顧客、関与者指定なしは空リストを返す
			nameList.add(CommonConstant.BLANK);
			nameList.add(CommonConstant.BLANK);

		}

		return nameList;
	}

	/**
	 * 宛先置換用のMapを取得する
	 *
	 * @param personId
	 * @param kanyoshaSeq
	 * @return
	 */
	public Map<String, String> getAtesakiMap(Long personId, Long kanyoshaSeq) {

		String zipCode = null;
		String address1 = null;
		String address2 = null;
		String faxNo = null;

		if (personId != null) {
			// 宛先が顧客の場合
			PersonBean personBean = tPersonDao.selectPersonAddDataByPersonId(personId);

			// 郵送先タイプ
			String transferAddressType = personBean.getTransferAddressType();
			if (TransferAddressType.OTHER.equalsByCode(transferAddressType)) {
				// その他の場合

				zipCode = personBean.getTransferZipCode();
				address1 = personBean.getTransferAddress1();
				address2 = personBean.getTransferAddress2();

			} else {
				// 指定なし、居住地（所在地）の場合

				zipCode = personBean.getZipCode();
				address1 = personBean.getAddress1();
				address2 = personBean.getAddress2();

			}

			// 連絡先を取得
			List<TPersonContactEntity> tCustomerContactEntitieList = tPersonContactDao.selectPersonContactByPersonId(personId);
			// 優先FAX番号を取得
			faxNo = commonCustomerService.getYusenFaxNo(tCustomerContactEntitieList);

		} else if (kanyoshaSeq != null) {
			// 宛先が関与者の場合

			// 関与者連絡情報を取得
			KanyoshaContactBean kanyoshaContactBean = tKanyoshaDao.selectKanyoshaContactByKanyoshaSeq(kanyoshaSeq);

			// 郵送先タイプ
			String transferAddressType = kanyoshaContactBean.getTransferAddressType();
			if (TransferAddressType.OTHER.equalsByCode(transferAddressType)) {
				// その他の場合
				zipCode = kanyoshaContactBean.getTransferZipCode();
				address1 = kanyoshaContactBean.getTransferAddress1();
				address2 = kanyoshaContactBean.getTransferAddress2();
			} else {
				// 指定なし、居住地（所在地）の場合
				zipCode = kanyoshaContactBean.getZipCode();
				address1 = kanyoshaContactBean.getAddress1();
				address2 = kanyoshaContactBean.getAddress2();
			}

			// 優先FAX番号を取得
			List<TPersonContactEntity> tPersonContactEntitieList = tPersonContactDao
					.selectPersonContactByCustomerId(List.of(kanyoshaContactBean.getCustomerId()));
			faxNo = commonCustomerService.getYusenFaxNo(tPersonContactEntitieList);
		}

		// 宛先情報を設定
		Map<String, String> atesakiMap = new HashMap<String, String>();
		atesakiMap.put(TXT_ZIPCODE_KEY, StringUtils.null2blank(zipCode));
		atesakiMap.put(TXT_ADDRESS1_KEY, StringUtils.null2blank(address1));
		atesakiMap.put(TXT_ADDRESS2_KEY, StringUtils.null2blank(address2));
		atesakiMap.put(TXT_FAX_NO_KEY, StringUtils.null2blank(faxNo));

		return atesakiMap;
	}

	/**
	 * 敬称：「様」を付与する
	 *
	 * @param name
	 * @return
	 */
	public String addKeishoSama(String name) {
		String keishoName = null;
		if (StringUtils.isNotEmpty(name)) {
			keishoName = name + CommonConstant.FULL_SPACE + ChohyoConstatnt.KEISHO_SAMA;
		}
		return keishoName;
	}

	/**
	 * 敬称：「御中」を付与する
	 *
	 * @param name
	 * @return
	 */
	public String addKeishoOnchu(String name) {
		String keishoName = null;
		if (StringUtils.isNotEmpty(name)) {
			keishoName = name + CommonConstant.FULL_SPACE + ChohyoConstatnt.KEISHO_ONCHU;
		}
		return keishoName;
	}

	/**
	 * 法人の表記
	 *
	 * @param customerName
	 * @param daihyoName
	 * @param tantoName
	 * @return
	 */
	public String addKeishoHojin(String customerName, String daihyoName, String tantoName) {
		String name = null;
		if (StringUtils.isNotEmpty(customerName)) {

			if (StringUtils.isEmpty(daihyoName) && StringUtils.isEmpty(tantoName)) {
				// 代表名、担当名が未入力の場合

				// 会社名 + (全角スペース) + 御中
				name = customerName + CommonConstant.FULL_SPACE + ChohyoConstatnt.KEISHO_ONCHU;

			} else if (StringUtils.isNotEmpty(tantoName)) {
				// 担当名が入力ありの場合

				// 会社名 + (全角スペース) + 担当名 + (全角スペース) + 様
				name = customerName + CommonConstant.FULL_SPACE + addKeishoSama(tantoName);

			} else if (StringUtils.isNotEmpty(daihyoName)) {
				// 代表名が入力ありの場合

				// 会社名 + (全角スペース) + 代表名 + (全角スペース) + 様
				name = customerName + CommonConstant.FULL_SPACE + addKeishoSama(daihyoName);

			}
		}

		return name;
	}

	/**
	 * 企業・団体の表記（宛先１、宛先２）
	 *
	 * @param customerName
	 * @param daihyoName
	 * @param tantoName
	 * @return
	 */
	public List<String> addKeishoHojinList(String customerName, String daihyoName, String tantoName) {
		List<String> nameList = new ArrayList<String>();
		if (StringUtils.isNotEmpty(customerName)) {

			if (StringUtils.isEmpty(daihyoName) && StringUtils.isEmpty(tantoName)) {
				// 代表名、担当名が未入力の場合

				// 会社名 + (全角スペース) + 御中
				nameList.add(customerName + CommonConstant.FULL_SPACE + ChohyoConstatnt.KEISHO_ONCHU);
				nameList.add(CommonConstant.BLANK);

			} else if (StringUtils.isNotEmpty(tantoName)) {
				// 担当名が入力ありの場合

				// 会社名
				// 担当名 + (全角スペース) + 様
				nameList.add(customerName);
				nameList.add(addKeishoSama(tantoName));

			} else if (StringUtils.isNotEmpty(daihyoName)) {
				// 代表名が入力ありの場合

				// 会社名
				// 代表名 + (全角スペース) + 様
				nameList.add(customerName);
				nameList.add(addKeishoSama(daihyoName));

			}
		}

		return nameList;
	}

	/**
	 * 弁護士の表記
	 *
	 * @param customerName
	 * @param jimushoName
	 * @return
	 */
	public List<String> addKeishoLawyerList(String customerName, String jimushoName) {
		List<String> nameList = new ArrayList<String>();
		if (StringUtils.isEmpty(jimushoName)) {
			// 事務所名が未入力の場合

			// 名前 + (全角スペース) + 様
			nameList.add(addKeishoSama(customerName));
			nameList.add(CommonConstant.BLANK);

		} else if (StringUtils.isNotEmpty(jimushoName)) {
			// 事務所名が入力ありの場合

			// 事務所名
			// 名前 + (全角スペース) + 様
			nameList.add(jimushoName);
			nameList.add(addKeishoSama(customerName));

		}

		return nameList;
	}

	/**
	 * 帳票に出力する弁護士のみを生成
	 *
	 * @param ankenId
	 * @return
	 */
	public List<AnkenTantoAccountDto> dispAnkenTantoBengoshi(Long ankenId) {
		// 担当弁護士、担当事務を取得
		List<AnkenTantoAccountBean> ankenTantoAccountBeanList = tAnkenTantoDao.selectByIdForAccount(ankenId);

		// 弁護士のみ抽出
		List<AnkenTantoAccountDto> tanto = this.dispAnkenTanto(ankenTantoAccountBeanList, true);
		List<AnkenTantoAccountDto> lawerList = tanto.stream()
				.filter(dto -> !TantoType.JIMU.equalsByCode(dto.getTantoType()))
				.collect(Collectors.toList());
		return lawerList;
	}

	/**
	 * 帳票に出力する弁護士のみを抽出
	 *
	 * @param tanto
	 * @return
	 */
	public List<AnkenTantoAccountDto> dispAnkenTantoBengoshi(List<AnkenTantoAccountDto> tanto) {
		List<AnkenTantoAccountDto> lawerList = tanto.stream()
				.filter(dto -> !TantoType.JIMU.equalsByCode(dto.getTantoType()))
				.collect(Collectors.toList());
		return lawerList;
	}

	/**
	 * 帳票に出力する事務（主担当のみ）を生成
	 *
	 * @param ankenId
	 * @return
	 */
	public List<AnkenTantoAccountDto> dispAnkenMainTantoJimu(Long ankenId) {
		// 担当弁護士、担当事務を取得
		List<AnkenTantoAccountBean> ankenTantoAccountBeanList = tAnkenTantoDao.selectByIdForAccount(ankenId);

		List<AnkenTantoAccountDto> tanto = this.dispAnkenTanto(ankenTantoAccountBeanList, true);
		// 事務のみ抽出
		List<AnkenTantoAccountDto> jimuList = tanto.stream()
				.filter(dto -> TantoType.JIMU.equalsByCode(dto.getTantoType()))
				.collect(Collectors.toList());

		return jimuList;
	}

	/**
	 * 帳票に出力する事務を抽出
	 *
	 * @param tanto
	 * @return
	 */
	public List<AnkenTantoAccountDto> dispAnkenTantoJimu(List<AnkenTantoAccountDto> tanto) {
		// 事務のみ抽出
		List<AnkenTantoAccountDto> jimuList = tanto.stream()
				.filter(dto -> TantoType.JIMU.equalsByCode(dto.getTantoType()))
				.collect(Collectors.toList());

		return jimuList;
	}

	/**
	 * 帳票に出力する弁護士、事務（主担当）を取得
	 *
	 * @param ankenId
	 * @return
	 */
	public List<AnkenTantoAccountDto> dispAnkenTantoBengoshiMainJimu(Long ankenId) {
		// 担当弁護士、担当事務を取得
		List<AnkenTantoAccountBean> ankenTantoAccountBeanList = tAnkenTantoDao.selectByIdForAccount(ankenId);
		return this.dispAnkenTanto(ankenTantoAccountBeanList, true);
	}

	/**
	 * 帳票に出力する案件に紐づく弁護士、事務を取得
	 *
	 * @param ankenId
	 * @return
	 */
	public List<AnkenTantoAccountDto> dispAnkenTantoBengoshiJimuAll(Long ankenId) {
		// 担当弁護士、担当事務を取得
		List<AnkenTantoAccountBean> ankenTantoAccountBeanList = tAnkenTantoDao.selectByIdForAccount(ankenId);
		return this.dispAnkenTanto(ankenTantoAccountBeanList, false);
	}

	/**
	 * 帳票に出力する裁判に紐づく弁護士、事務（主担当）を取得
	 *
	 * @param saibanSeq
	 * @return
	 */
	public List<SaibanTantoAccountDto> dispSaibanTantoBengoshiMainJimu(Long saibanSeq) {
		// 担当弁護士、担当事務を取得
		List<SaibanTantoAccountBean> saibanTantoAccountBeanList = tSaibanTantoDao.selectBySeqForAccount(saibanSeq);
		return this.dispSaibanTanto(saibanTantoAccountBeanList);
	}

	/**
	 * タイムチャージデータを取得
	 *
	 * @param kaikeiList
	 * @param customerId
	 * @return
	 */
	public ExcelTimeChargeDto getTimeChargeData(List<TKaikeiKirokuEntity> kaikeiList, Long customerId) {

		ExcelTimeChargeDto timeChargeData = new ExcelTimeChargeDto();

		// タイムチャージ出力データ取得
		List<ExcelTimeChargeListDto> excelTimeChargeListDto = seisanshoDocService.convertExcelTimeChargeList(kaikeiList);

		// タイムチャージが一件もない場合
		if (CollectionUtils.isEmpty(excelTimeChargeListDto)) {
			timeChargeData.setCreateFlg(false);
			return timeChargeData;
		}
		// タイムチャージ出力データを設定
		timeChargeData.setTimeChargeList(excelTimeChargeListDto);

		// 宛先名の設定
		String atesakiName = commonChohyoService.getAtesakiName(customerId, null);
		timeChargeData.setName(atesakiName);

		int totalTime = 0;
		for (ExcelTimeChargeListDto dto : excelTimeChargeListDto) {
			totalTime = totalTime + dto.getTime();
		}
		// 合計時間
		timeChargeData.setTotalTime(totalTime);

		// 報酬額
		List<BigDecimal> hoshuList = excelTimeChargeListDto.stream()
				.map(ExcelTimeChargeListDto::getShukkinGaku)
				.collect(Collectors.toList());

		// 報酬合計
		BigDecimal hoshuTotal = commonKaikeiService.calcTotal(hoshuList);
		timeChargeData.setHoshuTotal(hoshuTotal);
		timeChargeData.setDispHoshuTotal(commonKaikeiService.toDispAmountLabel(hoshuTotal));
		timeChargeData.setCreateFlg(true);

		return timeChargeData;
	}

	/**
	 * 帳票に出力する担当弁護士、担当事務をカンマ区切りのString型で生成
	 *
	 * @param dispAnkenTantoList
	 * @return
	 */
	public String dispAnkenTantoToComma(List<AnkenTantoAccountDto> dispAnkenTantoList) {

		List<String> accountNameList = dispAnkenTantoList.stream().map(AnkenTantoAccountDto::getAccountName).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(accountNameList)) {
			return CommonConstant.BLANK;
		}
		return StringUtils.list2Comma(accountNameList);
	}

	// ==========================================================================
	// privateメソッド
	// ==========================================================================
	/**
	 * 帳票に出力する弁護士、事務（主担当）を生成
	 *
	 * @param ankenTantoAccountBeanList
	 * @return
	 */
	private List<AnkenTantoAccountDto> dispAnkenTanto(List<AnkenTantoAccountBean> ankenTantoAccountBeanList, boolean jimuMainTantoFlg) {

		// 出力用のリスト
		List<AnkenTantoAccountDto> ankenTantoAccountDtoList = new ArrayList<AnkenTantoAccountDto>();

		// 事務リスト
		List<AnkenTantoAccountDto> tempJimuList = new ArrayList<AnkenTantoAccountDto>();
		// 売上計上先map
		Map<Long, Boolean> salesOwnerMap = new HashMap<Long, Boolean>();

		// 表示する担当者リスト
		for (AnkenTantoAccountBean bean : ankenTantoAccountBeanList) {

			// 帳票の表示用
			AnkenTantoAccountDto dto = new AnkenTantoAccountDto();
			BeanUtils.copyProperties(bean, dto);

			// 売上計上先と担当弁護士の除外フラグ
			boolean jyogaiFlg = false;

			Long accountSeq = bean.getAccountSeq();
			String tantoType = bean.getTantoType();

			// アカウントタイプの判定
			if (TantoType.SALES_OWNER.equalsByCode(tantoType)) {
				// 売上計上先の場合
				salesOwnerMap.put(accountSeq, Boolean.valueOf(false));

			} else {
				// 担当弁護士 or 担当事務

				if (TantoType.LAWYER.equalsByCode(tantoType)) {
					// 弁護士の場合

					// 売上計上先の弁護士がいるか判定
					if (salesOwnerMap.containsKey(accountSeq)) {
						jyogaiFlg = true;
						if (SystemFlg.FLG_ON.equalsByCode(bean.getAnkenMainTantoFlg())) {
							// 売上計上先の弁護士を主担当とする
							salesOwnerMap.put(accountSeq, Boolean.valueOf(true));
						}
					}

				} else {
					// 事務の場合
					jyogaiFlg = true;
					tempJimuList.add(dto);

				}

			}

			if (!jyogaiFlg) {
				ankenTantoAccountDtoList.add(dto);
			}

		}

		// 担当弁護士で主担当になっているか判定
		ankenTantoAccountDtoList.forEach(dto -> {
			Boolean tantoBengoshi = salesOwnerMap.get(dto.getAccountSeq());
			if (tantoBengoshi != null) {
				if (tantoBengoshi.booleanValue()) {
					// 主担当フラグをON
					dto.setAnkenMainTantoFlg(SystemFlg.FLG_ON.getCd());
				}
			}
		});

		if (jimuMainTantoFlg) {
			// 主担当の事務のみ抽出

			// 主担当の事務抽出
			List<AnkenTantoAccountDto> mainJimuList = tempJimuList.stream()
					.filter(e -> SystemFlg.FLG_ON.equalsByCode(e.getAnkenMainTantoFlg()))
					.collect(Collectors.toList());

			// 主担当の事務が存在する場合は、主担当の事務のみ表示対象
			if (!CollectionUtils.isEmpty(mainJimuList)) {
				// 主担当の事務
				tempJimuList = mainJimuList;
			}
		}

		// 事務員を追加
		ankenTantoAccountDtoList.addAll(tempJimuList);

		return ankenTantoAccountDtoList;
	}

	/**
	 * 帳票に出力する弁護士、事務（主担当）を生成(裁判)
	 *
	 * @param saibanTantoAccountBeanList
	 * @return
	 */
	private List<SaibanTantoAccountDto> dispSaibanTanto(List<SaibanTantoAccountBean> saibanTantoAccountBeanList) {

		// 出力用のリスト
		List<SaibanTantoAccountDto> saibanTantoAccountDtoList = new ArrayList<SaibanTantoAccountDto>();

		// 事務リスト
		List<SaibanTantoAccountDto> tempJimuList = new ArrayList<SaibanTantoAccountDto>();
		// 売上計上先map
		Map<Long, Boolean> salesOwnerMap = new HashMap<Long, Boolean>();

		for (SaibanTantoAccountBean bean : saibanTantoAccountBeanList) {

			// 帳票の表示用
			SaibanTantoAccountDto dto = new SaibanTantoAccountDto();
			BeanUtils.copyProperties(bean, dto);

			// 売上計上先と担当弁護士の除外フラグ
			boolean jyogaiFlg = false;

			Long accountSeq = bean.getAccountSeq();
			String tantoType = bean.getTantoType();

			// アカウントタイプの判定
			if (TantoType.SALES_OWNER.equalsByCode(tantoType)) {
				// 売上計上先の場合
				salesOwnerMap.put(accountSeq, Boolean.valueOf(false));

			} else {
				// 担当弁護士 or 担当事務

				if (TantoType.LAWYER.equalsByCode(tantoType)) {
					// 弁護士の場合

					// 売上計上先の弁護士がいるか判定
					if (salesOwnerMap.containsKey(accountSeq)) {
						jyogaiFlg = true;
						if (SystemFlg.FLG_ON.equalsByCode(bean.getSaibanMainTantoFlg())) {
							// 売上計上先の弁護士を主担当とする
							salesOwnerMap.put(accountSeq, Boolean.valueOf(true));
						}
					}

				} else {
					// 事務の場合
					jyogaiFlg = true;
					tempJimuList.add(dto);

				}

			}

			if (!jyogaiFlg) {
				saibanTantoAccountDtoList.add(dto);
			}

		}

		// 担当弁護士で主担当になっているか判定
		saibanTantoAccountDtoList.forEach(dto -> {
			Boolean tantoBengoshi = salesOwnerMap.get(dto.getAccountSeq());
			if (tantoBengoshi != null) {
				if (tantoBengoshi.booleanValue()) {
					// 主担当フラグをON
					dto.setSaibanMainTantoFlg(SystemFlg.FLG_ON.getCd());
				}
			}
		});

		// 主担当の事務抽出
		List<SaibanTantoAccountDto> mainJimuList = tempJimuList.stream()
				.filter(e -> SystemFlg.FLG_ON.equalsByCode(e.getSaibanMainTantoFlg()))
				.collect(Collectors.toList());

		// 主担当の事務が存在する場合は、主担当の事務のみ表示対象
		if (!CollectionUtils.isEmpty(mainJimuList)) {
			// 主担当の事務
			tempJimuList = mainJimuList;
		}

		// 事務員を追加
		saibanTantoAccountDtoList.addAll(tempJimuList);

		return saibanTantoAccountDtoList;
	}

	/**
	 * 公判担当検察官情報を取得し、オブジェクトを返す
	 *
	 * @param saibanSeq
	 * @return
	 */
	private Map<String, String> getAtesakiMapForKohan(Long saibanSeq) {

		// Map生成
		Map<String, String> atesakiMapKohan = new HashMap<String, String>();

		// 公判担当検察官情報を取得
		TSaibanAddKeijiEntity tSaibanAddKeijiEntity = tSaibanAddKeijiDao.selectBySaibanSeq(saibanSeq);
		if (tSaibanAddKeijiEntity == null) {
			// 公判担当検察官情報が存在しない場合
			return atesakiMapKohan;
		}

		// 公判担当検察官項目-画面項目
		String atesakiName1 = tSaibanAddKeijiEntity.getKensatsuchoName();
		String atesakiName2 = this.addKeishoSama(tSaibanAddKeijiEntity.getKensatsukanName());
		String atesakiName3 = this.addKeishoSama(tSaibanAddKeijiEntity.getJimukanName());
		String faxNo = tSaibanAddKeijiEntity.getKensatsuFaxNo();

		// 公判担当検察官項目-マスタ項目
		String zipCode = null;
		String address1 = null;
		String address2 = null;

		// 捜査機関マスタを取得
		MSosakikanEntity mSosakikanEntity = mSosakikanDao.selectById(tSaibanAddKeijiEntity.getSosakikanId());
		if (mSosakikanEntity != null) {
			zipCode = mSosakikanEntity.getSosakikanZip();
			address1 = mSosakikanEntity.getSosakikanAddress1();
			address2 = mSosakikanEntity.getSosakikanAddress2();
			atesakiName1 = mSosakikanEntity.getSosakikanName();
			if (StringUtils.isEmpty(atesakiName2) && StringUtils.isEmpty(atesakiName3)) {
				atesakiName1 = addKeishoHojin(atesakiName1, null, null);
			}
		}

		// Mapに設定
		atesakiMapKohan.put(TXT_SK_ATESAKI1_KEY, StringUtils.null2blank(atesakiName1));
		atesakiMapKohan.put(TXT_SK_ATESAKI2_KEY, StringUtils.null2blank(atesakiName2));
		atesakiMapKohan.put(TXT_SK_ATESAKI3_KEY, StringUtils.null2blank(atesakiName3));
		atesakiMapKohan.put(TXT_FAX_NO_KEY, StringUtils.null2blank(faxNo));

		// 〒番号、住所を取得
		atesakiMapKohan.put(TXT_ZIPCODE_KEY, StringUtils.null2blank(zipCode));
		atesakiMapKohan.put(TXT_ADDRESS1_KEY, StringUtils.null2blank(address1));
		atesakiMapKohan.put(TXT_ADDRESS2_KEY, StringUtils.null2blank(address2));

		return atesakiMapKohan;
	}

	/**
	 * 捜査機関情報を取得し、オブジェクトを返す
	 *
	 * @param sosakikanSeq 捜査機関連番
	 * @return 入力フォーム
	 */
	private Map<String, String> getAtesakiMapForSosaKikan(Long sosakikanSeq) {

		// Map生成
		Map<String, String> atesakiMapSosakina = new HashMap<String, String>();

		// 捜査機関情報を取得
		TAnkenSosakikanEntity tAnkenSosakikanEntity = tAnkenSosakikanDao.selectBySosakikanSeq(sosakikanSeq);
		if (tAnkenSosakikanEntity == null) {
			// 捜査機関情報が存在しない場合
			return atesakiMapSosakina;
		}

		// 捜査機関項目-画面項目
		String atesakiName1 = tAnkenSosakikanEntity.getSosakikanName();
		String atesakiName2 = this.addKeishoSama(tAnkenSosakikanEntity.getTantosha1Name());
		String atesakiName3 = this.addKeishoSama(tAnkenSosakikanEntity.getTantosha2Name());
		String faxNo = tAnkenSosakikanEntity.getSosakikanFaxNo();

		// 捜査機関項目-マスタ項目
		String zipCode = null;
		String address1 = null;
		String address2 = null;

		// 捜査機関マスタを取得
		MSosakikanEntity mSosakikanEntity = mSosakikanDao.selectById(tAnkenSosakikanEntity.getSosakikanId());
		if (mSosakikanEntity != null) {
			zipCode = mSosakikanEntity.getSosakikanZip();
			address1 = mSosakikanEntity.getSosakikanAddress1();
			address2 = mSosakikanEntity.getSosakikanAddress2();
			atesakiName1 = mSosakikanEntity.getSosakikanName();
			if (StringUtils.isEmpty(atesakiName2) && StringUtils.isEmpty(atesakiName3)) {
				atesakiName1 = addKeishoHojin(atesakiName1, null, null);
			}
		}

		// Mapに設定
		atesakiMapSosakina.put(TXT_SK_ATESAKI1_KEY, StringUtils.null2blank(atesakiName1));
		atesakiMapSosakina.put(TXT_SK_ATESAKI2_KEY, StringUtils.null2blank(atesakiName2));
		atesakiMapSosakina.put(TXT_SK_ATESAKI3_KEY, StringUtils.null2blank(atesakiName3));
		atesakiMapSosakina.put(TXT_FAX_NO_KEY, StringUtils.null2blank(faxNo));

		// 〒番号、住所を取得
		atesakiMapSosakina.put(TXT_ZIPCODE_KEY, StringUtils.null2blank(zipCode));
		atesakiMapSosakina.put(TXT_ADDRESS1_KEY, StringUtils.null2blank(address1));
		atesakiMapSosakina.put(TXT_ADDRESS2_KEY, StringUtils.null2blank(address2));

		return atesakiMapSosakina;
	}

	/**
	 * 送付書(FAX)を出力する
	 *
	 * <pre>
	 * 「捜査機関」or「公判担当検察官」
	 * </pre>
	 *
	 * @param response
	 * @param ankenId
	 * @param sosakikanSeq
	 * @param saibanSeq
	 * @throws Exception
	 */
	private void outputSouhushoFaxForSosaKikanKohan(HttpServletResponse response, Long ankenId, Long sosakikanSeq, Long saibanSeq)
			throws Exception {

		// ■1.BuilderとDTOを定義
		Wn0007WordBuilder wn0007WordBuilder = new Wn0007WordBuilder();
		Wn0007WordDto wn0007WordDto = wn0007WordBuilder.createNewTargetBuilderDto();

		// ■2.WordConfigを設定
		wn0007WordBuilder.setConfig(wordConfig);

		// ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定
		// ID
		wn0007WordDto.setId(StringUtils.null2blank(ankenId));

		// 宛先情報
		Map<String, String> atesakiMap = new HashMap<String, String>();
		if (sosakikanSeq != null) {
			// 捜査機関の場合
			atesakiMap = this.getAtesakiMapForSosaKikan(sosakikanSeq);

		} else if (saibanSeq != null) {
			// 公判担当検察官情報の場合
			atesakiMap = this.getAtesakiMapForKohan(saibanSeq);

		}

		// 宛先情報
		wn0007WordDto.setFaxNo(atesakiMap.get(TXT_FAX_NO_KEY));
		wn0007WordDto.setAtesaki1(atesakiMap.get(TXT_SK_ATESAKI1_KEY));
		wn0007WordDto.setAtesaki2(atesakiMap.get(TXT_SK_ATESAKI2_KEY));
		wn0007WordDto.setAtesaki3(atesakiMap.get(TXT_SK_ATESAKI3_KEY));

		// 現在日付を和暦文字列で取得
		String dateStr = DateUtils.getDateToJaDate();
		wn0007WordDto.setCreatedAt(dateStr);

		// 事務所情報
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());

		wn0007WordDto.setTenantZipCode(StringUtils.null2blank(mTenantEntity.getTenantZipCd()));
		wn0007WordDto.setTenantAddress1(StringUtils.null2blank(mTenantEntity.getTenantAddress1()));
		wn0007WordDto.setTenantAddress2(StringUtils.null2blank(mTenantEntity.getTenantAddress2()));
		wn0007WordDto.setTenantName(StringUtils.null2blank(mTenantEntity.getTenantName()));
		wn0007WordDto.setTenantTelNo(StringUtils.null2blank(mTenantEntity.getTenantTelNo()));
		wn0007WordDto.setTenantFaxNo(StringUtils.null2blank(mTenantEntity.getTenantFaxNo()));

		// 担当弁護士、担当事務情報の取得
		List<AnkenTantoAccountDto> dispAnkenTanto = this.dispAnkenTantoBengoshiMainJimu(ankenId);
		wn0007WordDto.setAnkenTantoAccountDtoList(dispAnkenTanto);

		wn0007WordBuilder.setWn0007WordDto(wn0007WordDto);

		try {
			// Wordファイルの出力処理
			wn0007WordBuilder.makeWordFile(response);

		} catch (Exception e) {
			throw new RuntimeException(e);

		}

	}

	/**
	 * 送付書(一般)を出力する
	 *
	 * <pre>
	 * 「捜査機関」or「公判担当検察官」
	 * </pre>
	 *
	 * @param response
	 * @param ankenId
	 * @param sosakikanSeq
	 * @param saibanSeq
	 * @throws Exception
	 */
	private void outputSouhushoIppanForSosaKikanKohan(HttpServletResponse response, Long ankenId, Long sosakikanSeq, Long saibanSeq)
			throws Exception {

		// ■1.BuilderとDTOを定義
		Wn0006WordBuilder wn0006WordBuilder = new Wn0006WordBuilder();
		Wn0006WordDto wn0006WordDto = wn0006WordBuilder.createNewTargetBuilderDto();

		// ■2.WordConfigを設定
		wn0006WordBuilder.setConfig(wordConfig);

		// ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定
		// ID
		wn0006WordDto.setId(StringUtils.null2blank(ankenId));

		// 宛先情報
		Map<String, String> atesakiMap = new HashMap<String, String>();
		if (sosakikanSeq != null) {
			// 捜査機関の場合
			atesakiMap = this.getAtesakiMapForSosaKikan(sosakikanSeq);

		} else if (saibanSeq != null) {
			// 公判担当検察官情報の場合
			atesakiMap = this.getAtesakiMapForKohan(saibanSeq);

		}

		// 宛先情報
		wn0006WordDto.setZipCode(atesakiMap.get(TXT_ZIPCODE_KEY));
		wn0006WordDto.setAddress1(atesakiMap.get(TXT_ADDRESS1_KEY));
		wn0006WordDto.setAddress2(atesakiMap.get(TXT_ADDRESS2_KEY));
		wn0006WordDto.setAtesaki1(atesakiMap.get(TXT_SK_ATESAKI1_KEY));
		wn0006WordDto.setAtesaki2(atesakiMap.get(TXT_SK_ATESAKI2_KEY));
		wn0006WordDto.setAtesaki3(atesakiMap.get(TXT_SK_ATESAKI3_KEY));

		// 現在日付を和暦文字列で取得
		String dateStr = DateUtils.getDateToJaDate();
		wn0006WordDto.setCreatedAt(dateStr);

		// 事務所情報
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());

		wn0006WordDto.setTenantZipCode(StringUtils.null2blank(mTenantEntity.getTenantZipCd()));
		wn0006WordDto.setTenantAddress1(StringUtils.null2blank(mTenantEntity.getTenantAddress1()));
		wn0006WordDto.setTenantAddress2(StringUtils.null2blank(mTenantEntity.getTenantAddress2()));
		wn0006WordDto.setTenantName(StringUtils.null2blank(mTenantEntity.getTenantName()));
		wn0006WordDto.setTenantTelNo(StringUtils.null2blank(mTenantEntity.getTenantTelNo()));
		wn0006WordDto.setTenantFaxNo(StringUtils.null2blank(mTenantEntity.getTenantFaxNo()));

		// 担当弁護士、担当事務情報の取得
		List<AnkenTantoAccountDto> dispAnkenTanto = this.dispAnkenTantoBengoshiMainJimu(ankenId);
		wn0006WordDto.setAnkenTantoAccountDtoList(dispAnkenTanto);

		wn0006WordBuilder.setWn0006WordDto(wn0006WordDto);

		try {
			// Wordファイルの出力処理
			wn0006WordBuilder.makeWordFile(response);

		} catch (Exception e) {
			throw new RuntimeException(e);

		}

	}

	/**
	 * 外○名の形式で値を返す
	 * 
	 * @param dataCount
	 * @return
	 */
	public String formatOthersCount(Long dataCount) {
		String formatVal = CommonConstant.BLANK;
		if (dataCount != null) {
			long allDataCount = dataCount.longValue();
			if (allDataCount > 1L) {
				formatVal = " 外" + String.valueOf(allDataCount - 1L) + "名";
			}
		}
		return formatVal;
	}
}
