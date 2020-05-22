package com.example.Zuul;

import com.example.Zuul.client.AuthClient;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Component
public class AuthFilter extends ZuulFilter {

    @Autowired
    private AuthClient authClient;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        //Dodati logiku da vraca null kad je generisan access token
        if(request.getHeader("Authorization") == null) {
            return null;
        }

        String accessToken = authClient.verify(request.getHeader("Authorization"));

        System.out.println("U zuul filteru");
        System.out.println(accessToken);

        ctx.addZuulRequestHeader("Auth", accessToken);

        return null;
    }
}
