package by.dutov.jee.utils;

import by.dutov.jee.dao.PersonRepositoryInMemory;
import by.dutov.jee.people.Teacher;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class FinanceTest {
    private Teacher teacher;

    @Before
    public void setUp() throws Exception {
        teacher = new Teacher()
                .withUserName("teapcher")
                .withPassword("123")
                .withName("Lolka")
                .withAge(13)
                .withRole("TEACHER")
                .withSalary(5000);

        Finance.salaryHistory.put(teacher, new HashMap<>());

        for (int i = 1; i <= PersonRepositoryInMemory.CURRENT_MONTH; i++) {
            Finance.salaryHistory.get(teacher).put(i, i * 100.0);
        }
    }

    @Test
    public void AverageSalary_WithMinRangeEqualsOneAndMaxRangeEqualsThree_ShouldReturnTwoHundred() {
        //expected
        double expected = 200.0;

        //actual
        double actual = Finance.averageSalary(1, 3, teacher);

        //assert
        assertEquals(expected, actual, 0);
    }

    @Test
    public void AverageSalary_WithMinRangeEqualsTwoAndMaxRangeEqualsOne_ShouldReturnMinusOne() {
        //expected
        double expected = -1;

        //actual
        double actual = Finance.averageSalary(2, 1, teacher);

        //assert
        assertEquals(expected, actual, 0);
    }

    @Test
    public void AverageSalary_NO_NULL(){
        double actual = Finance.averageSalary(1, 2, null);

        assertNotNull(actual);
    }
}