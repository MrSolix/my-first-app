package by.dutov.jee.repository.person.postgres;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.repository.DAOInterface;
import by.dutov.jee.repository.RepositoryDataSource;
import by.dutov.jee.repository.group.postgres.GroupDAOPostgres;
import by.dutov.jee.service.exceptions.ApplicationException;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static by.dutov.jee.repository.RepositoryDataSource.connectionType;


@Slf4j
@Repository("postgresPerson")
@Lazy
public class PersonDAOPostgres extends AbstractPersonDAOPostgres implements DAOInterface<Person> {
    public static final String DELETE_GRADES_BY_STUDENT_ID = "delete from grades gr where gr.student_id = ?;";
    public static final String DELETE_STUDENT_FROM_GROUP = "delete from group_student gs where gs.student_id = ?;";
    public static final String DELETE_TEACHER_FROM_GROUP = "update \"group\" g set teacher_id = null where teacher_id = ?";
    //language=SQL
    public static final String SELECT_GROUP_FOR_STUDENT = "select " +
            "g.id g_id from users u " +
            "left join group_student gs " +
            "on u.id = gs.student_id " +
            "left join \"group\" g " +
            "on g.id = gs.group_id ";
    //language=SQL
    public static final String SELECT_GROUP_FOR_TEACHER = "select " +
            "g.id g_id from users u " +
            "left join \"group\" g " +
            "on g.teacher_id = u.id ";
    public static final String SELECT_USER =
            "select " +
                    "u.id u_id, " +
                    "u.user_name u_user_name, u.password u_pass, u.salt u_salt, " +
                    "u.name u_name, u.age u_age, u.roles u_role " +
                    "from users u";
    //language=SQL
    public static final String SELECT_GRADES = "select " +
            "g.grade g_grade, g.theme_name g_theme_name, g.id g_id " +
            "from grades g " +
            "left join users u " +
            "on u.id = g.student_id ";
    //language=SQL
    public static final String SELECT_SALARY = "select s.salary s_salary from salaries s";
    //language=SQL
    public static final String INSERT_USER = "insert into users (user_name, password, salt, \"name\", age, roles)" +
            " values (?, ?, ?, ?, ?, ?) returning id;";
    //language=SQL
    public static final String UPDATE_USER = "update users u " +
            "set user_name = ?, password = ?, salt = ?, name = ?, age = ?, roles = ?";
    //language=SQL
    public static final String DELETE_USER = "delete from users u ";
    public static final String WHERE_TEACHER_ID = " where s.teacher_id = ?;";
    public static final String WHERE_USER_NAME = " where u.user_name = ? ";
    public static final String WHERE_USER_ID = " where u.id = ? ";
    //language=SQL
    public static final String SELECT_USER_BY_NAME = SELECT_USER + WHERE_USER_NAME;
    public static final String SELECT_USER_BY_ID = SELECT_USER + WHERE_USER_ID;
    public static final String SELECT_GRADES_BY_USERNAME = SELECT_GRADES + WHERE_USER_NAME;
    public static final String SELECT_SALARY_BY_TEACHER_ID = SELECT_SALARY + WHERE_TEACHER_ID;
    public static final String SELECT_GROUP_BY_TEACHER_ID = SELECT_GROUP_FOR_TEACHER + WHERE_USER_ID;
    public static final String SELECT_GROUP_BY_STUDENT_ID = SELECT_GROUP_FOR_STUDENT + WHERE_USER_ID;
    public static final String UPDATE_USER_BY_ID = UPDATE_USER + WHERE_USER_ID;
    public static final String DELETE_USER_BY_ID = DELETE_USER + WHERE_USER_NAME;

    public static final String G_ID = "g_id";
    public static final String U_USER_NAME = "u_user_name";
    public static final String U_PASS = "u_pass";
    public static final String U_SALT = "u_salt";
    public static final String U_ID = "u_id";
    public static final String U_NAME = "u_name";
    public static final String U_AGE = "u_age";
    public static final String U_ROLE = "u_role";
    public static final String G_GRADE = "g_grade";
    public static final String G_THEME_NAME = "g_theme_name";
    public static final String S_SALARY = "s_salary";
    public static final String UPDATE_GRADES_BY_STUDENT_ID = "update grades g set theme_name = ?, grade = ? where g.student_id = ? and g.id = ?";
    public static final String INSERT_GRADES_BY_STUDENT_ID = "insert into grades (theme_name, grade, student_id) values (?, ?, ?)";

