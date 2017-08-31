package ng.monitoring;

import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.ProxyConfig;
import org.simplejavamail.mailer.config.ServerConfig;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;

/**
 * Created by adam on 8/30/2017.
 */
public class EmailUtils
{
    private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    public static void sendStatsEmail(String aStatsSummary)
    {
        String sEmailBody = String.format("%s\n\n-----------------------------", aStatsSummary);

        Email email = new Email();

        email.addNamedToRecipients("Recipient's name", "recipient@yahoo.com");
//        email.addNamedToRecipients("C. Cane", "candycane@candyshop.org");
//        email.addNamedCcRecipients("C. Bo", "chocobo@candyshop.org");
//        email.addRecipients("Tasting Group", Message.RecipientType.BCC, "taster1@cgroup.org;taster2@cgroup.org;tester <taster3@cgroup.org>");
//        email.addBccRecipients("Mr Sweetnose <snose@candyshop.org>");

        email.setFromAddress("no-reply", "no-reply@bogus.com");
        email.setReplyToAddress("no-reply", "no-reply@bogus.com");
        email.setSubject("Status Update as of 8/30/2017");
//        email.setText(sEmailBody);

        // To maintain spacing, we use an old-style <pre>...</pre> tag
        // If the email is displayed as HTML, then it appears as courier
        email.setTextHTML("<pre>" + sEmailBody + "</pre>");

        email.addHeader("X-Priority", 5);
        email.setUseReturnReceiptTo(false);

//        email.signWithDomainKey(privateKeyData, "somemail.com", "selector");

        // NOTE:  To use smtp.gmail.com, we must configure the myname@gmail.com account to allow less secure apps
        //        See https://myaccount.google.com/lesssecureapps
        Mailer mailer = new Mailer(
                new ServerConfig("smtp.gmail.com", 465, "myname@gmail.com", "my password here"), TransportStrategy.SMTP_SSL

        );

        mailer.setDebug(true);
        mailer.sendMail(email);
    }
}
