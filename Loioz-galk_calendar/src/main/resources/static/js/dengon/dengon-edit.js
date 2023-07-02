/**
 * メッセージ編集用のjsファイル
 */

/* 選択された受信メッセージのSEQリストを取得する */
function getSelectedReceiveDengonSeqList() {
	let receiveDengonSeqList = [];
	$('input.msgitem_check:checked').each(function(){
		let sendFlg = $(this).data("check-dengon-send-flg");
		if (sendFlg == "0") {
			receiveDengonSeqList.push($(this).data("check-dengon-seq"));
		}
	});
	return receiveDengonSeqList;
}

/* 選択された送信メールのSEQリストを取得する */
function getSelectedSendDengonSeqList() {
	let sendDengonSeqList = [];
	$('input.msgitem_check:checked').each(function(){
		let sendFlg = $(this).data("check-dengon-send-flg");
		if (sendFlg == "1") {
			sendDengonSeqList.push($(this).data("check-dengon-seq"));
		}
	});
	return sendDengonSeqList;
}

/* 現在選択されているメッセージ一覧のSEQリストを取得する関数 */
function getSelectedDengonSeqList() {
	let dengonSeqList = [];
	$('input.msgitem_check:checked').each(function(){
		dengonSeqList.push($(this).data("check-dengon-seq"));
	});
	return dengonSeqList;
}

/** メッセージ詳細の受信者を全て表示 */
$(document).on("click", "#detailReceiverAllBtn", function () {

	// 受信者をすべて表示しているか判定
	if ($(this).hasClass("btn_down_arrow")) {
		// 閉じた状態 -> 受信者をすべて開く
		$(this).removeClass("btn_down_arrow").addClass("btn_up_arrow");
		$("#detailReceiverArea").removeClass("char_ellipsis");

	} else {
		// 開いた状態 -> 受信者を閉じる（表示エリアのみ）
		$(this).removeClass("btn_up_arrow").addClass("btn_down_arrow");
		$("#detailReceiverArea").addClass("char_ellipsis");

	}

});

