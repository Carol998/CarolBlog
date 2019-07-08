package Blog.dao;

import Blog.entity.UserContent;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UserContentMapper extends Mapper<UserContent> {
}
