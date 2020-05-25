package com.example.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.server.security.SecurityConstants.HEADER_BEARER_TOKEN;
import static com.example.server.security.SecurityConstants.TOKEN_BEARER_PREFIX;


public class JWTAuthenticationFilter extends OncePerRequestFilter {

//    @Value("${HEADER_BEARER_TOKEN}")
//    private String HEADER_BEARER_TOKEN;

    @Autowired
    private JWTTokenHelper jwtTokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJWTFromRequest(request);

        if(StringUtils.hasText(jwt) && jwtTokenHelper.validate(jwt)) {
            String username = jwtTokenHelper.getUserUsernameFromJWT(jwt);
            List<String> privileges = jwtTokenHelper.getPrivilegesFromAccessToken(jwt);

            if(username != null && privileges != null) {
                Set<SimpleGrantedAuthority> authorities = new HashSet<>();

                for (String privilege : privileges) {
                    authorities.add(new SimpleGrantedAuthority(privilege));
                }
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);

    }


    private String getJWTFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(HEADER_BEARER_TOKEN);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_BEARER_PREFIX)){
            return bearerToken.substring(7);
        }

        return null;
    }
}
