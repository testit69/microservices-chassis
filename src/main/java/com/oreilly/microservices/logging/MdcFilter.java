package com.oreilly.microservices.logging;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MdcFilter implements Filter {

    public static final String HEADER_SESSION_ID = "X-Session-ID";
    public static final String MDC_SESSION_ID = "session_id";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String sessionId = ((HttpServletRequest) request).getHeader(HEADER_SESSION_ID);

        if (StringUtils.isNotBlank(sessionId)) {
            MDC.put(MDC_SESSION_ID, sessionId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_SESSION_ID);
        }
    }

    @Override
    public void destroy() {

    }
}
