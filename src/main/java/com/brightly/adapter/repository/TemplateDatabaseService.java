package com.brightly.adapter.repository;

import com.brightly.adapter.repository.model.Template;
import com.brightly.domain.ports.TemplateRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class TemplateDatabaseService implements TemplateRepository {

    @Inject
    TemplateDatabaseRepository templateDatabaseRepository;

    @Override
    public Uni<String> createTemplate(String html) {
        Template newTemp = new Template();
        newTemp.setHtmlContent(html);
        return persistIntoTable(newTemp);
    }

    @Override
    public Uni<Template> findByUuid(UUID templateId) {
        return templateDatabaseRepository.find("id=?1", templateId).firstResult()
                .onItem()
                .ifNull()
                .failWith(new RuntimeException("Template Not found for Id:" + templateId));
    }

    public Uni<String> persistIntoTable(Template template)
    {
        return templateDatabaseRepository.persist(template).chain(isPer -> {
            if(!templateDatabaseRepository.isPersistent(isPer)) {
                throw new RuntimeException("Fail to persist");
            }
            return Uni.createFrom().item(template.getId().toString());
        });
    }
}
