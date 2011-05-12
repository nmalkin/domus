

/**
 * A ChildCheckBox that refers to a specific instance of a Dorm.
 * 
 * @author nmalkin
 * 
 */
public class DormCheckBox extends ChildCheckBox {
    /** the dorm to which this checkbox refers */
    private Dorm _dorm;

    public DormCheckBox(Dorm dorm) {
        super(dorm.getName());

        _dorm = dorm;
    }

    public Dorm getDorm() {
        return _dorm;
    }
}
