package Blog.dao;

import Blog.entity.Comment;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface CommentMapper extends Mapper<Comment> {
    List<Comment> findAllFirstComment(long cid);

    List<Comment> findAllChildrenComment(long cid, String children);

    List<Comment> selectAll(long cid);

    Integer insertComment(Comment comment);
}
