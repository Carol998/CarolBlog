package Blog.service;

import Blog.common.PageHelper;
import Blog.entity.Comment;
import Blog.entity.User;
import Blog.entity.UserContent;

import java.util.List;

public interface UserContentService {
    PageHelper.Page<UserContent> findAll(UserContent content, Integer pageNum, Integer pageSize);
    PageHelper.Page<UserContent> findAll(UserContent content, Comment comment, Integer pageNum, Integer pageSize);
    PageHelper.Page<UserContent> findAllByUpvote(UserContent content, Integer pageNum, Integer pageSize);

    PageHelper.Page<UserContent> findAll(Integer pageNum, Integer pageSize);
    /**
     * 添加文章
     * @param content
     */
    void addContent(UserContent content);

    /**
     * 根据用户id查询文章集合
     * @param uid
     * @return
     */
    List<UserContent> findByUserId(Long uid);

    /**
     * 查询所有文章
     * @return
     */
    List<UserContent> findAll();

    /**
     * 根据文章id查找文章
     * @param id
     * @return
     */
    UserContent findById(long id);
    /**
     * 根据文章id更新文章
     * @param content
     * @return
     */
    void updateById(UserContent content);

    /**
     * 根据用户名字查找分类
     * @param uid
     * @return
     */
    List<UserContent> findCategoryByuid(Long uid);

    /**
     * 查找分类文章
     * @param category
     * @param uid
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageHelper.Page<UserContent> findByCategroy(String category,Long uid,Integer pageNum,Integer pageSize);

    /**
     * 查找私密文章
     * @param uid
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageHelper.Page<UserContent> findPersonalContent(Long uid,Integer pageNum,Integer pageSize);

    /**
     * 添加文章，返回主键id
     * @param userContent
     * @return
     */
    int addUserContent(UserContent userContent);

    /**
     * 删除文章
     * @param id
     */
    void deleteContentById(Long id);
}
