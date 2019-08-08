package Blog.controller;

import Blog.entity.User;
import Blog.entity.UserContent;
import Blog.service.UserContentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class writeController extends BaseController {

    @Autowired
    private UserContentService userContentService;

    /**
     * 跳转新建文章页面
     *
     * @param model
     * @param cid
     * @return
     */
    @RequestMapping("/writedream")
    public String writeBlog(Model model,
                            @RequestParam(value = "cid", required = false) Long cid) {
        User user = (User) getSession().getAttribute("user");
        if (cid != null) {
            UserContent userContent = userContentService.findById(cid);
            model.addAttribute("cont", userContent);
        }
        model.addAttribute("user", user);
        return "/write/writedream";
    }

    @RequestMapping("/doWritedream")
    public String doWriteBlog(Model model,
                              @RequestParam(value = "id", required = false) String id,
                              @RequestParam(value = "category", required = false) String category,
                              @RequestParam(value = "txt_Title", required = false) String title,
                              @RequestParam(value = "content", required = false) String content,
                              @RequestParam(value = "private_dream", required = false) String private_dream) {
        User user = (User) getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "用户未登陆，请先登陆");
            return "../login";
        }
        UserContent userContent = new UserContent();
        userContent.setuId(user.getId());
        userContent.setTitle(title);
        userContent.setCategory(category);
        userContent.setContent(content);
        if ("on".equals(private_dream)) {
            userContent.setPersonal("1");
        } else {
            userContent.setPersonal("0");
        }
        userContent.setRptTime(new Date());
        String imgUrl = user.getImgUrl();
        if (StringUtils.isBlank(imgUrl)) {
            userContent.setImgUrl("/images/icon_m.jpg");
        } else {
            userContent.setImgUrl(imgUrl);
        }
        userContent.setNickName(user.getNickName());
        userContent.setUpvote(0);
        userContent.setDownvote(0);
        userContent.setCommentNum(0);
        userContentService.addUserContent(userContent);
        model.addAttribute("content", userContent);
        return "write/writesuccess";
    }

    /**
     * 添加文章成功后查询已添加的文章
     *
     * @param model
     * @param cid
     * @return
     */
    @RequestMapping("/watch")
    public String watchContent(Model model,
                               @RequestParam(value = "cid", required = false) long cid) {
        User user = (User) getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "请登陆");
            return "../login";
        }
        UserContent userContent = userContentService.findById(cid);
        model.addAttribute("cont", userContent);
        return "personal/watch";
    }
}
