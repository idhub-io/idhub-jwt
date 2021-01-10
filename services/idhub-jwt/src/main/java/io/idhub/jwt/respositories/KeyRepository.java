package io.idhub.jwt.respositories;

import io.idhub.jwt.model.ApplicationConfig;
import io.idhub.jwt.model.Key;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface KeyRepository extends ReactiveCrudRepository<Key, UUID> {

    Flux<Key> findByApplicationId(String applicationId);
    Mono<Key> findByIdAndApplicationId(UUID id, String applicationId);

    Mono<Void> deleteByIdAndApplicationId(UUID id, String applicationId);

}