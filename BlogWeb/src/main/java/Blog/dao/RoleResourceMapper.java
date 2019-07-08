package Blog.dao;

import Blog.entity.RoleResource;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface RoleResourceMapper extends Mapper<RoleResource> {
}
