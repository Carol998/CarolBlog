package Blog.dao;

import Blog.entity.RoleUser;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface RoleUserMapper extends Mapper<RoleUser> {
}
