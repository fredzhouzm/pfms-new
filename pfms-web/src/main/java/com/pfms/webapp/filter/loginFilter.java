package com.pfms.webapp.filter;

import com.pfms.webapp.model.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Fred on 16/5/22.
 */
public class LoginFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if(isStaticResource(httpServletRequest) || isExclusionRequest(httpServletRequest)){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }else{
            Authentication authentication = (Authentication) httpServletRequest.getSession().getAttribute("session_authentication");
            if(authentication == null || authentication.isValid() == true){
                httpServletResponse.sendRedirect("http://127.0.0.1:8083/");
            }
            else{
                filterChain.doFilter(httpServletRequest,httpServletResponse);
            }
        }
    }

    private boolean isStaticResource(HttpServletRequest request){
        String servletpath = request.getServletPath();
        if(servletpath.startsWith("/css")
                ||servletpath.startsWith("/JavaScript")
                ||servletpath.startsWith("/images")
                ||servletpath.endsWith("/favicon.ico")
                ||servletpath.startsWith("/fonts")){
            return true;
        }else{
            return false;
        }
    }

    private boolean isExclusionRequest(HttpServletRequest request){
        String servletPath = request.getServletPath();
        if(servletPath.equals("/")
                ||servletPath.startsWith("/register")
                ||servletPath.startsWith("/login")
                ||servletPath.startsWith("/registerCheck")){
            return true;
        }else{
            return false;
        }
    }
}
