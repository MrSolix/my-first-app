package by.dutov.jee.repository;

public class ConstantsClass {
    //POSTGRES PERSON
    public static final String DELETE_GRADES_BY_STUDENT_ID = "delete from grades gr where gr.student_id = ?;";
    public static final String DELETE_STUDENT_FROM_GROUP = "delete from group_student gs where gs.student_id = ?;";
    public static final String DELETE_TEACHER_FROM_GROUP = "update \"group\" g set teacher_id = null where teacher_id = ?";
    public static final String SELECT_GROUP_FOR_STUDENT = "select " +
            "g.id g_id from \"group\" g " +
            "left join group_student gs " +
            "on g.id = gs.group_id " +
            "left join users u " +
            "on u.id = gs.student_id ";
    public static final String SELECT_GROUP_FOR_TEACHER = "select " +
            "g.id g_id from \"group\" g " +
            "left join users u " +
            "on g.teacher_id = u.id ";
    public static final String SELECT_USER = "select " +
            "u.id u_id, " +
            "u.user_name u_user_name, u.password u_pass, u.salt u_salt, " +
            "u.name u_name, u.age u_age, u.roles u_role " +
            "from users u";
    public static final String SELECT_USER_BY_ROLE = "select " +
            "u.id u_id, u.user_name u_user_name, u.password u_pass, " +
            "u.salt u_salt, u.name u_name, u.age u_age " +
            "from users u " +
            "where u.roles = ?;";
    public static final String SELECT_GRADES = "select " +
            "g.grade g_grade, g.theme_name g_theme_name, g.id g_id " +
            "from grades g " +
            "left join users u " +
            "on u.id = g.student_id ";
    public static final String SELECT_SALARY = "select s.salary s_salary from salaries s";
    public static final String INSERT_USER = "insert into users (user_name, password, salt, \"name\", age, roles)" +
            " values (?, ?, ?, ?, ?, ?) returning id;";
    public static final String UPDATE_USER = "update users u " +
            "set user_name = ?, password = ?, salt = ?, name = ?, age = ?, roles = ?";
    public static final String DELETE_USER = "delete from users u";
    public static final String WHERE_TEACHER_ID = " where s.teacher_id = ?;";
    public static final String WHERE_USER_NAME = " where u.user_name = ? ";
    public static final String WHERE_USER_ID = " where u.id = ? ";
    public static final String WHERE_USER_ROLE = "and u.roles = ?";
    public static final String SELECT_USER_BY_NAME = SELECT_USER + WHERE_USER_NAME;
    public static final String SELECT_USER_BY_NAME_AND_ROLE = SELECT_USER + WHERE_USER_NAME + WHERE_USER_ROLE;
    public static final String SELECT_USER_BY_ID = SELECT_USER + WHERE_USER_ID;
    public static final String SELECT_USER_BY_ID_AND_ROLE = SELECT_USER + WHERE_USER_ID + WHERE_USER_ROLE;
    public static final String SELECT_GRADES_BY_USERNAME = SELECT_GRADES + WHERE_USER_NAME;
    public static final String SELECT_SALARY_BY_TEACHER_ID = SELECT_SALARY + WHERE_TEACHER_ID;
    public static final String SELECT_GROUP_BY_TEACHER_ID = SELECT_GROUP_FOR_TEACHER + WHERE_USER_ID;
    public static final String SELECT_GROUP_BY_STUDENT_ID = SELECT_GROUP_FOR_STUDENT + WHERE_USER_ID;
    public static final String UPDATE_USER_BY_ID = UPDATE_USER + WHERE_USER_ID;
    public static final String DELETE_USER_BY_ID = DELETE_USER + WHERE_USER_ID;
    public static final String G_GRADE = "g_grade";
    public static final String G_THEME_NAME = "g_theme_name";
    public static final String S_SALARY = "s_salary";
    public static final String UPDATE_GRADES_BY_STUDENT_ID = "update grades g set theme_name = ?, grade = ? where g.student_id = ? and g.id = ?";
    public static final String INSERT_GRADES_BY_STUDENT_ID = "insert into grades (theme_name, grade, student_id) values (?, ?, ?)";

