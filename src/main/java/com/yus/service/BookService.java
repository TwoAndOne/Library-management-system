package com.yus.service;

import com.yus.pojo.Books;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Yus
 * @data 2021/2/27 18:12
 */

public interface BookService {
    //增加一个Book
    int addBook(Books book);

    //根据id删除一个Book
    int deleteBookById(@Param("bookID") int id);

    //更新Book
    int updateBook(Books books);

    //根据id查询,返回一个Book
    Books queryBookById(@Param("bookID") int id);

    //查询全部Book,返回list集合
    List<Books> queryAllBook();
}
