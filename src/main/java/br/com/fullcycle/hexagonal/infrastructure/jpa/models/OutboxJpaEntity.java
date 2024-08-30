package br.com.fullcycle.hexagonal.infrastructure.jpa.models;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import br.com.fullcycle.hexagonal.application.domain.DomainEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "Outbox")
@Table(name = "outbox")
public class OutboxJpaEntity {
    @Id
    private UUID id;

    @Column(columnDefinition = "JSON", length = 4_000)
    private String content;

    private boolean published;

    public OutboxJpaEntity() {
    }

    public OutboxJpaEntity(UUID id, String content, boolean published) {
        this.id = id;
        this.content = content;
        this.published = published;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OutboxJpaEntity outpub = (OutboxJpaEntity) o;
        return Objects.equals(id, outpub.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static OutboxJpaEntity of(DomainEvent domainEvent, final Function<DomainEvent, String> toJson) {
        return new OutboxJpaEntity(
                UUID.fromString(domainEvent.id()),
                toJson.apply(domainEvent),
                false);
    }

    public OutboxJpaEntity notePublished() {
        this.setPublished(true);
        return this;
    }
}
