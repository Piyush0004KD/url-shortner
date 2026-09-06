package com.PiyushKD.urlShortner.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@NoArgsConstructor
public class url {

    @Id
    @Generated()
    private int id;


}
