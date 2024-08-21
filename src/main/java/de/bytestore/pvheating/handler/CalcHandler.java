package de.bytestore.pvheating.handler;

public class CalcHandler {
    // Koeffizent from Water.
    private static double waterKO = 4.19;

    // Tank Volume in L.
    private static double volume = 160;

    // Efficiency of Heating Element.
    private static double efficiency = 0.99;

    /**
     * Calculates the time (in Seconds) needed to reach the desired temperature based on
     * the current temperature and input power.
     *
     * @param tempCurrent the current temperature in degrees Celsius
     * @param powerIO     the input power in watts
     * @return the time in seconds needed to reach the desired temperature,
     * or 0 if the current temperature is already equal to or higher than the desired temperature
     */
    public static double calcTime(double tempCurrent, double powerIO) {
        double tempWish = ConfigHandler.getCached().getTemperature().getDesiredTemperature();

        if (tempCurrent < tempWish && powerIO > 0) {
            // Calculate Delta T. eq. (60°c - 8°C)
            double deltaT = tempWish - tempCurrent;

            // Calculate Time. eq. t = volume * waterKO * deltaT
            //                         -------------------------
            //                          currentPower * efficiency
            return (volume * waterKO * deltaT) / (powerIO * efficiency);

        }

        return 0;
    }
}
