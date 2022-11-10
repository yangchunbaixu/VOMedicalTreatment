import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class test {
    @Test
    public void sendEmail() throws Exception {
        //QQ邮箱测试
        String userName = "2011350626@qq.com"; // 发件人邮箱
        String password = "tpdmzwbxxbcxeefa"; // 发件人密码，其实不一定是邮箱的登录密码，对于QQ邮箱来说是SMTP服务的授权文本
        String smtpHost = "smtp.qq.com"; // 邮件服务器

        //163邮箱测试
        // String userName = "gblfy02@163.com"; // 发件人邮箱
        // String password = "TBFJUSKCUOPEYOYU"; // 发件人密码，其实不一定是邮箱的登录密码，对于QQ邮箱来说是SMTP服务的授权文本
        // String smtpHost = "smtp.163.com"; // 邮件服务器

        String to = "2011350626@11.com"; // 收件人，多个收件人以半角逗号分隔
        String cc = "2011350626@qq.com"; // 抄送，多个抄送以半角逗号分隔
        String subject = "这是邮件的主题 163"; // 主题
        String body = "这是邮件的正文163"; // 正文，可以用html格式的哟
        List<String> attachments = Arrays.asList("D:\\aa.txt"); // 附件的路径，多个附件也不怕

        EmailUtils emailUtils = EmailUtils.entity(smtpHost, userName, password, to, cc, subject, body, attachments);

        emailUtils.send(); // 发送！
    }

}
