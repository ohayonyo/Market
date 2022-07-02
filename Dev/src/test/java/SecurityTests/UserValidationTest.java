package SecurityTests;

import Exceptions.UserExistsException;
import Exceptions.UserNotExistException;
import Exceptions.WrongPasswordException;
import Security.UserValidation;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class UserValidationTest {


    @Mock private Map<String, String> mapOfUsers;
    private UserValidation userValidation;
    private final String userName = "Barak";
    private final String passwordCoded = DigestUtils.md5Hex("bbb123");
    private final String password = "bbb123";
    private final String secondPassword = "BBB456";

    @BeforeEach
    void setUp() {
        userValidation = new UserValidation(mapOfUsers);
    }

    @Test
    void registerGoodUser() throws UserExistsException {
        userValidation.register(userName, password);
        verify(mapOfUsers).put(userName, passwordCoded);
    }

    @Test
    void registerUserThatExists() {
        when(mapOfUsers.containsKey(userName)).thenReturn(true);
        assertThrows(UserExistsException.class, () -> userValidation.register(userName, password));
    }

    @Test
    void validateGoodUser() throws UserNotExistException, WrongPasswordException {
        when(mapOfUsers.containsKey(userName)).thenReturn(true);
        when(mapOfUsers.get(userName)).thenReturn(passwordCoded);
        userValidation.validateUser(userName, password);
    }

    @Test
    void validateUserThatNotExists() {
        assertThrows(UserNotExistException.class, () -> userValidation.validateUser(userName, password));
    }

    @Test
    void validateUserThatExistsWrongPassword() {
        when(mapOfUsers.containsKey(userName)).thenReturn(true);
        when(mapOfUsers.get(userName)).thenReturn("bbb123");
        assertThrows(WrongPasswordException.class, () -> userValidation.validateUser(userName, secondPassword));
    }
}
