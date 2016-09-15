package scope.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BooleanProperty {

	private boolean bool = false;

	  private PropertyChangeSupport changes = new PropertyChangeSupport( this );

	  public void setBoolean(boolean bool)
	  {
	    this.bool = bool;
	    changes.firePropertyChange("boolean", null, bool);
	  }

	  public boolean getBoolean()
	  {
	    return bool;
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
