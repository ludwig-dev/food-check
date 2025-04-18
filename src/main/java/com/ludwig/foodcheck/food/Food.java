package com.ludwig.foodcheck.food;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "livsmedel")
@Getter
@Setter
public class Food {
    @Id
    private int nummer;

    private String namn;

    @Column(name = "vetenskapligt_namn")
    private String vetenskapligtNamn;

    @Column(name = "livsmedels_typ_id")
    private int livsmedelsTypId;

    @Column(name = "livsmedels_typ")
    private String livsmedelsTyp;

    private String version;
    private String projekt;
}

