package Blog.controller;

import Blog.common.MD5Util;
import Blog.entity.User;
import Blog.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController extends BaseController{

    @Autowired
    private UserService userService;

    /**
     * 跳转login登录页
     * @return
     */
    @RequestMapping("/login")
    public String login(){
        return "../login";
    }

    @RequestMapping(value = "/doLogin",method = RequestMethod.POST)
    public String doLogin(Model model,
                          @RequestParam(value = "username",required = false) String email,
                          @RequestParam(value = "password",required = false) String password,
                          @RequestParam(value = "code",required = false) String code,
                          @RequestParam(value = "state",required = false) String state,
                          @RequestParam(value = "pageNum",required = false) Integer pageNum,
                          @RequestParam(value = "pageSize",required = false) Integer pageSize){

        //判断验证码是否右填写、是否正确。
        if(StringUtils.isBlank(code)){
            model.addAttribute("error","fail");
            return "../login";
        }
        int b = checkCode(code);
        if(b==-1){
            model.addAttribute("error","fail");
            return "../login";
        }
        else if(b==0){
            model.addAttribute("error","fail");
            return "../login";
        }
        String pwd = MD5Util.encodeToHex("salt"+password);
        User user = userService.Login(email,pwd);
        //用户已注册，查看其是否已激活
        if(user != null){
            if(!user.getState().equals("1")){
                model.addAttribute("email",email);
                model.addAttribute("error","active");
                return "../login";
            }
            else {
                getSession().setAttribute("user",user);
                model.addAttribute("user",user);
                return "redirect:/list";
            }
        }
        else {
            model.addAttribute("email",email);
            model.addAttribute("error","fail");
            return "../login";
        }

    }

    /**
     * 判断验证码是否正确
     * @param code
     * @return
     */
    public Integer checkCode(String code){
        Object vcode = getRequest().getSession().getAttribute("VERCODE_KEY");
        if(vcode == null)
            return -1;
        else if(code.equalsIgnoreCase(vcode.toString())){
            return 1;
        }
        else
            return 0;
    }

}
