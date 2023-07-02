package jp.loioz.app.common.word.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.values.XmlValueDisconnectedException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.springframework.http.MediaType;

import jp.loioz.app.common.word.config.WordConfig;
import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.common.constant.ChohyoConstatnt.OutputWordTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.AnkenTantoAccountDto;
import jp.loioz.dto.AzukariItemListDto;
import jp.loioz.dto.SaibanTantoAccountDto;

/**
 * Word出力用抽象クラス
 */
/**
 * @author ichimura
 *
 * @param <T>
 */
public abstract class AbstractMakeWordBuilder<T> {

	/**
	 * 設定クラス
	 */
	private WordConfig config;

	/**
	 * 生成するWordファイル定義
	 */
	private OutputWordTemplate formTemplate;

	/**
	 * 対応するBuilderのDTOを生成する
	 *
	 * @return 対応するBuilderのDTO
	 */
	public abstract T createNewTargetBuilderDto();

	/**
	 * 指定のフォーマットのWordファイルを生成する
	 *
	 * @param formTemplate 使用するテンプレートファイル
	 * @param response
	 * @throws Exception
	 */
	protected void makeWordFile(OutputWordTemplate formTemplate, HttpServletResponse response, String name) throws Exception {

		this.formTemplate = formTemplate;

		// ファイル名取得
		String fileName = this.formTemplate.getCd();
		// ファイルパス
		String dirPath = this.formTemplate.getDirPath();
		// ファイル拡張子
		String extension = this.formTemplate.getFileType();
		// ファイルパス生成
		String inputFilePath = this.config.getWordRoot() + dirPath + fileName + extension;

		try (XWPFDocument doc = new XWPFDocument(new FileInputStream(inputFilePath))) {

			// データを設定する
			this.setWordData(doc);

			// 作成したワークブックをダウンロード可能な状態にする
			this.makeDownloadableState(doc, response, name);
		}

	}

	/**
	 * Wordファイルに出力データを設定する
	 *
	 * @param doc
	 * @throws IOException 
	 */
	protected abstract void setWordData(XWPFDocument doc) throws IOException;

	/**
	 * 作成したWordファイルをダウンロード可能な状態にする
	 *
	 * @param doc 作成したWordドキュメント
	 * @param HttpServletResponse
	 * @throws Exception
	 */
	protected void makeDownloadableState(XWPFDocument doc, HttpServletResponse response, String name) throws Exception {

		// 出力日の取得
		String date = DateUtils.parseToString(LocalDateTime.now(),
				DateUtils.DATE_YYYY + DateUtils.DATE_MM + DateUtils.DATE_DD + DateUtils.TIME_FORMAT_HH + DateUtils.TIME_FORMAT_MM
						+ DateUtils.TIME_FORMAT_SS);
		String encodeFileName;
		if (StringUtils.isBlank(name)) {

			encodeFileName = this.formTemplate.getVal() + CommonConstant.UNDER_BAR + date
					+ this.formTemplate.getFileType();
		} else {
			encodeFileName = this.formTemplate.getVal() + CommonConstant.UNDER_BAR + name + CommonConstant.UNDER_BAR + date
					+ this.formTemplate.getFileType();
		}
		// ファイル名に含まれる非推奨文字を消去
		encodeFileName = encodeFileName.replaceAll(CommonConstant.SPACE, CommonConstant.BLANK);
		encodeFileName = encodeFileName.replaceAll(CommonConstant.FULL_SPACE, CommonConstant.BLANK);
		// ファイルダウンロードの準備
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader("Content-Disposition", "attachment; filename*=" + URLEncoder.encode(encodeFileName, StandardCharsets.UTF_8.name()));

		doc.write(response.getOutputStream());
	}

	/**
	 * wordに記載するチェックボックスの文字列を取得する
	 *
	 * @param isCheck
	 * @return isCheckがtrue:☑、isCheckがfalse:□
	 */
	protected String getCheckBox(boolean isCheck) {
		return isCheck ? "☑" : "□";
	}

	/**
	 * Wordファイルに複数部のデータを書き込む
	 *
	 * @param doc
	 * @param copyDtoList 複数部分の書き込みデータ
	 * @throws IOException 
	 */
	protected void writeWordMultiCopy(XWPFDocument doc, List<Object> copyDtoList) throws IOException {

		// テンプレートの構成要素のcloneを作成
		List<IBodyElement> cloneBodyElements = this.cloneBodyElements(doc.getBodyElements());

		int azukariShoCount = copyDtoList.size();

		for (int i = 0; i < azukariShoCount; i++) {

			// 書き込み
			this.writeWordSingleCopy(doc, copyDtoList.get(i), i);

			if (i != azukariShoCount - 1) {
				// また次の書き込みが行われる場合

				// 改ページを挿入
				this.addBreakAtTheEnd(doc);
				// テンプレートcloneを追加
				this.addBodyElements(doc, cloneBodyElements);
			}
		}
	}

