package pl.matkmiec.backup.entity;

/** Defines the types of backups that can be created in the system.
 * Currently, supports two types of backups: SMS and CONTACTS.
 * This enum can be extended in the future to include additional backup types as needed.
 * */
public enum BackupType {
    /** Represents an SMS backup. */
    SMS,
    /** Represents a contacts backup. */
    CONTACTS
}
