# Library-management-system
SSM初级项目 整合Spring+SpringMVC+Mybatis
## 项目结构

![image-20210228184100861](https://gitee.com/yuswork/my-images/raw/master/img/image-20210228184100861.png)



## 配置文件：

**applicationContext.xml**

导入以下三个文件：

- spring-dao.xml
- spring-service.xml
- spring-mvc.xml

**spring-dao.xml**

配置整合mybatis

1. 关联数据库文件

`<context:property-placeholder location="classpath:database.properties"/>`

2. 数据库连接池
3. 配置SqlSessionFactory对象
    1. 注入数据库连接池
    2. 配置MyBatis全局配置文件`mybatis-config.xml`
4. 配置扫描Dao接口包，动态实现Dao接口注入的Spring容器中

**spring-service.xml**

1. 组件扫描`com.yus.service`
2. Service实现类注入容器BookServiceImpl
3. 配置事务管理器

**spring-mvc.xml**

1. 开启SpringMVC注解驱动
2. 静态资源默认servlet配置
3. 配置视图解析器
4. 组件扫描`com.yus.controller`

**mybatis.xml**

1. setting：日志
2. typeAliases：别名
3. mapper注册

**database.properties**

1. driver
2. url
3. username
4. password

**web.xml**

`E:\IDEA work space\ssmbuild\web\WEB-INF\web.xml`

1. servlet:
    1. springframework的DispatcherServlet的注册
    2. 初始参数，把applicationContext.xml导入
    3. 启动等级：1（数字越小越高）
2. servlet-mapping
3. encodingFilter：解决乱码问题
4. Session：过期时间

## POJO层

实体类层

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Books {
    private int bookID;
    private String bookName;
    private int bookCounts;
    private String detail;
}
```



## DAO层：

BookMapper与BookMapper.xml

一个接口一个xml文件

```java
public interface BookMapper {
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
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.yus.dao.BookMapper">
    <!--增加一个Book-->
    <insert id="addBook" parameterType="Books">
        insert into ssmbuild.books(bookName,bookCounts,detail)
        values (#{bookName}, #{bookCounts}, #{detail})
    </insert>

    <!--根据id删除一个Book-->
    <delete id="deleteBookById" parameterType="int">
        delete from ssmbuild.books where bookID=#{bookID}
    </delete>

    <!--更新Book-->
    <update id="updateBook" parameterType="Books">
        update ssmbuild.books
        set bookName = #{bookName},bookCounts = #{bookCounts},detail = #{detail}
        where bookID = #{bookID}
    </update>

    <!--根据id查询,返回一个Book-->
    <select id="queryBookById" resultType="Books">
        select * from ssmbuild.books
        where bookID = #{bookID}
    </select>

    <!--查询全部Book-->
    <select id="queryAllBook" resultType="Books">
        SELECT * from ssmbuild.books
    </select>
</mapper>
```

## service层

```java
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
```

```java
public class BookServiceImpl implements BookService{
    private BookMapper bookMapper;

    public void setBookMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Override
    public int addBook(Books book) {
        return bookMapper.addBook(book);
    }

    @Override
    public int deleteBookById(int id) {
        return bookMapper.deleteBookById(id);
    }

    @Override
    public int updateBook(Books books) {
        return bookMapper.updateBook(books);
    }

    @Override
    public Books queryBookById(int id) {
        return bookMapper.queryBookById(id);
    }

    @Override
    public List<Books> queryAllBook() {
        return bookMapper.queryAllBook();
    }
}
```

## Controller层

controller层需要与视图层结合起来写，写一个功能测试一个。

1. 默认页面index.jsp显示一个按钮，点击后查询所有图书，controller层list方法接受到`/allBook`请求，执行list方法的方法体，调用service层查询所有图书，把返回值添加进Model，然后转发`allBook.jsp`页面进行显示
2. `allBook.jsp`页面有添加、删除、修改的按钮
3. 在视图也就是页面上转换到另一个页面都需要使用controller层代为操作

```java
@Controller
@RequestMapping("/book")
public class BookController {

    @Autowired
    @Qualifier("BookServiceImpl")
    private BookService bookService;

    @RequestMapping("/allBook")
    public String list(Model model){
        List<Books> list = bookService.queryAllBook();
        model.addAttribute("list",list);
        return "allBook";
    }

    @RequestMapping("/toAddBook")
    public String toAddBook(){
        return "addBook";
    }

    @RequestMapping("/addBook")
    public String addBook(Books books){
        System.out.println(books);
        bookService.addBook(books);
        return "redirect:/book/allBook";
    }

    @RequestMapping("/toUpdateBook")
    public String toUpdateBook(int id,Model model){
        System.out.println(id);
        Books book = bookService.queryBookById(id);
        model.addAttribute("book",book);
        return "updateBook";
    }

    @RequestMapping("/updateBook")
    public String updateBook(Books books){
        System.out.println(books);
        bookService.updateBook(books);
        return "redirect:/book/allBook";
    }

    @RequestMapping("/del/{bookID}")
    public String del(@PathVariable("bookID") int id){
        bookService.deleteBookById(id);
        return "redirect:/book/allBook";
    }
}
```

## View层

```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>书籍列表</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- 引入 Bootstrap -->
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="row clearfix">
        <div class="col-md-12 column">
            <div class="page-header">
                <h1>
                    <small>书籍列表 —— 显示所有书籍</small>
                </h1>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-4 column">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/book/toAddBook">新增</a>
        </div>
    </div>
    <div class="row clearfix">
        <div class="col-md-12 column">
            <table class="table table-hover table-striped">
                <thead>
                <tr>
                    <th>书籍编号</th>
                    <th>书籍名字</th>
                    <th>书籍数量</th>
                    <th>书籍详情</th>
                    <th>操作</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="book" items="${requestScope.get('list')}">
                    <tr>
                        <td>${book.getBookID()}</td>
                        <td>${book.getBookName()}</td>
                        <td>${book.getBookCounts()}</td>
                        <td>${book.getDetail()}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/book/toUpdateBook?id=${book.getBookID()}">更改</a> |
                            <a href="${pageContext.request.contextPath}/book/del/${book.getBookID()}">删除</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
```

