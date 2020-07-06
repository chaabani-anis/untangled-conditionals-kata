import dependencies.Config;
import dependencies.Emailer;
import dependencies.Logger;
import dependencies.Project;

public class Pipeline {
    private final Config config;
    private final Emailer emailer;
    private final Logger log;

    public Pipeline(Config config, Emailer emailer, Logger log) {
        this.config = config;
        this.emailer = emailer;
        this.log = log;
    }

    public void run(Project project) {
        if (testFailure(project)) {
            sendMail("Tests failed");
            return;
        }
        if (projectDeploy(project)) {
            sendMail("Deployment completed successfully");
        }
        else {
            sendMail("Deployment failed");
        }
    }

    private boolean testFailure(Project project) {
        if (!project.hasTests()) {
            log.info("No tests");
            return false;
        }
        else if ("success".equals(project.runTests())) {
            log.info("Tests passed");
            return false;
        }

        log.error("Tests failed");
        return true;
    }

    private boolean projectDeploy(Project project) {
        if ("success".equals(project.deploy())) {
            log.info("Deployment successful");
            return true;
        }

        log.error("Deployment failed");
        return false;
    }

    private void sendMail(String body) {
        if (config.sendEmailSummary()) {
            log.info("Sending email");
            emailer.send(body);
        } else {
            log.info("Email disabled");
        }
    }
}