package com.fastfoodpos;

import com.fastfoodpos.application.service.ManageProductService;
import com.fastfoodpos.config.DataSourceProvider;
import com.fastfoodpos.domain.port.out.ProductRepositoryPort;
import com.fastfoodpos.infrastructure.persistence.jdbc.JdbcProductRepositoryAdapter;

import javax.sql.DataSource;

public class MainTest {
    public static void main(String[] args) {

        DataSource ds = DataSourceProvider.getDataSource();

        ProductRepositoryPort repo = new JdbcProductRepositoryAdapter(ds);
        ManageProductService service = new ManageProductService(repo);

        // test findAll
        service.findAll().forEach(p -> System.out.println(">> " + p.getName()));

        // test findById
        service.findById(5).ifPresent(p -> System.out.println(">> encontre: " + p.getName()));
    }
}
