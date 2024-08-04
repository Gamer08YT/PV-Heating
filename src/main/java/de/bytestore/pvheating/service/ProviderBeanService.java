package de.bytestore.pvheating.service;

import de.bytestore.pvheating.handler.templates.ProviderTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

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
}
