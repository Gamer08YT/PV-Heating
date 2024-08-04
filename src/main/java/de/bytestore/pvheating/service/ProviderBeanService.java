package de.bytestore.pvheating.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProviderBeanService {

    public ProviderBeanService() {
    }

    public void test() {
        System.out.println("TEST");
    }
//    private final ListableBeanFactory beanFactory;
//
//    /**
//     * Retrieves the children of the current ProviderService instance.
//     *
//     * @return A map containing the names as keys and corresponding ProviderTemplate instances as values.
//     */
//    public Collection<ProviderTemplate> getChildren() {
//        return beanFactory.getBeansOfType(ProviderTemplate.class).values();
//    }
}
