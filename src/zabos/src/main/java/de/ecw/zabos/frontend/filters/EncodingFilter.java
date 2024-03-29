package de.ecw.zabos.frontend.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter für das UTF8-Encoding
 * 
 * @author ckl
 * 
 */
public class EncodingFilter implements Filter
{
    private String encoding;

    public void init(FilterConfig config) throws ServletException
    {
        encoding = config.getInitParameter("requestEncoding");

        if (encoding == null)
        {
            encoding = "UTF-8";
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                    FilterChain next) throws IOException, ServletException
    {
        request.setCharacterEncoding(encoding);
        next.doFilter(request, response);
    }

    public void destroy()
    {
    }
}
