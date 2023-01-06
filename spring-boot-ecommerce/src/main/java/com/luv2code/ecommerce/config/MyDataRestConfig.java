package com.luv2code.ecommerce.config;

import com.luv2code.ecommerce.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

    @Value("${allowed.origins}")
    private String[] theAllowedOrigins;
    private EntityManager entityManager;

    // autowired jpa entity manager
    @Autowired
    public MyDataRestConfig(EntityManager theEntityManager){
        entityManager = theEntityManager;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

        HttpMethod[] theUnsupportedActions = {HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PATCH};

        // disable HTTP methods for Product: PUT, POST, DELETE and PATCH
        disableHttpMethod(config.getExposureConfiguration()
                .forDomainType(Product.class), theUnsupportedActions);

        // disable HTTP methods for ProductCategory: PUT, POST, DELETE and PATCH
        disableHttpMethod(config.getExposureConfiguration()
                .forDomainType(ProductCategory.class), theUnsupportedActions);

        disableHttpMethod(config.getExposureConfiguration()
                .forDomainType(Country.class), theUnsupportedActions);

        disableHttpMethod(config.getExposureConfiguration()
                .forDomainType(State.class), theUnsupportedActions);

        disableHttpMethod(config.getExposureConfiguration()
                .forDomainType(Order.class), theUnsupportedActions);


        // call an internal helper method
        exposeIds(config);

        // configure cors mapping
        cors.addMapping(config.getBasePath()+"/**").allowedOrigins(theAllowedOrigins);
    }

    private static void disableHttpMethod(ExposureConfigurer config, HttpMethod[] theUnsupportedActions) {
        config
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions));
    }

    private void exposeIds(RepositoryRestConfiguration config) {
        //expose entity ids

        // - get a list of all entity classes from the entity manager
        Set<javax.persistence.metamodel.EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        // - create an array of the entity types
        List<Class> entityClasses = new ArrayList<>();

        // - get the entity types for the entities
        for (EntityType<?> tempEntityType : entities) {
            entityClasses.add(tempEntityType.getJavaType());
        }

        // - expose the entity ids for the array of entity/domain types
        Class[] domainTypes = entityClasses.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);

    }
}