package fi.hut.soberit.agilefant.test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * A Spring ContextLoader that makes wiring mocks and real beans easy.
 * 
 * @author Joonas Javanainen
 */
public class MockContextLoader extends GenericXmlContextLoader {

    private Map<String, Class<?>> mockDefinitions = new HashMap<String, Class<?>>();
    private Map<String, String> mockConstructors = new HashMap<String, String>();
    private Map<String, Class<?>> beanDefinitions = new HashMap<String, Class<?>>();

    /**
     * Registers TestedBean and Mock annotations as Autowired annotations
     * 
     * @param context
     */
    private void registerAnnotationsAsAutowired(
            GenericApplicationContext context) {
        BeanDefinitionBuilder mockAnnotationBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
        mockAnnotationBuilder.addPropertyValue("autowiredAnnotationType", Mock.class);
        context.registerBeanDefinition("mockAutowiredBeanPostProcessor",
                mockAnnotationBuilder.getBeanDefinition());
        
        BeanDefinitionBuilder beanAnnotationBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
        beanAnnotationBuilder.addPropertyValue("autowiredAnnotationType", TestedBean.class);
        context.registerBeanDefinition("beanAutowiredBeanPostProcessor",
                beanAnnotationBuilder.getBeanDefinition());
    }

    /**
     * Registers bean definitions based on all detected mock annotations
     * @param context
     */
    protected void registerMocks(GenericApplicationContext context) {
        for (Map.Entry<String, Class<?>> definition : mockDefinitions
                .entrySet()) {
            String beanName = definition.getKey();
            Class<?> classToMock = definition.getValue();
            BeanDefinition beanDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(EasyMock.class).setFactoryMethod(
                            mockConstructors.get(beanName)).addConstructorArgValue(classToMock)
                    .getBeanDefinition();
            context.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    /**
     * Registers bean definitions based on all detected bean annotations
     * @param context
     */
    protected void registerBeans(GenericApplicationContext context) {
        for (Map.Entry<String, Class<?>> definition : beanDefinitions
                .entrySet()) {
            String beanName = definition.getKey();
            Class<?> beanClass = definition.getValue();
            BeanDefinition beanDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(beanClass).getBeanDefinition();
            context.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    @Override
    protected void customizeContext(GenericApplicationContext context) {
        registerAnnotationsAsAutowired(context);
        registerMocks(context);
        registerBeans(context);

        super.customizeContext(context);
    }

    /**
     * Default XML context configurations are disabled because
     * usually they are not needed with mock-heavy tests.
     */
    @Override
    protected boolean isGenerateDefaultLocations() {
        return false;
    }

    /**
     * Detects TestedBean and Mock annotations and collects them for later use.
     */
    public String[] modifyLocations(Class<?> clazz, String... locations) {
        for (Field field : clazz.getDeclaredFields()) {
            Mock mockAnnotation = field.getAnnotation(Mock.class);
            if (mockAnnotation != null) {
                mockDefinitions.put(field.getName(), field.getType());
                if(mockAnnotation.strict()) {
                    mockConstructors.put(field.getName(), "createStrictMock");
                } else {
                    mockConstructors.put(field.getName(), "createMock");
                }
            }
            TestedBean beanAnnotation = field.getAnnotation(TestedBean.class);
            if (beanAnnotation != null) {
                beanDefinitions.put(field.getName(), field.getType());
            }
        }
        return locations;
    }

}
