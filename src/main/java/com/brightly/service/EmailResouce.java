package com.brightly.service;

import com.brightly.mailengine.proto.Message;
import com.brightly.mailengine.proto.Messages;
import com.brightly.mailengine.proto.MutinyMailEngineGrpc;
import com.brightly.mailengine.proto.Response;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.quarkus.grpc.GrpcService;
import io.quarkus.mailer.Mail;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

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

    @Override
    public Uni<Response> sendMessage(Messages request) {



        List<Message> msgs = request.getMessagesList();
        List<Mail> mails = new ArrayList<>();

        String htmlContent = "";
        try {

            for(Message msg : msgs) {
                // Render the template with dynamic values
                StringWriter stringWriter = new StringWriter();

                // Load the template
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile("templates/mustache/email_template.mustache");

                Map<String, Object> data = new HashMap<>();
                data.put("subject", msg.getSubject());
                data.put("body", msg.getBody());

                mustache.execute(stringWriter, data).flush();
                htmlContent = stringWriter.toString();

                mails.add(Mail.withHtml(msg.getEmail(), msg.getSubject(), htmlContent));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        for(Mail mail : mails) {
            logger.info("\n" + mail.getTo() + "\n" + mail.getSubject() + "\n" + mail.getHtml()+ "\n\n");
        }

        return Uni.createFrom().item(Response.newBuilder().setResponseMessage("Successfully Send Email").build());
    }


}

