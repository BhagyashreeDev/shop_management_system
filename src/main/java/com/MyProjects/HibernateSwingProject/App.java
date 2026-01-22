package com.MyProjects.HibernateSwingProject;
import org.hibernate.Session;

import com.MyProjects.HibernateSwingProject.util.HibernateUtil;
import com.MyProjects.HibernateSwingProject.view.MainDashboard;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    try {
            // Try to open a session
            Session session = HibernateUtil.getSessionFactory().openSession();
            System.out.println("Connection Successful! Hibernate is ready.");
            session.close();
            
             //Now start your UI (We will create this next)
             MainDashboard frame = new MainDashboard();
             frame.setVisible(true);
            
        } catch (Exception e) {
            System.out.println("Connection Failed! Check your SQL settings or hibernate.cfg.xml");
            e.printStackTrace();
        }
    }
}
