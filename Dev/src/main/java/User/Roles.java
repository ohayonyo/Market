package User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
public class Roles {


    public Roles() {
        this.roles = new HashSet<>();
    }

    public enum Role {
        STORE_OWNER,
        STORE_MANAGER,
        STORE_FOUNDER,
        INVENTORY_MANAGEMENT,
        STORE_POLICY,
        STORE_BID
    }

    @Id
    @GeneratedValue
    @Column(name = "roles_id")
    private int id;

    @ElementCollection
    private Set<Role> roles;

    public boolean contains(Role role)
    {
        return roles.contains(role);
    }

    public boolean remove(Role role)
    {
        return roles.remove(role);
    }

    public void add(Role role) {
        roles.add(role);
    }

    @Override
    public String toString() {
        return roles.toString();
    }
}
