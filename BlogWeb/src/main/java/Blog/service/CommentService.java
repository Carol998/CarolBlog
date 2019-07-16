package Blog.service;

import Blog.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentService {
    List<Comment> selectAll(@Param("cid") long cid);

    List<Comment> findAllFirstComment(@Param("cid") long cid);

    List<Comment> findAllChildrenComment(@Param("cid") long cid,@Param("children") String children);

    int insertComment(Comment comment);
}
