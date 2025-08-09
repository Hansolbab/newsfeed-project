package com.example.newsfeedproject.category.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;


    //param을 이용해서 조회할 것이기 때문에 저장은 enum이름을 저장
    //toString() 오버라이딩해도 무시
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private Long feedId;
}
