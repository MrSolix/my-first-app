package by.dutov.jee.repository.person.postgres;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.repository.group.postgres.GroupDAOPostgres;
import by.dutov.jee.service.exceptions.ApplicationException;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static by.dutov.jee.repository.RepositoryDataSource.commitMany;
import static by.dutov.jee.repository.RepositoryDataSource.commitSingle;
import static by.dutov.jee.repository.RepositoryDataSource.connectionType;
import static by.dutov.jee.utils.DataBaseUtils.closeAndRemove;
import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;

@Slf4j
public class PersonDAOPostgres extends AbstractPersonDAOPostgres<Person> {
    public static final String DELETE_GRADES_BY_STUDENT_ID = "delete from grades gr where gr.student_id = ?;";
    public static final String DELETE_STUDENT_FROM_GROUP = "delete from group_student gs where gs.student_id = ?;";
    public static final String DELETE_TEACHER_FROM_GROUP = "update \"group\" g set teacher_id = null where teacher_id = ?";
    private static volatile PersonDAOPostgres instance;
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
                    "u.name u_name, u.age u_age, u.role u_role " +
                    "from users u";
    //language=SQL
    public static final String SELECT_GRADES = "select " +
            "g.grade g_grade, t.name t_name " +
            "from grades g " +
            "left join users u " +
            "on u.id = g.student_id " +
            "left join theme t " +
            "on t.id = g.theme_id";
    //language=SQL
    public static final String SELECT_SALARY = "select s.salary s_salary from salaries s";
    //language=SQL
    public static final String INSERT_USER = "insert into users (user_name, password, salt, \"name\", age, role)" +
            " values (?, ?, ?, ?, ?, ?) returning id;";
    //language=SQL
    public static final String UPDATE_USER = "update users u " +
            "set user_name = ?, password = ?, salt = ?, name = ?, age = ?, role = ?";
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
    public static final String T_NAME = "t_name";
    public static final String S_SALARY = "s_salary";

    public PersonDAOPostgres() {
        //singleton
    }

    public static PersonDAOPostgres getInstance() {
        if (instance == null) {
            synchronized (PersonDAOPostgres.class) {
                if (instance == null) {
                    instance = new PersonDAOPostgres();
                }
            }
        }
        return instance;
    }

    @Override
    public Person save(Person person) {
        connectionType = ConnectionType.MANY;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataSource.getConnection();
            Person user = super.save(person);
            if (Role.TEACHER.equals(person.getRole())) {
                Teacher teacher = ((Teacher) user);
                int executeUpdate = saveSalary(teacher, con, ps);
                if (executeUpdate <= 0) {
                    throw new DataBaseException("Не удалось сохранить или изменить зарплату");
                }
            }
            commitMany(con);
            return user;
        } catch (DataBaseException e) {
            rollBack(con);
            log.error("Пользователь не сохранен", e);
            throw new DataBaseException(e);
        } catch (SQLException e) {
            rollBack(con);
            log.error("Ошибка с сохранением", e);
            throw new ApplicationException(e);
        } finally {
            closeQuietly(ps);
            closeAndRemove(con);
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
    public Optional<? extends Person> find(String name) {
        return getUser(name, null);
    }

    @Override
    public Optional<? extends Person> find(Integer id) {
        return getUser(null, id);
    }

    @Override
    public Person update(Integer id, Person person) {
        person.setId(id);
        return save(person);
    }

    @Override
    public Person remove(Person person) {
        connectionType = ConnectionType.MANY;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataSource.getConnection();
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
            commitMany(con);
            return person;
        } catch (DataBaseException | SQLException e) {
            rollBack(con);
            log.error("Не удалось удалить пользователя.");
            throw new DataBaseException(e);
        } finally {
            closeQuietly(ps);
            closeAndRemove(con);
        }
    }

    @Override
    public List<? extends Person> findAll() {
        return super.findAll();
    }

    @Override
    protected List<? extends Person> resultSetToEntities(ResultSet rs) throws SQLException {
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

    private Optional<? extends Person> getUser(String name, Integer id) {
        connectionType = ConnectionType.MANY;
        Connection con = null;
        try {
            Optional<? extends Person> person;
            if (name != null) {
                person = super.find(name);
            } else {
                person = super.find(id);
            }
            con = dataSource.getConnection();
            Person user = person.get();
            if (Role.STUDENT.equals(user.getRole())) {
                Student student = (Student) user;
                student.setGrades(getGrades(user.getUserName()));
                Set<Group> groups = getGroup(student, SELECT_GROUP_BY_STUDENT_ID);
                student.setGroups(groups);
                commitMany(con);
                return Optional.of(student);
            } else if (Role.TEACHER.equals(user.getRole())) {
                Teacher teacher = (Teacher) user;
                teacher.setSalary(findSalary(teacher.getId()));
                Group group = getGroup(teacher, SELECT_GROUP_BY_TEACHER_ID)
                        .stream().findFirst().orElse(null);
                teacher.setGroup(group);
                commitMany(con);
                return Optional.of(teacher);
            } else {
                commitMany(con);
                return Optional.of(((Admin) user));
            }
        } catch (DataBaseException e) {
            rollBack(con);
            return Optional.empty();
        } catch (SQLException s) {
            rollBack(con);
            log.error("Ошибка в общем методе поиска пользователя", s);
            throw new DataBaseException(s);
        } finally {
            closeAndRemove(con);
        }
    }


    private Set<Group> getGroup(Person user, String sql) {
        GroupDAOPostgres instance = GroupDAOPostgres.getInstance(RepositoryFactory.getDataSource());
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<Integer, Group> groupMap = new ConcurrentHashMap<>();
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, user.getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                int gId = rs.getInt(G_ID);
                putIfAbsentAndReturn(groupMap, gId, instance.find(gId).orElse(new Group()));
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении группы", e);
        } finally {
            closeQuietly(rs, ps);
        }
        Collection<Group> values = groupMap.values();
        return values.isEmpty() ? new HashSet<>() : new HashSet<>(values);
    }

    private List<Grade> getGrades(String name) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_GRADES_BY_USERNAME);
            ps.setString(1, name);
            rs = ps.executeQuery();
            List<Grade> grades = new ArrayList<>();
            while (rs.next()) {
                String tName = rs.getString(T_NAME);
                int gGrade = rs.getInt(G_GRADE);

                grades.add(new Grade()
                        .withName(tName)
                        .withGrade(gGrade)
                );
            }
            commitSingle(con);
            return grades;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps);
        }
    }

    private double findSalary(Integer id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement(SELECT_SALARY_BY_TEACHER_ID);
            ps.setDouble(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(S_SALARY);
            }
            return -1;
        } catch (SQLException e) {
            rollBack(con);
            log.error(e.getMessage());
            throw new DataBaseException(e);
        } finally {
            closeQuietly(rs, ps);
        }
    }

    private void removeTeacherFromGroup(Connection con, PreparedStatement ps, Teacher teacher) throws SQLException {
        Set<Group> group = getGroup(teacher, SELECT_GROUP_BY_TEACHER_ID);
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
        Set<Group> group = getGroup(student, SELECT_GROUP_BY_STUDENT_ID);
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

    public static void main(String[] args) {
        PersonDAOPostgres instance = PersonDAOPostgres.getInstance();
        Student student = new Student()
                .withUserName("test")
                .withPassword("123")
                .withName("test")
                .withAge(30);
        System.out.println(instance.remove(student));
    }
}
