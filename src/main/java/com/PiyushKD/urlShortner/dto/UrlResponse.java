package com.PiyushKD.urlShortner.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Data
public class UrlResponse {

    private final String shortUrl;

}
