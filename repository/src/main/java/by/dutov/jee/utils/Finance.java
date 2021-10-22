package by.dutov.jee.utils;

import by.dutov.jee.dao.PersonRepositoryInMemory;
import by.dutov.jee.people.Teacher;

import java.util.HashMap;
import java.util.Map;

public class Finance {
    public static Map<Teacher, Map<Integer, Double>> salaryHistory;

    static {
        salaryHistory = new HashMap<>();
    }

    public static double averageSalary(int minRange, int maxRange, Teacher teacher) {
        int rangeCount = maxRange - minRange + 1;
        double avgSal = 0.0;
        if (minRange >= 1 && maxRange <= PersonRepositoryInMemory.CURRENT_MONTH
        && rangeCount > 0) {
            if (salaryHistory.containsKey(teacher)) {
                Map<Integer, Double> integerDoubleMap = salaryHistory.get(teacher);
                for (int i = 1; i <= rangeCount; i++) {
                    avgSal += integerDoubleMap.get(i);
                }
            } else {
                return -1;
            }
        } else {
            return -1;
        }
        return avgSal/rangeCount;
    }


}
