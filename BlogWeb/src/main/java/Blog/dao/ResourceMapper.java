package Blog.dao;

import Blog.entity.Resource;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface ResourceMapper extends Mapper<Resource> {
}
