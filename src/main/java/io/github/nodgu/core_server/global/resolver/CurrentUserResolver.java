package io.github.nodgu.core_server.global.resolver;

import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.global.annotation.CurrentUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserResolver implements HandlerMethodArgumentResolver {
    
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
               parameter.getParameterType().equals(User.class);
    }
    
    @Override
    public Object resolveArgument(MethodParameter parameter, 
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, 
                                WebDataBinderFactory binderFactory) throws Exception {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return authentication.getPrincipal();
        }
        
        return null;
    }
} 