package Blog.controller;

import Blog.common.CodeCaptchaServlet;
import Blog.common.MD5Util;
import Blog.entity.User;
import Blog.mail.SendEmail;
import Blog.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 验证电话号码是否被注册
     * @param model
     * @param phone
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/checkPhone")
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
    @RequestMapping(value = "checkEmail")
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
    @RequestMapping(value = "/checkCode")
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
    @RequestMapping(value = "/doRegister")
    public String register(Model model,
                           @RequestParam(value = "phone",required = false) String phone,
                           @RequestParam(value = "email",required = false) String email,
                           @RequestParam(value = "password",required = false) String password,
                           @RequestParam(value = "nickName",required = false) String nickName,
                           @RequestParam(value = "code",required = false) String code){

        if(StringUtils.isBlank(code)){
            model.addAttribute("error","非法验证码,请重新注册");
            return "../register";
        }
        if(checkCode(code)==0) {
            model.addAttribute("error","验证码不正确，请重新输入");
            return "../register";
        }else if(checkCode(code)==-1){
            model.addAttribute("error","验证码超时");
            return "../register";
        }

        User u = userService.findByEmail(email);
        if (u!=null) {
            model.addAttribute("error", "该用户已存在,请重新注册");
            return "../register";
        }
        User user = new User();
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(MD5Util.encodeToHex("salt"+password));
        user.setNickName(nickName);
        user.setImgUrl("/images/img.jpg");
        //用于用户激活，state为0时表示该用户未激活
        user.setState("0");
        user.setEnable("0");
        //生成邮件激活码
        String emailCode = MD5Util.encodeToHex("salt"+email+password);
        //24小时有效，保存激活码
        redisTemplate.opsForValue().set(email,emailCode, 24, TimeUnit.HOURS);
        userService.register(user);
        //发送邮件激活码
        SendEmail.sendEmailMessage(email,emailCode);
        String message = email+","+emailCode;
        model.addAttribute("message",message);
        return "/register/registerSuccess";
    }


    /**
     * 验证验证码的正确性
     * @param code
     * @return
     */
    public int checkCode(String code) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取之前存入到session中的验证码
        String vcode = (String) attributes.getRequest().getSession().getAttribute(CodeCaptchaServlet.VERCODE_KEY);
        if (vcode.equalsIgnoreCase(code))
            return 1;
        else if(vcode == null)
            return -1;
        else
            return 0;
    }

    @RequestMapping("/sendEmail")
    @ResponseBody
    public Map<String,Object> sendEmail(Model model){
        Map map = new HashMap<String,Object>();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取之前存入到session中的验证码
        String vcode = (String) attributes.getRequest().getSession().getAttribute(CodeCaptchaServlet.VERCODE_KEY);
        String email = attributes.getRequest().getParameter("email");
        SendEmail.sendEmailMessage(email,vcode);
        map.put("success","success");
        return map;
    }

    @RequestMapping("/register")
    public String register(Model model){
        return "../register";
    }
}
