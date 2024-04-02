package com.brightly.domain;

import com.brightly.domain.ports.TemplateRepository;
import com.brightly.domain.ports.TemplateService;
import com.brightly.mailengine.proto.TemplateId;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class TemplateServiceImpl implements TemplateService {

    @Inject
    TemplateRepository templateRepository;

    @Override
    public Uni<TemplateId> createTemplate(String html) {
        return templateRepository.createTemplate(html).chain(id ->
            Uni.createFrom().item(TemplateId.newBuilder().setId(id.toString()).build()));
    }
}
