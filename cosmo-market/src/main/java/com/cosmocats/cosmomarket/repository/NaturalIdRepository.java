package com.cosmocats.cosmomarket.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NaturalIdRepository<T, ID> extends JpaRepository<T, ID> {

    Optional<T> findByNaturalId(ID naturalId);
}
