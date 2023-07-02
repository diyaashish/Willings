/**---------------------------------
 * common.js
 ---------------------------------*/

let benzo = {global: {common: {}, fragment: {}, }};

/**
 * HTML要素の初回レンダリング時に共通で必要な処理をここにまとめる
 * @param selector 初期化を行うエレメントのセレクタ。ここで指定された要素内に対して初期化を行う。
 *                 selectorを指定しない場合は、画面全体を初期化の範囲とする。
 */
function commonSetUpElement(selector) {

	if (!selector) {
		selector = '';
	} else {
		selector = selector + ' ';
	}
	
	// ツールチップの初期設定
	$(selector + '[data-toggle="tooltip"]').tooltip()

	// ポップオーバーの初期設定
	$(selector + '[data-toggle="popover"]').popover()
	
	// テキストボックス内の削除ボタンを有効化する
	activationAddTextClear();
	
	// テキストボックス内の削除ボタンを有効化する（form-controll使用）
	activationFcAddTextClear();

	// Datepickerの初期設定
	$(selector + "[data-toggle='datepicker']").filter("[data-target], [data-target-name]").each(function(){
		setUpDatepickerButton(this);
	});
	
	// popoverを閉じる
	$(document).on('click touchend', function(event) {
		if (!$(event.target).closest('.popover').length) {
			$(document).find('.outPutContainerClose').popover('hide');
		}
	});

	// 住所検索（上書きする）※重複処理が発生を防ぐためoff関数を使用する
	$(document).off("click", ".searchAddress");
	$(document).on("click", ".searchAddress", function() {
		
		const addressContainer = $(this).closest(".addressContainer");
		const zipCode = addressContainer.find(".zipCode");
		const address1 = addressContainer.find(".address1");
		
		if (zipCode.prop("disabled") || address1.prop("disabled")) {
			return false;
		}
		
		const dummyAddressContainer = addressContainer.clone();
		const dummyZipCode = dummyAddressContainer.find(".zipCode");
		const dummyAddress1 = dummyAddressContainer.find(".address1");

		// name属性が重複すると、最初に検出したセレクタに反映されてしまうので、ダミー要素を作成し、値をコピーする
		$("body").prepend(dummyAddressContainer);
		dummyAddressContainer.addClass("hidden")
		dummyZipCode.attr("name","dummyZipCode");
		dummyAddress1.attr("name","dummyAddress1");

		AjaxZip3.zip2addr(
				dummyZipCode.attr("name"),
				"",
				dummyAddress1.attr("name"),
				dummyAddress1.attr("name"),
				"",
				"",
				false);
		
		AjaxZip3.onSuccess = function () {
			zipCode.val(dummyZipCode.val());
			address1.val(dummyAddress1.val())
			// 値のコピーが終わったら要素を削除
			dummyAddressContainer.remove();
		}
	});
}

/**
 * マウスで掴んでスクロール
 * @param element
 * @returns
 */
function mouseDragScrollable(element) {
	let target; // 動かす対象
	$(element).each(function (i, e) {
		$(e).mousedown(function (event) {
			event.preventDefault();
			target = $(e); // 動かす対象
			$(e).data({
				"down": true,
				"move": false,
				"x": event.clientX,
				"y": event.clientY,
				"scrollleft": $(e).scrollLeft(),
				"scrolltop": $(e).scrollTop(),
			});
			return false
		});
		// move後のlink無効
		$(e).click(function (event) {
			if ($(e).data("move")) {
				return false
			}
		});
	});
	// 要素内/外でのevent
	$(document).mousemove(function (event) {
		if ($(target).data("down")) {
			event.preventDefault();
			let move_x = $(target).data("x") - event.clientX;
			let move_y = $(target).data("y") - event.clientY;
			if (move_x !== 0 || move_y !== 0) {
				$(target).data("move", true);
			} else {
				return;
			};
			$(target).scrollLeft($(target).data("scrollleft") + move_x);
			$(target).scrollTop($(target).data("scrolltop") + move_y);
			return false
		}
	}).mouseup(function (event) {
		$(target).data("down", false);
		return false;
	});
}

/**
 * tagにツールチップに関する属性を設定する
 * @param tag
 * @returns
 */
function appendDataToggleTooltipAttribute(tag) {
	return tag.attr("data-toggle", "tooltip");
}

/**
 * tagからツールチップに関する属性を削除する
 * 
 * @param tag
 * @returns
 */
function removeDataToggleTooltipAttribute(tag) {
	return tag.removeAttr("data-toggle", "tooltip");
}

/**
 * 関数の遅延発火処理
 * 
 * @param func 実行する処理
 * @param option 遅延実行時のオプションパラメータ
 * @param ...args 関数実行に渡したい引数
 * @returns 
 */
function lazyQueueFn(func, option, ...args) {
	
	// オプションのデフォルト値
	let defaultOption = {
		lazyMilSec: 500,
		clearQueueId: ''
	}
	
	// デフォルト値を入力値によって上書き
	const result = {...defaultOption, ...option};
	
	// キュー内の処理をIDをキーとして削除する
	if (result.clearQueueId) {
		clearTimeout(result.clearQueueId);
	}
	
	// キューに処理を追加、発行したIDを返却
	const queueId = setTimeout(func, result.lazyMilSec, ...args);
	return queueId;
}

$(function() {
	
	// 全てのクリックイベント
	$(document).on('click',function(e) {
		
		// datepicker内の場合はなにもしない
		let datepicker = $(".ui-datepicker:visible");
		if (datepicker.length) {
			return;
		}
		
		// 自作ポップオーバーの非表示処理（ポップオーバー外のクリック）
		let popover = $(e.target).closest('.popover_box');
		if(!popover.length) {
			// ポップオーバー要素の外側をクリックした時の操作
			// 全てのポップオーバーを非表示とする
			$('.popover_box').not('.notAutoClose').hide();
		} else {
			// ターゲット要素をクリックした時の操作
			// 特になにもしない
		}
		
	});

	// 自作ポップオーバーの非表示処理（閉じるクリック）
	$(document).on('click', '.popoverBox .closePopoverBox', function () {
		let popover = $(this).closest('.popoverBox');
		popover.hide();
	})
});

$(function() {
	
	// テキストボックス内の削除ボタンを有効化する
	activationAddTextClear();
	
	// テキストボックス内の削除ボタンを有効化する（form-controll使用）
	activationFcAddTextClear();
	
	// ツールチップを有効化する
	activationTooltip();

	// メッセージエリアを非表示にする
	$('.msg_page_close_icon').on('click', function() {
		let messageArea = $(this).closest(".messageArea");
		if(messageArea.data("js")){
			messageArea.find(".infoMessageArea").hide();
			messageArea.find(".warnMessageArea").hide();
			messageArea.find(".errorMessageArea").hide();
		}else{
			messageArea.addClass('hidden');
		}
	});

	/** JSで表示したメッセージエリアを非表示にする */
	$('.closeMsgIconJs').on('click', function() {
		/** INFOメッセージエリアを初期化 */
		$("#fragMessageAreaInfo").hide();
		$('#infoMessageAreaJs').addClass('hidden');
		
		/** WARNメッセージエリアを初期化 */
		$("#fragMessageAreaWarn").hide();
		$('#warnMessageAreaJs').addClass('hidden');
		
		/** ERRORメッセージエリアを初期化 */
		$("#fragMessageAreaError").hide();
		$('#errorMessageAreaJs').addClass('hidden');

	});
	
});

