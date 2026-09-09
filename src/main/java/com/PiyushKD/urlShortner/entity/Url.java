package com.PiyushKD.urlShortner.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="url")
public class Url {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "url_seq"
    )
    @SequenceGenerator(
            name="url_seq",
            sequenceName = "url_id_seq",
            allocationSize = 1
    )
    private Long id;

    @NotBlank
    private String longUrl;

    @NotBlank
    private String shortUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;


}
