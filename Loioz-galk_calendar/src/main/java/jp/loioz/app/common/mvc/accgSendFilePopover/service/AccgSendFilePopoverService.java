package jp.loioz.app.common.mvc.accgSendFilePopover.service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.mvc.accgSendFilePopover.dto.SendFileItemDto;
import jp.loioz.app.common.mvc.accgSendFilePopover.form.AccgSendFilePopoverViewForm;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.bean.AccgDocFileBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.common.constant.CommonConstant.AccgDocSendType;
import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAccgDocActSendDao;
import jp.loioz.dao.TAccgDocActSendFileDao;
import jp.loioz.dao.TAccgDocFileDao;
import jp.loioz.entity.TAccgDocActSendEntity;
import jp.loioz.entity.TAccgDocActSendFileEntity;
import jp.loioz.entity.TAccgDocFileEntity;

/**
 * 会計書類ポップオーバーサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccgSendFilePopoverService extends DefaultService {

	/** 会計書類ファイルDaoクラス */
	@Autowired
	private TAccgDocFileDao tAccgDocFileDao;

	/** 会計書類送付Daoクラス */
	@Autowired
	private TAccgDocActSendDao tAccgDocActSendDao;

	/** 会計書類-対応-送付-ファイルDaoクラス */
	@Autowired
	private TAccgDocActSendFileDao tAccgDocActSendFileDao;

	/** ファイルストレージサービス */
	@Autowired
	private FileStorageService fileStorageService;

	/** 会計管理のサービスクラス */
	@Autowired
	private CommonAccgService commonAccgService;

	/**
	 * 会計書類ポップオーバー画面表示オブジェクトを作成する
	 * 
	 * @param accgDocActSendSeq
	 * @return
	 * @throws AppException
	 */
	public AccgSendFilePopoverViewForm createAccgSendFilePopoverViewForm(Long accgDocActSendSeq) throws AppException {

		TAccgDocActSendEntity tAccgDocActSendEntity = tAccgDocActSendDao.selectAccgDocActSendByActSendSeq(accgDocActSendSeq);
		List<TAccgDocActSendFileEntity> tAccgDocActSendFileEntities = tAccgDocActSendFileDao.selectAccgDocActSendFileByAccgDocActSendSeq(accgDocActSendSeq);
		if (tAccgDocActSendEntity == null || CollectionUtils.isEmpty(tAccgDocActSendFileEntities)) {
			// SEQをキーとした情報が見つからない場合
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// ファイル情報を取得
		List<Long> accgDocFileSeqList = tAccgDocActSendFileEntities.stream().map(TAccgDocActSendFileEntity::getAccgDocFileSeq).collect(Collectors.toList());
		List<TAccgDocFileEntity> tAccgDocFileEntities = tAccgDocFileDao.selectAccgDocFileByAccgDocFileSeq(accgDocFileSeqList);

		// ファイル情報の取得Mapを作成
		Map<Long, TAccgDocFileEntity> accgDocFileSeqToEntityMapper = tAccgDocFileEntities.stream()
				.collect(Collectors.toMap(TAccgDocFileEntity::getAccgDocFileSeq, Function.identity()));

		// 送付ファイル情報を作成
		List<SendFileItemDto> sendFileItemDtoList = tAccgDocActSendFileEntities.stream()
				.map(e -> {
					var tAccgDocFileEntity = accgDocFileSeqToEntityMapper.get(e.getAccgDocFileSeq());
					var accgDocFileType = AccgDocFileType.of(tAccgDocFileEntity.getAccgDocFileType());
					var accgDocSeq = tAccgDocFileEntity.getAccgDocSeq();
					return SendFileItemDto.builder()
							.accgDocFileSeq(e.getAccgDocFileSeq())
							.accgDocFileType(accgDocFileType)
									.fileName(this.createAccgDocFileName(accgDocSeq, accgDocFileType,
											FileExtension.ofExtension(tAccgDocFileEntity.getFileExtension())))
							.lastDownloadDateTime(DateUtils.parseToString(e.getDownloadLastAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM))
							.build();
				})
				.sorted(Comparator.comparing(SendFileItemDto::getFileDispOrder))
				.collect(Collectors.toList());

		var viewForm = new AccgSendFilePopoverViewForm();

		viewForm.setAccgDocActSendSeq(accgDocActSendSeq);
		viewForm.setAccgDocSendType(AccgDocSendType.of(tAccgDocActSendEntity.getSendType()));
		viewForm.setSendSubject(tAccgDocActSendEntity.getSendSubject());
		viewForm.setSendTo(tAccgDocActSendEntity.getSendTo());
		viewForm.setSendCc(StringUtils.toArray(tAccgDocActSendEntity.getSendCc()));
		viewForm.setSendBcc(StringUtils.toArray(tAccgDocActSendEntity.getSendBcc()));
		viewForm.setFileItemList(sendFileItemDtoList);

		return viewForm;
	}

	/**
	 * 送付済みファイルのダウンロード処理
	 * 
	 * @param accgDocFileSeq
	 * @param response
	 * @throws AppException
	 */
	public void downloadSendFile(Long accgDocFileSeq, HttpServletResponse response) throws AppException {

		// ファイル情報の取得
		List<AccgDocFileBean> accgDocFileBeans = tAccgDocFileDao.selectAccgDocFileBeanByAccgDocFileSeq(accgDocFileSeq);

		if (CollectionUtils.isEmpty(accgDocFileBeans)) {
			// ファイルが存在しない場合 -> 排他エラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (accgDocFileBeans.stream().anyMatch(e -> !FileExtension.PDF.equalsByValue(e.getFileExtension()))) {
			// 当ポップオーバーはPDF以外を扱わない
			throw new RuntimeException("ダウンロード対象がPDFではありません");
		}

		if (accgDocFileBeans.size() > 1) {
			// PDFが複数ファイルに分割されることはない
			throw new RuntimeException("想定外のデータパターンです");
		}

		// ダウンロードするPDF情報の取得
		AccgDocFileBean accgDocFileBean = accgDocFileBeans.stream().findFirst().orElseThrow();

		// ファイル名を作成
		String fileName = this.createAccgDocFileName(accgDocFileBean.getAccgDocSeq(),
				AccgDocFileType.of(accgDocFileBean.getAccgDocFileType()),
				FileExtension.ofExtension(accgDocFileBean.getFileExtension()));

		try (InputStream is = fileStorageService.fileDownload(accgDocFileBean.getS3ObjectKey()).getObjectContent()) {
			// レスポンス情報の書き込み
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()) + "\"");
			response.setHeader(CommonConstant.HEADER_NAME_OF_AJAX_PROC_RESULT, CommonConstant.HEADER_VALUE_OF_AJAX_PROC_RESULT_SUCCESS);

			// ファイルオブジェクトの書き込み
			IOUtils.copy(is, response.getOutputStream());
		} catch (Exception ex) {
			// 想定外のエラー
			throw new RuntimeException(ex);
		}

	}

	/**
	 * 会計書類ファイル名を作成します
	 * 
	 * @param accgDocSeq
	 * @param accgDocFileType
	 * @param FileExtension
	 * @return
	 */
	private String createAccgDocFileName(Long accgDocSeq, AccgDocFileType accgDocFileType, FileExtension fileExtension) {
		return commonAccgService.createAccgDocFileName(accgDocSeq, accgDocFileType, fileExtension);
	}
}
