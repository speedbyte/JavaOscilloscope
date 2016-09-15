package scope.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;

public class TimestampProperty {

	private Timestamp timestamp;

	  private PropertyChangeSupport changes = new PropertyChangeSupport( this );

	  public void setTimestamp(Timestamp ts)
	  {
	    this.timestamp = ts;
	    changes.firePropertyChange("timestamp", null, ts);
	  }

	  public Timestamp getTimestamp()
	  {
	    return timestamp;
	  }

	  public void addPropertyChangeListener( PropertyChangeListener l )
	  {
	    changes.addPropertyChangeListener( l );
	  }

	  public void removePropertyChangeListener( PropertyChangeListener l )
	  {
	    changes.removePropertyChangeListener( l );
	  }
	
}
