package com.brightly.service;

import com.brightly.mailengine.proto.Message;
import com.brightly.mailengine.proto.Messages;
import com.brightly.mailengine.proto.MutinyMailEngineGrpc;
import com.brightly.mailengine.proto.Response;

import io.quarkus.grpc.GrpcService;
import io.quarkus.mailer.Mail;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.logging.Logger;

import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;


@GrpcService
public class EmailResouce extends MutinyMailEngineGrpc.MailEngineImplBase {

    @Inject
    Logger logger;

    @Inject
    VelocityEngine velocityEngine;

    @Override
    public Uni<Response> sendMessage(Messages request) {

        List<Message> msgs = request.getMessagesList();
        List<Mail> mails = new ArrayList<>();

        for (Message msg : msgs) {
            String renderedTemplate = renderEmail(msg.getSubject(), msg.getBody());
            mails.add(Mail.withHtml(msg.getEmail(), msg.getSubject(), renderedTemplate));
        }

        for (Mail mail : mails) {
        logger.info("\n" + mail.getTo() + "\n" + mail.getSubject() + "\n" + mail.getHtml());
        }

        return Uni.createFrom().item(Response.newBuilder().setResponseMessage("Successfully Send Email").build());
    }

    public String renderEmail(String subject, String body) {
        VelocityContext context = new VelocityContext();
        context.put("subject", subject);
        context.put("body", body);

        // Get the template
        Template template = velocityEngine.getTemplate("template.vm");

        // Render the template
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // Get the rendered template as a string
        String renderedTemplate = writer.toString();

        return renderedTemplate;
    }
}

