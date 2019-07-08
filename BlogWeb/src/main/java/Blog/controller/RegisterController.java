package Blog.controller;

import Blog.common.CodeCaptchaServlet;
import Blog.entity.User;
import Blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;


@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    /**
     * 验证电话号码是否被注册
     * @param model
     * @param phone
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/checkPhone",method = RequestMethod.POST)
    public Map<String,String> checkPhone(Model model, @RequestParam(value = "phone",required = false) String phone){
        Map<String,String> map = new HashMap<>();
        //该电话号码未注册
        if(userService.findByPhone(phone)==null){
            map.put("message","success");
        }
        else{
            map.put("message","fail");
        }
        return map;
    }

    /**
     * 邮箱是否被注册
     * @param model
     * @param email
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "checkEmail",method = RequestMethod.POST)
    public Map<String,String> checkEmail(Model model,@RequestParam(value = "email",required = false) String email){
        Map<String,String> map = new HashMap<>();
        if(userService.findByEmail(email)==null){
            map.put("message","success");
        }else{
            map.put("message","fail");
        }
        return map;
    }

    /**
     * 验证码的验证
     * @param model
     * @param code
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/checkCode",method = RequestMethod.POST)
    public Map<String,String> checkCode(Model model,@RequestParam(value = "code",required = false) String code){
        Map<String,String> map = new HashMap<>();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取之前存入到session中的验证码
        String vcode =  (String) attributes.getRequest().getSession().getAttribute(CodeCaptchaServlet.VERCODE_KEY);
        if(code.equals(vcode))
            map.put("message","success");
        else
            map.put("message","fail");
        return map;
    }

    /**
     * 用户注册
     * @param model
     * @param phone
     * @param email
     * @param password
     * @param nickName
     * @return
     */
    @RequestMapping(value = "/doRegister",method = RequestMethod.POST)
    public String register(Model model,
                           @RequestParam(value = "phone",required = false) String phone,
                           @RequestParam(value = "email",required = false) String email,
                           @RequestParam(value = "password",required = false) String password,
                           @RequestParam(value = "nickName",required = false) String nickName,
                           @RequestParam(value = "code",required = false) String code){

        User user = new User();
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(password);
        user.setNickName(nickName);
        if(userService.register(user)==1)
            return "/register/registerSuccess";
        else
            return "/register";
    }
}
