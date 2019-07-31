package Blog.controller;

import Blog.common.DateUtils;
import Blog.common.PageHelper;
import Blog.entity.Comment;
import Blog.entity.Upvote;
import Blog.entity.User;
import Blog.entity.UserContent;
import Blog.service.CommentService;
import Blog.service.UpvoteService;
import Blog.service.UserContentService;
import Blog.service.UserService;
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
public class IndexJspController extends BaseController{

    @Autowired
    private UpvoteService upvoteService;

    @Autowired
    private UserContentService userContentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

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

    /**
     * 点击reply评论按钮，展示该文章的所有评论及其二级评论
     * @param model
     * @param conid 文章id
     * @return
     */
    @ResponseBody
    @RequestMapping("/reply")
    public Map<String,String> reply(Model model,
                                    @RequestParam(value = "content_id",required = false)Long conid){

        Map map = new HashMap<String,Object>();
        //找到所有的以及评论
        List<Comment> commentList = commentService.findAllFirstComment(conid);
        if(commentList!=null && commentList.size()>0){
            //遍历一级评论表，根据该一级评论，找到下面的二级评论
            for(Comment comment:commentList){
                List<Comment> comments = commentService.findAllChildrenComment(comment.getConId(),comment.getChildren());
                if(comments!=null && comments.size()>0){
                    for(Comment com : comments){
                        if(com.getById()!=null){
                            User byUser = userService.findById(com.getById());
                            com.setByUser(byUser);
                        }
                    }
                }
               comment.setComList(comments);
            }
        }
        map.put("list",commentList);
        return map;
    }

    /**
     * 评论内容
     * @param model
     * @param id
     * @param content_id 文章id
     * @param uid  评论用户id
     * @param bid
     * @param oSize  评论内容
     * @param comment_time  评论时间
     * @param upvote
     * @return
     */
    @RequestMapping("/comment")
    @ResponseBody
    public Map<String,Object> comment(Model model,
                                      @RequestParam(value = "id",required = false) Long id,
                                      @RequestParam(value = "content_id",required = false)Long content_id,
                                      @RequestParam(value = "uid",required = false)Long uid,
                                      @RequestParam(value = "by_id",required = false)Long bid,
                                      @RequestParam(value = "oSize",required = false)String oSize,
                                      @RequestParam(value = "comment_time",required = false)String comment_time,
                                      @RequestParam(value = "upvote",required = false)Integer upvote){

        Map map = new HashMap<String,Object>();
        User user = (User) getSession().getAttribute("user");
        if(user==null){
            map.put("data","fail");
            return map;
        }
        //id为null，表示此时为添加评论，否则是评论点赞或者取消赞
        if(id==null){
            Date date = DateUtils.StringToDate(comment_time,"yyyy-MM-dd HH:mm:ss");
            Comment comment = new Comment();
            comment.setConId(content_id);
            comment.setComId(uid);
            comment.setComContent(oSize);
            comment.setCommTime(date);
            if(upvote==null){
                upvote=0;
            }
            comment.setUpvote(upvote);
            comment.setById(bid);
            User u = userService.findById(uid);
            comment.setUser(u);
            commentService.insertComment(comment);
            map.put("data",comment);

            UserContent userContent = userContentService.findById(content_id);
            userContent.setCommentNum(userContent.getCommentNum()+1);
            userContentService.updateById(userContent);
        }else {
            Comment c = commentService.findById(id);
            c.setUpvote(upvote);
            commentService.update(c);
        }
        return map;
    }
}
