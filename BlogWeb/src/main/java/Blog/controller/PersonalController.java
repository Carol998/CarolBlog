package Blog.controller;

import Blog.common.PageHelper;
import Blog.entity.User;
import Blog.entity.UserContent;
import Blog.service.UserContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PersonalController extends BaseController{

    @Autowired
    private UserContentService userContentService;

    /**
     * 跳转个人主页
     * @param model
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list")
    public String showList(Model model,
                           @RequestParam(value = "id",required = false)Long id,
                           @RequestParam(value = "pageNum",required = false) Integer pageNum,
                           @RequestParam(value = "pageSize",required = false) Integer pageSize){

        User user = (User) getSession().getAttribute("user");

        if(user == null){
            return "../login";
        }
        else {
            model.addAttribute("user",user);
            //查询文章的分类
            List<UserContent> categorys = userContentService.findCategoryByuid(user.getId());
            model.addAttribute("categorys",categorys);

            //查询非私密的文章
            UserContent userContent = new UserContent();
            userContent.setPersonal("0");
            pageSize = 4;
            PageHelper.Page<UserContent> page = userContentService.findAll(userContent,pageNum,pageSize);
            model.addAttribute("page",page);

            //查询私密文章
            UserContent ucontent = new UserContent();
            ucontent.setPersonal("1");
            PageHelper.Page<UserContent> page2 = userContentService.findAll(ucontent,pageNum,pageSize);
            model.addAttribute("page2",page2);

            //查询热门文章
            UserContent hotContent = new UserContent();
            hotContent.setPersonal("0");
            PageHelper.Page<UserContent> hotpage = userContentService.findAll(hotContent,pageNum,pageSize);
            model.addAttribute("hotpage",hotpage);
        }
        return "personal/personal";
    }

    /**
     * 通过分类查询文章,此时查询的文章为非私密文章
     * @param model
     * @param category
     * @param pageNum
     * @return
     */
    @ResponseBody
    @RequestMapping("/findByCategory")
    public Map<String,Object> findByCategory(Model model,
                                             @RequestParam(value = "category",required = false) String category,
                                             @RequestParam(value = "pageNum",required = false) Integer pageNum,
                                             @RequestParam(value = "pageSize",required = false) Integer pageSize){
        Map map = new HashMap<String,Object>();
        User user = (User) getSession().getAttribute("user");
        if(user == null){
            //当前用户未登陆
            map.put("pageCate","fail");
            return map;
        }
        pageSize = 4;
        PageHelper.Page<UserContent> userContentList = userContentService.findByCategroy(category,user.getId(),pageNum,pageSize);
        map.put("pageCate",userContentList);
        return map;
    }

    /**
     * 分页查找私密文章
     * @param model
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping("/findPersonal")
    public Map<String,Object> findPersonalContent(Model model,
                                                  @RequestParam(value = "pageNum",required = false) Integer pageNum,
                                                  @RequestParam(value = "pageSize",required = false) Integer pageSize){
        Map map = new HashMap<String,Object>();
        User user = (User) getSession().getAttribute("user");
        if(user == null){
            map.put("page2","fail");
            return map;
        }
        pageSize = 4;
        PageHelper.Page<UserContent> userContentPage = userContentService.findPersonalContent(user.getId(),pageNum,pageSize);
        map.put("page2",userContentPage);
        return map;
    }
}
