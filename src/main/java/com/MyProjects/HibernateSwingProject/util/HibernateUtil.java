package com.MyProjects.HibernateSwingProject.util;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.MyProjects.HibernateSwingProject.model.Product;
import com.MyProjects.HibernateSwingProject.model.ProductImage;
import com.MyProjects.HibernateSwingProject.model.User;

public class HibernateUtil {
    private static  SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure()
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(ProductImage.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

  
    	public static SessionFactory getSessionFactory() {
            if (sessionFactory == null) {
                try {
                    // 1. Load the XML configuration
                    Configuration configuration = new Configuration().configure();
                    
                    // 2. Load the external properties file
                    Properties props = new Properties();
                    props.load(HibernateUtil.class.getClassLoader().getResourceAsStream("db.properties"));

                    // 3. Inject the secrets into the configuration
                    configuration.setProperty("hibernate.connection.username", props.getProperty("db.username"));
                    configuration.setProperty("hibernate.connection.password", props.getProperty("db.password"));

                    sessionFactory = configuration.buildSessionFactory();
                } catch (Exception e) {
                    System.err.println("Initial SessionFactory creation failed." + e);
                    throw new ExceptionInInitializerError(e);
                }
            }
            return sessionFactory;
        }
   
    
    public static void shutdown() {
        getSessionFactory().close();
    }
}