$(function(){
	
	let ajaxRunning = false;
	benzo.global.dengonModal = {};
	// 送信するユーザーリストを取得
	$.ajax({
		url : "/user/dengon/edit/getReceiveAccountList",
		type : "POST",
	}).done(function(result) {
		benzo.global.dengonModal.canSendUser = result.accountList;
	}).fail(function(jqXHR, textStatus, errorThrown) {
	}).always(function() {
	});
	
	/* すべて選択をクリックしたら一覧のチェックボックスを変更する */
	$(document).on('click', '.msgCheckAll', function() {
		/* 全て選択されていれば全解除/それ以外は全選択 */
		let checkAll = $('.msg_list_body').find('input.msgitem_check:checked').length != $('.msg_list_body').find('input.msgitem_check').length;
		$('.msg_list_body').find('input.msgitem_check').each(function() {
			$(this).prop('checked', checkAll);
		});
	});

	/*メッセージの新規作成ボタン押下時 */
	$(document).on("click", ".createNewMessage", function() {

		let formData = [];
		formData.push(
				{name : "dengonSeq", value : null},
		);
		
		if(ajaxRunning) {
			return false;
		}
		ajaxRunning = true;
		
		$.ajax({
			url : "edit/create",
			type : "POST",
			data: formData,
		}).done(function(val) {
			// モーダルの表示
			$(".dengon-create-modal-body").html(val);
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function() {
			ajaxRunning = false;
		});
	});

	// メッセージ詳細・下書きボタン押下時
	$('.msg_body').on('click', 'button[name="draft"]', function() {
		
		var detailDengonSeq = $(this).parents('.msg_body_dengon_seq').attr('data-seq');

		if(ajaxRunning) {
			return false;
		}
		ajaxRunning = true;
		
		$.ajax({
			url : "/user/dengon/edit/create",
			type : "POST",
			data : {"dengonSeq" : detailDengonSeq},
		}).done(function(val) {
			$(".dengon-create-modal-body").html(val);
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function() {
			ajaxRunning = false;
		});
	});

	/* メッセージ詳細・返信ボタン押下時 */
	$('.msg_body').on('click', 'button[name="reply"]', function() {

		let detailDengonSeq = $(this).parents('.msg_body_dengon_seq').attr('data-seq');
		if (detailDengonSeq == null) {
			return false;
		}

		let url = $(this).attr('name');
		let data = {"dengonSeq" : detailDengonSeq, "isReplyAll" : $(this).hasClass('reply_all')};

		if(ajaxRunning) {
			return false;
		}
		ajaxRunning = true;
		
		$.ajax({
			url : "/user/dengon/edit/reply",
			type : "POST",
			data : data,
		}).done(function(val) {
			// モーダルの表示
			$(".dengon-create-modal-body").html(val);
			// 要返信に設定されていれば切り替える
			if ($('div#msgstatus').hasClass('is_reply')) {
				$('div#msgstatus').click();
			}
			$(".addCustomer").tooltip();
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function() {
			ajaxRunning = false;
		});
	});

	// 初期表示で案件、裁判、顧客を非表示にする
	$(document).ready(function() {
		$('.isAnkenRow, .isSaibanRow, .isKokyakuRow').hide();
	});

	/* メッセージ編集 送信/下書き保存ボタン押下時 */
	$('.dengon-create-modal-body').on('click', 'button.draftBtn, button.sendBtn', function() {

		let url = $(this).attr('name');
		let btnText = $(this).text();
		let accountSeqList = getAtesakiUserSeqList();
		let customerIdList = getSelectedCustomerId();
		let modal = $(this).closest('.modal');

		if (accountSeqList.length == 0) {
			if (url == "send") {
				alert("宛先を選んでください");
				return false;
			} else if (url == "draft") {
				accountSeqList = null;
			}
		}

		$('input[name="dto.draftFlg"]').val(url == "draft" ? "1" : "0");
		let data = $('.dengon-create-modal-body [name^="dto."]').serializeArray();
		data.push({name : "accountSeqList", value : accountSeqList});
		data.push({name : "customerIdList", value : customerIdList});
		data.push({name : "mailFlg", value : $('#dengonCreateModal input[type="checkbox"]').prop('checked')})

		if(ajaxRunning) {
			return false;
		}
		ajaxRunning = true;
		
		$.ajax({
			url : "/user/dengon/edit/" + url,
			type : "POST",
			data : data,
		}).done(function(result) {
			if (result.succeeded) {
				alert(result.message);
				if(result.reload){
					location.reload();
				}else{
					$('button.edit_cancel').click();
					$('.current-box').click();
				}
			}else{
				showErrorMessage(result.message, modal, result.errors);
			}
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function(result) {
			ajaxRunning = false;
		});
	});

	// キャンセルボタン押下時に入力内容をクリアしてモーダルを閉じる
	$('#dengonCreateModal').on('click', '[data-dismiss="modal"]', function() {
		$('#dengonCreateModal').find('input[type="text"], textarea').val("");
	});

	// メッセージモーダルクローズ時に入力内容があれば閉じない
	$(document).on('hide.bs.modal', '#dengonCreateModal', function() {
		if ($('input[name="dto.title"]').val().trim().length > 0 || $('textarea[name="dto.body"]').val().trim().length > 0) {
			return false;
		}

		$(this)
			.find('input[type="text"]').val("").end()
			.find('.user_selectbtn, .msgText').remove().end();

		$('.isAnkenRow, .isSaibanRow, .isKokyakuRow').hide();
	});

	/* 宛先モーダルクローズ時にモダールの中身を削除する */
	$(document).on('hide.bs.modal', '#userSelectModal', function() {
		$(this).find('.user_selectbtn, select option').remove().end();
	});

	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	/* 業務履歴 */
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	//メッセージ作成画面を開く
	$(document).on('click', '.sendMsg[data-target="#dengonCreateModal"]', function() {
		$('#dengonCreateModal').modal('show');
		var gyomuHistorySeq = $(this).parents('tr').attr('data-id');
		if(gyomuHistorySeq ==null ){
			gyomuHistorySeq = $(this).attr("data-gyomu-history-seq");
		}

		$.ajax({
			url : "/user/dengon/edit/gyomuhistory",
			type : "POST",
			data : {"gyomuhistorySeq" : gyomuHistorySeq,},
		}).done(function(val) {
			// モーダルの表示
			$(".dengon-create-modal-body").html(val);
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function() {
		});
	});
	
	//メッセージ作成画面を開く
	$(document).on('click', '.createMsg[data-target="#dengonCreateModal"]', function() {
		$('#dengonCreateModal').modal('show');
		var gyomuHistorySeq = $(this).parents('tr').attr('data-id');
		if(gyomuHistorySeq ==null ){
			gyomuHistorySeq = $(this).attr("data-gyomu-history-seq");
		}

		$.ajax({
			url : "/user/dengon/edit/gyomuhistory",
			type : "POST",
			data : {"gyomuhistorySeq" : gyomuHistorySeq,},
		}).done(function(val) {
			// モーダルの表示
			$(".dengon-create-modal-body").html(val);
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function() {
		});
	});
	
	benzo.global.dengonModal.atesakiSearchTimeout = function(){};
	benzo.global.dengonModal.atesakiSearchInterval = 500;
	$(document).on('focus', '.atesakiContainer .atesakiSearch', function(e) {
		$('.inline_input_container').addClass('atesaki_focus');
	});
	$(document).on('blur', '.atesakiContainer .atesakiSearch', function(e) {
		$('.inline_input_container').removeClass('atesaki_focus');
	});
	
	$(document).on('keydown', '.atesakiContainer .atesakiSearch', function(e) {
		// EnterKey時の処理
		if(atesakiSearchKeyAction(e.which)) {
			return false;
		}
		// 処理中のアイコンを表示
		const atesakiSearch = (searchInput) => {
			const userList = benzo.global.dengonModal.canSendUser;
			const searchVal = $(searchInput).val();
			const selectedUser = getAtesakiUserSeqList();
			const searchResult = userList.filter(user => {
				// 選択中のユーザーを除外
				if(selectedUser.indexOf(user.accountSeq) !== -1) {
					return;
				}
				// 名称検索
				if(user.accountName.indexOf(searchVal) !== -1 || user.accountNameKana.indexOf(searchVal) !== -1) {
					return user;
				}
			});
			if(searchVal == ""){
				$('.atesakiPoperObj').popover('dispose');
			} else {
				setUpAtesakiPoper($(searchInput).data("target"), searchResult);
			}
		};
		
		// 連続入力時に検索を行わないようにtimeout処理をする
		clearTimeout(benzo.global.dengonModal.atesakiSearchTimeout);
		benzo.global.dengonModal.atesakiSearchTimeout = setTimeout(atesakiSearch, benzo.global.dengonModal.atesakiSearchInterval, $(this));
	});
	
	<!--/* 宛先Poperのセットアップ処理 */-->
	function setUpAtesakiPoper(target, atesakiList) {
		$(target).popover('dispose');
		let poperOption = {
				html: true,
				placement: 'bottom',
				offset: "200,0",
				template:  '<div class="popover mt-1" role="tooltip"><div class="popover-body"></div></div>',
		};
		if(atesakiList.length < 1) {
			poperOption.template = '<div class="popover mt-1" role="tooltip"><div class="popover-body py-2"></div></div>',
			poperOption.content = '<div class="popover_info">該当するアカウントが見つかりません</div>'
		} else {
			poperOption.content = function() {
				let contents = $('<div class="atesaki_poper atesakiPoper"></div>');
				let appendList = atesakiList.slice(0,10).map((user, i) => {
					
					let button = $('<button class="btn add_user addUser"></button>')
						.data("accountSeq", user.accountSeq)
						.attr("data-account-seq", user.accountSeq)
						.text(user.accountName);
					
					if(i === 0) {
						// 最初の要素
						button.attr("aria-selected", true);
					} else {
						button.attr("aria-selected", false);
					}
					
					return button;
				});
				contents.append(appendList);
				return contents;
			}
		}
		$(target).popover(poperOption);
		$(target).popover('show');
	}
	
	<!--/* キーアクションを定 */-->
	function atesakiSearchKeyAction(keyCode) {
		
		let breakFlg = false;
		const poperFocusUserBtn = $('.atesakiPoper .addUser[aria-selected=true]');
		switch (keyCode){
			case void 0:
				// キーボード入力ではないが呼び出された場合
				breakFlg = true;
				break;
			case 13:
				// Enter処理
				poperFocusUserBtn.trigger("click");
				breakFlg = true;
				break;
			case 38:
				// ↑処理
				if(poperFocusUserBtn.length && poperFocusUserBtn.prev().length) {
					poperFocusUserBtn.attr("aria-selected", false)
					.prev().attr("aria-selected", true);
				}
				breakFlg = true;
				break;
			case 40:
				// ↓処理
				if(poperFocusUserBtn.length && poperFocusUserBtn.next().length) {
					poperFocusUserBtn.attr("aria-selected", false)
					.next().attr("aria-selected", true);
				}
				if(!poperFocusUserBtn.length) {
					$('.atesakiPoper .addUser:first').attr("aria-selected",true);
				}
				breakFlg = true;
				break;
			default:
				break;
		}
		return breakFlg;
	}
	
	<!--/* focus時に検索押下処理 */-->
	$(document).on('focus', '.atesakiContainer .atesakiSearch', function(e) {
		let keyEvent = $.Event('keydown');
		keyEvent.which = 999;
		keyEvent.keyCode = 999
		$(this).trigger(keyEvent);// 存在しないキーコードで検索処理を発火させる。
	});
	
	<!--/* 検索結果ボタン押下処理 */-->
	$(document).on("click", ".atesakiPoper .addUser", function() {
		$(this).attr("aria-selected", false);
		const poper = $(this).closest(".popover");
		$('.addBeforeTarget').before($(this));
		$('.atesakiSearch').val('');
		$('.atesakiPoperObj').popover('dispose');
		
		// 宛先部署選択ポップオーバーに削除したユーザーを追加
		if($(".atesakiSelectPoper").length){
			$("#atesakliPoperBushoSelect").trigger("change");
		}
	});
	
	<!--/* 宛先から削除する処理 */-->
	$(document).on("click", ".atesakiContainer .addUser", function() {
		$('.atesakiPoperObj').popover('dispose');
		$(this).remove();

		// 宛先部署選択ポップオーバーに削除したユーザーを追加
		if($(".atesakiSelectPoper").length){
			$("#atesakliPoperBushoSelect").trigger("change");
		} 
		
	});

	<!--/* popoverを閉じる */-->
	$(document).on('click touchend', function(e) {
		if (!$(e.target).closest('.popover').length && !$(e.target).is(".atesakiSearch")) {
			$('.atesakiPoperObj').popover('dispose');
		}
	});
	
	<!--/* 宛先のアカウントSEQを取得 */-->
	function getAtesakiUserSeqList() {
		let data = [];
		$('.atesakiContainer .addUser').each(function(i, btn){
			data.push($(btn).data("accountSeq"));
		})
		return data;
	};
	
	/** 検索ボタン押下 */
	$(document).on("click", "#atesakiListPoper", function(){

		let target = $(this);
		const modal = $(this).closest(".modal");
		const relatedAnkenIdList = modal.find(".isAnkenRow[data-anken-id]").get().map((obj) => $(obj).data("ankenId"));
		
		// 宛先リストのテンプレートを取得
		$.ajax({
			url : "/user/dengon/edit/getAtesakiListPopTemplate",
			type : "POST",
			data : {ankenId: String(relatedAnkenIdList)},
		}).done(function(result) {
			benzo.global.dengonModal.atesakiPoperListTemplate = result;
			setUpAtesakiListPoper(target);
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function() {
		});
		
	});
	
	/** 宛先部署選択ポップオーバー設定 */
	function setUpAtesakiListPoper(target) {
		$(target).popover('dispose');
		$(target).popover({
			html: true,
			placement: 'right',
			offset: "150,20",
			template:  '<div class="popover atesaki_list" role="tooltip"><div class="popover-header"></div><div class="popover-body"></div></div>',
			title: '宛先選択',
			content: function() {
				const atesaliPoper = $(benzo.global.dengonModal.atesakiPoperListTemplate).clone();
				return atesaliPoper;
			},
		});
		$(target).popover('show');
		$("#atesakliPoperBushoSelect").trigger("change");
	};
	
	/** 部署切り替え */
	$(document).on("change", "#atesakliPoperBushoSelect", function() {
		
		const selectVal = $(this).val();
		const userList = benzo.global.dengonModal.canSendUser;
		const selectedUser = getAtesakiUserSeqList();
		
		const shozokuUser = userList.filter(user => {
			// 選択中のユーザーを除外
			if(selectedUser.indexOf(user.accountSeq) !== -1) {
				return;
			}
			
			if (selectVal.startsWith("ankenOption")) {
				if ($(this).find("option[value='"+ selectVal +"']").data("accountSeq").indexOf(user.accountSeq) !== -1) {
					return user;
				}
			} else {
				// 部署所属ユーザーのみに絞り込み
				if(user.bushoShozoku.indexOf(selectVal) !== -1) {
					return user;
				}
			}
			
		});
		
		let appendList = shozokuUser.map((user, i) => {
			return button = $('<button class="btn add_user addUser"></button>')
				.data("accountSeq", user.accountSeq)
				.attr("data-account-seq", user.accountSeq)
				.text(user.accountName);
		});
		
		$(".atesakiSelectPoper .userList").html(appendList);
	})
	
	/** ユーザー追加 */
	$(document).on("click", ".atesakiSelectPoper .addUser", function(e) {
		$('.addBeforeTarget').before($(this));
	})
	
	/** 全員追加 */
	$(document).on("click", ".atesakiSelectPoper .addAll", function(e) {
		$('.addBeforeTarget').before($(".atesakiSelectPoper .addUser"));
	})
	
	/** 宛先部署選択ポップオーバーを閉じる */
	$(document).on('click touchend', function(e) {
		if (!$(e.target).closest('.atesaki_list').length && !$(e.target).closest('#atesakiListPoper').length && !$(e.target).closest('.inline_input_container').length && !$(e.target).is('.addUser')) {
			$('#atesakiListPoper').popover('hide');
		}
	});
	
	/** 顧客紐付け */
	benzo.global.dengonModal.customerSearchTimeout = function(){};
	benzo.global.dengonModal.customerSearchInterval = 500;
	$(document).on("change keydown paste cut", "#customerSearchInput", function(e) {
		// EnterKey時の処理
		if(customerSearchKeyAction(e.which)) {
			return false;
		}
		// 連続入力時に検索を行わないようにtimeout処理をする
		clearTimeout(benzo.global.dengonModal.customerSearchTimeout);
		benzo.global.dengonModal.customerSearchTimeout = setTimeout(customerSearchAjax, benzo.global.dengonModal.customerSearchInterval, $(this));
	});
	
	<!--/** 顧客検索Ajax */-->
	function customerSearchAjax(searchInputObj) {
		benzo.global.dengonModal.customerSearchAjaxId = new Date().getTime().toString();
		const searchVal = $(searchInputObj).val();
		const selectedCustomerId = getSelectedCustomerId();

		let param = [
			{name : "searchText", value : searchVal},
			{name : "exclusionCustomerIdList", value : selectedCustomerId},
			{name : "uuid", value : benzo.global.dengonModal.customerSearchAjaxId},
		]
		
		$.ajax({
			url : "/user/dengon/edit/searchCustomer",
			type : "POST",
			data : param,
		}).done(function(result) {
			// 最後に実行されたajaxリクエストのみ有効にする
			if(result.uuid != benzo.global.dengonModal.customerSearchAjaxId) {
				console.log("NO LAST AJAX RESPONSE");
				return;
			}
			
			if(searchVal == ""){
				$($(searchInputObj).data("target")).popover('dispose');
			} else {
				setUpCustomerSearchPopUp($(searchInputObj).data("target"), result.customerList);
			}
			
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function() {
		});
	}
	
	<!--/** キーアクションを定義 */-->
	function customerSearchKeyAction(keyCode) {
		
		let breakFlg = false;
		const poperFocusCustomerBtn = $('.customerSearchPoper .addCustomer[aria-selected=true]');
		switch (keyCode){
			case void 0:
				// キーボード入力ではないが呼び出された場合
				breakFlg = true;
				break;
			case 13:
				// Enter処理
				poperFocusCustomerBtn.trigger("click");
				breakFlg = true;
				break;
			case 38:
				// ↑処理
				if(poperFocusCustomerBtn.length && poperFocusCustomerBtn.prev().length) {
					poperFocusCustomerBtn.attr("aria-selected", false)
					.prev().attr("aria-selected", true);
				}
				breakFlg = true;
				break;
			case 40:
				// ↓処理
				if(poperFocusCustomerBtn.length && poperFocusCustomerBtn.next().length) {
					poperFocusCustomerBtn.attr("aria-selected", false)
					.next().attr("aria-selected", true);
				}
				if(!poperFocusCustomerBtn.length) {
					$('.customerSearchPoper .addCustomer:first').attr("aria-selected",true);
				}
				breakFlg = true;
				break;
			default:
				break;
		}
		return breakFlg;
	}
	
	<!--/** 顧客Poperのセットアップ処理 */-->
	function setUpCustomerSearchPopUp(target, customerList) {
		$(target).popover('dispose');
		let poperOption = {
				html: true,
				placement: 'bottom',
				offset: "800,0",
				template: '<div class="popover customerSearchPoper my-2" role="tooltip"><div class="popover-body"></div></div>',
		};
		if (customerList.length < 1) {
			poperOption.template = '<div class="popover customerSearchPoper mt-1" role="tooltip"><div class="popover-body py-2"></div></div>',
			poperOption.content = '<div class="popover_info">該当する顧客が見つかりません</div>'
		} else {
			poperOption.content = function() {
				let contents = $('<div class="customer_search"></div>');
				let appendList = customerList.map((customer, i) => {
					let button = $('<button class="btn add_customer addCustomer" data-boundary="window" data-container="body" data-toggle="tooltip" data-trigger="hover" title="選択解除"></button>')
						.data("id", customer.customerId)
						.attr("data-id", customer.customerId)
						.text(customer.customerName + " (" + customer.customerIdDisp + ")");
					
					if(i === 0) {
						// 最初の要素
						button.attr("aria-selected", true);
					} else {
						button.attr("aria-selected", false);
					}
					
					return button;
				});
				contents.append(appendList);
				return contents;
			}
		}
		$(target).popover(poperOption);
		$(target).popover('show');
	};
	
	<!--/** 顧客Poper内ボタンの押下 */-->
	$(document).on("click", ".customerSearchPoper .addCustomer", function() {
		if ($("#customerSelectedArea .addCustomer").length) {
			alert("選択可能な顧客は1件のみです。");
		} else {
			$('#customerSearchInput').val("");
			$("#customerSearchPoperObj").popover('dispose');
			$("#customerSelectedArea").append($(this));
			$(".addCustomer").tooltip();
		}
		$("#customerSearchArea").addClass("hidden");
	});

	<!--/** 顧客選択中を外す */-->
	$(document).on("click", "#customerSelectedArea .addCustomer", function() {
		$(".addCustomer").tooltip('hide');
		$(this).remove();
		$("#customerSearchArea").removeClass("hidden");
	});
	
	<!--/** 選択中顧客IDを取得する */-->
	function getSelectedCustomerId(){
		let customerId = $.map($("#customerSelectedArea .addCustomer"), (customer, i) => {
			return $(customer).data("id");
		});
		return customerId;
	};
	
	/** 宛先部署選択ポップオーバーを閉じる */
	$(document).on('click touchend', function(e) {
		if (!$(e.target).closest('.customer_search').length && !$(e.target).closest('#customerSelectedArea').length && !$(e.target).closest('#customerSearchInput').length && !$(e.target).is('.addCustomer')) {
			$("#customerSearchPoperObj").popover('hide');
		}
	});
	
})