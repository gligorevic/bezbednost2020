package com.example.AuthService.security;

import com.example.AuthService.domain.User;
import com.example.AuthService.repository.UserRepository;
import com.example.AuthService.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

import static com.example.AuthService.security.SecurityConstants.HEADER_BEARER_TOKEN;
import static com.example.AuthService.security.SecurityConstants.TOKEN_BEARER_PREFIX;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTokenHelper jwtTokenHelper;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJWTFromRequest(httpServletRequest);

        System.out.println("U filteru sam http");
        System.out.println(jwt);

        if(StringUtils.hasText(jwt) && jwtTokenHelper.validate(jwt)) {
            String username = jwtTokenHelper.getUserUsernameFromJWT(jwt);
            List<String> privileges = jwtTokenHelper.getPrivilegesFromAccessToken(jwt);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if(username != null && privileges != null) {
                Set<SimpleGrantedAuthority> authorities = new HashSet<>();

                for (String privilege : privileges) {
                    authorities.add(new SimpleGrantedAuthority(privilege));
                }

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }


    private String getJWTFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(HEADER_BEARER_TOKEN);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_BEARER_PREFIX)){
            return bearerToken.substring(7);
        }

        return null;
    }
}
