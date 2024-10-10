package com.thinker.cloud.security.component;

import com.thinker.cloud.security.utils.SecurityMessageSourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * @author admin
 */
@Slf4j
public class PreAuthenticationChecks implements UserDetailsChecker {

    private final MessageSourceAccessor messages = SecurityMessageSourceUtils.getAccessor();

    @Override
    public void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            log.debug("User account is locked");

            throw new LockedException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.locked",
                    "User account is locked"));
        }

        if (!user.isAccountNonExpired()) {
            log.debug("User account is expired");

            throw new AccountExpiredException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.expired",
                    "User account has expired"));
        }

        if (!user.isEnabled()) {
            log.debug("User account is disabled");

            throw new DisabledException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.disabled",
                    "User is disabled"));
        }
    }
}
