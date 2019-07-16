package Blog.service.impl;

import Blog.dao.CommentMapper;
import Blog.entity.Comment;
import Blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 根据文章id查找所有评论
     * @param cid
     * @return
     */
    @Override
    public List<Comment> selectAll(long cid) {
        return null;
    }

    /**
     * 根据文章id查询所有的一级评论
     * @param cid
     * @return
     */
    @Override
    public List<Comment> findAllFirstComment(long cid) {
        return null;
    }

    /**
     * 根据文章id与二级评论的ids查询所有的评论
     * @param cid
     * @param children
     * @return
     */
    @Override
    public List<Comment> findAllChildrenComment(long cid, String children) {
        return null;
    }

    /**
     * 添加评论
     * @param comment
     * @return
     */
    @Override
    public int insertComment(Comment comment) {
        return 0;
    }
}
