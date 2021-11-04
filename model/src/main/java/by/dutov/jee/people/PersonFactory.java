package by.dutov.jee.people;

import by.dutov.jee.people.*;

public class PersonFactory {

    private PersonFactory(){
        //factory empty private
    }

    public Person createPerson (Role role) {
        Person person = null;

        switch (role) {
            case STUDENT:
                person = new Student();
                break;
            case TEACHER:
                person = new Teacher();
                break;
            case ADMIN:
                person = new Admin();
                break;
        }
        return person;
    }
}
