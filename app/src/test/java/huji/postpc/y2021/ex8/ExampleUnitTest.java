package huji.postpc.y2021.ex8;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void check_inprogress()
    {
        CalculatorHolder calculatorHolder = new CalculatorHolder();
        calculatorHolder.put_new_calculation(new CalculatorClass(100));
        calculatorHolder.put_new_calculation(new CalculatorClass(19));
        calculatorHolder.put_new_calculation(new CalculatorClass(10));
        CalculatorClass calculatorClass = calculatorHolder.calculators_arr.get(0);
        assertEquals(calculatorClass.calculator_status, calculator_status.in_progress);

    }
    @Test
    public void check_order_calc()
    {
        CalculatorHolder calculatorHolder = new CalculatorHolder();
        calculatorHolder.put_new_calculation(new CalculatorClass(100));
        calculatorHolder.put_new_calculation(new CalculatorClass(19));
        calculatorHolder.put_new_calculation(new CalculatorClass(10));
        CalculatorClass calculatorClass = calculatorHolder.calculators_arr.get(0);
        assertEquals(calculatorClass.given_num_to_find_root_for, 10);

    }

    @Test
    public void check_order_calc2()
    {
        CalculatorHolder calculatorHolder = new CalculatorHolder();
        calculatorHolder.put_new_calculation(new CalculatorClass(0));
        calculatorHolder.put_new_calculation(new CalculatorClass(100));
        calculatorHolder.put_new_calculation(new CalculatorClass(100));
        CalculatorClass calculatorClass = calculatorHolder.calculators_arr.get(0);
        assertEquals(calculatorClass.given_num_to_find_root_for, 0);
        calculatorHolder.calculate_ended(calculatorClass,calculator_status.finished_calculating_found_roots);
        CalculatorClass calculatorClass1 = calculatorHolder.calculators_arr.get(0);
        assertEquals(calculatorClass1.given_num_to_find_root_for, 100);

    }
}