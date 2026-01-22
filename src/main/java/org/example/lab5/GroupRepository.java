package org.example.lab5;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository for managing groups in memory.
 */
public class GroupRepository {
    private final Map<String, Group> groups = new HashMap<>();

    public void add(Group group) {
        groups.put(group.getName(), group);
    }

    public Group getByName(String name) {
        return groups.get(name);
    }

    public Collection<Group> getAll() {
        return groups.values();
    }

    public void clear() {
        groups.clear();
    }

    public boolean exists(String name) {
        return groups.containsKey(name);
    }
}
