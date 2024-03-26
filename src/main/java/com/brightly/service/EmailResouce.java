package com.brightly.service;

import com.brightly.mailengine.proto.Message;
import com.brightly.mailengine.proto.Messages;
import com.brightly.mailengine.proto.MutinyMailEngineGrpc;
import com.brightly.mailengine.proto.Response;
import freemarker.template.Configuration;
import freemarker.template.Template;

//import com.github.mustachejava.DefaultMustacheFactory;
//import com.github.mustachejava.Mustache;
//import com.github.mustachejava.MustacheFactory;
import io.quarkus.grpc.GrpcService;
import io.quarkus.mailer.Mail;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@GrpcService
public class EmailResouce extends MutinyMailEngineGrpc.MailEngineImplBase {

    @Inject
    Logger logger;

    /*
    For freemarker
     */

    @Override
    public Uni<Response> sendMessage(Messages request) {
        Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfig.setClassLoaderForTemplateLoading(Thread.currentThread().getContextClassLoader(), "/templates/freemarker");
        freemarkerConfig.setDefaultEncoding("UTF-8");

        List<Message> msgs = request.getMessagesList();

        List<Mail> mails = new ArrayList<>();

        for (Message msg : msgs) {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("subject", msg.getSubject());
            dataModel.put("message", msg.getBody());

        String emailBody = processTemplate(freemarkerConfig, "email_template.ftl", dataModel);
            mails.add(Mail.withHtml(msg.getEmail(), msg.getSubject(), emailBody));
        }

        for (Mail mail : mails) {
            logger.info("\n" + mail.getTo() + "\n" + mail.getSubject() + "\n" + mail.getHtml());
        }

        return Uni.createFrom().item(Response.newBuilder().setResponseMessage("Successfully Send Email").build());
    }

    public static String processTemplate(Configuration freemarkerConfig, String templateName, Map<String, Object> dataModel) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
}


}