/**
 * エラーメッセージクリアする
 */
function clearAllMessages(container) {
	if(container){
		getMessageArea(container).find(".infoMessageArea, .warnMessageArea, .errorMessageArea").hide()
			.find(".msgText").text("");
	} else {
		$("#fragMessageAreaInfo, #fragMessageAreaWarn, #fragMessageAreaError").hide()
			.find(".msgText").text("").end()
			.find(".forJs").text("");
	}
}

/**
 * 各項目エラーメッセージをクリアする
 * ※ダイアログで使用※
 */
function clearAllItemMsg() {
	$('[id*="itemMsg_"]').text('');
	$('[id*="itemMsg_"]').addClass('hidden');
}

/**
 * メッセージを表示
 */
function showMessage(messageAreaSelector, message, container, itemMsg, showAsHtml){
	clearAllMessages(container);
	clearAllItemMsg();
	if (container) {
		// ヘッダのエラーメッセージを設定
		if (showAsHtml) {
			// 改行コードをbrタグに変換
			message = lineBreakCode2BrTag(message);
			getMessageArea(container).find(messageAreaSelector).show().find(".msgText").html(message);
		} else {
			getMessageArea(container).find(messageAreaSelector).show().find(".msgText").text(message);
		}
		// エラーメッセージを設定
		$.each(itemMsg, function(index, val) {
			$('[id="itemMsg_' + val.field + '"]').append(val.defaultMessage + "<br>");
			$('[id="itemMsg_' + val.field + '"]').removeClass('hidden');
		});

	} else {
		// 改行コードをbrタグに変換
		message = lineBreakCode2BrTag(message);
		$(messageAreaSelector).show().find(".forJs").html(message);
	}
}

/**
 * INFOメッセージをテキストとして表示
 */
function showInfoMessage(message, container, itemMsg) {
	let messageAreaSelector = container ? ".infoMessageArea" : "#fragMessageAreaInfo";
	showMessage(messageAreaSelector, message, container, itemMsg, false);
}
/**
 * INFOメッセージをHTMLとして表示
 */
function showInfoMessageAsHtml(message, container, itemMsg) {
	let messageAreaSelector = container ? ".infoMessageArea" : "#fragMessageAreaInfo";
	showMessage(messageAreaSelector, message, container, itemMsg, true);
}
/**
 * WARNメッセージをテキストとして表示
 */
function showWarnMessage(message, container, itemMsg) {
	let messageAreaSelector = container ? ".warnMessageArea" : "#fragMessageAreaWarn";
	showMessage(messageAreaSelector, message, container, itemMsg, false);
}
/**
 * WARNメッセージをHTMLとして表示
 */
function showWarnMessageAsHtml(message, container, itemMsg) {
	let messageAreaSelector = container ? ".warnMessageArea" : "#fragMessageAreaWarn";
	showMessage(messageAreaSelector, message, container, itemMsg, true);
}
/**
 * ERRORメッセージをテキストとして表示
 */
function showErrorMessage(message, container, itemMsg) {
	let messageAreaSelector = container ? ".errorMessageArea" : "#fragMessageAreaError";
	showMessage(messageAreaSelector, message, container, itemMsg, false);
}
/**
 * ERRORメッセージをHTMLとして表示
 */
function showErrorMessageAsHtml(message, container, itemMsg) {
	let messageAreaSelector = container ? ".errorMessageArea" : "#fragMessageAreaError";
	showMessage(messageAreaSelector, message, container, itemMsg, true);
}
function getMessageArea(container){
	return $(container).find(".messageArea").addBack(".messageArea");
}

/**
 * メッセージ表示する
 * ※親画面でJSでメッセージ表示する際に使用
 * ※以下の関数内で呼んでいる「showXxxxMessage(message)」は第二引数（container）を渡さずnullになる。
 *   そのため、後続処理の判定の結果メッセージをtextではなく、htmlで表示するようになっている。
 */
let execShowMsg;
function showInfoMessageForJs(message) {
	clearTimeout(execShowMsg);
	$('#infoMessageAreaJs').removeClass('hidden');
	$('#fragMessageAreaInfo').show();
	showInfoMessage(message);
	execShowMsg = setTimeout(function(){
		$('#fragMessageAreaInfo').hide();
	}, 2500);
}
function showWarnMessageForJs(message) {
	$('#warnMessageAreaJs').removeClass('hidden');
	$('#fragMessageAreaWarn').show();
	showWarnMessage(message);
}
function showErrorMessageForJs(message) {
	$('#errorMessageAreaJs').removeClass('hidden');
	$('#fragMessageAreaError').show();
	showErrorMessage(message);
}
let execErrorMsg;
function showErrorMessageFadeOutForJs(message) {
	clearTimeout(execErrorMsg);
	$('#errorMessageAreaJs').removeClass('hidden');
	$('#fragMessageAreaError').show();
	showErrorMessage(message);
	execErrorMsg = setTimeout(function(){
		$('#fragMessageAreaError').hide();
	}, 2500);
}

/**
 * メッセージを表示 + エラーメッセージアイコン等を表示
 * ※親画面でJSでメッセージ表示する際に使用
 */
function showItemMsg(itemMsg){
	clearAllMessages(false);
	clearAllItemMsg();
	// エラーメッセージを設定
	$.each(itemMsg, function(index, val) {
		$('[id="itemMsg_' + val.field + '"]').append(val.defaultMessage + "<br>");
		$('[id="itemMsg_' + val.field + '"]').removeClass('hidden');
	});
}
function showErrorMessageItemMsgForJs(message, itemMsg) {
	showItemMsg(itemMsg);
	$("#errorMessageAreaJs").removeClass("hidden");
	const messageAreaSelector = "#fragMessageAreaError";
	$(messageAreaSelector).show().find(".forJs").text(message);
}

/** モーダル内メッセージ表示 */
function showInfoMsgForModal(message) {
	showMessageForModal(".infoMessageArea", message);
}
function showWarnMsgForModal(message) {
	showMessageForModal(".warnMessageArea", message);
}
function showErrorMsgForModal(message) {
	showMessageForModal(".errorMessageArea", message);
}
function showMessageForModal(messageArea, message) {
	$(".messageArea > div").hide();
	$(messageArea).show();
	$(messageArea).find(".msgText").text(message);
}


/** ツールチップを有効化する */
function activationTooltip() {
	// ツールチップ
	$(".star_btn_main, .btn_edit_ov").tooltip({
		container: ".global_header",
		offset: "0, 5",
		trigger: "hover"
	});
}

