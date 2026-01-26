package org.example.lab7;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.lab5.*;

/**
 * Service layer for group operations.
 * Handles business logic for group management.
 */
public class GroupService {
    private static final Logger log = LogManager.getLogger(GroupService.class);

    private final GroupRepository groupRepo;

    public GroupService(GroupRepository groupRepo) {
        this.groupRepo = groupRepo;
    }

    /**
     * Creates a new group.
     *
     * @param name group name (must be unique)
     * @param description group description
     * @return the created group
     * @throws IllegalArgumentException if name is empty
     * @throws IllegalStateException if group with this name already exists
     */
    public Group createGroup(String name, String description) {
        log.debug("Creating group: {}", name);

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required.");
        }

        name = name.trim();

        // Check if group already exists
        if (groupRepo.exists(name)) {
            throw new IllegalStateException("Group with name '" + name + "' already exists.");
        }

        String desc = description != null ? description.trim() : "";
        Group group = new Group(name, desc);
        groupRepo.add(group);

        log.info("Group created: {}", name);
        return group;
    }

    /**
     * Updates a group's description.
     *
     * @param groupName name of group to update
     * @param newDescription new description
     * @throws IllegalArgumentException if group not found
     */
    public void updateGroupDescription(String groupName, String newDescription) {
        log.debug("Updating description for group: {}", groupName);

        Group group = groupRepo.getByName(groupName);
        if (group == null) {
            throw new IllegalArgumentException("Group not found: " + groupName);
        }

        if (newDescription == null || newDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }

        group.setDescription(newDescription.trim());
        log.info("Group description updated: {}", groupName);
    }

    /**
     * Removes a group from the system.
     *
     * @param groupName name of group to remove
     * @return number of students that were in the group
     * @throws IllegalArgumentException if group not found
     */
    public int removeGroup(String groupName) {
        log.debug("Removing group: {}", groupName);

        Group group = groupRepo.getByName(groupName);
        if (group == null) {
            throw new IllegalArgumentException("Group not found: " + groupName);
        }

        int memberCount = group.getMembers().size();

        // Remove all students from group (this also updates GroupRegistry)
        for (Student student : group.getMembers().toArray(new Student[0])) {
            group.removeStudent(student);
        }

        // Remove group from repository
        groupRepo.getAll().removeIf(g -> g.getName().equals(groupName));

        log.info("Group removed: {} (had {} members)", groupName, memberCount);
        return memberCount;
    }

    /**
     * Checks if a group exists.
     *
     * @param groupName name of group to check
     * @return true if group exists, false otherwise
     */
    public boolean exists(String groupName) {
        return groupRepo.exists(groupName);
    }

    /**
     * Gets a group by name.
     *
     * @param groupName name of group
     * @return the group, or null if not found
     */
    public Group getGroup(String groupName) {
        return groupRepo.getByName(groupName);
    }
}