	/**
	 * Wordファイルに複数部のデータを書き込む
	 *
	 * <pre>
	 * ※実装は実装クラス側で行う
	 * </pre>
	 *
	 * @param doc
	 * @param copyDto 1部分の書き込みデータ
	 * @param cnt
	 */
	protected void writeWordSingleCopy(XWPFDocument doc, Object copyDto, int cnt) {
		throw new RuntimeException("You must implement");
	};

	/**
	 * Wordファイルに指定のデータを書き込む（置換する）
	 *
	 * @param doc
	 * @param datas
	 * @return 書き込み（置換）が完了したドキュメント
	 */
	protected XWPFDocument writeWord(XWPFDocument doc, List<WordEnum> datas) {

		// Wordのパラグラフ内の文言の変換
		for (XWPFParagraph p : doc.getParagraphs()) {

			List<XWPFRun> runs = p.getRuns();
			if (runs != null) {

				for (XWPFRun r : runs) {
					String text = r.getText(0);
					if (text == null) {
						continue;
					}

					// キー文字列に一致する文字がないかを捜索し、一致する場合は文字列を置換
					for (WordEnum data : datas) {
						if (text.contains(data.getKey())) {
							text = text.replace(data.getKey(), data.getValue());
							r.setText(text, 0);
						}
					}
				}

			}
		}

		// Wordのテーブルオブジェクト内の文言の変換
		for (XWPFTable tbl : doc.getTables()) {
			for (XWPFTableRow row : tbl.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					for (XWPFParagraph p : cell.getParagraphs()) {

						for (XWPFRun r : p.getRuns()) {

							String text = r.getText(0);

							if (text == null) {
								continue;
							}

							// キー文字列に一致する文字がないかを捜索し、一致する場合は文字列を置換
							for (WordEnum data : datas) {
								if (text.contains(data.getKey())) {
									text = text.replace(data.getKey(), data.getValue());
									r.setText(text, 0);
								}
							}
						}
					}
				}
			}
		}