    private final RepositoryDataSource repositoryDataSource;
    private final GroupDAOPostgres groupDAOPostgres;

    @Autowired
    public PersonDAOPostgres(RepositoryDataSource repositoryDataSource, GroupDAOPostgres groupDAOPostgres) {
        super(repositoryDataSource);
        this.repositoryDataSource = repositoryDataSource;
        this.groupDAOPostgres = groupDAOPostgres;
    }

    @Override
    public Person save(Person person) {
        connectionType = ConnectionType.MANY;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = repositoryDataSource.getConnection();
            Person user = super.save(person);
            if (Role.TEACHER.equals(person.getRole())) {
                saveSalary(((Teacher) person), con, ps);
            }
            repositoryDataSource.commitMany(con);
            return user;
        } catch (DataBaseException e) {
            repositoryDataSource.rollBack(con);
            log.error("Пользователь не сохранен", e);
            throw new DataBaseException(e);
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error("Ошибка с сохранением", e);
            throw new ApplicationException(e);
        } finally {
            repositoryDataSource.closeQuietly(ps);
            repositoryDataSource.close(con);
        }
    }

    private int saveSalary(Teacher teacher, Connection con, PreparedStatement ps) throws SQLException {
        double salary = findSalary(teacher.getId());
        if (salary != -1) {
            ps = con.prepareStatement("update salaries s set salary = ? where s.teacher_id = ?");
            ps.setDouble(1, salary);
            ps.setInt(2, teacher.getId());
        } else {
            ps = con.prepareStatement("insert into salaries (teacher_id, salary) values (?, ?)");
            ps.setInt(1, teacher.getId());
            ps.setDouble(2, teacher.getSalary());
        }
        return ps.executeUpdate();
    }

    @Override
    public Optional<Person> find(String name) {
        return getUser(name, null);
    }

    @Override
    public Optional<Person> find(Integer id) {
        return getUser(null, id);
    }

    @Override
    public Person update(Integer id, Person person) {

        Connection con = null;
        PreparedStatement ps = null;
        try {
            Optional<Person> optionalPerson = super.find(id);
            Person oldPerson = optionalPerson.get();
            con = repositoryDataSource.getConnection();
            Person newPerson = setPersonFields(oldPerson, person);
            super.update(newPerson.getId(), newPerson);
            if (Role.STUDENT.equals(newPerson.getRole())) {
                return updateStudent(((Student) oldPerson), ((Student) newPerson), ((Student) person), con, ps);
            }
            if (Role.TEACHER.equals(newPerson.getRole())) {
                return updateTeacher(((Teacher) newPerson), ((Teacher) person), con, ps);
            }
            return newPerson;
        } catch (DataBaseException dataBaseException) {
            repositoryDataSource.rollBack(con);
            log.error(dataBaseException.getMessage(), dataBaseException);
            throw new DataBaseException(dataBaseException);
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error("Ошибка в общем методе изменения пользователя", e);
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(ps);
        }
    }

    private Teacher updateTeacher(Teacher newTeacher, Teacher teacher, Connection con, PreparedStatement ps) throws SQLException {
        Double salary = teacher.getSalary();
        Group group = teacher.getGroup();
        if (group != null) {
            saveGroup(newTeacher, teacher, con, ps);
        }
        if (salary != null) {
            newTeacher.setSalary(salary);
            saveSalary(newTeacher, con, ps);
        }
        return newTeacher;
    }

    private void saveGroup(Teacher newTeacher, Teacher teacher, Connection con, PreparedStatement ps) throws SQLException {
        Optional<Group> optionalGroup = groupDAOPostgres.find(teacher.getGroup().getId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            if (group.getTeacher() == null) {
                Optional<Person> person = super.find(teacher.getId());
                if (person.isPresent()) {
                    removeTeacherFromGroup(con, ps, ((Teacher) person.get()));
                }
                group.setTeacher(teacher);
                groupDAOPostgres.save(group);
                newTeacher.setGroup(group);
            }
        }
    }

    private Student updateStudent(Student oldStudent, Student newStudent, Student student, Connection con, PreparedStatement ps) throws SQLException {
        Set<Group> groups = student.getGroups();
        List<Grade> grades = student.getGrades();
        if (groups != null && !groups.isEmpty()) {
            newStudent.setGroups(groups);
            groups.forEach(group -> groupDAOPostgres.saveStudentInGroup(group, newStudent));
        }
        if (grades != null && !grades.isEmpty()) {
            List<Grade> newGrades = saveGrades(oldStudent, student, con, ps);
            newStudent.setGrades(newGrades);
        }
        return newStudent;
    }

    private List<Grade> saveGrades(Student oldStudent, Student student, Connection con, PreparedStatement ps) throws SQLException {
        List<Grade> grades = getGrades(oldStudent.getUserName());
        List<Grade> studentGrades = student.getGrades();
        Set<Grade> allGrades = new HashSet<>(grades);
        allGrades.removeAll(studentGrades);
        allGrades.addAll(studentGrades);
        if (allGrades.isEmpty()) {
            return new ArrayList<>();
        }
        List<Grade> updateGrades = equalsGradeLists(grades, new ArrayList<>(allGrades));
        for (Grade g : updateGrades) {
            saveOneGrade(student, con, g, UPDATE_GRADES_BY_STUDENT_ID, ps);
        }
        List<Grade> newGrades = checkNewGrades(allGrades, updateGrades);
        for (Grade g : newGrades) {
            saveOneGrade(student, con, g, INSERT_GRADES_BY_STUDENT_ID, ps);
        }
        List<Grade> result = new ArrayList<>();
        result.addAll(updateGrades);
        result.addAll(newGrades);
        return result;
    }

    private void saveOneGrade(Student student, Connection con, Grade g, String sql, PreparedStatement ps) throws SQLException {
        ps = con.prepareStatement(sql);
        if (g.getId() != null) {
            ps.setInt(4, g.getId());
        }
        ps.setString(1, g.getThemeName());
        ps.setInt(2, g.getGrade());
        ps.setInt(3, student.getId());
        if (ps.executeUpdate() <= 0) {
            throw new DataBaseException("Ошибка с записью оценок");
        }
    }

    private List<Grade> checkNewGrades(Set<Grade> allGrades, List<Grade> updateGrades) {
        List<Grade> result = new ArrayList<>();
        allGrades.removeAll(updateGrades);
        for (Grade allGrade : allGrades) {
            Grade newGrade = new Grade();
            if (allGrade.getThemeName() != null) {
                newGrade.setThemeName(allGrade.getThemeName());
            } else {
                newGrade.setThemeName("Math");
            }
            if (allGrade.getGrade() != null) {
                newGrade.setGrade(allGrade.getGrade());
            } else {
                newGrade.setGrade(0);
            }
            result.add(newGrade);
        }
        return result;
    }

    private List<Grade> equalsGradeLists(List<Grade> oldGrades, List<Grade> allGrades) {
        List<Grade> result = new ArrayList<>();
        Map<Integer, Grade> mapGrades = new HashMap<>();
        oldGrades.forEach(grade -> mapGrades.putIfAbsent(grade.getId(), grade));
        for (Grade allGrade : allGrades) {
            if (oldGrades.contains(allGrade)) {
                Grade oldGrade = mapGrades.get(allGrade.getId());
                Grade newGrade = new Grade();
                newGrade.setId(oldGrade.getId());
                if (allGrade.getThemeName() != null) {
                    newGrade.setThemeName(allGrade.getThemeName());
                } else {
                    newGrade.setThemeName(oldGrade.getThemeName());
                }
                if (allGrade.getGrade() != null) {
                    newGrade.setGrade(allGrade.getGrade());
                } else {
                    newGrade.setGrade(oldGrade.getGrade());
                }
                result.add(newGrade);

            }
        }
        return result;
    }

    private Person setPersonFields(Person oldPerson, Person person) {
        String userName = person.getUserName();
        byte[] password = person.getPassword();
        byte[] salt = person.getSalt();
        String name = person.getName();
        Integer age = person.getAge();
        if (userName != null) {
            oldPerson.setUserName(userName);
        }
        if (password != null) {
            oldPerson.setPassword(password);
        }
        if (salt != null) {
            oldPerson.setSalt(salt);
        }
        if (name != null) {
            oldPerson.setName(name);
        }
        if (age != null) {
            oldPerson.setAge(age);
        }
        return oldPerson;
    }

    @Override
    public Person remove(Person person) {
        connectionType = ConnectionType.MANY;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = repositoryDataSource.getConnection();
            Optional<? extends Person> user = super.find(person.getUserName());
            if (Role.STUDENT.equals(user.get().getRole())) {
                Student student = (Student) user.get();
                removeGrades(con, ps, student);
                removeStudentFromGroups(con, ps, student);
            } else if (Role.TEACHER.equals(user.get().getRole())) {
                Teacher teacher = (Teacher) user.get();
                removeTeacherFromGroup(con, ps, teacher);
            }
            super.remove(person);
            repositoryDataSource.commitMany(con);
            return person;
        } catch (DataBaseException | SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error("Не удалось удалить пользователя.");
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(ps);
            repositoryDataSource.close(con);
        }
    }

    @Override
    protected List<Person> resultSetToEntities(ResultSet rs) throws SQLException {
        Map<Integer, Person> personMap = new ConcurrentHashMap<>();
        while (rs.next()) {
            final int id = rs.getInt(U_ID);
            final String userName = rs.getString(U_USER_NAME);
            final byte[] pass = rs.getBytes(U_PASS);
            final byte[] salt = rs.getBytes(U_SALT);
            final String name = rs.getString(U_NAME);
            final int age = rs.getInt(U_AGE);
            final Role role = Role.getTypeByStr(rs.getString(U_ROLE));

            if (Role.STUDENT.equals(role)) {
                personMap.putIfAbsent(id, new Student()
                        .withId(id)
                        .withUserName(userName)
                        .withBytePass(pass)
                        .withSalt(salt)
                        .withName(name)
                        .withAge(age)
                        .withRole(role));
            }
            if (Role.TEACHER.equals(role)) {
                personMap.putIfAbsent(id, new Teacher()
                        .withId(id)
                        .withUserName(userName)
                        .withBytePass(pass)
                        .withSalt(salt)
                        .withName(name)
                        .withAge(age)
                        .withRole(role));
            }
            if (Role.ADMIN.equals(role)) {
                personMap.putIfAbsent(id, new Admin()
                        .withId(id)
                        .withUserName(userName)
                        .withBytePass(pass)
                        .withSalt(salt)
                        .withName(name)
                        .withAge(age)
                        .withRole(role));
            }
        }
        Collection<Person> values = personMap.values();
        return values.isEmpty() ? new ArrayList<>() : new ArrayList<>(values);
    }

    @Override
    protected String selectUser() {
        return SELECT_USER;
    }

    @Override
    protected String deleteUser() {
        return DELETE_USER_BY_ID;
    }

    @Override
    protected String updateUser() {
        return UPDATE_USER_BY_ID;
    }

    @Override
    protected String insertUser() {
        return INSERT_USER;
    }

    @Override
    protected String selectUserById() {
        return SELECT_USER_BY_ID;
    }

    @Override
    protected String selectUserByName() {
        return SELECT_USER_BY_NAME;
    }

    private Optional<Person> getUser(String name, Integer id) {
        connectionType = ConnectionType.MANY;
        Connection con = null;
        try {
            Optional<Person> person;
            if (name != null) {
                person = super.find(name);
            } else {
                person = super.find(id);
            }
            con = repositoryDataSource.getConnection();
            Person user = person.get();
            if (Role.STUDENT.equals(user.getRole())) {
                Student student = (Student) user;
                student.setGrades(getGrades(user.getUserName()));
                Set<Group> groups = getGroup(student.getId(), SELECT_GROUP_BY_STUDENT_ID);
                student.setGroups(groups);
                repositoryDataSource.commitMany(con);
                return Optional.of(student);
            } else if (Role.TEACHER.equals(user.getRole())) {
                Teacher teacher = (Teacher) user;
                teacher.setSalary(findSalary(teacher.getId()));
                Group group = getGroup(teacher.getId(), SELECT_GROUP_BY_TEACHER_ID)
                        .stream().findFirst().orElse(null);
                teacher.setGroup(group);
                repositoryDataSource.commitMany(con);
                return Optional.of(teacher);
            } else {
                repositoryDataSource.commitMany(con);
                return Optional.of(user);
            }
        } catch (DataBaseException e) {
            repositoryDataSource.rollBack(con);
            return Optional.empty();
        } catch (SQLException s) {
            repositoryDataSource.rollBack(con);
            log.error("Ошибка в общем методе поиска пользователя", s);
            throw new DataBaseException(s);
        } finally {
            repositoryDataSource.close(con);
        }
    }


    private Set<Group> getGroup(Integer userId, String sql) {
        Connection con;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<Integer, Group> groupMap = new ConcurrentHashMap<>();
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                int gId = rs.getInt(G_ID);
                groupDAOPostgres.find(gId).ifPresent(group -> putIfAbsentAndReturn(groupMap, gId, group));
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении группы", e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps);
        }
        Collection<Group> values = groupMap.values();
        return values.isEmpty() ? new HashSet<>() : new HashSet<>(values);
    }

    private List<Grade> getGrades(String name) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(SELECT_GRADES_BY_USERNAME);
            ps.setString(1, name);
            rs = ps.executeQuery();
            List<Grade> grades = new ArrayList<>();
            while (rs.next()) {
                int gId = rs.getInt(G_ID);
                String tName = rs.getString(G_THEME_NAME);
                int gGrade = rs.getInt(G_GRADE);


                grades.add(new Grade()
                        .withId(gId)
                        .withName(tName)
                        .withGrade(gGrade)
                );
            }
            repositoryDataSource.commitSingle(con);
            return grades;
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps);
        }
    }

    private double findSalary(Integer id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = repositoryDataSource.getConnection();
            ps = con.prepareStatement(SELECT_SALARY_BY_TEACHER_ID);
            ps.setDouble(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(S_SALARY);
            }
            return -1;
        } catch (SQLException e) {
            repositoryDataSource.rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            repositoryDataSource.closeQuietly(rs, ps);
        }
    }

    private void removeTeacherFromGroup(Connection con, PreparedStatement ps, Teacher teacher) throws SQLException {
        Set<Group> group = getGroup(teacher.getId(), SELECT_GROUP_BY_TEACHER_ID);
        if (!group.isEmpty()) {
            ps = con.prepareStatement(DELETE_TEACHER_FROM_GROUP);
            ps.setInt(1, teacher.getId());
            if (ps.executeUpdate() <= 0) {
                throw new DataBaseException("Не удалось удалить учителя из группы");
            }
        }
    }

    private void removeGrades(Connection con, PreparedStatement ps,
                              Student student) throws SQLException {
        List<Grade> grades = getGrades(student.getUserName());
        if (!grades.isEmpty()) {
            ps = con.prepareStatement(DELETE_GRADES_BY_STUDENT_ID);
            ps.setInt(1, student.getId());
            if (ps.executeUpdate() <= 0) {
                throw new DataBaseException("Не удалось удалить оценки студента");
            }
        }
    }

    private void removeStudentFromGroups(Connection con, PreparedStatement ps, Student student) throws SQLException {
        Set<Group> group = getGroup(student.getId(), SELECT_GROUP_BY_STUDENT_ID);
        if (!group.isEmpty()) {
            ps = con.prepareStatement(DELETE_STUDENT_FROM_GROUP);
            ps.setInt(1, student.getId());
            if (ps.executeUpdate() <= 0) {
                throw new DataBaseException("Не удалось удалить студента из группы");
            }
        }
    }

    private static <K, V> V putIfAbsentAndReturn(Map<K, V> map, K key, V value) {
        if (key == null) {
            return null;
        }
        map.putIfAbsent(key, value);
        return map.get(key);
    }
}
