package io.gunmarket.demo.product.repo;

import io.gunmarket.demo.product.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepo extends JpaRepository<Product, Long>, CustomProductRepo {

}