		return doc;
	}

	/**
	 * 指定したkeyを行ごと削除する
	 *
	 * @param doc
	 * @param datas 削除するKEY
	 */
	protected void writeWordList(XWPFDocument doc, List<WordEnum> datas, Map<WordEnum, List<String>> map) {

		List<XWPFParagraph> paragraphs = doc.getParagraphs();

		int writeCnt = 0;
		int maxCnt = map.size();

		for (int i = 0; i < paragraphs.size(); i++) {

			List<XWPFRun> runs = paragraphs.get(i).getRuns();

			if (runs != null) {

				for (XWPFRun r : runs) {
					String text = r.getText(0);
					if (text == null) {
						continue;
					}

					// キー文字列に一致する文字がないかを捜索し、一致する場合は文字列を置換
					for (WordEnum data : datas) {
						String key = data.getKey();
						if (text.contains(key)) {

							// 書き込むリスト
							List<String> writeDataList = map.get(data);
							if (LoiozCollectionUtils.isNotEmpty(writeDataList)) {
								for (String writeData : writeDataList) {
									// テンプレートの書式を取得
									ParagraphAlignment alignment = paragraphs.get(i).getAlignment();

									XmlCursor cursor = paragraphs.get(i).getCTP().newCursor();
									XWPFParagraph createPg = doc.insertNewParagraph(cursor);
									createPg.setAlignment(alignment);

									XWPFRun run = createPg.createRun();
									text = text.replace(data.getKey(), writeData);
									run.setFontFamily(OutputConstant.FONT_MINCHO);
									run.setFontSize(OutputConstant.FONT_DEFAULT_SIZE);
									run.setText(text, 0);
									text = data.getKey();
								}
								// テンプレート行を削除
								i += (writeDataList.size());
								doc.removeBodyElement(doc.getPosOfParagraph(paragraphs.get(i)));

							} else {
								// データが存在しない場合は行ごと削除
								doc.removeBodyElement(doc.getPosOfParagraph(paragraphs.get(i)));
							}

							// 書き込みカウント
							writeCnt++;

						}
					}
				}
			}
			// 書き込み終了か判定
			if (writeCnt == maxCnt) {
				break;
			}

		}

	};

	/**
	 * テーブルの行を行数分コピーする
	 *
	 * @param table
	 * @param tableSize
	 * @return
	 */
	protected List<XWPFTableRow> tableRowCopy(XWPFTable table, int tableSize) {

		XWPFTableRow tableRow = table.getRow(0);
		List<XWPFTableRow> tableRowList = new ArrayList<XWPFTableRow>();

		try {
			// テーブル行コピー
			for (int i = 0; i < tableSize; i++) {
				CTRow ctrow = CTRow.Factory.parse(tableRow.getCtRow().newInputStream());
				tableRowList.add(new XWPFTableRow(ctrow, table));
			}

		} catch (Exception e) {
			// 何もしない
		}

		return tableRowList;
	};

	/**
	 * 指定したkeyを行ごと削除する
	 *
	 * @param doc
	 * @param datas 削除するKEY
	 */
	protected void deleteRows(XWPFDocument doc, List<WordEnum> datas) {

		List<XWPFParagraph> deleteParagraphList = new ArrayList<>();
		for (XWPFParagraph p : doc.getParagraphs()) {

			List<XWPFRun> runs = p.getRuns();
			if (runs != null) {

				for (XWPFRun r : runs) {
					String text = r.getText(0);
					if (text == null) {
						continue;
					}

					// キー文字列に一致する文字がないかを捜索
					for (WordEnum data : datas) {
						if (text.contains(data.getKey())) {

							// 一致して対象のkeyが空文字か判定する
							if (StringUtils.isEmpty(data.getValue())) {
								// 空文字の場合、行ごと消す対象とする
								deleteParagraphList.add(p);
							}

						}
					}
				}

			}
		}
		// 対象の行を削除
		for (XWPFParagraph deleteParagraph : deleteParagraphList) {
			doc.removeBodyElement(doc.getPosOfParagraph(deleteParagraph));
		}

	};

	/**
	 * 担当者の書き込み処理（弁護士、担当事務）
	 *
	 * @param table
	 * @param rowSize
	 * @param tableRowList
	 * @param ankenTantoAccountDtoList
	 */
	public void writeDocxTanto(XWPFTable table, int rowSize, List<XWPFTableRow> tableRowList,
			List<AnkenTantoAccountDto> ankenTantoAccountDtoList) {

		for (int i = 0; i < rowSize; i++) {
			// 行取得
			XWPFTableRow editRow = tableRowList.get(i);
			AnkenTantoAccountDto dto = ankenTantoAccountDtoList.get(i);

			// 事務は名前の列を均等割を解除
			boolean jimuFlg = false;
			if (CommonConstant.TantoType.JIMU.equalsByCode(dto.getTantoType())) {
				jimuFlg = true;
			}

			int idx = 0;
			int nameCellIdx = 1;
			// 書き込み処理
			List<String> dispValList = dto.getTantoDispList();
			for (XWPFTableCell cell : editRow.getTableCells()) {
				if (idx == nameCellIdx && jimuFlg) {
					// 事務の名前セルを均等割解除
					XWPFParagraph paragraph = cell.getParagraphs().get(0);
					paragraph.setAlignment(ParagraphAlignment.LEFT);
				}
				cell.setText(dispValList.get(idx++));
			}
			table.addRow(editRow);
		}

		// テンプレートの行を削除
		table.removeRow(0);

	}

	/**
	 * 裁判担当者の書き込み処理（弁護士、担当事務）
	 *
	 * @param table
	 * @param rowSize
	 * @param tableRowList
	 * @param saibanTantoAccountDtoList
	 */
	public void writeDocxTantoForSaiban(XWPFTable table, int rowSize, List<XWPFTableRow> tableRowList,
			List<SaibanTantoAccountDto> saibanTantoAccountDtoList) {

		for (int i = 0; i < rowSize; i++) {
			// 行取得
			XWPFTableRow editRow = tableRowList.get(i);
			SaibanTantoAccountDto dto = saibanTantoAccountDtoList.get(i);

			// 事務は名前の列を均等割を解除
			boolean jimuFlg = false;
			if (CommonConstant.TantoType.JIMU.equalsByCode(dto.getTantoType())) {
				jimuFlg = true;
			}

			int idx = 0;
			int nameCellIdx = 1;
			// 書き込み処理
			List<String> dispValList = dto.getTantoDispList();
			for (XWPFTableCell cell : editRow.getTableCells()) {
				if (idx == nameCellIdx && jimuFlg) {
					// 事務の名前セルを均等割解除
					XWPFParagraph paragraph = cell.getParagraphs().get(0);
					paragraph.setAlignment(ParagraphAlignment.LEFT);
				}
				cell.setText(dispValList.get(idx++));
			}
			table.addRow(editRow);
		}

		// テンプレートの行を削除
		table.removeRow(0);

	}

	/**
	 * 品目の書き込み処理
	 *
	 * @param table
	 * @param rowSize
	 * @param tableRowList
	 * @param items
	 */
	public void writeDocxItem(XWPFTable table, int rowSize, List<XWPFTableRow> tableRowList,
			List<AzukariItemListDto> items) {

		for (int i = 0; i < rowSize; i++) {
			// 行取得
			XWPFTableRow editRow = tableRowList.get(i);
			AzukariItemListDto dto = items.get(i);
			int hinmokuCellNum = 0;
			int countCellNum = 1;
			// 書き込み処理
			XWPFTableCell cell1 = editRow.getCell(hinmokuCellNum);
			cell1.setText(dto.getHinmoku());
			XWPFTableCell cell2 = editRow.getCell(countCellNum);
			cell2.setText(dto.getAzukariCount());
			table.addRow(editRow);
		}

		// テンプレートの行を削除
		table.removeRow(0);

	}

	/**
	 * テンプレート内部のテキストを取得
	 *
	 * @param List<XWPFRun> runs(テンプレート本文の内容)
	 */
	protected StringBuilder addTemplateInternalText(List<XWPFRun> runs) {
		StringBuilder sb = new StringBuilder();

		for (XWPFRun r : runs) {
			int pos = r.getTextPosition();
			if (r.getText(pos) != null) {
				sb.append(r.getText(pos));
			}
		}
		return sb;
	}

	/**
	 * ドキュメントの最後に改ページを挿入する
	 *
	 * @param doc
	 */
	protected void addBreakAtTheEnd(XWPFDocument doc) {
		XWPFParagraph paragraph = doc.createParagraph();
		XWPFRun run = paragraph.createRun();
		run.addBreak(BreakType.PAGE);
	}

	/**
	 * ドキュメントの一番後ろに引数のエレメントを追加する
	 *
	 * @param doc
	 * @param bodyElements
	 */
	protected void addBodyElements(XWPFDocument doc, List<IBodyElement> bodyElements) {
		for (IBodyElement bodyElement : bodyElements) {
			BodyElementType elementType = bodyElement.getElementType();

			if (elementType == BodyElementType.PARAGRAPH) {
				XWPFParagraph source = (XWPFParagraph) bodyElement;

				XWPFParagraph clone = doc.createParagraph();
				this.cloneParagraph(clone, source);

			} else if (elementType == BodyElementType.TABLE) {
				XWPFTable source = (XWPFTable) bodyElement;

				XWPFTable clone = doc.createTable();
				this.cloneTable(clone, source);
			}
		}
	}

	/**
	 * ドキュメントの一番後ろに引数のパラグラフを追加する
	 *
	 * @param doc
	 * @param paragraphs
	 */
	protected void addParagraphs(XWPFDocument doc, List<XWPFParagraph> paragraphs) {
		for (XWPFParagraph paragraph : paragraphs) {
			XWPFParagraph clone = doc.createParagraph();
			this.cloneParagraph(clone, paragraph);
		}
	}

	/**
	 * IBodyElementオブジェクトリストのcloneを作成する
	 *
	 * @param bodyElements
	 * @return
	 * @throws IOException 
	 */
	protected List<IBodyElement> cloneBodyElements(List<IBodyElement> bodyElements) throws IOException {

		List<IBodyElement> cloneBodyElements = new ArrayList<>();

		for (IBodyElement bodyElement : bodyElements) {
			BodyElementType elementType = bodyElement.getElementType();

			if (elementType == BodyElementType.PARAGRAPH) {
				XWPFParagraph source = (XWPFParagraph) bodyElement;

				try (XWPFDocument outputDocument = new XWPFDocument();) {
					XWPFParagraph clone = outputDocument.createParagraph();
					
					this.cloneParagraph(clone, source);
					cloneBodyElements.add(clone);
				}

			} else if (elementType == BodyElementType.TABLE) {
				XWPFTable source = (XWPFTable) bodyElement;

				try (XWPFDocument outputDocument = new XWPFDocument();) {
					XWPFTable clone = outputDocument.createTable();
					// テーブルの情報を設定
					this.cloneTable(clone, source);
					cloneBodyElements.add(clone);
				}
			}
		}

		return cloneBodyElements;
	}

	/**
	 * XWPFParagraphオブジェクトリストのcloneを作成する
	 *
	 * @param paragraphs
	 * @return clone
	 * @throws IOException 
	 */
	protected List<XWPFParagraph> cloneParagraphs(List<XWPFParagraph> paragraphs) throws IOException {

		List<XWPFParagraph> cloneParagraphs = new ArrayList<>();

		for (XWPFParagraph source : paragraphs) {
			
			try (XWPFDocument outputDocument = new XWPFDocument();) {
				XWPFParagraph clone = outputDocument.createParagraph();

				this.cloneParagraph(clone, source);
				cloneParagraphs.add(clone);
			}
		}

		return cloneParagraphs;
	}

	/**
	 * XWPFParagraphオブジェクトのcloneを作成する
	 *
	 * @param clone
	 * @param source
	 */
	protected void cloneParagraph(XWPFParagraph clone, XWPFParagraph source) {
		CTPPr pPr = clone.getCTP().isSetPPr() ? clone.getCTP().getPPr() : clone.getCTP().addNewPPr();
		pPr.set(source.getCTP().getPPr());
		for (XWPFRun r : source.getRuns()) {
			XWPFRun nr = clone.createRun();
			this.cloneRun(nr, r);
		}
	}

	/**
	 * XWPFRunオブジェクトのcloneを作成する
	 *
	 * @param clone
	 * @param source
	 */
	protected void cloneRun(XWPFRun clone, XWPFRun source) {
		CTRPr rPr = clone.getCTR().isSetRPr() ? clone.getCTR().getRPr() : clone.getCTR().addNewRPr();
		rPr.set(source.getCTR().getRPr());
		clone.setText(source.getText(0));
	}

	/**
	 * XWPFTableオブジェクトのcloneを作成する
	 *
	 * @param cloneTbl
	 * @param sourceTbl
	 */
	protected void cloneTable(XWPFTable cloneTbl, XWPFTable sourceTbl) {
		// tableの設定
		cloneTbl.getCTTbl().setTblPr(sourceTbl.getCTTbl().getTblPr());
		cloneTbl.getCTTbl().setTblGrid(sourceTbl.getCTTbl().getTblGrid());
		// rowの設定
		for (XWPFTableRow sourceRow : sourceTbl.getRows()) {
			XWPFTableRow cloneRow = cloneTbl.createRow();
			cloneRow.getCtRow().setTrPr(sourceRow.getCtRow().getTrPr());
			// cellの設定
			for (int c = 0; c < sourceRow.getTableCells().size(); c++) {
				// 新規の行には1つのセルをcreateする
				XWPFTableCell cloneCell = c == 0 ? cloneRow.getTableCells().get(0) : cloneRow.createCell();
				XWPFTableCell sourceCell = sourceRow.getTableCells().get(c);
				cloneCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
				XmlCursor cursor = cloneCell.getParagraphArray(0).getCTP().newCursor();
				// cell内の要素の設定
				for (IBodyElement sourceElem : sourceCell.getBodyElements()) {
					BodyElementType elemType = sourceElem.getElementType();
					if (elemType == BodyElementType.PARAGRAPH) {
						XWPFParagraph sourcePar = (XWPFParagraph) sourceElem;

						try {
							// 既に削除され、存在するべきではないbodyElementが渡ってきた場合はclone処理を行わないための処理
							// 【補足】
							// cell内のデフォルトのparagraphは、本ループの処理の後に削除されるが、
							// デフォルトのparagraphインスタンスに対応しているbodyElementインスタンスは削除されずに残っているため、
							// 本来1対1で紐づく2つのインスタンスにずれが生じている場合ここでエラーが発生する。
							sourcePar.getCTP().toString();
						} catch (XmlValueDisconnectedException e) {
							continue;
						}

						XWPFParagraph clonePar = cloneCell.insertNewParagraph(cursor);
						this.cloneParagraph(clonePar, sourcePar);
						cursor.toNextToken();
					} else if (elemType == BodyElementType.TABLE) {
						;
						XWPFTable sourceTable = (XWPFTable) sourceElem;
						XWPFTable cloneTable = cloneCell.insertNewTbl(cursor);
						this.cloneTable(cloneTable, sourceTable);
						cursor.toNextToken();
					}
				}
				// paragraph配列の最後の要素は、cellがcreateされたときに自動で設定されているデフォルトのparagraphであり、不要なものなので削除
				int paragraphsSize = cloneCell.getParagraphs().size();
				cloneCell.removeParagraph(paragraphsSize - 1);
			}
		}
		// tableのrow配列の先頭は、tableがcreateされたときに自動で設定されているデフォルトのrowであり、不要なものなので削除
		cloneTbl.removeRow(0);
	}

	// -----------------------------------------------------------------------------
	// getter/setter
	// -----------------------------------------------------------------------------
	public void setConfig(WordConfig config) {
		this.config = config;
	}
}
