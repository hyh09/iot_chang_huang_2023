package org.thingsboard.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Optional;

/**
 * 自动注入创建人，更新人
 *
 * @author wwj
 * @since 2021.10.21
 */
@Component
@Slf4j
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    /**
     * 获得当前用户
     *
     * @return 当前用户
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            var es = (SecurityUser) authentication.getPrincipal();
            return Optional.of(es.getId().toString());
        } else {
            return Optional.empty();
        }
    }
}
