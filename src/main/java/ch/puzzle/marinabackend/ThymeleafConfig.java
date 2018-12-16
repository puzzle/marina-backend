package ch.puzzle.marinabackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
@ComponentScan("ch.puzzle.marinabackend")
public class ThymeleafConfig {

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public SpringResourceTemplateResolver pdfTemplateResolver() {
        SpringResourceTemplateResolver pdfTemplateResolver = new SpringResourceTemplateResolver();
        pdfTemplateResolver.setApplicationContext(applicationContext);
        pdfTemplateResolver.setPrefix("classpath:/pdf/");
        pdfTemplateResolver.setSuffix(".html");
        pdfTemplateResolver.setTemplateMode("XHTML");
        pdfTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        pdfTemplateResolver.setCacheable(false);
        pdfTemplateResolver.setOrder(1);
        return pdfTemplateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(pdfTemplateResolver());
        engine.setMessageSource(messageSource());
        engine.setEnableSpringELCompiler(true);
        engine.addDialect(new Java8TimeDialect());
        return engine;
    }

    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/i18n/messages");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(1);
        return messageSource;
    }
}
