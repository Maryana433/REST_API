package com.maryana.restspringboot.repository;

import com.maryana.restspringboot.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {


    List<Book> findByIsDeletedFalse();

    Page<Book> findByIsDeletedFalse(Pageable pageable);

    Page<Book> findByIsDeletedFalseAndTitleContaining(String title, Pageable pageable);
    Page<Book> findByIsDeletedFalseAndAuthorContaining(String author, Pageable pageable);
    Page<Book> findByIsDeletedFalseAndTitleContainingAndAuthorContaining(String title,String author, Pageable pageable);



}
