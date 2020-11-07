package com.pensasha.school.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		String targetUrl = determineTargetUrl(authentication);

		if (response.isCommitted()) {
			System.out.println("Can't redirect");
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(Authentication authentication) {
		String url = "";

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		List<String> roles = new ArrayList<String>();

		for (GrantedAuthority a : authorities) {
			roles.add(a.getAuthority());
		}

		if (isAdmin(roles)) {
			url = "/adminHome";
		}else if (isCEO(roles)) {
			url = "/ceoHome";
		}else if (isOfficeAssistant(roles)) {
			url = "/officeAssistantHome";
		}else if (isFieldOfficer(roles)) {
			url = "/fieldOfficerHome";
		} else if (isPrincipal(roles)) {
			url = "/schools/principal";
		} else if (isDeputyPrincipal(roles)) {
			url = "/schools/deputyPrincipal";
		}else if (isDos(roles)) {
			url = "/schools/dosHome";
		} else if (isTeacher(roles)) {
			url = "/teacherHome";
		} else if(isBursar(roles)) {
			url = "/schools/bursarHome";
		} else if(isAccountsClerk(roles)) {
			url = "/schools/accountsClerkHome";
		}

		return url;
	}

	@Override
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	@Override
	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	private boolean isPrincipal(List<String> roles) {
		if (roles.contains("ROLE_PRINCIPAL")) {
			return true;
		}
		return false;
	}

	private boolean isDeputyPrincipal(List<String> roles) {
		if (roles.contains("ROLE_DEPUTYPRINCIPAL")) {
			return true;
		}
		return false;
	}

	private boolean isTeacher(List<String> roles) {
		if (roles.contains("ROLE_TEACHER")) {
			return true;
		}
		return false;
	}

	private boolean isAdmin(List<String> roles) {
		if (roles.contains("ROLE_ADMIN")) {
			return true;
		}
		return false;
	}

	private boolean isCEO(List<String> roles) {
		if (roles.contains("ROLE_C.E.O")) {
			return true;
		}
		return false;
	}

	private boolean isOfficeAssistant(List<String> roles) {
		if (roles.contains("ROLE_OFFICEASSISTANT")) {
			return true;
		}
		return false;
	}

	private boolean isFieldOfficer(List<String> roles) {
		if (roles.contains("ROLE_FIELDOFFICER")) {
			return true;
		}
		return false;
	}

	private boolean isDos(List<String> roles) {
		if (roles.contains("ROLE_D.O.S")) {
			return true;
		}
		return false;
	}

	private boolean isBursar(List<String> roles) {
		if (roles.contains("ROLE_BURSAR")) {
			return true;
		}
		return false;
	}

	private boolean isAccountsClerk(List<String> roles) {
		if (roles.contains("ROLE_ACCOUNTSCLERK")) {
			return true;
		}
		return false;
	}

}
