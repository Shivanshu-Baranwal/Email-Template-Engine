package com.brightly.service;

import com.brightly.mailengine.proto.Message;
import com.brightly.mailengine.proto.Messages;
import com.brightly.mailengine.proto.MutinyMailEngineGrpc;
import com.brightly.mailengine.proto.Response;
//import freemarker.template.Configuration;
//import freemarker.template.Template;

//import com.github.mustachejava.DefaultMustacheFactory;
//import com.github.mustachejava.Mustache;
//import com.github.mustachejava.MustacheFactory;
import io.quarkus.grpc.GrpcService;
import io.quarkus.mailer.Mail;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
    For thymeleaf
     */

    @Inject
    TemplateEngine templateEngine;

    @Override
    public Uni<Response> sendMessage(Messages request) {


        List<Message> msgs = request.getMessagesList();
        List<Mail> mails = new ArrayList<>();

        for (Message msg : msgs) {
            Context context = new Context();
            context.setVariable("Subject", msg.getSubject()); // Set dynamic value
            context.setVariable("Body", msg.getBody()); // Set dynamic value

            String renderedTemplate = templateEngine.process("email-template.html", context);

            mails.add(Mail.withHtml(msg.getEmail(), msg.getSubject(), renderedTemplate));
        }

        for (Mail mail : mails) {
        logger.info("\n" + mail.getTo() + "\n" + mail.getSubject() + "\n" + mail.getHtml());
        }


        return Uni.createFrom().item(Response.newBuilder().setResponseMessage("Successfully Send Email").build());
    }

}

