package Blog.dao;

import Blog.entity.UserContent;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface UserContentMapper extends Mapper<UserContent> {

    /**
     * 根据用户id查询出文章的分类
     * @param uid
     * @return
     */
    List<UserContent> findCategoryByUid(@Param("uid") Long uid);
}
