package Blog.controller;

import Blog.common.DateUtils;
import Blog.common.MD5Util;
import Blog.common.PageHelper;
import Blog.entity.User;
import Blog.entity.UserContent;
import Blog.entity.UserInfo;
import Blog.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PersonalController extends BaseController {

    @Autowired
    private UserContentService userContentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UpvoteService upvoteService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserService userService;

    /**
     * 跳转个人主页
     *
     * @param model
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list")
    public String showList(Model model,
                           @RequestParam(value = "id", required = false) Long id,
                           @RequestParam(value = "pageNum", required = false) Integer pageNum,
                           @RequestParam(value = "pageSize", required = false) Integer pageSize,
                           @RequestParam(value = "manage", required = false) String manage) {

        User user = (User) getSession().getAttribute("user");
        UserContent content = new UserContent();
        UserContent uc = new UserContent();
        if (user != null) {
            model.addAttribute("user", user);
            content.setuId(user.getId());
            uc.setuId(user.getId());
        } else {
            return "../login";
        }

        if (StringUtils.isNotBlank(manage)) {
            model.addAttribute("manage", manage);
        }

        //查询梦分类
        List<UserContent> categorys = userContentService.findCategoryByuid(user.getId());
        model.addAttribute("categorys", categorys);
        //发布的梦 不含私密梦
        content.setPersonal("0");
        pageSize = 10; //默认每页显示10条数据
        PageHelper.Page<UserContent> page = findAll(content, pageNum, pageSize); //分页

        model.addAttribute("page", page);

        //查询私密梦
        uc.setPersonal("1");
        PageHelper.Page<UserContent> page2 = findAll(uc, pageNum, pageSize);
        model.addAttribute("page2", page2);

        //查询热梦
        pageSize = 15; //默认每页显示15条数据
        UserContent uct = new UserContent();
        uct.setPersonal("0");
        PageHelper.Page<UserContent> hotPage = findAllByUpvote(uct, pageNum, pageSize);
        model.addAttribute("hotPage", hotPage);
        return "personal/personal";
    }

    /**
     * 通过分类查询文章,此时查询的文章为非私密文章
     *
     * @param model
     * @param category
     * @param pageNum
     * @return
     */
    @ResponseBody
    @RequestMapping("/findByCategory")
    public Map<String, Object> findByCategory(Model model,
                                              @RequestParam(value = "category", required = false) String category,
                                              @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map map = new HashMap<String, Object>();
        User user = (User) getSession().getAttribute("user");
        if (user == null) {
            //当前用户未登陆
            map.put("pageCate", "fail");
            return map;
        }
        pageSize = 10;
        PageHelper.Page<UserContent> userContentList = userContentService.findByCategroy(category, user.getId(), pageNum, pageSize);
        map.put("pageCate", userContentList);
        return map;
    }

    /**
     * 分页查找私密文章
     *
     * @param model
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping("/findPersonal")
    public Map<String, Object> findPersonalContent(Model model,
                                                   @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map map = new HashMap<String, Object>();
        User user = (User) getSession().getAttribute("user");
        if (user == null) {
            map.put("page2", "fail");
            return map;
        }
        pageSize = 10;
        PageHelper.Page<UserContent> userContentPage = userContentService.findPersonalContent(user.getId(), pageNum, pageSize);
        map.put("page2", userContentPage);
        return map;
    }

    /**
     * 删除文章
     *
     * @param model
     * @param cid
     * @return
     */
    @RequestMapping("/deleteContent")
    public String deleteContent(Model model,
                                @RequestParam(value = "cid", required = false) Long cid) {
        User user = (User) getSession().getAttribute("user");
        if (user == null) {
            return "../login";
        }
        commentService.deleteCommentByCid(cid);
        upvoteService.deleteUpvoteByCid(cid);
        userContentService.deleteContentById(cid);
        return "redirect:/list?manage=manage";
    }

    /**
     * 个人信息修改页面的跳转
     *
     * @param model
     * @return
     */
    @RequestMapping("/profile")
    public String profile(Model model) {
        User user = (User) getSession().getAttribute("user");
        if (user == null) {
            return "../login";
        }
        UserInfo userInfo = userInfoService.findByUid(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("userInfo", userInfo);
        return "personal/profile";
    }

    /**
     * 个人信息中心保存已修改的头像
     *
     * @param model
     * @param url
     * @return
     */
    @RequestMapping("/saveImage")
    @ResponseBody
    public Map<String, Object> saveImg(Model model,
                                       @RequestParam(value = "url", required = false) String url) {
        Map map = new HashMap<String, Object>();
        User user = (User) getSession().getAttribute("user");
        user.setImgUrl(url);
        userService.update(user);
        map.put("msg", "success");
        return map;
    }

    /**
     * 修改个人信息
     *
     * @param model
     * @param name
     * @param nickName
     * @param sex
     * @param birthday
     * @param address
     * @return
     */
    @RequestMapping("/saveUserInfo")
    public String saveUserInfo(Model model,
                               @RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "nick_name", required = false) String nickName,
                               @RequestParam(value = "sex", required = false) String sex,
                               @RequestParam(value = "birthday", required = false) String birthday,
                               @RequestParam(value = "address", required = false) String address) {
        User user = (User) getSession().getAttribute("user");
        if (user == null) {
            return "../login";
        }
        UserInfo u = userInfoService.findByUid(user.getId());
        Boolean flag = false;
        if (u == null) {
            u = new UserInfo();
        } else {
            flag = true;
        }

        u.setuId(user.getId());
        u.setName(name);
        u.setSex(sex);
        Date date = DateUtils.StringToDate(birthday, "yyyy-MM-dd");
        u.setBirthday(date);
        u.setAddress(address);
        user.setNickName(nickName);
        userService.update(user);

        if (flag) {
            userInfoService.update(u);
        } else {
            userInfoService.addUInfo(u);
        }

        model.addAttribute("user", user);
        model.addAttribute("userInfo", u);
        return "personal/profile";
    }

    /**
     * 修改密码跳转页面
     *
     * @param model
     * @return
     */
    @RequestMapping("/repassword")
    public String repassword(Model model) {
        User user = (User) getSession().getAttribute("user");
        if (user == null) {
            return "../login";
        }
        model.addAttribute("user", user);
        return "personal/repassword";
    }

    /**
     * 修改密码
     *
     * @param model
     * @return
     */
    @RequestMapping("/updatePassword")
    public String updatePassword(Model model,
                                 @RequestParam(value = "old_password", required = false) String oldPsw,
                                 @RequestParam(value = "password", required = false) String psw) {
        User user = (User) getSession().getAttribute("user");
        if (user != null) {
            oldPsw = MD5Util.encodeToHex("salt" + oldPsw);
            if (user.getPassword().equals(oldPsw)) {
                psw = MD5Util.encodeToHex("salt" + psw);
                user.setPassword(psw);
                userService.update(user);
                model.addAttribute("message", "success");
            } else {
                model.addAttribute("message", "fail");
            }
        }
        model.addAttribute("user", user);
        return "personal/passwordSuccess";
    }
}
