/**********************************************
 * 会計管理、精算書作成
 **********************************************/

/** datepickerの処理 */
$(document).on('click' , ".btn_popcal, .inputDateSlash" ,function(){
	let input = $(this).siblings(".inputDateSlash");

	/** このIDのみDatePickerで独自処理があるので除く */
	if($(input).is("#start-date-select")){
		return false;
	}

	$(input).datepicker({
		dateFormat: 'yy/mm/dd',
	});
	$(input).focus();
});

/**
 * 会計共通のjsファイル
 * アカウントの紐づく口座情報取得
 */
function getKozaDetail(ginkoAccountSeq, kozaTarget1, kozaTarget2) {
	if (ginkoAccountSeq == ""){
		kozaTarget1.text("");
		kozaTarget2.text("");
		$(".kozaAreaAccount").addClass("hidden");
	} else {
		$.ajax({
			url : "/user/kaikeiManagement/getKozaInfo",
			type : "POST",
			data : {"ginkoAccountSeq" : ginkoAccountSeq}
		}).done(function(val){
			if(val.succeeded){
				// 口座情報を取得できた場合
				let viewKoza1 = val.ginkoName + "　" + val.shitenName;
				let shitenNo = val.shitenNo;
				if (shitenNo) {
					viewKoza1 = viewKoza1 + "（" + shitenNo + "）";
				}
				kozaTarget1.text(viewKoza1);
				kozaTarget2.text(val.kozaType + "　" + val.kozaNo + "　" + val.kozaName);
				$(".kozaAreaAccount").removeClass("hidden");
			} else {
				// 口座情報を取得できなかった場合
				kozaTarget1.text("");
				kozaTarget2.text("");
				$(".kozaAreaAccount").addClass("hidden");
			}
		}).fail(function(jqXHR, textStatus, errorThrown) {
			kozaTarget1.text("");
			kozaTarget2.text("");
		});
	}
}

/**
 * 会計共通のjsファイル
 * 顧客or関与者の口座情報取得
 */
function getCustomerKozaDetail(dataType, customerId, kanyoshaSeq, kozaTarget1, kozaTarget2, errorMsgTarget){

	if (dataType == "") {
		kozaTarget1.text("");
		kozaTarget2.text("");
		$(".kozaAreaCustomerKanyosha").addClass("hidden");
		return false;
	}
	
	$.ajax({
		url : "/user/kaikeiManagement/getCustomerKozaInfo",
		type : "POST",
		data : {"customerId" : customerId,
				"kanyoshaSeq" : kanyoshaSeq}
	}).done(function(val){
		if (val.succeeded){
			let viewKoza1 = val.ginkoName + "　" + val.shitenName;
			let shitenNo = val.shitenNo;
			if (shitenNo) {
				viewKoza1 = viewKoza1 + "（" + shitenNo + "）";
			}
			kozaTarget1.text(viewKoza1);
			kozaTarget2.text(val.kozaType + "　" + val.kozaNo + "　" + val.kozaName);
			$(".kozaAreaCustomerKanyosha").removeClass("hidden");
		} else {
			kozaTarget1.text("");
			kozaTarget2.text("");
			if (errorMsgTarget) {
				errorMsgTarget.text(val.message);
				errorMsgTarget.removeClass("hidden");
			}
			$(".kozaAreaCustomerKanyosha").addClass("hidden");
		}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		return false;
	});
}