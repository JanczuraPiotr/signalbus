package pl.templates;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.spring6.messageresolver.SpringMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

public class ThymeleafTestBase {
    protected TemplateEngine templateEngine;
    protected ResourceBundleMessageSource messageSource;
    protected SpringMessageResolver messageResolver;

    public ThymeleafTestBase() {
        messageResolver = new SpringMessageResolver();

        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");

        messageResolver = new SpringMessageResolver();
        messageResolver.setMessageSource(messageSource);
    }

    protected void beforeEach() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);


        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        templateEngine.setMessageResolver(messageResolver);
        templateEngine.addDialect(new LayoutDialect());

        StandardLinkBuilder linkBuilder = new StandardLinkBuilder() {
            @Override
            protected String computeContextPath(
                    IExpressionContext context, String linkBase, Map<String, Object> parameters) {
                return "";
            }
        };
        templateEngine.setLinkBuilder(linkBuilder);
    }
}
