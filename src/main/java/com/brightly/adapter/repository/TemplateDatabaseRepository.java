package com.brightly.adapter.repository;

import com.brightly.adapter.repository.model.Template;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TemplateDatabaseRepository implements PanacheRepository<Template> {
}
