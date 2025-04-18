package com.ludwig.foodcheck.recipe;

import com.ludwig.foodcheck.users.User;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
@Getter
@Setter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isPublic = false;
}
