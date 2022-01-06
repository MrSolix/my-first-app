package by.dutov.jee.service.facade;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.service.person.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class Finance {
    private final Map<Integer, Map<Integer, Double>> salaryHistory;
    private static final int CURRENT_MONTH = LocalDate.now().getMonthValue();
    private final PersonService personService;

    @Autowired
    private Finance(PersonService personService) {
        this.personService = personService;
        salaryHistory = new ConcurrentHashMap<>();
        init();
    }

    private void init() {
        List<? extends Person> all = personService.findAll();
        for (Person p : all) {
            if (p.getRole().equals(Role.TEACHER)) {
                Optional<? extends Person> user = personService.find(p.getId());
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

    public Map<Integer, Map<Integer, Double>> getSalaryHistory() {
        return salaryHistory;
    }

    public double averageSalary(Integer minRange, Integer maxRange, Teacher teacher) {
        int rangeCount = maxRange - minRange + 1;
        double avgSal = 0.0;
        if (minRange < 1 || maxRange > CURRENT_MONTH
                || rangeCount <= 0
                || teacher == null
                || !getSalaryHistory().containsKey(teacher.getId())) {
            return -1;
        }
        avgSal = getAvgSal(teacher, minRange, maxRange, avgSal);
        return avgSal / rangeCount;
    }

    private double getAvgSal(Teacher teacher, int minRange, int maxRange, double avgSal) {
        Map<Integer, Double> integerDoubleMap = getSalaryHistory().get(teacher.getId());
        for (int i = minRange; i <= maxRange; i++) {
            avgSal += integerDoubleMap.get(i);
        }
        return avgSal;
    }

    public void saveSalary(Teacher teacher, Integer month, Double salary) {
        if (month >= 1 && month <= CURRENT_MONTH) {
            getSalaryHistory().putIfAbsent(teacher.getId(), new HashMap<>());
            getSalaryHistory().get(teacher.getId()).putIfAbsent(month, salary);
        }
    }

    public ModelAndView getSalary(ModelAndView modelAndView, String userName) {
        InternalResourceView internalResourceView = new InternalResourceView();
        internalResourceView.setAlwaysInclude(true);
        modelAndView.setView(internalResourceView);
        modelAndView.setViewName("/admin/salaryPage");
        Optional<? extends Person> person = personService.find(userName);
        if (person.isEmpty() || !Role.TEACHER.equals(person.get().getRole())) {
            log.info("person == null or person role != \"TEACHER\"");
            modelAndView.addObject("errorStringInSalaryPage",
                    "the teacher's login is incorrect");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
        log.info("Salary = {}", ((Teacher) person.get()).getSalary());
        modelAndView.addObject("teacher", person.get());
        modelAndView.setStatus(HttpStatus.OK);
        return modelAndView;
    }

    public ModelAndView getAverageSalary(ModelAndView modelAndView, String min, String max, String userName) {
        InternalResourceView internalResourceView = new InternalResourceView();
        internalResourceView.setAlwaysInclude(true);
        modelAndView.setView(internalResourceView);
        modelAndView.setViewName("/admin/averageSalaryPage");
        int minRange = -1;
        int maxRange = -1;
        if (!"".equals(min) &&
                !"".equals(max)) {
            minRange = Integer.parseInt(min);
            maxRange = Integer.parseInt(max);
        }
        log.info("userName = {}, minRange = {}, maxRange = {}", userName, minRange, maxRange);

        Optional<? extends Person> person = personService.find(userName);
        log.info("Get person from db");

        if (person.isEmpty() || !Role.TEACHER.equals(person.get().getRole())) {
            log.info("person == null or person role != \"TEACHER\"");
            modelAndView.addObject("errorStringInAvgSalaryPage",
                    "the teacher's login is incorrect");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
        double averageSalary = averageSalary(minRange, maxRange, (Teacher) person.get());
        if (averageSalary <= 0) {
            log.info("incorrect value in fields \"minRange\" or \"maxRange\"");
            modelAndView.addObject("errorStringInAvgSalaryPage",
                    "months are incorrect");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        log.info("Average Salary = {}", averageSalary);
        modelAndView.addObject("averageSalary", averageSalary);
        modelAndView.setStatus(HttpStatus.OK);
        return modelAndView;
    }
}