/** 入力があったテキストボックスに削除ボタンを付与を有効化する */
function activationAddTextClear() {
	$(".addTextClear").addClear({
		tabbable : false,
		right: 6
	});
}
/** 入力があったテキストボックスに削除ボタンを付与を有効化する「form-control」の場合 */
function activationFcAddTextClear() {
	$(".fcAddTextClear").addClear({
		tabbable : false,
		top: 8
	});
}

/* blurイベント */
$(function(){
	// 小文字→大文字に変換
	$(document).on('blur', '.to-upper', function() {
		if (!($(this).val())) {
			return;
		}
		$(this).val($(this).val().toUpperCase());
	});
	// ゼロパディング
	$(document).on('blur', '.zero-padding', function() {
		if (!($(this).val())) {
			return;
		}
		$(this).val(lpad($(this).val(), $(this).attr("maxlength"), "0"));
	});
});

/* パディング処理Left */
function lpad (str, max, opt_padstr) {
	padstr = opt_padstr === undefined ? " " : opt_padstr;
	str = str.toString();
	return str.length < max ? lpad(padstr + str, max, padstr) : str;
}

/* パディング処理Right */
function rpad (str, max, opt_padstr) {
	padstr = opt_padstr === undefined ? " " : opt_padstr;
	str = str.toString();
	return str.length < max ? rpad(str + padstr, max, padstr) : str;
}

/* 入力制御 */
$(function(){
	$(document).on('keydown', '.inputTime, .inputTime > div > input', function(e) {
		var k = e.keyCode;
		// 0～9, テンキ—0～9, backspace, delete, →, ←, Home、End以外は入力キャンセル
		if(!((k >= 48 && k <= 57) || (k >= 96 && k <= 105) || k == 8 || k == 46 || k == 39 || k == 37 || k == 186 || k == 9 || k == 35 || k == 36)) {
			return false;
		}
		if (((k >= 48 && k <= 57) || (k >= 96 && k <= 105) || (k == 186))  && e.target.value.length >= 5) {
			return false;
		}
	});

	$(document).on('keydown', '.inputNum3', function(e) {
		var k = e.keyCode;
		// 0～9, テンキ—0～9, backspace, delete, →, ←, Home、End以外は入力キャンセル
		if(!((k >= 48 && k <= 57) || (k >= 96 && k <= 105) || k == 8 || k == 46 || k == 39 || k == 37 || k == 9 || k == 35 || k == 36)) {
			return false;
		}
		if (((k >= 48 && k <= 57) || (k >= 96 && k <= 105))  && e.target.value.length >= 3) {
			return false;
		}
	});
	$(document).on('keydown', '.inputNum4', function(e) {
		var k = e.keyCode;
		// 0～9, テンキ—0～9, backspace, delete, →, ←, Home、End以外は入力キャンセル
		if(!((k >= 48 && k <= 57) || (k >= 96 && k <= 105) || k == 8 || k == 46 || k == 39 || k == 37 || k == 9 || k == 35 || k == 36)) {
			return false;
		}
		if (((k >= 48 && k <= 57) || (k >= 96 && k <= 105))  && e.target.value.length >= 4) {
			return false;
		}
	});

	$(document).on('keydown', '.inputNum8', function(e) {
		var k = e.keyCode;
		// 0～9, テンキ—0～9, backspace, delete, →, ←, Home、End以外は入力キャンセル
		if(!((k >= 48 && k <= 57) || (k >= 96 && k <= 105) || k == 8 || k == 46 || k == 39 || k == 37 || k == 9 || k == 35 || k == 36)) {
			return false;
		}
		if (((k >= 48 && k <= 57) || (k >= 96 && k <= 105))  && e.target.value.length >= 8) {
			return false;
		}
	});

	$(document).on('blur', '.input_number_only, .inputNumberOnly',function(){
		// 全角 ⇒ 半角変換
		// フォーカスが外れた後、半角数字以外の文字を除去
		inputNumberOnlyFunc(this)
	});

	$(document).on('blur', '.input_number_only_except_zero',function(){
		// 全角 ⇒ 半角変換
		this.value = toHalfWidth(this.value);
		// フォーカスが外れた後、半角数字以外の文字を除去
		this.value = this.value.replace(/[^1-9]/g,'');
		_addClearBtnHidden(this);
	});

	$(document).on('blur', '.input_num_hyphen, .inputNumHyphen',function(){
		// 全角 ⇒ 半角変換
		this.value = toHalfWidth(this.value);
		// フォーカスが外れた後、半角数字と半角ハイフン以外の文字を除去
		this.value = this.value.replace(/[^0-9\-]/g,'');
		_addClearBtnHidden(this);
	});

	$(document).on('blur', '.input_num_slash',function(){
		// 全角 ⇒ 半角変換
		this.value = toHalfWidth(this.value);
		// フォーカスが外れた後、半角数字と半角スラッシュ以外の文字を除去
		this.value = this.value.replace(/[^0-9\/]/g,'');
		_addClearBtnHidden(this);
	});

	$(document).on('blur', '.input_half, .inputHalf',function(){
		// フォーカスが外れた後、半角英数字かピリオド, ハイフン, アンダーバー, @以外の文字を除去
		this.value = this.value.replace(/[^a-zA-Z0-9@._-]/g, '');
		_addClearBtnHidden(this);
	});

	$(document).on('blur', '.inputDateSlash',function(){
		// フォーカスが外れた後、半角数字と半角スラッシュ以外の文字を除去
		this.value = this.value.replace(/[^0-9\/]/g,'');

		// 値なければなにもしない
		if(!this.value){
			return false;
		}

		// 入力された値を数値化し、パディング処理を行う
		this.value = this.value.split("/")
			.map((num, index) => {
				const padCount = index ===0 ? 4 : 2; // 最初は4桁
				return lpad(Number(num), padCount, "0");
			}).slice(0,3).join("/");
		_addClearBtnHidden(this);
	});
	
	$(document).on('blur', '.inputRemoveSpace', function() {
		// フォーカスが外れた後、スペース文字を除去
		this.value = this.value.replace(/( |　)+/g,'');
		_addClearBtnHidden(this);
	});
	
	$(document).on('blur', '.inputCommaArray', function() {
		// フォーカスが外れた後、配列文字列に置換できる形にフォーマット
		let array = split(this.value);
		array = emptyFilter(array);
		this.value = arrayToJoinStr(array)
		_addClearBtnHidden(this);
	});
});

/**
 * 入力制御関数
 * ・全角 ⇒ 半角変換
 * ・フォーカスが外れた後、半角数字以外の文字を除去
 */
function inputNumberOnlyFunc(target) {
	// 全角 ⇒ 半角変換
	target.value = toHalfWidth(target.value);
	// フォーカスが外れた後、半角数字以外の文字を除去
	target.value = target.value.replace(/[^0-9]/g,'');
	_addClearBtnHidden(target);
}

