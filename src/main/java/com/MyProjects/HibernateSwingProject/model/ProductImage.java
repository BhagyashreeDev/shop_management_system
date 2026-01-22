package com.MyProjects.HibernateSwingProject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

    
}
