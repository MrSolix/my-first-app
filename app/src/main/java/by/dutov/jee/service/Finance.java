package by.dutov.jee.service;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Finance {
    private final Map<Teacher, Map<Integer, Double>> salaryHistory;
    private static final int CURRENT_MONTH = LocalDate.now().getMonthValue();
    private static PersonDAOInterface<Person> daoRepository;
    private static Finance instance;
    private static final String MIN_RANGE = "minRange";
    private static final String MAX_RANGE = "maxRange";

    private Finance() {
        daoRepository = RepositoryFactory.getDaoRepository();
        salaryHistory = new ConcurrentHashMap<>();
        staticSaveSalaries();
        //singleton
    }

    public Map<Teacher, Map<Integer, Double>> getSalaryHistory() {
        return salaryHistory;
    }

    public static Finance getInstance() {
        if (instance == null) {
            synchronized (Finance.class) {
                if (instance == null) {
                    instance = new Finance();
                }
            }
        }
        return instance;
    }

    private void staticSaveSalaries() {
        List<? extends Person> all = daoRepository.findAll();
        for (Person p : all) {
            if (p.getRole().equals(Role.TEACHER)) {
                Optional<? extends Person> user = daoRepository.find(p.getId());
                if (user.isPresent()) {
                    Teacher teacher = (Teacher) user.get();
                    for (int i = 1; i < CURRENT_MONTH; i++) {
                        saveSalary(teacher, i, i * 110.0);
                    }
                    saveSalary(teacher, CURRENT_MONTH, teacher.getSalary());
                }
            }
        }
    }

    public double averageSalary(Integer minRange, Integer maxRange, Teacher teacher) {
        int rangeCount = maxRange - minRange + 1;
        double avgSal = 0.0;
        if (minRange < 1 || maxRange > CURRENT_MONTH
                || rangeCount <= 0
                || teacher == null
                || !getSalaryHistory().containsKey(teacher)) {
            return -1;
        }
        avgSal = getAvgSal(teacher, minRange, maxRange, avgSal);
        return avgSal / rangeCount;
    }

    private double getAvgSal(Teacher teacher, int minRange, int maxRange, double avgSal) {
        Map<Integer, Double> integerDoubleMap = getSalaryHistory().get(teacher);
        for (int i = minRange; i <= maxRange; i++) {
            avgSal += integerDoubleMap.get(i);
        }
        return avgSal;
    }

    public void saveSalary(Teacher teacher, Integer month, Double salary) {
        if (month >= 1 && month <= CURRENT_MONTH) {
            getSalaryHistory().putIfAbsent(teacher, new HashMap<>());
            getSalaryHistory().get(teacher).putIfAbsent(month, salary);
        }
    }

    public void getSalary(HttpServletRequest req, HttpServletResponse resp, String userName) throws ServletException, IOException {
        Optional<? extends Person> person = daoRepository.find(userName);
        if (person.isEmpty() || !Role.TEACHER.equals(person.get().getRole())) {
            log.info("person == null or person role != \"TEACHER\"");
            String errorString = "the teacher's login is incorrect";
            req.setAttribute("errorStringInSalaryPage", errorString);
        } else {
            log.info("Salary = {}", ((Teacher) person.get()).getSalary());
            req.setAttribute("teacher", person.get());
        }
    }

    public void getAverageSalary(HttpServletRequest req, String userName) {
        int minRange = -1;
        int maxRange = -1;
        if (!req.getParameter(MIN_RANGE).equals("") &&
                !req.getParameter(MAX_RANGE).equals("")) {
            minRange = Integer.parseInt(req.getParameter(MIN_RANGE));
            maxRange = Integer.parseInt(req.getParameter(MAX_RANGE));
        }
        log.info("userName = {}, minRange = {}, maxRange = {}", userName, minRange, maxRange);

        Optional<? extends Person> person = daoRepository.find(userName);
        log.info("Get person from db");

        if (person.isEmpty() || !Role.TEACHER.equals(person.get().getRole())) {
            log.info("person == null or person role != \"TEACHER\"");
            CommandServletUtils.errorMessage(req, "the teacher's login is incorrect"
                    , "errorStringInAvgSalaryPage");
        } else {
            double averageSalary = averageSalary(minRange, maxRange, (Teacher) person.get());
            if (averageSalary <= 0) {
                log.info("incorrect value in fields \"minRange\" or \"maxRange\"");
                CommandServletUtils.errorMessage(req, "months are incorrect"
                        , "errorMonthsInAvgSalaryPage");
            } else {
                log.info("Average Salary = {}", averageSalary);
                req.setAttribute("averageSalary", averageSalary);
            }
        }
    }
}
