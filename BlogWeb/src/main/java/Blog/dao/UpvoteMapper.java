package Blog.dao;

import Blog.entity.Upvote;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UpvoteMapper extends Mapper<Upvote> {
}
