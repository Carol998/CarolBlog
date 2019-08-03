package Blog.service.impl;

import Blog.common.PageHelper;
import Blog.dao.CommentMapper;
import Blog.dao.UserContentMapper;
import Blog.entity.Comment;
import Blog.entity.User;
import Blog.entity.UserContent;
import Blog.service.UserContentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserContentServiceImpl implements UserContentService {

    @Autowired
    private UserContentMapper userContentMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public PageHelper.Page<UserContent> findAll(UserContent content, Integer pageNum, Integer pageSize) {
        //分页查询
        System.out.println("第"+pageNum+"页");
        System.out.println("每页显示："+pageSize+"条");
        PageHelper.startPage(pageNum, pageSize);//开始分页
        List<UserContent> list =  userContentMapper.select( content );
        PageHelper.Page endPage = PageHelper.endPage();//分页结束
        return endPage;
    }

    @Override
    public PageHelper.Page<UserContent> findAll(UserContent content, Comment comment, Integer pageNum, Integer pageSize) {
        //分页查询
        System.out.println("第"+pageNum+"页");
        System.out.println("每页显示："+pageSize+"条");
        PageHelper.startPage(pageNum, pageSize);//开始分页
        List<UserContent> list =  userContentMapper.select( content );

        List<Comment> comments = commentMapper.select( comment );

        PageHelper.Page endPage = PageHelper.endPage();//分页结束
        List<UserContent> result = endPage.getResult();
        return endPage;
    }

    @Override
    public PageHelper.Page<UserContent> findAllByUpvote(UserContent content, Integer pageNum, Integer pageSize) {
        Example e = new Example(UserContent.class);
        e.setOrderByClause("upvote DESC");
        PageHelper.startPage(pageNum, pageSize);//开始分页
        List<UserContent> list = userContentMapper.selectByExample(e);
        PageHelper.Page endPage = PageHelper.endPage();//分页结束
        return endPage;
    }

    @Override
    public void addContent(UserContent content) {
        userContentMapper.insert(content);
    }

    @Override
    public List<UserContent> findByUserId(Long uid) {
        UserContent userContent = new UserContent();
        userContent.setuId(uid);
        return userContentMapper.select(userContent);
    }

    @Override
    public List<UserContent> findAll() {
        return userContentMapper.select(null);
    }

    @Override
    public UserContent findById(long id) {
        UserContent userContent = new UserContent();
        userContent.setId(id);
        return userContentMapper.selectOne(userContent);
    }

    @Override
    public void updateById(UserContent content) {
        userContentMapper.updateByPrimaryKeySelective(content);
    }

    @Override
    public List<UserContent> findCategoryByuid(Long uid) {
        return userContentMapper.findCategoryByUid(uid);
    }

    @Override
    public PageHelper.Page<UserContent> findByCategroy(String category, Long uid, Integer pageNum, Integer pageSize) {
        UserContent userContent = new UserContent();
        if(StringUtils.isNotBlank(category) && !"null".equals(category)){
            userContent.setCategory(category);
        }
        userContent.setuId(uid);
        userContent.setPersonal("0");
        PageHelper.startPage(pageNum, pageSize);//开始分页
        userContentMapper.select(userContent);
        PageHelper.Page endPage = PageHelper.endPage();//分页结束
        return endPage;
    }

    @Override
    public PageHelper.Page<UserContent> findPersonalContent(Long uid, Integer pageNum, Integer pageSize) {
        UserContent userContent = new UserContent();
        userContent.setuId(uid);
        userContent.setPersonal("1");
        PageHelper.startPage(pageNum,pageSize);
        userContentMapper.select(userContent);
        PageHelper.Page endPage = PageHelper.endPage();
        return endPage;
    }
}
