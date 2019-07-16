package Blog.interceptor;
import Blog.common.PageHelper;
import Blog.dao.UserContentMapper;
import Blog.entity.UserContent;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.*;

import java.io.IOException;
import java.util.List;

/**
 * index过滤器
 */
public class IndexJspFilter implements Filter{
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("===========自定义过滤器==========");
        ServletContext context = request.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);

        //手动获取UserContent对象
        UserContentMapper userContentMapper = ctx.getBean(UserContentMapper.class);
        PageHelper.startPage(null, null);//开始分页
        List<UserContent> list =  userContentMapper.select( null );
        PageHelper.Page endPage = PageHelper.endPage();//分页结束
        request.setAttribute("page", endPage );
        chain.doFilter(request, response);
    }

    public void destroy() {

    }
}
