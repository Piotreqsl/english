package org.example.lab5;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registry that tracks which group each student belongs to.
 * Ensures that each student can only belong to one group at a time.
 */
public class GroupRegistry {
    private static final Logger log = LogManager.getLogger(GroupRegistry.class);
    private static final Map<String, String> studentToGroup = new HashMap<>();
    
    /**
     * Checks if a student is already assigned to a group.
     * 
     * @param studentId the student's ID
     * @return true if the student is assigned to a group, false otherwise
     */
    public static boolean isAssigned(String studentId) {
        return studentToGroup.containsKey(studentId);
    }
    
    /**
     * Gets the name of the group a student is assigned to.
     * 
     * @param studentId the student's ID
     * @return the group name, or null if not assigned
     */
    public static String getGroupName(String studentId) {
        return studentToGroup.get(studentId);
    }
    
    /**
     * Assigns a student to a group.
     * 
     * @param studentId the student's ID
     * @param groupName the group name
     */
    static void assign(String studentId, String groupName) {
        studentToGroup.put(studentId, groupName);
        log.info("Assigned personId={} to group={}", studentId, groupName);
    }
    
    /**
     * Removes a student's group assignment.
     * 
     * @param studentId the student's ID
     */
    static void unassign(String studentId) {
        if (studentToGroup.remove(studentId) != null) {
            log.debug("Unassigned personId={} from registry", studentId);
        } else {
            log.warn("Attempt to unassign non-registered personId={}", studentId);
        }
    }
    
    /**
     * Clears all assignments (useful for testing).
     */
    public static void clear() {
        studentToGroup.clear();
    }
}
