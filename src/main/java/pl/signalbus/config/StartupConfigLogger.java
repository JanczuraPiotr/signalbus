package pl.signalbus.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@PropertySource("classpath:application-metadata.properties")
public class StartupConfigLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupConfigLogger.class);

    private final ServerProperties serverProperties;

    @Autowired
    private Environment env;

    public StartupConfigLogger(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @PostConstruct
    public void init() {
        log.info("=======================================================================");
        log.info("========== APPLICATION STARTED WITH THE FOLLOWING PARAMETERS ==========");
        log.info("=======================================================================");

        log.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));
        log.info("vaadin.productionMode: {}", env.getProperty("vaadin.productionMode", "???"));

        log.info("server.ssl.enabled: {}", serverProperties.getSsl() != null && serverProperties.getSsl().isEnabled());
        var cookie = serverProperties.getServlet().getSession().getCookie();
        boolean sslEnabled = serverProperties.getSsl() != null && serverProperties.getSsl().isEnabled();
        boolean isSecure = cookie.getSecure() != null ? cookie.getSecure() : sslEnabled;
        boolean isHttpOnly = cookie.getHttpOnly() != null ? cookie.getHttpOnly() : true;
        String sameSite = cookie.getSameSite() != null ? cookie.getSameSite().name() : "not set (browser default)";
        log.info("server.servlet.session.cookie.secure: {} (effective: {})", cookie.getSecure(), isSecure);
        log.info("server.servlet.session.cookie.http-only: {} (effective: {})", cookie.getHttpOnly(), isHttpOnly);
        log.info("server.servlet.session.cookie.same-site: {}", sameSite);

        log.info("build.version: {}", env.getProperty("build.version", "???"));
        log.info("build.timestamp: {}", env.getProperty("build.timestamp", "???"));
        log.info("git.branch: {}", env.getProperty("git.branch", "???"));
        log.info("git.commit.id: {}", env.getProperty("git.commit.id", "???"));
        log.info("git.commit.time: {}", env.getProperty("git.commit.time", "???"));
        log.info("git.commit.id.describe: {}", env.getProperty("git.commit.id.describe", "???"));

        log.info("=======================================================================");
    }

}
