package jp.loioz.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 預り元に紐づく情報を管理するDtoクラス
 */
@Data
public class AzukariItemUserDto {

	// 預品連番
	private List<Long> ankenItemSeqList = new ArrayList<>();

	// 預り元種別
	private String azukariFromType;

	// 預り元顧客ID
	private Long azukariFromCustomerId;

	// 預り元関与者SEQ,
	private Long azukariFromKanyoshaSeq;

	// 預り元名
	private String azukariFrom;
	
	// 宛先リスト
	private List<String> atesakiNameList;
	
	// 預かり品目リスト
	private List<AzukariItemListDto> azukariHimokuList;
}