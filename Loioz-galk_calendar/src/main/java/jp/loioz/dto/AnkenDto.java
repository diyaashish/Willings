package jp.loioz.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.SaibanId;
import lombok.Builder;
import lombok.Data;

/**
 * 案件情報
 */
@Data
@Builder
public class AnkenDto {

	/** 遷移元顧客ID */
	private Long transitionCustomerId;

	/** 案件ID */
	private AnkenId ankenId;

	/** 分野 */
	private String bunya;

	/** 分野種別 */
	private String bunyaType;

	/** 案件種別 */
	private AnkenType ankenType;

	/** 案件名 */
	private String ankenName;

	/** 顧客 */
	private List<Customer> customerList;

	/** 相手方 */
	private List<Aitegata> aitegataList;

	/** 共犯者 */
	private List<Kyohansha> kyohanshaList;

	/** 被害者 */
	private List<Higaisha> higaishaList;

	/** 当事者・関与者 */
	private List<TojishaKanyosha> tojishaKanyoshaList;

	/** 案件ステータス */
	private String ankenStatus;

	/** 案件ステータス名 */
	private String ankenStatusName;

	/** 案件が終了したかどうか */
	private boolean isCompAnken;

	/** 裁判IDリスト */
	private List<SaibanId> saibanIdList;

	/** 裁判ステータス */
	private String saibanStatus;

	/**
	 * 顧客全員分の名前を取得する
	 * 
	 * @param personIdToFirstDisplay 最初に表示したい名前の名簿ID
	 * @return
	 */
	public String getCustomerName(Long personIdToFirstDisplay) {
		if (LoiozCollectionUtils.isEmpty(customerList)) {
			return "";
		}
		List<String> customerNameList = new ArrayList<>();
		for (Customer customer : customerList) {
			// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
			if (personIdToFirstDisplay.equals(customer.getPersonId())) {
				customerNameList.add(0, customer.getCustomerName());
			} else {
				customerNameList.add(customer.getCustomerName());
			}
		}
		// カンマ区切りに変換
		return String.join(", ", customerNameList);
	}

