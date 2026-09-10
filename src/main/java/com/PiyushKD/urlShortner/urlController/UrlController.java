package com.PiyushKD.urlShortner.urlController;

import com.PiyushKD.urlShortner.dto.UrlRequest;
import com.PiyushKD.urlShortner.dto.UrlResponse;
import com.PiyushKD.urlShortner.entity.Url;
import com.PiyushKD.urlShortner.urlService.UrlService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping()
    public ResponseEntity<String> uploadUrl(@RequestBody UrlRequest request){
        UrlResponse shortUrl = urlService.createShortUrl(request);
        return ResponseEntity.ok("http://localhost:8080/"+shortUrl.getShortUrl());
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> getSite(@PathVariable String shortUrl){

        String original = urlService.getWebsite(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION,original)
                .build();
    }

    @GetMapping
    public ResponseEntity<Page<Url>> getAllUrls(Pageable pageable) {
        return ResponseEntity.ok(urlService.getAll(pageable));
    }


}
