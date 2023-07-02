package jp.loioz.common.service.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.pdf.config.PdfConfig;
import jp.loioz.common.constant.ChohyoConstatnt.OutputPdfFonts;
import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.constant.PdfConstant;
import jp.loioz.dto.FileContentsDto;

/**
 * PDF関連の処理を司るサービスクラス
 */
@Service
public class PdfService extends DefaultService {

	/** PDFConfig */
	@Autowired
	private PdfConfig config;

	/**
	 * PDF -> PNG処理
	 *
	 * @param pdfByteData
	 * @return PNGのByteオブジェクト / PDFページ数
	 * @throws IOException
	 */
	public List<FileContentsDto> convertPdf2PngList(FileContentsDto pdfByteData) throws IOException {

		try (PDDocument document = PDDocument.load(pdfByteData.getByteArray());) {
			PDFRenderer renderer = new PDFRenderer(document);

			List<FileContentsDto> imageByteArrayDtoList = new ArrayList<>();
			for (int i = 0; i < document.getPages().getCount(); i++) {
				BufferedImage image = renderer.renderImageWithDPI(i, 300, ImageType.RGB);

				try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
					ImageIO.write(image, "PNG", baos);
					byte[] imageByte = baos.toByteArray();

					imageByteArrayDtoList.add(new FileContentsDto(imageByte, FileExtension.PNG.getVal()));
				}
			}

			return imageByteArrayDtoList;
		}
	}

	/**
	 * 複数のPDFをマージする
	 * 
	 * @param ベースとなるPDF
	 * @param ベースに追加するPDF
	 * @return
	 * @throws IOException
	 */
	public FileContentsDto mergePdf(FileContentsDto basePdf, FileContentsDto... subPdf) throws IOException {

		// PDBPageのマージマネージャークラス
		PDFMergerUtility merger = new PDFMergerUtility();

		// 親PDFをロード
		try (ByteArrayInputStream baioP = new ByteArrayInputStream(basePdf.getByteArray());) {
			// マージマネージャに追加
			merger.addSource(baioP);
		}

		// 子PDFをロード
		for (FileContentsDto subPdfContents : subPdf) {
			try (ByteArrayInputStream baioS = new ByteArrayInputStream(subPdfContents.getByteArray());) {
				// マージマネージャに追加
				merger.addSource(baioS);
			}
		}

		// マージ処理
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			merger.setDestinationStream(baos);
			merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

			return new FileContentsDto(baos.toByteArray(), "pdf");
		}
	}

	/**
	 * PDFにページ番号を設定
	 *
	 * @param pdfByteData
	 * @param pageNoFontSize
	 * @throws IOException
	 */
	public void writePdfPeger(FileContentsDto pdfByteData) throws IOException {

		try (PDDocument doc = PDDocument.load(pdfByteData.getByteArray());
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			// 1ページの場合はページ番号を非表示
			if (doc.getPages().getCount() <= 1) {
				return;
			}

			OutputPdfFonts bizUdpM = OutputPdfFonts.BIZ_UDP_MINCHO;
			PDFont font = PDType0Font.load(doc, new File(config.getPdfRoot() + bizUdpM.getDir() + bizUdpM.getFileName() + bizUdpM.getFileType()));

			// ページ番号
			int count = 1;
			// 既存PDFの各ページに対して処理を行う
			for (PDPage page : doc.getPages()) {
				try (PDPageContentStream content = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true)) {
					String pageNoText = String.format("%d　/　%d", count, doc.getNumberOfPages());

					float pageCenterPointX = PdfConstant.A4_DEFAULT_PDF_WIDTH / 2;
					float textWidth = getTextWidth(font, (float) PdfConstant.PAGE_NO_FONT_SIZE, pageNoText);

					content.beginText();
					content.setFont(font, PdfConstant.PAGE_NO_FONT_SIZE);
					content.newLineAtOffset(pageCenterPointX - (textWidth / 2), PdfConstant.A4_DEFAULT_PDF_BOTTOM_PADDING); // 座標指定は左下から算出する
					content.showText(pageNoText);
					count++;
					content.endText();
				}
			}

			// 書き込み処理
			doc.save(baos);
			pdfByteData.setByteArray(baos.toByteArray());
		}

	}

	/**
	 * HttpResponseにPdf情報を設定し、画面表示時情報を設定する
	 * 
	 * @param is
	 * @param response
	 * @param fileName
	 * @throws IOException
	 */
	public void setPreviewPdf(InputStream is, HttpServletResponse response, String fileName) throws IOException {

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));

		IOUtils.copy(is, response.getOutputStream());
	}

	/**
	 * PDFBoxの出力用文字列幅を取得する
	 * 
	 * @param font
	 * @param fontPx
	 * @param text
	 * @return
	 * @throws IOException
	 */
	private float getTextWidth(PDFont font, float fontPx, String text) throws IOException {

		float width = font.getStringWidth(text);
		return (width * fontPx / 1000f);
	}

}
