package Blog.service.impl;

import Blog.dao.CommentMapper;
import Blog.entity.Comment;
import Blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 根据文章id查找所有评论
     *
     * @param cid
     * @return
     */
    @Override
    public List<Comment> selectAll(long cid) {
        return commentMapper.selectAll(cid);
    }

    /**
     * 根据文章id查询所有的一级评论
     *
     * @param cid
     * @return
     */
    @Override
    public List<Comment> findAllFirstComment(long cid) {
        return commentMapper.findAllFirstComment(cid);
    }

    /**
     * 根据文章id与二级评论的ids查询所有的评论
     *
     * @param cid
     * @param children
     * @return
     */
    @Override
    public List<Comment> findAllChildrenComment(long cid, String children) {
        return commentMapper.findAllChildrenComment(cid, children);
    }

    /**
     * 添加评论
     *
     * @param comment
     * @return
     */
    @Override
    public int insertComment(Comment comment) {
        return commentMapper.insertComment(comment);
    }

    /**
     * 根据id查找评论
     *
     * @param id
     * @return
     */
    @Override
    public Comment findById(Long id) {
        return commentMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Comment comment) {
        return commentMapper.updateByPrimaryKey(comment);
    }

    @Override
    public void deleteCommentById(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        commentMapper.deleteByPrimaryKey(comment);
    }

    @Override
    public void deleteChildrenCom(String children) {
        Example example = new Example(Comment.class);
        Example.Criteria criteria = example.createCriteria();
        List<Object> list = new ArrayList<Object>();
        String[] split = children.split(",");
        for (int i = 0; i < split.length; i++) {
            list.add(split[i]);
        }
        criteria.andIn("id", list);
        commentMapper.deleteByExample(example);
    }

    @Override
    public void deleteCommentByCid(Long cid) {
        Comment comment = new Comment();
        comment.setConId(cid);
        commentMapper.delete(comment);
    }
}
