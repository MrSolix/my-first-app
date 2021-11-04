package by.dutov.jee.service;

import by.dutov.jee.people.Role;
import by.dutov.jee.people.Teacher;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FinanceTest {
    private Teacher teacher;
    private final Finance finance = Finance.getInstance();

    @Before
    public void setUp() throws Exception {
        teacher = new Teacher()
                .withId(90)
                .withUserName("teacher")
                .withPassword("123")
                .withName("Lolka")
                .withAge(13)
                .withRole(Role.TEACHER)
                .withSalary(5000);

        for (int i = 1; i <= LocalDate.now().getMonthValue(); i++) {
            finance.saveSalary(teacher, i, i * 100.0);
        }
    }

    @Test
    public void AverageSalary_WithMinRangeEqualsOneAndMaxRangeEqualsThree_ShouldReturnTwoHundred() {
        //expected
        double expected = 200.0;

        //actual
        double actual = finance.averageSalary(1, 3, teacher);

        //assert
        assertEquals(expected, actual, 0);
    }

    @Test
    public void AverageSalary_WithMinRangeEqualsTwoAndMaxRangeEqualsOne_ShouldReturnMinusOne() {
        //expected
        double expected = -1;

        //actual
        double actual = Finance.getInstance().averageSalary(2, 1, teacher);

        //assert
        assertEquals(expected, actual, 0);
    }

    @Test
    public void AverageSalary_NO_NULL(){
        double actual = finance.averageSalary(1, 2, null);

        assertNotNull(actual);
    }
}