package Blog.controller;

import Blog.common.PageHelper;
import Blog.entity.Upvote;
import Blog.entity.User;
import Blog.entity.UserContent;
import Blog.service.UpvoteService;
import Blog.service.UserContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexJspController extends BaseController{

    @Autowired
    private UpvoteService upvoteService;

    @Autowired
    private UserContentService userContentService;

    @RequestMapping("/index_list")
    public String findAll(Model model,
                          @RequestParam(value = "id",required = false) String id,
                          @RequestParam(value = "pageNum",required = false) Integer pageNum,
                          @RequestParam(value = "pageSize",required = false) Integer pageSize){
        User user = (User) getSession().getAttribute("user");
        if(user!=null){
            model.addAttribute("user",user);
        }
        PageHelper.Page<UserContent> page = findAll(null,pageNum,pageSize);
        model.addAttribute("page",page);
        return "../index";
    }

    /**
     * 退出登陆
     * @param model
     * @return
     */
    @RequestMapping("/loginout")
    public String logout(Model model){
        getSession().removeAttribute("user");

        //释放session资源，使其失效
        getSession().invalidate();
        return "../login";
    }

    /**
     * 点赞或踩
     * @param model
     * @param id   文章id
     * @param uid
     * @param upvote
     * @return
     */
    @RequestMapping("/upvote")
    @ResponseBody
    public Map<String,String> upvote(Model model,
                                     @RequestParam(value = "id",required = false)long id,
                                     @RequestParam(value = "uid",required = false)long uid,
                                     @RequestParam(value = "upvote",required = false)long upvote){
        Map<String,String> map = new HashMap<>();
        User user = (User) getSession().getAttribute("user");
        //用户未登陆
        if(user==null){
            map.put("data","fail");
        }
        Upvote up = new Upvote();
        up.setuId(uid);
        up.setContentId(id);
        Upvote upvote1 = upvoteService.findByup(up);
        UserContent userContent = userContentService.findById(id);

        //点赞
        if(upvote==1){
            if(upvote1==null){
                up.setIp(getClientIpAddress());
                up.setUpvote("1");
                up.setUpvoteTime(new Date());
                upvoteService.addUpvote(up);
            }else {
                if(upvote1.getUpvote().equals("1")){
                    map.put("data","done");
                    return map;
                }else {
                    up.setIp(getClientIpAddress());
                    up.setUpvote("1");
                    up.setUpvoteTime(new Date());
                    upvoteService.updateUpvote(up);
                }
            }
            userContent.setUpvote(userContent.getUpvote()+1);
        }
        else {
            if(upvote1==null){
                up.setIp(getClientIpAddress());
                up.setDownvote("1");
                up.setUpvoteTime(new Date());
                upvoteService.addUpvote(up);
            }
            else {
                if(upvote1.getDownvote().equals("1")){
                    map.put("data","down");
                    return map;
                }else {
                    up.setIp(getClientIpAddress());
                    up.setDownvote("1");
                    up.setUpvoteTime(new Date());
                    upvoteService.updateUpvote(up);
                }
            }
            userContent.setDownvote(userContent.getDownvote()+1);
        }
        userContentService.updateById(userContent);
        map.put("data","success");
        return map;
    }

}
