package math;

public class Mathf {
    //Clamp a value between two values
    public static float Clamp(float val, float min, float max) {
        //If the value is lower than minimum, return minimum
        if (val < min) return min;

        //If the value is greater than maximum, return maximum
        if (val > max) return max;

        //Else, return the value
        return val;
    }
}
