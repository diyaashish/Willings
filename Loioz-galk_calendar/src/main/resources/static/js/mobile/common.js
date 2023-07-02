/**---------------------------------
 * スマホ画面共通JS
 ---------------------------------*/

benzo.global.common.mobile = {
	calc1vwpx : function() {return window.innerWidth * 0.01},
	calc1vhpx : function() {return window.innerHeight * 0.01},
	calc1vminpx : function(){
		const vwpx = window.innerWidth * 0.01;
		const vhpx = window.innerHeight * 0.01;
		return vwpx > vhpx ? vhpx : vwpx;
	},
	calc1vmaxpx : function() {
		const vwpx = window.innerWidth * 0.01;
		const vhpx = window.innerHeight * 0.01;
		return vwpx > vhpx ? vwpx : vhpx;
	},
}

benzo.global.common.mobile.resizeTimeout = function(){};
benzo.global.common.mobile.resizeInterval = 500;
$(function(){
	/* アドレスバー対応 */
	const resizeViewPort = () => {
		// ビューポート値を算出する
		vwpx = benzo.global.common.mobile.calc1vwpx();
		vhpx = benzo.global.common.mobile.calc1vhpx();
		vminpx = benzo.global.common.mobile.calc1vminpx();
		vmaxpx = benzo.global.common.mobile.calc1vmaxpx();
		// カスタム変数--vhの値をドキュメントのルートに設定
		document.documentElement.style.setProperty('--vh', `${vhpx}px`);
		document.documentElement.style.setProperty('--vw', `${vwpx}px`);
		document.documentElement.style.setProperty('--vmin', `${vminpx}px`);
		document.documentElement.style.setProperty('--vmax', `${vmaxpx}px`);
	};
	resizeViewPort();

	// resizeイベント
	$(window).on("resize", function() {
		
		// リサイズイベントの連続処理を制御（0.5秒以内に発生したイベントは上書きする）
		clearTimeout(benzo.global.common.mobile.resizeTimeout);
		benzo.global.common.mobile.resizeTimeout = setTimeout(resizeViewPort, benzo.global.common.mobile.resizeInterval);
	});
});

<!-- /*-スマホ グローバルメニュー表示*/ -->
$(function(){
	$("#smartHeaderMenuOpen").on("click", function(){
		$(".bg_smart_gloval_menu").show("fade");
		$(".smart_gloval_menu").show("slide", {direction: "right" }, 500);
	});
	$("#smartHeaderMenuClose").on("click", function(){
		$(".bg_smart_gloval_menu").hide("fade");
		$(".smart_gloval_menu").hide("slide", {direction: "right" }, 500);
	});
	$(".bg_smart_gloval_menu").on("click", function() {
		$("#smartHeaderMenuClose").trigger("click");
	});
});

// 横画面制御のjs
$(function() {
	
	// 初期ロード時も確認する
	orientationCheck();
	
	/* 向き切り替え時の処理 */
	$(window).on("orientationchange", function(e) {
		orientationCheck();
	});
	
	function orientationCheck(){
		//画面の向きを 0,90,180,-90 のいずれかで取得
		const orientation = window.orientation;
		switch (orientation) {
		case 0:
			$("#directionControll").hide();
			break;
		case 90:
			$("#directionControll").attr("data-rotate", "left")
			$("#directionControll").show();
			break;
		case -90:
			$("#directionControll").attr("data-rotate", "right")
			$("#directionControll").show();
			break;
		case 180:
			$("#directionControll").hide();
			break;
		default:
			$("#directionControll").show();
			break;
		}
	};
});
