package nextstep.subway.auth.domain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import nextstep.subway.auth.application.AuthService;
import nextstep.subway.auth.application.AuthorizationException;
import nextstep.subway.auth.infrastructure.AuthorizationExtractor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
	private final AuthService authService;

	public LoginCheckInterceptor(AuthService authService) {
		this.authService = authService;
	}

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
		LoginCheck loginCheck = ((HandlerMethod)handler).getMethodAnnotation(LoginCheck.class);
		if (loginCheck == null) {
			return true;
		}

		LoginMember loginMember = authService.findMemberByToken(AuthorizationExtractor.extract(req));
		if (!loginMember.isLogin()) {
			throw new AuthorizationException();
		}

		return true;
	}
}
