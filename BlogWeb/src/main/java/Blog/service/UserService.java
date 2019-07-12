package Blog.service;

import Blog.entity.User;

public interface UserService {

    int register(User user);

    User Login(String email,String Password);

    User findByEmail(String email);

    User findByPhone(String phone);

    User findById(Long id);

    void deleteByEmail(String email);

    void update(User user);

}
