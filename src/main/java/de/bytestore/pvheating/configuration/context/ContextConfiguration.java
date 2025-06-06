package de.bytestore.pvheating.configuration.context;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalInputProvider;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;
import com.pi4j.plugin.pigpio.provider.pwm.PiGpioPwmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Context.class)
public class ContextConfiguration {
    private final Logger logger = LoggerFactory.getLogger(ContextConfiguration.class);
    private Context pi4j;

    public ContextConfiguration() {
        try {
//            if (BoardInfoHelper.current().getBoardModel() == BoardModel.UNKNOWN) {
//                this.pi4j = Pi4J.newContextBuilder()
//                        .add(new MockPlatform())
//                        .add(MockAnalogInputProvider.newInstance(),
//                                MockAnalogOutputProvider.newInstance(),
//                                MockSpiProvider.newInstance(),
//                                MockPwmProvider.newInstance(),
//                                MockSerialProvider.newInstance(),
//                                MockI2CProvider.newInstance(),
//                                MockDigitalInputProvider.newInstance(),
//                                MockDigitalOutputProvider.newInstance())
//                        .build();
//            } else {
            //this.pi4j = Pi4J.newAutoContext();


            PiGpio piGpio = PiGpio.newNativeInstance();
            //PiGpio.newSocketInstance("192.168.3.193", 8888);

            // Register Providers (Remote).
            this.pi4j = Pi4J.newContextBuilder().addPlatform(PiGpioDigitalInputProvider.newInstance(piGpio), PiGpioDigitalOutputProvider.newInstance(piGpio), PiGpioPwmProvider.newInstance(piGpio)).build();
//            }
        } catch (Error e) {
            logger.error("Pi4J library failed to load: {}", e.getMessage());
        }
    }

    @Bean
    @ConditionalOnMissingBean
    Context context() {
        return pi4j;
    }
}