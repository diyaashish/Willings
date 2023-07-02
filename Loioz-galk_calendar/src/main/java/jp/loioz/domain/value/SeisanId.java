package jp.loioz.domain.value;

import java.util.Comparator;

import org.seasar.doma.Column;
import org.seasar.doma.Embeddable;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.LoiozObjectUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.entity.TSeisanKirokuEntity;
import lombok.Value;

/**
 * 精算ID
 */
@Value
@Embeddable
public class SeisanId implements Comparable<SeisanId> {

	private static final String DELIMITER = "-";

	/** 顧客ID */
	@Column(name = "customer_id")
	CustomerId customerId;

	/** 精算ID */
	@Column(name = "seisan_id")
	Long seisanId;

	public SeisanId(CustomerId customerId, Long seisanId) {
		this.customerId = LoiozObjectUtils.defaultIfNull(customerId, CustomerId.of(customerId));
		this.seisanId = seisanId;
	}

	public SeisanId(Long customerId, Long seisanId) {
		this.customerId = CustomerId.of(customerId);
		this.seisanId = seisanId;
	}

	/**
	 * 文字列から精算IDに変換する<br>
	 * 精算IDとして正しい文字列でない場合はnullを返却する<br>
	 * <br>
	 * 書式：{顧客ID}-{精算ID}
	 *
	 * @param str 精算ID文字列
	 * @return 精算ID
	 */
	public static SeisanId parse(String str) {

		String[] splitStr = StringUtils.split(str, DELIMITER);
		if (splitStr == null || splitStr.length != 2) {
			return null;
		}

		Long customerId = LoiozNumberUtils.parseAsLong(splitStr[0]);
		Long seisanId = LoiozNumberUtils.parseAsLong(splitStr[1]);
		if (!LoiozObjectUtils.allNotNull(customerId, seisanId)) {
			return null;
		}

		return new SeisanId(customerId, seisanId);
	}

	/**
	 * 精算記録情報エンティティから精算IDに変換する
	 *
	 * @param entity エンティティ
	 * @return 精算ID
	 */
	public static SeisanId fromEntity(TSeisanKirokuEntity entity) {
		if (entity == null) {
			return null;
		}
		return new SeisanId(entity.getCustomerId(), entity.getSeisanId());
	}

	@Override
	public String toString() {
		if (!LoiozObjectUtils.allNotNull(customerId, seisanId)) {
			return CommonConstant.BLANK;
		}
		return customerId.asLong() + DELIMITER + seisanId;
	}

	@Override
	public int compareTo(SeisanId other) {
		Comparator<CustomerId> customerIdComparator = Comparator.nullsLast(CustomerId::compareTo);
		Comparator<Long> seisanIdComparator = Comparator.nullsLast(Long::compareTo);

		Comparator<SeisanId> saibanAnkenIdComparator = Comparator.nullsLast(
				Comparator.comparing(SeisanId::getCustomerId, customerIdComparator));
		Comparator<SeisanId> saibanBranchNumberComparator = Comparator.nullsLast(
				Comparator.comparing(SeisanId::getSeisanId, seisanIdComparator));

		return saibanAnkenIdComparator
				.thenComparing(saibanBranchNumberComparator)
				.compare(this, other);
	}
}
