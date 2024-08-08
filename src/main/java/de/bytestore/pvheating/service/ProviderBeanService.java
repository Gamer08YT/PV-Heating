package de.bytestore.pvheating.service;

import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.handler.templates.ProviderTemplate;
import de.bytestore.pvheating.objects.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ProviderBeanService {

    public ProviderBeanService(@Autowired ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    private final ListableBeanFactory beanFactory;

    /**
     * Retrieves the children of the current ProviderService instance.
     *
     * @return A map containing the names as keys and corresponding ProviderTemplate instances as values.
     */
    public Collection<ProviderTemplate> getChildren() {
        return beanFactory.getBeansOfType(ProviderTemplate.class).values();
    }

//    public Provider byGPIOType(List<GPIOType> typesIO) {
//        ArrayList<Provider> valueProvider = getProviders();
//        ArrayList<Provider> newItems = new ArrayList<>();
//
//        Set<GPIOType> set = new HashSet<>(Arrays.asList(valueIO.type()));
//
//        valueProvider.forEach(provider -> {
//            boolean anyMatch = Arrays.stream(provider.getTypes())
//                    .anyMatch(set::contains);
//
//            if(anyMatch) {
//                newItems.add(provider);
//            }
//        });
//    }

//    public ArrayList<Provider> getProvider(GPIOType[] types) {
//        Collection<ProviderTemplate> providersIO = getChildren();
//
//    }

}
