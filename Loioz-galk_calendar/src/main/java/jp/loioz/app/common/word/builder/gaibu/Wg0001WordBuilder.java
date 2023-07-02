package jp.loioz.app.common.word.builder.gaibu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import jp.loioz.app.common.word.common.AbstractMakeWordBuilder;
import jp.loioz.app.common.word.dto.gaibu.Wg0001WordDto;
import jp.loioz.app.common.word.templateDataEnum.WordEnum;
import jp.loioz.app.common.word.templateDataEnum.gaibu.Wg0001WordEnum;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.ChohyoConstatnt.OutputWordTemplate;
import jp.loioz.common.utility.DateUtils;
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
 * Wg0001WordBuilder wg0001WordBuilder = new Wg0001WordBuilder(); wg0001WordDto
 * wg0001WordDto = wg0001WordBuilder.createNewTargetBuilderDto();
 *
 * ■2.WordConfigを設定（必須） wg0001WordBuilder.setConfig(wordConfig);
 *
 * ■3.Wordに出力するデータをDTOに設定 & BuilderにDTOを設定する（必須） wg0001WordDto.setValue(value);
 * wg0001WordBuilder.wg0001WordDto(wg0001WordDto);
 *
 * ■4.Excel作成(必須) try { rsoApplicationFormWordBuilder.makeWordFile(response); }
 * catch (Exception e) { // ※サンプルのため、簡易的なエラーハンドリング
 * logger.error("Wordファイルの生成に失敗しました。"); }
 * ============================================
 */

/**
 * 〇〇書を出力するBuilderクラス
 */
@Setter
public class Wg0001WordBuilder extends AbstractMakeWordBuilder<Wg0001WordDto> {

	private static final String DB_DATE_FORMAT = DateUtils.DATE_FORMAT_NON_DELIMITED;
	private static final String OATH_FORM_DATE_FORMAT = DateUtils.DATE_JP_YYYY_MM_DD_ZERO_FILL;

	/**
	 * Builder用DTO
	 */
	private Wg0001WordDto wg0001WordDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Wg0001WordDto createNewTargetBuilderDto() {
		return new Wg0001WordDto();
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
		String memberNameKana1 = wg0001WordDto.getBoardMemberNameKana1();
		String memberName1 = wg0001WordDto.getBoardMemberName1();
		String memberNameKana2 = wg0001WordDto.getBoardMemberNameKana2();
		String memberName2 = wg0001WordDto.getBoardMemberName2();
		String memberNameKana3 = wg0001WordDto.getBoardMemberNameKana3();
		String memberName3 = wg0001WordDto.getBoardMemberName3();
		String memberNameKana4 = wg0001WordDto.getBoardMemberNameKana4();
		String memberName4 = wg0001WordDto.getBoardMemberName4();
		String memberNameKana5 = wg0001WordDto.getBoardMemberNameKana5();
		String memberName5 = wg0001WordDto.getBoardMemberName5();
		String memberNameKana6 = wg0001WordDto.getBoardMemberNameKana6();
		String memberName6 = wg0001WordDto.getBoardMemberName6();
		String memberNameKana7 = wg0001WordDto.getBoardMemberNameKana7();
		String memberName7 = wg0001WordDto.getBoardMemberName7();
		String memberNameKana8 = wg0001WordDto.getBoardMemberNameKana8();
		String memberName8 = wg0001WordDto.getBoardMemberName8();
		String createDate = wg0001WordDto.getCreateDate();
		String rsoName = wg0001WordDto.getRsoName();

		// 日付文字列のフォーマットを変
		createDate = DateUtils.convertStringDate(createDate, DB_DATE_FORMAT, OATH_FORM_DATE_FORMAT);

		// Dtoの値をWord書き込み用のEnumに変換
		WordEnum memberNameKanaEnum1 = Wg0001WordEnum.BOARDMEMBER_NAME_KANA_1.setValue(memberNameKana1);
		WordEnum memberNameEnum1 = Wg0001WordEnum.BOARDMEMBER_NAME_1.setValue(memberName1);
		WordEnum memberNameKanaEnum2 = Wg0001WordEnum.BOARDMEMBER_NAME_KANA_2.setValue(memberNameKana2);
		WordEnum memberNameEnum2 = Wg0001WordEnum.BOARDMEMBER_NAME_2.setValue(memberName2);
		WordEnum memberNameKanaEnum3 = Wg0001WordEnum.BOARDMEMBER_NAME_KANA_3.setValue(memberNameKana3);
		WordEnum memberNameEnum3 = Wg0001WordEnum.BOARDMEMBER_NAME_3.setValue(memberName3);
		WordEnum memberNameKanaEnum4 = Wg0001WordEnum.BOARDMEMBER_NAME_KANA_4.setValue(memberNameKana4);
		WordEnum memberNameEnum4 = Wg0001WordEnum.BOARDMEMBER_NAME_4.setValue(memberName4);
		WordEnum memberNameKanaEnum5 = Wg0001WordEnum.BOARDMEMBER_NAME_KANA_5.setValue(memberNameKana5);
		WordEnum memberNameEnum5 = Wg0001WordEnum.BOARDMEMBER_NAME_5.setValue(memberName5);
		WordEnum memberNameKanaEnum6 = Wg0001WordEnum.BOARDMEMBER_NAME_KANA_6.setValue(memberNameKana6);
		WordEnum memberNameEnum6 = Wg0001WordEnum.BOARDMEMBER_NAME_6.setValue(memberName6);
		WordEnum memberNameKanaEnum7 = Wg0001WordEnum.BOARDMEMBER_NAME_KANA_7.setValue(memberNameKana7);
		WordEnum memberNameEnum7 = Wg0001WordEnum.BOARDMEMBER_NAME_7.setValue(memberName7);
		WordEnum memberNameKanaEnum8 = Wg0001WordEnum.BOARDMEMBER_NAME_KANA_8.setValue(memberNameKana8);
		WordEnum memberNameEnum8 = Wg0001WordEnum.BOARDMEMBER_NAME_8.setValue(memberName8);
		WordEnum createDateEnum = Wg0001WordEnum.CREATE_DATE.setValue(createDate);
		WordEnum rsoNameEnum = Wg0001WordEnum.RSO_NAME.setValue(rsoName);

		// Enumをリストにまとめる
		List<WordEnum> Wg0001WordEnums = new ArrayList<>();
		Wg0001WordEnums.add(memberNameKanaEnum1);
		Wg0001WordEnums.add(memberNameEnum1);
		Wg0001WordEnums.add(memberNameKanaEnum2);
		Wg0001WordEnums.add(memberNameEnum2);
		Wg0001WordEnums.add(memberNameKanaEnum3);
		Wg0001WordEnums.add(memberNameEnum3);
		Wg0001WordEnums.add(memberNameKanaEnum4);
		Wg0001WordEnums.add(memberNameEnum4);
		Wg0001WordEnums.add(memberNameKanaEnum5);
		Wg0001WordEnums.add(memberNameEnum5);
		Wg0001WordEnums.add(memberNameKanaEnum6);
		Wg0001WordEnums.add(memberNameEnum6);
		Wg0001WordEnums.add(memberNameKanaEnum7);
		Wg0001WordEnums.add(memberNameEnum7);
		Wg0001WordEnums.add(memberNameKanaEnum8);
		Wg0001WordEnums.add(memberNameEnum8);
		Wg0001WordEnums.add(createDateEnum);
		Wg0001WordEnums.add(rsoNameEnum);

		// Enumのデータをドキュメントに書き込む
		super.writeWord(doc, Wg0001WordEnums);
	}

}
