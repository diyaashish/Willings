package jp.loioz.app.common.word.builder.naibu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import jp.loioz.app.common.word.common.AbstractMakeWordBuilder;
import jp.loioz.app.common.word.dto.naibu.Wn0001WordDto;
import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.app.common.word.templateDataEnum.naibu.Wn0001WordEnum;
import jp.loioz.common.constant.ChohyoConstatnt.OutputWordTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.dto.AzukariItemListDto;
import lombok.Setter;

/*
 * ========== | Builder作成者が実装する必要があるクラス・メソッド |========== ■1.Builder（新規作成）
 * Wordファイルの作成、出力処理を行うクラス。下記のメソッドを実装する。 ーmakeWordFile Wordファイルの生成 ー
 * setWordDataDtoが保持するデータをEnumに設定し、Wordへの書き込み処理をコールする
 *
 * ■2.Dto（新規作成） Builderの外の処理からBuilderに書き込みデータを渡すためのDataTransferObject
 * Wordに書き込むデータを保持するプロパティを用意する。
 *
 * ■3.Enum（新規作成） 共通のWord書き込み処理へ引数として渡すクラス
 * Wordのテンプレートファイルに予め記載をしておくKey文字列と、Key文字列と置換を行う書き込み値を保持する
 *
 * ■4.OutputWordTemplate（追記） 利用するテンプレートファイル名などの情報を定義する。
 *
 * =======================================================
 */

/*
 * ========== | 呼び出し側のプログラムでの実装例 | ========== ■1.BuilderとDTOを定義（必須）
 * Wn0001WordBuilder wn0001WordBuilder = new WN0001WordBuilder(); Wn0001WordDto
 * wn0001WordDto = wn0001WordBuilder.createNewTargetBuilderDto();
 *
 * ■2.WordConfigを設定（必須） wn0001WordBuilder.setConfig(wordConfig);
 *
 * ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定する（必須） wn0001WordDto.setValue(value);
 * wn0001WordBuilder.wn0001WordDto(wn0001WordDto);
 *
 * ■4.Word作成(必須) try { wn0001WordBuilder.makeWordFile(response); } catch
 * (Exception e) { // ※サンプルのため、簡易的なエラーハンドリング
 * logger.error("Wordファイルの生成に失敗しました。"); }
 * ============================================
 */

/**
 * 受領書を出力するBuilderクラス
 */
@Setter
public class Wn0001WordBuilder extends AbstractMakeWordBuilder<Wn0001WordDto> {

	/**
	 * Builder用DTO
	 */
	private Wn0001WordDto wn0001WordDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wn0001WordDto createNewTargetBuilderDto() {
		return new Wn0001WordDto();
	}

	/**
	 * 指定のフォーマットのWordファイルを生成する
	 *
	 * @param response
	 * @throws IOException
	 * @throws Exception
	 */
	public void makeWordFile(HttpServletResponse response) throws IOException, Exception {

		super.makeWordFile(OutputWordTemplate.WORD_NAIBU_1, response, CommonConstant.BLANK);
	}

	/**
	 * Wordファイル内にデータを出力する
	 *
	 * @param doc 出力先のWordファイル情報
	 */
	@Override
	protected void setWordData(XWPFDocument doc) {

		// DtoからWordへの書き込みデータを取得
		String ankenId = Optional.of(wn0001WordDto.getAnkenId()).orElse("");
		List<String> lawyers = wn0001WordDto.getLawyerNameList();
		String createdAt = wn0001WordDto.getCreatedAt();
		List<AzukariItemListDto> items = wn0001WordDto.getHinmoku();

		// Enumをリストにまとめる
		List<WordEnum> wn0001WordEnums = new ArrayList<>();
		wn0001WordEnums.add(Wn0001WordEnum.TXT_ANKEN_ID.setValue(ankenId));
		wn0001WordEnums.add(Wn0001WordEnum.TXT_CREATED_AT.setValue(createdAt));

		// 追加弁護士を設定
		List<WordEnum> addEnumList = new ArrayList<WordEnum>();
		addEnumList.add(Wn0001WordEnum.TXT_LAWYER);

		Map<WordEnum, List<String>> map = new HashMap<WordEnum, List<String>>();
		map.put(Wn0001WordEnum.TXT_LAWYER, lawyers);

		// Enumのデータをドキュメントに書き込む
		this.writeWord(doc, wn0001WordEnums, items, addEnumList, map);
	}

	/**
	 * Wordファイルに指定のデータを書き込む（置換する）
	 *
	 * @param doc
	 * @param datas
	 * @param items
	 * @return 書き込み（置換）が完了したドキュメント
	 */
	protected XWPFDocument writeWord(XWPFDocument doc, List<WordEnum> datas, List<AzukariItemListDto> items, List<WordEnum> addEnumList,
			Map<WordEnum, List<String>> map) {

		// 通常の書き込み
		writeWord(doc, datas);

		// 行追加書き込み処理
		writeWordList(doc, addEnumList, map);

		// 書き込みテーブルの準備
		int rowSize = items.size();

		// テーブルの取得
		XWPFTable table = doc.getTableArray(0);
		if (table != null) {

			// 行コピー
			List<XWPFTableRow> tableRowList = tableRowCopy(table, rowSize);

			// 品目の書き込み処理
			writeDocxItem(table, rowSize, tableRowList, items);

		}

		return doc;
	}

}
