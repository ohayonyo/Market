package Security;

import org.apache.commons.codec.digest.DigestUtils;

import Exceptions.UserExistsException;
import Exceptions.UserNotExistException;
import Exceptions.WrongPasswordException;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "UserValidation")
public class UserValidation {
    // first String = userName, second String = password

    @ElementCollection
    private Map<String, String> users;
    @Id
    private Integer id;

    public UserValidation(Map<String, String> mapOfUsers) {
        this.id=1;
        this.users = mapOfUsers;
    }

    public UserValidation() {
        this.id=1;
        this.users = new HashMap<>();
    }

    public void register(String userName, String password) throws UserExistsException
    {
        if(users.containsKey(userName))
            throw new UserExistsException(userName);
        users.put(userName, DigestUtils.md5Hex(password));
    }

    public void validateUser(String userName, String password) throws UserNotExistException, WrongPasswordException
    {
        if(!users.containsKey(userName))
            throw new UserNotExistException(userName);
        if(!users.get(userName).equals(DigestUtils.md5Hex(password)))
            throw new WrongPasswordException(userName);
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public void setUsers(Map<String, String> users) {
        this.users = users;
    }

    public void removeUser(String userName) {
        users.remove(userName);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
