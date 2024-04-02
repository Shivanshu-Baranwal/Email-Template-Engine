package com.brightly.domain.ports;

import com.brightly.adapter.repository.model.Template;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public interface TemplateRepository {

    Uni<String> createTemplate(String html);

    Uni<Template> findByUuid(UUID templateId);
}
