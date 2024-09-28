package dbp.hackathon.Ticket.Email;

import org.springframework.context.ApplicationEvent;
import java.util.Map;

public class TicketEmailEvent extends ApplicationEvent {
    private final String email;
    private final String subject;
    private final String templateName;
    private final Map<String, Object> variables;

    public TicketEmailEvent(String email, String subject, String templateName, Map<String, Object> variables) {
        super(email);
        this.email = email;
        this.subject = subject;
        this.templateName = templateName;
        this.variables = variables;
    }

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }
}