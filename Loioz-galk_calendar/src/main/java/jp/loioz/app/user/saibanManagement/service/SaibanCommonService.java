package jp.loioz.app.user.saibanManagement.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonSaibanService;
import jp.loioz.app.user.saibanManagement.form.SaibanCommonInputForm;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.SaibankanDto;

/**
 * 裁判管理系画面の共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanCommonService extends DefaultService {

	/** 共通裁判用処理クラス */
	@Autowired
	private CommonSaibanService commonSaibanService;
	
	/**
	 * 裁判の裁判官情報を登録する
	 * 
	 * @param saibanSeq
	 * @param saibankanInputFormList
	 * @throws AppException
	 */
	public void insertSaibanSaibankan(Long saibanSeq, List<SaibanCommonInputForm.SaibankanInputForm> saibankanInputFormList) throws AppException {
		
		// フォームをDtoに変換
		List<SaibankanDto> saibankanDtoList = this.convertSaibankanInputFormToDto(saibankanInputFormList);
		// 登録処理
		commonSaibanService.insertSaibankanEntityWithDtoVal(saibankanDtoList, saibanSeq);
	}
	
	/**
	 * 裁判の裁判官情報を更新する
	 * 
	 * @param saibanSeq
	 * @param basicInputForm
	 * @throws AppException 
	 */
	public void updateSaibanSaibankan(Long saibanSeq, List<SaibanCommonInputForm.SaibankanInputForm> saibankanInputFormList) throws AppException {
		
		// フォームをDtoに変換
		List<SaibankanDto> saibankanDtoList = this.convertSaibankanInputFormToDto(saibankanInputFormList);
		// 更新処理
		commonSaibanService.updateSaibankanList(saibanSeq, saibankanDtoList);
	}
	
	/**
	 * 裁判官の入力フォームをDtoに変換
	 * 
	 * @param saibankanInputFormList
	 * @return
	 */
	public List<SaibankanDto> convertSaibankanInputFormToDto(List<SaibanCommonInputForm.SaibankanInputForm> saibankanInputFormList) {
		
		if (CollectionUtils.isEmpty(saibankanInputFormList)) {
			return Collections.emptyList();
		}
		
		List<SaibankanDto> saibankanDtoList = saibankanInputFormList.stream()
				.filter(e -> StringUtils.isNotEmpty(e.getSaibankanName()))
				.map(e -> SaibankanDto.builder()
						.saibankanName(e.getSaibankanName())
						.build())
				.collect(Collectors.toList());
		
		return saibankanDtoList;
	}
	
	/**
	 * 裁判官のDtoを入力フォームに変換
	 * 
	 * @param saibankanDtoList
	 * @return
	 */
	public List<SaibanCommonInputForm.SaibankanInputForm> convertSaibankanDtoToInputForm(List<SaibankanDto> saibankanDtoList) {

		if (CollectionUtils.isEmpty(saibankanDtoList)) {
			return Collections.emptyList();
		}
		
		List<SaibanCommonInputForm.SaibankanInputForm> saibankanInputFormList = saibankanDtoList.stream()
				.map(dto -> {
					SaibanCommonInputForm.SaibankanInputForm intpuform = new SaibanCommonInputForm.SaibankanInputForm();
					intpuform.setSaibankanName(dto.getSaibankanName());
					return intpuform;
				})
				.collect(Collectors.toList());
		
		return saibankanInputFormList;
	}
}
