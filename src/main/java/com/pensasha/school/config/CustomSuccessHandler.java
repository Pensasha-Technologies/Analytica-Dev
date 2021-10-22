package com.pensasha.school.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = this.determineTargetUrl(authentication);
        if (response.isCommitted()) {
            System.out.println("Can't redirect");
            return;
        }
        this.redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {

        String url = "";
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        ArrayList<String> roles = new ArrayList<String>();

        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }

        if (this.isAdmin(roles)) {
            url = "/adminHome";
        } else if (this.isCEO(roles)) {
            url = "/ceoHome";
        } else if (this.isOfficeAssistant(roles)) {
            url = "/officeAssistantHome";
        } else if (this.isFieldOfficer(roles)) {
            url = "/fieldOfficerHome";
        } else if (this.isPrincipal(roles)) {
            url = "/schools/principal";
        } else if (this.isDeputyPrincipal(roles)) {
            url = "/schools/deputyPrincipal";
        } else if (this.isDos(roles)) {
            url = "/schools/dosHome";
        } else if (this.isTeacher(roles)) {
            url = "/teacherHome";
        } else if (this.isBursar(roles)) {
            url = "/schools/bursarHome";
        } else if (this.isAccountsClerk(roles)) {
            url = "/schools/accountsClerkHome";
        }

        return url;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return this.redirectStrategy;
    }

    private boolean isPrincipal(List<String> roles) {
        return roles.contains("ROLE_PRINCIPAL");
    }

    private boolean isDeputyPrincipal(List<String> roles) {
        return roles.contains("ROLE_DEPUTYPRINCIPAL");
    }

    private boolean isTeacher(List<String> roles) {
        return roles.contains("ROLE_TEACHER");
    }

    private boolean isAdmin(List<String> roles) {
        return roles.contains("ROLE_ADMIN");
    }

    private boolean isCEO(List<String> roles) {
        return roles.contains("ROLE_C.E.O");
    }

    private boolean isOfficeAssistant(List<String> roles) {
        return roles.contains("ROLE_OFFICEASSISTANT");
    }

    private boolean isFieldOfficer(List<String> roles) {
        return roles.contains("ROLE_FIELDOFFICER");
    }

    private boolean isDos(List<String> roles) {
        return roles.contains("ROLE_D.O.S");
    }

    private boolean isBursar(List<String> roles) {
        return roles.contains("ROLE_BURSAR");
    }

    private boolean isAccountsClerk(List<String> roles) {
        return roles.contains("ROLE_ACCOUNTSCLERK");
    }
}