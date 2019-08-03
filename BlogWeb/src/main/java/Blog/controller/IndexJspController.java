package Blog.controller;

import Blog.common.DateUtils;
import Blog.common.PageHelper;
import Blog.common.StringUtil;
import Blog.entity.Comment;
import Blog.entity.Upvote;
import Blog.entity.User;
import Blog.entity.UserContent;
import Blog.service.CommentService;
import Blog.service.UpvoteService;
import Blog.service.UserContentService;
import Blog.service.UserService;
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

    /**
     * 删除评论
     * @param model
     * @param id
     * @param uid
     * @param con_id
     * @param fid
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteComment")
    public Map<String,Object> deleteComment(Model model,
                                            @RequestParam(value = "id",required = false)Long id,
                                            @RequestParam(value = "uid",required = false) Long uid,
                                            @RequestParam(value = "con_id",required = false) Long con_id,
                                            @RequestParam(value = "fid",required = false) Long fid){
        int num = 0;
        Map<String,Object> map = new HashMap<>();
        User user = (User)getSession().getAttribute("user");
        if(user==null){
            //用户未登陆，跳转登录页面
            map.put("data","fail");
            return map;
        }
        else {
            if(user.getId().equals(uid)){
                Comment comment = commentService.findById(id);
                if(StringUtils.isBlank(comment.getChildren())){
                    //当前评论对象无子评论
                    if(fid!=null){
                        //当前评论对象无子评论且具有父评论
                        Comment fcomment = commentService.findById(fid);
                        //删除该父评论下子评论中的该评论id
                        String c = StringUtil.getString(fcomment.getChildren(),id);
                        fcomment.setChildren(c);
                        commentService.update(fcomment);
                    }
                    //当前评论对象无子评论且无父评论，直接删除
                    commentService.deleteCommentById(id);
                    num = num+1;
                }
                else {   //该评论具有子评论
                    String children = comment.getChildren();
                    commentService.deleteChildrenCom(children);
                    String[] arr = children.split(",");
                    commentService.deleteCommentById(id);
                    num=num+arr.length+1;
                }
                UserContent userContent = userContentService.findById(con_id);
                if(userContent!=null){
                    if(userContent.getCommentNum()-num>0){
                        userContent.setCommentNum(userContent.getCommentNum()-num);
                    }else {
                        userContent.setCommentNum(0);
                    }
                    userContentService.updateById(userContent);
                }
                map.put("data",userContent.getCommentNum());
            }
            else {
                //当前用户不是该文章用户，无权限
                map.put("data","no-access");
            }
        }
        return map;
    }

    /**
     *对一级评论进行评论
     * @param model
     * @param id  一级评论id
     * @param conid 文章id
     * @param uid  评论用户id
     * @param bid   被评论者id
     * @param oSize   评论内容
     * @param comment_time  评论时间
     * @param upvote
     * @return
     */
    @ResponseBody
    @RequestMapping("/comment_child")
    public Map<String,Object> comment_Children(Model model,
                                               @RequestParam(value = "id",required = false)Long id,
                                               @RequestParam(value = "content_id",required = false)Long conid,
                                               @RequestParam(value = "uid",required = false)Long uid,
                                               @RequestParam(value = "by_id",required = false)Long bid,
                                               @RequestParam(value = "oSize",required = false)String oSize,
                                               @RequestParam(value = "comment_time",required = false)String comment_time,
                                               @RequestParam(value = "upvote",required = false)Integer upvote){
        Map map = new HashMap<String,Object>();
        User user = (User) getSession().getAttribute("user");
        if(user == null){
            //当前用户未登陆
            map.put("data","fail");
            return map;
        }
        else {
            //在comment表中添加评论
            Comment comment = new Comment();
            comment.setCommTime(DateUtils.StringToDate(comment_time,"yyyy-MM-dd HH:mm:ss"));
            comment.setComContent(oSize);
            comment.setConId(conid);
            comment.setComId(uid);
            if(upvote==null)
                upvote = 0;
            comment.setUpvote(upvote);
            comment.setById(bid);
            User user1 = userService.findById(uid);
            comment.setUser(user1);
            commentService.update(comment);

            //在一级评论中添加该新增评论的信息
            Comment fcomment = commentService.findById(id);
            if(StringUtils.isBlank(fcomment.getChildren())){
                //子评论为空，则直接添加
                fcomment.setChildren(comment.getId().toString());
            }else {
                fcomment.setChildren(fcomment.getChildren()+","+comment.getId());
            }
            commentService.update(fcomment);
            map.put("data",comment);

            UserContent userContent = userContentService.findById(conid);
            userContent.setCommentNum(userContent.getCommentNum()+1);
            userContentService.updateById(userContent);
        }
        return map;
    }
}
