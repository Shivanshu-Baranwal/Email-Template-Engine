package com.brightly.domain.ports;

import com.brightly.mailengine.proto.TemplateId;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface TemplateService {

    Uni<TemplateId> createTemplate(String html);
}
