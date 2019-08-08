package Blog.controller;

import Blog.common.PageHelper;
import Blog.entity.Comment;
import Blog.entity.User;
import Blog.entity.UserContent;
import Blog.service.UserContentService;
import Blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
public class BaseController {

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "X-Real-IP"};

    @Autowired
    private UserService userService;

    @Autowired
    private UserContentService userContentService;

    /**
     * 获取request
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    /**
     * 获取response
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        return httpServletResponse;
    }

    public static HttpSession getSession() {
        HttpSession session = null;
        try {
            session = getRequest().getSession();
        } catch (Exception e) {
        }
        return session;
    }

    /***
     * 获取客户端ip地址(可以穿透代理)
     * @return
     */
    public static String getClientIpAddress() {
        HttpServletRequest request = getRequest();
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public boolean isLogin(Long id) {
        return userService.findById(id) != null;
    }

    public User getUser(Long id) {
        return userService.findById(id);
    }

    public List<UserContent> getUserContentList(Long uid) {
        List<UserContent> list = userContentService.findByUserId(uid);
        return list;
    }

    public List<UserContent> getAllUserContentList() {
        List<UserContent> list = userContentService.findAll();
        return list;
    }

    public PageHelper.Page<UserContent> findAll(UserContent content, Integer pageNum, Integer pageSize) {
        PageHelper.Page<UserContent> page = userContentService.findAll(content, pageNum, pageSize);
        return page;
    }

    public PageHelper.Page<UserContent> findAll(UserContent content, Comment comment, Integer pageNum, Integer pageSize) {
        PageHelper.Page<UserContent> page = userContentService.findAll(content, comment, pageNum, pageSize);
        return page;
    }

    public PageHelper.Page<UserContent> findAllByUpvote(UserContent content, Integer pageNum, Integer pageSize) {
        PageHelper.Page<UserContent> page = userContentService.findAllByUpvote(content, pageNum, pageSize);
        return page;
    }
}
