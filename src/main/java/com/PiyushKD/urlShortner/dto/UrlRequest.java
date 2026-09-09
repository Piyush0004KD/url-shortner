package com.PiyushKD.urlShortner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Data
@Builder
public class UrlRequest {

    private final String originalUrl;

}
