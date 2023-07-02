//Bootstrap用 =========================================

$(function () {
  $('[data-toggle="tooltip"]').tooltip()
})
$(function () {
  $('[data-toggle="popover"]').popover()
})

//予定表 ユーザー選択のタブ
$(document).ready(function () {
  $(".miniuserselect_tab > dt").click(function () {
    if ($("+dd", this).is(":not(:visible)")) {
      $(".miniuserselect_tab > dt").removeClass("selected");
      $(".miniuserselect_tab > dd").hide();
      $("+dd", this).fadeIn();
      $(this).addClass("selected");
    }
  });
});

//予定表 ユーザー選択のリスト
$(document).ready(function () {
  $(".user_list_accordion > dt.selected + dd").show();
});
$(document).ready(function () {
  $(".user_list_accordion > dt").click(function () {
    if ($("+dd", this).css("display") === "none") {
      $(this).addClass("selected");
      $("+dd", this).slideDown("fast");
      return false;
    } else {
      $(this).removeClass("selected");
      $("+dd", this).slideUp("fast");
    }
  });
});

/** 業務履歴Filter */
$(document).ready(function () {
	let checkedName = $(".gyomuHistoryHeader__user").find(".isCheckedName").text();
	let checkedId = $(".gyomuHistoryHeader__user").find(".isCheckedId").text();
	if (checkedId) {
		checkedId = ' (' + checkedId + ')';
	}
	$(".gyomuHistoryHeader__user > dt").text(checkedName + checkedId);
	//ヘッダーメニューのアコーディオン
	$(".gyomuHistoryHeader__user > dt").click(function () {
		if ($(".gyomuHistoryHeader__user > dd").css("display") === "none") {
			$(".gyomuHistoryHeader__user > dd").slideDown("fast");
			$(this).addClass("selected");
			return false;
		} else {
			$.when(
				$(".gyomuHistoryHeader__user > dd").slideUp("fast")
			).done(function () {
				$(".gyomuHistoryHeader__user > dt").removeClass("selected")
			});
			return false;
		}
	});
	
	$(document).on("click", function(e){
		if ($('.gyomuHistoryHeader__user').find('dt').hasClass('selected')) {
			if (!$(event.target).closest('a').length) {
				if ($(".gyomuHistoryHeader__user > dd").css("display") === "block") {
					$.when(
						$(".gyomuHistoryHeader__user > dd").slideUp("fast")
					).done(function () {
						$(".gyomuHistoryHeader__user > dt").removeClass("selected")
					});
					return false;
				}
			}
		}
	});
	
});

$(function(){
	$(document).on("click", function(e){
		
		// メインメニュー開閉
		if ($(e.target).closest(".mainMenuFnc").length) {
			// 選択中のメニューを非選択状態に変更
			_removeSeletedGlobalMenu();
			// 該当のメニューを選択状態に変更
			$(".mainMenuFnc").addClass('global_header_nav__seleted');
			_mainMenuOpCl();
			return false;
		}
		// 追加メニュー開閉
		if ($(e.target).closest(".headerAdditionFnc").length) {
			// 選択中のメニューを非選択状態に変更
			_removeSeletedGlobalMenu();
			// 該当のメニューを選択状態に変更
			$(".headerAdditionFnc").addClass('global_header_nav__seleted');
			_additionMenuOpCl();
			return false;
		}
		// 事務所設定メニュー開閉
		if ($(e.target).closest(".headerOfficeFnc").length) {
			// 選択中のメニューを非選択状態に変更
			_removeSeletedGlobalMenu();
			// 該当のメニューを選択状態に変更
			$(".headerOfficeFnc").addClass('global_header_nav__seleted');
			_tenantMenuOpCl();
			return false;
		}
		// アカウント設定メニュー開閉
		if ($(e.target).closest(".headerAccountFnc").length) {
			// 選択中のメニューを非選択状態に変更
			_removeSeletedGlobalMenu();
			// 該当のメニューを選択状態に変更
			$(".headerAccountFnc").addClass('global_header_nav__seleted');
			_accountMenuOpCl();
			return false;
		}
		// メールサブメニュー開閉
		if ($(e.target).closest(".msg_header_navi_sub > dt").length) {
			mailHeaderNaviSub();
			return false;
		}
		// ポップオーバーを閉じる
		if(!$(e.target).data('bs.popover') && !$(e.target).closest(".popover").length){
			popoverHideAction();
		}

		// 各メニューの開閉
		if ($('#headerScreenMenu').hasClass("smopen")) {
			// メインメニュー以外のクリックでメインメニューを閉じる
			if (!$(event.target).closest("#headerScreenMenu").length) {
				// 選択中のメニューを非選択状態に変更
				_removeSeletedGlobalMenu();
				_mainMenuOpCl();
			}
		} else if ($('#headerAdditionMenu').hasClass("smopen")) {
			// 追加メニュー以外のクリックで追加メニューを閉じる
			if (!$(event.target).closest("#headerAdditionMenu").length) {
				// 選択中のメニューを非選択状態に変更
				_removeSeletedGlobalMenu();
				_additionMenuOpCl();
			}
		} else if ($('#headerOfficeMenu').hasClass("smopen")) {
			// 事務所設定メニュー以外のクリックで設定メニューを閉じる
			if (!$(event.target).closest("#headerOfficeMenu").length) {
				// 選択中のメニューを非選択状態に変更
				_removeSeletedGlobalMenu();
				_tenantMenuOpCl();
			}
		} else if ($('#headerAccountMenu').hasClass("smopen")) {
			// アカウント設定メニュー以外のクリックで設定メニューを閉じる
			if (!$(event.target).closest("#headerAccountMenu").length) {
				// 選択中のメニューを非選択状態に変更
				_removeSeletedGlobalMenu();
				_accountMenuOpCl();
			}
		} else if($('.msg_header_navi_sub > dt').hasClass("selected")) {
			// メールサブメニュー以外のクリックでメールサブメニューを閉じる
			if (!$(event.target).closest(".msg_header_navi_sub").length) {
				mailHeaderNaviSub();
			}
		}
	});
});

