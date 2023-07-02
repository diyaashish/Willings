package jp.loioz.domain.value;

import java.util.Comparator;

import org.seasar.doma.Column;
import org.seasar.doma.Embeddable;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.LoiozObjectUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.entity.TSaibanEntity;
import lombok.Value;

/**
 * 裁判ID
 */
@Value
@Embeddable
public class SaibanId implements Comparable<SaibanId> {

	private static final String DELIMITER = "-";

	/** 案件ID */
	@Column(name = "anken_id")
	AnkenId ankenId;

	/** 枝番 */
	@Column(name = "saiban_branch_no")
	Long branchNumber;

	public SaibanId(AnkenId ankenId, Long branchNumber) {
		this.ankenId = LoiozObjectUtils.defaultIfNull(ankenId, AnkenId.of(ankenId));
		this.branchNumber = branchNumber;
	}

	public SaibanId(Long ankenId, Long branchNumber) {
		this.ankenId = AnkenId.of(ankenId);
		this.branchNumber = branchNumber;
	}

	/**
	 * 文字列から裁判IDに変換する<br>
	 * 裁判IDとして正しい文字列でない場合はnullを返却する<br>
	 * <br>
	 * 書式：{案件ID}-{枝番}
	 *
	 * @param str 裁判ID文字列
	 * @return 裁判ID
	 */
	public static SaibanId parse(String str) {

		String[] splitStr = StringUtils.split(str, DELIMITER);
		if (splitStr == null || splitStr.length != 2) {
			return null;
		}

		Long ankenId = LoiozNumberUtils.parseAsLong(splitStr[0]);
		Long branchNumber = LoiozNumberUtils.parseAsLong(splitStr[1]);
		if (!LoiozObjectUtils.allNotNull(ankenId, branchNumber)) {
			return null;
		}

		return new SaibanId(ankenId, branchNumber);
	}

	/**
	 * 裁判情報エンティティから裁判IDに変換する
	 *
	 * @param entity エンティティ
	 * @return 裁判ID
	 */
	public static SaibanId fromEntity(TSaibanEntity entity) {
		if (entity == null) {
			return null;
		}
		return new SaibanId(entity.getAnkenId(), entity.getSaibanBranchNo());
	}

	@Override
	public String toString() {
		if (!LoiozObjectUtils.allNotNull(ankenId, branchNumber)) {
			return CommonConstant.BLANK;
		}
		return ankenId + DELIMITER + branchNumber;
	}

	@Override
	public int compareTo(SaibanId other) {
		Comparator<AnkenId> ankenIdComparator = Comparator.nullsLast(AnkenId::compareTo);
		Comparator<Long> branchNumberComparator = Comparator.nullsLast(Long::compareTo);

		Comparator<SaibanId> saibanAnkenIdComparator = Comparator.nullsLast(
				Comparator.comparing(SaibanId::getAnkenId, ankenIdComparator));
		Comparator<SaibanId> saibanBranchNumberComparator = Comparator.nullsLast(
				Comparator.comparing(SaibanId::getBranchNumber, branchNumberComparator));

		return saibanAnkenIdComparator
				.thenComparing(saibanBranchNumberComparator)
				.compare(this, other);
	}
}
