package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant.ToiawaseStatus;
import jp.loioz.common.constant.CommonConstant.ToiawaseType;
import lombok.Data;

/**
 * 問い合わせ一覧表示用Dto
 */
@Data
public class ToiawaseListDto {

	/** 問い合わせSEQ */
	private Long toiawaseSeq;

	/** 件名 */
	private String subject;

	/** 問い合わせ種別 */
	private ToiawaseType toiawaseType;

	/** 作成日時 */
	private String createdAt;

	/** 直近のアクティビティ */
	private String updatedAt;

	/** 問い合わせステータス */
	private ToiawaseStatus toiawaseStatus;

	/** 未読の詳細が存在するかどうか */
	private boolean hasNoReadDetail;

}
