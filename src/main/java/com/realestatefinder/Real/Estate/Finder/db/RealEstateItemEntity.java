package com.realestatefinder.Real.Estate.Finder.db;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RealEstateItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String url;

    @Column(name="description",columnDefinition="LONGTEXT")
    private String description;

}