    //POSTGRES GROUP
    public static final String SELECT_ID_GROUP = "select g.id g_id from \"group\" g where g.id = ?;";
    public static final String SELECT_GROUP_ALL_FIELDS_FOR_STUDENT = "select " +
            "u.id u_id, u.user_name u_user_name, u.password u_pass, " +
            "u.salt u_salt, u.name u_name, u.age u_age, u.roles u_role " +
            "from users u " +
            "left join group_student gs " +
            "on u.id = gs.student_id " +
            "left join \"group\" g " +
            "on g.id = gs.group_id ";
    public static final String SELECT_GROUP_ALL_FIELDS_FOR_TEACHER = "select " +
            "u.id u_id, u.user_name u_user_name, u.password u_pass, " +
            "u.salt u_salt, u.name u_name, u.age u_age, u.roles u_role, t.salary t_salary " +
            "from users u " +
            "left join \"group\" g " +
            "on u.id = g.teacher_id " +
            "left join salaries t " +
            "on t.teacher_id = u.id";
    public static final String INSERT_GROUP = "insert into \"group\" (teacher_id) " +
            "values (?) returning id;";
    public static final String INSERT_STUDENT_IN_GROUP = "insert into group_student " +
            "(group_id, student_id) values (?, ?);";
    public static final String WHERE_ID = " where g.id = ?;";
    public static final String WHERE_GROUP_ID = " where gs.group_id = ? ";
    public static final String UPDATE_GROUP = "update \"group\" g set teacher_id = ?" + WHERE_ID;
    public static final String UPDATE_STUDENT_IN_GROUP = "update group_student gs set group_id = ?, student_id = ?" + WHERE_GROUP_ID +
            "and gs.student_id = ?;";
    public static final String DELETE_GROUP = "delete from \"group\" g " + WHERE_ID;
    public static final String DELETE_STUDENT_IN_GROUP = "delete from group_student gs where gs.group_id isnull";
    public static final String UPDATE_STUDENT_IN_GROUP_FOR_DELETE = "update group_student gs " +
            "set group_id = null, student_id = null" + WHERE_GROUP_ID + ";";
    public static final String SELECT_TEACHER_FOR_GROUP = SELECT_GROUP_ALL_FIELDS_FOR_TEACHER + WHERE_ID;
    public static final String SELECT_STUDENT_FOR_GROUP = SELECT_GROUP_ALL_FIELDS_FOR_STUDENT + WHERE_ID;
    public static final String T_SALARY = "t_salary";

    //POSTGRES GENERAL
    public static final int POSITION_ID = 1;
    public static final String G_ID = "g_id";
    public static final String U_ID = "u_id";
    public static final String U_USER_NAME = "u_user_name";
    public static final String U_PASS = "u_pass";
    public static final String U_SALT = "u_salt";
    public static final String U_NAME = "u_name";
    public static final String U_AGE = "u_age";
    public static final String U_ROLE = "u_role";


    //JPA
    public static final String REMOVE_TEACHER_FROM_GROUP = "update Group g set g.teacher = null where g.id = :id and g.teacher.id = :teacher_id";


    //ERRORS
    public static final String PERSON_NOT_FOUND = "Person not found";
    public static final String GROUP_NOT_FOUND = "Group not found";
    public static final String ERROR_FROM_REMOVE = "Error from remove";
    public static final String ERROR_FROM_UPDATE = "Error from update";
    public static final String ERROR_FROM_SAVE = "Error from save";
    public static final String ERROR_FROM_FIND = "Error from find";
    public static final String ERROR_FROM_FIND_ALL = "Error from findAll";
}
