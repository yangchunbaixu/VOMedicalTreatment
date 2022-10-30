package com.atguigu.yygh.msm.config;

import sun.jvm.hotspot.debugger.AddressException;

import javax.websocket.Session;
import java.util.Properties;

public class MailUtils {
  /*  public static boolean sendEmail(String fromEmail,String toEmail, String emailMsg) throws MessagingException{
        Properties prop=new Properties();
        prop.setProperty("mail.host", "stmp.qq.com");
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        //使用javaMail发送邮件的5个步骤
        //创建session
        Session session=Session.getInstance(prop);
        //2.通过session得到transport对象
        Transport ts=session.getTransport();
        //3.使用邮箱的用户名和密码链接上邮件服务器，发送邮件时，发送人需要提交邮箱的用户名和密码（授权码）给smtp服务器，用户名和密码都通过验证之后
        //才能够正常发送邮件给收件人，QQ邮箱需要使用SSL，端口号465或587       ts.connect("smtp.qq.com",587,"QQ号","授权码");
        ts.connect("smtp.qq.com",587,"发送者的qq号，去掉@qq.com","授权码");
        //4、创建邮件
        Message message;
        try {
            message = createSimpleMail(fromEmail,toEmail,emailMsg,session);
            //5、发送邮件
            ts.sendMessage(message,message.getAllRecipients());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ts.close();
        return true;
    }
    *//**
     * 创建邮件
     *//*
    public static Message createSimpleMail(String fromEmail,String toEmail,String emailMsg,Session session) throws AddressException, MessagingException{
        //创建邮件
        MimeMessage message=new MimeMessage(session);
        //指明邮件的发送人
        message.setFrom(new InternetAddress(fromEmail));
        //指明收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        //设置邮件的标题
        message.setSubject("用户激活");
        //设置邮件的内容
        message.setContent(emailMsg,"text/html;charset=UTF-8");
        //返回创建好的邮件对象
        return message;
    }
    *//**
     * 邮箱发送测试
     * @param args
     * @throws MessagingException
     * @throws AddressException
     *//*
    @Test
    public void testSendMail() throws AddressException, MessagingException{

        sendEmail("发送者的qq号","接受者的qq号");


    }*/
}