function inputDateSlashFunc(targetObj) {

	let value = $(targetObj).val();
	// 半角数字と半角スラッシュ以外の文字を除去
	value = value.replace(/[^0-9\/]/g,'');
	// 値なければなにもしない
	if(isNaN(new Date(value))){
		return false;
	}

	// 入力された値を数値化し、パディング処理を行う
	value = value.split("/")
		.map((num, index) => {
			const padCount = index ===0 ? 4 : 2; // 最初は4桁
			return lpad(Number(num), padCount, "0");
		}).slice(0,3).join("/");
	$(targetObj).val(value);
}

/** テキストボックス内の☓ボタンも非表示 */
function _addClearBtnHidden(obj) {
	// テキストボックス内チェック
	if (obj.value.length === 0) {
		$(obj).parent().find("a").css("display", "none");
	}
}
/** 検索条件エリア内のテキストボックスの☓ボタンをすべて非表示 */
function allClearBtnHidden() {
	$(".searchform_block_main").find(".add-clear-span > a").css("display", "none");
}

//全角 ⇒ 半角変換
function toHalfWidth(strVal){
	// 半角変換
	var halfVal = strVal.replace(/[！-～]/g, function( tmpStr ) {
		return String.fromCharCode( tmpStr.charCodeAt(0) - 0xFEE0 );
	});

	// 文字コードシフトで対応できない文字の変換
	return halfVal.replace(/”/g, "\"")
		.replace(/[‐－―]/g, "-")
		.replace(/[／]/g, "/")
		.replace(/’/g, "'")
		.replace(/‘/g, "`")
		.replace(/￥/g, "\\")
		.replace(/　/g, " ")
		.replace(/〜/g, "~");
}
/*
 * @classdesc セレクトボックスの切替え時にPOSTリクエストを発行します。
 * @param objSelector	セレクトボックスオブジェクト
 * @param url			actionに設定するURL
 * @param formId		formのID
 */
function changeSelector(objSelector, url, formId){

	var selectedValue = objSelector.options[objSelector.selectedIndex].value;

	// パラメータを付加
	url = url + "=" + selectedValue;

	// formオブジェクトを取得
	var f = document.forms[formId];

	f.method = 'POST';
	f.action = url;

	// サブミット実行
	$(f).submit();
}

/**
 * 改行コードをbrタグに変換する
 *
 * @param  <string>  変換対象文字列
 */
function lineBreakCode2BrTag(str) {

	if (!str) {
		// 文字がnullやundefinedの場合
		return "";
	}

	return str.replace(/\r?\n/g, '<br>')
}

/**
 * 文字列のエスケープ処理を行う
 *
 * @param  <string>  エスケープ対象文字列
 */
var escapeHtml = (function (String) {
	var escapeMap = {
		'&': '&amp;',
		"'": '&#x27;',
		'`': '&#x60;',
		'"': '&quot;',
		'<': '&lt;',
		'>': '&gt;'
	};
	var escapeReg = '[';
	var reg;
	for (var p in escapeMap) {
		if (escapeMap.hasOwnProperty(p)) {
			escapeReg += p;
		}
	}
	escapeReg += ']';
	reg = new RegExp(escapeReg, 'g');

	return function escapeHtml (str) {
		str = (str === null || str === undefined) ? '' : '' + str;
		return str.replace(reg, function (match) {
			return escapeMap[match];
		});
	};
}(String));

/* 数値文字列をカンマ区切りにする */
function formatComma(value){
    let strValue = String(value);
    strValue = unformatComma(strValue);
    strValue = strValue.trim();
    if(!isDecimalString(strValue)){
        return value;
    }
    strValue = trimZeroOfDecimal(strValue);

    let commaValue = strValue.replace(/^([+-]?\d+)/, function(s){
        return s.replace(/(\d)(?=(\d{3})+$)/g, '$1,')
    })

    return commaValue
}

/* カンマを除去する */
function unformatComma(value){
    return String(value).replace(/,/g, "");
}

/* 10進数として有効な文字列であるか判定する */
function isDecimalString(str){
    return /^[+-]?(\d+)(\.\d+)?$/.test(str);
}

/*
 * 10進数値文字列の0をトリムする
 *
 * trimZeroOfDecimal("010")      => "10"
 * trimZeroOfDecimal("-010.010") => "-10.01"
 * trimZeroOfDecimal("10.0")     => "10"
 * trimZeroOfDecimal("000")      => "0"
 * trimZeroOfDecimal("00abc00")  => "00abc00"
 * trimZeroOfDecimal("")         => ""
 * trimZeroOfDecimal(null)       => null
 */
function trimZeroOfDecimal(value){
    if(!isDecimalString(String(value))){
        return value;
    }
    return String(value)
        .replace(/^([+-])?(0+)(?!\.|$)/, "$1")
        .replace(/\.0*$|(\.\d+?)(0+)$/, "$1");
}

/*
 * Base64にエンコードされた文字列をUTF8の文字列にデコードする
 * 
 * @param {string} str Base64にエンコードされている文字列
 * @return {string} デコード文字列（UTF8）
 */
