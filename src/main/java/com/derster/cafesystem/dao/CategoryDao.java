package com.derster.cafesystem.dao;

import com.derster.cafesystem.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
@Repository
public interface CategoryDao extends JpaRepository<Category, Integer> {

    List<Category> getAllCategory();
}
