package io.idhub.jwt.respositories;

import io.idhub.jwt.model.ApplicationConfig;
import io.idhub.jwt.model.Key;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ApplicationConfigRepository extends ReactiveCrudRepository<ApplicationConfig, String> {

    Mono<ApplicationConfig> findByApplicationId(String applicationId);

}