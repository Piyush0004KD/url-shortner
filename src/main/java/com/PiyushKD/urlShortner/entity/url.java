package com.PiyushKD.urlShortner.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="url")
public class url {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_seq"
    )
    @SequenceGenerator(
            name="user_seq",
            sequenceName = "user_id_seq",
            allocationSize = 1
    )
    private Long id;

    @NotBlank
    private String longUrl;

    @NotBlank
    private String shortUrl;


}
