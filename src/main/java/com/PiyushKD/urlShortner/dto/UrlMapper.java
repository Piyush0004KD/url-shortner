package com.PiyushKD.urlShortner.dto;

import com.PiyushKD.urlShortner.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlMapper {

    public Url toEntity(UrlRequest request){

        return Url.builder()
                .longUrl(request.getOriginalUrl())
                .build();

    }

    public UrlResponse toDto(Url url){

        return UrlResponse.builder()
                .shortUrl(url.getShortUrl())
                .build();
    }

}
