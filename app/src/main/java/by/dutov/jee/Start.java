package by.dutov.jee;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Grades;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.EntityManagerHelper;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

public class Start {
    public static void main(String[] args) {
//        Configuration cfg = new Configuration().configure();
//        SessionFactory sessionFactory = cfg.buildSessionFactory();
        EntityManager em = EntityManagerHelper.getInstance().getEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        TypedQuery<Grades> studentQuery = em.createQuery("from Grades ", Grades.class);
        List<Grades> resultList = studentQuery.getResultList();

        resultList.forEach(student -> System.out.println("!!!!" + student));


//                System.out.println("!!!" + student);


//        Student managerStudent = new Student().withUserName("Slavik")
//                .withPassword("123")
//                .withName("Slavik")
//                .withAge(25);
//        em.persist(managerStudent);
//
//        TypedQuery<Student> query = em.createQuery("from Student where userName = 'Slavik'", Student.class);
//        Student student = query.getSingleResult();



//        Group group = em.find(Group.class, 4);
//        System.out.println("!!!" + group);

        transaction.commit();
        em.close();



//        sessionFactory.close();

//        Session session = sessionFactory.openSession();
//        Transaction transaction = session.beginTransaction();
//
//        Student student = session.find(Student.class, 4);
//        System.out.println("!!!" + student);
//
//        transaction.commit();
//        session.close();
    }
}
