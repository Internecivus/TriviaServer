package com.trivia.api.filter;

import javax.ejb.Schedule;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;


// Limits the request rate per IP in a given time frame.
@WebFilter(filterName = "RateLimiterFilter", urlPatterns = {"/*"}, initParams = { @WebInitParam(name = "mood", value = "awake")})
public class RateLimiterFilter implements Filter {
    private Long limitRate = 1_000L;
    private String errorMessage = "Status error code 429: Too Many Requests. The request rate is limited to 1,000 per hour per IP address.";
    private HashMap<String, Long> visitorsRequests;

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String IP = servletRequest.getRemoteAddr();

        if(visitorsRequests.containsKey(IP)) {

            visitorsRequests.put(IP, visitorsRequests.get(IP) + 1);
        } else {
            visitorsRequests.put(IP, 1L);
        }

        if (visitorsRequests.get(IP) > limitRate) {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.sendError(429, errorMessage); //"Too Many Requests" HTTP status code
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        visitorsRequests = new HashMap<>();
    }

    @Schedule(hour = "*")
    public void clearFilter() {
        visitorsRequests.clear();
    }

}