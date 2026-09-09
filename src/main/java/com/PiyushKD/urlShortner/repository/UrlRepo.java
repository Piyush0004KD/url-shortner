package com.PiyushKD.urlShortner.repository;

import com.PiyushKD.urlShortner.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepo extends JpaRepository<Url,Long> {

    Optional<Url> findByShortUrl(String shortUrl);
    boolean existsByShortUrl(String shortUrl);
    List<Url> findAll();

}