	/**
	 * 顧客にパラメータ名簿IDの名簿が存在するか
	 * 
	 * @param personId
	 * @return
	 */
	public boolean existsCustomer(Long personId) {
		if (personId == null) {
			return false;
		}
		long count = customerList.stream().filter(customer -> personId.equals(customer.getPersonId())).count();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 引数のpersonIdが顧客リストに存在する場合は、<br>
	 * その顧客のタイトルラベルを取得する。
	 * 
	 * @param personId
	 * @return
	 */
	public String getCustomerTitleLabel(Long personId) {
		if (personId == null) {
			return "";
		}

		Optional<Customer> customerDtoOpt = customerList.stream().filter(customer -> personId.equals(customer.getPersonId())).findFirst();
		if (customerDtoOpt.isEmpty()) {
			return "";
		}

		return "顧客";
	}

	/**
	 * 相手方全員分の名前を取得する
	 * 
	 * @param personIdToFirstDisplay 最初に表示したい名前の名簿ID
	 * @return
	 */
	public String getAitegataName(Long personIdToFirstDisplay) {
		if (LoiozCollectionUtils.isEmpty(aitegataList)) {
			return "";
		}
		// 相手方代理人だけのリスト
		List<Aitegata> dairininList = aitegataList.stream().filter(aitegata -> SystemFlg.FLG_ON.equalsByCode(aitegata.getDairiFlg())).collect(Collectors.toList());

		// 相手方名（代理人名） の文字列リストを作成
		List<String> aitegataNameList = new ArrayList<>();
		for (Aitegata aitegata : aitegataList.stream().filter(aitegata -> SystemFlg.FLG_OFF.equalsByCode(aitegata.getDairiFlg())).collect(Collectors.toList())) {
			// 代理人が設定されている場合は、代理人名を括弧でくくる
			if (aitegata.getRelatedKanyoshaSeq() != null) {
				for (Aitegata dairinin : dairininList) {
					if (aitegata.getRelatedKanyoshaSeq().equals(dairinin.getKanyoshaSeq())) {
						// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
						if (personIdToFirstDisplay.equals(aitegata.getPersonId()) || personIdToFirstDisplay.equals(dairinin.getPersonId())) {
							aitegataNameList.add(0, aitegata.getKanyoshaName() + "（" + dairinin.getKanyoshaName() + "）");
						} else {
							aitegataNameList.add(aitegata.getKanyoshaName() + "（" + dairinin.getKanyoshaName() + "）");
						}
					} else {
						// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
						if (personIdToFirstDisplay.equals(aitegata.getPersonId())) {
							aitegataNameList.add(0, aitegata.getKanyoshaName());
						} else {
							aitegataNameList.add(aitegata.getKanyoshaName());
						}
					}
				}
			} else {
				// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
				if (personIdToFirstDisplay.equals(aitegata.getPersonId())) {
					aitegataNameList.add(0, aitegata.getKanyoshaName());
				} else {
					aitegataNameList.add(aitegata.getKanyoshaName());
				}
			}
		}
		// カンマ区切りに変換
		return String.join(", ", aitegataNameList);
	}

	/**
	 * 相手方にパラメータ名簿IDの名簿が存在するか
	 * 
	 * @param personId
	 * @return
	 */
	public boolean existsAitegata(Long personId) {
		if (personId == null) {
			return false;
		}
		long count = aitegataList.stream().filter(aitegata -> personId.equals(aitegata.getPersonId())).count();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 引数のpersonIdが相手方リストに存在する場合は、<br>
	 * その相手方のタイトルラベルを取得する。
	 * 
	 * @param personId
	 * @return
	 */
	public String getAitegataTitleLabel(Long personId) {
		if (personId == null) {
			return "";
		}

		Optional<Aitegata> aitegataDtoOpt = aitegataList.stream().filter(aitegata -> personId.equals(aitegata.getPersonId())).findFirst();
		if (aitegataDtoOpt.isEmpty()) {
			return "";
		}

		Aitegata aitegataDto = aitegataDtoOpt.get();
		if (SystemFlg.codeToBoolean(aitegataDto.getDairiFlg())) {
			// 代理人の場合
			return "相手方代理人";
		} else {
			return "相手方";
		}
	}

	/**
	 * 共犯者全員分の名前を取得する
	 * 
	 * @param personIdToFirstDisplay 最初に表示したい名前の名簿ID
	 * @return
	 */
	public String getKyohanshaName(Long personIdToFirstDisplay) {
		if (LoiozCollectionUtils.isEmpty(kyohanshaList)) {
			return "";
		}
		// 共犯者代理人だけのリスト
		List<Kyohansha> dairininList = kyohanshaList.stream().filter(kyohansha -> SystemFlg.FLG_ON.equalsByCode(kyohansha.getDairiFlg())).collect(Collectors.toList());

		// 共犯者名（代理人名） の文字列リストを作成
		List<String> kyohanshaNameList = new ArrayList<>();
		for (Kyohansha kyohansha : kyohanshaList.stream().filter(kyohansha -> SystemFlg.FLG_OFF.equalsByCode(kyohansha.getDairiFlg())).collect(Collectors.toList())) {
			// 代理人が設定されている場合は、代理人名を括弧でくくる
			if (kyohansha.getRelatedKanyoshaSeq() != null) {
				for (Kyohansha dairinin : dairininList) {
					if (kyohansha.getRelatedKanyoshaSeq().equals(dairinin.getKanyoshaSeq())) {
						// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
						if (personIdToFirstDisplay.equals(kyohansha.getPersonId()) || personIdToFirstDisplay.equals(dairinin.getPersonId())) {
							kyohanshaNameList.add(0, kyohansha.getKanyoshaName() + "（" + dairinin.getKanyoshaName() + "）");
						} else {
							kyohanshaNameList.add(kyohansha.getKanyoshaName() + "（" + dairinin.getKanyoshaName() + "）");
						}
					} else {
						// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
						if (personIdToFirstDisplay.equals(kyohansha.getPersonId())) {
							kyohanshaNameList.add(0, kyohansha.getKanyoshaName());
						} else {
							kyohanshaNameList.add(kyohansha.getKanyoshaName());
						}
					}
				}
			} else {
				// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
				if (personIdToFirstDisplay.equals(kyohansha.getPersonId())) {
					kyohanshaNameList.add(0, kyohansha.getKanyoshaName());
				} else {
					kyohanshaNameList.add(kyohansha.getKanyoshaName());
				}
			}
		}
		// カンマ区切りに変換
		return String.join(", ", kyohanshaNameList);
	}

	/**
	 * 共犯者にパラメータ名簿IDの名簿が存在するか
	 * 
	 * @param personId
	 * @return
	 */
	public boolean existsKyohansha(Long personId) {
		if (personId == null) {
			return false;
		}
		long count = kyohanshaList.stream().filter(kyohansha -> personId.equals(kyohansha.getPersonId())).count();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 引数のpersonIdが共犯者リストに存在する場合は、<br>
	 * その共犯者のタイトルラベルを取得する。
	 * 
	 * @param personId
	 * @return
	 */
	public String getKyohanshaTitleLabel(Long personId) {
		if (personId == null) {
			return "";
		}

		Optional<Kyohansha> kyohanshaDtoOpt = kyohanshaList.stream().filter(kyohansha -> personId.equals(kyohansha.getPersonId())).findFirst();
		if (kyohanshaDtoOpt.isEmpty()) {
			return "";
		}

		Kyohansha kyohanshaDto = kyohanshaDtoOpt.get();
		if (SystemFlg.codeToBoolean(kyohanshaDto.getDairiFlg())) {
			// 弁護人の場合
			return "共犯者弁護人";
		} else {
			return "共犯者";
		}
	}

	/**
	 * 被害者全員分の名前を取得する
	 * 
	 * @param personIdToFirstDisplay 最初に表示したい名前の名簿ID
	 * @return
	 */
	public String getHigaishaName(Long personIdToFirstDisplay) {
		if (LoiozCollectionUtils.isEmpty(higaishaList)) {
			return "";
		}
		// 被害者代理人だけのリスト
		List<Higaisha> dairininList = higaishaList.stream().filter(higaisha -> SystemFlg.FLG_ON.equalsByCode(higaisha.getDairiFlg())).collect(Collectors.toList());

		// 被害者名（代理人名） の文字列リストを作成
		List<String> higaishaNameList = new ArrayList<>();
		for (Higaisha higaisha : higaishaList.stream().filter(higaisha -> SystemFlg.FLG_OFF.equalsByCode(higaisha.getDairiFlg())).collect(Collectors.toList())) {
			// 代理人が設定されている場合は、代理人名を括弧でくくる
			if (higaisha.getRelatedKanyoshaSeq() != null) {
				for (Higaisha dairinin : dairininList) {
					if (higaisha.getRelatedKanyoshaSeq().equals(dairinin.getKanyoshaSeq())) {
						// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
						if (personIdToFirstDisplay.equals(higaisha.getPersonId()) || personIdToFirstDisplay.equals(dairinin.getPersonId())) {
							higaishaNameList.add(0, higaisha.getKanyoshaName() + "（" + dairinin.getKanyoshaName() + "）");
						} else {
							higaishaNameList.add(higaisha.getKanyoshaName() + "（" + dairinin.getKanyoshaName() + "）");
						}
					} else {
						// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
						if (personIdToFirstDisplay.equals(higaisha.getPersonId())) {
							higaishaNameList.add(0, higaisha.getKanyoshaName());
						} else {
							higaishaNameList.add(higaisha.getKanyoshaName());
						}
					}
				}
			} else {
				// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
				if (personIdToFirstDisplay.equals(higaisha.getPersonId())) {
					higaishaNameList.add(0, higaisha.getKanyoshaName());
				} else {
					higaishaNameList.add(higaisha.getKanyoshaName());
				}
			}
		}
		// カンマ区切りに変換
		return String.join(", ", higaishaNameList);
	}

	/**
	 * 被害者にパラメータ名簿IDの名簿が存在するか
	 * 
	 * @param personId
	 * @return
	 */
	public boolean existsHigaisha(Long personId) {
		if (personId == null) {
			return false;
		}
		long count = higaishaList.stream().filter(higaisha -> personId.equals(higaisha.getPersonId())).count();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 引数のpersonIdが被害者リストに存在する場合は、<br>
	 * その被害者のタイトルラベルを取得する。
	 * 
	 * @param personId
	 * @return
	 */
	public String getHigaishaTitleLabel(Long personId) {
		if (personId == null) {
			return "";
		}

		Optional<Higaisha> higaishaDtoOpt = higaishaList.stream().filter(higaisha -> personId.equals(higaisha.getPersonId())).findFirst();
		if (higaishaDtoOpt.isEmpty()) {
			return "";
		}

		Higaisha higaishaDto = higaishaDtoOpt.get();
		if (SystemFlg.codeToBoolean(higaishaDto.getDairiFlg())) {
			// 代理人の場合
			return "被害者代理人";
		} else {
			return "被害者";
		}
	}

	/**
	 * 当事者・関与者全員分の名前を取得する
	 * 
	 * @param personIdToFirstDisplay 最初に表示したい名前の名簿ID
	 * @return
	 */
	public String getTojishaKanyoshaName(Long personIdToFirstDisplay) {
		if (LoiozCollectionUtils.isEmpty(tojishaKanyoshaList)) {
			return "";
		}
		List<String> tojishaKanyoshaNameList = new ArrayList<>();
		for (TojishaKanyosha tojishaKanyosha : tojishaKanyoshaList) {
			// 先頭に表示したい名簿IDと合致する場合、先頭に追加する
			if (personIdToFirstDisplay.equals(tojishaKanyosha.getPersonId())) {
				tojishaKanyoshaNameList.add(0, tojishaKanyosha.getKanyoshaName());
			} else {
				tojishaKanyoshaNameList.add(tojishaKanyosha.getKanyoshaName());
			}
		}

		return String.join(", ", tojishaKanyoshaNameList);
	}

	/**
	 * 当事者・関与者にパラメータ名簿IDの名簿が存在するか
	 * 
	 * @param personId
	 * @return
	 */
	public boolean existsTojishaKanyosha(Long personId) {
		if (personId == null) {
			return false;
		}
		long count = tojishaKanyoshaList.stream().filter(tojishaKanyosha -> personId.equals(tojishaKanyosha.getPersonId())).count();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 引数のpersonIdが当事者・関与者リストに存在する場合は、<br>
	 * その当事者・関与者のタイトルラベルを取得する。
	 * 
	 * @param personId
	 * @return
	 */
	public String getTojishaKanyoshaTitleLabel(Long personId) {
		if (personId == null) {
			return "";
		}

		Optional<TojishaKanyosha> tojishaKanyoshaDtoOpt = tojishaKanyoshaList.stream().filter(tojishaKanyosha -> personId.equals(tojishaKanyosha.getPersonId())).findFirst();
		if (tojishaKanyoshaDtoOpt.isEmpty()) {
			return "";
		}

		return "関与者";
	}

	/**
	 * 案件の顧客情報
	 *
	 */
	@Data
	@Builder
	public static class Customer {

		/** 名簿ID */
		private Long personId;

		/** 顧客名 */
		private String customerName;
	}

	/**
	 * 案件の相手方情報
	 *
	 */
	@Data
	@Builder
	public static class Aitegata {

		/** 関与者SEQ */
		private Long kanyoshaSeq;

		/** 名簿ID */
		private Long personId;

		/** 関与者種別 */
		private String kanyoshaType;

		/** 代理フラグ */
		private String dairiFlg;

		/** 関与者名 */
		private String kanyoshaName;

		/** 代理人関与者SEQ */
		private Long relatedKanyoshaSeq;
	}

	/**
	 * 案件の共犯者情報
	 *
	 */
	@Data
	@Builder
	public static class Kyohansha {

		/** 関与者SEQ */
		private Long kanyoshaSeq;

		/** 名簿ID */
		private Long personId;

		/** 関与者種別 */
		private String kanyoshaType;

		/** 代理フラグ */
		private String dairiFlg;

		/** 関与者名 */
		private String kanyoshaName;

		/** 代理人関与者SEQ */
		private Long relatedKanyoshaSeq;
	}

	/**
	 * 案件の被害者情報
	 *
	 */
	@Data
	@Builder
	public static class Higaisha {

		/** 関与者SEQ */
		private Long kanyoshaSeq;

		/** 名簿ID */
		private Long personId;

		/** 関与者種別 */
		private String kanyoshaType;

		/** 代理フラグ */
		private String dairiFlg;

		/** 関与者名 */
		private String kanyoshaName;

		/** 代理人関与者SEQ */
		private Long relatedKanyoshaSeq;
	}

	/**
	 * 案件の当事者・関与者情報
	 *
	 */
	@Data
	@Builder
	public static class TojishaKanyosha {

		/** 関与者SEQ */
		private Long kanyoshaSeq;

		/** 名簿ID */
		private Long personId;

		/** 関与者名 */
		private String kanyoshaName;
	}

}