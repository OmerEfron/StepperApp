package utils.Valitator;
import jakarta.servlet.http.HttpServletRequest;
import utils.SessionUtils;

import static utils.ServletUtils.ADMIN_USERNAME;

public class UserValidatorImpl implements UserValidator{

    private final HttpServletRequest request;

    public UserValidatorImpl(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Boolean isAdmin() {
        return SessionUtils.getUsername(request).equals(ADMIN_USERNAME);
    }

    @Override
    public Boolean isLoggedIn() {
        return SessionUtils.getUsername(request) != null;
    }
}
