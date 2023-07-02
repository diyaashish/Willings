package jp.loioz.app.common.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.common.constant.OnboardingConstant.Onboarding;

/**
 * オンボーディングのコントローラー
 */
@Controller
@RequestMapping(value = "user/onboarding")
public class OnboardingController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private final static String ONBOARDING_VIEW_PATH = "common/onboarding/onboardingModal::onboardingModal";

	/**
	 * 外部呼び出しの初期ログインOnboarding
	 *
	 * @return 初期ログインOnboarding
	 */
	@RequestMapping(value = "/firstOnboarding", method = RequestMethod.POST)
	public ModelAndView getFirstLoginOnboarding() {
		return getMyModelAndView(Onboarding.FIRST_LOGIN, ONBOARDING_VIEW_PATH, "onboards");
	}

	/**
	 * 外部呼び出し 複数のオンボーディングを一つスライドとして表示
	 *
	 * @param onboardings
	 * @return
	 */
	@RequestMapping(value = "/customize", method = RequestMethod.POST)
	public ModelAndView customize(@RequestParam(name = "onboardEnumCd") List<Onboarding> onboardings) {
		return getMyModelAndView(onboardings, ONBOARDING_VIEW_PATH, "onboards");
	}

}
