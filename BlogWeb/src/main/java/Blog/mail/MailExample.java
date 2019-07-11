package Blog.mail;

/**
 * 测试发送邮件类
*/

public class MailExample {

    public static void main (String args[]) throws Exception {
        String email = "1456720424@qq.com";
        String validateCode = "text";
        SendEmail.sendEmailMessage(email,validateCode);
    }
}
