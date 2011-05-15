package domus.db;


/**
 * A public class containing constants used to compile the database.
 * 
 * @author mmschnei
 * 
 */
public class DatabaseConstants {
    /** path name to the data files */
    protected final static String PATH_NAME = "data/";

    /** array of sophomore only dorms on campus */
    protected final static String[] SOPHOMORE_DORMS = { "111 Brown Street",
            "Diman House", "Grad Center D", "Harkness House", "Caswell Hall",
            "New Pembroke #1" };

    /** array of gender neutral dorms on campus */
    protected final static String[] NEUTRAL_DORMS = { "Caswell Hall",
            "Young Orchard #10", "Young Orchard #2", "Young Orchard #4",
            "Gregorian Quad A", "Gregorian Quad B", "Morriss Hall",
            "Hegeman C", "Hegeman D", "Harkness House", "Marcy House",
            "Goddard House", "New Pembroke #1", "New Pembroke #2",
            "Barbour Hall Apartments", "Grad Center A", "Grad Center B",
            "Grad Center C", "Grad Center D" };

    /** array of dorms with apartment rate rooms */
    protected final static String[] APARTMENT_RATE_DORMS = {
            "Barbour Hall Apartments", "Gregorian Quad A", "Gregorian Quad B",
            "Chapin House", "Diman House", "Goddard House", "Harkness House",
            "Marcy House", "Morriss Hall", "Sears House", "Woolley Hall",
            "Young Orchard #10", "Young Orchard #2", "Young Orchard #4",
            "Wayland House" };
}
