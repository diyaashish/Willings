/**
 * イベント実行時の入力キーがタブかマウス入力かを判定します
 */
function isTabOrMouseInput(keyCode) {
	let returnFlg = false;
	switch (keyCode){
		case void 0:
		case 9:
			<!--/** tabとキーボード入力以外で呼び出された場合 */-->
			returnFlg = true;
			break;
		default:
			break;
	}
	return returnFlg;
}

<!--/* 請求書詳細、精算書詳細の既入金項目の並び順を採番します */-->
function renumberDocRepayOrder() {
	let repayDocOrder = 1;
	$('.repayRow').each(function(){
		if ($(this).find('.docRepayOrder').length) {
			$(this).find('.docRepayOrder').val(repayDocOrder);
		}
		repayDocOrder++;
	});
}

<!--/* 請求書詳細、精算書詳細の請求項目の並び順を採番します */-->
function renumberDocInvoiceOrder() {
	let docInvoiceOrder = 1;
	$('.invoiceRow').each(function(){
		if ($(this).find('.docInvoiceOrder').length) {
			$(this).find('.docInvoiceOrder').val(docInvoiceOrder);
		}
		docInvoiceOrder++;
	});
}