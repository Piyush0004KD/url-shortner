package com.PiyushKD.urlShortner.urlService;

import com.PiyushKD.urlShortner.dto.UrlMapper;
import com.PiyushKD.urlShortner.dto.UrlRequest;
import com.PiyushKD.urlShortner.dto.UrlResponse;
import com.PiyushKD.urlShortner.entity.Url;
import com.PiyushKD.urlShortner.repository.UrlRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UrlService {

    private final UrlRepo urlRepository;
    private final UrlMapper mapper;
    private final String characters =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public UrlResponse createShortUrl(UrlRequest request) {



        if (urlRepository.count()>= 100) {
            throw new RuntimeException("Maximum number of URLs reached");
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }

        String shortUrl = sb.toString();

        Url saveUrl = Url.builder()
                .shortUrl(shortUrl)
                .longUrl(request.getOriginalUrl())
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(saveUrl);

        return mapper.toDto(saveUrl);

    }

    public String getWebsite(String url){

        Url urlEntity = urlRepository.findByShortUrl(url)
                .orElseThrow(() -> new RuntimeException("Url not found"));

        return urlEntity.getLongUrl();

    }

    public Page<Url> getAll(Pageable pageable){

        return urlRepository.findAll(pageable);

    }

    public void deleteLink(Long id){
        Url url = urlRepository.findById(id).orElseThrow(()-> new RuntimeException("no short url"));
        urlRepository.delete(url);
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void deleteExpiredUrl(){
        List<Url> list = urlRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for(Url x : list){

            Duration duration = Duration.between(x.getCreatedAt(),now);
            if(duration.toHours()>=1){
                deleteLink(x.getId());
            }

        }

    }


}
