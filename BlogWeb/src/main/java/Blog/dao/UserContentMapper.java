package Blog.dao;

import Blog.entity.User;
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

    /**
     * 插入文章，返回id
     * @param userContent
     * @return
     */
    int insertUserContent (UserContent userContent);

    /**
     * 连接查询UserContent
     * @param userContent
     * @return
     */
    List<UserContent> findByJoin(UserContent userContent);
}
