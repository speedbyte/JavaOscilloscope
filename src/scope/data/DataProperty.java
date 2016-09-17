package scope.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A property class to store any type of data so that it can be passed between threads to communicate and control
 * @author Philipp
 *
 * @param <E> The datatype that is stored in this property
 */
public class DataProperty<E> {
	
	/**
	 * The data that is stored in this property
	 */
	private E data;
	/**
	 * Add PropertyChangeSupport to this class
	 */
	private PropertyChangeSupport changes = new PropertyChangeSupport( this );
	/**
	 * Set a new value to this property and report its change to all listeners 
	 * @param data
	 */
	  public void setData(E data)
	  {
	    this.data = data;
	    changes.firePropertyChange("data", null, data);
	  }
	  /**
	   * Get the data stored in this property
	   * @return the data
	   */
	  public E getData()
	  {
	    return data;
	  }
	  /**
		 * Adds a property change listener to the calling class and reports any changes to this property to this class
		 * @param l the change listener
		 */
	  public void addPropertyChangeListener( PropertyChangeListener l )
	  {
	    changes.addPropertyChangeListener( l );
	  }
	  /**
	   * Removes a change listener
	   * @param l the change listener
	   */
	  public void removePropertyChangeListener( PropertyChangeListener l )
	  {
	    changes.removePropertyChangeListener( l );
	  }


}