/**
 * グローバルメニューの選択状態を解除
 */
function _removeSeletedGlobalMenu() {
	$(".global_header a").removeClass('global_header_nav__seleted');
}

/**
 * メインメニューの開閉
 */
function _mainMenuOpCl(){
	// メインメニュー表示・非表示
	$("#headerScreenMenu").toggleClass("smopen");
	// 追加メニューを非表示
	$("#headerAdditionMenu").removeClass("smopen")
	// 事務所設定メニューを非表示
	$("#headerOfficeMenu").removeClass("smopen");
	// アカウント設定メニューを非表示
	$("#headerAccountMenu").removeClass("smopen");
}
/**
 * 追加メニューの開閉
 */
function _additionMenuOpCl(){
	// メインメニュー表示・非表示
	$("#headerScreenMenu").removeClass("smopen");
	// 追加メニューを非表示
	$("#headerAdditionMenu").toggleClass("smopen")
	// 事務所設定メニューを非表示
	$("#headerOfficeMenu").removeClass("smopen");
	// アカウント設定メニューを非表示
	$("#headerAccountMenu").removeClass("smopen");
}
/**
 * 事務所設定メニューの開閉
 */
function _tenantMenuOpCl() {
	// メインメニューを非表示
	$("#headerScreenMenu").removeClass("smopen");
	// 追加メニューを非表示
	$("#headerAdditionMenu").removeClass("smopen")
	// 事務所設定メニュー表示・非表示
	$("#headerOfficeMenu").toggleClass("smopen");
	// アカウントメニューを非表示
	$("#headerAccountMenu").removeClass("smopen");
}
/**
 * アカウントメニューの開閉
 */
function _accountMenuOpCl() {
	// メインメニューを非表示
	$("#headerScreenMenu").removeClass("smopen")
	// 追加メニューを非表示
	$("#headerAdditionMenu").removeClass("smopen")
	// 事務所設定メニューを非表示
	$("#headerOfficeMenu").removeClass("smopen");
	// アカウント設定メニュー表示・非表示
	$("#headerAccountMenu").toggleClass("smopen");
}

/**
 * 案件サイドメニュー：裁判一覧表示
 */
$(document).on("mouseenter", ".sidebarSubItemsOpCl", function(){
	$(".nav_sidebar_c_a__menu_list_items").removeClass("hidden");
});
$(document).on("mouseleave", ".sidebarSubItemsOpCl", function(){
	$(".nav_sidebar_c_a__menu_list_items").addClass("hidden");
});

/**
 * メールサブメニューのアコーディオンの開閉
 */
function mailHeaderNaviSub() {
	// 開く
	if ($(".msg_header_navi_sub > dd").css("display") === "none") {
		$(".msg_header_navi_sub > dd").slideDown("fast");
		$(".msg_header_navi_sub > dt").addClass("selected");
		return false;
	} else {
		// 閉じる
		$.when(
			$(".msg_header_navi_sub > dd").slideUp("fast")
		).done(function () {
			$(".msg_header_navi_sub > dt").removeClass("selected")
		});
		return false;
	}
}

/** ポップオーバーを閉じるアクション */
function popoverHideAction() {
	const popover = $("[data-outside='close']");
	popover.each(function(i) {
		$(this).popover("hide");
	});
}

/** 対応ブラウザの判定 */
function judgeBrowser(){
	let userAgent = window.navigator.userAgent.toLowerCase();
	if (userAgent.indexOf('iphone') != -1
			|| userAgent.indexOf('ipad') != -1
			|| userAgent.indexOf('android') != -1) {
		// モバイル端末は非表示
	} else {
		// PC
		// 複数ブラウザに対応
		// （edgeはchromeの文字列を含むため、chromeより先に判定）
		// （chromeはsafariの文字列を含むため、safariより先に判定）
		if(userAgent.indexOf('edg') != -1) {
			// ユーザーは対応ブラウザを使用
		} else if(userAgent.indexOf('chrome') != -1) {
			// ユーザーは対応ブラウザを使用
		} else if(userAgent.indexOf('macintosh') != -1 && userAgent.indexOf('safari') != -1) {
			// ユーザーは対応ブラウザを使用
		} else {
			// 非対応
			$(".browser_message").show();
		}
	}
};

/** MacでSafariブラウザを使用しているかを判定 */
function isMacSafari(){
	let userAgent = window.navigator.userAgent.toLowerCase();
	if (userAgent.indexOf('iphone') != -1
			|| userAgent.indexOf('ipad') != -1
			|| userAgent.indexOf('android') != -1) {
		// モバイル端末
		return false;
	} else {
		// PC
		// 複数ブラウザに対応
		// （edgeはchromeの文字列を含むため、chromeより先に判定）
		// （chromeはsafariの文字列を含むため、safariより先に判定）
		if(userAgent.indexOf('edg') != -1) {
			return false;
		} else if(userAgent.indexOf('chrome') != -1) {
			return false;
		} else if(userAgent.indexOf('macintosh') != -1 && userAgent.indexOf('safari') != -1) {
			return true;
		} else {
			return false;
		}
	}
};