function b64DecodeUnicode(str) {
    return decodeURIComponent(atob(str).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
}

(function($) {
    $.fn.formattedValue = function() {
        return formatComma(this.val());
    };
    $.fn.unformattedValue = function() {
        return unformatComma(this.val());
    };
    $.fn.formatComma = function() {
        this.val(this.formattedValue());
        return this;
    };
    $.fn.unformatComma = function() {
        this.val(this.unformattedValue());
        return this;
    };
})(jQuery);

$(function() {
    $(document).on({
        "focus" : function() {
            $(this).unformatComma();
        },
        "blur" : function() {
            $(this).formatComma();
        }
    }
    , ".commaFormat");

    $(document).on("submit", "form", function() {
        $(this).find(".commaFormat").each(function() {
            $(this).unformatComma();
        });
    });

    $(".commaFormat").each(function() {
        $(this).formatComma();
    });
});

benzo.global.common.submitted = false;
$(function() {
    $(document).on("submit", function(e) {
        if(!$(e.target).is(".allowDoubleSubmit")){
            if(benzo.global.common.submitted){
                e.preventDefault();
            } else {
                benzo.global.common.submitted = true;
            }
        }
    });
});

$(function() {
    $(document).on("change", ".zipCodeFormat", function() {
        var zipString = $(this).val();
        if (/\d{7}|[０-９]{7}/.test(zipString)) {
        	$(this).val(zipString.replace(/(\d{3}|[０-９]{3})/, "$1-"));
        }
    });
});

$(function() {
	$(document).on("click", ".copyText", function() {
		copyToClipboard($(this));
	});
})

/**
 * クリップボードコピー
 * 
 * @param obj
 * @returns
 */
function copyToClipboard(obj) {
	let copyText = "";
	if ($(obj).attr("data-copy-val")) {
		copyText = $(obj).attr("data-copy-val");
	} else {
		copyText = $(obj).text();
	};
	if(!copyText.length || copyText.length > 256) {
		return false;
	}
	const copyInput = $("<input type='text' value='" + copyText + "'/>")
	$("body").append(copyInput);
	copyInput.select();
	const result = document.execCommand("copy");
	copyInput.remove();
}

/** クリック時に呼ばれる全ての親イベントを強制終了する */
$(function() {
	$(document).on('click', '.stop-events', function(e){
		e.stopPropagation();
	});
});

benzo.global.common.holidays = [];
$(function(){
	$.ajax({
		url : "/common/getHolidays",
		type : "GET",
	}).done(function(result){
		benzo.global.common.holidays = result;
	}).fail(function(jqXHR, textStatus, errorThrown) {
		// 取得失敗時はなにもしない -> 動作上は影響はないのでシステムエラーにはしない
	});
});

/**
 * datepickerのボタンを設定する
 *
 * @param {HTMLElement} button カレンダーボタン
 * @param {HTMLElement|string} target 日付を入力するinput要素
 */
function setUpDatepickerButton(button, target){
	if(typeof target === "undefined"){
		if($(button).data("target")){
			target = $($(button).data("target"));
		} else if($(button).data("targetName")){
			target = $("[name='" + $(button).data("targetName") + "']");
		} else {
			return;
		}
	}
	$(target).datepicker("destroy");
	$(target).datepicker({
		showOn: null,
		beforeShowDay: function(date) {
			if (date.getDay() == 0) {
				// 日曜日の場合 -> 特にクラスは付与しない
				return [true, '', null];
			} else if (date.getDay() == 6) {
				// 土曜日の場合 -> 特にクラスは付与しない
				return [true, '', null];
			}

			const holidays = benzo.global.common.holidays;
			let isHoliday = holidays.some((value) => value == $.datepicker.formatDate("yy-mm-dd", date));
			if (isHoliday) {
				return [true ,'ui-datepicker-week-end' , null];
			} else {
				return [true, '', null];
			}
		},
	});
	$(button).off("click.benzo_common_datepicker");
	$(button).on("click.benzo_common_datepicker", function(){
		$(target).datepicker("show");
	});
}

$(function() {
	$("[data-toggle='datepicker']").filter("[data-target], [data-target-name]").each(function(){
		setUpDatepickerButton(this);
	});
});

$(function() {

	/**
	 * Enterキーによるクリック連動
	 */
	$(document).on("keyup", "[data-enter-sync]", function(e){
		if(e.which == 13) {
			let targetSelector = $(this).data("enterSync");
			let target = $(targetSelector);
			clickSync(e, targetSelector, target);
		}
	});

	/**
	 * クリック連動
	 */
	$(document).on("click", "[data-click-sync]", function(e){
		let targetSelector = $(this).data("clickSync");
		let target = $(targetSelector);
		clickSync(e, targetSelector, target);
	});

	/**
	 * 親要素のクリックを子孫要素に連動
	 */
	$(document).on("click", "[data-click-sync-for-descendants]", function(e){
		let targetSelector = $(this).data("clickSyncForDescendants");
		let target = $(this).find(targetSelector);
		clickSync(e, targetSelector, target);
	});

	function clickSync(e, targetSelector, target){
		if($(e.target).closest(targetSelector).length > 0){
			return;
		}
		if($(e.target).closest("label").find(targetSelector).length > 0){
			return;
		}
		if(target.is("a")){
			target[0].click();
		} else {
			target.trigger("click");
		}
	}
});

/**
 * 日付をyyyy/mm/ddのフォーマットで出力する
 *
 */
function formatYYYYMMDD(year, month, date) {
	return year + "/" + lpad(month, 2, "0") + "/" + lpad(date, 2 ,"0");
}

/**
 * 時間をHH:mm:ss形式でフォーマットする
 */
function formatHHMMSS(hour, minute, second){
	return lpad(hour, 2, "0") + ":" + lpad(minute, 2, "0") + ":" + lpad(second, 2, "0");
}

/**
 * 時間をHH:mm形式でフォーマットする
 */
function formatHHMM(hour, minute){
	return lpad(hour, 2, "0") + ":" + lpad(minute, 2, "0");
}
/**
 * 時間をH:mm形式でフォーマットする
 */
function formatHMM(hour, minute){
	return hour + ":" + lpad(minute, 2, "0");
}

/**
 * 数値の四捨五入
 */
function round(num, step) {
	return Math.round(num / step) * step
}
/**
 * 数値の切り上げ処理
 */
function ceil(num, step) {
	return Math.ceil(num / step) * step
}
/**
 * 数値の切り下げ処理
 */
function floor(num, step) {
	return Math.floor(num / step) * step
}

/**
 * 日付をフォーマットする
 * @param  {Date}   date     日付
 * @param  {String} [format] フォーマット
 * @return {String}          フォーマット済み日付
 */
function formatDate(date, format) {
  if (!format) format = 'YYYY-MM-DD hh:mm:ss.SSS';
  format = format.replace(/YYYY/g, date.getFullYear());
  format = format.replace(/MM/g, ('0' + (date.getMonth() + 1)).slice(-2));
  format = format.replace(/DD/g, ('0' + date.getDate()).slice(-2));
  format = format.replace(/hh/g, ('0' + date.getHours()).slice(-2));
  format = format.replace(/mm/g, ('0' + date.getMinutes()).slice(-2));
  format = format.replace(/ss/g, ('0' + date.getSeconds()).slice(-2));
  if (format.match(/S/g)) {
    var milliSeconds = ('00' + date.getMilliseconds()).slice(-3);
    var length = format.match(/S/g).length;
    for (var i = 0; i < length; i++) format = format.replace(/S/, milliSeconds.substring(i, i + 1));
  }
  return format;
};

/** js Dateオブジェクトに指定日数を追加するメソッドを追加 */
Date.prototype.addDays = function(days) {
    let dat = new Date(this.valueOf())
    dat.setDate(dat.getDate() + days);
    return dat;
}
/** 日付と日付の間を配列取得するjs */
function getDates(startDate, stopDate, format) {
let dateArray = new Array();
	let currentDate = startDate;
	while (currentDate <= stopDate) {
		if (format) {
			dateArray.push($.datepicker.formatDate(format, currentDate))
		} else {
			dateArray.push(currentDate)
		}
		currentDate = currentDate.addDays(1);
	}
	return dateArray;
}

/**
 * 配列の重複を除去する
 */
function uniqueArray(array){
	return array.filter(function(element, index, self) {
		return self.indexOf(element) === index;
	});
}

/**
 * 文字列booleanをboolean型に変換
 */
function toBoolean(boolStr) {
	return boolStr.toLowerCase() === "true";
}

/**
 * カンマ文字列を配列に変換
 */
function split(str) {
	return str.split(',')
}

/**
 * 配列からエンプティ要素を除外
 */
function emptyFilter(array) {
	return array.filter((str) => str.trim() !== '');
}

/**
 * 配列をカンマ区切りで結合
 */
function arrayToJoinStr(array) {
	return array.join(',');
}

/**
 * オブジェクトをhtmlに反映する
 *
 * @param {Object} obj 反映するデータ
 * @param {HTMLElement|string} target データを反映するhtml要素
 */
function populateObjectToHtml(obj, target){
	let $target = (typeof target !== "undefined") ? $(target) : $("body");
	_populateObjectToHtml(obj);

	function _populateObjectToHtml(_obj, parentKey){
		$.each(_obj, function(key, value){
			let itemName = (typeof parentKey !== "undefined") ? parentKey + "[" + key + "]" : key;
			if(value && typeof value === "object"){
				_populateObjectToHtml(value, itemName);
				return;
			}
			let formItem = $target.find('[name="' + itemName + '"]');
			if(formItem.length != 0){
				if(formItem.is("input[type='checkbox']")){
					formItem.prop("checked", value);

				} else if(formItem.is("input[type='radio']")){
					formItem.filter("[value='" + value + "']").prop("checked", true);

				} else if(formItem.is("input, textarea")){
					formItem.val(value);

				} else if(formItem.is("select")){
					let option = formItem.find("option[value='" + value + "']");
					if(option.length != 0){
						option.prop("selected", true);
					} else {
						formItem.find("option:first").prop("selected", true);
					}
				}
			}
			let textItem = $target.find('[data-text-name="' + itemName + '"]');
			if(textItem.length != 0){
				textItem.text(value);
			}
		});
	}
}

function claerSearchConditions(formId){
	$('#' + formId).find("textarea, :text, select").val("").end().find(":checked").prop("checked", false);
}

/**
 * 複数のプルダウンでOptionを共有する関数
 *
 * @param {className} 共有させたい要素のクラス
 */
function gropingSelectOptions(...args){

	args.forEach(function(className,i){

		//初期遷移時
		$(document).ready(function(){
			logic();
		});

		//選択を表示するときに非表示にする
		$("." + className).on("change",function(){
			logic();
		});

		//遅延読み込み
		$(".removeInputItem").on("click",function(){
			setTimeout(logic(),50);
		});

		//中身の処理
		let logic = () =>{
			let selectedValues = [];
			$("." + className+" option:not([value='']):selected").each(function() {
				let $opt = $(this);
				selectedValues.push($opt.val())
			});
			$("." + className+" option:not([value='']):not(:selected)").each(function() {
				let $opt = $(this);
				let val = $opt.val();

				if ($.inArray(val, selectedValues) >= 0) {
					$opt.prop("hidden", true);
				} else {
					$opt.prop("hidden", false);
				}
			});
		}
	});
}

/**
 * listEditModal内選択肢のチェックされている物を上位にする関数
 *
 * @param {className} 共有させたい物のクラス
 */
function listEditModalTableSort(...args){

	args.forEach(function(className,i){

		let modal = $("."+className).closest(".modal");
		let modalId = "#"+modal.prop("id");

		//編集ボタン押下アクション
		$(document).on("click","[data-target='"+modalId+"']", function(){
			logic();
		});

		let logic = () =>{
			let checkedItemList = [];
			$("." + className + " input[type='checkbox']:checked").each(function(){
				let $item =  $(this).parents("tr");
				checkedItemList.push($item);
				$item.remove();
			});
			$(modal).find("tbody").prepend(checkedItemList);
		}
	});
}

<!--/* 詳細検索の開閉 */-->
$(function() {
	$(".searchform_detail_block_title, .openDetailFilter").on("click", function() {
		$(".searchform_detail_block_title, .openDetailFilter").toggleClass("detail_open");
		if ($(".searchform_detail_block_title, .openDetailFilter").hasClass("detail_open")) {
			$(".searchform_detail_block_title, .openDetailFilter").attr("data-original-title", "詳細検索を閉じる");
		} else {
			$(".searchform_detail_block_title, .openDetailFilter").attr("data-original-title", "詳細検索を開く");
		}
		$(".detail_block_multiLine").toggleClass("hidden");
	});
});

<!--/* 詳細検索の開閉処理  */-->
function setDetailSearchOpen() {
	let flg = $(".detail_block_multiLine").hasClass("hidden");
	if (flg) {
		$("#detailSearchFlg").val("0");
	} else {
		$("#detailSearchFlg").val("1");
	}
}

/** Cookieの取得  */
function getCookieValue(targetCookieName) {

	let cookieValue = null

	// ブラウザが保持するCookieの値を配列で取得
	const cookieAry = document.cookie.split(';');

	cookieAry.forEach(function(val) {
		//cookie名と値に分ける
		const content = val.split('=');
		const cookieName = content[0].trim();

		if (cookieName === targetCookieName) {
			cookieValue = content[1].trim();
		}
	})

	return cookieValue
}
/** 顧客テーブル（行結合ありの場合に使用）  */
function addEven() {
	let numTh = $(".addEvenList").find("th").length;
	let isEven = true;
	$(".addEvenList tr").each(function() {
		if (numTh == $(this).find("td").length) {
			isEven = !isEven;
		}
		/* 一番上のtdから数えて奇数行に「odd」、偶数行に「even」クラスを追加します。 */
		$(this).find("td").addClass(isEven ? "even" : "odd");
	});
}

/** 完了チェックしてある行の色を変える */
$(document).on("change", ".completedCheck", function(){
	let row = $(this).closest("tr");
	if($(this).prop("checked")){
		row.addClass("row_completed");
	} else {
		row.removeClass("row_completed");
	}
});

/** 入力項目追加 */
$(document).on("click", ".addInputItem", function(){
	let target = $($(this).data("target") + ".hidden");
	target.first().removeClass("hidden");
	let targetFirst = $($(this).data("target")).first();
	targetFirst.find(".removeInputItem").removeClass("hidden");
	showMainTanto(this);
	hideIfNoTarget(this);
});

/** 主担当フラグを表示するかの制御 */
function showMainTanto(button){
	let showTarget = $($(button).data("target"));
	let hiddenTarget = $($(button).data("target") + ".hidden");
	if(showTarget.length){
		let targetLength = showTarget.length - hiddenTarget.length;
		if(targetLength == 1){
			showTarget.find(".star_btn_main").addClass("hidden");
		}else{
			showTarget.find(".star_btn_main").removeClass("hidden");
		}
	}
}

/** 主担当マーク（checkbox）を活性化させる */
$(document).on("click", ".star_btn_main", function(){
	if ($(this).hasClass('btn_checked')){
		$(this).removeClass('btn_checked');
		$(this).next().val(false);
	} else {
		$(this).addClass('btn_checked');
		$(this).next().val(true);
	}
});

/** 項目が最大数まで追加されている場合に、追加ボタンを非表示にする */
function hideIfNoTarget(button){
	let target = $($(button).data("target") + ".hidden");
	if(target.length <= 0){
		$(button).addClass("hidden");
	}
}

/** 登録上限数に達している場合の非表示処理 */
$(function() {
	$(".addInputItem").each(function() {
		hideIfNoTarget(this);
		showMainTanto(this);
	});
});

/** セレクトボックス（multiple）のダブルクリックで選択解除 */
$(document).on("dblclick", "select[multiple] > option", function(){
	$(this).prop('selected', false);
});

/** ダウンロード処理 */
let dlAjaxRunning = false;
// 非同期処理用

/** バイナリファイルのダウンロード処理 */
function downloadFileAjax(postUrl, button, param) {
	downloadFileAjaxCommonFunc(postUrl, button, param, false);
}
/** CSVファイルのダウンロード処理 */
function downloadCsvFileAjax(postUrl, button, param) {
	downloadFileAjaxCommonFunc(postUrl, button, param, true);
}
/** システムで用意したファイルのダウンロード処理 */
function downloadFileAjaxCommonFunc(postUrl, button, param, isCsvFile) {
	//二重処理チェック
	if(dlAjaxRunning){
		return false;
	}
	dlAjaxRunning = true;
	$(button).find('.fa-download').addClass('hidden');
	$(button).find('.fa-spinner').removeClass('hidden');
	$.ajax({
		url : postUrl,
		type : "POST",
		data : param,
		xhrFields : {
			responseType: isCsvFile ? 'text' : 'blob'
		},
	}).done(function (response, status, xhr) {
		
		try {
			const fileName = _getFileName(xhr);
			if (fileName.split(".").length == 1) {
				const errorMsg = getAjaxProcResutlMessage(xhr);
				if (errorMsg != "") {
					showErrorMessageForJs(errorMsg);
				} else {
					alert("ファイルダウンロードに失敗しました。");
				}
				return false;
			}
			
			let blob;
			if (isCsvFile) {
				/** 文字化け対策 */
				let bom = new Uint8Array([0xEF, 0xBB, 0xBF])
				blob = new Blob([bom, response], { type: 'text/csv' });
			} else {
				blob = new Blob([response], { type: 'application/octet-stream' });
			}
			
			_blobDownload(blob, fileName);
		} catch (ex) {
			alert("ファイルダウンロードに失敗しました。");
		}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert("ファイルダウンロードに失敗しました。");
	}).always(function() {
		dlAjaxRunning = false;
		$(button).find('.fa-download').removeClass('hidden');
		$(button).find('.fa-spinner').addClass('hidden');
	});
}
// アップロードしたファイルのダウンロード処理
function downloadFileAjaxForS3(postUrl, button, param, loadingStyle) {
	//二重処理チェック
	if(dlAjaxRunning){
		return false;
	}
	dlAjaxRunning = true;
	$('.fa-download').addClass('hidden');
	$('.fa-spinner').removeClass('hidden');
	$.ajax({
		url : postUrl,
		type : "POST",
		data : param,
		xhrFields : {
			responseType: 'blob'
		},
	}).done(function (response, status, xhr) {
		try {
			const fileName = _getFileName(xhr);
			let blob = new Blob([response], { type: 'application/octet-stream' });
			_blobDownload(blob, fileName);

		} catch (ex) {
			alert("ファイルダウンロードに失敗しました。");
		}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert("ファイルダウンロードに失敗しました。");
	}).always(function() {
		dlAjaxRunning = false;
		$('.fa-download').removeClass('hidden');
		$('.fa-spinner').addClass('hidden');
	});
}

/**
 * ファイルダウンロード処理のPromise
 * 
 * @param postUrl ダウンロード処理
 * @param button
 * @param param
 * @param isCsvFile 
 * @returns
 */
function downloadAjaxPromise(postUrl, button, param, isCsvFile) {
	return new Promise((resolve, reject) => {
		button.find('.fa-download').addClass('hidden');
		button.find('.fa-spinner').removeClass('hidden');
		$.ajax({
			url : postUrl,
			type : "POST",
			data : param,
			xhrFields : {
				responseType: 'blob'
			},
		}).done(function (response, status, jqXHR) {
			
			if (getAjaxProcResutl(jqXHR) && !isAjaxProcSuccess(jqXHR)) {
				return reject(getAjaxProcResutlMessage(jqXHR));
			}
			
			try{
				const fileName = _getFileName(jqXHR);
				
				let blob;
				if (isCsvFile) {
					/** 文字化け対策 */
					let bom = new Uint8Array([0xEF, 0xBB, 0xBF])
					blob = new Blob([bom, response], { type: 'text/csv' });
				} else {
					blob = new Blob([response], { type: 'application/octet-stream' });
				}
				
				_blobDownload(blob, fileName);
				return resolve();
			} catch (ex) {
				// Js内でエラーが発生した場合
				console.log(ex);
				return reject("ファイルダウンロードに失敗しました。");
			}
		}).fail(function(jqXHR, textStatus, errorThrown) {
			return reject("ファイルダウンロードに失敗しました。");
		}).always(function() {
			button.find('.fa-download').removeClass('hidden');
			button.find('.fa-spinner').addClass('hidden');
		});
	});
	
}

/**
 * ダウンロード処理：ファイル名の取得
 * 
 * @param jqXhr
 * @returns
 */
function _getFileName(jqXhr) {
	let fileName = "";
	let disposition = jqXhr.getResponseHeader('Content-Disposition');

	if (disposition) {
		let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
		let matches = filenameRegex.exec(disposition);
		if (matches !== null && matches[1]) {
			fileName = decodeURI(decodeURI(matches[1].replace(/['"]/g, '')));
		}
	}
	return fileName;
}

/**
 * ダウンロード処理
 * 
 * @param blob
 * @param fileName
 * @returns
 */
function _blobDownload(blob, fileName) {
	if (typeof window.navigator.msSaveBlob !== 'undefined') {
		// IE対策(不要説)
		window.navigator.msSaveBlob(blob, fileName);
	} else {
		let URL = window.URL || window.webkitURL;
		let downloadUrl = URL.createObjectURL(blob);
		let a = document.createElement("a");
		
		if (typeof a.download === 'undefined' || !fileName) {
			window.location = downloadUrl;
		} else {
			a.href = downloadUrl;
			a.download = fileName;
			document.body.appendChild(a);
			a.target = "_blank";
			a.click();
			a.remove();
		}
	}
}

/** アイコン表示 */
function displayIcon(clickObj, targetObj) {
	$(clickObj).addClass('hidden');
	$(targetObj).removeClass('hidden');
}
/** アイコン非表示 */
function hiddenIcon(clickObj, targetObj) {
	$(clickObj).removeClass('hidden');
	$(targetObj).addClass('hidden');
}

/** モーダルの閉じる処理 */
function hideModalPromise(modal) {
	return new Promise((resolve) => {
		$(modal).one('hidden.bs.modal', function() {
			resolve();
		});
		$(modal).modal("hide");
	});
}

/** モーダルの表示処理 */
function showModalPromise(modal){
	return new Promise((resolve) => {
		$(modal).one('shown.bs.modal', function() {
			resolve();
		});
		$(modal).modal("show");
	});
};

/** フラグメント並び替えセットアップ処理 */
function setUpSortTargetFragment(selector) {
	_setUpSortTargetList(selector);
}

/** 共通並び替えセットアップ処理 */
function setUpSortTargetCommon() {
	_setUpSortTargetList("#sortTargetList");
}

/** 並び替えセットアップ処理 */
function _setUpSortTargetList(selector) {
	/* 初期の表示順を保存する */
	let currentDispOrder = new Map();
	$(selector + ' tr').each(function(idx){
		currentDispOrder.set($(this).data('target-id'), parseInt(idx));
	});

	/* 表示順の変更処理 */
	$(selector).sortable({
		  containment: "parent",
		  handle: ".sortableObj",
	});

	$(selector).bind("sortupdate", function(){
		let targetId = [];
		let index = [];

		$(this).find('tr').each(function(idx){
			targetId.push($(this).data("target-id"));
			/* 変更箇所を記憶する */
			if (currentDispOrder.get($(this).data("target-id")) != parseInt(idx)) {
				index.push(parseInt(idx));
			}
		});
		/* パラメータをStringに変換して渡す */
		if (targetId.length > 0) {
			$.ajax({
				url : "updateDispOrder",
				type : "POST",
				data : {"targetId" : String(targetId), "index" : String(index)},
			}).done(function(responce) {
				if (responce.succeeded) {
					let dispOrder = responce.dispOrder;
					$(selector + ' tr').each(function(idx){
						currentDispOrder.set($(this).data("target-id"), parseInt(idx));
						$(this).find(".sortableIdx").text(dispOrder[idx]);
					});
				} else {
					if (responce.message) {
						alert(responce.message);
					}
				}
			}).fail(function(jqXHR, textStatus, errorThrown) {
			}).always(function() {
			});
		}
	});

	/** 一覧の並べ替え */
	$(selector + ' tr').on({
		mouseenter: function() {
			_commonSortableObjOn(this);
		},
		mouseleave: function() {
			_commonSortableObjOff(this);
		}
	});
}

/** 並び順変更 */
$(function() {
	// 共通ソート処理設定
	setUpSortTargetCommon();
});

/** 共通マウスカーソルが重なった時に並び替えマークの表示処理 */
function _commonSortableObjOn(obj) {
	$(obj).find('.icon_drag_indicator').removeClass('hidden');
	$(obj).find('.sortable_obj_space').addClass('hidden');
}
/** タスク行からマウスカーソルが離れた時に並び替えマークの非表示処理 */
function _commonSortableObjOff(obj) {
	$(obj).find('.icon_drag_indicator').addClass('hidden');
	$(obj).find('.sortable_obj_space').removeClass('hidden');
}


/** エディタHTMLの差し替え */
$(function(){
	setUpPreview($(".previewTargetObj"));
});
function setUpPreview(targetObj){
	const previewTargetObj = $(targetObj);
	$(previewTargetObj).each((i , content) => {$(content).html($(content).text());});
};

/** onboardingの呼び出し関数 */
function getOnboarding(...onboaringCd) {
	const modal = $("#onboardingModal");

	if (!onboaringCd) {
		return false;
	}
	const param =[];
	$(onboaringCd).each((i , val) => {param.push({"name" : "onboardEnumCd", "value" : val});});

	$.ajax({
		url : "/user/onboarding/customize",
		type : "POST",
		data : param,
	}).done(function(result) {
		const newContents = $(result);
		$("#main-contents").append(newContents);
	}).fail(function(jqXHR, textStatus, errorThrown) {
		// なにもしない
	});
}

/** カスタムセレクトボックスの初期選択関数 (モーダル呼び出しなど)*/
function setUpCustomSelectBox() {
	$(".custom_selectbox_container").each(function(i,val) {
		const $selectContents = $(val);
		const $select = $(val).find(".select")
		const $selectedInput = $selectContents.find(".customeSelectInput");
		let $option;
		if ($selectedInput.val()) {
			$option = $selectContents.find(".custom_pulldown_container .option[data-value='"+$selectedInput.val()+"']")
		} else {
			$option = $selectContents.find(".custom_pulldown_container .option:first-child");
		}
		const selectText = $option.find(".mainText").length ? $option.find(".mainText").text() : $option.text();
		$select.text(selectText);
		$selectedInput.val($option.data("value"));
		$selectContents.find(".custom_pulldown_container").addClass("hidden");
	});
}

/** カスタムセレクトボックスの関数 */
$(function(){
	// 初期遷移時処理
	setUpCustomSelectBox()

	// 選択肢の表示処理
	$(document).on("click", ".custom_selectbox_container .select", function() {
		const selectContent = $(this).closest(".custom_selectbox_container");
		const selectedValue = selectContent.find(".customeSelectInput").val();
		$(".custom_pulldown_container").addClass("hidden");
		selectContent.find(".custom_pulldown_container").removeClass("hidden");
		selectContent.find(".custom_pulldown_container .option[data-value='"+selectedValue+"']").focus();
	});

	// オプション選択時の処理
	$(document).on("click", ".custom_pulldown_container .option", function() {
		const selectContent= $(this).closest(".custom_selectbox_container");
		const option = $(this);

		const selectText = option.find(".mainText").length ? option.find(".mainText").text() : option.text();
		selectContent.find(".select").text(selectText); // 選択文字をラベルに反映
		selectContent.find(".customeSelectInput").val(option.data("value")); // 選択した値をhiddenに設定
		selectContent.find(".customeSelectInput").trigger("change"); // hidden項目のchangeアクションを発火
		selectContent.find(".custom_pulldown_container").addClass("hidden"); // オプションを閉じる
	});

	// キーボードによる操作処理
	$(document).on("keydown", ".custom_pulldown_container .option", function(e) {
		e.preventDefault(); // 本来設定されているキーイベントを外す
		const selectContent= $(this).closest(".custom_selectbox_container");
		const option = $(this);
		if (e.which == 38) { // 上
			$(option.prev()).focus();
		} else if (e.which == 40) { // 下
			$(option.next()).focus();
		} else if (e.which == 13) { // Enter
			option.trigger("click");
		}
	});

	// フォーカス時にaria-selectedを付与
	$(document).on("focus", ".custom_pulldown_container .option", function() {
		const selectContent= $(this).closest(".custom_selectbox_container");
		selectContent.find(".custom_pulldown_container .option[aria-selected=true]").attr("aria-selected", false);
		$(this).attr("aria-selected", true);
	});

	// 指定要素以外をクリックしたときに閉じる
	$(document).on("click", function(e) {
		const activCustomPop = $(".custom_pulldown_container:not('.hidden')");
		if(activCustomPop.length) {
			if (!$(e.target).closest(".custom_selectbox_container").length) {
				$(".custom_pulldown_container").addClass("hidden");
			}
		}
	});

});