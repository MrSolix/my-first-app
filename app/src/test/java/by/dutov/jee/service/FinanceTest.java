package by.dutov.jee.service;

import by.dutov.jee.config.ApplicationConfigTest;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.service.facade.Finance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfigTest.class})
@WebAppConfiguration
public class FinanceTest {
    private Teacher teacher;
    @Autowired
    private Finance finance;

    @Before
    public void setUp() {
        teacher = new Teacher()
                .withId(3)
                .withUserName("teacher")
                .withPassword("123")
                .withName("Lolka")
                .withAge(13)
                .withSalary(5000);

        for (int i = 1; i <= LocalDate.now().getMonthValue(); i++) {
            finance.saveSalary(teacher, i, i * 100.0);
        }
    }

    @Test
    public void AverageSalary_WithMinRangeEqualsOneAndMaxRangeEqualsThree_ShouldReturnTwoHundred() {
        //expected
        double expected = 100.0;

        //actual
        double actual = finance.averageSalary(1, 1, teacher);

        //assert
        assertEquals(expected, actual, 0);
    }

    @Test
    public void AverageSalary_WithMinRangeEqualsTwoAndMaxRangeEqualsOne_ShouldReturnMinusOne() {
        //expected
        double expected = -1;

        //actual
        double actual = finance.averageSalary(2, 1, teacher);

        //assert
        assertEquals(expected, actual, 0);
    }

    @Test
    public void AverageSalary_NO_NULL() {
        double actual = finance.averageSalary(1, 2, null);

        assertNotNull(actual);
    }
}