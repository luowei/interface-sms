package net.oilchem.common;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-3-19
 * Time: 上午10:44
 * To change this template use File | Settings | File Templates.
 */
public class ArgumentFilter  implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new RequestWrapper( (HttpServletRequest)servletRequest ), servletResponse);
    }

    @Override
    public void destroy() {
    }
}
