package com.brightly.service;

import com.brightly.mailengine.proto.Message;
import com.brightly.mailengine.proto.Messages;
import com.brightly.mailengine.proto.MutinyMailEngineGrpc;
import com.brightly.mailengine.proto.Response;


import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.quarkus.grpc.GrpcService;
import io.quarkus.mailer.Mail;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@GrpcService
public class EmailResouce extends MutinyMailEngineGrpc.MailEngineImplBase {

    @Inject
    Logger logger;

    @Override
    public Uni<Response> sendMessage(Messages request) {

        List<Message> msgs = request.getMessagesList();
        List<Mail> mails = new ArrayList<>();

        for (Message msg : msgs) {
            String htmlContent = renderEmail(msg.getSubject(), msg.getBody());
            mails.add(Mail.withHtml(msg.getEmail(), msg.getSubject(), htmlContent));
        }

        for (Mail mail : mails) {
        logger.info("\n" + mail.getTo() + "\n" + mail.getSubject() + "\n" + mail.getHtml());
        }

        return Uni.createFrom().item(Response.newBuilder().setResponseMessage("Successfully Send Email").build());
    }

    public String renderEmail(String sub, String body) {
        String renderedTemplate = "";
        try {

            // Load the Handlebars template from file
            String templatePath = "C:\\Users\\z004vxjk\\IdeaProjects\\mail-engine\\src\\main\\resources\\templates\\handlebars\\email-template.hbs";
            String templateContent = new String(Files.readAllBytes(Paths.get(templatePath)));

            // Initialize Handlebars engine
            Handlebars handlebars = new Handlebars();

            // Compile the template
            Template template = handlebars.compileInline(templateContent);

            // Prepare data to render
            Map<String, Object> data = new HashMap<>();
            data.put("subject", sub);
            data.put("body", body);

            // Render the template with data
            renderedTemplate = template.apply(data);

        }catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return renderedTemplate;
    }
}

