package jp.loioz.dto;

import java.math.BigDecimal;
import java.util.List;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SeisanId;
import lombok.Data;

@Data
public class NyushukkinYoteiListDto {

	// ********************************
	// DBと対となる情報
	// ********************************
	// t_nyushukkin_yotei
	/** 入出金予定SEQ */
	private Long nyushukkinYoteiSeq;

	/** 顧客ID */
	private Long customerId;

	/** 案件ID */
	private Long ankenId;

	/** 入出金タイプ */
	private String nyushukkinType;

	/** 入出金項目ID */
	private Long nyushukkinKomokuId;

	/** 入出金項目名 */
	private String nyushukkinKomokuName;

	/** 入金-支払者種別 */
	private String nyukinShiharaishaType;

	/** 入金-支払者 */
	private String nyukinShiharaisha;

	/** 入金-入金先口座SEQ */
	private Long nyukinSakiKozaSeq;

	/** 出金-出金先口座SEQ */
	private Long shukkinSakiKozaSeq;

	/** 出金-支払先アカウント種別 */
	private String shukkinShiharaiSakiAcctType;

	/** 出金-支払先アカウントSEQ */
	private Long shukkinShiharaiSakiAcctSeq;

	/** 入出金予定日 */
	private String nyushukkinYoteiDate;

	/** 入出金予定額 */
	private BigDecimal nyushukkinYoteiGaku;

	/** 入出金日 */
	private String nyushukkinDate;

	/** 入出金額 */
	private BigDecimal nyushukkinGaku;

	/** 摘要 */
	private String tekiyo;

	/** 精算SEQ */
	private Long seisanSeq;

	// t_anken
	/** 案件名 */
	private String ankenName;

	/** 案件名(表示用) */
	private String dispAnkenName;

	// t_person
	/** 顧客名 */
	private String customerName;

	// ********************************
	// 画面表示用
	// ********************************

	/** 案件ID(表示用) */
	private AnkenId displayAnkenId;

	/** 顧客ID(表示用) */
	private CustomerId displayCustomerId;

	/** 精算ID(表示用) */
	private SeisanId displaySeisanId;

	/** 入出金タイプが入金かどうか */
	private boolean isNyukin;

	/** 出金時の支払先口座のユニークID */
	private Long shukkinShiharaiSakiUniqueId;

	/** 入金項目ID */
	private Long nyukinKomokuId;

	/** 出金項目ID */
	private Long shukkinKomokuId;

	/** 入出金予定額(文字列) */
	private String displayNyushukkinYoteiGaku;

	/** 入金予定額(文字列) */
	private String displayNyukinYoteiGaku;

	/** 入金予定額(文字列) */
	private String displayShukkinYoteiGaku;

	/** 入出金額(文字列) */
	private String displayNyushukkinGaku;

	/** 処理済みかどうか */
	private boolean isCompleted = false;

	/** 事務所・担当弁護士の口座リスト */
	private List<GinkoKozaDto> tenantAccountGinkoKozaList;

	/** 支払者：顧客ID */
	private Long shiharaiCustomerId;

	/** 入金済みかどうか */
	private String completeFlg;

	/** data-target(データによって変えたいのでここで定義) */
	private String dataTarget;

	/**
	 * 入金-入金先口座の表示名を取得します。
	 * 
	 * @return 出金先口座の表示名
	 */
	public String getNyukinSakiKozaName() {

		for (GinkoKozaDto dto : tenantAccountGinkoKozaList) {

			if (dto.getGinkoAccountSeq().equals(nyukinSakiKozaSeq)) {
				return dto.getListDisplayName();
			}
		}
		return null;
	}

	/**
	 * 出金-出金先口座の表示名を取得します。
	 * 
	 * @return 出金先口座の表示名
	 */
	public String getShukkinSakiKozaName() {

		for (GinkoKozaDto dto : tenantAccountGinkoKozaList) {

			if (dto.getGinkoAccountSeq().equals(shukkinSakiKozaSeq)) {
				return dto.getListDisplayName();
			}
		}
		return null;
	}
}