package com.lafuente.sap.rest;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author GUIDO CACERES PINTO
 */
@WebFilter(filterName = "CORSFilter", urlPatterns = {"/*"})
public class CORSFilter implements Filter {

    private static final boolean DEBUG = true;
    private FilterConfig filterConfig = null;

    public CORSFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        if (StringUtils.isEmpty(servletRequest.getHeader("X-Forwarded-Host"))) {
            servletResponse.addHeader("Access-Control-Allow-Origin", "*");
            servletResponse.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            servletResponse.addHeader("Access-Control-Allow-Credentials", "false");
            servletResponse.addHeader("Access-Control-Allow-Headers",
                    "Access-Control-Allow-Credentials, Access-Control-Allow-Headers, Origin, X-Requested-With, Content-Type, Accept, Authorization, Access-Control-Allow-Origin, Access-Control-Allow-Methods, apikey");

        }
        /// For HTTP OPTIONS verb/method reply with ACCEPTED status code
        if (servletRequest.getMethod().equals("OPTIONS")) {
            servletResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
        long elapsed = System.currentTimeMillis() - startTime;
        if (DEBUG && request instanceof HttpServletRequest) {
            String uri = ((HttpServletRequest) request).getRequestURI();
            log("URI: " + uri + " time: " + elapsed + " ms");
        }
    }

    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (DEBUG) {
                log("CORSFilter: Initializing filter");
            } 
        }
    }

    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("CORSFilter()");
        }
        StringBuilder sb = new StringBuilder("CORSFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());

    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
