package com.brightly.adapter.grpc;

import com.brightly.domain.ports.TemplateRepository;
import com.brightly.domain.ports.TemplateService;
import com.brightly.mailengine.proto.Message;
import com.brightly.mailengine.proto.Message1;
import com.brightly.mailengine.proto.MutinyMailEngineGrpc;
import com.brightly.mailengine.proto.Response;

import com.brightly.mailengine.proto.Template;
import com.brightly.mailengine.proto.TemplateId;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;


@GrpcService
public class EmailResource extends MutinyMailEngineGrpc.MailEngineImplBase {

    @Inject
    Logger logger;

    TemplateService templateService;

    @Inject
    EmailResource(TemplateService templateService) {
        this.templateService = templateService;
    }

    /*
    For thymeleaf
     */

    @Inject
    TemplateEngine templateEngine;

    @Inject
    TemplateRepository templateRepository;


    @Override
    public Uni<Response> sendMessage(Message request) {
        Context context = new Context();
        context.setVariable("Subject", request.getSubject()); // Set dynamic value
        context.setVariable("Body", request.getBody()); // Set dynamic value

        String renderedTemplate = templateEngine.process("email-template.html", context);

        logger.info(renderedTemplate);

        return Uni.createFrom().item(Response.newBuilder().setResponseMessage("Successfully Send Email").build());
    }

    @Override
    @WithTransaction
    public Uni<Response> sendMessageV2(Message1 request) {
        return templateRepository.findByUuid(UUID.fromString(request.getId())).chain(template -> renderValue(template,
                request.getSubject(), request.getBody()));
    }

    public Uni<Response> renderValue(com.brightly.adapter.repository.model.Template template, String sub, String body)
    {
        Context context = new Context();
        context.setVariable("Subject", sub);
        context.setVariable("Body", body);

        String templateContent = template.getHtmlContent();

        String renderedTemplate = templateEngine.process(templateContent, context);

        logger.info(renderedTemplate);

        return Uni.createFrom().item(Response.newBuilder().setResponseMessage("Successfully Send Email").build());
    }

    @Override
    @WithTransaction
    public Uni<TemplateId> createTemplate(Template request) {
        return templateService.createTemplate(request.getHtml());
    }
}

