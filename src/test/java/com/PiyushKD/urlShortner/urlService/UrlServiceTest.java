package com.PiyushKD.urlShortner.urlService;

import com.PiyushKD.urlShortner.dto.UrlMapper;
import com.PiyushKD.urlShortner.dto.UrlRequest;
import com.PiyushKD.urlShortner.dto.UrlResponse;
import com.PiyushKD.urlShortner.entity.Url;
import com.PiyushKD.urlShortner.repository.UrlRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepo urlRepo;
    @Mock
    private UrlMapper mapper;
    @Mock
    private RedisTemplate<String , String> redisTemplate;
    @Mock
    ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UrlService urlService;




    @Test
    void checkingUrl_Success() {

        UrlRequest request= new UrlRequest("https://www.google.com/");
        when(urlRepo.count()).thenReturn(0L);

        UrlResponse expected = new UrlResponse("abcd12");
        when(mapper.toDto(any(Url.class))).thenReturn(expected);


        UrlResponse response = urlService.createShortUrl(request);

        assertNotNull(response);

        verify(urlRepo).save(any(Url.class));
        verify(mapper).toDto(any(Url.class));
    }

    @Test
    void checkingUrl_fail(){

        UrlRequest request = new UrlRequest("https://www.google.com/");
        when(urlRepo.count()).thenReturn(101L);

        assertThrows(
                RuntimeException.class,
                ()-> urlService.createShortUrl(request)
        );


        verify(urlRepo,never()).save(any(Url.class));
        verify(mapper,never()).toDto(any(Url.class));

    }

    @Test
    void checkingUrl_for99(){

        UrlRequest request = new UrlRequest("https://www.google.com/");
        when(urlRepo.count()).thenReturn(99L);
        UrlResponse expected = new UrlResponse("abcd12");
        when(mapper.toDto(any(Url.class))).thenReturn(expected);

        UrlResponse response = urlService.createShortUrl(request);

        assertNotNull(response);
        verify(urlRepo).save(any(Url.class));
        verify(mapper).toDto(any(Url.class));

    }

    @Test
    void checkingUrl_for100(){

        UrlRequest request = new UrlRequest("https://www.google.com/");
        when(urlRepo.count()).thenReturn(100L);
        assertThrows(
                RuntimeException.class,
                ()->  urlService.createShortUrl(request)
        );

        verify(urlRepo,never()).save(any(Url.class));
        verify(mapper,never()).toDto(any(Url.class));

    }

    @Test
    void deleteExpiredUrl_success(){

        when(urlRepo.findAll()).thenReturn(Collections.emptyList());
        urlService.deleteExpiredUrl();

        verify(urlRepo, never()).delete(any(Url.class));
        verify(redisTemplate, never()).delete(anyString());

    }

    @Test
    void deleteExpiredUrl_expired(){

        Url url = new Url();
        url.setId(1L);
        url.setShortUrl("abcd12");
        url.setCreatedAt(LocalDateTime.now().minusHours(2));
        when(urlRepo.findAll()).thenReturn(List.of(url));
        when(urlRepo.findById(1L)).thenReturn(Optional.of(url));


        urlService.deleteExpiredUrl();

        verify(redisTemplate).delete("abcd12");
        verify(urlRepo).delete(url);

    }

    @Test
    void deleteExpiredUrl_NotExpired(){

        Url url = new Url();
        url.setId(1L);
        url.setShortUrl("abc123");
        url.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        when(urlRepo.findAll()).thenReturn(List.of(url));

        urlService.deleteExpiredUrl();

        verify(urlRepo, never()).delete(any(Url.class));
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void deleteLink_Success(){
        Url url = new Url();
        url.setCreatedAt(LocalDateTime.now());
        url.setId(1L);
        url.setLongUrl("https://www.google.com/");
        url.setShortUrl("abcd12");
        when(urlRepo.findById(1L)).thenReturn(Optional.of(url));

        urlService.deleteLink(1L);

        verify(redisTemplate).delete("abcd12");
        verify(urlRepo).delete(url);


    }

    @Test
    void deleteLink_fail(){

        when(urlRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(
                RuntimeException.class,
                ()->urlService.deleteLink(1L)
        );


        verify(redisTemplate,never()).delete(anyString());
        verify(urlRepo,never()).delete(any(Url.class));

    }

    @Test
    void getAll_success() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Url> expectedPage = new PageImpl<>(List.of());

        when(urlRepo.findAll(pageable)).thenReturn(expectedPage);

        Page<Url> result = urlService.getAll(pageable);

        assertEquals(expectedPage, result);

        verify(urlRepo).findAll(pageable);
    }

    @Test
    void getWebsite_throughRedis(){
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("abcd12")).thenReturn("https://www.google.com/");
        String originalUrl = urlService.getWebsite("abcd12");

        assertEquals("https://www.google.com/",originalUrl);

        verify(urlRepo,never()).findByShortUrl("abcd12");
        verify(valueOperations, never())
                .set(anyString(), anyString(), any(Duration.class));
    }


    @Test
    void getWebsite_notThroughRedis(){
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        Url url = Url.builder()
                        .id(1L)
                        .longUrl("https://www.google.com/")
                        .shortUrl("abcd12")
                        .createdAt(LocalDateTime.now())
                                .build();

        when(valueOperations.get("abcd12"))
                .thenReturn(null);

        when(urlRepo.findByShortUrl("abcd12"))
                .thenReturn(Optional.of(url));

        String originalUrl = urlService.getWebsite(url.getShortUrl());

        assertEquals("https://www.google.com/", originalUrl);

        verify(valueOperations).set(
                eq("abcd12"),
                eq("https://www.google.com/"),
                any(Duration.class)
        );


    }

    @Test
    void getWebsite_noUrl(){
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("abcd12"))
                .thenReturn(null);

        when(urlRepo.findByShortUrl("abcd12"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                ()-> urlService.getWebsite("abcd12")
        );

        verify(urlRepo).findByShortUrl("abcd12");
        verify(valueOperations, never())
                .set(anyString(), anyString(), any(Duration.class));

    }






}