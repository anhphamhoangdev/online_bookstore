package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Category {
    @Id
    private String categoryID;
    private String categoryName;
    @OneToMany(mappedBy = "category", cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private Collection<Book> books = new ArrayList<>();

    //Getter and setter
    public String getCategoryID() {
        return categoryID;
    }
    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Collection<Book> getBooks() {
        return books;
    }
    public void setBooks(Collection<Book> books) {
        this.books = books;
    }
}