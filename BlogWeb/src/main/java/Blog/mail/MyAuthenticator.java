package Blog.mail;

import javax.mail.PasswordAuthentication;

/**
 * 验证邮箱与授权码的正确性
 */

public class MyAuthenticator extends javax.mail.Authenticator {
    private String strUser;
    private String strPwd;
    public MyAuthenticator(String user, String password) {
        this.strUser = user;
        this.strPwd = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(strUser, strPwd);
    }
}
