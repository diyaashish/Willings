/**
 * 部署所属用のjsファイル
 */
$(function(){
	/* 所属一覧のユーザーをクリックで追加/削除する */
	$('.targetModalBody').on("click", 'button.user_selectbtn', function() {
		/* 所属一覧ならfalse : 追加一覧ならtrue */
		if ($(this).parent('div').hasClass('is_userlist')) {
			// 所属一覧(左)へ
			$('div.is_participantlist').append("<button class='user_selectbtn' data-seq='" + $(this).attr('data-seq') + "' data-type='" + $(this).attr('data-type') + "'>" + $(this).text() + "</button>");
		} else {
			// 追加一覧(右)へ
			if ($(this).attr('data-type') == $('.targetModalBody').find('select.changeSelect').val()) {
				$('div.is_userlist').append("<button class='user_selectbtn' data-seq='" + $(this).attr('data-seq') + "' data-type='" + $(this).attr('data-type') + "'>" + $(this).text() + "</button>");
			}
		}
		$(this).remove();
	});

	/* 全員追加ボタン押下時 */
	$('.targetModalBody').on("click", 'a.userSelectAll', function() {
		$('.is_userlist').find('button').each(
				function () {
					$('div.is_participantlist').append("<button class='user_selectbtn' data-seq='" + $(this).attr('data-seq') + "' data-type='" + $(this).attr('data-type') + "'>" + $(this).text() + "</button>");
					$(this).remove();
				}
			);
	});

	/* 全員削除ボタン押下時 */
	$('.targetModalBody').on("click", 'a.userRemoveAll', function() {
		$('.is_participantlist').find('button').each(
				function () {
					if ($(this).attr('data-type') == $('.targetModalBody').find('select.changeSelect').val()) {
						$('div.is_userlist').append("<button class='user_selectbtn' data-seq='" + $(this).attr('data-seq') + "' data-type='" + $(this).attr('data-type') + "'>" + $(this).text() + "</button>");
					}
					$(this).remove();
				}
			);
	});

	/* セレクトボックス変更時にアカウント種別を元に所属アカウント一覧を生成する */
	$('.targetModalBody').on('change', 'select.changeSelect', function() {
		var accountType = $(this).val();
		var accountSeqList = getSelectedAccountSeqList();

		$.ajax({
			url : "selectAccountList",
			type : "POST",
			data : {"accountType" : accountType, "accountSeq" : String(accountSeqList)},
			dataType : 'json',
		}).done(function(data) {
			$('div.is_userlist').children().remove();
			$.each(data, function(i, e) {
				$('div.is_userlist').append("<button class='user_selectbtn' data-seq='" + e.accountSeq + "' data-type='" + e.accountType + "'>" + e.accountNameSei +" " + e.accountNameMei + "</button>");
			});
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function() {
		});
	});

	/* 更新ボタン押下時 */
	let ajaxShozokuRunning = false;
	$('.targetModalBody').on('click', '#shozokuUpdate', function() {
		// 2重押下防止
		if(ajaxShozokuRunning){
			return false;
		}
		ajaxRunning = true;
		let modal = $(this).closest('.modal');
		var params = [];
		params.push(
				{name : "tempId", value : $('#modalTempId').val()},
				{name : "accountSeqList", value : getSelectedAccountSeqList()});

		$.ajax({
			type : "POST",
			url : "shozokuUpdate",
			data : params,
		})
		.done(function(result) {
			if(result.succeeded){
				alert('更新しました');
				modal.modal("hide");
				location.reload();
			} else {
				showErrorMessage(result.message, modal, result.errors);
			}
		})
		.fail(function() {
			showErrorMessage(/*[[#{E00013}]]*/ "error", modal);
		})
		.always(function(){
			ajaxRunning = false;
		});
	});
})

/* 現在選択されている所属のアカウントSEQリストを取得する関数 */
function getSelectedAccountSeqList() {
	let accountSeqList = [];
	$('.targetModalBody').find('div.is_participantlist').find('button').each(
		function () {
			accountSeqList.push($(this).attr('data-seq'));
		}
	);
	return accountSeqList;
}
